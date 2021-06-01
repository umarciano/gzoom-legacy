import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
//chiamato o subito dopo il login 
//oppure quando cerco un party per ruolo e in quel caso securityParentRoleTypeId = classificazione-ruolo e accedo alla classificazione ruoli 
//Debug.log("******************************* .groovy --> parameters.userLoginPermission=" + parameters.userLoginPermission);
//Debug.log("******************************* .groovy --> context.userLoginValidRoleList=" + context.userLoginValidRoleList);
//Debug.log("******************************* .groovy --> parameters.userLoginValidRoleList=" + parameters.userLoginValidRoleList);
//Debug.log("******************************* .groovy --> context.managementChildExtraParams=" + context.managementChildExtraParams);
//Debug.log("******************************* .groovy --> parameters.parentRoleTypeId=" + parameters.parentRoleTypeId);
//Debug.log("******************************* .groovy --> context.parentRoleTypeId=" + context.parentRoleTypeId);

if(UtilValidate.isEmpty(context.parentRoleTypeId) && UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
    extraParams = StringUtil.strToMap(context.managementChildExtraParams);
    context.putAll(extraParams);
    //Debug.log("extraParams"+extraParams);
}
if (UtilValidate.isEmpty(parameters.userLoginParentRoleTypeList)) {
	result = dispatcher.runSync("loadUserLoginParentRoleTypeList", ["userLoginValidRoleList" : parameters.userLoginValidRoleList, "userLoginId": userLogin.userLoginId, "userLogin": userLogin]);
	
	if (UtilValidate.isNotEmpty(parameters.searchMap) && UtilValidate.isNotEmpty(parameters.searchMap.parentRoleTypeId)) {
		context.userLoginParentRoleTypeList = EntityUtil.filterByAnd(result.userLoginParentRoleTypeList, ["roleTypeId" : parameters.searchMap.parentRoleTypeId]);
	} else {
		context.userLoginParentRoleTypeList = result.userLoginParentRoleTypeList;
	}
	context.userLoginParentRoleTypeIdList = EntityUtil.getFieldListFromEntityList(context.userLoginParentRoleTypeList, "roleTypeId", true);
} else {
	if (UtilValidate.isNotEmpty(parameters.searchMap) && UtilValidate.isNotEmpty(parameters.searchMap.parentRoleTypeId)) {
		context.userLoginParentRoleTypeList = EntityUtil.filterByAnd(parameters.userLoginParentRoleTypeList, ["roleTypeId" : parameters.searchMap.parentRoleTypeId]);
	} else {
		context.userLoginParentRoleTypeList = parameters.userLoginParentRoleTypeList;
	}
	context.userLoginParentRoleTypeIdList = EntityUtil.getFieldListFromEntityList(context.userLoginParentRoleTypeList, "roleTypeId", true);
}

//Debug.log("******************************* .groovy --> parameters.userLoginValidRoleList=" + parameters.userLoginValidRoleList);
//Debug.log("******************************* .groovy --> context.userLoginPermission=" + context.userLoginPermission);
Debug.log("******************************* .groovy --> context.userLoginParentRoleTypeList=" + context.userLoginParentRoleTypeList);
