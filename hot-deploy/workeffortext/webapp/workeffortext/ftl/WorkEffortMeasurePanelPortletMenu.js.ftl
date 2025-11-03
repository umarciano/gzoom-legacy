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
                            // Extended callback per aggiornare anche il valore nella tabella principale
                            var extendedCallback = function() {
                                // Esegui il callback originale
                                if (callback) {
                                    callback();
                                }
                                
                                // Aggiorna il valore nella cella della tabella senza reload completo
                                WorkEffortMeasurePanelPortletMenu.updateIndicatorValueInTable(data.id.weTransMeasureId);
                            };
                            
                            ajaxUpdateArea("WorkEffortMeasurePanel_${parameters.contentIdInd?if_exists}", "<@ofbizUrl>reload${parameters.reloadRequestType}TransactionPanel</@ofbizUrl>",
                                    {"workEffortMeasureId" : data.id.weTransMeasureId, "reloadPanel" : "Y", "onlyWithBudget" : "${parameters.onlyWithBudget}", "reloadRequestType" : "${parameters.reloadRequestType?if_exists}", "accountFilter" : accountFilterField.getValue(), "rootInqyTree" : "${parameters.rootInqyTree?if_exists?default('N')}", "weTransId" : data.id.weTransId, "weTransEntryId" : data.id.weTransEntryId, "layoutType" : "${parameters.layoutType!}",
                                    "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}",
                                    "saveView" : "N", "${parameters.extraParam1Name?if_exists}" : "${parameters.extraParam1Value?if_exists}", "${parameters.extraParam2Name?if_exists}" : "${parameters.extraParam2Value?if_exists}",
                                    "${parameters.extraParam3Name?if_exists}" : "${parameters.extraParam3Value?if_exists}", "${parameters.extraParam4Name?if_exists}" : "${parameters.extraParam4Value?if_exists}",
                                    "${parameters.extraParam5Name?if_exists}" : "${parameters.extraParam5Value?if_exists}", "contentIdInd" : "${parameters.contentIdInd?if_exists}", "contentIdSecondary" : "${parameters.contentIdSecondary?if_exists}" }, {onComplete: extendedCallback});
                         
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
        
        /**
         * Aggiorna SOLO il valore dell'indicatore nella tabella principale
         * senza ricaricare l'intera form (approccio chirurgico)
         */
        updateIndicatorValueInTable : function(workEffortMeasureId) {
            try {
                // Cerca la tabella principale degli indicatori (NON nel detail panel!)
                var indicatorTable = null;
                var myTabs = Control.Tabs.instances[0];
                
                if (myTabs) {
                    // Itera su tutti i tab per trovare la tabella degli indicatori
                    var tabContainers = myTabs.containers;
                    for (var i = 0; i < tabContainers.length; i++) {
                        var container = $(tabContainers[i]);
                        if (container) {
                            var tables = container.select("table.basic-table.list-table");
                            tables.each(function(table) {
                                var tableId = table.identify();
                                // La tabella degli indicatori ha ID che contiene "WEMFPMMFINDICATOR"
                                if (tableId && tableId.indexOf("WEMFPMMFINDICATOR") > -1) {
                                    indicatorTable = table;
                                    throw $break;
                                }
                            });
                            if (indicatorTable) break;
                        }
                    }
                }
                
                // Fallback: cerca in tutto il documento
                if (!indicatorTable) {
                    var allTables = $$("table.basic-table.list-table");
                    allTables.each(function(table) {
                        var tableId = table.identify();
                        if (tableId && tableId.indexOf("WEMFPMMFINDICATOR") > -1) {
                            indicatorTable = table;
                            throw $break;
                        }
                    });
                }
                
                if (!indicatorTable) {
                    console.warn("[GZOOM] Tabella indicatori non trovata");
                    return;
                }
                
                // Cerca la riga con il workEffortMeasureId specificato
                var targetRow = null;
                var rows = indicatorTable.select("tr");
                
                rows.each(function(row) {
                    // In OFBiz multi-form, i campi hanno suffisso _o_N
                    var measureIdInput = row.down("input[name^='workEffortMeasureId_o_']");
                    
                    if (measureIdInput && measureIdInput.value == workEffortMeasureId) {
                        targetRow = row;
                        throw $break;
                    }
                });
                
                if (!targetRow) {
                    console.warn("[GZOOM] Riga indicatore " + workEffortMeasureId + " non trovata");
                    return;
                }
                
                // Cerca direttamente la cella con classe "indicator-value-cell" (l'ultima colonna)
                var valueCell = targetRow.down("td.indicator-value-cell");
                
                if (!valueCell) {
                    // Fallback: cerca l'ultima TD della riga
                    var allTds = targetRow.select("td");
                    if (allTds && allTds.length > 0) {
                        valueCell = allTds[allTds.length - 1]; // Ultima cella
                    }
                }
                
                if (!valueCell) {
                    console.warn("[GZOOM] Cella valore non trovata per indicatore " + workEffortMeasureId);
                    return;
                }
                
                // Recupera il nuovo valore tramite AJAX leggero
                // Anche questi campi hanno il suffisso _o_N in OFBiz multi-form
                var glAccountIdInput = targetRow.down("input[name^='glAccountId_o_']");
                var workEffortIdInput = targetRow.down("input[name^='workEffortId_o_']");
                
                if (!glAccountIdInput || !workEffortIdInput) {
                    console.warn("[GZOOM] Dati mancanti per recupero valore");
                    return;
                }
                
                var glAccountId = glAccountIdInput.value;
                var workEffortId = workEffortIdInput.value;
                
                // Chiamata AJAX per recuperare il nuovo valore
                new Ajax.Request('/emplperf/control/getIndicatorValue', {
                    method: 'post',
                    parameters: {
                        glAccountId: glAccountId,
                        workEffortId: workEffortId,
                        workEffortMeasureId: workEffortMeasureId
                    },
                    onSuccess: function(transport) {
                        try {
                            var response = transport.responseText.evalJSON();
                            if (response && response.indicatorValue !== undefined) {
                                // Aggiorna SOLO il testo della cella (non il DOM completo per evitare resize loop)
                                var newValue = response.indicatorValue;
                                // Usa textContent invece di update() per evitare side-effects
                                valueCell.textContent = newValue;
                            }
                        } catch(e) {
                            console.error("[GZOOM ERROR] Errore parsing risposta: " + e.message);
                        }
                    },
                    onFailure: function() {
                        console.error("[GZOOM ERROR] Errore nel recupero valore indicatore");
                    }
                });
                
            } catch(e) {
                console.error("[GZOOM ERROR] Errore in updateIndicatorValueInTable: " + e.message);
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
