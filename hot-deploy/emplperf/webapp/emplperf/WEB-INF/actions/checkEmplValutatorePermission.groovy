// Script per gestire utenti con ruolo WEM_EVAL_MANAGER (Valutatori)
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

// Inizializzazione variabili per Valutatori
context.isValutatore = false;
context.hideUnitaResponsabile = false;
context.evalManagerPartyIdReadOnly = false;

if (userLogin != null && userLogin.partyId) {
    // Verifica se l'utente loggato ha il ruolo WEM_EVAL_MANAGER
    def userPartyRole = delegator.findOne("PartyRole", 
        [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_MANAGER"], false);
    
    if (userPartyRole) {
        // L'utente è un Valutatore
        context.isValutatore = true;
        context.hideUnitaResponsabile = true;
        context.evalManagerPartyIdReadOnly = true;
        
        // Precompila il campo evalManagerPartyId con l'utente loggato
        parameters.evalManagerPartyId = userLogin.partyId;
        
        Debug.logInfo("Valutatore " + userLogin.partyId + " - campo evalManagerPartyId impostato come read-only", 
            "checkValutatorePermission");
        
        // Trova tutti i Valutati assegnati a questo Valutatore
        // attraverso WorkEffortPartyAssignment
        def evalPartyIdConditions = [];
        evalPartyIdConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER"));
        evalPartyIdConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId));
        def evalManagerCondition = EntityCondition.makeCondition(evalPartyIdConditions, EntityOperator.AND);
        
        def workEffortAssignments = delegator.findList("WorkEffortPartyAssignment", 
            evalManagerCondition, null, null, null, false);
        
        if (workEffortAssignments) {
            // Raccogli tutti i workEffortId assegnati a questo Valutatore
            def workEffortIds = [];
            workEffortAssignments.each { assignment ->
                workEffortIds.add(assignment.workEffortId);
            }
            
            // Trova tutti i Valutati (WEM_EVAL_IN_CHARGE) associati a questi workEffort
            def valutatoConditions = [];
            valutatoConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_IN_CHARGE"));
            valutatoConditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
            def valutatoCondition = EntityCondition.makeCondition(valutatoConditions, EntityOperator.AND);
            
            def valutatoAssignments = delegator.findList("WorkEffortPartyAssignment", 
                valutatoCondition, null, null, null, false);
            
            // Crea lista dei Valutati disponibili per questo Valutatore
            def availableValutati = [];
            def availableValutatiIds = [];
            def processedPartyIds = new HashSet();
            
            valutatoAssignments.each { assignment ->
                if (!processedPartyIds.contains(assignment.partyId)) {
                    processedPartyIds.add(assignment.partyId);
                    availableValutatiIds.add(assignment.partyId);
                    
                    // Ottieni i dettagli del party
                    def partyView = delegator.findOne("PartyNameView", 
                        [partyId: assignment.partyId], false);
                    
                    if (partyView) {
                        availableValutati.add([
                            partyId: assignment.partyId,
                            groupName: partyView.groupName ?: partyView.firstName + " " + partyView.lastName
                        ]);
                    }
                }
            }
            
            // Ordina per nome
            availableValutati.sort { it.groupName };
            context.availableValutatiList = availableValutati;
            context.availableValutatiIds = availableValutatiIds;
            
            Debug.logInfo("Valutatore " + userLogin.partyId + " ha accesso a " + 
                availableValutati.size() + " Valutati", "checkValutatorePermission");
        } else {
            // Valutatore senza assegnazioni - lista vuota
            context.availableValutatiList = [];
            context.availableValutatiIds = [];
            
            Debug.logInfo("Valutatore " + userLogin.partyId + " non ha Valutati assegnati", 
                "checkValutatorePermission");
        }
    }
}
