<#include  "/workeffortext/webapp/workeffortext/birt/ftl/param/managementPrintBirtForm_workEffortId_mandatory.ftl" />

<tr>
   <td class="label">${uiLabelMap.Area}</td>
    
   <td class="widget-area-style">
	   <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild">
	   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[Y]"/>
	   <!--snapshot -->
	   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
		<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
	   </#if>
	  
	   <!--localeSecondarySet -->
   	   <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortNameLang, sourceReferenceId, weParentId]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortNameLang]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortNameLang, sourceReferenceId <#if parameters.snapshot?if_exists?default("N") == 'Y'>, workEffortRevisionDescr</#if>]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortNameLang"/>
	   <#else>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId, weParentId]]"/>
	   	   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName, sourceReferenceId <#if parameters.snapshot?if_exists?default("N") == 'Y'>, workEffortRevisionDescr</#if>]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName"/>
	   </#if>
	   
	   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
			<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[weParentId| equals | field:workEffortId]! [workEffortId| not-equal | field:workEffortId]! [workEffortSnapshotId| not-equal | [null-field]]! [weContextId| like| CTX%25]! [organizationId| equals| ${defaultOrganizationPartyId?if_exists}]]]"/>
	    <#else>
	    	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[weParentId| equals | field:workEffortId]! [workEffortId| not-equal | field:workEffortId]! [workEffortSnapshotId| equals| [null-field]]! [weContextId| like| CTX%25]! [organizationId| equals| ${defaultOrganizationPartyId?if_exists}]]]"/>	    
	   	
	   </#if>
	   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
	   
	   <div class="droplist_container">
	   <input type="hidden" name="workEffortIdChild" value=""  class="droplist_code_field"/>
	   <input type="text" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field" name="workEffortName_workEffortIdChild" size="100" maxlength="255" value=""  class="droplist_edit_field"/>
	   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div>

   </td>
</tr>

<script>
    WorkEffortIdWorkEffortIdChildExtPrintBirtExtraParameter = {
        load : function() {
            $('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').writeAttribute("readonly", "readonly");
            $('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').next().writeAttribute("style", "display: none;");
            	            
            var workEffortIdDropList = DropListMgr.getDropList('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId');
            if (workEffortIdDropList) {
                workEffortIdDropList.registerOnChangeListener(WorkEffortIdWorkEffortIdChildExtPrintBirtExtraParameter.changeRadioButtons, 'WorkEffortIdWorkEffortIdChildExtPrintBirtExtraParameterChangeRadioButtons');
            }
        },
        changeRadioButtons : function() {
            var workEffortId = this._codeField.getValue();
            if (workEffortId) {
                $('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').removeAttribute("readonly");
                $('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').next().removeAttribute("style");
            } else {
            	$$('input:[name="workEffortIdChild"]')[0].setValue("");
            	$('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').setValue("");
            	$('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').writeAttribute("readonly", "readonly");
            	$('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').next().writeAttribute("style", "display: none;");
            }
        }
    }
    
    //WorkEffortIdWorkEffortIdChildExtPrintBirtExtraParameter.load();
    document.observe("dom:loaded", jQuery(WorkEffortIdWorkEffortIdChildExtPrintBirtExtraParameter.load));   
</script>