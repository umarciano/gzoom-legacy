import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***BS parameters.menuItem" + parameters.menuItem);
context.permission = "@base-permission@"; 
parameters.weContextId = "@context-id@";

if (UtilValidate.isEmpty(parameters.currentStatusId)) {
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = parameters.currentStatusContains;
	parameters.remove("currentStatusId");
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;


