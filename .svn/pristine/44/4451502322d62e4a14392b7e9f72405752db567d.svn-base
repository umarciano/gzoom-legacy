// TODO sistemare
WorkEffortAssignmentAssignmentViewTableAvailability = {
	originalForm: null,
		
	load: function() {
	    WorkEffortAssignmentAssignmentViewTableAvailability.originalForm = $("WEAVMM004_WorkEffortAssignmentView");
        WorkEffortAssignmentAssignmentViewTableAvailability.loadTable($("table_WEAVMM004_WorkEffortAssignmentView"));
        
	},
	loadTable: function(table) {
	    if(Object.isElement(table) && (typeof TableKit != "undefined")) {
			if (TableKit.isSelectable(table)) {
				WorkEffortAssignmentAssignmentViewTableAvailability.manageSelectedRow(table);
	            TableKit.registerObserver(table, 'onSelectEnd', 'WorkEffortAssignmentAssignmentViewTableAvailability', function(table, e) {
	                WorkEffortAssignmentAssignmentViewTableAvailability.manageSelectedRow(table);
	            });
	            var bodyRows = TableKit.getBodyRows(table);
	            if (bodyRows && bodyRows.size() > 0){
	               	bodyRows.each(function(row, rowIndex) {
	               		WorkEffortAssignmentAssignmentViewTableAvailability.partyDisableRowHandler(row, rowIndex);
	               	});
	            }
			}
		}
	},

	manageSelectedRow: function(table){
		var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
        	var rowIndex = TableKit.getRowIndex(selectedRow);
        	var formName = WorkEffortAssignmentAssignmentViewTableAvailability.originalForm.readAttribute("name");
			
			var partyLookup = selectedRow.down("div#" + formName + "_partyId_o_" + rowIndex);
			
			if(Object.isElement(partyLookup)) {
				var lookup = LookupMgr.getLookup(partyLookup.identify());
				if(lookup) {
					lookup.registerOnSetInputFieldValue(WorkEffortAssignmentAssignmentViewTableAvailability.partyLookupHandler.curry(partyLookup, rowIndex), 'WorkEffortAssignmentAssignmentViewTableAvailability_partyLookupHandler');
				}
			}
		}

	},

	partyDisableRowHandler: function(row, rowIndex) {
		var formName = WorkEffortAssignmentAssignmentViewTableAvailability.originalForm.readAttribute("name");
		
		var span = $(row.down("span#" + formName + "_availability_o_" + rowIndex + "_sp"));
		
		if(Object.isElement(span) && (span.innerHTML == null || span.innerHTML == "" || span.innerHTML == "&nbsp;")) {
    		var partyId = $(row.down("input#partyId_o_" + rowIndex));
        	var partyIdField = new Element("input", { "type": "hidden", "name": "partyId", "value": partyId.getValue() });
            
            //inserisco nell form tutti i campi richeisti dal servizio
            var fromDate = row.down("input[name=fromDate_o_" + rowIndex + "]")
            var fromDateField = new Element("input", { "type": "hidden", "name": "fromDate", "value": fromDate.getValue() });
            
            var thruDate = row.down("input[name=thruDate_o_" + rowIndex + "]")
            var thruDateField = new Element("input", { "type": "hidden", "name": "thruDate", "value": thruDate.getValue() });
            
            WorkEffortAssignmentAssignmentViewTableAvailability.populateClonedForm(rowIndex, partyIdField, fromDateField, thruDateField);
    	}
	},
	
	partyLookupHandler: function(lookupParty, rowIndex) {
		var partyId = $(lookupParty.down("input#partyId_o_" + rowIndex));
    	var partyIdField = new Element("input", { "type": "hidden", "name": "partyId", "value": partyId.getValue() });
        if(partyId.getValue()) {
        	//inserisco nell form tutti i campi richeisti dal servizio
            var fromDate = lookupParty.up("tr").down("input[name=fromDate_o_" + rowIndex + "]")
            var fromDateField = new Element("input", { "type": "hidden", "name": "fromDate", "value": fromDate.getValue() });
            
            var thruDate = lookupParty.up("tr").down("input[name=thruDate_o_" + rowIndex + "]")
            var thruDateField = new Element("input", { "type": "hidden", "name": "thruDate", "value": thruDate.getValue() });
            
            WorkEffortAssignmentAssignmentViewTableAvailability.populateClonedForm(rowIndex, partyIdField, fromDateField, thruDateField);
        }
        
	},
	
	populateClonedForm: function(rowIndex, partyIdField, fromDateField, thruDateField) {
		var formCloned = WorkEffortAssignmentAssignmentViewTableAvailability.cloneForm();
    	formCloned.insert(partyIdField);
        formCloned.insert(fromDateField);
        formCloned.insert(thruDateField);
   
    	ajaxSubmitFormUpdateAreas(formCloned, '', '', { onComplete : function(request) {WorkEffortAssignmentAssignmentViewTableAvailability.onComplete(request, rowIndex)} });
    	
    	formCloned.remove();
	},
	
	onComplete: function(request, rowIndex) {
		var responseText = request.responseText.evalJSON(true);
			
		var availabilityValue = responseText.availability;
		var partyId = request.request.parameters.partyId;
		var form = WorkEffortAssignmentAssignmentViewTableAvailability.originalForm;
		if(Object.isElement(form)) {
        	var availability = $(form.name + "_availability_o_" + rowIndex + "_sp");
	        if(Object.isElement(availability)) {
	        	availability.innerHTML = availabilityValue;
	        }
	    }
    },
    	    
	cloneForm : function(){
        var newFormId = WorkEffortAssignmentAssignmentViewTableAvailability.originalForm.readAttribute("id") + "_clone";
        var form = new Element("form", {"id" : newFormId, "name" : newFormId});
        
        var onclickStr = WorkEffortAssignmentAssignmentViewTableAvailability.originalForm.readAttribute("onSubmit");
        
        var arrayStr = onclickStr.split(',');
		arrayStr[3] = '/workeffortext/control/getWorkEffortPartyAssignmentSumRoleTypeWeight';
		onclickStr = arrayStr.join();
        
        form.writeAttribute("onSubmit", onclickStr);
        form.action='<@ofbizUrl>getWorkEffortPartyAssignmentSumRoleTypeWeight</@ofbizUrl>';
        
        var parameters = onclickStr.substring(onclickStr.lastIndexOf(",")+ 1, onclickStr.indexOf("\')"));
        if (parameters && Object.isString(parameters)) {
            var parametersMap = $H(parameters.toQueryParams());
            if (parametersMap) {
                parametersMap.each(function(pair) {
                	var field = form.getInputs("hidden", pair.key).first();
                    if (field) {
                        field.writeAttribute("value", pair.value);
                    } else {
                        field = new Element("input", { "type": "hidden", "name": pair.key, "value": pair.value });
                        form.insert(field);
                    }
                });
            }
        }
        document.body.insert(form);
        return form;
    }
}

WorkEffortAssignmentAssignmentViewTableAvailability.load();
