import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

layoutContentId = "";

if(UtilValidate.isEmpty(context.workEffortId) && UtilValidate.isNotEmpty(parameters.workEffortId)) {
	context.workEffortId = parameters.workEffortId;
}

def workEffort = delegator.findOne("WorkEffortView", ["workEffortId" : context.workEffortId], false);
layoutType = context.folderContentIds[context.i];

if (UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(workEffort)) {
    
	def layoutContent = EntityUtil.getFirst(delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffort.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", layoutType)]), null, ["sequenceNum"], null, false));
	if (UtilValidate.isNotEmpty(layoutContent)) {
		layoutContentId = layoutContent.contentId;
	}
}

return layoutContentId;