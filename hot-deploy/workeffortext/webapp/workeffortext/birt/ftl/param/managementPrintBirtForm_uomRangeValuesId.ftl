<tr>
   <td class="label">${uiLabelMap.UomRangeValuesId}</td>
   <td class="widget-area-style">
   <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_uomRangeValuesId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[UomRangeValues]"/>
   
   <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
       <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[commentsLang, uomRangeId, uomRangeValuesId]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[commentsLang]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[commentsLang]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="commentsLang"/>
   <#else>
	   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[comments, uomRangeId, uomRangeValuesId]]"/>
   	   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[comments]]"/>
   	   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[comments]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="comments"/>
   </#if>
   
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[uomRangeId| equals| ${uomRangeId?if_exists}]]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="uomRangeValuesId"/>
   
   <div class="droplist_container"> 
   <input type="hidden" class="lookup_field_description uomRangeId" name="uomRangeId" id="uomRangeId_uomRangeValuesId"/>
   <input type="text" size="25" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="uomRangeValuesId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_uomRangeValuesId_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="uomRangeValuesId"/><span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>

</tr>