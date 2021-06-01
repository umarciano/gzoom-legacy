WorkEffortAssignmentViewExtension = {
    // Manage onSelectEnd for open portlet
    load: function(newContentToExplore, withoutResponder) {
        // newContentToExplore is null only the first time,
        // then newContentToExplore is a tab or a div, even if with other entityName
        
        // Special Case
        // If the user add o remove a record from list, the event "onSelectEnd" is on a table that not in HTML,
        // so, the screenletName, in method manageSelected, is null,
        // the method load is recall when the table is replaced in HTML, so
        // the newContentToExplore is a smaller div, with class 'child-management', and it call manageSelected
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management") 
                && newContentToExplore.identify().indexOf("WorkEffortAssignment") > -1) {
            assignListTable = newContentToExplore.down("table.selectable");
            if(Object.isElement(assignListTable)) {
                WorkEffortAssignmentViewExtension.manageSelected(assignListTable);
            }
        }
        
        // If newContentToExplore is a tab, find the div with class 'child-management-container'
        if(Object.isElement(newContentToExplore)  && !newContentToExplore.hasClassName("child-management-container")) {
            var newContentToExplore = newContentToExplore.down("div.child-management-container");
        }
        
        // the tab is one of all tab, so find only tab for entityName = WorkEffortAssocExtView
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management-container") 
            && newContentToExplore.identify().indexOf("WorkEffortAssignment") > -1) {
            //Gestione form
            WorkEffortAssignmentViewExtension.registerForm(newContentToExplore);
        }
      
      
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortAssignmentViewExtension.responder, "WorkEffortAssignmentViewExtension");
        }
    },
    
    /**
    * Responder functions
    **/
    responder : {
    	onAfterLoad : function(newContent) {
            WorkEffortAssignmentViewExtension.load(newContent, true);
        },
        unLoad : function() {
            return typeof "WorkEffortAssignmentViewExtension" === "undefined";
        }
    },
    
    registerForm: function(newContentToExplore) {
        var table = newContentToExplore.down('table.selectable');
        if (Object.isElement(table)) {
            if(!TableKit.isRegistered(table, "onSelectEnd", "WorkEffortAssignmentViewExtension_selectEnd")){
                TableKit.registerObserver(table, 'onSelectEnd', 'WorkEffortAssignmentViewExtension_selectEnd', WorkEffortAssignmentViewExtension.manageSelected);
                WorkEffortAssignmentViewExtension.manageSelected(table);
            }
            // TODO dblclick, forse funziona da solo
        }
    },
    
    manageSelected: function(table) {
        if (Object.isElement(table)) {
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
            
            var selectRow = TableKit.Selectable.getSelectedRows(table)[0];    
        	if(selectRow != null){
        		var rowIndex = TableKit.getRowIndex(selectRow);
        		var showComment = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "showComment", rowIndex);
        		var insertMode = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "insertMode", rowIndex);
                if (insertMode != 'Y' && showComment && showComment == "Y"){
        		    var workEffortId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "workEffortId", rowIndex);
        			var partyId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "partyId", rowIndex);
        			var roleTypeId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "roleTypeId", rowIndex);
        			var fromDate = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "fromDate", rowIndex);
        			var isObiettivo = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "isObiettivo", rowIndex);
        			
        			var entityName = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "entityName", rowIndex);
        			
        			ajaxUpdateArea(transactionPanelId, "<@ofbizUrl>reloadWorkEffortAssignmentViewPanel</@ofbizUrl>", $H({"workEffortId" : workEffortId, "partyId": partyId, 
                        "roleTypeId": roleTypeId, "fromDate": fromDate, "saveView" : "N", "rootInqyTree" : "${parameters.rootInqyTree?if_exists}", "isObiettivo" : isObiettivo,
                        "entityName": entityName}));
        		} else {
        		    WorkEffortAssignmentViewExtension.deletePortlet();
                }
        	}
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

    getFieldValue: function(selectRow, fieldName, rowIndex){
    	var fieldValue = "";
    	var field = selectRow.down("input[name='" + fieldName + "_o_" + rowIndex + "']");
    	if (Object.isElement(field)) {
    		fieldValue = field.getValue();
    	}
    	return fieldValue;
    }   
}

WorkEffortAssignmentViewExtension.load();