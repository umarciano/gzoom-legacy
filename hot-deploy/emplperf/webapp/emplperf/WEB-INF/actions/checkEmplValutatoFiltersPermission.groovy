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

// Inizializza variabili di default
context.isEmplValutato = false;
context.hideAllFiltersExceptScheda = false;
context.useWorkEffortPartyView = false;
context.userPartyId = "";

// Verifica se l'utente è loggato e ha il permesso EMPLVALUTATO_VIEW
// DEPRECATO: Logica spostata in checkEnableNewThrowReport.groovy
// Mantenuto vuoto per compatibilità

if (userLogin) {
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
