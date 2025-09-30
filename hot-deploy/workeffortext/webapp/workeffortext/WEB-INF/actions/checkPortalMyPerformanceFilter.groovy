import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

/**
 * Script per filtrare le schede nel portale "Mie performance" (GP_WE_PORTAL_3)
 * affinché vengano mostrate solo le schede negli stati "Valutazione Condivisa" (WEEVALST_EXECSHARED) 
 * oppure "Valutazione Conclusa" (WEEVALST_EXECFINAL)
 * 
 * Per utenti con permesso EMPLVALUTATO_VIEW, il filtro viene gestito a livello di risultati
 * invece che a livello di query per garantire la compatibilità.
 */

def module = "checkPortalMyPerformanceFilter.groovy";

// Verifica se siamo nel portale GP_WE_PORTAL_3 (Mie performance)
def isMyPerformancePortal = false;
def session = request.getSession();

// Controlla i parametri e la sessione
def portalPageId = parameters.portalPageId;
def sessionPortalPageId = session.getAttribute("portalPageId");
def forceReadOnly = parameters.forceReadOnly;

if ("GP_WE_PORTAL_3".equals(portalPageId) || "GP_WE_PORTAL_3".equals(sessionPortalPageId) || "Y".equals(forceReadOnly)) {
    isMyPerformancePortal = true;
    Debug.logInfo("checkPortalMyPerformanceFilter: Detected GP_WE_PORTAL_3 portal access", module);
}

// Controlla anche il referrer per essere sicuri
def referrer = request.getHeader("Referer");
if (referrer != null && referrer.contains("portalPageId=GP_WE_PORTAL_3")) {
    isMyPerformancePortal = true;
    Debug.logInfo("checkPortalMyPerformanceFilter: Detected GP_WE_PORTAL_3 in referrer", module);
}

// Verifica se l'utente ha il permesso EMPLVALUTATO_VIEW
def isEmplValutatoUser = false;
if (security != null && userLogin != null) {
    def hasPermission = security.hasPermission("EMPLVALUTATO_VIEW", userLogin);
    if (hasPermission && userLogin?.partyId) {
        def userPartyRole = delegator.findOne("PartyRoleView", 
            [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_IN_CHARGE"], false);
        if (userPartyRole) {
            isEmplValutatoUser = true;
            Debug.logInfo("checkPortalMyPerformanceFilter: Rilevato utente EMPLVALUTATO_VIEW: " + userLogin.partyId, module);
        }
    }
}

// Se siamo nel portale "Mie performance"
if (isMyPerformancePortal) {
    if (isEmplValutatoUser) {
        // Per utenti EMPLVALUTATO_VIEW: non impostare filtri di stato qui
        // Il filtro OR verrà applicato successivamente sui risultati
        Debug.logInfo("checkPortalMyPerformanceFilter: Utente EMPLVALUTATO_VIEW - filtro OR verrà applicato sui risultati", module);
        
        // Imposta flag per identificare che serve il filtro OR sui risultati
        context.portalEmplValutatoFilter = true;
        
    } else {
        // Per altri utenti: usa il filtro OR standard
        // Primo stato: Valutazione Condivisa
        parameters.currentStatusId_fld0_value = "WEEVALST_EXECSHARED";
        parameters.currentStatusId_fld0_op = "equals";
        
        // Secondo stato: Valutazione Conclusa
        parameters.currentStatusId_fld1_value = "WEEVALST_EXECFINAL";
        parameters.currentStatusId_fld1_op = "equals";
        
        // Operatore OR tra i due filtri
        parameters.currentStatusId_op = "or";
        
        // Imposta anche nel context per compatibilità
        context.currentStatusId_value = "WEEVALST_EXECSHARED";
        context.currentStatusContains = "WEEVALST_EXECSHARED,WEEVALST_EXECFINAL";
        context.currentStatusId = "WEEVALST_EXECSHARED";
        
        Debug.logInfo("checkPortalMyPerformanceFilter: Applied OR filter for WEEVALST_EXECSHARED and WEEVALST_EXECFINAL status", module);
    }
} else {
    // Se non siamo nel portale, usa il comportamento standard
    if (UtilValidate.isEmpty(context.currentStatusId)) {
        context.currentStatusId = "";
    }
}

Debug.logInfo("checkPortalMyPerformanceFilter: isMyPerformancePortal = " + isMyPerformancePortal + 
    ", isEmplValutatoUser = " + isEmplValutatoUser, module);
