WorkEffortMyPerformanceSummaryListExtension = {
    load: function(workEffortId) {
        <#if portletAssoc?has_content>
		    <#assign portletAssocList = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssoc) +  "]", "\\|")>
	        <#list portletAssocList as portletAssocAssoc>
	            <#assign portalPagePortletIdentifications = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssocAssoc) +  "]", "\\,")>
	            <#if portalPagePortletIdentifications?has_content>
	                <#assign portalPortletId = portalPagePortletIdentifications[0]>
	                <#assign portletSeqId = portalPagePortletIdentifications[1]>
	                <#assign portalPageId = parameters.portalPageId>
	                
	                <#-- Forza read-only se siamo nel portale GP_WE_PORTAL_3 (NOPORTAL_MY) -->
	                <#assign readOnlyParam = "">
	                <#if portalPageId?? && portalPageId == "GP_WE_PORTAL_3">
	                    <#assign readOnlyParam = "&forceReadOnly=Y&managementFormType=view">
	                </#if>
	                 
	                // Aggiungi portalPageId anche al readOnlyParam per garantire propagazione
	                var urlParams = 'workEffortId='+workEffortId+'&noDataLoaded=Y&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N${readOnlyParam}';
	                <#if portalPageId?? && portalPageId == "GP_WE_PORTAL_3">
	                    urlParams += '&portalPageId=${portalPageId}';
	                </#if>
	                ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>', urlParams);
	            </#if>
	        </#list>
        </#if>
    }
}
