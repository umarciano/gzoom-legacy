	<#import "/base/webapp/common/ftl/gzoomMacro.ftl"  as gzoom/>

	<script type="text/javascript">
        var selectors = ["div.mblShowDescription"];
        populateTooltip(selectors);
    </script>
	
	<#macro formatDate date=null>
		<#if date?is_date>
			${date?string("dd/MM/yyyy")}
		</#if>
	</#macro>
	
	<#assign pattern = "##"/>
	
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
    <#if workEffortAchieveList?has_content>
     <table class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable dblclick-open-management" cellspacing="0">
        <thead>
            <tr class="header-row-2">
                <th><div>${uiLabelMap.FormFieldTitle_workEffortName}</div></th>
                <!--<th>${uiLabelMap.WorkEffortAchieveViewAdvance}</th>-->
                <#assign singleHeaderTitle = false>
                <#if workEffortAchieveList?has_content>
                    <#assign firstItem = workEffortAchieveList[0]>
                    <#if !firstItem.budgetValue?has_content>
                        <#assign singleHeaderTitle = true>
                    </#if>
                </#if>
                <th><#if !singleHeaderTitle><div>${uiLabelMap.WorkEffortAchieveViewRealized}</div><div>${uiLabelMap.WorkEffortAchieveViewProgrammed}</div><#else><div>${uiLabelMap.WorkEffortAchieveViewPerformance}</div></#if></th>
                <th>${uiLabelMap.BaseActions}</th>
            </tr>
        </thead>
        <tbody>
            <#assign index=0/>
            <#if parameters.workEffortAnalysisId?has_content>
                <#assign weAnalisis = delegator.findOne("WorkEffortAnalysis", Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortAnalysisId", parameters.workEffortAnalysisId), true)>
            </#if>
            
            
            <#list workEffortAchieveList as item>
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td >
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
                    	
                    	<div>
                    		<div class="mblContainer mblDisplayStringCell <#if showTooltip?if_exists>mblShowDescription</#if>" style="font-size: 3em;" <#if item.description?has_content> description="${StringUtil.wrapString(item.description)}"</#if>>
                				<div class="mblParameter" name="entityName" value="WorkEffortAchieveChildView"></div>
                				<div class="mblParameter" name="parentEntityName" value="WorkEffortAchieveView"></div>
                				<div class="mblParameter" name="workEffortIdFrom" value="${item.workEffortId?if_exists}"></div>
                				<div class="mblParameter" name="noConditionFind" value="Y"></div>
                				<div class="mblParameter" name="saveView" value="N"></div>
                				<div class="mblParameter" name="childManagement" value="Y"></div>
                				<div class="mblParameter" name="sortField" value="sequenceNum|sourceReferenceId|workEffortId"></div>
                				<#if (item.etch)?has_content>${(item.etch)?if_exists} - </#if>
                				<#if "Y" == showOrgUnit?if_exists>
                                	${item.weOrgPartyDescr?if_exists}
                                <#else>
            						${item.workEffortName?if_exists}
            					</#if>
                			</div>
                			<#assign itemDescription = StringUtil.wrapString(item.description?default(""))>
                			<#if itemDescription?length &gt; 500>
                				<#assign itemDescription = StringUtil.wrapString(itemDescription?substring(0,500))+"...">
                			</#if>
                    	</div>
                    </td>
                    <td  style="padding: 1px !important; text-align: center; width: 100px;">
                        <div style="float: left;">
                            <#if ""!=(item.imageSrc)?if_exists> 
                                <img class="speedometer speedometer_enlarge" src="${item.imageSrc}" speed="${item.imageValue}" target="${item.budgetValue?if_exists}"></img>
                            <#else>
                                <div class="noSpeedometerPresent"><span>${uiLabelMap.NoSpeedometerPresent}</span></div>
                            </#if>
                        </div>
                        <div style="float: right; padding-top: 20px; padding-right: 5px;">
                            <div style="height: 40%;">
                                <div>
                                    <#if item.imageValue?has_content>
                                        <#assign imageValue = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.imageValue, pattern, locale)/>
                                        ${imageValue?if_exists}%
                                    </#if>   
                                </div>
                                <div>
                                    <#if item.budgetValue?has_content>
                                        <#assign budgetValue = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.budgetValue, pattern, locale)/>
                                        ${budgetValue?if_exists}%
                                    </#if>                        
                                </div>
                            </div>
                            <div style="height: 30%;">
                                <#if (item.alertContentId?exists)>
                                    <img class="mblDisplayIconCell" src="/content/control/stream?contentId=${item.alertContentId}"/> 
                                <#else>
                                </#if>
                            </div>
                            <div style="height: 30%">
                                <#if (item.hasScoreAlertSrc?exists)>
                                    <img class="mblDisplayIconCell" src="${item.hasScoreAlertSrc}"/> 
                                <#else>
                                    <div/>
                                </#if>
                            </div>
                        </div>
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
                </tr>
                <#assign index = index+1>
            </#list>
        </tbody>
     </table>
	</#if>