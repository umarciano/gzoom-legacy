import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


if (UtilValidate.isNotEmpty(context.rootFolder)) {
	def folderList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition([EntityCondition.makeCondition("contentIdStart", context.rootFolder),
																							 EntityCondition.makeCondition("caContentAssocTypeId", "FOLDER_OF")]), null, null, null, false);
																						 
	if (UtilValidate.isNotEmpty(folderList)) {
		def groupIds = EntityUtil.getFieldListFromEntityList(EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition("userLoginId", userLogin.userLoginId), null, null, null, false)), "groupId", false);
		
		def contentIdList = EntityUtil.getFieldListFromEntityList(folderList, "contentId", true);
		def exprs = UtilMisc.toList(
			EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIdList),
			EntityCondition.makeCondition("groupId", EntityOperator.IN, groupIds));
		def securityGroupContents = EntityUtil.filterByDate(delegator.findList("SecurityGroupContent", EntityCondition.makeCondition(exprs), null, null, null, false));
		if (UtilValidate.isNotEmpty(securityGroupContents)) {
			securityGroupContents.each { securityGroupContent ->
				context[securityGroupContent.contentId] = securityGroupContent.contentId;
			}
		}
	}
}