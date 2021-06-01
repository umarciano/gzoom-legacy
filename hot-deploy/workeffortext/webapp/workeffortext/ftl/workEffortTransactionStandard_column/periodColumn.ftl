<#-- COLONNA O COLONNE PERIODI -->
<#-- RENDER DEL TH - TESTATA -->
<#if renderThead == "Y">
    <#if context.showPeriods == "NONE">
        <th class="master-th"><div>${uiLabelMap.FormFieldTitle_weTransValue}</div></th>
        <#assign colspan = colspan + 1/>
    <#elseif context.showPeriods == "OPEN">
        <th class="master-th"><div>
        <#assign styleForAlign = "" />
        
        <#if context.periodScrolling == "Y" && customTimePeriodId != firstCustomTimePeriodId>
            <#assign styleForAlign = "position: relative; bottom: 4px;" />
            <a class="scroll-left fa fa-2x" onclick="javascript: WorkEffortTransactionStandard.refreshForm(null, false);"></a> 
        </#if>
        <#if context.periodScrolling == "Y" && customTimePeriodId != lastCustomTimePeriodId>
            <#assign styleForAlign = "position: relative; bottom: 4px;" />
        </#if>
        <span style="${styleForAlign}">${customTimePeriodCode?if_exists}</span>
        <#if context.periodScrolling == "Y" && customTimePeriodId != lastCustomTimePeriodId>
            <a class="scroll-right fa fa-2x" onclick="javascript: WorkEffortTransactionStandard.refreshForm(null, true);"></a> 
        </#if>
        </div></th>
        <#assign colspan = colspan + 1/>
    <#else>
        <#assign size = customTimePeriodList?size />
        <#assign colspan = colspan + size/>
        <#list customTimePeriodList?sort_by("fromDate") as customTimePeriod>
            <#assign styleForAlign = "" />
            
            <#if customTimePeriod_index == (size - 1)>
                <th class="master-th"><div>
            <#else>
                <th><div>
            </#if>
                <#if customTimePeriod_index == 0 && customTimePeriod.customTimePeriodId != firstCustomTimePeriodId>
                    <#if context.periodScrolling == "Y">
                        <#assign styleForAlign = "position: relative; bottom: 4px;" />
                        <a class="scroll-left fa fa-2x" onclick="javascript: WorkEffortTransactionStandard.refreshForm(null, false);"></a>
                    </#if>
                </#if>
                <#if customTimePeriod_index == (size - 1) && customTimePeriod.customTimePeriodId != lastCustomTimePeriodId>
                    <#if context.periodScrolling == "Y">
                        <#assign styleForAlign = "position: relative; bottom: 4px;" />
                    </#if>
                </#if>
                <span style="${styleForAlign}">${customTimePeriod.customTimePeriodCode?if_exists}</span>
                <#if customTimePeriod_index == (size - 1) && customTimePeriod.customTimePeriodId != lastCustomTimePeriodId>
                    <#if context.periodScrolling == "Y">
                        <a class="scroll-right fa fa-2x" onclick="javascript: WorkEffortTransactionStandard.refreshForm(null, true);"></a>
                    </#if>
                </#if>
            </div></th>
        </#list>
    </#if>
