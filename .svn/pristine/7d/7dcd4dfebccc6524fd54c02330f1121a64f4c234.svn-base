<script type="text/javascript">
    MassivePrintPopupMgr = Class.create({ });

    MassivePrintPopupMgr.getParametersToSubmit = function(enabledSendMail) {
        var container = $('popup-print-radio-container')
        var checkedTypeEkement = container.select('input.print-type-radio').find(function(element) {
            return element.checked;
        });
        var checkedEkement = container.select('input.print-radio').find(function(element) {
            return element.checked;
        });
        var checkedParamsEkement = container.select('input.print-addparams-radio').find(function(element) {
            return element.checked;
        });
        if (Object.isElement(checkedEkement) && Object.isElement(checkedTypeEkement)) {
            var parametersToSubmit = $H({});
            parametersToSubmit.set('reportContentId', checkedEkement.getValue());
            parametersToSubmit.set('outputFormat', checkedTypeEkement.getValue());
            
            if(Object.isElement(checkedParamsEkement)){
            	parametersToSubmit.set('filterContentId', checkedParamsEkement.getValue());
            }
            
            
            container.select('input.print-parameters','select.print-parameters').each(function(element) {
                parametersToSubmit.set(element.readAttribute('name'), element.getValue());
            });
            
            parametersToSubmit.set('entityNamePrefix', 'WorkEffortRoot');
            parametersToSubmit.set('entityName', 'WorkEffortRootView');
            
            parametersToSubmit.set('responsiblePartyId', '${parameters.responsiblePartyId?if_exists}');
            parametersToSubmit.set('responsibleRoleTypeId', '${parameters.responsibleRoleTypeId?if_exists}');
            
            parametersToSubmit.set('weResponsiblePartyId', '${parameters.weResponsiblePartyId?if_exists}');
            parametersToSubmit.set('weResponsibleRoleTypeId', '${parameters.weResponsibleRoleTypeId?if_exists}');
            
            parametersToSubmit.set('searchDate', '${parameters.searchDate?if_exists}');
            
            parametersToSubmit.set('evalManagerPartyId', '${parameters.evalManagerPartyId?if_exists}');
            parametersToSubmit.set('evalPartyId', '${parameters.evalPartyId?if_exists}');
            
            parametersToSubmit.set('currentStatusContains', '${parameters.currentStatusContains?if_exists}');
            
            var containerSendMail = $('popup-print-send-mail');
        	if (Object.isElement(containerSendMail)){
	        	containerSendMail.select('input','textarea').each(function(element) {
	                parametersToSubmit.set(element.readAttribute('name'), element.getValue());
	            });
	            
	            parametersToSubmit.set("enabledSendMail", enabledSendMail);
        	}
            return parametersToSubmit;
        }
        return false;
    }
    
    MassivePrintPopupMgr.validate = function(parametersToSubmit, callBack) {
        if (parametersToSubmit && Object.isFunction(callBack)) {
            parametersToSubmit = $H(parametersToSubmit);
            new Ajax.Request('<@ofbizUrl>validateManagementPrintBirt</@ofbizUrl>', {
                parameters : parametersToSubmit.toObject(),
                onSuccess: function(response) {
                    var data = {};
                    try {
                        data = response.responseText.evalJSON(true);
                    } catch(e) {}
    
                    if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                        callBack();
                    } else {
                        modal_box_messages.onAjaxLoad(data, Prototype.K)
                    }
    
                }
            });
        }
    }
    
    MassivePrintPopupMgr.validateSendEmailFields = function() {   
        var massivePrint = '${enableSendMassivePrintMail?if_exists}';
        if(! massivePrint || massivePrint != 'Y') {
        	return true;
        }
        var subject = '';
        var content = '';
        
        if(document.getElementById('NewEmailExt_subject')) {
            subject = document.getElementById('NewEmailExt_subject').value;
            if(subject) {
                subject = subject.trim();
            }
        }
        if(document.getElementById('NewEmailExt_content')) {
            content = document.getElementById('NewEmailExt_content').value;
            if(content) {
                content = content.trim();
            }
        }        

    	var valid = true;
    	var messages = new Array();
    	
    	if(! subject || subject == '') {
    	    messages.push('${uiLabelMap.BaseSendMassiveSubjectMandatory}');
    	    valid = false;
    	}
    	if(! content || content == '') {
    	    messages.push('${uiLabelMap.BaseSendMassiveContentMandatory}');
    	    valid = false;
    	}
    	if(! valid) {
    	    var msg = '';
    	    for(var i = 0; i < messages.length; i++) {
    	        msg += messages[i];
    	        if(i < messages.length - 1) {
    	        	msg += '<br/>';
    	        } 	        
    	    }
    	    if(msg != '') {
    	    	modal_box_messages._resetMessages();
            	modal_box_messages.alert(msg);    	    
    	    }
    	}
    	
    	return valid;
    }    
    
    MassivePrintPopupMgr.load = function() {
        var newContent = $('MB_window');
        if (typeof CalendarDateSelect != 'undefined') {
            CalendarDateSelect.reloadCalendar(newContent, true);
        }
        
        $('select-addparams-print-row').hide();
        $('select-type-print-row').hide(); 
    }
    
    MassivePrintPopupMgr.load();
