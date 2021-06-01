package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummeryEP extends TestExecuteChildPerformFindWorkEffortRootPartySummery {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummeryEP.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap(ContextPermissionPrefixEnum.CTX_EP.getCode());
    }
}
