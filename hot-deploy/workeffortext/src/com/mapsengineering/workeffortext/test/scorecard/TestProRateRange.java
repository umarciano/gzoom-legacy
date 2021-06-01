package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import javolution.util.FastMap;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.E;
import com.mapsengineering.workeffortext.scorecard.ProRateRange;

/**
 * Test Pro Rate Range
 *
 */
public class TestProRateRange extends BaseTestCase {

    public static final String MODULE = TestProRateRange.class.getName();

    private Map<String, Object> kpi;

    protected void setUp() throws Exception {
        super.setUp();
        kpi = FastMap.newInstance();
    }

    /**
     * test Zero
     */
    public void testZero() {
        try {
            kpi.put(E.weMeasureUomId.name(), "NOT_EXISTING");
            kpi.put(E.workEffortMeasureId.name(), "M10004");
            kpi.put(E.workEffortId.name(), "W10017");
            ProRateRange pr = new ProRateRange(delegator);
            double result = pr.convert(kpi, 123);
            assertEquals(0d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * test 12BSC
     */
    public void test12BSC() {
        try {
            kpi.put(E.weMeasureUomId.name(), "12BSC");
            kpi.put(E.workEffortMeasureId.name(), "M10004");
            kpi.put(E.workEffortId.name(), "W10017");
            ProRateRange pr = new ProRateRange(delegator);
            double result = pr.convert(kpi, 200d);
            double expected = (200d - 100d) * 0d / (500d - 100d);
            assertEquals(expected, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Log
     */
    public void testJobLogger() {
        ProRateRange pr = new ProRateRange(delegator);
        assertNotNull(pr.getJobLogger());
    }
}
