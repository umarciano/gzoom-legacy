package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootInqySummeryBSUserLogin17 extends TestExecuteChildPerformFindWorkEffortRootInqySummery {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootInqySummeryBSUserLogin17.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_BS.getCode();
    }
    
    protected String getUserLoginId() {
        return "user17";
    }
    
    protected String getTitle() {
        return "Test execute TestExecuteChildPerformFindWorkEffortRootInqySummery BS user17";
    }
}
