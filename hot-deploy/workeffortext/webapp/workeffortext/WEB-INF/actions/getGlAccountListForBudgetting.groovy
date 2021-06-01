import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import com.mapsengineering.base.menu.MenuHelper;

// Centro di Responsabilita'
def respCenterId = parameters.cdcResponsibleId;
// Centro di Costo
def partyId = parameters.partyId;

def workEffortTypeId = parameters.workEffortTypeId;
def debitCreditFlag = parameters.debitCreditFlag;
def glAccountList = [];
def workEffortList = [];

//Ottengo la lista dei capitoli

def conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyIdTo", context.userLogin.partyId));
conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, ["ORG_RESPONSIBLE", "ORG_DELEGATE"]));
conditionList.add(EntityCondition.makeCondition("thruDate", null));
conditionList.add(EntityCondition.makeCondition("partyIdFrom", partyId));
def partyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList), null, null, null, false);
Debug.log("Find PartyRelationship with " + conditionList);
if(UtilValidate.isNotEmpty(partyRelList)){
	for(partyRel in partyRelList) {
	    def condList = [];
	    condList.add(EntityCondition.makeCondition("partyId", partyId));
	    condList.add(EntityCondition.makeCondition("roleTypeId", partyRel.roleTypeIdFrom));
	    condList.add(EntityCondition.makeCondition("accountTypeEnumId", "FINANCIAL"));
	    condList.add(EntityCondition.makeCondition("debitCreditDefault", debitCreditFlag));
		Debug.log("Find GlAccountAndRoleView with " + condList);
		def glAccountRoleList = delegator.findList("GlAccountAndRoleView", EntityCondition.makeCondition(condList), null, null, null, false);	
		for(glAccountRole in glAccountRoleList) {
	        def glAccount = delegator.findOne("GlAccount", ["glAccountId": glAccountRole.glAccountId], false);
	        if(!glAccountList.contains(glAccount)) {
	            glAccountList.add(glAccount);
	        }
	    }
	}
} else {
	// Caso dove abbiamo relazione con Centro di Responsabili e non direttamente con CDC
	def partyRelationListCond = [];
	partyRelationListCond.add(EntityCondition.makeCondition("partyIdTo", context.userLogin.partyId));
	partyRelationListCond.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, ["ORG_RESPONSIBLE", "ORG_DELEGATE"]));
	partyRelationListCond.add(EntityCondition.makeCondition("thruDate", null));
	partyRelationListCond.add(EntityCondition.makeCondition("partyIdFrom", respCenterId));
	Debug.log("Find PartyRelationship with " + partyRelationListCond);
	def partyRelationList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(partyRelationListCond), null, null, null, false);
	for(partyRel in partyRelationList) {		
		def condList = [];
		condList.add(EntityCondition.makeCondition("respCenterId", respCenterId));
		condList.add(EntityCondition.makeCondition("partyId", partyId));		
		condList.add(EntityCondition.makeCondition("accountTypeEnumId", "FINANCIAL"));
		condList.add(EntityCondition.makeCondition("debitCreditDefault", debitCreditFlag));
		Debug.log("Find GlAccountAndRoleView with " + condList);
		def glAccountRoleList = delegator.findList("GlAccountAndRoleView", EntityCondition.makeCondition(condList), null, null, null, false);
		for(glAccountRole in glAccountRoleList) {
			def glAccount = delegator.findOne("GlAccount", ["glAccountId": glAccountRole.glAccountId], false);
			if(!glAccountList.contains(glAccount)) {
				glAccountList.add(glAccount);
			}
		}
	}		
}

//Ottengo la lista degli obiettivi
def workConditon = [];
workConditon.add(EntityCondition.makeCondition("rootTypeId", workEffortTypeId));
workConditon.add(EntityCondition.makeCondition("orgUnitId", partyId));
workConditon.add(EntityCondition.makeCondition("workEffortSnapshotId", null));
workEffortList = delegator.findList("WorkEffortForBudgetView", EntityCondition.makeCondition(workConditon), null, ["sourceReferenceId"], null, true);

def workEffortPeriodTypeCond = [];
workEffortPeriodTypeCond.add(EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId));
workEffortPeriodTypeCond.add(EntityCondition.makeCondition("glFiscalTypeEnumId", "GLFISCTYPE_TARGETFIN"));
workEffortPeriodTypeCond.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
workEffortPeriodTypeCond.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("statusEnumId", "DETECTABLE"),
        EntityCondition.makeCondition("statusEnumId", "OPEN"), EntityCondition.makeCondition("statusEnumId", "REOPEN"), EntityCondition.makeCondition("statusEnumId", "CLOSE")));
def workEffortTypePeriod =  EntityUtil.getFirst(delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(workEffortPeriodTypeCond), null, null, null, false));
def customTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": workEffortTypePeriod.customTimePeriodId], false);

//La data di rilevazione deve stare in mezzo a quelle dei capitoli
def nullDateCond = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", null), EntityCondition.makeCondition("thruDate", null));
def betweenDateCond = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, customTimePeriod.thruDate), EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, customTimePeriod.fromDate));
def dateCond = EntityCondition.makeCondition(nullDateCond, EntityOperator.OR, betweenDateCond);
glAccountList = EntityUtil.filterByCondition(glAccountList, dateCond);

//I capitoli da selezionare devono avere sulla tabella associata WORK_EFFORT_PURPOSE_ACCOUNT la finalita
def wType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffortTypeId], false);
def glAccountIt = glAccountList.iterator();
while (glAccountIt.hasNext()) {
    def glAccount = glAccountIt.next();
    def workEffortPurposeAccount = delegator.findOne("WorkEffortPurposeAccount", ["workEffortPurposeTypeId": wType.wePurposeTypeIdRes, "glAccountId": glAccount.glAccountId], false);
    if (UtilValidate.isEmpty(workEffortPurposeAccount)) {
        glAccountIt.remove();
    }
}

glAccountList = EntityUtil.orderBy(glAccountList, ["accountCode"]);

context.glAccountList = glAccountList;
context.workEffortList = workEffortList;
context.workEffortTypePeriod = workEffortTypePeriod;