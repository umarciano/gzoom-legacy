package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootInqySummeryBS extends TestExecuteChildPerformFindWorkEffortRootInqySummery {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootInqySummeryBS.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap(ContextPermissionPrefixEnum.CTX_BS.getCode());
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqy ";
    }
}
