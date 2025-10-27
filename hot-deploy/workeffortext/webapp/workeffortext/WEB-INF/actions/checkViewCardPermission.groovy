// Script per verificare i permessi di Presa Visione Scheda
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;

// Controllo permessi - di default false
hasViewCardPermission = false;

if (security != null && userLogin != null) {
    // 1. Verifica permesso EMPLPERFCARD_VIEW
    hasViewCardPermission = security.hasEntityPermission("EMPLPERFCARD", "_VIEW", userLogin);
    
    Debug.logInfo("checkViewCardPermission - hasViewCardPermission (EMPLPERFCARD_VIEW): " + hasViewCardPermission, "checkViewCardPermission");
    
    // 2. Se non ha il permesso, verifica se è censito come Valutato (WEM_EVAL_IN_CHARGE)
    if (!hasViewCardPermission && parameters.workEffortId != null) {
        Debug.logInfo("checkViewCardPermission - Checking if user is WEM_EVAL_IN_CHARGE for workEffortId: " + parameters.workEffortId, "checkViewCardPermission");
        
        try {
            // Cerca nella tabella WorkEffortPartyAssignment se l'utente è il Valutato
            def conditions = EntityCondition.makeCondition([
                EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, parameters.workEffortId),
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId),
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_IN_CHARGE")
            ], EntityOperator.AND);
            
            def assignments = delegator.findList("WorkEffortPartyAssignment", conditions, null, null, null, false);
            
            if (assignments && assignments.size() > 0) {
                hasViewCardPermission = true;
                Debug.logInfo("checkViewCardPermission - User IS WEM_EVAL_IN_CHARGE - Permission granted", "checkViewCardPermission");
            } else {
                Debug.logInfo("checkViewCardPermission - User is NOT WEM_EVAL_IN_CHARGE", "checkViewCardPermission");
            }
        } catch (Exception e) {
            Debug.logError("checkViewCardPermission - Error checking WEM_EVAL_IN_CHARGE: " + e.getMessage(), "checkViewCardPermission");
        }
    }
} 

Debug.logInfo("checkViewCardPermission - Final hasViewCardPermission: " + hasViewCardPermission, "checkViewCardPermission");
context.hasViewCardPermission = hasViewCardPermission;
