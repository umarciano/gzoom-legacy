// Script per gestire il permesso EMPLVALUTATORE_VIEW e manipolare il campo evalManagerPartyId
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;

// Inizializzazione variabili
context.evalManagerPartyIdReadOnly = false;

if (security != null && userLogin != null) {
    // Verifica se l'utente ha il permesso EMPLVALUTATORE_VIEW
    def hasPermission = security.hasPermission("EMPLVALUTATORE_VIEW", userLogin);
    
    if (hasPermission && userLogin?.partyId) {
        // Verifica che l'utente loggato sia presente nella dropdown WEM_EVAL_MANAGER
        def userPartyRole = delegator.findOne("PartyRoleView", 
            [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_MANAGER"], false);
        
        if (userPartyRole && userPartyRole.partyName && userPartyRole.parentRoleCode) {
            // L'utente è presente nella dropdown - comportamento normale
            
            // Forza il valore nei parameters
            parameters.evalManagerPartyId = userLogin.partyId;
            
            // Crea lista per la dropdown read-only
            def evalManagerPartyIdList = [];
            evalManagerPartyIdList.add([partyId: userLogin.partyId, 
                                       partyName: userPartyRole.partyName, 
                                       parentRoleCode: userPartyRole.parentRoleCode]);
            context.evalManagerPartyIdList = evalManagerPartyIdList;
            
            // Imposta flag per indicare che il campo deve essere read-only
            context.evalManagerPartyIdReadOnly = true;
            
            Debug.logInfo("EMPLVALUTATORE_VIEW: Campo evalManagerPartyId impostato come read-only per utente " + 
                userLogin.partyId + " (" + userPartyRole.partyName + " - " + userPartyRole.parentRoleCode + ")", 
                "checkEmplValutatorePermission");
                
        } else {
            // L'utente ha il permesso ma NON è nella dropdown - applica sicurezza preventiva
            
            // Forza valori che garantiscono nessun risultato
            parameters.evalManagerPartyId = userLogin.partyId;
            parameters.sourceReferenceId = "NO_RESULT_SECURITY_FILTER";
            
            // Crea lista vuota per la dropdown read-only
            def evalManagerPartyIdList = [];
            evalManagerPartyIdList.add([partyId: userLogin.partyId, 
                                       partyName: "Utente non autorizzato", 
                                       parentRoleCode: "N/A"]);
            context.evalManagerPartyIdList = evalManagerPartyIdList;
            
            // Imposta flag per indicare che i campi devono essere read-only
            context.evalManagerPartyIdReadOnly = true;
            
            Debug.logInfo("EMPLVALUTATORE_VIEW: Utente " + userLogin.partyId + 
                " ha il permesso ma non è presente nella dropdown WEM_EVAL_MANAGER - applicato filtro di sicurezza", 
                "checkEmplValutatorePermission");
        }
    }
}
