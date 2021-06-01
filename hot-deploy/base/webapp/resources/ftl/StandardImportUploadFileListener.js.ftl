StandardImportUploadFileListener = {
	uploadFile : "uploadFile",
	form : false,
	
	load : function() {
	    var form = $$(".uploadFile");
		if(Object.isArray(form) && form.size() > 0) {
		    form = form[0];
		}

		if (Object.isElement(form) && form.tagName === "FORM") {
			StandardImportUploadFileListener.form = form;
			Element.addMethods("FORM", {
				onAfterSubmit : function(element) {
					return element;
				}
			});
			StandardImportUploadFileListener.registerForm(form);
		}
	},
	
	registerForm : function(form) {
		var targetFrame = $("target_upload");

    	if(Object.isElement(targetFrame)) {
    		Event.observe(targetFrame, "load", StandardImportUploadFileListener.uploadCompleted);
    	}
	},
	
	uploadCompleted : function(event) {
	    //force stopWaiting becauase is not ajax.Request
		Utils.stopWaiting();
		
		var doc = StandardImportUploadFileListener.getIframeDocument($("target_upload"));
    	var errorMessageDiv = doc.getElementsByClassName("errorMessage")[0];
    	if (Object.isElement(errorMessageDiv)) {
    	
    		var data = $H({});
    		data["_ERROR_MESSAGE_"] = errorMessageDiv.innerHTML; 
    		modal_box_messages.onAjaxLoad(data, null);
    		modal_box_messages.alert("Errore:");
    		
    	} else {
    	
	    	var data = $H({});
			data["_ERROR_MESSAGE_"] = "";
			modal_box_messages.onAjaxLoad(data, null);
			
		}
		
		var messTotale = "";
		
		var sessionId = doc.getElementById("sessionId");
        if(sessionId != null && sessionId.innerHTML != ""){
            sessionId = sessionId.innerHTML;
            messTotale += '${uiLabelMap.BaseMessageStandardSessionId}'.replace("sessionId", sessionId);
        }
        
		var resultETLList = doc.getElementById("resultETLList");
        if(resultETLList != null && resultETLList.innerHTML != ""){
            resultETLList = resultETLList.innerHTML.evalJSON(true);
            resultETLList.each(function(element){    
                msg = element.entityName + "<br>";
                msg += '${uiLabelMap.BaseMessageStandardRecordElaborated}'.replace("recordElaborated", element.recordElaborated) + "<br>";
                msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", element.blockingErrors) + "<br>";
                msg += '${uiLabelMap.BaseMessageStandardJobLogId}'.replace("jobLogId", element.jobLogId) + "<br><br>";
                
                messTotale += msg;            
            });
        }   
         
    	var resultListUploadFile = doc.getElementById("resultListUploadFile");
    	if(resultListUploadFile != null && resultListUploadFile.innerHTML != ""){
    		resultListUploadFile = resultListUploadFile.innerHTML.evalJSON(true);
    		resultListUploadFile.each(function(element){
    		
				msg = element.entityName + "<br>";
				msg += '${uiLabelMap.BaseMessageStandardRecordElaborated}'.replace("recordElaborated", element.recordElaborated) + "<br>";
				msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", element.blockingErrors) + "<br>";
				msg += '${uiLabelMap.BaseMessageStandardJobLogId}'.replace("jobLogId", element.jobLogId) + "<br><br>";
				
	    		messTotale += msg;    		
			});
    	}
    	
    	var resultList = doc.getElementById("resultList");
    	if(resultList != null && resultList.innerHTML != ""){
    		resultList = resultList.innerHTML.evalJSON(true);
    		resultList.each(function(element){
    		    if(element.entityName != null && element.entityName != ""){
    				msg = element.entityName + "<br>";
    				msg += '${uiLabelMap.BaseMessageStandardRecordElaborated}'.replace("recordElaborated", element.recordElaborated) + "<br>";
    				msg += '${uiLabelMap.BaseMessageStandardBlockingErrors}'.replace("blockingErrors", element.blockingErrors) + "<br>";
    				msg += '${uiLabelMap.BaseMessageStandardJobLogId}'.replace("jobLogId", element.jobLogId) + "<br><br>";
    	    		
    	    		messTotale += msg;
    		    }
			});
    	}	
    	
    	
    	if(messTotale != ""){
  	  		modal_box_messages.alert(messTotale, null, function() {StandardImportUploadFileListener.refreshForm()});
    	}
	},
	
	refreshForm : function() {
	    ajaxUpdateAreas('main-section-container,<@ofbizUrl>${parameters._LAST_VIEW_NAME_?if_exists}</@ofbizUrl>,externalLoginKey=${parameters.externalLoginKey?if_exists}&ajaxRequest=Y&clearSaveView=Y&cleanAccountingSession=Y&ownerContentId=GP_MENU_00231');
	    LookupProperties.afterHideModal();
    },
	
	getIframeDocument : function(frameElement) {
		var doc = null;
  		if (frameElement.contentDocument) {
    		doc = frameElement.contentDocument; 
  		} else if (frameElement.contentWindow) {
    		doc = frameElement.contentWindow.document;
  		} else if (frameElement.document) {
    		doc = frameElement.document;
  		} else {
    		return null;
  		}
  		return doc;
	}
}

StandardImportUploadFileListener.load();