
<#-- TIPO SCHEDA OPZIONALE -->
<tr>
   <td class="label">${uiLabelMap.HeaderRootType}</td>
   <td class="widget-area-style"><div  class="droplist_field " id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortType]"/>
   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isRoot| equals| Y]! [isTemplate| equals| N]! <#if parentTypeId?has_content>[parentTypeId| equals| ${parentTypeId}]<#else>[parentTypeId| like| CTX%25]</#if>]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortTypeId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
   <div class="droplist_container"><input type="hidden" name="workEffortTypeId" value=""  class="droplist_code_field"/>
   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId_edit_field" name="description_workEffortTypeId" size="100" maxlength="255" value=""  class="droplist_edit_field"/>
   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>

<tr>
   <td class="label">${uiLabelMap.WorkeffortRoot}</td>
    <!--snapshot -->
   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
   </#if>
   <td class="widget-area-style">
	   <div  class="droplist_field " id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId">
	   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	   
	  
	   <!--localeSecondarySet -->
   	   <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortNameLang, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortNameLang]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortNameLang]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortNameLang"/>
	   <#else>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
	   	   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName"/>
	   </#if>
	   
	   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
			<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[weIsTemplate| equals| N]! [weIsRoot| equals| Y]! [workEffortTypeId| equals| ${parameters.workEffortTypeId?if_exists}]! [workEffortTypeId| equals| ${parameters.workEffortTypeId?if_exists}]! [workEffortSnapshotId| notEqual | [null-field]]! <#if parameters.parentTypeId?if_exists?has_content>[weContextId| equals| ${parameters.parentTypeId?if_exists}]<#else>[weContextId| like| CTX%25]</#if>]]"/>
	    <#else>
	    	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[weIsTemplate| equals| N]! [weIsRoot| equals| Y]! [workEffortSnapshotId| equals| [null-field]]! <#if parameters.parentTypeId?if_exists?has_content>[weContextId| equals| ${parameters.parentTypeId?if_exists}]<#else>[weContextId| like| CTX%25]</#if>]]"/>	    
	   	
	   </#if>
	   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
	   
	   <div class="droplist_container">
	   <input type="hidden" name="workEffortId" value=""  class="droplist_code_field "/>
	   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="workEffortName_workEffortId" size="100" maxlength="255" value=""  class="droplist_edit_field "/>
	   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div>
   </td>
</tr>

<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_monitoringDate.ftl" />

<#if parameters.snapshot?if_exists?default("N")  == 'Y'>
<tr>
   	<td class="label">${uiLabelMap.FormFieldTitle_workEffortRevisionId}</td>
	<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortRevision]"/>
	<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortRevisionId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isAutomatic| equals| N]! [workEffortTypeIdCtx| like| CTX%]]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortRevisionId"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
	<div class="droplist_container">
		<input type="hidden" name="workEffortRevisionId" value=""  class="droplist_code_field"/>
		<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId_edit_field" name="description_workEffortRevisionId" size="50" value="" class="droplist_edit_field"/>
		<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
   	<td class="label">${uiLabelMap.FormFieldTitle_workEffortRevisionIdComp}</td>
	<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionIdComp">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortRevision]"/>
	<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortRevisionId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isAutomatic| equals| N]! [workEffortTypeIdCtx| like| CTX%]]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortRevisionId"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
	<div class="droplist_container">
		<input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="workEffortRevisionIdComp_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_etch_edit_value"/>
   		<input type="hidden" class="droplist_code_field" name="workEffortRevisionIdComp"/>
   		<span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
</#if>

<#-- livello punteggio -->
<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_elementoValutazione}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_scoreIndType">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[ScoreIndividuale]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[etch]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[etch]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[etch]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[etch]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="etch"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="etch"/><div class="droplist_container">
   <input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="etch_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_etch_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="scoreIndType"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>

<#-- modello valutazione -->
<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_modelloValutazione}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_valutIndType">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[ScoreIndividualeModel]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortScoreIndId, workEffortName]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortScoreIndId, workEffortName]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortScoreIndId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortScoreIndId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
   <input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="workEffortName_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortName_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="valutIndType"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>

<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_orgUnitId.ftl" />

<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_roleTypeId}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortAndRoleTypeIndividualeView]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, roleTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentTypeId, roleTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentTypeId| equals| EMPLOYEE]]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="roleTypeId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
   <input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="roleTypeId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="roleTypeId"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>

<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_partyId.ftl" />

<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_currentStatusId.ftl" />

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

<tr>
    <td class="label">${uiLabelMap.ShowPersonalData}</td>
    <td class="widget-area-style"><select name="showPersonalData" id="${printBirtFormId?default("ManagementPrintBirtForm")}_showPersonalData" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>

<tr>
    <td class="label">${uiLabelMap.TypeNotes}</td>
    <td class="widget-area-style"><select name="typeNotes" id="${printBirtFormId?default("ManagementPrintBirtForm")}_typeNotes" size="1" class=""><option value="Y">${uiLabelMap.CommonInternal}</option><option value="ALL">${uiLabelMap.CommonAll}</option><option selected="selected" value="N">${uiLabelMap.CommonExternal}</option></select></td>
</tr>

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

<tr>
    <td class="label">${uiLabelMap.ParametriOrdinamento}</td>
    <td class="widget-area-style"><select name="ordinamento" id="${printBirtFormId?default("ManagementPrintBirtForm")}_ordinamento" size="1" class=""><option selected="selected" value="N">${uiLabelMap.CognomeNomeValutato}</option><option  value="RESPONSABILE">${uiLabelMap.UnitaResponsabile}</option><option  value="RESP_CATEG">${uiLabelMap.UnitaResponsabileCategoria}</option><option  value="CATEGORIA">${uiLabelMap.Categoria}</option><option  value="CATEG_RESP">${uiLabelMap.CategoriaUnitaResponsabile}</option><option  value="REFERENTE">${uiLabelMap.HeaderReferentName}</option><option  value="PUNTEGGIO">${uiLabelMap.WorkEffortAchieveViewScore}</option></select></td>
</tr>

<tr>
	<input type="hidden" name="snapshot" id="snapshot" value="${parameters.snapshot?if_exists?default("N")}" />
</tr>


<script type="text/javascript">
    WorkEffortPrintBirtExtraParameter = {
        load : function() {           
           
           //$('button-ok').hide();
           $('button-ok-disabled').hidden = true;
            
           var workEffortTypeId = "${parameters.workEffortTypeId?if_exists}";
                       
            if (workEffortTypeId) {
                ajaxUpdateArea('select-print-cell', '<@ofbizUrl>workEffortLoadPrintBirtList</@ofbizUrl>', $H({'workEffortTypeId' : workEffortTypeId}), {'onSuccess' : function(response, responseText) {
                    if (response.responseText && response.responseText.indexOf('<p>') != -1) {
                        $('select-type-print-row').hide();
                        $('select-print-row').show();
                    } else {
                   		$('select-type-print-row').hide();
                        $('select-print-row').hide();
                    }
                }});
            }
            
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
    
    WorkEffortPrintBirtExtraParameter.load();
</script>
	

