package com.mapsengineering.workeffortext.test.scorecard;

import java.util.List;

import javolution.util.FastList;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.AssociatedAchievesResult;

/**
 * Test Associated Achieves Result Cover
 *
 */
public class TestAssociatedAchievesResult extends BaseTestCase {

    /**
     * Count
     */
    public void testScoreCount() {
        AssociatedAchievesResult ar = new AssociatedAchievesResult();
        ar.setScoreCount(100d);
        ar.incrementScoreCount(50d);
        double sc = ar.getScoreCount();
        assertEquals(150d, sc);
    }
    
    /**
     * ChildwithoutScore
     */
    public void testChildwithoutScore() {
        AssociatedAchievesResult ar = new AssociatedAchievesResult();
        List<String> list = FastList.newInstance(); 
        ar.setChildWithoutScore(list);
        assertNotNull(ar.getChildWithoutScore());
    }
}
