// Script per verificare i permessi di Presa Visione Scheda
import org.ofbiz.security.Security;
import org.ofbiz.entity.GenericValue;

// Controllo permessi - di default false
hasViewCardPermission = false;

if (security != null && userLogin != null) {
    hasViewCardPermission = security.hasEntityPermission("EMPLPERFCARD", "_VIEW", userLogin);
} 

context.hasViewCardPermission = hasViewCardPermission;
