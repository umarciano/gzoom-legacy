import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

def insertEnabled = false;

def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : context.workEffortId;

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], true);
if (UtilValidate.isNotEmpty(workEffort)) {
	def statusItem = delegator.getRelatedOne("CurrentStatusItem", workEffort);
	def lastStatus = EntityUtil.getFirst(delegator.findList("StatusItem", EntityCondition.makeCondition("statusTypeId", statusItem.statusTypeId), null, ["-sequenceId"], null, true));
	
	if(!workEffort.currentStatusId.equals(lastStatus.statusId)) {
		insertEnabled = true;
	}
}

context.insertEnabled = insertEnabled;