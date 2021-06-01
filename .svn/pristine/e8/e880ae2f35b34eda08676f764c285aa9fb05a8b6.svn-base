<form method="post" action="<@ofbizUrl>managementPrintBirt</@ofbizUrl>"  class="basic-form" id="${printBirtFormId?default("ManagementPrintBirtForm")}" target="_blank">
    <input type="hidden" name="externalLoginKey" value="${externalLoginKey}"/>
    <#if printBirtScreenLocation?has_content && printBirtScreenName?has_content>
    ${screens.render(printBirtScreenLocation, printBirtScreenName, context)}
    </#if>
    <table cellspacing="0" cellpadding="0" class="single-editable" style="margin-top: 1.3em; margin-bottom: 1.3em">
        <tbody>
            <tr>
                <td class="label">
                    <a href="#" id="button-ok" class="smallSubmit button-ok" onclick="javascript: PrintBirtFormWrapper.submitForm($('${printBirtFormId?default("ManagementPrintBirtForm")}')); return false;">${uiLabelMap.BaseButtonPrint}</a>
                    <a href="" id="button-ok-disabled" class="smallSubmit-disabled button-ok" onclick="javascript: return false;">${uiLabelMap.BaseButtonPrint}</a>
                    <a href="#" id="button-mail" style="display: none;" class="smallSubmit button-mail" onclick="javascript: PrintBirtFormWrapper.submitFormEmail($('${printBirtFormId?default("ManagementPrintBirtForm")}')); return false;">${uiLabelMap.BaseEmail}</a>
                </td>
            </tr>
        <tbody>
    </table>
</form>
<script type="text/javascript">
    PrintBirtFormWrapper = {
        submitForm : function(form) {
            if (Object.isElement(form)) {
                var checkedEkement = form.select('input.print-radio').find(function(element) {
                    return element.checked;
                });
                if (Object.isElement(checkedEkement)) {
                    var reportId=checkedEkement.getValue();
                    
                    if (reportId) {
		                //controllo campi obbligatori
		                var mandatoryFields = form.select("input.mandatory");
		                for(var i = 0; i < mandatoryFields.size() ; i++) {
		                    var input = mandatoryFields[i];
		                    if(input.getValue() == "") {
		                        modal_box_messages.alert(['BaseMessageExecuteMandatoryField']);
		                        return false;
		                    }
		                }
		                
                        var parametersMap = $H(Form.serialize(form, true));
                        parametersMap.set('reportContentId', reportId);
                        parametersMap.set('externalLoginKey', '${externalLoginKey}');
                        new Ajax.Request('<@ofbizUrl>validateManagementPrintBirt</@ofbizUrl>', {
                            parameters : parametersMap.toObject(),
                            onSuccess: function(response) {
                                var data = {};
                                try {
                                    data = response.responseText.evalJSON(true);
                                 } catch(e) {}

                                if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                                	var serviceName = checkedEkement.readAttribute("servicename");
                                	var serviceNameField = form.down('input[name=serviceName]');
                                	if (serviceName && ! serviceName.empty()) {
                                		if (! Object.isElement(serviceNameField)) {
                                			form.insert(new Element('input', {name : 'serviceName', type : 'hidden', value : serviceName}));
                                		} else {
                                			serviceNameField.setValue(serviceName);
                                		}
                                		form.writeAttribute('action', '<@ofbizUrl>managementServicePrintBirtAsync</@ofbizUrl>');
                                	} else {
                                		if (Object.isElement(serviceNameField)) {
                                			serviceNameField.remove()
                                		}
                                		form.writeAttribute('action', '<@ofbizUrl>managementPrintBirt</@ofbizUrl>');
                                	}
                                	
                                    form.submit();
                                } else {
                                    //data['messageContext'] = 'BaseValidateReportParameters'

                                    modal_box_messages.onAjaxLoad(data, Prototype.K)
                                }

                            }
                        });
                    }
                }
            }
        },
        submitFormEmail : function(form) {
            if (Object.isElement(form)) {
                var checkedEkement = form.select('input.print-radio').find(function(element) {
                    return element.checked;
                });
                if (Object.isElement(checkedEkement)) {
                    var reportId=checkedEkement.getValue();
                    
                    if (reportId) {
		                //controllo campi obbligatori
		                var mandatoryFields = form.select("input.mandatory");
		                for(var i = 0; i < mandatoryFields.size() ; i++) {
		                    var input = mandatoryFields[i];
		                    if(input.getValue() == "") {
		                        modal_box_messages.alert(['BaseMessageExecuteMandatoryField']);
		                        return false;
		                    }
		                }
		                
                        var parametersMap = $H(Form.serialize(form, true));
                        parametersMap.set('reportContentId', reportId);                        
                        new Ajax.Request('<@ofbizUrl>validateManagementPrintBirt</@ofbizUrl>', {
                            parameters : parametersMap.toObject(),
                            onSuccess: function(response) {
                                var data = {};
                                try {
                                    data = response.responseText.evalJSON(true);
                                } catch(e) {}

                                if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {                                                                   	
                                	new Ajax.Request('<@ofbizUrl>managementPrintBirtSendEmail</@ofbizUrl>', {
                                		parameters : parametersMap.toObject(),
                                		onSuccess: function(response) {
                                		
                                			var data = response.responseText.evalJSON(true);
			    	                    	if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
			    	                            modal_box_messages._resetMessages();
			    	                            modal_box_messages.onAjaxLoad(data, Prototype.K);
			    	                            return false;
			    	                        }
                                			var result = Object.isUndefined(data.resultList) ? data : data.resultList[0];
		    	                            if(result.sessionId) {
		    	                            	var sessionId = result.sessionId;
		    	                            	if(sessionId) {
		 	    	                            	modal_box_messages._loadMessage(null, 'BaseMessageExecute_sessionId', function(msg) {
		 	    	                            		msg = msg.replace("sessionId", sessionId);
		 	    	                            		modal_box_messages.alert(msg);
		 	        	                            	
		 	    	                            	});
		 	    	                            }
		    	                            } 
                                		}
                                	});                              	                               	
                                } else {
                                    modal_box_messages.onAjaxLoad(data, Prototype.K)
                                }

                            }
                        });
                    }
                }
            }
        }
    }
</script>