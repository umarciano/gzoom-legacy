/*
 * Script per verificare se l'utente è in modalità read-only del portale NOPORTAL_MY
 * Autore: GitHub Copilot Assistant
 * Data: ${groovy.time.TimeCategory.now}
 */

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilValidate

// Debug log
final String module = "checkPortalReadOnlyMode.groovy"

// Check se il parametro forceReadOnly è già settato
forceReadOnly = parameters.forceReadOnly
portalPageId = parameters.portalPageId

// Se forceReadOnly non è settato, verifica il contesto della sessione
if (UtilValidate.isEmpty(forceReadOnly) || !"Y".equals(forceReadOnly)) {
    // Verifica se stiamo arrivando dal portale NOPORTAL_MY
    userLogin = session.getAttribute("userLogin")
    if (userLogin != null) {
        // Verifica i gruppi di sicurezza dell'utente usando la sintassi OFBiz tradizionale
        userLoginSecurityGroupList = delegator.findByAnd("UserLoginSecurityGroup", [userLoginId: userLogin.userLoginId])
        isNoportalMyUser = false
        
        if (userLoginSecurityGroupList) {
            for (userLoginSecurityGroup in userLoginSecurityGroupList) {
                if ("NOPORTAL_MY".equals(userLoginSecurityGroup.groupId)) {
                    isNoportalMyUser = true
                    break
                }
            }
        }
        
        // Se l'utente appartiene al gruppo NOPORTAL_MY, forza la modalità read-only
        if (isNoportalMyUser) {
            forceReadOnly = "Y"
            context.forceReadOnly = "Y"
            parameters.forceReadOnly = "Y"
        }
    }
}

// Verifica anche l'URL referrer o altri indicatori del portale
portalPageIdFromParams = parameters.portalPageId ?: session.getAttribute("portalPageId")

// Check request URL and referrer for portal context
request = context.request
if (request != null) {
    requestUrl = request.getRequestURL().toString()
    queryString = request.getQueryString()
    referrer = request.getHeader("Referer")
    
    // Check if we're coming from the portal
    if (queryString != null && queryString.contains("portalPageId=GP_WE_PORTAL_3")) {
        portalPageIdFromParams = "GP_WE_PORTAL_3"
    }
    
    if (referrer != null && referrer.contains("portalPageId=GP_WE_PORTAL_3")) {
        portalPageIdFromParams = "GP_WE_PORTAL_3"
    }
}

if ("GP_WE_PORTAL_3".equals(portalPageIdFromParams)) {
    forceReadOnly = "Y"
    context.forceReadOnly = "Y"
    parameters.forceReadOnly = "Y"
    // Salva nella sessione per le successive chiamate ma solo per questo portale
    session.setAttribute("isNoPortalMyReadOnly", "Y")
    session.setAttribute("portalPageId", "GP_WE_PORTAL_3")
}

// Verifica se c'è già il flag nella sessione
sessionReadOnly = session.getAttribute("isNoPortalMyReadOnly")
if ("Y".equals(sessionReadOnly)) {
    forceReadOnly = "Y"
    context.forceReadOnly = "Y"
    parameters.forceReadOnly = "Y"
}

// Salva il risultato nel context
context.isPortalReadOnlyMode = "Y".equals(forceReadOnly)
