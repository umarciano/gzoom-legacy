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
	                 
	                ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>','workEffortId='+workEffortId+'&noDataLoaded=Y&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N');
	            </#if>
	        </#list>
        </#if>
    }
}
