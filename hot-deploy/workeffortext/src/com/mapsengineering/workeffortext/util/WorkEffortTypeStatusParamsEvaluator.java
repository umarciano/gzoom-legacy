package com.mapsengineering.workeffortext.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import bsh.EvalError;

/**
 * Cerca i params specifici di un WorkEffortTypeStatus
 */
public class WorkEffortTypeStatusParamsEvaluator {
	private Map<String, Object> context;
    private Delegator delegator;

    /**
     * la classe si occupa di estrarre i params della WorkEffortTypeStatus e di popolare il context
     * @param context
     * @param parameters
     * @param delegator
     */
	public WorkEffortTypeStatusParamsEvaluator(Map<String, Object> context, Delegator delegator) {
		this.context = context;
        this.delegator = delegator;
    }

    /**
     * estrae i params, utilizzando workEffortTypeRootId e currentStatusId in input
     * @param workEffortTypeRootId
     * @param currentStatusId
     * @param putInContext
     * @return
     * @throws GenericEntityException
     * @throws EvalError
     */
    public Map<String, Object> evaluateParams(String workEffortTypeRootId, String currentStatusId, boolean putInContext) throws GenericEntityException, EvalError {
        Map<String, Object> mapParams = getParams(workEffortTypeRootId, currentStatusId);
        if (putInContext) {
        	context.putAll(mapParams);
        }      
        return mapParams;
    }

    /**
     * estrae i params con workEffortTypeRootId e currentStatusId in input
     * @param workEffortTypeRootId
     * @param currentStatusId
     * @return
     * @throws GenericEntityException
     * @throws EvalError
     */
    public Map<String, Object> getParams(String workEffortTypeRootId, String currentStatusId) throws GenericEntityException, EvalError {
        Map<String, Object> mapParams = new HashMap<String, Object>();
        List<EntityCondition> conditionWorkEffortTypeStatus = new ArrayList<EntityCondition>();
        conditionWorkEffortTypeStatus.add(EntityCondition.makeCondition(E.workEffortTypeRootId.name(), EntityOperator.EQUALS, workEffortTypeRootId));
        conditionWorkEffortTypeStatus.add(EntityCondition.makeCondition(E.currentStatusId.name(), EntityOperator.EQUALS, currentStatusId));
        conditionWorkEffortTypeStatus.add(EntityCondition.makeCondition(E.params.name(), EntityOperator.NOT_EQUAL, null));

        List<GenericValue> workEffortTypeContentList = delegator.findList(E.WorkEffortTypeStatus.name(), EntityCondition.makeCondition(conditionWorkEffortTypeStatus), null, null, null, false);
        GenericValue workEffortTypeContent = EntityUtil.getFirst(workEffortTypeContentList);
        if (UtilValidate.isNotEmpty(workEffortTypeContent) && UtilValidate.isNotEmpty(workEffortTypeContent.getString(E.params.name()))) {
            BshUtil.eval(workEffortTypeContent.getString(E.params.name()), mapParams);
            mapParams.remove("context");
            mapParams.remove("bsh");
        }
        return mapParams ;
    }
}
