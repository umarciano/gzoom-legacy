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

def securityGroupConditions = [];
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FULLADMIN"));
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FLEXADMIN"));
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_LIKE, "NOPORTAL%"));
def securityGroupList = delegator.findList("SecurityGroup", EntityCondition.makeCondition(securityGroupConditions), null, null, null, false);

if(UtilValidate.isNotEmpty(securityGroupList)) {
	securityGroupList.each{ securityGroupItem ->
		
		// controllo su security_group_permission per permission_id = 'EMPLPERFMGR_VIEW'
		def securityGroupPermissionConditions = [];
		securityGroupPermissionConditions.add(EntityCondition.makeCondition("groupId", securityGroupItem.groupId));
		securityGroupPermissionConditions.add(EntityCondition.makeCondition("permissionId", "WORKEFFORTMGR_VIEW"));
		def securityGroupPermissionList = delegator.findList("SecurityGroupPermission", EntityCondition.makeCondition(securityGroupPermissionConditions), 
			null, null, null, false);
		
		if(UtilValidate.isNotEmpty(securityGroupPermissionList)) {	
			def securityGroupContentConditions = [];
			securityGroupContentConditions.add(EntityCondition.makeCondition("groupId", securityGroupItem.groupId));
			securityGroupContentConditions.add(EntityCondition.makeCondition("contentId", "GP_MENU_00485"));
			def securityGroupContentList = delegator.findList("SecurityGroupContent", 
				EntityCondition.makeCondition(securityGroupContentConditions), null, null, null, false);
			
		    if (UtilValidate.isEmpty(securityGroupContentList)) {
		    	GenericValue securityGroupContent = delegator.makeValue("SecurityGroupContent");
		    	securityGroupContent.put("groupId", securityGroupItem.groupId);
		    	securityGroupContent.put("contentId", "GP_MENU_00485");
		    	securityGroupContent.put("fromDate", new Timestamp(UtilDateTime.toDate(1, 1, 2019, 0, 0, 0).getTime()));
		    	
		    	securityGroupContent.create();
		    }	    
		}
	}
}
	
return result;
