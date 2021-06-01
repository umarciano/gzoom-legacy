package com.mapsengineering.workeffortext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Test WorkEffortRootSnapshot, error file not found where snapshot attactment
 */
public class TestWorkEffortRootSnapshotAndContent extends TestWorkEffortRootSnapshotWithoutResourceValues {

    public static final String MODULE = TestWorkEffortRootSnapshotAndContent.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), "MVD-12");
        context.put(E.workEffortTypeIdTo.name(), "MVD-12");

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2013, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()));

        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2013, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()));
    }
    
    /**
     * WorkEffort with 1 blockingErrors
     */
    public void testWorkEffortRootSnapshot() {
        Debug.log(getTitle());

        try {
            Map<String, Object> result = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertEquals(1l, result.get(ServiceLogger.BLOCKING_ERRORS));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    protected String getTitle() {
        return "Test Exception WorkEffort Root Snapshot With Content";
    }

    protected String getWorkEffortRevisionId() {
        return "MVD-12.01";
    }

    protected String getWorkEffortRootId() {
        return "W13440";
    }
}
