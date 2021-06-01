package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.workeffortext.services.E;

/** 
 * Test ScoraCard for December 2017
 *
 */
public class TestScoreCardSonsAndAssoc extends ScoreCardTestCase {

    public static final String MODULE = TestScoreCardSonsAndAssoc.class.getName();

    public static final String AMM = "AMM";
    public static final String WECAL = "WECAL";
    public static final String OTH_SCO = "OTH_SCO";

    public static final String WORK_EFFORT_TYPE_ID = "15PM012";

    @Override
    protected String getCustomTimePeriodId() {
        return "F2017Y";
    }

    @Override
    /**
     * Peso solo su associativa: 
     * Found E10651 with 68.0 * 100.0
     * Found E10675 with 100.0 * 100.0
     * Found E10676 with 100.0 * 0.0 
     * E quindi totale = 84 */
    protected double getExpectedResult() {
        return 84d;
    }

    /**
     * Peso solo su figli:
     * Calculated score from workEffort "E15870 - ATTIVITA' ORDINARIA DEL SETTORE: LEGALE" = 10000.0 with weight = 100.0
     * Calculated score from workEffort "E15871 - ATTIVITA' ORDINARIA DEL SETTORE: PATRIMONIO" = 9036.363636363636 with weight = 100.0 
     * E quindi totale = 95 */
    protected double getExpectedResultSons() {
        return 95d;
    }

    /**
     * Peso ripartito:
     *  - childScore = 20.0 * 95.18181818181819 = 1903.6363636363637
     *  - assocWorkEffortScore 84.0 * weightAssocWorkEffort 80.0 = 6720.0
     * Score calculated is the sum of childScore (1903.6363636363637) + kpiScore (0.0) + reviewScore (0.0) + assocWorkEffortScore (6720.0)
     * @return
     * E quindi totale = 86 */
    protected double getExpectedSonsAndAssoc() {
        return 86d;
    }

    @Override
    protected void assertResult(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResult(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResult(), value.get(E.entryOrigAmount.name()));
    }

    protected void assertResultSonsAndAssoc(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedSonsAndAssoc(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedSonsAndAssoc(), value.get(E.entryOrigAmount.name()));
    }

    protected void assertResultSons(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultSons(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultSons(), value.get(E.entryOrigAmount.name()));
    }

    protected String getWorkEffortId() {
        return "E12500";
    }

    /**
     * Test
     */
    public void testResult() {
        try {
            /** Test */
            super.testResult();

            /** Test cambio peso */
            GenericValue gv = delegator.findOne(WORK_EFFORT, false, E.workEffortId.name(), getWorkEffortId());
            gv.set(E.weightSons.name(), 100d);
            gv.set(E.weightAssocWorkEffort.name(), 0d);
            gv.store();

            Map<String, Object> localContextNoCalc = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueSons = getFirstMovement(SCORE, WECAL);
            assertResultSons(valueSons);

            /** Test cambio peso */
            gv.set(E.weightSons.name(), 20d);
            gv.set(E.weightAssocWorkEffort.name(), 80d);
            gv.store();

            localContextNoCalc = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueSonsAndAssoc = getFirstMovement(SCORE, WECAL);
            assertResultSonsAndAssoc(valueSonsAndAssoc);

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertEccezione();
        }
    }
}
