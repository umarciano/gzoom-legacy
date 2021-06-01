<script>
    if ("${parameters.errorLoadMeasure?if_exists}" != "" &&  "${parameters.errorLoadMeasure?if_exists}" != null) {
        var data = $H({});
        data["_ERROR_MESSAGE_"] = "${parameters.errorLoadMeasureDescr?if_exists}"; 
        modal_box_messages.onAjaxLoad(data, null);
        modal_box_messages.alert("${parameters.errorLoadMeasure?if_exists}");
    }
</script>
<div id="container-${parameters.reloadRequestType}Transaction-${parameters.contentIdInd}">
    <#if glAccountDescr?has_content>
        <div align="left" class="container-transaction-panel-glacc-description">
            <textarea readonly="readonly" class="transaction-panel-glacc-description">${context.glAccountDescr}</textarea>
        </div>
    </#if>

    <#if periodList?has_content>
        <table id="${parameters.reloadRequestType}TransactionTable_${parameters.contentIdInd}" class="basic-table list-table padded-row-table" cellspacing="0">
            <thead>
                <tr class="header-row-2">
                    <th rowspan="2"><div>${destinationColumnDescription}</div></th>
                    <th rowspan="2"></th>
                    <#list periodList as period>
                        <th rowspan="2">${period.customTimePeriodCode?if_exists}</th>
                    </#list>
                </tr>
            </thead>
            <tbody style="height: auto;">
                <#assign index=0/>
                <#list destinationList as destination>
                    <#assign showDestinationName = "true"/>
                    <#list glFiscalTypeList as glFiscalType>
                        <tr  <#if index%2 != 0>class="alternate-row"</#if>>
                            <#if showDestinationName = "true">
                                <#assign showDestinationName = "false"/>
                                <td style="width: 400px; font-weight: bold" rowspan="${glFiscalTypeList.size()}">
                                    <div>${destination.partyName}</div>
                                    <input name="partyId" type="hidden" value="${destination.partyId}"/>
                                </td>
                            </#if>
                            <td style="width: 70px;">
                     			<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    				${glFiscalType.descriptionLang?if_exists}
                    			<#else>
                    				${glFiscalType.description}
                    			</#if>                           
                            </td>
                            <#list transactionPanelMap[destination.partyId] as item>
                                <#if item.glFiscalTypeId == glFiscalType.glFiscalTypeId>
                                    <td style="text-align: <#if item.weTransUomType?has_content && item.weTransUomType == "RATING_SCALE">center;<#else>right;</#if> cursor:pointer;"
                                    <#if item.isReadOnly == "Y"  || item.crudEnumId?if_exists == "NONE" ||
                                     item.valModId?if_exists == "ALL_NOT_MOD" || 
                                     (item.valModId?if_exists == "ACTUAL_NOT_MOD" && item.weTransTypeValueId?if_exists == "ACTUAL") ||
                                     (item.valModId?if_exists == "BUDGET_NOT_MOD" && item.weTransTypeValueId?if_exists == "BUDGET") > readonly="readonly"</#if>>       
                                       <#if item.hasComments?has_content && item.hasComments == "Y"><a class="fa transactionWithNote" href="#"></a>&nbsp;</#if><#if item.weTransValue?has_content><#if item.weTransValue?is_number>${item.weTransValue?string("#,##0.########")}<#else>${item.weTransValue?if_exists}</#if></#if>
                                        <input name="weTransId" type="hidden" value="${item.weTransId?if_exists}"/>
                                        <input name="weTransEntryId" type="hidden" value="${item.weTransEntryId?if_exists}"/>
                                        <input name="weTransMeasureId" type="hidden" value="${item.weTransMeasureId?if_exists}"/>
                                        <input name="weTransWeId" type="hidden" value="${item.weTransWeId?if_exists}"/>
                                        <input name="weTransTypeValueId" type="hidden" value="${item.weTransTypeValueId?if_exists}"/>
                                        <input name="isReadOnly" type="hidden" value="${item.isReadOnly}"/>
                                        <input name="rootInqyTree" type="hidden" value="${parameters.rootInqyTree}"/>
                                        <input name="customTimePeriodId" type="hidden" value="${item.customTimePeriodId?if_exists}"/>
                                        <input name="entryPartyId" type="hidden" value="${item.entryPartyId?if_exists}"/>
                                        <input name="entryRoleTypeId" type="hidden" value="${item.entryRoleTypeId?if_exists}"/>
                                        <input name="crudEnumId" type="hidden" value="${item.crudEnumId?if_exists}"/>
                                        <input name="valModId" type="hidden" value="${item.valModId?if_exists}"/>
                                        <input name="voucherRef" type="hidden" value="${item.voucherRef?if_exists}"/>
                                        <input name="reloadRequestType" type="hidden" value="${reloadRequestType?if_exists}"/>
                                        <input name="onlyWithBudget" type="hidden" value="${onlyWithBudget?if_exists}"/>
                                        <input name="accountFilter" type="hidden" value="${accountFilter?if_exists}"/>
                                        <input name="contentIdInd" type="hidden" value="${parameters.contentIdInd?if_exists}"/>
                                        <input name="contentIdSecondary" type="hidden" value="${contentIdSecondary?if_exists}"/>
                                    </td>
                                </#if>
                           </#list>
                        </tr>  
                    </#list>
                    <#assign index = index+1>
                </#list>
                <#if showTotal>
                    <#assign showTotalEtch = "true"/>
                    <#list glFiscalTypeList as glFiscalType>
                        <tr class="header-row-2">
                            <#if showTotalEtch = "true">
                                <#assign showTotalEtch= "false"/>
                                <td rowspan="${glFiscalTypeList.size()}">${uiLabelMap.TotalColumn}</td>
                            </#if>
                            <td>${glFiscalType.description}</td>
                            <#list periodList as period>
                                <td style="text-align: right;">${totalMap[glFiscalType.glFiscalTypeId + period.customTimePeriodId]?if_exists}</td>
                            </#list>                      
                        </tr>
                    </#list>
                </#if>
            </tbody>
        </table>
    </#if>
</div>
