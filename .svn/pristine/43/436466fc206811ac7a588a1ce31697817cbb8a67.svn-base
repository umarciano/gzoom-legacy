<tr>
   <td class="label">${uiLabelMap.HeaderRootType}</td>
   <td class="widget-area-style"><div  class="droplist_field mandatory" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortType]"/><input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isRoot| equals| Y]! <#if parentTypeId?has_content>[parentTypeId| equals| ${parentTypeId}]<#else>[parentTypeId| like| CTX%25]</#if>]]"/><input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortTypeId"/><input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container"><input type="hidden" name="workEffortTypeId" value=""  class="droplist_code_field mandatory"/><input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortTypeId_edit_field" name="description_workEffortTypeId" size="100" maxlength="255" value=""  class="droplist_edit_field mandatory"/><span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
	<td class="label">${uiLabelMap.WorkeffortRoot}</td>
<#if parameters.snapshot?if_exists?default("N") == 'Y'>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="parameters.snapshot"/> 
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="snapshot" value="Y"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals| field:workEffortTypeId]! [workEffortSnapshotId| not-equals| null]! <#if parentTypeId?has_content>[weContextId| equals| ${parentTypeId}]<#else>[weContextId| like| CTX%25]</#if>]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="sourceReferenceId_workEffortId" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortId" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description workEffortId" name="workEffortId" id="workEffortId" /></div></div></td>
  <#else>
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeId| equals| field:workEffortTypeId]! [workEffortSnapshotId| equals| null]! <#if parentTypeId?has_content>[weContextId| equals| ${parentTypeId}]<#else>[weContextId| like| CTX%25]</#if>]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="sourceReferenceId_workEffortId" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortId" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description workEffortId" name="workEffortId" id="workEffortId" /></div></div></td>
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
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isAutomatic| equals| N]! [workEffortTypeIdCtx| like| CTX%]]]"/>
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
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[isAutomatic| equals| N]! [workEffortTypeIdCtx| like| CTX%]]]"/>
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
<#if parameters.snapshot?if_exists?default("N")  == 'Y'>
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="snapshot" value="Y"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortSnapshotId| not-equals| null]! [weContextId| like| CTX%25]]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field" name="sourceReferenceId_workEffortIdChild" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortIdChild" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description workEffortId" name="workEffortIdChild" id="workEffortIdChild" /></div></div></td>
 <#else>
	<td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortSnapshotId| equals| null]! [weContextId| like| CTX%25]]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortIdChild_edit_field" name="sourceReferenceId_workEffortIdChild" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortIdChild" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description workEffortId" name="workEffortIdChild" id="workEffortIdChild" /></div></div></td>
</#if>
   
</tr>
<tr>
   <td class="label">${uiLabelMap.WorkEffortMeasureKpiThruDate}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("monitoringDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="monitoringDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames" value=""/></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
   <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="lookup_parameter" type="hidden" name="parentRoleTypeId" value="ORGANIZATION_UNIT"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleOrgUnitView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookupPartyRoleOrgUnitView</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode, orgUnitRoleTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="<#--[[[roleTypeId| equals| field:orgUnitRoleTypeId_fld0_value]]]-->"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_field" name="parentRoleCode_orgUnitId" value="" size="25"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_orgUnitId" readonly="readonly" style="width: 56.2em;"/><input type="hidden" class="lookup_field_description partyId" name="orgUnitId" id="orgUnitId" /></div></div></td>
   <input type="hidden" name="orgUnitRoleTypeId" class="orgUnitRoleTypeId"/>
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
    <td class="label">${uiLabelMap.TypeFormat}</td>
    <td class="widget-area-style"><select name="typeFormat" id="${printBirtFormId?default("ManagementPrintBirtForm")}_typeFormat" size="1" class=""><option value="AREA_SOTT_PROCES">${uiLabelMap.AreaSottProcesso}</option><option  value="AREA_SOTT">${uiLabelMap.AreaSott}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.PrintRischioControllaton}</td>
    <td class="widget-area-style"><select name="printRischioControllato" id="${printBirtFormId?default("ManagementPrintBirtForm")}_printRischioControllato" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.PrintAllProcess}</td>
    <td class="widget-area-style"><select name="printAllProcess" id="${printBirtFormId?default("ManagementPrintBirtForm")}_printAllProcess" size="1" class=""><option selected="selected" value="Y">${uiLabelMap.CommonYes}</option><option value="N">${uiLabelMap.CommonNo}</option></select></td>
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
        }
    }
    
    WorkEffortPrintBirtExtraParameter.load();
</script>