package com.mapsengineering.accountingext.test;

import java.util.Map;

import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.accountingext.services.E;
import com.mapsengineering.accountingext.services.InputAndDetailValue;
import com.mapsengineering.base.util.JobLogger;

/**
 * Test Indicator Calc Service indicator output with inputEnumId = ACCINP_OBJ
 * Gestione del modello per obiettivi come indicatore di output
 *
 */
public class TestIndicatorCalcServicesAnno extends BaseTestIndicatorCalcServices {
    private static final String GL_ACCOUNT_ID_FOR_ANNO = "G60007";
    private static final Double AMOUNT_FOR_OLD_MOV = 17.0;
    
    /**
     * Test ACCINP_OBJ same workEffort Create movement for 2012,
     * WORK_EFFORT_ASSOC in 2012
     */
    public void testAcctgTransInterfaceAnno() {
    	updateCustomMethodFromAggregToAnnoSum();
    	runTestAcctgTransInterfaceAnno();
        updateCustomMethodFromAnnoSumToAggreg();
    }    
    
    /**
     * Test ACCINP_OBJ same workEffort
     */
    private void runTestAcctgTransInterfaceAnno() {
        Map<String, Object> result = super.runTest(GL_ACCOUNT_ID_FOR_ANNO, null, ACTUAL, ACTUAL, getRefDate2013(), 2, AMOUNT_FOR_OLD_MOV);
        assertEquals(0l, result.get(JobLogger.ERROR_MESSAGES));
    }
}
