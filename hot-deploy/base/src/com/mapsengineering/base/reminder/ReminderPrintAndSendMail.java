package com.mapsengineering.base.reminder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.birt.BirtFactory;
import org.ofbiz.birt.BirtWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.birt.service.BirtService;
import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.async.AsyncJobUtil;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.OfbizServiceContext;


public class ReminderPrintAndSendMail extends GenericService {

    public static final String MODULE = ReminderPrintAndSendMail.class.getName();
    public static final String SERVICE_NAME = "reminderPrintAndSendMail";
    public static final String SERVICE_TYPE = "REMINDER_PRINT_SEND_MAIL";
    private static final String DEFAULT_CONTENT_MIME_TYPE_ID = "text/plain";
    private static final String DEFAULT_REPORT_CONTENT_MIME_TYPE_ID = "application/pdf";
    private static final String DEFAULT_REPORT_OUTPUT_FORMAT = "pdf";
    private static final String XSLX_REPORT_CONTENT_MIME_TYPE_ID = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String QUERYEXECUTOR_TMP_FILE_PREFIX = "qex";
    
    private String enabledSendMail;
    private Map<String, Object> birtContext;
    
    private DispatchContext dctx;
    //private Map<String, Object> context;
    
       
    public ReminderPrintAndSendMail(DispatchContext dctx, Map<String, Object> context, JobLogger jobLogger) {
        super(dctx, context, jobLogger, SERVICE_NAME, SERVICE_TYPE, MODULE);
        enabledSendMail = (String) context.get(E.enabledSendMail.name());        
              
        birtContext = new HashMap<String, Object>();
        birtContext.put(E.reportContentId.name(), context.get(E.reportContentId.name()));
        birtContext.put(E.outputFormat.name(), getElementOrDefault((String)context.get(E.outputFormat.name()), DEFAULT_REPORT_OUTPUT_FORMAT));
        birtContext.put(E.contentType.name(), getElementOrDefault((String)context.get(E.contentType.name()), DEFAULT_REPORT_CONTENT_MIME_TYPE_ID));
        birtContext.put(E.userLogin.name(), getUserLogin());
        birtContext.put(E.locale.name(), getLocale());
        
        this.dctx = dctx;
        //this.context = context;
        
    }
    
    public String getElementOrDefault(String value, String defaultValue) {
        if (UtilValidate.isEmpty(value)) {
        	value = defaultValue;
        }
        return value;
    }
    
    public Map<String, Object> printAndSendMail(Map<String, Object> ele) {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        try {
            JobLogLog session = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StaRemPriAndSendMail", null, getLocale());
            JobLogLog enabledSendMailLog = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StaRemPriAndSendMail", null, getLocale());
            addLogInfo(session.getLogMessage() + " " + getSessionId() + enabledSendMailLog.getLogMessage() + "  " + enabledSendMail + " - " + startTimestamp, (String)ele.get(E.partyIdTo.name()) );
            
            Map<String, Object> checkReportParameters = Utils.checkReportParameters(context, ele, getReportId(), getLocale(), getLocalDispatcherName());
            if (checkReportParameters == null) {
            	JobLogLog reportLog = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ERROR_CONTENT_ID", UtilMisc.toMap(E.reportId.name(), (Object)getReportId()), getLocale());
                throw new GeneralException(reportLog.getLogMessage() + " - " + reportLog);
            }
            
            /**
	         * Verifica la lingua per la stampa: se e' una stampa da interfaccia usa quella selezionata, altrimenti viene recuperata quella dell'utente destinatario
	         */
            // servizio per determinare se utilizzare la prima o la seconda lingua
            Map<String, Object> isPrimLangParams = getDctx().makeValidContext("isPartyLanguagePrimary", ModelService.IN_PARAM, context);
            isPrimLangParams.put(E.partyId.name(), (String)ele.get(E.partyIdTo.name())); 
            Map<String, ? extends Object> isPrimLangResult = runSync("isPartyLanguagePrimary", isPrimLangParams, "success", "error", false, false, (String)ele.get(E.partyIdTo.name())); 
            Boolean isPrimaryLang = (Boolean)isPrimLangResult.get(E.isPrimary.name());
            Locale primaryLang = (Locale)isPrimLangResult.get(E.primaryLanguage.name());
            Locale secondaryLang = (Locale)isPrimLangResult.get(E.secondaryLanguage.name());
            
	        if(isPrimaryLang && UtilValidate.isNotEmpty(primaryLang)){
	        	 birtContext.put(BirtWorker.BIRT_LOCALE, primaryLang);
	        } else if(!isPrimaryLang && UtilValidate.isNotEmpty(secondaryLang)){
	        	birtContext.put(BirtWorker.BIRT_LOCALE, secondaryLang);
	        	checkReportParameters.put("langLocale", "_LANG");
	        }
            
            Map<String, Object> birtResult = createBirtReport(checkReportParameters);
            JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndReminderPrinMail", null, getLocale());
            addLogInfo(reminder.getLogMessage() + birtResult, (String)ele.get(E.partyIdTo.name()));
            
            if (E.Y.name().equals(enabledSendMail)) {
                createCommunicationEventAndSendMail(ele, birtResult, isPrimaryLang);
             }
            
        } catch (Exception e) {
            String logMessage = MessageUtil.getExceptionMessage(e);
            Debug.log("printAndSendMail error: " + logMessage);
            e.printStackTrace();
            setResult(ServiceUtil.returnError(logMessage));
            JobLogLog reminder  = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ERROR_MAIL", null, getLocale());
            addLogError(e, reminder.getLogCode(), reminder.getLogMessage());
        } 
        
        Timestamp endTimestamp = UtilDateTime.nowTimestamp();
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndRemPriAndSendMail", null, getLocale());
        addLogInfo(reminder.getLogMessage() + " - " + endTimestamp, null);
        
        return getResult();
    }
    
    
    
