<#-- COLONNA CON INDICATORE -->
<#-- RENDER DEL RIGHE POPOLATE -->
<#if renderNewRow != "Y">
    <#-- RENDER DEL TH - TESTATA -->
    <#if renderThead == "Y">
	    <th class="${glAccountIdTitleAreaClass}"><div>${glAccountIdTitleValue?if_exists}</div></th>
	<#else>
    <#-- RENDER DEL TD - BODY -->
        <#assign openManagementStyle = "">
        <#assign openManagementCss = "">
        <#if detailEnabled != 'NONE'>
            <#assign openManagementCss = "open-management">
            <#assign openManagementStyle = "cursor:pointer;">
        </#if>
        
        <#assign openDetailCss = "">
        <#if showDetail == 'ONE'>
            <#assign openDetailCss = "refresh-form-td">
            <#assign openManagementStyle = "cursor:pointer;">
        </#if>

        <#-- COLONNA CON INDICATORE -->
        <!-- Fix GN-5242 GN-5325: -->
        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
            <td class="${indexClass} ${openManagementCss} ${openDetailCss} widget-area-style" rowspan="${rowspan}" title="${firstTd.glDescriptionLang?if_exists}" style="${openManagementStyle} width: 400px;">
        <#else>
            <td class="${indexClass} ${openManagementCss} ${openDetailCss} widget-area-style" rowspan="${rowspan}" title="${firstTd.glDescription?if_exists}" style="${openManagementStyle} width: 400px;">
        </#if>
        
        <#assign workEffortMeasureProductList = delegator.findByAndCache("WorkEffortMeasureProduct",Static["org.ofbiz.base.util.UtilMisc"].toMap("glAccountId", firstTd.weTransAccountId, "productId", firstTd.transProductId))>
        <#if workEffortMeasureProductList?has_content>
            <#assign workEffortMeasureProduct = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workEffortMeasureProductList) />
            <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                <#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortMeasureProduct.uomDescrLang) />
            <#else>
                <#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortMeasureProduct.uomDescr) />
            </#if>
        <#else>
            <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                <#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(firstTd.weTransAccountDescLang?default("")) />
            <#else>
                <#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(firstTd.weTransAccountDesc) />
            </#if>
        </#if>
        <#if context.showUomDescr != 'Y'>
            <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                <#if firstTd.weTransUomDescLang?has_content>
                    <#assign description = description + " - " + Static["org.ofbiz.base.util.StringUtil"].wrapString(firstTd.weTransUomDescLang?default(""))>
                </#if>                              
            <#else>
                <#if firstTd.weTransUomDesc?has_content>
                    <#assign description = description + " - " + Static["org.ofbiz.base.util.StringUtil"].wrapString(firstTd.weTransUomDesc?default(""))>
                </#if>
            </#if>
        </#if>
        
        <#if context.showAccountCode == 'Y'>
            <#assign glAccountItem = delegator.findOne("GlAccount", Static["org.ofbiz.base.util.UtilMisc"].toMap("glAccountId", firstTd.weTransAccountId), false)>
            <#if glAccountItem?has_content>
            	<#assign glAccountCode = glAccountItem.accountCode?if_exists>
            	<#if glAccountCode?has_content>
            		<#assign description = glAccountCode + " - " + description>
            	</#if>
            </#if>
        </#if>
        
        <input type="hidden" name="entityName_o_${index}" value="WorkEffortMeasure" class="mandatory"/>
        <input type="hidden" name="operation_o_${index}" value="DELETE" class="submit-field"/>
        <input type="hidden" name="entityPkFields_o_${index}" value="workEffortMeasureId" class="ignore_check_modification"/>
        <input type="hidden" value="${context.folderIndex}" name="folderIndex_o_${index}" class="ignore_check_modification"/>
        <input type="hidden" name="backAreaId_o_${index}" class="ignore_check_modification"/>
        <input type="hidden" name="forcedBackAreaId_o_${index}" class="ignore_check_modification"/>
        <input type="hidden" name="successCode_o_${index}" class="ignore_check_modification"/>
        <input type="hidden" name="saveView_o_${index}" value="N" class="ignore_check_modification"/>
        <input type="hidden" name="contextManagement_o_${index}" value="N" class="ignore_check_modification"/>
        <input type="hidden" name="subFolder_o_${index}" value="Y" class="ignore_check_modification"/>
        <input type="hidden" name="_rowSubmit_o_${index}" value="Y" class="submit-field ignore_check_modification"/>
        <input type="hidden" name="insertMode_o_${index}" value="Y" class="ignore_check_modification"/>
        
        <input type="hidden" name="crudService_o_${index}" value="crudServiceDefaultOrchestration_WorkEffortMeasure" class="ignore_check_modification"/>
        <input type="hidden" name="_AUTOMATIC_PK__o_${index}" value="Y" class="ignore_check_modification"/>
        <input type="hidden" name="workEffortMeasureId_o_${index}" value="${firstTd.weTransMeasureId}" class="ignore_check_modification"/>
        <input type="hidden" name="workEffortId_o_${index}" value="${firstTd.weTransWeId}" class="ignore_check_modification"/>
        <input type="hidden" name="glAccountId_o_${index}" value="${firstTd.weTransAccountId}" class="ignore_check_modification"/>
        <input type="hidden" name="defaultOrganizationPartyId_o_${index}" value="${defaultOrganizationPartyId?if_exists}" class="ignore_check_modification"/>
        
        <div>${description}</div>
        </td>
    </#if>
