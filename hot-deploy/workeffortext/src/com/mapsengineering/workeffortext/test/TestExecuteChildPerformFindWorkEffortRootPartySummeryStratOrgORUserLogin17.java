package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgORUserLogin17 extends TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgUserLogin17 {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgORUserLogin17.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_OR.getCode();
    }
    
    protected int getRowListNumber() {
        return 16;
    }
    
    protected String getTitle() {
        return "Test execute TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrg OR ";
    }
}
