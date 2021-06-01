package com.mapsengineering.emplperf.update;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.emplperf.update.ParamsEnum;

/**
 * Condition for PartyEvalInCharge
 */
public class EmplPerfReadConditionCreator {
	
	/**
	 * return estimatedStartDate conditions
	 * @param condition
	 * @param estimatedCompletionDate
	 */
	protected void buildEstimatedStartDateCondition(List<EntityCondition> condition, Timestamp estimatedCompletionDate) {
        if (UtilValidate.isNotEmpty(estimatedCompletionDate)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.estimatedStartDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, estimatedCompletionDate));
        }	
	}

    /**
     * return estimatedCompletionDate conditions
     * @param condition
     * @param estimatedStartDate
     * @param readDate
     * @param evalEndDate
     */
    protected void buildEstimatedCompletionDateCondition(List<EntityCondition> condition, Timestamp estimatedStartDate, Timestamp readDate) {
    	if (UtilValidate.isNotEmpty(estimatedStartDate)) {
    		condition.add(EntityCondition.makeCondition(ParamsEnum.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDate));
    	}
    	List<EntityCondition> conditionList = buildEstimatedCompletionDateOrCondition(readDate);
    	if (UtilValidate.isNotEmpty(conditionList)) {
    		condition.add(EntityCondition.makeCondition(conditionList, EntityOperator.OR));
    	}
    }
    
    /**
     * 
     * @param readDate
     * @param evalEndDate
     * @return
     */
    private List<EntityCondition> buildEstimatedCompletionDateOrCondition(Timestamp readDate) {
    	List<EntityCondition> conditionList = FastList.newInstance();
    	if (UtilValidate.isNotEmpty(readDate)) {
    		conditionList.add(EntityCondition.makeCondition(ParamsEnum.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN, readDate));
    	}
    	List<EntityCondition> evalDateConditionList = buildEvalDateCondition();
    	conditionList.add(EntityCondition.makeCondition(evalDateConditionList));
    	
    	return conditionList;
    }
    
    /**
     * 
     * @return
     */
    private List<EntityCondition> buildEvalDateCondition() {
    	List<EntityCondition> evalDateConditionList = FastList.newInstance();
    	evalDateConditionList.add(EntityCondition.makeCondition(ParamsEnum.estimatedCompletionDate.name(), EntityOperator.EQUALS_FIELD, EmplPerfServiceEnum.evalEndDate.name()));
    	evalDateConditionList.add(EntityCondition.makeCondition(EmplPerfServiceEnum.evalEndDate.name(), EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD));
    	
    	return evalDateConditionList;
    }    
  
}
