import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.util.ContextIdEnum;

workEffortAssignmentSummaryList = null;
orgUnitId = null;
if (UtilValidate.isNotEmpty(parameters.orgUnitId)) {
	orgUnitId = parameters.orgUnitId;
}

/** In caso di portlet per performance Individuale viene utilizzata la stessa vista usata nelle interrogazioni individuali
 * e non la vista analoga alle altre portelt, 
 * perche' il weTotal delle altre viste conteggia piu volte le schede su cui l'utente e' assegnato con piu' valori, 
 * e se uso il raggruppamento su workEffortPartyAssignment, la vista diventa lenta */
context.permission = UtilValidate.isNotEmpty(parameters.permission_value) ? parameters.permission_value : "EMPLPERF";
parameters.weContextId = UtilValidate.isNotEmpty(parameters.weContextId_value) ? parameters.weContextId_value : "CTX_EP";

parameters.entityName = UtilValidate.isNotEmpty(parameters.entityName) ? parameters.entityName : "WorkEffortRootInqySummaryView";
context.entityNamePrefix = UtilValidate.isNotEmpty(parameters.entityNamePrefix) ? parameters.entityNamePrefix : "WorkEffortRootInqySummary";

localeSecondarySet = context.localeSecondarySet;
parameters.orderBy = "parentRoleCode";
parameters.queryOrderBy = "PPR.PARENT_ROLE_CODE";
parameters.remove("workEffortId");

/**
 * Nel caso di individuali devo anche aggiungere isTemplate = 'N' xch� nella vista che uso non � inserito!
 */
if ("CTX_EP".equals(parameters.weContextId)) {
    parameters.weIsTemplate = 'N';
}

parameters.organizationId = context.defaultOrganizationPartyId;

Debug.log("Search performance with entityName = " + parameters.entityName);

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);

if (UtilValidate.isNotEmpty(context.listIt)) {
	
	/**
     * Filtro la lista per il campo weActivation != ACTSTATUS_REPLACED e da ACTSTATUS_CLOSED
     */
    context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(
            EntityCondition.makeCondition("weActivation", EntityOperator.NOT_EQUAL, "ACTSTATUS_CLOSED"),
            EntityCondition.makeCondition("weActivation", EntityOperator.NOT_EQUAL, "ACTSTATUS_REPLACED")
            ));
    
	workEffortAssignmentSummaryList = context.listIt;
}

if (UtilValidate.isNotEmpty(context.listIt)) {	
	orgUnitId = workEffortAssignmentSummaryList[0].orgUnitId;
} else {
    // se la lista e' vuota vuol dire che non ho nessuna orgUnitId abilitata
    // oppure ho orgUnitId senza schede,
    // metto una orgUnitId inesistente in modo che il groovy successivo non estragga tutte le schede
	orgUnitId = "NOT_EXISTS";
}


def portalTypeId = getPortalTypeId(parameters.weContextId);
def statusCondList = [];
statusCondList.add(EntityCondition.makeCondition("portalTypeId", portalTypeId));
statusCondList.add(EntityCondition.makeCondition("actStEnumId", "ACTSTATUS_ACTIVE"));

def fieldsToSelect = UtilMisc.toSet("statusId", "statusTypeId", "sequenceId", "description", "descriptionLang");
def orderBy = "Y".equals(localeSecondarySet) ? ["sequenceId", "descriptionLang"] : ["sequenceId", "description"];
def statusList = delegator.findList("StatusItemAndTypeView", EntityCondition.makeCondition(statusCondList), fieldsToSelect, orderBy, null, false);

//gn2438 raggruppo gli stati per sequence e descrizione
statusItemList = [];
if (UtilValidate.isNotEmpty(statusList)) {
	lastStatusSeqId = "";
	lastStatusDescr = "";
	lastStatusItem = [:];
	
	statusList.each{ status ->
		statusItem = [:];
		statusDescr = "Y".equals(localeSecondarySet) ? status.descriptionLang : status.description;
		
		if (UtilValidate.isEmpty(lastStatusDescr) || (UtilValidate.isNotEmpty(statusDescr) && ! statusDescr.equalsIgnoreCase(lastStatusDescr) && UtilValidate.isNotEmpty(status.sequenceId) && ! status.sequenceId.equals(lastStatusSeqId))) {
			statusItem.sequenceId = status.sequenceId;
			statusItem.statusDescr = statusDescr;
			statusItemList.add(statusItem);
			lastStatusItem = statusItem;
		}
		
		statusItem = lastStatusItem;
	    lastStatusDescr = "Y".equals(localeSecondarySet) ? status.descriptionLang : status.description;
		lastStatusSeqId = status.sequenceId;
	}
}

