package com.mapsengineering.workeffortext.test.scorecard;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.test.BaseTestCase;

/**
 * Base test case for Score Card
 *
 */
public abstract class ScoreCardTestCase extends BaseTestCase {

    public static final String MODULE = ScoreCardTestCase.class.getName();

    public static final String CUSTOM_TIME_PERIOD = "CustomTimePeriod";
    public static final String WORK_EFFORT = "WorkEffort";
    public static final String WORK_EFFORT_MEASURE = "WorkEffortMeasure";
    public static final String ACCTG_TRANS_ENTRIES_VIEW = "AcctgTransAndEntriesView";
    public static final String CUSTOM_TIME_PERIOD_ID = "customTimePeriodId";
    public static final String WORK_EFFORT_ID = "workEffortId";
    public static final String LAST_CORRECT_SCORE_DATE = "lastCorrectScoreDate";
    public static final String THRU_DATE = "thruDate";
    public static final String GL_ACCOUNT_ID = "glAccountId";
    public static final String WORK_EFFORT_MEASURE_ID = "workEffortMeasureId";
    public static final String VOUCHER_REF = "voucherRef";
    public static final String ENTRY_GL_ACCOUNT_ID = "entryGlAccountId";
    public static final String ENTRY_GL_ACCOUNT_TYPE_ID = "entryGlAccountTypeId";
    public static final String ENTRY_AMOUNT = "entryAmount";
    public static final String TRANSACTION_DATE = "transactionDate";
    public static final String WE_MEASURE_TYPE_ENUM_ID = "weMeasureTypeEnumId";
    public static final String SCORE = "SCORE";
    public static final String WECAL = "WECAL";
    public static final String TARGET = "target";
    public static final String PERFORMANCE = "performance";
    public static final String SCORE_VALUE_TYPE = "scoreValueType";
    public static final String WEMT_SCORE = "WEMT_SCORE";
    public static final String ACTUAL = "ACTUAL";
    public static final String BUDGET = "BUDGET";
    public static final String EXTRA_SCORE_CARD_CALC = "extraScoreCardCalc";
    public static final String SPR10 = "SPR10";

    private GenericValue userLogin;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userLogin = (GenericValue)context.get(USER_LOGIN);

        Locale locale = UtilValidate.isNotEmpty(userLogin.getString(LAST_LOCALE)) ? new Locale(userLogin.getString(LAST_LOCALE)) : Locale.ENGLISH;
        context.put(LOCALE, locale);
        context.put(PERFORMANCE, ACTUAL);
        context.put(TARGET, BUDGET);
        context.put(SCORE_VALUE_TYPE, ACTUAL);
        context.put(WORK_EFFORT_ID, getWorkEffortId());
        context.put(CUSTOM_TIME_PERIOD_ID, getCustomTimePeriodId());
        
        context.put(GenericService.ORGANIZATION_PARTY_ID, COMPANY);
    }

    /**
     * Print parameters, run service scoreCard, assertResult for glAccount SCORE
     */
    public void testResult() {
        try {
            printParameters();

            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync("extraScoreCardCalc", localContext);

            GenericValue value = getFirstMovement(SCORE, WECAL);

            assertResult(value);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertEccezione();
        }
    }

    protected GenericValue getFirstMovement(String glAccountId, String glAccountTypeId) throws GeneralException {
        GenericValue ctp = delegator.findOne(CUSTOM_TIME_PERIOD, UtilMisc.toMap(CUSTOM_TIME_PERIOD_ID, context.get(CUSTOM_TIME_PERIOD_ID)), false);

        Debug.log(" CUSTOM_TIME_PERIOD_ID " + context.get(CUSTOM_TIME_PERIOD_ID));
        Debug.log(" WORK_EFFORT_ID " + context.get(WORK_EFFORT_ID));
        
        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(WORK_EFFORT_ID, context.get(WORK_EFFORT_ID)));
        condList.add(EntityCondition.makeCondition(ENTRY_GL_ACCOUNT_ID, glAccountId));
        condList.add(EntityCondition.makeCondition(ENTRY_GL_ACCOUNT_TYPE_ID, glAccountTypeId));
        condList.add(EntityCondition.makeCondition(TRANSACTION_DATE, ctp.getTimestamp(THRU_DATE)));

        List<GenericValue> valueList = delegator.findList(ACCTG_TRANS_ENTRIES_VIEW, EntityCondition.makeCondition(condList), null, null, null, false);
        Debug.log(" Found " + ACCTG_TRANS_ENTRIES_VIEW + " with condition " + EntityCondition.makeCondition(condList) + " : " + valueList);
        return EntityUtil.getFirst(valueList);

    }

    protected void assertEccezione() {
        assertTrue(false);
    }

    protected String getWorkEffortId() {
        return "W10000";
    }

    protected void assertResult(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResult(), value.get(ENTRY_AMOUNT));
    }

    protected void printParameters() {
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder str = new StringBuilder();
        str.append(lineSeparator);
        str.append("PERFORMANCE              " + context.get(PERFORMANCE));
        str.append(lineSeparator);
        str.append("TARGET                   " + context.get(TARGET));
        str.append(lineSeparator);
        str.append("SCORE_VALUE_TYPE         " + context.get(SCORE_VALUE_TYPE));
        str.append(lineSeparator);
        str.append("WORK_EFFORT_ID           " + context.get(WORK_EFFORT_ID));
        str.append(lineSeparator);

        Debug.log(str.toString());
    }

    protected abstract String getCustomTimePeriodId();

    protected abstract double getExpectedResult();
}
