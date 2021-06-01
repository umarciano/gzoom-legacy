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
public class TestWorkEffortRootPhysicalDelete extends BaseTestCase {

    public static final String MODULE = TestWorkEffortRootPhysicalDelete.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortId.name(), "RCW12900");
    }

    /**
     * Test Main Loop
     */
    public void testWorkEffortRootPhysicalDeleteMainLoop() {
        try {
            WorkEffortRootPhysicalDelete service = new WorkEffortRootPhysicalDelete(dispatcher.getDispatchContext(), context);
            service.mainLoop();
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
