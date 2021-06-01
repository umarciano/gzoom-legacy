import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;

/** Recupero params del workEffortTypeStatus.params 
 * il campo e' utilizzato per contenere i possibil i valori del duplicateAdmit default = "N", possibili valori = COPY, CLONE, SNAPSHOT
 * e il nextLevelAtOpen
 * e noPreviousStatus = Y, N
 * 
 * duplicateAdmit = "N"; // COPY, CLONE, SNAPSHOT
 * nextLevelAtOpen = "N"; // Y, gestito in checkWorkEffortTypeStatus.groovy
 * noPreviousStatus Y, N
 * */
def workEffortTypeId = UtilValidate.isNotEmpty(context.workEffortTypeId) ? context.workEffortTypeId : parameters.workEffortTypeId;
def currentStatusId = UtilValidate.isNotEmpty(context.currentStatusId) ? context.currentStatusId : parameters.currentStatusId;
WorkEffortTypeStatusParamsEvaluator paramsStatusEvaluator = new WorkEffortTypeStatusParamsEvaluator(context, delegator);
paramsStatusEvaluator.evaluateParams(workEffortTypeId, currentStatusId, true);

