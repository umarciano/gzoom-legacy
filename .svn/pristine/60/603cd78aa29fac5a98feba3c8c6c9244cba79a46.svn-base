import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

//Bug 4589

def we = null;

// parentWorkEffortId e il workeffort attuale
if(UtilValidate.isNotEmpty(parameters.parentWorkEffortId)) {
	we = delegator.findOne("WorkEffort", ["workEffortId" : parameters.parentWorkEffortId], false);
}

// 1) se workEffortTypeId vuoto, si hanno considerazioni sul workEffortTypeId possibile (parameters.wefromWetoEnumId, eventuale we.workEffortTypeId)
if (UtilValidate.isEmpty(parameters.workEffortTypeId)) {
	def workEffortTypeIdList = [];
	
	if(UtilValidate.isNotEmpty(we)) {
		def condList = [];
		def contentId = parameters.contentId;
		
		condList.add(EntityCondition.makeCondition("wefromWetoEnumId", parameters.wefromWetoEnumId));
		condList.add(EntityCondition.makeCondition("workEffortTypeId", we.workEffortTypeId));
		if(UtilValidate.isNotEmpty(contentId)) {
			condList.add(EntityCondition.makeCondition("contentId", contentId));	
		}
		
		def workEffortTypeAssocAndAssocTypeList = delegator.findList("WorkEffortTypeAssocAndAssocType", EntityCondition.makeCondition(condList), null, null, null, false);
		if (UtilValidate.isNotEmpty(workEffortTypeAssocAndAssocTypeList)) {
			workEffortTypeIdList = EntityUtil.getFieldListFromEntityList(workEffortTypeAssocAndAssocTypeList, "workEffortTypeIdRef", true);
		}
	}
	
	if (UtilValidate.isNotEmpty(workEffortTypeIdList)) {
		parameters.workEffortTypeId_value = StringUtil.join(workEffortTypeIdList, ",");
		parameters.workEffortTypeId_op = "in";
	} else {
		parameters.workEffortTypeId = "![null-field]";
	}
}

//2) se fromAssoc = Y, si hanno considerazioni sul orgUnitId possibile (parameters.assocLevelSameUO, parameters.assocLevelParentUO, parameters.assocLevelChildUO, eventuale we.orgUnitId)
/**
 * gestione dei params assocLevel
 */
if ("Y".equals(parameters.get("fromAssoc"))) {
	def orgUnitId = UtilValidate.isNotEmpty(parameters.orgUnitId) ? parameters.orgUnitId : 
		               (UtilValidate.isNotEmpty(we) ? we.orgUnitId : "");
	def orgUnitRoleTypeId = UtilValidate.isNotEmpty(parameters.orgUnitRoleTypeId) ? parameters.orgUnitRoleTypeId : 
        (UtilValidate.isNotEmpty(we) ? we.orgUnitRoleTypeId : "");
	def orgUnitIdList = [];
	def assocLevel = false;
	if ("Y".equals(parameters.get("assocLevelSameUO"))) {
		assocLevel = true;
		orgUnitIdList.add(orgUnitId);
	}
	if ("Y".equals(parameters.get("assocLevelParentUO"))) {
		assocLevel = true;
		def levelParentUoPartyIdList = getLevelParentUoPartyIdList(orgUnitId, orgUnitRoleTypeId);
		if(UtilValidate.isNotEmpty(levelParentUoPartyIdList)) {
			orgUnitIdList.addAll(levelParentUoPartyIdList);
		}
	}
	if ("Y".equals(parameters.get("assocLevelChildUO"))) {
		assocLevel = true;
		def levelChildUoPartyIdList = getLevelChildUoPartyIdList(orgUnitId, orgUnitRoleTypeId);
		if(UtilValidate.isNotEmpty(levelChildUoPartyIdList)) {
			orgUnitIdList.addAll(levelChildUoPartyIdList);
		}
	}
	if ("Y".equals(parameters.get("assocLevelSisterUO"))) {
		assocLevel = true;
		def levelSisterUoPartyIdList = getLevelSisterUoPartyIdList(orgUnitId, orgUnitRoleTypeId);
		if (UtilValidate.isNotEmpty(levelSisterUoPartyIdList)) {
			orgUnitIdList.addAll(levelSisterUoPartyIdList);
		}
	}	
	if(assocLevel) {
		parameters.orgUnitId_value = StringUtil.join(orgUnitIdList, ",");
		parameters.orgUnitId_op = "in";
		parameters.sortField = "sourceReferenceId|weEtch";
	}
}



