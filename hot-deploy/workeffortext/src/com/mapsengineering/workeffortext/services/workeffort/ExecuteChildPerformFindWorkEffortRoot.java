package com.mapsengineering.workeffortext.services.workeffort;

import java.sql.SQLException;
import java.util.Map;


import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.service.DispatchContext;


/**
 * ExecuteChildPerformFindWorkEffortRoot Service Find
 */
public class ExecuteChildPerformFindWorkEffortRoot extends ExecuteChildPerformFindWorkEffort {

    private static final String SERVICE_NAME = "executeChildPerformFindWorkEffortRoot";

    private static final String queryWorkEffortRoot = "sql/workeffort/queryWorkEffortRoot.sql.ftl";


    /**
     * ExecuteChildPerformFindWorkEffortRoot
     */
    public static Map<String, Object> executeChildPerformFindWorkEffortRoot(DispatchContext dctx, Map<String, Object> context) {
        ExecuteChildPerformFindWorkEffortRoot obj = new ExecuteChildPerformFindWorkEffortRoot(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindWorkEffortRoot(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        mainLoop(queryWorkEffortRoot);
    }

    protected MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = super.mapContextUpdate();
        mapContext.put(WE.currentStatusContains.name(), (String)context.get(WE.currentStatusContains.name())); 
        mapContext.put(WE.workEffortId.name(), (String)context.get(WE.workEffortId.name()));
        mapContext.put(WE.gpMenuEnumId.name(), (String)context.get(WE.gpMenuEnumId.name()));
        mapContext.put(WE.queryOrderBy.name(), (String)context.get(WE.queryOrderBy.name()));

        return mapContext;
    }
}
