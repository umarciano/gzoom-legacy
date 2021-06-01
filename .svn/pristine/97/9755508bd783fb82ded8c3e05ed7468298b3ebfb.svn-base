<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
   <td class="widget-area-style">
   <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId">
   
   
   <!-- controllo se ho i permessi -->
   <#assign mapService = Static["com.mapsengineering.base.birt.util.Utils"].getMapUserPermisionOrgUnit(parameters.security, parameters.parentTypeId, parameters.userLogin )/> 

  	   
   <#if mapService.isOrgMgr  || mapService.isSup  || mapService.isTop >

	   <#assign result = dispatcher.runSync("executePerformFindPartyRoleOrgUnit", mapService)/>
	   
	   <input  class="autocompleter_parameter" type="hidden" name="localAutocompleter" value="Y"/>
       <#list result.rowList as ele>
       		<input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_${ele.partyId}" name="orgUnitId_${ele.partyId}" value="${ele.parentRoleCode} - <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${ele.partyNameLang}<#else>${ele.partyName}</#if>"/>
       </#list>
   <#else>
		<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
		<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyAndPartyParentRoleAndRoleTypeView]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	   
	   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>, parentRoleCode]]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentRoleCode]]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
		   
	   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentRoleTypeId| equals| ORGANIZATION_UNIT]! [organizationId| equals| ${defaultOrganizationPartyId?if_exists}]]]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/> 
	   <input  class="autocompleter_parameter" type="hidden" name="partyName_description" value="@{parentRoleCode} - @{partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>}"/>
	      
   </#if>
       
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="partyId"/>   
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
   <div class="droplist_container"> 
   <input type="hidden" class="droplist_code_field" name="orgUnitId"/>
   <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field" name="partyName_orgUnitId" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_value"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>