import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.*;
import com.mapsengineering.base.birt.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;


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
