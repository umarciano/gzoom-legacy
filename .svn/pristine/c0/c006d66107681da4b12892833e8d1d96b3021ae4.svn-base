package com.mapsengineering.accountingext.test;

import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service indicator output with inputEnumId = ACCINP_OBJ
 * Gestione del modello per obiettivi come indicatore di output
 *
 */
public class TestIndicatorCalcServicesObj extends BaseTestIndicatorCalcServices {

    /**
     * Test ACCINP_OBJ No Input Calc for the indicator id G20012
     */
    public void testAcctgTransInterface3BNoInputCalc() {
        // Calculation method id = A/B*100 inputEnumId = ACCINP_OBJ,
        // detailEnumId = ACCDET_NULL 
        // Found 0 input Calc.
        Map<String, Object> result = super.runTest("G20012", null, ACTUAL, ACTUAL, UtilDateTime.toTimestamp(MOUNTH_AUG, DAY_31, YEAR_2012, 0, 0, 0), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test ACCINP_OBJ same workEffort No Row to process for the indicator id G20030
     */
    public void testAcctgTransInterface3BNoRowForIndicator() {
        // Calculation method id = A/B*100 inputEnumId = ACCINP_OBJ,
        // detailEnumId = ACCDET_NULL NO WerkEffortMeasure = no row
        // A G20031 ACCINP_OBJ stesso we //
        // B G20032 ACCINP_OBJ stesso we
        Map<String, Object> result = super.runTest("G20030", null, ACTUAL, ACTUAL, getRefDate2013(), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test ACCINP_OBJ same workEffort Create movement
     */
    public void testAcctgTransInterface3B() {
        // Calculation method id = A/B*100 inputEnumId = ACCINP_OBJ,
        // detailEnumId = ACCDET_NULL
        // A G20014 ACCINP_OBJ stesso we //
        // B G20015 ACCINP_OBJ stesso we
        // 1 mov with amount = 1 origAmount = 1 perfAmountCalc = 0
        Map<String, Object> result = super.runTest("G20013", null, ACTUAL, ACTUAL, getRefDate2012(), 1);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
