import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

def updateWorkEffortTypeContent() {
	
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

conditionWorkEffortTypeContent = EntityCondition.makeCondition(EntityCondition.makeCondition("parentTypeId", "CTX_EP"), 
		EntityCondition.makeCondition("contentId", EntityOperator.LIKE, "WEFLD_IND%")
);
workEffortTypeContentList = delegator.findList("WorkEffortTypeAndTypeContent", conditionWorkEffortTypeContent, null, null, null, false);
workEffortTypeContentList.each{ wTypeFolder ->
	parametersString = "";
	
	def wTypeList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortTypeId", wTypeFolder.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId": wTypeFolder.contentId)), null, null, null, false);
	if(UtilValidate.isEmpty(wTypeList)) {
		if(UtilValidate.isNotEmpty(wTypeFolder.params)) {
			parametersString = wTypeFolder.params;
		}
		parametersString =   "noConditionFind=\"Y\"; " + parametersString;
		def workEffortTypeContentFolder = delegator.getRelatedOne("WorkEffortTypeContent", wTypeFolder);
		workEffortTypeContentFolder.params = parametersString;
		
		delegator.store(workEffortTypeContentFolder);
	} else {
		def wType = EntityUtil.getFirst(wTypeList);
		if(UtilValidate.isNotEmpty(wType.params)) {
			parametersString = wType.params;
		}
		parametersString =   "noConditionFind=\"Y\"; " + parametersString;
		wType.params = parametersString;
		delegator.store(wType);
	}
	
	
	
}

return result;

