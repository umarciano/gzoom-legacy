package com.mapsengineering.workeffortext.test.rootcopy;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootdelete.WorkEffortRootPhysicalDelete;

/**
 * Test RootPhysicalDelete
 */
public class TestWorkEffortRootPhysicalDeleteLogAndException extends BaseTestCase {

    public static final String MODULE = TestWorkEffortRootPhysicalDelete.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortId.name(), "RCW12900");
    }

    /**
     * Test add log Error
     */
    public void testWorkEffortRootPhysicalDeleteLogError() {
        try {
            WorkEffortRootPhysicalDelete service = new WorkEffortRootPhysicalDelete(dispatcher.getDispatchContext(), context);
            service.addLogError("Test addLogError");
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Test add log info
     */
    public void testWorkEffortRootPhysicalDeleteLogInfo() {
        try {
            WorkEffortRootPhysicalDelete service = new WorkEffortRootPhysicalDelete(dispatcher.getDispatchContext(), context);
            service.addLogInfo("Test addLogInfo");
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Test runSync workEffortRootPhysicalDelete with response error 
     */
    public void testWorkEffortRootPhysicalDeleteException() {
        try {
            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("workEffortRootPhysicalDelete", "IN", context);
            Map<String, Object> result = dispatcher.runSync("workEffortRootPhysicalDelete", localContext);
            assertTrue(ServiceUtil.isError(result));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
