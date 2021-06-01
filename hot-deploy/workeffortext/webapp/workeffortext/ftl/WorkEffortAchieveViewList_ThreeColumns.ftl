<#import "/base/webapp/common/ftl/gzoomMacro.ftl"  as gzoom/>

<script type="text/javascript">
    var WorkEffortAchieveViewList_ThreeColumns = {
        cellOpenManagement : function(e) {
            var cellSelected = Event.element(e).up('td');
            var table = cellSelected.up('table');
            if (Object.isElement(cellSelected)) {
                var populateElementCollection = null;
                var checkExecutability = null;
                var managementSelectedElementItem = Toolbar.getInstance()._getItemBySelector('.management-selected-element');
                if (managementSelectedElementItem) {
                    populateElementCollection = managementSelectedElementItem.populateElementCollection;
                    managementSelectedElementItem.populateElementCollection = WorkEffortAchieveViewList_ThreeColumns.selectedElementPopulateCollection.curry(cellSelected);
                    
                    checkExecutability = managementSelectedElementItem.checkExecutability;
                    managementSelectedElementItem.checkExecutability = WorkEffortAchieveViewList_ThreeColumns.checkExecutability.curry(cellSelected);
                }
            
                var operationField = cellSelected.select('input').find(function(element) {
                    return element.readAttribute('name').startsWith('operation');
                });
                if (!Object.isElement(operationField) || (Object.isElement(operationField) && operationField.getValue() !== 'CREATE')) {
                    var content = table.up("div.data-management-container");
                    if (!Object.isElement(content))
                        content = table.up("div#searchListContainer");      
                    var item = Toolbar.getInstance(content.identify()).getItem(".management-selected-element");
                    if (!Object.isElement(item))
                        item = Toolbar.getInstance(content.identify()).getItem(".search-selected-element");
                    if (Object.isElement(item)) 
                        item.fire('dom:click');
                }
                
                if (managementSelectedElementItem) {
                    managementSelectedElementItem.populateElementCollection = populateElementCollection;
                    managementSelectedElementItem.checkExecutability = checkExecutability;
                }
            }
        },
        
        checkExecutability : function(cell, callBack, container) {
            var res = Object.isElement(cell);

            if (!res) {
                modal_box_messages.alert(['BaseMessageNoSelection']);
            } else {
                callBack();
                throw $break;
            }

            return res;
        },
        
        selectedElementPopulateCollection : function(cellSelected, form, container) {
            if (cellSelected) {
                $A(cellSelected.select('input', 'select')).each(function(element) {
                    var elementName = element.readAttribute("name");
                    if(elementName.indexOf('_o_') > -1) {
                        elementName = elementName.substring(0,elementName.indexOf('_o_'));
                    }
                    var currentValue = element.getValue();
                    if (currentValue) {
                        var findElement = $A(form.getElements()).find(function(elm) {
                            return elementName === elm.readAttribute("name");
                        });
                        if (findElement && !findElement.hasClassName('url-params')) {
                            var valueHidden = findElement.readAttribute('value');
                            if (valueHidden) {
                                if (valueHidden.indexOf(currentValue) == -1) {
                                    if (valueHidden.indexOf(']') != -1) {
                                        valueHidden = valueHidden.replace(']', '|]');
                                    } else {
                                        valueHidden = valueHidden.concat('|]');
                                    }

                                    valueHidden = valueHidden.replace(']', currentValue+ ']');
                                    if (valueHidden.indexOf('[') == -1)
                                        valueHidden = '['.concat(valueHidden);
                                }
                            } else {
                                valueHidden = currentValue;
                            }

                            findElement.writeAttribute('value', valueHidden);
                        } else if (!findElement || (findElement && !findElement.hasClassName('url-params'))) {


                            var inputHidden = new Element('input', {'type' : 'hidden', 'value' : currentValue, 'name' : elementName, 'id' : elementName});
                            form.insert(inputHidden);
                        }
                    }
                });
            }
        },
        
        load : function() {
            var selectableTable = $('ThreeCol_Table');
            
            selectableTable.select('td').each(function(cell) {
                Event.stopObserving(cell, 'dblclick');
                Event.observe(cell, 'dblclick', WorkEffortAchieveViewList_ThreeColumns.cellOpenManagement);
            });
            
            // Sandro: ho cambiato il target del tooltip per evitare la sovrapposizione tra tooltip del div e tooltip immagine 
            var selectors = ["div.tooltipDescription"];
            populateTooltip(selectors);
        }
    }
    
    WorkEffortAchieveViewList_ThreeColumns.load();
</script>

