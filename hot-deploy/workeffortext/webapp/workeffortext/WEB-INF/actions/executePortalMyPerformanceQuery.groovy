import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Script per eseguire la query del portale "Mie performance" con gestione
 * del filtro OR per utenti EMPLVALUTATO_VIEW
 */

def module = "executePortalMyPerformanceQuery.groovy";

// Verifica se è necessario applicare il filtro OR per utenti EMPLVALUTATO_VIEW
def applyOrFilter = context.portalEmplValutatoFilter ?: false;

if (applyOrFilter) {
    /* (1) Codice precedente alla fix per comprendere schede in stato final:
    Debug.logInfo("executePortalMyPerformanceQuery: Esecuzione query per utente EMPLVALUTATO_VIEW con filtro OR", module);
    
    // Per utenti EMPLVALUTATO_VIEW: esegui query senza filtro di stato, poi applica filtro OR
    def conditions = [];
    conditions.add(EntityCondition.makeCondition("userLoginId", userLogin.userLoginId));
    
    def mainCondition = EntityCondition.makeCondition(conditions);
    
    // Esegui la query su tutti gli stati
    def allResults = delegator.findList("MyPerformance", mainCondition, null, 
        ["estimatedStartDate DESC", "estimatedCompletionDate DESC", "orgUnitName", "partyName", "workEffortName", "stDescription"], 
        null, false);
    
    Debug.logInfo("executePortalMyPerformanceQuery: Risultati totali trovati: " + allResults.size(), module);
    
    // Applica filtro OR per stati WEEVALST_EXECSHARED e WEEVALST_EXECFINAL
    def statusConditions = [];
    statusConditions.add(EntityCondition.makeCondition("currentStatusId", "WEEVALST_EXECSHARED"));
    statusConditions.add(EntityCondition.makeCondition("currentStatusId", "WEEVALST_EXECFINAL"));
    def orCondition = EntityCondition.makeCondition(statusConditions, EntityOperator.OR);
    
    // Filtra i risultati
    context.listIt = EntityUtil.filterByCondition(allResults, orCondition);
    
    Debug.logInfo("executePortalMyPerformanceQuery: Risultati dopo filtro OR: " + context.listIt.size() + 
        " (stati: WEEVALST_EXECSHARED, WEEVALST_EXECFINAL)", module);
    */

    Debug.logInfo("executePortalMyPerformanceQuery: Esecuzione query per utente EMPLVALUTATO_VIEW con filtro OR (robusto)", module);

    // Per utenti EMPLVALUTATO_VIEW: per evitare che l'aggregazione/group-by nella view faccia perdere righe,
    // prendiamo prima gli workEffortId che hanno stato SHARED o FINAL e poi recuperiamo i record per quegli ID.
    def baseCond = EntityCondition.makeCondition("userLoginId", userLogin.userLoginId)

    // workEffortId con SHARED
    def sharedCond = EntityCondition.makeCondition([baseCond, EntityCondition.makeCondition("currentStatusId", "WEEVALST_EXECSHARED")], EntityOperator.AND)
    def sharedRows = delegator.findList("MyPerformance", sharedCond, UtilMisc.toSet("workEffortId"), null, null, false)
    def sharedIds = (sharedRows.collect { it.workEffortId } as List).findAll { it != null } as Set

    // workEffortId con FINAL
    def finalCond = EntityCondition.makeCondition([baseCond, EntityCondition.makeCondition("currentStatusId", "WEEVALST_EXECFINAL")], EntityOperator.AND)
    def finalRows = delegator.findList("MyPerformance", finalCond, UtilMisc.toSet("workEffortId"), null, null, false)
    def finalIds = (finalRows.collect { it.workEffortId } as List).findAll { it != null } as Set

    // unione degli ID (OR semantics)
    def ids = (sharedIds + finalIds) as Set
    Debug.logInfo("executePortalMyPerformanceQuery: sharedIds=" + sharedIds.size() + ", finalIds=" + finalIds.size() + ", union=" + ids.size(), module)

    if (ids && !ids.isEmpty()) {
        def condWithIds = EntityCondition.makeCondition([baseCond, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, ids.toList())], EntityOperator.AND)
        context.listIt = delegator.findList("MyPerformance", condWithIds, null,
            ["estimatedStartDate DESC", "estimatedCompletionDate DESC", "orgUnitName", "partyName", "workEffortName", "stDescription"],
            null, false)
    } else {
        context.listIt = []
    }

    Debug.logInfo("executePortalMyPerformanceQuery: Risultati dopo union fetch: " + context.listIt.size(), module);

} else {
    Debug.logInfo("executePortalMyPerformanceQuery: Esecuzione query standard con filtro currentStatusId", module);
    
    // Per altri utenti: usa la query standard con filtro currentStatusId
    def conditions = [];
    conditions.add(EntityCondition.makeCondition("userLoginId", userLogin.userLoginId));
    
    /* (1) Codice precedente alla fix per comprendere schede in stato final:
    // Applica filtro di stato se specificato
    if (UtilValidate.isNotEmpty(context.currentStatusId)) {
    */

    // Applica filtro di stato se specificato: preferiamo context.currentStatusContains (csv) -> IN
    if (UtilValidate.isNotEmpty(context.currentStatusContains)) {
        def vals = context.currentStatusContains.split(',')*.trim()
        if (vals && vals.size() > 0) {
            conditions.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, vals as List))
        }
    } else if (UtilValidate.isNotEmpty(context.currentStatusId)) {
        conditions.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.LIKE, context.currentStatusId + "%"));
    }
    
    def mainCondition = EntityCondition.makeCondition(conditions);
    
    context.listIt = delegator.findList("MyPerformance", mainCondition, null, 
        ["estimatedStartDate DESC", "estimatedCompletionDate DESC", "orgUnitName", "partyName", "workEffortName", "stDescription"], 
        null, false);
    
    Debug.logInfo("executePortalMyPerformanceQuery: Query standard completata. Risultati: " + context.listIt.size(), module);
}
