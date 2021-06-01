<script type="text/javascript">
    CopyAllPopupMgr = Class.create({ });

    CopyAllPopupMgr.getParametersToSubmit = function() {    
       
       	var description = $('snapShotDescription')
        if (description != null) {
            var parametersToSubmit = {};
            parametersToSubmit.snapShotDescription = description.getValue();
            return parametersToSubmit;
        }
        return false;
    }
   
    CopyAllPopupMgr.validate = function(parametersToSubmit, callBack) {
    
    	var newForm = new Element('form', {id : 'copyAllWorkEffort', name : 'copyAllWorkEffort'});
    	newForm.insert(new Element('input', {name : 'deleteOldRoots', type : 'hidden', value : 'N'}));
    	newForm.insert(new Element('input', {name : 'checkExisting', type : 'hidden', value : 'N'}));
    	newForm.insert(new Element('input', {name : 'glAccountCreation', type : 'hidden', value : 'Y'}));
        newForm.insert(new Element('input', {name : 'snapshot', type : 'hidden', value : 'Y'}));
        newForm.insert(new Element('input', {name : 'workEffortId', type : 'hidden', value : '${parameters.workEffortId?if_exists}'}));
        newForm.insert(new Element('input', {name : 'estimatedStartDateTo', type : 'hidden', value : '${parameters.estimatedStartDate?if_exists}'}));
        newForm.insert(new Element('input', {name : 'estimatedStartDateFrom', type : 'hidden', value : '${parameters.estimatedStartDate?if_exists}'}));
        newForm.insert(new Element('input', {name : 'estimatedCompletionDateTo', type : 'hidden', value : '${parameters.estimatedCompletionDate?if_exists}'}));
        newForm.insert(new Element('input', {name : 'estimatedCompletionDateFrom', type : 'hidden', value : '${parameters.estimatedCompletionDate?if_exists}'}));
        newForm.insert(new Element('input', {name : 'workEffortTypeIdFrom', type : 'hidden', value : '${parameters.workEffortTypeId?if_exists}'}));
        newForm.insert(new Element('input', {name : 'workEffortTypeIdTo', type : 'hidden', value : '${parameters.workEffortTypeId?if_exists}'}));
        newForm.insert(new Element('input', {name : 'organizationPartyId', type : 'hidden', value : '${parameters.organizationPartyId?if_exists}'}));
        /* Aggiungo la descrione appen inserita */
        if(parametersToSubmit != null){
        	newForm.insert(new Element('input', {name : 'snapShotDescription', type : 'hidden', value : parametersToSubmit.get("snapShotDescription")}));
        }
        
        
        new Ajax.Request("<@ofbizUrl>workEffortSnapshot</@ofbizUrl>", {
            parameters: newForm.serialize(true),
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
                } else {
	                var jobLogId = data.jobLogId;
	                if(jobLogId) {
	                	modal_box_messages._loadMessage(null, 'BaseMessageStandardImport', function(msg) {
                    		msg = msg.replace("jobLogId", jobLogId);
                    		msg = msg.replace("blockingErrors", data.blockingErrors);
                    		modal_box_messages.alert(msg);
                    	});
                	}
                }
            }
        });
    }
    
    CopyAllPopupMgr.load = function() {
        var newContent = $('MB_window');
        if (typeof CalendarDateSelect != 'undefined') {
            CalendarDateSelect.reloadCalendar(newContent, true);
        }
    }
    
    CopyAllPopupMgr.load();
    
</script>
<div class="popup-copy-all-boxs-container">
    <span class="hidden-label" id="popup-copy-all-box-title">${uiLabelMap.BaseMenusHistoricizeTab}</span>
    <div class="popup-copy-all-box-container">
        <div class="popup-copy-all-body-container">
            <div id="popup-copy-all-text-container" class="popup-copy-all-text-container">
               ${uiLabelMap.SnapShotComments}
               <br/>
               <textarea rows="3" cols="40" name="snapShotDescription" id="snapShotDescription" class="mandatory"></textarea>
               <br/>
               <br/>
               <br/>
            </div>
        </div>
        <div class="popup-copy-all-buttons-container">
            <a href="#" class="smallSubmit button-cancel" onclick="javascript:Modalbox.options.afterHide = Modalbox.options.afterHide.curry(false); Modalbox.hide()">${uiLabelMap.BaseButtonCancel}</a>
            <a href="#" class="smallSubmit button-ok" onclick="javascript:Modalbox.options.afterHide = Modalbox.options.afterHide.curry(CopyAllPopupMgr.getParametersToSubmit(), CopyAllPopupMgr.validate); Modalbox.hide()">${uiLabelMap.WorkEffortViewHistoricize}</a>
        </div>
    </div>
</div>