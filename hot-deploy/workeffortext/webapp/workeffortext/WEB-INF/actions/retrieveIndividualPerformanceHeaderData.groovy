import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

import java.util.*;

inChargeName = "";
evalManagerPersonName = "";
evalApproverPersonName = "";
inChargeLabel = "";
evalManagerPersonLabel = "";
evalApproverPersonLabel = "";
emplPositionTypeDescription = "";
evalManagerName = "";
weStatusDescr = "";
estimatedStartDate = null; 
estimatedCompletionDate = null;
//membershipOrgName = "";
orgUnitName = "";
orgUnitNameLang = "";
weStatusId = "";
processId = "";
noPreviousStatus = "";
evalManagerLabel = context.uiLabelMap["HeaderReferentName"];

if (UtilValidate.isNotEmpty(parameters.workEffortIdRoot)) {
    rootWe = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortIdRoot], false);
    if (UtilValidate.isNotEmpty(rootWe)) {
    	nowStamp = UtilDateTime.nowTimestamp();
    	
    	//Root WorkEffort
    	statusItem = rootWe.getRelatedOneCache("CurrentStatusItem");
    	weStatusDescr = statusItem.description;
    	weStatusDescrLang = statusItem.descriptionLang;
    	weStatusId = rootWe.currentStatusId;
    	estimatedStartDate = rootWe.estimatedStartDate;
    	estimatedCompletionDate = rootWe.estimatedCompletionDate;
    	processId = rootWe.processId;
    
    	context.currentStatusId = rootWe.currentStatusId;
    	context.duplicateAdmit = "N"; // COPY, CLONE, SNAPSHOT
    	GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/loadWorkEffortTypeStatusParams.groovy", context);
    	parameters.duplicateAdmit = context.duplicateAdmit;
    	
    	//Valutato	
    	assignments = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(["workEffortId": parameters.workEffortIdRoot, "roleTypeId": "WEM_EVAL_IN_CHARGE"]), null, null, null, false);	
    	if (UtilValidate.isNotEmpty(assignments)) {
    		inChargePartyId = assignments[0].partyId;

    		inChargePerson = delegator.findOne("PartyAndPerson", ["partyId": inChargePartyId], false);
    		if (UtilValidate.isNotEmpty(inChargePerson)) {
    			inChargeName = inChargePerson.partyName;
    			emplPositionTypeId = inChargePerson.emplPositionTypeId;
    			if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
    				emplPositionType = delegator.findOne("EmplPositionType", ["emplPositionTypeId": emplPositionTypeId], false);
    				if (UtilValidate.isNotEmpty(emplPositionType))
    					emplPositionTypeDescription = emplPositionType.description;
    			}
    		}
    		
    		if(UtilValidate.isNotEmpty(rootWe.orgUnitId)) {
    			orgUnit = delegator.findOne("Party", ["partyId": rootWe.orgUnitId], true);
    			if(UtilValidate.isNotEmpty(orgUnit)) {
    				orgUnitName = orgUnit.partyName;
    				orgUnitNameLang = orgUnit.partyNameLang;
    			}
    		}
    		
    		//Valutatore Campo specializzato per le Schede Individuali
    		conditionList = new ArrayList();
    		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowStamp));
    		conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, parameters.workEffortIdRoot));
    		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER"));
    		assignmentRoleList = delegator.findList("WorkEffortAssignmentRoleView", EntityCondition.makeCondition(conditionList), null, null, null, true);
    		if (UtilValidate.isNotEmpty(assignmentRoleList)) {
    			evalManagerId = assignmentRoleList[0].partyId;
    			evalManagerPerson = delegator.findOne("PartyAndPerson", ["partyId": evalManagerId], true);
    			if (UtilValidate.isNotEmpty(evalManagerPerson)) {
    				evalManagerName = evalManagerPerson.partyName;
    			}
    		}
    	}
    	
		context.backStatusId = "";
		def retrieveBackStatusList = true;
		def hasPermissionBackStatus = true;
		def permission = "";
		def weContextId = UtilValidate.isNotEmpty(parameters.weContextId) ? parameters.weContextId : context.weContextId;
		if (UtilValidate.isNotEmpty(weContextId)) {
			permission = ContextPermissionPrefixEnum.getPermissionPrefix(weContextId);
		}
		if (UtilValidate.isEmpty(permission)) {
			permission = "WORKEFFORT";
		}
		if (! security.hasPermission(permission + "MGR_ADMIN", userLogin)) {
			def workEffortTypeStatusParamsEvaluator = new WorkEffortTypeStatusParamsEvaluator(context, delegator);
			def paramsMap = workEffortTypeStatusParamsEvaluator.evaluateParams(rootWe.workEffortTypeId, rootWe.currentStatusId, false);
			if (UtilValidate.isNotEmpty(paramsMap) && UtilValidate.isNotEmpty(paramsMap.noPreviousStatus)) {
				noPreviousStatus = paramsMap.noPreviousStatus;
			}
			if ("Y".equals(noPreviousStatus)) {
				hasPermissionBackStatus = false;
			}
		}
		if (hasPermissionBackStatus) {
			if (UtilValidate.isNotEmpty(rootWe.workEffortTypePeriodId)) {
				def workEffortTypePeriod = delegator.findOne("WorkEffortTypePeriod", ["workEffortTypePeriodId": rootWe.workEffortTypePeriodId], false);
				if (UtilValidate.isNotEmpty(workEffortTypePeriod)) {
					retrieveBackStatusList = ("OPEN".equals(workEffortTypePeriod.statusEnumId) || "REOPEN".equals(workEffortTypePeriod.statusEnumId));
				}
			}
			if (retrieveBackStatusList) {
				def conditionList = [];
				conditionList.add(EntityCondition.makeCondition("workEffortId", parameters.workEffortIdRoot));
				conditionList.add(EntityCondition.makeCondition("statusIdTo", weStatusId));
				def statusList = delegator.findList("WorkEffortStatusValidChange", EntityCondition.makeCondition(conditionList), null, ["-statusDatetime"], null, false);
				def listItem = EntityUtil.getFirst(statusList);
				if (UtilValidate.isNotEmpty(listItem)) {
				    context.backStatusId = listItem.statusId;
				} else {
					def condition = EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, parameters.workEffortIdRoot),
					  EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, weStatusId), EntityCondition.makeCondition("sequenceId", EntityOperator.LESS_THAN, statusItem.sequenceId),
					  EntityCondition.makeCondition("statusTypeId", statusItem.statusTypeId)]);					
					def backStatusList = delegator.findList("WorkEffortStatusAndItemView", condition, null, ["-statusDatetime"], null, false);
					def backStatusItem = EntityUtil.getFirst(backStatusList);
					if (UtilValidate.isNotEmpty(backStatusItem)) {
					    context.backStatusId = backStatusItem.statusId;
					}
				}
			}			
		}
		
		//etichetta valutato
		inChargeLabel = getRoleLabel(delegator, "WEM_EVAL_IN_CHARGE");

		//dati relativi al valutatore prevalente
		evalApproverPersonName = getPersonByRole(delegator, parameters.workEffortIdRoot, "WEM_EVAL_APPROVER");
    	if (UtilValidate.isNotEmpty(evalApproverPersonName)) {
    		evalApproverPersonLabel = getRoleLabel(delegator, "WEM_EVAL_APPROVER");
    	}
    	
		//dati relativi al referente valutazione
    	evalManagerPersonName = getPersonByRole(delegator, parameters.workEffortIdRoot, "WEM_EVAL_MANAGER");
    	if (UtilValidate.isNotEmpty(evalManagerPersonName)) {
    		evalManagerPersonLabel = getRoleLabel(delegator, "WEM_EVAL_MANAGER");
    	}
    	
		def parentRel = "";
		def parentRelWe = "";
		context.showParentAssoc = "N";
		WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	    paramsEvaluator.evaluateParams(rootWe.workEffortTypeId, "WEFLD_MAIN", false);
	    if ("Y".equals(context.showParentAssoc)) {
	    	def workEffortTypeAssocCondList = [];
	    	workEffortTypeAssocCondList.add(EntityCondition.makeCondition("workEffortTypeId", rootWe.workEffortTypeId));
	    	workEffortTypeAssocCondList.add(EntityCondition.makeCondition("isParentRel", "Y"));
	    	def workEffortTypeAssocList = delegator.findList("WorkEffortTypeAssoc", EntityCondition.makeCondition(workEffortTypeAssocCondList), null, null, null, false);
	    	def workEffortTypeAssoc = EntityUtil.getFirst(workEffortTypeAssocList);
	    	if (UtilValidate.isNotEmpty(workEffortTypeAssoc)) {
	    		parentRel = "Y".equals(context.localeSecondarySet) ? workEffortTypeAssoc.commentsLang : workEffortTypeAssoc.comments;
	    		def workEffortAssocAndParentViewCondList = [];
	    		workEffortAssocAndParentViewCondList.add(EntityCondition.makeCondition("workEffortIdFrom", rootWe.workEffortId));
	    		workEffortAssocAndParentViewCondList.add(EntityCondition.makeCondition("workEffortAssocTypeId", workEffortTypeAssoc.workEffortAssocTypeId));
	    		def workEffortAssocAndParentViewList = delegator.findList("WorkEffortAssocAndParentView", EntityCondition.makeCondition(workEffortAssocAndParentViewCondList), null, null, null, false);
		    	def workEffortAssocAndParentView = EntityUtil.getFirst(workEffortAssocAndParentViewList);
		    	if (UtilValidate.isNotEmpty(workEffortAssocAndParentView)) {
		    		def weName = "Y".equals(context.localeSecondarySet) ? workEffortAssocAndParentView.wrToNameLang : workEffortAssocAndParentView.wrToName;
		    		parentRelWe = UtilValidate.isNotEmpty(workEffortAssocAndParentView.wrToEtch) ? workEffortAssocAndParentView.wrToEtch + " - " + weName : weName;
		    	} else {
		    		workEffortAssocAndParentViewCondList = [];
		    		workEffortAssocAndParentViewCondList.add(EntityCondition.makeCondition("workEffortIdTo", rootWe.workEffortId));
		    		workEffortAssocAndParentViewCondList.add(EntityCondition.makeCondition("workEffortAssocTypeId", workEffortTypeAssoc.workEffortAssocTypeId));
		    		workEffortAssocAndParentViewList = delegator.findList("WorkEffortAssocAndParentView", EntityCondition.makeCondition(workEffortAssocAndParentViewCondList), null, null, null, false);
			    	workEffortAssocAndParentView = EntityUtil.getFirst(workEffortAssocAndParentViewList);
			    	if (UtilValidate.isNotEmpty(workEffortAssocAndParentView)) {
			    		def weName = "Y".equals(context.localeSecondarySet) ? workEffortAssocAndParentView.wrFromNameLang : workEffortAssocAndParentView.wrFromName;
			    		parentRelWe = UtilValidate.isNotEmpty(workEffortAssocAndParentView.wrFromEtch) ? workEffortAssocAndParentView.wrFromEtch + " - " + weName : weName;
			    	}
		    	}
	    	}
	    }
		
		context.parentRel = parentRel;
		context.parentRelWe = parentRelWe;
    }
}

