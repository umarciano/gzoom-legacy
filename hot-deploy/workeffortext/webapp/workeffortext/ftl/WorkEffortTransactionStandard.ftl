<#if !parameters.errorLoadTrans?has_content>
    <form id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}" class="basic-form cachable noTableResizeHeight formToRefresh" name="WorkEffortTransactionViewStandardManagementMultiForm" method="post" action="<@ofbizUrl>elaborateMultiFormForUpdateAjax</@ofbizUrl>" 
    onsubmit="javascript:ajaxSubmitFormUpdateAreas('WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}','','child-management-screenlet-container-WorkEffortTransactionView-${context.relationTitle?if_exists},<@ofbizUrl>childManagementListContainerOnly</@ofbizUrl>,searchDate=${parameters.searchDate?if_exists}&contextManagement=N&forcedBackAreaId=&folderIndex=${context.folderIndex?if_exists}&screenNameListIndex=${context.screenNameListIndex?if_exists}&backAreaId=&saveView=N&relationTitle=${context.relationTitle?if_exists}&successCode=&entityName=WorkEffortTransactionView&parentEntityName=WorkEffortView&managementFormType=multi&workEffortId=${context.weTransWeId?if_exists}&weTransWeId=${context.weTransWeId?if_exists}&contentIdInd=${context.contentIdInd?if_exists}&contentId=${context.contentIdInd?if_exists}&contentIdSecondary=${context.contentIdSecondary?if_exists}&wizard=N&subFolder=Y&scrollInt=${context.scrollInt}&rootInqyTree=${parameters.rootInqyTree?if_exists}&specialized=${parameters.specialized?if_exists}'); return false;">
        
        
		<div id="WorkEffortTransactionViewStandardScreen" class="tableContainer" style="height: auto;">
		    <table id="table_TRANSSTD_WorkEffortTransactionIndicatorView-${context.relationTitle}" class="basic-table list-table padded-row-table resizable draggable toggleable selectable no-jar-selectable customizable headerFixable multi-editable noTableResizeHeight" cellspacing="0" cellpadding="0">
		        <#assign colspan = 0/>
		        <#assign renderThead = "Y"/>
		        <#assign renderNewRow = "N"/>
		        <thead>
		            <tr class="header-row-2">
                        <#if context.showComments == "LEFT">
                            <#if context.commentsEtchDescr?if_exists == "action">
                                <#assign commentsLabel = uiLabelMap.Indicator_comments_action>
                            <#elseif context.commentsEtchDescr?if_exists == "dataSource">
                            	<#assign commentsLabel = uiLabelMap.Indicator_comments_Data_Source>
                            <#elseif context.commentsEtchDescr?if_exists == "verificationSource">
                            	<#assign commentsLabel = uiLabelMap.Indicator_comments_Verification_Source>
                            <#elseif context.commentsEtchDescr?if_exists == "category">
                            	<#assign commentsLabel = uiLabelMap.Indicator_comments_category>                            	
                            <#else>
                                <#assign commentsLabel = uiLabelMap.Indicator_comments>
                            </#if>
                            <#if commentsTitleAreaClass?has_content>
                                <th class="${commentsTitleAreaClass}"><div>${commentsLabel}</div></th>
                            <#else>
                                <th style="width: 10%"><div>${commentsLabel}</div></th>
                            </#if>
                        </#if>
                        
		                <#-- COLONNA CON SEQUENCE_ID -->
                        <#if context.showSequenceId == "Y">
                            <th style="width: 8em;">${uiLabelMap.FormFieldTitle_sequenceId}</th>
                        </#if>                        
                        
		                <#if context.showType == "SX">
	                        <th style="width: 10em;">${uiLabelMap.WorkEffortTypology}</th>
	                    </#if>  
	                    
	                    <#if context.showAccountReference == "Y">
	                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/accountReferenceColumn.ftl" />
	                    </#if>                      
		            
		                <#-- COLONNA CON INDICATORE -->
		                <#if context.showAccountReference == "Y">
		                    <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glAccountColumnNoMandatory.ftl" />
		                <#else>
		                    <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glAccountColumn.ftl" />
		                </#if>
		                
		                <#if context.showType == "Y">
		                    <th style="width: 10em;">${uiLabelMap.WorkEffortTypology}</th>
		                </#if>
		                
		                <#if context.showDirection == "Y">
	                        <th style="width: 10em;">${uiLabelMap.FormFieldTitle_debitCreditDefault}</th>
	                    </#if>
		                
                        <#-- COLONNA CON KPI_WEIGHT (OPT) -->
		                <#if context.showKpiWeight == "Y">
                            <th style="width: 6em;">${uiLabelMap.FormFieldTitle_kpiScoreWeight}</th>
                        </#if>
                        <#-- COLONNA CON KPI_OTHER_WEIGHT (OPT) -->
                        <#if context.showKpiOtherWeight == "Y">
                            <th style="width: 6em;">${uiLabelMap.FormFieldTitle_kpiOtherWeight}</th>
                        </#if>
                        <#-- COLONNA CON ABBREVIATION - DEFAULT_UOM_ID -->
                        <#if context.showUom == "Y">
                            <th style="width: 8em;">${uiLabelMap.FormFieldTitle_defaultUomId}</th>
                        </#if>
                        <#-- COLONNA CON UOM_DESCR (OPT) -->
                        <#if context.showUomDescr == "Y">
                            <#if multiTypeLang?has_content && multiTypeLang?if_exists != "NONE">
                            	<th class="${uomDescrTitleAreaClass}">
                                	<div>
                                    	${uiLabelMap.Indicator_uomDescr}
                                    	<img src="${primaryLangFlagPath?if_exists}" title="${primaryLangTooltip?if_exists}"/>
                                	</div>
                            	</th>
                            	<th class="${uomDescrTitleAreaClass}">
                                	<div>
                                    	${uiLabelMap.Indicator_uomDescr}
                                    	<img src="${secondaryLangFlagPath?if_exists}" title="${secondaryLangTooltip?if_exists}"/>
                                	</div>
                            	</th>                            
                            <#else>
                                <th class="${uomDescrTitleAreaClass}"><div>${uiLabelMap.Indicator_uomDescr}</div></th>
                            </#if>
                        </#if>
                        
                        <#if context.showComments == "RIGHT"  || context.showComments == "Y">
                            <#if context.commentsEtchDescr?if_exists == "action">
                                <#assign commentsLabel = uiLabelMap.Indicator_comments_action>
                            <#elseif context.commentsEtchDescr?if_exists == "dataSource">
                            	<#assign commentsLabel = uiLabelMap.Indicator_comments_Data_Source>
                            <#elseif context.commentsEtchDescr?if_exists == "verificationSource">
                            	<#assign commentsLabel = uiLabelMap.Indicator_comments_Verification_Source>
                            <#elseif context.commentsEtchDescr?if_exists == "category">
                            	<#assign commentsLabel = uiLabelMap.Indicator_comments_category>                             	
                            <#else>
                                <#assign commentsLabel = uiLabelMap.Indicator_comments>
                            </#if>
                            <#if commentsTitleAreaClass?has_content>
                                <th class="${commentsTitleAreaClass}"><div>${commentsLabel}</div></th>
                            <#else>
                                <th style="width: 10%"><div>${commentsLabel}</div></th>
                            </#if>
                        </#if>                        
                        
                        <#-- COLONNA CON DETAIL (OPT) -->
                        <#if context.showDetail != "N">
                        <th><div>${uiLabelMap.Detail}</div></th>
                        <#assign colspan = colspan + 1/>
                        </#if>
                        <#-- COLONNA CON GL_FISCAL_TYPE (OPT) -->
                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glFiscalTypeColumn.ftl" />
                        
                        <#-- COLONNA O COLONNE PERIODI -->
                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/periodColumn.ftl" />
                            
		            </tr>
		        </thead>
		        <#assign renderThead = "N"/>
		        <tbody class="valIndicatore">
		        	<#assign index=0/>
		        	<#assign indexClass=0/>
		        	<#assign kpiScoreForWeTransValueTot=0/>
                    <#assign workEffortTransactionIndicatorViewTotal=null />
                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
		                <#assign droplistSelectFields = "[[uomRatingValue, uomCodeLang]]" />
		                <#assign droplistDisplayFields = "[[uomCodeLang]]" />
		                <#assign droplistEntityDescriptionField = "uomCodeLang" />
		            <#else>
		                <#assign droplistSelectFields = "[[uomRatingValue, uomCode]]" />
		                <#assign droplistDisplayFields = "[[uomCode]]" />
		                <#assign droplistEntityDescriptionField = "uomCode" />		                    	
		            </#if>
                    
		            <#list workEffortTransactionIndicatorViewList?if_exists as measureMap>
    		            <#assign partyList = measureMap.rowList />
    		            <#assign weTransMeasureId = "">
    		            <#assign renderFirstTd = true>
    		            <#assign renderOtherTd = true>
    		            <#assign renderDetailTd = true>
                        <#assign rowspanMeasure = partyList.size()>
                        <#-- un tr per cella, in modo da gestire il reset della form -->
                        <#assign periodIndex = 1>
                        <#list partyList?if_exists as mappaParty>
        		            
        		            <#assign entryPartyId = mappaParty.entryPartyId />
                            <#assign typeList = mappaParty.rowList />
                            <#assign rowspanDetail = typeList.size()> <#-- TODO -->

                            <#list typeList?if_exists as mappaType>
                                <#if mappaType.weTransTypeValueId?has_content>
                        		    <#assign weTransTypeValueId = mappaType.weTransTypeValueId?if_exists />
                        		</#if>

                          
                                        		        		
        		        		<#assign rowList = mappaType.rowList />
                            	<#assign rowspanPeriod = rowList.size()>
                            	<#assign rowspanParty = rowspanDetail * rowspanPeriod />
                                <#assign rowspan = rowspanMeasure * rowspanDetail * rowspanPeriod + 1 />
                            	<#assign firstTd = rowList[0]>
                            	<#assign rowListSize = rowList?size>
                            
                            	<#if "TOTAL" != measureMap.weTransMeasureId>
        		                    
                                    <#assign periodNonehasTrans = false>
                                    <#list rowList?if_exists as workEffortTransactionIndicatorView>
                                        <#if renderFirstTd>
                                            <tr <#if indexClass%2 != 0>
                                                class="indexClass_${indexClass} alternate-row"
                                                <#else>
                                                class="indexClass_${indexClass}"
                                                </#if>
                                            >
                                            <#assign renderFirstTd = false>
                                            
                                            <#-- COLONNA CON SEQUENCE_ID -->
                                            <#if context.showSequenceId == "Y">
                                                <td rowspan="${rowspan}" style="width: 6em;">
                                                    <input readonly="readonly" class="numericInList ignore_check_modification" type="text" maxlength="15" size="3" value="${firstTd.sequenceId?if_exists}" name="sequenceId_o_${index}">
                                                </td>
                                            </#if>                                            
                                            
                                            <#if context.showComments == "LEFT">
                                                <td rowspan="${rowspan}"><div>${firstTd.comments?if_exists}</div></td>
                                            </#if>
                                            
                                            <#if context.showType == "SX">
                                                <td rowspan="${rowspan}" style="width: 10em;">
                                                    <div>${firstTd.gltDescr?if_exists}</div>
                                                </td>   
                                            </#if>
                                            
	                                        <#if context.showAccountReference == "Y">
	                                            <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/accountReferenceColumn.ftl" />
	                                        </#if>                                                                                        
                                            
                                            <#-- COLONNA CON INDICATORE -->
                                            <#if context.showAccountReference == "Y">
		                                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glAccountColumnNoMandatory.ftl" />
		                                    <#else>
		                                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glAccountColumn.ftl" />
		                                    </#if>
                                            
                                            <#if context.showType == "Y">
                                                <td rowspan="${rowspan}" style="width: 10em;">
                                                    <div>${firstTd.gltDescr?if_exists}</div>
                                                </td>   
                                            </#if>
                                            
                                            <#if context.showDirection == "Y">
                                                <td rowspan="${rowspan}" style="width: 10em;">
                                                    <input readonly="readonly" class="ignore_check_modification" type="text" value="${firstTd.dcDescr?if_exists}" title="${firstTd.dcDescr?if_exists}" name="dcDescr_o_${index}">
                                                </td>   
                                            </#if>                                            
                                            
                                            <#-- COLONNA CON KPI_WEIGHT (OPT) -->
                                            <#if context.showKpiWeight == "Y">
                                                <td rowspan="${rowspan}" style="width: 6em;">
                                                    <input readonly="readonly" class="numericInList ignore_check_modification" type="text" maxlength="15" size="3" value="${firstTd.kpiScoreWeight?if_exists}" name="kpiScoreWeight_o_${index}">
                                                </td>
                                            </#if>
                                        
                                            <#-- COLONNA CON KPI_OTHER_WEIGHT (OPT) -->
                                            <#if context.showKpiOtherWeight == "Y">Other
                                                <td rowspan="${rowspan}" style="width: 6em;">
                                                    <input readonly="readonly" class="numericInList ignore_check_modification" type="text" maxlength="15" size="3" value="${firstTd.kpiOtherWeight?if_exists}" name="kpiOtherWeight_o_${index}">
                                                </td>
                                            </#if>
                                        
                                            <#-- COLONNA CON ABBREVIATION - DEFAULT_UOM_ID -->
                                            <#if context.showUom == "Y">
                                                <td rowspan="${rowspan}" style="width: 8em;">
                                                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                                                        <div>${firstTd.weTransUomAbbLang?if_exists}</div>
                                                    <#else>
                                                        <div>${firstTd.weTransUomAbb?if_exists}</div>
                                                    </#if>
                                                </td>
                                            </#if>
                                            
                                            <#-- COLONNA CON UOM_DESCR (OPT) -->
                                            <#if context.showUomDescr == "Y">
                                                <#if multiTypeLang?has_content && multiTypeLang?if_exists != "NONE">
                                                    <td rowspan="${rowspan}">
                                                        <div>${firstTd.weTransUomDesc?if_exists}</div>
                                                    </td>
                                                    <td rowspan="${rowspan}">
                                                        <div>${firstTd.weTransUomDescLang?if_exists}</div>
                                                    </td>
                                                <#else>
                                                    <td rowspan="${rowspan}">
                                                        <div>${firstTd.weTransUomDesc?if_exists}</div>
                                                    </td>
                                                </#if>
                                            </#if>
                                            
                                            <#if context.showComments == "RIGHT"  || context.showComments == "Y">
                                                <td rowspan="${rowspan}"><div>${firstTd.comments?if_exists}</div></td>
                                            </#if>
                                        </tr>
                                        <#assign index = index+1>
                                            
                                        </#if>
                                        
                                        <#if "ONE" == context.showDetail && workEffortTransactionIndicatorView.weTransMeasureId != parameters.workEffortMeasureId?if_exists>
                                                <tr 
                                                <#if indexClass%2 != 0>
                                                    class="indexClass_${indexClass} alternate-row"
                                                <#else>
                                                    class="indexClass_${indexClass}"
                                                </#if>
                                            >
                                            <td colspan="${colspan}" class="slave-td widget-area-style"></td>
                                        <#else>
                                            <tr 
                                                <#if indexClass%2 != 0>
                                                    class="indexClass_${indexClass} alternate-row"
                                                <#else>
                                                    class="indexClass_${indexClass}"
                                                </#if>
                                            >
                                            
                                            <#-- COLONNA CON DETAIL (OPT) -->
                                            <#if renderDetailTd>
                                                <#if context.showDetail != "N">
                                                    <td rowspan="${rowspanParty}" >${workEffortTransactionIndicatorView.entryPartyName?if_exists}</td>
                                                </#if>
                                            </#if>
                                            
                                            <#-- COLONNA CON GL_FISCAL_TYPE (OPT) -->
                                            <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glFiscalTypeColumn.ftl" />
                                            <#-- COLONNA O COLONNE PERIODI -->
                                            <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/periodColumn.ftl" />
                                            <#assign index = index+1>
                                            <#assign periodIndex = periodIndex+1>
                                        </#if>
                                    </#list>
                                    <#assign renderOtherTd = true>
                               </#if>
                               
                           </#list>
                           <#assign renderDetailTd = true>
                           </tr>
                       </#list>
                       <#assign indexClass = indexClass+1>
                       
                    </#list>
                    
                    <#if "Y" == insertMode>
                        <tr <#if index%2 != 0>class="alternate-row new-row"
                            <#else>class="new-row"
                            </#if>>
                            
                            <#-- COLONNA CON SEQUENCE_ID -->
                            <#if context.showSequenceId == "Y">
                                <td style="width: 6em;">
                                    <input class="numericInList" type="text" maxlength="15" size="3" value="1" name="sequenceId_o_${index}">
                                </td>
                            </#if>                            
                            
                            <#if context.showComments == "LEFT">
                                <td><input size="20" name="comments_o_${index}"/></td>
                            </#if> 
                            
                            <#if context.showType == "SX">
                                <td style="width: 10em;">
                                    <input readonly="readonly" type="text" name="gltDescr_o_${index}">
                                </td>                            
                            </#if>   
                             
                            <#if context.showAccountReference == "Y">
                                <#assign renderNewRow = "Y">
	                            <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/accountReferenceColumn.ftl" />
	                        </#if>                         
                            
                            <#-- COLONNA CON INDICATORE -->
                            <#assign renderNewRow = "Y">                           
                            <#if context.showAccountReference == "Y">
		                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glAccountColumnNoMandatory.ftl" />
		                    <#else>
		                        <#include  "/workeffortext/webapp/workeffortext/ftl/workEffortTransactionStandard_column/glAccountColumn.ftl" />
		                    </#if>
                            
                            <#if context.showType == "Y">
                                <td style="width: 10em;">
                                    <input readonly="readonly" type="text" name="gltDescr_o_${index}">
                                </td>                            
                            </#if>
                            
                            <#if context.showDirection == "Y">
                                <td style="width: 10em;">
                                    <input readonly="readonly" type="text" name="dcDescr_o_${index}">
                                </td>                            
                            </#if>
                            
                            <#-- COLONNA CON KPI_WEIGHT (OPT) -->
                            <#if context.showKpiWeight == "Y">
                                <td style="width: 6em;">
                                    <input decimal_digits="0" class="numericInList" type="text" maxlength="15" size="3" value="100" name="kpiScoreWeight_o_${index}">
                                </td>
                            </#if>
                            
                            <#-- COLONNA CON KPI_OTHER_WEIGHT (OPT) -->
                            <#if context.showKpiOtherWeight == "Y">
                                <td style="width: 6em;">
                                    <input decimal_digits="0" class="numericInList" type="text" maxlength="15" size="3" value="100" name="kpiOtherWeight_o_${index}">
                                </td>
                            </#if>
                            
                            <#-- COLONNA CON ABBREVIATION -->
                            <#if context.showUom == "Y">
                                <td style="width: 8em;">
                                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                                        <div class="abbreviationLang"></div>
                                    <#else>
                                        <div class="abbreviation"></div>
                                    </#if>
                                </td>
                            </#if>
                            
                            <#if context.showUomDescr == 'Y'>
                                <#if multiTypeLang?has_content && multiTypeLang?if_exists != "NONE">
                                    <td>
                                       <#if context.showAccountReference == "Y">
                                           <input name="uomDescr_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_uomDescr_o_${index}" type="text" class="accountName">
                                       <#else>
                                           <input name="uomDescr_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_uomDescr_o_${index}" type="text">
                                        </#if>
                                    </td>
                                    <td>
                                       <#if context.showAccountReference == "Y">
                                           <input name="uomDescrLang_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_uomDescrLang_o_${index}" type="text" class="accountNameLang">
                                       <#else>
                                           <input name="uomDescrLang_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_uomDescrLang_o_${index}" type="text">
                                        </#if>
                                    </td>
                                <#else> 
                                	<td>
                                    	<#if context.showAccountReference == "Y">
                                        	<input name="uomDescr_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_uomDescr_o_${index}" type="text" class="accountName">
                                    	<#else>
                                        	<input name="uomDescr_o_${index}" size="20" maxlength="255" id="WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_uomDescr_o_${index}" type="text">
                                    	</#if>
                                	</td>                                                                   
                                </#if>
                            </#if>
                            
                            <#if context.showComments == "RIGHT" || context.showComments == "Y">
                                <td><input size="20" name="comments_o_${index}"/></td>
                            </#if>
                            
                            <#if context.showPeriods == "OPEN" || context.showPeriods == "NONE">
                                <#if context.glFiscalTypeId == "ALL">    
                                    <td colspan="2"><div/></td>
                                <#else>
                                    <td><div/></td>
                                </#if>
                            <#else>
                                <#assign size = customTimePeriodList?size />
                                <#if context.glFiscalTypeId == "ALL">    
                                    <#assign size = customTimePeriodList?size + 1 />
                                </#if>
                                <td colspan="${size}"><div/></td>
                            </#if>
                        </tr>
                    </#if>
		        </tbody>
		    </table>
		</div>
		
		<input class="management-reset-button ignore_check_modification" type="submit" value="Reset Button" name="resetButton" style="display: none;">
		<input class="save-button ignore_check_modification" type="submit" value="Invia" name="submitButton" style="display: none;">
		<input class="management-delete-button ignore_check_modification" type="submit" value="Cancella" name="deleteButton" style="display: none;">    
	</form>
<br>
<br>
<br>
</#if>