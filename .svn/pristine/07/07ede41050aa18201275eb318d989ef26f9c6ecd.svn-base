import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

//check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

def menuList = ["GP_MENU_00485", "GP_MENU_00514", "GP_MENU_00515", "GP_MENU_00516", "GP_MENU_00517", "GP_MENU_00518", "GP_MENU_00519", "GP_MENU_00520", "GP_MENU_00521", "GP_MENU_00522", "GP_MENU_00523", "GP_MENU_00524", "GP_MENU_00525", "GP_MENU_00526", "GP_MENU_00527", "GP_MENU_00528", "GP_MENU_00529", "GP_MENU_00530", "GP_MENU_00531", "GP_MENU_00532", "GP_MENU_00533", "GP_MENU_00534", "GP_MENU_00535", "GP_MENU_00536"];

def securityGroupConditions = [];
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FULLADMIN"));
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FLEXADMIN"));
def securityGroupList = delegator.findList("SecurityGroup", EntityCondition.makeCondition(securityGroupConditions), null, null, null, false);

if (UtilValidate.isNotEmpty(securityGroupList)) {
	securityGroupList.each{ securityGroupItem ->
	    menuList.each{ menuItem ->
	        def securityGroupContent = delegator.makeValue("SecurityGroupContent");
	        securityGroupContent.groupId = securityGroupItem.groupId;
	        securityGroupContent.contentId = menuItem;
	        securityGroupContent.fromDate = new Timestamp(UtilDateTime.toDate(1, 1, 2021, 0, 0, 0).getTime());
	        delegator.create(securityGroupContent);
	    }
	}
}

return result;

