import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;


res = "success";

/**
 * I filtri applicati i nquesta ricerca vanno applicati anche nel groovy executePerformFindEPWorkEffortRootInqy.groovy
 * perche altrimenti il cambio stato fa una query diversa...
 */
//Debug.log("***EP parameters.menuItem" + parameters.menuItem);
context.permission = "EMPLPERF"; 
parameters.weContextId = "CTX_EP";

if (UtilValidate.isEmpty(parameters.currentStatusId)) {
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = parameters.currentStatusContains;
	parameters.remove("currentStatusId");
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);

//Bug 35
if (UtilValidate.isNotEmpty(context.listIt) && (UtilValidate.isNotEmpty(parameters.evalManagerPartyId) || UtilValidate.isNotEmpty(parameters.evalPartyId))) {
	conditionList = null;
	if (UtilValidate.isNotEmpty(parameters.evalManagerPartyId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("partyId", parameters.evalManagerPartyId));
		condList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_MANAGER"));	//valutatore	
		def evalManagerList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
		def evalManagerWorkEffortId = EntityUtil.getFieldListFromEntityList(evalManagerList, "workEffortId", false);		
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, evalManagerWorkEffortId));
		request.setAttribute("listIt", context.listIt);
	}
	
	if (UtilValidate.isNotEmpty(parameters.evalPartyId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("partyId", parameters.evalPartyId));
		condList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_IN_CHARGE"));	//valutato
		def evalList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
		def evalWorkEffortId = EntityUtil.getFieldListFromEntityList(evalList, "workEffortId", false);
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, evalWorkEffortId));
		request.setAttribute("listIt", context.listIt);
	}
	
		
}
return res;
