
	<script type="text/javascript">
    	// WorkEffortCalendar puo essere caricato 1 sola volta, 
    	// perche non dipende dalla portlet che si apre
    	// quindi uso un oggetto globale
    	if (typeof WorkEffortCalendar === 'undefined') {
            WorkEffortCalendar = {
                onBeforeLoadListener : function(contentToUpdate, options) {
                    if (Object.isString(contentToUpdate) && !Object.isElement($(contentToUpdate))) {
                        Utils.hideModalBox({afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : Object.extend({name: 'WorkEffortCalendar', onBeforeLoad : WorkEffortCalendar.onBeforeLoadListener}, LookupProperties.responderOnLoad)}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : Object.extend({name: 'WorkEffortCalendar', onBeforeLoad : WorkEffortCalendar.onBeforeLoadListener}, LookupProperties.responderOnLoad)})});
            
                        var data = options['data'];
            
                        UpdateAreaResponder.executeAjaxUpdateAreas(contentToUpdate, data, function(contentToUpdate, data) {
                            var areaArray = contentToUpdate.split(",");
            
                            for (var i = 0; i < 3; i = i + 1) {
                                areaArray = areaArray.without(areaArray[0]);
                            }
            
                            return areaArray.join(',');
                        });
                    } else {
                        return true;
                    }
            
                    return false;
                }
            }
        }

        /* NON SERVE PIU var partyPerformanceSummaryListTable = $('table_WorkEffortPartyPerformance_${parameters.weContextId_value?if_exists}');
                    
	    if (Object.isElement($(partyPerformanceSummaryListTable)) && TableKit.isSelectable(partyPerformanceSummaryListTable)) {
	        console.log("partyPerformanceSummaryListTable", $(partyPerformanceSummaryListTable));
	        TableKit.registerObserver(partyPerformanceSummaryListTable, 'onSelectEnd', "WorkEffortPerformanceSummaryListExtension_selectEnd", function(table, e) {
	            var selectRow = TableKit.Selectable.getSelectedRows(table)[0];
	            var orgUnitId = selectRow.descendants().find(function(element) {
	                return element.readAttribute('name') === 'orgUnitId';
	            });
	            var workEffortId = selectRow.descendants().find(function(element) {
	                return element.readAttribute('name') === 'workEffortId';
	            });
	            var partyId = selectRow.descendants().find(function(element) {
	                return element.readAttribute('name') === 'partyId';
	            });
	            var weContextId_value = selectRow.descendants().find(function(element) {
	                return element.readAttribute('name') === 'weContextId_value';
	            });
	            // console.log("orgUnitId", orgUnitId);
	            // non ci sono associazioni portletAssoc per la lista sotto 
	            <#if portletAssoc?has_content>
		            <#assign portletAssocList = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssoc) +  "]", "\\|")>
		            var button = Event.element(e);
		            var str = button.readAttribute('onclick');
		            
		            try {
		            	eval('var evalFuncTmp = function() { ' + str + ' }');
		                evalFuncTmp();
		            } catch(e) {}
		            
		            <#list portletAssocList as portletAssocAssoc>
		                <#assign portalPagePortletIdentifications = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssocAssoc) +  "]", "\\,")>
		                <#if portalPagePortletIdentifications?has_content>
		                    <#assign portalPortletId = portalPagePortletIdentifications[0]>
		                    <#assign portletSeqId = portalPagePortletIdentifications[1]>
		                    <#assign portalPageId = parameters.portalPageId>
		                    if(orgUnitId) {
	            				ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '${managementPaginationTarget}','&noDataLoaded=Y&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}&weContextId_value='+weContextId_value.getValue()+'&orgUnitId='+orgUnitId.getValue()+'&workEffortIdRoot=' + workEffortId.getValue() + '&partyId='+partyId.getValue()+'&all=true&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N&noLeftBar=${parameters.noLeftBar?if_exists?string}');
		                	}
	            		</#if>
		            </#list>
		        </#if>
	        } );
	    }*/
    </script>
    <#-- Inserisco il modulo in base al weContextId_value -->
    <#assign modulo = "emplperf"/>
    <#assign form = "EmplPerfRootViewForms"/>
    <#assign screenName = "EmplPerfScreens.xml"/>
    <#if parameters.weContextId_value == "CTX_BS">
    	<#assign modulo = "stratperf"/>
    	<#assign form = "StratPerfRootViewForms"/>
    	<#assign screenName = "StratPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_OR">
    	<#assign modulo = "orgperf"/>
    	<#assign form = "OrgPerfRootViewForms"/>
    	<#assign screenName = "OrgPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_CO">
    	<#assign modulo = "corperf"/>
    	<#assign form = "CorPerfRootViewForms"/>
    	<#assign screenName = "CorPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_PR">
    	<#assign modulo = "procperf"/>
    	<#assign form = "ProcPerfRootViewForms"/>
    	<#assign screenName = "ProcPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_CG">
    	<#assign modulo = "cdgperf"/>
    	<#assign form = "CdgPerfRootViewForms"/>
    	<#assign screenName = "CdgPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_TR">
    	<#assign modulo = "trasperf"/>
    	<#assign form = "TrasPerfRootViewForms"/>
    	<#assign screenName = "TrasPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_RE">
    	<#assign modulo = "rendperf"/>
    	<#assign form = "RendPerfRootViewForms"/>
    	<#assign screenName = "RendPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_GD">
    	<#assign modulo = "gdprperf"/>
    	<#assign form = "GdprPerfRootViewForms"/>
    	<#assign screenName = "GdprPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_PA">
    	<#assign modulo = "partperf"/>
    	<#assign form = "PartPerfRootViewForms"/>
    	<#assign screenName = "PartPerfScreens.xml"/>
    <#elseif  parameters.weContextId_value == "CTX_DI">
    	<#assign modulo = "dirigperf"/>
    	<#assign form = "DirigPerfRootViewForms"/>
    	<#assign screenName = "DirigPerfScreens.xml"/>     	    	
    </#if>
    
    <#if !parameters.entityName?if_exists?contains("StratOrg")>
        <#assign childManagementFormListScreenName = "WorkEffortPartyPerformanceSummaryListScreenBody"/>
        <#assign listScreenEntityName = "WorkEffortRootInqyPartySummaryView"/>
    <#else>
        <#assign childManagementFormListScreenName = "WorkEffortPartyPerformanceSummaryStratOrgListScreenBody"/>
        <#assign listScreenEntityName = "WorkEffortRootInqyPartySummaryStratOrgView"/>
    </#if>     
    
    <#if orgUnitSelectedName?has_content>
    	<table>
			<tr>
    			<td class="label">${uiLabelMap.OrganizationUnit}</td>
    			<td class="widget-area-style"><span class="cella">${orgUnitSelectedName?if_exists}</span></td>
			</tr>    	
    	</table>
    </#if>
    
    <form class="basic-form noTableResizeHeight" id="WorkEffortPartyPerformance_${parameters.weContextId_value?if_exists}" > 
	     <table id="table_WorkEffortPartyPerformance_${parameters.weContextId_value?if_exists}" class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable noTableResizeHeight" cellspacing="0">
	        <thead>
	            <tr class="header-row-2">
	                <th>${uiLabelMap.WorkEffortTypology}</th>
	                               
	                <#if !parameters.entityName?if_exists?contains("StratOrg")>
	                <th>${uiLabelMap.BaseParty}</th> 
	                <#else>
	                	<th>
	                	    <#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	                	        <#if parameters.sortField?if_exists != 'workEffortNameLang' && parameters.sortField?if_exists != '-workEffortNameLang'>
	                	           <a class="sort-order" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=workEffortNameLang')" href="#">${uiLabelMap.WorkEffortName}</a>
	                	        </#if>
	                	        <#if parameters.sortField?if_exists == 'workEffortNameLang'> 
	                	            <a class="sort-order-desc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=-workEffortNameLang')" href="#">${uiLabelMap.WorkEffortName}</a> 
	                	        </#if>
	                	        <#if parameters.sortField?if_exists == '-workEffortNameLang'> 
	                	            <a class="sort-order-asc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=workEffortNameLang')" href="#">${uiLabelMap.WorkEffortName}</a>
	                	        </#if>
	                	    <#else>
	                	        <#if parameters.sortField?if_exists != 'workEffortName' && parameters.sortField?if_exists != '-workEffortName'>
	                	           <a class="sort-order" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=workEffortName')" href="#">${uiLabelMap.WorkEffortName}</a>
	                	        </#if>
	                	        <#if parameters.sortField?if_exists == 'workEffortName'> 
	                	            <a class="sort-order-desc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=-workEffortName')" href="#">${uiLabelMap.WorkEffortName}</a> 
	                	        </#if>
	                	        <#if parameters.sortField?if_exists == '-workEffortName'> 
	                	            <a class="sort-order-asc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=workEffortName')" href="#">${uiLabelMap.WorkEffortName}</a>
	                	        </#if>	                	    
	                	    </#if>
	                	</th>	                	
	                </#if>
	                <th>                
	                    <#if parameters.sortField?if_exists != 'estimatedStartDate' && parameters.sortField?if_exists != '-estimatedStartDate'>
	                	    <a class="sort-order" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=estimatedStartDate')" href="#">${uiLabelMap.WorkeffortEstimatedStartDateTitle}</a>
	                	</#if>
	                	<#if parameters.sortField?if_exists == 'estimatedStartDate'> 
	                	     <a class="sort-order-desc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=-estimatedStartDate')" href="#">${uiLabelMap.WorkeffortEstimatedStartDateTitle}</a> 
	                	</#if>
	                	<#if parameters.sortField?if_exists == '-estimatedStartDate'> 
	                	     <a class="sort-order-asc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=estimatedStartDate')" href="#">${uiLabelMap.WorkeffortEstimatedStartDateTitle}</a>
	                	</#if>	                	                
	                </th>
	                <th>${uiLabelMap.WorkeffortEstimatedCompletionDateTitle}</th>                                
	                <th>
	                	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	                	    <#if parameters.sortField?if_exists != 'weStatusDescrLang' && parameters.sortField?if_exists != '-weStatusDescrLang'>
	                	        <a class="sort-order" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=weStatusDescrLang')" href="#">${uiLabelMap.CommonStatus}</a>
	                	    </#if>
	                	    <#if parameters.sortField?if_exists == 'weStatusDescrLang'> 
	                	         <a class="sort-order-desc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=-weStatusDescrLang')" href="#">${uiLabelMap.CommonStatus}</a> 
	                	    </#if>
	                	    <#if parameters.sortField?if_exists == '-weStatusDescrLang'> 
	                	         <a class="sort-order-asc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=weStatusDescrLang')" href="#">${uiLabelMap.CommonStatus}</a>
	                	    </#if>
	                	<#else>
	                	    <#if parameters.sortField?if_exists != 'weStatusDescr' && parameters.sortField?if_exists != '-weStatusDescr'>
	                	        <a class="sort-order" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=weStatusDescr')" href="#">${uiLabelMap.CommonStatus}</a>
	                	    </#if>
	                	    <#if parameters.sortField?if_exists == 'weStatusDescr'> 
	                	         <a class="sort-order-desc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=-weStatusDescr')" href="#">${uiLabelMap.CommonStatus}</a> 
	                	    </#if>
	                	    <#if parameters.sortField?if_exists == '-weStatusDescr'> 
	                	         <a class="sort-order-asc" onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noLeftBar=${parameters.noLeftBar?if_exists?string}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;sortField=weStatusDescr')" href="#">${uiLabelMap.CommonStatus}</a>
	                	    </#if>	                	    
	                	</#if>
	                </th>                
	                <th>${uiLabelMap.BaseActions}</th>
	            </tr>
	        </thead>
	        <tbody>
	            <#assign index=0/>
	            <#list listIt as workEffort>
	            	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	            		<#if workEffort.etchLang?has_content>	            		
		        			<#assign weType = workEffort.etchLang?if_exists>
		        		<#else>
		        			<#assign weType = workEffort.weTypeDescriptionLang?if_exists>
		        		</#if>	            	
	            	<#else>
	            		<#if workEffort.etch?has_content>	            		
		        			<#assign weType = workEffort.etch?if_exists>
		        		<#else>
		        			<#assign weType = workEffort.weTypeDescription?if_exists>
		        		</#if>
		        	</#if>           
	                <tr <#if index%2 != 0>class="alternate-row"</#if>>
	                    <td class="orgUnitColumn">
	                        <input type="hidden" name="orgUnitId" id="orgUnitId" value="${workEffort.orgUnitId?if_exists}"/>
	                        <input type="hidden" name="workEffortId" id="workEffortId" value="${workEffort.workEffortId}"/>
	                        <input type="hidden" name="partyId" id="partyId" value="${workEffort.partyId?if_exists}"/>
	                        <input type="hidden" name="weContextId_value" id="weContextId_value" value="${parameters.weContextId_value}"/>
	                        <div class="workEffortTypeIdPartySummaryColumn" title="${weType?if_exists}">
	                            ${weType?if_exists}
	                        </div>                      
	                    </td>
	                    <#if !parameters.entityName?if_exists?contains("StratOrg")>
	                    
	                    <td>
	                    	<#if security.hasEntityPermission("PARTYMGR", "_VIEW", userLogin)>
	                    		<a href="#" onclick="ajaxUpdateAreas('common-container,/partyext/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=Party&partyId=${workEffort.partyId?if_exists}&saveView=N'); return false;" class="event">${workEffort.partyNameInCharge?if_exists}</a>
	                    	<#else>
	                    		${workEffort.partyNameInCharge?if_exists}
	                    	</#if>
	                    </td>
	                    <#else>
	                		<td>
	                			<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	                			    <div class="workEffortNamePartySummaryColumn" title="<#if workEffort.weEtch?has_content>${workEffort.weEtch?if_exists} - </#if> ${workEffort.workEffortNameLang?if_exists}">
	                			    	<#if workEffort.weEtch?has_content>${workEffort.weEtch?if_exists} - </#if> ${workEffort.workEffortNameLang?if_exists}
	                			    </div>
	                			<#else>
	                			    <div class="workEffortNamePartySummaryColumn" title="<#if workEffort.weEtch?has_content>${workEffort.weEtch?if_exists} - </#if> ${workEffort.workEffortName?if_exists}">
	                			    	<#if workEffort.weEtch?has_content>${workEffort.weEtch?if_exists} - </#if> ${workEffort.workEffortName?if_exists}
	                			    </div>	                			
	                			</#if>
	                        </td>
	                    </#if>
	                    <td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(workEffort.estimatedStartDate?if_exists, locale)}</td>
	                    <td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(workEffort.estimatedCompletionDate?if_exists, locale)}</td>
	                    <td>
	                        <#if workEffort.currentStatusId?has_content>
	                            <#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", workEffort.currentStatusId), true)>
	                            <#if statusItem?has_content >                            
	                            	<#assign rootInqyTree = "Y"/>                            	
	                            	<#if workEffort.canUpdateRoot?has_content && workEffort.canUpdateRoot == "Y">
	                            	    <#assign rootInqyTree = "N"/>
	                            	</#if>
	                            	                           	                            	                   		                            
		                            <#assign dateFormat = Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale)>
		                            <#assign anno = "">
		                            <#assign estimatedStartDate = "">
		                            <#assign estimatedCompletionDate = "">
		                            <#if workEffort.estimatedStartDate?has_content>
		                            	<#assign estimatedStartDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(workEffort.estimatedStartDate?if_exists, locale)>
		                            </#if>
		                            <#if workEffort.estimatedCompletionDate?has_content>
		                            	<#assign estimatedCompletionDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(workEffort.estimatedCompletionDate?if_exists, locale)>
		                            </#if>
		                            <#if !parameters.entityName?if_exists?contains("StratOrg")>
		                            	<#if workEffort.estimatedCompletionDate?has_content>
	                            			<#assign anno = Static["org.ofbiz.base.util.UtilDateTime"].getYear(workEffort.estimatedCompletionDate?if_exists, timeZone, locale)>
	                            		</#if>
	                            	</#if>
	                            	
	                            	<#if localeSecondarySet?has_content && localeSecondarySet?default('N') == 'Y'>
	                            	    <#assign statusDesc = workEffort.weStatusDescrLang?if_exists>
	                            	<#else>
	                            		<#assign statusDesc = workEffort.weStatusDescr?if_exists>
	                            	</#if>
	                            	<div class="workEffortStatusPartySummaryColumn">
	                            	    <#if workEffort.canViewRoot?has_content && workEffort.canViewRoot == "Y">
		                                    <a href="#" onclick=" CleanCookie.loadTreeView(); ajaxUpdateAreas('common-container,/${modulo}/control/managementContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&entityName=WorkEffortView&noLeftBar=${parameters.noLeftBar?if_exists?string}&rootInqyTree=${rootInqyTree}&specialized=Y&rootTree=N&loadTreeView=Y&workEffortIdRoot=${workEffort.workEffortId?if_exists}&workEffortId=${workEffort.workEffortId?if_exists}&weHierarchyTypeId=${workEffort.weHierarchyTypeId?if_exists}&successCode=management&sourceReferenceId=${workEffort.sourceReferenceId?if_exists}&saveView=current&searchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormResultLocation=component://${modulo}/widget/forms/${form}.xml&advancedSearchFormLocation=component://${modulo}/widget/forms/${form}.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://${modulo}/widget/screens/${screenName}&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://${modulo}/widget/forms/${form}.xml'); return false;" class="event" title="${statusDesc?if_exists}">${statusDesc?if_exists}<#if anno?has_content> - ${anno}</#if></a>
	                            	    <#else>
	                            	        ${statusDesc?if_exists}<#if anno?has_content> - ${anno}</#if>
	                            	    </#if>
	                            	</div>
	                            </#if>
	                        </#if>
	                    </td>
	                    <td style="width: 8%;">
	                          <div class="contact-actions performance-actions">
	                            <ul>                               
	                                  <#assign resultExtraTargetParameters = "">
	                                  <#if extraTargetParameters?has_content>
	                                    <#list StringUtil.wrapString(extraTargetParameters)?split("|") as extraTargetParameter>
	                                        <#if resultExtraTargetParameters?length != 0>
	                                            <#assign resultExtraTargetParameters = StringUtil.wrapString(resultExtraTargetParameters) + "|" + StringUtil.wrapString(extraTargetParameter)?replace("&","*") + "*period=week*start=" + parameters.start?if_exists>
	                                        <#else>
	                                            <#assign resultExtraTargetParameters = StringUtil.wrapString(extraTargetParameter)?replace("&","*") + "*period=week*start=" + parameters.start?if_exists>    
	                                        </#if>
	                                        
	                                    </#list>  
	                                    <#assign extraTargetParameters = resultExtraTargetParameters>
	                                  </#if>
	                                  
	                                 
	                                 <#if workEffort.canUpdateRoot?has_content && workEffort.canUpdateRoot == "Y">
		                            	<li class="class-collegato-active"><i class="fas fa-star"></i></li>                               		
	                               	 <#else>
	                               	 	<li class="class-collegato-disabled"><i class="far fa-star"></i></li>
		                             </#if>
		                             
		                             <#if workEffort.nextValidStatusId?has_content && workEffort.canUpdateRoot?has_content && workEffort.canUpdateRoot == "Y">
		                             	<li class="ok fa">       
	                               	 		<a href="#" onclick="ChangeStatus.change('${workEffort.nextValidStatusId?if_exists}', '${workEffort.workEffortId?if_exists}')" title="${workEffort.nextValidStatusDescr?if_exists}"></a>
	                                    
		                                    <script type="text/javascript">
			                                    ChangeStatus = {
			                                    	change : function(statusId, workEffortId) {		 
			                                    		new Ajax.Request("<@ofbizUrl>checkFolderActivated</@ofbizUrl>", {
	                                    				    parameters: {"workEffortRootId": workEffortId, "folder": "WEFLD_STATUS"},
	                                    				    onSuccess: function(response) {
	                                    					    var data = response.responseText.evalJSON(true);
	                                    					    if (data) {
	                                    						    if (data.isFolderVisible == "Y") {
	                                    							    Modalbox.show($('popup-reason-boxs-container'), {statusId : statusId, workEffortId : workEffortId, transitions : false});
	                                    						    } else {
	                                    							    Modalbox.show($('popup-boxs-container'), {statusId : statusId, workEffortId : workEffortId, transitions : false});
	                                    						    }
	                                    					    }
	                                    				    },
	                                    				    onFailure: function() {
	                                    					    Modalbox.show($('popup-boxs-container'), {statusId : statusId, workEffortId : workEffortId, transitions : false});
	                                    				    }
	                                    			    });			                                    	    	                                    	
			                                    	}
			                                    }		                                    
			                                    
			                                    ReasonPopupMgr = Class.create({ });
			                                    
			                                    ReasonPopupMgr.getParametersToSubmit = function() {    
			                                        var parametersToSubmit = {};
			                                       	
			                                        var reason = $('reasonDescription')
			                                        if (reason != null) {
			                                            parametersToSubmit.reason = reason.getValue();
			                                        }
			                                        return parametersToSubmit;
			                                    }	
	
						                        ReasonPopupMgr.validate = function() {
						                                    
						                            parametersToSubmit = ReasonPopupMgr.getParametersToSubmit();
						                                    	
			                                    	var newForm = new Element('form', {id : 'changeStatusForm', name : 'changeStatusForm'});
		                                            newForm.writeAttribute('action', '<@ofbizUrl>elaborateFormForUpdateAjax</@ofbizUrl>');
		                                            newForm.insert(new Element('input', {name : 'workEffortId', type : 'hidden', value : Modalbox.options.workEffortId}));
		                                            newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortStatus'}));
		                                            newForm.insert(new Element('input', {name : 'crudService', type : 'hidden', value : 'crudServiceDefaultOrchestration_WorkEffortRootStatus'}));
		                                            newForm.insert(new Element('input', {name : 'operation', type : 'hidden', value : 'CREATE'}));
		                                            newForm.insert(new Element('input', {name : 'saveView', type : 'hidden', value : 'N'}));
		                                            newForm.insert(new Element('input', {name : 'ignoreAuxiliaryParameters', type : 'hidden', value : 'Y'}));
		                                            <#assign defaultDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
		                                            newForm.insert(new Element('input', {name : 'statusDatetime', type : 'hidden', value : '${defaultDateTime}'}));
		                                            newForm.insert(new Element('input', {name : 'statusId', type : 'hidden', value : Modalbox.options.statusId}));
		                                            newForm.insert(new Element('input', {name : 'reason', type : 'text', id: 'pippo_reason', value : parametersToSubmit.reason}));
				                                    newForm.insert(new Element('input', {name : 'messageContext', type : 'hidden', value : 'BaseMessageSaveData'}));
			                                                
			                                        document.body.insert(newForm);
			                                                    
		                                            ajaxSubmitFormUpdateAreas('changeStatusForm','','common-container, <@ofbizUrl>showPortalPageMainContainerOnly</@ofbizUrl>, externalLoginKey=${requestAttributes.externalLoginKey?if_exists}&ajaxCall=Y&portalPageId=${parameters.portalPageId}&parentPortalPageId=${parameters.parentPortalPageId?if_exists}&saveView=Y',
		                                                    {onComplete: function(transport) {ReasonPopupMgr.checkErrorsAndSubmit(transport)}});		                                                    
	
			                                                		                                                
			                                        newForm.remove();           
						                        }
	
						                        ReasonPopupMgr.checkErrorsAndSubmit = function(transport) {
						                            var data = transport.responseText.evalJSON(true);
						                            if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
						                                Modalbox.hide();
						                                modal_box_messages._resetMessages();
						                                modal_box_messages.onAjaxLoad(data, Prototype.K);					                                		
						                                ReasonPopupMgr.reset();
						                            } else {
						                                Modalbox.hide();
						                                UpdateAreaResponder._updateElement(transport, 'common-container, <@ofbizUrl>showPortalPageMainContainerOnly</@ofbizUrl>, externalLoginKey=${requestAttributes.externalLoginKey?if_exists}&ajaxCall=Y&portalPageId=${parameters.portalPageId}&parentPortalPageId=${parameters.parentPortalPageId?if_exists}&saveView=Y');
						                            }					                                	
						                        }					                                   		
											 	
		                                    </script>                         
	                               		</li>
		                             
		                             <#else>
		                             	<li class="ok-disabled fa"><a href="#"></a></li> 								 
									 </#if>	                               
	                            </ul>
	                          </div>
	                    </td>
	                </tr>
	                <#assign index = index+1>
	            </#list>
	        </tbody>
	     </table>
     </form>
     <#if listIt?has_content && completeListSize &gt; 0>
        <#assign lowIndex = (parameters["VIEW_INDEX_" + parameters.PAGINATOR_NUMBER])*(parameters["VIEW_SIZE_" + parameters.PAGINATOR_NUMBER]) + 1/>
        <#assign highIndex = lowIndex + (listIt?size - 1)/>
        <#assign firstEnabled = parameters["VIEW_INDEX_" + parameters.PAGINATOR_NUMBER] != 0/>
        <#assign lastEnabled = highIndex != completeListSize/>
         <div class="nav-pager">          
             <ul>
              <li class="nav-first<#if !firstEnabled>-disabled disabled</#if> fa"><#if firstEnabled><a onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;PAGINATOR_NUMBER=${parameters.PAGINATOR_NUMBER}&amp;VIEW_SIZE_${parameters.PAGINATOR_NUMBER}=${parameters["VIEW_SIZE_" + parameters.PAGINATOR_NUMBER]}&amp;VIEW_INDEX_${parameters.PAGINATOR_NUMBER}=0&amp;sortField=${parameters.sortField?if_exists}')" href="#"></a><#else><a href="#"></a></#if></li>
              <li class="nav-previous<#if !firstEnabled>-disabled disabled</#if> fa"><#if firstEnabled><a onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;PAGINATOR_NUMBER=${parameters.PAGINATOR_NUMBER}&amp;VIEW_SIZE_${parameters.PAGINATOR_NUMBER}=${parameters["VIEW_SIZE_" + parameters.PAGINATOR_NUMBER]}&amp;VIEW_INDEX_${parameters.PAGINATOR_NUMBER}=${parameters["VIEW_INDEX_" + parameters.PAGINATOR_NUMBER] - 1}&amp;sortField=${parameters.sortField?if_exists}')" href="#"></a><#else><a href="#"></a></#if></li>
              <li class="page-selector">${uiLabelMap.CommonPage}</li><li class="pager"><div><span>${lowIndex}/${lowIndex + (listIt?size - 1)} di ${completeListSize}</span></div></li>
              <li class="nav-next<#if !lastEnabled>-disabled disabled</#if> fa"><#if lastEnabled><a onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;PAGINATOR_NUMBER=${parameters.PAGINATOR_NUMBER}&amp;VIEW_SIZE_${parameters.PAGINATOR_NUMBER}=${parameters["VIEW_SIZE_" + parameters.PAGINATOR_NUMBER]}&amp;VIEW_INDEX_${parameters.PAGINATOR_NUMBER}=${parameters["VIEW_INDEX_" + parameters.PAGINATOR_NUMBER] + 1}&amp;sortField=${parameters.sortField?if_exists}')" href="#"></a><#else><a href="#"></a></#if></li>
              <li class="nav-last<#if !lastEnabled>-disabled disabled</#if> fa"><#if lastEnabled><a onclick="javascript:ajaxUpdateAreas('child-management-container-body-${listScreenEntityName},/${modulo}/control/childManagementListContainerOnly,externalLoginKey=${requestAttributes.externalLoginKey}&amp;noExecuteChildPerformFind=Y&amp;weContextId_value=${parameters.weContextId_value}&amp;portalPageId=${parameters.portalPageId}&amp;portalPortletId=${parameters.portalPortletId}&amp;portletScreenlet=Y&amp;portletSeqId=${parameters.portletSeqId}&amp;_LAST_VIEW_NAME_=${parameters._LAST_VIEW_NAME_}&amp;entityName=${listScreenEntityName}&amp;ajaxCall=Y&amp;parentEntityName=WorkEffortView&amp;childManagementFormListScreenName=${childManagementFormListScreenName}&amp;managementFormListScreenName=&amp;managementFormListScreenLocation=&amp;childManagementFormListScreenLocation=component://workeffortext/widget/screens/WorkeffortExtScreens.xml&amp;searchFormLocation=&amp;searchFormResultLocation=&amp;advancedSearchFormLocation=&amp;orgUnitId=${parameters.orgUnitId?if_exists}&amp;saveView=N&amp;contextManagementFormName=&amp;contextManagementFormLocation=component://base/widget/BaseForms.xml&amp;contextManagementFormScreenName=&amp;contextManagementFormScreenLocation=&amp;actionMenuName=&amp;actionMenuLocation=&amp;PAGINATOR_NUMBER=${parameters.PAGINATOR_NUMBER}&amp;VIEW_SIZE_${parameters.PAGINATOR_NUMBER}=${parameters["VIEW_SIZE_" + parameters.PAGINATOR_NUMBER]}&amp;VIEW_INDEX_${parameters.PAGINATOR_NUMBER}=${lastViewIndex}&amp;sortField=${parameters.sortField?if_exists}')" href="#"></a><#else><a href="#"></a></#if></li>
             </ul>
        </div>
    </#if>

