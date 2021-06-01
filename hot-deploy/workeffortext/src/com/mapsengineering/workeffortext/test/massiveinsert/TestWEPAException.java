package com.mapsengineering.workeffortext.test.massiveinsert;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.massiveinsert.wepa.MassiveInsertWEPA;

/**
 * Test WEPA Error
 *
 */
public class TestWEPAException extends BaseTestCase {

    public static final String MODULE = TestWEPAException.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortId.name(), "RCW12824");
    }

    /**
     * Service return error response
     */
    public void testWEPAException() {
        try {
            Map<String, Object> result = MassiveInsertWEPA.massiveInsertWEPA(dispatcher.getDispatchContext(), context);
            assertFalse(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
