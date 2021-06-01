import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.find.WorkEffortFindServices;

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


def roleTypeId = UtilValidate.isNotEmpty(context.roleTypeId) ? context.roleTypeId : (UtilValidate.isNotEmpty(context.orgUnitRoleTypeId) ? context.orgUnitRoleTypeId : parameters.orgUnitRoleTypeId);
Debug.log("### executePerformFindPartyRoleOrgUnit -> roleTypeId="+roleTypeId);

def partyRoleList = null;
def orderBy = UtilMisc.toList("parentRoleCode");

def organizationId = context.organizationId;
if (UtilValidate.isEmpty(organizationId)) {
	def workEffortFindServices = new WorkEffortFindServices(delegator, dispatcher); 
	organizationId = workEffortFindServices.getOrganizationId(userLogin, false);
}


// controllo i permessi
def mapService = Utils.getMapUserPermisionOrgUnit(security, weContextId, userLogin);

if (mapService.isOrgMgr  || mapService.isSup  || mapService.isTop) {
    mapService.put("roleTypeId", roleTypeId);
    mapService.put("statusId", "PARTY_ENABLED");
    mapService.put("organizationId", organizationId);
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
    partyRoleList = delegator.findList("PartyRoleOrgUnitView", condition, null, orderBy, null, false);
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
        partyRoleList.addAll(delegator.findList("PartyRoleOrgUnitView", condition, null, orderBy, null, false));
        //EntityUtil.orderBy(partyRoleList, orderBy);
    }

}

context.orgUnitDisplayField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";
context.partyRoleList = partyRoleList;

