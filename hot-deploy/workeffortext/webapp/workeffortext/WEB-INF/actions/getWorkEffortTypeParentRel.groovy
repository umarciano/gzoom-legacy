import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;


def parentRelAssocTypeId = "";
def parentRelTypeIdRef = "";
def parentRelTypeDesc = "";

if(UtilValidate.isNotEmpty(parameters.workEffortTypeId)) {
	def parentRelConditionList = [];
	parentRelConditionList.add(EntityCondition.makeCondition("workEffortTypeId", parameters.workEffortTypeId));
	parentRelConditionList.add(EntityCondition.makeCondition("wefromWetoEnumId", "WETATO"));
	parentRelConditionList.add(EntityCondition.makeCondition("isParentRel", "Y"));
	
	def parentRelList = delegator.findList("WorkEffortTypeAssoc", EntityCondition.makeCondition(parentRelConditionList), null, null, null, false);
	def parentRelItem = EntityUtil.getFirst(parentRelList);
	if(UtilValidate.isNotEmpty(parentRelItem)) {
		parentRelAssocTypeId = parentRelItem.workEffortAssocTypeId;
		parentRelTypeIdRef = parentRelItem.workEffortTypeIdRef;
		parentRelTypeDesc = parentRelItem.comments;
	}
}


context.parentRelAssocTypeId = parentRelAssocTypeId;
context.parentRelTypeIdRef = parentRelTypeIdRef;
context.parentRelTypeDesc = parentRelTypeDesc;
