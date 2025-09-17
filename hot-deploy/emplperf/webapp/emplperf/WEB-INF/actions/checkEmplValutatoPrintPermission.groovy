import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;

/**
 * Script per il controllo permessi EMPLVALUTATO_VIEW per le stampe
 * Usato per filtrare la visibilità dei report nel menu GP_MENU_00208
 * Il Valutato deve vedere solo "Stampa scheda Obiettivi" e non "Lista Valutazioni Individuali"
 */

def hasEmplValutatoPermission = false;

// Verifica se l'utente ha il permesso EMPLVALUTATO_VIEW
if (security.hasPermission("EMPLVALUTATO_VIEW", userLogin)) {
    hasEmplValutatoPermission = true;
}

context.hasEmplValutatoPermission = hasEmplValutatoPermission;

// Se l'utente ha il permesso EMPLVALUTATO_VIEW, impostiamo una lista filtrata
// che esclude i report di tipo "Lista Valutazioni Individuali"
if (hasEmplValutatoPermission) {
    // Lista di contentId da escludere per gli utenti Valutato
    def excludedReportsForValutato = ["REPORT_SLVI", "REPORT_LVI", "REPORT_LVI_STA", "REPORT_LVI_RIE"];
    
    context.excludedReportsForValutato = excludedReportsForValutato;
}
