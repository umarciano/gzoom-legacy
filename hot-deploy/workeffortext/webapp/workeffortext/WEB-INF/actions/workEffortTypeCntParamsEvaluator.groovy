import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;
import org.ofbiz.base.util.*;

def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromFolderIndex(parameters.contentIdSecondary);
def workEffortTypeId = "";
if (UtilValidate.isNotEmpty(parameters.workEffortId)) {
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
	if (UtilValidate.isNotEmpty(workEffort)) {
		workEffortTypeId = workEffort.workEffortTypeId;
	}
}

WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
paramsEvaluator.evaluateParams(workEffortTypeId, layoutType, false);