import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;
import javolution.util.FastList;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

replaceTheme("GPLUS", "GPLUS_GREEN_ACC");
replaceTheme("GPLUS_GREEN", "GPLUS_GREEN_ACC");
replaceTheme("OLD_GPLUS", "GPLUS_GREEN_ACC");
replaceTheme("GPLUS_VIOLET", "GPLUS_VIOLET_ACC");
replaceTheme("GPLUS_BLU", "GPLUS_BLUE_ACC");

removeTheme("GPLUS");
removeTheme("GPLUS_GREEN");
removeTheme("OLD_GPLUS");
removeTheme("GPLUS_VIOLET");
removeTheme("GPLUS_BLU");



def replaceTheme(actualTheme, replacementTheme) {
	def condList = FastList.newInstance();
	condList.add(EntityCondition.makeCondition("userPrefTypeId", "VISUAL_THEME"));
	condList.add(EntityCondition.makeCondition("userPrefValue", actualTheme));
	
	def userPreferenceList = delegator.findList("UserPreference", EntityCondition.makeCondition(condList), null, null, null, false);
	if (UtilValidate.isNotEmpty(userPreferenceList)) {
		userPreferenceList.each{ userPreferenceItem ->
			userPreferenceItem.userPrefValue = replacementTheme;
			delegator.store(userPreferenceItem);
		}
	}
}

def removeTheme(theme) {
	def visualThemeResourceList = delegator.findList("VisualThemeResource", EntityCondition.makeCondition("visualThemeId", theme), null, null, null, false);
	if (UtilValidate.isNotEmpty(visualThemeResourceList)) {
		visualThemeResourceList.each{ visualThemeResourceItem ->
			delegator.removeValue(visualThemeResourceItem);
		}
	}
	
	def visualTheme = delegator.findOne("VisualTheme", ["visualThemeId": theme], false);
	if (UtilValidate.isNotEmpty(visualTheme)) {
		delegator.removeValue(visualTheme);
	}
}

return result;

