package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootOR extends BaseTestExecuteChildPerformFindWorkEffortRoot  {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootOR.class.getName();
    
    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_OR.getCode();
    }
    
    protected int getRowListNumber() {
        return 1;
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRoot OR";
    }
}
