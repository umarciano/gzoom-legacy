// loadUserLoginParentRoleTypeList
// Carica la lista dei ruoli padri ammessi (RoleType).

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;

dispatcher = dctx.getDispatcher();
delegator = dctx.getDelegator();

if (UtilValidate.isEmpty(context.userLoginId))
	context.userLoginId = userLogin.userLoginId;

results = dispatcher.runSync("getUserLoginPermission", ["userLoginId": context.userLoginId, "userLogin" : userLogin]);

//Debug.log("************************************** LoadUserLoginParentRoleTypeList.groovy --> results.userLoginPermission = " + results.userLoginPermission);

if (ServiceUtil.isError(results)) {
	return results;
}

userLoginPermission = results.userLoginPermission;

userLoginParentRoleTypeList = [];
result = ServiceUtil.returnSuccess();
if ("PARTYMGRROLE_VIEW".equals(userLoginPermission) || "PARTYMGRROLE_ADMIN".equals(userLoginPermission)) {
    if (UtilValidate.isNotEmpty(userLoginValidRoleList)) {
        userLoginParentRoleTypeList = [];
        userLoginValidRoleList.each { userLoginValidRole ->
            roleType = userLoginValidRole.getRelatedOne("RoleType");
            if (UtilValidate.isNotEmpty(roleType.parentTypeId) && !roleType.parentTypeId.equals(roleType.roleTypeId)) {
                roleType = roleType.getRelatedOne("ParentRoleType");
            }
            if (! userLoginParentRoleTypeList.contains(roleType))
                userLoginParentRoleTypeList.add(roleType);
        }
    }
} else {
    condition = EntityCondition.makeCondition(
                EntityCondition.makeCondition("parentTypeId", null),
                EntityOperator.OR, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS_FIELD, "roleTypeId")
            );
    orderBy = ["description"];
    userLoginParentRoleTypeList = delegator.findList("RoleType", condition, null, orderBy, null, true);
}
result.userLoginParentRoleTypeList = userLoginParentRoleTypeList;
//Debug.log("**** userLoginParentRoleTypeList=" + userLoginParentRoleTypeList);

return result;