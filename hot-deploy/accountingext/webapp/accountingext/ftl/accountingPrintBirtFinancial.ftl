
<tr>
	<input name="accountTypeEnumId" id="${printBirtFormId?default("ManagementPrintBirtForm")}_accountTypeEnumId" value="${parameters.accountTypeEnumId}" type="hidden">
</tr>

<#-- tipologia -->
<tr>
    <td class="label">${uiLabelMap.GlAccount_glAccountTypeId}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlAccountType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, glAccountTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glAccountTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[accountTypeEnumId| equals| ${parameters.accountTypeEnumId}]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountTypeId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container">
    <input type="hidden" name="glAccountTypeId" value=""  class="droplist_code_field"/><input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountTypeId_edit_field" name="description_glAccountTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>

<#-- unita da -->
<tr>
    <td class="label">${uiLabelMap.GlAccountFolderFT}</td>
    <td class="widget-area-style">
	<div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountId">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
	<input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://accountingext/widget/screens/LookupScreens.xml"/>
	<input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/>
	<input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlAccount]"/>
	<input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glAccountId, accountName, glAccountId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glAccountId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountName]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[accountTypeEnumId| equals| ${parameters.accountTypeEnumId}]]]"/>
	<#if parameters.snapshot?if_exists?default("N") == 'Y'> 
		<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[glAccountTypeId| equals| field:glAccountTypeId]! [workEffortSnapshotId| equals| null]! [snapshot| equals| Y]]]"/>
	<#else>
	 	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[glAccountTypeId| equals| field:glAccountTypeId]! [workEffortSnapshotId| equals| null]]]"/>
	</#if>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountId"/>
	<div class="lookup_container">
	<input type="text" class="lookup_field_code lookup_field_accountCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountId_edit_field" name="accountCode_glAccountId" value="" size="12"/>
	<span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/>
	<input type="text" class="lookup_field_description lookup_field_accountName" name="accountName_glAccountId" readonly="readonly"/>
	<input type="hidden" class="lookup_field_description glAccountId" name="glAccountId" id="glAccountId" />
	</div></div></td>
</tr>

<#-- unita a -->
<tr>
    <td class="label">${uiLabelMap.GlAccountFolderTF}</td>
    <td class="widget-area-style">
	<div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountIdTo">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
	<input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://accountingext/widget/screens/LookupScreens.xml"/>
	<input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/>
	<input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlAccount]"/>
	<input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glAccountId, accountName, glAccountId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glAccountId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountName]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[accountTypeEnumId| equals| ${parameters.accountTypeEnumId}]]]"/>
	<#if parameters.snapshot?if_exists?default("N") == 'Y'> 
		<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[glAccountTypeId| equals| field:glAccountTypeId]! [workEffortSnapshotId| equals| null]! [snapshot| equals| Y] ]]"/>
	<#else>
		<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[glAccountTypeId| equals| field:glAccountTypeId]! [workEffortSnapshotId| equals| null] ]]"/>
	</#if>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountId"/>
	<div class="lookup_container">
	<input type="text" class="lookup_field_code lookup_field_accountCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountIdTo_edit_field" name="accountCode_glAccountIdTo" value="" size="12"/>
	<span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/>
	<input type="text" class="lookup_field_description lookup_field_accountName" name="accountName_glAccountIdTo" readonly="readonly"/>
	<input type="hidden" class="lookup_field_description glAccountId" name="glAccountIdTo" id="glAccountIdTo" />
	</div></div></td>
</tr>


<#-- codice -->
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_accountCode}</td>
    <td colspan="10">
    <div class="text-find" id="${printBirtFormId?default("ManagementPrintBirtForm")}_accountCode">
    <div class="droplist_container"> 
    	<select name="accountCode_op" class="selectBox filter-field-selection">
    		<option value="between">${uiLabelMap.between}</option>
    		<option value="notBetween">${uiLabelMap.not_between}</option>
    		<option value="like">${uiLabelMap.like}</option>
    		<option value="equals">${uiLabelMap.equal}</option>
    		<option value="notEqual">${uiLabelMap.not_equal}</option>
    		<option value="empty">${uiLabelMap.is_empty}</option>
    		<option value="notEmpty">${uiLabelMap.is_not_empty}</option>
    		<option value="contains" selected="selected">${uiLabelMap.contains}</option>
    	</select>
    	<input size="30" autocomplete="off" class="text-find-element" name="accountCode" id="GlAccountTypeFinancialViewSearchForm_accountCode" type="text">
    	</div></div>
