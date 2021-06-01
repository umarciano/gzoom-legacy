WorkEffortPerformanceSummaryListExtension = {
    load: function() {
        var performanceSummaryListTable = $('table_WorkEffortPlanPerformanceSummaryManagementListForm_${parameters.weContextId_value}');
        
        if(Object.isElement(performanceSummaryListTable)) {
            if (TableKit.isSelectable(performanceSummaryListTable)) {           	
                TableKit.registerObserver(performanceSummaryListTable, 'onSelectEnd', "WorkEffortPerformanceSummaryListExtension_selectEnd", function(table, e) {
                    var selectRow = TableKit.Selectable.getSelectedRows(table)[0];
                    if (selectRow) {
                    	WorkEffortPerformanceSummaryListExtension.onSelectRow(selectRow);
                    }
                } );
                var selectRow = TableKit.Selectable.getSelectedRows(performanceSummaryListTable)[0];
                if (selectRow) {
                	WorkEffortPerformanceSummaryListExtension.onSelectRow(selectRow);
                }
            }
        }
    },
    
    onSelectRow: function (selectRow) {
        var orgUnitId = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'orgUnitId';
        });
        var weContextId_value = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'weContextId_value';
        });
        <#assign portletAssocList = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssoc) +  "]", "\\|")>
        <#list portletAssocList as portletAssocAssoc>
            <#assign portalPagePortletIdentifications = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssocAssoc) +  "]", "\\,")>
            <#if portalPagePortletIdentifications?has_content>
                <#assign portalPortletId = portalPagePortletIdentifications[0]>
                <#assign portletSeqId = portalPagePortletIdentifications[1]>
                <#assign portalPageId = parameters.portalPageId>
                 
                ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>','&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}&weContextId_value='+weContextId_value.getValue()+'&orgUnitId='+orgUnitId.getValue()+'&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N');
            </#if>
        </#list>
    }
}

WorkEffortPerformanceSummaryListExtension.load();

<#--document.observe("dom:loaded", WorkEffortPerformanceSummaryListExtension.load);-->