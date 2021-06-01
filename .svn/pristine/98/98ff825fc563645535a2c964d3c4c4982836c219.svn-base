import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


context.inputFields.workEffortIdTo = parameters.workEffortId;
if(UtilValidate.isEmpty(parameters.snapshot) || ! "Y".equals(parameters.snapshot)) {
	context.inputFields.wrToSnapShotId_fld0_op = "empty";
	context.inputFields.wrFromSnapShotId_fld0_op = "empty";
}

/**
 * Filtro la lista per searchDate
 */
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/populateFolderDateParams.groovy", context);

parameters.contentId = "WEFLD_WETO";

Debug.log(" Obiettivo search " + context.entityName + " with condition " + context.inputFields + ", then filter with contentId = " + parameters.contentId);
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);


//nomi dei campi
context.workEffortNameField = "Y".equals(context.localeSecondarySet) ?  "workEffortNameLang" : "workEffortName";
context.workEffortTypeDescriptionField = "Y".equals(context.localeSecondarySet) ?  "weTypeDescriptionLang" : "weTypeDescription";
context.workEffortDescriptionField = "Y".equals(context.localeSecondarySet) ? "descriptionLang" : "description";
context.weOrgPartyDescrField = "Y".equals(context.localeSecondarySet) ?  "weOrgPartyDescrLang" : "weOrgPartyDescr";

//valorizzazione in base ai parametri
context.workEffortNameValue = "";
context.weOrgPartyDescrValue = "";
context.weEtchValue = "";

context.workEffortNameDescription = "";
context.orderByField = context.workEffortNameField;
context.workEffortNameValue = context.workEffortNameField;
context.workEffortNameDescription = context.workEffortNameDescription + ("Y".equals(context.localeSecondarySet) ? " @{workEffortNameLang}" : " @{workEffortName}");


def listReturn = [];

if (UtilValidate.isNotEmpty(context.listIt)) {
	for (GenericValue value: context.listIt) {
		def map = [:];
		map.putAll(value);
		
		if (UtilValidate.isNotEmpty(value.workEffortIdTo)) {
			def parentFrom = delegator.findOne("WorkEffort", ["workEffortId" : value.workEffortIdTo], false);
			
			if (UtilValidate.isNotEmpty(parentFrom)) {
				map.worEffortParentIdFrom = parentFrom.workEffortParentId;
			}
		}
		
		if (UtilValidate.isNotEmpty(value.workEffortIdFrom)) {
			def parentTo = delegator.findOne("WorkEffort", ["workEffortId" : value.workEffortIdFrom], false);
			
			if (UtilValidate.isNotEmpty(parentTo)) {
				map.worEffortParentIdTo = parentTo.workEffortParentId;
			}
		}
		
	    listReturn.add(map);
	    
	}
}

context.listIt = listReturn;