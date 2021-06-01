package com.mapsengineering.workeffortext.services.relatedassignment;

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
 * ExecuteChildPerformFindTimeEntry Service Find
 */
public class ExecuteChildPerformFindRelatedAssignment extends GenericService {

    public static final String MODULE = ExecuteChildPerformFindRelatedAssignment.class.getName();
    private static final String SERVICE_NAME = "executePerformFindRelatedAssignment";
    private static final String SERVICE_TYPE = null;

    private static final String queryRelatedAssignment = "sql/relatedassignment/queryRelatedAssignment.sql.ftl";


    /**
     * ExecuteChildPerformFindRelatedAssignment
     */
    public static Map<String, Object> executeChildPerformFindRelatedAssignment(DispatchContext dctx, Map<String, Object> context) {

        ExecuteChildPerformFindRelatedAssignment obj = new ExecuteChildPerformFindRelatedAssignment(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindRelatedAssignment(DispatchContext dctx, Map<String, Object> context) {
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
            JdbcQueryIterator queryTimeRelAssList = new FtlQuery(getDelegator(), queryRelatedAssignment, mapContextUpdate()).iterate();
            try {
                while (queryTimeRelAssList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryTimeRelAssList.next();
                    Map<String, Object> row = FastMap.newInstance();
                    
                    row.put(WRA.collegato.name(), ele.getString(WRA.COLLEGATO.name()));
                    row.put(WRA.assegnazione.name(), ele.getString(WRA.ASSEGNAZIONE.name()));                  
                    row.put("isReadOnly", "Y");
                    rowList.add(row);
                }

                result.put("rowList", rowList);
            } finally {
            	queryTimeRelAssList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }

    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WRA.workEffortId.name(), getWorkEffortId());
        mapContext.put(WRA.roleTypeId.name(), getRoleTypeId());

        return mapContext;
    }
    
    private String getWorkEffortId() {
        return (String)context.get(WRA.workEffortId.name());
    }
    
    private String getRoleTypeId() {
        return (String)context.get(WRA.roleTypeId.name());
    }    

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
