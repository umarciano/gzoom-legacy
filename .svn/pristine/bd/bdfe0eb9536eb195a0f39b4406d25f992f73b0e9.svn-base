package com.mapsengineering.workeffortext.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Integration Test 
 * Base WorkEffort Snapshot
 *
 */
public class TestWorkEffortRootSnapshotWithoutResourceValues extends BaseTestWorkEffortSnapshot {

    public static final String MODULE = TestWorkEffortRootSnapshotWithoutResourceValues.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), "PEG12");

        context.put(E.workEffortId.name(), getWorkEffortRootId());
        context.put(E.workEffortRevisionId.name(), getWorkEffortRevisionId());

        context.put(E.workEffortTypeIdTo.name(), "PEG12");
    }

    /**
     * WorkEffort with 1 measure and 1 transaction, without resource
     */
    public void testWorkEffortRootSnapshot() {
        Debug.log(getTitle());

        try {
            checkBeforeSnapshot(getWorkEffortId(), getExpectedMeasure(), getExpectedResourceTransaction());

            Map<String, Object> result = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);

            checkAfterSnapshot(getWorkEffortId(), getWorkEffortRevisionId(), getExpectedMeasure(), getExpectedResourceTransaction());

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    protected int getExpectedMeasure() {
        return 1;
    }

    protected int getExpectedResourceTransaction() {
        return 0;
    }

    protected String getWorkEffortRevisionId() {
        return "R10002";
    }

    protected String getWorkEffortRootId() {
        return "W30000";
    }
    
    protected String getWorkEffortId() {
        return "W30000";
    }
    
    protected String getTitle() {
        return "Snapshot PEG12 Without Resource Values and hasAcctgTrans N ";
    }

}
