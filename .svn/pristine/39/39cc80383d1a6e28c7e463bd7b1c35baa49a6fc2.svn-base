import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Escludo per tutti i content che sono dentro SecurityPermissionContent, ma li mostro solo per gli utenti con il permesso associato
 */
if (UtilValidate.isNotEmpty(context.rootFolder)) {
	def folderList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition([EntityCondition.makeCondition("contentIdStart", context.rootFolder),
																							 EntityCondition.makeCondition("caContentAssocTypeId", "FOLDER_OF")]), null, null, null, false);
																						 
	if (UtilValidate.isNotEmpty(folderList)) {
		 
		def permissionIds = EntityUtil.getFieldListFromEntityList(EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroupPermissioneView", EntityCondition.makeCondition("userLoginId", userLogin.userLoginId), null, null, null, false)), "permissionId", true);		
		def contentIdList = EntityUtil.getFieldListFromEntityList(folderList, "contentId", true);
		
		def securityPermissionContents = EntityUtil.filterByDate(delegator.findList("SecurityPermissionContent", EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIdList), null, null, null, false));
		if(UtilValidate.isNotEmpty(securityPermissionContents)){
			securityPermissionContents.each { securityPermissionContent ->
				
				def exprs = UtilMisc.toList(
					EntityCondition.makeCondition("contentId", EntityOperator.EQUALS, securityPermissionContent.contentId),
					EntityCondition.makeCondition("permissionId", EntityOperator.IN, permissionIds));
				
				def securityPermissions = EntityUtil.filterByDate(delegator.findList("SecurityPermissionContent", EntityCondition.makeCondition(exprs), null, null, null, false));
				if(UtilValidate.isEmpty(securityPermissions)){
					context[securityPermissionContent.contentId] = securityPermissionContent.contentId;
				}
				
			}
		}
	
	}
}