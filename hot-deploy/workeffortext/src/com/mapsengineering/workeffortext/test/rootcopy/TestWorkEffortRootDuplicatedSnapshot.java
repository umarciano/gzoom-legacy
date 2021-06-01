package com.mapsengineering.workeffortext.test.rootcopy;

import com.mapsengineering.workeffortext.services.E;

/**
 * Test WorkEffortRootCopy
 */
public class TestWorkEffortRootDuplicatedSnapshot extends TestWorkEffortRootDuplicatedCopy {

    public static final String MODULE = TestWorkEffortRootDuplicatedSnapshot.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        ctp.setString(E.params.name(), "duplicateAdmit=\"SNAPSHOT\"");
        ctp.store();
    }
    
    protected String getTitle() {
        return "Test WorkEffort Root Duplicate with duplicateAdmit = SNAPSHOT";
    }
    
    protected Long getExpectedRecordElaborated() {
        return 1l;
    }
}
