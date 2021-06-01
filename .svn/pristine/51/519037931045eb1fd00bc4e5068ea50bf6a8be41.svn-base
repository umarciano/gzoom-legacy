LookupProperties = {
	options: {
	    'delegateDblClickEvent' : function(table, e) {
	        if (typeof LookupMgr !== 'undefined' && typeof TableKit !== 'undefined') {
	            if (TableKit.isSelectable(table)) {
	                var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
	                if (selectedRow) {
	                    var lookup = LookupMgr.getActiveLookup();
	                    if (lookup) {
	                        var firstCell = $(selectedRow.cells[0]);
	                        if (firstCell) {
	                        	LookupProperties.doubleClickSelected = true;
	                            lookup.setInputFieldFromElements($A(firstCell.select('input')));
	                            lookup.dispatchOnSetInputFieldValue();
	                        } else {
	                        	LookupProperties.doubleClickSelected = false;
	                        }
	                    }
	                }
	            }
	        }
	        Modalbox.hide();
	    },
	    registerMenu: true,
	    registerDropList: true,
	    registerTabs: true,
	    registerLookup: true,
	    registerTable: true,
	    registerForm: true,
	    registerAjaxReloading: true,
	    registerForcusEvent: true,
	    registerCalendar: true
	},
	doubleClickSelected : false,
    responderOnLoad : {
		name : 'default',
        // su onLoad quando cerca la toolbar non trova come prima toolbar quella della modalBox,
        // ma forceResearchFromNewContent non va bene perch� il newContent non contiene la toolbar della modalBox
        onAfterLoad : function(newContent, options) {
            var currentOptions = Object.extend({}, LookupProperties.options || {});
            currentOptions = Object.extend(currentOptions, options || {});

            if (!Object.isElement(newContent)) {
                newContent = $('MB_window');
            }

            var contentToUpdateId = newContent.identify();

            var isInMB = newContent.ancestors().find(function(element) {
                var id = element.identify();
                return id === 'MB_content' || id === 'MB_window';
            });

            if (contentToUpdateId && (contentToUpdateId === 'MB_content' || contentToUpdateId === 'MB_window' || isInMB)) {
                if (typeof Tabs !== 'undefined') {
                    if (Object.isArray(LookupProperties.tabs_instances)) {
                        LookupProperties.tabs_instances.each(function(tabInstance) {
                            tabInstance.deregisterEvent();
                            tabInstance.loadTab(newContent.down('#'+tabInstance.id), true);

                            if (!tabInstance.getActiveContainer()){
                                tabInstance.first();
                            }
                        });
                    }
                }

                if (typeof RegisterMenu !== 'undefined') {
                    Toolbar.clearInstance(LookupProperties.toolbar_instance);
                    LookupProperties.toolbar_instance = RegisterMenu.functionRegistered(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', true, newContent, null, true);

                    if (LookupProperties.toolbar_instance) {
                        try {
                            var backItem =  LookupProperties.toolbar_instance.removeItem('.back');
                            LookupProperties.toolbar_instance.insertItem({ selector: '.back',
                                                                          onClick: function(event) {
                                                                                var element = Event.element(event);
                                                                                Event.stop(event);
    
                                                                                Utils.hideModalBox();
                                                                          }});
                            LookupProperties.toolbar_instance.registerItem(backItem.up('li'), newContent);
                        } catch (e) {
                            console.log(e);
                        }
                    }
                }
                if (typeof DropList !== 'undefined') {
                    DropListMgr.loadElement(newContent, false, LookupProperties.dropListIndexInstance, {suffix : 'Lookup_field'});
                }
                if (typeof Lookup !== 'undefined') {
                    LookupMgr.loadElement(newContent, false, LookupProperties.lookupListIndexInstance, {suffix : 'Lookup_field'});
                }
                if (typeof FormKit !== 'undefined') {
                    LookupProperties.forms = FormKit.reload(newContent);
                }
                if (!Object.isElement(LookupProperties.selectable_table) || !Object.isElement($(LookupProperties.selectable_table).up('div'))) {
                    TableKit.reload(newContent);

                    var tableList = newContent.select('table');

                    LookupProperties.selectable_table = $A(tableList).find(function(table) {
                        return TableKit.isSelectable(table);
                    });
                }
                if (Object.isElement(LookupProperties.selectable_table)) {
                    TableKit.registerObserver(LookupProperties.selectable_table, 'onDblClickSelectEnd', 'dblclick-select-table', currentOptions.delegateDblClickEvent);
                }

                if (typeof CalendarDateSelect != 'undefined') {
                    CalendarDateSelect.reloadCalendar(newContent);
                }

                LookupProperties.singleEditableList = CellSelection._registerFocusEvent(newContent);

                CellSelection._selectRow(newContent);
            }
        }
    },

    afterLoadModal : function(options) {
    	var currentOptions = Object.extend({}, LookupProperties.options || {});
        currentOptions = Object.extend(currentOptions, options || {});
        if (!('responderOnLoad' in currentOptions)) {
        	currentOptions.responderOnLoad = LookupProperties.responderOnLoad;
        }

        var lookupWindow = $('MB_window');

        if (currentOptions.registerTabs && typeof Tabs !== 'undefined') {
            LookupProperties.tabs_instances = Tabs.load(lookupWindow, true, null, true, true, true);
        }
        if (currentOptions.registerMenu && typeof RegisterMenu !== 'undefined') {
            LookupProperties.toolbar_instance = RegisterMenu.functionRegistered(['.management', '.contextForm', '.search-parameters', '.search-list'], 'li.action-menu-item', true, lookupWindow, null, true);
            var backItem =  LookupProperties.toolbar_instance.removeItem('.back');
            LookupProperties.toolbar_instance.insertItem({ selector: '.back',
                                                          onClick: function(event) {
                                                                var element = Event.element(event);
                                                                Event.stop(event);

                                                                Utils.hideModalBox();
                                                          }});
            /**Aggiunto controllo perch� nella lookup della portlet non capira la toolbar*/
            if(Object.isElement(backItem)){
            	LookupProperties.toolbar_instance.registerItem(backItem.up('li'), lookupWindow);
            }
        }
        if (currentOptions.registerDropList && typeof DropList !== 'undefined') {
            LookupProperties.dropListIndexInstance = DropListMgr.loadElement(lookupWindow, true, null, {suffix : 'Lookup_field'});
        }
        if (currentOptions.registerLookup && typeof Lookup !== 'undefined') {
            LookupProperties.lookupListIndexInstance = LookupMgr.loadElement(lookupWindow, true, null, {suffix : 'Lookup_field'});
        }
        if (currentOptions.registerTable &&  typeof TableKit !== 'undefined') {
            TableKit.reload(lookupWindow);

            var tableList = lookupWindow.select('table');

            LookupProperties.selectable_table = $A(tableList).find(function(table) {
                return TableKit.isSelectable(table);
            });
            if (Object.isElement(LookupProperties.selectable_table)) {
                TableKit.registerObserver(LookupProperties.selectable_table, 'onDblClickSelectEnd', 'dblclick-select-table', currentOptions.delegateDblClickEvent);
            }
        }
        if (currentOptions.registerCalendar && typeof CalendarDateSelect != 'undefined') {
            CalendarDateSelect.reloadCalendar(lookupWindow);
        }

        if (Object.isArray(LookupProperties.afterLoadModalListener) && LookupProperties.afterLoadModalListener.size() > 0) {
            LookupProperties.afterLoadModalListener.invoke('apply');
        }
        if (currentOptions.registerForm && typeof FormKit !== 'undefined') {
            LookupProperties.forms = FormKit.reload(lookupWindow);
        }

        if (currentOptions.registerForcusEvent)
            LookupProperties.singleEditableList = CellSelection._registerFocusEvent(lookupWindow);

        CellSelection._selectRow(lookupWindow);

        LookupProperties.freezedResponders = UpdateAreaResponder.Responders.unregisterAll();

        if (currentOptions.registerAjaxReloading) {
        	var responderOnLoadToRegister = currentOptions.responderOnLoad;
        	var name = null;
        	if (Object.isUndefined(responderOnLoadToRegister)) {
        		responderOnLoadToRegister = LookupProperties.responderOnLoad;
        		name = LookupProperties.responderOnLoad.name
        	} else {
        		name = currentOptions.responderOnLoad.name
        	}
            UpdateAreaResponder.Responders.register(responderOnLoadToRegister, name);
        }
    },

    afterLoadModalListener : null,

    registerAfterLoadModal : function(f) {
        if (Object.isFunction(f)) {
            if (!Object.isArray(LookupProperties.afterLoadModalListener))
                LookupProperties.afterLoadModalListener = new Array();
            LookupProperties.afterLoadModalListener.push(f);
        }
    },

    deregisterAfterLoadModal : function(f) {
        if (Object.isFunction(f)) {
            if (Object.isArray(LookupProperties.afterLoadModalListener) && LookupProperties.afterLoadModalListener.size() > 0) {
                LookupProperties.afterLoadModalListener = LookupProperties.afterLoadModalListener.without(f);
            }
        }
    },

    clearAfterLoadModal : function() {
        if (Object.isArray(LookupProperties.afterLoadModalListener)){
            LookupProperties.afterLoadModalListener = null;
        }
    },

    beforeHideModal : function(options) {
        var currentOptions = Object.extend({}, LookupProperties.options || {});
        currentOptions = Object.extend(currentOptions, options || {});

        if (currentOptions.registerMenu && typeof Toolbar !== 'undefined' && LookupProperties.toolbar_instance) {
            Toolbar.clearInstance(LookupProperties.toolbar_instance);
            LookupProperties.toolbar_instance =null;
            LookupProperties.exists_toolbar_instance = false;
        }

        if (currentOptions.registerTabs && typeof Tabs !== 'undefined' && LookupProperties.tabs_instances && LookupProperties.tabs_instances.size() > 0) {
            LookupProperties.tabs_instances.each(function(instance) {
                //Tabs.load(null, null, true);
                if ('lookup' in instance.options) {
                    instance.options.lookup = false;
                }

                Control.Tabs.clearInstance(instance);
            });
        }
        if (currentOptions.registerDropList && typeof DropList !== 'undefined' && LookupProperties.dropListIndexInstance >= 0) {
            DropListMgr.clearInstance(LookupProperties.dropListIndexInstance);
        }
        if (currentOptions.registerLookup && typeof Lookup !== 'undefined' && LookupProperties.lookupListIndexInstance >= 0) {
            LookupMgr.clearInstance(LookupProperties.lookupListIndexInstance);
        }
        if (currentOptions.registerTable && typeof TableKit !== 'undefined' && LookupProperties.selectable_table) {
            TableKit.unregisterObserver(LookupProperties.selectable_table, 'onDblClickSelectEnd', 'dblclick-select-table');
            TableKit.unloadTable(LookupProperties.selectable_table);
            LookupProperties.selectable_table = null;
        }
        if (currentOptions.registerForm && typeof FormKit !== 'undefined' && LookupProperties.forms) {
            LookupProperties.forms.each(function(form) {
                FormKit.unloadForm(form);
            });
            LookupProperties.forms = null;
        }

        if (currentOptions.registerForcusEvent && LookupProperties.singleEditableList) {
            LookupProperties.singleEditableList.each(function(table) {
                table.select('input, select').each(function(element) {
                    if (element.type !== 'hidden' && element.type !== 'submit') {
                        Event.stopObserving(element, 'focus');

                        Event.stopObserving(element, 'blur');
                    }
                });
            });
            LookupProperties.singleEditableList = null;
        }
    },

    afterHideModal : function(options) {
        var currentOptions = Object.extend({}, LookupProperties.options || {});
        currentOptions = Object.extend(currentOptions, options || {});
        if (!('responderOnLoad' in currentOptions)) {
        	currentOptions.responderOnLoad = LookupProperties.responderOnLoad;
        }

        if (currentOptions.registerAjaxReloading) {
        	var responderOnLoadToRemove = currentOptions.responderOnLoad;
        	var name = null;
        	if (Object.isUndefined(responderOnLoadToRemove)) {
        		responderOnLoadToRemove = LookupProperties.responderOnLoad;
        		name = LookupProperties.responderOnLoad.name
        	} else {
        		name = currentOptions.responderOnLoad.name
        	}
            UpdateAreaResponder.Responders.unregister(responderOnLoadToRemove, name);
        }

        if (Object.isHash(LookupProperties.freezedResponders)) {
            UpdateAreaResponder.Responders.registerAll(LookupProperties.freezedResponders);
            LookupProperties.freezedResponders = null;
        }

        LookupProperties.clearAfterLoadModal();
        
        if (LookupProperties.doubleClickSelected) {
        	LookupMgr.getActiveLookup().dispatchOnSetInputFieldValue();
        	
        	LookupProperties.doubleClickSelected = false;
        }
    },

    toolbar_instance : null, // instanza di toolbar
    forms : null,
    tabs_instances : null,
    selectable_table : null,
    dropListIndexInstance : null,
    lookupListIndexInstance : null,
    freezedResponders: null,
    singleEditableList: null,
}