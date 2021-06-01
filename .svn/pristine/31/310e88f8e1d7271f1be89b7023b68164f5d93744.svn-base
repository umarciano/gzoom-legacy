import org.ofbiz.base.util.*;

def partyId = context.partyId;
if (UtilValidate.isEmpty(partyId)) {
	partyId = parameters.partyId;
}

def entityName = parameters.entityName;
def keyMap = ["partyId" : partyId];
if (UtilValidate.isNotEmpty(partyId)) {
	if ("PartyRoleView".equals(entityName)) {
		if (UtilValidate.isEmpty(parameters.roleTypeId)) {
			entityName = "Party";
			
			if (UtilValidate.isNotEmpty(parameters.parentRoleTypeId)) {
				def partyParentRole = delegator.findOne("PartyParentRole", ["partyId" : partyId, "roleTypeId" : parameters.parentRoleTypeId], false);
				if (UtilValidate.isNotEmpty(partyParentRole)) {
					def parentRoleType = partyParentRole.getRelatedOneCache("RoleType");
					if (UtilValidate.isNotEmpty(parentRoleType)) {
						context.put("parentShortLabel", parentRoleType.shortLabel);
					}
					
					context.put("parentRoleCode", partyParentRole.parentRoleCode);
				}
			}
			
		} else {
			keyMap.put("roleTypeId", parameters.roleTypeId);
		}
	}
	
	
	def party = delegator.findOne(entityName, keyMap, false);
	if (UtilValidate.isEmpty(context.listIt)) {
		context.listIt = [party];
	}
	
	context.putAll(party);
}