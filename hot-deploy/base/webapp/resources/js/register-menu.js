RegisterMenu = {
    functionRegistered : function(containerSelector, menuItemSelector, newInstance, newContent, contentToUpdate, forceReserachMenuFromNewContent) {
        // ctrl se ancora attivo con up Object.isElement($(newContent.identify()) non funziona
        if(!Object.isElement($(newContent).up('div'))) {
            newContent = $(newContent.identify());
        }
        var res = RegisterMenu.findMenus(newContent, containerSelector, menuItemSelector, forceReserachMenuFromNewContent, newInstance, contentToUpdate);
        if (!res) {
            res = RegisterMenu.reloadItems(newContent, {itemSelector:'li.action-menu-item'}, newInstance, null, null, contentToUpdate);
        }
        return res;
    },

    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent, options) {
        		Toolbar.enableToolbar();
            
        		var contentToElaborateSize = newContent.select('.management', '.search-parameters', '.search-list', '.contextForm', 'input[type="submit"]').size();
        	
                /*var contentToElaborateSize = (Object.isElement(newContent.down('.management')) ? 1 : 0) + (Object.isElement(newContent.down('.search-parameters')) ? 1 : 0) + (Object.isElement(newContent.down('.search-list')) ? 1 : 0)
                                           + (Object.isElement(newContent.select('input').find(function(element) {
                                                return element.readAttribute('type') == 'submit';
                                              })) ? 1 : 0) + (Object.isElement(newContent.down('.contextForm')) ? 1 : 0);*/
                                              
                //ignoro volontariamente la toolbar nel caso non mi serva
                var ignoreToolbar = newContent.down("input.ignoreToolbar");                                              

                if (contentToElaborateSize > 0 && (!Object.isElement(ignoreToolbar) || "Y" != ignoreToolbar.getValue())) {
                    var contentToUpdate = options['contentToUpdate'];

                    RegisterMenu.registerTabEvent(newContent, contentToUpdate);
                }
            }
        }, 'register-menu');
        RegisterMenu.registerTabEvent();
    },

    registerTabEvent : function(newContent, contentToUpdate, newInstance) {
        var res = false;
        if(!Object.isElement($(newContent))) {
            newContent = $(document.body);
        }
        if(typeof Control.Tabs !== 'undefined') {
        	var searchListSize = newContent.select('.search-list').size();
        	var containerSize = newContent.select('.management', '.search-parameters', '.contextForm').size();
        	var totalContainerSize = searchListSize + containerSize;
            if (totalContainerSize > 0) {
            	var forceRegistration = false;
            	var massiveRegister = false;
            	var dispatchEvent = false;
            	if (Object.isString(contentToUpdate))
            		contentToUpdate = $(contentToUpdate);
            	
            	var tabsAnchestorContainer = newContent;
            	if (Object.isElement(contentToUpdate)) {
            		var tabs = Control.Tabs.findByTabId(contentToUpdate.identify());
            		if (!tabs && Object.isArray(Control.Tabs.instances)) {
            			tabs = Control.Tabs.instances[0];
            		}
                    if (tabs) {
                    	var activeContainer = tabs.getActiveContainer();
                    	forceRegistration = Object.isElement(activeContainer) && activeContainer.identify() === contentToUpdate.identify();
                    	
                    	massiveRegister = tabs.isMassiveRegistered("registerMenu");
                    	
                    	if (!massiveRegister && tabsAnchestorContainer.select('.tabs').size() > 0) {
	                    	var activeContainerIndex = tabs.getActiveContainerIndex();
	                    	if (activeContainerIndex > 0) {
	                    		newContent = activeContainer;
	                    	}
                    	}
                    }
            	}
            	
            	if (!massiveRegister && tabsAnchestorContainer.select('.tabs').size() > 0) {
	                res = Control.Tabs.massiveRegisterEvent(newContent, function(container) {
	                	if (searchListSize > 0) {
	                		var containerChildElementSize = container.childElements().size();
		                    var contentToElaborateSize = container.select('.search-list').size();
		                    if (containerChildElementSize > 0 && contentToElaborateSize < searchListSize) {
		                    	container = container.ancestors().find(function(element) {
		                            var currentSize = element.select('.search-parameters', '.search-list').size();
		
		                            return currentSize == containerSize;
		                        });
		                    }
	                	}
	                	
	                	
	                	/*var containerChildElementSize = container.childElements().size();
	                    var contentToElaborateSize = container.select('.management', '.search-parameters', '.search-list', '.contextForm').size();
	                    if (containerChildElementSize > 0 && contentToElaborateSize < containerSize) {
	                    	container = container.ancestors().find(function(element) {
	                            var currentSize = element.select('.management', '.search-parameters', '.search-list', '.contextForm').size();
	
	                            return currentSize == containerSize;
	                        });
	                    }*/
	
	                    return container;
	                }, RegisterMenu.functionRegistered.curry(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', newInstance), "registerMenu", function(loadFromAjax) {
	                	return !loadFromAjax;
	                }, forceRegistration, true);
	
	                if (!res) {
	                    res = RegisterMenu.functionRegistered(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', newInstance, newContent, contentToUpdate);
	                }
            	} else {
            		res = RegisterMenu.functionRegistered(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', newInstance, newContent, contentToUpdate);
            	}
            } else {
                res = RegisterMenu.functionRegistered(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', newInstance, newContent, contentToUpdate);
            }
        } else {
            res = RegisterMenu.functionRegistered(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', newInstance, newContent, contentToUpdate);
        }
        return res;
    },

    findMenus : function(newContent, containerSelector, menuItemSelector, forceReserachMenuFromNewContent, newInstance, contentToUpdate) {
        var _findMenu = function(newContent, selector, menuItemSelector, newInstance, forceReserachMenuFromNewContent, contentToUpdate, toolbarInstance) {
            var contentIsForm = false;
            var instance = null;
            if (Object.isElement($(newContent))) {
                var elementForMenuBar = (forceReserachMenuFromNewContent && Object.isElement(newContent)) ? $(newContent) :  $(document.body);
                var currentMenuBar = Object.isElement(elementForMenuBar.down(menuItemSelector)) ? elementForMenuBar.down(menuItemSelector).up('ul') : null;
                var itemListContainer = currentMenuBar;

                var buttonContent;

                if (newContent.tagName === "FORM" || newContent.tagName === "TABLE")
                    buttonContent = $(newContent);
                else
                    buttonContent = $(newContent).down(selector) /*|| $(contentToUpdate)*/;

                if (Object.isElement(buttonContent)) {
                    if (Object.isElement(buttonContent.previous('.context-menu-bar'))) {
                        var clonedItemList = buttonContent.previous('.context-menu-bar').select(menuItemSelector).map(function(element) { return element.cloneNode(true) });

                        if (Object.isElement(currentMenuBar)) {
                            if (Object.isUndefined(newInstance) || newInstance) {
                                Toolbar.clearInstance();

                                currentMenuBar.select(menuItemSelector).invoke('remove');
                                clonedItemList.each(function(element) {
                                    currentMenuBar.insert(element);
                                });

                                newInstance = true;
                            }
                        } else {
                            return false;
                        }
                    }

                    if (!newInstance) {
                    	var newInstanceField = newContent.down('input.newInstanceMenu');
                        if ((!Object.isElement(newInstanceField) || newInstanceField.getValue() === 'Y') && Object.isUndefined(newInstance) && newContent.tagName !== "TABLE" && newContent.tagName !== "FORM") {
                            // Al caricamento di un DIV cancella  solo la sua relativa instanza in toolbar
                            // Serve per il reload delle singole portlet
                            Toolbar.clearInstance();
                        }
                        instance = RegisterMenu.reloadItems(buttonContent, itemListContainer, newInstance, {itemSelector: menuItemSelector}, toolbarInstance, contentToUpdate || newContent);
                    }
                    else {
                        instance = RegisterMenu.loadItems(buttonContent, itemListContainer, newInstance, {itemSelector: menuItemSelector}, toolbarInstance, contentToUpdate || newContent);
                    }

                    return instance;
                }
            }

            return false;
        }

        if (Object.isString(containerSelector)) {
            containerSelector = [containerSelector];
        }

        if (!Object.isArray(containerSelector)) {
            return false;
        }

        var res = false;
        containerSelector.each(function(selector) {
            var newInstanceInternal = !res && newInstance;

            var toolbarInstance = _findMenu(newContent, selector, menuItemSelector, newInstanceInternal, forceReserachMenuFromNewContent, contentToUpdate, res);
            if (toolbarInstance) {
                res = toolbarInstance;
            }
        });

        return res;
    },
    loadItems : function(newContent, itemListContainer, newInstance, options, toolbarInstance, content) {
        if (!Toolbar.hasRegisteredItems()) {
            RegisterCommonMenu.loadItems(newContent);
            RegisterSearchMenu.loadItems(newContent);
            RegisterSearchResultMenu.loadItems(newContent);
            RegisterManagementMenu.loadItems(newContent);
        }
        return RegisterMenu.reloadItems(newContent, itemListContainer, newInstance, options, toolbarInstance, content);
    },
    reloadItems : function(newContent, itemListContainer, newInstance, options, toolbarInstance, content) {
        if (!Toolbar.hasRegisteredItems()) {
            return RegisterMenu.loadItems(newContent, itemListContainer, newInstance, options, toolbarInstance, content);
        }
        return Toolbar.loadRegisteredItems(newContent, itemListContainer, newInstance, options, toolbarInstance, content);
    },
    getMainActionButton : function(element, selector) {
        var res = element;

        var currentMainInstance = $A(Control.Tabs.instances).find(function(instance) {
            var mainContainer = instance.getActiveContainer();
            return mainContainer.down((this.selector || selector) + '-button');
        }.bind(this));

        if (currentMainInstance) {
            var button = currentMainInstance.getContainerAtIndex(0).down((this.selector || selector) + '-button');
            if (button)
                res = button;
        } else {
            var button = $$((this.selector || selector) + '-button');
            if (button)
                res = button.first();
        }
        return res;
    },
    selectedElementPopulateCollection : function(useDelimiterToGet, useDelimiterToPut, excludeSingleEditable, form, container) {
        var elaboratedForms = new Array();
        var portlets = Object.isElement($(container)) ? $(container).select('.portlet') : [];
        
        var containerToElaborate = container;
        if (Object.isArray(portlets) && portlets.size() > 0) {
        	var containerToElaborate = portlets.find(function(potletContainer) {
        		return potletContainer.hasClassName('main-portlet');
        	});
        	
        	if (!Object.isElement(containerToElaborate)) {
        		return;
        	}
        }
        
            var tables = Object.isElement($(containerToElaborate)) ? $(containerToElaborate).select('table') : TableKit.tables;

            tables.each(function(table) {
                if(TableKit.isSelectable(table)) {
                    if(Object.isUndefined(useDelimiterToGet) && table.hasClassName('multi-editable')){
                        useDelimiterToGet = false;
                    } else if(!excludeSingleEditable && Object.isUndefined(useDelimiterToGet) && table.hasClassName('single-editable')) {
                        useDelimiterToGet = true;
                    }
                    var selectedRows = TableKit.Selectable.getSelectedRows(table);
                    if (selectedRows) {
                        selectedRows.each(function(row, index) {
                            $A(row.select('input', 'select')).each(function(element) {
                                var elementName = element.readAttribute("name");
                                if(!useDelimiterToGet && elementName.indexOf('_o_') > -1) {
                                    elementName = elementName.substring(0,elementName.indexOf('_o_'));
                                }
                                var currentValue = element.getValue();
                                if (currentValue) {
                                    var findElement = $A(form.getElements()).find(function(elm) {
                                        return elementName === elm.readAttribute("name");
                                    });
                                    if (!useDelimiterToPut) {
                                        if (findElement && !findElement.hasClassName('url-params')) {
                                            var valueHidden = findElement.readAttribute('value');
                                            if (valueHidden) {
                                                if (valueHidden.indexOf(currentValue) == -1) {
                                                    if (valueHidden.indexOf(']') != -1) {
                                                        valueHidden = valueHidden.replace(']', '|]');
                                                    } else {
                                                        valueHidden = valueHidden.concat('|]');
                                                    }

                                                    valueHidden = valueHidden.replace(']', currentValue+ ']');
                                                    if (valueHidden.indexOf('[') == -1)
                                                        valueHidden = '['.concat(valueHidden);
                                                }
                                            } else {
                                                valueHidden = currentValue;
                                            }

                                            findElement.writeAttribute('value', valueHidden);
                                        } else if (!findElement || (findElement && !findElement.hasClassName('url-params'))) {


                                            var inputHidden = new Element('input', {'type' : 'hidden', 'value' : currentValue, 'name' : elementName, 'id' : elementName});
                                            form.insert(inputHidden);
                                        }
                                    } else {
                                        if (!findElement  || (findElement && !findElement.hasClassName('auxiliary-field'))) {
                                            var newElementName = elementName;
                                            if (elementName.indexOf('_o_') == -1) {
                                                newElementName = elementName + '_o_' + index;
                                            } else {
                                                newElementName = elementName.substring(0, elementName.indexOf('_o_')) + '_o_' + index;
                                            }
                                            var inputHidden = new Element('input', {'type' : 'hidden', 'value' : currentValue, 'name' : newElementName, 'id' : newElementName});
                                            if (element.hasClassName('auxiliary-field') && element.readAttribute('name').indexOf('_o_') == -1) {
                                                inputHidden.writeAttribute('name', elementName);
                                                inputHidden.writeAttribute('id', elementName);
                                                inputHidden.addClassName('auxiliary-field');
                                            }

                                            form.insert(inputHidden);
                                        }
                                    }
                                }
                            });
                        });

                        throw $break;
                    }
                } else if(!excludeSingleEditable && table.hasClassName('single-editable')) {
                    var originalForm = table.up('form');
                    if (!elaboratedForms.include(originalForm.identify())) {
                        elaboratedForms.push(originalForm.identify());
                        var fields = originalForm.getElements();
                        if(fields) {
                            fields.each(function(elm) {
                                form.insert(elm.cloneNode(true));
                            });
                        }
                    }
                }
            });
    }
}

RegisterCommonMenu= {
    loadItems : function(newContent) {
        if (typeof Toolbar != 'undefined') {
            Toolbar.addItem({
                selector : '.print',
                useForm : 'current',
                withOnSubmitForm : false,
                filterFormElement : function(element) {
                    if (Object.isElement(element)) {
                        var elementName = element.readAttribute('name');
                        if ('saveView' === elementName || 'filterByDate' === elementName)
                            return false;
                    }

                    return true;
                }
            }, newContent);

            //Sandro: aggiunto comportamento di apertura/chiusura menu al pulsante iniziale della toolbar
            //n.b.  Necessita di avere jquery caricato e anche resolve.conflict.js (per le animazioni)
            Toolbar.addItem(
                {
                    selector : '.openclose',
                    onClick : function(event) {
                        try {
                            Event.stop(event);
                            var mainContainer = $j("div.right-column");
                            var leftCol = $j("div#left-bar-container");
                            if (mainContainer.hasClass("right-column-fullopen")) {
                                //menu chiuso, lo apro
                                mainContainer.removeClass("right-column-fullopen");
                                leftCol.show("slow");
                                $j("li.open").show();
                                $j("li.close").hide();
                            } else {
                                leftCol.hide("slow", function() {
                                mainContainer.addClass("right-column-fullopen");
                                $j("li.open").hide();
                                $j("li.close").show();
                                });
                            }
                        } catch(e) { }
                    }
                }, newContent
            );

        }
    }
}


RegisterSearchMenu = {
    loadItems : function(newContent) {
        if (typeof Toolbar != 'undefined') {
            Toolbar.addItem(
                {
                    selector : '.search',
                    useForm : 'current',
                    checkExecutability : function(callBack, form) {
                    	//check if there is input[class=light-mandatory], almost one is mandatory
    	                var noOnePopulate = false;
			            var mandatoryFields = form.select("input.light-mandatory");
			            if(mandatoryFields && mandatoryFields.size() > 0){
                        	noOnePopulate = true;
			            }
                        mandatoryFields.each(function(element) {
                        	if(element.getValue()) {
                        		noOnePopulate = false;
                        	}
                        });
                        
                        if(noOnePopulate) {
                        	modal_box_messages._resetMessages();
                        	modal_box_messages.alert(['BaseMessageNoOneSearchParameters']);

                            return false;
                        }
                        
                        callBack();
                        return true;
                    }
                }, newContent
            );

            Toolbar.addItem({
                selector : '.export-search',
                useForm : 'current'
                , getActionButton : function(element, selector) {
                    var button = $$(selector + '-button').first();
                    var form = button.up('form');
                    button.writeAttribute('href', form.action);
                    button.writeAttribute('target', '_blank');
                    return button;
                }
                , populateElementCollection : function(form, content) {
                    form.insert(new Element('input', { 'type': 'hidden', 'name': 'exportSearchButton', 'value': 'Y' }));
                    form.insert(new Element('input', { 'type': 'hidden', 'name': 'PAGINATOR_NUMBER', 'value': '0' }));
                    form.insert(new Element('input', { 'type': 'hidden', 'name': 'VIEW_INDEX_0', 'value': '0' }));
                    form.insert(new Element('input', { 'type': 'hidden', 'name': 'VIEW_SIZE_0', 'value': '65000' }));
                }
            }, newContent);
            
            Toolbar.addItem({
                selector : '.search-massive-print'
                , messageCallBack : function(callBack, element) {
                	if (!element.hasClassName('single-print')) {
	                	var url = null;
	                	if (Object.isElement(element) && element.tagName === "A") {
	                		url = element.href;
	                	}
	                	
	                    Utils.showModalBox(url, {'afterLoadModal' : function() {
	                    	$(Modalbox.MBcaption).update($('popup-print-box-title').innerHTML);
	                        Modalbox._setPosition();
	                    }, 'afterHideModal' : function(parametersToSubmit, validateFunction) {
	                    	parametersToSubmit = $H(parametersToSubmit);
	                    	if (Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
	                    		this.parametersToSubmit = parametersToSubmit;
	                    		if (Object.isFunction(validateFunction)) {
	                    			var params = $H(parametersToSubmit);
	                    			if (element.tagName==='A') {
	                    				var href = element.readAttribute('href');
	                    				if (href && href.indexOf('?') != -1) {
	                    					var paramsHref = href.substring(href.indexOf('?')+1);
	                    					var paramsHrefMap = $H(paramsHref.toQueryParams());
	                    					paramsHrefMap.each(function(pair) {
	                    						if (!params.get(pair.key)) {
	                    							params.set(pair.key, pair.value);
	                    						}
	                    					});
	                    				}
	                    			}
	    	                    	validateFunction(params, callBack);
	                    		} else {
	                    			callBack();
	                    		}
	                    		
	                    	} else {
	                    		delete this.parametersToSubmit;
	                    	}
	                    }.bind(this), 'resizeToContent' : true});
                	} else {
                		callBack();
                	}
                },
                elaborateForm : function(form) {
                	var action = form.readAttribute('action');
                	var parametersToSubmit = $H(this.parametersToSubmit);
                	if (action && Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
                		var questionIndex = action.indexOf('?');
                		form.writeAttribute('action', action.substring(0, action.lastIndexOf('/')+1) + 'workEffortRootSearchResultContainerOnly' + (questionIndex != -1 ? action.substring(questionIndex) : ''));
                	}
                	
                	// eseguo qui request e return null cosi' non viene eseguito submit della form
                	new Ajax.Request(form.readAttribute('action'), {
	                    parameters: form.serialize(true),
	                    onSuccess: function(transport) {
	                    	var data = transport.responseText.evalJSON(true);
	                    	if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
	                            modal_box_messages._resetMessages();
	                            modal_box_messages.onAjaxLoad(data, Prototype.K);
	                            return false;
	                        }
	                        else {
	                        	var result = Object.isUndefined(data.resultList) ? data : data.resultList[0];
	                            if (result.sessionId) {
	                            	var sessionId = result.sessionId;
	                            	if(sessionId) {
    	                            	modal_box_messages._loadMessage(null, 'BaseMessageExecute_sessionId', function(msg) {
    	                            		msg = msg.replace("sessionId", sessionId);
    	                            		modal_box_messages.alert(msg);
        	                            	
    	                            	});
    	                            }
	                            }
	                        }
	                    }
                	});
                    return null;
                },
                populateElementCollection : function(form, container) {
                	if (this.parametersToSubmit) {
                		this.parametersToSubmit.each(function(pair) {
                			if (pair.value) {
	                			var inputElement = form.select('input[name="'+pair.key+'"]').first();
	                			if (Object.isElement(inputElement)) {
	                				inputElement.setValue(pair.value);
	                			} else {
	                				form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
	                			}
                			}
                		});
                	}
                	form.insert(new Element('input', { 'type': 'hidden', 'name': 'massivePrintButton', 'value': 'Y' }));
                }
            }, newContent);

            Toolbar.addItem(
                {
                    selector : '.search-insertmode',
                    useForm : 'current',
                    filterFormElement : function(element) {
                        return element.hasClassName('form-location') && element.readAttribute('name') !== 'backAreaId' && element.readAttribute('name') !== 'saveView';
                    },
                    onSubmit : function(onclickStr, form) {
                        if (onclickStr && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                            var argument = onclickStr.substring(onclickStr.indexOf('(')+ 2, onclickStr.lastIndexOf(','));

                            var parameters = onclickStr.substring(onclickStr.lastIndexOf(',')+ 1, onclickStr.indexOf('\')'));
                            var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));

                            if (parameters && Object.isString(parameters)) {
                                var parametersMap = $H(parameters.toQueryParams());
                                if (parametersMap) {
                                    parametersMap.each(function(pair) {
                                        var field = form.getInputs('hidden', pair.key).first();
                                        if (field) {
                                            field.writeAttribute('value', pair.value);
                                        } else {
                                            field = new Element('input', { 'type': 'hidden', 'name': pair.key, 'value': pair.value });
                                            form.insert(field);
                                        }
                                    });
                                }
                            }

                            var selectableTable = $A($(container).select('table')).find(function(table) {
                                return TableKit.isSelectable(table);
                            });

                            var managementFormType = parametersMap.get("managementFormType");
                            var wizard = parametersMap.get("wizard");

                            if (wizard === "Y")
                                return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\', \'' + container + '\',\'' + argument + '\', {postLoadElaborateContent : RegisterManagementMenu.postLoadElaborateContent}); return false;';
                            else {
                                if (selectableTable && (managementFormType === "multi")) {
                                       return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\', \'' + container + '\',\'' + argument + '\', {preLoadElaborateContent : RegisterManagementMenu.preLoadElaborateContent, postLoadElaborateContent : RegisterManagementMenu.postLoadElaborateContent}); return false;';
                                } else {
                                    return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\', \'' + container + '\',\'' + argument + '\'); return false;';
                                }
                            }
                        }
                        return onclickStr;
                    },
                    onClickDisableToolbar :  RegisterManagementMenu.disableAllItemToolbar
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.back',
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.search-reset',
                    onClick : function(event) {
                        var element = Event.element(event);
                        Event.stop(event);
                        var anchor = $(this.currentItem);
                        var content = $(anchor.toolbar_instance.content);
                        if(content.tagName != 'TABLE') {
                            content = content.down('table.single-editable');
                        }
                        var form = content.up('form');
                        if(form) {
                            form.reset();
                        }
                    }
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.filter-by-date',
                    useForm : 'current',
                    onSubmit : function(onclickStr, form) {
                         if (onclickStr && onclickStr.indexOf('ajaxSubmitFormUpdateAreas') == -1 && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                             var argument = onclickStr.substring(onclickStr.indexOf('(')+ 2, onclickStr.lastIndexOf(','));
                            var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));

                            return 'ajaxSubmitFormUpdateAreas(\''+ form.identify() + '\',\'' + container + '\',\'' + argument + '\'); return false;';
                        }
                        return onclickStr;
                    },
                    onClickDisableToolbar :  RegisterManagementMenu.disableAllItemToolbar
                },newContent
               );
        }
    }
}

