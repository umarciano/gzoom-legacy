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

import com.mapsengineering.accountingext.services.E;

/**
 * GlAccount no Model with Detail
 *
 */
public class AccinpUoDetectFiller extends BaseFiller {

	public static final String MODULE = AccinpUoDetectFiller.class.getName();
    
	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpUoDetectFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

	/**
	 * Return null if extraCondition is empty
	 */
    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        // se non ho ne workeffortId ne partyId-roleTypeId non fa nulla
        if(UtilValidate.isEmpty(extraCondition)) {
            return null;
        }
        
        GenericValue glAccountRef = getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountIdRef), true);
        List<EntityCondition> valueCond = FastList.newInstance();
        
        valueCond.add(conditionValues);
        
        if (E.Y.name().equals(glAccountRef.getString(E.detectOrgUnitIdFlag.name()))) {
            valueCond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.partyId.name(), extraCondition.get(E.partyId.name())), EntityCondition.makeCondition(E.roleTypeId.name(), extraCondition.get(E.roleTypeId.name()))));
        }
        
        return EntityCondition.makeCondition(valueCond);
        
    }
    
	@Override
	public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
		List<Map<String, Object>> extraConditionList = FastList.newInstance();
		// se presente workEffortId si lavora solo su l'unita' organizzativa della scheda , else li ricerca tra i movimenti gia' presenti
		String workEffortId = (String) context.get(E.workEffortId.name());
		if (UtilValidate.isNotEmpty(workEffortId)) {
		    Map<String, Object> extraCondition = getPartyAndRoleFromWorkEffort(workEffortId);
		    extraConditionList.add(extraCondition);
		} else {
		    extraConditionList.addAll(populateExtraCondition(glAccountId));
		}
		return extraConditionList;
	}
	
	private List<Map<String, Object>> populateExtraCondition(String glAccountId) throws GenericEntityException {
	    List<Map<String, Object>> extraConditionList = FastList.newInstance();
        EntityCondition condition = EntityCondition.makeCondition(
                EntityCondition.makeCondition(E.glFiscalTypeId.name(), getGlFiscalTypeIdInput()), 
                EntityCondition.makeCondition(E.transactionDate.name(), getThruDate()), 
                EntityCondition.makeCondition(E.glAccountId.name(), glAccountId));
        List<GenericValue> parents = getDelegator().findList("AcctgTransAndEntryAndGlAccountRef", condition, null, null, null, false);

        for(GenericValue transRoleAndParty : parents) {        
            if(UtilValidate.isNotEmpty(checkOrgUnitIdExist(transRoleAndParty))) {
                extraConditionList.add(checkOrgUnitIdExist(transRoleAndParty));
            }
        }
        return extraConditionList;
    }

    private Map<String, Object> checkOrgUnitIdExist(GenericValue transRoleAndParty) {
        Map<String, Object> extraCondition = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(transRoleAndParty.get(E.orgUnitId.name())) && UtilValidate.isNotEmpty(transRoleAndParty.get(E.orgUnitRoleTypeId.name()))) {
            extraCondition.put(E.partyId.name(), transRoleAndParty.get(E.orgUnitId.name()));
            extraCondition.put(E.roleTypeId.name(), transRoleAndParty.get(E.orgUnitRoleTypeId.name()));
        }
        return extraCondition;
    }

    /**
	 * Call super.getParametersToStore and super.getPartyRoleExtraParametersToStore
	 * @throws GenericEntityException 
	 */
	public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException{
    	Map<String, Object> extraParametersToStore = FastMap.newInstance();
    	extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition));
    	extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(extraCondition));
        return extraParametersToStore;
    }
	
	private Map<String, Object> getPartyAndRoleFromWorkEffort(String workEffortId) throws GenericEntityException {
        Map<String, Object> mappa = FastMap.newInstance();
        
        GenericValue workEffort = getDelegator().findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
        mappa.put(E.partyId.name(), workEffort.get(E.orgUnitId.name()));
        mappa.put(E.roleTypeId.name(), workEffort.get(E.orgUnitRoleTypeId.name()));
        return mappa;
    }
}
