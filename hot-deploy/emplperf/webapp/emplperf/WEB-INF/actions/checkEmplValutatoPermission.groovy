// Script per gestire il permesso EMPLVALUTATO_VIEW e manipolare il campo evalPartyId
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;

// Inizializzazione variabili
context.evalPartyIdReadOnly = false;

if (security != null && userLogin != null) {
    // Verifica se l'utente ha il permesso EMPLVALUTATO_VIEW
    def hasPermission = security.hasPermission("EMPLVALUTATO_VIEW", userLogin);
    
    if (hasPermission && userLogin?.partyId) {
        // Verifica che l'utente loggato sia presente nella dropdown WEM_EVAL_IN_CHARGE
        def userPartyRole = delegator.findOne("PartyRoleView", 
            [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_IN_CHARGE"], false);
        
        if (userPartyRole && userPartyRole.partyName && userPartyRole.parentRoleCode) {
            // L'utente è presente nella dropdown - comportamento normale
            
            // Forza il valore nei parameters
            parameters.evalPartyId = userLogin.partyId;
            
            // Auto-popola anche il campo Stato Attuale con "Valutazione da Completare"
            parameters.weStatusDescr = "Valutazione da Completare";
            parameters.weStatusDescrLang = "Valutazione da Completare";
            
            // Imposta flag per indicare che il campo deve essere read-only
            context.evalPartyIdReadOnly = true;
            
            Debug.logInfo("EMPLVALUTATO_VIEW: Campo evalPartyId impostato come read-only per utente " + 
                userLogin.partyId + " (" + userPartyRole.partyName + " - " + userPartyRole.parentRoleCode + ")", 
                "checkEmplValutatoPermission");
                
        } else {
            // L'utente ha il permesso ma NON è nella dropdown - applica sicurezza preventiva
            
            // Forza valori che garantiscono nessun risultato
            parameters.evalPartyId = userLogin.partyId;
            parameters.sourceReferenceId = "NO_RESULT_SECURITY_FILTER";
            parameters.weStatusDescr = "Valutazione da Completare";
            parameters.weStatusDescrLang = "Valutazione da Completare";
            
            // Imposta flag per indicare che i campi devono essere read-only
            context.evalPartyIdReadOnly = true;
            
            Debug.logInfo("EMPLVALUTATO_VIEW: Utente " + userLogin.partyId + 
                " ha il permesso ma non è presente nella dropdown WEM_EVAL_IN_CHARGE - applicato filtro di sicurezza", 
                "checkEmplValutatoPermission");
        }
    }
}