RegisterSearchResultMenu = {
    loadItems : function(newContent) {
        if (typeof Toolbar != 'undefined') {
            Toolbar.addItem(
                {
                    selector : '.search-save-layout',
                    onClick : function(event) {
                        Event.stop(event);
                        TableKit.Customizable.saveUserpreference();
                    }
                }, newContent
            );   

         Toolbar.addItem(
                {
                    selector : '.search-selected-element',
                    populateElementCollection : RegisterMenu.selectedElementPopulateCollection.curry(true, false, true),
                    checkExecutability : function(callBack, container) {
                        var tables = TableKit.tables;
                        if (Object.isElement($(container))) {
                        	var containerToElaborate = $(container);
                        	if (containerToElaborate.hasClassName('portlet')) {
                        		containerToElaborate = containerToElaborate.up();
                        	}
                        	
                        	if (Object.isElement(containerToElaborate)) {
	                            var portlets = containerToElaborate.select('.portlet');
	                            if (Object.isArray(portlets) && portlets.size() > 0) {
	                                callBack();
	                                return true;
	                            }
	                            tables = containerToElaborate.select('table');
                        	}
                        }

                        tables.each(function(table) {
                            if(TableKit.isSelectable($(table))) {
                                var selectedRows = TableKit.Selectable.getSelectedRows(table);
                                if (selectedRows && selectedRows.size() > 0) {
                                    callBack();

                                    throw $break;
                                } else {
                                	modal_box_messages._resetMessages();
                                	modal_box_messages.alert(['BaseMessageNoSelection']);

                                    return false;
                                }
                            }

                        });

                        return true;
                    },
                    onSubmit : function(onclickStr, form) {
                        if (onclickStr && onclickStr.indexOf('ajaxSubmitFormUpdateAreas') == -1 && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                            var parameters = onclickStr.substring(onclickStr.indexOf('(')+1, onclickStr.indexOf(')'));
                            var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));
                            
                            return 'ajaxSubmitFormUpdateAreas(\''+ form.identify() + '\',\'' + container + '\',' + parameters + '); return false;';
                        }
                        return onclickStr;
                    }
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.search-print-record'
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.search-export-record'
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.search-import-std'
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.execute',
                    
                    onClick : function(event) {
                    	var enableMassiveInsert = false;
                    	var massiveInsertReloadFormId = null;
                    	var element = Event.element(event);
                    	Event.stop(event);
                        var anchor = $(this.currentItem);
                        var content = $(anchor.toolbar_instance.content);
                        if(content.tagName != 'TABLE') {
                            content = content.down('table.selectable');
                            if(typeof content === 'undefined') {
                                content = $(anchor.toolbar_instance.content).down('table.single-editable');
                            }
                        }
                        var form = content.up('form');
                        
                        
                        /* GN-407 lookup seleziona tutti */
                        if(!Object.isElement(form)) {
                        	form =  $(anchor.toolbar_instance.content).down('form');
                        	
                        	// recupera se abilitato inseriemnto massivo e in caso id della form da ricaricare
                        	enableMassiveInsertField = form.getInputs('hidden', 'enableMassiveInsert').first();
                        	if(Object.isElement(enableMassiveInsertField)) {
                        		enableMassiveInsert = enableMassiveInsertField.getValue();
                        		massiveInsertReloadFormIdField = form.getInputs('hidden', 'massiveInsertReloadFormId').first();
                            	if(Object.isElement(massiveInsertReloadFormIdField)) {
                            		massiveInsertReloadFormId = massiveInsertReloadFormIdField.getValue();
                            		
                            	}
                        	}
                        }
                        
                        //divido i due tipi di import, quello con upload e quello senza
                        if(!form.hasClassName('uploadFile')){
                        
	                        var newForm = form.cloneNode(false);
	                    	
	                        //check mandatory field
	    	                var mandatoryFields = form.select("input.mandatory");
	    	                for(var i = 0; i < mandatoryFields.size() ; i++) {
	    	                    var input = mandatoryFields[i];                             
	    	                    if(input.getValue() == "") {
	    	                    	modal_box_messages._resetMessages();
	    	                        modal_box_messages.alert(['BaseMessageSaveDataMandatoryField']);
	    	                        return false;
	    	                    }
	    	                }
	    	                
	    	                //check if there is input[class=execute-field], almost one is mandatory
	    	                var noOneChecked = false;
				            var executeFields = form.select("input.execute-field");
				            if(executeFields && executeFields.size() > 0){
				            	noOneChecked = true;
				            }
				            
				            var executeFieldStr = "";
				            for(var i = 0; i < executeFields.size() ; i++) {
				                var executeField = executeFields[i];
				                if(Object.isElement(executeField)){
				                	if("INPUT" == executeField.tagName && "checkbox" == executeField.type){
				                		if(executeField.checked == true) {
						                	noOneChecked = false;
						                	// memorizzo str con String separate da |
						                	executeFieldStr = executeFieldStr.concat("|").concat(executeField.getValue());
						                    var field = newForm.getInputs('hidden', executeField.name).first();
						                    if (field){
						                    	field.value = executeFieldStr;
						                    } else {
						                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : executeField.name, 'value' : executeFieldStr}));
						                    }
						                }
				                	}
				                }
				            }
				            if(noOneChecked){
				            	modal_box_messages._resetMessages();
				            	modal_box_messages.alert(['BaseMessageNoSelection']);
				                return false;
				            }
				            
				            // popolo campo entityListToImport
				            var optionsFieldArray = form.select("select.options-field","input.options-field");
				            if(Object.isArray(optionsFieldArray) && optionsFieldArray.size() > 0){
				            	for(var i = 0; i < optionsFieldArray.size() ; i++) {
				                    var optionsField = optionsFieldArray[i];
					                if(Object.isElement(optionsField)){
					                	if(optionsField.tagName == 'INPUT' && "checkbox" == optionsField.type) {
					                		if(optionsField.checked == true) {
					                			var field = newForm.getInputs('hidden', optionsField.name).first();
					    	                    if (field){
					    	                    	field.value = optionsField.getValue();
					    	                    } else {
					    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : optionsField.name, 'value' : optionsField.getValue()}));
					    	                    }
					                		}
					                	} else {
					                		if(optionsField.up("div.lookup_field")) {
					                			// in presenza di lookup inviamo anche i campi hidden
					                			var fieldsHidden = $(optionsField.up("div.lookup_field")).select("input[type=hidden]");
					                	    	if (fieldsHidden.size() > 0) {
					                	    	
					                	    		fieldsHidden.each(function(element) {
					                	    			var fieldHidden = newForm.getInputs('hidden', element.name).first();
								                		var fieldValue = element.getValue();
								                		if (fieldHidden){
								                			fieldHidden.value = fieldValue;
							    	                    } else {
							    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : element.name, 'value' : fieldValue}));
							    	                    }
					                		        });
					                	    	}
					                		} else if(optionsField.up("div.text-find")) {
					                			// in presenza di text-find inviamo anche il campo hidden per il case-sensitive e poi la select per la option
					                			var fieldsHidden = $(optionsField.up("div.text-find")).select("select.filter-field-selection","input[type=hidden]","input[type=checkbox]");
					                	    	if (fieldsHidden.size() > 0) {
					                	    	
					                	    		fieldsHidden.each(function(element) {
					                	    			var fieldHidden = newForm.getInputs('hidden', element.name).first();
								                		var fieldValue = element.getValue();
								                		if (fieldHidden){
								                			fieldHidden.value = fieldValue;
							    	                    } else {
							    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : element.name, 'value' : fieldValue}));
							    	                    }
					                		        });
					                	    	}
					                		}
				                			var field = newForm.getInputs('hidden', optionsField.name).first();
					                		var fieldValue = optionsField.getValue();
					                		if (field){
				    	                    	field.value = fieldValue;
				    	                    } else {
				    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : optionsField.name, 'value' : fieldValue}));
				    	                    }
					                	}
					                }
				            	}
				            }
				            
				            function launchExecution() {
				            	//change action
		    	                var action = "";
		    	                var anchorTarget = form.select("a.execute-button");
		    	                if(Object.isArray(anchorTarget) && anchorTarget.size() > 0){
		    		                var anchor = anchorTarget[0];
		    		                if(Object.isElement(anchor)){
		    		                	var target = anchor.readAttribute("onclick");
		    		                	action = target.substring(target.indexOf(',') + 1, target.lastIndexOf(','));
		    		                }
		    	                }
		    	                newForm.writeAttribute('action', action);
		    	                
		    	                new Ajax.Request(newForm.readAttribute('action'), {
		    	                    parameters: newForm.serialize(true),
		    	                    onSuccess: function(transport) {
		    	                    	var data = transport.responseText.evalJSON(true);
		    	                    	if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
		    	                            modal_box_messages._resetMessages();
		    	                            modal_box_messages.onAjaxLoad(data, Prototype.K);
		    	                            return false;
		    	                        }
		    	                        else if(enableMassiveInsert){
		    	                        	var result = Object.isUndefined(data.resultList) ? data : data.resultList[0];
		    	                            var jobLogId = result.jobLogId;
		    	                            if(jobLogId) {
		    	                            	modal_box_messages._loadMessage(null, 'BaseMessageStandardImport', function(msg) {
		    	                            		msg = msg.replace("jobLogId", jobLogId);
		    	                            		msg = msg.replace("blockingErrors", result.blockingErrors);
		    	                            		modal_box_messages.alert(msg, null, function(element) {
		    	                            			var form = $(massiveInsertReloadFormId);
			    	            						
			    	            						var onclickStr = form.readAttribute("onSubmit");
			    	    		                        var attributes =onclickStr.split(","); 
			    	    		                        var request = attributes[3];
			    	    		                        var cont = attributes[2].substring(attributes[2].indexOf('\'')+1);
			    	    		                        
			    	    		                        var parameters = $H(attributes[4].substring(0, attributes[4].lastIndexOf('\'')).toQueryParams());
			    	    		                        
			    	    		                        ajaxUpdateAreas(cont+',' + request + ',' + parameters.toQueryString());
			    	    		                        LookupProperties.afterHideModal();
		    	                            			
		    	                            		});
		    	                            	});
		    	                            }
		    	                        } else {
		    	                        	/**
	    	                            	 * GN-24 Calcolo Indicatri: se il servizio non mi restituisce una lista di elementi ma
	    	                            	 *  il singolo elemento allora prendo quello. In questo modo visualizzao il popup in ogni caso. 
	    	                            	 */
		    	                        	var result = Object.isUndefined(data.resultList) ? data : data.resultList[0];
		    	                            var jobLogId = result.jobLogId;
		    	                            if(jobLogId) {
		    	                            	modal_box_messages._loadMessage(null, 'BaseMessageStandardImport', function(msg) {
		    	                            		msg = msg.replace("jobLogId", jobLogId);
		    	                            		msg = msg.replace("blockingErrors", result.blockingErrors);
		    	                            		modal_box_messages.alert(msg);
		    	                            	});
		    	                            } else if(result.sessionId) {
		    	                            	/**
		    	                            	 * GN-24 Calcolo massivo punteggi: in questo non ho joblogid ma sessionid; quindi se non  
		    	                            	 * esiste joblog id controllo se esiste sessionid e mostro popup
		    	                            	 */
		    	                            	var sessionId = result.sessionId;
		    	                            	if(sessionId) {
		 	    	                            	modal_box_messages._loadMessage(null, 'BaseMessageExecute_sessionId', function(msg) {
		 	    	                            		msg = msg.replace("sessionId", sessionId);
		 	    	                            		modal_box_messages.alert(msg);
		 	        	                            	
		 	    	                            	});
		 	    	                            }
		    	                            } else if(!Object.isUndefined(result.listSize)) {
		    	                            	var listSize = result.listSize;
	 	    	                            	modal_box_messages._loadMessage(null, 'BaseMessageExecuteData_listSize', function(msg) {
	 	    	                            		msg = msg.replace("listSize", listSize);
	 	    	                            		modal_box_messages.alert(msg);
	 	        	                            	
	 	    	                            	});
		    	                            }
		    	                        }
		    	                    }
		    	                });
				            }
				            /**
		                	 * Per inserire un messaggio di conferma prima dell'esecuzione del servizio bisogna valorizzare nella form il campo:
		                	 * messageExecuteDataConfirmField: il messaggio che viene visualizzato
		                	 */
		                	var messageExecuteDataConfirmField = form.getInputs('hidden', 'messageExecuteDataConfirmField').first();
		                	if (Object.isElement(messageExecuteDataConfirmField) && null != messageExecuteDataConfirmField.getValue() && "" != messageExecuteDataConfirmField.getValue()) {
		                		modal_box_messages.confirm(messageExecuteDataConfirmField.getValue(), null, launchExecution);
		                	} else {
		                		launchExecution();
		                	}
		                	
				            
	                    }else{
	                    	
	    	                var noOneChecked = false;
				            var executeFields = form.select("input.execute-field");
				            if(executeFields && executeFields.size() > 0){
				            	noOneChecked = true;
				            }
				            
				            var executeFieldName = form.select("input.execute-field-name").first();
				            
				            var executeFieldStr = "";
				            for(var i = 0; i < executeFields.size() ; i++) {
				                var executeField = executeFields[i];
				                if(Object.isElement(executeField)){
				                     if(("INPUT" == executeField.tagName && "checkbox" == executeField.type && executeField.checked == true)
				                    || ("INPUT" == executeField.tagName && executeField.getValue())){
				                        if(!executeField.hasClassName('displayNone')) {
				                            noOneChecked = false;
				                        }
                                        // memorizzo str con String separate da |
                                        executeFieldStr = executeFieldStr.concat("|").concat(executeField.getValue());
                                                                                    
                                        var field = form.getInputs('hidden', executeFieldName.name).first();
                                        if (field){
                                            field.value = executeFieldStr;
                                        } else {
                                            form.insert(new Element('input', {'type' : 'hidden', 'name' : executeFieldName.name, 'value' : executeFieldStr}));
                                        }
				                	}
				                }
				            }
				            if(noOneChecked){
				            	modal_box_messages._resetMessages();
				            	modal_box_messages.alert(['BaseMessageNoSelection']);
				                return false;
				            }
				            //force startWaiting because is not ajax.Request
				            Utils.startWaiting();
				            form.submit();
	                    }
                    }
                }, newContent
            );
            
            Toolbar.addItem(
                    {
                        selector : '.email',
                        
                        onClick : function(event) {
                        	var form = null;
                        	var enableMassiveInsert = false;
                        	var element = Event.element(event);
                        	Event.stop(event);
                            var anchor = $(this.currentItem);
                            var content = $(anchor.toolbar_instance.content);
                            if(content.tagName != 'TABLE') {
                                content = content.down('table.selectable');
                                if(typeof content === 'undefined') {
                                    content = $(anchor.toolbar_instance.content).down('table.single-editable');
                                }
                            }
                            if(content) {
                            	form = content.up('form');
                            }
                            
                            if(!Object.isElement(form)) {
                            	form =  $(anchor.toolbar_instance.content).down('form');
                            }
                            
                            var newForm = form.cloneNode(false);
	                    	
	                        //check mandatory field
	    	                var mandatoryFields = form.select("input.mandatory");
	    	                for(var i = 0; i < mandatoryFields.size() ; i++) {
	    	                    var input = mandatoryFields[i];                             
	    	                    if(input.getValue() == "") {
	    	                    	modal_box_messages._resetMessages();
	    	                        modal_box_messages.alert(['BaseMessageSaveDataMandatoryField']);
	    	                        return false;
	    	                    }
	    	                }
	    	                
				            // popolo campi
				            var optionsFieldArray = form.select("select.options-field","input.options-field","textarea.options-field");
				            if(Object.isArray(optionsFieldArray) && optionsFieldArray.size() > 0){
				            	for(var i = 0; i < optionsFieldArray.size() ; i++) {
				                    var optionsField = optionsFieldArray[i];
					                if(Object.isElement(optionsField)){
					                	if(optionsField.tagName == 'INPUT' && "checkbox" == optionsField.type) {
					                		if(optionsField.checked == true) {
					                			var field = newForm.getInputs('hidden', optionsField.name).first();
					    	                    if (field){
					    	                    	field.value = optionsField.getValue();
					    	                    } else {
					    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : optionsField.name, 'value' : optionsField.getValue()}));
					    	                    }
					                		}
					                	} else {
					                		if(optionsField.up("div.lookup_field")) {
					                			// in presenza di lookup inviamo anche i campi hidden
					                			var fieldsHidden = $(optionsField.up("div.lookup_field")).select("input[type=hidden]");
					                	    	if (fieldsHidden.size() > 0) {
					                	    	
					                	    		fieldsHidden.each(function(element) {
					                	    			var fieldHidden = newForm.getInputs('hidden', element.name).first();
								                		var fieldValue = element.getValue();
								                		if (fieldHidden){
								                			fieldHidden.value = fieldValue;
							    	                    } else {
							    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : element.name, 'value' : fieldValue}));
							    	                    }
					                		        });
					                	    	}
					                		} else if(optionsField.up("div.text-find")) {
					                			// in presenza di text-find inviamo anche il campo hidden per il case-sensitive e poi la select per la option
					                			var fieldsHidden = $(optionsField.up("div.text-find")).select("select.filter-field-selection","input[type=hidden]","input[type=checkbox]");
					                	    	if (fieldsHidden.size() > 0) {
					                	    	
					                	    		fieldsHidden.each(function(element) {
					                	    			var fieldHidden = newForm.getInputs('hidden', element.name).first();
								                		var fieldValue = element.getValue();
								                		if (fieldHidden){
								                			fieldHidden.value = fieldValue;
							    	                    } else {
							    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : element.name, 'value' : fieldValue}));
							    	                    }
					                		        });
					                	    	}
					                		}
				                			var field = newForm.getInputs('hidden', optionsField.name).first();
					                		var fieldValue = optionsField.getValue();
					                		if (field){
				    	                    	field.value = fieldValue;
				    	                    } else {
				    	                    	newForm.insert(new Element('input', {'type' : 'hidden', 'name' : optionsField.name, 'value' : fieldValue}));
				    	                    }
					                	}
					                }
				            	}
				            }
				            
				            function launchExecution() {
				            	//write action
		    	                var action = "";
		    	                var anchorTarget = form.select("a.execute-button");
		    	                if(Object.isArray(anchorTarget) && anchorTarget.size() > 0){
		    		                var anchor = anchorTarget[0];
		    		                if(Object.isElement(anchor)){
		    		                	var target = anchor.readAttribute("onclick");
		    		                	action = target.substring(target.indexOf(',') + 1, target.lastIndexOf(','));
		    		                }
		    	                }
		    	                newForm.writeAttribute('action', action);
		    	                
		    	                new Ajax.Request(newForm.readAttribute('action'), {
		    	                    parameters: newForm.serialize(true),
		    	                    onSuccess: function(transport) {
		    	                    	var data = transport.responseText.evalJSON(true);
		    	                    	if (data._ERROR_MESSAGE_ != null || data._ERROR_MESSAGE_LIST_ != null) {
		    	                            modal_box_messages._resetMessages();
		    	                            modal_box_messages.onAjaxLoad(data, Prototype.K);
		    	                            return false;
		    	                        }
		    	                        else {
		    	                        	modal_box_messages._resetMessages();
		    				            	modal_box_messages.alert(['BaseMessageSendMail']);
		    				                return false;
		    	                        }
		    	                    }
		    	                });
				            }
				            /**
		                	 * Per inserire un messaggio di conferma prima dell'esecuzione del servizio bisogna valorizzare nella form il campo:
		                	 * messageExecuteDataConfirmField: il messaggio che viene visualizzato
		                	 */
		                	var messageExecuteDataConfirmField = form.getInputs('hidden', 'messageExecuteDataConfirmField').first();
		                	if (Object.isElement(messageExecuteDataConfirmField) && null != messageExecuteDataConfirmField.getValue() && "" != messageExecuteDataConfirmField.getValue()) {
		                		modal_box_messages.confirm(messageExecuteDataConfirmField.getValue(), null, launchExecution);
		                	} else {
		                		launchExecution();
		                	}
                        }
                    }, newContent
                );
            
            //Add delete button
            Toolbar.addItem(
                {
                    selector : '.search-delete',
                    populateElementCollection : RegisterMenu.selectedElementPopulateCollection.curry(true, true, true),
                    elaborateForm : RegisterManagementMenu._elaborateFormtoPutOperation.curry('DELETE', 'BaseMessageDeleteData', true, {'fromDelete': 'Y'}),
                    checkExecutability : function(callBack, container) {
                        var tables = TableKit.tables;
                        if (Object.isElement($(container))) {
                            tables = $(container).select('table');
                        }

                        tables.each(function(table) {
                            if(TableKit.isSelectable($(table))) {
                                var selectedRows = TableKit.Selectable.getSelectedRows(table);
                                if (selectedRows && selectedRows.size() > 0) {
                                    callBack();

                                    throw $break;
                                } else {
                                	modal_box_messages._resetMessages();
                                    modal_box_messages.alert(['BaseMessageNoSelection']);

                                    return false;
                                }
                            }
                        });

                        return true;
                    },
                    messageCallBack : function(callBack) {
                    	modal_box_messages.confirm(['BaseMessageDeleteDataConfirm'],null,callBack);
                    },
                    onSubmit : function(onclickStr, form) {
                        if (onclickStr && onclickStr.indexOf('ajaxSubmitFormUpdateAreas') == -1 &&  onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                            var parameters = onclickStr.substring(onclickStr.indexOf('(')+1, onclickStr.indexOf(')'));
                            var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));

                            return 'ajaxSubmitFormUpdateAreas(\''+ form.identify() + '\',\'\',' + parameters + '); return false;';
                        }
                        return onclickStr;
                    },
                    onClickDisableToolbar :  RegisterManagementMenu.disableAllItemToolbar
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.search-print-birt',
                    messageCallBack : function(callBack, element) {
                    	if (!element.hasClassName('single-print')) {
    	                	var url = null;
    	                	if (Object.isElement(element) && element.tagName === "A") {
    	                		url = element.href;
    	                	}
    	                	
    	                    Utils.showModalBox(url, {'afterLoadModal' : function() {
    	                    	$(Modalbox.MBcaption).update($('popup-print-box-title').innerHTML);
    	                        Modalbox._setPosition();
    	                    }, 'afterHideModal' : function(parametersToSubmit, validateFunction) {
    	                    	parametersToSubmit = $H(parametersToSubmit);
    	                    	if (Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
    	                    		this.parametersToSubmit = parametersToSubmit;
    	                    		if (Object.isFunction(validateFunction)) {
    	                    			var params = $H(parametersToSubmit);
    	                    			if (element.tagName==='A') {
    	                    				var href = element.readAttribute('href');
    	                    				if (href && href.indexOf('?') != -1) {
    	                    					var paramsHref = href.substring(href.indexOf('?')+1);
    	                    					var paramsHrefMap = $H(paramsHref.toQueryParams());
    	                    					paramsHrefMap.each(function(pair) {
    	                    						if (!params.get(pair.key)) {
    	                    							params.set(pair.key, pair.value);
    	                    						}
    	                    					});
    	                    				}
    	                    			}
    	                    			validateFunction(params, callBack);
    	                    		} else {
    	                    			callBack();
    	                    		}
    	                    		
    	                    	} else {
    	                    		delete this.parametersToSubmit;
    	                    	}
    	                    }.bind(this), 'resizeToContent' : true});
                    	} else {
                    		callBack();
                    	}
                    },
                    elaborateForm : function(form) {
                    	var action = form.readAttribute('action');
                    	var parametersToSubmit = $H(this.parametersToSubmit);
                    	if (action && Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
                    		var questionIndex = action.indexOf('?');
                    		form.writeAttribute('action', action.substring(0, action.lastIndexOf('/')+1) + 'managementPrintBirt' + (questionIndex != -1 ? action.substring(questionIndex) : ''));
                    	}
                        return form;
                    },
                    populateElementCollection : function(form, container) {
                    	if (this.parametersToSubmit) {
                    		this.parametersToSubmit.each(function(pair) {
                    			if (pair.value) {
    	                			var inputElement = form.select('input[name="'+pair.key+'"]').first();
    	                			if (Object.isElement(inputElement)) {
    	                				inputElement.setValue(pair.value);
    	                			} else {
    	                				form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
    	                			}
                    			}
                    		});
                    		
                    	}
                    }
                }, newContent
            );
            Toolbar.addItem(
                {
                    selector : '.search-export-birt',
                    useForm : 'current'
                }, newContent
            );
        }
    },
    onClickNewsFilter : function(event) {
        Event.stop(event);
        var liExpanded = $$('li.expanded');
        var liCollapsed = $$('li.collapsed');
        if (liCollapsed && liCollapsed.size()) {
            var anchor = $(liCollapsed[0]).down('a');
            anchor.simulate('click');
        } else if (liExpanded && liExpanded.size()) {
            Form.focusFirstElement($('searchForm'));
        }
    }
}

