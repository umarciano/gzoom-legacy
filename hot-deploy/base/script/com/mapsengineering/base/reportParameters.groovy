import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";
birtParameters = [:];

Debug.log("************************ parameters.entityName = " + parameters.entityName);
//Gestione parametri per la perform find
def entityNameSpecificSession = session.getAttribute(parameters.entityName + "_searchParamsMap_N");
if (UtilValidate.isNotEmpty(entityNameSpecificSession)) {
	qsMap = entityNameSpecificSession.queryStringMap;
	birtParameters.put("queryStringMap", qsMap);
}

request.setAttribute("birtParameters", birtParameters);    

return res;