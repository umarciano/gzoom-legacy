QueryConfigViewManagement = {
    showQueryInfo: function () {
    	var cond0Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond0Info');
    	var cond1Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond1Info');
    	var cond2Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond2Info');
    	var cond3Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond3Info');
    	var cond4Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond4Info');
    	var cond5Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond5Info');
    	var cond6Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond6Info');
    	var cond7Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond7Info');
    	var cond8Info = QueryConfigViewManagement.getParam(QueryConfigViewManagement.loadManagementForm(), 'cond8Info');
    	var preQuery = $('query-info-cnt').down("pre");
    	preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND0#', decodeURIComponent(cond0Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND1#', decodeURIComponent(cond1Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND2#', decodeURIComponent(cond2Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND3#', decodeURIComponent(cond3Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND4#', decodeURIComponent(cond4Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND5#', decodeURIComponent(cond5Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND6#', decodeURIComponent(cond6Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND7#', decodeURIComponent(cond7Info));
        preQuery.innerHTML = preQuery.innerHTML.replaceAll('#COND8#', decodeURIComponent(cond8Info));
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
                var jobLogId = data.jobLogId;
                console.log(" data ", data);
                console.log(" jobLogId " + jobLogId);
                if(jobLogId) {
                    modal_box_messages._resetMessages();
                    modal_box_messages.alert("${uiLabelMap.QueryConfigSuccsfullyExecuted}<br><br>${uiLabelMap.StandardImport_jobLogId}" + " " + jobLogId);
                } else {
                    modal_box_messages._resetMessages();
                    modal_box_messages.alert("${uiLabelMap.QueryConfigSuccsfullyExecuted}");
                }
                
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