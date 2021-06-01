
var Toolbar = Class.create({
    initialize : function(items, options, contentToExplore, content) {
        if (!Toolbar.instance)
            Toolbar.instance = [this];
        else
            Toolbar.instance.push(this);

        this.items = new Array();
        if (Object.isArray(items)) {
            $A(items).each(function(item) {
                var tmpItem = Object.clone(this._baseItem);
                this.items.push(Object.extend(tmpItem, Object.clone(item) || {}));
            }.bind(this));
        }

        if (Object.isElement(content))
            this.content = content.identify();
        else if (Object.isString(content)) {
            this.content = content;
        }

        Object.extend(this.options || {}, options || {});
        if (this.options.autoLoad)
            this.loadItems(contentToExplore, null, this.options);
    },
    _baseItem : {
        selector : '',
           onClickStart : Prototype.K,
           onClick : Prototype.K,
           onClickEnd : Prototype.K,
           messageCallBack : Prototype.K,
           populateElementCollection : Prototype.K,
           getActionButton : Prototype.K,
           elaborateForm : Prototype.K,
           checkExecutability : Prototype.K,
           replaceAnchor : true,
           useForm : 'new-form',
           withOnSubmitForm : true,
           onSubmit : Prototype.K,
           onAfterSubmit : Prototype.K,
           currentItem : false,
           filterFormElement: function(element) {
               return element || true;
           },
           onClickDisableToolbar : Prototype.K, // override function for disable all item of toolbar
           oldClassName : false, // className of list before disable Toolbar
           oldAnchorId : false, // id of anchor before disable Toolbar
           oldAnchorElement : false, // anchor before disable Toolbar
           form : {
               class : 'basic-form',
               method : 'POST'
           }
    },
    options : {
        itemSelector : 'li',
        autoLoad : true
    },
    items : [],
    loadItems : function(contentToExplore, itemListContainer, options) {
        Object.extend(this.options || {}, options || {});

        var elementList = (Object.isElement(itemListContainer) ? itemListContainer : (Object.isElement(contentToExplore) ? contentToExplore : $(document.body))).select(this.options.itemSelector);

        $A(elementList).each(function(element) {
            this.registerItem(element, contentToExplore);
         }.bind(this));

        if (contentToExplore) {
            $A(this.items).each(function(item) {
                $A(contentToExplore.select(item.selector+'-button')).each(function(element){
                    if (element.up('tr'))
                        element = element.up('tr');
                    element.hide();
                }.bind(this));
            }.bind(this));

        }
    },
    _getItemBySelector : function(selector) {
        if (!this.items || this.items.size() > 0) {
            return $A(this.items).find(function(item) {
                return item.selector === selector;
            });
        }
        return null;
    },
    insertItem : function(item) {
        if (!this.items)
            this.items = new Array();
        var justExistItem = this._getItemBySelector(item.selector);
        if (justExistItem)
            this.items = this.items.without(justExistItem);
        var tmpItem = Object.clone(this._baseItem);
        this.items.push(Object.extend(tmpItem, item || {}));

    },
    removeItem : function(selector) {
        var item = null;
        if (this.hasItems() && selector) {
            if (this.items.indexOf(selector) != -1) {
                item = selector;
            } else {
                item = this._getItemBySelector(selector);
            }
        }
        if (item) {
            this.items = this.items.without(item);
            this._clearEvent(item.currentItem);

            return $(item.currentItem);
        }
        return null;
    },
    getItem : function(selector) {
        var item = this._getItemBySelector(selector);
        if (item) {
            return $(item.currentItem);
        }
        return null;
    },
    hasItems : function() {
        return this.items && this.items.size() > 0;
    },
    /* aggiunta funzione updateItem all'istanza di toolbar che permette di sovrascrivere funzioni per un item già esistente */
    updateItem : function(selector, newItem) {
        var justExistItem = this._getItemBySelector(selector);
        var tmpItem = Object.clone(justExistItem || this._baseItem);
        this.items = this.items.without(justExistItem);
        this._clearEvent(justExistItem.currentItem);
        this.items.push(Object.extend(tmpItem, newItem || {}));
        return this.getItem(selector);
    },
    registerItem : function(item, contentToExplore) {
        if ('currentItem' in item) {
            item = item.currentItem;
        }

        var itemClassName = '';
        if (Object.isElement(item)) {
            if ('justRegistered' in item && item.justRegistered) {
                item.justRegistered = false;
            }

            itemClassName = item.className;
            if ("A" !== item.tagName)
                item = item.down('a');
        }

        if (Object.isElement(item)) {
            Object.extend(item, {'toolbar_instance' : this});

            var itemHref = item.readAttribute('href');
            if (!itemHref || '' === itemHref) {
                itemHref = '#';
                item.writeAttribute('href', itemHref);
            }
            var itemOnClick = item.readAttribute('onclick');

            $A($w(itemClassName)).each(function(className) {

                var registeredItem = this._getItemBySelector('.' + className);

                if (registeredItem) {
                    registeredItem.currentItem = item.identify();

                    if (Object.isFunction(registeredItem.onClick) && registeredItem.onClick !== Prototype.K) {
                        //Se sono stati registrati dei selettori allora verifico che per quell'item sia stata indicata una funzione di callback per
                        //l'evento click
                        item.removeAttribute('onclick');
                        // per non registrare 2 volte l'evento di openclose
                        // ma registrare il reset anche se si carica il subfolder
                        if (!('justRegistered' in item) || ('justRegistered' in item && !item['justRegistered'])) {
                            this._observeEvent(item, registeredItem.onClick.bind(registeredItem))
                            Object.extend(item, {justRegistered : true});
                        }
                        throw $break;
                    }
                    var currentSelector = (registeredItem && registeredItem.selector) ? registeredItem.selector : '.' + className;

                    var element = (!Object.isElement(contentToExplore) ? $(document.body).down(currentSelector + '-button') : contentToExplore.down(currentSelector + '-button'));
                    var elementList = [];

                    if (Object.isElement(element)) {
                        elementList.push(element);
                    }
                    elementList.each(function(element) {
                        if (!itemOnClick && (!itemHref || '#'=== itemHref)) {
                            if ("A" === element.tagName) {
                                if (registeredItem.replaceAnchor) {
                                    if (!(Object.isFunction(registeredItem.getActionButton) && registeredItem.getActionButton !== Prototype.K)) {
                                        var buttonHref = element.readAttribute('href');
                                        var buttonTarget = element.readAttribute('target');
                                        var buttonOnClick = element.readAttribute('onclick');

                                        item.writeAttribute('href', buttonHref);
                                        item.writeAttribute('target', buttonTarget);

                                        if (buttonOnClick && buttonOnClick !== 'null')
                                        	Object.extend(item, {onclickFunction : buttonOnClick});

                                        if (registeredItem.useForm == 'current' && element.up('form')) {
                                            Object.extend(item, {form : $(element.up('form'))});
                                        }
                                    }

                                    if (!('justRegistered' in item) || ('justRegistered' in item && !item['justRegistered'])) {
                                        this._observeEvent(item, this._observeItemEvent.bind(this, registeredItem));
                                        Object.extend(item, {justRegistered : true});
                                    }

                                }
                            } else if ("INPUT" === element.tagName && "submit" === element.readAttribute('type')) {
                                // nel secondo folder il save è un input già registrato ma deve essere ripulito
                                //perchè la form a cui è associato è cambiata
                                this._clearEvent(item);
                                if (!(Object.isFunction(registeredItem.getActionButton) && registeredItem.getActionButton !== Prototype.K)) {
                                    this._observeEvent(item, this._submitForm.bind(this, element.up('form'), registeredItem, false, false));
                                }
                                else {
                                    this._observeEvent(item, this._submitForm.bind(this, null, registeredItem, false, false));
                                }
                                Object.extend(item, {justRegistered : true});
                            }
                        }
                        if (element.up('tr'))
                            element = element.up('tr');
                        element.hide();
                    }.bind(this));
                }
            }.bind(this));
        }
    },
    _isActiveInWindow : function(element) {
        if (Object.isElement(element)) {
            element = $(element);
            var firstAncestorHidden = $A(element.ancestors()).find(function(anchestor) {
                return anchestor.getStyle('display') && anchestor.getStyle('display') === 'none';
            });
            return !(firstAncestorHidden && Object.isElement(firstAncestorHidden));
        } else {
            return false;
        }
        return true;
    },
    _observeEvent : function(item, callback) {
        if (item && Object.isFunction(callback)) {
            item.eventCallbackFunction = callback;

            Event.observe(item, 'click', callback);
            Event.observe(item, 'dom:click', callback);
        }
    },
    _clearEvent : function(item) {
        if (item) {
            Event.stopObserving(item, 'click');
            Event.stopObserving(item, 'dom:click');
            item.eventCallbackFunction = null;
        }
    },
    _evalFunc : function(str) {
        eval('var evalFuncTmp = function() { ' + str + '; }');
        return evalFuncTmp();
    },

    _evalBool : function(str) {
        return (this._evalFunc(str) != false);
    },
    _submitForm : function(form, registeredItem, isJustMessageCallback, isJustChecked, e) {
        var element = $(Event.element(e));

        if (Object.isElement(element) && "A" === element.tagName) {
            if (!form && registeredItem && (Object.isFunction(registeredItem.getActionButton) && registeredItem.getActionButton !== Prototype.K)) {
                var button = registeredItem.getActionButton(element, registeredItem.selector);
                if (button !== element) {
                    form = button.up('form');
                }
            }
        }

        if (!isJustChecked && registeredItem.checkExecutability && registeredItem.checkExecutability !== Prototype.K) {
            registeredItem.checkExecutability(this._executeSubmitForm.bind(this, element, form, registeredItem, true, true, e), form);
        } else if (!isJustMessageCallback && registeredItem.messageCallBack && registeredItem.messageCallBack !== Prototype.K) {
            registeredItem.messageCallBack(this._executeSubmitForm.bind(this, element, form, registeredItem, true, isJustChecked, e), element, form);
        } else {
            return this._executeSubmitForm(element, form, registeredItem, isJustMessageCallback, isJustChecked, e);
        }

        return true;
    },
    _executeSubmitForm : function(element, form, registeredItem, isJustMessageCallback, isJustChecked, e) {
        if (!isJustChecked && registeredItem.checkExecutability && registeredItem.checkExecutability !== Prototype.K) {
            registeredItem.checkExecutability(this._executeSubmitForm.bind(this, element, form, registeredItem, isJustMessageCallback, true, e), form);
        } else if (!isJustMessageCallback && registeredItem.messageCallBack && registeredItem.messageCallBack !== Prototype.K) {
            registeredItem.messageCallBack(this._executeSubmitForm.bind(this, element, form, registeredItem, true, isJustChecked, e), element);
        } else {
            form = $(form);
            if (registeredItem.useForm==='new-form' && (!('newForm' in form) || (('newForm' in form) && !form.newForm))) {
                var formId = form.identify() + '_clone_' + new Date().getTime();
                form = this._createForm(form.readAttribute('action'), form.readAttribute('target'), registeredItem.populateElementCollection, Object.extend({'id' : formId, 'name' : formId, 'target' : target, 'onsubmit' : form.readAttribute('onsubmit')}, registeredItem.form || {}));
                document.body.insert(form);
            }
            if (form && registeredItem && (Object.isFunction(registeredItem.elaborateForm) && registeredItem.elaborateForm !== Prototype.K)) {
                form = registeredItem.elaborateForm(form);
            }

            if (form) {
            	if(registeredItem.onClickDisableToolbar && registeredItem.onClickDisableToolbar !== Prototype.K){
            		this.saveToolbar();
            		registeredItem.onClickDisableToolbar();
            	}
            	
            	var action = form.readAttribute('onsubmit');
                if (Object.isFunction(registeredItem.onSubmit) && registeredItem.onSubmit !== Prototype.K && registeredItem.useForm === "new-form")
                    action = registeredItem.onSubmit(action, form);
                if (action && !this._evalBool(action)) {
                    if (('newForm' in form) && form.newForm) {
                        form.removeAttribute('onsubmit');
                    }
                    registeredItem.onAfterSubmit(form);

                    if (('newForm' in form) && form.newForm) {
                        form.remove();
                    }

                    return false;
                }
                form.submit();

                registeredItem.onAfterSubmit(form);

                if (('newForm' in form) && form.newForm) {
                	Event.stopObserving(form);
                    form.remove();
                }
            }
        }
    },
    _observeItemEvent : function(registeredItem, e) {
        var element = $(Event.element(e));

        Event.stop(e);

        if (registeredItem.checkExecutability && registeredItem.checkExecutability !== Prototype.K){
            registeredItem.checkExecutability(this._executeObserveItemEvent.bind(this, element, registeredItem, false, true, e), this.content);
        } else if (registeredItem.messageCallBack && registeredItem.messageCallBack !== Prototype.K) {
            registeredItem.messageCallBack(this._executeObserveItemEvent.bind(this, element, registeredItem, true, true, e), element);
        } else {
            this._executeObserveItemEvent(element, registeredItem, false, true, e);
        }
    },
    _executeObserveItemEvent : function(element, registeredItem, isJustMessageCallback, isJustChecked, e) {
        if (!isJustChecked && registeredItem.checkExecutability && registeredItem.checkExecutability !== Prototype.K) {
            registeredItem.checkExecutability(this._executeObserveItemEvent.bind(this, element, registeredItem, isJustMessageCallback, true, e), form);
        } else if (!isJustMessageCallback && registeredItem.messageCallBack && registeredItem.messageCallBack !== Prototype.K) {
            registeredItem.messageCallBack(this._executeObserveItemEvent.bind(this, element, registeredItem, true, isJustChecked, e), element);
        } else {
            if (Object.isElement(element) && "A" === element.tagName) {
                var url = null;
                var target = null;
                if (Object.isFunction(registeredItem.getActionButton) && registeredItem.getActionButton !== Prototype.K) {
                    var button = registeredItem.getActionButton(element,registeredItem.selector);
                    if (button !== element) {
                        url = button.readAttribute('href');
                        target = button.readAttribute('target');
                        var buttonOnClick = button.readAttribute('onclick');

                        if (buttonOnClick)
                            Object.extend(element, {onclickFunction : buttonOnClick});

                        if (registeredItem.useForm == 'current' && button.up('form')) {
                            Object.extend(element, {form : $(button.up('form'))});
                        }
                    }
                } else {
                    url = element.readAttribute('href');
                    target = element.readAttribute('target');
                }

                if (registeredItem.useForm) {
                    Event.stop(e);

                    var formId = element.identify() + '_form';
                    var form = this._createForm(url, target, registeredItem.populateElementCollection.bind(registeredItem), Object.extend({'id' : formId, 'name' : formId, 'target' : target}, registeredItem.form || {}));

                    if ('onclickFunction' in element && element['onclickFunction']) {
                        form.writeAttribute('onsubmit', registeredItem.onSubmit(element['onclickFunction'], form));
                    }

                    document.body.insert(form);

                    if ("current" == registeredItem.useForm) {
                        var currentForm = $(element['form']);

                        if (currentForm) {
                            var elements = null;

                            if (Object.isFunction(registeredItem.filterFormElement)) {
                                var filteredElementList = $A(currentForm.getElements()).select(registeredItem.filterFormElement);
                                if (filteredElementList && filteredElementList.size() > 0)
                                    elements = Form.serializeElements(filteredElementList);
                            }

                            this._populateFormFromString(form, elements/*, registeredItem.filterFormElement*/);
                        }
                    }

                    this._submitForm(form,registeredItem,isJustMessageCallback,isJustChecked,e);

                    if (Object.isElement($(form.identify())))
                        form.remove();
                } else {
                    if ('onclickFunction' in element) {
                        if (!this._evalBool(element['onclickFunction'])) {
                            Event.stop(e);
                            return false;
                        }
                    }
                }
            }
        }
    },
    _createForm : function(url, target, populateElementCollection, options) {
        options = Object.extend(options || {});
        var form = new Element('form', options);
        Object.extend(form, {'newForm': true});

        return this._initFormFromAction(form, url, target, populateElementCollection);
    },
    _initFormFromAction : function(form, url, target, populateElementCollection) {
        if (form) {
            var action;
            var queryString;
            var idxParams = url.indexOf('?');
            if (idxParams >= 0) {
                action = url.substring(0, idxParams);
                queryString = url.substring(idxParams + 1);
            } else {
                action = url;
                queryString = '';
            }
            if (target) {
                form.writeAttribute("target", target);
                if (target === '_blank') {
                	if (action) {
                		if (action.indexOf('?') != -1) {
                			action += '&';
                		} else {
                			action += '?';
                		}
                		action += 'dummy=' + new Date().getTime();
                	}
                }
            }
            
            
            form.writeAttribute("action", action);
            this._populateFormFromString(form, queryString);

            if (Object.isFunction(populateElementCollection))
                populateElementCollection(form, this.content);
        }

        return form;
    },
    _populateFormFromString : function(form, str) {
        if (str && Object.isString(str)) {
            var queryParams = $H({}).merge(str.toQueryParams());
            if (queryParams) {
                $A(queryParams.each(function(pair) {
                    var findElement = $A(form.getElements()).find(function(elm) {
                        return pair.key === elm.readAttribute("name");
                    });

                    if (!findElement) {
                        var inputHidden = this._setHiddenFormField(form, pair.key, pair.value);
                        inputHidden.addClassName('url-params');
                    }
                    else {
                        findElement.writeAttribute('value', pair.value);
                    }
                }.bind(this)));
            }
        }
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
    },
    cloneInstance : function() {
        var newInstance = Object.clone(this, true);

        Toolbar.instance.push(newInstance);

        return newInstance;
    },
    clearInstance : function() {
        this.items.each(function(item) {
            var currentItem = $(item.currentItem);
            if (Object.isElement(currentItem)) {
                this._clearEvent(currentItem);
                if ('justRegistered' in currentItem && currentItem['justRegistered']) {
                    currentItem['justRegistered'] = false;
                }
                var itemHref = currentItem.readAttribute('href');
                if (!itemHref || '#' !== itemHref) {
                    currentItem.writeAttribute('href', '#');
                }
            }

        }.bind(this));
    },
    content : false,
    
    /** save information for enable item of toolbar*/
    saveToolbar: function() {
    	var items = Toolbar.getInstance().items;
    	items.each(function(item, index) {
        	var currentItem = $(item.currentItem);
        	if (Object.isElement(currentItem)) {
            	var listItem = $($(currentItem).up('li'));
            	if (Object.isElement(listItem)) {
        			item.oldAnchorId = item.currentItem; // id of anchor
	            	item.oldClassName = listItem.className; // className before disable
	            	item.oldAnchorElement = listItem.down('a'); // anchor
            	}
            }
        });
    }
});
Object.extend(Toolbar, {
    instance : null,
    items: null,
    load : function(items, contentToExplore, content) {
        new Toolbar(items || Toolbar.items, null, contentToExplore, content);
    },
    addItem : function(item, contentToExplore) {
        /*if (!Object.isArray(Toolbar.instance) || (Object.isArray(Toolbar.instance) && Toolbar.instance.size() == 0))
            new Toolbar(new Array(item), {autoLoad : false}, contentToExplore);
        else
            Toolbar.instance[0].insertItem(item);*/
        if (!Object.isArray(Toolbar.items)) {
            Toolbar.items = [item];
        } else {
            Toolbar.items.push(item);
        }
    },
    hasRegisteredItems : function() {
        return Object.isArray(Toolbar.items) && Toolbar.items.size() > 0;
    },
    loadRegisteredItems : function(contentToExplore, itemListContainer, newInstance, options, toolbarInstance, content) {
        var instance = null;
        if (newInstance || (!Object.isArray(Toolbar.instance) || (Object.isArray(Toolbar.instance) && Toolbar.instance.size() == 0))) {
            /*if (Toolbar.instance && Toolbar.instance.size() > 0) {
                instance = Toolbar.instance[0].cloneInstance();
                Object.extend(instance.options || {}, options || {});
            } else {
                instance = new Toolbar(Toolbar.items, Object.extend(options || {}, {autoLoad : false}), contentToExplore);
            }*/
            instance = new Toolbar(Toolbar.items, Object.extend(options || {}, {autoLoad : false}), contentToExplore, content);
        } else {
            instance = toolbarInstance || Toolbar.instance[0];

            if (Object.isElement(content))
                instance.content = content.identify();
            else if (Object.isString(content)) {
                instance.content = content;
            }
        }

        if (instance) {
            instance.loadItems(contentToExplore, itemListContainer);
        }

        return instance;
    },
    clearInstance : function(position) {
        if (Toolbar.instance && Toolbar.instance.size() > 0) {
            var instanceToRemove = null;
            // se position = 0 if(0) è falsa
            if (!Object.isUndefined(position)) {
                if (!Object.isNumber(position)) {
                    instanceToRemove = position;
                }

                if (!instanceToRemove && !Object.isUndefined(position) && position < Toolbar.instance.size()) {
                    instanceToRemove = Toolbar.instance[position]
                }

                if (instanceToRemove) {
                    instanceToRemove.clearInstance();
                    Toolbar.instance = Toolbar.instance.without(instanceToRemove);
                }
            } else {
                Toolbar.instance.each(function(currentInstance) {
                    var findItem = currentInstance.items.find(function(item) {
                        return item.currentItem;
                    });
                    /* per cancellare l'istanza di toolbar in tutti i casi
                    if (Object.isUndefined(findItem) || (!Object.isUndefined(findItem) && !Object.isElement($(findItem.currentItem)))) {
                    */
                        Toolbar.clearInstance(currentInstance);
                    /*}*/
                });
            }
        }
    },
    getInstance : function(content) {
        var toReturn = Toolbar.instance[0];
        if (content) {
            for (i = 0; i < Toolbar.instance.length; i++) {
                if (Toolbar.instance[i].content == content) {
                toReturn = Toolbar.instance[i];
                break;
                }
            }
        }
        return toReturn;
    },
    
    /** if item.oldAnchorId is not null enable all item of toolbar */
    enableToolbar: function() {  
    	var items = this.getInstance().items;
        items.each(function(item, index) {
    		var listElement = $(item.oldAnchorId);
        	if (Object.isElement(listElement) && "LI" === listElement.tagName) {
        		listElement.removeAttribute('id');
        		listElement.innerHTML = '';
	        	listElement.className = item.oldClassName;
    			listElement.insert(item.oldAnchorElement);
    			
	        	delete item.oldAnchorId;
	        	delete item.oldAnchorElement;
	        	delete item.oldClassName;
	        }else{
        		delete item.oldAnchorId;
	        	delete item.oldAnchorElement;
	        	delete item.oldClassName;
	        }
        });
    }
});

ToolbarExtensions = {
    load : function() {
        Object.clone = Object.clone.wrap(
            function(proceed, object, deep) {
                if (!deep) {
                    return proceed(object);
                } else {
                    var newInstance = new object.constructor();
                    Object.keys(object).each(function(property) {
                        if (!deep)
                            newInstance[property] = object[property];
                        else if (typeof object[property] == 'object')
                            newInstance[property] = Object.clone(object[property],deep);
                        else
                            newInstance[property] = object[property];
                    });
                    return newInstance;
                }
            }
        );
    }
}

document.observe("dom:loaded", ToolbarExtensions.load);