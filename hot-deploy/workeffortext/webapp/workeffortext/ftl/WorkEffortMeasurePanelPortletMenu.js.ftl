    WorkEffortMeasurePanelPortletMenu = {
         // TODO ognuno il suo div
        load: function() {
            Utils.stopObserveEvent(new Array($(document.body)), "click");
            
            // var screenlet = $("child-management-screenlet-body-WorkEffortMeasureIndicatorModelPortlet");
            // var screenlets = $$("div.transactionPortlet");
            
            // search only in active container
            var screenlet = WorkEffortMeasurePanelPortletMenu.getActiveScreenlet();
            if(Object.isElement(screenlet)) {
                WorkEffortMeasurePanelPortletMenu.hideButtons(screenlet);
                var portletFormDisabledValue = WorkEffortMeasurePanelPortletMenu.getPortletFormDisabledValue(screenlet);
                
                var saveMenuItem = screenlet.down("li.save");
                if(Object.isElement(saveMenuItem)) {
                	if(portletFormDisabledValue == 'Y') {
                		saveMenuItem.hide();
                	} else {
                		saveMenuItem.show();
                		saveMenuItem.observe("click", WorkEffortMeasurePanelPortletMenu.onClickSave);
                		saveMenuItem.observe("dom:click", WorkEffortMeasurePanelPortletMenu.onClickSave);
                	}
                }
                
                var deleteMenuItem = screenlet.down("li.delete");
                if(Object.isElement(deleteMenuItem)) {
                	if(portletFormDisabledValue == 'Y') {
                		deleteMenuItem.hide();
                	} else {
                		deleteMenuItem.show();
                		deleteMenuItem.observe("click", WorkEffortMeasurePanelPortletMenu.onClickDelete);
                	}
                }
                Utils.observeEvent(new Array($(document.body)), "click", WorkEffortMeasurePanelPortletMenu.onClickOther);
            }
        },
        
        getActiveScreenlet: function() {
            // search only in active container
            var myTabs = Control.Tabs.instances[0];
            var containerSelected = $('main-container');
            if(myTabs){
                containerSelected = $(myTabs.getActiveContainer());
            }
            var screenlets = $(containerSelected).select("div.transactionPortlet");
         
            // TODO Special Case : Popup
            if(!Object.isElement(screenlets) || (Object.isArray(screenlets) && screenlets.size() == 0)) {
                var popup = $("MB_window");
                if (Object.isElement(popup)) {
                    screenlets = $(popup).select("div.transactionPortlet");
                }
            }
            
            if(Object.isArray(screenlets) && screenlets.size() > 0) {
                return screenlets[0];
            }
            return null;
        },
        
        hideButtons: function(screenlet) {
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
        
        getPortletFormDisabledValue: function(screenlet) {
        	var form = screenlet.down("form.basic-form");
        	if(Object.isElement(form)) {
        		var isPortletFormDisabledField = form.down("input[name='isPortletFormDisabled']");
        		if(Object.isElement(isPortletFormDisabledField)) {
        			return isPortletFormDisabledField.getValue();
        		}
        	}
        	return '';
        },
        
        onClickSave: function(e) {
            var saveMenuItem = Event.element(e);
            var callback = (e.memo && e.memo.callback) || Prototype.K;
            var screenlet = saveMenuItem.up("div.transactionPortlet") 
            //var screenlet = $("child-management-screenlet-body-WorkEffortMeasureIndicatorModelPortlet");
            var form = screenlet.down("form.basic-form");
            //var executability = WorkEffortMeasurePanelPortletMenu._checkExecutability(null, form);
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
                        
                        var workEffortMeasureField = elaboratedForm.down("input[name='weTransMeasureId']");
                        var accountFilterField = elaboratedForm.down("input[name='accountFilter']");
                        
                        // formToRefresh is the form of valoriIndicatori, so there is always even if folder is open and closed 
                        // panelToRefresh is the panel if a indicatore is selected
                        var panelToRefresh = $("WorkEffortMeasurePanel_${parameters.contentIdInd?if_exists}");
                     // search only in active container
                        var myTabs = Control.Tabs.instances[0];
                        var containerSelected = $('main-container');
                        if(myTabs){
                            containerSelected = $(myTabs.getActiveContainer());
                        }
                        var formToRefresh = $(containerSelected).select("form.formToRefresh");                                              
                        if(!Object.isElement(panelToRefresh)) {
                            if(Object.isArray(formToRefresh)) {
                                formToRefresh.each(function(item) {
                                    WorkEffortMeasurePanelPortletMenu.refreshForm($(item));
                                });
                            }
                        } else {
                            ajaxUpdateArea("WorkEffortMeasurePanel_${parameters.contentIdInd?if_exists}", "<@ofbizUrl>reload${parameters.reloadRequestType}TransactionPanel</@ofbizUrl>",
                                    {"workEffortMeasureId" : data.id.weTransMeasureId, "reloadPanel" : "Y", "onlyWithBudget" : "${parameters.onlyWithBudget}", "reloadRequestType" : "${parameters.reloadRequestType?if_exists}", "accountFilter" : accountFilterField.getValue(), "rootInqyTree" : "${parameters.rootInqyTree?if_exists?default('N')}", "weTransId" : data.id.weTransId, "weTransEntryId" : data.id.weTransEntryId, "layoutType" : "${parameters.layoutType!}",
                                    "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}",
                                    "saveView" : "N", "${parameters.extraParam1Name?if_exists}" : "${parameters.extraParam1Value?if_exists}", "${parameters.extraParam2Name?if_exists}" : "${parameters.extraParam2Value?if_exists}",
                                    "${parameters.extraParam3Name?if_exists}" : "${parameters.extraParam3Value?if_exists}", "${parameters.extraParam4Name?if_exists}" : "${parameters.extraParam4Value?if_exists}",
                                    "${parameters.extraParam5Name?if_exists}" : "${parameters.extraParam5Value?if_exists}", "contentIdInd" : "${parameters.contentIdInd?if_exists}", "contentIdSecondary" : "${parameters.contentIdSecondary?if_exists}" }, {onComplete: callback});
                         
                        }
                    }};
                    WorkEffortMeasurePanelPortletMenu.ajaxSubmitFormUpdateAreas(elaboratedForm, options);
                	//Elimino la portlet
                    var portlets = $$("div.transactionPortlet");
                    portlets.each(function(portlet) {
                        if (Object.isElement(portlet) && Object.isElement(portlet.down())) {
                            portlet.down().remove();
                        }
                    });
                    if(Modalbox.initialized) {
                        Modalbox.hide();
                    }
                	
                    //aftersubmit
                    if (Object.isFunction(elaboratedForm.onAfterSubmit)) {
                        elaboratedForm.onAfterSubmit();
                    }
                    elaboratedForm.remove();
                    
                    return false;
                }
            } else {
                callback();
            }
        },
        
        refreshForm : function(form) {
            var onclickStr = form.readAttribute("onSubmit");
            var attributes =onclickStr.split(","); 
            var request = attributes[3];
            var container = attributes[2].substring(attributes[2].indexOf('\'')+1);
            
            var parameters = $H(attributes[4].substring(0, attributes[4].lastIndexOf('\'')).toQueryParams());
            
            ajaxUpdateAreas(container+',' + request + ',' + parameters.toQueryString());
        },
        
        onClickDelete: function(e) {
            var deleteMenuItem = Event.element(e);
            var screenlet = deleteMenuItem.up("div.transactionPortlet")
            var form = screenlet.down("form.basic-form");
            
            //checkExecutability
            WorkEffortMeasurePanelPortletMenu.deleteCheckExecutability(form);
        },
        
        onClickOther: function(event) {
            var element = Event.element(event);
            
            var parentScreenlet = element.up("div.transactionPortlet");
            // il click fuori dal pannello scatena il controllo delle modifiche
        	if (!Object.isElement(parentScreenlet)) {
        	    // search only in active container
                var screenlet = WorkEffortMeasurePanelPortletMenu.getActiveScreenlet();
                if(Object.isElement(screenlet)) {
                	var form = screenlet.down("form.cachable");
                	if (Object.isElement(form)) {
                        var resultcheckModification = FormKitExtension.checkModficationWithAlert(form);
                    }
                }
            }
            return true;
        },
        
        deleteCheckExecutability: function(form) {
            var table = form.down('table.single-editable');
            if (!Object.isElement(table)) {
                table = form.down('table.multi-editable');
            }
    
            if (table && TableKit.isSelectable(table)) {
                var selectedRows = TableKit.Selectable.getSelectedRows(table);
                if (selectedRows && selectedRows.size() > 0) {
                    //callBack();
                    modal_box_messages.confirm(['BaseMessageDeleteDataConfirm'],null,WorkEffortMeasurePanelPortletMenu.deleteExecutability.curry(form));
    
                    return true;
                } else {
                    modal_box_messages.alert(['BaseMessageNoSelection']);
    
                    return false;
                }
            } else {
                modal_box_messages.confirm(['BaseMessageDeleteDataConfirm'],null,WorkEffortMeasurePanelPortletMenu.deleteExecutability.curry(form));
            }
    
            return true;
        },
        
        deleteExecutability: function(form) {
            var operation = form.down("input[name='operation']");
            operation.remove();
            operation = new Element("input", {type: "hidden", name: "operation", value: "DELETE"});
            form.insert(operation);
            
            var callback = (e.memo && e.memo.callback) || Prototype.K;
            
            var options = {onComplete : function(response) {
                var data = response.responseText.evalJSON(true);
                
                if (data["_ERROR_MESSAGE_"] != null || data["_ERROR_MESSAGE_LIST_"] != null) {
                    modal_box_messages.onAjaxLoad(data, Prototype.K);
                    return false;
                }
                        
                var workEffortMeasureField = form.down("input[name='weTransMeasureId']");
                var accountFilterField = form.down("input[name='accountFilter']");
                
                // formToRefresh is the form of valoriIndicatori, so there is always even if folder is open and closed 
                // panelToRefresh is the panel if a indicatore is selected
                var panelToRefresh = $("WorkEffortMeasurePanel_${parameters.contentIdInd?if_exists}");
                
                // search only in active container
                var myTabs = Control.Tabs.instances[0];
                var containerSelected = $('main-container');
                if(myTabs){
                    containerSelected = $(myTabs.getActiveContainer());
                }
                var formToRefresh = $(containerSelected).select("form.formToRefresh");
                if(!Object.isElement(panelToRefresh)) {
                    if(Object.isArray(formToRefresh)) {
                	    formToRefresh.each(function(item) {
                	        WorkEffortMeasurePanelPortletMenu.refreshForm($(item));
                        });
                    }
                } else {
                    ajaxUpdateArea("WorkEffortMeasurePanel_${parameters.contentIdInd?if_exists}", "<@ofbizUrl>reload${parameters.reloadRequestType}TransactionPanel</@ofbizUrl>",
                            {"workEffortMeasureId" : data.id.weTransMeasureId, "reloadPanel" : "Y", "onlyWithBudget" : "${parameters.onlyWithBudget}", "reloadRequestType" : "${parameters.reloadRequestType?if_exists}", "accountFilter" : accountFilterField.getValue(), "rootInqyTree" : "${parameters.rootInqyTree?if_exists?default('N')}", "weTransId" : data.id.weTransId, "weTransEntryId" : data.id.weTransEntryId, "layoutType" : "${parameters.layoutType!}",
                            "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}",
                            "saveView" : "N", "${parameters.extraParam1Name?if_exists}" : "${parameters.extraParam1Value?if_exists}", "${parameters.extraParam2Name?if_exists}" : "${parameters.extraParam2Value?if_exists}",
                            "${parameters.extraParam3Name?if_exists}" : "${parameters.extraParam3Value?if_exists}", "${parameters.extraParam4Name?if_exists}" : "${parameters.extraParam4Value?if_exists}",
                            "${parameters.extraParam5Name?if_exists}" : "${parameters.extraParam5Value?if_exists}", "contentIdInd" : "${parameters.contentIdInd?if_exists}", "contentIdSecondary" : "${parameters.contentIdSecondary?if_exists}" }, {onComplete: callback});
                }
                
            }};
            
            WorkEffortMeasurePanelPortletMenu.ajaxSubmitFormUpdateAreas(form, options);
             //Elimino la portlet
            var portlets = $$("div.transactionPortlet");
            portlets.each(function(portlet) {
                if (Object.isElement(portlet) && Object.isElement(portlet.down())) {
                    portlet.down().remove();
                }
            });

            return false;
        },
        
        ajaxSubmitFormUpdateAreas : function(form, options) {
            var params = $H($(form).serialize(true));
            if (!params.get("ajaxCall")) {
                params.set("ajaxCall", "Y");
            }
    
            options = Object.extend({
                parameters: params.toObject()}, options || {});
            new Ajax.Request($(form).action, options);
         }
    }
    
    WorkEffortMeasurePanelPortletMenu.load();
