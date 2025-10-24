// Script per verificare i permessi di Condivisione Valutazione
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;

Debug.logInfo("###############################################", "checkShareEvaluationPermission");
Debug.logInfo("=== SCRIPT EXECUTION STARTED ===", "checkShareEvaluationPermission");
Debug.logInfo("###############################################", "checkShareEvaluationPermission");

// Controllo permessi - di default false
hasShareEvaluationPermission = false;

Debug.logInfo("*** checkShareEvaluationPermission.groovy - START ***", "checkShareEvaluationPermission");
Debug.logInfo("workEffortId: " + workEffortId, "checkShareEvaluationPermission");
Debug.logInfo("parameters.workEffortId: " + parameters.workEffortId, "checkShareEvaluationPermission");
Debug.logInfo("parameters.contentId: " + parameters.contentId, "checkShareEvaluationPermission");
Debug.logInfo("userLogin: " + userLogin, "checkShareEvaluationPermission");
Debug.logInfo("security: " + security, "checkShareEvaluationPermission");

if (security != null && userLogin != null && workEffortId != null) {
    // 1. Verifica che l'utente abbia il permesso EMPLVALUTATORE_VIEW (Valutatore)
    boolean hasValutatorePermission = security.hasPermission("EMPLVALUTATORE_VIEW", userLogin);
    
    Debug.logInfo("hasValutatorePermission (EMPLVALUTATORE_VIEW): " + hasValutatorePermission, "checkShareEvaluationPermission");
    
    // 2. Verifica che l'utente sia censito come WEM_EVAL_MANAGER sulla scheda corrente
    boolean isEvalManager = false;
    
    def partyId = userLogin.getString("partyId");
    Debug.logInfo("partyId: " + partyId, "checkShareEvaluationPermission");
    
    // Cerca nella tabella WorkEffortPartyAssignment se l'utente ha il ruolo WEM_EVAL_MANAGER per questo workEffortId
    def conditions = [
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER")
    ];
    
    def andCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    def assignments = delegator.findList("WorkEffortPartyAssignment", andCondition, null, null, null, false);
    
    Debug.logInfo("WorkEffortPartyAssignment trovati: " + (assignments != null ? assignments.size() : 0), "checkShareEvaluationPermission");
    
    if (assignments != null && assignments.size() > 0) {
        isEvalManager = true;
        Debug.logInfo("Trovati assignment records: " + assignments, "checkShareEvaluationPermission");
    }
    
    // L'utente deve avere ENTRAMBI i requisiti:
    // - Permesso EMPLVALUTATORE_VIEW
    // - Ruolo WEM_EVAL_MANAGER sulla scheda specifica
    hasShareEvaluationPermission = hasValutatorePermission && isEvalManager;
    
    Debug.logInfo("isEvalManager: " + isEvalManager, "checkShareEvaluationPermission");
    Debug.logInfo("hasShareEvaluationPermission FINALE: " + hasShareEvaluationPermission, "checkShareEvaluationPermission");
} else {
    Debug.logInfo("Uno dei parametri è null - security: " + security + ", userLogin: " + userLogin + ", workEffortId: " + workEffortId, "checkShareEvaluationPermission");
}

context.hasShareEvaluationPermission = hasShareEvaluationPermission;

Debug.logInfo("###############################################", "checkShareEvaluationPermission");
Debug.logInfo("=== SCRIPT EXECUTION COMPLETED ===", "checkShareEvaluationPermission");
Debug.logInfo("context.hasShareEvaluationPermission FINAL: " + context.hasShareEvaluationPermission, "checkShareEvaluationPermission");
Debug.logInfo("###############################################", "checkShareEvaluationPermission");

Debug.logInfo("*** checkShareEvaluationPermission.groovy - END - hasShareEvaluationPermission: " + hasShareEvaluationPermission + " ***", "checkShareEvaluationPermission");
