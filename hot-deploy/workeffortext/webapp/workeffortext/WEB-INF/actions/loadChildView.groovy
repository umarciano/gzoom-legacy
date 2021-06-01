import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.listIt = [];
if (UtilValidate.isNotEmpty(context.childViewList)) {
	conditionList = null;
	
	context.childViewList.each { childView ->
		if (UtilValidate.isEmpty(conditionList)) {
			conditionList = [];
		}
		/** Caso snapshot **/
		def conditionList = [];
		conditionList.add(EntityCondition.makeCondition("workEffortIdFrom", childView));
			
		if (UtilValidate.isNotEmpty(parameters.snapshot) && parameters.snapshot == 'Y') {
			conditionList.add(EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, null));			
		} else {
			conditionList.add(EntityCondition.makeCondition(
				EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.EQUALS, null),
				EntityOperator.OR,
				EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.EQUALS, "")
				));
		}
		
		
		def workEffortAnalysisId = UtilValidate.isNotEmpty(parameters.workEffortAnalysisId) ? parameters.workEffortAnalysisId: context.workEffortAnalysisId;
		def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId": workEffortAnalysisId], true);
		
		if(UtilValidate.isNotEmpty(workEffortAnalysis)){
			
			if (workEffortAnalysis.excludeValidity == "N") {
				
				def startDate = org.ofbiz.base.util.UtilDateTime.getYearStart(workEffortAnalysis.referenceDate, timeZone, locale);
				def endDate = org.ofbiz.base.util.UtilDateTime.getYearEnd(workEffortAnalysis.referenceDate, timeZone, locale);
				
				conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
				conditionList.add(EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
				
			}
			
		}
		
		def list = delegator.findList("WorkEffortAchieveChildView", EntityCondition.makeCondition(conditionList), null, ["sequenceNum", "sourceReferenceId", "workEffortId"], null, true);
		context.listIt.addAll(list);
	}
}
