package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/** 
 * Test con admin e BS
 */
public class TestExecuteChildPerformFindWorkEffortRootBS extends BaseTestExecuteChildPerformFindWorkEffortRoot {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootBS.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_BS.getCode();
    }
    
    protected int getRowListNumber() {
        return 11;
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRoot BS";
    }
}
