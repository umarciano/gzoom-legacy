package com.mapsengineering.workeffortext.test.scorecard;

/** 
 * Test ScoraCard for July
 * @author dain
 *
 */
public class TestScoreCardJul extends ScoreCardTestCase {

    @Override
    protected String getCustomTimePeriodId() {
        return "201207";
    }

    @Override
    protected double getExpectedResult() {
        return 489d;
    }

}