<#assign modulo = "emplperf"/>
<#assign form = "EmplPerfRootViewForms"/>
<#assign screenName = "EmplPerfScreens.xml"/>
<#if weContext?if_exists == "CTX_BS">
    <#assign modulo = "stratperf"/>
    <#assign form = "StratPerfRootViewForms"/>
    <#assign screenName = "StratPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_OR">
    <#assign modulo = "orgperf"/>
    <#assign form = "OrgPerfRootViewForms"/>
    <#assign screenName = "OrgPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_CO">
    <#assign modulo = "corperf"/>
    <#assign form = "CorPerfRootViewForms"/>
    <#assign screenName = "CorPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_PR">
    <#assign modulo = "procperf"/>
    <#assign form = "ProcPerfRootViewForms"/>
    <#assign screenName = "ProcPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_CG">
    <#assign modulo = "cdgperf"/>
    <#assign form = "CdgPerfRootViewForms"/>
    <#assign screenName = "CdgPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_TR">
    <#assign modulo = "trasperf"/>
    <#assign form = "TrasPerfRootViewForms"/>
    <#assign screenName = "TrasPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_RE">
    <#assign modulo = "rendperf"/>
    <#assign form = "RendPerfRootViewForms"/>
    <#assign screenName = "RendPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_GD">
    <#assign modulo = "gdprperf"/>
    <#assign form = "GdprPerfRootViewForms"/>
    <#assign screenName = "GdprPerfScreens.xml"/>
<#elseif  weContext?if_exists == "CTX_PA">
    <#assign modulo = "partperf"/>
    <#assign form = "PartPerfRootViewForms"/>
    <#assign screenName = "PartPerfScreens.xml"/>  
<#elseif  weContext?if_exists == "CTX_DI">
    <#assign modulo = "dirigperf"/>
    <#assign form = "DirigPerfRootViewForms"/>
    <#assign screenName = "DirigPerfScreens.xml"/>      
</#if>

<div class="header-breadcrumbs">
    <div id="header-breadcrumbs-th"><@gzoom.breadcrumb parameters.breadcrumbsCurrentItem?if_exists "common-container"/></div>
    <#assign activeTabIndex = null>
    <#if activeTabIndex?has_content>
        <#if !weChildren?has_content && (parameters.workEffortTypeId?has_content || parameters.weTypeId?has_content)>
            <#assign activeTabIndex = 1>
            <input type="hidden" id="activeTabIndex" name="activeTabIndex" value="management_${activeTabIndex}" />
        </#if>
    </#if>
</div>
<#assign singleHeaderTitle = false>
<#if workEffortAchieveList?has_content>
    <#assign firstItem = workEffortAchieveList[0]>
    <#if !firstItem.budgetValue?has_content>
        <#assign singleHeaderTitle = true>
    </#if>
