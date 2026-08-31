<#if context.listIt?has_content>
<table cellspacing="0" cellpadding="0" class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" id="table_MPML0001_MyPerformance">
    <thead>
        <tr class="header-row-2">
            <th id="table_MPML0001_MyPerformance.Tipology">${uiLabelMap.WorkEffortTypology}</th>
            <th id="table_MPML0001_MyPerformance.orgUnitId">${uiLabelMap.FormFieldTitle_orgUnitId}</th>
            <th id="table_MPML0001_MyPerformance.estimatedStartDate" class="">${uiLabelMap.performanceEstimatedStartDate}</th>
            <th id="table_MPML0001_MyPerformance.estimatedCompletionDate">${uiLabelMap.performanceEstimatedCompletionDate}</th>   
            <th id="table_MPML0001_MyPerformance.stDescription">${uiLabelMap.CommonStatus}</th>
            <#if 0==1>
            <th id="table_MPML0001_MyPerformance.baseActions">${uiLabelMap.BaseActions}</th>
            </#if>
        </tr>
   </thead>
   <tbody style="height: auto;">
    <#assign index=0/>
            <#list listIt as item>
                <tr <#if index%2 != 0>class="alternate-row"</#if>>
                    <td>
                        <input type="hidden" class="mandatory" value="MyPerformance" name="entityName">
                        <input type="hidden" class="mandatory" name="operation">
                        <input type="hidden" value="BaseMessageSaveData" name="messageContext">
                        <input type="hidden" value="${item.estimatedStartDate}" name="estimatedStartDate">
                        <input type="hidden" value="${item.estimatedCompletionDate}" name="estimatedCompletionDate">
                        <input type="hidden" value="${item.orgUnitId}" name="orgUnitId">
                        <input type="hidden" value="${item.partyId?if_exists}" name="partyId">
                        <input type="hidden" value="${item.stDescription}" name="stDescription">
                        <input type="hidden" value="${item.workEffortId}" name="workEffortId">
                        <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
                            <#if item.weTypeEtchLang?has_content>
                                <#assign weType=item.weTypeEtchLang>
                            <#else>
                                <#assign weType=item.weTypeDescriptionLang?if_exists>
                            </#if>
                        <#else>
                            <#if item.weTypeEtch?has_content>
                                <#assign weType=item.weTypeEtch>
                            <#else>
                                <#assign weType=item.weTypeDescription?if_exists>
                            </#if>                        
                        </#if>
                        <div onclick="WorkEffortMyPerformanceSummaryListExtension.load('${item.workEffortId?if_exists}');">${weType?if_exists}</div>
                    </td>
                    <td class="orgUnitColumn">
                        <#assign orgUnitCode = item.orgUnitRoleCode?if_exists>
                        <#if orgUnitCode?has_content>
                            <#assign dashIndex = orgUnitCode?index_of("-")>
                            <#if dashIndex != -1>
                                <#assign orgUnitCode = orgUnitCode?substring(0, dashIndex)?trim>
                            </#if>
                        </#if>
                        <div onclick="WorkEffortMyPerformanceSummaryListExtension.load('${item.workEffortId?if_exists}');">${orgUnitCode} - ${item.orgUnitName?if_exists}</div>
                    </td>
                    <td>
                        <div onclick="WorkEffortMyPerformanceSummaryListExtension.load('${item.workEffortId?if_exists}');">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(item.estimatedStartDate, locale)}</div>
                    </td>
                    <td>
                        <div onclick="WorkEffortMyPerformanceSummaryListExtension.load('${item.workEffortId?if_exists}');">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(item.estimatedCompletionDate, locale)}</div>
                    </td>
                    <td class="center">
                        <#assign rootInqyTree = "Y"/>                            	
	                    <#if item.canUpdateRoot?has_content && item.canUpdateRoot == "Y">
	                        <#assign rootInqyTree = "N"/>
	                    </#if>
	                    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	                        <#assign statusDesc=item.stDescriptionLang?if_exists>
	                    <#else>
	                        <#assign statusDesc=item.stDescription?if_exists>
	                    </#if>
	                    <#if item.canViewRoot?has_content && item.canViewRoot == "Y">
                            <a href="#" onclick=" CleanCookie.loadTreeView(); ajaxUpdateAreas('common-container,/emplperf/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=WorkEffortView&noLeftBar=${parameters.noLeftBar?if_exists?string}&rootInqyTree=${rootInqyTree}&specialized=Y&rootTree=N&loadTreeView=Y&workEffortIdRoot=${item.workEffortId?if_exists}&workEffortId=${item.workEffortId?if_exists}&weHierarchyTypeId=${item.weHierarchyTypeId?if_exists}&successCode=management&sourceReferenceId=${item.sourceReferenceId?if_exists}&saveView=Y&searchFormLocation=component://emplperf/widget/forms/EmplPerfRootViewForms.xml&searchFormResultLocation=component://emplperf/widget/forms/EmplPerfRootViewForms.xml&advancedSearchFormLocation=component://emplperf/widget/forms/EmplPerfRootViewForms.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://emplperf/widget/screens/EmplPerfScreens.xml&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://emplperf/widget/forms/EmplPerfRootViewForms.xml'); return false;" class="event" title="${statusDesc?if_exists}">${statusDesc?if_exists}</a>
                        <#else>
                            ${statusDesc?if_exists}
                        </#if>
                    </td>
                    <#if 0==1>
                    <td style="width: 8%">
                       <div class="contact-actions performance-actions">
                           <ul>
                               <!-- Actions column disabled -->
                           </ul>
                       </div>
                    </td>
                    </#if>
                </tr>
            </#list>
   </tbody>
</table>
</#if>

<div style="display: none;" id="popup-boxs-container" class="popup-reason-boxs-container">
<span class="hidden-label" id="popup-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-box-container">
    <div class="popup-body-container">
        <div id="popup-text-container" class="popup-reason-text-container">
        	${uiLabelMap.WorkEffortConfirmChangeStatus}
           <br/>
           <br/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-cancel" onclick="javascript:Modalbox.hide();">${uiLabelMap.BaseButtonCancel}</a>
        <a href="#" class="smallSubmit button-ok" onclick="javascript: ReasonPopupMgr.validate();">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>