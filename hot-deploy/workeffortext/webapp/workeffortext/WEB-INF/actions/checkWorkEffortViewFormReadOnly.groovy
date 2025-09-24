import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

// Check global portal read-only mode first
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkPortalReadOnlyMode.groovy", context);

def isReadOnlyField = UtilValidate.isNotEmpty(context.isReadOnly) ? context.isReadOnly : parameters.isReadOnly;
def isPosted = UtilValidate.isNotEmpty(context.isPosted) ? context.isPosted : parameters.isPosted;
def crudEnumId = UtilValidate.isNotEmpty(context.crudEnumId) ? context.crudEnumId : parameters.crudEnumId;
def forceReadOnly = UtilValidate.isNotEmpty(context.forceReadOnly) ? context.forceReadOnly : parameters.forceReadOnly;

def hasPermission = security.hasPermission("WORKEFFORTMGR_CREATE", userLogin) || security.hasPermission("WORKEFFORTMGR_ADMIN", userLogin) || security.hasPermission("WORKEFFORTMGR_UPDATE", userLogin) || security.hasPermission("WORKEFFORTORG_ADMIN", userLogin) || security.hasPermission("WORKEFFORTROLE_ADMIN", userLogin) || security.hasPermission("EMPLPERF_ADMIN", userLogin) || security.hasPermission("EMPLPERF_CREATE", userLogin) || security.hasPermission("EMPLPERF_UPDATE", userLogin) || security.hasPermission("EMPLPERF_VIEW", userLogin);

// Bypass temporaneo per admin per risolvere problemi post-HTTPS
if ("admin".equals(userLogin?.userLoginId)) {
	hasPermission = true;
}

def isReadOnly = false;
if (isReadOnlyField instanceof Boolean) {
	isReadOnly = isReadOnlyField;
} else if (isReadOnlyField instanceof String) {
	isReadOnly = "true".equalsIgnoreCase(isReadOnlyField);
}

def isWorkEffortViewFormReadOnly = "N";
// Debug.log(" isReadOnly " + isReadOnly);
// Debug.log(" isPosted " + isPosted);
// Debug.log(" crudEnumId " + crudEnumId);
// Debug.log(" hasPermission " + hasPermission);
// Debug.log(" forceReadOnly " + forceReadOnly);
if (isReadOnly || "Y".equals(isPosted) || "NONE".equals(crudEnumId) || "INSERT".equals(crudEnumId) || ! hasPermission || "Y".equals(forceReadOnly)) {
	isWorkEffortViewFormReadOnly = "Y";
}

if ("Y".equals(isWorkEffortViewFormReadOnly)) {	
	def partyList = delegator.findList("PartyAndPartyParentRole", EntityCondition.makeCondition("partyId", context.orgUnitId), null, null, null, false);
	def orgUnit = EntityUtil.getFirst(partyList);	
	if (UtilValidate.isNotEmpty(orgUnit)) {
		if (UtilValidate.isNotEmpty(context.codeField)) {
			context.orgUnitDesc = "Y".equals(context.localeSecondarySet) ? orgUnit.get(context.codeField) + " - " + orgUnit.partyNameLang : orgUnit.get(context.codeField) + " - " + orgUnit.partyName;
		} else {
			context.orgUnitDesc = "Y".equals(context.localeSecondarySet) ? orgUnit.partyNameLang : orgUnit.partyName;
		}
	}
}

Debug.log("context.isWorkEffortViewFormReadOnly " + isWorkEffortViewFormReadOnly);
context.isWorkEffortViewFormReadOnly = isWorkEffortViewFormReadOnly;
