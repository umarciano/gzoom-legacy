WorkEffortRevisionExecute = {        
    load: function() {               
        var form = $("WRM_010_WorkEffortRevision");
        var button = $$(".search-execute")[0];
        var target = "<@ofbizUrl>massiveWorkEffortSnapshot</@ofbizUrl>";
        var workEffortRevisionIdFiled;
        var workEffortRevisionId = '';
        var isStoricizedFiled;
        var isStoricized;
        
        if(Object.isElement(button) && Object.isElement(form)) {
            isStoricizedFiled =  form['isStoricized'];
            isStoricized = '';
            if(Object.isElement(isStoricizedFiled)) {
                isStoricized = $(isStoricizedFiled).getValue();
            }               
            workEffortRevisionIdFiled =  form['workEffortRevisionId'];
            if(Object.isElement(workEffortRevisionIdFiled)) {
                workEffortRevisionId = $(workEffortRevisionIdFiled).getValue();
            }

            button.observe("click", function(e) {
                var button = Event.element(e);               
                
                //controllo campi obbligatori
                var mandatoryFields = form.select("input.mandatory");
                for(var i = 0; i < mandatoryFields.size() ; i++) {
                    var input = mandatoryFields[i];
                    if(input.getValue() == "") {
                        modal_box_messages.alert(['BaseMessageExecuteMandatoryField']);
                        return false;
                    }
                }
                
                if(isStoricized == 'Y') {
                    modal_box_messages.confirm('${uiLabelMap.ExecuteRevisionNewStoricizationConfirm}', null, launchStoricization);
                } else {
                    modal_box_messages.confirm('${uiLabelMap.ExecuteRevisionStoricizationConfirm}', null, launchStoricization);
                }
            });
        }
        
        function launchStoricization() {
                new Ajax.Request(target, {
                    parameters: {"workEffortRevisionId": workEffortRevisionId},
                    onSuccess: function(transport) {
                        var data = transport.responseText.evalJSON(true);
                        
                        if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
                            modal_box_messages._resetMessages();
                            modal_box_messages.onAjaxLoad(data, Prototype.K);
                            return false;
                        }
                        
                        var jobLogId = data.jobLogId;
	                    if(jobLogId) {
	                	    modal_box_messages._loadMessage(null, 'BaseMessageStandardImport', function(msg) {
                    		    msg = msg.replace("jobLogId", jobLogId);
                    		    msg = msg.replace("blockingErrors", data.blockingErrors);
                    		    modal_box_messages.alert(msg);
                    	    });
                	    }

                        var isStoricizedOut = data.isStoricized;
                        if(isStoricizedOut && Object.isElement(isStoricizedFiled)) {
                            $(isStoricizedFiled).setValue(isStoricizedOut);
                            isStoricized = $(isStoricizedFiled).getValue();
                        }
                    }
                });        
        }
    }    
}

WorkEffortRevisionExecute.load();        