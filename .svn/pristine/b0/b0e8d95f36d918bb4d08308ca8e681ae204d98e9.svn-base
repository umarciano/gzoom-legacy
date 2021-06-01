package com.mapsengineering.workeffortext.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummery extends BaseTestExecuteChildPerformFindWorkEffortRoot {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummery.class.getName();

    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());

        try {
            Map<String, Object> rootSearchRootInqyServiceRes = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqyPartySummary", context);
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummary = " + rootSearchRootInqyServiceRes);
        } catch (GenericServiceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqyPartySummary ";
    }
}
