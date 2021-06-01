import com.mapsengineering.base.menu.MenuHelper
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;

def currentWorkeffort = delegator.findOne("WorkEffortView", UtilMisc.toMap("workEffortId", parameters.workEffortId), false);

long startTime = System.currentTimeMillis();

parameters.rootInqyTree="N";
Debug.log("getWorkeffortExtSelectedMenuItem.groovy survey= " + parameters.survey);
if(UtilValidate.isEmpty(parameters.survey) || parameters.survey == 'N'){
	if (UtilValidate.isNotEmpty(currentWorkeffort)) {
	    def currentStatus = currentWorkeffort.currentStatusId;
	
	    if (UtilValidate.isNotEmpty(currentStatus)) {
	        if (currentStatus.indexOf("MONIT") != -1) {
	            if (UtilValidate.isNotEmpty(MenuHelper.getSubContentMap(delegator, locale, context.monitMenuItemId, context.userLogin, parameters.security, parameters._serverId, null))) {
	                context.selectedMenuItem = context.monitMenuItemId;
	            }
	        } else if (currentStatus.indexOf("EXEC") != -1) {
	            if (UtilValidate.isNotEmpty(MenuHelper.getSubContentMap(delegator, locale, context.execMenuItemId, context.userLogin, parameters.security, parameters._serverId, null))) {
	                context.selectedMenuItem = context.execMenuItemId;
	            }
	        } else if (currentStatus.indexOf("PLAN") != -1) {
	            if (UtilValidate.isNotEmpty(MenuHelper.getSubContentMap(delegator, locale, context.planMenuItemId, context.userLogin, parameters.security, parameters._serverId, null))) {
	                context.selectedMenuItem = context.planMenuItemId;
	            }
	        }
	    }
	}
} else{
	context.selectedMenuItem = "GP_MENU_00130";
}

if (UtilValidate.isEmpty(context.selectedMenuItem) && UtilValidate.isNotEmpty(MenuHelper.getSubContentMap(delegator, locale, context.inqyMenuItemId, context.userLogin, parameters.security, parameters._serverId, null))) {
    context.selectedMenuItem = context.inqyMenuItemId;
    context.loadInqyTree="Y";
    parameters.rootInqyTree="Y";
}

if (UtilValidate.isNotEmpty(context.selectedMenuItem)) {
    def moduleMap = MenuHelper.getModule(delegator, locale, context.selectedMenuItem, null);

    if (UtilValidate.isNotEmpty(moduleMap)) {
        context.selectedModule = moduleMap.moduleContentId;
        parameters.breadcrumbs = moduleMap.breadcrumbs;
    }
}
long endTime = System.currentTimeMillis();
Debug.log("Run script in " + (endTime - startTime) + " milliseconds at location = component://workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkeffortExtSelectedMenuItem.groovy");
