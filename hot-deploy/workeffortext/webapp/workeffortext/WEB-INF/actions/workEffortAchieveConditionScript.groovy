import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

def screenNameListIndex = "";

def workEffortTypeId = parameters.workEffortTypeId;
if (UtilValidate.isEmpty(workEffortTypeId)) {
	if (UtilValidate.isNotEmpty(parameters.weTypeId)) {
		workEffortTypeId = parameters.weTypeId;
	} else if (UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
		def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], true);
		if (UtilValidate.isNotEmpty(workEffortAnalysis)) {
			workEffortTypeId = workEffortAnalysis.workEffortTypeId;
		}
	}
}
//Debug.log("context.listIt = " + context.listIt);

def workEffortType = null;
//Bug passaggio da un folder all'altro
if(UtilValidate.isEmpty(workEffortTypeId)) {
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], true);
	workEffortTypeId = workEffort.workEffortTypeId;
}

if (UtilValidate.isNotEmpty(workEffortTypeId)) {
	workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffortTypeId], true);
	
	/**
	 * GN-354 - controllo se esistono indicatori per al visualizzazione del Tab
	 */
	workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition(
																				EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId), 
																				EntityCondition.makeCondition("contentId", EntityOperator.LIKE, "WEFLD_IND%")
																			), null, null, null, false);
}
//Bug passando da un folder all'altro
def weId = UtilValidate.isNotEmpty(parameters.workEffortIdFrom) ? parameters.workEffortIdFrom : parameters.workEffortId;  

def workEfforChildren = delegator.findList("WorkEffortAchieveChildView", EntityCondition.makeCondition("workEffortIdFrom", weId), null, null, null, true);

context.weChildren = workEfforChildren;

if (UtilValidate.isNotEmpty(workEfforChildren)) {
	context.firstChild = workEfforChildren[0];
}

if (UtilValidate.isNotEmpty(workEffortType) && UtilValidate.isNotEmpty(workEffortTypeContentList)) {
	screenNameListIndex = "1";
	
	if (UtilValidate.isEmpty(workEfforChildren) && UtilValidate.isNotEmpty(parameters.tableActiveTab)) {
		parameters.tableActiveTab.WorkEffortAchieveExtTabMenu = "1";
		screenNameListIndex = "3";
	}
	
} else if ("WorkEffortTransactionView".equalsIgnoreCase(parameters.entityName)) {
	screenNameListIndex = "2";
}
context.weTypeFormMenu = workEffortType;

Debug.log("-------------------- screenNameListIndex=" + screenNameListIndex);
return screenNameListIndex;