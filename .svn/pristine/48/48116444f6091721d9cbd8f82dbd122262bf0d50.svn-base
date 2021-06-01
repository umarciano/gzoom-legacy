import com.mapsengineering.gzoomjbpm.*;
import com.mapsengineering.gzoomjbpm.rmi.*;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

returnMap = ServiceUtil.returnSuccess();
processMap = [:];

stub = getStub();
if(stub != null) {
	workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": context.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffortRoot.processId)) {
		processMap = stub.getVariables(workEffortRoot.processId, [JbpmStub.STATUS]);
		if(UtilValidate.isNotEmpty(processMap.get(JbpmStub.ERROR))) {
			returnMap = ServiceUtil.returnError(processMap.get(JbpmStub.ERROR));
		}
	}
}

returnMap.processMap = processMap;

return returnMap;

def getStub() {
	return JbpmFactory.instance();
}