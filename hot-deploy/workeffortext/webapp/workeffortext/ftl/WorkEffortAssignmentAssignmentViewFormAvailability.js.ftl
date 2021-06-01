
WorkEffortAssignmentAssignmentViewFormAvailability = {
    // TODO gestione Availability
	load: function() {
        WorkEffortAssignmentAssignmentViewFormAvailability.loadForm($("WEAVM001_WorkEffortAssignmentViewExtends"));
	},
	loadForm: function(form) {
		if(Object.isElement(form)) {
			WorkEffortAssignmentAssignmentViewFormAvailability.manageForm(form);
		}
	},
	manageForm: function(form){
	
		var formName = form.readAttribute("name");
		var partyLookup = form.down("div#" + formName + "_partyId");
        if(Object.isElement(partyLookup)) {
			var lookup = LookupMgr.getLookup(partyLookup.identify());
			if(lookup) {
				lookup.registerOnSetInputFieldValue(WorkEffortAssignmentAssignmentViewFormAvailability.partyLookupHandler.curry(partyLookup), 'WorkEffortAssignmentAssignmentViewFormAvailability_partyLookupHandler');
			}
		}

	},
	partyLookupHandler: function(lookupParty) {
        var originalForm = lookupParty.up("form");
		var formCloned = WorkEffortAssignmentAssignmentViewFormAvailability.cloneForm(originalForm);
    	var partyId = $(lookupParty.down("input#partyId"));
    	
    	var partyIdField = new Element("input", { "type": "hidden", "name": "partyId", "value": partyId.getValue() });
    	
        //inserisco nell form tutti i campi richeisti dal servizio
        var fromDate = lookupParty.up("form").down("input[name=fromDate]")
        var fromDateField = new Element("input", { "type": "hidden", "name": "fromDate", "value": fromDate.getValue() });
        
        var thruDate = lookupParty.up("form").down("input[name=thruDate]")
        var thruDateField = new Element("input", { "type": "hidden", "name": "thruDate", "value": thruDate.getValue() });
        
        formCloned.insert(partyIdField);
        formCloned.insert(fromDateField);
        formCloned.insert(thruDateField);
        
 		 		
    	ajaxSubmitFormUpdateAreas(formCloned, '', '', { onComplete : WorkEffortAssignmentAssignmentViewFormAvailability.onComplete });
    	
    	formCloned.remove();
	},
	
	onComplete: function(request) {
		var responseText = request.responseText.evalJSON(true);
		
		var availabilityValue = responseText.availability;
        	
        //prende nome form dall'id
        var form = $("WEAVM001_WorkEffortAssignmentViewExtends");
        if(Object.isElement(form)){
	    	var availability = $(form.name +"_availability");
	    	if(Object.isElement(availability)){
	    		availability.setValue(availabilityValue);
	    	}
    	}
    },
	    
	cloneForm : function(originalForm, fields){
        var newFormId = originalForm.readAttribute("id") + "_clone";
        var form = new Element("form", {"id" : newFormId, "name" : newFormId});
        
        var onclickStr = originalForm.readAttribute("onSubmit");
        
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
                    if(!(fields && fields.size() > 0 && fields.include(pair.key))) {
                        var field = form.getInputs("hidden", pair.key).first();
                        if (field) {
                            field.writeAttribute("value", pair.value);
                        } else {
                            field = new Element("input", { "type": "hidden", "name": pair.key, "value": pair.value });
                            form.insert(field);
                        }
                    }
                });
            }
        }
        document.body.insert(form);
        return form;
    }
}


WorkEffortAssignmentAssignmentViewFormAvailability.load();

