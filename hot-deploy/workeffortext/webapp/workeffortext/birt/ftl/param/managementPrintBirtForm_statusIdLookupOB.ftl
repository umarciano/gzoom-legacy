<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_currentStatusId}</td>
   <td class="widget-area-style">
       <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_statusId"> 
           <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[StatusItem]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[statusId, description<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[statusCode| equals| LOOKUPOB]]]"/>
	       
	       <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="statusId"/>   
           <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
           <div class="droplist_container"> 
               <input type="hidden" class="droplist_code_field" name="statusId"/>
               <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field" name="description_statusId" id="${printBirtFormId?default("ManagementPrintBirtForm")}_statusId_edit_value"/>
               <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span>
           </div>   
       </div>
   </td>
</tr>