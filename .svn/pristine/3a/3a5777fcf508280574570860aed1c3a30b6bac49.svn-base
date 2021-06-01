SystemInfoNotesLinkListener = {

    systemInfoNotesLinkClass : ".system-info-notes-link",
    systemInfoNotesDeleteLinkClass : ".system-info-notes-delete-link",
    systemInfoNotesStatusLinkClass : ".system-info-notes-status-link",
    communicationLinkClass : ".communication-link",
    closeCommunicationLinkClass : ".close-communication-link",
    deleteCommunicationLinkClass : ".delete-communication-link",
    systemInfoNotesDeleteOneLinkClass : ".system-info-notes-delete-one-link",
    
    load: function(newContent, withoutResponder) {
         var newContent = Object.isElement($(newContent)) ? $(newContent) : $(document.body);

         var systemInfoNotesCreateLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesLinkClass);

         Utils.stopObserveEvent(systemInfoNotesCreateLink, "click", SystemInfoNotesLinkListener.createLinkListener);
         Utils.observeEvent(systemInfoNotesCreateLink, "click", SystemInfoNotesLinkListener.createLinkListener);

         var systemInfoNotesDeleteAllLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesDeleteLinkClass);
         Utils.stopObserveEvent(systemInfoNotesDeleteAllLink, "click", SystemInfoNotesLinkListener.deleteAllLinkListener);
         Utils.observeEvent(systemInfoNotesDeleteAllLink, "click", SystemInfoNotesLinkListener.deleteAllLinkListener);

         var systemInfoNotesStatusLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesStatusLinkClass);
         Utils.stopObserveEvent(systemInfoNotesStatusLink, "click", SystemInfoNotesLinkListener.infoNotesStatusListener);
         Utils.observeEvent(systemInfoNotesStatusLink, "click", SystemInfoNotesLinkListener.infoNotesStatusListener);

         var communicationLink = newContent.select(SystemInfoNotesLinkListener.communicationLinkClass);
         Utils.stopObserveEvent(communicationLink, "click", SystemInfoNotesLinkListener.communicationListener);
         Utils.observeEvent(communicationLink, "click", SystemInfoNotesLinkListener.communicationListener);

         var closeCommunicationLink = newContent.select(SystemInfoNotesLinkListener.closeCommunicationLinkClass);
         Utils.stopObserveEvent(closeCommunicationLink, "click", SystemInfoNotesLinkListener.closeCommunicationListener);
         Utils.observeEvent(closeCommunicationLink, "click", SystemInfoNotesLinkListener.closeCommunicationListener);
         
         var deleteCommunicationLink = newContent.select(SystemInfoNotesLinkListener.deleteCommunicationLinkClass);
         Utils.stopObserveEvent(deleteCommunicationLink, "click", SystemInfoNotesLinkListener.deleteCommunicationListener);
         Utils.observeEvent(deleteCommunicationLink, "click", SystemInfoNotesLinkListener.deleteCommunicationListener);
         
         var systemInfoNotesDeleteOneLink = newContent.select(SystemInfoNotesLinkListener.systemInfoNotesDeleteOneLinkClass);
         Utils.stopObserveEvent(systemInfoNotesDeleteOneLink, "click", SystemInfoNotesLinkListener.deleteOneLinkListener);
         Utils.observeEvent(systemInfoNotesDeleteOneLink, "click", SystemInfoNotesLinkListener.deleteOneLinkListener);
         
         SystemInfoNotesLinkListener.responderOnLoad = Object.extend({name: 'SystemInfoNotesLinkListener', onBeforeLoad : SystemInfoNotesLinkListener.onBeforeLoadListener}, LookupProperties.responderOnLoad);

         if (!withoutResponder) {
             UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    if (newContent) {
                        SystemInfoNotesLinkListener.load(newContent, true);
                    }
                }
            }, 'system-info-notes-link-listener');
         }
    },

    communicationListener: function(event) {
        Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());
            var title = "";
            if (params.get("title")) {
                title = params.get("title");
                title = title.replace("[", "");
                title = title.replace("]", "");
            }
            params.unset("title");

            Utils.showModalBox(href, {'title' : title, afterLoadModal: LookupProperties.afterLoadModal.curry({registerMenu : false, 'responderOnLoad' : SystemInfoNotesLinkListener.responderOnLoad}), beforeHideModal: LookupProperties.beforeHideModal.curry({registerMenu : false}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : SystemInfoNotesLinkListener.responderOnLoad}), width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.40)});
        }

    },

    closeCommunicationListener: function(event) {
        Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "A") {
            var portletMainContainer = element.up('.portlet-main-container');
            if (Object.isElement(portletMainContainer)) {
                portletMainContainer.down().addClassName('hidden');
            }
        }

    },

    infoNotesStatusListener: function(event) {
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());

            var extraTargetParameters = params.get("extraTargetParameters");
            if (extraTargetParameters) {
                Event.stop(event);

                params.unset("extraTargetParameters");

                extraTargetParameters = extraTargetParameters.replace(/\*/g,'&');

                var extraParams = $H(extraTargetParameters.toQueryParams());
                extraParams = extraParams.merge(params);

                var currentPortlet = element.up('.portlet-main-container');
                if (!Object.isElement(currentPortlet.next('#portlet-container_' + extraParams.get('portalPageId') + '_' + extraParams.get('portalPortletId') + '_' + extraParams.get('portletSeqId'))))
                    Element.insert(currentPortlet, {'after' : new Element('div', {'id' : 'portlet-container_' + extraParams.get('portalPageId') + '_' + extraParams.get('portalPortletId') + '_' + extraParams.get('portletSeqId'), class : 'portlet-main-container'})});

                ajaxUpdateArea('portlet-container_' + extraParams.get('portalPageId') + '_' + extraParams.get('portalPortletId') + '_' + extraParams.get('portletSeqId'), href.substring(0, href.indexOf('?')), extraParams.toQueryString())
            }
        }
    },

    createLinkListener: function(event) {
        Event.stop(event);
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());
            var title = "";
            if (params.get("title")) {
                title = params.get("title");
                title = title.replace("[", "");
                title = title.replace("]", "");
            }
            params.unset("title");

            Utils.showModalBox(href, {'title' : title, afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : SystemInfoNotesLinkListener.responderOnLoad}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : SystemInfoNotesLinkListener.responderOnLoad}), width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.50), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.75)});
        }

    },

    responderOnLoad : false,


    onBeforeLoadListener : function(contentToUpdate, options) {
    	if (Object.isString(contentToUpdate) && !Object.isElement($(contentToUpdate))) {
            var data = options['data'];
            Utils.hideModalBox({
                afterHide: LookupProperties.afterHideModal.wrap(function(original, options) {
                    original(options);
                    UpdateAreaResponder.executeAjaxUpdateAreas(contentToUpdate, data);
                })
            });
        } else {
            return true;
        }

        return false;
    },
    
    deleteAllLinkListener: function(event) {
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());

            var extraAreaTarget = params.get("extraAreaTarget");
            var extraAreaId = params.get("extraAreaId");
            var extraTargetParameters = params.get("extraTargetParameters");

            if (extraAreaTarget && extraAreaId && extraTargetParameters) {
                Event.stop(event);

                var currentPortlet = element.up('.portlet-main-container');
                if (Object.isElement(currentPortlet)) {
	                var selectableTable = currentPortlet.down('table.selectable');
	                if (Object.isElement(selectableTable)) {
		                modal_box_messages.confirm(['BaseMessageDeleteDataConfirm'],null,function() {
			                params.unset("extraAreaTarget");
			                params.unset("extraAreaId");
			                params.unset("extraTargetParameters");
			
			                href = href.substring(0, href.indexOf('?'));
			                var form = new Element('form', {'action' : href});
			                params.each(function(pair) {
			                    form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
			                });
			
			                document.body.insert(form);
			
			                extraTargetParameters = extraTargetParameters.replace(/\*/g,'&');
			                ajaxSubmitFormUpdateAreas(form.identify(), '', extraAreaId + ',' + extraAreaTarget + ',' + extraTargetParameters);
			
			                form.remove();
		                });
	                } else {
	                	modal_box_messages.alert(['BaseMessageNoSelection']);
	                }
                }
            }
        }
    },
    
    deleteCommunicationListener: function(event) {
        var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());

            var extraAreaTarget = params.get("extraAreaTarget");
            var extraAreaId = params.get("extraAreaId");
            var extraTargetParameters = params.get("extraTargetParameters");

            if (extraAreaTarget && extraAreaId && extraTargetParameters) {
                Event.stop(event);
                
                var currentPortlet = element.up('.portlet-main-container');
                if (Object.isElement(currentPortlet)) {
	                var selectableTable = currentPortlet.down('table.selectable');
	                if (Object.isElement(selectableTable)) {
	                	var selectedRows = TableKit.Selectable.getSelectedRows(selectableTable);
	                    if (selectedRows) {
	                    	modal_box_messages.confirm(['BaseMessageDeleteDataConfirm'],null,function() {
	                        	params.unset("extraAreaTarget");
	                            params.unset("extraAreaId");
	                            params.unset("extraTargetParameters");
	                            
	                            selectedRows.each(function(row, index) {
		                            $A(row.select('input')).each(function(element) {
		                                var elementName = element.readAttribute("name");
		                                if(elementName.indexOf('_o_') > -1) {
		                                    elementName = elementName.substring(0,elementName.indexOf('_o_'));
		                                }
		                                var elementValue = element.readAttribute("value");
		                                if (elementValue) {
		                                	params.set(elementName, elementValue);
		                                }
		                            });
		                    	});
	                            
	                            href = href.substring(0, href.indexOf('?'));
	                            var form = new Element('form', {'action' : href});
	                            params.each(function(pair) {
	                                form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
	                            });

	                            document.body.insert(form);

	                            var extraAreaIdList = extraAreaId.split('0$;')
	                            if (!Object.isArray(extraAreaIdList) || (Object.isArray(extraAreaIdList) && extraAreaIdList.length == 0)) {
	                            	extraAreaIdList = [extraAreaId];
	                            }
	                            var extraTargetParametersList = extraTargetParameters.split('0$;')
	                            if (!Object.isArray(extraTargetParametersList) || (Object.isArray(extraTargetParametersList) && extraTargetParametersList.length == 0)) {
	                            	extraTargetParametersList = [extraTargetParameters];
	                            }
	                            
	                            var submitParameters = extraAreaIdList.inject('', function(str, areaId, index) {
	                            	return str + (!str.empty() ? ',' : '') + areaId + ',' + extraAreaTarget + ',' + extraTargetParametersList[index].replace(/\*/g,'&');
	                            });
	                            
	                            ajaxSubmitFormUpdateAreas(form.identify(), '', submitParameters);

	                            form.remove();
	                        });
	                    } else {
	                    	modal_box_messages.alert(['BaseMessageNoSelection']);
	                    }
	                }
                }

                
            }
        }
    },
    
    deleteOneLinkListener: function(event) {
    	var element = Event.element(event);
        if (element.tagName == "A") {
            var href = element.getAttribute("href");
            var params = $H(href.toQueryParams());

            var extraAreaTarget = params.get("extraAreaTarget");
            var extraAreaId = params.get("extraAreaId");
            var extraTargetParameters = params.get("extraTargetParameters");

            if (extraAreaTarget && extraAreaId && extraTargetParameters) {
                Event.stop(event);

                var currentPortlet = element.up('.portlet-main-container');
                if (Object.isElement(currentPortlet)) {
	                var selectableTable = currentPortlet.down('table.selectable');
	                if (Object.isElement(selectableTable)) {
		                modal_box_messages.confirm(['BaseMessageDeleteDataConfirm'],null,function() {
			                params.unset("extraAreaTarget");
			                params.unset("extraAreaId");
			                params.unset("extraTargetParameters");
			
			                href = href.substring(0, href.indexOf('?'));
			                
			                var form = new Element('form', {'action' : href});
			                params.each(function(pair) {
			                    form.insert(new Element('input', {'type' : 'hidden', 'name' : pair.key, 'value' : pair.value}));
			                });
			
			                
			                var modelListTable = $('table_PE_PNVML001_SystemInfoNotes');
			                if(Object.isElement(modelListTable)) {	       
			                    var selectRow = TableKit.Selectable.getSelectedRows(modelListTable)[0];                
			                    var queryParams = "";
			    	            selectRow.select('input[type="hidden"]').each(function(elm) {
			    	            	form.insert(new Element('input', {'type' : 'hidden', 'name' : elm.readAttribute('name'), 'value' : elm.getValue()}));
			    	            });
			                }
			                
			                
			                document.body.insert(form);
			
			                extraTargetParameters = extraTargetParameters.replace(/\*/g,'&');
			                ajaxSubmitFormUpdateAreas(form.identify(), '', extraAreaId + ',' + extraAreaTarget + ',' + extraTargetParameters);
			
			                form.remove();
		                });
	                } else {
	                	modal_box_messages.alert(['BaseMessageNoSelection']);
	                }
                }
            }
        }
    },

    _evalFunc : function(str) {
        eval('var evalFuncTmp = function() { ' + str + '; }');
        return evalFuncTmp();
    },

    _evalBool : function(str) {
        return (this._evalFunc(str) != false);
    }
}

document.observe("dom:loaded", SystemInfoNotesLinkListener.load);