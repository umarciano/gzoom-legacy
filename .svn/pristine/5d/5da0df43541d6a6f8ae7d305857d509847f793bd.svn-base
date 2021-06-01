import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;

def workEffortIdRoot = context.inputFields.workEffortIdRoot;
def orgUnitRoleTypeId = context.inputFields.orgUnitRoleTypeId;
def orgUnitId = context.inputFields.orgUnitId;
def oldOrgUnitId = context.inputFields.oldOrgUnitId;
def estimatedStartDate = context.inputFields.estimatedStartDate;
def oldEstimatedStartDate = context.inputFields.oldEstimatedStartDate;
def estimatedCompletionDate = context.inputFields.estimatedCompletionDate;
def oldEstimatedCompletionDate = context.inputFields.oldEstimatedCompletionDate;

def updateOrgUnitId = UtilValidate.isNotEmpty(orgUnitId) && ! orgUnitId.equals(oldOrgUnitId);
def updateEstimatedStartDate = UtilValidate.isNotEmpty(estimatedStartDate) && ! estimatedStartDate.equals(oldEstimatedStartDate);
def updateEstimatedCompletionDate = UtilValidate.isNotEmpty(estimatedCompletionDate) && ! estimatedCompletionDate.equals(oldEstimatedCompletionDate);

def workEffortConditionList = [];
workEffortConditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_EQUAL, workEffortIdRoot));
workEffortConditionList.add(EntityCondition.makeCondition("workEffortParentId", workEffortIdRoot));
workEffortConditionList.add(EntityCondition.makeCondition("workEffortRevisionId", null));

def orConditionList = [];
orConditionList.add(EntityCondition.makeCondition("orgUnitId", oldOrgUnitId));
orConditionList.add(EntityCondition.makeCondition("estimatedStartDate", oldEstimatedStartDate));
orConditionList.add(EntityCondition.makeCondition("estimatedCompletionDate", oldEstimatedCompletionDate));

workEffortConditionList.add(EntityCondition.makeCondition(orConditionList, EntityOperator.OR));

def toUpdateByCompletionDateList = [];
def workEffortList = delegator.findList("WorkEffort", EntityCondition.makeCondition(workEffortConditionList), null, null, null, false);
if (UtilValidate.isNotEmpty(workEffortList)) {
	for (GenericValue workEffort : workEffortList) {
		if (UtilValidate.isNotEmpty(workEffort)) {
			def toStore = false;
			if (updateOrgUnitId && UtilValidate.isNotEmpty(workEffort.orgUnitId) && workEffort.orgUnitId.equals(oldOrgUnitId)) {
				toStore = true;
				workEffort.orgUnitRoleTypeId = orgUnitRoleTypeId;
				workEffort.orgUnitId = orgUnitId;
			}						
			def oldWeEstimatedStartDate = workEffort.estimatedStartDate;
			if (updateEstimatedStartDate && UtilValidate.isNotEmpty(oldWeEstimatedStartDate) && oldWeEstimatedStartDate.equals(oldEstimatedStartDate)) {
				toStore = true;
				workEffort.estimatedStartDate = estimatedStartDate;
			}			
			def oldWeEstimatedCompletionDate = workEffort.estimatedCompletionDate;
			if (updateEstimatedCompletionDate && UtilValidate.isNotEmpty(oldWeEstimatedCompletionDate) && oldWeEstimatedCompletionDate.equals(oldEstimatedCompletionDate)) {
				toStore = true;
				workEffort.estimatedCompletionDate = estimatedCompletionDate;
			}
			if (toStore) {
				delegator.store(workEffort);
			}
			if ((updateEstimatedStartDate && UtilValidate.isNotEmpty(oldWeEstimatedStartDate) && oldWeEstimatedStartDate.equals(oldEstimatedStartDate)) || (updateEstimatedCompletionDate && UtilValidate.isNotEmpty(oldWeEstimatedCompletionDate) && oldWeEstimatedCompletionDate.equals(oldEstimatedCompletionDate))) {
				toUpdateByCompletionDateList.add(workEffort);
			}			
		}
	}
}

context.toUpdateByCompletionDateList = toUpdateByCompletionDateList;
