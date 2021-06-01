import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


def isReadOnlyField = UtilValidate.isNotEmpty(context.isReadOnly) ? context.isReadOnly : parameters.isReadOnly;
def hasPermission = security.hasPermission("WORKEFFORTMGR_CREATE", userLogin) || security.hasPermission("WORKEFFORTMGR_ADMIN", userLogin) || security.hasPermission("WORKEFFORTMGR_UPDATE", userLogin) || security.hasPermission("WORKEFFORTORG_ADMIN", userLogin) || security.hasPermission("WORKEFFORTROLE_ADMIN", userLogin);

def isReadOnly = false;
if (isReadOnlyField instanceof Boolean) {
	isReadOnly = isReadOnlyField;
} else if (isReadOnlyField instanceof String) {
	isReadOnly = "true".equalsIgnoreCase(isReadOnlyField);
}

def isWorkEffortViewFormReadOnly = "N";

if (isReadOnly || ! hasPermission) {
	isWorkEffortViewFormReadOnly = "Y";
}

if ("Y".equals(isWorkEffortViewFormReadOnly)) {	
	def partyList = delegator.findList("PartyAndPartyParentRole", EntityCondition.makeCondition("partyId", context.orgUnitId), null, null, null, false);
	def orgUnit = EntityUtil.getFirst(partyList);	
	if (UtilValidate.isNotEmpty(orgUnit)) {
		context.orgUnitDesc = "Y".equals(context.localeSecondarySet) ? orgUnit.parentRoleCode + " - " + orgUnit.partyNameLang : orgUnit.parentRoleCode + " - " + orgUnit.partyName;
	}
}

context.isWorkEffortViewFormReadOnly = isWorkEffortViewFormReadOnly;
