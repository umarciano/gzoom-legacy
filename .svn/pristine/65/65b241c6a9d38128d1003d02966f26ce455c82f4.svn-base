import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

def getDefaultParamsStringFrom(contentId) {
    if(contentId.contains("ASSO_W_C_SC_FR_LAY")) {
    	return "showAssocWeight=\"N\";showComment=\"Y\";showScoreValue=\"Y\";";
    }
    if(contentId.contains("ASSO_WE_CO_FROM_LAY")) {
    	return "showAssocWeight=\"N\";showComment=\"Y\";";
    }
    if(contentId.contains("ASSO_WE_FROM_LAY")) {
    	return "showRelationship=\"N\";";
    }
    if(contentId.contains("ASSOC_WE_SCO_FR_LAY")) {
    	return "showAssocWeight=\"N\";showScoreValue=\"Y\";";
    }
    if(contentId.contains("EVAL_REL_LAY")) {
    	return "showComment=\"Y\";";
    }
    if(contentId.contains("EVAL_REL_SCO_LAY")) {
    	return "showComment=\"Y\";showScoreValue=\"Y\";";
    }
    if(contentId.contains("LEV_BEL_WE_FROM_LAY")) {
    	return "assocLevelBelow=\"Y\";";
    }
    return "";
}

def getDefaultParamsStringTo(contentId) {
    if(contentId.contains("ASSO_WE_TO_LAY")) {
    	return "showRelationship=\"N\";";
    }
    if(contentId.contains("ASSOC_W_C_SC_TO_LAY")) {
    	return "showAssocWeight=\"N\";showComment=\"Y\";showScoreValue=\"Y\";";
    }
    if(contentId.contains("ASSOC_WE_CO_TO_LAY")) {
    	return "showAssocWeight=\"N\";showComment=\"Y\";";
    }
    if(contentId.contains("ASSOC_WE_SCO_TO_LAY")) {
    	return "showAssocWeight=\"N\";showScoreValue=\"Y\";";
    }
    if(contentId.contains("ASSOC_WE_TO_LAY")) {
    	return "showAssocWeight=\"N\";";
    }
    if(contentId.contains("EVAL_REL_SCO_TO_LAY")) {
    	return "showComment=\"Y\";showScoreValue=\"Y\";";
    }
    if(contentId.contains("EVAL_REL_TO_LAY")) {
    	return "showComment=\"Y\";";
    }
    return "";
}

def updateWorkEffortTypeContentFolder(folder) {
	def contentList = delegator.findList("Content", EntityCondition.makeCondition("contentTypeId", folder), null, null, null, false);
	if (UtilValidate.isNotEmpty(contentList)) {
		for (GenericValue content: contentList) {
			def defaultParamsString = "";
			if(folder.contains("WEFROM")) {
				defaultParamsString = getDefaultParamsStringFrom(content.contentId);
			}			
			if(folder.contains("WETO")) {
				defaultParamsString = getDefaultParamsStringTo(content.contentId);
			}
			updateWorkEffortTypeContent(folder, content.contentId, defaultParamsString);
		}
	}	
}

def updateWorkEffortTypeContent(folder, contentId, defaultParamsString) {
	// rimuovere tutti i layout
	conditionWorkEffortTypeContent = EntityCondition.makeCondition("contentId", contentId); 
	workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", conditionWorkEffortTypeContent, null, null, null, false);
	workEffortTypeContentList.each{ wType ->
		
		workEffortTypeContent = delegator.findOne("WorkEffortTypeContent", ["workEffortTypeId": wType.workEffortTypeId, "contentId": folder], false);
		if(UtilValidate.isNotEmpty(workEffortTypeContent)) {
			parametersString = "";
			if(UtilValidate.isNotEmpty(wType.params)) {
				parametersString = wType.params;
			}
			if(UtilValidate.isNotEmpty(workEffortTypeContent.params)) {
				parametersString = parametersString + workEffortTypeContent.params;
			}
			
			parametersString =  defaultParamsString + parametersString;
			workEffortTypeContent.params = parametersString;
			delegator.store(workEffortTypeContent);
		}
		
		delegator.removeValue(wType);
	}
	
	// alla fine remove 
	conditionContent = EntityCondition.makeCondition("contentId", ""); 
	content =  delegator.findOne("Content", ["contentId": contentId], false);
	delegator.removeValue(content);
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




def folderList = ["WEFLD_WEFROM", "WEFLD_WEFROM2", "WEFLD_WEFROM3", "WEFLD_WEFROM4", "WEFLD_WEFROM5",
		"WEFLD_WETO", "WEFLD_WETO2", "WEFLD_WETO3", "WEFLD_WETO4", "WEFLD_WETO5"];

for (String folder : folderList) {
	Debug.log(" - folder " + folder);
	updateWorkEffortTypeContentFolder(folder);
}

return result;

