<tr>
<td class="label">${uiLabelMap.WorkEffortAnalysis}</td>
<td class="widget-area-style">
<div  class="droplist_field mandatory" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortAnalysisId">
<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[<#if parentTypeId?has_content>[parentTypeId| equals| ${parentTypeId}]<#else>[parentTypeId| like| CTX%25]</#if>]]"/>
<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortAnalysisAndType]"/>
<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortAnalysisId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortAnalysisId"/>
<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
<div class="droplist_container">
<input type="hidden" name="workEffortAnalysisId" value="" class="droplist_code_field mandatory"/>
	<input type="text" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortAnalysisId_edit_field" name="description_workEffortAnalysisId" size="100" maxlength="255" value=""  class="droplist_edit_field mandatory"/>
	<span class="droplist-anchor">
	<a class="droplist_submit_field fa fa-2x" href="#"></a></span>
</div>
</div>
</td>
</tr>
<tr>
   <td class="label">${uiLabelMap.WorkEffort}</td>
   <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="fromManagement" value="Y"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[workEffortId, workEffortName, sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[sourceReferenceId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[workEffortName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortSnapshotId| equals| null]! <#if parentTypeId?has_content>[weContextId| equals| ${parentTypeId}]<#else>[weContextId| like| CTX%25]</#if>]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="sourceReferenceId"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_sourceReferenceId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortId_edit_field" name="sourceReferenceId_workEffortId" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_workEffortName" name="workEffortName_workEffortId" readonly="readonly"/><input type="hidden" class="lookup_field_description workEffortId" name="workEffortId" id="workEffortId" /></div></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.WorkEffortMeasureKpiThruDate}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("monitoringDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="monitoringDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames" value=""/></div></td>
</tr>

<tr>
	<td class="label"><b><i>${uiLabelMap.WorkEffortAnalysis_UnitaCoinvolte}</i></b></td>
	<td><hr></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.Responsibility}</td>
   <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="lookup_parameter" type="hidden" name="parentRoleTypeId" value="ORGANIZATION_UNIT"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookupPartyRoleOrgUnitView</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="<#--[[[roleTypeId| equals| field:orgUnitRoleTypeId_fld0_value]]]-->"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_field" name="parentRoleCode_orgUnitId" value="" size="25"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_orgUnitId" readonly="readonly"/><input type="hidden" class="lookup_field_description partyId" name="orgUnitId" id="orgUnitId" /></div></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.WorkEffortAnalysis_AltroRuolo}</td>
   <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyIdOtherRole"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://partyext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="lookup_parameter" type="hidden" name="parentRoleTypeId" value="ORGANIZATION_UNIT"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyIdOtherRole_edit_field" name="parentRoleCode_partyIdOtherRole" value="" size="25"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_partyIdOtherRole" readonly="readonly"/><input type="hidden" class="lookup_field_description partyId" name="partyIdOtherRole" id="partyIdOtherRole" /></div></div></td>
</tr>
<tr>
	<td class="label"><b><i>${uiLabelMap.WorkEffortAnalysis_SoggettiCoinvolti}</i></b></td>
	<td><hr></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.previewFormRespName}</td>
   <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://partyext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentRoleTypeId| equals| EMPLOYEE]]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_field" name="parentRoleCode_partyId" value="" size="25"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_partyId" readonly="readonly"/><input type="hidden" class="lookup_field_description partyId" name="partyId" id="partyId" /></div></div></td>
</tr>


<tr>
	<td colspan="1">
		<b><i>${uiLabelMap.ParametriOpzionale} </i></b> <br><br>
	</td>
	<td colspan="1">
		<br><hr><br>
	</td>	
</tr>
<tr>
    <td class="label">${uiLabelMap.ExposeReleaseDate}</td>
    <td class="widget-area-style"><select name="exposeReleaseDate" id="${printBirtFormId?default("ManagementPrintBirtForm")}_exposeReleaseDate" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExposePaginator}</td>
    <td class="widget-area-style"><select name="exposePaginator" id="${printBirtFormId?default("ManagementPrintBirtForm")}_exposePaginator" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.ExcludeIntroduction}</td>
    <td class="widget-area-style"><select name="excludeIntroduction" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeIntroduction" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>

<tr>
    <td class="label">${uiLabelMap.WorkEffortAnalysis_ExcludePageAndNote}</td>
    <td class="widget-area-style"><select name="excludeImageNote" id="${printBirtFormId?default("ManagementPrintBirtForm")}_excludeImageNote" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>


<script type="text/javascript">
    WorkEffortAnalysisPrintBirtExtraParameter = {
        load : function() {
            $('select-print-row').hide();
            $('button-ok').hide();
            $('select-addparams-print-row').hide();
            $('select-type-print-row').hide();
            
            var workEffortAnalysisIdDropList = DropListMgr.getDropList('${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortAnalysisId');
            if (workEffortAnalysisIdDropList) {
                workEffortAnalysisIdDropList.registerOnChangeListener(WorkEffortAnalysisPrintBirtExtraParameter.changeRadioButtons, 'WorkEffortAnalysisPrintBirtExtraParameterChangeRadioButtons');
            }
        },
        changeRadioButtons : function() {
            var workEffortAnalysisId = this._codeField.getValue();
            
            if (workEffortAnalysisId) {
                ajaxUpdateArea('select-print-cell', '<@ofbizUrl>WorkEffortAnalysisLoadPrintBirtList</@ofbizUrl>', $H({'workEffortAnalysisId' : workEffortAnalysisId}), {'onSuccess' : function(response, responseText) {
                    if (response.responseText && response.responseText.indexOf('<p>') != -1) {
                        $('select-print-row').show();
                        $('button-ok-disabled').hide();
                        $('button-ok').show();
                    } else {
                        $('select-print-row').hide();
                        $('button-ok-disabled').show();
                        $('button-ok').hide();
                    }
                }});
            }
            
        }
    }
    
    WorkEffortAnalysisPrintBirtExtraParameter.load();
</script>