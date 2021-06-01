package com.mapsengineering.partyext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.partyext.common.E;

/**
 * Test for Update Party endDate
 *
 */
public class TestWorkEffortParty extends BaseTestCase {

    public static final String MODULE = TestWorkEffortParty.class.getName();

    /**
     * Test
     */
    public void testUpdatePartyEndDate() {
        Map<String, Object> localContext = this.context;
        localContext.put(E.partyId.name(), "D2001");
        localContext.put(E.endDate.name(), new Timestamp(UtilDateTime.toDate(8, 31, 2013, 0, 0, 0).getTime()));
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext("updatePartyEndDate", ModelService.IN_PARAM, localContext);
            Debug.log(" updatePartyEndDate serviceParams " + serviceParams);
            Map<String, Object> result = this.dispatcher.runSync("updatePartyEndDate", serviceParams);
            assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

}
