package com.mapsengineering.workeffortext.test.scorecard;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Test WorkEffort Measure
 *
 */
public class TestWorkEffortMeasure extends ScoreCardTestCaseUno {

    public static final String MODULE = TestWorkEffortMeasure.class.getName();

    /**
     * test WorkEffortMeasure
     */
    public void testWorkEffortMeasure() {
        try {
            printParameters();
            
            List<EntityCondition> ecList = FastList.newInstance();
            ecList.add(EntityCondition.makeCondition(WORK_EFFORT_ID, context.get(WORK_EFFORT_ID)));
            ecList.add(EntityCondition.makeCondition(WE_MEASURE_TYPE_ENUM_ID, WEMT_SCORE));

            List<GenericValue> measList = delegator.findList(WORK_EFFORT_MEASURE, EntityCondition.makeCondition(ecList), null, null, null, false);
            GenericValue meas = EntityUtil.getFirst(measList);
            assertNotNull(meas);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
