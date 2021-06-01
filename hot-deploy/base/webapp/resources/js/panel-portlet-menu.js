PanelPortletMenu = {
    // screenlet name in which find form    
    screenletName: null,
        
	load: function(newContentToExplore, withoutResponder) {
        // console.log("PanelPortletMenu : load ", newContentToExplore);
        Utils.stopObserveEvent(new Array($(document.body)), "click", PanelPortletMenu.onClickOther);
    	
        // the first time newContentToExplore is null only, but document.body have not standardPortlet,
        // then is the new div
        var newContentToExplore = Object.isElement($(newContentToExplore)) ? $(newContentToExplore) : $(document.body);
        // console.log("PanelPortletMenu : load newContentToExplore " + newContentToExplore.id);
        
        // Manage only div.standardPortlet
        if(Object.isElement(newContentToExplore) && newContentToExplore.hasClassName('standardPortlet')) {
            Utils.observeEvent(new Array($(document.body)), "click", PanelPortletMenu.onClickOther);
        
            PanelPortletMenu.screenletName = newContentToExplore.identify();
   	        PanelPortletMenu.loadPortlet(newContentToExplore);
   		}
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(PanelPortletMenu.responder, "panelPortletMenu");
        }
    },
    
    /**
     * Responder functions
     **/
    responder : {
        onAfterLoad : function(newContent) {
            // console.log("PanelPortletMenu : onAfterLoad " + newContent);
            PanelPortletMenu.load(newContent, true);
             
        },
        unLoad : function() {
            // console.log("PanelPortletMenu : unLoad");
            return typeof "PanelPortletMenu" === "undefined";
        }
    },

    loadPortlet: function(screenlet) {
        // console.log("PanelPortletMenu : loadPortlet");
        PanelPortletMenu.hideButtons(screenlet);
        var readOnly = PanelPortletMenu.manageSaveButton(screenlet);
        // panel has only save button, with class save search-save portlet-menu-item fa
        var saveMenuItem = screenlet.down("li.save");
        if(Object.isElement(saveMenuItem)) {
        	if(readOnly) {
        		saveMenuItem.hide();
            } else {
            	saveMenuItem.show();
                saveMenuItem.observe("click", PanelPortletMenu.onClickSave);
                saveMenuItem.observe("dom:click", PanelPortletMenu.onClickSave);
            }
        }
    },

    hideButtons: function(screenlet) {
        // console.log("PanelPortletMenu : hideButtons", screenlet);
        if(Object.isElement(screenlet)) {
            var resetButton = screenlet.down("input.management-reset-button");
            var saveButton = screenlet.down("input.save-button");
            var deleteButton = screenlet.down("input.management-delete-button");
            var copyButton = screenlet.down("a.copy-button");
            if(Object.isElement(resetButton)) {
                resetButton.up("tr").setStyle({display: "none"});
            }
            if(Object.isElement(saveButton)) {
                saveButton.up("tr").setStyle({display: "none"});
            }
            if(Object.isElement(deleteButton)) {
                deleteButton.up("tr").setStyle({display: "none"});
            }
            if(Object.isElement(copyButton)) {
                copyButton.up("tr").setStyle({display: "none"});
            }
        }
    },
    
    manageSaveButton: function(screenlet) {
        // console.log("PanelPortletMenu : manageSaveButton");
        var form = screenlet.down("form.cachable");
    	if (form) {
    	    // TODO note HTML?
    		var commentsField = form.down("textarea[name=comments]");
    		if (Object.isElement(commentsField)) {
    			var readonly = commentsField.getAttribute("readonly");
    			if (readonly == "readonly") {
    				return true;
    			}
    		}
    		var noteInfoField = form.down("textarea[name=noteInfo]");
            if (Object.isElement(noteInfoField)) {
                var readonly = noteInfoField.getAttribute("readonly");
                if (readonly == "readonly") {
                    return true;
                }
            }
    	}
    	return false;
    },
    
    onClickSave: function(e) {
        // console.log("PanelPortletMenu : onClickSave");
        var saveMenuItem = Event.element(e);
        var callback = (e.memo && e.memo.callback) || Prototype.K;
        // console.log("PanelPortletMenu : onClickSave callback " + callback);
        
        var screenlet = saveMenuItem.up("div.standardPortlet") 
        var form = screenlet.down("form.cachable");
        // PanelPortletMenu._checkExecutability vs RegisterManagementMenu._checkExecutability show message with BaseMessageSaveDataMandatoryField or BaseMessageSaveDataNoData
        var executability = RegisterManagementMenu._checkExecutability(null, form);
        if(executability) {
            var elaboratedForm = RegisterManagementMenu._filterDataToSave(null, "BaseMessageSaveData", false, null, form);
            if(elaboratedForm) {
                
                var options = {onComplete : function(response) {
                    var data = response.responseText.evalJSON(true);
                        
                    if (data["_ERROR_MESSAGE_"] != null || data["_ERROR_MESSAGE_LIST_"] != null) {
                        modal_box_messages.onAjaxLoad(data, Prototype.K);
                        return false;
                    }
                    if(data["failMessage"] != null) {
                        modal_box_messages.onAjaxLoad(data, Prototype.K);
                    }
                    
                    Utils.stopWaiting();
                    
                    var screenlet = $(PanelPortletMenu.screenletName);
                    if(Object.isElement(screenlet)) {
                        var form = screenlet.down("form.cachable");
                        if (Object.isElement(form)) {
                            FormKit.loadFields(form);
                        }
                    }
                }};
                
                Utils.startWaiting();
                
                // Nelle option del ajaxSubmitFormUpdateAreas viene gestito il reload, quindi
                // o si mettono le option oppure si cerca di fare il reload cercando l'ide dello screenlet e la request da fare
                ajaxSubmitFormUpdateAreas(elaboratedForm.identify(), "", "", options);
            	
                //aftersubmit
                if (Object.isFunction(elaboratedForm.onAfterSubmit)) {
                    elaboratedForm.onAfterSubmit();
                }
                elaboratedForm.remove();
                
                return true;
            }
        } else {
            callback();
        }
    },
    
    // TODO resta il problema se un utente modifica il pannello e poi preme il salvataggio in alto a sinistra
    onClickOther: function(event) {
        var element = Event.element(event);
        // console.log("PanelPortletMenu : onClickOther ", element);
        
        var parentScreenlet = element.up("div.standardPortlet");
        // console.log("PanelPortletMenu : parentScreenlet ", parentScreenlet);
        
        // il click fuori dal pannello scatena il controllo delle modifiche
        if (!Object.isElement(parentScreenlet)) {
            var screenlet = $(PanelPortletMenu.screenletName);
            // console.log("PanelPortletMenu : screenlet ", screenlet);
            if(Object.isElement(screenlet)) {
                // console.log("PanelPortletMenu : screenlet ", screenlet);
                var form = screenlet.down("form.cachable");
                // console.log("PanelPortletMenu : form ", form);
                if (Object.isElement(form)) {
                        var resultcheckModification = FormKitExtension.checkModficationWithAlert(form);
                        // console.log("PanelPortletMenu : FormKitExtension.checkModficationWithAlert(form) result = ", resultcheckModification);
                    }
                }
            }
        return true;
    }
};

document.observe("dom:loaded", PanelPortletMenu.load);
