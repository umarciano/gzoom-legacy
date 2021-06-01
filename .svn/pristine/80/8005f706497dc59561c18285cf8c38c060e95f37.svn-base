import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

if(UtilValidate.isEmpty(parameters.workEffortId)) {
	def wem = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": parameters.workEffortMeasureId], false);
	def currentWe = delegator.findOne("WorkEffort", ["workEffortId": wem.workEffortId], false);
	
	if (UtilValidate.isNotEmpty(currentWe.workEffortParentId)) {
		def rootWrk = delegator.findOne("WorkEffort", ["workEffortId": currentWe.workEffortParentId], false);
		def rootWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": rootWrk.workEffortTypeId], false);
		
		def assocList = delegator.findList("WorkEffortAssocExtView", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortAssocTypeId", rootWorkEffortType.hierarchyAssocTypeId), EntityCondition.makeCondition("workEffortIdTo", currentWe.workEffortId)),
			null, null, null, false);
		def parentWorkEffort = delegator.findOne("WorkEffort", ["workEffortId": assocList[0].workEffortIdFrom], false);
		
		context.workEffortTypeId = delegator.findOne("WorkEffortType", ["workEffortTypeId": parentWorkEffort.workEffortTypeId], false).workEffortTypeId;
		context.workEffortId = parentWorkEffort.workEffortId;
	}
}