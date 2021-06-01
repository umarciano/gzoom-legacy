package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import javolution.util.FastMap;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.DirectRange;
import com.mapsengineering.workeffortext.scorecard.E;

/**
 * Test Direct Range Cover
 *
 */
public class TestDirectRange extends BaseTestCase {

    private Map<String, Object> kpi;

    protected void setUp() throws Exception {
        super.setUp();
        kpi = FastMap.newInstance();
    }
    
    /**
     * Not Exists
     */
    public void testNotExists() {
        DirectRange dr = new DirectRange(delegator);
        kpi.put(E.workEffortMeasureId.name(), "M10004");
        kpi.put(E.workEffortId.name(), "W10017");
        kpi.put(E.weMeasureUomId.name(), "NOT_EXISTS");
        double result = dr.convert(kpi, 123d);
        assertEquals(123d, result);
    }
    
    /**
     * Direct Range
     */
    // testare con diverse fasce valori
    public void testDirectRange() {
        DirectRange dr = new DirectRange(delegator);
        kpi.put(E.workEffortMeasureId.name(), "M10004");
        kpi.put(E.workEffortId.name(), "W10017");
        kpi.put(E.weMeasureUomId.name(), "TAC3");
        double result = dr.convert(kpi, 123d);
        assertEquals(123d, result);
    }
    
    /**
     * Direct Range Value
     */
    public void testValueDirectRange() {
        DirectRange dr = new DirectRange(delegator);
        kpi.put(E.workEffortMeasureId.name(), "M10004");
        kpi.put(E.workEffortId.name(), "W10017");
        kpi.put(E.weMeasureUomId.name(), "TAC3");
        double result = dr.convert(kpi, 88d);
        assertEquals(100d, result);
    }
    
    /**
     * Logger
     */
    public void testLogger() {
        DirectRange dr = new DirectRange(delegator);
        assertNotNull(dr.getJobLogger());
    }
}
