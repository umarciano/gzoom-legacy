package com.mapsengineering.base.standardimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericServiceLoop;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.communication.enumeration.CommunicationEventFieldEnum;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.EntityCodeEnum;
import com.mapsengineering.base.standardimport.common.FieldConfig;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.MessageUtil;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;


/**
 * Standard Import with external file excel and csv
 * TODO Result with recordElaborated and blockingErrors from standardImport
 */
public class ImportManagerExternalFile extends GenericServiceLoop {

    public static final String MODULE = ImportManagerExternalFile.class.getName();

    private static final String SERVICE_NAME = "ImportManagerExternalFile";
    private static final String SERVICE_TYPE_ID = "STR_IMPORT_EXT";
    
    private static final String RESOURCE_LABEL = "StandardImportUiLabels";
    private static final String PROP_OFBIZ_HOME = "ofbiz.home";

    /**
     * Si usa sempre lo stesso contentType, sia se si tratta di csv o xls
     */
    private static final String DEFAULT_CONTENT_TYPE = "application/vnd.ms-excel";
    /**
     * contentType per xlsx
     */
    private static final String DEFAULT_CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    
    private String directoryPathInXls;
    private String directoryPathOutXls;
    
    private String username;
    private String password;
    private String directorySmbPath;
    private String domain;
    private boolean smbProtocol = false;
    
    private String toString;

    private String defaultPartyIdEmailAddress;

    private String description;

    private String defaultOrganizationPartyIdValue;
    
    private FieldConfig fieldConfig;

    private Timestamp refDate;
    
    private NtlmPasswordAuthentication auth;

    /**
     * if checkOnlyMoveSmbFile = onlyMoveSmbFile, the file is wrote in folder directoryPathInXls
     */
    private String checkOnlyMoveSmbFile;

    /**
     * if checkOnlyUpload = onlyUpload, the record is wrote in standard import tables
     */
    private String checkOnlyUpload;