<#-- POPUP per il cambio di stato!! -->
<div style="display: none;" id="popup-reason-boxs-container" class="popup-reason-boxs-container">
<span class="hidden-label" id="popup-reason-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-reason-box-container">
    <div class="popup-reason-body-container">
        <div id="popup-reason-text-container" class="popup-reason-text-container">
        	${uiLabelMap.WorkEffortConfirmChangeStatus}
           <br/>
           <textarea rows="6" cols="70" name="reasonDescription" id="reasonDescription" class="mandatory"></textarea>
           <br/>
           <br/>
           <br/>
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-cancel" onclick="Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
        <a href="#" class="smallSubmit button-ok" onclick="javascript: ReasonPopupMgr.validate();">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>
<div style="display: none;" id="popup-check-boxs-container" class="popup-check-boxs-container">
<span class="hidden-label" id="popup-check-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
<div class="popup-check-box-container">
    <div class="popup-check-body-container">
        <div id="popup-check-text-container" class="popup-check-text-container">
        </div>
    </div>
    <div class="popup-copy-all-buttons-container">
        <a href="#" class="smallSubmit button-ok" onclick="javascript:ReasonPopupMgr.reset(); Modalbox.hide()">${uiLabelMap.BaseButtonOK}</a>
    </div>
</div>
</div>

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