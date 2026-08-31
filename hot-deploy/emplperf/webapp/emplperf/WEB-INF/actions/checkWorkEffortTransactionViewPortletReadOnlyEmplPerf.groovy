// GN-CUSTOM: Estensione dello script checkWorkEffortTransactionViewPortletReadOnly.groovy
// per gestire il permesso ADMINISTRATOR_VIEW
//
// Gli amministratori con ADMINISTRATOR_VIEW possono vedere tutto ma NON possono modificare le valutazioni
// Questo script aggiunge il controllo PRIMA degli altri controlli

import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.Debug;

// Prima esegui la logica standard
runAction("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkWorkEffortTransactionViewPortletReadOnly.groovy");

// Poi aggiungi il controllo ADMINISTRATOR_VIEW
if (security != null && userLogin != null) {
    def hasAdminPermission = security.hasPermission("ADMINISTRATOR_VIEW", userLogin);
    
    if (hasAdminPermission) {
        // L'utente è un amministratore con ADMINISTRATOR_VIEW
        // Deve vedere tutto ma in read-only - sovrascrive qualsiasi altro controllo
        context.isPortletFormDisabled = "Y";
        context.isAdministratorView = true;
        context.hideEditButtons = true;
        
        Debug.logInfo("ADMINISTRATOR_VIEW: Utente " + userLogin.partyId + 
            " ha il permesso ADMINISTRATOR_VIEW - form portlet impostato come read-only (isPortletFormDisabled=Y)", 
            "checkWorkEffortTransactionViewPortletReadOnlyEmplPerf");
    }
}
