import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.insertMode = context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;

def glAccountId = UtilValidate.isNotEmpty(context.glAccountId) ? context.glAccountId : parameters.glAccountId;

if(!"N".equals(context.insertMode)) {
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
}
else {
	def conditionList = [];

	conditionList.add(EntityCondition.makeCondition("weMeasureWeId", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("weMeasureAccountId", glAccountId));
	conditionList.add(EntityCondition.makeCondition("weMeasureWeSnapId", EntityOperator.EQUALS, null));
	
	def prodId = parameters.productId;
	if(UtilValidate.isNotEmpty(prodId)){
		conditionList.add(EntityCondition.makeCondition("weMeasureProductId", prodId));
	}
	
	context.listIt = delegator.findList("WorkEffortMeasureView", EntityCondition.makeCondition(conditionList), null, null, null, false);
	
}