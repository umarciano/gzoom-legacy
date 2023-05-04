package com.mapsengineering.workeffortext.services.workeffort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;


import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;


/**
 * ExecuteChildPerformFindWorkEffortRootInqy Service Find
 */
public class ExecuteChildPerformFindWorkEffortAnalysis extends GenericService {

    private static final String SERVICE_NAME = "executeChildPerformFindWorkEffortAnalysis";
    public static final String MODULE = ExecuteChildPerformFindWorkEffortAnalysis.class.getName();
    private static final String SERVICE_TYPE = null;

    private static final String queryWorkEffortAnalysis = "sql/workeffort/queryWorkEffortAnalysis.sql.ftl";


    /**
     * ExecuteChildPerformFindWorkEffortRootInqy
     */
    public static Map<String, Object> executeChildPerformFindWorkEffortAnalysis(DispatchContext dctx, Map<String, Object> context) {
    	ExecuteChildPerformFindWorkEffortAnalysis obj = new ExecuteChildPerformFindWorkEffortAnalysis(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindWorkEffortAnalysis(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        
        try {
            JdbcQueryIterator queryWorkEffortAnalysisList = new FtlQuery(getDelegator(), queryWorkEffortAnalysis, mapContextUpdate()).iterate();
            try {
                while (queryWorkEffortAnalysisList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryWorkEffortAnalysisList.next();
                    Map<String, Object> row = FastMap.newInstance();
                                
                    row.put(WE.workEffortAnalysisId.name(), ele.getString(WE.WORK_EFFORT_ANALYSIS_ID.name()));
                    row.put(WE.workEffortParentId.name(), ele.getString(WE.WORK_EFFORT_PARENT_ID.name()));
                    row.put(WE.parentSourceReferenceId.name(), ele.getString(WE.PARENT_SOURCE_REFRENECE_ID.name()));
                    row.put(WE.workEffortId.name(), ele.getString(WE.WORK_EFFORT_ID.name()));
                    row.put(WE.etch.name(), ele.getString(WE.ETCH.name()));
                    row.put(WE.sourceReferenceId.name(), ele.getString(WE.SOURCE_REFERENCE_ID.name()));
                    row.put(WE.workEffortSnapshotId.name(), ele.getString(WE.WORK_EFFORT_SNAPSHOT_ID.name()));
                    row.put(WE.workEffortTypeId.name(), ele.getString(WE.WORK_EFFORT_TYPE_ID.name()));
                    row.put(WE.parentTypeId.name(), ele.getString(WE.PARENT_TYPE_ID.name()));
                    row.put(WE.comments.name(), ele.getString(WE.COMMENTS.name()));
                    row.put(WE.workEffortName.name(), ele.getString(WE.WORK_EFFORT_NAME.name()));
                    row.put(WE.description.name(), ele.getString(WE.DESCRIPTION.name()));
                    row.put(WE.uomRangeScoreId.name(), ele.getString(WE.UOM_RANGE_SCORE_ID.name()));
                    row.put(WE.weUomRangeScoreId.name(), ele.getString(WE.WE_UOM_RANGE_SCORE_ID.name()));
                    row.put(WE.estimatedStartDate.name(), ele.getTimestamp(WE.ESTIMATED_START_DATE.name()));
                    row.put(WE.estimatedCompletionDate.name(), ele.getTimestamp(WE.ESTIMATED_COMPLETION_DATE.name()));
                    row.put(WE.actualStartDate.name(), ele.getTimestamp(WE.ACTUAL_START_DATE.name()));
                    row.put(WE.actualCompletionDate.name(), ele.getTimestamp(WE.ACTUAL_COMPLETION_DATE.name()));
                    row.put(WE.userLoginId.name(), ele.getString(WE.USER_LOGIN_ID.name()));
                    row.put(WE.glFiscalTypeId.name(), ele.getString(WE.GL_FISCAL_TYPE_ID.name()));
                    row.put(WE.amount.name(), ele.getDouble(WE.AMOUNT.name()));
                    row.put(WE.hasScoreAlert.name(), ele.getString(WE.HAS_SCORE_ALERT.name()));
                    row.put(WE.transactionDate.name(), ele.getTimestamp(WE.TRANSACTION_DATE.name()));
                    rowList.add(row);                                   
                }

                result.put("rowList", rowList);
            } finally {
            	queryWorkEffortAnalysisList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }

    /**
     * Set mapContext
     * @return
     * @throws SQLException
     */
    protected MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WE.isOrgMgr.name(), (Boolean)context.get(WE.isOrgMgr.name()));
        mapContext.put(WE.isRole.name(), (Boolean)context.get(WE.isRole.name()));
        mapContext.put(WE.isSup.name(), (Boolean)context.get(WE.isSup.name()));   
        mapContext.put(WE.isTop.name(), (Boolean)context.get(WE.isTop.name()));
        mapContext.put(WE.workEffortAnalysisId.name(), (String)context.get(WE.workEffortAnalysisId.name()));

        return mapContext;
    }
    
    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
