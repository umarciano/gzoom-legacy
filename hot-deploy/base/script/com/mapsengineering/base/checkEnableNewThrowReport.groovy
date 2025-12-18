import org.ofbiz.base.util.*;
import org.ofbiz.security.Security;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

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

Debug.logInfo("CHECKENABLER_DEBUG: Script checkEnableNewThrowReport.groovy in esecuzione per utente " + 
    (userLogin ? userLogin.partyId : "NON_LOGGATO"), "checkEnableNewThrowReport");

// Verifica se l'utente è loggato e ha il permesso EMPLVALUTATO_VIEW
if (userLogin) {
    Security security = request.getAttribute("security");
    
    // Imposta flag isAdmin in sessione (utenti con EMPLPERFMGR_ADMIN)
    boolean isAdmin = security && security.hasPermission("EMPLPERFMGR_ADMIN", userLogin);
    session.setAttribute("isAdmin", isAdmin);
    
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
        // Verifica se l'utente è un Valutatore (WEM_EVAL_MANAGER)
        def evalManagerRole = delegator.findOne("PartyRole", 
            [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_MANAGER"], false);
            
        if (evalManagerRole) {
            // Utente Valutatore: applica logica specifica
            session.setAttribute("isEmplValutatore", true);
            session.setAttribute("hideAllFiltersExceptScheda", false);
            session.setAttribute("useWorkEffortPartyView", false);
            
            def userPartyId = userLogin.partyId;
            if (userPartyId) {
                context.userPartyId = userPartyId;
                session.setAttribute("userPartyId", userPartyId);
            }
            
            // Recupera l'elenco dei Valutati (per filtrare le schede di valutazione)
            try {
                def evaluatedByRelations = delegator.findList("PartyRelationship", 
                    EntityCondition.makeCondition([
                        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userPartyId),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "WEF_EVALUATED_BY")
                    ], EntityOperator.AND), 
                    null, null, null, false);
                    
                Debug.logInfo("EMPLVALUTATORE_DEBUG: Trovate " + evaluatedByRelations.size() + " relazioni WEF_EVALUATED_BY per utente " + userPartyId, "checkEnableNewThrowReport");
                
                if (evaluatedByRelations && evaluatedByRelations.size() > 0) {
                    // Estrai i partyId dei Valutati (partyIdFrom)
                    def evaluatedPartyIds = evaluatedByRelations.collect { it.partyIdFrom };
                    // Converti in stringa separata da virgole per FreeMarker
                    def evaluatedPartyIdsString = evaluatedPartyIds.join(",");
                    session.setAttribute("evaluatedPartyIds", evaluatedPartyIdsString);
                    
                    Debug.logInfo("EMPLVALUTATORE_EVALUATED: Trovati " + evaluatedPartyIds.size() + " Valutati per Valutatore " + userPartyId + ": " + evaluatedPartyIdsString, "checkEnableNewThrowReport");
                } else {
                    // CASO EDGE: Valutatore senza Valutati assegnati
                    // Verifica se l'utente ha permessi di ADMIN (ADMINISTRATOR_VIEW)
                    def isAdminView = security && security.hasPermission("ADMINISTRATOR_VIEW", userLogin);
                    
                    if (isAdminView) {
                        // ADMIN: mostra TUTTO (nessun filtro)
                        Debug.logInfo("EMPLVALUTATORE_EVALUATED: Nessun Valutato trovato per Valutatore " + userPartyId + 
                            " ma ha permessi ADMINISTRATOR_VIEW - MOSTRA TUTTE LE SCHEDE", "checkEnableNewThrowReport");
                        session.setAttribute("evaluatedPartyIds", "");
                        session.setAttribute("isEmplValutatoreAdmin", true);
                    } else {
                        // NON-ADMIN: mostra SOLO la sua scheda come Valutato (se esiste)
                        Debug.logInfo("EMPLVALUTATORE_EVALUATED: Nessun Valutato trovato per Valutatore " + userPartyId + 
                            " e NON ha permessi ADMINISTRATOR_VIEW - MOSTRA SOLO SUA SCHEDA", "checkEnableNewThrowReport");
                        session.setAttribute("evaluatedPartyIds", userPartyId);
                        session.setAttribute("isEmplValutatoreAdmin", false);
                    }
                }
            } catch (Exception e) {
                Debug.logError("EMPLVALUTATORE_EVALUATED: Errore recupero Valutati per utente " + userPartyId + ": " + e.getMessage(), "checkEnableNewThrowReport");
                e.printStackTrace();
                session.setAttribute("evaluatedPartyIds", "");
            }
            
            // Cerca la UOC (Unità Responsabile) dell'utente Valutatore per la prepopolazione
            try {
                // Debug: cerchiamo TUTTE le relazioni per questo utente per capire la struttura
                def allUserRelations = delegator.findList("PartyRelationship", 
                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userPartyId), 
                    null, null, null, false);
                    
                Debug.logInfo("EMPLVALUTATORE_DEBUG: Trovate " + allUserRelations.size() + " relazioni totali per utente " + userPartyId, "checkEnableNewThrowReport");
                
                for (relation in allUserRelations) {
                    Debug.logInfo("EMPLVALUTATORE_DEBUG: Relazione: " + relation.partyIdFrom + " -> " + relation.partyIdTo + 
                        " | relationshipName: '" + relation.relationshipName + "' | partyRelationshipTypeId: '" + relation.partyRelationshipTypeId + "'", 
                        "checkEnableNewThrowReport");
                }
                
                // Cerca relazioni ORG_RESPONSIBLE o ORG_DELEGATE dove l'utente è responsabile/delegato di una UOC
                def managementRelations = delegator.findList("PartyRelationship", 
                    EntityCondition.makeCondition([
                        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userPartyId),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, ["ORG_RESPONSIBLE", "ORG_DELEGATE"]),
                        EntityCondition.makeCondition(EntityOperator.OR,
                            EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
                            EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())
                        )
                    ], EntityOperator.AND), 
                    null, null, null, false);
                    
                Debug.logInfo("EMPLVALUTATORE_DEBUG: Trovate " + managementRelations.size() + " relazioni ORG_RESPONSIBLE/ORG_DELEGATE per utente " + userPartyId, "checkEnableNewThrowReport");
                
                def userOrgUnitId = null;
                def uocDescription = "";
                
                // PREPOPOLA SOLO se l'utente ha ORG_RESPONSIBLE o ORG_DELEGATE
                if (managementRelations && managementRelations.size() > 0) {
                    // Il partyIdFrom è la UOC di cui l'utente è responsabile/delegato
                    userOrgUnitId = managementRelations[0].partyIdFrom;
                    def relationType = managementRelations[0].partyRelationshipTypeId;
                    
                    Debug.logInfo("EMPLVALUTATORE_UOC: Trovata UOC tramite " + relationType + " per utente " + userPartyId + ": " + userOrgUnitId, "checkEnableNewThrowReport");
                    
                    // Imposta variabili di sessione
                    session.setAttribute("userOrgUnitId", userOrgUnitId);
                    
                    // Ottieni anche la descrizione della UOC per il template
                    def uocParty = delegator.findOne("Party", [partyId: userOrgUnitId], false);
                    
                    if (uocParty) {
                        uocDescription = uocParty.description ?: uocParty.partyName ?: ("UOC " + userOrgUnitId);
                        session.setAttribute("userOrgUnitDescription", uocDescription);
                    } else {
                        uocDescription = "UOC " + userOrgUnitId;
                        session.setAttribute("userOrgUnitDescription", uocDescription);
                    }
                    
                    Debug.logInfo("EMPLVALUTATORE_UOC: Impostate variabili sessione - userOrgUnitId: " + userOrgUnitId + ", userOrgUnitDescription: " + uocDescription, "checkEnableNewThrowReport");
                } else {
                    // Utente NON ha ORG_RESPONSIBLE né ORG_DELEGATE - NON prepopolare
                    Debug.logInfo("EMPLVALUTATORE_UOC: Utente " + userPartyId + " NON ha relazioni ORG_RESPONSIBLE/ORG_DELEGATE - campo NON sarà prepopolato (dropdown normale)", "checkEnableNewThrowReport");
                }
            } catch (Exception e) {
                Debug.logError("EMPLVALUTATORE_UOC: Errore ricerca UOC per utente " + userPartyId + ": " + e.getMessage(), "checkEnableNewThrowReport");
                e.printStackTrace();
            }
            
            Debug.logInfo("EMPLVALUTATORE_VIEW: Utente " + userLogin.partyId + " identificato come Valutatore", "checkEnableNewThrowReport");
        } else {
            // Pulisci attributi di sessione per utenti normali
            session.removeAttribute("isEmplValutato");
            session.removeAttribute("isEmplValutatore");
            session.removeAttribute("hideAllFiltersExceptScheda");
            session.removeAttribute("useWorkEffortPartyView");
            session.removeAttribute("userPartyId");
        }
    }
}

return result;