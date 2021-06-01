import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def returnMap = ServiceUtil.returnSuccess();

def glAccountId = parameters.glAccountId;
def roleTypeId = parameters.roleTypeId;
def fromDate = UtilValidate.isNotEmpty(parameters.fromDate) ? parameters.fromDate : UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

def partyAndPartyRoleConditions = [];
partyAndPartyRoleConditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
partyAndPartyRoleConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
def partyAndPartyRoleList = delegator.findList("PartyAndPartyRole", EntityCondition.makeCondition(partyAndPartyRoleConditions), null, null, null, false);

if (UtilValidate.isNotEmpty(partyAndPartyRoleList)) {
	def glAccountRoleConditions = [];
	glAccountRoleConditions.add(EntityCondition.makeCondition("glAccountId", glAccountId));
	glAccountRoleConditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
	def glAccountRoleList = delegator.findList("GlAccountRole", EntityCondition.makeCondition(glAccountRoleConditions), null, null, null, false);	
	def partyIdList = EntityUtil.getFieldListFromEntityList(glAccountRoleList, "partyId", true);
	
	partyAndPartyRoleList.each { partyAndPartyRoleItem ->
	    if (partyIdList == null || (partyIdList != null && ! partyIdList.contains(partyAndPartyRoleItem.partyId))) {
	    	def localParameters = [:];
	    	localParameters.operation = "CREATE";
	    	localParameters.glAccountId = glAccountId;
	    	localParameters.partyId = partyAndPartyRoleItem.partyId;
	    	localParameters.roleTypeId = partyAndPartyRoleItem.roleTypeId;
	    	localParameters.fromDate = fromDate;
	    	
	    	def serviceMap = [:];
	    	serviceMap.put("entityName", "GlAccountRole");
	    	serviceMap.put("operation", "CREATE");
	    	serviceMap.put("userLogin", userLogin);
	    	serviceMap.put("parameters", localParameters);
	    	serviceMap.put("locale", locale);
	    	//serviceMap.put("timeZone", timeZone);
            dctx.getDispatcher().runSync("crudServiceDefaultOrchestration_GlAccountRole", serviceMap);	
	    }
	}
}