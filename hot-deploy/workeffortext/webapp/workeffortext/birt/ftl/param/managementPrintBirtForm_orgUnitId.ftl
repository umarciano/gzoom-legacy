<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
   <td class="widget-area-style">
   <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId">
   
   
   <!-- controllo se ho i permessi -->
   <#assign mapService = Static["com.mapsengineering.base.birt.util.Utils"].getMapUserPermisionOrgUnit(parameters.security, parameters.parentTypeId, parameters.userLogin, false )/> 

  	   
   <#if mapService.isOrgMgr  || mapService.isSup  || mapService.isTop >
       <#if orderUoBy?if_exists == "EXTCODE">
           <#assign dummy = mapService.put("queryOrderBy", "PA.EXTERNAL_ID")?default("")>
       <#elseif orderUoBy?if_exists == "UONAME">
           <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
               <#assign dummy = mapService.put("queryOrderBy", "PA.PARTY_NAME_LANG")?default("")>
           <#else>
               <#assign dummy = mapService.put("queryOrderBy", "PA.PARTY_NAME")?default("")>
           </#if>
       <#else>
           <#assign dummy = mapService.put("queryOrderBy", "PP.PARENT_ROLE_CODE")?default("")>
       </#if>
	   <#assign result = dispatcher.runSync("executePerformFindPartyRoleOrgUnit", mapService)/>
	   
	   <input  class="autocompleter_parameter" type="hidden" name="localAutocompleter" value="Y"/>
       <#list result.rowList as ele>
           <#if showUoCode?if_exists == "MAIN">
       		   <input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_${ele.partyId}" name="orgUnitId_${ele.partyId}" value="${ele.parentRoleCode} - <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${ele.partyNameLang}<#else>${ele.partyName}</#if>"/>
       	   <#elseif showUoCode?if_exists == "EXT">
       	       <input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_${ele.partyId}" name="orgUnitId_${ele.partyId}" value="${ele.externalId?if_exists} - <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${ele.partyNameLang}<#else>${ele.partyName}</#if>"/>
       	   <#else>
       	       <input type="hidden" class="autocompleter_local_data" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_${ele.partyId}" name="orgUnitId_${ele.partyId}" value="<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>${ele.partyNameLang}<#else>${ele.partyName}</#if>"/>
       	   </#if>
       </#list>
   <#else>
		<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
		<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyAndPartyParentRoleAndRoleTypeView]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	   
	   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, parentRoleCode, externalId, partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	   <#if orderUoBy?if_exists == "EXTCODE">
	       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[externalId]]"/>
	   <#elseif orderUoBy?if_exists == "UONAME">
	       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	   <#else>
	       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentRoleCode]]"/>
	   </#if>
	   <#if showUoCode?if_exists == "MAIN">
	       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[parentRoleCode, partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	   <#elseif showUoCode?if_exists == "EXT">
	       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[externalId, partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	   <#else>
	       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>]]"/>
	   </#if>
		   
	   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentRoleTypeId| equals| ORGANIZATION_UNIT]! [organizationId| equals| ${defaultOrganizationPartyId?if_exists}]]]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/> 
	   <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
	       <input  class="autocompleter_parameter" type="hidden" name="partyName_description" value="@{partyNameLang}"/>
	   <#else>
	       <input  class="autocompleter_parameter" type="hidden" name="partyName_description" value="@{partyName}"/>
	   </#if>  
   </#if>
       
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="partyId"/>   
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="partyName<#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>Lang</#if>"/>
   <div class="droplist_container"> 
   <input type="hidden" class="droplist_code_field" name="orgUnitId"/>
   <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field" name="partyName_orgUnitId" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_value"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>