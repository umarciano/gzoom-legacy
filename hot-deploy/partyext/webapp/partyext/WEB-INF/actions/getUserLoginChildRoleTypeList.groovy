import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
//oppure quando cerco un party per ruolo e in quel caso securityParentRoleTypeId = classificazione-ruolo e accedo alla classificazione ruoli 
//Debug.log("******************************* .groovy --> parameters.parentRoleTypeId=" + parameters.parentRoleTypeId);
//Debug.log("******************************* .groovy --> context.parentRoleTypeId=" + context.searchMap);

//Debug.log("******************************* .groovy --> parameters.searchMap=" + parameters.searchMap);
//Debug.log("******************************* .groovy --> parameters.userLoginPermission=" + parameters.userLoginPermission);
//Debug.log("******************************* .groovy --> context.userLoginValidRoleList=" + context.userLoginValidRoleList);
//Debug.log("******************************* .groovy --> parameters.userLoginValidRoleList=" + parameters.userLoginValidRoleList);
//Debug.log("******************************* .groovy --> context.managementChildExtraParams=" + context.managementChildExtraParams);

if(UtilValidate.isEmpty(context.parentRoleTypeId) && UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
    extraParams = StringUtil.strToMap(context.managementChildExtraParams);
    context.putAll(extraParams);
    //Debug.log("extraParams"+extraParams);
}

parentRoleTypeId = context.parentRoleTypeId;
if(UtilValidate.isNotEmpty(parameters.searchMap) && UtilValidate.isNotEmpty(parameters.searchMap.parentRoleTypeId)) {
    parentRoleTypeId = parameters.searchMap.parentRoleTypeId;
}
roleTypeNotLike = context.roleTypeNotLike;
if(UtilValidate.isNotEmpty(parameters.searchMap) && UtilValidate.isNotEmpty(parameters.searchMap.roleTypeNotLike)) {
	roleTypeNotLike = parameters.searchMap.roleTypeNotLike;
}
roleTypeId = "";
if(UtilValidate.isNotEmpty(parameters.searchMap) && UtilValidate.isNotEmpty(parameters.searchMap.roleTypeId)) {
    roleTypeId = parameters.searchMap.roleTypeId;
}

context.userLoginChildRoleTypeList = [];
result = dispatcher.runSync("loadUserLoginChildRoleTypeList", ["parentRoleTypeId" : parentRoleTypeId, "roleTypeId" : roleTypeId, "roleTypeNotLike" : roleTypeNotLike, "userLoginValidRoleList" : parameters.userLoginValidRoleList, "userLogin": userLogin]);
userLoginChildRoleTypeList = result.userLoginChildRoleTypeList;
context.userLoginChildRoleTypeList = userLoginChildRoleTypeList;
context.userLoginChildRoleTypeIdList = EntityUtil.getFieldListFromEntityList(userLoginChildRoleTypeList, "roleTypeId", true);

//Debug.log("******************************* .groovy --> roleTypeNotLike=" + roleTypeNotLike);
//Debug.log("******************************* .groovy --> context.userLoginPermission=" + context.userLoginPermission);
//Debug.log("******************************* .groovy --> context.userLoginChildRoleTypeList=" + context.userLoginChildRoleTypeList);
