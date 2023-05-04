<#if renderNewRow != "Y">
    <#if renderThead == "Y">
	    <th class="${accountReferenceTitleAreaClass}"><div>${uiLabelMap.WorkEffortTransaction_Riferimento?if_exists}</div></th>
	<#else>
	    <td rowspan="${rowspan}">&nbsp;</td>
	</#if>
<#else>
    <td>
        <input name="glAccountIdRef_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_glAccountIdRef_o_${index}" type="hidden" class="glAccountIdRef">
        <div  class="droplist_field" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_accountReference">
            <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
            <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[AccountReferenceView]"/>
         
            <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
                <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glAccountId, glAccountIdRef, accountNameLang]]"/>
                <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[accountNameLang]]"/>
                <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountNameLang]]"/> 
                <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="accountNameLang"/>
            <#else>
                <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glAccountId, glAccountIdRef, accountName]]"/>
                <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[accountName]]"/> 
                <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountName]]"/>
                <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="accountName"/>
            </#if> 
            <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortId| equals| ${parameters.workEffortId?if_exists}]! [contentId| equals| ${parameters.contentIdInd?if_exists}]<#if context.showAccountReference != "Y">! [workEffortPurposeTypeId| inOrNull| ${showAccountReference?if_exists?default('')}]</#if>! [fromDate| less-equals| ${workEffortView.estimatedCompletionDate}]! [thruDate| greater-equals| ${workEffortView.estimatedStartDate}]]]"/>
            <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
            <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountId"/> 
            <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[Y]"/> 
            <div class="droplist_container">
                <input type="hidden" name="accountReference_o_${index}" value=""  class="droplist_code_field"/>
                <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
                    <input type="text" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_accountReference_edit_field" name="accountNameLang_accountReference" size="100" maxlength="255" value=""  class="droplist_edit_field"/>
                <#else>
                    <input type="text" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_accountReference_edit_field" name="accountName_accountReference" size="100" maxlength="255" value=""  class="droplist_edit_field"/>
                </#if>
                <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span>
            </div>                 
        </div>
    </td>
</#if>