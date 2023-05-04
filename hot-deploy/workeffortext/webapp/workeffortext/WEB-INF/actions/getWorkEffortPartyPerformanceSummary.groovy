import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import com.mapsengineering.base.util.*;
import com.mapsengineering.base.util.paginator.*;

import java.math.*;

workEffortIdRoot = parameters.workEffortIdRoot;
oldWorkEffortId = parameters.workEffortId;
oldWorkEffortTypeId = parameters.workEffortTypeId;

localeSecondarySet = context.localeSecondarySet;

if ("Y".equals(context.calendarEvent)) {
	parameters.entityName = "WorkEffortRootInqyPartyWithEventSummaryView"
	context.entityNamePrefix = "WorkEffortRootInqyPartyWithEventSummary";
	
	//Se arrivaz il parametro workEffortIdRoot vuole dire che ho gia l'id con cui associare e mi servirà
	//per la query di inquiry per recuperare gli eventi associati
	if (UtilValidate.isNotEmpty(parameters.workEffortIdRoot)) {
		workEffortIdRoot = parameters.workEffortIdRoot;
		parameters.workEffortId = parameters.workEffortIdRoot;
	}
	
	if (UtilValidate.isNotEmpty(parameters.workEffortTypeId)) {
		parameters.remove("workEffortTypeId");
	}
} else {
	parameters.entityName = parameters.entityName!= null ? parameters.entityName : "WorkEffortRootInqyPartySummaryView"
	context.entityNamePrefix = context.entityNamePrefix != null ? context.entityNamePrefix : "WorkEffortRootInqyPartySummary";
}
parameters.orderBy = "Y".equals(localeSecondarySet) ? "partyNameLang|partyNameInCharge|estimatedStartDate|currentStatusId|weTypeEtchLang" : "partyName|partyNameInCharge|estimatedStartDate|currentStatusId|weTypeEtch";

def weContextId = UtilValidate.isNotEmpty(parameters.weContextId) ? parameters.weContextId : parameters.weContextId_value;
if ("CTX_EP".equals(weContextId)) {
	res = GroovyUtil.runScriptAtLocation("com/mapsengineering/emplperf/executePerformFindEPWorkEffortRootInqy.groovy", context);	
} else {
	res = GroovyUtil.runScriptAtLocation("com/mapsengineering/dirigperf/executePerformFindDIWorkEffortRootInqy.groovy", context);	
}

//gn2438 faccio in modo che vengano estratti solo i workEffort con lo stato uguale a uno degli stati definiti per il portale
if (UtilValidate.isNotEmpty(context.listIt)) {
	def portalTypeId = getPortalTypeId(weContextId);
	def statusCondList = [];
	statusCondList.add(EntityCondition.makeCondition("portalTypeId", portalTypeId));
	statusCondList.add(EntityCondition.makeCondition("actStEnumId", "ACTSTATUS_ACTIVE"));

	def fieldsToSelect = UtilMisc.toSet("statusId", "statusTypeId", "sequenceId");
	def statusList = delegator.findList("StatusItemAndTypeView", EntityCondition.makeCondition(statusCondList), fieldsToSelect, null, null, false);
	
	def statusIdList = EntityUtil.getFieldListFromEntityList(statusList, "statusId", true);
	if (UtilValidate.isNotEmpty(statusIdList)) {
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(
			EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, statusIdList)
		));
	} else {
		context.listIt = [];
	}
}

