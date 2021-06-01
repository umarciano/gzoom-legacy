Tabs = {
    load: function(newContentToExplore, withoutResponder, contentToUpdate, mantainOldInstance, ignoreCookie, lookup) {
 //  alert(newContentToExplore.id);
        var newTabs = [];
        if (Control.Tabs) {
            var tabContainerList = !Object.isElement(newContentToExplore) ? $$('.tabs') : newContentToExplore.select('.tabs');
            if (tabContainerList && Object.isArray(tabContainerList) && tabContainerList.size() > 0) {
                if (!mantainOldInstance)
                    Control.Tabs.refreshInstances();
                tabContainerList.each(function(tabContainer) {
                	var cookiedLink = null;
                	
                	// activeTabIndex usato nel WorkEffortAchieve...
                	var activeTabIndexElement = $('activeTabIndex');
                	if (Object.isElement(activeTabIndexElement)) {
                		cookiedLink = activeTabIndexElement.getValue();
                	} 
                	
                	if (!cookiedLink) {
	                    var jar = new CookieJar({path : "/"});
	                    cookiedLink = jar.get('activeLink' + (lookup ? 'Lookup' : '') + tabContainer.identify());
                	}
                    // cookiedLink popolato dai cookie
                	var id = null;
                    var liElement = tabContainer.down('li.tab');
                    if (Object.isElement(liElement)) {
                        var anchor = liElement.down('a');
                        if (Object.isElement(anchor)) {
                            var clean_href = anchor.href.replace(window.location.href.split('#')[0],'');
                            if(clean_href.substring(0,1) == '#') {
                                id = clean_href.substring(1);
                            } else {
                                id = clean_href;
                            }
                        }
                    }
                    // id contiene id del folder da visualizzare
                    // cookiedLink contiene id del folder da vislualizzare tramite cookiedLink
                    if (id) {
                    	// se il tab prima contiene solo un folder attivo e poi ne contiene piu di 1, 
                    	// la ricerca per id, (managament_0 vs management_1) non ottiene risultati,
                    	// quindi cerchaimo anche per id del tab
                        var tabs = Control.Tabs.findByTabId(id, tabContainer.identify());
                        // se tabs e' vuota vuol dire che non ho ancora caricato l'istanza dei tab
                        // quindi la carico
                        if (!tabs) {
                            var options = Tabs['_callBack'];
                            options.lookup = lookup;
                            if (!ignoreCookie && cookiedLink) {
                            	options = Object.extend(options, {defaultTab : cookiedLink})
                            } else if (!ignoreCookie && !cookiedLink && id) {
                                options = Object.extend(options, {defaultTab : id})
                            } else if ('defaultTab' in options){
                            	delete options.defaultTab;
                            }
                            tabs = new Control.Tabs(tabContainer, options);
                            if(tabs) {
                            	// il cookiedLink potrebb essere il primo folder, che potrebbe essere nascosto, 
                            	// quindi dopo averlo caricato controllo che non sia quello nascosto...
                            	var linkByCookie = tabs.getLink(cookiedLink);
                            	if(!linkByCookie){
                            		linkByCookie = tabs.getLink(id);
                            	}
                            	if (!linkByCookie || linkByCookie.up('li').hasClassName('hidden')) {
                            		linkByCookie = 'last';
                            		Object.extend(tabs.options, {defaultTab : linkByCookie});
                            		tabs.loadTab(tabContainer, true);
                            	} else {
                            		newTabs.push(tabs);
                            	}
                            }
                            
                        } else {
                            if (!ignoreCookie && cookiedLink) {
                            	var linkByCookie = tabs.getLink(cookiedLink);
                            	if (!linkByCookie || linkByCookie.up('li').hasClassName('hidden')) {
                            		linkByCookie = 'last';
                            	}
                                Object.extend(tabs.options, {defaultTab : linkByCookie});
                            } else if (!ignoreCookie && !cookiedLink && id) {
                            	Object.extend(tabs.options, {defaultTab : id});
                            } else {
                            	Object.extend(tabs.options, {defaultTab : 'first'});
                            }
                            tabs.loadTab(tabContainer, true);
                        }

                        if (!tabs.getActiveContainer()){
                            tabs.first();
                        }
                    }
                });
            } else {
                if (Object.isElement(newContentToExplore)) {
                	var tabsCleared = false;
                    if ($$('.tabs').size() == 0) {
                        Control.Tabs.clearInstances();
                        tabsCleared = true;
                    } else {
                    	var subFolderContainerList = [newContentToExplore].concat(newContentToExplore.select('div'));
                        if (subFolderContainerList) {
                            //Control.Tabs.refreshInstances();
                            var tabsNew = [];
                            subFolderContainerList.each(function(subFolderContainer) {

                                var tabs = Control.Tabs.findByTabId(subFolderContainer.identify());
                                if (tabs) {
                                    tabs.reloadContainer(subFolderContainer.identify());
                                    tabsNew.push(tabs);
                                }
                            });
                            if(!tabsNew || !Object.isArray(tabsNew) || tabsNew.size() == 0) {
                                var otherTabs = $$('.tabs');
                                if(!otherTabs || !Object.isArray(otherTabs) || otherTabs.size() == 0) {
                                    Control.Tabs.clearInstances();
                                    tabsCleared = true;
                                }
                            }
                        }
                    }
                    
              		/*if (!tabsCleared && contentToUpdate) {
                    	var currentContentToUpdate = $(contentToUpdate);
                    		
                		if (Object.isElement(currentContentToUpdate)) {
                			var tabs = Control.Tabs.findByTabId(currentContentToUpdate.identify());
                			if (tabs) {
                				tabs.deregisterEvent(currentContentToUpdate.identify());
                			}
                		}
                    }*/
                }
            }

            if (!withoutResponder) {
                UpdateAreaResponder.Responders.register( {
                    // spostato nell'onLoad perch� in questo modo
                    // al caricamento di register-dashboard-responder in Control.Tabs i tabs sono gi� registrati
                    onLoad : function(newContent, options) {
                        if (newContent) {
                            Tabs.load(newContent, true, options.contentToUpdate);
                        }
                    }
                }, 'tabs');
            }
        }
        return newTabs;
    },

    clearInstances : function() {
        Control.Tabs.clearInstances();
    },
    clearInstance : function(position) {
        Control.Tabs.clearInstance(position);
    },
    _callBack : {
        setClassOnContainer : true,
        _loadFromAjax : false,
        lookup : false,
//        _reloadMenu : false,
        afterChange: function(new_container, init) {
            // console.log("afterChange : new_container " + new_container);
            // console.log("afterChange : init " + init);
            //Memorizzo nel cookie il tab attivo
            var jar = new CookieJar({path : "/"});
            var link = jar.get('activeLink' + (this.options.lookup ? 'Lookup' : '') + this.getId());
            if (link) {
                jar.remove('activeLink' + (this.options.lookup ? 'Lookup' : '') + this.getId())
            }
            jar.put('activeLink' + (this.options.lookup ? 'Lookup' : '') + this.getId(), new_container.identify());

            this.dispatchEvent(new_container.identify(), null, new_container.identify(), Tabs._callBack._loadFromAjax);
            Tabs._callBack._loadFromAjax = false;
        },
        // se activeContainer non e' un element e init = true stiamo nel primo folder
        // che puo essere il simple_search, in caso di ricerca oppure
        // management_0 oppure
        // management_0 del context_link ( TODO qui si dovrebbe fare il controllo)
        // quindi non si fa nessun controllo per form modificata
        // in questo metodo arrivo pero' 2 volte 
        // perche' da tabs.setActiveTab() se res (valore di ritorno) e' true allora va avanti
        // se res e' false, si ritorna nel metodo beforeChange 
        // ma stavolta con un UpdateAreaResponder.Responders.indexOf(Tabs._callBack._selectContainerResponder) valorizzato e quindi res impostato con true
        beforeChange: function(activeContainer, container, init) {
            container = $(container.identify());
            if (Object.isElement(activeContainer)) {
                // console.log("beforeChange : activeContainer " + activeContainer.id);
            } else {
                // console.log("beforeChange : activeContainer null", );
            }
            // console.log("beforeChange : container " + container.id);
            var res = init;
            // console.log("beforeChange : init res " + res);
            // console.log("beforeChange : init init " + init);
            if (Object.isElement(activeContainer)) {
                // TODO che succede se le form da modificare sono piu di 1?
                // e se quella da modificare non e' l'ultima? perche' in quel caso l'ultimo resultcheckModification sara false
                var resultcheckModification = false;
                // check modification only the first time
                if (UpdateAreaResponder.Responders.indexOf(Tabs._callBack._selectContainerResponder) == -1) {
                    // console.log("beforeChange : Controllo le modifche solo in questo caso");
                    var cachableForms = $(activeContainer).descendants().findAll(function(element){
                        return FormKit.isCachable(element);
                    });
                    for (i = 0; i < cachableForms.length; i++) {
                        cachableForm = cachableForms[i];
                        // console.log("beforeChange : cachableForms " + cachableForm.id);
                        // console.log("beforeChange : init resultcheckModification " + resultcheckModification);
                        resultcheckModification = FormKitExtension.checkModficationWithAlert(cachableForms[i]);
                        // console.log("beforeChange : FormKitExtension.checkModficationWithAlert(" + cachableForm.id + ") = " + resultcheckModification);
                        // resultcheckModification = false if there is not modification 
                        // or if user click on "Cancel"
                        // else resultcheckModification = object with modifcation
                        
                    }
                }
                // console.log("beforeChange : adesso resultcheckModification " + resultcheckModification);
                if (!resultcheckModification) {
                    // se modifico form principale qui non DEVE entrare
                    // console.log("beforeChange : entra dentro la gestione del UpdateAreaResponder.Responders");
                    var activeContainerIndex = this.getContainerIndex(activeContainer.identify())
                    var containerIndex = this.getContainerIndex(container.identify())
                    var nextLink = this.getLink(container.identify());
                    // console.log("beforeChange : Tabs._callBack._selectContainerResponder ", (Tabs._callBack._selectContainerResponder));
                    // console.log("beforeChange : UpdateAreaResponder.Responders " + UpdateAreaResponder.Responders);
                    if (UpdateAreaResponder.Responders.indexOf(Tabs._callBack._selectContainerResponder) == -1) {
                        // console.log("beforeChange : containerIndex " + containerIndex);
                        if (containerIndex != 0) {
                        	if (activeContainerIndex !== 0 && !nextLink.readAttribute('onclick')) {
                        		activeContainer = this.getContainerAtIndex(0);
                        	}
                            res = Tabs._callBack._elaborateSelectedRow(activeContainer, container);
                            // console.log("beforeChange : res " + res);
                    	} else {
                    	    // console.log("beforeChange : else res = true");
                            res = true;
                    	}
                    } else {
                        // Unregister _selectContainerResponder at the end of _selectContainerResponder.onAfterLoad()
                        // otherwise concurrent calls to beforeChange will trigger an infinite loop (as in double click).
                        // console.log("beforeChange : else res = true (Unregister _selectContainerResponder)");
                        res = true;
                    }
                    // console.log("beforeChange : res " + res);
                } else {
                    // console.log("beforeChange : forzo il false altrimenti apre il tab ma senza request verso il server");
            		// se non metto false apre il tab ma senza request verso il server
                    res = false;
                }
            } else if (init) {
                // console.log("beforeChange : siamo nell'init true");
            	var firstContainer = this.getContainerAtIndex(0);
            	// console.log("beforeChange : firstContainer ", firstContainer);
	            if (Object.isElement(firstContainer)) {
	            	var selectableTable = firstContainer.down('table.selectable');
	            	if (Object.isElement(selectableTable) && TableKit.isSelectable(selectableTable)) {
	            		var selectedRow = TableKit.Selectable.getSelectedRows(selectableTable)[0];
	            		if (Object.isElement(selectedRow)) {
	            			var nextLink = this.getLink(container.identify());
	            			Object.extend(nextLink, {'rowIndex' : TableKit.getRowIndex(selectedRow)});
	            		}
	            	}
            	}
            }
            // console.log("beforeChange : return res " + res);
            return res;
        },
        _getActiveContainerIndex : function(new_container) {

            var currentInstance = Control.Tabs.findByTabId(new_container.identify())
            if (currentInstance)
                return currentInstance.getActiveContainerIndex();
            return -1;
        },
        _getElementToFocus : function(selectedRows, tBody) {
            var elementToFocus = null;
            if (selectedRows && selectedRows.size() > 0) {
                var firstSelectedRow = $(selectedRows[0]);
                var scrollPosition = firstSelectedRow.cumulativeScrollOffset();
                if (scrollPosition) {
                    this._setScrollXY(scrollPosition.left, scrollPosition.top, tBody);
                }
                elementToFocus = this._findFirstElement(firstSelectedRow, ['input']);
            }
            return elementToFocus;
        },
        _findFirstElement: function(container, typeElement) {
            typeElement = typeElement || ['input', 'select', 'textarea'];
            var elements = $(container).descendants().findAll(function(element) {
              return 'hidden' != element.type && !element.disabled;
            });
            var firstByIndex = elements.findAll(function(element) {
              return element.hasAttribute('tabIndex') && element.tabIndex >= 0;
            }).sortBy(function(element) { return element.tabIndex }).first();

            return firstByIndex ? firstByIndex : elements.find(function(element) {
              return typeElement.include(element.tagName.toLowerCase());
            });
        },
        _setScrollXY : function(x, y, content) {
            x = x || 0;
            y = y || 0;

             if (!Prototype.Browser.IE) {
                if (!content)
                    content = document.body;

                // DOM
                content.scrollLeft = x;
                content.scrollTop = y;
            } else  {
                if (!content)
                    content = document.documentElement;

                // IE6 standards compliant mode
                content.scrollLeft = x;
                content.scrollTop = y;
            }
        },
        _elaborateSelectedRow : function(container, nextContainer) {
            var res = true;
            if (Tabs.CheckSelectionOnTabRules.dispatch(container)) {
                var tab = Control.Tabs.findByTabId(nextContainer.identify());
                if(!tab) {
                	var tab = Control.Tabs.findByTabId(container.identify());
                }
                if (tab) {
                    var nextLink = tab.getLink(nextContainer.identify());
                    if (nextLink.readAttribute('onclick')) {
                        if ((!('justLoaded' in nextLink)) || !nextLink['justLoaded']) {
                            var event = nextLink.readAttribute('onclick');

                            this._selectContainerResponder._tab = tab;
                            this._selectContainerResponder._nextContainer = nextContainer;
                            UpdateAreaResponder.Responders.register(this._selectContainerResponder, 'selectContainer', 0);

                            eval('var evalFuncTmp = function() { ' + event + '; }');
                            evalFuncTmp();

                            res = false;
                        }
                        Object.extend(nextLink, {'justLoaded' : true});
                    } else {
                    	var pkList = [];
                    	var entityPkFieldsElement = null;
                    	var subFolderExtraParamFieldsElement = null;
                    	var forcedBackAreaIdFieldElement = null;
                    	var variableContainer = null;
                    	var rowIndex = -1;
                    	
                    	var table = container.down('table.single-editable');
                		if (Object.isElement(table)) {
                			var currentForm = table.up('form');
                			if (Object.isElement(currentForm)) {
                				entityPkFieldsElement = currentForm.down('input[name=\"entityPkFields\"]');
                				subFolderExtraParamFieldsElement = currentForm.down('input[name=\"subFolderExtraParamFields\"]');
                				forcedBackAreaIdFieldElement = currentForm.down('input[name=\"forcedBackAreaId\"]');
                				variableContainer = currentForm;
                			}
                		} else {
	                    	table = container.down('table.selectable');
	                    	if (Object.isElement(table) && TableKit.isSelectable(table)) {
	                    		var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
	                            if (selectedRow) {
	                            	rowIndex = TableKit.getRowIndex(selectedRow);
	                            	entityPkFieldsElement = selectedRow.down('input[name=\"entityPkFields_o_' + rowIndex + '\"]');
	                            	if (!Object.isElement(entityPkFieldsElement)) {
	                            		entityPkFieldsElement = selectedRow.down('input[name=\"entityPkFields\"]');
	                            	}
	                            	subFolderExtraParamFieldsElement = selectedRow.down('input[name=\"subFolderExtraParamFields_o_' + rowIndex + '\"]');
	                            	if (!Object.isElement(subFolderExtraParamFieldsElement)) {
	                            		subFolderExtraParamFieldsElement = selectedRow.down('input[name=\"subFolderExtraParamFields\"]');
	                            	}
	                            	forcedBackAreaIdFieldElement = selectedRow.down('input[name=\"forcedBackAreaId_o_' + rowIndex + '\"]');
                                    if (!Object.isElement(forcedBackAreaIdFieldElement)) {
                                        forcedBackAreaIdFieldElement = selectedRow.down('input[name=\"forcedBackAreaId\"]');
                                    }
	                            	variableContainer = selectedRow;
	                            }
	                    	} 	
                    	}
                    	
                    	var nextContainerChildElementSize = nextContainer.childElements().size();
                    	// sandro : bug 4144 - riattivo il caricamento delle tab sempre
                    	nextContainerChildElementSize = 0;
                    	rowIndex = -1;
                    	// end - bug 4144                    	
                    	
                    	if ((rowIndex == -1 && nextContainerChildElementSize == 0) || (!('rowIndex' in nextLink) && rowIndex >= 0) || (('rowIndex' in nextLink) && rowIndex !== nextLink['rowIndex'])) {
	                    	if (Object.isElement(variableContainer) && Object.isElement(entityPkFieldsElement)) {
	                    		var entityPkFields = entityPkFieldsElement.getValue();
	                    		if (entityPkFields) {
	                    			pkList = $A(entityPkFields.split('|'));
	                    			
	                    			if (pkList.size() > 0) {
		                    			//Se ho i campi cihiave allora parto
	                    				var inputFields = variableContainer.select('input, select');
	                    				
		                    			var insertMode = inputFields.find(function(element) {
		                                    return (/^insertMode/).exec(element.readAttribute('name'));
		                                });
		                    			
		                    			if (!Object.isElement(insertMode) || 'N' === insertMode.readAttribute('value')) {
		                    				var subFolderLinkElement = container.down('a.subFolderLinkUrl');
		                    				if (Object.isElement(subFolderLinkElement)) {
		                    					var formId = table.identify() + "_virtual_form"
		                    					
		                    					var pkElementList = inputFields.select(function(element) {
		                    						var elementName = this._cleanIndex(element.readAttribute('name'));
		                                            return pkList.indexOf(elementName) != -1 || elementName === 'operationalEntityName' || elementName === 'operation';
		                                        }.bind(this));
		                    					
		                    					if (Object.isElement(insertMode))
		                    						pkElementList.push(insertMode);
		                    					
		                    					if (Object.isElement(subFolderExtraParamFieldsElement)) {
		                    						var subFolderExtraParamFields = subFolderExtraParamFieldsElement.getValue();
		                    						var extraParamList = $A(subFolderExtraParamFields.split('|'));
		                    						if (extraParamList.size() > 0) {
		                    							var extraParamElementList = inputFields.select(function(element) {
				                    						var elementName = this._cleanIndex(element.readAttribute('name'));
				                                            return extraParamList.indexOf(elementName) != -1;
				                                        }.bind(this));
		                    							
		                    							if (extraParamElementList.size() > 0) {
		                    								extraParamElementList.each(function(element) {
		                    									pkElementList.push(element);
		                    								});
		                    							}
		                    						}
		                    					}
		                    					if (Object.isElement(forcedBackAreaIdFieldElement)) {
		                    					    pkElementList.push(forcedBackAreaIdFieldElement);
		                    					}

		                    					
		                    					var form = this._createForm(subFolderLinkElement.readAttribute('href'), pkElementList, Object.extend({'id' : formId, 'name' : formId, 'target' : target}));
	                                            var saveViewElement = form.down('#saveView');
	                                            if (Object.isElement(saveViewElement)) {
	                                            	saveViewElement.setValue('N');
	                                            } else {
	                                               saveViewElement = new Element('input', {'type' : 'hidden', 'value' : 'N', 'name' : 'saveView'});
	                                               form.insert(saveViewElement);
	                                            }
	                                            var folderIndex = tab.getContainerIndex(nextContainer.identify());
	                                            form.insert(new Element('input', {'type' : 'hidden', 'value' : folderIndex, 'name' : 'folderIndex'}));
	                                            form.insert(new Element('input', {'type' : 'hidden', 'value' : folderIndex, 'name' : 'itemIndex'}));
	                                            
	                                            var operationalEntityNameField = form.down('input[name="operationalEntityName"]');
	                                            if (Object.isElement(operationalEntityNameField))
	                                            	form.insert(new Element('input', {'type' : 'hidden', 'value' : operationalEntityNameField.getValue(), 'name' : 'parentEntityName'}));
	                                            document.body.insert(form);
	                                            
	                                            var link = tab.getLink(nextContainer.identify());
	                                            var links = [link];
	
	                                            this._selectContainerResponder._tab = tab;
	                                            this._selectContainerResponder._nextContainer = nextContainer;
	                                            UpdateAreaResponder.Responders.register(this._selectContainerResponder, 'selectContainer', 0);
	
	                                            new Ajax.Request($(form).action, {parameters : $(form).serialize(true), evalScripts : true, contentToUpdate : nextContainer.identify(), content : $A(links), 'postLoadElaborateContent' : this._postLoadElaborateContent});
	
	                                            form.remove();
	                                            
	                                            Object.extend(nextLink, {'rowIndex' : rowIndex});
	                                            
	                                            res = false;
		                    				}
		                    			}
	                    			}
	                    		}
	                    	}
                    	}
                    }
                }
            }

            return res;
        },
        _postLoadElaborateContent : function(contentToUpdate, newTmpElement) {
			var res = newTmpElement; 
			if (newTmpElement.hasClassName('data-management-container')) {
				var childElements = newTmpElement.childElements(); 
				var parent = newTmpElement.up();
				childElements.each(function(element) {
					element = element.remove(); 
					parent.insert(element);
				});
				 
				newTmpElement.remove(); 
				res = parent; 
			} 
			res.proceed = true; 
			return res;
		},
        /**
         * Clean field name of index. If there isn't index return name
         */
        _cleanIndex: function(attr) {
            var newName = attr.replace(/(_o_[0-9]+)/, "");
            return newName;
        },
        _selectContainerResponder : {
            _tab : null,
            _nextContainer : null,
            onAfterLoad : function(newContent) {
                try {
                    if (this._tab) {
                    	Tabs._callBack._loadFromAjax = true;
                        this._tab.setActiveTab(this._nextContainer.identify());
                    }
                } finally {
                    // always unregister in the end
                    UpdateAreaResponder.Responders.unregister(this, 'selectContainer');
                }
            }
        },
        _createForm : function(actionUrl, elementCollection, option) {
            option = Object.extend(option || {}, {'class' : 'basic-form', 'method' : 'POST'});
            var form = new Element('form', option);
            this._initFormFromAction(form, actionUrl, null, elementCollection);
            return form;
        },
        _initFormFromAction : function(form, actionUrl, actionTarget, elementCollection) {
            if (form) {
                form.writeAttribute("action", (actionUrl.indexOf('?') != -1 ? actionUrl.substring(0, actionUrl.indexOf('?')) : actionUrl));
                if (actionTarget)
                    form.writeAttribute("target", actionTarget);
                var queryParams = $H({}).merge(actionUrl.toQueryParams());
                if (queryParams) {
                    $A(queryParams.each(function(pair) {
                        var findElement = $A(form.getElements()).find(function(elm) {
                            return pair.key === elm.readAttribute("name");
                        });

                        if (!findElement) {
                            var inputHidden = this._setHiddenFormField(form, pair.key, pair.value);
                            inputHidden.addClassName('url-params');
                        }
                    }.bind(this)));
                }

                this._populateForm(form, elementCollection);
            }
        },
        _populateForm : function(form, elementCollection) {
            $A(elementCollection).each(function(element) {
                var elementName = element.readAttribute("name");
                if(elementName.indexOf('_') > -1) {
                    elementName = elementName.substring(0,elementName.indexOf('_'));
                }
                var currentValue = element.getValue();
                if (currentValue) {
                    var findElement = $A(form.getElements()).find(function(elm) {
                        return elementName === elm.readAttribute("name");
                    });

                    if (findElement && !findElement.hasClassName('url-params')) {
                        var valueHidden = findElement.readAttribute('value');
                        if (valueHidden) {
                            if (valueHidden.indexOf(']') != -1) {
                                valueHidden = valueHidden.replace(']', '|]');
                            } else {
                                valueHidden = valueHidden.concat('|]');
                            }
                        }
                        valueHidden = valueHidden.replace(']', currentValue+ ']');
                        if (valueHidden.indexOf('[') == -1)
                            valueHidden = '['.concat(valueHidden);
                        findElement.writeAttribute('value', valueHidden);
                    } else if (!findElement || (findElement && !findElement.hasClassName('url-params'))) {
                        var inputHidden = new Element('input', {'type' : 'hidden', 'value' : currentValue, 'name' : elementName, 'id' : elementName});
                        form.insert(inputHidden);
                    }
                }
            });
        },
        _setHiddenFormField : function(form, fieldName, fieldValue) {
            if (form) {
                var field = form.getInputs('hidden', fieldName).first();
                if (field) {
                    field.writeAttribute('value', fieldValue);
                } else {
                    field = new Element('input', { 'type': 'hidden', 'name': fieldName, 'value': fieldValue });
                    form.insert(field);
                }
            }
            return field;
        }
    }


};

Tabs.CheckSelectionOnTabRules = {
    selectionOnTabRules: $H({}),

    _each: function(iterator) {
        this.selectionOnTabRules._each(iterator);
    },

    register: function(container, rule) {
        var rules = this.selectionOnTabRules.get(container.identify());
        if (!rules) {
            rules = [];
        }
        rules.push(rule);
        this.selectionOnTabRules.set(container.identify(), rules);
    },

    unregister: function(container, rule) {
        var rules = this.selectionOnTabRules.get(container.identify());
        if (rules) {
            rules = rules.without(rule);
            if (rules.size() == 0) {
                this.selectionOnTabRules.unset(container.identify());
                return;
            }
            this.selectionOnTabRules.set(container.identify(), rules);
        }
    },

    dispatch: function(container) {
        var returnValue = true;
        var rules = this.selectionOnTabRules.get(container.identify());
        if (rules && rules.size() > 0) {
            rules.each(function(rule) {
                if (Object.isFunction(rule)) {
                    try {
                        returnValue = returnValue && rule.apply(rule, [container]);
                    } catch (e) { }
                }
            });
        }

        return returnValue;
    }

};
Object.extend(Tabs.CheckSelectionOnTabRules, Hash);

document.observe("dom:loaded", Tabs.load);