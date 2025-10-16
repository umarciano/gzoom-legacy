/*
 * Script per gestire i permessi dei filtri per utenti Valutato nel menu GP_MENU_00208 
 * DEPRECATO: Funzionalità integrata in checkEnableNewThrowReport.groovy
 * Mantenuto per compatibilità ma non più utilizzato
 */

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.security.Security;

// Leggi variabili dalla sessione (impostate da checkEnableNewThrowReport.groovy)
context.isEmplValutato = session.getAttribute("isEmplValutato") ?: false;
context.isEmplValutatore = session.getAttribute("isEmplValutatore") ?: false;
context.hideAllFiltersExceptScheda = session.getAttribute("hideAllFiltersExceptScheda") ?: false;
context.useWorkEffortPartyView = session.getAttribute("useWorkEffortPartyView") ?: false;
context.userPartyId = session.getAttribute("userPartyId") ?: "";
context.evaluatedPartyIds = session.getAttribute("evaluatedPartyIds") ?: "";

println "CHECKEMPLVALUTATO_DEBUG: isEmplValutato=" + context.isEmplValutato;
println "CHECKEMPLVALUTATO_DEBUG: isEmplValutatore=" + context.isEmplValutatore;
println "CHECKEMPLVALUTATO_DEBUG: evaluatedPartyIds=" + context.evaluatedPartyIds;

// DEPRECATO: Vecchia logica basata su security permission
// Mantenuto solo come fallback se non ci sono variabili di sessione
if (!context.isEmplValutato && !context.isEmplValutatore && userLogin) {
    Security security = request.getAttribute("security");
    
    if (security && security.hasPermission("EMPLVALUTATO_VIEW", userLogin)) {
        context.isEmplValutato = true;
        context.hideAllFiltersExceptScheda = true;
        context.useWorkEffortPartyView = true;
        
        // Salva il partyId dell'utente per il filtering
        def userPartyId = userLogin.partyId;
        if (userPartyId) {
            context.userPartyId = userPartyId;
        }
    }
}
