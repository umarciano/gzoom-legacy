package com.mapsengineering.accountingext.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.accountingext.services.E;

/**
 * GlAccount no Model with Detail
 *
 */
public class AccinpUoDetailFiller extends BaseFiller {

	public static final String MODULE = AccinpUoDetailFiller.class.getName();
    
	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpUoDetailFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

	/**
	 * Return null if extraCondition is empty
	 */
    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        List<EntityCondition> valueCond = FastList.newInstance();
        GenericValue glAccountRef = getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountIdRef), true);

        if(UtilValidate.isEmpty(extraCondition)) {
        	return null;
        }
        
        valueCond.add(conditionValues);

        if (InputAndDetailValue.ACCDET_NULL.equals(glAccountRef.getString(E.detailEnumId.name()))) {
        	valueCond.add(getConditionWithDetail(extraCondition));
        }

        return EntityCondition.makeCondition(valueCond);
        
    }
    
    protected EntityCondition getConditionWithDetail(Map<String, Object> extraCondition){
    	List<EntityCondition> cond = FastList.newInstance();
    	cond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.entryPartyId.name(), extraCondition.get(E.partyId.name())), EntityCondition.makeCondition(E.entryRoleTypeId.name(), extraCondition.get(E.roleTypeId.name()))));
    	
    	return EntityCondition.makeCondition(cond);
    }

	@Override
	public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
		List<Map<String, Object>> extraConditionList = FastList.newInstance();
		//Selezione glAccountRole
        EntityCondition partyParentRoleCondition = EntityCondition.makeCondition(EntityCondition.makeCondition(E.parentTypeId.name(), EntityOperator.LIKE, "GOAL%"), EntityCondition.makeCondition(E.glAccountId.name(), glAccountId));
        List<GenericValue> parents = getDelegator().findList("GlAccountRoleAndParty", partyParentRoleCondition, null, null, null, false);
		
        for(GenericValue glAccountroleAndParty : parents) {        
		    Map<String, Object> extraCondition = FastMap.newInstance();
			extraCondition.put(E.partyId.name(), glAccountroleAndParty.get(E.partyId.name()));
			extraCondition.put(E.roleTypeId.name(), glAccountroleAndParty.get(E.roleTypeId.name()));
			extraConditionList.add(extraCondition);
		}
		return extraConditionList;
	}
	
	/**
	 * Call super.getParametersToStore and super.getPartyRoleExtraParametersToStore
	 * @throws GenericEntityException 
	 */
	public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException{
    	Map<String, Object> extraParametersToStore = FastMap.newInstance();
    	extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition));
    	extraParametersToStore.putAll(super.getEntryPartyRoleExtraParametersToStore(extraCondition));
    	extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(organizationId));
        return extraParametersToStore;
    }
}
