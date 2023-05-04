package com.mapsengineering.workeffortext.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import bsh.EvalError;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusCntParamsEvaluator;

public class TestWorkEffortTypeStatusCntParamsEvaluator extends BaseTestCase {
	private final String W60002 = "W60002";
	private final String PEG12 = "PEG12";
	private final String WEPERFST_PLANINIT = "WEPERFST_PLANINIT";
	private final String WEFLD_NOTE = "WEFLD_NOTE";
	private final String WEFLD_NOTE2 = "WEFLD_NOTE";
	private final String WEFLD_NOTE3 = "WEFLD_NOTE";
	private final String hideContent = "hideContent";
	private final String Y = "Y";
	
    public enum E {
    	WorkEffortTypeStatusCnt, workEffortTypeId, statusId, contentId, WorkEffort, workEffortId, currentStatusId, params
    }
	
    /**
     * test
     * @throws GeneralException
     * @throws EvalError
     */
	public void testWorkEffortTypeStatusCntParamsEvaluator() throws GeneralException, EvalError {
		addHideContentParam();
		makeTestOnlyWithWorKeffort();
		makeTestWithAllFields();
	}
	
	/**
	 * test with only workEffortId
	 * @throws GeneralException
	 * @throws EvalError
	 */
	private void makeTestOnlyWithWorKeffort() throws GeneralException, EvalError {
		WorkEffortTypeStatusCntParamsEvaluator workEffortTypeStatusCntParamsEvaluator = getWorkEffortTypeStatusCntParamsEvaluator(W60002, "", "");
		workEffortTypeStatusCntParamsEvaluator.run();
		assertHasHideContent(workEffortTypeStatusCntParamsEvaluator);
	}
	
	/**
	 * test with all fields
	 * @throws GeneralException
	 * @throws EvalError
	 */
	private void makeTestWithAllFields() throws GeneralException, EvalError {
		WorkEffortTypeStatusCntParamsEvaluator workEffortTypeStatusCntParamsEvaluator = getWorkEffortTypeStatusCntParamsEvaluator(W60002, PEG12, WEPERFST_PLANINIT);
		workEffortTypeStatusCntParamsEvaluator.run();
		assertHasHideContent(workEffortTypeStatusCntParamsEvaluator);
	}
	
	/**
	 * get WorkEffortTypeStatusCntParamsEvaluator
	 * @param workEffortId
	 * @param workEffortTypeId
	 * @param statusId
	 * @return
	 * @throws GeneralException
	 */
	private WorkEffortTypeStatusCntParamsEvaluator getWorkEffortTypeStatusCntParamsEvaluator(String workEffortId, String workEffortTypeId, String statusId) throws GeneralException {
		WorkEffortTypeStatusCntParamsEvaluator workEffortTypeStatusCntParamsEvaluator = new WorkEffortTypeStatusCntParamsEvaluator(delegator);
		workEffortTypeStatusCntParamsEvaluator.init(workEffortId, workEffortTypeId, statusId, "");
		return workEffortTypeStatusCntParamsEvaluator;
	}
	
	/**
	 * sets hideContentParam
	 * @throws GeneralException
	 */
	private void addHideContentParam() throws GeneralException {
		List<String> contentIdList = new ArrayList<String>();
		contentIdList.add(WEFLD_NOTE);
		contentIdList.add(WEFLD_NOTE2);
		contentIdList.add(WEFLD_NOTE3);
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		conditionList.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), PEG12));
		conditionList.add(EntityCondition.makeCondition(E.statusId.name(), WEPERFST_PLANINIT));
		conditionList.add(EntityCondition.makeCondition(E.contentId.name(), EntityOperator.IN, contentIdList));
		List<GenericValue> workEffortTypeStatusCntList = delegator.findList(E.WorkEffortTypeStatusCnt.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
		if (UtilValidate.isNotEmpty(workEffortTypeStatusCntList)) {
			for (GenericValue workEffortTypeStatusCnt : workEffortTypeStatusCntList) {
				if (UtilValidate.isNotEmpty(workEffortTypeStatusCnt)) {
					workEffortTypeStatusCnt.set(E.params.name(), "hideContent=\"Y\";");
					delegator.store(workEffortTypeStatusCnt);
				}
			}
		}
	}
	
	/**
	 * assert hide content
	 * @param workEffortTypeStatusCntParamsEvaluator
	 */
	private void assertHasHideContent(WorkEffortTypeStatusCntParamsEvaluator workEffortTypeStatusCntParamsEvaluator) {
		Map<String, Map<String, Object>> paramsContentMap = workEffortTypeStatusCntParamsEvaluator.getParamsContentMap();
		assertNotNull(paramsContentMap);
		assertHideContent(paramsContentMap, WEFLD_NOTE);
		assertHideContent(paramsContentMap, WEFLD_NOTE2);
		assertHideContent(paramsContentMap, WEFLD_NOTE3);
	}
	
	/**
	 * assert has hide content
	 * @param paramsContentMap
	 * @param contentId
	 */
	private void assertHideContent(Map<String, Map<String, Object>> paramsContentMap, String contentId) {
		assertTrue(paramsContentMap.containsKey(contentId));
		Map<String, Object> map = paramsContentMap.get(contentId);
		assertNotNull(map);
		assertTrue(map.containsKey(hideContent));
		assertEquals(Y, (String) map.get(hideContent));
	}

}