</tr>

<#-- titolo -->
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_accountName}</td>
    <td colspan="10">
    <div class="text-find" id="${printBirtFormId?default("ManagementPrintBirtForm")}_accountName">
    <div class="droplist_container"> 
        <select name="accountName_op" class="selectBox filter-field-selection">
            <option value="between">${uiLabelMap.between}</option>
    		<option value="notBetween">${uiLabelMap.not_between}</option>
    		<option value="like">${uiLabelMap.like}</option>
    		<option value="equals">${uiLabelMap.equal}</option>
    		<option value="notEqual">${uiLabelMap.not_equal}</option>
    		<option value="empty">${uiLabelMap.is_empty}</option>
    		<option value="notEmpty">${uiLabelMap.is_not_empty}</option>
    		<option value="contains" selected="selected">${uiLabelMap.contains}</option>
    	</select>
    	<input size="30" autocomplete="off" class="text-find-element" name="accountName" id="GlAccountTypeFinancialViewSearchForm_accountName" type="text">
    	</div></div>
</tr>

<#-- natura -->
<tr> 
    <td class="label">${uiLabelMap.FormFieldTitle_glResourceTypeId}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glResourceTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlResourceType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, glResourceTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glResourceTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glResourceTypeId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
    <input type="hidden" name="glResourceTypeId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_glResourceTypeId_edit_field" name="description_glResourceTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- stato -->
<tr>
    <td class="label">${uiLabelMap.GlAccount_currentStatusId}</td>	   
	<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_statusId">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[StatusItem]"/>
	<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, statusId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[statusId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[statusTypeId| equals | GL_ACCOUNT]]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="statusId"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
	<input type="hidden" name="statusId" value=""  class="droplist_code_field"/>
	<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_statusId_edit_field" name="description_statusId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
	<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
	</div></div></td>
</tr>

<#-- e modello --> 
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_inputEnumId2}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_enumId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[Enumeration]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, enumId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[enumId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[enumTypeId| equals| GLACCINPUT]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="enumId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container"><input type="hidden" name="enumId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_enumId_edit_field" name="description_enumId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- unita di misura --> 
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_defaultUomId}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_uomId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[Uom]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, uomId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[uomId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="uomId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container"><input type="hidden" name="uomId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_uomId_edit_field" name="description_uomId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- con dettagli -->
<tr>
    <td class="label">${uiLabelMap.GlAccountDetail}</td>
  	<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_detailEnumId">
  	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
  	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[Enumeration]"/>
  	<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
  	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, enumId]]"/>
  	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[enumId]]"/>
  	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
  	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[enumTypeId| equals| GLACCDET]]]"/>
  	<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
  	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="enumId"/>
  	<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
  	<input type="hidden" name="detailEnumId" value=""  class="droplist_code_field"/>
  	<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_detailEnumId_edit_field" name="description_enumId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
  	<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
  	</div></div></td>
</tr>

