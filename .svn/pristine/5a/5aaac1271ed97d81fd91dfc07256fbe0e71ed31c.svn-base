import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;

def checkPermission(permission, currentUserLogin) {
	security = dctx.getSecurity();
    if (security.hasPermission(permission, currentUserLogin))
        return permission;
    return null;
}

if (UtilValidate.isEmpty(context.userLoginId))
	context.userLoginId = userLogin.userLoginId;

currentUserLogin = userLogin;
if (!context.userLoginId.equals(userLogin.userLoginId)) {
	currentUserLogin = delegator.findOne("UserLogin", ["userLoginId", context.userLoginId], true);
}
//Debug.log("**** userLogin=" + userLogin);
//Debug.log("**** userLoginPermission=" + userLoginPermission);
//Debug.log("**** security=" + security);

//Debug.logInfo("-=-=-=- TEST GROOVY SERVICE -=-=-=-", "");
result = ServiceUtil.returnSuccess();
userLoginPermission = "";
if (UtilValidate.isNotEmpty(userLoginId)) {
    userLoginPermission = checkPermission("SECURITY_ADMIN", currentUserLogin);
//        Debug.log("**** userLoginPermission=" + userLoginPermission);
    if (UtilValidate.isEmpty(userLoginPermission)) {
        userLoginPermission = checkPermission("PARTYMGR_ADMIN", currentUserLogin);
//            Debug.log("**** userLoginPermission=" + userLoginPermission);
        if (UtilValidate.isEmpty(userLoginPermission)) {
            userLoginPermission = checkPermission("PARTYMGRROLE_ADMIN", currentUserLogin);
//                Debug.log("**** userLoginPermission=" + userLoginPermission);
            if (UtilValidate.isEmpty(userLoginPermission)) {
                userLoginPermission = checkPermission("PARTYMGRROLE_VIEW", currentUserLogin);
//                    Debug.log("**** userLoginPermission=" + userLoginPermission);
                if (UtilValidate.isEmpty(userLoginPermission)) {
                    userLoginPermission = checkPermission("PARTYMGR_VIEW", currentUserLogin);
//                        Debug.log("**** userLoginPermission=" + userLoginPermission);
                }
            }
        }
    }
}
result.userLoginPermission = userLoginPermission;
return result;