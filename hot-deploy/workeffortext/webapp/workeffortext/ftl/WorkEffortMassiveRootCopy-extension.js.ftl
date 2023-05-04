WorkEffortMassiveRootCopyManagement = {	
	load: function() {
	    var container = $("WorkEffortMassiveRootCopySubmit"); 
        var button = $$(".search-execute")[0];
        if(Object.isElement(button) && Object.isElement(container)) {
            Event.stopObserving(button, 'click');
            button.observe("click", function(e) {
                var workEffortRootIdField = container.down("input[name='workEffortRootId']");
                var workEffortRootId = Object.isElement(workEffortRootIdField) ? workEffortRootIdField.getValue() : "";
                var partyIdListField = container.down("input[name='party-list']");
                var partyIdList = Object.isElement(partyIdListField) ? partyIdListField.getValue() : "";
                if (workEffortRootId == "" || partyIdList == "") {
                    modal_box_messages.alert("${uiLabelMap.MassiveRootCopyErrorMandatoryFields}");
                    return false;
                }
                
                new Ajax.Request("<@ofbizUrl>massiveRootCopy</@ofbizUrl>", {
                    parameters: {"workEffortRootId": workEffortRootId, "partyIdList": partyIdList},
                    onSuccess: function(transport) {
                        var data = transport.responseText.evalJSON(true);
                        
                        if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
                            modal_box_messages._resetMessages();
                            modal_box_messages.onAjaxLoad(data, Prototype.K);
                            return false;
                        }
                        
                        var sessionId = data.sessionId;
                        if(sessionId) {
                            modal_box_messages._resetMessages();
                            modal_box_messages.alert("${uiLabelMap.AsynchServiceExecution_sessionId}" + " " + sessionId);
                        }
                    }
                });
            });
        }	       
	}
}
WorkEffortMassiveRootCopyManagement.load();