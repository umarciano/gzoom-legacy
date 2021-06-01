<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_orgUnitRoleTypeId}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitRoleTypeId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[RoleType]"/><input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, roleTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[roleTypeId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentTypeId| equals| ORGANIZATION_UNIT]]]"/><input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="roleTypeId"/><input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container"><input type="hidden" name="orgUnitRoleTypeId" value=""  class="droplist_code_field"/><input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitRoleTypeId_edit_field" name="description_orgUnitRoleTypeId" size="100" maxlength="255"value=""  class="droplist_edit_field"/><span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_orgUnitId}</td>
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookupPartyRoleOrgUnitView</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[roleTypeId| equals| field:orgUnitRoleTypeId]]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_orgUnitId_edit_field" name="parentRoleCode_orgUnitId" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_orgUnitId" readonly="readonly"/><input type="hidden" class="lookup_field_description partyId" name="orgUnitId" id="orgUnitId" /></div></div></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_employeeId}</td>
<!-- Bug 4912 punto 4 -->    
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_employeeId"><input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/><input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/><input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/><input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/><input  class="lookup_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/><input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/><input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/><input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyId]]"/><input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/><input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[roleTypeId| equals| EMPLOYEE]]]"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container"><input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_employeeId_edit_field" name="parentRoleCode_employeeId" value="" size="12"/><span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/><input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_employeeId" readonly="readonly"/><input type="hidden" class="lookup_field_description partyId" name="employeeId" id="employeeId" /></div></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.CommonFrom}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("fromDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="fromDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames" value="mandatory"/></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.CommonTo}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("thruDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="thruDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames"/></div></td>
</tr>
<tr>
	<td colspan="1">
		<br>
	</td>	
</tr>
<tr>
	<td colspan="2">
		<b><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${uiLabelMap.ParametriOpzionale} </i></b> <br><br>
	</td>
</tr>
<tr>
    <td class="label">${uiLabelMap.RoleTypeWeight}</td>
    <td class="widget-area-style"><select name="roleTypeWeight" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeWeight" size="1" class=""><option value="Y">${uiLabelMap.CommonYes}</option><option selected="selected" value="N">${uiLabelMap.CommonNo}</option></select></td>
</tr>
<tr>
    <td class="label">${uiLabelMap.SelectionSection}</td>
    <td class="widget-area-style"><select name="selectionSection" id="${printBirtFormId?default("ManagementPrintBirtForm")}_selectionSection" size="1" class=""><option selected="selected" value="ALL">${uiLabelMap.All}</option><option value="DETAIL">${uiLabelMap.Detail}</option><option value="SUMMARY">${uiLabelMap.Summary}</option></select></td>
</tr>


<script type="text/javascript">
    StaffEmploymentPrintBirtExtraParameter = {
        load : function() {
            $('button-ok-disabled').hide();
            $('button-ok').show();
        }
    }
    
    StaffEmploymentPrintBirtExtraParameter.load();
</script>