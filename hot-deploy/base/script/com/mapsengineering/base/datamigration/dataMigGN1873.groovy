import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import javolution.util.FastList;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();


def getReplacedParams(params, oldParam, replacementParams) {
	return params.replace(oldParam, replacementParams);
}

def updateWorkEffortTypeContent(folder, oldParam, replacementParams) {
	List<EntityCondition> conditions = FastList.newInstance();
	conditions.add(EntityCondition.makeCondition("contentId", folder));
	conditions.add(EntityCondition.makeCondition("params", EntityOperator.LIKE, "%" + oldParam + "%"));

	workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition(conditions), null, null, null, false);
	workEffortTypeContentList.each{ wType ->
		
		workEffortTypeContent = delegator.findOne("WorkEffortTypeContent", ["workEffortTypeId": wType.workEffortTypeId, "contentId": folder], false);
		if(UtilValidate.isNotEmpty(workEffortTypeContent)) {
			parametersString = "";
			if(UtilValidate.isNotEmpty(workEffortTypeContent.params)) {
				parametersString = getReplacedParams(workEffortTypeContent.params, oldParam, replacementParams);				
				workEffortTypeContent.params = parametersString;
				delegator.store(workEffortTypeContent);
			}
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


def oldParam = "assocLevelBelow=\"Y\"";
def replacementParams = "assocLevelSameUO=\"Y\"; assocLevelChildUO=\"Y\"";

def folderList = ["WEFLD_WEFROM", "WEFLD_WEFROM2", "WEFLD_WEFROM3", "WEFLD_WEFROM4", "WEFLD_WEFROM5",
		"WEFLD_WETO", "WEFLD_WETO2", "WEFLD_WETO3", "WEFLD_WETO4", "WEFLD_WETO5"];

for (String folder : folderList) {
	Debug.log(" - folder " + folder);
	updateWorkEffortTypeContent(folder, oldParam, replacementParams);
}

return result;

