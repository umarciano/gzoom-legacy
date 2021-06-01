package com.mapsengineering.accountingext.test;

import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service for indicator output with inputEnumId = ACCINP_UO
 * OR Gestione del dettaglio (<> 'ACCDET_NULL') non modello (ACCINP_UO) come indicatore di output
 *
 */
public class TestIndicatorCalcServicesUo extends BaseTestIndicatorCalcServices {
    private static final String GL_ACCOUNT_ID_FOR_UO = "G20000";
    /**
     * Test Case 2A No Row to process for factor calculation A
     */
    public void testAcctgTransInterfaceNoRowForFactor() {
        // Indicator Id = G20000, Calculation method id = A/B*100
        // Indicator inputEnumId = ACCINP_UO, detailEnumId = ACCDET_NULL
        // A G10000 // no row to process
        // B G10001 // no row to process
        Map<String, Object> result = super.runTest(GL_ACCOUNT_ID_FOR_UO, null, BUDGET, BUDGET, UtilDateTime.toTimestamp(MOUNTH_AUG, DAY_31, YEAR_2012, 0, 0, 0), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test Create movement
     */
    public void testAcctgTransInterface() {
        // Indicator Id = G20000, Calculation method id = A/B*100
        // Indicator inputEnumId = ACCINP_UO, detailEnumId = ACCDET_NULL
        // A G10000
        // B G10001
        // 1 mov with amount = 2.544,44 origAmount = 2.544,44 perfAmountCalc = 0
        Map<String, Object> result = super.runTest(GL_ACCOUNT_ID_FOR_UO, null, ACTUAL, ACTUAL, getRefDate2012(), 1);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test ACCINP_UO Create movement
     */
    public void testAcctgTransInterface3C() {
        // Calculation method id = A/B*100 inputEnumId = ACCINP_UO, detailEnumId != ACCDET_NOSUM
        // A TEST2 ACCINP_UO ACCDET_NULL
        // B TEST3 ACCINP_PRD ACCDET_NULL
        Map<String, Object> result = super.runTest("TEST1", null, ACTUAL, ACTUAL, getRefDate2012(), 1);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
