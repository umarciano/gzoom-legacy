package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import javolution.util.FastMap;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.E;
import com.mapsengineering.workeffortext.scorecard.MaxRange;

/**
 * Test Max Range Cover
 *
 */
public class TestMaxRange extends BaseTestCase {

    public static final String MODULE = TestMaxRange.class.getName();

    private Map<String, Object> kpi;

    protected void setUp() throws Exception {
        super.setUp();
        kpi = FastMap.newInstance();
    }

    /**
     * Thru Value
     */
    // testare con una fascia valori
    public void testThruValue() {
        try {
            kpi.put(E.weMeasureUomId.name(), "NOT_EXISTS");
            kpi.put(E.workEffortMeasureId.name(), "M10004");
            kpi.put(E.workEffortId.name(), "W10017");
            MaxRange mr = new MaxRange(delegator);
            double result = mr.convert(kpi, Double.MAX_VALUE);
            assertEquals(100d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Ref Value
     */
    public void testRefValue() {
        try {
            kpi.put(E.weMeasureUomId.name(), "12BSC");
            kpi.put(E.workEffortMeasureId.name(), "M10004");
            kpi.put(E.workEffortId.name(), "W10017");
            MaxRange mr = new MaxRange(delegator);
            double result = mr.convert(kpi, 123d);
            assertEquals(123d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * From Value
     */
    public void testFromValue() {
        try {
            kpi.put(E.weMeasureUomId.name(), "12BSC");
            kpi.put(E.workEffortMeasureId.name(), "M10004");
            kpi.put(E.workEffortId.name(), "W10017");
            MaxRange mr = new MaxRange(delegator);
            double result = mr.convert(kpi, -123d);
            assertEquals(100d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Log
     */
    public void testJobLogger() {
        try {
            MaxRange mr = new MaxRange(delegator);
            assertNotNull(mr.getJobLogger());
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
