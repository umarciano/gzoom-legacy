package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.workeffortext.services.E;

/** 
 * Test ScoraCard per Soglie, with workEffort root 20220101U1469
 *
 */
public class TestScoreCardSoglie extends ScoreCardTestCase {

    public static final String MODULE = TestScoreCardSoglie.class.getName();
    
    @Override
    protected String getCustomTimePeriodId() {
        return "Y2022";
    }

    @Override
    protected double getExpectedResult() {
        return 97d;
    }
    
    @Override
    protected void assertResult(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResult(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResult(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultWithSoglie(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultWithSoglie(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultWithSoglie(), value.get(E.entryOrigAmount.name()));
    }
    
    
    
    private double getExpectedResultWithSoglie() {
        return 95d;
    }

    protected String getWorkEffortId() {
        return "20220101U1469";
    }
    
    /**
     * Test
     */
    public void testResult() {
        try {
            /** Test solo con Target e Consuntivo */
            super.testResult();
            
            /** Test solo con Soglie */
            context.put(PERFORMANCE, ACTUAL);
            context.put(TARGET, BUDGET);
            context.put(SCORE_VALUE_TYPE, ACTUAL);
            context.put("limitExcellent", "UPPER_LIMIT");
            context.put("limitMax", "UPPER_LIMIT");
            context.put("limitMed", "MEDIUM_LIMIT");
            context.put("limitMin", "LOWER_LIMIT");
            
            Map<String, Object> localContextNoCalc = dispatcher.getDispatchContext().makeValidContext(EXTRA_SCORE_CARD_CALC, "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueWithSoglie = getFirstMovement(SCORE, WECAL);
            assertResultWithSoglie(valueWithSoglie);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertEccezione();
        }
    }
}