context.inChargeName = inChargeName;
context.evalManagerPersonName = evalManagerPersonName;
context.evalApproverPersonName = evalApproverPersonName;
context.inChargeLabel = inChargeLabel;
context.evalManagerPersonLabel = evalManagerPersonLabel;
context.evalApproverPersonLabel = evalApproverPersonLabel;
context.emplPositionTypeDescription = emplPositionTypeDescription;
context.evalManagerName = evalManagerName;
context.weStatusDescr = weStatusDescr;
context.weStatusDescrLang = weStatusDescrLang;
context.weStatusId = weStatusId;
context.processId = processId; 
//context.membershipOrgName = membershipOrgName;
context.orgUnitName = orgUnitName;
context.orgUnitNameLang = orgUnitNameLang;
context.estimatedStartDate = estimatedStartDate;
context.estimatedCompletionDate = estimatedCompletionDate;
context.evalManagerLabel = evalManagerLabel;

def getPersonByRole(delegator, workEffortIdRoot, role) {
	def wepaList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(["workEffortId": workEffortIdRoot, "roleTypeId": role]), null, null, null, false);
	def wepa = EntityUtil.getFirst(wepaList);
	if (UtilValidate.isNotEmpty(wepa)) {
		def person = delegator.findOne("PartyAndPerson", ["partyId": wepa.partyId], false);
		if (UtilValidate.isNotEmpty(person)) {
			return person.partyName;
		}
	}
	return "";
}

def getRoleLabel(delegator, role) {
	def roleType = delegator.findOne("RoleType", ["roleTypeId": role], false);
	if (UtilValidate.isNotEmpty(roleType)) {
		return "Y".equals(context.localeSecondarySet) ? roleType.descriptionLang : roleType.description;
	}
	return "";
}
