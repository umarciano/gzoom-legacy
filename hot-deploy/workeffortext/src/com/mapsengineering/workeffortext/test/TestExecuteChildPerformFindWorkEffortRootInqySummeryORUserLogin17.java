package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootInqySummeryORUserLogin17 extends TestExecuteChildPerformFindWorkEffortRootInqySummeryBSUserLogin17 {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootInqySummeryORUserLogin17.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_OR.getCode();
    }
    
    protected int getRowListNumber() {
        return 4;
    }
    
    protected String getTitle() {
        return "Test execute TestExecuteChildPerformFindWorkEffortRootInqySummery OR user17 ";
    }
}
