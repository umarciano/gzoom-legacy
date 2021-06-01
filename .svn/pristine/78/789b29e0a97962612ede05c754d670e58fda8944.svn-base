import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;

def contentId = UtilValidate.isNotEmpty(parameters.contentId) ? parameters.contentId : context.contentId;
def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromFolderIndex(contentId);
context.layoutType = layoutType;

def workEffortTypeId = parameters.workEffortTypeId;

if(UtilValidate.isEmpty(workEffortTypeId)) {
	def workEffort = null;
	def workEffortId = parameters.workEffortId;
	if(UtilValidate.isNotEmpty(workEffortId)) {
		workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		if(UtilValidate.isNotEmpty(workEffort)) {
			workEffortTypeId = workEffort.workEffortTypeId;
		}
	}
}

context.showList = "N";
context.showDate = "Y";
context.showInternalNote = "N";

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(workEffortTypeId, layoutType, false);
}