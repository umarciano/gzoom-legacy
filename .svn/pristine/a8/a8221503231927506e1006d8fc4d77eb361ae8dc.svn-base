import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();


// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

addPermission("TRASPERFMGR_CREATE", "TRASPERFMGR_ROOT");
addPermission("BSCPERFMGR_CREATE", "BSCPERFMGR_ROOT");
addPermission("RENDPERFMGR_CREATE", "RENDPERFMGR_ROOT");
addPermission("PROCPERFMGR_CREATE", "PROCPERFMGR_ROOT");
addPermission("ORGPERFMGR_CREATE", "ORGPERFMGR_ROOT");
addPermission("GDPRPERFMGR_CREATE", "GDPRPERFMGR_ROOT");
addPermission("CORPERFMGR_CREATE", "CORPERFMGR_ROOT");
addPermission("EMPLPERFMGR_CREATE", "EMPLPERFMGR_ROOT");
addPermission("CDGPERFMGR_CREATE", "CDGPERFMGR_ROOT");



def addPermission(permission, toAddPermission) {
	def securityGroupPermissionList = delegator.findList("SecurityGroupPermission", EntityCondition.makeCondition("permissionId", permission), null, null, null, false);
	if (UtilValidate.isNotEmpty(securityGroupPermissionList)) {
		securityGroupPermissionList.each{ securityGroupPermissionItem ->
			def securityGroupPermission = delegator.findOne("SecurityGroupPermission", ["groupId" : securityGroupPermissionItem.groupId, "permissionId" : toAddPermission], false);
			if (UtilValidate.isEmpty(securityGroupPermission)) {
				GenericValue sgp = delegator.makeValue("SecurityGroupPermission");
				sgp.put("groupId", securityGroupPermissionItem.groupId);
				sgp.put("permissionId", toAddPermission);
				sgp.create();
			}
		}
	}
}
	
return result;
