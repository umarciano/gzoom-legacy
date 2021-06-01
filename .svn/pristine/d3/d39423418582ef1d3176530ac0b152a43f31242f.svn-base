package com.mapsengineering.workeffortext.test.scorecard;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.ExternalAchievesReader;
import com.mapsengineering.workeffortext.scorecard.ExternalAchievesResult;

/**
 * TestExternalAchieveReader
 */
public class TestExternalAchieveReader extends BaseTestCase {

    public static final String MODULE = TestExternalAchieveReader.class.getName();

    /**
     * TEST readExternalAchieves
     */
    public void testExternalAchieveReader() {
        try {
            ExternalAchievesReader er = new ExternalAchievesReader(delegator);
            GenericValue ct = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", "201212"), true);
            ExternalAchievesResult res = er.readExternalAchieves("W10021", ct.getTimestamp("thruDate"), "ACTUAL", "12BSC", "M_10005");
            assertNotNull(res);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * TEST readExternalAchieves Exception
     */
    public void testExternalAchieveReaderException() {
        try {
            ExternalAchievesReader er = new ExternalAchievesReader(delegator);
            GenericValue ct = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", "201212"), true);
            er.readExternalAchieves("NOT_EXISTS", ct.getTimestamp("thruDate"), "ACTUAL", "12BSC", "M_10005");
            assertTrue(false);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            // Debug.logError(e, MODULE);
            assertTrue(true);
        }
    }
}
