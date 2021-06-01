import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.birt.util.Utils;

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
Debug.log("### executePerformFindOrgValuedParty -> weContextId="+weContextId);

def evalPartyId = UtilValidate.isNotEmpty(context.evalPartyId) ? context.evalPartyId : parameters.evalPartyId;
Debug.log("### executePerformFindOrgValuedParty -> evalPartyId="+evalPartyId);

def valuedPartyList = null;
def mapService = [:];


// controllo i permessi
def isOrgMgr = security.hasPermission("EMPLPERFORG_ADMIN", userLogin);

if (isOrgMgr) {
	def partyRelationshipTypeId = UtilProperties.getPropertyValue("BaseConfig.properties", "EmplPerfInsertFromTemplate.partyRelationshipTypeId", "ORG_EMPLOYMENT");
	Debug.log("### executePerformFindOrgValuedParty -> partyRelationshipTypeId="+partyRelationshipTypeId);
	mapService.put("partyRelationshipTypeId", partyRelationshipTypeId);
	mapService.put("userLogin", context.userLogin);
	mapService.put("timeZone", context.timeZone);
	
    //utilizzano due dispacher diversi
    def localDispacher = dispatcher;
    if (dispatcher instanceof org.ofbiz.service.ServiceDispatcher ) {
        localDispacher = dispatcher.getLocalDispatcher("partymgr");
    }
    
    def result = localDispacher.runSync("executePerformFindOrgValuedParty", mapService);
    valuedPartyList = result.rowList;
    
} else {
    condition =  EntityCondition.makeCondition("roleTypeId","WEM_EVAL_IN_CHARGE");
    valuedPartyList = delegator.findList("PartyRoleView", condition, null, ["partyName"], null, false);
}

context.valuedPartyList = valuedPartyList;

