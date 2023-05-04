import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.find.WorkEffortFindServices;

// servizio richiamato nel form di dettaglio di una scheda/obiettivo per visualizzare la lista di unita organizative
def security = context.security;
if (UtilValidate.isEmpty(security)) {
    security = SecurityFactory.getInstance(delegator);
}

def weContextId = UtilValidate.isNotEmpty(context.weContextId) ? context.weContextId : parameters.weContextId;
def parentTypeId = UtilValidate.isNotEmpty(context.parentTypeId) ? context.parentTypeId : parameters.parentTypeId;
weContextId = UtilValidate.isNotEmpty(context.weContextId) ? weContextId : parentTypeId;
if (weContextId == null) {
    weContextId = "";
}
Debug.log("### executePerformFindPartyRoleOrgUnit -> weContextId="+weContextId);

def roleTypeId = "";
if (UtilValidate.isNotEmpty(parameters.workEffortId)) {
	roleTypeId = UtilValidate.isNotEmpty(context.orgUnitRoleTypeId) ? context.orgUnitRoleTypeId : parameters.orgUnitRoleTypeId;
} else {
	roleTypeId = UtilValidate.isNotEmpty(context.roleTypeId) ? context.roleTypeId : (UtilValidate.isNotEmpty(context.orgUnitRoleTypeId) ? context.orgUnitRoleTypeId : parameters.orgUnitRoleTypeId);
}
Debug.log("### executePerformFindPartyRoleOrgUnit -> roleTypeId="+roleTypeId);

def partyRoleList = null;
def orderBy = UtilMisc.toList("parentRoleCode");
if ("EXTCODE".equals(context.orderUoBy)) {
	orderBy = UtilMisc.toList("externalId");
}
if ("UONAME".equals(context.orderUoBy)) {
	orderBy = "Y".equals(context.localeSecondarySet) ? UtilMisc.toList("partyNameLang") : UtilMisc.toList("partyName");
}
def codeField = "";
if ("MAIN".equals(context.showUoCode)) {
	codeField = "parentRoleCode";
}
if ("EXT".equals(context.showUoCode)) {
	codeField = "externalId";
}
context.codeField = codeField;

def organizationId = context.organizationId;
if (UtilValidate.isEmpty(organizationId)) {
	def workEffortFindServices = new WorkEffortFindServices(delegator, dispatcher); 
	organizationId = workEffortFindServices.getOrganizationId(userLogin, false);
}


// controllo i permessi
def mapService = Utils.getMapUserPermisionOrgUnit(security, weContextId, userLogin, true);
Debug.log("### executePerformFindPartyRoleOrgUnit -> is limited user " + (mapService.isOrgMgr || mapService.isSup || mapService.isTop));

if (mapService.isOrgMgr  || mapService.isSup  || mapService.isTop) {
	def queryOrderBy = "PP.PARENT_ROLE_CODE";
	if ("EXTCODE".equals(context.orderUoBy)) {
		queryOrderBy = "PA.EXTERNAL_ID";
	}
	if ("UONAME".equals(context.orderUoBy)) {
		queryOrderBy = "Y".equals(context.localeSecondarySet) ? "PA.PARTY_NAME_LANG" : "PA.PARTY_NAME";	
	}
    mapService.put("roleTypeId", roleTypeId);
    mapService.put("statusId", "PARTY_ENABLED");
    mapService.put("organizationId", organizationId);
    mapService.put("queryOrderBy", queryOrderBy);
    //utilizzano due dispacher diversi
    def localDispacher = dispatcher;
    if (dispatcher instanceof org.ofbiz.service.ServiceDispatcher ) {
        localDispacher = dispatcher.getLocalDispatcher("partymgr");
    }
    
    Debug.log("### executePerformFindPartyRoleOrgUnit -> mapService="+mapService);
    def result = localDispacher.runSync("executePerformFindPartyRoleOrgUnit", mapService);
    partyRoleList = result.rowList;
    
} else {
    
    condition = EntityCondition.makeCondition("parentRoleTypeId", "ORGANIZATION_UNIT");
    if (UtilValidate.isNotEmpty(roleTypeId)) {
        condition =  EntityCondition.makeCondition(condition, EntityCondition.makeCondition("roleTypeId", roleTypeId));
    }
    condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
    condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition("organizationId", organizationId));
    
    Debug.log("### executePerformFindPartyRoleOrgUnit ->  condition="+condition);
    Set fieldsToSelect = UtilMisc.toSet("partyId", "partyName", "partyNameLang", "parentRoleCode", "externalId");
    EntityFindOptions findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    partyRoleList = delegator.findList("PartyRoleOrgUnitView", condition, fieldsToSelect, orderBy, findOptions, false);
}

//Debug.log(".....................### executePerformFindPartyRoleOrgUnit ->  partyRoleList="+partyRoleList);

//TODO da fare 
// se ho un orgUnitId devo vedere se è presente nella lista se no lo aggiungo!

def orgUnitId = UtilValidate.isNotEmpty(context.orgUnitId) ? context.orgUnitId : parameters.orgUnitId;
Debug.log("### executePerformFindPartyRoleOrgUnit ->  orgUnitId="+orgUnitId);
if (UtilValidate.isNotEmpty(orgUnitId)) {
    def isOrgUnitId= false;
    for(partyRole in partyRoleList) {
        if (partyRole.partyId == orgUnitId) {
            isOrgUnitId = true;
            break;
        }
    }
    Debug.log("### executePerformFindPartyRoleOrgUnit ->  isOrgUnitId="+isOrgUnitId);
    if (!isOrgUnitId) {
        //devo aggiungere la riag del party
        condition =  EntityCondition.makeCondition(EntityCondition.makeCondition("parentRoleTypeId", "ORGANIZATION_UNIT"), EntityCondition.makeCondition("orgUnitId", orgUnitId));
        if (UtilValidate.isNotEmpty(roleTypeId)) {
            condition =  EntityCondition.makeCondition(condition, EntityCondition.makeCondition("roleTypeId", roleTypeId));
        }
        Debug.log("### executePerformFindPartyRoleOrgUnit ->  condition="+condition);
        Set fieldsToSelect = UtilMisc.toSet("partyId", "partyName", "partyNameLang", "parentRoleCode");
        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setDistinct(true);
        partyRoleList.addAll(delegator.findList("PartyRoleOrgUnitView", condition, fieldsToSelect, orderBy, findOptions, false));
        //EntityUtil.orderBy(partyRoleList, orderBy);
    }

}

context.orgUnitDisplayField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";
context.partyRoleList = partyRoleList;

