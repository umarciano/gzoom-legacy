import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

//Considero la presenza di una root
if (UtilValidate.isEmpty(parameters.workEffortIdRoot)) {
	oldWorkEffortId = parameters.workEffortId;
	parameters.workEffortId = parameters.workEffortIdRoot;
	res = GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkEffortPartyPerformanceSummary.groovy", context);
	if (UtilValidate.isNotEmpty(context.listIt)) {
		context.partyId = context.listIt[0].partyId;
	}
	
	if (UtilValidate.isNotEmpty(oldWorkEffortId)) {
		parameters.workEffortId = oldWorkEffortId;
	} else {
		parameters.remove("workEffortId");
	}
}

context.commEntityName = "WorkEffortCommunicationEventAndRoleView";
context.listCondition = [EntityCondition.makeCondition("workEffortId", parameters.workEffortIdRoot)];