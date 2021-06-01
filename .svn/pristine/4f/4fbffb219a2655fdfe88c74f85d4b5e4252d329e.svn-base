<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_roleTypeId}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   	<#if parameters.parentTypeId?if_exists == "CTX_EP">
		<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortAndRoleTypeIndividualeView]"/>
	<#else>
		<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[RoleType]"/>
	</#if>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, roleTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentTypeId, roleTypeId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentTypeId| equals| EMPLOYEE]]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="roleTypeId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
   <div class="droplist_container"> <input type="text" size="100" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="roleTypeId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="roleTypeId"/><span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>