if (UtilValidate.isNotEmpty(context.listIt)) {
	
	
	/**
	 * Filtro la lista per il campo weActivation != ACTSTATUS_REPLACED e da ACTSTATUS_CLOSED
	 */
	context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(
			EntityCondition.makeCondition("weActivation", EntityOperator.NOT_EQUAL, "ACTSTATUS_CLOSED"),
			EntityCondition.makeCondition("weActivation", EntityOperator.NOT_EQUAL, "ACTSTATUS_REPLACED")
			));
	
	
	workEffortPartyPerformanceSummaryList = context.listIt;
	
	if (UtilValidate.isEmpty(workEffortIdRoot) && UtilValidate.isNotEmpty(workEffortPartyPerformanceSummaryList) && workEffortPartyPerformanceSummaryList.size() > 0) {
		workEffortIdRoot = workEffortPartyPerformanceSummaryList[0].workEffortId;
	}
	
	
    //Nel calendario filtrato per le testate delle schede, devo mosdtrare solo gli eventi associati a queste schede.Mi serve pertanto solo
    //la lista dei workeffortId delle testate e non tutto il resto dell'elaborazione
    if ("Y".equals(context.calendarEvent)) {
        context.listIt = EntityUtil.getFieldListFromEntityList(workEffortPartyPerformanceSummaryList, "eventWorkEffortId", true);
    } else {

        listIt = [];

        lastMap = [:];
        lastOrgUnitId = null;
        lastEstimatedStartDate = null;
        lastPartyId = null;
        total = null;
		
		
        workEffortPartyPerformanceSummaryList.each { workEffortPartyPerformanceSummary ->
        	currentMap = null;
            // campi su cui viene fatto il groupBy
            currentOrgUnitId = workEffortPartyPerformanceSummary.orgUnitId;
            currentPartyId = workEffortPartyPerformanceSummary.partyIdInCharge;
            currentEstimatedStartDate = workEffortPartyPerformanceSummary.estimatedStartDate;
            
            if (UtilValidate.isEmpty(lastOrgUnitId) || currentOrgUnitId != lastOrgUnitId || UtilValidate.isEmpty(lastPartyId) || currentPartyId != lastPartyId || UtilValidate.isEmpty(lastEstimatedStartDate) || currentEstimatedStartDate != lastEstimatedStartDate) {
                if (UtilValidate.isNotEmpty(lastMap) && UtilValidate.isNotEmpty(total)) {
                    lastMap["total"] = total;
                    total = null;
                }
                currentMap = ["orgUnitId": currentOrgUnitId, "partyId": currentPartyId, "currentStatusId": workEffortPartyPerformanceSummary.currentStatusId, "workEffortId" : workEffortPartyPerformanceSummary.workEffortId, "nextStatusId" : workEffortPartyPerformanceSummary.nextStatusId, "weHierarchyTypeId" : workEffortPartyPerformanceSummary.weHierarchyTypeId, "partyName": workEffortPartyPerformanceSummary.partyName, "partyNameInCharge": workEffortPartyPerformanceSummary.partyNameInCharge];
				
				if (workEffortPartyPerformanceSummary.containsKey("managementRoleTypeId")) {
					currentMap.managementRoleTypeId = workEffortPartyPerformanceSummary.managementRoleTypeId;
				}
				if (workEffortPartyPerformanceSummary.containsKey("managWeStatusEnumId")) {
					currentMap.managWeStatusEnumId = workEffortPartyPerformanceSummary.managWeStatusEnumId;
				}
				
				currentMap.etch = workEffortPartyPerformanceSummary.etch;
				currentMap.etchLang = workEffortPartyPerformanceSummary.etchLang;
				currentMap.weTypeDescription = workEffortPartyPerformanceSummary.weTypeDescription;
				currentMap.weTypeDescriptionLang = workEffortPartyPerformanceSummary.weTypeDescriptionLang;
				currentMap.workEffortName = workEffortPartyPerformanceSummary.workEffortName;
				currentMap.workEffortNameLang = workEffortPartyPerformanceSummary.workEffortNameLang;
				currentMap.estimatedStartDate = workEffortPartyPerformanceSummary.estimatedStartDate;
				currentMap.estimatedCompletionDate = workEffortPartyPerformanceSummary.estimatedCompletionDate;
				currentMap.sourceReferenceId = workEffortPartyPerformanceSummary.sourceReferenceId;
				currentMap.sequenceId = workEffortPartyPerformanceSummary.sequenceId;
				currentMap.weEtch = workEffortPartyPerformanceSummary.weEtch;
				currentMap.weStatusDescr = workEffortPartyPerformanceSummary.weStatusDescr;
				currentMap.weStatusDescrLang = workEffortPartyPerformanceSummary.weStatusDescrLang;
				
                listIt.add(currentMap);
                lastMap = currentMap;
                lastOrgUnitId = currentOrgUnitId;
                lastPartyId = currentPartyId;
                lastEstimatedStartDate = currentEstimatedStartDate;
            } else {
                currentMap = lastMap;
            }
                                         				           
            if (UtilValidate.isNotEmpty(total)) {
                total = total.add(workEffortPartyPerformanceSummary.weTotal);
            } else {
                total = new BigDecimal(workEffortPartyPerformanceSummary.weTotal);
            }
            
        }

        if (UtilValidate.isNotEmpty(lastMap) && UtilValidate.isNotEmpty(total)) {
            lastMap["total"] = total;
        }
        context.listIt = listIt;
	
    }
}

