<#if periodList?has_content>
    <table id="IndicatorTransactionGanttTable" class="basic-table list-table padded-row-table" cellspacing="0">
        <thead>
                <tr class="header-row-2">
                    <th rowspan="2"><div>${columnEtch?if_exists}</div></th>
                    <#if context.showWeigthColumn == "Y">
                        <th rowspan="2">${uiLabelMap.WorkEffortViewIdentificationData_assocWeight}</th>  
                    </#if>
                    <#if hideFiscalType?if_exists?default('N') != "Y">
                        <th rowspan="2">${uiLabelMap.FormFieldTitle_weTransValueProgress}</th>
                    </#if>
                    <#list periodList as period>
                        <th rowspan="2">${period.customTimePeriodCode?if_exists}</th>
                    </#list> 
                </tr>
            </thead>
        <tbody style="height: auto;">
            <#assign index=0/>
            <#list workEffortChildList as workEffort>
                <#assign showWorkEffortName = "true"/>
                <#list glFiscalTypeList as glFiscalType>
                    <tr  <#if index%2 != 0>class="alternate-row"</#if>>
                        <#if showWorkEffortName = "true">
                            <#assign showWorkEffortName = "false"/>
                            <td style="width: 400px; font-weight: bold" rowspan="${glFiscalTypeList?size}">
                                <div><#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>${workEffort.wrToNameLang}<#else>${workEffort.wrToName}</#if></div>
                                <input name="workEffortIdTo" type="hidden" value="${workEffort.workEffortIdTo}"/>
                            </td>
                            <#if context.showWeigthColumn == "Y">
                                <td style="width: 30px; text-align: center;" rowspan="${glFiscalTypeList?size}">
                                   <div>${workEffort.assocWeight}</div> 
                                </td>
                            </#if>
                        </#if>
                        <#-- 8C8C8C grigio scuro -->
                        <#-- BCBCBC grigio chiaro -->
                        <#-- 5E9EFF blu -->
                        <#if hideFiscalType?if_exists?default('N') != "Y">
                        	<td style="width: 70px;">
                     			<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    				${glFiscalType.descriptionLang?if_exists}
                    			<#else>
                    				${glFiscalType.description}
                    			</#if>                         
                        	</td>
                        </#if>
                        <#list transactionPanelMap[workEffort.workEffortIdTo] as item>
                            <#if item.glFiscalTypeId == glFiscalType.glFiscalTypeId>
                            
                            <#-- imposto gli stili da usare nelle colonne in base ai dati -->
                            <#assign dataColCssStyle = "transaction-data-cell">
                            <#if item.isReadOnly == "Y">
                                <#assign dataColCssStyle = "transaction-data-cell-readonly">
            				</#if>
            				<#assign dataCntCssStyle = "transaction-data">
                            <#if item.weTransId?has_content>
                            	<#if item.isReadOnly != "Y">
                            		<#assign dataCntCssStyle = "transaction-data-present">
                            	</#if>
                            <#else>
                            	<#if glFiscalType.glFiscalTypeEnumId == "GLFISCTYPE_TARGET">
                            		<#if item.fromDate &lt;= workEffort.wrToThruDate && item.thruDate &gt;= workEffort.wrToFromDate>
                            			<#assign dataCntCssStyle = "transaction-data-empty-period-intersect">
                            		</#if>
                            	</#if>
                            	<#if glFiscalType.glFiscalTypeEnumId == "GLFISCTYPE_ACTUAL">
                            		<#if item.hasPeriodDates?if_exists == "Y">
                            			<#if item.fromDate &lt;= item.actualThruDate?if_exists && item.thruDate &gt;= workEffort.wrToActualFromDate>
                            				<#assign dataCntCssStyle = "transaction-data-empty-period-intersect">
                            			</#if> 
                            			<#else> 
                            				<#if item.hasShowActualDates?if_exists == "Y">
                            					<#if item.fromDate &lt;= workEffort.wrToActualThruDate && item.thruDate &gt;= workEffort.wrToActualFromDate>
                            					<#assign dataCntCssStyle = "transaction-data-empty-period-intersect">
                            				</#if>
                            			</#if>                          		
                            		</#if>
                            	</#if>
                            </#if>
                                                        
                            <td class="${dataColCssStyle?if_exists}">
								<div class="${dataCntCssStyle?if_exists}">
        							<div style="text-align: center" title="<#if item.weTransValue?has_content>${item.weTransValue}</#if>"><#if item.hasComments?if_exists == "Y"><a class="fa transactionWithNote" href="#"></a>&nbsp;</#if>${item.weTransValue?if_exists}</div>
                                    <input name="weTransId" type="hidden" value="${item.weTransId?if_exists}"/>
                                    <input name="weTransEntryId" type="hidden" value="${item.weTransEntryId?if_exists}"/>
                                    <input name="weTransMeasureId" type="hidden" value="${item.weTransMeasureId?if_exists}"/>
                                    <input name="weTransWeId" type="hidden" value="${item.weTransWeId?if_exists}"/>
                                    <input name="weTransTypeValueId" type="hidden" value="${item.weTransTypeValueId?if_exists}"/>
                                    <input name="isReadOnly" type="hidden" value="${item.isReadOnly}"/>
                                    <input name="customTimePeriodId" type="hidden" value="${item.customTimePeriodId?if_exists}"/>
                                    <input name="glFiscalTypeEnumId" type="hidden" value="${item.glFiscalTypeEnumId?if_exists}"/>
                                	<input name="parentWorkEffortTypeId" type="hidden" value="${parentWorkEffortTypeId?if_exists}"/> 
                                	<input name="glAccountId" type="hidden" value="${item.glAccountId?if_exists}"/>
                                	<input name="isAdmin" type="hidden" value="${item.isAdmin?if_exists}"/> 
                                	<input name="weTransDate" type="hidden" value="${item.thruDate?if_exists}"/>                                 	
                                	<input name="weTransCurrencyUomId" type="hidden" value="${item.weTransCurrencyUomId?if_exists}"/>                               	                               	                              
                                    <#if !item.weTransId?has_content>&nbsp;</#if>
                                </div>                                    
                            </td>
                            </#if>
                       </#list>
                    </tr>  
                </#list>
                <#assign index = index+1>
            </#list>
            <#if ("Y" == context.showTotalRow)>
                <#assign firstRow = "true"/>
                <#list glFiscalTypeList as glFiscalType>
                    <tr class="header-row-2">
                        <#if firstRow = "true">
                            <#assign firstRow = "false"/>
                            <td rowspan="${glFiscalTypeList?size}">
                                <div style="font-weight: bold">
                                    ${uiLabelMap.SalGantt_projectSal}
                                </div>
                            </td>
                            <#if context.showWeigthColumn == "Y">
	                            <td style="width: 30px;" rowspan="${glFiscalTypeList?size}">
	                                <div>${context.assocWeightSum}</div>
	                            </td>
                            </#if>
                        </#if>
                        <#if hideFiscalType?if_exists?default('N') != "Y">
                        	<td style="width: 70px;">
                            	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                    				<div>${glFiscalType.descriptionLang?if_exists}</div>
                    			<#else>
                    				<div>${glFiscalType.description}</div>
                    			</#if>
                        	</td>
                        </#if>
                        <#list periodList as period>
                            <#assign key = glFiscalType.glFiscalTypeId + period.customTimePeriodId/>
                            <td><#if footerMap[key]?has_content && footerMap[key] != 0>${footerMap[key]?if_exists}%</#if></td>
                        </#list>
                    </tr>
                </#list> 
            </#if>
        </tbody>
    </table>
    <br>
    <br>
    <br>
</#if>