<#else> 
    <#-- RENDER DELLA RIGA VUOTA -->
    <#-- COLONNA CON INDICATORE -->
    <td class="widget-area-style">
    
    <input type="hidden" name="entityName_o_${index}" value="WorkEffortMeasure" class="mandatory">
    <input type="hidden" name="operation_o_${index}" value="CREATE" class="submit-field">
    <input type="hidden" name="entityPkFields_o_${index}" value="workEffortMeasureId" class="ignore_check_modification"/>
    <input type="hidden" value="${context.folderIndex}" name="folderIndex_o_${index}" class="ignore_check_modification"/>
    <input type="hidden" name="backAreaId_o_${index}" class="ignore_check_modification"/>
    <input type="hidden" name="forcedBackAreaId_o_${index}" class="ignore_check_modification"/>
    <input type="hidden" name="successCode_o_${index}" class="ignore_check_modification"/>
    <input type="hidden" name="saveView_o_${index}" value="N" class="ignore_check_modification"/>
    <input type="hidden" name="contextManagement_o_${index}" value="N" class="ignore_check_modification"/>
    <input type="hidden" name="subFolder_o_${index}" value="Y" class="ignore_check_modification"/>
    <input type="hidden" name="_rowSubmit_o_${index}" value="Y" class="submit-field ignore_check_modification"/>
    <input type="hidden" name="insertMode_o_${index}" value="Y" class="ignore_check_modification"/>
    
    <input type="hidden" name="crudService_o_${index}" value="crudServiceDefaultOrchestration_WorkEffortMeasure" class="ignore_check_modification"/>
    <input type="hidden" name="_AUTOMATIC_PK__o_${index}" value="Y" class="ignore_check_modification"/>
    <input type="hidden" name="workEffortMeasureId_o_${index}">
    <input type="hidden" name="workEffortId_o_${index}" value="${parameters.workEffortId}">
    <input type="hidden" name="accountTypeEnumId_o_${index}" value="INDICATOR"/>
    
    <input type="hidden" name="isPosted_o_${index}" value="N" class="ignore_check_modification"/>
    <input type="hidden" name="isReadOnly_o_${index}" value="false" class="ignore_check_modification"/>
    
    <input type="hidden" name="fromDate_o_${index}" value="${workEffortView.estimatedStartDate}" class="mandatory">
    <input type="hidden" name="thruDate_o_${index}" value="${workEffortView.estimatedCompletionDate}" class="mandatory">
    <input type="hidden" name="defaultOrganizationPartyId_o_${index}" value="${defaultOrganizationPartyId?if_exists}" class="ignore_check_modification"/>
    
    <div  class="droplist_field mandatory" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_glAccountId">
        <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
        <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[GlAccountAndMeasureView]"/>
        
        
        <#if (parameters.languageSettinngs.localeSecondarySet)?if_exists?default('N') == 'Y'>
            <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glAccountId, accountCode, accountNameLang, descriptionLang, glAccountTypeDescrLang, respCenterNameLang, weMeasureTypeEnumId, weScoreRangeEnumId, weScoreConvEnumId, weAlertRuleEnumId, uomRangeId, uomRangeDescription, weWithoutPerf, uomDescrLang, uomTypeId, abbreviationLang, inputEnumId, detailEnumId, indProductId, indInternalName, indProductMainCode, mpUomDescrLang]]"/>
            <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[accountCode]]"/>
            <#if context.showAccountCode == 'Y'>
                <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountCode | accountNameLang | mpUomDescrLang]]"/>
            <#else>
                <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountNameLang | mpUomDescrLang]]"/>
            </#if>
            <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="accountNameLang"/>  
        <#else>
            <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[glAccountId, accountCode, accountName, description, glAccountTypeDescr, respCenterName, weMeasureTypeEnumId, weScoreRangeEnumId, weScoreConvEnumId, weAlertRuleEnumId, uomRangeId, uomRangeDescription, weWithoutPerf, uomDescr, uomTypeId, abbreviation, inputEnumId, detailEnumId, indProductId, indInternalName, indProductMainCode, mpUomDescr]]"/>
            <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[accountCode]]"/>
            <#if context.showAccountCode == 'Y'>
                <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountCode | accountName | mpUomDescr]]"/>
            <#else>
                <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[accountName | mpUomDescr]]"/>
            </#if>
            <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="accountName"/>  
        </#if> 
        <#if levelAccountUo?if_exists?default('N') == 'Y'>
            <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeIdInd| equals| ${workEffortView.workEffortTypeId}]! [currentStatusId| equals| GLACC_ACTIVE]! [periodTypeId| equals| ${periodTypeId}]! [contentIdInd| equals| ${contentIdInd}]! [purposeTypeEnumId| equals| PT_INDICATOR]! [organizationPartyId| equals| ${defaultOrganizationPartyId?if_exists}]! [respCenterId| inOrNull| ${orgUnitIdListAccount?if_exists?default('')}]]]"/>
        <#else>
            <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[workEffortTypeIdInd| equals| ${workEffortView.workEffortTypeId}]! [currentStatusId| equals| GLACC_ACTIVE]! [periodTypeId| equals| ${periodTypeId}]! [contentIdInd| equals| ${contentIdInd}]! [purposeTypeEnumId| equals| PT_INDICATOR]! [organizationPartyId| equals| ${defaultOrganizationPartyId?if_exists}]]]"/>
        </#if>
        <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
        <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="glAccountId"/>
        <div class="droplist_container">
        
        <input type="hidden" name="glAccountId_o_${index}" value=""  class="droplist_code_field mandatory"/>
        
        <input type="hidden" id="weMeasureTypeEnumId_o_${index}" name="weMeasureTypeEnumId_o_${index}" class="weMeasureTypeEnumId"/>
        <input type="hidden" id="weScoreRangeEnumId_o_${index}" name="weScoreRangeEnumId_o_${index}" class="weScoreRangeEnumId"/>
        <input type="hidden" id="weScoreConvEnumId_o_${index}" name="weScoreConvEnumId_o_${index}" class="weScoreConvEnumId"/>
        <input type="hidden" id="weAlertRuleEnumId_o_${index}" name="weAlertRuleEnumId_o_${index}" class="weAlertRuleEnumId"/>
        
        <input type="hidden" id="uomRangeId_o_${index}" name="uomRangeId_o_${index}" class="uomRangeId"/>
        
        <input type="hidden" id="weWithoutPerf_o_${index}" name="weWithoutPerf_o_${index}" class="weWithoutPerf"/>
        
        <input type="hidden" id="uomTypeId_o_${index}" name="uomTypeId_o_${index}" class="uomTypeId"/>
        
        <input type="hidden" id="abbreviation_o_${index}" name="abbreviation_o_${index}" class="abbreviation"/>
        <input type="hidden" id="abbreviationLang_o_${index}" name="abbreviationLang_o_${index}" class="abbreviationLang"/>
        
        <input type="hidden" id="inputEnumId_o_${index}" name="inputEnumId_o_${index}" class="inputEnumId"/>
        <input type="hidden" id="detailEnumId_o_${index}" name="detailEnumId_o_${index}" class="detailEnumId"/>
        <input type="hidden" id="weOtherGoalEnumId_o_${index}" name="weOtherGoalEnumId_o_${index}"/>
        
        <input type="hidden" id="productId_o_${index}" name="productId_o_${index}" class="indProductId"/>
        
        <input type="text" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_glAccountId_edit_field" name="accountName_glAccountId" size="100" maxlength="255" value=""  class="droplist_edit_field mandatory"/>
        <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div>
    </div></td>
</#if>