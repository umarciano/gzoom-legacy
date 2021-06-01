import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import javolution.util.FastList;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();


def getCorrespondingParams(layoutContentId) {
	if("WE_ASS_DATE_LAY".equals(layoutContentId)) {
		return "showDates=\"Y\"";
	}
	if("WE_ASS_DATE2_LAY".equals(layoutContentId)) {
		return "showDates=\"Y\"; showRoleTypeWeight=\"N\"";
	}
	if("WE_ASS_WE_LAY".equals(layoutContentId)) {
		return "showRoleTypeWeight=\"N\"";
	}
	
	return "";	
}

def updateWorkEffortTypeContent(folder, contentId) {
	workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition("contentId", contentId), null, null, null, false);
	
	workEffortTypeContentList.each{ wType ->		
		workEffortTypeContent = delegator.findOne("WorkEffortTypeContent", ["workEffortTypeId": wType.workEffortTypeId, "contentId": folder], false);
		if(UtilValidate.isNotEmpty(workEffortTypeContent)) {
			workEffortTypeContent.params = getCorrespondingParams(contentId);
			delegator.removeValue(wType);
			delegator.store(workEffortTypeContent);
		}
	}
}

// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

def helperInfo = delegator.getGroupHelperInfo(groupName);
def dbUtil = new DatabaseUtil(helperInfo);


def folder = "WEFLD_WROLE";
def layoutFolderList = delegator.findList("Content", EntityCondition.makeCondition("contentTypeId", folder), null, null, null, false);
if(UtilValidate.isNotEmpty(layoutFolderList)) {
	layoutFolderList.each{ layoutFolderItem ->
		updateWorkEffortTypeContent(folder, layoutFolderItem.contentId);
		delegator.removeValue(layoutFolderItem);
		
		def dataResource = delegator.findOne("DataResource", ["dataResourceId": layoutFolderItem.contentId], false);
		if(UtilValidate.isNotEmpty(dataResource)) {
			delegator.removeValue(dataResource);
		}
	}	
}


return result;
