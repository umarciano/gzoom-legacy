import java.util.Comparator;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import org.ofbiz.base.util.*;
import com.mapsengineering.base.birt.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;

/** Recupero workEffort, workEffortType, workEffortRoot, workEffortTypePeriodId */
def workEffortView = delegator.findOne("WorkEffortAndTypePeriodAndCustomTime", ["workEffortId" : parameters.workEffortId], false);

def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromContext();
context.RelatedAssignmentTitleValue = uiLabelMap["WorkEffortRelatedAssignment_Related"];
/** Recupero title */
if(UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(context[layoutType + "_title"])) {
	context.RelatedAssignmentTitleValue = context[layoutType + "_title"];
}


/** Recupero params */
WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
paramsEvaluator.evaluateParams(workEffortView.workEffortTypeId, layoutType, false);

context.roleTypeId = UtilValidate.isNotEmpty(context.roleTypeId) ? context.roleTypeId : "_NA_";


serviceMap = ["workEffortId": parameters.workEffortId,
              "roleTypeId" : context.roleTypeId, 
              "userLogin": context.userLogin];

def res = dispatcher.runSync("executeChildPerformFindRelatedAssignment", serviceMap);

relatedAssignmentList = [];

if(UtilValidate.isNotEmpty(res.rowList)) {
	relatedAssignmentList.addAll(res.rowList);
	context.relatedAssignmentList = relatedAssignmentList;
}
