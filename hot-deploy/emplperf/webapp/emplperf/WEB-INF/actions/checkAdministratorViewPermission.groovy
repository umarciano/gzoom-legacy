// Script per gestire il permesso ADMINISTRATOR_VIEW
// Gli amministratori possono vedere tutto ma NON possono modificare le valutazioni
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.Debug;

// Inizializzazione variabili
context.isAdministratorView = false;
context.hideEditButtons = false;

if (security != null && userLogin != null) {
    // Verifica se l'utente ha il permesso ADMINISTRATOR_VIEW
    def hasAdminPermission = security.hasPermission("ADMINISTRATOR_VIEW", userLogin);
    
    if (hasAdminPermission) {
        // L'utente è un amministratore - deve vedere tutto ma in read-only
        context.isAdministratorView = true;
        context.hideEditButtons = true;
        
        Debug.logInfo("ADMINISTRATOR_VIEW: Utente " + userLogin.partyId + 
            " - Modalità Read-Only attivata (visualizzazione completa senza modifica)", 
            "checkAdministratorViewPermission");
    } else {
        Debug.logInfo("ADMINISTRATOR_VIEW: Utente " + userLogin.partyId + 
            " - Non ha permesso amministratore, comportamento normale", 
            "checkAdministratorViewPermission");
    }
}
