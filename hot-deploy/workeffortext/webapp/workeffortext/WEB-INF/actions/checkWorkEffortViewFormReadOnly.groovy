import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

final String strategicContextId = (
    parameters?.weContextId ?:
    parameters?.get("weContextId") ?:
    context?.weContextId ?:
    context?.get("weContextId") ?:
    parameters?.workEffortTypeId ?:
    parameters?.get("workEffortTypeId") ?:
    context?.workEffortTypeId ?:
    context?.get("workEffortTypeId")
) ?: "";
if ("CTX_BS".equals(strategicContextId)) {
    Debug.logInfo("checkWorkEffortViewFormReadOnly: skipping generic individual-performance readonly logic for strategic CTX_BS flow; stratperf owns note permissions", "checkWorkEffortViewFormReadOnly");
    context.isWorkEffortViewFormReadOnly = "N";
    return;
}

// Feature stubs: evaluate visibility for valutatore/valutato notes
def canViewNotaValutatore(context, parameters, userLogin) {
	try {
		// Resolve workEffortId for the current context/parameters
		def weId = parameters?.workEffortId ?: parameters?.workEffortIdFrom ?: context?.workEffortId ?: parameters?.id ?: context?.workEffortIdFrom
		
		// If we have a workEffortId, check WorkEffortPartyAssignment for role WEM_EVAL_MANAGER
		if (weId && userLogin?.partyId) {
			def cond = EntityCondition.makeCondition([
				EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, weId),
				EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER"),
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId)
			], EntityOperator.AND)
			
			def assignments = delegator.findList("WorkEffortPartyAssignment", cond, null, null, null, false)
			
			try {
				Debug.logInfo("canViewNotaValutatore: workEffortId=${weId}, userPartyId=${userLogin.partyId}, foundAssignments=${assignments?.size() ?: 0}", "checkWorkEffortViewFormReadOnly")
			} catch (Throwable t) { }
			
			// Se l'utente è VALUTATORE di questa scheda, può editare noteInfo1
			if (assignments && assignments.size() > 0) {
				return true
			}
		}
	} catch (Throwable t) {
		try {
			Debug.logError("canViewNotaValutatore ERROR: ${t.message}", "checkWorkEffortViewFormReadOnly")
		} catch (Throwable ignore) { }
	}
	return false
}

def canViewNotaValutato(context, parameters, userLogin) {
	try {
		// Resolve workEffortId for the current context/parameters
		def weId = parameters?.workEffortId ?: parameters?.workEffortIdFrom ?: context?.workEffortId ?: parameters?.id ?: context?.workEffortIdFrom
		// Fast path: explicit evaluated party id passed in parameters or context
		def evalPartyIdParam = parameters?.evalPartyId ?: context?.evalPartyId
		if (evalPartyIdParam != null) {
			try {
				if (evalPartyIdParam.toString() == (userLogin?.partyId?.toString())) return true
			} catch (Throwable t) { }
		}

		// If we have a workEffortId, check WorkEffortPartyAssignment for role WEM_EVAL_IN_CHARGE
		if (weId) {
			try {
				def cond = EntityCondition.makeCondition([
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, weId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_IN_CHARGE"),
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin?.partyId)
				], EntityOperator.AND)
				def res = delegator.findList("WorkEffortPartyAssignment", cond, null, null, null, false)
				try {
					Debug.logInfo("checkWorkEffortViewFormReadOnly: foundWorkEffortPartyAssignments=" + (res?.size() ?: 0), "checkWorkEffortViewFormReadOnly")
				} catch (Throwable t) { }
				if (res && res.size() > 0) return true
			} catch (Throwable t) { }
		}

		// Fallback: if availableValutatiIds/context lists contain the user
		def avail = context?.availableValutatiIds ?: context?.evalPartyIdList ?: null
		if (avail instanceof List) {
			try {
				if (avail.contains(userLogin?.partyId)) return true
			} catch (Throwable t) { }
		}
	} catch (Throwable ignore) { }
	return false
}

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
// If the current request is for an evaluated user (evalPartyIdReadOnly), avoid forcing the
// entire WorkEffort view form into read-only mode so that field-level overrides (e.g. noteInfo2)
// can still be applied. Only set the global read-only when not an evaluated-user editing scenario.
def evalPartyIdReadOnly = context.get("evalPartyIdReadOnly");
def evalIsReadOnly = false;
if (evalPartyIdReadOnly instanceof Boolean) {
	evalIsReadOnly = evalPartyIdReadOnly;
} else if (evalPartyIdReadOnly instanceof String) {
	evalIsReadOnly = "true".equalsIgnoreCase(evalPartyIdReadOnly);
}

