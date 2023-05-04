<script type="text/javascript">
	refreshForm = function (isNext) {
        var form = $("WETVRD002${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}");
        var onclickStr = form.readAttribute("onSubmit");
        var attributes =onclickStr.split(","); 
        var request = attributes[3];
        var container = attributes[2].substring(attributes[2].indexOf('\'')+1);
        
        var parametersMap = $H(attributes[4].substring(0, attributes[4].lastIndexOf('\'')).toQueryParams());
        var scrollInt = parametersMap.get("scrollInt");
        if (isNext) {
            parametersMap.set("scrollInt", new Number(scrollInt) + 1);
        } else {
            parametersMap.set("scrollInt", new Number(scrollInt) - 1);
        }
        
        ajaxUpdateAreas(container+',' + request + ',' + parametersMap.toQueryString());
    }
    
    checkRadioButton = function (element) {
    	kpiScoreWeight = null;
       	element = $(element);
       	valueOld = 0;
       	valueNew = 0;
       	if(element.readAttribute('readonly')) {
       		element.checked = !element.checked;
       		return;
       	}
       	tr = element.up("tr");
       	valueNew = element.getValue();
       	if(!element.checked) {
       		valueNew = 0;
       		valueOld = element.value;
       	}
       	tr.select("input[type=checkbox]").each(function(input) {
       		if (input.checked && input != element) {
       			input.checked = false;
       			valueOld = input.value;
       		}
       	});
       	
       	selectedTd = element.up('td');
		tr.select("input").each(function(input) {
       		if(input.readAttribute("name").indexOf('kpiScoreForValueWeightHidden') > -1 ) {
       			kpiScoreWeightTotOutputHidden = input;
       			$break;
       		} else if(input.readAttribute("name").indexOf('kpiScoreForValueWeight') > -1 ) {
       			kpiScoreWeightTotOutput = input;
       			kpiScoreWeightTotOutput.setValue("");
       			$break;
       		}
       		
       		if(input.readAttribute("name").indexOf('kpiScoreWeight') > -1 ) {
       			kpiScoreWeight = input;
       			$break;
       		}
       		
        });
       	
       	totaliOutput = $$('.totaliOutput');
       	totaliOutput.each(function(output) {
	       	if(output.readAttribute("name").indexOf('totalKpiScoreForValueWeightHidden') > -1 ) {
	       		totalKpiScoreForValueWeightHidden = output;
	   			$break;
	   		} else if(output.readAttribute("name").indexOf('totalKpiScoreForValueWeight') > -1 ) {
	   			totalKpiScoreForValueWeight = output;
	   			totalKpiScoreForValueWeight.setValue("");
	   			$break;
	   		}
       	});
       	
       	if (Object.isElement(kpiScoreWeight)) {
       		new Ajax.Request("<@ofbizUrl>calculateKpiScoreTot</@ofbizUrl>", {
	            parameters: {
	                "valueNew": valueNew,
	                "kpiScoreWeight": kpiScoreWeight.getValue(),
	                "valueTotOld": totalKpiScoreForValueWeightHidden.getValue(),
	                "valueOld": valueOld
	                
	            }, 
	        	onSuccess: function(response){
	           		var data = response.responseText.evalJSON(true);
	           		kpiScoreWeightTotOutput.setValue(data.valueForKpiNew);
	           		kpiScoreWeightTotOutputHidden.setValue(data.valueForKpiNew);
	           		
	           		totalKpiScoreForValueWeight.setValue(data.valueTotNew);
	           		totalKpiScoreForValueWeightHidden.setValue(data.valueTotNew);
	            }
	        });
       	}
    }
    
    if ("${parameters.errorLoadTrans?if_exists}" != "" &&  "${parameters.errorLoadTrans?if_exists}" != null) {
		var data = $H({});
		data["_ERROR_MESSAGE_"] = "${parameters.errorLoadTransDescr?if_exists}"; 
		modal_box_messages.onAjaxLoad(data, null);
		modal_box_messages.alert("${parameters.errorLoadTrans?if_exists}");
	}		
</script>

