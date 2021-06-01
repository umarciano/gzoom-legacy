<tr>
   <td class="label">${uiLabelMap.HeaderRootType}</td>
   <td class="widget-area-style"><div  class="droplist_field mandatory" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortType]"/><input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isRoot| equals| Y]! <#if parentTypeId?has_content>[parentTypeId| equals| ${parentTypeId}]<#else>[parentTypeId| like| CTX%25]</#if>]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortTypeId"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
   <div class="droplist_container"><input type="hidden" name="workEffortTypeId" value=""  class="droplist_code_field mandatory"/>
   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId_edit_field" name="description_workEffortTypeId" size="100" maxlength="255" value=""  class="droplist_edit_field mandatory"/>
   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
	<td class="label">${uiLabelMap.WorkeffortRoot}</td>
<#if parameters.snapshot?if_exists?default("N") == 'Y'>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
    <input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/>
    <input  class="lookup_parameter" type="hidden" name="snapshot" value="Y"/>
    <input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/>
    <input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
    <input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId, workEffortRevisionId, workEffortRevisionDescr]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals| field:workEffortTypeId]! [workEffortSnapshotId| not-equal| null] <#if parentTypeId?has_content>[weContextId| equals| ${parentTypeId}]<#else>[weContextId| like| CTX%25]</#if>]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/>
    <div class="lookup_container">    
    <input type="hidden" class="lookup_field_description workEffortRevisionDescr" name="workEffortRevisionDescr_workEffortId" id="workEffortRevisionDescr_workEffortId" />    
    <input type="hidden" class="lookup_field_description workEffortRevisionId" name="workEffortRevisionId_workEffortId" id="workEffortRevisionId_workEffortId" />
    <input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="sourceReferenceId_workEffortId" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortId" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description workEffortId" name="workEffortId" id="workEffortId" /></div></div></td>
	
  	
  <#else>
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
    <input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/>
    <input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/>
    <input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
    <input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals| field:workEffortTypeId]! [workEffortSnapshotId| equals| null]! <#if parentTypeId?has_content>[weContextId| equals| ${parentTypeId}]<#else>[weContextId| like| CTX%25]</#if>]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/><div class="lookup_container">
    <input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="sourceReferenceId_workEffortId" value="" size="12"/>
    <span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortId" readonly="readonly" style="width: 56.2em;"/>
    <input type="hidden" class="lookup_field_description workEffortId" name="workEffortId" id="workEffortId" /></div></div></td>
</#if>
</tr>

<#if parameters.snapshot?if_exists?default("N")  == 'Y'>
<tr>
   	<td class="label">${uiLabelMap.FormFieldTitle_workEffortRevisionId}</td>
	<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortRevision]"/>
	<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortRevisionId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isAutomatic| equals| N]! <#if parentTypeId?has_content>[workEffortTypeIdCtx| equals| ${parentTypeId}]<#else>[workEffortTypeIdCtx| like| CTX%]</#if>]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortRevisionId"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
	<div class="droplist_container">
		<input type="hidden" name="workEffortRevisionId" value=""  class="droplist_code_field"/>
		<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId_edit_field" name="description_workEffortRevisionId" size="50" value="" class="droplist_edit_field"/>
		<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
   	<td class="label">${uiLabelMap.FormFieldTitle_workEffortRevisionIdComp}</td>
	<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionIdComp">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortRevision]"/>
	<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortRevisionId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isAutomatic| equals| N]! <#if parentTypeId?has_content>[workEffortTypeIdCtx| equals| ${parentTypeId}]<#else>[workEffortTypeIdCtx| like| CTX%]</#if>]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortRevisionId"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
	<div class="droplist_container">
		<input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="workEffortRevisionIdComp_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_etch_edit_value"/>
   		<input type="hidden" class="droplist_code_field" name="workEffortRevisionIdComp"/>
   		<span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
   		
   	<#--		<input type="hidden" name="workEffortRevisionIdComp" value=""  class="droplist_code_field"/>
		<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionIdComp_edit_field" name="description_workEffortRevisionIdComp" size="50" value="" class="droplist_edit_field"/>
		<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
	-->
</tr>
</#if>

<tr>
   <td class="label">${uiLabelMap.WorkEffort}</td>
    
   <td class="widget-area-style">
	   <div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild">
	   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/>
	   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[Y]"/>
	   <!--snapshot -->
	   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
		<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${parameters.snapshot}"/> 	
	   </#if>
	  
	   <!--localeSecondarySet -->
   	   <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortNameLang, sourceReferenceId]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortNameLang]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortNameLang]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortNameLang"/>
	   <#else>
		   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/>
	   	   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortName]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/>
		   <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="workEffortName"/>
	   </#if>
	   
	   <#if parameters.snapshot?if_exists?default("N") == 'Y'>	
			<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortSnapshotId| not-equal | [null-field]]! [weContextId| like| CTX%25]]]"/>
	    <#else>
	    	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortSnapshotId| equals| [null-field]]! [weContextId| like| CTX%25]]]"/>	    
	   	
	   </#if>
	   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortId"/>
	   
	   <div class="droplist_container">
	   <input type="hidden" name="workEffortIdChild" value=""  class="droplist_code_field"/>
	   <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field" name="workEffortName_workEffortIdChild" size="100" maxlength="255" value=""  class="droplist_edit_field"/>
	   <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div>

   </td>
