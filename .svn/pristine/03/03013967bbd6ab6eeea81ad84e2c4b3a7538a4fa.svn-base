package com.mapsengineering.workeffortext.services.workeffort;

import java.sql.SQLException;
import java.util.Map;


import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.service.DispatchContext;


/**
 * ExecuteChildPerformFindWorkEffortRootInqy Service Find
 */
public class ExecuteChildPerformFindWorkEffortRootInqy extends ExecuteChildPerformFindWorkEffort {

    private static final String SERVICE_NAME = "executeChildPerformFindWorkEffortRootInqy";

    private static final String queryWorkEffortRootInqy = "sql/workeffort/queryWorkEffortRootInqy.sql.ftl";


    /**
     * ExecuteChildPerformFindWorkEffortRootInqy
     */
    public static Map<String, Object> executeChildPerformFindWorkEffortRootInqy(DispatchContext dctx, Map<String, Object> context) {
        ExecuteChildPerformFindWorkEffortRootInqy obj = new ExecuteChildPerformFindWorkEffortRootInqy(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindWorkEffortRootInqy(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        mainLoop(queryWorkEffortRootInqy);
    }

    protected MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = super.mapContextUpdate();
        mapContext.put(WE.weActivation.name(), (String)context.get(WE.weActivation.name()));   
        mapContext.put(WE.workEffortRevisionId.name(), (String)context.get(WE.workEffortRevisionId.name()));
        mapContext.put(WE.workEffortId.name(), (String)context.get(WE.workEffortId.name()));
        mapContext.put(WE.isRootActive.name(), (String)context.get(WE.isRootActive.name()));
        mapContext.put(WE.queryOrderBy.name(), (String)context.get(WE.queryOrderBy.name()));

        return mapContext;
    }
}
