	<tr>
		<td colspan="1" style="width: 15%;">
			<hr>
		</td>
		<td >
			<hr>
		</td>	
	</tr>

<#-- Leggi le variabili dalla sessione invece che dal contesto -->
<#assign sessionHideFilters = session.getAttribute("hideAllFiltersExceptScheda")!false />
<#assign sessionIsEmplValutato = session.getAttribute("isEmplValutato")!false />
<#assign sessionUseWorkEffortPartyView = session.getAttribute("useWorkEffortPartyView")!false />
<#assign sessionUserPartyId = session.getAttribute("userPartyId")!"" />

<#-- Per utenti Valutato mostra solo workEffortId (Scheda), per tutti gli altri mostra tutto -->
<#if sessionHideFilters == true>
	<#-- Utente Valutato: mostra solo il campo Scheda con dropdown filtrato -->
	<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId.ftl" />
<#else>
	<#-- Utente normale: mostra tutti i campi -->
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

<#-- Campi opzionali per TUTTI gli utenti (sia Valutato che normali) -->
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
</script>
	-->

		