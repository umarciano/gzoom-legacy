import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;

def result = ServiceUtil.returnSuccess();

def crudEnumId = "";

def workEffortId = parameters.workEffortId;
def folder = parameters.folder;

def workEffortTypeStatusCntAndWorkEffortConditions = [];
workEffortTypeStatusCntAndWorkEffortConditions.add(EntityCondition.makeCondition("workEffortId", workEffortId));
workEffortTypeStatusCntAndWorkEffortConditions.add(EntityCondition.makeCondition("contentId", folder));
def workEffortTypeStatusCntAndWorkEffortList = delegator.findList("WorkEffortTypeStatusCntAndWorkEffort", EntityCondition.makeCondition(workEffortTypeStatusCntAndWorkEffortConditions), null, null, null, false);
def workEffortTypeStatusCntAndWorkEffortItem = EntityUtil.getFirst(workEffortTypeStatusCntAndWorkEffortList);

if (UtilValidate.isNotEmpty(workEffortTypeStatusCntAndWorkEffortItem)) {
	crudEnumId = workEffortTypeStatusCntAndWorkEffortItem.crudEnumId;
}


result.put("crudEnumId", crudEnumId);
return result;