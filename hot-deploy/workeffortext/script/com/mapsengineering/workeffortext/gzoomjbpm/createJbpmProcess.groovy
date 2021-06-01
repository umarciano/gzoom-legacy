import java.util.Map;

import com.mapsengineering.gzoomjbpm.*;
import com.mapsengineering.gzoomjbpm.rmi.*;

import org.jbpm.api.JbpmException;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

returnMap = ServiceUtil.returnSuccess();
processMap = [:];

stub = getStub();

if(stub != null) {
	workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": context.id.workEffortId], false);
	workEffortRootType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffortRoot.workEffortTypeId], false);
	
	if("Y".equals(workEffortRootType.isRoot)) {
		try {
			processMap = stub.startProcess(workEffortRootType.workEffortTypeId, workEffortRootType.description, null, null, null);
		}
		catch(JbpmException je) {
			returnMap = ServiceUtil.returnFailure(je.getMessage());
			return returnMap;
		}
		
		if(UtilValidate.isNotEmpty(processMap.get(JbpmStub.ERROR))) {
			returnMap = ServiceUtil.returnError(processMap.get(JbpmStub.ERROR));
		}
		else {
			if(UtilValidate.isNotEmpty(context.id) && UtilValidate.isNotEmpty(context.id.workEffortId)) {
				workEffortRoot.processId = processMap.get(JbpmStub.PID);
				delegator.store(workEffortRoot);
			}
		}
	}
}

returnMap.processMap = processMap;

return returnMap;

def getStub() {
	return JbpmFactory.instance();
}
