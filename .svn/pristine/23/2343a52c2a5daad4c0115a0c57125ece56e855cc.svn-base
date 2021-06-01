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

/** Recupero params per il folder principale */
def workEffortTypeId = UtilValidate.isNotEmpty(context.workEffortTypeId) ? context.workEffortTypeId : parameters.workEffortTypeId;

if (UtilValidate.isNotEmpty(workEffortTypeId)) {
    WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
    paramsEvaluator.evaluateParams(workEffortTypeId, "WEFLD_MAIN", false);    
}
