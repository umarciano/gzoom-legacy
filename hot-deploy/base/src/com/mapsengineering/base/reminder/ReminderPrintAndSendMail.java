package com.mapsengineering.base.reminder;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.birt.BirtWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.birt.service.BirtService;
import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;

public class ReminderPrintAndSendMail extends GenericService {

    public static final String MODULE = ReminderPrintAndSendMail.class.getName();
    public static final String SERVICE_NAME = "reminderPrintAndSendMail";
    public static final String SERVICE_TYPE = "REMINDER_PRINT_SEND_MAIL";
    private static final String DEFAULT_CONTENT_MIME_TYPE_ID = "text/plain";
    private static final String DEFAULT_REPORT_CONTENT_MIME_TYPE_ID = "application/pdf";
    private static final String DEFAULT_REPORT_OUTPUT_FORMAT = "pdf";
    
    private String enabledSendMail;
    private Map<String, Object> birtContext;
       
    public ReminderPrintAndSendMail(DispatchContext dctx, Map<String, Object> context, JobLogger jobLogger) {
        super(dctx, context, jobLogger, SERVICE_NAME, SERVICE_TYPE, MODULE);
        enabledSendMail = (String) context.get(E.enabledSendMail.name());        
              
        birtContext = new HashMap<String, Object>();
        birtContext.put(E.reportContentId.name(), context.get(E.reportContentId.name()));
        birtContext.put(E.outputFormat.name(), getElementOrDefault((String)context.get(E.outputFormat.name()), DEFAULT_REPORT_OUTPUT_FORMAT));
        birtContext.put(E.contentType.name(), getElementOrDefault((String)context.get(E.contentType.name()), DEFAULT_REPORT_CONTENT_MIME_TYPE_ID));
        birtContext.put(E.userLogin.name(), getUserLogin());
        birtContext.put(E.locale.name(), getLocale());
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
            setResult(ServiceUtil.returnError(e.getMessage()));
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
    
 }
