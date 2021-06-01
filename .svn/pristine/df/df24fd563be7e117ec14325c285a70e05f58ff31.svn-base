QueryConfigViewManagement = {
    showQueryInfo: function () {
        Modalbox.show($('query-info-cnt'), {title: '${uiLabelMap.QueryConfigTitle}', height: 600, width: 1000});
    },
    
    downloadExcel: function() {
        var linkExcel = document.getElementById('query-excel-download');
        var href = linkExcel.href;
        var form = QueryConfigViewManagement.loadManagementForm();
        var cond0Info = $(form).down("textarea[name='cond0Info']").getValue();
        linkExcel.href = '<@ofbizUrl>downloadExcelQueryConfig</@ofbizUrl>' + '?queryId=' + '${queryId?if_exists}' + 
        '&cond0Info=' + QueryConfigViewManagement.getParam(form, 'cond0Info') +
        '&cond1Info=' + QueryConfigViewManagement.getParam(form, 'cond1Info') +
        '&cond2Info=' + QueryConfigViewManagement.getParam(form, 'cond2Info') +
        '&cond3Info=' + QueryConfigViewManagement.getParam(form, 'cond3Info') +
        '&cond4Info=' + QueryConfigViewManagement.getParam(form, 'cond4Info') +
        '&cond5Info=' + QueryConfigViewManagement.getParam(form, 'cond5Info') +
        '&cond6Info=' + QueryConfigViewManagement.getParam(form, 'cond6Info') +
        '&cond7Info=' + QueryConfigViewManagement.getParam(form, 'cond7Info') +
        '&cond8Info=' + QueryConfigViewManagement.getParam(form, 'cond8Info');
        linkExcel.click();
    },
    
    executeQuery: function() {
        var form = QueryConfigViewManagement.loadManagementForm();
        new Ajax.Request("<@ofbizUrl>executeQueryConfig</@ofbizUrl>", {
            parameters: {
                "queryId": '${queryId?if_exists}', 
                "cond0Info": QueryConfigViewManagement.getParam(form, 'cond0Info'),
                "cond1Info": QueryConfigViewManagement.getParam(form, 'cond1Info'),
                "cond2Info": QueryConfigViewManagement.getParam(form, 'cond2Info'),
                "cond3Info": QueryConfigViewManagement.getParam(form, 'cond3Info'),
                "cond4Info": QueryConfigViewManagement.getParam(form, 'cond4Info'),
                "cond5Info": QueryConfigViewManagement.getParam(form, 'cond5Info'),
                "cond6Info": QueryConfigViewManagement.getParam(form, 'cond6Info'),
                "cond7Info": QueryConfigViewManagement.getParam(form, 'cond7Info'),
                "cond8Info": QueryConfigViewManagement.getParam(form, 'cond8Info')
            },
            onSuccess: function(response) {
            	var data = response.responseText.evalJSON(true);
                
                if (data["_ERROR_MESSAGE_"] != null || data["_ERROR_MESSAGE_LIST_"] != null) {
                	modal_box_messages._resetMessages();
                    modal_box_messages.onAjaxLoad(data, Prototype.K);
                    return false;
                }
                modal_box_messages._resetMessages();
                modal_box_messages.alert(['WorkeffortExtUiLabels', 'QueryConfigSuccsfullyExecuted']);
                
                Utils.stopWaiting();
            }
        });
        Utils.startWaiting();
    },
    
    loadManagementForm : function() {
	    var form = null;
	
     	var myTabs = Control.Tabs.instances[0];
     	var containerSelected = null;
     	if(!myTabs) {
     		containerSelected = $('main-container')
     	} else {
     		containerSelected = $(myTabs.getActiveContainer());
     	}
     	
    	if (containerSelected) {	     	
	     	form = $(containerSelected).down('form.basic-form');
	    }
	    return form;	
	},
	
	getParam: function(form, fieldName) {
	    var field = $(form).down("textarea[name='" + fieldName + "']");
	    if (field) {
	        return field.getValue() != null && field.getValue() != '' ? encodeURIComponent(field.getValue()) : '';
	    }
	    return '';
	}
}