package com.mapsengineering.base.reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.birt.util.UtilFilter;
import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;

public class ReminderExecuteQuery extends GenericService {

    public static final String MODULE = ReminderExecuteQuery.class.getName();
    public static final String SERVICE_NAME = "reminderExecuteQuery";
    public static final String SERVICE_TYPE = "REMINDER_EXECUTE_QUERY";
    private static final String QUERY_REMINDER_STRING =  "queryReminder";
    private static final String REMINDER_ASS_OBI =  "REMINDER_ASS_OBI";
    private static final String REMINDER_ASS_OBI_1 =  "REMINDER_ASS_OBI_1";
    private static final String REMINDER_ASS_OBI_2 =  "REMINDER_ASS_OBI_2";
    private static final String REMINDER_STATO = "REMINDER_STATO";
    
    private String queryReminder;
    
    
    public ReminderExecuteQuery(DispatchContext dctx, Map<String, Object> context, JobLogger jobLogger) {
        super(dctx, context, jobLogger, SERVICE_NAME, SERVICE_TYPE, MODULE);
        queryReminder = (String) this.context.get(QUERY_REMINDER_STRING);

        //gestione dei permessi per la query: metto nel context 
        UtilFilter utilFilter = new UtilFilter(dctx.getSecurity(), getUserLogin(), getLocalDispatcherName());
        
        this.context.put(E.filterInnerJoin.name(), utilFilter.getWorkEffortFilterInner());
        this.context.put(E.filterLeftJoin.name(), utilFilter.getWorkEffortFilterLeftJoin());
        this.context.put(E.filterWhere.name(), utilFilter.getWorkEffortFilterWhere());
        
    }
    
    public String getLocalDispatcherName() {
        return (String) context.get(E.localDispatcherName.name());
    }
    
    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
    
    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.workEffortTypeId.name(), getWorkEffortTypeId());
        mapContext.put(E.monitoringDate.name(), getMonitoringDate());
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
        mapContext.put(E.organizationId.name(), organizationId);
        
        getRetrieveWorkEffortReminder();
        
        return mapContext;
    }
    
    /**
     * Vado a prendere tutte le persone da sollecitare
     * @return 
     */
    public Map<String, Object> getListExecuteQuery() {
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StartRemExecuteQuery", null, getLocale());
        addLogInfo(reminder.getLogMessage(), null);
        try {
            JdbcQueryIterator queryReminderList = new FtlQuery(getDelegator(), queryReminder, mapContextUpdate()).iterate();
            try {
                while (queryReminderList.hasNext()) {
                    ResultSet ele = queryReminderList.next();
                    Map<String, Object> row = getGenericValue(ele);
                    rowList.add(row);
                }

                result.put("list", rowList);
                reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "SizeRemExecuteQuery", null, getLocale());
                addLogInfo(reminder.getLogMessage() + rowList.size(), null);
                
            } finally {
                queryReminderList.close();
            }

        } catch (Exception e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
            reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ERROR_QUERY", null, getLocale()); 
            addLogError(e, reminder.getLogCode(), reminder.getLogMessage());
        } finally {
            setResult(result);
        }
        
        reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndRemExecuteQuery", null, getLocale());
        addLogInfo(reminder.getLogMessage(), null);
        
        return getResult();
    }
    
    
    
	public Map<String, Object> getQueryResults(GenericHelperInfo helperInfo, String  query) throws Exception{
		Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if(query !=null && !query.isEmpty() )
		{
			connection = ConnectionFactory.getConnection(helperInfo);
			stmt = connection.prepareStatement(query);
	        rs = stmt.executeQuery();
			while (rs.next()) {
                 Map<String, Object> row = getGenericValue(rs);
                 rowList.add(row);
	        }
			rs.close();
			result.put("list", rowList);
		}
		return result;
	}

    
    
    private Map<String, Object> getGenericValue(ResultSet ele) throws SQLException {
        Map<String, Object> genericValue = FastMap.newInstance();
        //TODO - generilizzare
        
        genericValue.put(E.partyIdTo.name(),ele.getString(E.PARTY_ID_TO.name()));
        genericValue.put(E.partyId.name(),ele.getString(E.PARTY_ID.name()));
        genericValue.put(E.subject.name(),ele.getString(E.SUBJECT.name()));
        genericValue.put(E.content.name(),ele.getString(E.CONTENT.name()));    
        genericValue.put(E.subjectLang.name(),ele.getString(E.SUBJECT_LANG.name()));
        genericValue.put(E.contentLang.name(),ele.getString(E.CONTENT_LANG.name()));
        genericValue.put(E.contactMechIdTo.name(),ele.getString(E.CONTACT_MECH_ID_TO.name()));
        
        if(UtilValidate.isNotEmpty(context.get(E.retrieveWorkEffortReminder.name())) && Boolean.TRUE.equals((Boolean)context.get(E.retrieveWorkEffortReminder.name()))){
        	genericValue.put(E.workEffortId.name(),ele.getString(E.WORK_EFFORT_ID.name()));
        }
        
        if(REMINDER_STATO.equals(context.get(E.reportContentId.name()))) {
        	genericValue.put(E.evalPartyId.name(),ele.getString(E.EVAL_PARTY_ID.name()));
        }
        
        //genericValue.putAll((Map<? extends String, ? extends Object>)ele);
        JobLogLog reminder = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "RemExeQueryGV", null, getLocale());
        addLogInfo(reminder.getLogMessage() + genericValue.toString(), null);
        return genericValue;
    }
        
    private Timestamp getMonitoringDate() {
        return (Timestamp)context.get(E.monitoringDate.name());
    }

   
    private String getWorkEffortTypeId() {
        return (String)context.get(E.workEffortTypeId.name());
    }
    
    /**
     * retrieveWorkEffortReminder serve per estrarre nella query anche il workEffortId, 
     * utile per aggiornare workEffort.dataSoll, tranne che nei servizi in cui si cambia lo stato della scheda, per esempio per CMTorino
     * utile per creare il CommunicationEventWorkEff in createCommunicationEventAndSendMail
     */
    private void getRetrieveWorkEffortReminder(){
    	if(UtilValidate.isEmpty(context.get(E.retrieveWorkEffortReminder.name()))){
        	this.context.put(E.retrieveWorkEffortReminder.name(), false);
        }
    }
   
}
