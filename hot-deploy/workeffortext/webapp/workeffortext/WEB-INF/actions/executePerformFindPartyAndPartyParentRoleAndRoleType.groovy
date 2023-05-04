import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.birt.util.Utils;
import org.ofbiz.entity.GenericEntity;

// servizio richiamato nel form di ricerca schede (interrogazione, definizione,ecc...) per visualizzare la lista di unita organizative
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
def rootInqyTree = UtilValidate.isNotEmpty(context.rootInqyTree) ? context.rootInqyTree : parameters.rootInqyTree;
def gpMenuEnumId = UtilValidate.isNotEmpty(context.gpMenuEnumId) ? context.gpMenuEnumId : parameters.gpMenuEnumId;
def currentStatusContains = UtilValidate.isNotEmpty(context.currentStatusContains) ? context.currentStatusContains : parameters.currentStatusContains;
def snapshot = UtilValidate.isNotEmpty(context.snapshot) ? context.snapshot : parameters.snapshot;
if (UtilValidate.isEmpty(rootInqyTree)) {
	rootInqyTree = "";
}
if (UtilValidate.isEmpty(gpMenuEnumId)) {
	gpMenuEnumId = "";
}
if (UtilValidate.isEmpty(currentStatusContains)) {
	currentStatusContains = "";
}
if (UtilValidate.isEmpty(snapshot)) {
	snapshot = "";
}
def childStruct = UtilValidate.isNotEmpty(context.childStruct) ? context.childStruct : parameters.childStruct;

Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType -> weContextId = "+weContextId + " childStruct = " + childStruct);

def roleTypeId = UtilValidate.isNotEmpty(context.roleTypeId) ? context.roleTypeId : (UtilValidate.isNotEmpty(context.orgUnitRoleTypeId) ? context.orgUnitRoleTypeId : parameters.orgUnitRoleTypeId);
Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType -> roleTypeId = "+roleTypeId);
Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType -> parameters.gpMenuOrgUnitRoleTypeId = "+parameters.gpMenuOrgUnitRoleTypeId);

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


// controllo i permessi
def mapService = Utils.getMapUserPermisionOrgUnit(security, weContextId, userLogin, true);
Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType -> is limited user " + (mapService.isOrgMgr || mapService.isSup || mapService.isTop));

