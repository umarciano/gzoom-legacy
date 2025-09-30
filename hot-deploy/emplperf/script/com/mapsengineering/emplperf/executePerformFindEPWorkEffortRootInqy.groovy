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
 * Gestione speciale per utenti EMPLVALUTATO_VIEW
 * Se l'utente ha il permesso EMPLVALUTATO_VIEW:
 * 1. Rimuovi il filtro weStatusDescr per permettere la ricerca su tutti gli stati
 * 2. Dopo la ricerca, applica un filtro OR sui risultati per mostrare solo stati specifici
 */
def shouldApplyOrFilter = false;
if (security != null && userLogin != null) {
    def hasPermission = security.hasPermission("EMPLVALUTATO_VIEW", userLogin);
    if (hasPermission && userLogin?.partyId) {
        def userPartyRole = delegator.findOne("PartyRoleView", 
            [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_IN_CHARGE"], false);
        if (userPartyRole) {
            shouldApplyOrFilter = true;
            // Rimuovi il filtro di stato per permettere ricerca completa
            if (parameters.weStatusDescr == "Valutazione Condivisa") {
                parameters.remove("weStatusDescr");
                parameters.remove("weStatusDescrLang");
                Debug.logInfo("EMPLPERF: Rimosso filtro weStatusDescr per utente EMPLVALUTATO_VIEW: " + userLogin.partyId, "executePerformFindEPWorkEffortRootInqy");
            }
        }
    }
}

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
