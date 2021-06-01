package com.mapsengineering.workeffortext.test.scorecard;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.workeffortext.services.E;

/** 
 * Test ScoraCard for April with update of acctgTrans and acctgTransEntry
 *
 */
public class TestScoreCardApr extends ScoreCardTestCase {

    public static final String MODULE = TestScoreCardApr.class.getName();

    public static final String AMM = "AMM";
    public static final String WECAL = "WECAL";
    public static final String OTH_SCO = "OTH_SCO";
    
    @Override
    protected String getCustomTimePeriodId() {
        return "201204";
    }

    @Override
    protected double getExpectedResult() {
        return 255d;
    }
    
    protected double getExpectedResultOrigAmount() {
        return 255d;
    }
    
    protected double getExpectedResultAmount() {
        return 256d;
    }

    @Override
    protected void assertResult(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResult(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResult(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultAfterUpdate(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultAmount(), value.get(ENTRY_AMOUNT));
        assertEquals(getExpectedResultOrigAmount(), value.get(E.entryOrigAmount.name()));
    }
    
    /**
     * Result
     */
    public void testResult() {
        try {
            super.testResult();
            // TEST UPDATE di un movimento SCORE
            GenericValue value = getFirstMovement(SCORE, WECAL);
            
            String weTransEntryId = value.getString(E.entryAcctgTransEntrySeqId.name());
            String weTransId = value.getString(E.acctgTransId.name());

            executeUpdate(weTransId, weTransEntryId, E.SCORE.name());
            
            GenericValue valueAfterUpdate = getFirstMovement(SCORE, WECAL);
            
            assertResultAfterUpdate(valueAfterUpdate);

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertEccezione();
        }
    }
    
    protected void executeUpdate(String weTransId, String weTransEntryId, String weTransAccountId) throws GenericServiceException{
        Map<String, Object> crudContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_AcctgTransAndEntries", "IN", context);
        
        Map<String, Object> parameters = FastMap.newInstance();
        
        crudContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortTransactionIndicatorView.name());
        crudContext.put("operation", CrudEvents.OP_UPDATE);
        parameters.put("operation", CrudEvents.OP_UPDATE);
        parameters.put(E.weTransId.name(), weTransId);
        parameters.put(E.weTransEntryId.name(), weTransEntryId);
        parameters.put(E.weTransValue.name(), 256d);
        parameters.put(E.weTransAccountId.name(), weTransAccountId);
        parameters.put(E.weTransWeId.name(), context.get(WORK_EFFORT_ID));
        parameters.put(E.weTransTypeValueId.name(), E.ACTUAL.name());
        parameters.put(E.partyId.name(), E.Company.name());
        parameters.put(E.roleTypeId.name(), AMM);
        parameters.put(E.weTransCurrencyUomId.name(), OTH_SCO);
        parameters.put(E.customTimePeriodId.name(), context.get(CUSTOM_TIME_PERIOD_ID));
        parameters.put(E.acctgTransTypeId.name(), E.CTX_BS.name());
        parameters.put(E.defaultOrganizationPartyId.name(), E.Company.name());
        crudContext.put("parameters", parameters);
        
        dispatcher.runSync(E.crudServiceDefaultOrchestration_AcctgTransAndEntries.name(), crudContext);

    }
}
