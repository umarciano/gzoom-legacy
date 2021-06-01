package com.mapsengineering.accountingext.test;

import java.util.Map;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service
 *
 */
public class TestIndicatorCalcServicesFteMM extends BaseTestIndicatorCalcServices {

    /**
     * Test write movement for G70000
     */
    public void testIndicatorFteMM() {
        updateCustomMethodFteMM();
        Map<String, Object> result = super.runTest(null, 10l, BUDGET, BUDGET, getRefDate2013(), 38);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
