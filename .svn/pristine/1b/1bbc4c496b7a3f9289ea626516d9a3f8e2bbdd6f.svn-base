import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.showOrgUnit = "N";

def workEffortTypeId = parameters.workEffortTypeId;
if (UtilValidate.isEmpty(workEffortTypeId)) {
	workEffortTypeId = parameters.weTypeId;
}

if (UtilValidate.isEmpty(workEffortTypeId) && UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
	def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false);
	if (UtilValidate.isNotEmpty(workEffortAnalysis)) {
		workEffortTypeId = workEffortAnalysis.workEffortTypeId;
	}
}	

if (UtilValidate.isNotEmpty(context.analysisType) && UtilValidate.isNotEmpty(workEffortTypeId)) {
	def analysisContent = EntityUtil.getFirst(delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", context.analysisType)]), null, ["sequenceNum"], null, false));
	
	if (UtilValidate.isNotEmpty(analysisContent)) {
		context.analysisContentId = analysisContent.contentId;
		context.workEffortTypeId = workEffortTypeId;
		if (UtilValidate.isNotEmpty(analysisContent.params)) {
			BshUtil.eval(analysisContent.params, context);
		}
	}
}