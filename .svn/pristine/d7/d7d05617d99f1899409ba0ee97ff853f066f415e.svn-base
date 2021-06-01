
<tr>
   <td class="label">${uiLabelMap.WorkEffort}</td>
    
   <td class="widget-area-style">
	   <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild">
	   
	   
	   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
		<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
	   </#if>
	   
	   <!-- controllo se ho i permessi -->
	   <#assign mapServiceChild = Static["com.mapsengineering.base.birt.util.Utils"].getMapUserPermision(parameters.security, parameters.parentTypeId, parameters.userLogin, null)/>           
   
	   <#if mapService.isOrgMgr  || mapService.isRole || mapService.isSup  || mapService.isTop >
		   <#assign resultChild = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqy", mapServiceChild)/>
		   
		   <input  class="autocompleter_parameter" type="hidden" name="localAutocompleter" value="Y"/>
	       <#list resultChild.rowList as ele>
	       		<input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_${ele.workEffortId}" name="workEffortIdChild_${ele.workEffortId}" value="<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${ele.workEffortNameLang}<#else>${ele.workEffortName}</#if>"/>
	       </#list>
       <#else>
       	   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortSnapshotId| <#if parameters.snapshot?if_exists?default("N") == 'Y'>not-equal<#else>equals</#if>| [null-field]]! <#if parameters.parentTypeId?if_exists?has_content>[weContextId| equals| ${parameters.parentTypeId?if_exists}]<#else>[weContextId| like| CTX%25]</#if>]]"/>  
		   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>    
       </#if>
	   
	   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>

	   <div class="droplist_container">
	   <input type="hidden" name="workEffortIdChild" value=""  class="droplist_code_field"/>
	   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field" name="workEffortName_workEffortIdChild" size="100" maxlength="255" value=""  class="droplist_edit_field"/>
	   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div>

   </td>
</tr>
   
</tr>