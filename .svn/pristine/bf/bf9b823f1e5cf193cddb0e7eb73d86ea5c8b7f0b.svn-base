package com.mapsengineering.workeffortext.test.scorecard;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Test value not null
 * @author dain
 *
 */
public class TestValue extends ScoreCardTestCaseUno {

    public static final String MODULE = TestValue.class.getName();

    /**
     * Test value
     */
    public void testValue() {
        try {
            printParameters();
            
            GenericValue ctp = delegator.findOne(CUSTOM_TIME_PERIOD, UtilMisc.toMap(CUSTOM_TIME_PERIOD_ID, context.get(CUSTOM_TIME_PERIOD_ID)), false);
            
            List<EntityCondition> ecList = FastList.newInstance();
            ecList.add(EntityCondition.makeCondition(WORK_EFFORT_ID, context.get(WORK_EFFORT_ID)));
            ecList.add(EntityCondition.makeCondition(TRANSACTION_DATE, ctp.getTimestamp(THRU_DATE)));
            ecList.add(EntityCondition.makeCondition(ENTRY_GL_ACCOUNT_ID, SCORE));
            List<GenericValue> values = delegator.findList(ACCTG_TRANS_ENTRIES_VIEW, EntityCondition.makeCondition(ecList), null, null, null, false);
            GenericValue value = EntityUtil.getFirst(values);
            assertNotNull(value);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
