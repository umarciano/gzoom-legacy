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
                
                // Usa la relazione ORG_RESPONSIBLE dove l'utente è partyIdTo (responsabile della UOC)
                def orgResponsibleRelations = delegator.findList("PartyRelationship", 
                    EntityCondition.makeCondition([
                        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userPartyId),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ORG_RESPONSIBLE")
                    ], EntityOperator.AND), 
                    null, null, null, false);
                    
                Debug.logInfo("EMPLVALUTATORE_DEBUG: Trovate " + orgResponsibleRelations.size() + " relazioni ORG_RESPONSIBLE per utente " + userPartyId, "checkEnableNewThrowReport");
                
                if (orgResponsibleRelations && orgResponsibleRelations.size() > 0) {
                    // Il partyIdFrom è la UOC di cui l'utente è responsabile
                    def userOrgUnitId = orgResponsibleRelations[0].partyIdFrom;
                    session.setAttribute("userOrgUnitId", userOrgUnitId);
                    
                    // Ottieni anche la descrizione della UOC per il template
                    def uocParty = delegator.findOne("Party", [partyId: userOrgUnitId], false);
                    def uocDescription = "";
                    
                    if (uocParty) {
                        uocDescription = uocParty.description ?: uocParty.partyName ?: ("UOC " + userOrgUnitId);
                        session.setAttribute("userOrgUnitDescription", uocDescription);
                    } else {
                        uocDescription = "UOC " + userOrgUnitId;
                        session.setAttribute("userOrgUnitDescription", uocDescription);
                    }
                    
                    Debug.logInfo("EMPLVALUTATORE_UOC: Trovata UOC tramite ORG_RESPONSIBLE per utente " + userPartyId + ": " + userOrgUnitId + " (" + uocDescription + ")", "checkEnableNewThrowReport");
                } else {
                    Debug.logInfo("EMPLVALUTATORE_UOC: Nessuna relazione ORG_RESPONSIBLE trovata per utente " + userPartyId, "checkEnableNewThrowReport");
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