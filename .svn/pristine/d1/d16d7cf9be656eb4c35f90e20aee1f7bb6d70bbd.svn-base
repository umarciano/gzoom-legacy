import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

Debug.log("************************* getWorkEffortResponsibleByRole.groovy -> context.workEffort = " + context.workEffort);
Debug.log("************************* getWorkEffortResponsibleByRole.groovy -> context.workEffortId = " + context.workEffortId);
def workEffortId = context.workEffortId;
if ("Y".equals(parameters.insertMode)) {
	workEffortId = parameters.workEffortIdFrom;
}

def assignments = null;
def workEffort = context.workEffort != null ? context.workEffort : parameters.workEffort;
def weContextId = null;
def onlyResponsible = (UtilValidate.isNotEmpty(parameters.onlyResponsible)) ? parameters.onlyResponsible : context.onlyResponsible;

if (UtilValidate.isNotEmpty(workEffort) || UtilValidate.isNotEmpty(workEffortId)) {
	if (UtilValidate.isEmpty(workEffort)) {
		workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);
	} 
	
	def searchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);
	
	// def nowStamp = UtilDateTime.nowTimestamp();
	def refDate = UtilValidate.isNotEmpty(searchDate) ? searchDate : workEffort.estimatedCompletionDate;
	def fromDateCondition = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, refDate);
	def thruDateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, refDate));
	def dateCondition = EntityCondition.makeCondition(fromDateCondition, EntityOperator.AND, thruDateCondition);

	//Bug 3818
	if(UtilValidate.isNotEmpty(workEffort) && UtilValidate.isNotEmpty(workEffort.orgUnitId)) {
		def conditionList = [];
		conditionList.add(dateCondition);
		conditionList.add(EntityCondition.makeCondition(["partyIdFrom": workEffort.orgUnitId]));
				
		def responsibleConditionList = [];
		responsibleConditionList.addAll(conditionList)
		responsibleConditionList.add(EntityCondition.makeCondition(["partyRelationshipTypeId": "ORG_RESPONSIBLE"]));
		Debug.log(" Searc Resp and/or Deleg with condition = " + responsibleConditionList);
		assignments = delegator.findList("PartyRoleViewRelationship", EntityCondition.makeCondition(responsibleConditionList), null, null, null, true);
		if("Y" == parameters.respList){
			context.responsibileList = assignments;
		}
		if (UtilValidate.isEmpty(assignments) || "Y" == parameters.delList) {
			def delegateConditionList = [];
			delegateConditionList.addAll(conditionList)
			delegateConditionList.add(EntityCondition.makeCondition(["partyRelationshipTypeId": "ORG_DELEGATE"]))
			assignments = delegator.findList("PartyRoleViewRelationship", EntityCondition.makeCondition(delegateConditionList), null, null, null, true);
			if("Y" == parameters.delList){
				context.delegatoList = assignments;
			}
		}
		
		if (UtilValidate.isNotEmpty(assignments[0])) {
			
				if (UtilValidate.isNotEmpty(onlyResponsible) && onlyResponsible == 'Y' && assignments[0].partyRelationshipTypeId != "ORG_RESPONSIBLE") {
					Debug.log(" - Non ci sono responsabili " );
					return;
				}
				
				context.responsible = assignments[0].partyName;
				context.responsibleId = assignments[0].partyIdTo;
		}
	}
}
Debug.log("************************* getWorkEffortResponsibleByRole.groovy -> context.responsibleId = " + context.responsibleId);
