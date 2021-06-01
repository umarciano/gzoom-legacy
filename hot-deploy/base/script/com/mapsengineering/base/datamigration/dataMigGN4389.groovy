import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;


def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def contentId = "REPORT_SSVIC";
def workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition("contentId",  contentId), null, null, null, false);
if (UtilValidate.isNotEmpty(workEffortTypeContentList)) {
    workEffortTypeContentList.each{ workEffortTypeContentItem ->
        delegator.removeValue(workEffortTypeContentItem);
    }
}

def contentAssocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition("contentIdTo",  contentId), null, null, null, false);
if (UtilValidate.isNotEmpty(contentAssocList)) {
    contentAssocList.each{ contentAssocItem ->
        delegator.removeValue(contentAssocItem);
    }
}

def contentItem = delegator.findOne("Content", ["contentId" : contentId], false);
if (UtilValidate.isNotEmpty(contentItem)) {
    delegator.removeValue(contentItem);
}

def dataResourceItem = delegator.findOne("DataResource", ["dataResourceId" : contentId], false);
if (UtilValidate.isNotEmpty(dataResourceItem)) {
    delegator.removeValue(dataResourceItem);
}

return result;
