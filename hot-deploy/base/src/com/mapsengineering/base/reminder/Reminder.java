package com.mapsengineering.base.reminder;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.QueryExecutorService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJob;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import bsh.EvalError;

public class Reminder extends GenericService {

    public static final String MODULE = Reminder.class.getName();
    public static final String SERVICE_NAME = "reminder";
    public static final String SERVICE_TYPE = "REMINDER";
    private Timestamp monitoringDate;
    private GenericValue wt;
    private String conctachMechIdFrom;
    private AsyncJob asyncJob;
    
    private DispatchContext dctx;
    private Map<String, Object> context;
    
    public static Map<String, Object> reminder(DispatchContext dctx, Map<String, Object> context) {
        Reminder obj = new Reminder(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
    
    
    public Reminder(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        
        this.dctx = dctx;
        this.context = context;
        
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
    @SuppressWarnings("unused")
	public void mainLoop() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StartReminder", null, getLocale());
        addLogInfo(reminder.getLogMessage() + " - " + context.get(E.reportContentId.name()) + " - " + startTimestamp, null);

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
            		|| "REMINDER_VAL_DIP_1".equals(context.get(E.reportContentId.name())) || "REMINDER_VAL_DIP_2".equals(context.get(E.reportContentId.name())) 
            		|| "REMINDER_ASSOBI_22".equals(context.get(E.reportContentId.name())) || "REMINDER_ASSOBI1_22".equals(context.get(E.reportContentId.name()))
            		|| "REMINDER_ASSOBI2_22".equals(context.get(E.reportContentId.name())) || "REMINDER_VALDIP_22".equals(context.get(E.reportContentId.name()))
            		|| "REMINDER_VALDIP1_22".equals(context.get(E.reportContentId.name())) || "REMINDER_VALDIP2_22".equals(context.get(E.reportContentId.name()))){
                context.put(E.retrieveWorkEffortReminder.name(), true);
            }
            
            
            List<Map<String, Object>> list = reminderExecuteQuery();
            
            //GN-4891 nuovo metodo per ottenere i destinatari da una queryConfig di tipo R
            //List<Map<String, Object>> list = retrieveRecipients();
       
            if (list != null && list.size() > 0 ) {            	
                Debug.log("invio mail di riepilogo");
            	//invio la mail a chi ha lanciato l'invio massivo oppure alla mail di default
                Map<String, Object> resultRiepilogo = prindMail.printAndSendMail(printAndSendMailAdmin());
                
                //GN-4891 nuovo metodo per creare i report xslx da una queryConfig di tipo E ed inviarli ai destinatari 
                //Map<String, Object> resultRiepilogo = prindMail.printAndSendMailByQueryExecutor(printAndSendMailAdmin(),createReport());
                
                if (UtilValidate.isEmpty(resultRiepilogo) || !resultRiepilogo.equals(ServiceUtil.returnSuccess())) {
                    setBlockingErrors(getBlockingErrors() + 1); 
                    reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ErrorReminderMailAdm", null, getLocale());
                    addLogError(reminder.getLogMessage() + " " +(String)userLogin.get(E.userLoginId.name()) , (String)userLogin.get(E.userLoginId.name()));
                }
                Debug.log("invio mail ad ognuno");
	            for(Map<String, Object> ele : list) {
	                if (asyncJob.isInterrupted()) {
	                    getResult().putAll(ServiceUtil.returnError("User service interrrupted"));
	                    break;
	                }
	                
	                setRecordElaborated(getRecordElaborated() +1);
	                
	                Map<String, Object> result = prindMail.printAndSendMail(ele);
	                
	                //GN-4891 nuovo metodo per creare i report xslx da una queryConfig di tipo E ed inviarli ai destinatari 
	                //Map<String, Object> result = prindMail.printAndSendMailByQueryExecutor(ele,createReport());
	                
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
    
    
    
    private List<Map<String, Object>> retrieveRecipients() throws GeneralException, IOException {
    	EntityCondition condition = null;
    	Map<String, Object> results = null;
    	try {
			condition = EntityCondition.makeCondition(getQueryConfigConditions(E.R.name(),"_PRE"));
			List<GenericValue> queryList = findList(E.QueryConfig.name(), condition, false, null);
	    	if(queryList != null && !queryList.isEmpty()) {
	    		//per compatib. construttore
				this.context.put("queryId","");
	    		QueryExecutorService qes = new QueryExecutorService(this.dctx,this.context);
	    		String query = qes.replaceAllCond(queryList.get(0).getString("queryInfo"));
	    		GenericHelperInfo helperInfo = dctx.getDelegator().getGroupHelperInfo(QueryExecutorService.DEFAULT_GROUP_NAME);
	    		ReminderExecuteQuery executeQuery = new ReminderExecuteQuery(getDctx(), context, this.getJobLogger());
	    		results = executeQuery.getQueryResults(helperInfo,query);
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return manageResult(results);
    }
    
    
    
    private Map<String, Object> createReport() throws GeneralException, IOException {
    	Map<String, Object> ret = new HashMap<String, Object>();
    	Workbook wb = null;
    	try {
    		//per compatib. construttore
			this.context.put("queryId","");
    		QueryExecutorService qes = new QueryExecutorService(this.dctx,this.context);
    		EntityCondition condition = null;
    		condition = EntityCondition.makeCondition(getQueryConfigConditions(E.E.name(),"_POST"));
    		List<GenericValue> queryList = findList(E.QueryConfig.name(), condition,false,null);
    		if(queryList != null && !queryList.isEmpty()) {
    			wb = qes.generateWorkBook(queryList.get(0));
    			ret.put("queryName", queryList.get(0).get(E.queryName.name()));
    	    	ret.put("workBook", wb);
    		}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return ret;
    }
    
    
    private List<EntityCondition> getQueryConfigConditions(String queryType,String suffix) throws EvalError, GeneralException {
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        conditionList.add(EntityCondition.makeCondition(E.queryType.name(), queryType));
        conditionList.add(EntityCondition.makeCondition(E.queryActive.name(), E.Y.name()));
        conditionList.add(EntityCondition.makeCondition(E.queryCode.name(), "15AP2SOLL" + suffix));
        return conditionList;
    }  
}
