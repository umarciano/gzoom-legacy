package com.mapsengineering.workeffortext.test.scorecard;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.GenericResult;

/**
 * Test Generic Result Cover
 *
 */
public class TestGenericResult extends BaseTestCase {

    /**
     * Weighted Average
     */
    public void testWeightedAverage() {
        GenericResult g = new GenericResult();
        g.setWeightedAverage(100d);
        g.incrementWeightedAverage(50d);
        double wa = g.getWeightedAverage();
        assertEquals(150d, wa);
    }
    
    /**
     * Alert Count
     */
    public void testAlertCount() {
        GenericResult g = new GenericResult();
        g.setAlertCount(100d);
        g.incrementAlertCount(50d);
        double ac = g.getAlertCount();
        assertEquals(150d, ac);
    }
    
    /**
     * Weight Sum
     */
    public void testWeightSum() {
        GenericResult g = new GenericResult();
        g.setWeightSum(100d);
        g.incrementWeightSum(50d);
        double ws = g.getWeightSum();
        assertEquals(150d, ws);
    }
}
