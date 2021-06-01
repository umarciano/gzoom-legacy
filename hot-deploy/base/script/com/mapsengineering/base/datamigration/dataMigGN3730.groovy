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

def nowTimestamp = UtilDateTime.nowTimestamp();

def groupName = DatabaseUtil.DEFAULT_GROUP_NAME;

def distinctOption = new EntityFindOptions();
distinctOption.setDistinct(true);

def userLoginSecurityGroupList = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_EQUAL, "admin"), UtilMisc.toSet("userLoginId"), null, distinctOption, false);
if(UtilValidate.isNotEmpty(userLoginSecurityGroupList)) {
	userLoginSecurityGroupList.each{ userLoginSecurityGroupItem ->
		def noPortalPartConditions = [];
		noPortalPartConditions.add(EntityCondition.makeCondition("userLoginId", userLoginSecurityGroupItem.userLoginId));
		noPortalPartConditions.add(EntityCondition.makeCondition("groupId", "NOPORTAL_PART"));
		def noPortalPartList = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(noPortalPartConditions), null, null, null, false);
	
	    if (UtilValidate.isEmpty(noPortalPartList)) {
	    	GenericValue userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
	    	userLoginSecurityGroup.put("userLoginId", userLoginSecurityGroupItem.userLoginId);
	    	userLoginSecurityGroup.put("groupId", "NOPORTAL_PART");
	    	userLoginSecurityGroup.put("fromDate", nowTimestamp);
	    	
	    	userLoginSecurityGroup.create();
	    }
	}
}


def securityGroupConditions = [];
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FULLADMIN"));
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_EQUAL, "FLEXADMIN"));
securityGroupConditions.add(EntityCondition.makeCondition("groupId", EntityOperator.NOT_LIKE, "NOPORTAL%"));
def securityGroupList = delegator.findList("SecurityGroup", EntityCondition.makeCondition(securityGroupConditions), null, null, null, false);

if(UtilValidate.isNotEmpty(securityGroupList)) {
	securityGroupList.each{ securityGroupItem ->
		def userLoginSecurityGroups = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition("groupId", securityGroupItem.groupId), null, null, null, false);
	
	    if (UtilValidate.isNotEmpty(userLoginSecurityGroups)) {
			def securityGroupContentConditions = [];
			securityGroupContentConditions.add(EntityCondition.makeCondition("groupId", securityGroupItem.groupId));
			securityGroupContentConditions.add(EntityCondition.makeCondition("contentId", "NOPORTAL_PART"));
			def securityGroupContentList = delegator.findList("SecurityGroupContent", EntityCondition.makeCondition(securityGroupContentConditions), null, null, null, false);
			
		    if (UtilValidate.isEmpty(securityGroupContentList)) {
		    	GenericValue securityGroupContent = delegator.makeValue("SecurityGroupContent");
		    	securityGroupContent.put("groupId", securityGroupItem.groupId);
		    	securityGroupContent.put("contentId", "NOPORTAL_PART");
		    	securityGroupContent.put("fromDate", nowTimestamp);
		    	
		    	securityGroupContent.create();
		    }
	    }
	}
}
	
return result;
