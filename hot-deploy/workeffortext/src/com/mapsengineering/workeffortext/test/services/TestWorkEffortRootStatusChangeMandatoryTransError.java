package com.mapsengineering.workeffortext.test.services;

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
        super.testWorkEffortRootStatusChangeIsPosted("W10000", WEPERFST_PLANSUB, true);
    }
    
    /**
     * Change status for root with mandatory trans
     */
    public void testWorkEffortRootStatusChangeNoError() {
        super.testWorkEffortRootStatusChangeIsPosted("W30000", WEPERFST_PLANSUB, true);
    }
    
    /**
     * Change status for root with mandatory trans
     */
    public void testWorkEffortRootStatusChangeErrorNoteEmpty() {
        super.testWorkEffortRootStatusChangeIsPosted("RCW12824", WEPERFST_PLANSUB, false);
    }
    
    /**
     * Change status for root with mandatory trans empty
     */
    public void testWorkEffortRootStatusChangeNoErrorTransEmpty() {
        super.testWorkEffortRootStatusChangeIsPosted("W50000", WEPERFST_PLANSUB, true);
    }
}
