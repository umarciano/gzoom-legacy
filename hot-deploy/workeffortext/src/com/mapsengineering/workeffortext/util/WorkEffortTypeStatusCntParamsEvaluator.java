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

import bsh.EvalError;

/**
 * cerca i params del WorkEffortTypeStatusCnt
 * @author nito
 *
 */
public class WorkEffortTypeStatusCntParamsEvaluator {
	private Delegator delegator;
	private String workEffortId;
	private String workEffortTypeId;
	private String statusId;
	private String contentId;
	private Map<String, Map<String, Object>> paramsContentMap;
	
	/**
	 * costruttore
	 * @param delegator
	 */
	public WorkEffortTypeStatusCntParamsEvaluator(Delegator delegator) {
		this.delegator = delegator;
		this.paramsContentMap = new HashMap<String, Map<String, Object>>();
	}
	
	/**
	 * inizializza i campi
	 * @param workEffortId
	 * @param workEffortTypeId
	 * @param statusId
	 * @param contentId
	 * @throws GenericEntityException
	 */
	public void init(String workEffortId, String workEffortTypeId, String statusId, String contentId) throws GenericEntityException {
		this.workEffortId = workEffortId;
		this.contentId = contentId;
		if (UtilValidate.isEmpty(workEffortTypeId) || UtilValidate.isEmpty(statusId)) {
			GenericValue workEffort = getWorkEffort();
			if (UtilValidate.isNotEmpty(workEffort)) {
				this.workEffortTypeId = workEffort.getString(E.workEffortTypeId.name());
				this.statusId = workEffort.getString(E.currentStatusId.name());
			}
		} else {
			this.workEffortTypeId = workEffortTypeId;
			this.statusId = statusId;
		}
	}
	
	/**
	 * esegue ricarca parametri
	 * @throws GenericEntityException
	 * @throws EvalError
	 */
	public void run() throws GenericEntityException, EvalError {
		if (UtilValidate.isEmpty(contentId)) {
			evaluateParams();
		} else {
			evaluateParamsWithContentId();
		}
	}
	
	/**
	 * associa i parametri al contentId
	 * @throws GenericEntityException
	 * @throws EvalError
	 */
	private void evaluateParamsWithContentId() throws GenericEntityException, EvalError {
		Map<String, String> conditionMap = new HashMap<String, String>();
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		conditionMap.put(E.workEffortTypeId.name(), workEffortTypeId);
		conditionMap.put(E.statusId.name(), statusId);
		conditionMap.put(E.contentId.name(), contentId);
		GenericValue workEffortTypeStatusCnt = delegator.findOne(E.WorkEffortTypeStatusCnt.name(), conditionMap, false);
		if (UtilValidate.isNotEmpty(workEffortTypeStatusCnt) && UtilValidate.isNotEmpty(workEffortTypeStatusCnt.getString(E.params.name()))) {
			BshUtil.eval(workEffortTypeStatusCnt.getString(E.params.name()), paramsMap);
			paramsMap.remove("context");
			paramsMap.remove("bsh");
		}
		paramsContentMap.put(contentId, paramsMap);
	}
	
	/**
	 * associa i parametri ai contenId
	 * @throws GenericEntityException
	 * @throws EvalError
	 */
	private void evaluateParams() throws GenericEntityException, EvalError {
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		conditionList.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
		conditionList.add(EntityCondition.makeCondition(E.statusId.name(), statusId));
		List<GenericValue> workEffortTypeStatusCntList = delegator.findList(E.WorkEffortTypeStatusCnt.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
		if (UtilValidate.isNotEmpty(workEffortTypeStatusCntList)) {
			for (GenericValue workEffortTypeStatusCnt : workEffortTypeStatusCntList) {
				if (UtilValidate.isNotEmpty(workEffortTypeStatusCnt)) {
					Map<String, Object> paramsMap = new HashMap<String, Object>();
					if (UtilValidate.isNotEmpty(workEffortTypeStatusCnt.getString(E.params.name()))) {
						BshUtil.eval(workEffortTypeStatusCnt.getString(E.params.name()), paramsMap);
						paramsMap.remove("context");
						paramsMap.remove("bsh");
					}
					paramsContentMap.put(workEffortTypeStatusCnt.getString(E.contentId.name()), paramsMap);
				}
			}
		}
	}
	
	/**
	 * trova il WorkEffort
	 * @return
	 * @throws GenericEntityException
	 */
	private GenericValue getWorkEffort() throws GenericEntityException {
		GenericValue workEffort = null;
		if (UtilValidate.isNotEmpty(workEffortId)) {
			Map<String, String> workEffortMap = new HashMap<String, String>();
			workEffortMap.put(E.workEffortId.name(), workEffortId);
			workEffort = delegator.findOne(E.WorkEffort.name(), workEffortMap, false);
		}
		return workEffort;
	}
	
	/**
	 * get content map
	 * @return
	 */
	public Map<String, Map<String, Object>> getParamsContentMap() {
		return paramsContentMap;
	}
}
