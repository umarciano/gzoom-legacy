import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

def toReturnList = [];

def conditionList = [];
conditionList.add(EntityCondition.makeCondition(
			EntityCondition.makeCondition("workEffortId", ""),
			EntityOperator.OR,
			EntityCondition.makeCondition("workEffortId", null)));
if(UtilValidate.isNotEmpty(parameters.glAccountId)) {
	conditionList.add(EntityCondition.makeCondition("glAccountId", parameters.glAccountId));
}
if(UtilValidate.isNotEmpty(parameters.productId)) {
	conditionList.add(EntityCondition.makeCondition("productId", parameters.productId));
}

toReturnList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(conditionList), null, null, null, true);

context.listIt = toReturnList;
