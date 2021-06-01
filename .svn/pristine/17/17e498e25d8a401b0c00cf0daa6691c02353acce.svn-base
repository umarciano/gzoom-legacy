
<tr >
	<td  class="label" style="width: 18%;">	${uiLabelMap.HeaderRootType}</td>
	<td class="widget-area-style" >	
	       <div  class="droplist_field mandatory" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortAnalysisId">
		   <input  class="autocompleter_parameter" type="hidden" name="localAutocompleter" value="Y"/>
		   <#if workEffortTypeList?has_content>
		    	<#list workEffortTypeList as print>  	 
	       	       <input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortAnalysisId_${print.workEffortAnalysisId}" name="workEffortAnalysisId_${print.workEffortAnalysisId}" value="<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${print.descriptionTypeLang}<#else>${print.descriptionType}</#if>"/>
	           </#list>
	       </#if>
		   <input  class="autocompleter_option" type="hidden" name="choices" value="100"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortAnalysisId"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="descriptionType<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
		   <div class="droplist_container">
		   <input type="hidden" name="workEffortAnalysisId" value=""  class="droplist_code_field mandatory"/>
		   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortAnalysisId_edit_field" name="description_workEffortAnalysisId" size="100" maxlength="255" value=""  class="droplist_edit_field mandatory"/>
		   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div>
	</td>
</tr>




