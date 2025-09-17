import org.ofbiz.base.util.*;
import org.ofbiz.security.Security;

def result = "success";

def enableNewReport = UtilProperties.getPropertyValue("BaseConfig", "report.enableNewReport", "Y");
if ("Y".equals(enableNewReport)) {    
    result = "successNewReport";
}

// Aggiungi logica per controllo permessi EMPLVALUTATO_VIEW
// Inizializza variabili di default nel context E nella sessione
context.isEmplValutato = false;
context.hideAllFiltersExceptScheda = false;
context.useWorkEffortPartyView = false;
context.userPartyId = "";

// Verifica se l'utente è loggato e ha il permesso EMPLVALUTATO_VIEW
if (userLogin) {
    Security security = request.getAttribute("security");
    
    if (security && security.hasPermission("EMPLVALUTATO_VIEW", userLogin)) {
        // Log essenziale per audit
        println "EMPLVALUTATO_VIEW: Filtri applicati per utente " + userLogin.userLoginId;
        
        context.isEmplValutato = true;
        context.hideAllFiltersExceptScheda = true;
        context.useWorkEffortPartyView = true;
        
        // IMPORTANTE: Salva anche nella sessione per le chiamate AJAX
        session.setAttribute("isEmplValutato", true);
        session.setAttribute("hideAllFiltersExceptScheda", true);
        session.setAttribute("useWorkEffortPartyView", true);
        
        // Salva il partyId dell'utente per il filtering
        def userPartyId = userLogin.partyId;
        if (userPartyId) {
            context.userPartyId = userPartyId;
            session.setAttribute("userPartyId", userPartyId);
        }
    } else {
        // Pulisci attributi di sessione per utenti normali
        session.removeAttribute("isEmplValutato");
        session.removeAttribute("hideAllFiltersExceptScheda");
        session.removeAttribute("useWorkEffortPartyView");
        session.removeAttribute("userPartyId");
    }
}

return result;