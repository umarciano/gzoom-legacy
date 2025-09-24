import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

/**
 * Script per filtrare le schede nel portale "Mie performance" (GP_WE_PORTAL_3)
 * affinché vengano mostrate solo le schede nello stato "Valutazione Conclusa" (WEEVALST_EXECFINAL)
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

// Se siamo nel portale "Mie performance", aggiungi il filtro per lo stato
if (isMyPerformancePortal) {
    // Forza il filtro per mostrare solo schede nello stato "Valutazione Conclusa"
    parameters.currentStatusId_value = "WEEVALST_EXECFINAL";
    parameters.currentStatusId_fld0_value = "WEEVALST_EXECFINAL";
    parameters.currentStatusId_op = "equals";
    parameters.currentStatusId_fld0_op = "equals";
    
    // Imposta anche il context per compatibilità con la query XML
    context.currentStatusId_value = "WEEVALST_EXECFINAL";
    context.currentStatusContains = "WEEVALST_EXECFINAL";
    context.currentStatusId = "WEEVALST_EXECFINAL";
    
    Debug.logInfo("checkPortalMyPerformanceFilter: Applied filter for WEEVALST_EXECFINAL status", module);
} else {
    // Se non siamo nel portale, usa il comportamento standard (mostra tutte le schede)
    if (UtilValidate.isEmpty(context.currentStatusId)) {
        context.currentStatusId = "";
    }
}

Debug.logInfo("checkPortalMyPerformanceFilter: isMyPerformancePortal = " + isMyPerformancePortal, module);
