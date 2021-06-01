package com.mapsengineering.accountingext.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.accountingext.services.E;

/**
 *  GlAccount Model on Product with Detail
 *
 */
public class AccinpPrdDetailFiller extends AccinpPrdFiller {
	public static final String MODULE = AccinpPrdDetailFiller.class.getName();
    
	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpPrdDetailFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

	/**
	 * Return condition for <br>
	 * 1) entryPartyId,entryRoleTypeId OR detailEnumId = ACCDET_NULL <br>
	 * 2) entryProductId OR inputEnumId <> ACCINP_PRD
	 */
	protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        List<EntityCondition> valueCond = FastList.newInstance();
        
        if(UtilValidate.isEmpty(extraCondition)) {
        	return null;
        }
		valueCond.add(conditionValues);

		List<EntityCondition> valueCondPartyAndRole = FastList.newInstance();
        valueCondPartyAndRole.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.entryPartyId.name(), extraCondition.get(E.partyId.name())), EntityCondition.makeCondition(E.entryRoleTypeId.name(), extraCondition.get(E.roleTypeId.name()))));
        valueCondPartyAndRole.add(EntityCondition.makeCondition(E.detailEnumId.name(), InputAndDetailValue.ACCDET_NULL));
        valueCond.add(EntityCondition.makeCondition(valueCondPartyAndRole, EntityJoinOperator.OR));
		
		
        List<EntityCondition> productCond = FastList.newInstance();
    	productCond.add(EntityCondition.makeCondition(E.entryProductId.name(), EntityOperator.EQUALS, extraCondition.get(E.productId.name())));
    	productCond.add(EntityCondition.makeCondition(E.inputEnumId.name(), EntityOperator.NOT_EQUAL, InputAndDetailValue.ACCINP_PRD));
    	valueCond.add(EntityCondition.makeCondition(productCond, EntityJoinOperator.OR));
		
        return EntityCondition.makeCondition(valueCond);
    }
	
	
	/**
	 * Call super.getParametersToStore and super.getPartyRoleExtraParametersToStore
	 * @throws GenericEntityException 
	 */
	public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException{
    	Map<String, Object> extraParametersToStore = FastMap.newInstance();
    	extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition, organizationId));
    	extraParametersToStore.putAll(super.getEntryPartyRoleExtraParametersToStore(extraCondition));
    	extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(organizationId));
        return extraParametersToStore;
    }
}