if (parameters.containsKey("fieldList")) {
	if (UtilValidate.isNotEmpty(parameters.fieldList)) {
		if (ObjectType.instanceOf(parameters.fieldList, String.class)) {
			parameters.fieldList = StringUtil.toList(parameters.fieldList,"\\|");
		} else {
			parameters.remove("fieldList");
		}
	} else {
		parameters.remove("fieldList");
	}
}

tableSortField = parameters.get("tableSortField");
if (tableSortField) {
	it = tableSortField.keySet().iterator();
	while (it.hasNext()) {
	  tableSortFieldKey = it.next();
	  entityFromTableSortField = tableSortFieldKey.substring(tableSortFieldKey.lastIndexOf("_") + 1);

	  if (UtilValidate.isNotEmpty(parameters.entityName) && entityFromTableSortField.equals(parameters.entityName)) {
		  parameters.sortField = tableSortField.get(tableSortFieldKey);
	  }
   }
}
if(UtilValidate.isEmpty(parameters.sortField)){
	parameters.sortField = "weEtch|workEffortName";
}

/*
 * GN-581: in aggiorna obiettivi escludere dalla lista gli storici
 */
if (UtilValidate.isNotEmpty(parameters.isCreateObiettiviCollegati) && parameters.isCreateObiettiviCollegati == "Y") {
    parameters.workEffortSnapshotId_fld0_op = "empty";
}

//3) se we valorizzato si ha filtro sulle date
// Filtro sulle date
if (UtilValidate.isNotEmpty(we)) {
	parameters.estimatedStartDate_fld0_op = "lessThanEqualTo";
	parameters.estimatedStartDate_fld0_value = we.estimatedCompletionDate;	
	parameters.estimatedCompletionDate_fld0_op = "greaterThanEqualTo";
	parameters.estimatedCompletionDate_fld0_value = we.estimatedStartDate;
}

Debug.log("############################################  executePerformFindLookupWorkEffortView entityname="+ parameters.entityName);
result = FindWorker.performFind(parameters, dispatcher, timeZone, locale);

if (!ServiceUtil.isError(result)) {
	if (UtilValidate.isNotEmpty(result.listIt) && result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
		list = result.listIt.getCompleteList();
		result.listIt.close();
		result.listIt = list;
	}
	if (UtilValidate.isNotEmpty(result.listIt)) {
		Debug.log("############################################  executePerformFindLookupWorkEffortView result.listIt="+ result.listIt.size());
		Debug.log("############################################  executePerformFindLookupWorkEffortView parameters.isCreateObiettiviCollegati "+ parameters.isCreateObiettiviCollegati);
		Debug.log("############################################  executePerformFindLookupWorkEffortView parameters.evalPartyId "+ parameters.evalPartyId);
		
		/**
		 * Nel caso delle creazione di schede individuali o aggiornamento obiettivi collegati
		 */
		if(UtilValidate.isNotEmpty(parameters.isCreateObiettiviCollegati) && parameters.isCreateObiettiviCollegati == "Y"){
			// Valutato
			if (UtilValidate.isNotEmpty(parameters.evalPartyId)) {
				def wpaWorkEffortIdList = EntityUtil.getFieldListFromEntityList(parameters.wpaList, "workEffortId", false);
				result.listIt = EntityUtil.filterByCondition(result.listIt, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, wpaWorkEffortIdList));

				listOring = result.listIt;
				result.listIt = [];
				
				for(element in listOring) {
					def periodMap = element.getAllFields();
					periodMap.roleTypeWeight = 0;
					
					listElement = EntityUtil.filterByCondition(parameters.wpaList, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, periodMap.workEffortId));
					
					if(UtilValidate.isNotEmpty(listElement)){
						firstElement = listElement[0];
						periodMap.roleTypeWeight = firstElement.roleTypeWeight;
						periodMap.fromDate = firstElement.fromDate;
						periodMap.thruDate = firstElement.thruDate;
					}
					result.listIt.add(periodMap);
				}
				
				Debug.log("############################################  executePerformFindLookupWorkEffortView result.listIt="+ result.listIt.size());
				
			} else {
				result.listIt = [];
			}
				
		}
		
	}
	
	context.listIt = result.listIt;
}


