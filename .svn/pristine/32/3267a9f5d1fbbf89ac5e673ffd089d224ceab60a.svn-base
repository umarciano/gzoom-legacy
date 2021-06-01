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
   </#if>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortTypeId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
   <div class="droplist_container"> 
   <input type="text" size="100" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="workEffortTypeIdRef_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeIdRef_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="workEffortTypeIdRef"/><span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
