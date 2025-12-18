import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;


def isPortletReadOnly = false;

def specialized = parameters.specialized;
def customTimePeriodId = parameters.customTimePeriodId;
def glFiscalTypeEnumId = parameters.glFiscalTypeEnumId;
def parentWorkEffortTypeId = parameters.parentWorkEffortTypeId;

def weTransPeriodIsClosed = UtilValidate.isNotEmpty(context.weTransPeriodIsClosed) ? context.weTransPeriodIsClosed : parameters.weTransPeriodIsClosed;
def isPosted = UtilValidate.isNotEmpty(context.isPosted) ? context.isPosted : parameters.isPosted;
def crudEnumIdSecondary = UtilValidate.isNotEmpty(context.crudEnumIdSecondary) ? context.crudEnumIdSecondary : parameters.crudEnumIdSecondary;
def valModId = UtilValidate.isNotEmpty(context.valModId) ? context.valModId : parameters.valModId;
def weTransTypeValueId = UtilValidate.isNotEmpty(context.weTransTypeValueId) ? context.weTransTypeValueId : parameters.weTransTypeValueId;

def isReadOnly = UtilValidate.isNotEmpty(context.isReadOnly) ? context.isReadOnly : parameters.isReadOnly;

// ===== VERIFICA STATO SCHEDA DI VALUTAZIONE =====
// Se la scheda è in stato "Valutazione Conclusa" (WEEVALST_EXECFINAL), deve essere disabilitata
def workEffortId = parameters.workEffortId ?: context.workEffortId;
def isEvaluationCompleted = false;
if (UtilValidate.isNotEmpty(workEffortId)) {
	try {
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);
		if (UtilValidate.isNotEmpty(workEffort)) {
			def currentStatusId = workEffort.currentStatusId;
			isEvaluationCompleted = "WEEVALST_EXECFINAL".equals(currentStatusId);
			Debug.logInfo("=== DEBUG - Stato scheda valutazione: " + currentStatusId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
			Debug.logInfo("=== DEBUG - isEvaluationCompleted (WEEVALST_EXECFINAL): " + isEvaluationCompleted + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		}
	} catch (Exception e) {
		Debug.logError("=== ERROR checking workEffort status: " + e.getMessage() + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	}
}

// ===== VERIFICA SE L'UTENTE È UN VALUTATORE SULLA SCHEDA CORRENTE =====
// L'eccezione si applica solo se accede da GP_MENU_00139 (Valutazione) dove rootInqyTree=N
// NON si applica se accede da GP_MENU_00142 (Interrogazione/Consultazione) dove rootInqyTree=Y
def isEvaluatorOnCard = checkIfUserIsEvaluatorOnCard();
def forceReadOnly = UtilValidate.isNotEmpty(context.forceReadOnly) ? context.forceReadOnly : parameters.forceReadOnly;
def rootInqyTree = parameters.rootInqyTree;

// Determina se siamo in modalità consultazione/interrogazione
// GP_MENU_00142 (Interrogazione Schede Individuali) passa rootInqyTree=Y -> modalità SOLA LETTURA
// GP_MENU_00139 (Valutazione Risultati Individuali) passa rootInqyTree=N -> modalità VALUTAZIONE
def isConsultationMode = "Y".equals(rootInqyTree);

// ===== DEBUG: Log parametri in ingresso =====
Debug.logInfo("=== DEBUG checkWorkEffortTransactionViewPortletReadOnly: INIZIO CONTROLLO ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - User Login: " + context.userLogin?.userLoginId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - workEffortId (parameters): " + parameters.workEffortId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - workEffortId (context): " + context.workEffortId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - isEvaluatorOnCard: " + isEvaluatorOnCard + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - forceReadOnly: " + forceReadOnly + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - rootInqyTree: " + rootInqyTree + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - isConsultationMode (rootInqyTree='Y'): " + isConsultationMode + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - portalPageId: " + parameters.portalPageId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - rootInqyTree: " + parameters.rootInqyTree + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - isPosted: " + isPosted + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - weTransPeriodIsClosed: " + weTransPeriodIsClosed + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - crudEnumIdSecondary: " + crudEnumIdSecondary + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - valModId: " + valModId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - weTransTypeValueId: " + weTransTypeValueId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - isReadOnly (originale): " + isReadOnly + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - parentWorkEffortTypeId: " + parentWorkEffortTypeId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - customTimePeriodId: " + customTimePeriodId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - glFiscalTypeEnumId: " + glFiscalTypeEnumId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");


if ("Y".equals(parameters.rootInqyTree) || "Y".equals(isPosted)) {
	Debug.logInfo("=== DEBUG - DISABILITAZIONE: rootInqyTree='Y' O isPosted='Y' ===", "checkWorkEffortTransactionViewPortletReadOnly");
	isPortletReadOnly = true;
} else if (isEvaluationCompleted) {
	Debug.logInfo("=== DEBUG - DISABILITAZIONE: Scheda in stato 'Valutazione Conclusa' (WEEVALST_EXECFINAL) ===", "checkWorkEffortTransactionViewPortletReadOnly");
	isPortletReadOnly = true;
	context.hideEditButtons = true; // Nasconde anche i pulsanti Salva e Rimuovi
} else if (! checkWorkEffortPermissions()) {
	Debug.logInfo("=== DEBUG - DISABILITAZIONE: Utente senza permessi necessari ===", "checkWorkEffortTransactionViewPortletReadOnly");
	isPortletReadOnly = true;
} else {
	// GN-5256
	//def adminPermission = getAdminPermission(parentWorkEffortTypeId, specialized);
	//if (security.hasPermission(adminPermission, context.userLogin)) {
	//	isPortletReadOnly = false;
	//} else 
	//	{
		def periodIsClosed = "Y".equals(weTransPeriodIsClosed);
		def crudIsNone = "NONE".equals(crudEnumIdSecondary);
		def allNotMod = "ALL_NOT_MOD".equals(valModId);
		def actualNotModMatch = isActualMod(valModId, weTransTypeValueId);
		def budgetNotModMatch = isBudgetMod(valModId, weTransTypeValueId);
		
		// ECCEZIONE VALUTATORE: Se l'utente è un Valutatore sulla scheda E NON è in modalità consultazione,
		// ignora il flag isReadOnly (che può essere impostato da forceReadOnly del portale/gruppo NOPORTAL_MY)
		// L'eccezione si applica solo quando rootInqyTree != 'Y' (quindi in modalità valutazione GP_MENU_00139)
		def readOnlyFlag = false;
		if (isEvaluatorOnCard && !isConsultationMode) {
			Debug.logInfo("=== DEBUG - ECCEZIONE VALUTATORE: Utente è Valutatore sulla scheda e NON in modalità consultazione (rootInqyTree!='Y') - isReadOnly viene IGNORATO ===", "checkWorkEffortTransactionViewPortletReadOnly");
			readOnlyFlag = false; // Ignora isReadOnly per i Valutatori che accedono dalla Valutazione (GP_MENU_00139)
		} else {
			readOnlyFlag = isTrue(isReadOnly);
		}
		
		def rootHasPeriodCheck = rootHasPeriod(parentWorkEffortTypeId, customTimePeriodId, glFiscalTypeEnumId);
		
		Debug.logInfo("=== DEBUG - Controllo condizioni di disabilitazione: ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   periodIsClosed (weTransPeriodIsClosed='Y'): " + periodIsClosed + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   crudIsNone (crudEnumIdSecondary='NONE'): " + crudIsNone + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   allNotMod (valModId='ALL_NOT_MOD'): " + allNotMod + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   actualNotModMatch (ACTUAL_NOT_MOD + ACTUAL): " + actualNotModMatch + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   budgetNotModMatch (BUDGET_NOT_MOD + BUDGET): " + budgetNotModMatch + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   readOnlyFlag (isReadOnly=true): " + readOnlyFlag + " (originale: " + isReadOnly + ") ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   rootHasPeriod (periodo NON valido): " + (!rootHasPeriodCheck) + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		
		isPortletReadOnly = periodIsClosed || crudIsNone || allNotMod || actualNotModMatch || budgetNotModMatch || readOnlyFlag || ! rootHasPeriodCheck;
		
		if (isPortletReadOnly) {
			Debug.logInfo("=== DEBUG - DISABILITAZIONE per una delle condizioni sopra elencate ===", "checkWorkEffortTransactionViewPortletReadOnly");
		} else {
			Debug.logInfo("=== DEBUG - ABILITAZIONE: Tutte le condizioni sono soddisfatte ===", "checkWorkEffortTransactionViewPortletReadOnly");
		}
	//	}
}

def checkWorkEffortPermissions() {
	def hasAdmin = security.hasPermission("WORKEFFORTMGR_ADMIN", context.userLogin);
	def hasCreate = security.hasPermission("WORKEFFORTMGR_CREATE", context.userLogin);
	def hasUpdate = security.hasPermission("WORKEFFORTMGR_UPDATE", context.userLogin);
	def hasOrgAdmin = security.hasPermission("WORKEFFORTORG_ADMIN", context.userLogin);
	def hasRoleAdmin = security.hasPermission("WORKEFFORTROLE_ADMIN", context.userLogin);
	
	def hasPermission = hasAdmin || hasCreate || hasUpdate || hasOrgAdmin || hasRoleAdmin;
	
	Debug.logInfo("=== DEBUG - Controllo permessi utente: ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   WORKEFFORTMGR_ADMIN: " + hasAdmin + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   WORKEFFORTMGR_CREATE: " + hasCreate + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   WORKEFFORTMGR_UPDATE: " + hasUpdate + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   WORKEFFORTORG_ADMIN: " + hasOrgAdmin + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   WORKEFFORTROLE_ADMIN: " + hasRoleAdmin + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   Ha almeno un permesso: " + hasPermission + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	
	return hasPermission;
}

def getAdminPermission(parentWorkEffortTypeId, specialized) {
	if ("Y".equals(specialized) && UtilValidate.isNotEmpty(parentWorkEffortTypeId)) {
		def parentWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : parentWorkEffortTypeId], false);
		if (UtilValidate.isNotEmpty(parentWorkEffortType)) {
			def weContextId = parentWorkEffortType.parentTypeId;
			return ContextPermissionPrefixEnum.getPermissionPrefix(weContextId) + "MGR_ADMIN";
		}
	}
	return "WORKEFFORTMGR_ADMIN";
}

def isActualMod(valModId, weTransTypeValueId) {
	return "ACTUAL_NOT_MOD".equals(valModId) && "ACTUAL".equals(weTransTypeValueId);
}

def isBudgetMod(valModId, weTransTypeValueId) {
	return "BUDGET_NOT_MOD".equals(valModId) && "BUDGET".equals(weTransTypeValueId);
}

def isTrue(isReadOnly) {
	return UtilValidate.isNotEmpty(isReadOnly) && isReadOnly.booleanValue();
}

def checkIfUserIsEvaluatorOnCard() {
	// Verifica se l'utente loggato è un Valutatore (WEM_EVAL_MANAGER) sulla scheda workEffortId
	// Prova prima dai parameters, poi dal context
	def workEffortId = parameters.workEffortId ?: context.workEffortId;
	def userLoginId = context.userLogin?.userLoginId;
	def partyId = context.userLogin?.partyId;
	
	Debug.logInfo("=== DEBUG checkIfUserIsEvaluatorOnCard: workEffortId=" + workEffortId + ", partyId=" + partyId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	
	if (UtilValidate.isEmpty(workEffortId) || UtilValidate.isEmpty(partyId)) {
		Debug.logInfo("=== DEBUG checkIfUserIsEvaluatorOnCard: workEffortId o partyId mancanti - return false ===", "checkWorkEffortTransactionViewPortletReadOnly");
		return false;
	}
	
	try {
		// Cerca WorkEffortPartyAssignment con roleTypeId = WEM_EVAL_MANAGER
		def assignmentConditions = [];
		assignmentConditions.add(EntityCondition.makeCondition("workEffortId", workEffortId));
		assignmentConditions.add(EntityCondition.makeCondition("partyId", partyId));
		assignmentConditions.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_MANAGER"));
		
		def assignments = delegator.findList("WorkEffortPartyAssignment", 
			EntityCondition.makeCondition(assignmentConditions), 
			null, null, null, false);
		
		def isEvaluator = UtilValidate.isNotEmpty(assignments);
		
		Debug.logInfo("=== DEBUG checkIfUserIsEvaluatorOnCard: userLoginId=" + userLoginId + ", partyId=" + partyId + 
			", workEffortId=" + workEffortId + ", isEvaluator=" + isEvaluator + 
			" (assignments found: " + (assignments ? assignments.size() : 0) + ") ===", "checkWorkEffortTransactionViewPortletReadOnly");
		
		return isEvaluator;
	} catch (Exception e) {
		Debug.logError("=== ERROR checkIfUserIsEvaluatorOnCard: " + e.getMessage() + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		return false;
	}
}

def rootHasPeriod(parentWorkEffortTypeId, customTimePeriodId, glFiscalTypeEnumId) {
	Debug.logInfo("=== DEBUG - Verifica rootHasPeriod: ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   parentWorkEffortTypeId: " + parentWorkEffortTypeId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   customTimePeriodId: " + customTimePeriodId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   glFiscalTypeEnumId: " + glFiscalTypeEnumId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	Debug.logInfo("=== DEBUG -   defaultOrganizationPartyId: " + context.defaultOrganizationPartyId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
	
	def conditionList = [];
	conditionList.add(EntityCondition.makeCondition("workEffortTypeId", parentWorkEffortTypeId));
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("glFiscalTypeEnumId", glFiscalTypeEnumId));
	conditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
	
	def periodList = delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(conditionList), null, null, null, false);
	if (UtilValidate.isEmpty(periodList)) {
		Debug.logInfo("=== DEBUG -   Nessun periodo trovato in WorkEffortTypePeriod - ABILITATO (default) ===", "checkWorkEffortTransactionViewPortletReadOnly");
		return true;
	}
	def periodItem = EntityUtil.getFirst(periodList);
	if (UtilValidate.isNotEmpty(periodItem)) {
		def statusEnumId = periodItem.statusEnumId;
		def isValidStatus = "OPEN".equals(statusEnumId) || "REOPEN".equals(statusEnumId) || "DETECTABLE".equals(statusEnumId);
		Debug.logInfo("=== DEBUG -   Periodo trovato con statusEnumId: " + statusEnumId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		Debug.logInfo("=== DEBUG -   Status è valido (OPEN/REOPEN/DETECTABLE): " + isValidStatus + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
		return isValidStatus;
	}
	Debug.logInfo("=== DEBUG -   Periodo trovato ma vuoto - ABILITATO (default) ===", "checkWorkEffortTransactionViewPortletReadOnly");
	return true;
}

// GN-CUSTOM: Controllo permesso ADMINISTRATOR_VIEW
// Gli amministratori con questo permesso possono vedere tutto ma NON possono modificare
// ECCEZIONE: Possono modificare SOLO la Performance Strategica (CTX_BS)
if (security != null && userLogin != null) {
    def hasAdminViewPermission = security.hasPermission("ADMINISTRATOR_VIEW", userLogin);
    
    if (hasAdminViewPermission) {
        // Verifica se siamo in Performance Strategica (CTX_BS)
        def isStrategicPerformance = false;
        
        if (UtilValidate.isNotEmpty(parentWorkEffortTypeId)) {
            def parentWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : parentWorkEffortTypeId], false);
            if (UtilValidate.isNotEmpty(parentWorkEffortType)) {
                def weContextId = parentWorkEffortType.parentTypeId;
                isStrategicPerformance = "CTX_BS".equals(weContextId);
                
                Debug.logInfo("=== GN-CUSTOM: ADMINISTRATOR_VIEW - parentWorkEffortTypeId: " + parentWorkEffortTypeId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
                Debug.logInfo("=== GN-CUSTOM: ADMINISTRATOR_VIEW - weContextId: " + weContextId + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
                Debug.logInfo("=== GN-CUSTOM: ADMINISTRATOR_VIEW - isStrategicPerformance (CTX_BS): " + isStrategicPerformance + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
            }
        }
        
        // Se NON è Performance Strategica, forza read-only
        if (!isStrategicPerformance) {
            isPortletReadOnly = true;
            context.isAdministratorView = true;
            context.hideEditButtons = true;
            
            Debug.logInfo("=== GN-CUSTOM: ADMINISTRATOR_VIEW ===", "checkWorkEffortTransactionViewPortletReadOnly");
            Debug.logInfo("=== GN-CUSTOM: Utente " + userLogin.partyId + " ha il permesso ADMINISTRATOR_VIEW - form forzato in read-only (NON Performance Strategica) ===", "checkWorkEffortTransactionViewPortletReadOnly");
        } else {
            Debug.logInfo("=== GN-CUSTOM: ADMINISTRATOR_VIEW ===", "checkWorkEffortTransactionViewPortletReadOnly");
            Debug.logInfo("=== GN-CUSTOM: Utente " + userLogin.partyId + " ha il permesso ADMINISTRATOR_VIEW - MODIFICA ABILITATA per Performance Strategica (CTX_BS) ===", "checkWorkEffortTransactionViewPortletReadOnly");
        }
    }
}

// Determina se siamo in Performance Strategica (CTX_BS) per gestire il campo weTransValue
// Questo flag viene usato nel form XML per differenziare tra input numerico (CTX_BS) e dropdown 1-5 (altri contesti)
def isStrategicPerformanceContext = false;
if (UtilValidate.isNotEmpty(parentWorkEffortTypeId)) {
    def parentWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : parentWorkEffortTypeId], false);
    if (UtilValidate.isNotEmpty(parentWorkEffortType)) {
        def weContextId = parentWorkEffortType.parentTypeId;
        isStrategicPerformanceContext = "CTX_BS".equals(weContextId);
    }
}
context.isStrategicPerformance = isStrategicPerformanceContext;
Debug.logInfo("=== DEBUG - isStrategicPerformance (per campo weTransValue): " + isStrategicPerformanceContext + " ===", "checkWorkEffortTransactionViewPortletReadOnly");

context.isPortletFormDisabled = isPortletReadOnly ? "Y" : "N";
Debug.logInfo("=== DEBUG checkWorkEffortTransactionViewPortletReadOnly: RISULTATO FINALE ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - isPortletFormDisabled = " + context.isPortletFormDisabled + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG - La dropdown weTransValue sara': " + (context.isPortletFormDisabled == "Y" ? "DISABILITATA" : "ABILITATA") + " ===", "checkWorkEffortTransactionViewPortletReadOnly");
Debug.logInfo("=== DEBUG checkWorkEffortTransactionViewPortletReadOnly: FINE CONTROLLO ===", "checkWorkEffortTransactionViewPortletReadOnly");