package com.mapsengineering.workeffortext.test;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;

/**
 * Test WorkEffortTypePeriodOpen
 */
public class TestWorkEffortTypePeriodOpen extends BaseTestCase {

    public static final String MODULE = TestWorkEffortTypePeriodOpen.class.getName();

    /**
     * WorkEffort with 1 blockingErrors
     */
    public void testWorkEffortTypePeriod() {
        Debug.log(getTitle());

        try {
            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_WorkEffortTypePeriod", "IN", context);
            // localContext.put("organizationId", COMPANY);
            Map<String, Object> parameters = FastMap.newInstance();

            localContext.put(ServiceLogger.ENTITY_NAME, "WorkEffortTypePeriod");
            localContext.put("operation", CrudEvents.OP_UPDATE);
            parameters.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
            parameters.put("organizationId", COMPANY);
            parameters.put("operation", CrudEvents.OP_UPDATE);
            parameters.put("statusTypeId", "WE_STATUS_PERFORMANC");
            parameters.put("statusEnumId", "OPEN");
            parameters.put(E.openAllRoots.name(), E.Y.name());
            parameters.put(E.workEffortTypePeriodId.name(), "10005");
            localContext.put("parameters", parameters);

            Map<String, Object> result = dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffortTypePeriod", localContext);
            Debug.log(" - result " + result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    protected String getTitle() {
        return "Test WorkEffort Type Period Open ";
    }
}
