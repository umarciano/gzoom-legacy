import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

def parametersCrud = parameters;
def parameters = parameters.parameters;
def workEffortMeasureId = parameters.workEffortMeasureId; 

def isValidWarning = false; // the warning is only for orginal workEffort
def failMessage = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale).IndicatorUsedInWorkEffortList + "<br><br>";
if(UtilValidate.isNotEmpty(workEffortMeasureId) && "CREATE".equals(parametersCrud.operation) && !"Y".equals(parameters.isObiettivo)) {
	def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": workEffortMeasureId], false);
	if(UtilValidate.isNotEmpty(workEffortMeasure.glAccountId)) {
		def glAccount = delegator.findOne("GlAccount", ["glAccountId": workEffortMeasure.glAccountId], false);
		Debug.log(" - checkIndicatorUsedglAccount.inputEnumId " + glAccount.inputEnumId);
		if(!"ACCINP_OBJ".equals(glAccount.inputEnumId) && "ACCDET_NULL".equals(glAccount.detailEnumId)) {
			def conditionList = [];
			conditionList.add(EntityCondition.makeCondition("glAccountId", workEffortMeasure.glAccountId));
			if("ACCINP_PRD".equals(glAccount.inputEnumId)) {
				conditionList.add(EntityCondition.makeCondition("productId", workEffortMeasure.productId));
			}
			
			if(UtilValidate.isNotEmpty(workEffortMeasure.fromDate)) {
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, workEffortMeasure.fromDate));
			}
			if(UtilValidate.isNotEmpty(workEffortMeasure.thruDate)) {
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, workEffortMeasure.thruDate));
			}
			conditionList.add(EntityCondition.makeCondition("workEffortMeasureId", EntityOperator.NOT_EQUAL, workEffortMeasureId));
			def workEffortMeasureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(conditionList), null, null, null, false);
			if(UtilValidate.isNotEmpty(workEffortMeasureList)) {
				for(item in workEffortMeasureList) {
					Debug.log(" - checkIndicatorUsed item.workEffortMeasureId = " + item.workEffortMeasureId);
					def workEffort = delegator.findOne("WorkEffort", ["workEffortId": item.workEffortId], false);
					if(UtilValidate.isNotEmpty(workEffort)) {
						if(UtilValidate.isEmpty(workEffort.workEffortSnapshotId)) {
							isValidWarning = true;
							failMessage += workEffort.sourceReferenceId + " - "  + workEffort.workEffortName + "<br>";
						}
					}
				}
			}
		}
	}
}

if(isValidWarning) {
	return ServiceUtil.returnFailure(failMessage);
}