ContentUploadListener = {
	uploadContainerId: "uploadContainer",
	managementContainerId : "managementContainer",
	form : false,
	//progressBar : new Control.ProgressBar("progress_bar"),
	
	load : function() {
		var container = $(ContentUploadListener.uploadContainerId) || $(ContentUploadListener.managementContainerId);
		if (Object.isElement(container)) {
			var form = container.down("form.content-upload-form") || container.down("form.content-management-form");
			if (Object.isElement(form) && form.tagName === "FORM") {
				ContentUploadListener.form = form;
				Element.addMethods("FORM", {
					onAfterSubmit : function(element) {
					//	ContentUploadListener.getUploadProgressStatus();
						Utils.startWaiting();
				  		$(container).startWaiting();
				  		
						return element;
					}
				});
				ContentUploadListener.registerForm(form);
			}
		}
	},
	
	registerForm : function(form) {
		var targetFrame = $("target_upload");
    	if(Object.isElement(targetFrame)) {
    		Event.observe(targetFrame, "load", ContentUploadListener.uploadCompleted);
    	}
	},
	
	uploadCompleted : function(event) {
		var doc = ContentUploadListener.getIframeDocument($("target_upload"));
    	var contentIdP = doc.getElementById("contentId");
    	var errorMessageDiv = doc.getElementsByClassName("errorMessage")[0];
    	var container = $(ContentUploadListener.uploadContainerId) || $(ContentUploadListener.managementContainerId);
		if (Object.isElement(container)) {
			$(container).stopWaiting();
		}
		Utils.stopWaiting();
		if (Object.isElement(errorMessageDiv)) {
    		var data = $H({});
    		data["_ERROR_MESSAGE_"] = errorMessageDiv.innerHTML; 
    		modal_box_messages.onAjaxLoad(data, null);
    		modal_box_messages.alert("Errore durante il salvataggio dei dati");
    	}
		var formName = ContentUploadListener.form.readAttribute("name");
		if (Object.isElement(contentIdP)) {
    		var contentIdField = ContentUploadListener.form.down("input#" + formName + "_" + "contentId");
    		contentIdField.value = contentIdP.innerHTML;
    		var contentNameP = doc.getElementById("contentName");
    		if (Object.isElement(contentNameP)) {
    			var contentNameField = ContentUploadListener.form.down(".uploadedFileField");
    			contentNameField.insert('<p>' + contentNameP.innerHTML + '</p>');
    		}
            ContentUploadListener.refreshForm(container);
		}
    	
    	//	ContentUploadListener.progressBar.reset();
    	if(Object.isElement($("progressBarSavingMsg"))) {
        	$("progressBarSavingMsg").remove();
    	}
    	Toolbar.enableToolbar();
    },
    
    refreshForm : function() {
        // see FormKit cachable form for more detail
		// loadFields set the input field of form,
		// also new input, for example contentId and contentName,
		// in cachable array (FormKit.forms[id])
		FormKit.loadFields(ContentUploadListener.form);
		$A(ContentUploadListener.form.getInputs()).each(function(element) {
            element.writeAttribute('readonly', 'readonly');
        });

        // reload active tab
		var tabs = null;
		if (Object.isArray(Control.Tabs.instances)) {
			tabs = Control.Tabs.instances[0];
		}
		if (tabs) {
        	var activeContainer = tabs.getActiveContainer();
	        if (Object.isElement(activeContainer)) {
	        	tabs.setActiveTab(activeContainer.identify());
	        }
	    }
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
	},
	
	getUploadProgressStatus : function() {
		var i=0;
    	new PeriodicalExecuter(function(){
        	new Ajax.Request("/partymgr/control/getFileUploadProgressStatus", {
            	onSuccess: function(transport){
               		var data = transport.responseText.evalJSON(true);
                    var readPercent = data.readPercent;
                    ContentUploadListener.progressBar.setProgress(readPercent);
                    $("uploadContainer").insert("<p>" + readPercent + "</p>");
                    if(readPercent > 99){
                    	$("uploadContainer").insert("<span id='progressBarSavingMsg' class='label'>Saving..</span>");
                   }
                }
            });
        },0.1); 
	} 
}
ContentUploadListener.load();