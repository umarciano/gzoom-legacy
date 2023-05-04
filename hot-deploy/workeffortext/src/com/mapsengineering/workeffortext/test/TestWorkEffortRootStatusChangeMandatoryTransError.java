package com.mapsengineering.workeffortext.test;

import org.ofbiz.base.util.Debug;

/**
 * Change status for root with mandatory trans
 *
 */
public class TestWorkEffortRootStatusChangeMandatoryTransError extends BaseTestWorkEffortRootStatusChange {

    public static final String WEPERFST_PLANSUB = "WEPERFST_PLANSUB";
    
    /**
     * Change status for root with mandatory trans
     */
    public void testWorkEffortRootStatusChange() {
        Debug.log("testWorkEffortRootStatusChange WEPERFST_PLANSUB");
        super.testWorkEffortRootStatusChangeIsPosted("W10000", WEPERFST_PLANSUB, true);
    }
    
    /**
     * Change status for root with mandatory trans
     */
    public void testWorkEffortRootStatusChangeNoError() {
        Debug.log("testWorkEffortRootStatusChangeNoError WEPERFST_PLANSUB");
        super.testWorkEffortRootStatusChangeIsPosted("W30000", WEPERFST_PLANSUB, true);
    }
    
    /**
     * Change status for root with mandatory trans
     */
    public void testWorkEffortRootStatusChangeErrorNoteEmpty() {
        Debug.log("testWorkEffortRootStatusChangeErrorNoteEmpty WEPERFST_PLANSUB");
        super.testWorkEffortRootStatusChangeIsPosted("RCW12824", WEPERFST_PLANSUB, false);
    }
    
    /**
     * Change status for root with mandatory trans empty
     */
    public void testWorkEffortRootStatusChangeNoErrorTransEmpty() {
        Debug.log("testWorkEffortRootStatusChangeNoErrorTransEmpty WEPERFST_PLANSUB");
        super.testWorkEffortRootStatusChangeIsPosted("W50000", WEPERFST_PLANSUB, true);
    }
    
    /**
     * Change status for root with mandatory trans empty
     */
    public void testWorkEffortRootStatusChangeQueryConfig() {
        Debug.log("testWorkEffortRootStatusChangeQueryConfig WEPERFST_PLANSUB");
        super.testWorkEffortRootStatusChangeIsPosted("W50010", WEPERFST_PLANSUB, false);
    }
}
