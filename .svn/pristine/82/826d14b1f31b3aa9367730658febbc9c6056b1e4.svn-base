package com.mapsengineering.workeffortext.test.scorecard;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/** 
 * Test ScoraCard for December 2013, with workEffort root W70000 (only 1 workEffort), with copy and snapshot
 *
 */
public class TestScoreCard extends ScoreCardTestCase {

    public static final String MODULE = TestScoreCard.class.getName();
    
    public static final String AMM = "AMM";
    public static final String WECAL = "WECAL";
    public static final String OTH_SCO = "OTH_SCO";
    
    @Override
    protected String getCustomTimePeriodId() {
        return "201312";
    }

    @Override
    protected double getExpectedResult() {
        return 83d;
    }
    
    protected double getExpectedResultNoCalc() {
        return 75d;
    }
    
    protected double getExpectedResultNoCalcNoConv() {
        return 50d;
    }
    
    protected double getExpectedResultNoCalcConActual() {
        return 50d;
    }
    
    protected double getExpectedResultNoCalcNoActual() {
        return 25d;
    }
    
    protected double getExpectedResultNoConvNoActual() {
        return 50d;
    }
    
    protected double getExpectedResultNoConv() {
        return 52d;
    }
    
    @Override
    protected void assertResult(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResult(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResult(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultNoCalc(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultNoCalc(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultNoCalc(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultNoCalcNoConv(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultNoCalcNoConv(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultNoCalcNoConv(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultNoCalcConActual(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultNoCalcConActual(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultNoCalcConActual(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultNoConv(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultNoConv(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultNoConv(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultNoCalcNoActual(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultNoCalcNoActual(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultNoCalcNoActual(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultNoConvNoActual(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultNoConvNoActual(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultNoConvNoActual(), value.get(E.entryOrigAmount.name()));
    }
    
    protected String getWorkEffortId() {
        return "W70000";
    }
    
    /**
     * Test with WEWITHPERF_NO_CALC, WECONVER_NOCONVERSIO, with copy W70000 and snapshot W70000
     */
    public void testResult() {
        try {
            context.put(E.workEffortTypeIdFrom.name(), SPR10);
            context.put(E.workEffortId.name(), getWorkEffortId());
            context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime()));
            context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            context.put(E.workEffortTypeIdTo.name(), SPR10);
            context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime()));
            context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            context.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, "Company");
            context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
            context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.Y.name());
            context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.Y.name());
            
            Map<String, Object> result = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result copy " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            assertEquals(0l, result.get(ServiceLogger.BLOCKING_ERRORS));
            assertEquals(1l, result.get(ServiceLogger.RECORD_ELABORATED));
                    
            context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
            context.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
            context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
            context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.N.name());
            context.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, "Company");
            context.put(E.snapShotDescription.name(), "Automatica");
            context.put(E.workEffortTypeIdFrom.name(), SPR10);
            context.put(E.workEffortId.name(), getWorkEffortId());
            context.put(E.workEffortRevisionId.name(), "R10001");
            context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime()));
            context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            context.put(E.workEffortTypeIdTo.name(), SPR10);
            context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime()));
            context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));

            Map<String, Object> result2 = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result snapshot " + result2);
            assertTrue(ServiceUtil.isSuccess(result2));
            assertEquals(0l, result2.get(ServiceLogger.BLOCKING_ERRORS));
            assertEquals(1l, result2.get(ServiceLogger.RECORD_ELABORATED));
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 0, A forzato a 0 -> da valore 100
             * M00003 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 10, A = 10, -> da valore 100
             * M00004 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, ha solo A = 6 (ma a giugno e PRDABS_ABSOLUTE) -> non viene presa */
            super.testResult();
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_NO_CALC, WECONVER_PERCENTWRK, B = 0 -> quindi non viene presa
             * M00003 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 10, A = 10, -> da valore 100
             * M00004 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, ha solo A = 6 (ma a giugno e PRDABS_ABSOLUTE) -> non viene presa */
            GenericValue gvNoCalc = delegator.findOne(WORK_EFFORT_MEASURE, false, E.workEffortMeasureId.name(), "M0000212869");
            gvNoCalc.set(E.weWithoutPerf.name(), "WEWITHPERF_NO_CALC");
            gvNoCalc.store();
            
            Map<String, Object> localContextNoCalc = dispatcher.getDispatchContext().makeValidContext("extraScoreCardCalc", "IN", context);
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueNoCalc = getFirstMovement(SCORE, WECAL);
            assertResultNoCalc(valueNoCalc);
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_NO_CALC, WECONVER_PERCENTWRK, B = 0 -> quindi non viene presa
             * M00003 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 10, A = 10, -> da valore 100
             * M00004 WEWITHPERF_PERF_0, WECONVER_NOCONVERSIO, ha solo A = 6 (ma a giugno e PRDABS_ABSOLUTE) -> da valore 6 */
            GenericValue gvNoConv = delegator.findOne(WORK_EFFORT_MEASURE, false, E.workEffortMeasureId.name(), "M0000412869");
            gvNoConv.set(E.weScoreConvEnumId.name(), "WECONVER_NOCONVERSIO");
            gvNoConv.store();
            
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueNoConv = getFirstMovement(SCORE, WECAL);
            assertResultNoConv(valueNoConv);
            
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_NO_CALC, WECONVER_PERCENTWRK, B = 0 -> quindi non viene presa
             * M00003 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 10, A = 10, -> da valore 100
             * M00004 WEWITHPERF_PERF_0, WECONVER_NOCONVERSIO, rimosso A -> viene forzato a 0 */
            delegator.removeByCondition(E.AcctgTransEntry.name(), EntityCondition.makeCondition(E.acctgTransId.name(), "M0000420131ACT"));
            delegator.removeByCondition(E.AcctgTrans.name(), EntityCondition.makeCondition(E.acctgTransId.name(), "M0000420131ACT"));
            
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueNoConvNoActual = getFirstMovement(SCORE, WECAL);
            assertResultNoConvNoActual(valueNoConvNoActual);
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_NO_CALC, WECONVER_NOCONVERSIO, B = 0 -> quindi non viene presa
             * M00003 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 10, A = 10, -> da valore 100
             * M00004 WEWITHPERF_PERF_0, WECONVER_NOCONVERSIO, rimosso A -> viene forzato a 0 */
            GenericValue gvNoCalcNoConv = delegator.findOne(WORK_EFFORT_MEASURE, false, E.workEffortMeasureId.name(), "M0000212869");
            gvNoCalcNoConv.set(E.weScoreConvEnumId.name(), "WECONVER_NOCONVERSIO");
            gvNoCalcNoConv.store();
            
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueNoCalcNoConv = getFirstMovement(SCORE, WECAL);
            assertResultNoCalcNoConv(valueNoCalcNoConv);
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_NO_CALC, WECONVER_NOCONVERSIO, B = 0 -> quindi non viene presa
             * M00003 WEWITHPERF_NO_CALC, WECONVER_PERCENTWRK, B = 10, A = 10, -> da valore 100
             * M00004 WEWITHPERF_PERF_0, WECONVER_NOCONVERSIO, rimosso A -> viene forzato a 0 */
            GenericValue gvNoCalcConActual = delegator.findOne(WORK_EFFORT_MEASURE, false, E.workEffortMeasureId.name(), "M0000312869");
            gvNoCalcConActual.set(E.weWithoutPerf.name(), "WEWITHPERF_NO_CALC");
            gvNoCalcConActual.store();
            
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueNoCalcConActual = getFirstMovement(SCORE, WECAL);
            assertResultNoCalcConActual(valueNoCalcConActual);
            
            /** Test 5 misure, di cui 
             * SAL03 non e' una prestazione e quindi non e' estratta
             * M00001 WEWITHPERF_PERF_0, WECONVER_PERCENTWRK, B = 6, A = 3 -> da valore di 50
             * M00002 WEWITHPERF_NO_CALC, WECONVER_NOCONVERSIO, B = 0 -> non viene presa
             * M00003 WEWITHPERF_NO_CALC, WECONVER_PERCENTWRK, rimosso B, A = 10, -> non viene presa
             * M00004 WEWITHPERF_PERF_0, WECONVER_NOCONVERSIO, rimosso A -> viene forzato a 0 */
            delegator.removeByCondition(E.AcctgTransEntry.name(), EntityCondition.makeCondition(E.acctgTransId.name(), "M000032013ACT"));
            delegator.removeByCondition(E.AcctgTrans.name(), EntityCondition.makeCondition(E.acctgTransId.name(), "M000032013ACT"));
            delegator.removeByCondition(E.AcctgTransEntry.name(), EntityCondition.makeCondition(E.acctgTransId.name(), "M0000320131ACT"));
            delegator.removeByCondition(E.AcctgTrans.name(), EntityCondition.makeCondition(E.acctgTransId.name(), "M0000320131ACT"));
           
            dispatcher.runSync(EXTRA_SCORE_CARD_CALC, localContextNoCalc);
            GenericValue valueNoCalcNoActual = getFirstMovement(SCORE, WECAL);
            assertResultNoCalcNoActual(valueNoCalcNoActual);
            
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertEccezione();
        }
    }
}