RegisterManagementMenu = {
    loadItems : function(newContent) {
        Toolbar.addItem(
            {
                selector : '.management-search',
                onSubmit : function(onclickStr, form) {
                    CleanCookie.load();
                    
                    var oldOnclickStr = onclickStr;
                    var firstApexIndex = oldOnclickStr.indexOf("'");
                    var secondApexIndex = oldOnclickStr.indexOf("'", firstApexIndex+1);
                    onclickStr = oldOnclickStr.substring(0, firstApexIndex+1) + form.identify() + oldOnclickStr.substring(secondApexIndex);
                    
                    return onclickStr;
                }
            }, newContent
        );
        Toolbar.addItem(
            {
                selector : '.array-list',
                onSubmit : function(onclickStr, form) {
                    if (onclickStr && onclickStr.indexOf('ajaxSubmitFormUpdateAreas') == -1 && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                        var argument = onclickStr.substring(onclickStr.indexOf('(')+ 2, onclickStr.lastIndexOf(','));
                        var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));
                        return 'ajaxSubmitFormUpdateAreas(\''+ form.identify() + '\',\'' + container + '\',\'' + argument + '\'); return false;';
                     }
                    return onclickStr;
                }
            }, newContent
        );
        Toolbar.addItem(
            {
                selector : '.management-insertmode',
                useForm : 'current',
                onSubmit : function(onclickStr, form) {
                    if (onclickStr && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                    	if (onclickStr.indexOf('WorkEffortAssocExtView') >= 0 && onclickStr.indexOf('detail=Y') < 0) {
                    		return false;
                    	}
                    	
                    	var formInsertManagement = new FormInsertManagement(onclickStr, form);
                    	var argument = formInsertManagement.argument;
                    	var container = formInsertManagement.container;
                    	var parametersMap = formInsertManagement.parametersMap;

                        var currentInstance = $A(Control.Tabs.instances).find(function(instance) {
                            var activeContainer = instance.getActiveContainer();
                            return activeContainer.down((this.selector || selector) + '-button');
                        }.bind(this));

                        var activeContainer = null;
                        if (currentInstance) {
                            activeContainer = currentInstance.getActiveContainer();
                        }

                        var selectableTable = $A($(activeContainer || container).select('table')).find(function(table) {
                            return TableKit.isSelectable(table);
                        })

                        var wizard = parametersMap.get("wizard");
                        var managementFormType = parametersMap.get("managementFormType");

                        if (wizard === "Y")
                            return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\', \'' + container + '\',\'' + argument + '\', {postLoadElaborateContent : RegisterManagementMenu.postLoadElaborateContent}); return false;';
                        else {
                            if (selectableTable && (managementFormType === "multi")) {
                                   return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\', \'' + container + '\',\'' + argument + '\', {preLoadElaborateContent : RegisterManagementMenu.preLoadElaborateContent, postLoadElaborateContent : RegisterManagementMenu.postLoadElaborateContent}); return false;';
                            } else {
                                return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\', \'' + container + '\',\'' + argument + '\'); return false;';
                            }
                        }
                    }
                    return onclickStr;
                },
                onClickDisableToolbar :  RegisterManagementMenu.disableAllItemToolbar
            }, newContent
        );
        //Add save button
        Toolbar.addItem(
            {
                selector : '.save',
                useForm : 'current',
                onAfterSubmit : function(form) {
                    //Sandro: aggiunta eventuale gestione di callback prima della rimozione
                    if (Object.isFunction(form.onAfterSubmit)) {
                        form.onAfterSubmit();
                    }
                    form.remove();
                },
                messageCallBack : function(callBack, element, form) {
                	/**
                	 * Per inserire un messaggio di conferma prima del salvataggio bisogna valorizzare nella form i campi:
                	 * messageSaveDataConfirmValueField : il campo da controllare (se non è vuoto) per visialuzzare il messagio
                	 * messageSaveDataConfirmField: il messaggio che viene visualizzato
                	 */
                	var messageSaveDataConfirmValueField = form.getInputs('hidden', 'messageSaveDataConfirmValueField').first();
                	if (Object.isElement(messageSaveDataConfirmValueField) && null != messageSaveDataConfirmValueField.getValue() && "" != messageSaveDataConfirmValueField.getValue()) {
                		var elementControl = form.select('input[name="'+messageSaveDataConfirmValueField.getValue()+'"]').first()
                		if (Object.isElement(elementControl) && null != elementControl.getValue() && "" != elementControl.getValue()) {
                			var messageSaveDataConfirmField = form.getInputs('hidden', 'messageSaveDataConfirmField').first();
                			modal_box_messages.confirm(messageSaveDataConfirmField.getValue(), null, function () { 
                					return RegisterManagementMenu._checkExecutability(callBack, form); 
                				});
                		} else {
                			RegisterManagementMenu._checkExecutability(callBack, form);                			
                		}
                	} else {
                		RegisterManagementMenu._checkExecutability(callBack, form);
                	}
                },
                elaborateForm : RegisterManagementMenu._filterDataToSave.curry(null, 'BaseMessageSaveData', false, null),
                onClickDisableToolbar :  RegisterManagementMenu.disableAllItemToolbar
            }, newContent
        );
        
        //Add delete button
        Toolbar.addItem(
            {
                selector : '.management-delete',
                populateElementCollection : RegisterMenu.selectedElementPopulateCollection.curry(true, true, false),
                checkExecutability : function(callBack, form) {
                    var table = form.down('table.single-editable');
                    if (!Object.isElement(table)) {
                    	table = form.down('table.multi-editable');
                    }

                    if (table && TableKit.isSelectable(table)) {
                        var selectedRows = TableKit.Selectable.getSelectedRows(table);
                        if (selectedRows && selectedRows.size() > 0) {
                            //callBack();
                            this.messageCallBack(callBack, null, form);

                            return true;
                        } else {
                        	modal_box_messages._resetMessages();
                            modal_box_messages.alert(['BaseMessageNoSelection']);

                            return false;
                        }
                    } else {
                        this.messageCallBack(callBack, null, form);
                    }

                    return true;
                },
                elaborateForm : RegisterManagementMenu._elaborateFormtoPutOperation.curry('DELETE', 'BaseMessageDeleteData', true, {'fromDelete': 'Y'}),
                messageCallBack : function(callBack, element, form) {
                	// il valore del messaggio di alert viene sovrascritto da quello presente nell'innerHTML dell'anchor
                	var messageDeleteDataConfirm = 'BaseMessageDeleteDataConfirm';
                	var messageDeleteDataConfirmField = form.getInputs('hidden', 'messageDeleteAllDataConfirm').first();
                    if(Object.isElement(messageDeleteDataConfirmField) && null != messageDeleteDataConfirmField.getValue() && "" != messageDeleteDataConfirmField.getValue()) {
                		messageDeleteDataConfirm = messageDeleteDataConfirmField.getValue();
                	}
                    modal_box_messages.confirm([messageDeleteDataConfirm],null,callBack);
                },
                onSubmit : function(onclickStr, form) {
                	if (onclickStr && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                    	var field = form.getInputs('hidden', "postDeleteEntityName").first();
                    	var postDeleteEntityNameValue = null;
                    	if(Object.isElement(field)) {
                    		postDeleteEntityNameValue = field.getValue();
                    	}
                		var argument = onclickStr.substring(onclickStr.indexOf(','), onclickStr.lastIndexOf(','));

                        var parameters = onclickStr.substring(onclickStr.lastIndexOf(',')+ 1, onclickStr.indexOf('\')'));
                        var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));
						if(postDeleteEntityNameValue) {
							if (parameters && Object.isString(parameters)) {
	                            var parametersMap = $H(parameters.toQueryParams());
	                            if (parametersMap) {
	                                parametersMap.each(function(pair) {
	                                    if("entityName" == pair.key) {
	                                    	parametersMap.set(pair.key, postDeleteEntityNameValue);
	                                    }
	                                });
	                                parameters = $H(parametersMap).toQueryString();
	                            }
	                        }
						}
						return 'ajaxSubmitFormUpdateAreas(\'' + form.identify() + '\'' + argument + ',' + parameters + '\'); return false;';
                    	
                    }
                    return onclickStr;
                },
                onClickDisableToolbar :  RegisterManagementMenu.disableAllItemToolbar
            }, newContent
        );
        Toolbar.addItem({
            selector : '.management-export-birt',
            useForm : 'current',
            withOnSubmitForm : false,
            filterFormElement : function(element) {
                if (Object.isElement(element)) {
                    var elementName = element.readAttribute('name');
                    if ('saveView' === elementName)
                        return false;
                }

                return true;
            }
        }, newContent);
        Toolbar.addItem({
            selector : '.management-import-std',
            useForm : 'current',
            withOnSubmitForm : false,
            filterFormElement : function(element) {
                if (Object.isElement(element)) {
                    var elementName = element.readAttribute('name');
                    if ('saveView' === elementName)
                        return false;
                }

                return true;
            }
        }, newContent);
      Toolbar.addItem(
            {
                selector : '.management-print-birt-gzoom2',
                onClick : function(event) {
                    try {
                        console.log("management-print-birt-gzoom2");
                        Event.stop(event);
                        var element = Event.element(event);
                        var url = null;
                        if (Object.isElement(element) && element.tagName === "A") {
                            url = element.href;
                        }
                        var paramsHref = url.substring(url.indexOf('?')+1);
                        var paramsHrefMap = $H(paramsHref.toQueryParams());
                        let msg={event : "print", printParams: paramsHref.toQueryParams()};

                        var w = window.top;
                        w.postMessage(msg, '*');
                    } catch(e) { }
                }
            }, newContent
        );
            
        Toolbar.addItem(
            {
                selector : '.management-print-birt',
                messageCallBack : function(callBack, element) {
                	if (!element.hasClassName('single-print')) {
	                	var url = null;
	                	if (Object.isElement(element) && element.tagName === "A") {
	                		url = element.href;
	                	}
	                	
	                    Utils.showModalBox(url, {'afterLoadModal' : function() {
	                    	$(Modalbox.MBcaption).update($('popup-print-box-title').innerHTML);
	                        Modalbox._setPosition();
	                    }, 'afterHideModal' : function(parametersToSubmit, validateFunction) {
	                    	parametersToSubmit = $H(parametersToSubmit);
	                    	if (Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
	                    		this.parametersToSubmit = parametersToSubmit;
	                    		if (Object.isFunction(validateFunction)) {
	                    			var params = $H(parametersToSubmit);
	                    			if (element.tagName==='A') {
	                    				var href = element.readAttribute('href');
	                    				if (href && href.indexOf('?') != -1) {
	                    					var paramsHref = href.substring(href.indexOf('?')+1);
	                    					var paramsHrefMap = $H(paramsHref.toQueryParams());
	                    					paramsHrefMap.each(function(pair) {
	                    						if (!params.get(pair.key)) {
	                    							params.set(pair.key, pair.value);
	                    						}
	                    					});
	                    				}
	                    			}
	                    			validateFunction(params, callBack);
	                    		} else {
	                    			callBack();
	                    		}
	                    		
	                    	} else {
	                    		delete this.parametersToSubmit;
	                    	}
	                    }.bind(this), 'resizeToContent' : true});
                	} else {
                		callBack();
                	}
                },
                elaborateForm : function(form) {
                	var action = form.readAttribute('action');
                	var parametersToSubmit = $H(this.parametersToSubmit);
                	if (action && Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
                		var questionIndex = action.indexOf('?');
                		form.writeAttribute('action', action.substring(0, action.lastIndexOf('/')+1) + 'managementPrintBirt' + (questionIndex != -1 ? action.substring(questionIndex) : ''));
                	}
                    return form;
                },
                populateElementCollection : function(form, container) {
                	if (this.parametersToSubmit) {
                		this.parametersToSubmit.each(function(pair) {
                			if (pair.value) {
	                			var inputElement = form.select('input[name="'+pair.key+'"]').first();
	                			if (Object.isElement(inputElement)) {
	                				inputElement.setValue(pair.value);
	                			} else {
	                				form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
	                			}
                			}
                		});
                		
                	}
                }
                
            }, newContent
        );
        Toolbar.addItem(
                {
                    selector : '.management-selected-element',
                    /* se la table ï¿½ single-editable useDelimiterToGet ï¿½ true altrimenti ï¿½ false*/
                    populateElementCollection : RegisterMenu.selectedElementPopulateCollection.curry(null, false, true),
                    checkExecutability : function(callBack, container) {
                        var res = true;
                        var tables = TableKit.tables;
                        
                        var containerToElaborate = $(container);
                    	if (Object.isElement(containerToElaborate) && containerToElaborate.hasClassName('portlet')) {
                    		containerToElaborate = containerToElaborate.up();
                    	}
                        
                        if (Object.isElement(containerToElaborate)) {
                            var portlets = containerToElaborate.select('.portlet');
                            if (Object.isArray(portlets) && portlets.size() > 0) {
                                callBack();
                                return true;
                            }
                            tables = $(container).select('table');
                        }

                        tables.each(function(table) {
                            if(TableKit.isSelectable($(table))) {
                                var selectedRows = TableKit.Selectable.getSelectedRows(table);
                                if (selectedRows && selectedRows.size() > 0) {
                                    if (!res)
                                        res = true;
                                    callBack();
                                    throw $break;
                                } else {
                                    if (res)
                                        res = false;
                                }
                            }
                        });

                        if (!res) {
                        	modal_box_messages._resetMessages();
                            modal_box_messages.alert(['BaseMessageNoSelection']);
                        }

                        return res;
                    },
                    onSubmit : function(onclickStr, form) {
                        if (onclickStr && onclickStr.indexOf('ajaxSubmitFormUpdateAreas') == -1 && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
                            var argument = onclickStr.substring(onclickStr.indexOf('(')+ 2, onclickStr.lastIndexOf(','));
                            var container = onclickStr.substring(onclickStr.indexOf('(\'')+ 2, onclickStr.indexOf(','));

                            return 'ajaxSubmitFormUpdateAreas(\''+ form.identify() + '\',\'' + container + '\',\'' + argument + '\'); return false;';
                        }
                        return onclickStr;
                    }
                }, newContent
            );
        Toolbar.addItem(
            {
                selector : '.management-reset',
                onClick : function(event) {
                    var element = Event.element(event);
                    Event.stop(event);
                    var anchor = $(this.currentItem);
                    var content = $(anchor.toolbar_instance.content);
                    var contents = new Array(content);
                    if(content.tagName != 'TABLE') {
                    	
                    	/**Prendo una lista di tabelle che sono presenti nel tab attivo  */
                    	var contents = new Array();
                    	if (content.select('table.selectable').length > 0 ) {
                    		contents.push(content.down('table.selectable'));    
                    	}
                        
                    	/**Prendo tutte le form che sono presenti nel tab attivo */
                    	if ($(anchor.toolbar_instance.content).select('table.single-editable').length > 0) {
                    		contents.push($(anchor.toolbar_instance.content).down('table.single-editable'));
                    	}
                    	
                    	/**Prendo tutte le form di tipo totale */
                    	if ($(anchor.toolbar_instance.content).select('table.single-editable-totale').length > 0) {
                    		contents.push($(anchor.toolbar_instance.content).down('table.single-editable-totale'));
                    	} 
                        
                        
                    } 
                    
                    /** Ciclo tutti gli elementi trovati nella form */
                    contents.each(function(contentElement){
                    	var form = contentElement.up('form');
                    	if (typeof form !== 'undefined') {
	                        if(typeof FormKit.Cachable !== 'undefined') {
	                            if(FormKit.isCachable(form)) {
	                            	if (TableKit.isSelectable(contentElement)) {                        	
	                            		var table = form.down("table.selectable");
	                            		if(Object.isElement(table)) {
	                            			var bodyRows = TableKit.getBodyRows(table);
	                            			bodyRows = bodyRows.select(function(row) {
	    	                        			var operationElement = row.descendants().find(function(element) {
	    	                        				return element.readAttribute('name') && element.readAttribute('name').startsWith('operation');
	    	                        			});
	    	                        			return Object.isElement(operationElement) && operationElement.getValue() === 'CREATE';
	    	                        		})
	                            		}
	                            		bodyRows.each(function(row) {
	                        				row.remove();
	                            		});
	                            		
	                            	}
	                            	FormKit.Cachable.resetForm(form);
	                            	
	                            	if (TableKit.isSelectable(contentElement)) {
	                            		TableKit.refreshBodyRows(contentElement);
	                            		var lastRow = TableKit.getRow(contentElement, TableKit.getBodyRows(contentElement).size()-1);
	                            		if (Object.isElement(lastRow)) {
	                            			lastRow.down('td').fire('dom:mousedown')
	                            		}
	                            	}
	                            }
	                        }
                    	}
	            	});
                    
                    
                }

            }, newContent
        );
        Toolbar.addItem(
            {
                selector : '.attachment'
            }, newContent
        );
    },
    
    /** disabled all item for active toolbar,
     * remove anchor, if exists
     * update className with "-disabled" and "disabled",
     * replace innerHTML from anchor to list,
     * copy id from anchor to list*/
    disableAllItemToolbar: function() {  
    	//log.debug("*************** disableAllItemToolbar");
    	var items = Toolbar.getInstance().items;
        items.each(function(item) {
        	var currentItem = $(item.currentItem);
        	if (Object.isElement(currentItem)) {
            	var listItem = $($(currentItem).up('li'));
            	if (Object.isElement(listItem)) {
                    var innerHTML = listItem.down('a').innerHTML;
	        		var anchorElementRemoved = listItem.down('a').remove();
	        		listItem.innerHTML = innerHTML;
	        		listItem.id = item.currentItem;
	            	
	            	// disable className
	            	var classNameStr = listItem.className;
	            	var classNameArray = classNameStr.split(' ');
	            	classNameArray.each(function(element, index){
	            		if('action-menu-item' != element && 'fa' != element && 'fa-2x' != element){
	            			classNameArray[index] = element + '-disabled';
	            		}
	            	});
	            	classNameArray.push('disabled');
	            	listItem.className = classNameArray.join(' ');
	            }
            }
        });
        //log.debug("*************** fine disableAllItemToolbar");
    },
     
    _checkModification : function(form) {
        if (form) {
            if(typeof FormKit.Cachable !== 'undefined') {
                if (FormKit.isCachable(form)) {
                    var deltaObject = $H(FormKit.Cachable.checkModification(form, FormKitExtension.checkModficationFilterCallback));
                    return deltaObject;
                }
            }
        }
        return null;
    },

    _getFormOperation : function(form) {
        var operationField = form.getInputs('hidden', 'operation').first();
        if (operationField)
            return $F(operationField);
        var table = form.down('table.multi-editable');
        if (Object.isElement(table)) {
            var lastRow = TableKit.getBodyRows(table).last();
            var operationField = lastRow.descendants().find(function(element) {
                if (element.getAttribute('name')) {
                    return -1 != element.getAttribute('name').search('operation');
                }
                else return false;
            });
            if (Object.isElement(operationField)) {
                return operationField.getAttribute('value');
            }
        }
        return null;
    },

    _getFormEntityPkFields : function(form, removeField) {
        var field = form.getInputs('hidden', 'entityPkFields').first();
        if (field) {
            if (removeField)
                field.remove();
            return $F(field);
        }
        var table = form.down('table.multi-editable');
        if (Object.isElement(table)) {
            var firstRow = TableKit.getBodyRows(table).first();
            var entityPkField = firstRow.descendants().find(function(element) {
                if (element.getAttribute('name')) {
                    return -1 != element.getAttribute('name').search('entityPkFields');
                }
                else return false;
            });
            if (Object.isElement(entityPkField)) {
                if (removeField)
                    entityPkField.remove();
                return $F(entityPkField);
            }
        }
        return null;
    },

    _getFormEntityPkFieldList : function(form, removeField) {
        var str = RegisterManagementMenu._getFormEntityPkFields(form, removeField);
        return str ? $A(str.split('|')) : $A();
    },

    _checkExecutability : function(callBack, form) {
        var operation = RegisterManagementMenu._getFormOperation(form) || '';
        var deltaObject = RegisterManagementMenu._checkModification(form);

        if (operation === 'CREATE' || (deltaObject && deltaObject.keys() && deltaObject.keys().size() > 0)) {
            if (typeof(ValidationManager)!='undefined') {
                Validation.addAllThese(CustomValidationArray);
                var validationOK = true;
                var fieldsToValidate = new Array();

                var table = form.down('table.multi-editable');
                var multiForm = table ? table.hasClassName('multi-editable') : false;
                if (multiForm) {
                    var createdRows = new Array();

                    for (var i = 0; i < deltaObject.keys().size(); i++) {
                        var formElement = form.down('[name=\"' + deltaObject.keys()[i] + '\"]');
                        if (Object.isElement(formElement) && createdRows.indexOf(formElement.up('tr')) < 0) {
                            var rowIndex = TableKit.getRowIndex(formElement.up('tr'));
                            var rowOperation = formElement.up('tr').down('input[name=\"operation_o_' + rowIndex + '\"]').getValue();
                            if (rowOperation === 'CREATE') {
                                createdRows.push(formElement.up('tr'));
                            }
                        }
                    }

                    createdRows.each(function(createdRow) {
                        createdRow.select('input', 'textarea', 'select').each(function(element) {
                               fieldsToValidate.push(element);
                               deltaObject.unset(element.readAttribute('name'));
                        });
                    });

                }
                else {
                    // Single Form
                    if (operation === 'CREATE') {
                        form.getElements().each(function(element) {
                            fieldsToValidate.push(element);
                               deltaObject.unset(element.readAttribute('name'));
                        });
                    }
                }

                for (var i = 0; i < deltaObject.keys().size(); i++) {
                    fieldsToValidate.push(form.down('[name=\"' + deltaObject.keys()[i] + '\"]'));
                }

                for(var i = 0; validationOK && i < fieldsToValidate.size(); i++) {
                    validationOK = Validation.validate(fieldsToValidate[i], {focusOnError: false, onSubmit: false});
                }

                if (validationOK) {
                    if (Object.isFunction(callBack)) {
                        callBack();
                    }
                } else {
                	modal_box_messages._resetMessages();
                    
                    modal_box_messages.alert(['BaseMessageSaveDataMandatoryField']);
                    return false;
                }
            }
        } else {
        	modal_box_messages._resetMessages();
            
            modal_box_messages.alert(['BaseMessageSaveDataNoData']);
            return false;
        }

        //Stefano Zilocchi***********************************************
//        if(LiveValidationManager.onSave()==false){
//
//        	modal_box_messages.alert(['BaseMessageSaveDataMandatoryField']);
//        	if (Object.isFunction(callBack)) {
//                callBack();
//            }
//
//
//        	//return false;
//
//        }


        return true;
    },

    _cloneForm : function(originalForm) {
        if (originalForm) {
            var newForm = originalForm.cloneNode(true);
            var newFormId = newForm.readAttribute('id') + '_clone';
            newForm.writeAttribute('id', newFormId);

            var onSubmitStr = newForm.readAttribute('onsubmit');
            if (onSubmitStr) {
                var argument = onSubmitStr.substring(onSubmitStr.lastIndexOf('(')+ 1, onSubmitStr.lastIndexOf(')'));
                argument = argument.gsub(' ', '+');
                argument = argument.gsub(',', ' ');
                var argumentArray = $w(argument);
                argumentArray[0] = '\'' + newFormId + '\'';
                argument = argumentArray.join(',');
                onSubmitStr = onSubmitStr.substring(0, onSubmitStr.lastIndexOf('(')+ 1) + argument + onSubmitStr.substring(onSubmitStr.lastIndexOf(')'));

                newForm.writeAttribute('onsubmit', onSubmitStr);
            }

            var specialeCaseFromOldForm = originalForm.getElements().select(function(element) {
                return ['textarea','select'].include(element.tagName.toLowerCase()) || element.type === 'file';
            });
            if (specialeCaseFromOldForm && specialeCaseFromOldForm.size() > 0) {
                var newFormElements = newForm.getElements();
                specialeCaseFromOldForm.each(function(element) {
                    var name = element.readAttribute('name');

                    var newElement = newFormElements.find(function(elementInNewForm) {
                        return elementInNewForm.readAttribute('name') === name;
                    });

                    if (newElement) {
                        if (newElement.type === 'file') {
                            // Correzione per CHROME
                            var fileName = element.files.item(0).fileName;
                            var td = element.up('td');
                            newElement.replace(element);
                            td.insert(fileName);
                        }
                        else {
                            newElement.setValue($F(element));
                        }
                    }
                });
            }

            return newForm;
        }
        return null;
    },

    _filterDataToSave : function(operation, messageContext, onlySelectedRow, extraParameters, form) {

        var operationFormValue = operation;
        if (!operationFormValue || operationFormValue == '')
            operationFormValue = RegisterManagementMenu._getFormOperation(form) || '';

        var deltaObject = RegisterManagementMenu._checkModification(form);

        var entityPkFieldList = RegisterManagementMenu._getFormEntityPkFieldList(form, false);

        var table = form.down('table.multi-editable');
        var multiForm = table ? table.hasClassName('multi-editable') : false;

        var toSendForm;

        if (form.hasClassName('content-upload-form') && operationFormValue == 'CREATE') {
            toSendForm = RegisterManagementMenu._cloneForm(form);
            var entityPkFieldListElement = toSendForm.down('[name=\"entityPkFields\"]');
            if (Object.isElement(entityPkFieldListElement)) {
            	var entityPkFieldList =  entityPkFieldListElement.getValue();
            	toSendForm.getElements().each(function(element) {
            		if (element.readAttribute('name').search('entityPkFields') != -1) {
            			element.remove();
            		}
            	})
            }
        }
        else {
            var formElements = form.getElements();

            if (deltaObject) {
                  var elementsToAdd = new Hash();
                   if (multiForm) {
                       var changedRows = new Array();

                    for (i = 0; i < deltaObject.keys().size(); i++) {
                        var formElement = form.down('[name=\"' + deltaObject.keys()[i] + '\"]');
                        if (Object.isElement(formElement) && changedRows.indexOf(formElement.up('tr')) < 0) {
                            changedRows.push(formElement.up('tr'));
                        }
                    }

                    changedRows.each(function(row) {
                        elementsToAdd = elementsToAdd.merge(RegisterManagementMenu._collectFormElements(row, entityPkFieldList));
                    });
                } else { // singleForm
                    elementsToAdd = RegisterManagementMenu._collectFormElements(form, entityPkFieldList);
                  }

                deltaObject = deltaObject.merge(elementsToAdd);
            }

            if (operationFormValue == 'CREATE') {
                  var fieldsToDelete = deltaObject.keys().findAll(function(key) {
                       return key.search('entityPkFields') != -1;
                   });
                   if (Object.isArray(fieldsToDelete) && fieldsToDelete.size() > 0) {
                       fieldsToDelete.each(function(fieldName) {
                           deltaObject.unset(fieldName);
                       });
                   }
            }

            toSendForm = form.cloneNode(false);
            var newFormId = form.readAttribute('id') + '_toSend';
            toSendForm.writeAttribute('id', newFormId);
            toSendForm.writeAttribute('name', form.readAttribute('name'));
            var onsubmit = form.readAttribute('onsubmit');
            if (Object.isString(onsubmit)) {
                var ajaxSubmitIndex = onsubmit.search('ajaxSubmitFormUpdateAreas');
                if (ajaxSubmitIndex != -1) {
                       toSendForm.writeAttribute('onsubmit', onsubmit.replace(form.readAttribute('id'), newFormId));
                }
            }

            deltaObject.keys().each(function(fieldName) {
                var oldNode = form.down('[name=\"' + fieldName + '\"]');
                var newNode = oldNode.cloneNode("SELECT" === oldNode.tagName);

                  toSendForm.insert(newNode);
                  newNode.setValue(oldNode.getValue());
            });

            if (multiForm) {
                   toSendForm.insert(new Element('input', {'name': '_useRowSubmit', 'value': 'Y'}));
            }
        }

        toSendForm.setStyle({visibility: 'hidden'});
        document.body.insert(toSendForm);
        return RegisterManagementMenu._elaborateFormtoPutOperation(operation, messageContext, onlySelectedRow, extraParameters, toSendForm);
    },

    _collectFormElements : function(toIterate, entityPkFieldList) {
        var classNameSubmitField = 'submit-field';
        var classNameMandatoryField = 'mandatory';
        var mandatoryFields = ['crudService', '_AUTOMATIC_PK_'];

        var elementsToAdd = new Hash();

        toIterate.select('input', 'textarea', 'select').each(function(formElement) {
            var formElementName = formElement.readAttribute('name');
            if (formElementName
                && (
                    formElement.hasClassName(classNameSubmitField) || formElement.up().hasClassName(classNameSubmitField) || formElement.readAttribute('type') === 'hidden'
                    || formElement.hasClassName(classNameMandatoryField) || formElement.up().hasClassName(classNameMandatoryField)
                    || mandatoryFields.indexOf(formElementName) != -1
                    || entityPkFieldList.find(function(pkField) {
                           return formElementName === pkField || formElementName.startsWith(pkField + '_o_');
                       })
                )
            ) {
                 var formElementValue = $F(formElement);
                   elementsToAdd.set(formElementName, formElementValue);
            }
        });

        return elementsToAdd;
    },

    _elaborateFormtoPutOperation : function(operation, messageContext, onlySelectedRow, extraParameters, form) {
        if (form) {
            if (messageContext) {
                var action = form.readAttribute('action');
                if (action) {
                    var url = action.substring(0, action.indexOf('?') == -1 ? action.length : action.indexOf('?'));

                    var queryParams = action.indexOf('?') == -1 ? {} : action.substring(action.indexOf('?') + 1).toQueryParams();
                    queryParams['messageContext'] = messageContext;

                    action = url + '?' + $H(queryParams).toQueryString();
                }
                form.writeAttribute('action', action);
            }

            if (operation) {
                var operationElements = $A(form.getInputs()).select(function(element) {
                    var returnValue = (/^operation([_]|$)/).exec(element.readAttribute('name'));
                    if(onlySelectedRow) {
                        if (element.up('tr')) {
                            returnValue = returnValue && $(element.up('tr')).hasClassName('selected-row');
                        }
                    }
                    return returnValue;
                });

                if (operationElements && operationElements.length > 0) {
                    $A(operationElements).each(function(element) {
                        element.writeAttribute('value', operation);
                    });
                } else {
                    var inputHidden = new Element('input', {'type' : 'hidden', 'value' : operation, 'name' : 'operation', 'id' : 'operation'});
                    form.insert(inputHidden);
                }
            }

            if (extraParameters) {
                var map = $H(extraParameters);
                map.each(function(pair) {
                    var findElement = $A(form.getElements()).find(function(elm) {
                        return pair.key === elm.readAttribute('name');
                    });

                    if (!findElement) {
                        var inputHidden = new Element('input', {'type' : 'hidden', 'value' : pair.value, 'name' : pair.key, 'id' : pair.key});
                        form.insert(inputHidden);
                    }
                });
            }

            if (FormKit.isCachable(form)) {
                var op = FormKit.option('freeze', form.identify());
                op.freeze = true;

                FormKit.register(form, op);
            }
        }
        return form;
    },

    /**
     * New element's input field have to be reindexed
     */
    _reindexNewElement: function(currentContent, newElement, filterRow) {
        var trArray = $A(currentContent.select("tr"));
        
        if (Object.isFunction(filterRow)) {
        	trArray = $A(trArray).select(filterRow);
        }
        var idx = -1;

        //now looking for index
        trArray.each(function(tr) {
            var input = tr.down("input[name]");
            if (input) {
                var name = input.readAttribute("name");
                if (name.lastIndexOf("_o_")>-1) {
                    var pos = name.lastIndexOf("_");
                    var fieldIdx = name.slice(pos+1);
                    fieldIdx = parseInt(fieldIdx);
                    if (fieldIdx > idx) {
                        idx = fieldIdx;
                    }
                }
            }
        }.bind(this));

        //now set new index
        if (idx > -1) {
            idx = idx + 1;
            var elArray = newElement.select("div, input, select, textarea, span");
            elArray.each( function(el) {
                var name = el.readAttribute("name");
                var id = el.readAttribute("id");
                //manage name
                if (name) {
                    name = this._changeIndex(name, idx);
                    el.writeAttribute("name", name);
                }
                if (id) {
                    id = this._changeIndex(id, idx);
                    el.writeAttribute("id", id);
                }
                if (el.hasClassName("lookup_field")) {
                    this._reindexNewAutocompleterElement(el, idx);
                }
                if (el.hasClassName("droplist_field")) {
                    this._reindexNewAutocompleterElement(el, idx);
                }
            }.bind(this));
        }
    },

    _reindexNewAutocompleterElement: function(element, newIdx) {
    	var constraintFieldsElement = element.select("input.autocompleter_parameter[name=\"constraintFields\"]");
    	if (constraintFieldsElement.size() > 0) {
    	
    		constraintFieldsElement.each(function(constraintField) {
	            // sample value: [[[roleTypeId| equals| field:roleTypeId_o_0]]]
	            var constraints = constraintField.readAttribute("value");
	            constraints = this._changeIndex(constraints, newIdx, true);
	            constraintField.writeAttribute("value", constraints);
	        }.bind(this));
    	} else {
    		var lookup = LookupMgr.getLookup(element);
    		if (lookup) {
    			lookup.refreshContraintParameters(newIndex, true);
    		}
    	}
    },

    _changeIndex: function(attr, newIdx, global) {
        if (global)
            return attr.replace(/(_o_[0-9]+)/g, "_o_" + newIdx);
        return attr.replace(/(_o_[0-9]+)/, "_o_" + newIdx);
    },

    preLoadElaborateContent : function(contentToUpdate, tmpElement) {
        // tmpElement ï¿½ un div che va inserito in child-management-screenlet-container-... se inserisce nuova riga (table.multi-editable)
        //            ï¿½ un div che va inserito in child-management-container-... se inserisce (table.single-editable)
        if (contentToUpdate) {
            contentToUpdate = $(contentToUpdate);
            tableToUpdate = contentToUpdate.down('table.multi-editable');
            newTable = tmpElement.down('table.multi-editable');
            if(newTable && tableToUpdate) {
                var trToAdd = newTable.down('tbody').select('tr').last();
                //Re-index element's name basing on current last input's name index
                //tableToUpdate = table
                //newTr = tr da inserire
                RegisterManagementMenu._reindexNewElement(tableToUpdate, newTable);
                var lastTableRow = newTable.down('tbody').select('tr').last();

                if (TableKit.isSelectable(tableToUpdate)) {
                	tbody = tableToUpdate.down('tbody');
                    if(!tbody) {
                        tbody = new Element('tbody');
                        tableToUpdate.insert(tbody);
                    }
                    tbody.insert(lastTableRow);
                }
                
                tableToUpdate.multi_editable_insert = true;
                return tableToUpdate;
            }
        }
        return null;
    },

    postLoadElaborateContent : function(contentToUpdate, tmpElement) {
        // tmpElement ï¿½ un div se nuova riga della ricerca
        //            ï¿½ una table negli altri casi
        if (tmpElement) {
            var table = null;
            if(tmpElement.tagName === 'TABLE') {
                table = tmpElement;
                Object.extend(table, {
                    'proceed' : false
                });
            }
            else {
                if(tmpElement.down('table.basic-table')){
                    table = tmpElement.down('table.basic-table');
                    Object.extend(table, {
                        'proceed' : true
                    });
                }
            }
            if (table) {
                if (TableKit.isSelectable(table)) {
                    //TableKit.reload(contentToUpdate, false);
                    var lastTableRow = TableKit.getRow(table, TableKit.getBodyRows(table).size()-1);
                    TableKit.Selectable._toggleSelection(lastTableRow, table);
                    var index = TableKit.getRowIndex(lastTableRow);
                    if(index % 2 == 1) {
                        lastTableRow.addClassName("alternate-row");
                    }
                    var cell = lastTableRow.down('td');
                    $(cell).fire('dom:mousedown');

                    return table;

                }
            }
        }
        return tmpElement;
    }
}

document.observe("dom:loaded", RegisterMenu.load);