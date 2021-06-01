package com.mapsengineering.workeffortext.test.scorecard;

import org.ofbiz.entity.GenericValue;

/**
 * Test Wrong Root
 *
 */
public class TestWrongRoot extends ScoreCardTestCase {

    @Override
    protected String getWorkEffortId() {
        return "WORK_EFFORT_NOT_EXISTS";
    }
    
    @Override
    protected String getCustomTimePeriodId() {
        return "201212";
    }

    @Override
    protected double getExpectedResult() {
        return 0;
    }
    
    @Override
    protected void assertResult(GenericValue value) {
        assertNull(value);
    }
    
    @Override
    protected void assertEccezione() {
        assertTrue(true);
    }
}
