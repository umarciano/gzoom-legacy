package com.mapsengineering.workeffortext.test;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;

/**
 * Test Create WorkEffort Root OBSA
 */
public class BaseTestCreateWorkEffortRoot extends BaseTestCase {

    public static final String MODULE = BaseTestCreateWorkEffortRoot.class.getName();
    
    public static final String STATUS_ID = "WEPERFST_MONITINIT";
    public static final Timestamp FROM_DATE_2019 = new Timestamp(UtilDateTime.toDate(1, 1, 2019, 0, 0, 0).getTime());
    public static final Timestamp THRU_DATE_2019 = new Timestamp(UtilDateTime.toDate(12, 31, 2019, 0, 0, 0).getTime());

    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap("18PMA3OBSA", "DSPL01", "SPL");
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public void testCreateWorkEffortRoot() {
        Debug.log(getTitle());
        try {
            Map<String, Object> result = dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffortRoot", context);
            Debug.log(" testCreateWorkEffortRoot " + result);
            assertResult(result);
            Map<String, Object> id = (Map<String, Object>)result.get("id");
            if (UtilValidate.isNotEmpty(id)) {
                String workEffortId = (String)id.get("workEffortId");
                GenericValue we = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
                Debug.log(" we " + we);
            }
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    protected void assertResult(Map<String, Object> result) {
        assertTrue(ServiceUtil.isSuccess(result));
    }

    protected String getTitle() {
        return "testCreateWorkEffortRoot OBSA";
    }

    protected Map<String, Object> setServiceMap(String workEffortTypeId, String orgUnitId, String orgUnitRoleTypeId) throws GenericServiceException {
        Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_WorkEffortRoot", "IN", context);
        Map<String, Object> parameters = FastMap.newInstance();
        localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortStatus.name());
        localContext.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
        parameters.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
        parameters.put("workEffortTypeId", workEffortTypeId);
        parameters.put("workEffortName", "Prova creazione OBSA");
        parameters.put("orgUnitId", orgUnitId);
        parameters.put("orgUnitRoleTypeId", orgUnitRoleTypeId);
        parameters.put("fromCard", "S");
        parameters.put("_AUTOMATIC_PK_", "Y");
        parameters.put("defaultStatusPrefix", "WEPERFST");
        parameters.put(E.estimatedStartDate.name(), FROM_DATE_2019);
        parameters.put(E.estimatedCompletionDate.name(), THRU_DATE_2019);
        parameters.put(GenericService.ORGANIZATION_ID, COMPANY);
        localContext.put(CrudEvents.PARAMETERS, parameters);
        return localContext;
    }
}
