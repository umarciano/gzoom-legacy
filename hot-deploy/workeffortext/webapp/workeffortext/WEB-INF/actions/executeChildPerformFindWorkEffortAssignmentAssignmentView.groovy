import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.*;
import com.mapsengineering.base.birt.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.workeffortext.util.LevelUoPartyExtractor;


def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);

def workEffortTypeId = parameters.workEffortTypeId != null ? parameters.workEffortTypeId : null;
if(UtilValidate.isEmpty(workEffortTypeId)){
	if(UtilValidate.isNotEmpty(workEffort)){
		workEffortTypeId = workEffort.workEffortTypeId;
	}
}

GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/populateFolderDateParams.groovy", context);

def workEffortParamView = delegator.findOne("WorkEffortAndTypePeriodAndThruDate", ["workEffortId" : parameters.workEffortId], false);
if(UtilValidate.isNotEmpty(workEffortParamView)){
	if(UtilValidate.isNotEmpty(workEffortParamView.workEffortTypePeriodId)){
		context.defaultThruDate = workEffortParamView.thruDate;
		context.defaultFromDate = workEffortParamView.fromDate;
	}
}

if(UtilValidate.isEmpty(context.defaultThruDate)){
	context.defaultThruDate = workEffort.estimatedCompletionDate;
	context.defaultFromDate = workEffort.estimatedStartDate;
}




context.showRoleTypeWeight = "Y";
context.showRoleTypeWeightActual = "N";
context.showDates = "N";
context.typeEmployment = "";
context.startDate = "BEGIN";
context.usePeriod = "";
context.showComment = "N";
context.detailEnabled = "Y";
context.hideLookUp = "N";
context.arrayNumRows = "0";
context.weTypeSubId = "";
context.weTypeSubFilter = ""; //SAME, PARENT
context.weTypeSubWeId = ""; // work_effort_type_id for endWorkEffortIdWe1, endWorkEffortIdWe2, endWorkEffortIdWe3 - work_effort_type.work_effort_type_id like 'weTypeSubWeId%'

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	def mapParams = paramsEvaluator.evaluateParams(workEffortTypeId, false);
	
	if (UtilValidate.isNotEmpty(mapParams)) {        
        modelEntity = delegator.getModelEntity(context.entityName);
        fieldNames = modelEntity.getAllFieldNames();
        Debug.log("##  executeChildPerformFindWorkEffortAssignmentAssignmentView.groovy params =" + UtilMap.subMap(context, fieldNames));
        def input = UtilMap.subMap(mapParams, fieldNames);
        context.inputFields.putAll(input);
        parameters.putAll(input);
	}
}

//****************** se non ho inserito il roleTypeId di default è = WE_ASSIGNMENT

if (UtilValidate.isEmpty(context.roleTypeId)) {
    context.roleTypeId = "WE_ASSIGNMENT";
    parameters.roleTypeId = "WE_ASSIGNMENT";
    context.inputFields.roleTypeId = "WE_ASSIGNMENT";
}
Debug.log("##  executeChildPerformFindWorkEffortAssignmentAssignmentView.groovy  roleTypeId =" + parameters.roleTypeId);

Debug.log(" First search " + context.entityName + " with condition " + context.inputFields);
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);

if (UtilValidate.isNotEmpty(workEffort) && UtilValidate.isNotEmpty(context.weTypeSubId) && UtilValidate.isNotEmpty(context.weTypeSubFilter)) {
	LevelUoPartyExtractor levelUoPartyExtractor = new LevelUoPartyExtractor(delegator, workEffort.orgUnitId, workEffort.orgUnitRoleTypeId);
	if ("SAME".equals(context.weTypeSubFilter)) {
		levelUoPartyExtractor.initLevelSameUO("Y", "N");
		levelUoPartyExtractor.initLevelParentUO("N", "N");
	} else if ("PARENT".equals(context.weTypeSubFilter)) {
		levelUoPartyExtractor.initLevelSameUO("N", "N");
		levelUoPartyExtractor.initLevelParentUO("Y", "N");		
	}
	levelUoPartyExtractor.initLevelChildUO("N", "N");
	levelUoPartyExtractor.initLevelSisterUO("N", "N");
	levelUoPartyExtractor.initLevelTopUO("N", "N");
	levelUoPartyExtractor.run();
	def orgUnitIdList = levelUoPartyExtractor.getOrgUnitIdList();
    context.orgUnitIdTypeSubList = StringUtil.join(orgUnitIdList, ",");
}
