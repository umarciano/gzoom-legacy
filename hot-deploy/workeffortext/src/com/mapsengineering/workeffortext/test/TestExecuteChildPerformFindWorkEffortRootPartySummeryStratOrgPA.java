package com.mapsengineering.workeffortext.test;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgPA extends TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrg {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgPA.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap(ContextPermissionPrefixEnum.CTX_PA.getCode());
    }
}
