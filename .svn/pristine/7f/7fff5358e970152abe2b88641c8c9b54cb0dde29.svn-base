import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;
context.accountTypeEnumId = "RESOURCE";

if("N".equals(context.insertMode)){
	def condition = EntityCondition.makeCondition("weTransWeId", parameters.workEffortId);

	context.condition = UtilValidate.isNotEmpty(context.valueResourceCondition) ? context.valueResourceCondition : condition;
	
	context.entityNameFind = "WorkEffortTransactionSimplifiedView";
	context.glAccountField = "weTransAccountId";
	context.orderByWemList = ["weMeasureMeasureType", "weTransAccountCode", "weTransMeasureId", "weTransDate", "weTransTypeValueDesc"];

}


GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWEMResource.groovy", context);

