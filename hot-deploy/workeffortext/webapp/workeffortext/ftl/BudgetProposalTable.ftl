<table>
    <tbody>
        <tr>
            <#if parameters.finalBudget != "Y">
                <td class="label">
                    <fieldset class="confined">
                        <legend>${uiLabelMap.Budget_balance}</legend>
                        <table class="basic-table" style="width: 120px">
                            <tbody>
                                <tr class="header-row-2">
                                    <td><#if cdcBudgetTotal?has_content><#if cdcBudgetTotal?is_number>${cdcBudgetTotal?string("#,##0.########")}<#else>${cdcBudgetTotal?if_exists}</#if><#else>&nbsp;</#if></td>
                                </tr>
                            </tbody>    
                        </table>
                    </fieldset>
                </td>
            </#if>
            
            <td class="label">
                <fieldset class="confined">
                    <legend>${uiLabelMap.All_Budget_proposed}</legend>
                    <table style="width: 100%;">
                        <tbody>
                            <tr>
                                <#list glFiscalTypeList as glFiscalType>
                                <td>
                                    <table>
                                        <tbody>
                                            <tr>
                                                <td>${glFiscalType.description}</td>
                                                <td>
                                                    <table class="basic-table" style="width: 120px">
                                                        <tbody>
                                                            <tr class="header-row-2">
                                                                <#assign item_rowTotal = "" >
                                                                <#if rowTotal[glFiscalType.glFiscalTypeId]?has_content>
                                                                    <#if rowTotal[glFiscalType.glFiscalTypeId]?is_number>
                                                                        <#assign item_rowTotal = rowTotal[glFiscalType.glFiscalTypeId]?string("#,##0.########") >
                                                                    <#else>
                                                                        <#assign item_rowTotal = rowTotal[glFiscalType.glFiscalTypeId]?if_exists >
                                                                    </#if>
                                                                <#else>
                                                                    <#assign item_rowTotal = "&nbsp;" >
                                                                </#if>
                                                                <td>${item_rowTotal}</td>
                                                            </tr>
                                                        </tbody>    
                                                    </table>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    </td>
                                </#list>
                            </tr>
                        </tbody>
                    </table>
                </fieldset>
            </td>

            <#if currentStatus?has_content>
                <td class="label" style="text-align: right !important;">${uiLabelMap.BudgetStatus}
                    <select id="budgetNextStatus">
                        <option value="${currentStatus.statusId}" selected="selected">${currentStatus.description}</option>
                        <#if nextStatusList?has_content>
                        <option disabled="disabled">---------</option>
                        <#list nextStatusList as nextStatus>
                        <option value="${nextStatus.statusIdTo}"
                            conditionexpression="${nextStatus.conditionExpression?if_exists}"
                            <#if nextStatus.denyStatusChangeMsg?has_content>denystatuschangemsg="${nextStatus.denyStatusChangeMsg}"</#if>
                            >${nextStatus.description}</option>
                        </#list> 
                        </#if>
                    </select>
                </td>
            </#if>
        </tr>
    </tbody>
