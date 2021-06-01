WorkEffortTypeContentAndContentViewExtension = {
    load: function() {
        var modelListTable = $('table_WorkEffortTypeContentAndContentViewListForm');

        if(Object.isElement(modelListTable)) {
            if (TableKit.isSelectable(modelListTable)) {
                if(Object.isElement(TableKit.Selectable.getSelectedRows(modelListTable)[0]) && !TableKit.isRegistered(modelListTable, 'onSelectEnd', 'WorkEffortTypeContentAndContentViewListForm_selectEnd')){
             		WorkEffortTypeContentAndContentViewExtension.onSelectRow(modelListTable);
             	}
                TableKit.registerObserver(modelListTable, 'onSelectEnd', "WorkEffortTypeContentAndContentViewListForm_selectEnd", WorkEffortTypeContentAndContentViewExtension.onSelectRow);
            }
        }
    },
    onSelectRow: function(table){
    	 var selectRow = TableKit.Selectable.getSelectedRows(table)[0];
        var monitoringDate = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'monitoringDate';
        }); 
        var workEffortId = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'workEffortId';
        }); 
        var pdfContentId = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'pdfContentId';
        });
        var entityName = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'entityName';
        }); 
        
        <#if portletAssoc?has_content>          
            <#assign portletAssocList = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssoc) +  "]", "\\|")>                     
            <#list portletAssocList as portletAssocAssoc>
                <#assign portalPagePortletIdentifications = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssocAssoc) +  "]", "\\,")>
                <#if portalPagePortletIdentifications?has_content>
                    <#assign portalPortletId = portalPagePortletIdentifications[0]>
                    <#assign portletSeqId = portalPagePortletIdentifications[1]>
                    <#assign portalPageId = (parameters.portalPageId)?if_exists>                             
                    ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>','entityName='+entityName.getValue()+'&pdfContentId='+pdfContentId.getValue()+'&workEffortId='+workEffortId.getValue()+'&monitoringDate='+monitoringDate.getValue()+'&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N');
                </#if>
            </#list>
        </#if>
    }
}

WorkEffortTypeContentAndContentViewExtension.load();
