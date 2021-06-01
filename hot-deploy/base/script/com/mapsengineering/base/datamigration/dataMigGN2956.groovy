import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

//check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

//tipologie anticorruzione
def corTypeCondList = [];
corTypeCondList.add(EntityCondition.makeCondition("parentTypeId", "CTX_CO"));
corTypeCondList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.NOT_EQUAL, "CTX_CO"));
def corTypeList = delegator.findList("WorkEffortType", EntityCondition.makeCondition(corTypeCondList), null, null, null, false);
if (UtilValidate.isNotEmpty(corTypeList)) {
	corTypeList.each{ corTypeItem ->	
	    def workEffortTypeContent = delegator.findOne("WorkEffortTypeContent", ["workEffortTypeId": corTypeItem.workEffortTypeId, "contentId": "WEFLD_MAIN"], false);
	    if(UtilValidate.isNotEmpty(workEffortTypeContent)) {
	    	def paramsStr = workEffortTypeContent.params;
	    	if (UtilValidate.isEmpty(workEffortTypeContent.params)) {
	    		paramsStr = "";
	    	}
	    	if (UtilValidate.isNotEmpty(workEffortTypeContent.params) && ! workEffortTypeContent.params.trim().endsWith(";")) {
	    		paramsStr += ";";
	    	}
	    	paramsStr += "etchDescr=\"printTitle\";";
	    	workEffortTypeContent.params = paramsStr;
	    	delegator.store(workEffortTypeContent);
	    }
    }
}

//tipologie perf strategica e operativa
def workEffortList = delegator.findList("WorkEffort", EntityCondition.makeCondition("description", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD), null, null, null, false);
def stratOrgCondList = [];
stratOrgCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["CTX_BS", "CTX_OR"]));
stratOrgCondList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.NOT_EQUAL, "CTX_BS"));
stratOrgCondList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.NOT_EQUAL, "CTX_OR"));
if (UtilValidate.isNotEmpty(workEffortList)) {
	stratOrgCondList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(workEffortList, "workEffortTypeId", true)));
} else {
	stratOrgCondList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.IN, ["null-field"]));
}
def stratOrgTypeList = delegator.findList("WorkEffortType", EntityCondition.makeCondition(stratOrgCondList), null, null, null, false);
if (UtilValidate.isNotEmpty(stratOrgTypeList)) {
	stratOrgTypeList.each{ stratOrgTypeItem ->	
	    def workEffortTypeContent = delegator.findOne("WorkEffortTypeContent", ["workEffortTypeId": stratOrgTypeItem.workEffortTypeId, "contentId": "WEFLD_MAIN"], false);
	    if(UtilValidate.isNotEmpty(workEffortTypeContent)) {
	    	def paramsStr = workEffortTypeContent.params;
	    	if (UtilValidate.isEmpty(workEffortTypeContent.params)) {
	    		paramsStr = "";
	    	}	    	
	    	if (UtilValidate.isNotEmpty(workEffortTypeContent.params) && ! workEffortTypeContent.params.trim().endsWith(";")) {
	    		paramsStr += ";";
	    	}
	    	paramsStr += "etchDescr=\"shortDescription\";";
	    	workEffortTypeContent.params = paramsStr;
	    	delegator.store(workEffortTypeContent);
	    }
    }
}

return result;