package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import javolution.util.FastMap;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.E;
import com.mapsengineering.workeffortext.scorecard.ProgresWrkConverter;

/**
 * Test Progres Wrk Converter Cover
 *
 */
public class TestProgresWrkConverter extends BaseTestCase {

    public static final String MODULE = TestProgresWrkConverter.class.getName();

    private Map<String, Object> currentKpi;

    protected void setUp() throws Exception {
        super.setUp();
        currentKpi = FastMap.newInstance();
    }

    /**
     * Progres Wrk Converter
     */
    public void testProgresWrkConverter() {
        try {
            currentKpi.put(E.targetValue.name(), 100d);
            currentKpi.put(E.actualValue.name(), 200d);
            ProgresWrkConverter pc = new ProgresWrkConverter(delegator);
            double result = pc.convert(currentKpi, "W10000", "XEFF.A1.1");
            assertEquals(2d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * NoTarget
     */
    public void testNoTarget() {
        try {
            ProgresWrkConverter pc = new ProgresWrkConverter(delegator);
            double res = pc.convert(currentKpi, "W10000", "XEFF.A1.1");
            assertEquals(0d, res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertFalse(true);
        }
    }
    
    /**
     * Target zero
     */
    public void testTargetzero() {
        try {
            ProgresWrkConverter pc = new ProgresWrkConverter(delegator);
            currentKpi.put(E.targetValue.name(), 0d);
            currentKpi.put(E.debitCreditDefault.name(), "D");
            double res = pc.convert(currentKpi, "W10000", "XEFF.A1.1");
            assertEquals(100d, res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertFalse(true);
        }
    }

    /**
     * Log
     */
    public void testLogger() {
        ProgresWrkConverter pc = new ProgresWrkConverter(delegator);
        assertNotNull(pc.getJobLogger());
    }
}
