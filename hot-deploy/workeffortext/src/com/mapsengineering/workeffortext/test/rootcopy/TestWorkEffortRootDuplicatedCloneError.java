package com.mapsengineering.workeffortext.test.rootcopy;

import org.ofbiz.base.util.UtilMisc;

import com.mapsengineering.workeffortext.services.E;

/**
 * Test WorkEffortRootCopy
 */
public class TestWorkEffortRootDuplicatedCloneError extends TestWorkEffortRootDuplicatedClone {

    public static final String MODULE = TestWorkEffortRootDuplicatedCloneError.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        ctp = delegator.findOne("WorkEffortTypeStatus", UtilMisc.toMap(E.workEffortTypeRootId.name(), "18PMA3OBSA_ERR_ROLE", E.currentStatusId.name(), "WEPERFST_PLANINIT"), false);
        ctp.setString(E.params.name(), "duplicateAdmit=\"CLONE\"");
        ctp.store();
    }
    
    protected String getTitle() {
        return "Test WorkEffort Root Duplicate Error with duplicateAdmit = CLONE";
    }
    
    protected String getWorkEffortId() {
        return "W80013";
    }
    
    protected Long getExpectedRecordElaborated() {
        return 1l;
    }
    
    protected Long getExpectedBlockingErrors() {
        return 1l;
    }
}
