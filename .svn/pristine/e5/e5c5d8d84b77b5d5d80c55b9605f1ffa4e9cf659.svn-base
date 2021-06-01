import com.mapsengineering.gzoomjbpm.*;
import com.mapsengineering.gzoomjbpm.rmi.*;

import org.ofbiz.service.*;
import org.ofbiz.base.util.*;

returnMap = ServiceUtil.returnSuccess();
isEnded = true;

stub = getStub();

if(stub != null) {
	workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffortRoot.processId)) {
		isEnded = stub.isEnded(workEffortRoot.processId);
	}
}

returnMap.isEnded = isEnded;

return returnMap;

def getStub() {
	return JbpmFactory.instance();
}