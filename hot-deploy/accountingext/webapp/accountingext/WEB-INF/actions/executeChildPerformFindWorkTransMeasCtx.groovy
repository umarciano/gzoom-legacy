import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

if(!"Y".equals(parameters.detail)) {
	def workEffortMeasureId = UtilValidate.isNotEmpty(context.workEffortMeasureId) ? context.workEffortMeasureId : parameters.workEffortMeasureId;
	def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : workEffortMeasureId], false);
	context.defaultUomDescr = workEffortMeasure.uomDescr;
	parameters.productId = parameters.productId == null ? workEffortMeasure.productId : parameters.productId;
	//bug 4279 periodTypeId se vuoto da glAccount
	context.periodTypeId = workEffortMeasure.periodTypeId;
	//context.condition = EntityCondition.makeCondition("voucherRef", parameters.workEffortMeasureId);
	context.condition = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", parameters.productId), 
			EntityCondition.makeCondition("glAccountId", parameters.glAccountId));
	
	GroovyUtil.runScriptAtLocation("component:/accountingext/webapp/accountingext/WEB-INF/actions/executeChildPerformFindAcctgTransAndEntries.groovy", context);
}
