
    <script type="text/javascript">
        var container = $("WorkEffortRootCopySubmit"); 
        var button = $$(".search-execute")[0];
        if(Object.isElement(button) && Object.isElement(container)) {
            button.observe("click", function(e) {
                var button = Event.element(e);
                var form = $("WorkEffortRootCopySubmit").down("form");
                var target = "<@ofbizUrl>workEffortRootCopyAsync</@ofbizUrl>";
                
                //controllo campi obbligatori
                var mandatoryFields = form.select("input.mandatory");
                for(var i = 0; i < mandatoryFields.size() ; i++) {
                    var input = mandatoryFields[i];
                    if(input.getValue() == "") {
                        modal_box_messages.alert(['BaseMessageExecuteMandatoryField']);
                        return false;
                    }
                }

                new Ajax.Request(target, {
                    parameters: form.serialize(true),
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
    </script>
    
<div id="WorkEffortRootCopySubmit" style="position: relative;">
    ${screens.render(submitFormScreenLocation, submitFormScreenName, context)}
    
    <div style="height: 10px;"></div>  
</div>
	
<div style="height: 10px;"></div>	
	