/**
 *  obiettivi con unita responsabile padre di quella dell'obiettivo corrente
 */
def getLevelParentUoPartyIdList(orgUnitId, orgUnitRoleTypeId) {
    def levelParentUoCondList = [];    
    levelParentUoCondList.add(EntityCondition.makeCondition("partyIdTo", orgUnitId));
    levelParentUoCondList.add(EntityCondition.makeCondition("roleTypeIdTo", orgUnitRoleTypeId));
    levelParentUoCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
    levelParentUoCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));     
    def levelParentUoPartyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(levelParentUoCondList), null, null, null, false);
        
    return getPartyIdList(levelParentUoPartyRelList, "partyIdFrom");
}

/**
 *  obiettivi con unita responsabile figlia di quella dell'obiettivo corrente
 */
def getLevelChildUoPartyIdList(orgUnitId, orgUnitRoleTypeId) {
    def levelChildUoCondList = [];    
    levelChildUoCondList.add(EntityCondition.makeCondition("partyIdFrom", orgUnitId));
    levelChildUoCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", orgUnitRoleTypeId));
    levelChildUoCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
    levelChildUoCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
    def levelChildUoPartyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(levelChildUoCondList), null, null, null, false);
    
    return getPartyIdList(levelChildUoPartyRelList, "partyIdTo");
}

/**
 * obiettivi con unita responsabile sorella
 */
def getLevelSisterUoPartyIdList(orgUnitId, orgUnitRoleTypeId) {
	def levelSisterUOPartyIdList = [];
	
	def parentCondList = [];
	parentCondList.add(EntityCondition.makeCondition("partyIdTo", orgUnitId));
	parentCondList.add(EntityCondition.makeCondition("roleTypeIdTo", orgUnitRoleTypeId));
	parentCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
	parentCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
	def parentList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(parentCondList), null, null, null, false);
	
	if (UtilValidate.isNotEmpty(parentList)) {
		for (GenericValue parentItem : parentList) {
			def childCondList = [];
			childCondList.add(EntityCondition.makeCondition("partyIdFrom", parentItem.partyIdFrom));
			childCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, orgUnitId));
			childCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", parentItem.roleTypeIdFrom));
			childCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
			childCondList.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
		    def childrenList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(childCondList), null, null, null, false);
		    
		    levelSisterUOPartyIdList.addAll(getPartyIdList(childrenList, "partyIdTo"));
		}
	}
	return levelSisterUOPartyIdList;	
}

/**
 * estrazione dei party dalla partyRelationshipList
 */
def getPartyIdList(partyRelationshipList, partyField) {
	def partyIdList = [];
	if (UtilValidate.isNotEmpty(partyRelationshipList)) {
		partyIdList = EntityUtil.getFieldListFromEntityList(partyRelationshipList, partyField, true);
	} else {
		partyIdList.add("![null-field]");
	}
	return partyIdList;
}
