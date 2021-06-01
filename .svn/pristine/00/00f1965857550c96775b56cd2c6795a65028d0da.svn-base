<script type="text/javascript">
    PrintPopupMgr = Class.create({ });

    PrintPopupMgr.getParametersToSubmit = function() {
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
            
            return parametersToSubmit;
        }
        return false;
    }
    
    PrintPopupMgr.validate = function(parametersToSubmit, callBack) {
        if (parametersToSubmit && Object.isFunction(callBack)) {
            parametersToSubmit = $H(parametersToSubmit);
            parametersToSubmit.set('externalLoginKey', '${externalLoginKey}');
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
                        //data['messageContext'] = 'BaseValidateReportParameters'
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
        
        $('select-addparams-print-row').hide();
        $('select-type-print-row').hide(); 
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
                ${screens.render(popupFieldContainerScreenLocation, popupFieldContainerScreenName, context)}                
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