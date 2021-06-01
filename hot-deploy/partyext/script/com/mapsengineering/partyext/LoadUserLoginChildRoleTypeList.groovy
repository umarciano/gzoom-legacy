// loadUserLoginChildRoleTypeList
// Carica la lista dei ruoli figli ammessi (RoleType).

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

dispatcher = dctx.getDispatcher();
delegator = dctx.getDelegator();

//Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.securityParentRoleTypeId=" + context.securityParentRoleTypeId);
//Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.parentRoleTypeId=" + context.parentRoleTypeId);
//Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.roleTypeId=" + context.roleTypeId);
//Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.userLoginPermission=" + context.userLoginPermission);
//Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.userLoginValidRoleList=" + context.userLoginValidRoleList);

if (UtilValidate.isEmpty(context.userLoginId))
	context.userLoginId = userLogin.userLoginId;

results = dispatcher.runSync("getUserLoginPermission", ["userLoginId": context.userLoginId, "userLogin" : userLogin]);

if (ServiceUtil.isError(results)) {
	return results;
}

userLoginPermission = results.userLoginPermission;

//Debug.log("************************************** LoadUserLoginParentRoleTypeList.groovy --> results.userLoginPermission = " + results.userLoginPermission);

result = ServiceUtil.returnSuccess();
userLoginChildRoleTypeList = [];
if (UtilValidate.isNotEmpty(userLoginPermission) && ("PARTYMGRROLE_VIEW".equals(userLoginPermission) || "PARTYMGRROLE_ADMIN".equals(userLoginPermission))) {
    if (UtilValidate.isNotEmpty(context.userLoginValidRoleList)) {
        userLoginValidRoleList.each { userLoginValidRole ->
            roleType = userLoginValidRole.getRelatedOne("RoleType");
            if(UtilValidate.isNotEmpty(context.roleTypeId)) {
                if(roleType.roleTypeId.equals(context.roleTypeId)) {
                    if (! userLoginChildRoleTypeList.contains(roleType))
                        userLoginChildRoleTypeList.add(roleType);
                }
            }
            else {
                if (UtilValidate.isNotEmpty(roleType.parentTypeId) && !roleType.parentTypeId.equals(roleType.roleTypeId)) {
                    if (UtilValidate.isEmpty(context.parentRoleTypeId) || roleType.parentTypeId.equals(context.parentRoleTypeId)) {
                        if (! userLoginChildRoleTypeList.contains(roleType))
                            userLoginChildRoleTypeList.add(roleType);
                    }
                } else {
                    if (UtilValidate.isEmpty(context.parentRoleTypeId) || roleType.roleTypeId.equals(context.parentRoleTypeId)) {
                        roleTypeChildren = roleType.getRelated("ChildRoleType");
                        if (UtilValidate.isNotEmpty(roleTypeChildren)) {
                            roleTypeChildren.each { roleTypeChild ->
                                if (! userLoginChildRoleTypeList.contains(roleTypeChild))
                                    userLoginChildRoleTypeList.add(roleTypeChild);
                            }
                        }
                    }
                }
            }
        }
    }
    result.userLoginChildRoleTypeList = userLoginChildRoleTypeList;
} else if (UtilValidate.isNotEmpty(userLoginPermission)){
    if (UtilValidate.isNotEmpty(context.roleTypeId))
        condition = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, context.roleTypeId);
    else  {
    	  
    	if (UtilValidate.isEmpty(context.parentRoleTypeId))
	        condition = EntityCondition.makeCondition("parentTypeId", EntityOperator.NOT_EQUAL, null);
	    else 
	        condition = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, context.parentRoleTypeId);
   
		if(UtilValidate.isNotEmpty(context.roleTypeNotLike)){
	    	condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_LIKE, roleTypeNotLike));
	    }
	 }
    
    
//  Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.userLoginValidRoleList=" + condition.toString());
    
    orderBy = ["description"];
    userLoginChildRoleTypeList = delegator.findList("RoleType", condition, null, orderBy, null, true);
    result.userLoginChildRoleTypeList = userLoginChildRoleTypeList;
}
//Debug.log("******************************* LoadUserLoginChildRoleTypeList.groovy --> context.userLoginValidRoleList=" + result.userLoginValidRoleList);

return result;
