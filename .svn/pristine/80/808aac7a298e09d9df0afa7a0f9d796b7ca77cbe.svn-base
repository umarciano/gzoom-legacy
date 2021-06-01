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

setUserPrefVisualTheme("_NA_");
setUserPrefVisualTheme("admin");



def setUserPrefVisualTheme(userLoginId) {
	def userPrefVisualTheme = delegator.findOne("UserPreference", ["userLoginId" : userLoginId, "userPrefTypeId" : "VISUAL_THEME"], false);
	if (UtilValidate.isNotEmpty(userPrefVisualTheme)) {
		if (! "GPLUS_GREEN_ACC".equals(userPrefVisualTheme.userPrefValue)) {
			userPrefVisualTheme.userPrefValue = "GPLUS_GREEN_ACC";
			userPrefVisualTheme.store();
		}
	} else {
		userPrefVisualTheme = delegator.makeValue("UserPreference");
		userPrefVisualTheme.put("userLoginId", userLoginId);
		userPrefVisualTheme.put("userPrefTypeId", "VISUAL_THEME");
		userPrefVisualTheme.put("userPrefGroupTypeId", "GLOBAL_PREFERENCES");
		userPrefVisualTheme.put("userPrefValue", "GPLUS_GREEN_ACC");
		userPrefVisualTheme.create();
	}
}

return result;
