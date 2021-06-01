package com.mapsengineering.workeffortext.util;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.util.FolderLayuotTypeExtractor;

import bsh.EvalError;

/**
 * Cerca i params specifici di un WorkEffortTypeContent
 */
public class WorkEffortTypeCntParamsEvaluator {
	private Map<String, Object> context;
	private Map<String, Object> parameters;
    private Delegator delegator;

    /**
     * la classe si occupa di estrarre i params della WorkEffortTypeContent e di popolare il context
     * @param context
     * @param parameters
     * @param delegator
     */
	public WorkEffortTypeCntParamsEvaluator(Map<String, Object> context, Map<String, Object> parameters, Delegator delegator) {
		this.context = context;
		this.parameters = parameters;
        this.delegator = delegator;
    }

    /**
     * estrae i params senza contentid in input 
     * @param workEffortTypeId
     * @param onlyWeTypeContentTypeId
     * @return
     * @throws Exception
     */
    public Map<String, Object> evaluateParams(String workEffortTypeId, boolean onlyWeTypeContentTypeId) throws GenericEntityException, EvalError {
        return evaluateParams(workEffortTypeId, null, onlyWeTypeContentTypeId);
    }

    /**
     * estrae i params, eventualmente utilizzando contentid in input, e li mette nel context 
     * @param workEffortTypeId
     * @param onlyWeTypeContentTypeId
     * @return
     * @throws Exception
     */
    public Map<String, Object> evaluateParams(String workEffortTypeId, String contentId, boolean onlyWeTypeContentTypeId) throws GenericEntityException, EvalError {
        String contentIdValue = UtilValidate.isNotEmpty(contentId) ? contentId : getFolderIndexLayoutType();
        
        Map<String, Object> mapParams = getParams(workEffortTypeId, contentIdValue, onlyWeTypeContentTypeId);
        context.putAll(mapParams);
        
        return mapParams;
    }

    /**
     * estrae i params con contentId in input
     * @param workEffortTypeId
     * @param contentId
     * @param onlyWeTypeContentTypeId
     * @return
     * @throws GenericEntityException 
     * @throws EvalError 
     * @throws Exception
     */
    public Map<String, Object> getParams(String workEffortTypeId, String contentId, boolean onlyWeTypeContentTypeId) throws GenericEntityException, EvalError {
        Map<String, Object> mapParams = FastMap.newInstance();
        List<EntityCondition> conditionWorkEffortTypeContent = FastList.newInstance();
        conditionWorkEffortTypeContent.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), EntityOperator.EQUALS, workEffortTypeId));
        conditionWorkEffortTypeContent.add(getContentCondition(contentId, onlyWeTypeContentTypeId));
        conditionWorkEffortTypeContent.add(EntityCondition.makeCondition(E.params.name(), EntityOperator.NOT_EQUAL, null));

        List<GenericValue> workEffortTypeContentList = delegator.findList(E.WorkEffortTypeContent.name(), EntityCondition.makeCondition(conditionWorkEffortTypeContent), null, null, null, false);
        GenericValue workEffortTypeContent = EntityUtil.getFirst(workEffortTypeContentList);
        if (UtilValidate.isNotEmpty(workEffortTypeContent) && UtilValidate.isNotEmpty(workEffortTypeContent.getString(E.params.name()))) {
            BshUtil.eval(workEffortTypeContent.getString(E.params.name()), mapParams);
            mapParams.remove("context");
            mapParams.remove("bsh");
        }
        return mapParams ;
    }

    /**
     * prende il contentId dal folderIndex
     * @return
     */
    private String getFolderIndexLayoutType() {
        return new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromFolderIndex();
    }

    /**
     * 
     * @param contentId
     * @param onlyWeTypeContentTypeId
     * @return
     */
    private EntityCondition getContentCondition(String contentId, boolean onlyWeTypeContentTypeId) {
        if (onlyWeTypeContentTypeId) {
            return EntityCondition.makeCondition(E.weTypeContentTypeId.name(), EntityOperator.EQUALS, contentId);
        }
        return EntityCondition.makeCondition(EntityCondition.makeCondition(E.contentId.name(), EntityOperator.EQUALS, contentId), EntityOperator.OR, EntityCondition.makeCondition(E.weTypeContentTypeId.name(), EntityOperator.EQUALS, contentId));
    }

}
