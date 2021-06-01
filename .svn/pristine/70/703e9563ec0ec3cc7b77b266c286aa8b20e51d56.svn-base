WorkEffortAndTypeViewListExtension = {
    load: function() {
        var modelListTable = $('table_WorkEffortAndTypeViewManagementListForm');

        if(Object.isElement(modelListTable)) {
            if (TableKit.isSelectable(modelListTable)) {
                TableKit.registerObserver(modelListTable, 'onSelectEnd', "WorkEffortAndTypeViewListExtension_selectEnd", function(table, e) {
                    var selectRow = TableKit.Selectable.getSelectedRows(table)[0];
                    var periodTypeId = selectRow.descendants().find(function(element) {
                        return element.readAttribute('name') === 'periodTypeId';
                    });                    
                    var workEffortTypeId = selectRow.descendants().find(function(element) {
                        return element.readAttribute('name') === 'workEffortTypeId';
                    });  
                    var workEffortId = selectRow.descendants().find(function(element) {
                        return element.readAttribute('name') === 'workEffortId';
                    });
                    var entityName = selectRow.descendants().find(function(element) {
                        return element.readAttribute('name') === 'entityName';
                    });
                    <#if portletAssoc?has_content>
	                    <#assign portletAssocList = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssoc?default("")) +  "]", "\\|")>
	                    <#list portletAssocList as portletAssocAssoc>
	                        <#assign portalPagePortletIdentifications = Static["org.ofbiz.base.util.StringUtil"].toList("[" + StringUtil.wrapString(portletAssocAssoc) +  "]", "\\,")>
	                        <#if portalPagePortletIdentifications?has_content>
	                            <#assign portalPortletId = portalPagePortletIdentifications[0]>
	                            <#assign portletSeqId = portalPagePortletIdentifications[1]>
	                            <#assign portalPageId = parameters.portalPageId>
	                            <#assign dummy = Static["org.ofbiz.base.util.Debug"].log("*** WorkEffortAndTypeView portalPageId=" + portalPageId?if_exists)?if_exists>
	                            ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>','entityName='+entityName.getValue()+'&periodTypeId='+periodTypeId.getValue()+'&workEffortTypeId='+workEffortTypeId.getValue()+'&workEffortId='+workEffortId.getValue()+'&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N');
	                        </#if>
	                    </#list>
                    </#if>
                } );
            }
        }
    }
}

WorkEffortAndTypeViewListExtension.load();