</tr>

<tr>
   <td class="label">${uiLabelMap.WorkEffortMeasureKpiThruDate}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("monitoringDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="monitoringDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames" value=""/></div></td>
</tr>


<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleOrgUnitView]"/>
   
   <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
       <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyNameLang, parentRoleCode, orgUnitRoleTypeId]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyNameLang]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyNameLang]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="partyNameLang"/>
   <#else>
	   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode, orgUnitRoleTypeId]]"/>
   	   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyName]]"/>
   	   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/>
       <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="partyName"/>
   </#if>
   
   <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="partyId"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentRoleTypeId| equals| ORGANIZATION_UNIT]]]"/>
  
   <div class="droplist_container"> 
   <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field" name="orgUnitId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_value"/>
   <input type="hidden" class="droplist_code_field" name="orgUnitId"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>


<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_workEffortTypeIdRef}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeIdRef"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortType]"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentTypeId, workEffortTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortTypeId"/><input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container"> <input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="workEffortTypeIdRef_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeIdRef_edit_value"/><input type="hidden" class="droplist_code_field" name="workEffortTypeIdRef"/><span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_workEffortTypeIdObb}</td>
    <td colspan="10">
    <div class="text-find" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId_Obb">
    	<input size="50" autocomplete="off" class="text-find-element" name="workEffortTypeId_Obb"  type="text">
  	</div>
  	</td>
</tr>
<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_roleTypeId}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[RoleType]"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, roleTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[parentTypeId, roleTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentTypeId| equals| EMPLOYEE]]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="roleTypeId"/><input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container"> <input type="text" size="50" maxlength="255" autocomplete="off" value="" class="droplist_edit_field" name="roleTypeId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId_edit_value"/><input type="hidden" class="droplist_code_field" name="roleTypeId"/><span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.previewFormRespName}</td>
   <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://partyext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[roleTypeId| equals| field:roleTypeId]]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_field" name="parentRoleCode_partyId" value="" size="25"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_partyId" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description partyId" name="partyId" id="partyId" /></div></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.CommonFrom}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("fromDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}">
   <input type="hidden" class="dateParams" name="paramName" value="fromDate"/>
   <input type="hidden" class="dateParams" name="time" value="false"/>
   <input type="hidden" class="dateParams" name="shortDateInput" value="true"/>
   <input type="hidden" class="dateParams" name="dateTimeValue" value=""/>
   <input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/>
   <input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/>
   <input type="hidden" class="dateParams" name="yearRange" value=""/>
   <input type="hidden" class="dateParams" name="localizedValue" value=""/>
   <input type="hidden" class="dateParams" name="size" value="10"/>
   <input type="hidden" class="dateParams" name="maxlength" value="10"/>
   <input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/>
   <input type="hidden" class="dateParams" name="classNames" /></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.CommonTo}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("thruDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}">
   <input type="hidden" class="dateParams" name="paramName" value="thruDate"/>
   <input type="hidden" class="dateParams" name="time" value="false"/>
   <input type="hidden" class="dateParams" name="shortDateInput" value="true"/>
   <input type="hidden" class="dateParams" name="dateTimeValue" value=""/>
   <input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/>
   <input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/>
   <input type="hidden" class="dateParams" name="yearRange" value=""/>
   <input type="hidden" class="dateParams" name="localizedValue" value=""/>
   <input type="hidden" class="dateParams" name="size" value="10"/>
   <input type="hidden" class="dateParams" name="maxlength" value="10"/>
   <input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/>
   <input type="hidden" class="dateParams" name="classNames"/></div></td>
</tr>
<tr>
	<td colspan="1">
		<br><hr><br>
	</td>	
</tr>
<tr>
	<td colspan="2">
		<b><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${uiLabelMap.ParametriOpzionale} </i></b> <br><br>
	</td>
</tr>

<tr>
    <td class="label">${uiLabelMap.ExcludeFinalcialResource}</td>
    <td class="widget-area-style"><select name="excludeFinalcialResource" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeFinalcialResource" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeHumanResource}</td>
    <td class="widget-area-style"><select name="excludeHumanResource" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeHumanResource" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeIndicator}</td>
    <td class="widget-area-style"><select name="excludeIndicator" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeIndicator" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludePerformanceKPI}</td>
    <td class="widget-area-style"><select name="excludePerformanceKPI" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludePerformanceKPI" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeGantt}</td>
    <td class="widget-area-style"><select name="excludeGantt" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeGantt" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeCollegati}</td>
    <td class="widget-area-style"><select name="excludeCollegati" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeCollegati" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeRequest}</td>
    <td class="widget-area-style"><select name="excludeRequest" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeRequest" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>

<tr>
    <td class="label">${uiLabelMap.ExcludeExtensionDescriotion}</td>
    <td class="widget-area-style"><select name="excludeExtensionDescriotion" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeExtensionDescriotion" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeReference}</td>
    <td class="widget-area-style"><select name="excludeReference" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeReference" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.SelectNote}</td>
    <td class="widget-area-style"><select name="selectNote" id="${printBirtFormId?default("ManagementPrintBirtForm")}_selectNote" size="1" class=""><option value="ALL">${uiLabelMap.CommonAll}</option><option  value="NONE">${uiLabelMap.CommonNone}</option><option selected="selected" value="DATE">${uiLabelMap.CommonTheDate}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.TypeNotes}</td>
    <td class="widget-area-style"><select name="typeNotes" id="${printBirtFormId?default("ManagementPrintBirtForm")}_typeNotes" size="1" class=""><option value="Y">${uiLabelMap.CommonInternal}</option><option value="N">${uiLabelMap.CommonExternal}</option><option selected="selected" value="ALL">${uiLabelMap.CommonAll}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExposeDetailIndicator}</td>
    <td class="widget-area-style"><select name="exposeDetailIndicator" id="${printBirtFormId?default("ManagementPrintBirtForm")}_exposeDetailIndicator" size="1" class=""><option selected="selected" value="Y">${uiLabelMap.CommonYes}</option><option value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExposeReleaseDate}</td>
    <td class="widget-area-style"><select name="exposeReleaseDate" id="${printBirtFormId?default("ManagementPrintBirtForm")}_exposeReleaseDate" size="1" class=""><option selected="selected" value="Y">${uiLabelMap.CommonYes}</option><option value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExposePaginator}</td>
    <td class="widget-area-style"><select name="exposePaginator" id="${printBirtFormId?default("ManagementPrintBirtForm")}_exposePaginator" size="1" class=""><option selected="selected" value="Y">${uiLabelMap.CommonYes}</option><option value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeValidity}</td>
    <td class="widget-area-style"><select name="excludeValidity" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeValidity" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeIntroduction}</td>
    <td class="widget-area-style"><select name="excludeIntroduction" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeIntroduction" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeScore}</td>
    <td class="widget-area-style"><select name="excludeScore" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeScore" size="1" class=""><option selected="selected" value="Y">${uiLabelMap.CommonYes}</option><option value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>

