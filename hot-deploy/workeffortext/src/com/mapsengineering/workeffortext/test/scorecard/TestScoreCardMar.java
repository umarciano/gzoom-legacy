package com.mapsengineering.workeffortext.test.scorecard;

/** 
 * Test ScoraCard for March
 * @author dain
 *
 */
public class TestScoreCardMar extends ScoreCardTestCase {

    @Override
    protected String getCustomTimePeriodId() {
        return "201203";
    }

    @Override
    protected double getExpectedResult() {
        return 184d;
    }

}
