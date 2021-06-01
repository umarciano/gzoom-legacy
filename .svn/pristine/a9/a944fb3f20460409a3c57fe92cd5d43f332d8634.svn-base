package com.mapsengineering.accountingext.test;

import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service
 *
 */
public class TestIndicatorCalcServices extends BaseTestIndicatorCalcServices {

    /**
     * Test Error NOT_EXIST Indicator id is not a valid identifier
     */
    public void testIndicatorCalcGlAccountNotExists() {
        // NOT_EXIST Indicator id is not a valid identifier
        Map<String, Object> result = super.runTest("NOT_EXIST", null, ACTUAL, ACTUAL, UtilDateTime.toTimestamp(MOUNTH_AUG, DAY_31, YEAR_2012, 0, 0, 0), 0);
        assertEquals(1l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test Error No Input Calc for the indicator id G90000
     */
    public void testIndicatorCalcNoInputCalc() {
        // No Input Calc for the indicator id G90000
        // Calculation method id = A/B inputEnumId = ACCINP_UO, detailEnumId = ACCDET_NULL
        // A factorCalculator is null
        // B factorCalculator is null
        Map<String, Object> result = super.runTest("G90000", null, ACTUAL, ACTUAL, UtilDateTime.toTimestamp(MOUNTH_AUG, DAY_31, YEAR_2012, 0, 0, 0), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test Error No input calc for B, but B is denominator
     */
    public void testIndicatorCalcWithoutOneInputCalc() {
        // Calculation method id = A/B inputEnumId = ACCINP_UO, detailEnumId = ACCDET_NULL
        // A factorCalculator is G90002
        // B factorCalculator is null
        Map<String, Object> result = super.runTest("G90001", null, ACTUAL, ACTUAL, getRefDate2013(), 0);
        assertEquals(1l, result.get(JobLogger.ERROR_MESSAGES));

    }

    /**
     * Test C=A/B
     */
    public void testIndicatorAdivB() {
        // formula C=A/B error amount div by 0 and warning origAmount div by 0
        Map<String, Object> result = super.runTest(null, new Long(10000), BUDGET, BUDGET, getRefDate2013(), null);
        assertEquals(1l, result.get(JobLogger.ERROR_MESSAGES));
    }
    
    /**
     * Test IndicatorInputCalc with GlFiscalType
     */
    public void testIndicatorInputCalcWithFiscalType() {
    	Map<String, Object> result = super.runTest("G60008", null, BUDGET, BUDGET, getRefDate2012(), 1, 2.0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