</#if>
<table cellspacing="15px" style="width: 100%" class="selectable dblclick-open-management" id="ThreeCol_Table">
    <tbody>
        <#assign index = 0>
        <#list workEffortAchieveList as item>
        
        	<#assign workEffortContentList = delegator.findByAndCache("WorkEffortContentAndInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", item.workEffortId, "workEffortContentTypeId", "TWOCOLINQIRY"), Static["org.ofbiz.base.util.UtilMisc"].toList("-fromDate"))/>
        	
        	<#if workEffortContentList?has_content>
        		<#assign backGroundContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workEffortContentList)/>
        		<#-- ToDo 3894 -->
        		<#if Static["org.ofbiz.base.util.UtilValidate"].isEmpty(parameters.userLoginId) || "_NA_" != parameters.userLoginId>
        			<#assign imgRequest = "img"/>
        			<#else>
        				<#assign imgRequest = "imgNotAuth"/>
        		</#if>
        		<#else>
        			<#assign backGroundContent = ""/>
        	</#if>		
        	
            <#if (index % 3) == 0>
                <tr>
            </#if>
                <td style="width: 30%; font-weight: bold; height: 30px; font-size: 1.1em; text-align: center;">
                
                <div id="value_${item.workEffortId}" class="mblContainer mblDisplayStringCell" style="color: red; font-weight: bold; height: 22px; font-size: 1.1em;">
                	<span>
                	<#if "Y" == showOrgUnit?if_exists>
                    	${item.weOrgPartyDescr?if_exists}
                    <#else>
						${item.workEffortName?if_exists}
					</#if>
                	</span>
                    <div class="mblParameter" name="entityName" value="WorkEffortAchieveChildView"></div>
                    <div class="mblParameter" name="parentEntityName" value="WorkEffortAchieveView"></div>
                    <div class="mblParameter" name="workEffortIdFrom" value="${item.workEffortId?if_exists}"></div>
                    <div class="mblParameter" name="noConditionFind" value="Y"></div>
                    <div class="mblParameter" name="saveView" value="N"></div>
                    <div class="mblParameter" name="childManagement" value="Y"></div>
                    <div class="mblParameter" name="sortField" value="sequenceNum|sourceReferenceId|workEffortId"></div>
                </div>
                            
                <#if !backGroundContent?has_content>
	                <b class="rtop">
					<b class="r1"></b>
					<b class="r2"></b>
					<b class="r3"></b>
					<b class="r4"></b>
					</b>
				</#if>
                            
                    <div <#if backGroundContent?has_content>class="resAnalysisWidgetImage mblShowDescription" style="color: white; background-image: url(<@ofbizUrl>${imgRequest}?saveView=N&imgId=${backGroundContent.dataResourceId?if_exists}</@ofbizUrl>);"<#else>class="resAnalysisWidgetBorder mblShowDescription" </#if>>
                        <div style="text-align: center; vertical-align: middle;" class="<#if showTooltip?if_exists>tooltipDescription</#if>" <#if item.description?has_content> description="${item.description}"</#if>>
                            <input type="hidden" name="entityPkFields_o_${index}" value="workEffortId|workEffortTypeId|workEffortIdFrom"/>
                            <input type="hidden" name="userLoginId_o_${index}" value="${(userLogin.userLoginId)?if_exists}"/>
                            <input type="hidden" name="workEffortId_o_${index}" value="${item.workEffortId?if_exists}"/>
                            <input type="hidden" name="workEffortIdHeader_o_${index}" value="${item.workEffortIdFrom?if_exists}"/>
                            <input type="hidden" name="workEffortIdFrom_o_${index}" value="${item.workEffortId?if_exists}"/><!-- Per la relazione su workEffortAchieveViewExt -->
                            <input type="hidden" name="workEffortTypeId_o_${index}" value="${item.workEffortTypeId?if_exists}"/>
                            <input type="hidden" name="operationalEntityName_o_${index}" value="${entityName}"/>
                            <input type="hidden" name="parentFormNotAllowed_o_${index}" value="Y"/>
                            <input type="hidden" name="subFolderExtraParamFields_o_${index}" value="parentFormNotAllowed"/>
                            <input type="hidden" name="headerEntityName_o_${index}" value="${parameters.entityName}"/>
                            <input type="hidden" name="workEffortAnalysisId_o_${index}" value="${parameters.workEffortAnalysisId?if_exists}"/>
                            <input type="hidden" name="transactionDate_o_${index}" value="${item.transactionDate?if_exists}"/>
                    		<input type="hidden" name="snapshot_o_${index}" value="${parameters.snapshot?if_exists}"/>
                            <!-- Gestione breadcrumbs -->
                            <input type="hidden" name="breadcrumbsCurrentItem_o_${index}" value="${parameters.breadcrumbsCurrentItem?if_exists}_**_${item.workEffortName?if_exists}"/>
                            <input type="hidden" name="sortField_o_${index}" value="sequenceNum|sourceReferenceId|workEffortId"/>
                            
                        </div>
                        <div style="text-align: right; padding-top: 11px;">
                            <#if item.imageSrc?has_content> 
                                <img class="speedometer speedometer_enlarge" src="${item.imageSrc}" speed="${item.imageValue}" target="${item.budgetValue?if_exists}"></img>
                            <#else>
                                <div class="speedometer"></div>
                            </#if>
                        </div>
                    </div>
                    
                    <#if !backGroundContent?has_content>
	                    <b class="rbottom">
						<b class="r4"></b>
						<b class="r3"></b>
						<b class="r2"></b>
						<b class="r1"></b>
						</b>
					</#if>

                </td>
                <td align="center">
                    <#if item.canUpdateRoot?if_exists == "Y">
                        <ul>
                            <li class="class-collegato-active-center"><i class="fas fa-star" onclick="ajaxUpdateAreas('common-container,/${modulo}/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=WorkEffortView&rootInqyTree=N&specialized=Y&rootTree=N&loadTreeView=Y&ignoreSelectedIdFromCookie=Y&ajaxCall=Y&sourceReferenceId=${item.parentSourceReferenceId?if_exists}&workEffortIdRoot=${item.workEffortParentId?if_exists}&workEffortId=${item.workEffortParentId?if_exists}&successCode=management&saveView=Y&searchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormResultLocation=component://${modulo}/widget/forms/${form}.xml&advancedSearchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://${modulo}/widget/screens/${screenName}&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://${modulo}/widget/forms/${form}.xml'); return false;"></i></li>
                        </ul>
                    <#else>
                        <ul>
                            <li class="class-collegato-disabled-center"><i class="fas fa-star" onclick="ajaxUpdateAreas('common-container,/${modulo}/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=WorkEffortView&rootInqyTree=Y&specialized=Y&rootTree=N&loadTreeView=Y&ignoreSelectedIdFromCookie=Y&ajaxCall=Y&sourceReferenceId=${item.parentSourceReferenceId?if_exists}&workEffortIdRoot=${item.workEffortParentId?if_exists}&workEffortId=${item.workEffortParentId?if_exists}&successCode=management&saveView=Y&searchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormResultLocation=component://${modulo}/widget/forms/${form}.xml&advancedSearchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://${modulo}/widget/screens/${screenName}&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://${modulo}/widget/forms/${form}.xml'); return false;"></i></li>
                        </ul>                                    
                    </#if>
                </td>                
            <#if ((index+1) % 3) == 0>
                </tr>
            </#if>
            <#assign index = index+1>
        </#list>
    </tbody>
</table>