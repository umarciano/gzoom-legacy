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
conditions.add(EntityCondition.makeCondition("contentId", "GP_MENU_00066"));
conditions.add(EntityCondition.makeCondition("contentIdTo", "GP_MENU_00334"));

contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(contentAssocList)) {
	Debug.log("Elimino contentAssoc...");
	delegator.removeAll(contentAssocList);
}

return result;

