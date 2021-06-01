package com.mapsengineering.base.reminder;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJob;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;

public class Reminder extends GenericService {

    public static final String MODULE = Reminder.class.getName();
    public static final String SERVICE_NAME = "reminder";
    public static final String SERVICE_TYPE = "REMINDER";
    
    private Timestamp monitoringDate;
    private GenericValue wt;
    private String conctachMechIdFrom;
    
    private AsyncJob asyncJob;
    
    public static Map<String, Object> reminder(DispatchContext dctx, Map<String, Object> context) {
        Reminder obj = new Reminder(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
    
    
    public Reminder(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        
        monitoringDate = (Timestamp) context.get(E.monitoringDate.name());
        if (UtilValidate.isEmpty(monitoringDate)) {
            monitoringDate =  new Timestamp(Calendar.getInstance().getTime().getTime());
            this.context.put(E.monitoringDate.name(), monitoringDate);
        }
        
        asyncJob = (AsyncJob) context.get("asyncJob");
        setSessionId(asyncJob.getJobId());
    }
    
    /**
     * Vado a prendere tutte le persone da sollecitare
     */
    public void mainLoop() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StartReminder", null, getLocale());
        addLogInfo(reminder.getLogMessage() + " - " + startTimestamp, null);

        try {
            
            setLocalDispatcherName();
            
            //metto nel contesto tutte le informazioni che mi servono
            context.put(E.contactMechIdFrom.name(), getContactMechIdFrom((String)userLogin.get(E.partyId.name())));
            ReminderPrintAndSendMail prindMail = new ReminderPrintAndSendMail(getDctx(), context, this.getJobLogger());
            
            // retrieveWorkEffortReminder serve per estrarre nella query anche il workEffortId, 
            // utile per aggiornare workEffort.dataSoll
            // utile per creare il CommunicationEventWorkEff in createCommunicationEventAndSendMail
            if ("REMINDER_ASS_OBI".equals(context.get(E.reportContentId.name())) || "REMINDER_ASS_OBI_1".equals(context.get(E.reportContentId.name())) 
            		|| "REMINDER_ASS_OBI_2".equals(context.get(E.reportContentId.name())) || "REMINDER_VAL_DIP".equals(context.get(E.reportContentId.name())) 
            		|| "REMINDER_VAL_DIP_1".equals(context.get(E.reportContentId.name())) || "REMINDER_VAL_DIP_2".equals(context.get(E.reportContentId.name()))){
                context.put(E.retrieveWorkEffortReminder.name(), true); 
            }
            List<Map<String, Object>> list = reminderExecuteQuery();
            if (list != null && list.size() > 0 ) {            	
            	//invio la mail a chi ha lanciato l'invio massivo oppure alla mail di default
                Map<String, Object> resultRiepilogo = prindMail.printAndSendMail(printAndSendMailAdmin());
                if (UtilValidate.isEmpty(resultRiepilogo) || !resultRiepilogo.equals(ServiceUtil.returnSuccess())) {
                    setBlockingErrors(getBlockingErrors() + 1); 
                    reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ErrorReminderMailAdm", null, getLocale());
                    addLogError(reminder.getLogMessage() + " " +(String)userLogin.get(E.userLoginId.name()) , (String)userLogin.get(E.userLoginId.name()));
                }
            	
	            for(Map<String, Object> ele : list) {
	                if (asyncJob.isInterrupted()) {
	                    getResult().putAll(ServiceUtil.returnError("User service interrrupted"));
	                    break;
	                }
	                
	                setRecordElaborated(getRecordElaborated() +1);
	                
	                Map<String, Object> result = prindMail.printAndSendMail(ele);
	                if (UtilValidate.isEmpty(result) || !result.equals(ServiceUtil.returnSuccess())) {
	                   setBlockingErrors(getBlockingErrors() + 1);
	                   reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ErrorReminderMail", null, getLocale());
	                   addLogError(reminder.getLogMessage() + " " +(String)userLogin.get(E.userLoginId.name()) , (String)ele.get(E.partyIdTo.name()));
	                } 
	            }                     
                
                //se e' il batch sollecito periodo, recupera i work effort e aggiorna il data_soll
                if(UtilValidate.isNotEmpty(context.get(E.batchReminder.name())) && Boolean.TRUE.equals(context.get(E.batchReminder.name()))){
                	context.put(E.retrieveWorkEffortReminder.name(), true); 
                	List<Map<String, Object>> listWorkEffort = reminderExecuteQuery();
                	 
                	 if (UtilValidate.isNotEmpty(listWorkEffort)) {
                     	
         	            for(Map<String, Object> element : listWorkEffort) {
         	            	String workEffortId = (String)element.get(E.workEffortId.name());
         	            	if (UtilValidate.isNotEmpty(workEffortId)) {
         	                    GenericValue wEElement = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
         	                    if (wEElement != null) {
         	                    	wEElement.set(E.dataSoll.name(), UtilDateTime.nowTimestamp());
         	                    	wEElement.store();
         	                    }
         	                }
         	            }
                	 }
                }
            }       

        } catch (Exception e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
            reminder  = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "BaseError", null, getLocale());
            addLogError(e, reminder.getLogCode(), reminder.getLogMessage());
            
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            getResult().put(ServiceLogger.SESSION_ID, getSessionId());
            
            Timestamp endTimestamp = UtilDateTime.nowTimestamp();
            reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndReminder", null, getLocale());
            addLogInfo(reminder.getLogMessage() + " - " + endTimestamp, null);
            
            writeLogs(startTimestamp, jobLogId);
        }
        
    }

     
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> manageResult(Map<String, Object> result) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String msg = "";
        if (ServiceUtil.isSuccess(result)) {
            list = (List<Map<String, Object>>)result.get("list");
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogError(msg);
        }
        return list;
    }
    
    private List<Map<String, Object>> reminderExecuteQuery() throws GeneralException, IOException {
        ReminderExecuteQuery executeQuery = new ReminderExecuteQuery(getDctx(), context, this.getJobLogger());
        return manageResult(executeQuery.getListExecuteQuery());
    }

    
    public void setLocalDispatcherName() throws GeneralException {
        String localDispatcherName = (String)context.get(E.localDispatcherName.name());
        if (UtilValidate.isEmpty(localDispatcherName)) {
            GenericValue wt = getWorkEffortType();
            localDispatcherName = ContextPermissionPrefixEnum.getPermissionPrefix(wt.getString(E.parentTypeId.name()));
            context.put(E.localDispatcherName.name(), localDispatcherName);
        }
    }
    
    /**
     * Dato il partyId prendo il concatMech, se il partyId o il contactMechId e vuoto prendo quello di default BaseConfig.Reminder.defaultPartyIdEmailAddress
     * @param partyId
     * @return
     * @throws GeneralException 
     */
    private String getContactMechIdFrom(String partyId) throws GeneralException {
        if (UtilValidate.isNotEmpty(conctachMechIdFrom)) {
            return conctachMechIdFrom;
        }
        
        String partyIdDefault = UtilValidate.isNotEmpty(partyId) ? partyId : UtilProperties.getPropertyValue("BaseConfig.properties", "Reminder.defaultPartyIdEmailAddress");
        if (UtilValidate.isNotEmpty(partyIdDefault)) {
            List<GenericValue> list = EntityUtil.filterByDate(getDelegator().findList(E.PartyContactMechDetail.name(), EntityCondition.makeCondition(E.partyId.name(), partyIdDefault), null, null, null, false));
            if (UtilValidate.isNotEmpty(list) && list.size() > 0) {
                conctachMechIdFrom = (String)((GenericValue)list.get(0)).get(E.contactMechId.name());
            } 
          
        }
        
        if (UtilValidate.isEmpty(conctachMechIdFrom)) {
            JobLogLog mail = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ERROR_MAIL_FROM", UtilMisc.toMap(E.partyId.name(), (Object)partyIdDefault), getLocale());
            throw new GeneralException(mail.getLogMessage() + " - " + partyIdDefault);
        }
        
        return conctachMechIdFrom;
    }
    
    public String getWorkEffortTypeId() {
        return (String) context.get(E.workEffortTypeId.name());
    }
    
    public Map<String, Object> printAndSendMailAdmin() throws GeneralException {
        Map<String, Object> ele = new HashMap<String, Object>();
        
        ele.put(E.contactMechIdTo.name(), getContactMechIdFrom((String)userLogin.get(E.partyId.name())));
        ele.put(E.partyIdTo.name(), (String)userLogin.get(E.partyId.name()));
        GenericValue wt = getWorkEffortType();
        ele.put(E.subject.name(), wt.get(E.etch.name()));
        ele.put(E.content.name(), wt.get(E.note.name()));
        ele.put(E.subjectLang.name(), wt.get(E.etchLang.name()));
        ele.put(E.contentLang.name(), wt.get(E.noteLang.name()));
        
        return ele;
    }
    
    public GenericValue getWorkEffortType() throws GeneralException {
        if (UtilValidate.isNotEmpty(wt)) {
            return wt;
        }
        wt = getDelegator().findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), getWorkEffortTypeId()), false);
        if (UtilValidate.isEmpty(wt)) {
            JobLogLog noWorkEffortTypeIdFound = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "NO_WORK_EFFORT_TYPE", UtilMisc.toMap(E.workEffortTypeId.name(), (Object)getWorkEffortTypeId()), getLocale());
            throw new GeneralException(noWorkEffortTypeIdFound.getLogMessage());
        }
            
        return wt;
    }
    
    
}
