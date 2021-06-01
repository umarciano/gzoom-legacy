import com.mapsengineering.gzoomjbpm.*;
import com.mapsengineering.gzoomjbpm.rmi.*;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;

returnMap = ServiceUtil.returnSuccess();
parameters = parameters.parameters;
processMap = [:];
transition = UtilValidate.isNotEmpty(parameters.transition) ? parameters.transition : null;

stub = getStub();

if(stub != null) {
	workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffortRoot.processId)) {
		processMap = stub.signalExecution(workEffortRoot.processId, transition, null, null, [JbpmStub.STATUS]);
		if(UtilValidate.isNotEmpty(processMap.get(JbpmStub.ERROR))) {
			returnMap = ServiceUtil.returnError(processMap.get(JbpmStub.ERROR));
		}
		else {
			statusId = processMap[JbpmStub.STATUS];
			if(UtilValidate.isNotEmpty(statusId)) {
				condList = [];
				condList.add(EntityCondition.makeCondition("workEffortIdFrom", parameters.workEffortId));
				condList.add(EntityCondition.makeCondition("workEffortAssocTypeId", "ROOT"));
				assocList = delegator.findList("WorkEffortAssoc", EntityCondition.makeCondition(condList), null, null, null, false);
				assocList.each { assoc ->
					workEffort = delegator.findOne("WorkEffort", ["workEffortId": assoc.workEffortIdTo], false);
					workEffort.currentStatusId = statusId;
					delegator.store(workEffort);
				}
			}
		}
	}
}

return returnMap;

def getStub() {
	return JbpmFactory.instance();
}