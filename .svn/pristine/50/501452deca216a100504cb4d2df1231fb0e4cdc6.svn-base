package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import javolution.util.FastMap;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.E;
import com.mapsengineering.workeffortext.scorecard.NoConversion;

/**
 * Test No Conversion Cover
 *
 */
public class TestNoConversion extends BaseTestCase {

    public static final String MODULE = TestNoConversion.class.getName();

    private Map<String, Object> currentKpi;

    protected void setUp() throws Exception {
        super.setUp();
        currentKpi = FastMap.newInstance();
    }

    /**
     * No Conversion
     */
    public void testNoConversion() {
        try {
            NoConversion nc = new NoConversion(delegator);
            currentKpi.put(E.actualValue.name(), 500d);
            double res = nc.convert(currentKpi, "W10000", "M10004");
            assertEquals(500d, res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Log
     */
    public void testLogger() {
        NoConversion nc = new NoConversion(delegator);
        assertNotNull(nc.getJobLogger());
    }
}
