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
import org.ofbiz.entity.util.EntityUtil;

/**Standard case: GlAccount has inputEnmuId = 'ACCINP_PRD' and detailEnumId = 'ACCDET_NULL' 
 * Select :
	ACCTG_TRANS.TRANSACTION_DATE = InputMap.Date
	ACCTG_TRANS_ENTRY.GL_ACCOUNT_ID = GL_ACCOUNT_INPUT_CALC.GL_ACCOUNT_ID_REF
	ACCTG_TRANS_ENTRY.GL_FISCAL_TYPE_ID = InputMap.TipoRilevazione
*/
public class AccinpPrdFiller extends BaseFiller {

	public static final String MODULE = AccinpPrdFiller.class.getName();
    
	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpPrdFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

	protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        if(UtilValidate.isEmpty(extraCondition)) {
        	return null;
        }
        List<EntityCondition> valueCond = FastList.newInstance();
        
        GenericValue glAccountRef = getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountIdRef), true);
        
        valueCond.add(conditionValues);

        if (InputAndDetailValue.ACCINP_PRD.equals(glAccountRef.getString(E.inputEnumId.name()))) {
        	String productId = (String) extraCondition.get(E.productId.name());
        	valueCond.add(EntityCondition.makeCondition(E.entryProductId.name(), EntityOperator.EQUALS, productId));
        }

        return EntityCondition.makeCondition(valueCond);
    }
	
	@Override
	public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
	    List<Map<String, Object>> extraConditionList = FastList.newInstance();
		List<String> productIdList = getProductIdList(glAccountId);
		for(String productId : productIdList) {
			Map<String, Object> extraCondition = FastMap.newInstance();
			extraCondition.put("productId", productId);
			extraConditionList.add(extraCondition);
		}
		return extraConditionList;
	}
	
	/**
	 * Call super.getParametersToStore and super.getProductExtraParametersToStore
	 * @throws GenericEntityException 
	 */
	public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException{
		Map<String, Object> extraParametersToStore = FastMap.newInstance();
    	extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition));
    	extraParametersToStore.putAll(super.getProductExtraParametersToStore(extraCondition));
    	extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(organizationId));
        return extraParametersToStore;
    }
	
	protected List<String> getProductIdList(String glAccountId) throws GenericEntityException{
    	List<GenericValue> wemList = FastList.newInstance();
    	//Selezione dei prodotto ammessi
        wemList = getDelegator().findList(E.WorkEffortMeasure.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.glAccountId.name(), glAccountId), EntityCondition.makeCondition(E.workEffortId.name(), GenericValue.NULL_FIELD), EntityCondition.makeCondition(E.productId.name(), EntityOperator.NOT_EQUAL, GenericValue.NULL_FIELD)), null, null, null, true);
        return EntityUtil.getFieldListFromEntityList(wemList, E.productId.name(), true);
    }
}
