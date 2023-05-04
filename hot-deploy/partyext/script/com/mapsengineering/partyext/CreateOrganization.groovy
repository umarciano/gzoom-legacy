import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.*;
import org.ofbiz.service.ServiceUtil;

dispatcher = dctx.getDispatcher();
delegator = dctx.getDelegator();

def result = ServiceUtil.returnSuccess();

def partyId = delegator.getNextSeqId("Party");
def groupName = context.groupName;
def parentRoleCode = context.parentRoleCode;

def party = delegator.makeValue("Party");
party.partyId = partyId;
party.statusId = "PARTY_ENABLED";
party.partyTypeId = "PARTY_GROUP";
party.partyName = groupName;
delegator.create(party);

def partyGroup = delegator.makeValue("PartyGroup");
partyGroup.partyId = partyId;
partyGroup.groupName = groupName;
delegator.create(partyGroup);

def partyParentRole = delegator.makeValue("PartyParentRole");
partyParentRole.partyId = partyId;
partyParentRole.roleTypeId = "INTERNAL_ORGANIZATIO";
partyParentRole.parentRoleCode = parentRoleCode;
partyParentRole.organizationId = partyId;
delegator.create(partyParentRole);

def partyRole = delegator.makeValue("PartyRole");
partyRole.partyId = partyId;
partyRole.roleTypeId = "INTERNAL_ORGANIZATIO";
partyRole.parentRoleTypeId = "INTERNAL_ORGANIZATIO";
delegator.create(partyRole);

def partyAcctgPreference = delegator.makeValue("PartyAcctgPreference");
partyAcctgPreference.partyId = partyId;
partyAcctgPreference.baseCurrencyUomId = "EUR";
delegator.create(partyAcctgPreference);

def userLoginValidPartyRole = delegator.makeValue("UserLoginValidPartyRole");
userLoginValidPartyRole.userLoginId = "admin";
userLoginValidPartyRole.partyId = partyId;
userLoginValidPartyRole.roleTypeId = "INTERNAL_ORGANIZATIO";
delegator.create(userLoginValidPartyRole);

def glAccountList = delegator.findList("GlAccount", null, null, null, null, false);
if (UtilValidate.isNotEmpty(glAccountList)) {
	for (GenericValue glAccount : glAccountList) {
		def glAccountOrganization = delegator.makeValue("GlAccountOrganization");
		glAccountOrganization.glAccountId = glAccount.glAccountId;
		glAccountOrganization.organizationPartyId = partyId;
		delegator.create(glAccountOrganization);
	}
}

return result;