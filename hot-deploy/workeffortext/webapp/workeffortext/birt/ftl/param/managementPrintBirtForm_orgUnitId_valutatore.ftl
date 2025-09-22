<#-- Template per campo orgUnitId disabilitato e prepopolato per Valutatori -->

<#-- Comportamento VALUTATORE: campo disabilitato con valore predefinito -->
${Static["org.ofbiz.base.util.Debug"].logInfo("TEMPLATE_DEBUG: managementPrintBirtForm_orgUnitId_valutatore.ftl ESEGUITO", "managementPrintBirtForm_orgUnitId_valutatore.ftl")}

<#-- Leggi la UOC dell'utente dalla sessione -->
<#assign userOrgUnitId = session.getAttribute("userOrgUnitId")!"" />
<#assign userOrgUnitDescription = session.getAttribute("userOrgUnitDescription")!"" />
${Static["org.ofbiz.base.util.Debug"].logInfo("TEMPLATE_DEBUG: userOrgUnitId dalla sessione = " + userOrgUnitId, "managementPrintBirtForm_orgUnitId_valutatore.ftl")}
${Static["org.ofbiz.base.util.Debug"].logInfo("TEMPLATE_DEBUG: userOrgUnitDescription dalla sessione = " + userOrgUnitDescription, "managementPrintBirtForm_orgUnitId_valutatore.ftl")}

<#-- Verifica che abbiamo una UOC valida -->
<#if userOrgUnitId?has_content>
    <#-- Recupera i dettagli della UOC specifica dal database -->
    <#assign uocParty = delegator.findOne("Party", {"partyId": userOrgUnitId}, false)!>
    
    <#-- Prepara i dati per la visualizzazione (usa la descrizione dalla sessione se disponibile) -->
    <#assign uocDisplayName = userOrgUnitDescription>
    <#if !uocDisplayName?has_content && uocParty?has_content>
        <#assign uocDisplayName = uocParty.partyName!"UOC " + userOrgUnitId>
    <#elseif !uocDisplayName?has_content>
        <#assign uocDisplayName = "UOC " + userOrgUnitId>
    </#if>
    
    ${Static["org.ofbiz.base.util.Debug"].logInfo("TEMPLATE_DEBUG: uocDisplayName = " + uocDisplayName, "managementPrintBirtForm_orgUnitId_valutatore.ftl")}
    
    <tr>
       <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
       <td class="widget-area-style">
       <div class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId">
       
       <!-- VALUTATORE: Autocompleter con singola UOC preselezionata e disabilitata -->
       <input class="autocompleter_parameter" type="hidden" name="localAutocompleter" value="Y"/>
       
       <!-- Dati della UOC del Valutatore -->
       <input type="hidden" class="autocompleter_local_data" 
              id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_${userOrgUnitId}" 
              name="orgUnitId_${userOrgUnitId}" 
              value="${uocDisplayName}"/>
       
       <input class="autocompleter_parameter" type="hidden" name="entityKeyField" value="partyId"/>   
       <input class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="partyName"/>
       
       <div class="droplist_container"> 
       <!-- Campo nascosto con il partyId preselezionato -->
       <input type="hidden" class="droplist_code_field" name="orgUnitId" value="${userOrgUnitId}"/>
       
       <!-- Campo testo con descrizione UOC preselezionata e disabilitato -->
       <input type="text" size="100" maxlength="255" 
              value="${uocDisplayName}" 
              class="droplist_edit_field" 
              name="partyName_orgUnitId" 
              id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_value"
              readonly="readonly"
              disabled="disabled"
              style="background-color: #f0f0f0; color: #666; cursor: not-allowed;"/>
       
       <!-- Pulsante dropdown disabilitato -->
       <span class="droplist-anchor">
           <a style="cursor: not-allowed; opacity: 0.5; pointer-events: none;" 
              class="droplist_submit_field fa fa-2x" 
              href="#"></a>
       </span>
       </div>
       
       <!-- Messaggio informativo per l'utente -->
       <div style="font-size: 11px; color: #666; margin-top: 3px;">
           <em>Campo preselezionato automaticamente per il Valutatore (non modificabile)</em>
       </div>
       
       </div>
       </td>
    </tr>
    
<#else>
    <!-- Fallback: se non troviamo la UOC, includiamo il template normale -->
    ${Static["org.ofbiz.base.util.Debug"].logError("TEMPLATE_ERROR: UOC non trovata per Valutatore, uso template normale", "managementPrintBirtForm_orgUnitId_valutatore.ftl")}
    <#include "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />
</#if>

<#-- Leggi la UOC dell'utente dalla sessione -->
<#assign userOrgUnitId = session.getAttribute("userOrgUnitId")!"" />
<#assign userOrgUnitDescription = session.getAttribute("userOrgUnitDescription")!"" />
${Static["org.ofbiz.base.util.Debug"].logInfo("TEMPLATE_DEBUG: userOrgUnitId dalla sessione = " + userOrgUnitId, "managementPrintBirtForm_orgUnitId_valutatore.ftl")}
${Static["org.ofbiz.base.util.Debug"].logInfo("TEMPLATE_DEBUG: userOrgUnitDescription dalla sessione = " + userOrgUnitDescription, "managementPrintBirtForm_orgUnitId_valutatore.ftl")}

<#-- Per ora, includiamo il template normale ma con un commento visibile -->
<!-- TEMPLATE VALUTATORE: Campo orgUnitId per Valutatori -->
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />

