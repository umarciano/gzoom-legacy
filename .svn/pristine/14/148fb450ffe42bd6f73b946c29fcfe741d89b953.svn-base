package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import javolution.util.FastMap;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.E;
import com.mapsengineering.workeffortext.scorecard.PercentGapConverter;

/**
 * Test Percent Gap Converter Cover
 *
 */
public class TestPercentGapConverter extends BaseTestCase {

    public static final String MODULE = TestPercentGapConverter.class.getName();

    private Map<String, Object> currentKpi;

    protected void setUp() throws Exception {
        super.setUp();
        currentKpi = FastMap.newInstance();
    }

    /**
     * Log
     */
    public void testJobLogger() {
        PercentGapConverter pg = new PercentGapConverter(delegator);
        assertNotNull(pg.getJobLogger());
    }

    /**
     * Target Not Exist
     */
    public void testTargetNotExist() {
        try {
            PercentGapConverter pg = new PercentGapConverter(delegator);
            double result = pg.convert(currentKpi, "W10010", "workEffortMeasureId");
            assertEquals(0d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Is Target Zero
     */
    public void testIsTargetZero() {
        try {
            PercentGapConverter pg = new PercentGapConverter(delegator);
            currentKpi.put(E.targetValue.name(), 0d);
            currentKpi.put(E.debitCreditDefault.name(), "D");
            double result = pg.convert(currentKpi, "W10010", "workEffortMeasureId");
            assertEquals(100d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Result
     */
    public void testResult() {
        try {
            PercentGapConverter pg = new PercentGapConverter(delegator);
            currentKpi.put(E.targetValue.name(), 500d);
            currentKpi.put(E.actualValue.name(), 800d);
            double result = pg.convert(currentKpi, "W10010", "workEffortMeasureId");
            assertEquals(60d, result);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
