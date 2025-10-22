import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***EP parameters.menuItem" + parameters.menuItem);
context.permission = "EMPLPERF"; 
parameters.weContextId = "CTX_EP";

/**
 * CRITICO: Forza l'applicazione dei filtri di sicurezza PRIMA della ricerca
 * per prevenire visualizzazione di risultati cached non pertinenti all'utente
 */
def shouldApplyOrFilter = false;
def userPartyId = null;

if (security != null && userLogin != null) {
    userPartyId = userLogin?.partyId;
    
    // Verifica permesso EMPLVALUTATO_VIEW
    def hasValutatoPermission = security.hasPermission("EMPLVALUTATO_VIEW", userLogin);
    if (hasValutatoPermission && userPartyId) {
        def userPartyRole = delegator.findOne("PartyRoleView", 
            [partyId: userPartyId, roleTypeId: "WEM_EVAL_IN_CHARGE"], false);
        if (userPartyRole) {
            shouldApplyOrFilter = true;
            
            // FORZA il filtro evalPartyId se non è già impostato
            if (UtilValidate.isEmpty(parameters.evalPartyId)) {
                parameters.evalPartyId = userPartyId;
                Debug.logInfo("EMPLPERF: Forzato filtro evalPartyId per utente EMPLVALUTATO_VIEW: " + userPartyId, "executePerformFindEPWorkEffortRootInqy");
            }
            
            // Rimuovi il filtro di stato per permettere ricerca completa
            if (parameters.weStatusDescr == "Valutazione Condivisa") {
                parameters.remove("weStatusDescr");
                parameters.remove("weStatusDescrLang");
                Debug.logInfo("EMPLPERF: Rimosso filtro weStatusDescr per utente EMPLVALUTATO_VIEW: " + userPartyId, "executePerformFindEPWorkEffortRootInqy");
            }
        } else {
            // Utente con permesso ma non nella dropdown - applica filtro di sicurezza
            parameters.evalPartyId = userPartyId;
            parameters.sourceReferenceId = "NO_RESULT_SECURITY_FILTER";
            Debug.logWarning("EMPLPERF: Applicato filtro di sicurezza NO_RESULT per utente " + userPartyId + " (permesso EMPLVALUTATO_VIEW ma non in dropdown)", "executePerformFindEPWorkEffortRootInqy");
        }
    }
    
    // Verifica permesso EMPLVALUTATORE_VIEW
    def hasValutatorePermission = security.hasPermission("EMPLVALUTATORE_VIEW", userLogin);
    if (hasValutatorePermission && userPartyId) {
        def userPartyRole = delegator.findOne("PartyRoleView", 
            [partyId: userPartyId, roleTypeId: "WEM_EVAL_MANAGER"], false);
        if (userPartyRole) {
            // FORZA il filtro evalManagerPartyId se non è già impostato
            if (UtilValidate.isEmpty(parameters.evalManagerPartyId)) {
                parameters.evalManagerPartyId = userPartyId;
                Debug.logInfo("EMPLPERF: Forzato filtro evalManagerPartyId per utente EMPLVALUTATORE_VIEW: " + userPartyId, "executePerformFindEPWorkEffortRootInqy");
            }
        } else {
            // Utente con permesso ma non nella dropdown - applica filtro di sicurezza
            parameters.evalManagerPartyId = userPartyId;
            parameters.sourceReferenceId = "NO_RESULT_SECURITY_FILTER";
            Debug.logWarning("EMPLPERF: Applicato filtro di sicurezza NO_RESULT per utente " + userPartyId + " (permesso EMPLVALUTATORE_VIEW ma non in dropdown)", "executePerformFindEPWorkEffortRootInqy");
        }
    }
}

/**
 * Gestione speciale per utenti EMPLVALUTATO_VIEW
 * Se l'utente ha il permesso EMPLVALUTATO_VIEW:
 * 1. Rimuovi il filtro weStatusDescr per permettere la ricerca su tutti gli stati
 * 2. Dopo la ricerca, applica un filtro OR sui risultati per mostrare solo stati specifici
 */

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);

/**
 * Se l'utente è un EMPLVALUTATO_VIEW, applica il filtro OR sui risultati
 */
if (shouldApplyOrFilter && UtilValidate.isNotEmpty(context.listIt)) {
    Debug.logInfo("EMPLPERF: Applicando filtro OR per stati WEEVALST_EXECSHARED e WEEVALST_EXECFINAL", "executePerformFindEPWorkEffortRootInqy");
    
    // Crea condizioni OR per gli stati WEEVALST_EXECSHARED e WEEVALST_EXECFINAL
    def condList = [];
    condList.add(EntityCondition.makeCondition("currentStatusId", "WEEVALST_EXECSHARED"));
    condList.add(EntityCondition.makeCondition("currentStatusId", "WEEVALST_EXECFINAL"));
    def orCondition = EntityCondition.makeCondition(condList, EntityOperator.OR);
    
    // Applica il filtro OR alla lista dei risultati
    def originalSize = context.listIt.size();
    context.listIt = EntityUtil.filterByCondition(context.listIt, orCondition);
    request.setAttribute("listIt", context.listIt);
    def newSize = context.listIt.size();
    
    Debug.logInfo("EMPLPERF: Filtro OR applicato. Risultati originali: " + originalSize + 
        ", risultati dopo filtro OR: " + newSize, "executePerformFindEPWorkEffortRootInqy");
}

/**
 * I filtri applicati i nquesta ricerca vanno applicati anche nel groovy executePerformFindEPWorkEffortRootInqy.groovy
 * perche altrimenti il cambio stato fa una query diversa...
 */
//Bug 35
if (UtilValidate.isNotEmpty(context.listIt) && (UtilValidate.isNotEmpty(parameters.evalManagerPartyId)
	|| UtilValidate.isNotEmpty(parameters.evalPartyId))) {
	
	if (UtilValidate.isNotEmpty(parameters.evalManagerPartyId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("partyId", parameters.evalManagerPartyId));
		condList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_MANAGER"));	//valutatore	
		def evalManagerList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
		def evalManagerWorkEffortId = EntityUtil.getFieldListFromEntityList(evalManagerList, "workEffortId", false);
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, evalManagerWorkEffortId));
		request.setAttribute("listIt", context.listIt);
	}
	if (UtilValidate.isNotEmpty(parameters.evalPartyId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("partyId", parameters.evalPartyId));
		condList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_IN_CHARGE"))	//valutato
		def evalList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
		def evalWorkEffortId = EntityUtil.getFieldListFromEntityList(evalList, "workEffortId", false);
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, evalWorkEffortId));
		request.setAttribute("listIt", context.listIt);
	}
	
}
	
return res;