context.statusItemList = statusItemList; 

if (UtilValidate.isEmpty(workEffortAssignmentSummaryList) || UtilValidate.isEmpty(statusList)) {
    context.listIt = [];
} else {
	listIt = [];
	totale = 0;
    lastMap = [:];
    lastOrgUnitId = null;
    workEffortAssignmentSummaryList.each{ workEffortAssignmentSummary ->
        currentMap = null;
        currentOrgUnitId = workEffortAssignmentSummary.orgUnitId;
		currentPartyName = workEffortAssignmentSummary.partyName;
		currentPartyNameLang = workEffortAssignmentSummary.partyNameLang;
		currentParentRoleCode = workEffortAssignmentSummary.parentRoleCode;
		currentExternalId = workEffortAssignmentSummary.externalId;
		currentStatusId = workEffortAssignmentSummary.currentStatusId;
        
		if (UtilValidate.isEmpty(lastOrgUnitId) || currentOrgUnitId != lastOrgUnitId) {
            currentMap = ["orgUnitId": currentOrgUnitId, "partyName": currentPartyName, "partyNameLang": currentPartyNameLang, "parentRoleCode": currentParentRoleCode, "externalId": currentExternalId];
            listIt.add(currentMap);
            lastMap = currentMap;
            lastOrgUnitId = currentOrgUnitId;
        } else {
            currentMap = lastMap;
        }
		
		totale = workEffortAssignmentSummary.weTotal;
		
		def keyTotal = currentOrgUnitId + "_" + workEffortAssignmentSummary.weStatusSeqId;
        if(UtilValidate.isNotEmpty(currentMap[keyTotal])){
			totale += currentMap[keyTotal];
		}
        currentMap[keyTotal] = totale;
    }

	context.listIt = listIt;
}

//calcolo totali riga e colonna
if (UtilValidate.isNotEmpty(context.listIt) && UtilValidate.isNotEmpty(context.statusItemList)) {
	finalList = [];
	def orgUnitTotalsMap = [:];
	def statusTotalsMap = [:];
	
	context.statusItemList.each { stItem ->
		statusTotalsMap[stItem.sequenceId] = 0;
	}
	
	def totGeneral = 0;
	
	context.listIt.each { listItem ->
		def orgUnitTotal = 0;
		context.statusItemList.each { statusItem ->
			def kTotal = listItem.orgUnitId + "_" + statusItem.sequenceId;
			
			if (UtilValidate.isNotEmpty(listItem[kTotal])) {
				totGeneral += listItem[kTotal];
				orgUnitTotal += listItem[kTotal];
				
				def statusTotal = statusTotalsMap[statusItem.sequenceId];
				if (statusTotal != null) {
					statusTotal += listItem[kTotal];
					statusTotalsMap[statusItem.sequenceId] = statusTotal;
				}
			}			
		}
		if (orgUnitTotal > 0) {
			orgUnitTotalsMap[listItem.orgUnitId] = orgUnitTotal;
			finalList.add(listItem);
		}		
    }
	
	context.orgUnitTotalsMap = orgUnitTotalsMap;
	context.statusTotalsMap = statusTotalsMap;
	context.totGeneral = totGeneral;
	context.listIt = finalList;
}


/**
 * Aggiungo alla lista tutte le organizzazione che nn hanno schede solo per solo per weContextId = 'CTX_EP' cio� valutazione individuale
 */
if(parameters.weContextId == 'CTX_EP'){
	organizationUnitUserLoginList = delegator.findList("OrganizationUnitUserLogin", EntityCondition.makeCondition("uvUserLoginId", userLogin.userLoginId), null, null, null, false);

	if(UtilValidate.isNotEmpty(organizationUnitUserLoginList)){
		orgUnitIdList = EntityUtil.getFieldListFromEntityList(workEffortAssignmentSummaryList, "orgUnitId", true);
		if(UtilValidate.isNotEmpty(orgUnitIdList)){
			organizationUnitUserLoginList = EntityUtil.filterByCondition(organizationUnitUserLoginList, EntityCondition.makeCondition(EntityCondition.makeCondition("orgUnitId", EntityOperator.NOT_IN, orgUnitIdList)));
		}
		context.listIt.addAll(organizationUnitUserLoginList);
	}
}

parameters.orgUnitId = UtilValidate.isNotEmpty(context.listIt) ? orgUnitId : "NOT_EXISTS";


def getPortalTypeId(weContextId) {
	def portalTypeId = "";
	def contextIdEnum = ContextIdEnum.parse(weContextId);
	if (contextIdEnum != null) {
		portalTypeId = "ST_PORTAL_" + contextIdEnum.weContext();
	}
	return portalTypeId;
}
