<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_weTransDate}</td>
   <td class="widget-area-style">
       <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_customTimePeriod"> 
       
           <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[CustomTimePeriod]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[customTimePeriodId, periodTypeId, periodName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, fromDate]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[customTimePeriodId]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[periodName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[periodTypeId| equals| FISCAL_YEAR]]]"/>
	       <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="fromDate"/>   
           <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="periodName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
           <div class="droplist_container"> 
               <input type="hidden" class="droplist_code_field mandatory" name="customTimePeriod"/>
               <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field mandatory" name="description_customTimePeriod" id="${printBirtFormId?default("ManagementPrintBirtForm")}_customTimePeriod_edit_value"/>
               <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span>
           </div>   
       </div>
   </td>
</tr>