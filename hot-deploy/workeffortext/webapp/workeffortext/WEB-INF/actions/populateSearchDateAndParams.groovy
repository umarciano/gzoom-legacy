import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

def workEffortId = "";
if ("Y".equals(parameters.isObiettivo)) {
	workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : context.workEffortId;
} else {
	workEffortId = UtilValidate.isNotEmpty(parameters.workEffortIdRoot) ? parameters.workEffortIdRoot : context.workEffortIdRoot;
}

def workEffortTypeId = "";
def workEffortTypePeriodId = "";

if(UtilValidate.isNotEmpty(workEffortId)) {
	def workEffortParamView = delegator.findOne("WorkEffortAndTypePeriodAndThruDate", ["workEffortId" : workEffortId], true);
	if(UtilValidate.isNotEmpty(workEffortParamView)) {
		workEffortTypeId = workEffortParamView.workEffortTypeId;
		if(UtilValidate.isNotEmpty(workEffortParamView.workEffortTypePeriodId)) {
			workEffortTypePeriodId = workEffortParamView.workEffortTypePeriodId;
			parameters.searchDate = UtilDateTime.toDateString(workEffortParamView.thruDate, locale);
		}
	}
}

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
    /** Recupero params */
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(workEffortTypeId, parameters.contentId, false);
}
