    <#if lockAmountScreenLocation?has_content && lockAmountScreenName?has_content>
    ${screens.render(lockAmountScreenLocation, lockAmountScreenName, context)}
    </#if>
    
    <script type="text/javascript">
        var form = $("LockAmountForm"); 
        var button = $$(".search-execute")[0];
        if(Object.isElement(button) && Object.isElement(form)) {
            button.observe("click", function(e) {
                var button = Event.element(e);
                var target = "<@ofbizUrl>lockAmount</@ofbizUrl>";
                
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
                        
                        var numLocked = data.numLocked;
                        modal_box_messages._resetMessages();
                        modal_box_messages.alert("${uiLabelMap.LockAmount_numLocked}" + " " + numLocked);
                    }
                });
            });
        }
    </script>
