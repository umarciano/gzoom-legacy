	<tr>
		<td colspan="1" style="width: 15%;">
			<hr>
		</td>
		<td >
			<hr>
		</td>	
	</tr>

<#-- Leggi le variabili dalla sessione per gestire la logica Valutatori -->
<#assign sessionIsEmplValutatore = session.getAttribute("isEmplValutatore")!false />
<#assign sessionHideFilters = session.getAttribute("hideAllFiltersExceptScheda")!false />

<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_monitoringDate_mandatory.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_onlyWorkEffortRevisionId.ftl" />
<#-- Elemento e Modello valutazione nascosti per Valutatore -->
<#if sessionIsEmplValutatore != true>
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_scoreIndType.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_valutIndType.ftl" />
</#if>
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_roleTypeId.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_partyId.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_currentStatusId.ftl" />

<#-- Parametri Opzionali e Ordinamento solo per utenti normali (non Valutatori) -->
<#if sessionIsEmplValutatore != true>
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
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_typeNotes.ftl" />
<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtFormIndividuale_ordinamento.ftl" />
</#if>

