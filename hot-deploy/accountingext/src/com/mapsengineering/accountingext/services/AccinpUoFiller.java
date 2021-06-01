package com.mapsengineering.accountingext.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;

/**Standard case: GlAccount has inputEnmuId = 'ACCINP_UO' and detailEnumId = 'ACCDET_NULL' 
 * Select :
	ACCTG_TRANS.TRANSACTION_DATE = InputMap.Date
	ACCTG_TRANS_ENTRY.GL_ACCOUNT_ID = GL_ACCOUNT_INPUT_CALC.GL_ACCOUNT_ID_REF
	ACCTG_TRANS_ENTRY.GL_FISCAL_TYPE_ID = InputMap.TipoRilevazione
*/
public class AccinpUoFiller extends BaseFiller {

	public static final String MODULE = AccinpUoFiller.class.getName();
    
	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	public AccinpUoFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }
    
    @Override
	public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
		List<Map<String, Object>> extraConditionList = FastList.newInstance();
		Map<String, Object> extraCondition = FastMap.newInstance();
		extraConditionList.add(extraCondition);
		return extraConditionList;
	}
	
	   /**
     * Call super.getParametersToStore and super.getPartyRoleExtraParametersToStore
     * @throws GenericEntityException 
     */
    public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException{
        Map<String, Object> extraParametersToStore = FastMap.newInstance();
        extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition));
        extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(organizationId));
        return extraParametersToStore;
    }

    @Override
    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition condition) throws GenericEntityException {
        return condition;
    }

}