if (UtilValidate.isNotEmpty(oldWorkEffortId)) {
	parameters.workEffortId = oldWorkEffortId;
}
if (UtilValidate.isNotEmpty(oldWorkEffortTypeId)) {
	parameters.workEffortTypeId = oldWorkEffortTypeId;
}
parameters.workEffortIdRoot = workEffortIdRoot;

// Paginazione
def paginatorParams = new PaginatorEventParams(parameters, "3", 15);
context.completeListSize = UtilValidate.isNotEmpty(context.listIt) ? context.listIt.size() : 0;
context.lastViewIndex = context.completeListSize > 0 ? (context.completeListSize - 1) / paginatorParams.getViewSize() : 0;
context.listIt = new ListPaginator(context.listIt).paginate(paginatorParams);
paginatorParams.put();

orgUnitSelectedName = "";

if(UtilValidate.isNotEmpty(context.listIt)) {
	def workEffortPartyPerformanceSummaryList = context.listIt;
	context.listIt = [];
	
	if(UtilValidate.isNotEmpty(workEffortPartyPerformanceSummaryList)) {
		def firstItem = workEffortPartyPerformanceSummaryList.get(0);
		if(UtilValidate.isNotEmpty(firstItem)) {
		    if ("MAIN".equals(context.showUoCode)) {
				def parentRoleCode = "";
				def partyParentRole = delegator.findOne("PartyParentRole", ["partyId" : firstItem.orgUnitId, "roleTypeId" : "ORGANIZATION_UNIT"], false);
				if (UtilValidate.isNotEmpty(partyParentRole)) {
					parentRoleCode = partyParentRole.parentRoleCode;
				}
				if (UtilValidate.isNotEmpty(parentRoleCode)) {
					orgUnitSelectedName = parentRoleCode + " - ";
				}
				orgUnitSelectedName += "Y".equals(localeSecondarySet) ? firstItem.partyNameLang : firstItem.partyName;
		    } else if ("EXT".equals(context.showUoCode)) {
		    	def externalId = "";
				def party = delegator.findOne("Party", ["partyId" : firstItem.orgUnitId], false);
				if (UtilValidate.isNotEmpty(party)) {
					externalId = party.externalId;
				}
		    	if (UtilValidate.isNotEmpty(externalId)) {
		    		orgUnitSelectedName = externalId + " - ";
		    	}
		    	orgUnitSelectedName += "Y".equals(localeSecondarySet) ? firstItem.partyNameLang : firstItem.partyName;
		    } else {
		    	orgUnitSelectedName = "Y".equals(localeSecondarySet) ? firstItem.partyNameLang : firstItem.partyName;
		    }
		}
	}
	
	workEffortPartyPerformanceSummaryList.each { workEffortPartyPerformanceSummary ->
	    def rootSearchRootInqyServiceMap = [:];
        rootSearchRootInqyServiceMap.put("workEffortRootId", workEffortPartyPerformanceSummary.workEffortId);
        rootSearchRootInqyServiceMap.put("userLogin", context.userLogin);
        def rootSearchRootInqyServiceRes = dispatcher.runSync("getCanViewUpdateWorkEffortRoot", rootSearchRootInqyServiceMap);
        workEffortPartyPerformanceSummary.canUpdateRoot = rootSearchRootInqyServiceRes.canUpdateRoot;
        workEffortPartyPerformanceSummary.canViewRoot = rootSearchRootInqyServiceRes.canViewRoot;
        
	    def nextValidStatusId = "";
	    def nextValidStatusDescr = "";
	    def nextValidStatus = getNextValidStatus(workEffortPartyPerformanceSummary.currentStatusId, workEffortPartyPerformanceSummary.sequenceId);
	    if (UtilValidate.isNotEmpty(nextValidStatus)) {
	    	nextValidStatusId = nextValidStatus.statusIdTo;
	    	nextValidStatusDescr = nextValidStatus.description;
	    }
	    workEffortPartyPerformanceSummary.nextValidStatusId = nextValidStatusId;
	    workEffortPartyPerformanceSummary.nextValidStatusDescr = nextValidStatusDescr;
	}
	context.listIt = workEffortPartyPerformanceSummaryList;
}

