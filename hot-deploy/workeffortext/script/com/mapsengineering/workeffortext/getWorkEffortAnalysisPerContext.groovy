import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

def res = ServiceUtil.returnSuccess();
def workEffortAnalysisList = [];
try {
	if (UtilValidate.isNotEmpty(parameters.contextId)) {
		def workEffortTypeList = delegator.findList("WorkEffortType", EntityCondition.makeCondition("parentTypeId", parameters.contextId), null, null, null, true);
		if (UtilValidate.isNotEmpty(workEffortTypeList)) {
			workEffortAnalysisList = EntityUtil.getRelated("WorkEffortAnalysis", workEffortTypeList);
		}
	} else {
		workEffortAnalysisList = delegator.findList("WorkEffortAnalysis", null, null, null, null, true);
	}
	
	workEffortAnalysisList = EntityUtil.orderBy(workEffortAnalysisList, ["description"]);
	if (UtilValidate.isEmpty(parameters.userLogin)) {
		workEffortAnalysisList = EntityUtil.filterByCondition(workEffortAnalysisList, EntityCondition.makeCondition("availabilityId", "AVAIL_PUBLIC"));
	} else if (!"admin".equals(parameters.userLogin.userLoginId)) {
		workEffortAnalysisList = EntityUtil.filterByCondition(workEffortAnalysisList, EntityCondition.makeCondition("availabilityId", EntityOperator.IN, ["AVAIL_INTERNAL","AVAIL_PUBLIC"]));
	}
} catch (Exception e) {
	Debug.logError(e, "");
	return ServiceUtil.returnError(e.getMessage());
}
res.workEffortAnalysisList = workEffortAnalysisList;
return res;