    /**
     * Search file in specific directory and call standard import, then move file in specific directory 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> doImportSrv(DispatchContext dctx, Map<String, Object> context) {
        ImportManagerExternalFile obj = new ImportManagerExternalFile(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public ImportManagerExternalFile(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
        this.directoryPathInXls = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.in");
        this.directoryPathOutXls = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.out");
        this.toString = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.sendMail.toString");
        this.defaultPartyIdEmailAddress = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.sendMail.defaultPartyIdEmailAddress");
        this.defaultOrganizationPartyIdValue = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.defaultOrganizationPartyId");
        this.fieldConfig = new FieldConfigService(dctx);
        this.username = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.username");
        this.password = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.password");
        this.directorySmbPath = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.directorySmbPath");
        this.domain = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.domain");
        //if username is not empty active smb protocol
        if(this.username!=null && !this.username.equals("")) {
        	//this.auth = new NtlmPasswordAuthentication("", this.username, this.password);
        	this.auth = new NtlmPasswordAuthentication(this.domain, this.username, this.password);
        	this.smbProtocol = true;
        }
        checkOnlyMoveSmbFile = (String)context.get(E.checkOnlyMoveSmbFile.name()); // onlyMoveSmbFile
        if (UtilValidate.isEmpty(checkOnlyMoveSmbFile)) {
            checkOnlyMoveSmbFile = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.checkOnlyMoveSmbFile");
        }
        checkOnlyUpload = (String)context.get(E.checkOnlyUpload.name()); // onlyUpload
        if (UtilValidate.isEmpty(checkOnlyUpload)) {
            checkOnlyUpload = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.folder.checkOnlyUpload");
        }
        
    }

    /**
     * Main loop
     */
    @Override
    protected void execute() throws Exception {
        try {
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            refDate = UtilDateTime.getDayStart(nowTimestamp);
            if (UtilValidate.isEmpty(getDefaultOrganizationPartyId())) {
                setDefaultOrganizationPartyId(defaultOrganizationPartyIdValue);
            }
            GenericValue serviceType = findOne("JobLogServiceType", EntityCondition.makeCondition(ServiceLogger.SERVICE_TYPE_ID, getServiceType()), "Found more " + getServiceType(), "No service name found " + getServiceType());
            description = serviceType.getString("description");
            setDescriptionEntityName(description);

            Map<String, Object> logParamSearch = UtilMisc.toMap(E.directoryPathInXls.name(), (Object)directoryPathInXls);
            JobLogLog jllSearch = new JobLogLog().initLogCode(RESOURCE_LABEL, "START_IMPORT_EXT", logParamSearch, getLocale());
            addLogInfo(jllSearch.getLogCode(), jllSearch.getLogMessage(), null, null, jllSearch.getParametersJSON());

            //move file from smb folder to local folder
            if(this.smbProtocol) {
                this.moveSmbFiles("xls");
                this.moveSmbFiles("xlsx");
                this.moveSmbFiles("csv");
            }
            	
            if (this.smbProtocol && E.onlyMoveSmbFile.name().equals(this.checkOnlyMoveSmbFile)) {
                logParamSearch = UtilMisc.toMap(E.directoryPathInXls.name(), (Object)directoryPathInXls);
                jllSearch = new JobLogLog().initLogCode(RESOURCE_LABEL, "ONLY_MOVE_EXT", logParamSearch, getLocale());
                addLogInfo(jllSearch.getLogCode(), jllSearch.getLogMessage(), null, RESOURCE_LABEL, jllSearch.getParametersJSON());
            } else {
                // upload file and EXCUTE standardImport only for upload
                executeUploadAndImportFile("xls", DEFAULT_CONTENT_TYPE);
                executeUploadAndImportFile("xlsx", DEFAULT_CONTENT_TYPE_XLSX);
                executeUploadAndImportFile("csv", DEFAULT_CONTENT_TYPE);
            }
            
            createCommunicationEventAndSendMail();
        } catch (Exception e) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), (Object) MessageUtil.getExceptionMessage(e));
            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_STD_EXT_FILE", logParameters, getLocale());
            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON());
            getResult().putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
        }
    }

    
    /**
     * Upload file and execute standardImport
     * @param defaultContentType 
     * @return
     * @throws Exception
     */
    private void executeUploadAndImportFile(String fileExtension, String defaultContentType) throws Exception {
        executeUploadAndImportFileWithEntityName(fileExtension, defaultContentType);
        executeUploadAndImportFileWithoutEntityName(fileExtension, defaultContentType);
    }
    
    /**
     * Search file with ORGA, PERS, ecc...
     * @param fileExtension
     * @param defaultContentType
     * @return
     * @throws Exception
     */
    private void executeUploadAndImportFileWithEntityName(String fileExtension, String defaultContentType) throws Exception {
        String inputPath = getInputPath();
        EntityCodeEnum[] entityCodes = EntityCodeEnum.values();
        for (EntityCodeEnum entityCode : entityCodes) {
            List<File> files = FileUtil.getFiles(inputPath, (entityCode + "." + fileExtension).toLowerCase());
            Map<String, Object> logParamSearch = UtilMisc.toMap(E.directoryPathInXls.name(), (Object)directoryPathInXls, E.entityCode.name(), entityCode, E.fileExtension.name(), fileExtension);
            JobLogLog jllSearch = new JobLogLog().initLogCode(RESOURCE_LABEL, "SEARCH_FILE", logParamSearch, getLocale());
            addLogInfo(jllSearch.getLogCode(), jllSearch.getLogMessage(), null, null, jllSearch.getParametersJSON());

            String entityName = entityCode.getInterfaceName();
            if (UtilValidate.isNotEmpty(files)) {
                // Upload 1 file per volta
                executeImportFile(entityName, files, defaultContentType);
                moveFile(files);
            }
        }
    }
    
    private String getInputPath() {
        if (directoryPathInXls.indexOf("\\") >= 0 || directoryPathInXls.indexOf("//") >= 0) {
            return directoryPathInXls;
        }
        String propValue = System.getProperty(PROP_OFBIZ_HOME);
        return new File(propValue).getAbsolutePath() + directoryPathInXls;
    }

    private void moveSmbFiles(String fileExtension) throws Exception {
       // SmbFile smbPath = new SmbFile(this.directorySmbPath,this.auth);
        //SmbFile finalDirectory = new SmbFile(this.directoryPathInXls,this.auth);
    	SmbFile smbPath = new SmbFile(this.directorySmbPath,this.auth);
    	Map<String, Object> logParamSearch = UtilMisc.toMap(E.directoryPathInXls.name(), (Object)directorySmbPath, E.fileExtension.name(), fileExtension);
        JobLogLog jllSearch = new JobLogLog().initLogCode(RESOURCE_LABEL, "SEARCH_FILE", logParamSearch, getLocale());
        addLogInfo(jllSearch.getLogCode(), jllSearch.getLogMessage(), null, RESOURCE_LABEL, jllSearch.getParametersJSON());
        for(SmbFile file: smbPath.listFiles()) {
        	SmbFileInputStream fis = new SmbFileInputStream(file);
        	if (file.getName().endsWith(fileExtension)) {
        	    Map<String, Object> logParameters = UtilMisc.toMap(E.filename.name(), (Object)file.getName());
                JobLogLog jllFound = new JobLogLog().initLogCode(RESOURCE_LABEL, "FOUND_FILE", logParameters, getLocale());
                addLogInfo(jllFound.getLogCode(), jllFound.getLogMessage(), null, RESOURCE_LABEL, jllFound.getParametersJSON());
                
                FileOutputStream out = new FileOutputStream( new File(getInputPath() + file.getName()));
                out.write(org.apache.poi.util.IOUtils.toByteArray(fis));
                out.close();
        	}
        }
    }
    
    private void executeUploadAndImportFileWithoutEntityName(String fileExtension, String defaultContentType) throws Exception {
        String inputPath = getInputPath();
        List<File> files = FileUtil.getFiles(inputPath, fileExtension);
        
        Map<String, Object> logParamSearch = UtilMisc.toMap(E.directoryPathInXls.name(), (Object)directoryPathInXls, E.fileExtension.name(), fileExtension);
        JobLogLog jllSearch = new JobLogLog().initLogCode(RESOURCE_LABEL, "SEARCH_FILE", logParamSearch, getLocale());
        addLogInfo(jllSearch.getLogCode(), jllSearch.getLogMessage(), null, null, jllSearch.getParametersJSON());

        if (UtilValidate.isNotEmpty(files)) {
            executeImportFile("", files, defaultContentType);
            moveFile(files);
        }
    }
    

    private void executeImportFile(String entityName, List<File> files, String defaultContentType) throws GeneralException {
        for (File file : files) {
            String filename = file.getName();
            
            Map<String, Object> logParameters = UtilMisc.toMap(E.filename.name(), (Object)filename);
            JobLogLog jllFound = new JobLogLog().initLogCode(RESOURCE_LABEL, "FOUND_FILE", logParameters, getLocale());
            addLogInfo(jllFound.getLogCode(), jllFound.getLogMessage(), null, null, jllFound.getParametersJSON());
            ByteBuffer buffer;
            try {
                buffer = ByteBuffer.wrap((byte[])(FileUtils.readFileToByteArray(file)));
            } catch (IOException e) {
                // TODO sistemare i log, mettendo eccezione nei parametr idel messaggio
                JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_EXT_FILE", logParameters, getLocale());
                String errorMessage = errorGeneric.getLogMessage() + MessageUtil.getExceptionMessage(e);
                addLogError(e, errorGeneric.getLogCode(), errorMessage, null, RESOURCE_LABEL, errorGeneric.getParametersJSON());
                return;
            }
            context.put(entityName + "UploadedFile", buffer);
            context.put("_" + entityName + "UploadedFile_fileName", filename);
            context.put("_" + entityName + "UploadedFile_contentType", defaultContentType);
            context.put(entityName + "DataSourceId", getDataSource(filename));
            context.put(entityName + "RefDate", refDate);
            
            if (entityName.equals(E.OrganizationInterface.name())) {
                context.put(E.entityListToImport.name(), entityName.concat("|OrgRespInterface"));
            } else if (entityName.equals(E.PersonInterface.name())) {
                context.put(E.entityListToImport.name(), entityName.concat("|PersRespInterface"));
            } else {
                context.put(E.entityListToImport.name(), entityName);
            }
            
            context.put(ServiceLogger.SESSION_ID, getSessionId());
            context.put(E.checkOnlyUpload.name(), checkOnlyUpload);
            Map<String, Object> logParamStart = UtilMisc.toMap(E.entityName.name(), (Object)entityName, E.filename.name(), filename);
            JobLogLog jllStart = new JobLogLog().initLogCode(RESOURCE_LABEL, "START_IMPORT_FILE", logParamStart, getLocale());
            addLogInfo(jllStart.getLogCode(), jllStart.getLogMessage(), null, null, jllStart.getParametersJSON());
            Map<String, Object> importManagerUploadFileResult = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log(" - importManagerUploadFileResult " + importManagerUploadFileResult);
            
            // TODO sistemare i log, importManagerUploadFileResult contiene solo i log della fine dell'upload del file
            Map<String, Object> logParamFinish = UtilMisc.toMap("result", (Object)importManagerUploadFileResult);
            JobLogLog jllFinish = new JobLogLog().initLogCode(RESOURCE_LABEL, "FINISH_IMPORT_FILE", logParamFinish, getLocale());
            addLogInfo(jllFinish.getLogCode(), jllFinish.getLogMessage(), null, null, jllFinish.getParametersJSON());
            
            List<Map<String, Object>> importManagerUploadFileResultList = UtilGenerics.toList(importManagerUploadFileResult.get("resultListUploadFile"));
            Debug.log(" - importManagerUploadFileResultList " + importManagerUploadFileResultList);
            getResult().putAll(importManagerUploadFileResult);
            
            // TODO set recordElaborated and blockingErrors from standardImport
            // Raccogliere i log e i record elaborati/errori bloccanti
            /*if (UtilValidate.isNotEmpty(importManagerResultList)) {
                for (Map<String, Object> result : importManagerResultList) {
                    setBlockingErrors(getBlockingErrors() + (Long)result.get(ServiceLogger.BLOCKING_ERRORS));
                    setRecordElaborated(getRecordElaborated() + (Long)result.get(ServiceLogger.RECORD_ELABORATED));
                }
            }*/
        }
    }

    /**
     *  Search is exists dataSource with name of file,
     *  the file is a csv, can not search the datasource in file
     * @param filename
     * @return
     * @throws GeneralException
     */
    private String getDataSource(String filename) throws GeneralException {
        filename = filename.substring(0, filename.indexOf('.'));
        GenericValue stfc = fieldConfig.getStfcDataSource(filename.toUpperCase());
        if(UtilValidate.isNotEmpty(stfc)) {
            return stfc.getString(E.dataSourceId.name());
        }
        return null;
    }

    private void moveFile(List<File> files) throws IOException {
        for (File file : files) {
            FileOutputStream outputStream = null;
            String filePath = file.getAbsolutePath();
            Map<String, Object> logFilePath = UtilMisc.toMap(E.filePath.name(), (Object)filePath);
            String filename = file.getName();
            Map<String, Object> logParameters = UtilMisc.toMap(E.filename.name(), (Object)filename, E.directoryPathOutXls.name(), directoryPathOutXls);
            try {
                if (file.exists()) {
                    String propValue = System.getProperty(PROP_OFBIZ_HOME);
                    String outputPath = new File(propValue).getAbsolutePath() + directoryPathOutXls;

                    ByteBuffer buffer = ByteBuffer.wrap((byte[])(FileUtils.readFileToByteArray(file)));
                    String outfile = FileUtil.getPatchedFileName(outputPath, filename);
                    outputStream = new FileOutputStream(new File(outfile));
                    outputStream.write(buffer.array());
                    
                    JobLogLog jllFound = new JobLogLog().initLogCode(RESOURCE_LABEL, "MOVE_FILE", logParameters, getLocale());
                    addLogInfo(jllFound.getLogCode(), jllFound.getLogMessage(), null, RESOURCE_LABEL, jllFound.getParametersJSON());
                } else {
                    JobLogLog jllNotExist = new JobLogLog().initLogCode(RESOURCE_LABEL, "NOT_EXISTS_FILE", logFilePath, getLocale());
                    addLogWarning(jllNotExist.getLogCode(), jllNotExist.getLogMessage(), null, RESOURCE_LABEL, jllNotExist.getParametersJSON());
                }
            } catch (IOException e) {
                JobLogLog jllError = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_FILE", logFilePath, getLocale());
                addLogError(e, jllError.getLogCode(), jllError.getLogMessage(), null, RESOURCE_LABEL, jllError.getParametersJSON());
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (!file.delete()) {
                    JobLogLog jllDelete = new JobLogLog().initLogCode(RESOURCE_LABEL, "NO_DELETE_FILE", logParameters, getLocale());
                    addLogWarning(jllDelete.getLogCode(), jllDelete.getLogMessage(), null, RESOURCE_LABEL, jllDelete.getParametersJSON());
                }
            }
        }
    }

    private void createCommunicationEventAndSendMail() throws GenericServiceException {
        Map<String, Object> logParamMailContent1 = UtilMisc.toMap(ServiceLogger.SESSION_ID, (Object)getSessionId(), ServiceLogger.RECORD_ELABORATED, (Object)getRecordElaborated(), ServiceLogger.BLOCKING_ERRORS, (Object)getBlockingErrors());
        JobLogLog mailContent1 = new JobLogLog().initLogCode(RESOURCE_LABEL, "STD_SESSION_ID", logParamMailContent1, getLocale());
        addLogInfo(mailContent1.getLogCode(), mailContent1.getLogMessage(), null, RESOURCE_LABEL, mailContent1.getParametersJSON());

        Map<String, Object> logParamMailContent2 = UtilMisc.toMap(ServiceLogger.SESSION_ID, (Object)getSessionId(), ServiceLogger.RECORD_ELABORATED, (Object)getRecordElaborated(), ServiceLogger.BLOCKING_ERRORS, (Object)getBlockingErrors());
        JobLogLog mailContent2 = new JobLogLog().initLogCode(RESOURCE_LABEL, "STD_BLOC_ERR", logParamMailContent2, getLocale());
        addLogInfo(mailContent2.getLogCode(), mailContent2.getLogMessage(), null, RESOURCE_LABEL, mailContent2.getParametersJSON());

        Map<String, Object> logParamMailContent3 = UtilMisc.toMap(ServiceLogger.SESSION_ID, (Object)getSessionId(), ServiceLogger.RECORD_ELABORATED, (Object)getRecordElaborated(), ServiceLogger.BLOCKING_ERRORS, (Object)getBlockingErrors());
        JobLogLog mailContent3 = new JobLogLog().initLogCode(RESOURCE_LABEL, "STD_REC_ELAB", logParamMailContent3, getLocale());
        addLogInfo(mailContent3.getLogCode(), mailContent3.getLogMessage(), null, RESOURCE_LABEL, mailContent3.getParametersJSON());

        if (UtilValidate.isEmpty(toString)) {
            JobLogLog noMailFound = new JobLogLog().initLogCode(RESOURCE_LABEL, "NO_MAIL_FOUND", null, getLocale());
            addLogWarning(noMailFound.getLogCode(), noMailFound.getLogMessage(), null, RESOURCE_LABEL, noMailFound.getParametersJSON());
            return;
        }

        Map<String, Object> logParamCommunication = UtilMisc.toMap(CommunicationEventFieldEnum.toString.name(), (Object)toString, CommunicationEventFieldEnum.partyIdEmailAddress.name(), defaultPartyIdEmailAddress);
        JobLogLog communication = new JobLogLog().initLogCode(RESOURCE_LABEL, "CREATE_COMMEV", logParamCommunication, getLocale());
        addLogInfo(communication.getLogCode(), communication.getLogMessage(), null, RESOURCE_LABEL, communication.getParametersJSON());

        String serviceName = "communicationEventCreate";
        Map<String, Object> serviceContext = getDctx().makeValidContext(serviceName, ModelService.IN_PARAM, context);
        serviceContext.put(CommunicationEventFieldEnum.partyIdEmailAddress.name(), defaultPartyIdEmailAddress);
        serviceContext.put(CommunicationEventFieldEnum.toString.name(), toString);
        serviceContext.put(CommunicationEventFieldEnum.resourceLabel.name(), RESOURCE_LABEL);
        serviceContext.put(CommunicationEventFieldEnum.content.name(), mailContent1.getLogMessage() + mailContent2.getLogMessage() + mailContent3.getLogMessage());
        serviceContext.put(CommunicationEventFieldEnum.subject.name(), description);
        serviceContext.put(ServiceLogger.JOB_LOGGER, jobLogger);
        dispatcher.runSync(serviceName, serviceContext);
    }
}