<#-- Debug: stampa un messaggio visibile nel HTML -->
<div style="background: yellow; padding: 5px; margin: 5px; border: 1px solid red;">
    <strong>DEBUG VALUTATORE:</strong> 
    UOC = ${userOrgUnitId}, 
    Descrizione = ${userOrgUnitDescription}
    <br/>Template Valutatore caricato correttamente!
</div>

<#-- Aggiungiamo uno script JavaScript per disabilitare il campo dopo il caricamento -->
<script type="text/javascript">
// Debug immediato - scriviamo nel DOM invece di alert
document.addEventListener('DOMContentLoaded', function() {
    // Aggiungiamo un div debug per confermare l'esecuzione
    var debugDiv = document.createElement('div');
    debugDiv.innerHTML = '<strong>JS DEBUG:</strong> JavaScript Valutatore eseguito! UOC: ${userOrgUnitId}';
    debugDiv.style.cssText = 'background: lightgreen; padding: 5px; margin: 5px; border: 1px solid green;';
    document.body.insertBefore(debugDiv, document.body.firstChild);
    
    // Esegui la logica principale
    disableOrgUnitFields();
});

// Funzione per cercare e disabilitare i campi orgUnit
function disableOrgUnitFields() {
    console.log("VALUTATORE: Iniziando ricerca campi orgUnit...");
    
    // UOC dell'utente dalla sessione
    var userOrgUnitId = "${userOrgUnitId}";
    var userOrgUnitDescription = "${session.getAttribute("userOrgUnitDescription")!""}";
    console.log("VALUTATORE: UOC utente dalla sessione: " + userOrgUnitId);
    console.log("VALUTATORE: Descrizione UOC dalla sessione: " + userOrgUnitDescription);
    
    if (!userOrgUnitId || userOrgUnitId === "") {
        console.log("VALUTATORE: Nessuna UOC da preselezionare");
        return;
    }
    
    // Cerca il campo nascosto orgUnitId (che contiene il party_id)
    var hiddenField = document.querySelector('input[name="orgUnitId"]');
    var textField = document.querySelector('input[name="partyName_orgUnitId"]');
    
    if (hiddenField) {
        // Imposta il party_id nel campo nascosto
        hiddenField.value = userOrgUnitId;
        console.log("VALUTATORE: Impostato party_id nel campo nascosto: " + userOrgUnitId);
        
        // Disabilita il campo nascosto
        hiddenField.disabled = true;
        hiddenField.readOnly = true;
    }
    
    if (textField) {
        // Cerca la descrizione della UOC negli elementi autocompleter_local_data
        var localDataElements = document.querySelectorAll('input.autocompleter_local_data');
        var uocDescription = "";
        
        console.log("VALUTATORE: Cercando descrizione UOC in " + localDataElements.length + " elementi autocompleter");
        
        for (var i = 0; i < localDataElements.length; i++) {
            var element = localDataElements[i];
            var elementId = element.id;
            
            // L'ID dovrebbe essere formato come: formId_orgUnitId_PARTY_ID
            if (elementId.endsWith("_" + userOrgUnitId)) {
                uocDescription = element.value;
                console.log("VALUTATORE: Trovata descrizione UOC per " + userOrgUnitId + ": " + uocDescription);
                break;
            }
        }
        
        // Se non trovata negli elementi locali, usa la descrizione dalla sessione
        if (!uocDescription || uocDescription === "") {
            if (userOrgUnitDescription && userOrgUnitDescription !== "") {
                uocDescription = userOrgUnitDescription;
                console.log("VALUTATORE: Utilizzata descrizione UOC dalla sessione: " + uocDescription);
            } else {
                uocDescription = "UOC ID: " + userOrgUnitId; // Ultimo fallback
                console.log("VALUTATORE: Utilizzato fallback per descrizione UOC");
            }
        }
        
        // Imposta la descrizione nel campo testo
        textField.value = uocDescription;
        console.log("VALUTATORE: Impostata descrizione nel campo testo: " + uocDescription);
        
        // Disabilita il campo testo
        textField.disabled = true;
        textField.readOnly = true;
        textField.style.backgroundColor = '#f0f0f0';
        textField.style.color = '#666';
        textField.style.cursor = 'not-allowed';
    }
    
    // Disabilita anche il pulsante di apertura dropdown
    var dropdownButton = document.querySelector('.droplist_submit_field');
    if (dropdownButton) {
        dropdownButton.style.pointerEvents = 'none';
        dropdownButton.style.opacity = '0.5';
        dropdownButton.style.cursor = 'not-allowed';
        
        // Rimuovi event listeners
        dropdownButton.onclick = function(e) {
            e.preventDefault();
            return false;
        };
        
        console.log("VALUTATORE: Disabilitato pulsante dropdown");
    }
    
    // Disabilita l'intero container dell'autocompleter
    var container = document.querySelector('.droplist_container');
    if (container) {
        container.style.pointerEvents = 'none';
        container.style.opacity = '0.8';
    }
    
    console.log("VALUTATORE: Operazione completata - campo orgUnit preselezionato e disabilitato");
}
}

// Esegui dopo il caricamento del DOM
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', disableOrgUnitFields);
} else {
    disableOrgUnitFields();
}

// Esegui anche dopo 1 secondo per essere sicuri che tutto sia caricato
setTimeout(disableOrgUnitFields, 1000);
</script>
