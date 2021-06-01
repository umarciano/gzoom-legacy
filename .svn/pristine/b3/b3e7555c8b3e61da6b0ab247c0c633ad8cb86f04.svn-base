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
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, null));
	conditionList.add(EntityCondition.makeCondition("glAccountId", glAccountId));
					
	context.listIt = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(conditionList), null, null, null, true);
}