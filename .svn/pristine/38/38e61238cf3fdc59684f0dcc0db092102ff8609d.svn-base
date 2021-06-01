<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_currentStatusId}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_currentStatusId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortWithTypeAndAllStatusView]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[statusId, sequenceId, description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sequenceId]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isTemplate| equals| N]! [isRoot| equals| Y]! [parentTypeId| equals| <#if parameters.parentTypeId?if_exists?has_content> ${parameters.parentTypeId?if_exists}<#else> CTX%25</#if>]]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[Y]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="statusId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
   <input type="text" size="100" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="statusId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_statusId_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="currentStatusId"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>