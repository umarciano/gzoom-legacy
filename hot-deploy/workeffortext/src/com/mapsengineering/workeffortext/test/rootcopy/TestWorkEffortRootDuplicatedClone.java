package com.mapsengineering.workeffortext.test.rootcopy;

import com.mapsengineering.workeffortext.services.E;

/**
 * Test WorkEffortRootCopy
 */
public class TestWorkEffortRootDuplicatedClone extends TestWorkEffortRootDuplicatedCopy {

    public static final String MODULE = TestWorkEffortRootDuplicatedClone.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        ctp.setString(E.params.name(), "duplicateAdmit=\"CLONE\"");
        ctp.store();
    }
    
    protected String getWorkEffortId() {
        return "W80010";
    }
    
    protected String getTitle() {
        return "Test WorkEffort Root Duplicate with duplicateAdmit = CLONE";
    }
    
    protected Long getExpectedRecordElaborated() {
        return 1l;
    }
}