if(UtilValidate.isEmpty(orgUnitSelectedName)) {
	if(UtilValidate.isNotEmpty(parameters.orgUnitId)) {
		def orgUnit = delegator.findOne("Party", ["partyId": parameters.orgUnitId], false);
		if(UtilValidate.isNotEmpty(orgUnit)) {
		    if ("MAIN".equals(context.showUoCode)) {
				def parentRoleCode = "";
				def partyParentRole = delegator.findOne("PartyParentRole", ["partyId" : parameters.orgUnitId, "roleTypeId" : "ORGANIZATION_UNIT"], false);
				if (UtilValidate.isNotEmpty(partyParentRole)) {
					parentRoleCode = partyParentRole.parentRoleCode;
				}
				if (UtilValidate.isNotEmpty(parentRoleCode)) {
					orgUnitSelectedName = parentRoleCode + " - ";
				}
				orgUnitSelectedName += "Y".equals(localeSecondarySet) ? orgUnit.partyNameLang : orgUnit.partyName;
		    } else if ("EXT".equals(context.showUoCode)) {
		    	if (UtilValidate.isNotEmpty(orgUnit.externalId)) {
		    		orgUnitSelectedName = orgUnit.externalId + " - ";
		    	}
		    	orgUnitSelectedName += "Y".equals(localeSecondarySet) ? orgUnit.partyNameLang : orgUnit.partyName;
		    } else {
		    	orgUnitSelectedName = "Y".equals(localeSecondarySet) ? orgUnit.partyNameLang : orgUnit.partyName;
		    }
		}
	}
}

context.orgUnitSelectedName = orgUnitSelectedName;

/*
 * GN-711: per ogni record estratto interrogo l'entity StatusItemAndValidChangeStatusTo
 * per avere il next status con sequenceId piu basso
 */
def getNextValidStatus(currentStatusId, sequenceId) {	
    def nextValidStatusConditions = [];
    def nextValidStatusFieldsToSelect = UtilMisc.toSet("statusId", "statusIdTo", "sequenceId", "description");
    
    nextValidStatusConditions.add(EntityCondition.makeCondition("statusId", currentStatusId));
    nextValidStatusConditions.add(EntityCondition.makeCondition("sequenceId", sequenceId));
    def nextValidStatusList = delegator.findList("StatusItemAndValidChangeStatusTo", EntityCondition.makeCondition(nextValidStatusConditions), nextValidStatusFieldsToSelect, 
            null, null, false);
    
    return EntityUtil.getFirst(nextValidStatusList);
}

def getPortalTypeId(weContextId) {
	def portalTypeId = "";
	def contextIdEnum = ContextIdEnum.parse(weContextId);
	if (contextIdEnum != null) {
		portalTypeId = "ST_PORTAL_" + contextIdEnum.weContext();
	}
	return portalTypeId;
}
