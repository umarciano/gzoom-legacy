import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

workEffortName = "";
weTypeDescription = "";
description = "";
weStatusDescr = "";
estimatedStartDate = null;
estimatedCompletionDate = null;
weStatusId = "";
processId = "";
noPreviousStatus = "";
context.statusChildModify = "Y";

if (UtilValidate.isNotEmpty(parameters.workEffortIdRoot)) {
	rootWe = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortIdRoot], false);
	
	if (UtilValidate.isNotEmpty(rootWe)) {
		
		//Root WorkEffort
		statusItem = rootWe.getRelatedOneCache("CurrentStatusItem");
		workEffortName = rootWe.workEffortName;
		workEffortNameLang = rootWe.workEffortNameLang;
		description = rootWe.description;
		descriptionLang = rootWe.descriptionLang;
		weStatusDescr = statusItem.description;
		weStatusDescrLang = statusItem.descriptionLang;
		weStatusId = rootWe.currentStatusId;
		estimatedStartDate = rootWe.estimatedStartDate;
		estimatedCompletionDate = rootWe.estimatedCompletionDate;
		
		workEffortType = rootWe.getRelatedOneCache("WorkEffortType");
		weTypeDescription = workEffortType.description;
		weTypeDescriptionLang = workEffortType.descriptionLang;
		processId = rootWe.processId;

		oldWorkEffortId = context.workEffortId;
		
		context.workEffortId = parameters.workEffortIdRoot;
		/**Utilizzato per far visualizzare solo il responsabile*/
		context.onlyResponsible = "Y";
		
		context.currentStatusId = rootWe.currentStatusId;
		context.duplicateAdmit = "N"; // COPY, CLONE, SNAPSHOT
        GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/loadWorkEffortTypeStatusParams.groovy", context);
        parameters.duplicateAdmit = context.duplicateAdmit;
        
        GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkEffortResponsibleByRole.groovy", context);
		
		context.workEffortId = oldWorkEffortId;
	
		context.workEffortName = workEffortName;
		context.workEffortNameLang = workEffortNameLang;
		context.weTypeDescription = weTypeDescription;
		context.weTypeDescriptionLang = weTypeDescriptionLang;
		context.description = description;
		context.descriptionLang = descriptionLang;
		context.weStatusDescr = weStatusDescr;
		context.weStatusDescrLang = weStatusDescrLang;
		context.weStatusId = weStatusId;
		context.processId = processId;
		context.estimatedStartDate = estimatedStartDate;
		context.estimatedCompletionDate = estimatedCompletionDate;
		
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

def weStatusId = "";
def weTypeParentEtch = "";
def weTypeParentEtchLang = "";
def workEffortParentName = "";
def workEffortParentNameLang = "";
def weTypeEtch = "";
def weTypeEtchLang = "";
def workEffortName = "";
def workEffortNameLang = "";
def orgUnit = "";
def orgUnitLang = "";
def workEffortParentId = "";
def workEffortId = context.workEffortId;

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);

if (UtilValidate.isNotEmpty(workEffort)) {
	workEffortName = workEffort.workEffortName;
	workEffortNameLang = workEffort.workEffortNameLang;
	
	def workEffortParent = delegator.findOne("WorkEffort", ["workEffortId": workEffort.workEffortParentId], false); 
	def workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffort.workEffortTypeId], false);
	def partyGroup = delegator.findOne("PartyGroup", ["partyId": workEffort.orgUnitId], false);
	def partyParentRole = delegator.findOne("PartyParentRole", ["partyId": workEffort.orgUnitId, "roleTypeId": "ORGANIZATION_UNIT"], false);
	
	if (UtilValidate.isNotEmpty(workEffortParent)) {
		weStatusId = workEffortParent.currentStatusId;
		workEffortParentId = workEffortParent.workEffortId;
		workEffortParentName = workEffortParent.workEffortName;
		workEffortParentNameLang = workEffortParent.workEffortNameLang;
		
		def workEffortTypeParent = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffortParent.workEffortTypeId], false);
		if (UtilValidate.isNotEmpty(workEffortTypeParent)) {
			weTypeParentEtch = UtilValidate.isNotEmpty(workEffortTypeParent.etch) ? workEffortTypeParent.etch : workEffortTypeParent.description;
			weTypeParentEtchLang = UtilValidate.isNotEmpty(workEffortTypeParent.etchLang) ? workEffortTypeParent.etchLang : workEffortTypeParent.descriptionLang;
		}
	}
	
	if (UtilValidate.isNotEmpty(workEffortType)) {
		weTypeEtch = UtilValidate.isNotEmpty(workEffortType.etch) ? workEffortType.etch : workEffortType.description;
		weTypeEtchLang = UtilValidate.isNotEmpty(workEffortType.etchLang) ? workEffortType.etchLang : workEffortType.descriptionLang;
	}
	
	if (UtilValidate.isNotEmpty(partyParentRole)) {
		orgUnit = partyParentRole.parentRoleCode;
		orgUnitLang = partyParentRole.parentRoleCode;
	}
	if (UtilValidate.isNotEmpty(partyGroup)) {
		orgUnit += " - " + partyGroup.groupName;
		orgUnitLang += " - " + partyGroup.groupNameLang;
	}
}

context.weStatusId = weStatusId;
context.weTypeParentEtch = weTypeParentEtch;
context.weTypeParentEtchLang = weTypeParentEtchLang;
context.workEffortParentName = workEffortParentName;
context.workEffortParentNameLang = workEffortParentNameLang;
context.weTypeEtch = weTypeEtch;
context.weTypeEtchLang = weTypeEtchLang;
context.workEffortName = workEffortName;
context.workEffortNameLang = workEffortNameLang;
context.orgUnit = orgUnit;
context.orgUnitLang = orgUnitLang;
context.workEffortParentId = workEffortParentId;