</table>
<br>
<div class="budgetContainer">
    <table id="BudgetProposalTable" class="basic-table list-table padded-row-table less-font-size-table" cellspacing="0">
        <thead>
            <tr class="header-row-2">
                <th>${uiLabelMap.BudgetProposalTable_Chapter}</th>
                <#if parameters.finalBudget?has_content && parameters.finalBudget = "Y">
                    <th>${uiLabelMap.BudgetProposalTable_Budget}</th>
                </#if>
                <th/>
                <#list workEffortList as workEffort>
                    <th title="${workEffort.workEffortName?if_exists}">${workEffort.sourceReferenceId?if_exists}</th>
                </#list>
                <th>${uiLabelMap.TotalColumn}</th>
            </tr>
        </thead>
        <tbody style="height: auto;">
            <#assign index=0/>
            <#assign count=0/>
            <#list glAccountList as glAccount>
                <#assign glAccountCode = "true"/>
                <#list glFiscalTypeList as glFiscalType>
                    <tr  <#if index%2 != 0>class="alternate-row"</#if>>
                            <#if glAccountCode = "true">
                                <#assign glAccountCode = "false"/>
                                <td rowspan="${glFiscalTypeList.size()}" title="${glAccount.accountName?if_exists}">
                                    <div>${glAccount.accountCode}</div>
                                    <input name="glAccountId" type="hidden" value="${glAccount.glAccountId}"/>
                                </td>
                                <#if parameters.finalBudget?has_content && parameters.finalBudget = "Y">
                                    <td rowspan="${glFiscalTypeList.size()}" style="text-align: right;">
                                    <#list glFiscalTypeList as glFiscalType2>
                                        <#--
                                        <#assign dummy = Static["org.ofbiz.base.util.Debug"].log(
                                            "*** chapterBudget[" + glAccount.glAccountId + "] = "
                                            + chapterBudget[glAccount.glAccountId]?default("")
                                            + " rowTotal[" + glAccount.glAccountId + glFiscalType2.glFiscalTypeId + "] = "
                                            + rowTotal[glAccount.glAccountId + glFiscalType2.glFiscalTypeId]?default("")
                                        )?if_exists>
                                        -->
                                        <#assign imgWarning = "">
                                        <#if chapterBudget[glAccount.glAccountId]?has_content && chapterBudget[glAccount.glAccountId]?is_number>
                                            <#if rowTotal[glAccount.glAccountId + glFiscalType2.glFiscalTypeId]?has_content
                                                && rowTotal[glAccount.glAccountId + glFiscalType2.glFiscalTypeId]?is_number
                                                && rowTotal[glAccount.glAccountId + glFiscalType2.glFiscalTypeId] &gt; chapterBudget[glAccount.glAccountId]
                                            >
                                                <#assign imgWarning = "${uiLabelMap.Budget_ChapterTotalGreaterThanBudget}">
                                            </#if>
                                        <#else>
                                            <#assign imgWarning = "${uiLabelMap.Budget_BudgetNotSet}">
                                        </#if>
                                        <#if imgWarning?has_content>
                                            <img class="mblDisplayIconCell" title="${imgWarning}" src="/workeffortext/images/messagebox_warning.gif" align="left"/>
                                        </#if>
                                    </#list>
                                    <#if chapterBudget[glAccount.glAccountId]?has_content>
                                        <#if chapterBudget[glAccount.glAccountId]?is_number>${chapterBudget[glAccount.glAccountId]?string("#,##0.########")}
                                        <#else>${chapterBudget[glAccount.glAccountId]?if_exists}
                                        </#if>
                                    </#if>
                                    </td>
                                </#if>
                            </#if>
                            
                            <td style="width: 90px;">${glFiscalType.description}</td>
                            <#list transactionPanelMap[glAccount.glAccountId] as item>
                                <#if item.weTransTypeValueId == glFiscalType.glFiscalTypeId>
                                    <td id="BudgetProposalTable_${count}"
                                            style="text-align: <#if item.weTransUomType?has_content && item.weTransUomType == "RATING_SCALE">center;<#else>right;</#if> cursor:pointer; <#if item.alertFlag?default("N") == "Y">background-color: #FFEE3A;</#if>"
                                		    <#if item.isReadOnly?default("N") == "Y">readonly="readonly"</#if>>
                            		    <#if item.hasComments?has_content && item.hasComments == "Y"><a class="fa transactionWithNote" href="#"></a>&nbsp;</#if>
                            		    <#if item.weTransValue?has_content>
                            		        <#if item.weTransValue?is_number>
                            		            ${item.weTransValue?string("#,##0.########")}
                            		        <#else>
                            		            ${item.weTransValue?if_exists}
                        		            </#if>
                    		            </#if>
                                        <input name="weTransId" type="hidden" value="${item.weTransId?if_exists}"/>
                                        <input name="weTransEntryId" type="hidden" value="${item.weTransEntryId?if_exists}"/>
                                        <input name="weTransMeasureId" type="hidden" value="${item.weTransMeasureId?if_exists}"/>
                                        <input name="weTransWeId" type="hidden" value="${item.weTransWeId?if_exists}"/>
                                        <input name="weTransTypeValueId" type="hidden" value="${item.weTransTypeValueId?if_exists}"/>
                                        <input name="isReadOnly" type="hidden" value="${item.isReadOnly?if_exists}"/>
                                        <input name="customTimePeriodId" type="hidden" value="${item.customTimePeriodId?if_exists}"/>
                                        <input name="weTransAccountId" type="hidden" value="${item.weTransAccountId?if_exists}"/>
                                        <input name="groupStatusId" type="hidden" value="${item.groupStatusId?if_exists}"/>
                                        <input name="glFiscalTypeEnumId" type="hidden" value="${item.glFiscalTypeEnumId?if_exists}"/>
                                		<input name="parentWorkEffortTypeId" type="hidden" value="${workEffortTypeId?if_exists}"/> 
                                    </td>
                                </#if>
                                <#assign count = count+1/>
                           </#list>
                           <td style="text-align: right; font-weight: bold;"><#if rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId]?has_content><#if rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId]?is_number>${rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId]?string("#,##0.########")}<#else>${rowTotal[glAccount.glAccountId + glFiscalType.glFiscalTypeId]?if_exists}</#if></#if></td>
                     </tr>  
                 </#list>
                 <#assign index = index+1>
            </#list>
            <tr class="header-row-2">
            	<td rowspan="2" style="padding: 0px !important;">${uiLabelMap.TotalColumn_1}<br/>${uiLabelMap.TotalColumn_2}</td>
                <#if parameters.finalBudget?has_content && parameters.finalBudget = "Y">
                    <td rowspan="2" style="text-align: right;"><#if chapterBudgetTotal?has_content><#if chapterBudgetTotal?is_number>${chapterBudgetTotal?string("#,##0.########")}<#else>${chapterBudgetTotal?if_exists}</#if></#if></td>
                </#if>
                <#assign firstExecution = true/>
                <#list glFiscalTypeList as glFiscalType>
                    <#if firstExecution = false>
                        <tr class="header-row-2">
                    </#if>
                    <td>${glFiscalType.description}</td>
                    <#list workEffortList as workEffort>
                        <td style="text-align: right;"><#if columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId]?has_content><#if columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId]?is_number>${columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId]?string("#,##0.########")}<#else>${columnTotal[workEffort.workEffortId + glFiscalType.glFiscalTypeId]?if_exists}</#if></#if></td>
                    </#list>
                    <#assign firstExecution = false/>
                    <td style="text-align: right;"><#if columnTotal[glFiscalType.glFiscalTypeId]?has_content><#if columnTotal[glFiscalType.glFiscalTypeId]?is_number>${columnTotal[glFiscalType.glFiscalTypeId]?string("#,##0.########")}<#else>${columnTotal[glFiscalType.glFiscalTypeId]?if_exists}</#if></#if></td>
                </#list>
            </tr>
        </tbody>
    </table>
</div>