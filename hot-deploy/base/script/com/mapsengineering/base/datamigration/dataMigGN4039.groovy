import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import javolution.util.FastList;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();


// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

List<EntityCondition> conditions = FastList.newInstance();
List<EntityCondition> conditionOrs = FastList.newInstance();
conditionOrs.add(EntityCondition.makeCondition("permissionId", "access"));
conditionOrs.add(EntityCondition.makeCondition("permissionId", "create"));
conditionOrs.add(EntityCondition.makeCondition("permissionId", "read"));
conditionOrs.add(EntityCondition.makeCondition("permissionId", "update"));
conditionOrs.add(EntityCondition.makeCondition("permissionId", "delete"));

conditions.add(EntityCondition.makeCondition(conditionOrs, EntityJoinOperator.OR));

securityGroupPermissionList = delegator.findList("SecurityGroupPermission", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(securityGroupPermissionList)) {
    Debug.log("Elimino securityGroupPermissionList...");
    delegator.removeAll(securityGroupPermissionList);
}

securityPermissionList = delegator.findList("SecurityPermission", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(securityPermissionList)) {
	Debug.log("Elimino securityPermissionList...");
	delegator.removeAll(securityPermissionList);
}

return result;

