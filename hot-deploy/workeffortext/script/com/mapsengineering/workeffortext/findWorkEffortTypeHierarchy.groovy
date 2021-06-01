import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

def workEffortTypeHierarchyViewList = [];
if (UtilValidate.isNotEmpty(parameters.workEffortId)) {
	workEffortTypeHierarchyViewList = delegator.findList("WorkEffortTypeHierarchyView", EntityCondition.makeCondition("workEffortId", parameters.workEffortId), null, null, null, false);
}

request.setAttribute("workEffortTypeHierarchyViewList", workEffortTypeHierarchyViewList);
return "success";