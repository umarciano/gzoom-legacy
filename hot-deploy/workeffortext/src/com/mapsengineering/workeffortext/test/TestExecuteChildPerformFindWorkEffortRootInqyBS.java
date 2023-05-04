package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootInqyBS extends TestExecuteChildPerformFindWorkEffortRootInqy {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootInqyBS.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_BS.getCode();
    }
    
    protected int getRowListNumber() {
        return 21;
    }
    
    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqy BS";
    }
}
