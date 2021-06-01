package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

/** 
 * Test ScoraCard for December 2018
 *
 */
public class TestScoreCardParentPeriod extends ScoreCardTestCase {

    public static final String MODULE = TestScoreCardParentPeriod.class.getName();
    
    @Override
    protected double getExpectedResult() {
        return 70d;
    }
    
    protected double getExpectedResultOpen() {
        return 70d; // TODO 63d;
    }
    
    protected double getExpectedResultParent() {
        return 63d;
    }

    private double getExpectedResultT2() {
        return 65; // TODO 56d;
    }
    
    protected String getWorkEffortId() {
        return "W80000";
    }
    
    @Override
    protected String getCustomTimePeriodId() {
        Debug.log("getCustomTimePeriodId");
        return "F2018T1";
    }
    
    /**
     * Test incrociato tra TARGET_PARENT_PERIOD, WEWITHTARG_PRV_SCORE
     */
    public void testResult() {
        try {
            /** Test  */
            Debug.log("Test with TARGET_PARENT_PERIOD e WEWITHTARG_PRV_SCORE");
            printParameters();
            super.testResult();
            
            GenericValue gv = delegator.findOne("WorkEffortType", false, "workEffortTypeId", "SPR10");
            gv.set("scorePeriodEnumId", "SCORE_PERIOD_OPEN");
            gv.store();
            printParameters();
            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContext);
            GenericValue value = getFirstMovement(SCORE, WECAL);
            Debug.log("SCORE_PERIOD_OPEN value " + value);
            assertResultOpen(value); 
              
            
            gv.set("scorePeriodEnumId", "SCORE_PERIOD_PARENT");
            gv.store();
            context.put(CUSTOM_TIME_PERIOD_ID, "F2018T1");
            printParameters();
            localContext = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContext);
            value = getFirstMovement(SCORE, WECAL);
            Debug.log("SCORE_PERIOD_PARENT value " + value);
            assertResultParent(value);
            
            context.put(CUSTOM_TIME_PERIOD_ID, "F2018T2");
            gv.set("scorePeriodEnumId", "SCORE_PERIOD_ALL");
            gv.store();
            printParameters();
            localContext = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContext);
            value = getFirstMovement(SCORE, WECAL);
            Debug.log("F2018T2 value " + value);
            assertResultT2(value);
            
            
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertEccezione();
        }
    }
    
    protected void assertResultT2(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultT2(), value.get(ENTRY_AMOUNT));
    }
    
    protected void assertResultOpen(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultOpen(), value.get(ENTRY_AMOUNT));
    }
    
    protected void assertResultParent(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultOpen(), value.get(ENTRY_AMOUNT));
    }
}
