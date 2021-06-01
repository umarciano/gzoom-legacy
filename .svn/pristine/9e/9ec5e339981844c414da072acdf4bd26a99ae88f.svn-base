
<tr> ${parameters.parentTypeId}
   <td class="label">${uiLabelMap.FormFieldTitle_workEffortTypeIdRef}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeIdRef">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortType]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentTypeId, workEffortTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
   <#if parameters.parentTypeId?if_exists == "CTX_EP">
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isRoot| equals| Y]! [isTemplate| equals| N]! <#if parameters.parentTypeId?has_content>[parentTypeId| equals| ${parameters.parentTypeId}]<#else>[parentTypeId| like| CTX%25]</#if>]]"/>
   <#else>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[<#if parameters.parentTypeId?has_content>[parentTypeId| equals| ${parameters.parentTypeId}]<#else>[parentTypeId| like| CTX%25]</#if>]]"/>
   </#if>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortTypeId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
   <div class="droplist_container"> 
   <input type="text" size="100" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="workEffortTypeIdRef_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeIdRef_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="workEffortTypeIdRef"/><span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.WorkEffort}</td>
    
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
			<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals | field:workEffortTypeIdRef]! [workEffortId| not-equal | field:workEffortId]! [workEffortSnapshotId| not-equal | [null-field]]! [weContextId| like| CTX%25]]]"/>
	    <#else>
	    	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals | field:workEffortTypeIdRef]! [workEffortId| not-equal | field:workEffortId]! [workEffortSnapshotId| equals| [null-field]]! [weContextId| like| CTX%25]]]"/>	    
	   	
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
    WorkEffortTypeIdRefWorkEffortIdChildParameter = {
        load : function() {
            $('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').writeAttribute("readonly", "readonly");
            $('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field').next().writeAttribute("style", "display: none;");
            	            
            var workEffortTypeIdRefDropList = DropListMgr.getDropList('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeIdRef');
            if (workEffortTypeIdRefDropList) {
                workEffortTypeIdRefDropList.registerOnChangeListener(WorkEffortTypeIdRefWorkEffortIdChildParameter.changeRadioButtons, 'WorkEffortTypeIdRefWorkEffortIdChildParameter');
            }
        },
        changeRadioButtons : function() {
            var workEffortTypeIdRef = this._codeField.getValue();
            if (workEffortTypeIdRef) {
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
    
    document.observe("dom:loaded", jQuery(WorkEffortTypeIdRefWorkEffortIdChildParameter.load));   
</script>