import com.mapsengineering.gzoomjbpm.*;
import com.mapsengineering.gzoomjbpm.rmi.*;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

import java.io.*;

returnMap = ServiceUtil.returnSuccess();
parameters = parameters.parameters;
processMap = [:];
imageName = "";

stub = getStub();

if(stub != null) {
	workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffortRoot) && UtilValidate.isNotEmpty(workEffortRoot.processId)) {
		selectVars = [JbpmStub.USER];
		processMap = stub.getProcessHistoryImage(workEffortRoot.processId, selectVars);
		if(UtilValidate.isNotEmpty(processMap.get(JbpmStub.ERROR))) {
			returnMap = ServiceUtil.returnError(processMap.get(JbpmStub.ERROR));
		}
		else {
			imageBytes = processMap.get(JbpmStub.STREAM);
			if(imageBytes != null) {
				try {
					imageName = workEffortRoot.workEffortId + "_" + UtilDateTime.nowTimestamp().getTime() + "." + JbpmStub.IMAGE_TYPE;
					OutputStream os = new FileOutputStream("hot-deploy/workeffortext/webapp/workeffortext/images/tmp/" + imageName);
					os.write(imageBytes);
					os.close();
					//UtilHttp.streamContentToBrowser(response, imageBytes, "image/" + JbpmStub.IMAGE_TYPE,  workEffortRoot.workEffortTypeId + "_" + workEffortRoot.workEffortId + "." + JbpmStub.IMAGE_TYPE);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

returnMap.id= [:];
returnMap.id.imageName = imageName;
return returnMap;

def getStub() {
	return JbpmFactory.instance();
}