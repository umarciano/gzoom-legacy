import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;


context.showActualDates = "N";
context.showScheduledDates = "N";
context.etchDescr  = "";
context.fixedDatesHumanResource = "N";
context.showEtch = "Y";
context.showCode = "Y";
context.showEtchField = "Y";
context.onlyRefDate = "N";
context.showRoleType = "Y";
context.showType = "Y";
context.usePeriod = "";

/** Recupero params per il folder principale */
def workEffortTypeId = "";
if (! "Y".equals(parameters.insertMode)) {
	workEffortTypeId = UtilValidate.isNotEmpty(context.workEffortTypeId) ? context.workEffortTypeId : parameters.workEffortTypeId;
}
if (UtilValidate.isNotEmpty(workEffortTypeId)) {
    WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
    paramsEvaluator.evaluateParams(workEffortTypeId, "WEFLD_MAIN", false);
    if (UtilValidate.isNotEmpty(context.isInsertMode) && context.isInsertMode) {
    	context.showType = "Y";
    }
}
