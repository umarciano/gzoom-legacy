import org.ofbiz.base.util.*;

res = "success";

if ("WorkEffortTypePeriod".equals(parameters.entityName) || "WorkEffortTypePeriodView".equals(parameters.entityName)) {
	res = "workEffortTypePeriodManagementContainerOnly";
}

if ("EvaluationReferentsView".equals(parameters.entityName)) {
	res = "evaluationReferentsManagementContainerOnly";
}

/** bug 4881 serve per eseguire entityOne sul dettaglio **/
GroovyUtil.runScriptAtLocation("component://base/script/com/mapsengineering/base/populateManagement.groovy", context);
		
// GN-359 Caso particolare in quando Obiettivi, Schede Obiettivo e specializzazioni a volte utilizzano la stessa entityName,
// cancellazione di una scheda deve riportare nella ricerca quindi fromDelete = Y
if("Y".equals(parameters.fromDelete) && ("WorkEffortView".equals(parameters.entityName) || "WorkEffortRootView".equals(parameters.entityName))) {
	if(UtilValidate.isNotEmpty(parameters.workEffortIdRoot) && (UtilValidate.isEmpty(parameters.workEffortIdFrom) || parameters.workEffortIdFrom.equals(parameters.workEffortId))) {
		res = "workEffortFromDelete";
	} else if(UtilValidate.isEmpty(parameters.workEffortIdRoot)) {
		res = "workEffortFromDelete";
	}
}

return res;
