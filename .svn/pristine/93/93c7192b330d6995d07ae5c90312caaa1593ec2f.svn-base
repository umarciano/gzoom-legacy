import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

def updateWorkEffortTypeContent(contentId, defaultParamsString) {
	// rimuovere tutti i layout
	conditionWorkEffortTypeContent = EntityCondition.makeCondition("contentId", contentId); 
	workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", conditionWorkEffortTypeContent, null, null, null, false);
	workEffortTypeContentList.each{ wType ->
		
		workEffortTypeContent = delegator.findOne("WorkEffortTypeContent", ["workEffortTypeId": wType.workEffortTypeId, "contentId": "WEFLD_PARTY"], false);

		parametersString = "";
		if(UtilValidate.isNotEmpty(wType.params)) {
			parametersString = wType.params;
		}
		if(UtilValidate.isNotEmpty(workEffortTypeContent.params)) {
			parametersString = parametersString + workEffortTypeContent.params;
		}
		
		parametersString =  defaultParamsString + parametersString;
		workEffortTypeContent.params = parametersString;
		
		delegator.removeValue(wType);
		delegator.store(workEffortTypeContent);
	}
	
	// alla fine remove se esiste
	content =  delegator.findOne("Content", ["contentId": contentId], false);
	if(UtilValidate.isNotEmpty(content)) {
		delegator.removeValue(content);
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

updateWorkEffortTypeContent("HUM_RES_DATEASS", "showDates=\"Y\";");
updateWorkEffortTypeContent("HUM_RES_UPLEVEL", "typeEmployment=\"Parent\";");
updateWorkEffortTypeContent("HUM_RES_DATEUPLEVEL", "typeEmployment=\"Parent\";showDates=\"Y\";"); 

return result;