if (mapService.isOrgMgr  || mapService.isSup  || mapService.isTop) {
	def queryOrderBy = "PP.PARENT_ROLE_CODE";
	if ("EXTCODE".equals(context.orderUoBy)) {
		queryOrderBy = "PA.EXTERNAL_ID";
	}
	if ("UONAME".equals(context.orderUoBy)) {
		queryOrderBy = "Y".equals(context.localeSecondarySet) ? "PA.PARTY_NAME_LANG" : "PA.PARTY_NAME";	
	}
    if (UtilValidate.isNotEmpty(roleTypeId)) {
        mapService.put("roleTypeId", roleTypeId);
    }
    else if (UtilValidate.isNotEmpty(parameters.gpMenuOrgUnitRoleTypeId)) {
        mapService.put("roleTypeId", parameters.gpMenuOrgUnitRoleTypeId);
    }
    mapService.put("organizationId", context.defaultOrganizationPartyId);
    // fromSearch utilizzato per legare le UO ai workEffort,
    // poiche di default devono essere visibili solo le UO a cui sono gia' legati dei workEffort.
    // se childStruct = Y -> fromSearch = N, perche estrae tutte le UO
    if (!"Y".equals(childStruct)) {
        mapService.put("fromSearch", "Y");
    }
    mapService.put("rootInqyTree", rootInqyTree);
    mapService.put("gpMenuEnumId", gpMenuEnumId);
    mapService.put("currentStatusContains", currentStatusContains);
    mapService.put("snapshot", snapshot);
    mapService.put("queryOrderBy", queryOrderBy);
    Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType -> childStruct="+childStruct);
    //utilizzano due dispacher diversi
    def localDispacher = dispatcher;
    if (dispatcher instanceof org.ofbiz.service.ServiceDispatcher ) {
        localDispacher = dispatcher.getLocalDispatcher("partymgr");
    }
    Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType.groovy fromSearch messo a Y");

    def result = localDispacher.runSync("executePerformFindPartyRoleOrgUnit", mapService);
    partyRoleList = result.rowList;
    
} else {
    def conditionList = [];
    conditionList.add(EntityCondition.makeCondition("parentRoleTypeId", "ORGANIZATION_UNIT"));
    if (UtilValidate.isNotEmpty(roleTypeId)) {
        conditionList.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
    }
    if (UtilValidate.isNotEmpty(parameters.gpMenuOrgUnitRoleTypeId)) {
        def roleTypeIdList = StringUtil.split(parameters.gpMenuOrgUnitRoleTypeId, "|");
        conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIdList));
    }
    conditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
    searchEntityName = "PartyRoleView";
    if (!"Y".equals(childStruct)) {
        searchEntityName = "PartyRoleOrgUnitWithWorkeffortView";
        if (UtilValidate.isNotEmpty(weContextId)) {
            conditionList.add(EntityCondition.makeCondition("wetParentTypeId", weContextId));
        }
        if ("Y".equals(snapshot)) {
            conditionList.add(EntityCondition.makeCondition("workEffortRevisionId", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD));
        } else {
            conditionList.add(EntityCondition.makeCondition("workEffortRevisionId", GenericEntity.NULL_FIELD));     
        }
        if (! "Y".equals(rootInqyTree)) {
            conditionList.add(EntityCondition.makeCondition("weActStEnumId", EntityOperator.NOT_EQUAL, "ACTSTATUS_CLOSED"));
            if (UtilValidate.isNotEmpty(currentStatusContains)) {
                conditionList.add(EntityCondition.makeCondition("weStatusId", EntityOperator.LIKE, "%" + currentStatusContains + "%"));
            }
            if (UtilValidate.isNotEmpty(gpMenuEnumId)) {
                conditionList.add(EntityCondition.makeCondition("gpMenuEnumId", gpMenuEnumId));
            }
        }
    }

    Set fieldsToSelect = UtilMisc.toSet("partyId", "partyName", "partyNameLang", "parentRoleCode", "externalId");
    EntityFindOptions findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType ->  cerca con = " + conditionList);
    partyRoleList = delegator.findList(searchEntityName, EntityCondition.makeCondition(conditionList), fieldsToSelect, orderBy, findOptions, false);
}

// Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType ->  partyRoleList="+partyRoleList);

//TODO da fare 
// se ho un orgUnitId devo vedere se è presente nella lista se no lo aggiungo!

def orgUnitId = UtilValidate.isNotEmpty(context.orgUnitId) ? context.orgUnitId : parameters.orgUnitId;
Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType ->  orgUnitId="+orgUnitId);
if (UtilValidate.isNotEmpty(orgUnitId)) {
    def isOrgUnitId= false;
    for(partyRole in partyRoleList) {
        if (partyRole.partyId == orgUnitId) {
            isOrgUnitId = true;
            break;
        }
    }
    Debug.log("### executePerformFindPartyAndPartyParentRoleAndRoleType ->  isOrgUnitId="+isOrgUnitId);
    if (!isOrgUnitId) {
        //devo aggiungere la riag del party
        condition =  EntityCondition.makeCondition(EntityCondition.makeCondition("parentRoleTypeId", "ORGANIZATION_UNIT"), EntityCondition.makeCondition("orgUnitId", orgUnitId));
        if (UtilValidate.isNotEmpty(roleTypeId)) {
            condition =  EntityCondition.makeCondition(condition, EntityCondition.makeCondition("roleTypeId", roleTypeId));
        }
        Set fieldsToSelect = UtilMisc.toSet("partyId", "partyName", "partyNameLang", "parentRoleCode");
        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setDistinct(true);
        partyRoleList.addAll(delegator.findList("PartyAndPartyParentRoleAndRoleTypeView", condition, fieldsToSelect, orderBy, findOptions, false));
        EntityUtil.orderBy(partyRoleList, orderBy);
    }

}

context.orgUnitDisplayField = "Y".equals(context.localeSecondarySet) ? "partyNameLang" : "partyName";
context.partyRoleList = partyRoleList;

