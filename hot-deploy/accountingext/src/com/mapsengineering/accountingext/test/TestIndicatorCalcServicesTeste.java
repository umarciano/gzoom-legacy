package com.mapsengineering.accountingext.test;

import java.util.Map;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service
 *
 */
public class TestIndicatorCalcServicesTeste extends BaseTestIndicatorCalcServices {

    /**
     * Test write movement for G70000
     */
    public void testIndicatorTeste() {
        updateCustomMethodTeste();
        Map<String, Object> result = super.runTest(null, 10l, BUDGET, BUDGET, getRefDate2013(), 38);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test, no found G70009, beacuse no GlAccountOrganization
     */
    public void testIndicatorTesteError() {
        Map<String, Object> result = super.runTest(null, 11l, BUDGET, BUDGET, getRefDate2013(), null);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
