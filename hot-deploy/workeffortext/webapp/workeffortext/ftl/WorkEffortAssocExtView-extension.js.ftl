WorkEffortAssocExtViewExtension = {
    // Manage onSelectEnd for open portlet
    // and dblclick
    
    load: function(newContentToExplore, withoutResponder) {
    	// newContentToExplore is null only the first time,
        // then newContentToExplore is a tab or a div, even if with other entityName
        
        // Special Case
        // If the user add o remove a record from list, the event "onSelectEnd" is on a table that not in HTML,
        // so, the screenletName, in method manageSelected, is null,
        // the method load is recall when the table is replaced in HTML, so
        // the newContentToExplore is a smaller div, with class 'child-management', and it call manageSelected
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management") 
                && newContentToExplore.identify().indexOf("WorkEffortAssocExtView") > -1) {
            assocListTable = newContentToExplore.down("table.selectable");
            if(Object.isElement(assocListTable)) {
                WorkEffortAssocExtViewExtension.onPanelSelectManagement(assocListTable);
            }
        }
        
        // If newContentToExplore is a tab, find the div with class 'child-management-container'
        if(Object.isElement(newContentToExplore)  && !newContentToExplore.hasClassName("child-management-container")) {
            newContentToExplore = newContentToExplore.down("div.child-management-container");
        }
        
        // the tab is one of all tab, so find only tab for entityName = WorkEffortAssocExtView
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management-container") 
            && newContentToExplore.identify().indexOf("WorkEffortAssocExtView") > -1) {
            //Gestione form
            WorkEffortAssocExtViewExtension.registerForm(newContentToExplore);
        }
  
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortAssocExtViewExtension.responder, "WorkEffortAssocExtViewExtension");
        }
    },
    
    /**
    * Responder functions
    **/
    responder : {
    	onAfterLoad : function(newContent) {
    		WorkEffortAssocExtViewExtension.load(newContent, true);
        },
        unLoad : function() {
    		return typeof "WorkEffortAssocExtViewExtension" === "undefined";
        }
    },
    
    registerForm: function(newContentToExplore) {
    	var table = newContentToExplore.down('table.selectable');
                if (Object.isElement(table)) {
	                if(!TableKit.isRegistered(table, "onSelectEnd", "WorkEffortAssocExtViewExtension_selectEnd")){
                        TableKit.registerObserver(table, "onSelectEnd", "WorkEffortAssocExtViewExtension_selectEnd", WorkEffortAssocExtViewExtension.onPanelSelectManagement);
                        WorkEffortAssocExtViewExtension.onPanelSelectManagement(table);
                    }
                    // rimossa la registrazione del dlbClick
	            }
    },
    
     deletePortlet: function(e) {
     	var table = e;
     
    	//Elimino la portlet
		var portlets = $$("div.transactionPortlet");
		portlets.each(function(portlet) {
	    	if (Object.isElement(portlet) && Object.isElement(portlet.down())) {
	        	portlet.down().remove();
	    	}
		});
    },
     
     onPanelSelectManagement: function(e) {
    	var table = e;

        var screenletName = $(table).up("div.child-management-container");
        if (!Object.isElement(screenletName)) {
            // Special Case
            // If the user add o remove a record from list, the event "onSelectEnd" is on a table that not in HTML,
            // so, the screenletName is null and return.
            // the method load is recall when the table is replaced in HTML, so
            // the newContentToExplore is a smaller div, with class 'child-management', and it call manageSelected
            return;
        }
        var transactionPanel = $(screenletName).down("div.standardPortlet");
        var transactionPanelId = transactionPanel.identify()
        
        var resultcheckModification = false;
        var form = transactionPanel.down("form.basic-form");
        if (Object.isElement(form)) {
            var resultcheckModification = FormKitExtension.checkModficationWithAlert(form);
        }
        
        if(table.tagName != "TABLE") {
            var element = Event.element(e);
            table = element.up("table");
        }
        var selectedRow = TableKit.Selectable.getSelectedRows(table)[0];
        if(selectedRow) {
        	
	        var rowIndex = TableKit.getRowIndex(selectedRow);
	        
	        var workEffortIdTo = selectedRow.down("input[name='workEffortIdTo_o_" + rowIndex + "']");
	        var workEffortIdFrom = selectedRow.down("input[name='workEffortIdFrom_o_" + rowIndex + "']");
	        var fromDate = selectedRow.down("input[name='fromDate_o_" + rowIndex + "']");
	        var workEffortAssocTypeId = selectedRow.down("input[name='workEffortAssocTypeId_o_" + rowIndex + "']");
	        
	        var contentId = selectedRow.down("input[name='contentId_o_" + rowIndex + "']");
            
	        // TODO a che serve? 
	        // LookupProperties.registerAfterLoadModal(WorkEffortAssocExtViewExtension.deletePortlet.curry(table));
	        
	        var insertMode = selectedRow.down('input[name=\"insertMode_o_' + rowIndex + '\"]').getValue();
	        var showComment = "";
	        var showCommentField = selectedRow.down('input[name=\"showComment_o_' + rowIndex + '\"]');
	        if (Object.isElement(showCommentField)) {
	        	showComment = showCommentField.getValue();
	        }
	        if (insertMode != 'Y' && showComment == 'Y') {
	        	if (workEffortIdTo.getValue() != "" && workEffortIdTo.getValue() != null) {
	        
		        	ajaxUpdateArea(transactionPanelId, "<@ofbizUrl>reloadWorkEffortAssocExtViewPanel</@ofbizUrl>", 
		        		$H({"workEffortIdTo" : workEffortIdTo.getValue(),
		         		"workEffortIdFrom" : workEffortIdFrom.getValue(),
			            "fromDate" : fromDate.getValue(),
			            "workEffortAssocTypeId" : workEffortAssocTypeId.getValue(),
			            "operation" : "UPDATE",
			            "rootInqyTree" : "${parameters.rootInqyTree?if_exists}",
		    	        "saveView" : "N", "contentId" : contentId.getValue()}));
	         	}
	        }else {
	        	WorkEffortAssocExtViewExtension.deletePortlet();
	     	}
        }
        return true;
    }
}


WorkEffortAssocExtViewExtension.load();