<#else>
    <#-- RENDER DEL TD - BODY -->
    <#if context.showPeriods != "NONE" || (context.showPeriods == "NONE" && (workEffortTransactionIndicatorView.weTransDate?has_content || (!periodNonehasTrans && workEffortTransactionIndicatorView_index == (rowListSize - 1))))>
        <#assign periodNonehasTrans = true>
    
        <#-- la riga e' readOnly per diversi motivi: -->
        <#assign valModId = workEffortTransactionIndicatorView.glValModId?default("") >
        <#if (workEffortTransactionIndicatorView.inputEnumId?if_exists == "ACCINP_PRD") >
            <#assign valModId = workEffortTransactionIndicatorView.wmValModId?default("") >
        </#if> 
        <#assign workEffortTypePeriodList = delegator.findByAnd("WorkEffortTypePeriod",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortTypeId", workEffortView.workEffortTypeRootId, "customTimePeriodId", workEffortTransactionIndicatorView.customTimePeriodId?if_exists, "glFiscalTypeEnumId", workEffortTransactionIndicatorView.glFiscalTypeEnumId, "organizationId", defaultOrganizationPartyId?if_exists))>
        <#assign workEffortTypePeriod = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workEffortTypePeriodList?default(null))?default(null)>
        <#assign isRil = workEffortTypePeriod?if_exists?has_content && prilStatusSet.contains(workEffortTypePeriod.statusEnumId?default(true))>
        
        <#assign hasMandatoryBudgetEmpty = (parameters.onlyWithBudget == "Y" && !workEffortTransactionIndicatorView.hasMandatoryBudgetEmpty?if_exists && workEffortTransactionIndicatorView.weTransTypeValueId?if_exists == "ACTUAL") />
        <#assign isReadOnlyRow = false />       
    
        <#if "Y" == parameters.rootInqyTree?if_exists?default('N') || "Y" == workEffortTransactionIndicatorView.isPosted?if_exists >
            <#assign isReadOnlyRow = true />
        <#elseif  !checkWorkEffortPermissions >
            <#assign isReadOnlyRow = true />
        <#else>
            <#if  security.hasPermission(adminPermission, context.userLogin) >
                <#assign isReadOnlyRow = false />
            <#else>
                <#assign isReadOnlyRow = 
                    (hasMandatoryBudgetEmpty
                    || !isRil
                    || crudEnumIdSecondary?if_exists == "NONE"
                    || valModId == "ALL_NOT_MOD"
                    || (valModId == "ACTUAL_NOT_MOD" && workEffortTransactionIndicatorView.weTransTypeValueId?if_exists == "ACTUAL")
                    || (valModId == "BUDGET_NOT_MOD" && workEffortTransactionIndicatorView.weTransTypeValueId?if_exists == "BUDGET")
                    || workEffortTransactionIndicatorView.isReadOnly?if_exists
                    || "Y" == parameters.rootInqyTree?if_exists?default('N')) 
                   />
            </#if> 
        </#if> 
        
        <td rowspan="${rowspanPeriod}" <#if isReadOnlyRow >readonly="readonly"</#if> 
            <#assign openPortletStyle = "">
            <#if showValuesPanel == 'Y'>
                <#assign openPortletStyle = "open-portlet">
            </#if>
            <#if periodIndex%rowspanPeriod == 0 || context.showPeriods == "NONE" || context.showPeriods == "OPEN">
            	style="cursor:pointer;"
                class="${openPortletStyle} display-inline-block slave-th slave-td widget-area-style">
            <#else>
            	style="cursor:pointer;"
                class="${openPortletStyle} master-td widget-area-style">
            </#if>
            <#assign renderOtherTd = false>
            <#assign renderDetailTd = false>
            <#if workEffortTransactionIndicatorView.hasComments?if_exists>
            	<span class="fa transactionWithNote" style="float: left;"></span>
            <#else>
                <#if showValuesPanel == 'Y'>
                    <span class="far fa-sticky-note" style="float: left;"></span>
                </#if>
            </#if>
            
            <input type="hidden" value="${isReadOnlyRow?string}" name="isReadOnlyRow_o_${index}" class="ignore_check_modification"/>
            
            <input type="hidden" value="${workEffortTransactionIndicatorView.fromDate?if_exists}" name="workEffortTransactionIndicatorViewfromDate_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.thruDate?if_exists}" name="workEffortTransactionIndicatorViewthruDate_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.customTimePeriodFromDate?if_exists}" name="workEffortTransactionIndicatorViewcustomTimePeriodFromDate_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.customTimePeriodThruDate?if_exists}" name="workEffortTransactionIndicatorViewcustomTimePeriodThruDate_o_${index}" class="ignore_check_modification"/>
            
            <input type="hidden" value="${hasMandatoryBudgetEmpty?string}" name="hasMandatoryBudgetEmpty_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${isRil?string}" name="isRil_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.customTimePeriodId?if_exists}" name="workEffortTransactionIndicatorViewcustomTimePeriodId_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTypePeriod?if_exists}" name="workEffortTypePeriod_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${crudEnumIdSecondary?if_exists}" name="crudEnumIdSecondary_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${valModId?if_exists}" name="valModId_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.isReadOnly?if_exists?string}" name="workEffortTransactionIndicatorViewisReadOnly_o_${index}" class="ignore_check_modification"/>
            
            <input type="hidden" <#if isReadOnlyRow >value="Y"<#else>value="N"</#if> name="isReadOnly_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.weTransMeasureId?if_exists}" name="workEffortMeasureId_o_${index}" class="ignore_check_modification"/>
                
            <input type="hidden" value="${context.folderIndex}" name="folderIndex_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${context.contentIdInd}" name="contentIdInd_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" name="backAreaId_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" name="forcedBackAreaId_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" name="successCode_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="WorkEffortTransactionIndicatorView" name="operationalEntityName_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="N" name="saveView_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="N" name="contextManagement_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" name="subFolder_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="Y" name="_rowSubmit_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="N" name="insertMode_o_${index}" class="ignore_check_modification"/>
            
            <input type="hidden" value="WorkEffortTransactionIndicatorView" name="entityName_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="UPDATE" name="operation_o_${index}" class="ignore_check_modification"/>
            <input type="hidden" value="${workEffortTransactionIndicatorView.inputEnumId?if_exists}" name="inputEnumId_o_${index}" class="ignore_check_modification"/>
            
            <#if !workEffortTransactionIndicatorView.partyId?if_exists?has_content && workEffortTransactionIndicatorView.detectOrgUnitIdFlag?if_exists?has_content && "Y" == workEffortTransactionIndicatorView.detectOrgUnitIdFlag>
                <input type="hidden" value="${workEffortTransactionIndicatorView.orgUnitId?if_exists}" name="partyId_o_${index}" class="ignore_check_modification">
                <input type="hidden" value="${workEffortTransactionIndicatorView.orgUnitRoleTypeId?if_exists}" name="roleTypeId_o_${index}" class="ignore_check_modification">
            <#else>
                <input type="hidden" value="${workEffortTransactionIndicatorView.partyId?if_exists}" name="partyId_o_${index}" class="ignore_check_modification">
                <input type="hidden" value="${workEffortTransactionIndicatorView.roleTypeId?if_exists}" name="roleTypeId_o_${index}" class="ignore_check_modification">
             </#if>
            
            <input type="hidden" value="${workEffortTransactionIndicatorView.entryPartyId?if_exists}" name="entryPartyId_o_${index}" class="ignore_check_modification">
            <input type="hidden" value="${workEffortTransactionIndicatorView.entryRoleTypeId?if_exists}" name="entryRoleTypeId_o_${index}" class="ignore_check_modification">
            
            <#assign workEffort = delegator.findOne("WorkEffort", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", workEffortTransactionIndicatorView.weTransWeId?if_exists), false)>
            <input type="hidden" value="${workEffort.workEffortTypeId}" name="workEffortTypeId_o_${index}" class="ignore_check_modification">
            <input type="hidden" value="FOLDER" name="weTypeContentTypeId_o_${index}" class="ignore_check_modification"/>
            
            <input type="hidden" value="${workEffortView.workEffortTypeRootId}" name="parentWorkEffortTypeId_o_${index}" class="ignore_check_modification">
            <input type="hidden" value="${workEffortTransactionIndicatorView.glFiscalTypeEnumId?if_exists}" name="glFiscalTypeEnumId_o_${index}" class="ignore_check_modification">
            
            
            <input type="hidden" name="weTransId_o_${index}" value="${workEffortTransactionIndicatorView.weTransId?if_exists}" class="ignore_check_modification"/>
            <input type="hidden" name="weTransEntryId_o_${index}" value="${workEffortTransactionIndicatorView.weTransEntryId?if_exists}" class="ignore_check_modification"/>
            
            <input type="hidden" name="weTransAccountId_o_${index}" value="${workEffortTransactionIndicatorView.weTransAccountId}" class="ignore_check_modification">
            <input type="hidden" name="weTransTypeValueId_o_${index}" value="${workEffortTransactionIndicatorView.weTransTypeValueId?if_exists}" class="ignore_check_modification">
            <input type="hidden" name="weTransDate_o_${index}" value="${workEffortTransactionIndicatorView.weTransDate?if_exists}" class="ignore_check_modification">
            <input type="hidden" name="customTimePeriodId_o_${index}" value="${workEffortTransactionIndicatorView.customTimePeriodId?if_exists}" class="ignore_check_modification">
            <input type="hidden" name="weTransCurrencyUomId_o_${index}" value="${workEffortTransactionIndicatorView.weTransCurrencyUomId?if_exists}" class="ignore_check_modification">
            <input type="hidden" name="weTransWeId_o_${index}" value="${workEffortTransactionIndicatorView.weTransWeId?if_exists}" class="ignore_check_modification">
            <input type="hidden" name="weTransMeasureId_o_${index}" value="${workEffortTransactionIndicatorView.weTransMeasureId}" class="ignore_check_modification">
            <input type="hidden" name="weTransUomType_o_${index}" value="${workEffortTransactionIndicatorView.weTransUomType?if_exists}" class="ignore_check_modification">
            <input type="hidden" name="valModId_o_${index}" value="${valModId}"  class="ignore_check_modification">
            <input type="hidden" name="defaultOrganizationPartyId_o_${index}" value="${defaultOrganizationPartyId?if_exists}"/>
            <input type="hidden" name="localeSecondarySet_o_${index}" value="${localeSecondarySet?if_exists}"/>
            
            <input type="hidden" value="crudServiceDefaultOrchestration_WorkEffortTransactionView_Simplified" name="crudService_o_${index}" class="ignore_check_modification">
            
            <#if context.elabScoreIndic?if_exists?default('N') != 'N'>
	        <input type="hidden" name="elabScoreIndic_o_${index}" value="${context.elabScoreIndic?if_exists}" class="submit-field"/>
	        <input type="hidden" name="crudServiceEpilog_o_${index}" value="crudServiceEpilog_elaboreteScoreIndic" class="submit-field"/>
	        <input type="hidden" name="openNewTransaction_o_${index}" value="N" class="submit-field"/>
	        <input type="hidden" name="searchDate_o_${index}" value="${context.searchDateCalculate?if_exists}" class="submit-field"/>     
	        </#if>
        
            <#if workEffortTransactionIndicatorView.weTransUomType?if_exists == 'RATING_SCALE' >
            <#assign uomCode = "" />
            <#assign uomRatingScaleList = delegator.findByAndCache("WorkEffortMeasRatSc",Static["org.ofbiz.base.util.UtilMisc"].toMap("uomId", workEffortTransactionIndicatorView.weTransCurrencyUomId, "workEffortMeasureId", workEffortTransactionIndicatorView.weTransMeasureId))>
            <#list uomRatingScaleList as uomRatingScale>
                <#if workEffortTransactionIndicatorView.weTransValue?if_exists?has_content && Static["java.lang.Double"].toString(workEffortTransactionIndicatorView.weTransValue) == Static["java.lang.Double"].toString(uomRatingScale.uomRatingValue) >
                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                        <#assign uomCode = uomRatingScale.uomCodeLang />
                    <#else>
                        <#assign uomCode = uomRatingScale.uomCode />
                    </#if>
                </#if>
            </#list>                                                            
            <div style="float: right; width: 90%" class="droplist_field" id="WorkEffortTransactionViewManagementMultiForm_weTransValue_o_${index}">
               <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
               <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[WorkEffortMeasRatSc]"/><input  class="autocompleter_parameter" type="hidden" name="distincts" value="[N]"/>
               <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="${droplistSelectFields?if_exists}"/>
               <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[uomRatingValue]]"/>
               <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="${droplistDisplayFields?if_exists}"/>
               <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[uomId| equals| ${workEffortTransactionIndicatorView.weTransCurrencyUomId?if_exists}]! [workEffortMeasureId| equals| ${workEffortTransactionIndicatorView.weTransMeasureId}]]]"/>
               <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
               <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="uomRatingValue"/>
               <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="${droplistEntityDescriptionField?if_exists}"/>
               <div class="droplist_container">
                    <input type="hidden" name="weTransValue_o_${index}" value="${workEffortTransactionIndicatorView.weTransValue?if_exists}" 
                        <#if isReadOnlyRow > 
                            class="droplist_code_field ignore_check_modification"
                        <#else>
                            class="droplist_code_field"
                        </#if>
                    />
                    <div class="droplist_input_field" style="width: 90% !important;">
                        <input style="cursor:pointer; width: 100% !important;" 
                            <#if isReadOnlyRow > 
                                readonly="readonly"
                                class="droplist_edit_field ignore_check_modification"
                            <#else>         
                                class="droplist_edit_field"
                            </#if> 
                            type="text" id="WorkEffortTransactionViewManagementMultiForm_weTransValue_o_${index}_edit_field" <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'> name="uomCodeLang_uomRatingValue_o_${index}"<#else>name="uomCode_uomRatingValue_o_${index}"</#if> value="${uomCode}"/>
                    </div>
                    <div class="droplist_icon">
                        <span class="droplist-anchor" style="float: right;"><a class="droplist_submit_field fa" style="font-size: 1.5em;" href="#"></a></span>
                    </div>
                </div>
            </div>
            <#elseif workEffortTransactionIndicatorView.weTransUomType?if_exists == 'DATE_MEASURE'>
                    <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("weTransValue_o_${index}_datePanel")>
                    <div class="datePanel" id="${id}">
                    <input type="hidden" class="dateParams" name="paramName" value="weTransValue"/>
                    <input type="hidden" class="dateParams" name="time" value="false"/>
                    <input type="hidden" class="dateParams" name="shortDateInput" value="true"/>
                    <input type="hidden" class="dateParams" name="dateTimeValue" value=""/>
                    <input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/>
                    <input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/>
                    <input type="hidden" class="dateParams" name="yearRange" value=""/>
                    <input type="hidden" class="dateParams" name="localizedValue" value="${workEffortTransactionIndicatorView.weTransValue?if_exists}"/>
                    <input type="hidden" class="dateParams" name="size" value="10"/>
                    <input type="hidden" class="dateParams" name="maxlength" value="10"/>
                    <input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/>
                    <input type="hidden" class="dateParams" name="classNames" 
                        
                    <#if isReadOnlyRow >
                        value="readonly ignore_check_modification"
                    <#else> 
                        value="" 
                    </#if> /></div>
            <#else>
                <input style="float: right; width: 80%"  style="cursor:pointer;" 
                    <#if isReadOnlyRow >readonly="readonly"
                        class="numericInList input_mask mask_double ignore_check_modification" 
                    <#else>
                        class="numericInList input_mask mask_double" 
                    </#if> 
                    type="text" maxlength="20" size="12" value="${workEffortTransactionIndicatorView.weTransValue?if_exists}" name="weTransValue_o_${index}" decimal_digits="${workEffortTransactionIndicatorView.weTransDecimalScale?if_exists}">
            </#if>
        </td>
    </#if>
</#if>
                                            