<#if uomRatingScaleList?has_content && !parameters.errorLoadTrans?has_content>
	<form id="WETVRD002${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}" class="basic-form cachable" name="WorkEffortTransactionViewRadioButtonManagementMultiForm" method="post" action="<@ofbizUrl>elaborateMultiFormForUpdateAjax</@ofbizUrl>" 
	onsubmit="javascript:ajaxSubmitFormUpdateAreas('WETVRD002${accountTypeEnumId}_WorkEffortTransactionView-${context.relationTitle}','','child-management-screenlet-container-WorkEffortTransactionView-${context.relationTitle},<@ofbizUrl>childManagementListContainerOnly</@ofbizUrl>,searchDate=${parameters.searchDate?if_exists}&contextManagement=N&forcedBackAreaId=&folderIndex=${context.folderIndex}&screenNameListIndex=${context.screenNameListIndex}&backAreaId=&saveView=N&relationTitle=${context.relationTitle}&successCode=&entityName=WorkEffortTransactionView&parentEntityName=WorkEffortView&managementFormType=multi&workEffortId=${context.weTransWeId}&weTransWeId=${context.weTransWeId}&contentIdInd=${context.contentIdInd}&contentId=${context.contentIdInd}&contentIdSecondary=${context.contentIdSecondary}&wizard=N&subFolder=Y&scrollInt=${context.scrollInt}'); return false;">
		<div>
	
		<div id="WorkEffortTransactionViewRadioButtonScreen" class="tableContainer" style="height: auto;">
			<table id="table_TRANSRADIO_WorkEffortTransactionIndicatorView-${context.relationTitle}" class="basic-table list-table padded-row-table hover-bar draggable toggleable selectable customizable multi-editable resizable headerFixable noTableResizeHeight" cellspacing="0" cellpadding="0">
				<thead>
		            <tr class="header-row-2">
		                <#if context.showType == "SX">
	                        <th style="width: 10em;">${uiLabelMap.WorkEffortTypology}</th>
	                    </#if>
		                <th class="${glAccountIdTitleAreaClass}"><div>${glAccountIdTitleValue}</div></th>
		                <#if context.showType == "Y">
		                    <th style="width: 10em;">${uiLabelMap.WorkEffortTypology}</th>
		                </#if>
		                <#if context.showKpiScore == "Y" && context.showKpiTotal == "Y">
                        	<th><div>${uiLabelMap.FormFieldTitle_kpiScoreWeight}</div> </th>                        	
                        </#if>
                        <#list uomRatingScaleList as uomRatingScale>
                            <th class="${uomRatingScaleTitleAreaClass}">
                                <div>
                                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                                        ${uomRatingScale.titleLang?if_exists}
                                    <#else>
                                        ${uomRatingScale.title}
                                    </#if>
                                </div>
                            </th>
                        </#list>
		                <#if context.showKpiTotal == "Y">
		                	<th>${uiLabelMap.TotalColumn}</th>  
		                </#if>
	                </tr>
		        </thead>
		        <tbody class="valIndicatore">
                    <#assign index=0/>
                    <#assign kpiScoreForWeTransValueTot=0/>
                    <#assign workEffortTransactionIndicatorViewTotal=null />
                    <#list workEffortTransactionIndicatorViewList?if_exists as measureMap>
            		    <#if "TOTAL" == measureMap.weTransMeasureId>
	                		<#assign measureMapTotal = measureMap />
                		<#else>
                			<#assign partyList = measureMap.rowList />
		            		<#list partyList?if_exists as mappaParty>
                        		<#assign typeList = mappaParty.rowList />
                        		<#list typeList?if_exists as mappaType>
                        		    <#if mappaType.weTransTypeValueId?has_content>
                        		        <#assign weTransTypeValueId = mappaType.weTransTypeValueId?if_exists />
                        		    </#if>
                                	<#assign rowList = mappaType.rowList />
                                	<#assign rowListSize = rowList?size>
                            		<#assign periodNonehasTrans = false>
                                    <#list rowList?if_exists as  workEffortTransactionIndicatorView>
                                
                                        <#assign kpiScoreWeightD = Static["java.lang.Double"].parseDouble("0") />
            	                		<#assign weTransValueD = Static["java.lang.Double"].parseDouble("0") />
            				        	<#-- la riga e readOnly per diversi motivi: -->  
            	                		<#assign valModId = workEffortTransactionIndicatorView.glValModId?if_exists?default("") >
            				        	<#if (workEffortTransactionIndicatorView.inputEnumId?if_exists == "ACCINP_PRD") >
            				        		<#assign valModId = workEffortTransactionIndicatorView.wmValModId?if_exists >
            				            </#if> 
            				            <#assign isReadOnlyRow = false />
                                        <#if "Y" == parameters.rootInqyTree?if_exists?default('N') || "Y" == workEffortTransactionIndicatorView.isPosted?if_exists >
                                            <#assign isReadOnlyRow = true />
                                        <#elseif  !checkWorkEffortPermissions >
                                            <#assign isReadOnlyRow = true />
                                        <#else>
                                            <#if  security.hasPermission(adminPermission, context.userLogin) >
                                                <#assign isReadOnlyRow = false />
                                            <#else>
                                                <#assign isReadOnlyRow = (!isRil || crudEnumIdSecondary?if_exists == "NONE" || valModId == "ALL_NOT_MOD"
                                                    || (valModId == "ACTUAL_NOT_MOD" && workEffortTransactionIndicatorView.weTransTypeValueId?if_exists == "ACTUAL")
                                                    || (valModId == "BUDGET_NOT_MOD" && workEffortTransactionIndicatorView.weTransTypeValueId?if_exists == "BUDGET")
                                                    || workEffortTransactionIndicatorView.isReadOnly?if_exists || "Y" == parameters.rootInqyTree?if_exists?default('N'))/>
                                            </#if> 
                                        </#if> 
                                        
                                        
                                        <#if context.showPeriods != "NONE" || (context.showPeriods == "NONE" && (workEffortTransactionIndicatorView.weTransDate?has_content || (!periodNonehasTrans && workEffortTransactionIndicatorView_index == (rowListSize - 1))))>
	        								<#assign periodNonehasTrans = true>
	    
	                                        <tr <#if index%2 != 0>class="alternate-row"</#if>>
	            			              		<#if context.showType == "SX">
	                                                <td>
	                                                    <div>${workEffortTransactionIndicatorView.gltDescr?if_exists}</div>
	                                                </td>   
	                                            </#if>
	            			              		
	            			              		<#if context.showTooltip == "Y">
	            			              			<#assign workEffortMeasRatScList = delegator.findByAndCache("WorkEffortMeasRatSc",Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortMeasureId", workEffortTransactionIndicatorView.weTransMeasureId, "uomId", context.uomId))>
	            			              			<#assign descriptionRatSc = "" />
	            	                        		
	            			              			<#list workEffortMeasRatScList as workEffortMeasRatSc>
	            					              		<#list uomRatingScaleList as uomRatingScale>
	            					              			<#assign valoreDouble = Static["java.lang.Double"].parseDouble(uomRatingScale.id?string) />
	            					              			<#if workEffortMeasRatSc.uomRatingValue == valoreDouble>
	            					              				<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	            					              					<#assign descriptionRatSc =  descriptionRatSc + uomRatingScale.titleLang + ":\n" />
	            					              					<#assign descriptionRatSc =  descriptionRatSc + workEffortMeasRatSc.uomDescrLang />					              				
	            					              				<#else>
	            					              					<#assign descriptionRatSc =  descriptionRatSc + uomRatingScale.title + ":\n" />
	            					              					<#assign descriptionRatSc =  descriptionRatSc + workEffortMeasRatSc.uomDescr />
	            					              				</#if>
	            					              				<#assign descriptionRatSc =  descriptionRatSc + "\n" />
	            				                    		</#if>
	            			              				</#list>
	            			                    	</#list>
	            			              		</#if>
	            			              		<td style="width: 400px;" title="${descriptionRatSc?if_exists}">
	            			              			<input type="hidden" value="WorkEffortTransactionIndicatorView" name="entityName_o_${index}" class="ignore_check_modification">
	            			              			<input type="hidden" value="UPDATE" name="operation_o_${index}">
	            									<input type="hidden" value="${context.folderIndex}" name="folderIndex_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" name="backAreaId_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" name="forcedBackAreaId_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" name="successCode_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" value="WorkEffortTransactionIndicatorView" name="operationalEntityName_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" value="N" name="saveView_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" value="N" name="contextManagement_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" name="subFolder_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" value="Y" name="_rowSubmit_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" value="N" name="insertMode_o_${index}" class="ignore_check_modification">
	            									
	            									<#assign workEffort = delegator.findOne("WorkEffort", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", workEffortTransactionIndicatorView.weTransWeId?if_exists), false)>
	            			                        <input type="hidden" value="${workEffort.workEffortTypeId}" name="workEffortTypeId_o_${index}" class="ignore_check_modification">
	            									<input type="hidden" value="FOLDER" name="weTypeContentTypeId_o_${index}" class="ignore_check_modification">
	            									
	            									<input type="hidden" name="weTransAccountId_o_${index}" value="${workEffortTransactionIndicatorView.weTransAccountId}" class="ignore_check_modification">
	            									<input type="hidden" name="weTransTypeValueId_o_${index}" value="${workEffortTransactionIndicatorView.weTransTypeValueId?if_exists}" class="ignore_check_modification">
	            									<input type="hidden" name="weTransDate_o_${index}" value="${workEffortTransactionIndicatorView.weTransDate?if_exists}" class="ignore_check_modification">
	                                                <input type="hidden" name="customTimePeriodId_o_${index}" value="${workEffortTransactionIndicatorView.customTimePeriodId?if_exists}" class="ignore_check_modification">
	            									<input type="hidden" name="weTransCurrencyUomId_o_${index}" value="${workEffortTransactionIndicatorView.weTransCurrencyUomId?if_exists}" class="ignore_check_modification">
	            									<input type="hidden" name="weTransWeId_o_${index}" value="${workEffortTransactionIndicatorView.weTransWeId?if_exists}" class="ignore_check_modification">
	            									<input type="hidden" name="weTransMeasureId_o_${index}" value="${workEffortTransactionIndicatorView.weTransMeasureId?if_exists}" class="ignore_check_modification"/>
	            									<input type="hidden" name="weTransUomType_o_${index}" value="${workEffortTransactionIndicatorView.weTransUomType?if_exists}" class="ignore_check_modification"/>
	            									<input type="hidden" name="defaultOrganizationPartyId_o_${index}" value="${defaultOrganizationPartyId?if_exists}" class="ignore_check_modification"/>
	            			                        
	            									<input type="hidden" name="modello_o_${index}" value="RADIO_BUTTON"/>
	            			                        
	            									<input type="hidden" value="crudServiceDefaultOrchestration_WorkEffortTransactionView_Simplified" name="crudService_o_${index}">
	            									
	            									<#if context.elabScoreIndic?has_content && context.elabScoreIndic?if_exists?default('N') != 'N'>
		                                               <input type="hidden" name="elabScoreIndic_o_${index}" value="${context.elabScoreIndic?if_exists}" class="submit-field"/>
		                                               <input type="hidden" name="crudServiceEpilog_o_${index}" value="crudServiceEpilog_elaboreteScoreIndic" class="submit-field"/>
		                                               <input type="hidden" name="openNewTransaction_o_${index}" value="N" class="submit-field"/>
		                                               <input type="hidden" name="searchDate_o_${index}" value="${context.searchDateCalculate?if_exists}" class="submit-field"/>     
		                                            </#if>
	            									
	            									<#assign workEffortMeasureProductList = delegator.findByAnd("WorkEffortMeasureProduct",Static["org.ofbiz.base.util.UtilMisc"].toMap("glAccountId", workEffortTransactionIndicatorView.weTransAccountId, "productId", workEffortTransactionIndicatorView.transProductId?if_exists?default("")))>
	            									<#if workEffortMeasureProductList?has_content>
	            							        	<#assign workEffortMeasureProduct = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workEffortMeasureProductList) />
	            							        	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	            							        		<#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortMeasureProduct.uomDescrLang) />
	            							        	<#else>
	            							        		<#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortMeasureProduct.uomDescr) />
	            							        	</#if>
	            								    <#else>
	            								    	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	            								    		<#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortTransactionIndicatorView.weTransAccountDescLang?if_exists?default("")) />
	            								    	<#else>
	            											<#assign description = Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortTransactionIndicatorView.weTransAccountDesc?if_exists?default("")) />
	            										</#if>
	            							        </#if>
	            							        <#if workEffortTransactionIndicatorView.weTransUomDesc?has_content>
	            							        	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	            							        		<#assign description = description + " - " + Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortTransactionIndicatorView.weTransUomDescLang?if_exists?default(""))>
	            							        	<#else>
	            			                            	<#assign description = description + " - " + Static["org.ofbiz.base.util.StringUtil"].wrapString(workEffortTransactionIndicatorView.weTransUomDesc?if_exists?default(""))>
	            			                            </#if>
	            			                        </#if>
	            	                        
	            			                        <div>${description}</div>
	            			                    </td>
	            			                    <#if context.showType == "Y">
	                                                <td>
	                                                    <div>${workEffortTransactionIndicatorView.gltDescr?if_exists}</div>
	                                                </td>   
	                                            </#if>
	                                            <#if context.showKpiScore == "Y" && context.showKpiTotal == "Y">
	            			                        <td>
	            			                        	<#if workEffortTransactionIndicatorView.kpiScoreWeight?if_exists?has_content>
	            			                        		<#assign kpiScoreWeightS = Static["java.lang.String"].valueOf(workEffortTransactionIndicatorView.kpiScoreWeight?c) />
	            				                        	<#assign kpiScoreWeightD = Static["java.lang.Double"].parseDouble(kpiScoreWeightS) />
	            				                        </#if>
	            			                        	<input readonly="readonly" class="numericInList" type="text" maxlength="15" size="3" value="${workEffortTransactionIndicatorView.kpiScoreWeight?if_exists}" name="kpiScoreWeight_o_${index}">
	            			                        </td>
	            			                    </#if>
	            			                    <#list uomRatingScaleList as item>
	            			                    	<#assign valoreDouble = Static["java.lang.Double"].parseDouble(item.id?string) />
	            	                        		<#if context.showTooltip == "Y">
	            				                    	<#list workEffortMeasRatScList as workEffortMeasRatSc>
	            				                    		<#if workEffortMeasRatSc.uomRatingValue == valoreDouble>
	            				                    			<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	            				                    				<#assign descriptionRatSc =  workEffortMeasRatSc.uomDescrLang />
	            				                    			<#else>
	            				                    				<#assign descriptionRatSc =  workEffortMeasRatSc.uomDescr />
	            				                    			</#if>
	            				                    		</#if>
	            				                    	</#list>
	            				                    </#if>
	            			                    	<td title="${descriptionRatSc?if_exists}">
	            			                        	<#assign valoreStr = Static["java.lang.String"].valueOf(item.id?string) />
	            			                        	
	            			                        	<input <#if isReadOnlyRow > readonly="readonly"</#if> name="weTransValue_${item.id}_o_${index}" value="${item.valore}" 
	            			                       			<#if workEffortTransactionIndicatorView.weTransValue?if_exists?has_content && Static["java.lang.Double"].toString(workEffortTransactionIndicatorView.weTransValue) == Static["java.lang.Double"].toString(item.valore) > checked='true' <#else></#if>
	            			                       		 type="checkbox" onclick="javascript: checkRadioButton(this);" />
	            			                        </td>
	            			                   </#list>
	            			                   <#if context.showKpiTotal == "Y">
	            			                        <td>
	            			                    		<#if workEffortTransactionIndicatorView.weTransValue?if_exists?has_content>
	            			                        		<#assign weTransValueS = Static["java.lang.String"].valueOf(workEffortTransactionIndicatorView.weTransValue?c) />
	            				                        	<#assign weTransValueD = Static["java.lang.Double"].parseDouble(weTransValueS) />
	            				                        </#if>
	            			                        	<#assign kpiScoreForWeTransValue = kpiScoreWeightD * weTransValueD / 100.0 />
	            			                        	<#assign kpiScoreForWeTransValueTot = kpiScoreForWeTransValueTot + kpiScoreForWeTransValue />
	            			                        	<input type="hidden" class="numericInList input_mask mask_double" maxlength="15" size="3" value="${kpiScoreForWeTransValue?if_exists}" name="kpiScoreForValueWeightHidden_o_${index}" decimal_digits="${scoreDecimalScale?if_exists}">
	            			                        	<input readonly="readonly" type="text" class="numericInList input_mask mask_double" maxlength="15" size="3" value="${kpiScoreForWeTransValue?if_exists}" name="kpiScoreForValueWeight_o_${index}" decimal_digits="${scoreDecimalScale?if_exists}">
	            			                        </td>
	            			                    </#if>
	            			                </tr>  
	            			                <#assign index = index+1>
            			                </#if>
    			                </#list>
    			                </#list>
    			                </#list>
    			            </#if>
			        
    			    </#list>
		            <#if ("Y" == context.showKpiTotal && measureMapTotal?has_content)>
		                <#assign ad = measureMapTotal.rowList />
                        <#list ad?if_exists as  ll>
                        <#assign rowList = ll.rowList />
                        <#list rowList?if_exists as  workEffortTransactionIndicatorViewTotal>
		                <tr class="">
			                <#if context.showType == "SX">
                                <td>
                                    <div>&nbsp;</div>
                                </td>   
                            </#if>
                            <td style="width: 400px; font-weight: bold">
			                	<input type="hidden" disabled="disabled" value="N" name="_rowSubmit_o_${index}" class="ignore_check_modification"/>
			                	<input type="hidden" disabled="disabled" value="UPDATE" name="operation_o_${index}" class="ignore_check_modification">
			                	<div>${uiLabelMap.TotalsColumn}</div>
			                </td>
			                <#if context.showType == "Y">
                                <td>
                                    <div>&nbsp;</div>
                                </td>   
                            </#if>
                            <#if context.showKpiScore == "Y">
	                        	<td class="numericInList">
	                        	 	<input type="hidden" disabled="disabled" maxlength="15" size="3" value='${workEffortTransactionIndicatorViewTotal.kpiScoreWeight?if_exists}' name="kpiScoreWeightHidden_o_${index}" class="numericInList">
	                        		<input style="font-weight: bold" disabled="disabled" readonly="readonly" type="text" maxlength="15" size="3" value="${workEffortTransactionIndicatorViewTotal.kpiScoreWeight?if_exists}" name="kpiScoreWeight_o_${index}" class="numericInList">
	                           	</td>
	                        </#if>
			                <#list uomRatingScaleList as uomRatingScale>
			                    <td></td>
			                </#list>
			                <td style="font-weight: bold" class="numericInList">
			                	<input class="totaliOutput numericInList input_mask mask_double" disabled="disabled" type="hidden" maxlength="15" size="3" value="${kpiScoreForWeTransValueTot}" name="totalKpiScoreForValueWeightHidden_o_${index}" decimal_digits="${scoreDecimalScale?if_exists}">
			            		<input style="font-weight: bold" readonly="readonly" disabled="disabled" class="totaliOutput numericInList input_mask mask_double" type="text" maxlength="15" size="3" value="${kpiScoreForWeTransValueTot}" name="totalKpiScoreForValueWeight_o_${index}" decimal_digits="${scoreDecimalScale?if_exists}">
                        	</td>  
		                </tr>
		                </#list>
		                </#list>
		            </#if>
		        </tbody>
		    </table>
		</div>
		
		<input class="management-reset-button" type="submit" value="Reset Button" name="resetButton" style="display: none;">
		<input class="save-button" type="submit" value="Invia" name="submitButton" style="display: none;">
		<input class="management-delete-button" type="submit" value="Cancella" name="deleteButton" style="display: none;">    
	</form>

    <br>
    <br>
    <br>
</#if>