<#-- finalita --> 
<tr>
    <td class="label">${uiLabelMap.WorkEffortPurposeAccount}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortPurposeTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortPurposeType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, workEffortPurposeTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[workEffortPurposeTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="workEffortPurposeTypeId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container"><input type="hidden" name="workEffortPurposeTypeId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortPurposeTypeId_edit_field" name="description_workEffortPurposeTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- commnento finalita -->
<tr>
    <td class="label">${uiLabelMap.WorkEffortPurposeAccountComments}</td>
    <td colspan="10">
    <div class="text-find" id="${printBirtFormId?default("ManagementPrintBirtForm")}_workEffortPurposeTypeCode">
    	<input size="50" autocomplete="off" class="text-find-element" name="workEffortPurposeTypeCode"  type="text">
  	</div>
  	</td>
</tr>

<#-- ruolo responsabile -->
<tr> 
    <td class="label">${uiLabelMap.GlAccountRespRole}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[RoleType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, roleTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[roleTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentTypeId| equals| ORGANIZATION_UNIT]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="roleTypeId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container"><input type="hidden" name="roleTypeId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId_edit_field" name="description_roleTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- responsabile -->
<tr>
    <td class="label">${uiLabelMap.GlAccountResp}</td>
    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
    <input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://workeffortext/widget/screens/LookupScreens.xml"/>
    <input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/>
    <input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/>
    <input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookupPartyRoleOrgUnitView</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[roleTypeId| equals| field:roleTypeId]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/><div class="lookup_container">
    <input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_field" name="parentRoleCode_partyId" value="" size="12"/>
    <span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/>
    <input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_partyId" readonly="readonly"/>
    <input type="hidden" class="lookup_field_description partyId" name="partyId" id="partyId" />
    </div></div></td>
</tr>

<#-- periodicità -->
<tr>
    <td class="label">${uiLabelMap.GlAccountPeriod}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_periodTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PeriodType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, periodTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[periodTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="periodTypeId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container"><input type="hidden" name="periodTypeId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_periodTypeId_edit_field" name="description_periodTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- calcolo -->
<tr> 
    <td class="label">${uiLabelMap.FormFieldTitle_calcCustomMethodId}</td>
<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_calcCustomMethodId">
<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[CustomMethod]"/>
<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[customMethodId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[customMethodId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[customMethodId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[customMethodTypeId| equals| GL_ACC]]]"/>
<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="customMethodId"/>
<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="customMethodId"/>
<div class="droplist_container"><input type="hidden" name="calcCustomMethodId" value=""  class="droplist_code_field"/>
<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_calcCustomMethodId_edit_field" name="customMethodId_calcCustomMethodId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
</div></div></td>
</tr>

<#-- priorità calcolo -->
<tr> 
    <td class="label">${uiLabelMap.FormFieldTitle_prioCalc}</td>
    <td class="widget-area-style"><input id="${printBirtFormId?default("ManagementPrintBirtForm")}_prioCalc" class="numericInSingle" type="text" maxlength="20" size="20" value="" name="prioCalc" decimal_digits="0">
</td>
</tr>

<#-- movimenti inclusi -->
<tr> 
    <td class="label">${uiLabelMap.FormFieldTitle_movimentiInclusi}</td>
    <td colspan="10">
    <div class="text-find" id="${printBirtFormId?default("ManagementPrintBirtForm")}_movIncluded">
    <div class="droplist_container"> 
    	<select name="movIncluded" class="selectBox filter-field-selection">
    		<option value="Y" selected="selected">${uiLabelMap.CommonYes}</option>
    		<option value="N">${uiLabelMap.CommonNo}</option>
    	</select>
   	</div></div>
</tr>

<#-- movimenti da data/a data -->
<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_movimentiFromData}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("fromDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="fromDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames" value=""/></div></td>
</tr>
<tr>
   <td class="label">${uiLabelMap.FormFieldTitle_movimentiToData}</td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("thruDate_datePanel")>
   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}"><input type="hidden" class="dateParams" name="paramName" value="thruDate"/><input type="hidden" class="dateParams" name="time" value="false"/><input type="hidden" class="dateParams" name="shortDateInput" value="true"/><input type="hidden" class="dateParams" name="dateTimeValue" value=""/><input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/><input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/><input type="hidden" class="dateParams" name="yearRange" value=""/><input type="hidden" class="dateParams" name="localizedValue" value=""/><input type="hidden" class="dateParams" name="size" value="10"/><input type="hidden" class="dateParams" name="maxlength" value="10"/><input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/><input type="hidden" class="dateParams" name="classNames" value=""/></div></td>
</tr>

<#-- tipo valore movimenti -->
<tr> 
    <td class="label">${uiLabelMap.FormFieldTitle_glFiscalTypeId}</td>
<td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glFiscalTypeId">
<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlFiscalType]"/>
<input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glFiscalTypeId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glFiscalTypeId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[glFiscalTypeId]]"/>
<input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glFiscalTypeId"/>
<input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="glFiscalTypeId"/>
<div class="droplist_container"><input type="hidden" name="glFiscalTypeId" value=""  class="droplist_code_field"/>
<input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_glFiscalTypeId_edit_field" name="glFiscalType_glFiscalTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
<span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
</div></div></td>
</tr>

<#-- Prodotto -->
<tr>
    <td class="label">${uiLabelMap.FormFieldTitle_productId}</td>
	    <td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_productId">
	    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	    <input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
	    <input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://productext/widget/screens/LookupScreens.xml"/>
	    <input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/>
	    <input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
	    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[Product]"/>
	    <input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/>
	    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[productId, description, internalName]]"/>
	    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[productId]]"/>
	    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
	    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="productId"/>
	    <div class="lookup_container">
	    <input type="text" class="lookup_field_code lookup_field_productId"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_productId_edit_field" name="productId" value="" size="12"/>
	    <span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/>
	    <input type="text" class="lookup_field_description lookup_field_internalName" name="description_productId" readonly="readonly"/>
	    <input type="hidden" class="lookup_field_description productId" name="productCode" id="productCode" />
	    </div></div></td>
