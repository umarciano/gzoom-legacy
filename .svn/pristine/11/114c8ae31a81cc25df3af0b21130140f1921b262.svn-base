WorkEffortNoteAndDataExtension = {
    // Manage onSelectEnd for open portlet
    // no dblclick for this form
    load: function(newContentToExplore, withoutResponder) {
        // newContentToExplore is null only the first time,
        // then newContentToExplore is a tab or a div, even if with other entityName
        
        // Special Case
        // If the user add o remove a record from list, the event "onSelectEnd" is on a table that not in HTML,
        // so, the screenletName, in method manageSelected, is null,
        // the method load is recall when the table is replaced in HTML, so
        // the newContentToExplore is a smaller div, with class 'child-management', and it call manageSelected
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management") && newContentToExplore.identify().indexOf("WorkEffortNoteAndData") > -1) {
            noteAndDataListTable = newContentToExplore.down("table.selectable");
            if(Object.isElement(noteAndDataListTable)) {
                WorkEffortNoteAndDataExtension.manageSelected(noteAndDataListTable);
            }
        }
        
        // If newContentToExplore is a tab, find the div with class 'child-management-container'
        if(Object.isElement(newContentToExplore)  && !newContentToExplore.hasClassName("child-management-container")) {
            var newContentToExplore = newContentToExplore.down("div.child-management-container");
        }
        
        // the tab is one of all tab, so find only tab for entityName = WorkEffortNoteAndData
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management-container") && newContentToExplore.identify().indexOf("WorkEffortNoteAndData") > -1) {
            noteAndDataListTable = newContentToExplore.down('table.selectable');
            if(Object.isElement(noteAndDataListTable)) {
                    if(!TableKit.isRegistered(noteAndDataListTable, 'onSelectEnd',  'WorkEffortNoteAndDataExtension_selectEnd')){
                        TableKit.registerObserver(noteAndDataListTable, 'onSelectEnd', 'WorkEffortNoteAndDataExtension_selectEnd', WorkEffortNoteAndDataExtension.manageSelected);
                        WorkEffortNoteAndDataExtension.manageSelected(noteAndDataListTable);
                    }
            }
        }
        
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortNoteAndDataExtension.responder, "WorkEffortNoteAndDataExtension");
        }
    },
    
    /**
     * Responder functions
     **/
     responder : {
         onAfterLoad : function(newContent) {
             WorkEffortNoteAndDataExtension.load(newContent, true);
             
         },
         unLoad : function() {
             return typeof "WorkEffortNoteAndDataExtension" === "undefined";
         }
     },
     
     manageSelected: function(table, e) {
        // only active container
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
        // even if there is modification, the row is selected
        if(selectRow != null){
    		var rowIndex = TableKit.getRowIndex(selectRow);
    		var showList = selectRow.down("input[name='showList_o_" + rowIndex + "']");
            var insertMode = selectRow.down("input[name='insertMode_o_" + rowIndex + "']");
            if(showList.getValue() == "N" && insertMode.getValue() == "N") {
                WorkEffortNoteAndDataExtension.reloadNoteDetail(transactionPanelId, selectRow, rowIndex);
            } else {
                // Elimino la portlet in caso di inserimento di nuova riga
                var portlets = $$("div.transactionPortlet");
                portlets.each(function(portlet) {
                    if (Object.isElement(portlet) && Object.isElement(portlet.down())) {
                        portlet.down().remove();
                    }
                });
            }
                		
    		var divDropListNoteNameField = selectRow.down("div#WorkEffortNoteAndDataManagementMultiForm_noteName_o_" +rowIndex);
    		if (Object.isElement(divDropListNoteNameField)) {
            	var dropListNoteName = DropListMgr.getDropList(divDropListNoteNameField.identify());
                if (dropListNoteName) {
                	dropListNoteName.registerOnChangeListener(WorkEffortNoteAndDataExtension.setNoteInfoField.curry(selectRow, rowIndex), 'setNoteInfoField');
                }
    		}
    		
    		var divDropListNoteNameLangField = selectRow.down("div#WorkEffortNoteAndDataManagementMultiForm_noteNameLang_o_" +rowIndex);
    		if (Object.isElement(divDropListNoteNameLangField)) {
            	var dropListNoteNameLang = DropListMgr.getDropList(divDropListNoteNameLangField.identify());
                if (dropListNoteNameLang) {
                	dropListNoteNameLang.registerOnChangeListener(WorkEffortNoteAndDataExtension.setNoteInfoLangField.curry(selectRow, rowIndex), 'setNoteInfoLangField');
                }
    		}    		
    	}
    },
 
    reloadNoteDetail: function(transactionPanelId, selectRow, rowIndex){
        	var isHtml = selectRow.down("input[name='isHtml_o_" + rowIndex + "']");
    		var noteId = selectRow.down("input[name='noteId_o_" + rowIndex + "']");
    		var workEffortId = selectRow.down("input[name='workEffortId_o_" + rowIndex + "']");
    		var entityName = selectRow.down("input[name='entityName_o_" + rowIndex + "']");
    		var noteContentId = selectRow.down("input[name='noteContentId_o_" + rowIndex + "']");
            
    		var isObiettivo = "";
    		var isObiettivoField = selectRow.down("input[name='isObiettivo_o_" + rowIndex + "']");
    		if (Object.isElement(isObiettivoField)) {
    			isObiettivo = isObiettivoField.getValue();
    		}
            	
    		ajaxUpdateArea(transactionPanelId, "<@ofbizUrl>reloadWorkEffortNoteAndDataPanel</@ofbizUrl>", $H({"noteId" : noteId.getValue(), "workEffortId": workEffortId.getValue(), 
    				"noteContentId": noteContentId.getValue(), "saveView" : "N", "rootInqyTree" : "${parameters.rootInqyTree?if_exists}", "isObiettivo" : isObiettivo, "showList" : "N"}));
    },
    
    setNoteInfoField: function(selectRow, rowIndex) {
        var workEffortTypeId = "";
		var workEffortTypeIdField = selectRow.down("input[name='workEffortTypeId_o_" + rowIndex + "']");
		if (Object.isElement(workEffortTypeIdField)) {
			workEffortTypeId = workEffortTypeIdField.getValue();
		}
		var noteName = "";
		var noteNameField = selectRow.down("input[name='noteName_o_" + rowIndex + "']");
		if (Object.isElement(noteNameField)) {
			noteName = noteNameField.getValue();
		}
		
		var noteInfoField = selectRow.down("textarea[name='noteInfo_o_" + rowIndex + "']");
		if (! noteInfoField) {
			noteInfoField = selectRow.down("input[name='noteInfo_o_" + rowIndex + "']");
		}
		var noteInfoLangField = selectRow.down("textarea[name='noteInfoLang_o_" + rowIndex + "']");
		if (! noteInfoLangField) {
			noteInfoLangField = selectRow.down("input[name='noteInfoLang_o_" + rowIndex + "']");
		}		
		
		new Ajax.Request("<@ofbizUrl>getNoteInfoAndNoteInfoLang</@ofbizUrl>", {
			parameters: {"workEffortTypeId": workEffortTypeId, "noteName": noteName},
			onSuccess: function(response) {
				var data = response.responseText.evalJSON(true);
				if (data) {
					if (Object.isElement(noteInfoField)) {
						noteInfoField.setValue(data.noteInfo);
					}
					if (Object.isElement(noteInfoLangField)) {
						noteInfoLangField.setValue(data.noteInfoLang);
					}					
				}
			}
		});
    },
    
    setNoteInfoLangField: function(selectRow, rowIndex) {
        var workEffortTypeId = "";
		var workEffortTypeIdField = selectRow.down("input[name='workEffortTypeId_o_" + rowIndex + "']");
		if (Object.isElement(workEffortTypeIdField)) {
			workEffortTypeId = workEffortTypeIdField.getValue();
		}
		var noteNameLang = "";
		var noteNameLangField = selectRow.down("input[name='noteNameLang_o_" + rowIndex + "']");
		if (Object.isElement(noteNameLangField)) {
			noteNameLang = noteNameLangField.getValue();
		}
		
		var noteInfoLangField = selectRow.down("textarea[name='noteInfoLang_o_" + rowIndex + "']");
		if (! noteInfoLangField) {
			noteInfoLangField = selectRow.down("input[name='noteInfoLang_o_" + rowIndex + "']");
		}
		var noteInfoField = selectRow.down("textarea[name='noteInfo_o_" + rowIndex + "']");
		if (! noteInfoField) {
			noteInfoField = selectRow.down("input[name='noteInfo_o_" + rowIndex + "']");
		}		
		
		new Ajax.Request("<@ofbizUrl>getNoteInfoAndNoteInfoLang</@ofbizUrl>", {
			parameters: {"workEffortTypeId": workEffortTypeId, "noteNameLang": noteNameLang},
			onSuccess: function(response) {
				var data = response.responseText.evalJSON(true);
				if (data) {
			        if (Object.isElement(noteInfoLangField)) {
						noteInfoLangField.setValue(data.noteInfoLang);
					}
					if (Object.isElement(noteInfoField)) {
						noteInfoField.setValue(data.noteInfo);
					}					
				}
			}
		});
    }    
    
}

WorkEffortNoteAndDataExtension.load();