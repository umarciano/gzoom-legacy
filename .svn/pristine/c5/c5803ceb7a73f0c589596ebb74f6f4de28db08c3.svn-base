CustomTimePeriodPrintViewManagementListFormExtension = {
    load: function() {
        var modelListTable = $('table_CustomTimePeriodPrintViewManagementListForm');

        if(Object.isElement(modelListTable)) {
            if (TableKit.isSelectable(modelListTable)) {
             	if(Object.isElement(TableKit.Selectable.getSelectedRows(modelListTable)[0]) && !TableKit.isRegistered(modelListTable, 'onSelectEnd', 'CustomTimePeriodPrintViewManagementListForm_selectEnd')){
             		CustomTimePeriodPrintViewManagementListFormExtension.onSelectRow(modelListTable);
             	}
                TableKit.registerObserver(modelListTable, 'onSelectEnd', "CustomTimePeriodPrintViewManagementListForm_selectEnd", CustomTimePeriodPrintViewManagementListFormExtension.onSelectRow);
            }
        }
    },
    onSelectRow: function(table){
    	var selectRow = TableKit.Selectable.getSelectedRows(table)[0];
        var thruDate = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'thruDate';
        }); 
        var workEffortId = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'workEffortId';
        }); 
        var workEffortTypeId = selectRow.descendants().find(function(element) {
            return element.readAttribute('name') === 'workEffortTypeId';
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
                    ajaxUpdateArea('portlet-container_${portalPageId}_${portalPortletId}_${portletSeqId}', '<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>','entityName='+entityName.getValue()+'&workEffortTypeId='+workEffortTypeId.getValue()+'&workEffortId='+workEffortId.getValue()+'&monitoringDate='+thruDate.getValue()+'&portalPageId=${portalPageId?if_exists}&portalPortletId=${portalPortletId?if_exists}&portletSeqId=${portletSeqId?if_exists}&saveView=N');
                </#if>
            </#list>
        </#if>
    }
}

CustomTimePeriodPrintViewManagementListFormExtension.load();

