import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
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

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}


def fieldsToSelect = UtilMisc.toSet("workEffortContentTypeId", "workEffortTypeId");
def distinctOption = new EntityFindOptions();
distinctOption.setDistinct(true);

def workEffortContenList = delegator.findList("WorkEffortContentAndWorkEffort", null, fieldsToSelect, null, distinctOption, false);

if(UtilValidate.isNotEmpty(workEffortContenList)) {
	workEffortContenList.each{ workEffortContenItem ->	
		GenericValue workEffortTypeContentType = delegator.makeValue("WorkEffortTypeContentType");
		workEffortTypeContentType.put("workEffortTypeId", workEffortContenItem.workEffortTypeId);
		workEffortTypeContentType.put("workEffortContentTypeId", workEffortContenItem.workEffortContentTypeId);
		workEffortTypeContentType.put("sequenceNum", Long.valueOf(1));	
		workEffortTypeContentType.create();
	}

}
		
return result;