</tr>

<#-- Tipo Statistico -->
<tr>
    <td class="label">${uiLabelMap.GlAccountStatType}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountGroupTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlAccountGroupType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, glAccountGroupTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glAccountGroupTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[accountTypeEnumId| equals| ${parameters.accountTypeEnumId}]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/><input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountGroupTypeId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/><div class="droplist_container">
    <input type="hidden" name="glAccountGroupTypeId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountGroupTypeId_edit_field" name="description_glAccountGroupTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- Statistico -->
<tr>
    <td class="label">${uiLabelMap.GlAccountGroupType}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountGroupId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlAccountGroup]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, glAccountGroupId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[glAccountGroupId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[glAccountGroupId| equals| glAccountGroupTypeId]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountGroupId"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container"><input type="hidden" name="glAccountGroupId" value=""  class="droplist_code_field"/>
    <input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_glAccountGroupId_edit_field" name="description_glAccountGroupId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td>
</tr>

<#-- Tipo Destinazione -->
<tr> 
    <td class="label">${uiLabelMap.GlAccountDestType}</td>
    <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_entryRoleTypeId">
    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[RoleType]"/>
    <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, roleTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[roleTypeId]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[parentTypeId| like| GOAL%]]]"/>
    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="roleTypeId"/><input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
    <div class="droplist_container">
    <input type="hidden" name="entryRoleTypeId" value=""  class="droplist_code_field"/><input type="text"id="${printBirtFormId?default("ManagementPrintBirtForm")}_entryRoleTypeId_edit_field" name="description_roleTypeId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
    </div></div></td> 

</tr>

<#-- Destinazione -->
<tr> 
    <td class="label">${uiLabelMap.GlAccountDest}</td>
	<td class="widget-area-style"><div  class="lookup_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_entryPartyId">
	<input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
	<input  class="autocompleter_option" type="hidden" name="lookupAutocomplete" value="Y"/>
	<input  class="lookup_parameter" type="hidden" name="lookupScreenLocation" value="component://partyext/widget/screens/LookupScreens.xml"/>
	<input  class="lookup_parameter" type="hidden" name="noConditionFind" value="N"/>
	<input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/>
	<input  class="autocompleter_parameter" type="hidden" name="lookupTarget" value="<@ofbizUrl>lookup</@ofbizUrl>"/>
	<input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyId]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[roleTypeId| equals| field:entryRoleTypeId]]]"/>
	<input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="parentRoleCode"/>
	<div class="lookup_container">
	<input type="text" class="lookup_field_code lookup_field_parentRoleCode"  id="${printBirtFormId?default("ManagementPrintBirtForm")}_entryPartyId_edit_field" name="parentRoleCode_partyId" value="" size="12"/>
	<span class="lookup-anchor"><a style="cursor: pointer;" class="lookup_field_submit fa fa-2x"></a></span><br class="clear"/>
	<input type="text" class="lookup_field_description lookup_field_partyName" name="partyName_partyId" readonly="readonly"/>
	<input type="hidden" class="lookup_field_description partyId" name="entryPartyId" id="entryPartyId" />
	</div></div></td>
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
</tr>
</#if>

<script>
    AccountingExtFinancialPrintBirt = {
        load : function() {
            $('button-ok-disabled').hide();
            $('button-ok').show();
        }
    }
    
    AccountingExtFinancialPrintBirt.load();
</script>