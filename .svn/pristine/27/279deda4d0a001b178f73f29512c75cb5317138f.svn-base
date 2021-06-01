import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;


result = ServiceUtil.returnSuccess();

def security = dctx.getSecurity();
def partyId = "";
def partyName = "";
def roleDescription = "";
def roleTypeId = "";
def parentRoleCode = "";

def evalPartyId = parameters.evalPartyId;
def userLogin = (GenericValue) context.get("userLogin");
def entityName = "";
if (!security.hasPermission("EMPLPERFORG_ADMIN", userLogin) && !security.hasPermission("EMPLPERFROLE_ADMIN", userLogin)) {
    entityName = "PartyEvalAllOrganizationView";
}
if (security.hasPermission("EMPLPERFORG_ADMIN", userLogin) && !security.hasPermission("EMPLPERFROLE_ADMIN", userLogin)) {
    entityName = "PartyEvalAllOrganizationOrgPermissionView";
}

if (UtilValidate.isNotEmpty(entityName)) {
	def condList = [];
	def partyRelationshipTypeId = UtilProperties.getPropertyValue("BaseConfig", "EmplPerfInsertFromTemplate.partyRelationshipTypeId");
	condList.add(EntityCondition.makeCondition("evalPartyId", evalPartyId));
	if (UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
		condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
	}
	condList.add(EntityCondition.makeCondition("parentRoleTypeId", "ORGANIZATION_UNIT"));
	condList.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
	
	def partyList = delegator.findList(entityName, EntityCondition.makeCondition(condList), null, null, null, false);
	def partyItem = EntityUtil.getFirst(partyList);
	if (UtilValidate.isNotEmpty(partyItem)) {
		partyId = partyItem.partyId;
		partyName = partyItem.partyName;
		roleDescription = partyItem.roleDescription;
		roleTypeId = partyItem.roleTypeId;
		parentRoleCode = partyItem.parentRoleCode;
	}
}

result.put("partyId", partyId);
result.put("partyName", partyName);
result.put("roleDescription", roleDescription);
result.put("roleTypeId", roleTypeId);
result.put("parentRoleCode", parentRoleCode);

return result;
