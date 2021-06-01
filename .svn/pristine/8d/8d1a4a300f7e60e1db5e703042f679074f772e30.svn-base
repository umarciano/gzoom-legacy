package com.mapsengineering.accountingext.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * GlAccount Model on WorkEffort
 *
 */
public class AccinpWeFiller extends BaseFiller {

	public static final String MODULE = AccinpWeFiller.class.getName();
    
	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpWeFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

	protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        List<EntityCondition> valueCond = FastList.newInstance();
        List<GenericValue> wemList = FastList.newInstance();
        GenericValue glAccountRef = getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountIdRef), true);
 
        if(UtilValidate.isEmpty(extraCondition)) {
        	return null;
        }
        valueCond.add(conditionValues);

        if (InputAndDetailValue.ACCINP_OBJ.equals(glAccountRef.getString(E.inputEnumId.name()))) {
        	EntityCondition conditionModelloObiettivo = getConditionModelloObiettivo(glAccountIdRef, wemList, extraCondition);
        	if(UtilValidate.isNotEmpty(conditionModelloObiettivo)) {
        		valueCond.add(conditionModelloObiettivo);
        	} else {
        		return null;
        	}
        } else {
        	valueCond.add(getConditionModelloNonObiettivo(wemList, extraCondition));
        }

        return EntityCondition.makeCondition(valueCond);
    }
    
    /**Create condition for indicator with ACCINP_OBJ <br/>
     * 05443_IndicatoriCalcolati. 
     * Query preliminare per determinare il voucher-ref di lettura */
    protected EntityCondition getConditionModelloObiettivo(String glAccountIdRef, List<GenericValue> wemList, Map<String, Object> extraCondition) throws GenericEntityException{
        List<EntityCondition> cond = FastList.newInstance();
        String workEffortId = (String) extraCondition.get(E.workEffortId.name());
        
        List<GenericValue> wemLetturaList = getDelegator().findList(E.WorkEffortMeasure.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.glAccountId.name(), glAccountIdRef), 
                EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, workEffortId)), null, null, null, true);
        
        GenericValue wemLettura = EntityUtil.getFirst(wemLetturaList);
        if(UtilValidate.isNotEmpty(wemLettura)) {
            cond.add(EntityCondition.makeCondition(E.entryVoucherRef.name(), EntityOperator.EQUALS, wemLettura.getString(E.workEffortMeasureId.name())));
        } else {
            List<GenericValue> wemAcctgList = getDelegator().findList(E.WorkEfforMeasAcctgTEView.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.accGlAccountId.name(), glAccountIdRef), 
                    EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, workEffortId)), null, null, null, true);
            GenericValue wemAcctg = EntityUtil.getFirst(wemAcctgList);
            if (UtilValidate.isNotEmpty(wemAcctg)) {
                cond.add(EntityCondition.makeCondition(E.entryVoucherRef.name(), EntityOperator.EQUALS, wemAcctg.getString(E.workEffortMeasureId.name())));
            } else {
                return null;
            }
        }
        
        List<EntityCondition> cond2 = FastList.newInstance();
    	List<EntityCondition> wemConditionList = new ArrayList<EntityCondition>();
    	wemConditionList.add(EntityCondition.makeCondition(E.glAccountId.name(), glAccountIdRef));
    	wemConditionList.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
    	wemConditionList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getYearEnd(getThruDate())));
    	wemConditionList.add(EntityCondition.makeCondition(getThruDateCondition(), EntityJoinOperator.OR));   	
    	List<GenericValue> wemLetturaList2 = getDelegator().findList(E.WorkEffortMeasure.name(), EntityCondition.makeCondition(wemConditionList), null, null, null, true);
    	
    	GenericValue wemLettura2 = EntityUtil.getFirst(wemLetturaList2);
    	if(UtilValidate.isNotEmpty(wemLettura2)) {
    		cond2.add(EntityCondition.makeCondition(E.entryVoucherRef.name(), EntityOperator.EQUALS, wemLettura2.getString(E.workEffortMeasureId.name())));
    	} else {
        	List<EntityCondition> wemAcctgConditionList2 = new ArrayList<EntityCondition>();
        	wemAcctgConditionList2.add(EntityCondition.makeCondition(E.accGlAccountId.name(), glAccountIdRef));
        	wemAcctgConditionList2.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
        	wemAcctgConditionList2.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getYearEnd(getThruDate())));
        	wemAcctgConditionList2.add(EntityCondition.makeCondition(getThruDateCondition(), EntityJoinOperator.OR));
    		List<GenericValue> wemAcctgList2 = getDelegator().findList(E.WorkEfforMeasAcctgTEView.name(), EntityCondition.makeCondition(wemAcctgConditionList2), null, null, null, true);
    		GenericValue wemAcctg2 = EntityUtil.getFirst(wemAcctgList2);
    		if (UtilValidate.isNotEmpty(wemAcctg2)) {
    			cond2.add(EntityCondition.makeCondition(E.entryVoucherRef.name(), EntityOperator.EQUALS, wemAcctg2.getString(E.workEffortMeasureId.name())));
    		} else {
    			return null;
    		}
    	}
        
    	return EntityCondition.makeCondition(cond2);
    }
    
    /**
     * imposta la condizione sul thruDate
     * @return
     */
    private List<EntityCondition> getThruDateCondition() {
    	List<EntityCondition> wemConditioThruDatenList = new ArrayList<EntityCondition>();
    	wemConditioThruDatenList.add(EntityCondition.makeCondition(E.thruDate.name(), GenericEntity.NULL_FIELD));
    	wemConditioThruDatenList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getYearStart(getThruDate())));
    	return wemConditioThruDatenList;
    }
    
    protected EntityCondition getConditionModelloNonObiettivo(List<GenericValue> wemList, Map<String, Object> extraCondition){
    	List<EntityCondition> cond = FastList.newInstance();
    	List<EntityCondition> weCond = FastList.newInstance();
    	List<EntityCondition> productCond = FastList.newInstance();

    	List<EntityCondition> valueCondPartyAndRole = FastList.newInstance();
    	
    	weCond.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, extraCondition.get(E.workEffortId.name())));
    	weCond.add(EntityCondition.makeCondition(E.workEffortId.name(), GenericValue.NULL_FIELD));
    	cond.add(EntityCondition.makeCondition(weCond, EntityJoinOperator.OR));
    	
    	productCond.add(EntityCondition.makeCondition(E.entryProductId.name(), EntityOperator.EQUALS, extraCondition.get(E.productId.name())));
    	productCond.add(EntityCondition.makeCondition(E.entryProductId.name(), GenericValue.NULL_FIELD));
    	cond.add(EntityCondition.makeCondition(productCond, EntityJoinOperator.OR));
    	
    	valueCondPartyAndRole.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.entryPartyId.name(), EntityOperator.EQUALS, extraCondition.get(E.partyId.name())), EntityCondition.makeCondition(E.entryRoleTypeId.name(), EntityOperator.EQUALS, extraCondition.get(E.roleTypeId.name()))));
    	valueCondPartyAndRole.add(EntityCondition.makeCondition(E.entryPartyId.name(), GenericValue.NULL_FIELD));
    	valueCondPartyAndRole.add(EntityCondition.makeCondition(E.entryRoleTypeId.name(), GenericValue.NULL_FIELD));
    	
    	cond.add(EntityCondition.makeCondition(valueCondPartyAndRole, EntityJoinOperator.OR));
        return EntityCondition.makeCondition(cond);
    }

	@Override
	public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
		String workEffortId = (String) context.get(E.workEffortId.name());
	    List<Map<String, Object>> extraConditionList = FastList.newInstance();
		List<GenericValue> wemList = FastList.newInstance();
		
		List<EntityCondition> condition = FastList.newInstance();
		condition.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.glAccountId.name(), glAccountId)));
		
		/**
		 * Usato nel calcolo degli indicatori da obiettivo
		 */
		if (UtilValidate.isNotEmpty(workEffortId)) {
			condition.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, workEffortId));
		} else {
			condition.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.NOT_EQUAL, GenericValue.NULL_FIELD));
		}
		condition.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getYearEnd(getThruDate())));
		condition.add(EntityCondition.makeCondition(getThruDateCondition(), EntityJoinOperator.OR));     
        
		wemList = getDelegator().findList(E.WorkEffortMeasure.name(), EntityCondition.makeCondition(condition), null, null, null, true);
		        
        if(UtilValidate.isEmpty(wemList)) {
        	return null;
        }
		for(GenericValue wem : wemList) {        
		    Map<String, Object> extraCondition = FastMap.newInstance();
			extraCondition.put(E.productId.name(), wem.get(E.productId.name()));
			extraCondition.put(E.workEffortId.name(), wem.get(E.workEffortId.name()));
			extraCondition.put(E.workEffortMeasureId.name(), wem.get(E.workEffortMeasureId.name()));
			extraCondition.put(E.partyId.name(), wem.get(E.partyId.name()));
			extraCondition.put(E.roleTypeId.name(), wem.get(E.roleTypeId.name()));
			extraConditionList.add(extraCondition);
		}
		return extraConditionList;
	}
	
	/**
	 * Call super.getParametersToStore and super.getWeExtraParametersToStore
	 * @throws GenericEntityException 
	 */
	public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException{
		Map<String, Object> extraParametersToStore = FastMap.newInstance();
    	extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition));
    	extraParametersToStore.putAll(super.getWeExtraParametersToStore(extraCondition));
    	return extraParametersToStore;
    }

}
