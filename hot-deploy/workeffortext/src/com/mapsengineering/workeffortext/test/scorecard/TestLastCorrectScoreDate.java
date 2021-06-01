package com.mapsengineering.workeffortext.test.scorecard;

import java.sql.Timestamp;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

/**
 * Test Last Correct Score Date
 *
 */
public class TestLastCorrectScoreDate extends ScoreCardTestCaseUno {

    public static final String MODULE = TestLastCorrectScoreDate.class.getName();

    /**
     * Test
     */
    public void testLastCorrectScoreDate() {
        try {
            printParameters();
            
            GenericValue wrk = delegator.findOne(WORK_EFFORT, UtilMisc.toMap(WORK_EFFORT_ID, context.get(WORK_EFFORT_ID)), false);
            Timestamp lastCorrectScoreDate = wrk.getTimestamp(LAST_CORRECT_SCORE_DATE);
            
            assertNotNull(lastCorrectScoreDate);
            
            GenericValue ctp = delegator.findOne(CUSTOM_TIME_PERIOD, UtilMisc.toMap(CUSTOM_TIME_PERIOD_ID, context.get(CUSTOM_TIME_PERIOD_ID)), false);
            assertEquals(ctp.getTimestamp(THRU_DATE), lastCorrectScoreDate);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
