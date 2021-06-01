import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

if ("DELETE".equals(parameters.operation)) {
	rootAssoc = "ROOT";
	workEffortIdFrom = parameters.workEffortIdFrom;
	workEffortAssocTypeId = parameters.weHierarchyTypeId;
	
	//Cerco il fratello
//Debug.log("------------ FRATELLO= workEffortIdFrom=" + workEffortIdFrom + " workEffortAssocTypeId=" + workEffortAssocTypeId);	
	foundList = delegator.findList("WorkEffortAssocExtView", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortIdFrom", workEffortIdFrom), EntityCondition.makeCondition("workEffortAssocTypeId", workEffortAssocTypeId)]), null, null, null, true)

	if (UtilValidate.isNotEmpty(foundList)) {
		selectChild = foundList[foundList.size()-1];
		parameters.workEffortIdTo = selectChild.workEffortIdTo;
		parameters.fromDate = selectChild.fromDate.toString();
//Debug.log("---------------------- selectChild=" + selectChild);		
	} else {
		//Cerco il padre
//Debug.log("----------------- PADRE= workEffortIdTo=" + workEffortIdFrom + " workEffortAssocTypeId=" + workEffortAssocTypeId);	
		parentRelation = EntityUtil.getFirst(delegator.findList("WorkEffortAssocExtView", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortIdTo", workEffortIdFrom), EntityCondition.makeCondition("workEffortAssocTypeId", workEffortAssocTypeId)]), null, null, null, true));
	
		if (UtilValidate.isNotEmpty(parentRelation)) {
			parameters.workEffortIdFrom = parentRelation.workEffortIdFrom;
			parameters.workEffortIdTo = parentRelation.workEffortIdTo;
			parameters.fromDate = parentRelation.fromDate.toString();
			
			parameters.id=StringUtil.mapToStr(parentRelation.getPrimaryKey(), false);
		}
		else {
			//Cerco la ROOT
			parentRelation = EntityUtil.getFirst(delegator.findList("WorkEffortAssocExtView", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortIdTo", workEffortIdFrom), EntityCondition.makeCondition("workEffortAssocTypeId", rootAssoc)]), null, null, null, true));
			parentRelation = parentRelation.getAllFields(); 
			parentRelation.weHierarchyTypeId = rootAssoc;
//Debug.log("---------------- ROOT");			
		}
		
		context.listIt = [parentRelation];
//Debug.log("-------------- parentRelation=" + parentRelation);		
	}
}