<script type="text/javascript">
    WorkEffortPrintBirtExtraParameter = {
        load : function() {
            $('select-print-row').hide();
            $('button-ok').hide();
            $('select-addparams-print-row').hide();
            $('select-type-print-row').hide();            
            
            var workEffortTypeidDropList = DropListMgr.getDropList('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId');
            if (workEffortTypeidDropList) {
                workEffortTypeidDropList.registerOnChangeListener(WorkEffortPrintBirtExtraParameter.changeRadioButtons, 'WorkEffortPrintBirtExtraParameterChangeRadioButtons');
            }
            
             //se sono nel caso dello storico mi metto in ascolto della lookup per settera la drop della versione
            <#if parameters.snapshot?if_exists?default("N")  == 'Y'>
	        var form = $('${printBirtFormId?default("ManagementPrintBirtForm")}');
		    if (form) {
		        var divLookupWorkEffortIdField = form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId');
		        //registro sul field
		        if(divLookupWorkEffortIdField) {
		            var lookup = LookupMgr.getLookup(divLookupWorkEffortIdField.identify());
		            if (lookup) {
		                lookup.registerOnSetInputFieldValue(WorkEffortPrintBirtExtraParameter.setWorkEffortRevisionId.curry(form), '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId');
		            }
		        }
		      }
            </#if>
        },
        changeRadioButtons : function() {
            var workEffortTypeId = this._codeField.getValue();
            
            if (workEffortTypeId) {
                ajaxUpdateArea('select-print-cell', '<@ofbizUrl>workEffortLoadPrintBirtList</@ofbizUrl>', $H({'workEffortTypeId' : workEffortTypeId}), {'onSuccess' : function(response, responseText) {
                    if (response.responseText && response.responseText.indexOf('<p>') != -1) {
                        $('select-type-print-row').hide();
                        $('select-print-row').show();
                        $('button-ok').hide();
            			$('button-ok-disabled').show();
                    } else {
                   		$('select-type-print-row').hide();
                        $('select-print-row').hide();
                    }
                }});
            }
        },
        setWorkEffortRevisionId : function(form){
	        var div = $(form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId'));
	        var workEffortRevisionId = $(div.down("input.workEffortRevisionId"));
	        var workEffortRevisionDescr = $(div.down("input.workEffortRevisionDescr"));
	        var elementDiv = $(form.down("div#" + '${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortRevisionId'));
	        
	        var elementDesc = $A(elementDiv.select('input')).find(function(s) {
	            return s.readAttribute('name').indexOf('description_workEffortRevisionId') > -1;
	        });
	        elementDesc.setValue(workEffortRevisionDescr.getValue());
	        
	        var elementId = $A(elementDiv.select('input')).find(function(s) {
	            return s.readAttribute('name').indexOf('workEffortRevisionId') > -1;
	        });
	        elementId.setValue(workEffortRevisionId.getValue());
	    }
    }
    
    WorkEffortPrintBirtExtraParameter.load();
</script>