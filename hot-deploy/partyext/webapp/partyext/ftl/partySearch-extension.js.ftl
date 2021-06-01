/* in caso di selezione del parentRoleTypeId viene sovrascritto il pulsante di inserimento in modo da:
 - modificare l'entityName da Party in PartyRoleView
 - inserire in sessione i valori per parentRoleTypeId e roleTypeId inseriti nella form di ricerca 
 
 - Utilizzato anche per la selezione a cascata dell droplist partyRelationshipTypeId-roleTypeId-partyId
 
 */

PartySearchExtension = {
    load: function(newContentToExplore, withoutResponder, lookup) {
        var form = Object.isElement(newContentToExplore) ? (newContentToExplore.tagName == 'FORM' ? $(newContentToExplore) : newContentToExplore.down('form')) : $('searchForm');
        if (form) {
            if (!lookup) {
                PartySearchExtension.registerDropList(form);
                if(form.readAttribute('name').indexOf('PartyRoleView') > -1) {
                	PartySearchExtension.setRoles(form.identify());
                }
            } else {
                LookupProperties.registerAfterLoadModal(PartySearchExtension.registerDropList.curry(form, lookup));
            }
        }
    },

    responder : {
        lookup : false,
        onLoad : function(newContent) {
            PartySearchExtension.load(newContent, true, PartySearchExtension.responder.lookup);
        },
        unLoad : function() {
            return typeof 'PartySearchExtension' === 'undefined';
        }
    },

    action : false,

    registerDropList : function(form, lookup) {

        var formName = form.readAttribute('name');
        
        //registro parentRoleTypeId        
        var dropList = DropListMgr.getDropList(formName + '_parentRoleTypeId');
        if (dropList) {
            dropList.registerOnChangeListener(PartySearchExtension.callFunction.curry(form), 'parentRoleTypeId');
        }
        
        if (!lookup) {
	        //registro partyRelationshipTypeIdFrom
	        var dropPartyRelationshipTypeIdFromList = DropListMgr.getDropList(formName + '_partyRelationshipTypeIdFrom');
	        if (dropPartyRelationshipTypeIdFromList) {
	            dropPartyRelationshipTypeIdFromList.registerOnChangeListener(PartySearchExtension.reloadForm.curry(form), 'partyRelationshipTypeIdFrom');
	        }
	        
	        //registro partyRelationshipTypeIdTo
	        var dropPartyRelationshipTypeIdToList = DropListMgr.getDropList(formName + '_partyRelationshipTypeIdTo');
	        if (dropPartyRelationshipTypeIdToList) {
	            dropPartyRelationshipTypeIdToList.registerOnChangeListener(PartySearchExtension.reloadForm.curry(form), 'partyRelationshipTypeIdTo');
	        }
	        
	        //registro roleTypeIdFrom
	        var dropRoleTypeIdFromList = DropListMgr.getDropList(formName + '_roleTypeIdFrom');
	        if (dropRoleTypeIdFromList) {
	            dropRoleTypeIdFromList.registerOnChangeListener(PartySearchExtension.reloadForm.curry(form), 'roleTypeIdFrom');
	        }
	        
	        //registro roleTypeIdTo
	        var dropRoleTypeIdToList = DropListMgr.getDropList(formName + '_roleTypeIdTo');
	        if (dropRoleTypeIdToList) {
	            dropRoleTypeIdToList.registerOnChangeListener(PartySearchExtension.reloadForm.curry(form), 'roleTypeIdTo');
	        }
        }
    },

	reloadForm : function(form) {
		/** reload è di due tipo per ora funziona solo per le form search!!!
		*  - Form search
		*  - lookup
		**/
		if (form) {
        	form.action = '<@ofbizUrl>searchSimpleContainerOnly</@ofbizUrl>';
            var field = form.getInputs('hidden', 'saveView').first();
            if (field)
                field.value = 'N';
            var element = new Element('input', {'type': 'hidden', 'name': 'ajaxReloading', 'value': 'Y'});
            form.insert(element);
            var field = form.getInputs('hidden', 'wizard').first();
            if (field) {
                field.value = 'Y';
            }    
            else {
	            var element = new Element('input', {'type': 'hidden', 'name': 'wizard', 'value': 'Y'});
	            form.insert(element);
            }
            ajaxSubmitFormUpdateAreas(form, '${searchPanelArea}', '');
        }   
	},
	
    callFunction : function(form) {
        if (form) {
        	form.action = '<@ofbizUrl>searchSimpleContainerOnly</@ofbizUrl>';
            var field = form.getInputs('hidden', 'saveView').first();
            if (field)
                field.value = 'N';
            var element = new Element('input', {'type': 'hidden', 'name': 'ajaxReloading', 'value': 'Y'});
            form.insert(element);
            var field = form.getInputs('hidden', 'wizard').first();
            if (field) {
                field.value = 'Y';
            }    
            else {
	            var element = new Element('input', {'type': 'hidden', 'name': 'wizard', 'value': 'Y'});
	            form.insert(element);
            }
            ajaxSubmitFormUpdateAreas(form, '${searchPanelArea}', '');
            PartySearchExtension.setRoles(form.identify());
        }   
    },
            
    setRoles : function(searchForm) {      
        var insertmodeButton = $$('li.insertmode').first();
        var content = $('searchForm');
        if (typeof RegisterMenu !== 'undefined') {
            var toolbar_instance = Toolbar.getInstance();
            
            if (toolbar_instance) {
            	/* modifica le funzioni eleborateForm per aggiungere i valori di parentRoleTypeId e roleTypeId inseriti nella form di ricerca
            	 e sostituisce la request "managementContainerOnly" con "insertPartyRoleContainerOnly" */
            	var insertmodeItem = toolbar_instance.updateItem('.search-insertmode',{
                                             onSubmit : function(onclickStr, form) {
							                        if (onclickStr && onclickStr.indexOf('(') != -1 && onclickStr.indexOf(')') != -1) {
							                            var argument = onclickStr.substring(onclickStr.indexOf('(')+ 2, onclickStr.lastIndexOf(','));
														
														var arrayArgs = argument.split(',');
	                        							var container = arrayArgs[0];
	                        
	                        							argument = container.concat(',<@ofbizUrl>insertPartyRoleContainerOnly</@ofbizUrl>'); 
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
                                             elaborateForm: function(form) {
                                              	 var field = form.getInputs('hidden', 'entityName').first();
										         if (field)
										             field.setValue('PartyRoleView');
										         var field = $(searchForm).getInputs('hidden', 'parentRoleTypeId').first();
										         if (field) {
										         	 var inputHidden = new Element('input', {'type' : 'hidden', 'value' : field.getValue(), 'name' : 'parentRoleTypeId', 'class' : 'url-params'});
                							         form.insert(inputHidden);
										         }
										         field = $(searchForm).getInputs('hidden', 'roleTypeId').first();
										         if (field) {
										             inputHidden = new Element('input', {'type' : 'hidden', 'value' : field.getValue(), 'name' : 'roleTypeId', 'class' : 'url-params'});
                							         form.insert(inputHidden);
                							     }
        									     return form; 
                                             }
                });
                if ('justRegistered' in insertmodeItem && insertmodeItem.justRegistered) {
	                insertmodeItem.justRegistered = false;
	            }
	            insertmodeItem.writeAttribute('href','#'); 
                toolbar_instance.registerItem(insertmodeButton, content);
            }
        }
    }
}

<#if lookup?has_content && lookup=="Y">
PartySearchExtension.load($('MB_window'), true, true);
<#else>
    <#--<#if !parameters.justRegisters?has_content>-->
        <#--<#if "N" == parameters.ajaxRequest?default("N")>
        document.observe("dom:loaded",PartySearchExtension.load);
        <#else>-->
        PartySearchExtension.load(null, false, false);
        <#--</#if>-->
    <#--</#if>-->
</#if>
