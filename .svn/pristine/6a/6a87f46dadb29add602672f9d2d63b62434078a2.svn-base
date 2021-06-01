package com.mapsengineering.accountingext.test;

import java.util.Map;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service indicator output
 * Gestione del modello per prodotti servizi come indicatore di output
 * Gestione del dettaglio (su modello per prodotti servizi) come indicatore di output
 *
 */
public class TestIndicatorCalcServicesPrd extends BaseTestIndicatorCalcServices {

    /**
     * Test ACCINP_PRD No Row to process for the indicator id G20010
     */
    public void testAcctgTransInterface3ANoRowForIndicator() {
        // Calculation id G20010 method id = A/B*1000 inputEnumId = ACCINP_PRD,
        // detailEnumId = ACCDET_NULL, but no workEffortMeasure = no productid
        // A G10000 ACCINP_UO
        // A G10002 ACCINP_UO
        // B G20002 ACCINP_PRD
        Map<String, Object> result = super.runTest("G20010", null, ACTUAL, ACTUAL, getRefDate2012(), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test ACCINP_PRD Create Movement
     */
    public void testAcctgTransInterface3A() {
        // Calculation id G20001 method id = A/B*1000 inputEnumId = ACCINP_PRD,
        // detailEnumId = ACCDET_NULL
        // A G10000 ACCINP_UO
        // A G10002 ACCINP_UO
        // B G20002 ACCINP_PRD
        // 1 mov with amount = 21.818,18 origAmount = 21.818,18 perfAmountCalc = 0
        Map<String, Object> result = super.runTest("G20001", null, ACTUAL, ACTUAL, getRefDate2012(), 1);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }

    /**
     * Test ACCINP_PRD ACCDET_NOSUM Create movement
     */
    public void testAcctgTransInterface3D() {
        // Calculation method id = A/B*100 inputEnumId = ACCINP_PRD, detailEnumId != ACCDET_NOSUM
        // A TEST5 ACCINP_UO ACCDET_NOSUM
        // B TEST6 ACCINP_UO ACCDET_NULL
        // A TEST7 ACCINP_PRD ACCDET_NOSUM
        // B TEST8 ACCINP_PRD ACCDET_NULL
        // 1 mov with amount = 1 origAmount = 1 perfAmountCalc = 0
        Map<String, Object> result = super.runTest("TEST4", null, ACTUAL, ACTUAL, getRefDate2012(), 1);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
