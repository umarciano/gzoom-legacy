package com.mapsengineering.gzope.services.timeentry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * ExecuteChildPerformFindTimeEntry Service Find
 */
public class ExecuteChildPerformFindTimeEntry extends GenericService {

    public static final String MODULE = ExecuteChildPerformFindTimeEntry.class.getName();
    private static final String SERVICE_NAME = "executePerformFindTimeEntry";
    private static final String SERVICE_TYPE = null;

    private static final String queryTimeEntry = "sql/timeentry/queryTimeEntry.sql.ftl";

    /**
     * ExecuteChildPerformFindTimeEntry
     */
    public static Map<String, Object> executeChildPerformFindTimeEntry(DispatchContext dctx, Map<String, Object> context) {

        ExecuteChildPerformFindTimeEntry obj = new ExecuteChildPerformFindTimeEntry(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindTimeEntry(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();

        try {
            JdbcQueryIterator queryTimeEntryList = new FtlQuery(getDelegator(), queryTimeEntry, mapContextUpdate()).iterate();
            Double hoursTotal = 0d;
            try {
                while (queryTimeEntryList.hasNext()) {
                    ResultSet ele = queryTimeEntryList.next();
                    Map<String, Object> row = FastMap.newInstance();
                    row.put(WTI.hours.name(), getHours(ele));
                    hoursTotal += getHours(ele);
                    
                    row.put(WTI.workEffortId.name(), getHours(ele));
                    row.put(WTI.projectId.name(), ele.getString(WTI.PROJECT_ID.name()));
                    row.put(WTI.projectName.name(), ele.getString(WTI.PROJECT_NAME.name()));
                    row.put(WTI.taskId.name(), ele.getString(WTI.TASK_ID.name()));
                    row.put(WTI.taskName.name(), ele.getString(WTI.TASK_NAME.name()));
                    
                    if(UtilValidate.isNotEmpty(getShowParty()) && "Y".equals(getShowParty())) {
                        row.put(WTI.partyId.name(), ele.getString(WTI.PARTY_ID.name()));
                        row.put(WTI.partyName.name(), ele.getString(WTI.PARTY_NAME.name()));
                    }
                    
                    row.put(WTI.estimatedStartDate.name(), ele.getTimestamp(WTI.ESTIMATED_START_DATE.name()));
                    row.put(WTI.estimatedCompletionDate.name(), ele.getTimestamp(WTI.ESTIMATED_COMPLETION_DATE.name()));
                    
                    row.put("isReadOnly", "Y");
                    rowList.add(row);
                }

                result.put("rowList", rowList);
                result.put("hoursTotal", hoursTotal);
            } finally {
                queryTimeEntryList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }

    private Double getHours(ResultSet ele) throws SQLException {
        if(UtilValidate.isNotEmpty(ele.getString(WTI.HOURS.name()))) {
            return ele.getDouble(WTI.HOURS.name());
        }
        return null;
    }

        private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WTI.workEffortId.name(), getWorkEffortId());
        mapContext.put(WTI.showParty.name(), getShowParty());

        return mapContext;
    }

    private String getShowParty() {
        return (String)context.get(WTI.showParty.name());
    }

    private String getWorkEffortId() {
        return (String)context.get(WTI.workEffortId.name());
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
