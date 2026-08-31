package com.mapsengineering.base.standardimport;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.BaseImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.EntityNameStdImportEnum;
import com.mapsengineering.base.standardimport.common.FieldConfig;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.helper.ImportManagerHelper;
import com.mapsengineering.base.standardimport.helper.ImportManagerUploadFileHelper;
import com.mapsengineering.base.standardimport.util.TakeOverUtil;
import com.mapsengineering.base.util.ExcelReaderUtil;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * ImportManager File
 *
 */
public class ImportManagerUploadFile extends BaseImportManager {

    public static final String MODULE = ImportManagerUploadFile.class.getName();
    private static final String SERVICE_TYPE_ID = "STD_IMPORT_UPLOAD";
    
    public static final String EXT = "Ext";

        
    private List<Map<String, Object>> resultListUploadFile = new ArrayList<Map<String, Object>>();
    private Map<String, Object> result = ServiceUtil.returnSuccess();
    private Map<String, Object> resultImportStandard = new HashMap<String, Object>();

    private ImportManagerUploadFileHelper importManagerUploadFileHelper;
    private ImportManagerHelper importManagerHelper;
    private ImportManagerFileCsv importManagerFileCsv;
    private FieldConfig fieldConfig;
    private Map<String, String> entityMap;
    /**
     * Set with list of table interface to import, can be populate from form or with standardImportFieldconfig
     */
    private Set<String> entitiesToImport;
    /**
     * 
     * @param dctx
     * @param context
     */
    public ImportManagerUploadFile(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context);
        importManagerUploadFileHelper = new ImportManagerUploadFileHelper(dctx, context);
        importManagerHelper = new ImportManagerHelper(dctx, context);
        this.fieldConfig = new FieldConfigService(dctx);
        initEntityMap();
        entitiesToImport = new HashSet<String>();
        importManagerFileCsv = new ImportManagerFileCsv(this, importManagerUploadFileHelper, dctx, entityMap);
    }

    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> doImportSrv(DispatchContext dctx, Map<String, Object> context) {
        ImportManagerUploadFile obj = new ImportManagerUploadFile(dctx, context);
        obj.doImport();
        return obj.result;
    }
    
    /**
     * inizializza la entityMap
     */
    private void initEntityMap() {
    	this.entityMap = new HashMap<String, String>();
    	this.entityMap.put(E.ORGANIZATION_INTERFACE.name(), E.OrganizationInterface.name());
    	this.entityMap.put(E.ORG_RESP_INTERFACE.name(), E.OrgRespInterface.name());
        this.entityMap.put(E.PERSON_INTERFACE.name(), E.PersonInterface.name());
        this.entityMap.put(E.PERS_RESP_INTERFACE.name(), E.PersRespInterface.name());
    	this.entityMap.put(E.GL_ACCOUNT_INTERFACE.name(), E.GlAccountInterface.name());
    	this.entityMap.put(E.ACCTG_TRANS_INTERFACE.name(), E.AcctgTransInterface.name());
    	this.entityMap.put(E.WE_ROOT_INTERFACE.name(), E.WeRootInterface.name());
    	this.entityMap.put(E.WE_SCHEDA_INTERFACE.name(), E.WeSchedaInterfaceExt.name());
    	this.entityMap.put(E.WE_INTERFACE.name(), E.WeInterface.name());
    	this.entityMap.put(E.WE_NOTE_INTERFACE.name(), E.WeNoteInterface.name());
    	this.entityMap.put(E.WE_MEASURE_INTERFACE.name(), E.WeMeasureInterface.name());
    	this.entityMap.put(E.WE_ASSOC_INTERFACE.name(), E.WeAssocInterface.name());
    	this.entityMap.put(E.WE_PARTY_INTERFACE.name(), E.WePartyInterface.name());
    	this.entityMap.put(E.ALLOCATION_INTERFACE.name(), E.AllocationInterface.name());
    }

    /**
     * Main import
     */
    public void doImport() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        try {

            List<String> entityListToImport = StringUtil.split((String)getContext().get(E.entityListToImport.name()), "|");
            String checkFromETL = (String)getContext().get("checkFromETL");
            String checkOnlyUpload = (String)getContext().get(E.checkOnlyUpload.name());

            if(UtilValidate.isNotEmpty(entityListToImport)) {
                Iterator<String> entityIterator = entityListToImport.iterator();
                while (entityIterator.hasNext()) {
                    cleanParameters();

                    String entityName = entityIterator.next();
                    doImportFileFromContext(entityName, checkFromETL, startTimestamp);
                }
            } else {
                doImportFileFromContext("", checkFromETL, startTimestamp);
            }
            

            if (E.onlyUpload.name().equals(checkOnlyUpload)) {
                resultImportStandard = ServiceUtil.returnSuccess();
            } else {
                String entityListToImportContext = (String)getContext().get(E.entityListToImport.name());
                if (UtilValidate.isNotEmpty(entitiesToImport)) {
               	    for (String entityToImport : entitiesToImport) {
               	        if (UtilValidate.isNotEmpty(entityToImport)) {
               		        if (entityToImport.equals(E.OrganizationInterface.name())) {
               		            entityListToImportContext = entityListToImportContext.concat("|" +entityToImport + "|OrgRespInterface");
               		        } else if (entityToImport.equals(E.PersonInterface.name())) {
               		            entityListToImportContext = entityListToImportContext.concat("|" +entityToImport + "|PersRespInterface");
                            } else {
                                entityListToImportContext = entityListToImportContext.concat("|" + entityToImport);
                            }
               		    }
               	    }
                }
                getContext().put(E.entityListToImport.name(), entityListToImportContext);
                // NB: non forzare piu' syncMode=TRUE qui: cosi' facendo si ignorava sempre
                // BaseConfig.properties#StandardImport.syncMode, bloccando la richiesta HTTP
                // per l'intera durata dell'import (fino ad abort lato client su import lunghi).
                resultImportStandard = importManagerUploadFileHelper.runStandardImport();
            }
            addStandardImportSummary();
            result.put("resultListUploadFile", resultListUploadFile);
            Debug.log(" resultListUploadFile " + resultListUploadFile);
            if (!ServiceUtil.isSuccess(resultImportStandard)) {
                Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), (Object) ServiceUtil.getErrorMessage(resultImportStandard));
                JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR", logParameters, getLocale());
                addLogError(null, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON(), MODULE);
                result = ServiceUtil.returnError(errorGeneric.getLogMessage());
                importManagerHelper.onImportAddList(SERVICE_TYPE_ID, startTimestamp, null, getRecordElaborated(), getBlockingErrors(), getWarningMessages(), getMessages(), getImportedListPK());
                return;
            }
            result.put("resultList", resultImportStandard.get("resultList"));
            result.put("resultETLList", resultImportStandard.get("resultETLList"));
            result.put("sessionId", resultImportStandard.get("sessionId"));

        } catch (Exception e) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.errorMsg.name(), (Object) MessageUtil.getExceptionMessage(e));
            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR", logParameters, getLocale());
            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON(), MODULE);
            result.putAll(ServiceUtil.returnError(errorGeneric.getLogMessage()));
            importManagerHelper.onImportAddList(SERVICE_TYPE_ID, startTimestamp, null, getRecordElaborated(), getBlockingErrors(), getWarningMessages(), getMessages(), getImportedListPK());
        }
    }

    @SuppressWarnings("unchecked")
    private void addStandardImportSummary() {
        List<Map<String, Object>> standardResults = (List<Map<String, Object>>) resultImportStandard.get("resultList");
        if (UtilValidate.isEmpty(standardResults)) {
            return;
        }

        long records = 0L;
        long errors = 0L;
        long warnings = 0L;
        String jobLogId = null;
        for (Map<String, Object> standardResult : standardResults) {
            records += getLongResult(standardResult, ServiceLogger.RECORD_ELABORATED);
            errors += getLongResult(standardResult, ServiceLogger.BLOCKING_ERRORS);
            warnings += getLongResult(standardResult, ServiceLogger.WARNING_MESSAGES);
            if (jobLogId == null && standardResult.get(ServiceLogger.JOB_LOG_ID) != null) {
                jobLogId = standardResult.get(ServiceLogger.JOB_LOG_ID).toString();
            }
        }

        Map<String, Object> summary = new HashMap<String, Object>();
        summary.put("entityName", "StandardImport");
        summary.put(ServiceLogger.RECORD_ELABORATED, records);
        summary.put(ServiceLogger.BLOCKING_ERRORS, errors);
        summary.put(ServiceLogger.WARNING_MESSAGES, warnings);
        summary.put(ServiceLogger.JOB_LOG_ID, jobLogId);
        resultListUploadFile.add(summary);
    }

    private long getLongResult(Map<String, Object> resultMap, String fieldName) {
        Object value = resultMap.get(fieldName);
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    private void cleanStagingForUploadedKeys(String entityName) throws GenericEntityException {
        String sourceEntityName = entityMap.get(entityName);
        if (UtilValidate.isEmpty(sourceEntityName) || !sourceEntityName.endsWith(EXT)) {
            return;
        }

        List<GenericValue> uploadedValues = getDelegator().findList(sourceEntityName, null, null, null, null, false);
        Set<String> keys = new HashSet<String>();
        for (GenericValue uploadedValue : uploadedValues) {
            String key = uploadedValue.getString(E.sourceReferenceRootId.name());
            if (UtilValidate.isNotEmpty(key)) {
                keys.add(key);
            }
        }
        if (keys.isEmpty()) {
            return;
        }

        String targetEntityName = sourceEntityName.substring(0, sourceEntityName.length() - EXT.length());
        removeStagingByKey(targetEntityName, keys);
        if (E.WeSchedaInterfaceExt.name().equals(sourceEntityName)) {
            removeStagingByKey(E.WeRootInterface.name(), keys);
        }
        addLogInfo("Pulizia staging per " + keys.size() + " chiavi del file caricato completata", MODULE);
    }

    private void removeStagingByKey(String entityName, Set<String> keys) throws GenericEntityException {
        ModelEntity modelEntity = getDelegator().getModelEntity(entityName);
        if (modelEntity == null || modelEntity.getField(E.sourceReferenceRootId.name()) == null) {
            return;
        }
        int removed = getDelegator().removeByCondition(entityName,
                EntityCondition.makeCondition(E.sourceReferenceRootId.name(), EntityOperator.IN, keys));
        if (removed > 0) {
            addLogInfo("Rimossi " + removed + " record da " + entityName, MODULE);
        }
    }

    private void doImportFileFromContext(String entityName, String checkFromETL, Timestamp startTimestamp) throws GenericEntityException {
        
        String uploadedFileContentType = (String) getContext().get("_" + entityName + "UploadedFile_contentType");
        String uploadedFileName = (String) getContext().get("_" + entityName + "UploadedFile_fileName");
        String uploadedFileDatasourceId = (String) getContext().get(entityName + "DataSourceId");
        Timestamp uploadedFileRefDate = (Timestamp) getContext().get(entityName + "RefDate");
        String checkImportFromExt = (String) getContext().get(entityName + "ImportFromExt");

        if (UtilValidate.isNotEmpty(uploadedFileName)) {

            String msg = SERVICE_NAME_UPLOAD_FILE + ": elaborating file " + uploadedFileName + " with " + entityName;
            addLogInfo(msg, MODULE);

            // GN-741 aggiunto excel come default (application/octet-stream)
            if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(uploadedFileContentType) 
            || "application/vnd.ms-excel".equals(uploadedFileContentType) 
            || "application/xls".equals(uploadedFileContentType) 
            || "application/octet-stream".equals(uploadedFileContentType)) {
                if (!importManagerUploadFileHelper.checkFromETL(entityName, checkFromETL)) {
                    if (uploadedFileName.toLowerCase().endsWith(".csv")) {
                        entitiesToImport = importManagerFileCsv.importCsv(entitiesToImport, entityName, uploadedFileName, uploadedFileDatasourceId, uploadedFileRefDate);
                    } else {
                        if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(uploadedFileContentType)) {
                            /** eseguo importazione file */
                            doImportFileXlsx(entityName, uploadedFileName, uploadedFileContentType, uploadedFileDatasourceId, uploadedFileRefDate);
                        } else {
                            /** eseguo importazione file */
                            doImportFileXls(entityName, uploadedFileName, uploadedFileContentType, uploadedFileDatasourceId, uploadedFileRefDate);
                        }                               
                    }
                    msg = "IMPORT FILE COMPLETED " + entityName;
                    addLogInfo(msg, MODULE);

                        cleanStagingForUploadedKeys(entityName);
                    
                    // Copy from WeSchedaInterfaceExt to WeSchedaInterface and add to import list
                    if (entitiesToImport.contains(E.WeSchedaInterfaceExt.name())) {
                        addLogInfo("Copying from WeSchedaInterfaceExt to WeSchedaInterface", MODULE);
                        createInterfaceValueFromExt(E.WeSchedaInterface.name());
                        addLogInfo("Copy from WeSchedaInterfaceExt to WeSchedaInterface completed", MODULE);
                        
                        // Add WeSchedaInterface to entitiesToImport so it gets processed by standardImport job
                        entitiesToImport.add(E.WeSchedaInterface.name());
                        addLogInfo("Added WeSchedaInterface to entitiesToImport list", MODULE);
                        
                        // Copy from WeSchedaInterface to WeRootInterface
                        addLogInfo("Copying from WeSchedaInterface to WeRootInterface", MODULE);
                        copyWeSchedaToWeRoot();
                        addLogInfo("Copy from WeSchedaInterface to WeRootInterface completed", MODULE);
                        
                        // Add WeRootInterface to entitiesToImport so it gets processed by standardImport job
                        entitiesToImport.add(E.WeRootInterface.name());
                        addLogInfo("Added WeRootInterface to entitiesToImport list", MODULE);
                    }
                }
            } else {
                msg = "File format is not correct: " + uploadedFileContentType + " : " + uploadedFileName;
                addLogError(msg, MODULE);
            }

            /** Esegue standardImport e Carico Log **/
            resultListUploadFile.add(importManagerHelper.onImportAddList(SERVICE_TYPE_UPLOAD_FILE, startTimestamp, entityName, getRecordElaborated(), getBlockingErrors(), getWarningMessages(), getMessages(), getImportedListPK()));
        
        } else if(UtilValidate.isNotEmpty(checkImportFromExt) && (entityName + EXT).equals(checkImportFromExt)) {
            createInterfaceValueFromExt(entityName);
            
        	/** Esegue standardImport e Carico Log **/
            resultListUploadFile.add(importManagerHelper.onImportAddList(SERVICE_TYPE_UPLOAD_FILE, startTimestamp, entityName, getRecordElaborated(), getBlockingErrors(), getWarningMessages(), getMessages(), getImportedListPK()));
        }
    }
    
    /**
     * Copia gli elementi della tabella entityName + "Ext" nella tabella entityName
     * 
     * @param entityName
     * @throws GenericEntityException
     */
    public void createInterfaceValueFromExt(String entityName) throws GenericEntityException {
        
        List<GenericValue> entityExtList = getDelegator().findList(entityName + ImportManagerUploadFile.EXT, null, null, null, null, false);
        if (UtilValidate.isNotEmpty(entityExtList)) {
            long count = 0;
            for (GenericValue gvExt: entityExtList) {
                try {
                    count ++;
                    Debug.logInfo("createInterfaceValueFromExt: Creating " + entityName + " from " + entityName + "Ext, count=" + count, MODULE);
                    GenericValue gv = getDelegator().makeValue(entityName, gvExt);
                    Debug.logInfo("createInterfaceValueFromExt: makeValue OK for " + entityName, MODULE);
                    gv.set(E.seq.name(), Long.valueOf(count));
                    Debug.logInfo("createInterfaceValueFromExt: seq set to " + count + " for " + entityName, MODULE);
                    
                    // Generate id for entities that require it
                    if (E.GlAccountInterface.name().equals(entityName) || E.AcctgTransInterface.name().equals(entityName) || E.WeRootInterface.name().equals(entityName) || E.WeInterface.name().equals(entityName) || E.WeMeasureInterface.name().equals(entityName) || E.WeSchedaInterface.name().equals(entityName)) {
                        Debug.logInfo("createInterfaceValueFromExt: Generating ID for " + entityName, MODULE);
                        String id = getDelegator().getNextSeqId(entityName);
                        Debug.logInfo("createInterfaceValueFromExt: Generated ID=" + id + " for " + entityName, MODULE);
                        gv.set(E.id.name(), id);
                        Debug.logInfo("createInterfaceValueFromExt: ID set to " + id + " for " + entityName, MODULE);
                    }
                    
                    if (E.WeSchedaInterface.name().equals(entityName)) {
                        removeStagingByKey(entityName, UtilMisc.toSet(gv.getString(E.sourceReferenceRootId.name())));
                    }
                    Debug.logInfo("createInterfaceValueFromExt: About to call gv.create() for " + entityName, MODULE);
                    gv.create();
                    // NB: non incrementare recordElaborated qui: la riga e' gia' stata contata in importValue()
                    // durante il parsing del file Excel; questa e' solo la migrazione interna Ext->Interface.
                    String msg = "Creating element: " + TakeOverUtil.toString(gv);
                    addLogInfo(msg, MODULE, TakeOverUtil.toString(gv));
                    gvExt.remove();
                } catch (GenericEntityException e) {
                    Map<String, Object> logParameters = UtilMisc.toMap(E.entityName.name(), (Object)entityName, E.record.name(), gvExt, E.errorMsg.name(), MessageUtil.getExceptionMessage(e));
                    JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_ENTITY_RECORD", logParameters, getLocale());
                    addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON(), MODULE);
                } 
            }
        }
    }

    /**
     * Copies records from WeSchedaInterface to WeRootInterface
     * This is needed for the evaluation card import flow where:
     * Excel -> WeSchedaInterfaceExt -> WeSchedaInterface -> WeRootInterface -> standardImport job
     * 
     * IMPORTANT: Checks for existing records to avoid duplicates
     * 
     * @throws GenericEntityException
     */
    public void copyWeSchedaToWeRoot() throws GenericEntityException {
        addLogInfo("Starting copy from WeSchedaInterface to WeRootInterface", MODULE);
        
        List<GenericValue> weSchedaList = getDelegator().findList(E.WeSchedaInterface.name(), null, null, null, null, false);
        
        if (UtilValidate.isNotEmpty(weSchedaList)) {
            addLogInfo("Found " + weSchedaList.size() + " records in WeSchedaInterface to process", MODULE);
            long countCreated = 0;
            long countSkipped = 0;
            
            for (GenericValue weScheda : weSchedaList) {
                try {
                    String sourceReferenceRootId = weScheda.getString(E.sourceReferenceRootId.name());
                    
                    // Check if WeRootInterface record already exists with this sourceReferenceRootId
                    EntityCondition condition = EntityCondition.makeCondition(E.sourceReferenceRootId.name(), sourceReferenceRootId);
                    List<GenericValue> existingRecords = getDelegator().findList(
                        E.WeRootInterface.name(), 
                        condition,
                        null,
                        null,
                        null,
                        false
                    );
                    
                    if (UtilValidate.isNotEmpty(existingRecords)) {
                        // Record already exists, skip creation
                        countSkipped++;
                        String msg = "Skipped WeRootInterface creation: record already exists for sourceReferenceRootId=" + sourceReferenceRootId;
                        addLogInfo(msg, MODULE);
                        Debug.logInfo("copyWeSchedaToWeRoot: " + msg, MODULE);
                        continue;
                    }
                    
                    // Record doesn't exist, create it
                    countCreated++;
                    
                    // Create new WeRootInterface record
                    GenericValue weRoot = getDelegator().makeValue(E.WeRootInterface.name());
                    
                    // Generate sequence ID
                    String id = getDelegator().getNextSeqId(E.WeRootInterface.name());
                    weRoot.set(E.id.name(), id);
                    weRoot.set(E.seq.name(), Long.valueOf(countCreated));
                    
                    // Copy common fields from WeSchedaInterface to WeRootInterface
                    weRoot.set(E.sourceReferenceRootId.name(), sourceReferenceRootId);
                    weRoot.set(E.dataSource.name(), weScheda.get(E.dataSource.name()));
                    weRoot.set(E.weContext.name(), weScheda.get(E.weContext.name()));
                    weRoot.set(E.workEffortTypeId.name(), weScheda.get(E.workEffortTypeId.name()));
                    weRoot.set(E.operationType.name(), weScheda.get(E.operationType.name()));
                    weRoot.set(E.statusItemDesc.name(), weScheda.get(E.statusItemDesc.name()));
                    weRoot.set(E.createSnapshot.name(), weScheda.get(E.createSnapshot.name()));
                    weRoot.set(E.snapshotDescription.name(), weScheda.get(E.snapshotDescription.name()));
                    weRoot.set(E.workEffortName.name(), weScheda.get(E.workEffortName.name()));
                    weRoot.set(E.description.name(), weScheda.get(E.description.name()));
                    weRoot.set(E.etch.name(), weScheda.get(E.etch.name()));
                    weRoot.set(E.orgTypeDesc.name(), weScheda.get(E.orgTypeDesc.name()));
                    weRoot.set(E.orgTypeCode.name(), weScheda.get(E.orgTypeCode.name()));
                    weRoot.set(E.orgDesc.name(), weScheda.get(E.orgDesc.name()));
                    weRoot.set(E.orgCode.name(), weScheda.get(E.orgCode.name()));
                    weRoot.set(E.estimatedStartDate.name(), weScheda.get(E.estimatedStartDate.name()));
                    weRoot.set(E.estimatedCompletionDate.name(), weScheda.get(E.estimatedCompletionDate.name()));
                    weRoot.set(E.workEffortPurposeTypeDesc.name(), weScheda.get(E.workEffortPurposeTypeDesc.name()));
                    weRoot.set(E.workEffortPurposeTypeId.name(), weScheda.get(E.workEffortPurposeTypeId.name()));
                    weRoot.set(E.specialTerms.name(), weScheda.get(E.specialTerms.name()));
                    
                    // Create the record
                    weRoot.create();
                    // NB: non incrementare recordElaborated qui: la riga e' gia' stata contata in importValue()
                    // durante il parsing del file Excel; questa e' solo la migrazione interna Interface->Root.
                    
                    String msg = "Created WeRootInterface record: " + TakeOverUtil.toString(weRoot);
                    addLogInfo(msg, MODULE, TakeOverUtil.toString(weRoot));
                    
                    Debug.logInfo("copyWeSchedaToWeRoot: Created WeRootInterface record with id=" + id + 
                                  " for sourceReferenceRootId=" + sourceReferenceRootId, MODULE);
                    
                } catch (GenericEntityException e) {
                    Map<String, Object> logParameters = UtilMisc.toMap(
                        E.entityName.name(), (Object)E.WeRootInterface.name(), 
                        E.record.name(), weScheda, 
                        E.errorMsg.name(), MessageUtil.getExceptionMessage(e)
                    );
                    JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_ENTITY_RECORD", logParameters, getLocale());
                    addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON(), MODULE);
                }
            }
            
            addLogInfo("Completed copy from WeSchedaInterface to WeRootInterface: " + countCreated + " records created, " + countSkipped + " records skipped (already exist)", MODULE);
        } else {
            addLogInfo("No records found in WeSchedaInterface to copy", MODULE);
        }
    }

    /**
     * eliminazione preventiva dalla tabella di interfaccia dei records con status KO e log dell'operazione
     * @param entityName
     */
    public void deleteKoRecordsFromInterface(String entityName) {
        try {
            int deleted = getDelegator().removeByCondition(entityName, EntityCondition.makeCondition("stato", "KO"));
            if (deleted > 0) {
                Map<String, Object> logParamImportInterface = UtilMisc.toMap(E.count.name(), (Object)deleted, E.entityName.name(), entityName);
                JobLogLog importInterface = new JobLogLog().initLogCode(RESOURCE_LABEL, "DELETE_RECORDS", logParamImportInterface, getLocale());
                addLogInfo(importInterface.getLogCode(), importInterface.getLogMessage(), null, RESOURCE_LABEL, importInterface.getParametersJSON(), MODULE);
            }
        } catch (GenericEntityException e) {
            Map<String, Object> logParamImportInterface = UtilMisc.toMap(E.entityName.name(), (Object) entityName, E.errorMsg.name(), (Object) MessageUtil.getExceptionMessage(e));
            JobLogLog importInterface = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERR_DELETE_RECORDS", logParamImportInterface, getLocale());
            addLogError(e, importInterface.getLogCode(), importInterface.getLogMessage(), null, RESOURCE_LABEL, importInterface.getParametersJSON(), MODULE);
        }
    }

    /**
     * 
     * @param entityName
     * @param uploadedFileName
     * @param uploadedFileContentType 
     * @param uploadedFileRefDate 
     * @param uploadedFileDatasourceId 
     */
    protected void doImportFileXls(String entityName, String uploadedFileName, String uploadedFileContentType, String uploadedFileDatasourceId, Timestamp uploadedFileRefDate) {
        
        ByteBuffer buffer = (ByteBuffer)getContext().get(entityName + "UploadedFile");
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());

        HSSFWorkbook workbook;
        try {
            workbook = new HSSFWorkbook(bais);

            //Get first sheet from the workbook
            HSSFSheet worksheet = workbook.getSheetAt(0);

            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = worksheet.iterator();
            doImportRowXls(entityName, uploadedFileName, rowIterator, worksheet, getFirstNotEmptyRow(worksheet), uploadedFileDatasourceId, uploadedFileRefDate);

        } catch (Exception e) {
            String errMsg = "Import failed for file " + uploadedFileName;
            addLogError(e, errMsg, MODULE);
        }
    }
    
    /**
     * ritorna prima riga non vuota dopo header di HSSFSheet
     * @param worksheet
     * @return
     */
    private HSSFRow getFirstNotEmptyRow(HSSFSheet worksheet) {
    	Iterator<Row> iter = worksheet.iterator();
    	int i = -1;
    	while(iter.hasNext()) {
    		i++;
    		Row row = iter.next();
    		if (i > 0 && ! ExcelReaderUtil.isEmpty(row)) {
    			return worksheet.getRow(i);
    		}
    	}
    	return null;
    }

    /**
     * 
     * @param entityName
     * @param uploadedFileName
     * @param uploadedFileContentType 
     * @param uploadedFileRefDate 
     * @param uploadedFileDatasourceId 
     */
    protected void doImportFileXlsx(String entityName, String uploadedFileName, String uploadedFileContentType, String uploadedFileDatasourceId, Timestamp uploadedFileRefDate) {

        ByteBuffer buffer = (ByteBuffer)getContext().get(entityName + "UploadedFile");
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
        ZipSecureFile.setMinInflateRatio(0d);
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(bais);

            //Get first sheet from the workbook
            XSSFSheet worksheet = workbook.getSheetAt(0);

            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = worksheet.iterator();
            doImportRowXlsx(entityName, uploadedFileName, rowIterator, worksheet, getFirstNotEmptyRow(worksheet), uploadedFileDatasourceId, uploadedFileRefDate);

        } catch (Exception e) {
            String errMsg = "Import failed for file " + uploadedFileName;
            addLogError(e, errMsg, MODULE);
        }
    }
    
    /**
     * ritorna prima riga non vuota dopo header di XSSFSheet
     * @param worksheet
     * @return
     */
    private XSSFRow getFirstNotEmptyRow(XSSFSheet worksheet) {
    	Iterator<Row> iter = worksheet.iterator();
    	int i = -1;
    	while(iter.hasNext()) {
    		i++;
    		Row row = iter.next();
    		if (i > 0 && ! ExcelReaderUtil.isEmpty(row)) {
    			return worksheet.getRow(i);
    		}
    	}
    	return null;
    }
    
    /**
     * importa la singola riga xls
     * @param entityName
     * @param uploadedFileName
     * @param rowIterator
     * @param worksheet
     * @param firstRow
     * @param uploadedFileRefDate 
     * @param uploadedFileDatasourceId 
     * @throws GeneralException 
     */
    private void doImportRowXls(String entityName, String uploadedFileName, Iterator<Row> rowIterator, HSSFSheet worksheet, HSSFRow firstRow, String uploadedFileDatasourceId, Timestamp uploadedFileRefDate) throws GeneralException {
    	Row rowHeader = getRowHeader(rowIterator);
    	String dataSource = getDataSource(rowHeader, firstRow, entityName, uploadedFileDatasourceId);
		if(UtilValidate.isEmpty(dataSource)) {
		    Map<String, Object> parameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) uploadedFileDatasourceId, E.entityName.name(), entityName, E.filename.name(), uploadedFileName);
            JobLogLog noDataSourceFound = new JobLogLog().initLogCode(RESOURCE_LABEL, "NO_DATA_SOURCE_FOUND", parameters, getLocale());
            throw new ImportException(entityName, null, noDataSourceFound);
    	}
    	doImportRow(entityName, uploadedFileName, rowIterator, rowHeader, worksheet, dataSource, uploadedFileRefDate);
    }
    
    /**
     * importa la singola riga xlsx
     * @param entityName
     * @param uploadedFileName
     * @param rowIterator
     * @param worksheet
     * @param firstRow
     * @throws GeneralException 
     */
    private void doImportRowXlsx(String entityName, String uploadedFileName, Iterator<Row> rowIterator, XSSFSheet worksheet, XSSFRow firstRow, String uploadedFileDatasourceId, Timestamp uploadedFileRefDate) throws GeneralException {
    	Row rowHeader = getRowHeader(rowIterator);
    	String dataSource = getDataSource(rowHeader, firstRow, entityName, uploadedFileDatasourceId);
    	if(UtilValidate.isEmpty(dataSource)) {
		    Map<String, Object> parameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) uploadedFileDatasourceId, E.entityName.name(), entityName, E.filename.name(), uploadedFileName);
            JobLogLog noDataSourceFound = new JobLogLog().initLogCode(RESOURCE_LABEL, "NO_DATA_SOURCE_FOUND", parameters, getLocale());
            throw new ImportException(entityName, null, noDataSourceFound);
		}
    	doImportRow(entityName, uploadedFileName, rowIterator, rowHeader, worksheet, dataSource, uploadedFileRefDate);
    }

    /**
     * esegue l import
     * @param entityName
     * @param uploadedFileName
     * @param rowIterator
     * @param rowHeader
     * @param worksheet
     * @param dataSourceColumnIndex
     * @param dataSource
     */
    private void doImportRow(String entityName, String uploadedFileName, Iterator<Row> rowIterator, Row rowHeader, Object worksheet, String dataSource, Timestamp uploadedFileRefDate) {   	
    	Row row = null;
        try {           
            	List<GenericValue> standardImportFieldConfigList = fieldConfig.getStandardImportFieldConfigItems(dataSource);
            	if (UtilValidate.isNotEmpty(standardImportFieldConfigList)) {
            		for (GenericValue standardImportFieldConfigItem : standardImportFieldConfigList) {
            			if (UtilValidate.isNotEmpty(standardImportFieldConfigItem)) {
                            deleteKoRecordsFromInterface(this.entityMap.get(standardImportFieldConfigItem.getString(E.standardInterface.name())));
            			    
            				Map<String, String> mapField = fieldConfig.getMapField(standardImportFieldConfigItem.getString(E.standardInterface.name()), dataSource, standardImportFieldConfigItem.getLong(E.interfaceSeq.name()));
                            Map<String, Map<String, String>> mapValue = fieldConfig.getMapValue(standardImportFieldConfigItem.getString(E.standardInterface.name()), dataSource, standardImportFieldConfigItem.getLong(E.interfaceSeq.name()));
                            
                            Iterator<Row> iterator = null;
                            if (worksheet instanceof HSSFSheet) {
                            	iterator = ((HSSFSheet) worksheet).iterator();
                            } else if (worksheet instanceof XSSFSheet) {
                            	iterator = ((XSSFSheet) worksheet).iterator();
                            }
                            if (iterator != null) {
                            	iterator.next();
                                int i = 0;
                            	while (iterator.hasNext()) {
                                    i++;
                            		row = iterator.next();
                                    if (!ExcelReaderUtil.isEmpty(row)) {
                                    	String entityInterfaceName = this.entityMap.get(standardImportFieldConfigItem.getString(E.standardInterface.name()));
                                    	if (! entitiesToImport.contains(entityInterfaceName) && ! entityInterfaceName.equals(entityName)) {
                                    		entitiesToImport.add(entityInterfaceName);
                                    	}
                                    	ModelEntity modelEntity = getDelegator().getModelEntity(entityInterfaceName);
                                        GenericValue element = importManagerUploadFileHelper.getElementRow(rowHeader, row, modelEntity, mapField, mapValue, dataSource, uploadedFileRefDate);
                                        if (!ExcelReaderUtil.isEmpty(element) && !elementExists(entityInterfaceName, element)) {
                                        	try {
                                        		new TransactionRunner(MODULE, true, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
                                            		@Override
                                            		public void run(){
                                            			try {
                                            				importValue(element, entityInterfaceName);
                                            			}
                                            			catch (Exception e) {
                                            				e.printStackTrace();
                                            			}
                                            		}
                                            		}).execute();
                                        	}
                                        	catch (Exception e) {
                                				e.printStackTrace();
                                				String errMsg = "Import failed for file " + uploadedFileName;
                                				errMsg += "# row: " + row.getRowNum();
                                				addLogError(e, errMsg, MODULE);
                                			}
                                        }
                                    }
                                }
                            }
            			}
            		}
            	} else {
            		importFile(entityName, uploadedFileName, rowIterator, rowHeader, dataSource, uploadedFileRefDate);
            	}
        } catch (Exception e) {
            String errMsg = "Import failed for file " + uploadedFileName;
            if (UtilValidate.isNotEmpty(row)) {
                errMsg += "# row: " + row.getRowNum();
            }
            addLogError(e, errMsg, MODULE);
        }

    }
    
    /**
     * ritorna il rowHeader
     * @param rowIterator
     * @return
     */
    private Row getRowHeader(Iterator<Row> rowIterator) {
        Row rowHeader = null;
        if (rowIterator.hasNext()) {
            rowHeader = rowIterator.next();
        }
        return rowHeader;
    }
    
    /**
     * ritorna indice della colonna dataSource
     * @param rowHeader
     * @return
     */
    private Integer getColumnIndex(Row rowHeader, String str) {
        Integer columnIndex = null;
        if(UtilValidate.isNotEmpty(rowHeader)){
            for (Cell head : rowHeader){
            	if(str.equals(head.toString())){
            		columnIndex = head.getColumnIndex();
            		break;
            	}
            } 
        }
        return columnIndex;
    }

    /**
     * ricava il dataSource dall' xls, se e' presente una colonna dataSouce,
     * oppure prende quello ricevuto dal servizio
     * oppure ricava il dataSource dall' xls, se e' presente una colonna con externalFieldName del DEFAULT,
     * oppure usa il valore di default per il DEFAULT
     * @param rowHeader
     * @param firstRow
     * @param entityName
     * @param uploadedFileDatasourceId 
     * @return
     * @throws GeneralException
     */
    private String getDataSource(Row rowHeader, Object firstRow, String entityName, String uploadedFileDatasourceId) throws GeneralException {
        // search colonum with name  dataSource
        Integer columnIndex = getColumnIndex(rowHeader, E.dataSource.name());
    	if (columnIndex != null) {
    	    String tmpDatasource = getCellValue(firstRow, columnIndex);
    	    GenericValue stfcDataSourceGenericValue = fieldConfig.getDataSource(tmpDatasource);
    	    if (UtilValidate.isNotEmpty(stfcDataSourceGenericValue)) {
    	        Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) tmpDatasource);
                JobLogLog jllDS = new JobLogLog().initLogCode(RESOURCE_LABEL, "DS_FOUND", logParameters, getLocale());
                addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
                return tmpDatasource;
    	    } else {
    	        Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) tmpDatasource);
                JobLogLog jllDS = new JobLogLog().initLogCode(RESOURCE_LABEL, "DS_NOT_FOUND", logParameters, getLocale());
                addLogWarning(jllDS.getLogCode(), jllDS.getLogMessage(), null, RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
    	    }
    	}
    	
    	// search dataSource from context, if exists
    	GenericValue stfcDataSourceGenericValue = fieldConfig.getDataSource(uploadedFileDatasourceId);
        if (UtilValidate.isNotEmpty(stfcDataSourceGenericValue)) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) uploadedFileDatasourceId);
            JobLogLog jllDS = new JobLogLog().initLogCode(RESOURCE_LABEL, "DS_FOUND_DB", logParameters, getLocale());
            addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
            return uploadedFileDatasourceId;
        }
        
        // altrimente prende quello presente nel file con un altro nome oppure il defaultValue
        stfcDataSourceGenericValue = fieldConfig.getStfcDataSourceDefault(entityName);
        if (UtilValidate.isNotEmpty(stfcDataSourceGenericValue)) {
        	String externalFieldName = stfcDataSourceGenericValue.getString(E.externalFieldName.name());
        	if (UtilValidate.isNotEmpty(externalFieldName)) {
        		columnIndex = getColumnIndex(rowHeader, externalFieldName);
        	}
        	if (columnIndex != null) {
        	    Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) getCellValue(firstRow, columnIndex), E.entityName.name(), entityName, E.externalFieldName.name(), externalFieldName);
                JobLogLog jllDS = new JobLogLog().initLogCode(RESOURCE_LABEL, "DS_FOUND_FILE", logParameters, getLocale());
                addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
                return getCellValue(firstRow, columnIndex);
        	}
        	
        	Map<String, Object> logParameters = UtilMisc.toMap(E.dataSourceId.name(), (Object) stfcDataSourceGenericValue.getString(E.defaultValue.name()));
            JobLogLog jllDS = new JobLogLog().initLogCode(RESOURCE_LABEL, "DS_FOUND_DEF", logParameters, getLocale());
            addLogDebug(jllDS.getLogCode(), jllDS.getLogMessage(), null, RESOURCE_LABEL, jllDS.getParametersJSON(), MODULE);
            return stfcDataSourceGenericValue.getString(E.defaultValue.name());
        }
    	return "";
    }
    
    /**
     * ricava il valore della cella dall xls
     * @param firstRow
     * @param columnIndex
     * @return
     */
    private String getCellValue(Object firstRow, Integer columnIndex) {
        if (firstRow instanceof HSSFRow) {
            HSSFCell cell = ((HSSFRow)firstRow).getCell(columnIndex);
            if (cell != null) {
                return cell.getStringCellValue();
            }
        } else if (firstRow instanceof XSSFRow) {
            XSSFCell cell = ((XSSFRow)firstRow).getCell(columnIndex);
            if (cell != null) {
                return cell.getStringCellValue();
            }
        }
        return "";	
    }
    
    /**
     * verifica per chiave logica se il record essite nella tabella interfaccia
     * @param entityName
     * @param element
     * @return
     * @throws Exception
     */
    public boolean elementExists(String entityName, GenericValue element) throws Exception {
    	List<EntityCondition> condList = new ArrayList<EntityCondition>();
    	if (E.GlAccountInterface.name().equals(entityName)) {
    		String accountCode = element.getString(E.accountCode.name());
    		String accountName = element.getString(E.accountName.name());
    		if (! ValidationUtil.isEmptyOrNA(accountCode)) {
    			condList.add(EntityCondition.makeCondition(E.accountCode.name(), accountCode));
    		} else {
    			condList.add(EntityCondition.makeCondition(E.accountName.name(), accountName));
    		}
    	}
    	if (E.WeInterface.name().equals(entityName)) {
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootId.name(), element.getString(E.sourceReferenceRootId.name())));
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceId.name(), element.getString(E.sourceReferenceId.name())));
    	}
    	if (E.WeAssocInterface.name().equals(entityName)) {
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootId.name(), element.getString(E.sourceReferenceRootId.name())));
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootIdFrom.name(), element.getString(E.sourceReferenceRootIdFrom.name())));
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootIdTo.name(), element.getString(E.sourceReferenceRootIdTo.name())));
    		if (! ValidationUtil.isEmptyOrNA(element.getString(E.sourceReferenceIdFrom.name()))) {
    			condList.add(EntityCondition.makeCondition(E.sourceReferenceIdFrom.name(), element.getString(E.sourceReferenceIdFrom.name())));
    		} else {
    			condList.add(EntityCondition.makeCondition(E.workEffortNameFrom.name(), element.getString(E.workEffortNameFrom.name())));
    		}
    		if (! ValidationUtil.isEmptyOrNA(element.getString(E.sourceReferenceIdTo.name()))) {
    			condList.add(EntityCondition.makeCondition(E.sourceReferenceIdTo.name(), element.getString(E.sourceReferenceIdTo.name())));
    		} else {
    			condList.add(EntityCondition.makeCondition(E.workEffortNameTo.name(), element.getString(E.workEffortNameTo.name())));
    		}
    		condList.add(EntityCondition.makeCondition(E.workEffortAssocTypeCode.name(), element.getString(E.workEffortAssocTypeCode.name())));
    	}
    	if (E.WeMeasureInterface.name().equals(entityName)) {
    		String accountCode = element.getString(E.accountCode.name());
    		String accountName = element.getString(E.accountName.name());
    		if (! ValidationUtil.isEmptyOrNA(accountCode)) {
    			condList.add(EntityCondition.makeCondition(E.accountCode.name(), accountCode));
    		} else {
    			condList.add(EntityCondition.makeCondition(E.accountName.name(), accountName));
    		}
    		String uomDescr = element.getString(E.uomDescr.name());
    		String uomDescrLang = element.getString(E.uomDescrLang.name());
    		if (! ValidationUtil.isEmptyOrNA(uomDescr)) {
    			condList.add(EntityCondition.makeCondition(E.uomDescr.name(), uomDescr));
    		}
    		if (! ValidationUtil.isEmptyOrNA(uomDescrLang)) {
    			condList.add(EntityCondition.makeCondition(E.uomDescrLang.name(), uomDescrLang));
    		}
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootId.name(), element.getString(E.sourceReferenceRootId.name())));
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceId.name(), element.getString(E.sourceReferenceId.name())));
    	}
    	if (E.WeNoteInterface.name().equals(entityName)) {
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootId.name(), element.getString(E.sourceReferenceRootId.name())));
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceId.name(), element.getString(E.sourceReferenceId.name())));
    		condList.add(EntityCondition.makeCondition(E.noteName.name(), element.getString(E.noteName.name())));
    	}
    	if (E.WePartyInterface.name().equals(entityName)) {
    		condList.add(EntityCondition.makeCondition(E.sourceReferenceRootId.name(), element.getString(E.sourceReferenceRootId.name())));
    		// FIX: sourceReferenceId può essere NULL per i record WEM_EVAL_MANAGER
    		// In questo caso, la chiave logica è: sourceReferenceRootId + roleTypeId + partyCode
    		if (! ValidationUtil.isEmptyOrNA(element.getString(E.sourceReferenceId.name()))) {
    			condList.add(EntityCondition.makeCondition(E.sourceReferenceId.name(), element.getString(E.sourceReferenceId.name())));
    		}
    		condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), element.getString(E.roleTypeId.name())));
    		condList.add(EntityCondition.makeCondition(E.partyCode.name(), element.getString(E.partyCode.name())));
    	}
    	if (UtilValidate.isNotEmpty(condList)) {
    		List<GenericValue> interfaceList = getDelegator().findList(entityName, EntityCondition.makeCondition(condList), null, null, null, false);
    		return UtilValidate.isNotEmpty(interfaceList);
    	}
    	return false;
    }
    
    /**
     * importa la riga
     * @param entityName
     * @param uploadedFileName
     * @param rowIterator
     * @param rowHeader
     * @param dataSource 
     * @param uploadedFileRefDate
     */
    private void importFile(String entityName, String uploadedFileName, Iterator<Row> rowIterator, Row rowHeader, String dataSource, Timestamp uploadedFileRefDate) {
    	Row row = null;
    	try {
    		ModelEntity modelEntity = getDelegator().getModelEntity(entityName);
    		Map<String, String> mapField = new HashMap<String, String>();
    		Map<String, Map<String, String>> mapValue = new HashMap<String, Map<String, String>>();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (!ExcelReaderUtil.isEmpty(row)) {
                    GenericValue element = importManagerUploadFileHelper.getElementRow(rowHeader, row, modelEntity, 
                    		mapField, mapValue, dataSource, uploadedFileRefDate);
                    if (!ExcelReaderUtil.isEmpty(element)) {
                        importValue(element, entityName);
                    }
                }
            }   		
    	} catch (Exception e) {
            String errMsg = "Import failed for file " + uploadedFileName;
            if (UtilValidate.isNotEmpty(row)) {
                errMsg += "# row: " + row.getRowNum();
            }
            addLogError(e, errMsg, MODULE);
        }
    }

    /**
     * 	
     * @param element
     * @param entityName
     * @param uploadedFileRefDate 
     * @param uploadedFileRefDate 
     */
    protected void importValue(GenericValue element, String entityName) {
        String msg = "";

        try {
            setRecordElaborated(getRecordElaborated() + 1);         
            
            Map<String, Object> key = null;
            try {
                key = EntityNameStdImportEnum.getLogicalPrimaryKey(entityName, element);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            EntityCondition entityCondition = EntityCondition.makeCondition(key);
            List<GenericValue> existElementList = getDelegator().findList(entityName, entityCondition, null, null, null, false);
            GenericValue existElement = EntityUtil.getFirst(existElementList);
            msg = "Search " + entityName + " with entityCondition: " + entityCondition + " , found : " + existElement;
            addLogInfo(msg, MODULE, TakeOverUtil.toString(element));
            
            // Skip WePartyInterface records with NULL source_reference_root_id (empty RUOLI sheet)
            if (E.WePartyInterface.name().equals(entityName) && UtilValidate.isEmpty(element.getString(E.sourceReferenceRootId.name()))) {
                msg = "Skipping WePartyInterface record with NULL source_reference_root_id (empty RUOLI sheet)";
                addLogInfo(msg, MODULE, TakeOverUtil.toString(element));
                return;
            }
            
            element.set(E.seq.name(), Long.valueOf(getRecordElaborated()));
            if (UtilValidate.isEmpty(existElement)) {
                Debug.logInfo("importValue: entityName=" + entityName + ", checking for ID generation", MODULE);
                if (E.GlAccountInterface.name().equals(entityName) || E.AcctgTransInterface.name().equals(entityName) || E.WeRootInterface.name().equals(entityName) || E.WeInterface.name().equals(entityName) || E.WeMeasureInterface.name().equals(entityName) || E.WeSchedaInterface.name().equals(entityName) || E.WeSchedaInterfaceExt.name().equals(entityName)) {
                    Debug.logInfo("importValue: Generating ID for entityName=" + entityName, MODULE);
                    String id = getDelegator().getNextSeqId(entityName);
                    Debug.logInfo("importValue: Generated ID=" + id + " for entityName=" + entityName, MODULE);
                    element.set(E.id.name(), id);
                    Debug.logInfo("importValue: ID set successfully", MODULE);
                }
                Debug.logInfo("importValue: About to call getDelegator().create() for entityName=" + entityName, MODULE);
                getDelegator().create(element);
                msg = "Creating element: " + TakeOverUtil.toString(element);
            } else {
                String id = existElement.getString(E.id.name());
                element.set(E.id.name(), id);
                getDelegator().store(element);
                msg = "Update element: " + TakeOverUtil.toString(element);
            }
            getImportedListPK().add(key);

            addLogInfo(msg, MODULE, TakeOverUtil.toString(element));
        } catch (GenericEntityException e) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.entityName.name(), (Object)entityName, E.record.name(), element, E.errorMsg.name(), MessageUtil.getExceptionMessage(e));
            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_ENTITY_RECORD", logParameters, getLocale());
            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON(), MODULE);
        } catch (Exception e) {
            Map<String, Object> logParameters = UtilMisc.toMap(E.entityName.name(), (Object)entityName, E.record.name(), element, E.errorMsg.name(), MessageUtil.getExceptionMessage(e));
            JobLogLog errorGeneric = new JobLogLog().initLogCode(RESOURCE_LABEL, "ERROR_RECORD", logParameters, getLocale());
            addLogError(e, errorGeneric.getLogCode(), errorGeneric.getLogMessage(), null, RESOURCE_LABEL, errorGeneric.getParametersJSON(), MODULE);
        }
    }

}
