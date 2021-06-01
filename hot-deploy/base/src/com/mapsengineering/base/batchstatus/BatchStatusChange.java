package com.mapsengineering.base.batchstatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJob;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;

public class BatchStatusChange extends GenericService {
    public static final String MODULE = BatchStatusChange.class.getName();
    public static final String SERVICE_NAME = "batchStatusChange";
    public static final String SERVICE_TYPE = "batchStatusChange";
    
    private static final String queryBatchStatus = "sql/batchstatus/queryBatchStatus.sql.ftl";
    
    private AsyncJob asyncJob;
    
    /**
     * servizio di cambio stato
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> batchStatusChange(DispatchContext dctx, Map<String, Object> context) {
    	BatchStatusChange obj = new BatchStatusChange(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
    
    
    public BatchStatusChange(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);       
        asyncJob = (AsyncJob) context.get("asyncJob");
        setSessionId(asyncJob.getJobId());
    }
    
    /**
     * servizio
     */
    public void mainLoop() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        
        JobLogLog batchStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StartBatchStatusChange", null, getLocale());
        addLogInfo(batchStatus.getLogMessage() + " - " + startTimestamp, null);
        
        try {
            batchStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StartBatchStatusChangeExecuteQuery", null, getLocale());
            addLogInfo(batchStatus.getLogMessage(), null);
        	JdbcQueryIterator queryBatchStatusList = new FtlQuery(getDelegator(), queryBatchStatus, mapContextUpdate()).iterate();
        	while(queryBatchStatusList.hasNext()) {
        		setRecordElaborated(getRecordElaborated() +1);
        		ResultSet ele = queryBatchStatusList.next();
        		String statusId = ele.getString(E.NEXT_STATUS_ID.name());
        		String workEffortId = ele.getString(E.WORK_EFFORT_ID.name());
        		changeStatus(statusId, workEffortId);
        	}
        	batchStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndBatchStatusChangeExecuteQuery", null, getLocale());
            addLogInfo(batchStatus.getLogMessage(), null);
        } catch(Exception e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
            batchStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ERROR_QUERY", null, getLocale()); 
            addLogError(e, batchStatus.getLogCode(), batchStatus.getLogMessage());
        }finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            getResult().put(ServiceLogger.SESSION_ID, getSessionId());
            
            Timestamp endTimestamp = UtilDateTime.nowTimestamp();
            batchStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndBatchStatusChange", null, getLocale());
            addLogInfo(batchStatus.getLogMessage() + " - " + endTimestamp, null);
            
            writeLogs(startTimestamp, jobLogId);
        }
    }
    
    /**
     * costruisce il contesto
     * @return
     * @throws SQLException
     */
    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
    
    /**
     * contesto
     * @return
     * @throws SQLException
     */
    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.workEffortTypeId.name(), (String)context.get(E.workEffortTypeId.name()));
        return mapContext;
    }
    
    /**
     * esegue il cambio stato
     * @param statusId
     * @param workEffortId
     */
    private void changeStatus(String statusId, String workEffortId) {
    	try {
            JobLogLog batchChangeStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "StartBatchStatusChangeStatusChange", null, getLocale());
            addLogInfo(batchChangeStatus.getLogMessage(), null);
    	    Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext(E.crudServiceDefaultOrchestration_WorkEffortRootStatus.name(), "IN", context);
            Map<String, Object> parameters = FastMap.newInstance();

            localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortStatus.name());
            localContext.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
            parameters.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
            parameters.put(E.workEffortId.name(), workEffortId);
            parameters.put(E.statusId.name(), statusId);
            parameters.put(E.statusDatetime.name(), UtilDateTime.nowTimestamp());

            localContext.put(CrudEvents.PARAMETERS, parameters);
            
            Map<String, Object> result = dispatcher.runSync(E.crudServiceDefaultOrchestration_WorkEffortRootStatus.name(), localContext);
            
            batchChangeStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "EndBatchStatusChangeStatusChange", null, getLocale());
            addLogInfo(batchChangeStatus.getLogMessage(), null);
            if (! ServiceUtil.isSuccess(result)) {
            	addLogError(ServiceUtil.getErrorMessage(result));
            }
    	} catch(Exception e) {
            JobLogLog changeStatus = new JobLogLog().initLogCode(E.BaseUiLabels.name(), "ErrorBatchStatusChange", null, getLocale()); 
            addLogError(e, changeStatus.getLogCode(), changeStatus.getLogMessage());
    	}
    }

}
