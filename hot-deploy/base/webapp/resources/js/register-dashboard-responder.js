RegisterDashboardResponder = {
	tabInstanceList : $A([]),
	registerDashboardEventContainerExecutedMap : $H({}),
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent, options) {
                var contentToUpdate = options['contentToUpdate'];

                if (Object.isArray(RegisterDashboardResponder.toolbarInstances) && RegisterDashboardResponder.toolbarInstances.size() > 0) {
                    RegisterDashboardResponder.toolbarInstances.each(function(instance) {
                        Toolbar.clearInstance(instance);
                    });
                }
                if (typeof TableKit != 'undefined') {
                    var portletContainers =  (!Object.isElement(newContent) ? $$('portlet') : newContent.select('.portlet'));
                    
                    if (Object.isArray(portletContainers) && portletContainers.size() > 0 && Object.isArray(RegisterDashboardResponder.toolbarInstances) && RegisterDashboardResponder.toolbarInstances.size() == 0) {
                        RegisterDashboardResponder.tabInstanceList = $A([]);

                        //index = 0 per il primo, 1 per il secondo, ecc...;
                        portletContainers.each(function(portletContainer, index) {
                        	//vado a cercare all'interno il flag useMainToolbar = Y che mi indica di usare 
                        	//la toolbar principale
                        	var useMainToolbar = portletContainer.down("input[name='useMainToolbar']");
                        	var isNewInstance = true;
                        	if (useMainToolbar && "Y" == useMainToolbar.getValue()) {
                        		isNewInstance = false;
                        	}
                            if (!portletContainer.hasClassName('main-portlet')) {
                                // cerco numero di tabs e cerco a quale tab contiene le portlet
                            	var containerSelected = null;
                            	
                            	var contentToUpdateElement = $(contentToUpdate);
                            	if (Object.isElement(contentToUpdateElement)) {
                            		var tabs = Control.Tabs.findByTabId(contentToUpdateElement.identify());
                            		if (tabs) {
                            			containerSelected = contentToUpdateElement;
                            		}
                            	}
                            	
                            	if (!Object.isElement(containerSelected)) {
	                                containerSelected = portletContainer.ancestors().find(function(element){
	                                    return Control.Tabs.findByTabId(element.identify());
	                                });
                            	}
                                
                                if (!Object.isElement(containerSelected)) {
                                	//Cerco tra gli ancestro dell'elemento in cui dovra essere inserito il nuovo contenuto se è presente un tab
                                	if (Object.isElement(contentToUpdateElement)) {
                                		containerSelected = contentToUpdateElement.ancestors().find(function(element){
                                            return Control.Tabs.findByTabId(element.identify());
                                        });
                                	}
                                }
                                
                                
                                if(Object.isElement(containerSelected)) {
                                    var tabInstance = Control.Tabs.findByTabId(containerSelected.identify());
                                    if (tabInstance) {
                                    	// Bug che si verifica quando passi in un folder secondario con portlet, torni al principale con portlet e clicchi su dettaglio
                                    	// commentato condizione 
                                        if (RegisterDashboardResponder.tabInstanceList.indexOf(tabInstance) == -1) // && tabInstance.getActiveContainer() === containerSelected)
                                        	RegisterDashboardResponder.tabInstanceList.push(tabInstance);
                                        RegisterDashboardResponder.registerDashboardEventContainerExecutedMap.unset("registerdashboard_" + portletContainer.identify());
                                        tabInstance.registerEvent(containerSelected.identify(), RegisterDashboardResponder.registeredFunction.curry(portletContainer, isNewInstance), "registerdashboard_" + portletContainer.identify());
                                    }
                                    else{
                                        RegisterDashboardResponder.registeredFunction(portletContainer, isNewInstance, contentToUpdate);
                                    }
                                }
                                else{
                                    RegisterDashboardResponder.registeredFunction(portletContainer, isNewInstance, contentToUpdate);
                                }
                            }
                        });
                    } else if (Control.Tabs.instances.size() == 0) {
                    	RegisterDashboardResponder.tabInstanceList = $A([]);
                    }
                }
            },
            onAfterLoad : function(newContent) {
            	var portletContainers =  (!Object.isElement(newContent) ? $$('portlet') : newContent.select('.portlet'));
            	if (Object.isArray(portletContainers) && portletContainers.size() > 0) {
            		portletContainers.each(function(portletContainer) {
		            	RegisterDashboardResponder.tabInstanceList.each(function(tabInstance) {
		                    //serve perchï¿½ la prima volta deve caricate i 5 portlet
		                    if (Object.isElement(tabInstance.getActiveContainer())) {
		                    	if (!RegisterDashboardResponder.registerDashboardEventContainerExecutedMap.get("registerdashboard_" + portletContainer.identify())) {
			                        tabInstance.dispatchEvent(tabInstance.getActiveContainer().identify(), "registerdashboard_" + portletContainer.identify());
		                    	} else {
		                    		RegisterDashboardResponder.registerDashboardEventContainerExecutedMap.unset("registerdashboard_" + portletContainer.identify());
		                    	}
		                    }
		                });
            		});
            	}
            }
        }, 'register-dashboard-responder');

    },
    registeredFunction : function(portletContainer, isNewInstance, contentToUpdate){
    	RegisterDashboardResponder.registerDashboardEventContainerExecutedMap.set("registerdashboard_" + portletContainer.identify(), true);
        var toolbarInstance = RegisterMenu.findMenus(portletContainer, ['.management', '.contextForm'], 'li.portlet-menu-item', true, isNewInstance);
        var table = portletContainer.down('table.selectable');
        if (table && (TableKit.isSelectable(table))) {
            TableKit.registerObserver(table, 'onDblClickSelectEnd', 'dblclick-select-table', RegisterDashboardResponder.openManagement.curry(toolbarInstance));
        }

    },
    toolbarInstances : [],
    openManagement : function(toolbarInstance, table, e) {
        var portlet = table.up('div.portlet');
        if (toolbarInstance) {
            var item = toolbarInstance.getItem(".management-selected-element");
            if(!Object.isElement(item))
                item = toolbarInstance.getItem(".search-selected-element");
            if (Object.isElement(item))
                item.fire('dom:click');
        }
    }

}

document.observe("dom:loaded", RegisterDashboardResponder.load);