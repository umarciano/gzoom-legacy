package com.mapsengineering.workeffortext.test.scorecard;

/** 
 * Test ScoraCard for October
 * @author dain
 *
 */
public class TestScoreCardOct extends ScoreCardTestCase {

    @Override
    protected String getCustomTimePeriodId() {
        return "201210";
    }

    @Override
    protected double getExpectedResult() {
        return 587d;
    }

}
