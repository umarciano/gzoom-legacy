WorkEffortStandardImportUploadFileListener = {
	uploadFile : "uploadFile",
	form : false,
	
	load : function() {
		var form = $$(".uploadFile");
		if(Object.isArray(form) && form.size() > 0) {
		    form = form[0];
		}

		if (Object.isElement(form) && form.tagName === "FORM") {
			WorkEffortStandardImportUploadFileListener.form = form;
			Element.addMethods("FORM", {
				onAfterSubmit : function(element) {
					return element;
				}
			});
			WorkEffortStandardImportUploadFileListener.registerForm(form);
		}
	},
	
	registerForm : function(form) {
		var targetFrame = $("target_upload");

    	if(Object.isElement(targetFrame)) {
    		Event.observe(targetFrame, "load", WorkEffortStandardImportUploadFileListener.uploadCompleted);
    	}
	},
	
	uploadCompleted : function(event) {
		//force stopWaiting becauase is not ajax.Request
		Utils.stopWaiting();
		
		var doc = WorkEffortStandardImportUploadFileListener.getIframeDocument($("target_upload"));
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
		
		if (Object.isElement(doc.getElementById("workEffortStandardImportResult"))) {
		    modal_box_messages.alert(doc.getElementById("workEffortStandardImportResult"), null, function() {WorkEffortStandardImportUploadFileListener.refreshForm()});		
		}

	},
	
	refreshForm : function() {
	    ajaxUpdateAreas('main-section-container,<@ofbizUrl>${parameters._LAST_VIEW_NAME_?if_exists}</@ofbizUrl>,externalLoginKey=${parameters.externalLoginKey?if_exists}&ajaxRequest=Y&clearSaveView=Y&cleanAccountingSession=Y&ownerContentId=GP_MENU_00242');
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

WorkEffortStandardImportUploadFileListener.load();