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

// delete from content_assoc where content_id = 'GP_MENU_00333' and content_id_to = 'GP_MENU_00334'
List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("contentId", "GP_MENU_00333"));
conditions.add(EntityCondition.makeCondition("contentIdTo", "GP_MENU_00334"));

contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(contentAssocList)) {
	Debug.log("Elimino contentAssoc...");
	delegator.removeAll(contentAssocList);
}

// delete from content_assoc where content_id = 'GP_MENU_00268' and content_id_to = 'GP_MENU_00333'
conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("contentId", "GP_MENU_00268"));
conditions.add(EntityCondition.makeCondition("contentIdTo", "GP_MENU_00333"));

contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(contentAssocList)) {
    Debug.log("Elimino contentAssoc...");
    delegator.removeAll(contentAssocList);
}


// delete from content_assoc where content_id = 'GP_MENU_00066' and content_id_to = 'GP_MENU_00334'
conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("contentId", "GP_MENU_00066"));
conditions.add(EntityCondition.makeCondition("contentIdTo", "GP_MENU_00334"));

contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditions), null, null, null, false);

if(UtilValidate.isNotEmpty(contentAssocList)) {
    Debug.log("Elimino contentAssoc...");
    delegator.removeAll(contentAssocList);
}


// delete from content_attribute where content_id = 'GP_MENU_00333'
contentAttrubiteList = delegator.findList("ContentAttribute", EntityCondition.makeCondition("contentId", "GP_MENU_00333"), null, null, null, false);

if(UtilValidate.isNotEmpty(contentAttrubiteList)) {
    Debug.log("Elimino contentAttrubite...");
    delegator.removeAll(contentAttrubiteList);
}

// delete from content where content_id = 'GP_MENU_00333
contentList = delegator.findList("Content", EntityCondition.makeCondition("contentId", "GP_MENU_00333"), null, null, null, false);

if(UtilValidate.isNotEmpty(contentList)) {
    Debug.log("Elimino content...");
    delegator.removeAll(contentList);
}


return result;

