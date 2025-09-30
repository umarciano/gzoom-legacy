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
    
} else {
    Debug.logInfo("executePortalMyPerformanceQuery: Esecuzione query standard con filtro currentStatusId", module);
    
    // Per altri utenti: usa la query standard con filtro currentStatusId
    def conditions = [];
    conditions.add(EntityCondition.makeCondition("userLoginId", userLogin.userLoginId));
    
    // Applica filtro di stato se specificato
    if (UtilValidate.isNotEmpty(context.currentStatusId)) {
        conditions.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.LIKE, context.currentStatusId + "%"));
    }
    
    def mainCondition = EntityCondition.makeCondition(conditions);
    
    context.listIt = delegator.findList("MyPerformance", mainCondition, null, 
        ["estimatedStartDate DESC", "estimatedCompletionDate DESC", "orgUnitName", "partyName", "workEffortName", "stDescription"], 
        null, false);
    
    Debug.logInfo("executePortalMyPerformanceQuery: Query standard completata. Risultati: " + context.listIt.size(), module);
}
