package com.mapsengineering.workeffortext.test;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.base.test.BaseTestCase;

/**
 * Base TestInsertUpdateMovement
 *
 */
public abstract class BaseTestInsertUpdateMovement extends BaseTestCase {

    public static final String MODULE = BaseTestInsertUpdateMovement.class.getName();
    private static final double AMOUNT_IN_CREATE = 356d;
    private static final double AMOUNT_IN_UPDATE = 355d;

    protected void setUp() throws Exception {
        super.setUp();
        context.put(GenericService.ORGANIZATION_PARTY_ID, COMPANY);
    }
    
    protected double getExpectedResult() {
        return AMOUNT_IN_CREATE;
    }
    
    protected double getExpectedResultAfterUpdate() {
        return AMOUNT_IN_UPDATE;
    }

    protected void assertResult(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResult(), value.get(E.entryAmount.name()));
        assertEquals(getExpectedResult(), value.get(E.entryOrigAmount.name()));
    }
    
    protected void assertResultAfterUpdate(GenericValue value) {
        assertNotNull(value);
        assertEquals(getExpectedResultAfterUpdate(), value.get(E.entryAmount.name()));
        assertEquals(getExpectedResultAfterUpdate(), value.get(E.entryOrigAmount.name()));
    }
    
    protected String getWorkEffortId() {
        return "W10000";
    }
    
    protected String getWorkEffortMeasureId() {
        return null;
    }
    
    protected String getCustomTimePeriodId() {
        return "201204";
    }
    
    protected String getRoleTypeId() {
        return "AMM";
    }
    
    protected String getGlAccountTypeId() {
        return "SSK";
    }
    
    protected String getUomId() {
        return "OTH_SCO";
    }
    
    protected String getGlAccountId() {
        return "G10001";
    }
    
    /**
     * Is different from glAccountId for FINANCIAL 
     * @return
     */
    protected String getEntryGlAccountId() {
        return "G10001";
    }
    
    /**
     * Create transaction
     */
    protected void createMovement() {
        try {
            Map<String, Object> localContext = setServiceMap(CrudEvents.OP_CREATE, getExpectedResult(), null, null); 
            
            dispatcher.runSync(E.crudServiceDefaultOrchestration_AcctgTransAndEntries.name(), localContext);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
        
    private Map<String, Object> setServiceMap(String operation, double weTransValue, String weTransId, String weTransEntryId) throws GenericServiceException {
        Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_AcctgTransAndEntries", "IN", context);

        Map<String, Object> parameters = FastMap.newInstance();
        
        localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortTransactionIndicatorView.name());
        localContext.put("operation", operation);
        parameters.put("operation", operation);
        
        parameters.put(E.weTransValue.name(), weTransValue);
        parameters.put(E.weTransId.name(), weTransId);
        parameters.put(E.weTransEntryId.name(), weTransEntryId);
        
        parameters.put(E.weTransAccountId.name(), getGlAccountId());
        parameters.put(E.weTransWeId.name(), getWorkEffortId());
        parameters.put(E.weTransMeasureId.name(), getWorkEffortMeasureId());
        parameters.put(E.weTransTypeValueId.name(), E.ACTUAL.name());
        parameters.put(E.partyId.name(), E.Company.name());
        parameters.put(E.roleTypeId.name(), getRoleTypeId());
        parameters.put(E.weTransCurrencyUomId.name(), getUomId());
        parameters.put(E.customTimePeriodId.name(), getCustomTimePeriodId());
        parameters.put(E.acctgTransTypeId.name(), E.CTX_BS.name());
        parameters.put(E.defaultOrganizationPartyId.name(), E.Company.name());
        localContext.put("parameters", parameters);

        return localContext;
    }
    
    protected abstract String getTitle(); 

    /**
     * Create transaction
     */
    protected GenericValue getMovement() {
        GenericValue value = null;
        try {
        
            GenericValue ctp = delegator.findOne(E.CustomTimePeriod.name(), UtilMisc.toMap(E.customTimePeriodId.name(), getCustomTimePeriodId()), false);

            Debug.log(" CUSTOM_TIME_PERIOD " + getCustomTimePeriodId());
            List<EntityCondition> condList = FastList.newInstance();
            condList.add(EntityCondition.makeCondition(E.workEffortId.name(), getWorkEffortId()));
            condList.add(EntityCondition.makeCondition(E.entryGlAccountId.name(), getEntryGlAccountId()));
            condList.add(EntityCondition.makeCondition(E.entryGlAccountTypeId.name(), getGlAccountTypeId()));
            condList.add(EntityCondition.makeCondition(E.transactionDate.name(), ctp.getTimestamp(E.thruDate.name())));

            List<GenericValue> valueList = delegator.findList(E.AcctgTransAndEntriesView.name(), EntityCondition.makeCondition(condList), null, null, null, false);
            Debug.log(" Found AcctgTransAndEntriesView with condition " + EntityCondition.makeCondition(condList) + " : " + valueList);
            value = EntityUtil.getFirst(valueList);
            
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
        return value;
    }    
        
    /**
     * Check the movement creates, update it and check if it is correct
     */
    protected void updateMovement() {
        try {
            GenericValue value = getMovement();
            assertResult(value);
            
            String weTransEntryId = value.getString(E.entryAcctgTransEntrySeqId.name());
            String weTransId = value.getString(E.acctgTransId.name());

            Map<String, Object> localContext = setServiceMap(CrudEvents.OP_UPDATE, getExpectedResultAfterUpdate(), weTransId, weTransEntryId); 
            
            dispatcher.runSync(E.crudServiceDefaultOrchestration_AcctgTransAndEntries.name(), localContext);

            GenericValue valueAfterUpdate = getMovement();
            assertResultAfterUpdate(valueAfterUpdate);
            
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    

    /**
     * Create transaction, then update it
     */
    public void testInsertUpdateResult() {
        Debug.log(getTitle());
        try {
            createMovement();

            updateMovement();
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
