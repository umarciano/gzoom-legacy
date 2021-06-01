package com.mapsengineering.accountingext.test;

import java.util.Map;

import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service indicator output with inputEnumId = ACCINP_OBJ
 * Gestione del modello per obiettivi come indicatore di output
 *
 */
public class TestIndicatorCalcServicesAggreg extends BaseTestIndicatorCalcServices {
    private static final String GL_ACCOUNT_ID_FOR_AGGREG = "G60007";
    private static final Double AMOUNT_FOR_OLD_MOV = 10.0;
    private static final Double AMOUNT_FOR_NEW_MOV = 17.0;
    private static final Double MAX_AMOUNT_FOR_NEW_MOV = 9.0;
    
    /**
     * Test ACCINP_OBJ same workEffort
     */
    protected void runTestAcctgTransInterfaceAggreg(boolean isMax) {
        Map<String, Object> result = super.runTest(GL_ACCOUNT_ID_FOR_AGGREG, null, ACTUAL, ACTUAL, getRefDate2013(), 1, AMOUNT_FOR_OLD_MOV);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));

        result = super.runTest(GL_ACCOUNT_ID_FOR_AGGREG, null, ACTUAL, ACTUAL, getRefDate2012(), 1, isMax ? MAX_AMOUNT_FOR_NEW_MOV : AMOUNT_FOR_NEW_MOV);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));

        result = super.runTest(GL_ACCOUNT_ID_FOR_AGGREG, null, ACTUAL, ACTUAL, getRefDate2014(), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));

        result = super.runTest(GL_ACCOUNT_ID_FOR_AGGREG, null, BUDGET, BUDGET, getRefDate2013(), 1, AMOUNT_FOR_OLD_MOV);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));

        result = super.runTest(GL_ACCOUNT_ID_FOR_AGGREG, null, BUDGET, BUDGET, getRefDate2012(), 1, isMax ? MAX_AMOUNT_FOR_NEW_MOV : AMOUNT_FOR_NEW_MOV);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));

        result = super.runTest(GL_ACCOUNT_ID_FOR_AGGREG, null, BUDGET, BUDGET, getRefDate2014(), 0);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
