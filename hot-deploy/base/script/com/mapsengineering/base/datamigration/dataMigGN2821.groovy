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

def contentsAssocToRemoveList = ["GP_MENU_00086", "GP_MENU_00105", "GP_MENU_00124", "GP_MENU_00269", "GP_MENU_00283", "GP_MENU_00335", "GP_MENU_00295", "GP_MENU_00308", "GP_MENU_00320"];

if (UtilValidate.isNotEmpty(contentsAssocToRemoveList)) {
	contentsAssocToRemoveList.each{ contentAssocToRemove ->		
		def contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition("contentId", contentAssocToRemove), null, null, null, false);
		if (UtilValidate.isNotEmpty(contentAssocList)) {
			delegator.removeAll(contentAssocList);
		}		
    }
}

return result;
