import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

Debug.log("******************************* getBirtWorkEffortTypeList.groovy -> parameters.contentId = " + parameters.contentId);
Debug.log("******************************* getBirtWorkEffortTypeList.groovy -> parameters.sequenceNum = " + parameters.sequenceNum);
Debug.log("******************************* getBirtWorkEffortTypeList.groovy -> parameters.etch = " + parameters.etch);
Debug.log("******************************* getBirtWorkEffortTypeList.groovy -> parameters.isAnalysis = " + parameters.workEffortAnalysisId);
Debug.log("******************************* getBirtWorkEffortTypeList.groovy -> parameters.parentTypeId = " + parameters.parentTypeId);


def workEffortTypeList = null;


if (UtilValidate.isNotEmpty(parameters.contentId) && UtilValidate.isNotEmpty(parameters.parentTypeId)) {
	def conditionList = [EntityCondition.makeCondition("contentId", parameters.contentId), EntityCondition.makeCondition("parentTypeId", parameters.parentTypeId)];
	if (UtilValidate.isNotEmpty(parameters.etch)) {
		def conditionOrList = [
				EntityCondition.makeCondition("etch", parameters.etch), 
				EntityCondition.makeCondition("etchLang", parameters.etch), 
				EntityCondition.makeCondition(EntityCondition.makeCondition("description", parameters.etch),  EntityCondition.makeCondition("etch", null)),
				EntityCondition.makeCondition(EntityCondition.makeCondition("descriptionLang", parameters.etch),  EntityCondition.makeCondition("etchLang", null))
				
			];
		conditionList.add(EntityCondition.makeCondition(conditionOrList, EntityOperator.OR));
	}    
	if (UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
		workEffortTypeList = delegator.findList("WorkEffortAnalysisAndTypeReportGroup", EntityCondition.makeCondition(conditionList), null, null, null, false);
	    
	} else {
		if (UtilValidate.isNotEmpty(parameters.sequenceNum)) {
			conditionList.add(EntityCondition.makeCondition("sequenceNum", parameters.sequenceNum));
		} else {
			conditionList.add(EntityCondition.makeCondition("sequenceNum", null));
		}
		conditionList.add(EntityCondition.makeCondition("isVisible", "Y"));
	    workEffortTypeList = delegator.findList("WorkEffortTypeContentReportView", EntityCondition.makeCondition(conditionList), UtilMisc.toSet("workEffortTypeId", "contentId", "sequenceNum", "descriptionType", "descriptionTypeLang"), ["sequenceNum"], null, false);
	}
    Debug.log("******************************* getFirstWorkEffortType.groovy -> conditionList = " + conditionList);  
}
	
Debug.log("******************************* getBirtWorkEffortTypeList.groovy -> workEffortTypeList = " + workEffortTypeList);

context.workEffortTypeList = workEffortTypeList;


