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

def contentsToRemoveList = ["GP_MENU_00258", "GP_MENU_00224", "GP_MENU_00226", "GP_MENU_00235", "GP_MENU_00259", "GP_MENU_00236", "GP_MENU_00260", "GP_MENU_00237", "GP_MENU_00279", "GP_MENU_00280", "GP_MENU_00293", "GP_MENU_00294", "GP_MENU_00305", "GP_MENU_00306", "GP_MENU_00318", "GP_MENU_00319", "GP_MENU_00330", "GP_MENU_00331", "GP_MENU_00345", "GP_MENU_00346"];

if (UtilValidate.isNotEmpty(contentsToRemoveList)) {
	contentsToRemoveList.each{ contentToRemove ->
		def contentAttributeList = delegator.findList("ContentAttribute", EntityCondition.makeCondition("contentId", contentToRemove), null, null, null, false);
		if (UtilValidate.isNotEmpty(contentAttributeList)) {
			delegator.removeAll(contentAttributeList);
		}
		
		def contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition("contentIdTo", contentToRemove), null, null, null, false);
		if (UtilValidate.isNotEmpty(contentAssocList)) {
			delegator.removeAll(contentAssocList);
		}
		
		def securityGroupContentList = delegator.findList("SecurityGroupContent", EntityCondition.makeCondition("contentId", contentToRemove), null, null, null, false);
		if (UtilValidate.isNotEmpty(securityGroupContentList)) {
			delegator.removeAll(securityGroupContentList);
		}		
		
		def contentItem = delegator.findOne("Content", ["contentId": contentToRemove], false);
		if (UtilValidate.isNotEmpty(contentItem)) {
			delegator.removeValue(contentItem);
		}
		
		def helpItem = "HELP_" + contentToRemove;
		def contentHelpItem = delegator.findOne("Content", ["contentId": helpItem], false);
		if (UtilValidate.isNotEmpty(contentHelpItem)) {
			delegator.removeValue(contentHelpItem);
		}	
		
		def dataResourceHelpItem = delegator.findOne("DataResource", ["dataResourceId": helpItem], false);
		if (UtilValidate.isNotEmpty(dataResourceHelpItem)) {
			delegator.removeValue(dataResourceHelpItem);
		}		
    }
}

return result;
