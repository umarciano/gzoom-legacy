import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def menuItems = StringUtil.split(context.menuItems, "|");
def permissions = StringUtil.split(context.permissions, "|");
def nowTimestamp = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

if (UtilValidate.isNotEmpty(menuItems)) {
	if (UtilValidate.isNotEmpty(permissions)) {
		permissions.each {permission ->
		    def securityGroupPermissionCondList = [];
		    securityGroupPermissionCondList.add(EntityCondition.makeCondition("permissionId", permission));
		    securityGroupPermissionCondList.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FULLADMIN"));
		    securityGroupPermissionCondList.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FLEXADMIN"));
		    def entityFindOptions = new EntityFindOptions();
		    entityFindOptions.setDistinct(true);
		    def fieldsToSelect = UtilMisc.toSet("groupId");
		    def securityGroupPermissionList = delegator.findList("SecurityGroupPermission", EntityCondition.makeCondition(securityGroupPermissionCondList), fieldsToSelect, null, entityFindOptions, false);
		    if (UtilValidate.isNotEmpty(securityGroupPermissionList)) {
		    	securityGroupPermissionList.each {securityGroupPermissionItem ->
		    		menuItems.each {menuItem ->
		    		    def securityGroupContentCondList = [];
		    		    securityGroupContentCondList.add(EntityCondition.makeCondition("groupId", securityGroupPermissionItem.groupId));
		    		    securityGroupContentCondList.add(EntityCondition.makeCondition("contentId", menuItem));
		    		    def securityGroupContentList = delegator.findList("SecurityGroupContent", EntityCondition.makeCondition(securityGroupContentCondList), null, null, null, false);
		    		    if (UtilValidate.isEmpty(securityGroupContentList)) {
		    		    	Debug.log("aggiunta funzione esclusa per groupId " + securityGroupPermissionItem.groupId + " contentId " + menuItem);
		    		    	def securityGroupContentGV = delegator.makeValue("SecurityGroupContent");
		    		    	securityGroupContentGV.groupId = securityGroupPermissionItem.groupId;
		    		    	securityGroupContentGV.contentId = menuItem;
		    		    	securityGroupContentGV.fromDate = nowTimestamp;
		    		    	delegator.create(securityGroupContentGV);
		    		    }
		    		}
		    	}
		    }
		}
	}
}