import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;

context.inputFields.workEffortIdFrom = parameters.workEffortId;
if(UtilValidate.isEmpty(parameters.snapshot) || ! "Y".equals(parameters.snapshot)) {
	context.inputFields.wrToSnapShotId_fld0_op = "empty";
	context.inputFields.wrFromSnapShotId_fld0_op = "empty";
}
context.inputFields.wrToActivation_op = "notEqual";
context.inputFields.wrToActivation = "ACTSTATUS_REPLACED";

/**
 * Filtro la lista per searchDate
 */
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/populateFolderDateParams.groovy", context);

Debug.log(" First search " + context.entityName + " with condition " + context.inputFields + ", then filter with contentId = " + (UtilValidate.isNotEmpty(parameters.contentId) ? parameters.contentId : "WEFLD_WEFROM"));
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);

def workEffortTypeId = parameters.workEffortTypeId != null ? parameters.workEffortTypeId : null;

if(UtilValidate.isEmpty(workEffortTypeId)){
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffort)){
		workEffortTypeId = workEffort.workEffortTypeId;
	}
}

parameters.contentId = UtilValidate.isNotEmpty(parameters.contentId) ? parameters.contentId : "WEFLD_WEFROM";

context.showOrgUnit = "N";
context.showDates = "N";
context.showAssocWeight = "Y";
context.showScoreValue = "N";  //Y, N, WEIGHTED
context.showRelationship = "Y";
context.showComment = "N";
context.assocLevelSameUO = "N";
context.assocLevelParentUO = "N";
context.assocLevelChildUO = "N";
context.assocLevelSisterUO = "N";
context.orgUnitIdRelation = context.defaultOrganizationPartyId;
context.detailEnabled = "Y";
context.showEtch = "Y"; // Y, N, C
context.showSequence = "Y";

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
	/** Recupero params */
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(workEffortTypeId, parameters.contentId, false);
}

// nomi dei campi
context.workEffortNameField = "Y".equals(context.localeSecondarySet) ?  "workEffortNameLang" : "workEffortName";
context.workEffortTypeDescriptionField = "Y".equals(context.localeSecondarySet) ?  "weTypeDescriptionLang" : "weTypeDescription";
context.workEffortDescriptionField = "Y".equals(context.localeSecondarySet) ? "descriptionLang" : "description";
context.weOrgPartyDescrField = "Y".equals(context.localeSecondarySet) ?  "weOrgPartyDescrLang" : "weOrgPartyDescr";

// valorizzazione in base ai parametri
context.workEffortNameValue = "";
context.weOrgPartyDescrValue = "";
context.weEtchValue = "";

context.workEffortNameDescription = "";
context.orderByField = context.workEffortNameField;
if ("Y".equals(context.showOrgUnit)) {
	context.orderByField = context.weOrgPartyDescrField;
	context.weOrgPartyDescrValue = context.weOrgPartyDescrField;
	context.workEffortNameDescription = "Y".equals(context.localeSecondarySet) ? "@{weOrgPartyDescrLang}" : "@{weOrgPartyDescr}";
	Debug.log("context.showOrgUnit " + context.showOrgUnit);
} else{
	if ("Y".equals(context.showEtch)) {
		context.weEtchValue = "weEtch";
		context.orderByField = context.weEtchValue;
//		context.workEffortNameDescription = "@{weEtch} - ";
		Debug.log("context.showEtch " + context.showEtch);
	}  else if ("C".equals(context.showEtch)){
		context.weEtchValue = "sourceReferenceId";
		context.orderByField = context.weEtchValue;
		context.workEffortNameDescription = "@{sourceReferenceId} -";
		Debug.log("context.showEtch " + context.showEtch);
	}
	context.workEffortNameValue = context.workEffortNameField;
	context.workEffortNameDescription = context.workEffortNameDescription + ("Y".equals(context.localeSecondarySet) ? " @{workEffortNameLang}" : " @{workEffortName}");
	Debug.log("context.workEffortNameValue " + context.workEffortNameValue);
}

def workEffortTypeAssocAndAssocTypeList = delegator.findByAnd("WorkEffortTypeAssocAndAssocType", [workEffortTypeId: workEffortTypeId, wefromWetoEnumId: "WETAFROM", contentId: parameters.contentId, isUnique: "N"]);
def mapKey = [];
for(GenericValue w: workEffortTypeAssocAndAssocTypeList){
	def map = [:];
	map.workEffortAssocTypeId = w.workEffortAssocTypeId;
	map.workEffortTypeIdRef = w.workEffortTypeIdRef;
	mapKey.add(map);
}

def listReturn = [];

if (UtilValidate.isNotEmpty(context.listIt)) {
	for (GenericValue value: context.listIt) {
		def map = [:];
		map.workEffortAssocTypeId = value.workEffortAssocTypeId;
		map.workEffortTypeIdRef = value.workEffortTypeIdTo;
		
	    if(mapKey.contains(map) || value.workEffortAssocTypeId == null){
	    	def mapValue = [:];
			mapValue.putAll(value);
			
			if (UtilValidate.isNotEmpty(value.workEffortIdTo)) {
				def parentFrom = delegator.findOne("WorkEffort", ["workEffortId" : value.workEffortIdTo], false);				
				if (UtilValidate.isNotEmpty(parentFrom)) {
					mapValue.worEffortParentIdFrom = parentFrom.workEffortParentId;
				}
			}
			
			if (UtilValidate.isNotEmpty(value.workEffortIdFrom)) {
				def parentTo = delegator.findOne("WorkEffort", ["workEffortId" : value.workEffortIdFrom], false);				
				if (UtilValidate.isNotEmpty(parentTo)) {
					mapValue.worEffortParentIdTo = parentTo.workEffortParentId;
				}
			}
			
		    listReturn.add(mapValue);
	    }
	}
}
context.listIt = listReturn;