    public String getReportId() {
        return (String) context.get(E.reportContentId.name());
    }
    
    public String getLocalDispatcherName() {
        return (String) context.get(E.localDispatcherName.name());
    }

    /**
     * Create CommunicationEvent, create CommEventContentAssoc, and if workEffortId is not null, create CommunicationEventWorkEff
     * @param ele
     * @param birtResult
     * @param isPrimaryLang
     * @throws GeneralException
     */
    // TODO scrivere meglio i log
    private void createCommunicationEventAndSendMail(Map<String, Object> ele, Map<String, Object> birtResult, Boolean isPrimaryLang) throws GeneralException {
        String partyIdTo = (String)ele.get(E.partyIdTo.name());
        Map<String, Object> parameters = UtilMisc.toMap(E.partyIdTo.name(), partyIdTo);
        JobLogLog communication = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "CreCommEvent_partyId", parameters, getLocale());
        addLogInfo(communication.getLogCode(), communication.getLogMessage() + partyIdTo, partyIdTo, E.BaseUiLabels.name(), communication.getParametersJSON());
        
        String contactMechIdTo = (String)ele.get(E.contactMechIdTo.name());
        if (UtilValidate.isEmpty(contactMechIdTo)) {
            communication = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "NoMailFount", null, getLocale());
            addLogWarning(communication.getLogCode(), communication.getLogMessage(), null, null, null);
            return;
        }
        
        String firstServiceName = "createCommunicationEvent";
        String secondServiceName = "createCommEventContentAssoc";
        String thirdServiceName = "createCommunicationEventWorkEff";
        
        Map<String, Object> commEventParams = getDctx().makeValidContext(firstServiceName, ModelService.IN_PARAM, context);
        // in questo caso makeValidContext aggiunge contactMechIdTo e partyIdTo
        commEventParams.putAll(getDctx().makeValidContext(firstServiceName, ModelService.IN_PARAM, ele));
        
        if(UtilValidate.isNotEmpty(isPrimaryLang) && !isPrimaryLang)
        {
            commEventParams.put(E.subject.name(), (String)ele.get(E.subjectLang.name()));
            commEventParams.put(E.content.name(),  (String)ele.get(E.contentLang.name()));
        }
        commEventParams.put(E.partyIdFrom.name(), userLogin.get(E.partyId.name())); 
        commEventParams.put(E.statusId.name(), E.COM_IN_PROGRESS.name());
        commEventParams.put(E.contentMimeTypeId.name(), DEFAULT_CONTENT_MIME_TYPE_ID);
        commEventParams.put(E.communicationEventTypeId.name(), E.AUTO_EMAIL_COMM.name());
        
        
        communication = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "CreCommEveWhitParams", null, getLocale());
        addLogInfo(communication.getLogMessage() + commEventParams, partyIdTo);
        
        Map<String, ? extends Object> commEventResult = runSync(firstServiceName, commEventParams, "success", "error", false, false, partyIdTo); 
        
        String communicationEventId = (String)commEventResult.get(E.communicationEventId.name());
        communication = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndCommEveWhitParams", null, getLocale());
        addLogInfo(communication.getLogMessage() + commEventResult, partyIdTo);
        Map<String, Object> commEventAssocParam = getDctx().makeValidContext(secondServiceName, ModelService.IN_PARAM, commEventParams);
        commEventAssocParam.put(E.contentId.name(), birtResult.get(E.contentId.name()));
        commEventAssocParam.put(E.communicationEventId.name(), communicationEventId);
        commEventAssocParam.put(E.sequenceNum.name(), 1L);
        communication = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "CreCommConAssWhitPar", null, getLocale());
        addLogInfo(communication.getLogMessage() + commEventAssocParam, partyIdTo);
        Debug.log(" - createCommEventContentAssoc with commEventAssocParam " + commEventAssocParam);
        Map<String, ? extends Object> commEventAssocResult = runSync(secondServiceName, commEventAssocParam, "success", "error", false, false, null); 
        
        Debug.log(" - createCommEventContentAssoc result commEventAssocResult " + commEventAssocResult);
        communication = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndCommConAsstWhitPa", null, getLocale());
        addLogInfo(communication.getLogMessage() + commEventAssocResult, partyIdTo);
        
        // se retrieveWorkEffortReminder = true, nel record ele e' presente anche il workEffortId, quindi puo' essere creato il CommunicationEventWorkEff
        String workEffortRootId = (String)ele.get(E.workEffortId.name());
        Debug.log("workEffortRootId " + workEffortRootId);
        if (UtilValidate.isNotEmpty(workEffortRootId)) {
            Debug.log(" createCommunicationEventWorkEff commEventParams " + commEventParams);
            commEventParams.put(E.communicationEventId.name(), communicationEventId);
            commEventParams.put(E.workEffortId.name(), workEffortRootId);
            
            Debug.log(" createCommunicationEventWorkEff commEventParams " + commEventParams);
            Map<String, ? extends Object> commEventResult2 = runSync(thirdServiceName, commEventParams, "success", "error", false, false, partyIdTo);
            Debug.log(" createCommunicationEventWorkEff commEventResult2 " + commEventResult2);
        }
        
    }

    private Map<String, Object>  createBirtReport(Map<String, Object> ele) throws GeneralException {
        Map<String, Object> birtParameters = new HashMap<String, Object>();
        birtParameters.putAll(ele);
        
        String organizationId = null;
        WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(getDelegator(), getDispatcher());     
        try {
        	organizationId = workEffortFindServices.getOrganizationId(getUserLogin(), false);
		} catch (Exception e) {
			organizationId = null;
		}
        if (UtilValidate.isEmpty(organizationId)) {
        	organizationId = "Company";
        }
        birtParameters.put(E.organizationId.name(), organizationId);
        
        if ("REMINDER_PRS".equals(ele.get("reportContentId"))) {
        	birtParameters.put("outputFileName", getPrsFileName(ele));
        }
        
        birtContext.put(BirtService.BIRT_PARAMETERS, birtParameters);
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "CreatedReport", null, getLocale());
        addLogInfo(reminder.getLogMessage() + birtParameters.values(), (String)ele.get(E.partyIdTo.name()));
        return runSync(E.createBirtReport.name(), birtContext, "success createBirtReport", "error createBirtReport", true, true, null);
    }
    
    private String getPrsFileName(Map<String, Object> ele) throws GeneralException {
		String parentRoleCode = "";			
		Delegator delegator = getDctx().getDelegator();
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		String partyId = (String) ele.get("partyId");
		if (UtilValidate.isNotEmpty(partyId)) {
			GenericValue partyParentRole = delegator.findOne("PartyParentRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "ORGANIZATION_UNIT"), false);
			if (UtilValidate.isNotEmpty(partyParentRole)) {
				parentRoleCode = partyParentRole.getString("parentRoleCode");
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Consuntivo Indicatori PRS_");
		if (UtilValidate.isNotEmpty(parentRoleCode)) {
			sb.append(parentRoleCode);
			sb.append("_");
		}
		sb.append(UtilDateTime.getYear((Timestamp) ele.get("monitoringDate"), timeZone, locale));
		return sb.toString();	
    }
    
    
    
    
    
    
    
    
    public Map<String, Object> printAndSendMailByQueryExecutor(Map<String, Object> ele, Map<String, Object> wb) {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        if(ele != null && wb != null) {
        	 try {
                 JobLogLog session = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StaRemPriAndSendMailByQueryExecutor", null, getLocale());
                 JobLogLog enabledSendMailLog = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StaRemPriAndSendMailByQueryExecutor", null, getLocale());
                 addLogInfo(session.getLogMessage() + " " + getSessionId() + enabledSendMailLog.getLogMessage() + "  " + enabledSendMail + " - " + startTimestamp, (String)ele.get(E.partyIdTo.name()) );  
                 IReportEngine engine = BirtFactory.getReportEngine();
                 File documentDir = new File(engine.getConfig().getTempDir());
                 String tmpFileName = File.separator + QUERYEXECUTOR_TMP_FILE_PREFIX + System.nanoTime() + ".tmp";
                 Workbook workbook = ((Workbook)wb.get("workBook"));
                 String queryName = ((String)wb.get("queryName"));
                 workbook.write(new FileOutputStream(documentDir + tmpFileName));
                 workbook.close();
                 Map<String, Object> uploadResult = uploadReportFile(XSLX_REPORT_CONTENT_MIME_TYPE_ID, queryName + ".xlsx", new File(documentDir + tmpFileName));
                 /**
     	         * Verifica la lingua per la stampa: se e' una stampa da interfaccia usa quella selezionata, altrimenti viene recuperata quella dell'utente destinatario
     	         */
                 // servizio per determinare se utilizzare la prima o la seconda lingua
                 Map<String, Object> isPrimLangParams = getDctx().makeValidContext("isPartyLanguagePrimary", ModelService.IN_PARAM, context);
                 isPrimLangParams.put(E.partyId.name(), (String)ele.get(E.partyIdTo.name())); 
                 Map<String, ? extends Object> isPrimLangResult = runSync("isPartyLanguagePrimary", isPrimLangParams, "success", "error", false, false, (String)ele.get(E.partyIdTo.name())); 
                 Boolean isPrimaryLang = (Boolean)isPrimLangResult.get(E.isPrimary.name());
                 Locale primaryLang = (Locale)isPrimLangResult.get(E.primaryLanguage.name());
                 //Locale secondaryLang = (Locale)isPrimLangResult.get(E.secondaryLanguage.name());
                 //Map<String, Object> birtResult = createBirtReport(checkReportParameters);
                 //JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndReminderPrintMail", null, getLocale());
                 //addLogInfo(reminder.getLogMessage() + birtResult, (String)ele.get(E.partyIdTo.name()));
                 if (E.Y.name().equals(enabledSendMail)) {
                     createCommunicationEventAndSendMail(ele, uploadResult, isPrimaryLang);
                  }
             } catch (Exception e) {
                 String logMessage = MessageUtil.getExceptionMessage(e);
                 Debug.log("printAndSendMail error: " + logMessage);
                 e.printStackTrace();
                 setResult(ServiceUtil.returnError(logMessage));
                 JobLogLog reminder  = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ERROR_MAIL", null, getLocale());
                 addLogError(e, reminder.getLogCode(), reminder.getLogMessage());
             } 
        }
        Timestamp endTimestamp = UtilDateTime.nowTimestamp();
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndRemPriAndSendMail", null, getLocale());
        addLogInfo(reminder.getLogMessage() + " - " + endTimestamp, null);
        return getResult();
    }
    
    
    
    private Map<String, Object> uploadReportFile(String contentType, String fileName, File reportFile) throws GenericServiceException, IOException, Exception {
    	ByteBuffer byteBuffer = null;
        FileInputStream fis = new FileInputStream(reportFile);
        try {
            FileChannel channel = fis.getChannel();
            try {
                byteBuffer = ByteBuffer.allocate((int)channel.size());
                while (true) {
                    if (channel.read(byteBuffer) <= 0) {
                        break;
                    }
                }
            } finally {
                channel.close();
            }
        } finally {
            fis.close();
        }
        String serviceName = "createContentFromUploadedFile";
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        Map<String, Object> serviceContext = ctx.getDctx().makeValidContext(serviceName, "IN", ctx);
        serviceContext.put("_uploadedFile_contentType", contentType);
        serviceContext.put("_uploadedFile_fileName", fileName);
        serviceContext.put("uploadedFile", byteBuffer);
        serviceContext.put("isPublic", "N");
        serviceContext.put("roleTypeNotRequired", "Y");
        serviceContext.put("partyId", ctx.getUserLogin().get("partyId"));
        String contentPartyId = (String)ctx.get("contentPartyId");
        serviceContext.put("contentTypeId", AsyncJobUtil.CONTENT_TYPE_TMP_ENCLOSE); // Allegati temporanei e cancellati dopo 24 ore, ma utilizzati per essere mostrati nella portlet
        if (UtilValidate.isNotEmpty(contentPartyId)) {
            serviceContext.put("partyId", contentPartyId);
        }
        Map<String, Object> serviceResult = ctx.getDispatcher().runSync(serviceName, serviceContext);
        String contentId = (String)serviceResult.get("contentId");
        Debug.logInfo("report output contentId: " + contentId, MODULE);
        return serviceResult;
    } 
 }
