
<table cellspacing="0" cellpadding="0" style="margin-top: 1.3em; width: 90%;" class="single-editable">
<tr>
<td>
	   <div  class="droplist_field ${mandatory}" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId">
	   
	   <!-- controllo se ho i permessi -->
	    <#if workEffortIdAll?default("N") == "Y">
	   	   	<#assign mapService = Static["com.mapsengineering.base.birt.util.Utils"].getMapUserPermision(parameters.security, parameters.parentTypeId, parameters.userLogin, null )/>           
	    <#else>
	    	<#assign mapService = Static["com.mapsengineering.base.birt.util.Utils"].getMapUserPermision(parameters.security, parameters.parentTypeId, parameters.userLogin, "18ORG0AMM", null )/> 
	  	</#if>
	  
	  	   
	   <#if (mapService.isOrgMgr  || mapService.isRole || mapService.isSup  || mapService.isTop) && parameters.useFilter?if_exists == "Y" >

		   <#assign result = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqy", mapService)/>
		   
		   <input  class="autocompleter_parameter" type="hidden" name="localAutocompleter" value="Y"/>
		   <input  class="autocompleter_option" type="hidden" name="choices" value="100"/>
		   <#if result.rowList?has_content>
	       	   <#list result.rowList as ele>
	       	       <input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_${ele.workEffortId}" name="workEffortId_${ele.workEffortId}" value="<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${ele.workEffortNameLang} - ${ele.sourceReferenceId}<#else>${ele.workEffortName} - ${ele.sourceReferenceId}</#if>"/>
	           </#list>
	       </#if>
       <#else>
       	   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, sourceReferenceId]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals| 18ORG0AMM]! [workEffortSnapshotId| <#if parameters.snapshot?if_exists?default("N") == 'Y'>not-equal<#else>equals</#if>| [null-field]]! [weContextId| equals| CTX_BS]]"/>  
		   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>    
       </#if>
   
	 
	   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
	   <div class="droplist_container">
	   <input type="hidden" name="workEffortId" value=""  class="droplist_code_field ${mandatory}"/>
	   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="workEffortName_workEffortId" size="100" maxlength="255" value=""  class="droplist_edit_field ${mandatory}"/>
	   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div>
   

</td>   

</tr>
</table>

