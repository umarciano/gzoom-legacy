import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import com.mapsengineering.workeffortext.util.LevelUoPartyExtractor;

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
	
	LevelUoPartyExtractor levelUoPartyExtractor = new LevelUoPartyExtractor(delegator, orgUnitId, orgUnitRoleTypeId);
	levelUoPartyExtractor.initLevelSameUO(parameters.get("assocLevelSameUO"), parameters.get("assocLevelSameUOAss"));
	levelUoPartyExtractor.initLevelParentUO(parameters.get("assocLevelParentUO"), parameters.get("assocLevelParentUOAss"));
	levelUoPartyExtractor.initLevelChildUO(parameters.get("assocLevelChildUO"), parameters.get("assocLevelChildUOAss"));
	levelUoPartyExtractor.initLevelSisterUO(parameters.get("assocLevelSisterUO"), parameters.get("assocLevelSisterUOAss"));
	levelUoPartyExtractor.initLevelTopUO(parameters.get("assocLevelTopUO"), parameters.get("assocLevelTopUOAss"));
	levelUoPartyExtractor.run();
	if(levelUoPartyExtractor.isLevelUO()) {
		def orgUnitIdList = levelUoPartyExtractor.getOrgUnitIdList();
		parameters.orgUnitId_value = StringUtil.join(orgUnitIdList, ",");
		parameters.orgUnitId_op = "in";
		parameters.sortField = "sourceReferenceId|weEtch";
	}
	if(levelUoPartyExtractor.isLevelUOAss()) {
		def wepaPartyIdList = levelUoPartyExtractor.getWepaPartyIdList();
		parameters.wepaPartyIdList_value = StringUtil.join(wepaPartyIdList, ",");
		parameters.wepaPartyIdList_op = "in";
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
