package com.mapsengineering.workeffortext.test.scorecard;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

/** 
 * Test ScoraCard for December 2018, with workEffort root W80001 (only 1 workEffort), with copy and snapshot
 *
 */
public class TestScoreCardParentNoPeriod extends TestScoreCard {

    public static final String MODULE = TestScoreCardParentNoPeriod.class.getName();
    
    @Override
    protected double getExpectedResult() {
        return 0d;
    }
    
    @Override
    protected void assertResult(GenericValue value) {
        assertNull(value);
    }
    
    protected void assertResultNoCalc(GenericValue value) {
        assertNull(value);
    }
    
    protected void assertResultNoCalcNoConv(GenericValue value) {
        assertNull(value);
    }
    
    protected void assertResultNoCalcConActual(GenericValue value) {
        assertNull(value);
    }
    
    protected void assertResultNoConv(GenericValue value) {
        assertNull(value);
    }
    
    protected void assertResultNoCalcNoActual(GenericValue value) {
        assertNull(value);
    }
    
    protected void assertResultNoConvNoActual(GenericValue value) {
        assertNull(value);
    }
    
    protected String getWorkEffortId() {
        return "W80001";
    }
    
    @Override
    protected String getCustomTimePeriodId() {
        Debug.log("getCustomTimePeriodId");
        return "F2018T1";
    }
}
