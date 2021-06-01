import java.util.*;
import org.ofbiz.service.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

// loadUserLoginValidRoleList
// Carica la lista dei ruoli ammessi (UserLoginValidRoleView).

dispatcher = dctx.getDispatcher();
delegator = dctx.getDelegator();

if (UtilValidate.isEmpty(context.userLoginId))
	context.userLoginId = userLogin.userLoginId;

results = dispatcher.runSync("getUserLoginPermission", ["userLoginId": context.userLoginId, "userLogin" : userLogin]);

if (ServiceUtil.isError(results)) {
	return results;
}
	
userLoginPermission = results.userLoginPermission;

result = ServiceUtil.returnSuccess();
//Debug.log("**** userLoginPermission=" + userLoginPermission);
if ("PARTYMGRROLE_VIEW".equals(userLoginPermission) || "PARTYMGRROLE_ADMIN".equals(userLoginPermission)) {
    //Debug.log("**** userLoginPermission=" + userLoginPermission);
    userLoginValidRoleList = delegator.findList("UserLoginValidRoleView", EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, context.userLoginId), null, null, null, true);
    //Debug.log("**** userLoginValidRoleList=" + userLoginValidRoleList);
    result.userLoginValidRoleList = userLoginValidRoleList;
    return result;
}
//Debug.log("**** userLoginValidRoleList = vuota");
result.userLoginValidRoleList = [];
return result;

