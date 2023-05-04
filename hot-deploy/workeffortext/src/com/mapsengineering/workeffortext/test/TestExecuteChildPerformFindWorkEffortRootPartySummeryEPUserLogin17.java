package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummeryEPUserLogin17 extends TestExecuteChildPerformFindWorkEffortRootPartySummeryUserLogin17 {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummeryEPUserLogin17.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_EP.getCode();
    }
    
    protected int getRowListNumber() {
        return 0;
    }
    
    protected String getTitle() {
        return "Test execute TestExecuteChildPerformFindWorkEffortRootPartySummeryEP ";
    }
}
