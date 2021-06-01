import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import  java.io.*;  

result = "success";

serviceInMap = dispatcher.getDispatchContext().makeValidContext("queryExecutorService", ModelService.IN_PARAM, parameters);
Debug.log(" Run sync service queryExecutorService with "+ serviceInMap + ", userLoginId =" + context.userLogin.userLoginId);
serviceInMap.put("userLogin", context.userLogin);
serviceInMap.put("locale", context.locale);
serviceInMap.put("response",response);
serviceInMap.put("request",request);
resService = dispatcher.runSync("queryExecutorService", serviceInMap);
Debug.log("resService: "+resService);

if(ServiceUtil.isError(resService)) {
	def errore = ServiceUtil.getErrorMessage(resService);
	request.setAttribute("_ERROR_MESSAGE_", errore);
	return "error";	
}


return result;
