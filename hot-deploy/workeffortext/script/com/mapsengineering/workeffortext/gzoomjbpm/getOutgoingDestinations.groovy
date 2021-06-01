import com.mapsengineering.gzoomjbpm.*;
import com.mapsengineering.gzoomjbpm.rmi.*;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

returnMap = ServiceUtil.returnSuccess();
destinationMap = [:];

stub = getStub();
if(stub != null) {
	workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": context.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffortRoot.processId)) {
		destinationMap = stub.getOutgoingDestinations(workEffortRoot.processId);
		if(UtilValidate.isNotEmpty(destinationMap.get(JbpmStub.ERROR))) {
			returnMap = ServiceUtil.returnError(destinationMap.get(JbpmStub.ERROR));
		}
	}
}

returnMap.destinationMap = destinationMap;

return returnMap;

def getStub() {
	return JbpmFactory.instance();
}