</script>
<div class="popup-print-boxes-container">
    <span class="hidden-label" id="popup-print-box-title">
    	<#if context.enableSendMassivePrintMail?if_exists == 'Y'>${uiLabelMap.BaseSendMassiveEmail}<#else>${uiLabelMap.BaseSelectPrint}</#if>
    </span>
    <div class="popup-print-box-container">
        <div class="popup-print-body-container">
        	<#if context.enableSendMassivePrintMail?if_exists == 'Y'>
            <div id="popup-print-send-mail">
	            <table>
		            <tbody>
			            <tr>
			   				<td style="width: 10em" class="label-for-print-popup">${uiLabelMap.WorkEffortCommunicationEventView_subject}</td>
						   <td class="widget-area-style"><input type="text" id="NewEmailExt_subject" size="60" name="subject" decimal_digits="0" class="input_mask mask_text"></td>
						</tr>
			  			<tr>
						    <td class="label-for-print-popup">${uiLabelMap.WorkEffortCommunicationEventView_content}</td>
						    <td class="widget-area-style"><textarea id="NewEmailExt_content" rows="20" cols="69" name="content"></textarea></td>
						</tr>
					</tbody>
	            </table>
            </div>
            </#if>
            <div id="popup-print-radio-container" class="popup-print-radio-container">
                <#-- Metto i campi -->
                <#if popupFieldContainerScreenLocation?has_content && popupFieldContainerScreenName?has_content>
                ${screens.render(popupFieldContainerScreenLocation, popupFieldContainerScreenName, context)}                
                </#if>
            </div>
        </div>
        <div class="popup-print-buttons-container">        
            <a href="#" class="smallSubmit button-cancel" onclick="javascript:Modalbox.options.afterHide = Modalbox.options.afterHide.curry(false); Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
            
            <a href="#" style="display:none" id="button-ok" class="smallSubmit button-send button-ok" onclick="javascript: if(MassivePrintPopupMgr.validateSendEmailFields() == false){return false;}Modalbox.options.afterHide = Modalbox.options.afterHide.curry(MassivePrintPopupMgr.getParametersToSubmit('Y'), MassivePrintPopupMgr.validate); Modalbox.hide()">${uiLabelMap.BaseButtonPrintAndSendMail}</a>
        	<a href="" id="button-ok-disabled" class="smallSubmit-disabled button-send button-ok-disabled" onclick="javascript: return false;">${uiLabelMap.BaseButtonPrintAndSendMail}</a>
        </div>
    </div>

</div>