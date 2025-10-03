// Script per creare liste dropdown read-only per Valutatori e Valutati
import org.ofbiz.base.util.Debug;

// Lista per Valutatore read-only
if (context.evalManagerPartyIdReadOnly) {
    evalManagerPartyIdList = [];
    if (userLogin?.partyId) {
        // Usa PartyRoleView per ottenere i dati corretti come fa GP_MENU_00142
        def userParty = delegator.findOne("PartyRoleView", [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_MANAGER"], false);
        if (userParty) {
            evalManagerPartyIdList.add([
                partyId: userLogin.partyId,
                partyName: userParty.partyName,
                parentRoleCode: userParty.parentRoleCode
            ]);
            
            // Forza il valore nei parameters se non è già impostato
            if (!parameters.evalManagerPartyId) {
                parameters.evalManagerPartyId = userLogin.partyId;
            }
        }
    }
    context.evalManagerPartyIdList = evalManagerPartyIdList;
    Debug.logInfo("Created evalManagerPartyIdList with " + evalManagerPartyIdList.size() + " entries", "createReadOnlyDropdownLists");
}

// Lista per Valutato read-only  
if (context.evalPartyIdReadOnly) {
    evalPartyIdList = [];
    if (userLogin?.partyId) {
        // Usa PartyRoleView per ottenere i dati corretti come fa GP_MENU_00142
        def userParty = delegator.findOne("PartyRoleView", [partyId: userLogin.partyId, roleTypeId: "WEM_EVAL_IN_CHARGE"], false);
        if (userParty) {
            evalPartyIdList.add([
                partyId: userLogin.partyId,
                partyName: userParty.partyName,
                parentRoleCode: userParty.parentRoleCode
            ]);
            
            // Forza il valore nei parameters se non è già impostato
            if (!parameters.evalPartyId) {
                parameters.evalPartyId = userLogin.partyId;
            }
        }
    }
    context.evalPartyIdList = evalPartyIdList;
    Debug.logInfo("Created evalPartyIdList with " + evalPartyIdList.size() + " entries", "createReadOnlyDropdownLists");
}

// Debug delle variabili di controllo
Debug.logInfo("===== DEBUG GP_MENU_00139 =====", "createReadOnlyDropdownLists");
Debug.logInfo("evalManagerPartyIdReadOnly: " + context.evalManagerPartyIdReadOnly, "createReadOnlyDropdownLists");
Debug.logInfo("evalPartyIdReadOnly: " + context.evalPartyIdReadOnly, "createReadOnlyDropdownLists");
Debug.logInfo("isValutatore: " + context.isValutatore, "createReadOnlyDropdownLists");
Debug.logInfo("availableValutatiIds: " + context.availableValutatiIds, "createReadOnlyDropdownLists");
Debug.logInfo("hideUnitaResponsabile: " + context.hideUnitaResponsabile, "createReadOnlyDropdownLists");
Debug.logInfo("userLogin.partyId: " + userLogin?.partyId, "createReadOnlyDropdownLists");
Debug.logInfo("parameters.evalManagerPartyId: " + parameters.evalManagerPartyId, "createReadOnlyDropdownLists");
Debug.logInfo("parameters.evalPartyId: " + parameters.evalPartyId, "createReadOnlyDropdownLists");
Debug.logInfo("evalManagerPartyIdList: " + context.evalManagerPartyIdList, "createReadOnlyDropdownLists");
Debug.logInfo("evalPartyIdList: " + context.evalPartyIdList, "createReadOnlyDropdownLists");
Debug.logInfo("===============================", "createReadOnlyDropdownLists");
