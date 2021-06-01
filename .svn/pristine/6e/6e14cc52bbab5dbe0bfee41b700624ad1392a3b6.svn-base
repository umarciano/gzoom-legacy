import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

def rootInqyTree = parameters.rootInqyTree;
def workEffortIdRoot = parameters.workEffortIdRoot;
def workEffortId = parameters.workEffortId;

if (! "Y".equals(rootInqyTree)) {
	def childRootModify = "Y";
	def workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId" : workEffortIdRoot], false);
	if (UtilValidate.isNotEmpty(workEffortRoot)) {
		WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(parameters, parameters, delegator);
		def map = paramsEvaluator.getParams(workEffortRoot.workEffortTypeId, "WEFLD_MAIN", false);
		if (UtilValidate.isNotEmpty(map) && UtilValidate.isNotEmpty(map.childRootModify)) {
			childRootModify = map.childRootModify;
		}
	}
	if ("N".equals(childRootModify)) {
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		if (UtilValidate.isNotEmpty(workEffortRoot) && UtilValidate.isNotEmpty(workEffort)) {
			if (! workEffortRoot.workEffortId.equals(workEffort.workEffortParentId)) {
				context.rootInqyTree = "Y";
				parameters.rootInqyTree = "Y";
			}
		}
	}
}
