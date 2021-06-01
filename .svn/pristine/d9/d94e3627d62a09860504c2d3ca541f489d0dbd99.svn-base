package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.AbsoluteGapConverter;
import com.mapsengineering.workeffortext.scorecard.E;

/**
 * Absolute Gap Converter Cover
 *
 */
public class TestAbsoluteGapConverter extends BaseTestCase {

    public static final String MODULE = TestAbsoluteGapConverter.class.getName();

    private Map<String, Object> kpi;

    protected void setUp() throws Exception {
        super.setUp();
        kpi = FastMap.newInstance();
    }

    /**
     * Test zero
     */
    public void testAbsoluteGapConverterZero() {
        try {
            AbsoluteGapConverter ag = new AbsoluteGapConverter(delegator);
            double res = ag.convert(kpi, "W10000", "M10004");
            assertEquals(0d, res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Test
     */
    public void testAbsoluteGapConverter() {
        try {
            AbsoluteGapConverter ag = new AbsoluteGapConverter(delegator);
            kpi.put(E.actualValue.name(), 200d);
            kpi.put(E.targetValue.name(), 50d);
            double res = ag.convert(kpi, "W10000", "M10004");
            assertEquals(150d, res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Log
     */
    public void testLogger() {
        AbsoluteGapConverter ag = new AbsoluteGapConverter(delegator);
        assertNotNull(ag.getJobLogger());
    }
}
