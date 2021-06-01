import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


def noPortalContentIdList = ["NOPORTAL_BSC", "NOPORTAL_COR", "NOPORTAL_EVAL", "NOPORTAL_MY", "NOPORTAL_ORG", "NOPORTAL_PROC", "NOPORTAL_CDG", "NOPORTAL_TRAS", "NOPORTAL_REND", "NOPORTAL_GDPR", "NOPORTAL_PART", "NOPORTAL_DIR"];

def securityGroupContentConditionList = [];
securityGroupContentConditionList.add(EntityCondition.makeCondition("groupId", parameters.groupId));
securityGroupContentConditionList.add(EntityCondition.makeCondition("contentId", EntityOperator.IN, noPortalContentIdList));
securityGroupContentConditionList.add(EntityCondition.makeCondition("thruDate", null));
def securityGroupContentList = delegator.findList("SecurityGroupContent", EntityCondition.makeCondition(securityGroupContentConditionList), null, null, null, false);

if (UtilValidate.isNotEmpty(securityGroupContentList)) {
	for (GenericValue securityGroupContent : securityGroupContentList) {
		def userLoginSecurityGroupConditionList = [];
		userLoginSecurityGroupConditionList.add(EntityCondition.makeCondition("userLoginId", parameters.userLoginId));
		userLoginSecurityGroupConditionList.add(EntityCondition.makeCondition("groupId", securityGroupContent.contentId));
		def userLoginSecurityGroupList = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(userLoginSecurityGroupConditionList), null, null, null, false);
		
		if (UtilValidate.isEmpty(userLoginSecurityGroupList)) {
			def userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
			userLoginSecurityGroup.userLoginId = parameters.userLoginId;
			userLoginSecurityGroup.groupId = securityGroupContent.contentId;
			userLoginSecurityGroup.fromDate = parameters.fromDate;
			delegator.create(userLoginSecurityGroup);
		} else {
			def userLoginSecurityGroup = getUserLoginSecurityGroupClosed(userLoginSecurityGroupList);
			if (UtilValidate.isNotEmpty(userLoginSecurityGroup)) {
				userLoginSecurityGroup.thruDate = null;
				delegator.store(userLoginSecurityGroup);
			}
		}
	}
}




/*
 * cerco se nella lista c'e un userLoginSecurityGroup senza thruDate,
 * se lo trovo sono a posto, altrimenti prendo il primo e metto
 * il thruDate a null
 */
def getUserLoginSecurityGroupClosed(userLoginSecurityGroupList) {
	def hasOpen = false;
	for (GenericValue userLoginSecurityGroup : userLoginSecurityGroupList) {
		if (UtilValidate.isNotEmpty(userLoginSecurityGroup) && UtilValidate.isEmpty(userLoginSecurityGroup.thruDate)) {
			hasOpen = true;
			break;
		}
	}
	if (! hasOpen) {
		return EntityUtil.getFirst(userLoginSecurityGroupList);
	}
	return null;
}
