import java.util.List;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

if ("Y".equals(parameters.insertMode)) {
	context.isAutomatic = "N";
}

context.isStoricized = "N";
if (UtilValidate.isNotEmpty(parameters.workEffortRevisionId)) {
	def workEffortViewList = delegator.findList("WorkEffortView", EntityCondition.makeCondition("workEffortRevisionId", parameters.workEffortRevisionId), null, null, null, true);
	if (UtilValidate.isNotEmpty(workEffortViewList)) {
		context.isStoricized = "Y";
	}		
}

context.workEffortTypeCtxList = getWorkEffortTypeCtxList();








def getWorkEffortTypeCtxList() {
	List<EntityCondition> conditions = FastList.newInstance();
	conditions.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.LIKE, "CTX%"));
	conditions.add(EntityCondition.makeCondition("workEffortTypeId", "EVENT"));	
	
	def workEffortTypeCtxList = delegator.findList("WorkEffortType", EntityCondition.makeCondition(conditions, EntityOperator.OR), null, UtilMisc.toList("workEffortTypeId"), 
			null, false);
	
	return workEffortTypeCtxList;
}
