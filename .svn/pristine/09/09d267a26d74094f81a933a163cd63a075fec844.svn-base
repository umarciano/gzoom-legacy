package com.mapsengineering.workeffortext.test.scorecard;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.AssociatedAchievesReader;
import com.mapsengineering.workeffortext.scorecard.AssociatedAchievesResult;

/**
 * Test Associated Achieves Reader Cover
 *
 */
public class TestAssociatedAchievesReader extends BaseTestCase {

    public static final String MODULE = TestAssociatedAchievesReader.class.getName();

    /**
     * Reader
     */
    public void testAssociatedAchievesReader() {
        try {
            GenericValue ct = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", "201212"), true);
            AssociatedAchievesReader r = new AssociatedAchievesReader(delegator);
            AssociatedAchievesResult res = r.readAssociatedAchieves("W10021", ct.getTimestamp("thruDate"), "ACTUAL", "ACTUAL", "12BSC", "W10000");
            assertNotNull(res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    /**
     * Log
     */
    public void testLogger() {
        AssociatedAchievesReader r = new AssociatedAchievesReader(delegator);
        assertNotNull(r.getJobLogger());
    }

}
