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

List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("portalPageId", "GP_WE_PORTAL"));
conditions.add(EntityCondition.makeCondition("portalPortletId", "GP_Calendar"));

portletAttributeList = delegator.findList("PortletAttribute", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(portletAttributeList)) {
	Debug.log("Pulizia PortletAttribute...");
	delegator.removeAll(portletAttributeList);
}

portletPagePortletList = delegator.findList("PortalPagePortlet", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(portletPagePortletList)) {
	Debug.log("Pulizia PortletAttribute...");
	delegator.removeAll(portletPagePortletList);
}

return result;

