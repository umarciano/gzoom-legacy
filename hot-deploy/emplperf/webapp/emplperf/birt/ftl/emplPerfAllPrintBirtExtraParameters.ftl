	<tr>
		<td colspan="1" style="width: 15%;">
			<hr>
		</td>
		<td >
			<hr>
		</td>	
	</tr>

<#-- Se il file PrintBirtForm_workEffortId esiste carica tutti i parametri, sennò carica solo quello di default -->
<#assign workEffortIdExists = Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(parameters.workEffortId!"") />

<#-- Leggi le variabili dalla sessione invece che dal contesto -->
<#assign sessionHideFilters = session.getAttribute("hideAllFiltersExceptScheda")!false />
<#assign sessionIsEmplValutato = session.getAttribute("isEmplValutato")!false />
<#assign sessionIsEmplValutatore = session.getAttribute("isEmplValutatore")!false />
<#assign sessionUseWorkEffortPartyView = session.getAttribute("useWorkEffortPartyView")!false />
<#assign sessionUserPartyId = session.getAttribute("userPartyId")!"" />

<#-- Per utenti Valutato mostra solo workEffortId (Scheda), per tutti gli altri mostra tutto -->
<#if sessionHideFilters == true>
	<#-- Utente Valutato: mostra solo il campo Scheda con dropdown filtrato E validazione obbligatoria -->
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId_mandatory.ftl" />
<#elseif sessionIsEmplValutatore == true>
	<#-- Utente Valutatore: mostra solo i campi essenziali, nasconde Tipologia Obiettivo, Elemento/Modello valutazione -->
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_monitoringDate.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_onlyWorkEffortRevisionId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_roleTypeId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_partyId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_currentStatusId.ftl" />
<#else>
	<#-- Utente normale: mostra tutti i campi SENZA validazione obbligatoria sul campo Scheda -->
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortTypeIdRef.ftl" />	
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_monitoringDate.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_onlyWorkEffortRevisionId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_scoreIndType.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_valutIndType.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_roleTypeId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_partyId.ftl" />
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_currentStatusId.ftl" />
</#if>

<#-- Campi opzionali solo per utenti normali (non Valutato e non Valutatore) -->
<#if sessionHideFilters != true && sessionIsEmplValutatore != true>
<tr>
	<td colspan="1">
		<br><hr><br>
	</td>	
</tr>

<tr>
	<td colspan="2">
		<b><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${uiLabelMap.ParametriOpzionale} </i></b> <br><br>
	</td>
</tr>
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_showPersonalData.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_typeNotes.ftl" />
</#if>


<#-- Parametri di ordinamento solo per utenti normali (non Valutatori e non Valutati) -->
<#if sessionHideFilters != true && sessionIsEmplValutatore != true>
<tr>
	<td colspan="1">
		<br><hr><br>
	</td>	
</tr>

<tr>
	<td colspan="2">
		<b><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </i></b> <br><br>
	</td>
</tr>

<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtFormIndividuale_ordinamento.ftl" />
</#if>


<tr>
	<input type="hidden" name="snapshot" id="snapshot" value="${parameters.snapshot?if_exists?default("N")}" />
</tr>

<!-- BHO
<script type="text/javascript">
    emplPerfAllPrintBirtExtraParameter = {
        load : function() {           
           
          
            
            //se sono nel caso dello storico mi metto in ascolto della lookup per settera la drop della versione
            <#if parameters.snapshot?if_exists?default("N")  == 'Y'>
	        var form = $('${printBirtFormId?default("ManagementPrintBirtForm")}');
		    if (form) {
		        var divLookupWorkEffortIdField = form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId');
		        //registro sul field
		        if(divLookupWorkEffortIdField) {
		            var lookup = LookupMgr.getLookup(divLookupWorkEffortIdField.identify());
		            if (lookup) {
		                lookup.registerOnSetInputFieldValue(WorkEffortPrintBirtExtraParameter.setWorkEffortRevisionId.curry(form), '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId');
		            }
		        }
		      }
            </#if>
           
        },
        setWorkEffortRevisionId : function(form){
	        var div = $(form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId'));
	        var workEffortRevisionId = $(div.down("input.workEffortRevisionId"));
	        var workEffortRevisionDescr = $(div.down("input.workEffortRevisionDescr"));
	        var elementDiv = $(form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId'));
	        
	        var elementDesc = $A(elementDiv.select('input')).find(function(s) {
	            return s.readAttribute('name').indexOf('description_workEffortRevisionId') > -1;
	        });
	        elementDesc.setValue(workEffortRevisionDescr.getValue());
	        
	        var elementId = $A(elementDiv.select('input')).find(function(s) {
	            return s.readAttribute('name').indexOf('workEffortRevisionId') > -1;
	        });
	        elementId.setValue(workEffortRevisionId.getValue());
	    }
    }
    
    emplPerfAllPrintBirtExtraParameter.load();
    
    // Estendi la validazione per includere il campo droplist workEffortId mandatory
    if (window.LiveValidationManager && LiveValidationManager.onSave) {
        var originalOnSave = LiveValidationManager.onSave;
        LiveValidationManager.onSave = function(event) {
            // Prima esegui la validazione standard
            var result = originalOnSave.call(this, event);
            
            // Poi controlla il campo workEffortId se mandatory
            var form = Event.findElement(event, 'form');
            if (form) {
                var workEffortIdDiv = form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId');
                if (workEffortIdDiv && workEffortIdDiv.hasClassName('mandatory')) {
                    var codeField = workEffortIdDiv.down("input.droplist_code_field");
                    if (codeField && (!codeField.getValue() || codeField.getValue().trim() == '')) {
                        alert('${uiLabelMap.WorkeffortRoot} è un campo obbligatorio');
                        Event.stop(event);
                        return false;
                    }
                }
            }
            
            return result;
        };
    }
</script>
	-->

		