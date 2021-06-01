import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def crudEnumId = "";

def workEffortId = parameters.workEffortId;
def workEffortTypeStatusCntAndWorkEffortConditions = [];
workEffortTypeStatusCntAndWorkEffortConditions.add(EntityCondition.makeCondition("workEffortId", workEffortId));
workEffortTypeStatusCntAndWorkEffortConditions.add(EntityCondition.makeCondition("contentId", "WEFLD_NOTE4"));
def workEffortTypeStatusCntAndWorkEffortList = delegator.findList("WorkEffortTypeStatusCntAndWorkEffort", EntityCondition.makeCondition(workEffortTypeStatusCntAndWorkEffortConditions), null, null, null, false);
def workEffortTypeStatusCntAndWorkEffortItem = EntityUtil.getFirst(workEffortTypeStatusCntAndWorkEffortList);
if (UtilValidate.isNotEmpty(workEffortTypeStatusCntAndWorkEffortItem)) {
	crudEnumId = workEffortTypeStatusCntAndWorkEffortItem.crudEnumId;
}
context.crudEnumId = crudEnumId;
