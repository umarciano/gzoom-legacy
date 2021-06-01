package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.ReadkpiConditionCreator;

/**
 * Test Read kpiCondition Creator Cover
 *
 */
public class TestReadkpiConditionCreator extends BaseTestCase {

    /**
     * test Periodical
     */
    public void testPeriodical() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2012, 11, 31, 0, 0, 0);
        Date date = cal.getTime(); 	
        ReadkpiConditionCreator rc = new ReadkpiConditionCreator(delegator, date, "M_10004", date, "TARGET_EXEC_PERIOD", null, "W_10000", "XEFF.A1.1", "Company");
        EntityCondition cond = rc.createReadKpiCondition(date, date, date, date, date, date, date, "BUDGET", "BUDGET", "BUDGET", "BUDGET", "ACTUAL");
        assertNotNull(cond);
    }
}
