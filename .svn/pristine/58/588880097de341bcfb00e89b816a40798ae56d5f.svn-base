import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def list = [];
def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId: context.workEffortId;
def workEffortAnalysisId = UtilValidate.isNotEmpty(parameters.workEffortAnalysisId) ? parameters.workEffortAnalysisId: context.workEffortAnalysisId;

def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId": workEffortAnalysisId], false);


if(UtilValidate.isNotEmpty(workEffortAnalysis)){
	def conditionList = [];
	conditionList.add(EntityCondition.makeCondition("workEffortId", workEffortId));
	conditionList.add(EntityCondition.makeCondition("workEffortAnalysisId", workEffortAnalysisId));
	conditionList.add(EntityCondition.makeCondition("workEffortSnapshotId", null));
	
	
	if (workEffortAnalysis.excludeValidity == "N") {
		
		def startDate = org.ofbiz.base.util.UtilDateTime.getYearStart(workEffortAnalysis.referenceDate, timeZone, locale);
		def endDate = org.ofbiz.base.util.UtilDateTime.getYearEnd(workEffortAnalysis.referenceDate, timeZone, locale);
		
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
		
	}
	
	list = delegator.findList("WorkEffortAchieveInqMeasView", EntityCondition.makeCondition(conditionList), null, ["glResourceTypeId","accountCode"], null, false);
}

context.listIt = list;


