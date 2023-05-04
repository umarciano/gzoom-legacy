package com.mapsengineering.workeffortext.test;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.trans.E;

/**
 * WorkEffort Root Status
 */
public class BaseTestWorkEffortRootStatusChange extends BaseTestCase {

    public static final String MODULE = BaseTestWorkEffortRootStatusChange.class.getName();

    public static final String STATUS_ID = "WEPERFST_MONITINIT";

    protected void setUp() throws Exception {
        super.setUp();
        context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
    }
    
    /**
     * Change status for different root
     */
    protected void testWorkEffortRootStatusChangeIsPosted(String workEffortId, String statusId, boolean isSuccess) {
        try {
            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_WorkEffortRootStatus", "IN", context);

            Map<String, Object> parameters = FastMap.newInstance();

            localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortStatus.name());
            localContext.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
            parameters.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
            parameters.put(E.workEffortId.name(), workEffortId);
            parameters.put(E.statusId.name(), statusId);
            parameters.put(E.statusDatetime.name(), UtilDateTime.nowTimestamp());
            parameters.put(E.reason.name(), "TEST");

            localContext.put(CrudEvents.PARAMETERS, parameters);
            
            Map<String, Object> result = dispatcher.runSync(E.crudServiceDefaultOrchestration_WorkEffortRootStatus.name(), localContext);
            Debug.log("For " + workEffortId + " with "+ statusId + " - " + result);
            assertEquals(isSuccess, ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    protected void testWorkEffortRootStatusChangeIsPosted(String workEffortId, boolean isSuccess) {
        testWorkEffortRootStatusChangeIsPosted(workEffortId, STATUS_ID, isSuccess);
    }
}
