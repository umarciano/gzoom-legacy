WorkEffortAssocExtViewList = {
    // Manage management-insertmode
    // and contextLink
    // and panel for total
    // TODO fix it
    CONTEXT_LINK_ROOT_SELECTOR : ".workeffort-root-link",
    
    load: function(withoutResponder) {
        WorkEffortAssocExtViewList.insertChoice = '${insertChoice?if_exists?default("N")}';
        WorkEffortAssocExtViewList.relationTitle = '${relationTitle?if_exists?default("")}';
    	WorkEffortAssocExtViewList.loadWorkEffortAssocExtTable($('table_${workEffortAssocExtViewType?if_exists}'));
    	var folderTitles = WorkEffortAssocExtViewList.getFolderTitles();
		
    	if(WorkEffortAssocExtViewList.isFormToListen() != null){
    		var insertModeLink = $(document.body).down(".management-insertmode");
    		if(Object.isElement(insertModeLink)) {
	            var insertModeAnchor = insertModeLink.down("a");
	            if (Object.isElement(insertModeAnchor)) {
	                insertModeAnchor.writeAttribute("href", "javascript:void(0);");
	            	Utils.stopObserveEvent([insertModeAnchor], this.eventName, WorkEffortAssocExtViewList.createLinkListener);
	            	Utils.observeEvent([insertModeAnchor], this.eventName, WorkEffortAssocExtViewList.createLinkListener);
	            	Event.stopObserving(insertModeAnchor, 'click');
            		insertModeAnchor.observe("click", function(e) {
            		    if (WorkEffortAssocExtViewList.insertChoice == 'Y') {
            		        var popupTitle = '${uiLabelMap.WeAssocInsertCatalogPopupTitle}' + ': ' + folderTitles.get(WorkEffortAssocExtViewList.relationTitle);
							Modalbox.show($('popup-catalog-choice'), {width: 400, title: popupTitle});
						} else {
		    				var contextForm = $$('#contextManagementSearchForm-WorkEffortAssocExtView-' + WorkEffortAssocExtViewList.relationTitle)[0];
		    				WorkEffortAssocExtViewList.submitInsertForm(insertModeAnchor, contextForm, true);
                    
						}
					});
	            }            
    		}
    		
    		var contextLinkToModifyArray = $$('.workEffortMenuAssocExtView');
        	if (Object.isArray(contextLinkToModifyArray)) {
	        	contextLinkToModifyArray.each( function(contextLinkToModify) {
		        	var contextLinkAnchor = contextLinkToModify.down("a");
		            Utils.stopObserveEvent([contextLinkAnchor], this.eventName, WorkEffortAssocExtViewList.cleanActiveLink);
		            Utils.observeEvent([contextLinkAnchor], this.eventName, WorkEffortAssocExtViewList.cleanActiveLink);
		            
		            
		            
	        	}.bind(this));
        	}
        	var contextLinkArray = $$(WorkEffortAssocExtViewList.CONTEXT_LINK_ROOT_SELECTOR);
            if (Object.isArray(contextLinkArray)) {
                contextLinkArray.each( function(contextLink) {
                    if (!contextLink.hasClassName('always-active')) {
                        contextLink.addClassName('hidden');
                    }
                });
            }        
    	}
    },
    
    getFolderTitles: function() {
        var folderTitles = $H({});
        folderTitles.set('From', '${WEFLD_WEFROM_title?if_exists?default("")}');
        folderTitles.set('From2', '${WEFLD_WEFROM2_title?if_exists?default("")}');
        folderTitles.set('From3', '${WEFLD_WEFROM3_title?if_exists?default("")}');
        folderTitles.set('From4', '${WEFLD_WEFROM4_title?if_exists?default("")}');
        folderTitles.set('From5', '${WEFLD_WEFROM5_title?if_exists?default("")}');
        folderTitles.set('To', '${WEFLD_WETO_title?if_exists?default("")}');
        folderTitles.set('To2', '${WEFLD_WETO2_title?if_exists?default("")}');
        folderTitles.set('To3', '${WEFLD_WETO3_title?if_exists?default("")}');
        folderTitles.set('To4', '${WEFLD_WETO4_title?if_exists?default("")}');
        folderTitles.set('To5', '${WEFLD_WETO5_title?if_exists?default("")}');
        return folderTitles;
    },
    
    activeLinkWorkEffortViewManagementTabMenu: 'management_${folderIndex?if_exists}',
    
    eventName: "mousedown",
    
    createLinkListener: function(event) {
    	var insertModeLink = $(document.body).down(".management-insertmode");
        var insertModeAnchor = insertModeLink.down("a");
        Utils.stopObserveEvent([insertModeAnchor], WorkEffortAssocExtViewList.eventName, WorkEffortAssocExtViewList.createLinkListener);
		var result = false;
		
		// perche google invoca l evento molte volte... controllare se per google ci sono altri Event.stop(event) o  Utils.stopObserveEvent([insertModeAnchor], this.eventName) da inserire
		Event.stop(event);
		
		var cachableForm = WorkEffortAssocExtViewList.isFormToListen();
		if(cachableForm != null) {
		    // TODO check only principal form...
	        if(Object.isElement(cachableForm)){
				result = FormKit.Cachable.checkModification(cachableForm.id, FormKitExtension.checkModficationFilterCallback);
			}
			if(result){
	        	if(confirm("${uiLabelMap.BaseSaveConfirm}")) {
	        		var onsubmit = $(cachableForm).readAttribute("onsubmit");
	            	var containerId = onsubmit.split(",")[2].substring(2);
	         		var saveItem = Toolbar.getInstance(containerId).getItem('.save');
	                if (Object.isElement(saveItem)) {
	                	saveItem.fire('dom:click');
	                }
	               
	        		FormKit.loadFields(cachableForm);
	        		return false;
	        	} else {
	        		var onsubmit = $(cachableForm).readAttribute("onsubmit");
	            	var containerId = onsubmit.split(",")[2].substring(2);
	            	var resetItem = Toolbar.getInstance(containerId).getItem('.management-reset');
	                if (Object.isElement(resetItem)) {
	                	resetItem.fire('dom:click');
	                }
	                return false;
	        	}
	        	
				return false;
			}
		}
		 
        return false;
	},
	
	isFormToListen : function(){
		var tab = Control.Tabs.findByTabId('management_0');
		if (!tab) {
			container = $('management-form-screenlet');
		} else {
			container = tab.getActiveContainer();
			if (!Object.isElement(container)) {
				container = $('management-form-screenlet');
			}
		}
		
		if (Object.isElement(container)) {
			var cachableForm =  $(container).down('form.cachable');
			
			if(Object.isElement(cachableForm)){
				// potrebbe essere 'form#WEAEVMM002_WorkEffortAssocExtView_WEFLD_WEFROM' ||'form#WEAEVMM003_WorkEffortAssocExtView_WEFLD_WETO'
				if(cachableForm.id.indexOf("WEAEVMM001") > -1 || cachableForm.id.indexOf("WEAEVMM002") > -1 || cachableForm.id.indexOf("WEAEVMM003") > -1) {
					return cachableForm;
				}
			}
		}
		
		return null;
	},
	
	loadWorkEffortAssocExtTable : function(workEffortAssocExtTable) {
        if(workEffortAssocExtTable) {
            if (typeof TableKit != 'undefined') {
                if (TableKit.isSelectable(workEffortAssocExtTable)) {
                    WorkEffortAssocExtViewList.managContextLink(workEffortAssocExtTable, WorkEffortAssocExtViewList.CONTEXT_LINK_ROOT_SELECTOR);
                    WorkEffortAssocExtViewList.manageSelectedRow(workEffortAssocExtTable);
                    TableKit.registerObserver(workEffortAssocExtTable, 'onSelectEnd', 'workeffortassocext-list-selection', function(workEffortAssocExtTable, e) {
                        WorkEffortAssocExtViewList.managContextLink(workEffortAssocExtTable, WorkEffortAssocExtViewList.CONTEXT_LINK_ROOT_SELECTOR);
                        WorkEffortAssocExtViewList.manageSelectedRow(workEffortAssocExtTable);
                    });
                    
                                      
                    /**
                    * Calcolo somma dei kpiScoreWeight solo nel caso di indicatore
                    */
                    if ($('${workEffortAssocExtViewType?if_exists}_assocWeightTot') != null) {
	            		WorkEffortAssocExtViewList.assocWeightTot(workEffortAssocExtTable); 
                   }
                }
            }
         }
    },
    
  
    assocWeightTot : function(workEffortAssocExtTable) {
    	/**
        * Utilizzato per calcolare la somma assocWeightTot  -- aggiungere il nome per ogni formato!!!
        */
        var assocWeightLists = workEffortAssocExtTable.select('input').findAll(function(element) {
            if(element.readAttribute('id')) {
                return element.readAttribute('id').indexOf("WorkEffortAssocExt${workEffortAssocExtAssocWeight?if_exists}ViewManagementMultiForm_assocWeight") > -1 ||
                element.readAttribute('id').indexOf("WorkEffortAssocExtFromView${workEffortAssocExtAssocWeight?if_exists}GeneralManagementMultiForm_assocWeight") > -1 ||
                element.readAttribute('id').indexOf("WorkEffortAssocExt${workEffortAssocExtAssocWeight?if_exists}ViewEvalManagementMultiForm_assocWeight") > -1 ||
                element.readAttribute('id').indexOf("WorkEffortAssocExt${workEffortAssocExtAssocWeight?if_exists}ViewEvalWithScoreManagementMultiForm_assocWeight") > -1 ||                
                element.readAttribute('id').indexOf("WorkEffortAssocFromLevelBelowLayoutManagementMultiForm_assocWeight") > -1 ;
            }
        });

        if(assocWeightLists) {
        	var newElementAdd = "";
    		var assocWeightTotInput = $('${workEffortAssocExtViewType?if_exists}_assocWeightTot');
    		var assocWeightTotInputHidden = $('${workEffortAssocExtViewType?if_exists}_assocWeightTotHidden');
    	
            assocWeightLists.each(function(element) {
            	
                //controllo per ogni elemento se è quello selezionato se si setto la variabile assocWeightOld
                tr = element.up('tr');
                if (tr.hasClassName('selected-row')) {
                	element.assocWeightOld = element.getValue();
                }
                
                //controllo se è un elemento appena inserito, se si aggiungo alla somma il valore di assocWeightOld
				var rowIndex = TableKit.getRowIndex(tr);       
                insertMode = tr.select("input[name=insertMode_o_"+ rowIndex +"]").first();
                if (typeof insertMode !== 'undefined'  && insertMode.getValue() == 'Y' ) {                	
                	WorkEffortAssocExtViewList.setValue(assocWeightTotInput, "");
                	
                	//controllo se è un elemento appena inserito, se si aggiungo alla somma il valore di assocWeightOld
	                var workEffortId;
	                if ("${workEffortAssocExtAssocWeight?if_exists}" == "To") {
	                	workEffortId = tr.select("input[name=workEffortIdFrom_o_"+ rowIndex +"]").first();
	                } else {
	                	workEffortId = tr.select("input[name=workEffortIdTo_o_"+ rowIndex +"]").first();
	                }
	                
	                
                	if ( typeof workEffortId !== 'undefined'  && workEffortId.getValue() !== '') {
	                	if (newElementAdd == "") {
	                		newElementAdd = "[";
	                	} else {
	                		newElementAdd += ";";
	                	}
	                	newElementAdd += element.getValue();
	                }
                } 
                
                Event.stopObserving(element, 'change');
                Event.observe(element, 'change', function(e) {
                    var assocWeightTotInput = $('${workEffortAssocExtViewType?if_exists}_assocWeightTot');
                    var assocWeightTotInputHidden = $('${workEffortAssocExtViewType?if_exists}_assocWeightTotHidden');
                    
                    WorkEffortAssocExtViewList.setValue(assocWeightTotInput, "");
                    
                    new Ajax.Request("<@ofbizUrl>calculateAssocWeightOrKpiScoreWeight</@ofbizUrl>", {
                        parameters: {
                            "amountTotValue": WorkEffortAssocExtViewList.getValue(assocWeightTotInputHidden),
                            "amountOld": this.assocWeightOld,
                            "amountNew": this.getValue()                                
                        }, 
                        onSuccess: function(response){
                            var data = response.responseText.evalJSON(true);
                            WorkEffortAssocExtViewList.setValue(assocWeightTotInput, data.amountTotValueNew);
                            WorkEffortAssocExtViewList.setValue(assocWeightTotInputHidden, data.amountTotValueNew);                                 
                        }
                    });
                });
				element.observe('focus',function(event) {
				  this.assocWeightOld = this.getValue();
				});
            });
   			
   			/**
   			* Calcolo la somma delle nuove righe che sono state inserite e aggiorno il totale
   			*/
   			if (newElementAdd != "") {
   				newElementAdd += "]";
   				new Ajax.Request("<@ofbizUrl>calculateAssocWeightOrKpiScoreWeightAddNew</@ofbizUrl>", {
                    parameters: {
                        "amountTotValue": WorkEffortAssocExtViewList.getValue(assocWeightTotInputHidden),
                        "amountElementList": newElementAdd		                     
                    }, 
	            	onSuccess: function(response){
	               		var data = response.responseText.evalJSON(true);
	               		WorkEffortAssocExtViewList.setValue(assocWeightTotInput, data.amountElementTotValueNew);
	               		WorkEffortAssocExtViewList.setValue(assocWeightTotInputHidden, data.amountElementTotValueNew);							                    
	                }
	            }); 
   			
   			}
            
        }
		                               
    },  
    
    managContextLink : function(table, contextLink) {
        var form = table.up("form");
        var selectedRows = $A(TableKit.Selectable.getSelectedRows(table));
    	if(selectedRows && selectedRows.size() > 0) {
    		var selectedRow = selectedRows.first();
        	
            var contextLinkArray = $$(contextLink);
            if (Object.isArray(contextLinkArray)) {
                contextLinkArray.each( function(contextLink) {
                    if (!contextLink.hasClassName('always-active')) {
                        contextLink.addClassName('hidden');
                    }
                });
            }
    		
        	var worEffortParentIdTo = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf('worEffortParentIdTo') > -1;
            });  
            
            var worEffortParentIdFrom = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf('worEffortParentIdFrom') > -1;
            }); 
            
            if( worEffortParentIdFrom.readAttribute('value') == worEffortParentIdTo.readAttribute('value')) {
                var context_link = $$(contextLink).first();
                if (Object.isElement(context_link)) {
	                context_link.addClassName('hidden');
                }
            }else{
                var rowIndex = TableKit.getRowIndex(selectedRow);            
                var relationTitle = WorkEffortAssocExtViewList.getRelationTitle(form, rowIndex);        
                var workEffortIdField = (relationTitle == 'from' ? worEffortParentIdFrom : worEffortParentIdTo);
                if(workEffortIdField.readAttribute('value')) {
                    new Ajax.Request("<@ofbizUrl>getCanViewUpdateWorkEffortRoot</@ofbizUrl>", {
                        parameters: {
                            "workEffortRootId": workEffortIdField.readAttribute('value')
                        }, 
                        onSuccess: function(response){
                            var data = response.responseText.evalJSON(true);
                            if(data.canViewRoot == "Y") {
                                if (Object.isArray(contextLinkArray)) {
                                    contextLinkArray.each( function(contextLink) {
                                        if (contextLink.hasClassName('hidden')) {
                                            contextLink.removeClassName('hidden');
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        }
    },
	
	getRelationTitle : function(form, rowIndex) {
	    var relationTitleField = $(form.down("input[name=relationTitle_o_" + rowIndex + "]"));
		if(Object.isElement(relationTitleField)) {
		    return relationTitleField.getValue();
		}
		return '';
	},
       
    manageSelectedRow : function(table) {
    	var form = table.up("form");
        var formName = form.readAttribute("name");
            
        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
        if(selectedRow) {
        	WorkEffortAssocExtViewList.changeSubmitFormIfChildTemplateExists(form);
            
            var rowIndex = TableKit.getRowIndex(selectedRow);            
			var relationTitle = WorkEffortAssocExtViewList.getRelationTitle(form, rowIndex);		
			var workEffortLookupName = (relationTitle == 'from' ? 'workEffortIdTo' : 'workEffortIdFrom');
            var workeffortLookup = form.down("div#" + formName + "_" + workEffortLookupName + "_o_" + rowIndex);
            if(workeffortLookup) {
                var lookup = LookupMgr.getLookup(workeffortLookup.identify());
                if (lookup) {
                    lookup.registerOnSetInputFieldValue(WorkEffortAssocExtViewList.changeSubmitFormIfChildTemplateExists.curry(form), "WorkEffortAssocExtViewList_workEffortLookupHandler");                    
                }
            }
            
            // in ascolto evento onchange droplist workeffort
            var dropListId = formName + "_" + workEffortLookupName + "_o_" + rowIndex;
            var workEffortDropList = DropListMgr.getDropList(dropListId);
            if(workEffortDropList) {
                workEffortDropList.registerOnChangeListener(WorkEffortAssocExtViewList.changeSubmitFormIfChildTemplateExists.curry(form), 'WorkEffortAssocExtViewList_workEffortDropListHandler');
            }
        }
    },
    
    changeSubmitFormIfChildTemplateExists : function (form) {		
        var onclickStr = form.readAttribute("onSubmit");
        var isToCloneWorkEffort = WorkEffortAssocExtViewList.isToCloneWorkEffortFromChildTemplate(form);

        if (isToCloneWorkEffort == true) {
			
			action = form.readAttribute('action');
        	action = action.substring(1, action.lenght);        	
        	modulo =  action.substring(0, action.indexOf('/'));
        	
		    var arrayStr = onclickStr.split(',');
		    arrayStr[2] = "'common-container";
			arrayStr[3] = "/"+ modulo + "/control/managementContainerOnly";
			arrayStr[4] = "entityName=WorkEffortView&loadTreeView=Y&screenNameListIndex=${context.screenNameListIndex?if_exists}" +
			              "&workEffortId=${parameters.workEffortIdRoot?if_exists}&noLeftBar=${parameters.noLeftBar?if_exists?string}&rootInqyTree=${parameters.rootInqyTree?if_exists}" +
			              "&rootTree=${parameters.rootTree?if_exists}&specialized=${parameters.specialized?if_exists}" +
			              "&clearSaveView=N&searchDate=${parameters.searchDate?if_exists?replace("&#47;", "/")}" +
			              "&externalLoginKey=${context.externalLoginKey?if_exists}&fromDelete=N'); return false;";			              
			              			              
		    onclickStr = arrayStr.join();       			
		    form.writeAttribute("onSubmit", onclickStr);
		    	
		    var jar = new CookieJar({path : "/"});
		    activeLinkWorkEffortViewManagementTabMenu = jar.get("activeLinkWorkEffortViewManagementTabMenu");
		    if (activeLinkWorkEffortViewManagementTabMenu == null) {
		    	jar.put("activeLinkWorkEffortViewManagementTabMenu", WorkEffortAssocExtViewList.activeLinkWorkEffortViewManagementTabMenu);
		    }
		}
    },
    
    cleanActiveLink : function (e) {
        var jar = new CookieJar({path : "/"});
        /*
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] Before: WorkEffortAssocExtViewList.activeLinkWorkEffortViewManagementTabMenu ........... " + WorkEffortAssocExtViewList.activeLinkWorkEffortViewManagementTabMenu);
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] Before: WorkEffortAssocExtViewList.activeLinkWorkEffortViewStandardManagementTabMenu ... " + WorkEffortAssocExtViewList.activeLinkWorkEffortViewStandardManagementTabMenu);
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] Before: activeLinkWorkEffortViewManagementTabMenu in cookie ........... " + jar.get('activeLinkWorkEffortViewManagementTabMenu'));
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] Before: activeLinkWorkEffortViewStandardManagementTabMenu in cookie ... " + jar.get('activeLinkWorkEffortViewStandardManagementTabMenu'));
        */
    	WorkEffortAssocExtViewList.activeLinkWorkEffortViewManagementTabMenu = "";
    	// Fix GN-5202
    	WorkEffortAssocExtViewList.activeLinkWorkEffortViewStandardManagementTabMenu = "";
    	jar.put("activeLinkWorkEffortViewManagementTabMenu", "");
    	// Fix GN-5202
		jar.put("activeLinkWorkEffortViewStandardManagementTabMenu", "");
		/*
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] After: WorkEffortAssocExtViewList.activeLinkWorkEffortViewManagementTabMenu ........... " + WorkEffortAssocExtViewList.activeLinkWorkEffortViewManagementTabMenu);
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] After: WorkEffortAssocExtViewList.activeLinkWorkEffortViewStandardManagementTabMenu ... " + WorkEffortAssocExtViewList.activeLinkWorkEffortViewStandardManagementTabMenu);
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] After: activeLinkWorkEffortViewManagementTabMenu in cookie ........... " + jar.get('activeLinkWorkEffortViewManagementTabMenu'));
        console.log("[WorkEffortAssocExtViewValue-list-extension.js.ftl::cleanActiveLink] After: activeLinkWorkEffortViewStandardManagementTabMenu in cookie ... " + jar.get('activeLinkWorkEffortViewStandardManagementTabMenu'));
		*/
    },
	
	isToCloneWorkEffortFromChildTemplate : function(form) {
		var ret = false;
		
        var table = form.down("table.multi-editable");
        var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
        var rowIndex = TableKit.getRowIndex(selectedRow);

		var relationTitle = WorkEffortAssocExtViewList.getRelationTitle(form, rowIndex);
		if (relationTitle == 'to') {
		    var childTemplateIdFrom = '';
		    
	        var childTemplateIdFromField = $(form.down("input[name=childTemplateIdFrom_o_" + rowIndex + "]"));
            if (Object.isElement(childTemplateIdFromField) && childTemplateIdFromField.getValue()) {
    			childTemplateIdFrom = childTemplateIdFromField.getValue();
		    }else {
		    	childTemplateIdFromField = $(form.down("input[name=childTemplateId_workEffortIdFrom_o_" + rowIndex + "]"));
		    	if (Object.isElement(childTemplateIdFromField)) {
        			childTemplateIdFrom = childTemplateIdFromField.getValue();
			    }
		    }
			
			if (childTemplateIdFrom != '') {
			    ret = true;
			}
		}		

	    return ret;
	},
    
    cloneForm : function(originalForm, fields){
        var newFormId = originalForm.readAttribute("id") + "_clone";
        var form = new Element("form", {"id" : newFormId, "name" : newFormId});

        var onclickStr = originalForm.readAttribute("onSubmit");
        var request = onclickStr.substring(onclickStr.indexOf("/"), onclickStr.lastIndexOf(","));
        form.action = request;
        var parameters = onclickStr.substring(onclickStr.lastIndexOf(",")+ 1, onclickStr.indexOf("\')"));
        if (parameters && Object.isString(parameters)) {
            var parametersMap = $H(parameters.toQueryParams());
            if (parametersMap) {
                parametersMap.each(function(pair) {
                    if(!(fields && fields.size() > 0 && fields.include(pair.key))) {
                        var field = form.getInputs("hidden", pair.key).first();
                        if (field) {
                            field.writeAttribute("value", pair.value);
                        } else {
                            field = new Element("input", { "type": "hidden", "name": pair.key, "value": pair.value });
                            form.insert(field);
                        }
                    }
                });
            }
        }
        document.body.insert(form);
        return form;
    },
    
    getContainer : function(originalForm){
        var onclickStr = originalForm.readAttribute("onSubmit");
        var parameters = onclickStr.split(",");
        return parameters[2].substring(parameters[2].indexOf("\'")+1)
    },
    
    cloneSelectedRow : function(originalForm, form, fields) {
        if(fields) {
            fields.each(function(element) {
                var name = element.readAttribute("name");
                if (name.indexOf("_o_") != -1) {
                    name = name.substring(0, name.indexOf("_o_"));
                }
                var field = form.getElements().find(function(elementInNewForm) {
                    return elementInNewForm.readAttribute("name") === name;
                });
                
                if (Object.isElement(field)) {
                    field.setValue(element.getValue());
                } else {                
                    field = element.cloneNode(true);
                    
                    field.writeAttribute("name", name);
                    form.insert(field);
                }
            });
        }
        var specialeCaseFromOldForm = originalForm.getElements().select(function(element) {
            return ["textarea","select"].include(element.tagName.toLowerCase());
        });
        if (specialeCaseFromOldForm && specialeCaseFromOldForm.size() > 0) {
            var newFormElements = form.getElements();
            specialeCaseFromOldForm.each(function(element) {

                var name = element.readAttribute("name");
                if (name.indexOf("_o_") != -1) {
                    name = name.substring(0, name.indexOf("_o_"));
                }
                var newElement = newFormElements.find(function(elementInNewForm) {
                    return elementInNewForm.readAttribute("name") === name;
                });

                if (newElement) {
                    var name = newElement.readAttribute("name");
                    if (name.indexOf("_o_") != -1) {
                        name = name.substring(0, name.indexOf("_o_"));
                    }
                    newElement.writeAttribute("name", name);
                    newElement.setValue($F(element));
                }
            });
        }
        return form;
    },
    
    preBeforeLoadElaborateContent : function(contentToUpdate) {
        //alert("preBeforeLoadElaborateContent");
        if (contentToUpdate) {
            var contentToUpdate = $(contentToUpdate);
            var tableToUpdate = contentToUpdate.down("table.multi-editable");
            
            var oldTr = TableKit.Selectable.getSelectedRows(tableToUpdate);
            oldTr.each(function(selectedRow){
                var input = selectedRow.down("input[name]");
                if (input) {
                    var name = input.readAttribute("name");
                    if (name.lastIndexOf("_o_")>-1) {
                        var pos = name.lastIndexOf("_");
                        var fieldIdx = name.slice(pos+1);
                        this._selectedIndex = fieldIdx;
                    }
                }
            });
        } 
        return contentToUpdate;
    },
    
    preLoadElaborateContent : function(contentToUpdate, tmpElement) {
        if (contentToUpdate) {
            var contentToUpdate = $(contentToUpdate);
            var tableToUpdate = contentToUpdate.down('table.multi-editable');
            var newTable = tmpElement.down('table.multi-editable');
            if(newTable && tableToUpdate) {
                var lastTableRow = newTable.down('tbody').select('tr').last();

                if (Object.isElement(lastTableRow) && TableKit.isSelectable(tableToUpdate)) {
                    var tbody = tableToUpdate.down('tbody');
                    if(!tbody) {
                        tbody = new Element('tbody');
                        tableToUpdate.insert(tbody);
                    }
                    // la riga non viene inserita coem ultima riga della tabella
                    // tbody.insert(lastTableRow);
                    // lastTableRow è la riga nuova
                    // rowToDelete è la riga da rimuovere
                }
                //elimino la vecchia riga, non posso usare TableKit
                var rows = tableToUpdate.down("tbody").select("tr");
                var rowToDelete = rows.find(function(row) {
                    var input = row.down("input[name]");
                    if (input) {
                        var name = input.readAttribute("name");
                        if (name.lastIndexOf("_o_")>-1) {
                            var pos = name.lastIndexOf("_");
                            var fieldIdx = name.slice(pos+1);
                            //alert("this._selectedIndex: " + this._selectedIndex);
                            return fieldIdx == this._selectedIndex;
                        }
                    }
                }); 
                
                //aggiusto l'indice della riga creata, perchè non è l'ultima
                //var tr = tableToUpdate.down("tbody").select("tr").last();
                if(Object.isElement(lastTableRow)) {
                    var elements = lastTableRow.select("div, input, select, textarea");
                    elements.each( function(el) {
                        var name = el.readAttribute("name");
                        var id = el.readAttribute("id");
                        if(name) {
                            name =WorkEffortAssocExtViewList._changeIndex(name, this._selectedIndex);
                            el.writeAttribute("name", name);
                        }
                        if(id) {
                            id = WorkEffortAssocExtViewList._changeIndex(id, this._selectedIndex);
                            el.writeAttribute("id", id);
                        }
                        if (el.hasClassName("lookup_field")) {
                            WorkEffortAssocExtViewList._reindexNewAutocompleterElement(el, this._selectedIndex);
                        }
                        if (el.hasClassName("droplist_field")) {
                            WorkEffortAssocExtViewList._reindexNewAutocompleterElement(el, this._selectedIndex);
                        }
                    });
                }
                
                if(Object.isElement(rowToDelete)) {
                    var row = TableKit.getRowIndex(lastTableRow);
                    if(rowToDelete.down('input[name=operation_o_' + this._selectedIndex + ']') && 'UPDATE' === rowToDelete.down('input[name=operation_o_' + this._selectedIndex + ']').getValue()){
                        if(Object.isElement(lastTableRow.down('input[name=operation_o_' + this._selectedIndex + ']'))){
                            lastTableRow.down('input[name=operation_o_' + this._selectedIndex + ']').setValue("UPDATE");
                        }else{
                            var field = new Element("input", { "type": "hidden", "name": "operation_o_" + this._selectedIndex, "value": "UPDATE" });
                            $(lastTableRow.up('form')).insert(field);
                        }
                    }
                    rowToDelete.replace(lastTableRow);
                }
                tableToUpdate.multi_editable_insert = true;
                return tableToUpdate;
            }
        }
        return null;
    },
    
    postLoadElaborateContent: function(contentToUpdate, tmpElement) {
        var table = null;
        if (tmpElement) {
            if(tmpElement.tagName === 'TABLE') {
                table = tmpElement;
                Object.extend(table, {
                    'proceed' : false
                });
            }
            else {
                if(tmpElement.down('table.basic-table')){
                    table = tmpElement.down('table.basic-table');
                }
            }
        }
        if(Object.isElement(table)) {
            TableKit.refreshBodyRows(table);
        }
        RegisterManagementMenu.postLoadElaborateContent(contentToUpdate, tmpElement);
        
        // riporta la selezione sulla riga modificata e sistema gli stili della riga
        if (table) {
            if (TableKit.isSelectable(table)) {
                //TableKit.reload(contentToUpdate, false);
                var updateTableRow = TableKit.getRow(table, this._selectedIndex);
                TableKit.Selectable._toggleSelection(updateTableRow, table);
                
                var index = TableKit.getRowIndex(updateTableRow);
                if(index % 2 == 1) {
                    updateTableRow.addClassName("alternate-row");
                }
                
                var cell = updateTableRow.down('td');
                $(cell).fire('dom:mousedown');
            }
        }
        return table;
    },
    
    submitInsertForm: function(insertModeAnchor, contextForm, withOptions) {
        var formId = insertModeAnchor.identify() + '_form';
        var form = new Element('form', Object.extend({'id' : formId, 'name' : formId, 'target' : null}, {}));
        document.body.insert(form);

        if (contextForm) {
            contextForm = $(contextForm);
            var link = contextForm.down("a.management-insertmode-button");
            if (link) {
                var formInsertManagement = new FormInsertManagement(link.readAttribute('onclick'), form);
                var argument = formInsertManagement.argument;
                var container = formInsertManagement.container;                  	
                if (argument.indexOf('WorkEffortView') >= 0 && argument.indexOf(WorkEffortAssocExtViewList.relationTitle) >= 0) {
                    argument = argument.replace('WorkEffortView', 'WorkEffortAssocExtView');
                }
                if (container.indexOf('WorkEffortView') >= 0 && container.indexOf(WorkEffortAssocExtViewList.relationTitle) >= 0) {
                    container = container.replace('WorkEffortView', 'WorkEffortAssocExtView');
                }
                    	
                var parametersMap = formInsertManagement.parametersMap;
                var filteredElementList = $A(contextForm.getElements());
                var elements = Form.serializeElements(filteredElementList);
                var queryParams = $H({}).merge(elements.toQueryParams());
                if (queryParams) {
                    $A(queryParams.each(function(pair) {
                        var findElement = $A(form.getElements()).find(function(elm) {
                            return pair.key === elm.readAttribute("name");
                        });
                        if (!findElement) {
                            field = new Element('input', { 'type': 'hidden', 'name': pair.key, 'value': pair.value });
                            form.insert(field);
                        } else {
                            findElement.writeAttribute('value', pair.value);
                        }
                    }));
                }
                if (withOptions) {
                    ajaxSubmitFormUpdateAreas(form.identify(), container ,argument, {preLoadElaborateContent : RegisterManagementMenu.preLoadElaborateContent, postLoadElaborateContent : RegisterManagementMenu.postLoadElaborateContent}); 
                } else {
                    ajaxSubmitFormUpdateAreas(form.identify(), container ,argument, {}); 
                }
            }
            form.remove();
        }                    	
    },
    
    insertFromCatalog: function() {
        var insertModeLink = $(document.body).down(".management-insertmode");
        var insertModeAnchor = insertModeLink.down("a");
        var contextForm = $$('#contextManagementSearchForm-WorkEffortAssocExtView-' + WorkEffortAssocExtViewList.relationTitle)[0];
	    WorkEffortAssocExtViewList.submitInsertForm(insertModeAnchor, contextForm, true);
    },
    
    insertDirect: function() {
        var insertModeLink = $(document.body).down(".management-insertmode");
        var insertModeAnchor = insertModeLink.down("a");
        var contextForm = $$('#contextManagementSearchForm-WorkEffortView')[0];
        if (!Object.isElement(contextForm)) {
            contextForm = $$('#contextManagementSearchForm-WorkEffortView-' +  WorkEffortAssocExtViewList.relationTitle)[0];
        }
        if (!Object.isElement(contextForm)) {
            var suffix = WorkEffortAssocExtViewList.relationTitle && WorkEffortAssocExtViewList.relationTitle.indexOf('From') > 0 ? 'From' : 'To';
            contextForm = $$('#contextManagementSearchForm-WorkEffortView-' + suffix)[0];
        }        
	    WorkEffortAssocExtViewList.submitInsertForm(insertModeAnchor, contextForm, false);
    },
    
    _changeIndex: function(attr, newIdx, global) {
        if (global)
            return attr.replace(/(_o_[0-9]+)/g, "_o_" + newIdx);
        return attr.replace(/(_o_[0-9]+)/, "_o_" + newIdx);
    },
    
    _reindexNewAutocompleterElement: function(element, newIdx) {
        var constraintFieldsElement = element.select("input.autocompleter_parameter[name=\"constraintFields\"]");
        if (constraintFieldsElement.size() > 0) {
        
            constraintFieldsElement.each(function(constraintField) {
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
    
    getValue : function(inputField) {
	    var valueStr = "";
		if(Object.isElement(inputField)) {
			valueStr = inputField.getValue();
		}
		if(valueStr.indexOf("/") > 0) {
			valueStr = valueStr.substring(0, valueStr.indexOf("/"));
		}
		return valueStr;
	},
	
	setValue : function(inputField, valueFiled) {
		valueStr = "";
		if(Object.isElement(inputField)) {
			valueStr = inputField.getValue();
		}
		if(valueStr.indexOf("/") > 0) {
			valueStr = valueStr.substring(valueStr.indexOf("/"));
		}
		inputField.setValue(valueFiled + valueStr);
	}
}

WorkEffortAssocExtViewList.load();