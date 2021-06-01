package com.mapsengineering.workeffortext.test.rootcopy;

import org.ofbiz.base.util.UtilMisc;

import com.mapsengineering.workeffortext.services.E;

/**
 * Test WorkEffortRootCopy
 */
public class TestWorkEffortRootDuplicatedCloneIndice extends TestWorkEffortRootDuplicatedCopy {

    public static final String MODULE = TestWorkEffortRootDuplicatedCloneIndice.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        ctp = delegator.findOne("WorkEffortTypeStatus", UtilMisc.toMap(E.workEffortTypeRootId.name(), "14O1IN", E.currentStatusId.name(), "WEORGST_PLANINIT"), false);
        ctp.setString(E.params.name(), "duplicateAdmit=\"CLONE\"");
        ctp.store();
    }

    protected String getWorkEffortId() {
        return "12200";
    }

    protected String getTitle() {
        return "Test WorkEffort Root Duplicate with duplicateAdmit = CLONE";
    }
    
    protected Long getExpectedRecordElaborated() {
        return 1l;
    }
}
