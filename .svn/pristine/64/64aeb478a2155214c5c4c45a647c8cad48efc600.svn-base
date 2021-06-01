<script type="text/javascript">
    PrintPopupMgr = Class.create({ });

    PrintPopupMgr.getParametersToSubmit = function() {
        var container = $('popup-print-radio-container');
        var parametersToSubmit =  PrintPopupMgr.getParamToSubmitReport();   
        parametersToSubmit.set('externalLoginKey', '${externalLoginKey}');
        container.select('input#','select#').each(function(element) {
            var type = element.readAttribute('type');
            //se sono nel caso di radio botton vado a prendere solamente quello selezionato!
            if ((type == 'radio' && element.checked) || type != 'radio') {
            	parametersToSubmit.set(element.readAttribute('name'), element.getValue());
            }
            if (type == 'radio' && element.checked) {
            	var serviceName = element.readAttribute('servicename');
            	if (serviceName && ! serviceName.empty()) {
            		parametersToSubmit.set('serviceName', serviceName);
            	}
            }
        });
        return parametersToSubmit;
    }
    PrintPopupMgr.getParamToSubmitReport = function() {
    	//aggiundo l elemento selezionato
    	var params = $H({});
        var containerToElaborate = Control.Tabs.instances[0] ? Control.Tabs.instances[0].getActiveContainer() : undefined;
        if (containerToElaborate) {
            var tables = Object.isElement($(containerToElaborate)) ? $(containerToElaborate).select('table') : TableKit.tables;
            var useDelimiterToGet = true;
            tables.each(function(table) {
                if(TableKit.isSelectable(table)) {
                    if(table.hasClassName('multi-editable')){
                        useDelimiterToGet = false;
                    }
                    var selectedRows = TableKit.Selectable.getSelectedRows(table);
                    if (selectedRows && selectedRows.size() > 0) {
                        var paramToSubmitReport = null;
                        var row = selectedRows[0];  
                        $A(row.select('input')).each(function(element) {
                            var elementName = element.readAttribute("name");
                            if(!useDelimiterToGet && elementName.indexOf('_o_') > -1) {
                                elementName = elementName.substring(0,elementName.indexOf('_o_'));
                            }
                            if (elementName == 'paramToSubmitReport') {
                                paramToSubmitReport = element.getValue();
                                return true;
                            }
                        });
                         if (paramToSubmitReport != null && paramToSubmitReport != '') {
                            $A(row.select('input')).each(function(element) {
                                 var elementName = element.readAttribute("name");
                                 if(!useDelimiterToGet && elementName.indexOf('_o_') > -1) {
                                     elementName = elementName.substring(0,elementName.indexOf('_o_'));
                                 }                                       
                                 if (paramToSubmitReport.includes("|"+elementName+"|")) { 
                                     params.set(elementName, element.getValue());                             
                                 }
                                                                         
                             });
                         }                  
                    }
                }
            }); 
        }
    	return params;
    }
    
    PrintPopupMgr.validate = function(parametersToSubmit, callBack) {
        if (parametersToSubmit && Object.isFunction(callBack)) {
            parametersToSubmit = $H(parametersToSubmit);
            var serviceName = parametersToSubmit.get('serviceName');
            new Ajax.Request('<@ofbizUrl>validateManagementPrintBirt</@ofbizUrl>', {
                parameters : parametersToSubmit.toObject(),
                onSuccess: function(response) {
                    var data = {};
                    try {
                        data = response.responseText.evalJSON(true);
                    } catch(e) {}
    
                    if (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined) {
                    	if (serviceName && ! serviceName.empty()) {                		
                            new Ajax.Request('<@ofbizUrl>managementServicePrintBirt</@ofbizUrl>', {
                            	parameters : parametersToSubmit.toObject(),
                                onSuccess: function(response) {
                                    var data = {};
                                    try {
                                        data = response.responseText.evalJSON(true);
                                    } catch(e) {}
                    
                                    if (! (data && data._ERROR_MESSAGE_LIST_ == undefined && data._ERROR_MESSAGE_ == undefined)) {
                                    	modal_box_messages.onAjaxLoad(data, Prototype.K);
                                    }                   
                                }
                            });                  		
                    	} else {
                    		callBack();
                    	}                     
                    } else {
                        modal_box_messages.onAjaxLoad(data, Prototype.K)
                    }
    
                }
            });
        }
    }
    
    PrintPopupMgr.load = function() {
        var newContent = $('MB_window');
        if (typeof CalendarDateSelect != 'undefined') {
            CalendarDateSelect.reloadCalendar(newContent, true);
        }
    }
    
    PrintPopupMgr.load();
</script>
<div class="popup-print-boxes-container">
    <span class="hidden-label" id="popup-print-box-title">${uiLabelMap.BaseSelectPrint}</span>
    <div class="popup-print-box-container">
        <#if context.listIsEmpty?if_exists == 'Y'>
        <div class="popup-print-void-container"  style="width:300px; height:60px;">      
        	<span class="label" >${uiLabelMap.BaseEmptytPrint}</span>
        </div>
        <#else>        
        <div class="popup-print-body-container">
            <div id="popup-print-radio-container" class="popup-print-radio-container">
                <#-- Metto i campi -->
                
                <#if popupFieldContainerScreenLocation?has_content && popupFieldContainerScreenName?has_content>
                <tr>
                	<td>
                		${screens.render(popupFieldContainerScreenLocation, popupFieldContainerScreenName, context)}                
                	</td>
                </tr>
                </#if>
            </div>
        </div>
        <div class="popup-print-buttons-container">        
            <a href="#" class="smallSubmit button-cancel" onclick="javascript:Modalbox.options.afterHide = Modalbox.options.afterHide.curry(false); Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
            <a href="#" style="display:none" id="button-ok" class="smallSubmit button-ok" onclick="javascript:Modalbox.options.afterHide = Modalbox.options.afterHide.curry(PrintPopupMgr.getParametersToSubmit(), PrintPopupMgr.validate); Modalbox.hide()">${uiLabelMap.BaseButtonPrint}</a>
            <a href="" id="button-ok-disabled" class="smallSubmit-disabled button-ok" onclick="javascript: return false;">${uiLabelMap.BaseButtonPrint}</a>            
        </div>
	   </#if>
    </div>

</div>