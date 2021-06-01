package com.mapsengineering.workeffortext.test.scorecard;

/** 
 * Test ScoraCard for February
 * @author dain
 *
 */
public class TestScoreCardFeb extends ScoreCardTestCase {

    @Override
    protected String getCustomTimePeriodId() {
        return "201202";
    }

    @Override
    protected double getExpectedResult() {
        return 50d;
    }

}
