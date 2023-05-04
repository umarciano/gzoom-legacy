import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

Debug.log("*** checkWefldOthersScoreParams.groovy ***" );

def workEffortTypeId = parameters.workEffortTypeId != null ? parameters.workEffortTypeId : null;

if(UtilValidate.isEmpty(workEffortTypeId)){
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffort)){
		workEffortTypeId = workEffort.workEffortTypeId;
	}
}

parameters.contentId = UtilValidate.isNotEmpty(parameters.contentId) ? parameters.contentId : "WEFLD_OTHER";

context.showNotWeight = "Y";
context.showKpiWeight = "Y";
context.showDetailWeight = "Y";
context.showAssocWeight = "Y"; 


Debug.log("*** showKpiWeight " + context.showKpiWeight);

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
	/** Recupero params */
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(workEffortTypeId, parameters.contentId, false);
}
//Debug.log("*** checkWefldOthersScoreParams showKpiWeight " + context.showKpiWeight);
