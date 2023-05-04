WorkEffortAssignmentViewExtension = {
    // Manage onSelectEnd for open portlet
    load: function(newContentToExplore, withoutResponder) {
        WorkEffortAssignmentViewExtension.rootInqyTree = '${parameters.rootInqyTree?if_exists}';
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
        
        if (! newContentToExplore) {
           newContentToExplore = $$("div.child-management-container")[0];
        }
        
        // the tab is one of all tab, so find only tab for entityName = WorkEffortAssocExtView
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management-container") 
            && newContentToExplore.identify().indexOf("WorkEffortAssignment") > -1) {
            //Gestione form
            WorkEffortAssignmentViewExtension.registerForm(newContentToExplore);
            if (WorkEffortAssignmentViewExtension.rootInqyTree != 'Y') {
                var table = newContentToExplore.down("table.selectable");
                if (table) {
                    var bodyRows = TableKit.getBodyRows(table);
                    if (bodyRows && bodyRows.size() > 0){
                        bodyRows.each(function(row, rowIndex) {
	               	        WorkEffortAssignmentViewExtension.manageCauseAndReplacementAndWorkeffortFields(row, rowIndex, false);
	               	        WorkEffortAssignmentViewExtension.manageWorkEffortField(row, rowIndex, false);
	                    });
                    }
                }
            }
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
        		var endDateField = $(selectRow.down("input[name='endDate_o_" + rowIndex + "']"));
                if (Object.isElement(endDateField)) {
                    Event.observe(endDateField, 'blur', function(e) {
                        Event.stop(e);
            	        WorkEffortAssignmentViewExtension.manageCauseAndReplacementAndWorkeffortFields(selectRow, rowIndex, true);    	
                    });
                }
                var form = $(selectRow.up("form"));
                var formName = form.readAttribute("name");
                if (Object.isElement(form)) {
                    var endReplacementDropList = DropListMgr.getDropList(formName + "_endReplacementEnumId_o_" + rowIndex);
        	        if (endReplacementDropList) {
        	            endReplacementDropList.registerOnChangeListener(WorkEffortAssignmentViewExtension.manageWorkEffortField.curry(selectRow, rowIndex, true), "manageWorkEffortField_" + rowIndex);
        	
        	        }
        	    }              
                if (Object.isElement(endDateField)) {
                    var partyIdLookup = $(selectRow.down("div#" + formName + "_partyId_o_" + rowIndex));
    		        if(Object.isElement(partyIdLookup)) {
    			       var lookup = LookupMgr.getLookup(partyIdLookup.identify());
                        if (lookup) {
                	        lookup.registerOnSetInputFieldValue(WorkEffortAssignmentViewExtension.managePersonFields.curry(selectRow, rowIndex), "managePersonFields_" + rowIndex);
                        }
    		        }
    		        var partyIdDropList = DropListMgr.getDropList(formName + "_partyId_o_" + rowIndex);
        	        if (partyIdDropList) {
        	            partyIdDropList.registerOnChangeListener(WorkEffortAssignmentViewExtension.managePersonFields.curry(selectRow, rowIndex), "managePersonFields_" + rowIndex);
        	
        	        }
    		        var periodFromDateDropList = DropListMgr.getDropList(formName + "_periodFromDate_o_" + rowIndex);
        	        if (periodFromDateDropList) {
        	            periodFromDateDropList.registerOnChangeListener(WorkEffortAssignmentViewExtension.managePersonFields.curry(selectRow, rowIndex), "managePersonFields_" + rowIndex);
        	
        	        } 
    		        var periodThruDateDropList = DropListMgr.getDropList(formName + "_periodThruDate_o_" + rowIndex);
        	        if (periodThruDateDropList) {
        	            periodThruDateDropList.registerOnChangeListener(WorkEffortAssignmentViewExtension.managePersonFields.curry(selectRow, rowIndex), "managePersonFields_" + rowIndex);
        	
        	        }
        			var fromDateField = $(selectRow.down("input[name='fromDate_o_" + rowIndex + "']"));
                	if (Object.isElement(fromDateField)) {
                    	Event.observe(fromDateField, 'blur', function(e) {
                        	Event.stop(e);
            	        	WorkEffortAssignmentViewExtension.managePersonFields(selectRow, rowIndex);    	
                    	});
                	} 
        			var thruDateField = $(selectRow.down("input[name='thruDate_o_" + rowIndex + "']"));
                	if (Object.isElement(thruDateField)) {
                    	Event.observe(thruDateField, 'blur', function(e) {
                        	Event.stop(e);
            	        	WorkEffortAssignmentViewExtension.managePersonFields(selectRow, rowIndex);    	
                    	});
                	}                 	       	                 	               	        
    		    }
                
      		    var showComment = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "showComment", rowIndex);
        		var insertMode = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "insertMode", rowIndex);
                if (insertMode != 'Y' && showComment && showComment == "Y"){
        		    var workEffortId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "workEffortId", rowIndex);
        			var partyId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "partyId", rowIndex);
        			var roleTypeId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "roleTypeId", rowIndex);
        			var fromDate = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "fromDate", rowIndex);
        			var isObiettivo = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "isObiettivo", rowIndex);
        			var weTypeSubId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "weTypeSubId", rowIndex);
        			var weTypeSubWeId = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "weTypeSubWeId", rowIndex);
        			
        			var entityName = WorkEffortAssignmentViewExtension.getFieldValue(selectRow, "entityName", rowIndex);
        			     			      			
        			ajaxUpdateArea(transactionPanelId, "<@ofbizUrl>reloadWorkEffortAssignmentViewPanel</@ofbizUrl>", $H({"workEffortId" : workEffortId, "partyId": partyId, 
                        "roleTypeId": roleTypeId, "fromDate": fromDate, "saveView" : "N", "rootInqyTree" : WorkEffortAssignmentViewExtension.rootInqyTree, "isObiettivo" : isObiettivo,
                        "weTypeSubId": weTypeSubId, "weTypeSubWeId": weTypeSubWeId, "entityName": entityName}));
        		} else {
        		    WorkEffortAssignmentViewExtension.deletePortlet();
                }
        	}
        }
    },
    
    manageCauseAndReplacementAndWorkeffortFields: function(row, rowIndex, clearFields) {
        var endDateField = $(row.down("input[name='endDate_o_" + rowIndex + "']"));
        if (Object.isElement(endDateField)) {
            var form = $(row.up("form"));
            if (Object.isElement(form)) {
                var formName = form.readAttribute("name");
            	var endDate = endDateField.getValue();
            	var endCauseDropList = DropListMgr.getDropList(formName + "_endCauseEnumId_o_" + rowIndex);
            	if (endCauseDropList) {
            	    if (endDate != '') {
            	        endCauseDropList._editField.removeAttribute("readonly");
        			    endCauseDropList._editField.removeClassName("readonly");
        			    endCauseDropList._submitField.show();
            	    } else {
            	        endCauseDropList._editField.setAttribute("readonly", "readonly");
        		        endCauseDropList._editField.addClassName("readonly");
        		        endCauseDropList._submitField.hide();
        		        if (clearFields) {
        		            endCauseDropList._editField.clear();
        				    endCauseDropList._codeField.clear();
        		        }
            	    }
            	}
            	var endReplacementDropList = DropListMgr.getDropList(formName + "_endReplacementEnumId_o_" + rowIndex);
            	if (endReplacementDropList) {
            	    if (endDate != '') {
            	        endReplacementDropList._editField.removeAttribute("readonly");
        			    endReplacementDropList._editField.removeClassName("readonly");
        			    endReplacementDropList._submitField.show();
            	    } else {
            	        endReplacementDropList._editField.setAttribute("readonly", "readonly");
        		        endReplacementDropList._editField.addClassName("readonly");
        		        endReplacementDropList._submitField.hide();
        		        if (clearFields) {
        		            endReplacementDropList._editField.clear();
        				    endReplacementDropList._codeField.clear();
        		        }
            	    }
            	}
            	var endWorkEffortDropList = DropListMgr.getDropList(formName + "_endWorkEffortId_o_" + rowIndex);
            	if (endWorkEffortDropList) {
            	    if (endDate == '') {
            	        endWorkEffortDropList._editField.setAttribute("readonly", "readonly");
        		        endWorkEffortDropList._editField.addClassName("readonly");
        		        endWorkEffortDropList._submitField.hide();
        		        if (clearFields) {
        		            endWorkEffortDropList._editField.clear();
        				    endWorkEffortDropList._codeField.clear();
        		        }            	    
            	    }
            	}
        	}
        }
    },
    
    manageWorkEffortField: function(row, rowIndex, clearFields) {
        var form = $(row.up("form"));
        if (Object.isElement(form)) {
            var formName = form.readAttribute("name");
            var endReplacementDropList = DropListMgr.getDropList(formName + "_endReplacementEnumId_o_" + rowIndex);
        	if (endReplacementDropList && endReplacementDropList._codeField) {
                var endReplacementValue = endReplacementDropList._codeField.getValue();
            	var endWorkEffortDropList = DropListMgr.getDropList(formName + "_endWorkEffortId_o_" + rowIndex);
            	if (endWorkEffortDropList) {
                	if (endReplacementValue == "END_SOST_NAGR") {
                    	endWorkEffortDropList._editField.removeAttribute("readonly");
        				endWorkEffortDropList._editField.removeClassName("readonly");
        				endWorkEffortDropList._submitField.show();
                	} else {
             	    	endWorkEffortDropList._editField.setAttribute("readonly", "readonly");
        		    	endWorkEffortDropList._editField.addClassName("readonly");
        		    	endWorkEffortDropList._submitField.hide();
        		    	if (clearFields) {
        		        	endWorkEffortDropList._editField.clear();
        					endWorkEffortDropList._codeField.clear();
        		    	}                
                	}
            	}           
        	}            
        }
    },
    
    managePersonFields: function(row, rowIndex) {
        var partyId = "";
        var weTypeSubFilter = "";
        var fromDate = null;
        var thruDate = null;
        var periodFromDate = "";
        var periodThruDate = "";
        var form = $(row.up("form"));
        if (Object.isElement(form)) {
            var partyIdField = $(form.down("input[name='partyId_o_" + rowIndex + "']"));
            if (Object.isElement(partyIdField)) {
                partyId = partyIdField.getValue();
            }
            var weTypeSubFilterField = $(form.down("input[name='weTypeSubFilter_o_" + rowIndex + "']"));
            if (Object.isElement(weTypeSubFilterField)) {
                weTypeSubFilter = weTypeSubFilterField.getValue();
            }
            var fromDateField = $(form.down("input[name='fromDate_o_" + rowIndex + "']"));
            if (Object.isElement(fromDateField)) {
                fromDate = fromDateField.getValue();
            }
            var thruDateField = $(form.down("input[name='thruDate_o_" + rowIndex + "']"));
            if (Object.isElement(thruDateField)) {
                thruDate = thruDateField.getValue();
            }
            var periodFromDateField = $(form.down("input[name='periodFromDate_o_" + rowIndex + "']"));
            if (Object.isElement(periodFromDateField)) {
                periodFromDate = periodFromDateField.getValue();
            } 
            var periodThruDateField = $(form.down("input[name='periodThruDate_o_" + rowIndex + "']"));
            if (Object.isElement(periodThruDateField)) {
                periodThruDate = periodThruDateField.getValue();
            }                        
            
            if (partyId != "") {
                new Ajax.Request("<@ofbizUrl>getAssignmentViewPersonFields</@ofbizUrl>", {
	                parameters: {"partyId": partyId, "weTypeSubFilter": weTypeSubFilter, "fromDate": fromDate, "thruDate": thruDate, "periodFromDate": periodFromDate, "periodThruDate": periodThruDate},
	                onSuccess: function(response) {
	                    var data = response.responseText.evalJSON(true);
	                    if (data) {
	                        var personCommentsField = $(form.down("input[name='personComments_o_" + rowIndex + "']"));
	                        if (Object.isElement(personCommentsField)) {
	                            var personComments = data.personComments ? data.personComments : "";
	                            personCommentsField.setValue(personComments);
	                        }
	                        var personAllocationField = $(form.down("input[name='personAllocation_o_" + rowIndex + "']"));
	                        if (Object.isElement(personAllocationField)) {
	                            var personAllocation = data.personAllocation ? data.personAllocation : "";
	                            personAllocationField.setValue(personAllocation);
	                        }
	                    }
	                },
	                onFailure: function() {
	                }
	            });
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