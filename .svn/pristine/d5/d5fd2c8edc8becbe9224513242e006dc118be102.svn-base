package com.mapsengineering.workeffortext.services.print;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.birt.service.BirtService;
import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.find.PartyEmailFindServices;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJobUtil;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.E;

public class PrintAndSendMail extends GenericService {

    public static final String MODULE = PrintAndSendMail.class.getName();
    public static final String SERVICE_NAME = "printAndSendMail";
    public static final String SERVICE_TYPE = "PRINT_SEND_MAIL";
    private static final String SUBJECT = "subject";
    private static final String CONTENT = "content";
    private static final String REPORT_CONTENT_ID = "reportContentId";
    private static final String OUTPUT_FORMAT = "outputFormat";
    private static final String DEFAULT_CONTENT_MIME_TYPE_ID = "text/plain";
    
    private String subject;
    private String content;
    private String reportContentId;
    private String outputFormat;
    private String enabledSendMail;

    public static Map<String, Object> printAndSendMail(DispatchContext dctx, Map<String, Object> context) {
        PrintAndSendMail obj = new PrintAndSendMail(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    public PrintAndSendMail(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(E.userLogin.name());
        
        subject = (String) context.get(SUBJECT);
        content = (String) context.get(CONTENT);
        reportContentId = (String) context.get(REPORT_CONTENT_ID);
        outputFormat = (String) context.get(OUTPUT_FORMAT);
    }

    public void mainLoop() {
        String msg;
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        
        try {
            String workEffortRootId = (String) context.get(E.workEffortRootId.name());
            setElabRef1(workEffortRootId);
            
            enabledSendMail = (String) context.get(E.enabledSendMail.name());
            subject = (String) context.get(SUBJECT);
            content = (String) context.get(CONTENT);
            reportContentId = (String) context.get(REPORT_CONTENT_ID);
            outputFormat = (String) context.get(OUTPUT_FORMAT);
            
            addLogInfo("Start Print for sessionId " + getSessionId() + " and enabledSendMail " + enabledSendMail);
            elaborateWorkEffort(workEffortRootId);
        } catch (Exception e) {
            msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            writeLogs(startTimestamp, jobLogId);
            
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
        }
    }
    
    private void elaborateWorkEffort(String workEffortRootId) throws GeneralException, IOException {
        GenericValue workEffort = delegator.findOne(E.WorkEffort.name(), true, E.workEffortId.name(), workEffortRootId);
        String workEffortName = (String) workEffort.get(E.workEffortName.name());
        setElabRef1(workEffortRootId);
        addLogInfo("Start elaboration for workEffortName = " + workEffortName + " [workEffortRootId = " + workEffortRootId + "]");
        
        String evalPartyId = getEvalPartyId(workEffortRootId);
        if (UtilValidate.isEmpty(evalPartyId)) {
            addLogWarning("No evaluator found for workEffortId = " + workEffortRootId);
            return;
        }
        
        if (!isEnabledReport(workEffort.getString(E.workEffortTypeId.name()))) {
            addLogWarning("ReportContentId = " + reportContentId + " not permitted for workEffortId = " + workEffortRootId);
            return;
        }
        
        Map<String, Object> birtResult = createBirtReport(workEffortRootId, workEffort.getString(E.workEffortTypeId.name()), workEffortName, evalPartyId); // TODO cosa mettere come nome del report?
        addLogInfo("Service createBirtReport finished with = " + birtResult);
        
        if (E.Y.name().equals(enabledSendMail)) {
           createCommunicationEventAndSendMail(workEffortRootId, evalPartyId, birtResult);
        }
    }

    private boolean isEnabledReport(String workEffortTypeId) throws GenericEntityException {
        GenericValue reportContent = delegator.findOne(E.WorkEffortTypeContent.name(), false, E.workEffortTypeId.name(), workEffortTypeId,
               E.contentId.name(), reportContentId);
        return UtilValidate.isNotEmpty(reportContent);
    }

    private void createCommunicationEventAndSendMail(String workEffortRootId, String evalPartyId, Map<String, Object> birtResult) throws GeneralException {
        String contactMechId = getValidEmailAddress(evalPartyId);
        if (UtilValidate.isEmpty(contactMechId)) {
            addLogWarning("No emailAddress found for evalPartyId = " + evalPartyId);
            return;
        }
        
        String msg = "Creating CommunicationEvent for workEffort and evalPartyId " + evalPartyId;
        addLogInfo(msg);
        
        Map<String, Object> commEventParams = getDctx().makeValidContext("createCommunicationEventWorkEff", ModelService.IN_PARAM, context);
        commEventParams.put(E.contactMechIdTo.name(), contactMechId);
        String contactMechIdFrom = getValidEmailAddress(getUserLogin().getString(E.partyId.name()));
        commEventParams.put(E.contactMechIdFrom.name(), contactMechIdFrom);
        commEventParams.put(E.partyIdTo.name(), evalPartyId);
        commEventParams.put(E.subject.name(), subject);
        commEventParams.put(E.content.name(), content);
        commEventParams.put(E.workEffortId.name(), workEffortRootId);
        commEventParams.put(E.partyIdFrom.name(), getUserLogin().getString(E.partyId.name()));
        commEventParams.put(E.statusId.name(), E.COM_IN_PROGRESS.name());
        commEventParams.put(E.contentMimeTypeId.name(), DEFAULT_CONTENT_MIME_TYPE_ID);
        commEventParams.put(E.communicationEventTypeId.name(), E.AUTO_EMAIL_COMM.name());
        
        addLogInfo(" - Run createCommunicationEventWorkEff with params = " + commEventParams);
        Map<String, ? extends Object> commEventResult = runSync("createCommunicationEventWorkEff", commEventParams, "success", "error", false, false, workEffortRootId); // TODO che facci ose eccezione o warning?
        
        addLogInfo(" - Service createCommunicationEventWorkEff finished with result = " + commEventResult);
        String communicationEventId = (String)commEventResult.get(E.communicationEventId.name());
        
        Map<String, Object> commEventAssocParam = getDctx().makeValidContext("createCommEventContentAssoc", ModelService.IN_PARAM, commEventParams);
        commEventAssocParam.put(E.contentId.name(), birtResult.get(E.contentId.name()));
        commEventAssocParam.put(E.communicationEventId.name(), communicationEventId);
        commEventAssocParam.put(E.sequenceNum.name(), 1L);
        
        addLogInfo(" - Run createCommEventContentAssoc with params = " + commEventAssocParam);
        Map<String, ? extends Object> commEventAssocResult = runSync("createCommEventContentAssoc", commEventAssocParam, "success", "error", false, false, workEffortRootId); // TODO che facci ose eccezione o warning?
        addLogInfo(" - Service createCommEventContentAssoc finished with result = " + commEventAssocResult);
        
    }

    /**
     * TODO sistemare i parametri si puo usare la classe GenericPrint che in base al report da lanciare si prende in automatico i 
     * parametri senza specificarli, altrimenti se vengono aggiunti dei nuovi bisogna ogni volta modificare la classe
     * @param workEffortRootId
     * @param workEffortTypeId
     * @param workEffortName
     * @param evalPartyId
     * @return
     * @throws IOException
     * @throws GeneralException
     */
    private Map<String, Object>  createBirtReport(String workEffortRootId, String workEffortTypeId, String workEffortName, String evalPartyId) throws IOException, GeneralException {
        Map<String, Object> birtContext = FastMap.newInstance();
        Map<String, Object> birtParameters = FastMap.newInstance();
        String userProfile = Utils.getUserProfile(getDctx().getSecurity(), getUserLogin(), "EMPLPERF");
        birtParameters.put(E.userProfile.name(), userProfile);
        birtParameters.put(E.localDispatcherName.name(), "EMPLPERF"); 
        
        birtParameters.put(E.workEffortId.name(), workEffortRootId);
        birtParameters.put(E.reportContentId.name(), reportContentId); // TODO anche qui?
        birtContext.put(E.reportContentId.name(), reportContentId); // TODO anche qui?
        birtContext.put(E.outputFormat.name(), outputFormat); // TODO anche qui?
        birtParameters.put(E.outputFormat.name(), outputFormat); // TODO anche qui?
        birtParameters.put(E.monitoringDate.name(), context.get(E.monitoringDate.name()));
        birtContext.put(E.contentType.name(), "application/pdf");
        birtContext.put(BirtService.BIRT_PARAMETERS, birtParameters);
        birtContext.put(E.name.name(), workEffortName + "_" + workEffortRootId);
        birtContext.put(E.userLogin.name(), getUserLogin());
        birtContext.put(E.contentPartyId.name(), evalPartyId); // TODO optional true; sovrascrive quello della session, verificare se funziona
        birtContext.put(E.contentTypeId.name(), AsyncJobUtil.CONTENT_TYPE_TMP_ENCLOSE); // TODO optional true; sovrascrive quello della session
        birtContext.put(E.locale.name(), getLocale());
        
        birtParameters.put(E.workEffortTypeId.name(), workEffortTypeId);
        
        birtParameters.put(E.exposePaginator.name(), "Y");
        birtParameters.put(E.typeNotes.name(), "N");
        birtParameters.put("snapshot", "N"); // TODO anche qui?
        birtParameters.put("scoreIndType", ""); // TODO anche qui?
        birtParameters.put("valutIndType", ""); // TODO anche qui?
        birtParameters.put("orgUnitRoleTypeId", ""); // TODO anche qui?
        birtParameters.put("roleTypeId", ""); // TODO anche qui?
        birtParameters.put(E.ordinamento.name(), ""); // TODO anche qui?
        birtParameters.put("langLocale", "");
        
        return runSync(E.createBirtReport.name(), birtContext, "success createBirtReport", "error createBirtReport", true, true, workEffortRootId); // TODO che facci ose eccezione o warning?
    }

    private String getEvalPartyId(String workEffortId) throws GeneralException {
        String evalPartyId = null;
        GenericValue wepa = getWorkEffortPartyAssignmentList(workEffortId);
        if (UtilValidate.isNotEmpty(wepa)) {
            evalPartyId = wepa.getString(E.partyId.name());
        }
        return evalPartyId;
    }

    private GenericValue getEmailAddress(String partyId) throws GeneralException {
        addLogInfo("Check if exist email address for evalPartyId " + partyId);
        
        PartyEmailFindServices partyEmailFindServices = new PartyEmailFindServices(delegator);
        List<GenericValue> emailAddressesList = partyEmailFindServices.getEmailAddress(partyId);
        return EntityUtil.getFirst(emailAddressesList);
    }
    
    private String getValidEmailAddress(String partyId) throws GeneralException {
        String contactMechId = null;
        GenericValue value = getEmailAddress(partyId);
        if (UtilValidate.isNotEmpty(value)) {
            String msg = "Email address found = " + value;
            addLogInfo(msg);
            contactMechId = value.getString(E.contactMechId.name());
        }
        return contactMechId;
    }
    
    /**
     * Ritorna lista workEffortPartyAssignment indipendentemente dalle date
     * @param workEffortId
     * @param roleTypeId
     * @return WorkEffortPartyAssignment of WEM_EVAL_IN_CHARGE
     * @throws GeneralException
     */
    public GenericValue getWorkEffortPartyAssignmentList(String workEffortId) throws GeneralException {
        EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortId.name(), workEffortId, E.roleTypeId.name(), E.WEM_EVAL_IN_CHARGE.name()));
        return EntityUtil.getFirst(delegator.findList(E.WorkEffortPartyAssignment.name(), cond, null, null, null, false));
    }
 }
