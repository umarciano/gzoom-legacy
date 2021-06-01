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
 * ExecuteChildPerformFindWorkEffortRootInqySummary Service Find
 * @author nito
 *
 */
public class ExecuteChildPerformFindWorkEffortRootInqyPartySummaryStratOrg extends GenericService {
	
    public static final String MODULE = ExecuteChildPerformFindWorkEffortRootInqyPartySummaryStratOrg.class.getName();
    private static final String SERVICE_NAME = "executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg";
    private static final String SERVICE_TYPE = null;
    
    private static final String queryWorkEffortRootInqyPartySummaryStratOrg = "sql/workeffort/queryWorkEffortRootInqyPartySummaryStratOrg.sql.ftl";
    
    /**
     * executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg(DispatchContext dctx, Map<String, Object> context) {
    	ExecuteChildPerformFindWorkEffortRootInqyPartySummaryStratOrg obj = new ExecuteChildPerformFindWorkEffortRootInqyPartySummaryStratOrg(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
	
    /**
     * Constructor
     * @param dctx
     * @param context
     * @param serviceName
     */
    public ExecuteChildPerformFindWorkEffortRootInqyPartySummaryStratOrg(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }
    
    /**
     * Main loop
     */
    protected void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        try {
            JdbcQueryIterator queryWorkEffortList = new FtlQuery(getDelegator(), queryWorkEffortRootInqyPartySummaryStratOrg, ExecuteChildPerformFindWorkEffortRootInqyPartySummaryUtil.mapContextUpdate(context)).iterate();
            try {
                while (queryWorkEffortList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryWorkEffortList.next();
                    Map<String, Object> row = FastMap.newInstance();                  
                    ExecuteChildPerformFindWorkEffortRootInqyPartySummaryUtil.fillRow(row, ele, true);
                    rowList.add(row);                  
                }
                result.put("rowList", rowList);
            } finally {
            	queryWorkEffortList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }
}
