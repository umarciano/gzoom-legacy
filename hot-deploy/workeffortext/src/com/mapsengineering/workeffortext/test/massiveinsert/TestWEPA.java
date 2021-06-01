package com.mapsengineering.workeffortext.test.massiveinsert;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.massiveinsert.wepa.MassiveInsertWEPA;

/**
 * Test WEPA Massive insert
 *
 */
public class TestWEPA extends BaseTestCase {

    public static final String MODULE = TestWEPA.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortId.name(), "W10000");

        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.partyId.name(), EntityJoinOperator.LIKE, "RCP1019%"));
        condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.EMPLOYEE.name()));

        List<GenericValue> partyRoleList = delegator.findList("PartyRoleView", EntityCondition.makeCondition(condList), null, null, null, false);
        Debug.log(" partyRoleList " + partyRoleList);
        context.put("listIt", partyRoleList);
    }

    /**
     * Test WEPA insert
     */
    public void testWEPA() {
        try {
            Map<String, Object> result = MassiveInsertWEPA.massiveInsertWEPA(dispatcher.getDispatchContext(), context);
            assertTrue(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
