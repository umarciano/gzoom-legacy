package com.mapsengineering.workeffortext.test.scorecard;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.ExternalAchievesResult;

/**
 * Test External Achieves Result Cover
 *
 */
public class TestExternalAchievesResult extends BaseTestCase {

    /**
     * External Achieves Result
     */
    public void testExternalAchievesResult() {
        ExternalAchievesResult er = new ExternalAchievesResult();
        er.setScoreCount(100d);
        er.incrementScoreCount(50d);
        double count = er.getScoreCount();
        assertEquals(150d, count);
    }
    
}