// Ensure per-field flags exist
if (context.get('canEditNoteInfo1') == null) context.canEditNoteInfo1 = false
if (context.get('canEditNoteInfo2') == null) context.canEditNoteInfo2 = false

// Set per-field edit flags using the visibility stubs when possible
def weIdForFlags = parameters.workEffortId ?: parameters.workEffortIdFrom ?: context.workEffortId ?: parameters.id ?: context.workEffortIdFrom
if (weIdForFlags) {
	try {
		context.canEditNoteInfo1 = canViewNotaValutatore(context, parameters, userLogin)
	} catch (Throwable t) {
		context.canEditNoteInfo1 = context.canEditNoteInfo1 ?: false
	}
	try {
		context.canEditNoteInfo2 = canViewNotaValutato(context, parameters, userLogin)
	} catch (Throwable t) {
		context.canEditNoteInfo2 = context.canEditNoteInfo2 ?: false
	}
	// Ensure flags are explicit booleans (avoid null which breaks widget/groovy checks)
	context.canEditNoteInfo1 = (context.canEditNoteInfo1 == true)
	context.canEditNoteInfo2 = (context.canEditNoteInfo2 == true)
	// Helpful diagnostic: log current user and available evaluated IDs so troubleshooting is easier
	try {
		def avail = context?.availableValutatiIds ?: context?.evalPartyIdList ?: null
		Debug.logInfo("checkWorkEffortViewFormReadOnly: userLogin.partyId=${userLogin?.partyId}, availableValutatiIds=${avail}", "checkWorkEffortViewFormReadOnly")
	} catch (Throwable t) { }
	try {
		Debug.logInfo("checkWorkEffortViewFormReadOnly: FINAL VALUES - canEditNoteInfo1=${context.canEditNoteInfo1}, canEditNoteInfo2=${context.canEditNoteInfo2}, types: ${context.canEditNoteInfo1?.getClass()?.name}, ${context.canEditNoteInfo2?.getClass()?.name}", "checkWorkEffortViewFormReadOnly")
		Debug.logInfo("checkWorkEffortViewFormReadOnly: BUTTON CHECK - noteId1=${context.noteId1}, noteId2=${context.noteId2}, multiTypeLang=${context.multiTypeLang}", "checkWorkEffortViewFormReadOnly")
	} catch (Throwable t) { }
}

if (!evalIsReadOnly) {
	if (isReadOnly || "Y".equals(isPosted) || "NONE".equals(crudEnumId) || "INSERT".equals(crudEnumId) || ! hasPermission || "Y".equals(forceReadOnly)) {
		isWorkEffortViewFormReadOnly = "Y";
	}
} else {
	Debug.log("checkWorkEffortViewFormReadOnly: skipping global readonly because evalPartyIdReadOnly=" + evalPartyIdReadOnly);
}

if ("Y".equals(isWorkEffortViewFormReadOnly)) {	
	def partyList = delegator.findList("PartyAndPartyParentRole", EntityCondition.makeCondition("partyId", context.orgUnitId), null, null, null, false);
	def orgUnit = EntityUtil.getFirst(partyList);	
	if (UtilValidate.isNotEmpty(orgUnit)) {
		if (UtilValidate.isNotEmpty(context.codeField)) {
			def code = orgUnit.get(context.codeField);
			def dashIdx = code != null ? code.indexOf("-") : -1;
			def trimmedCode = dashIdx >= 0 ? code.substring(0, dashIdx).trim() : code;
			context.orgUnitDesc = "Y".equals(context.localeSecondarySet) ? trimmedCode + " - " + orgUnit.partyNameLang : trimmedCode + " - " + orgUnit.partyName;
		} else {
			context.orgUnitDesc = "Y".equals(context.localeSecondarySet) ? orgUnit.partyNameLang : orgUnit.partyName;
		}
	}
}

Debug.log("context.isWorkEffortViewFormReadOnly " + isWorkEffortViewFormReadOnly);
context.isWorkEffortViewFormReadOnly = isWorkEffortViewFormReadOnly;
