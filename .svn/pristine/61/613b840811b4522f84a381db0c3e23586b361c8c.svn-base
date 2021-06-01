package com.mapsengineering.workeffortext.test.services;

import org.ofbiz.base.util.Debug;

/**
 * WorkEffort Root Status for different root
 */
public class TestWorkEffortRootStatusChangeIsPosted extends BaseTestWorkEffortRootStatusChange {

    /**
     * Change status 
     */
    public void testWorkEffortRootStatusChangeIsPostedWithChild() {
        Debug.log("testWorkEffortRootStatusChangeIsPostedWithChild");
        super.testWorkEffortRootStatusChangeIsPosted("W10000", true);
    }
    
    /**
     * Change status 
     */
    public void testWorkEffortRootStatusChangeIsPosted() {
        Debug.log("testWorkEffortRootStatusChangeIsPosted");
        super.testWorkEffortRootStatusChangeIsPosted("W30000", true);
    }
    
    /**
     * Change status, NoteData with noteId = "E1212112823" isPosted = N become Y
     */
    public void testWorkEffortRootStatusChangeAllIsPostedY() {
        Debug.log("testWorkEffortRootStatusChangeAllIsPostedY");
        super.testWorkEffortRootStatusChangeIsPosted("RCW12824", true);
    }
    
    /**
     * Change status 
     */
    public void testWorkEffortRootStatusChangeWithTransaction() {
        Debug.log("testWorkEffortRootStatusChangeWithTransaction");
        super.testWorkEffortRootStatusChangeIsPosted("W50000", true);
    }
}
