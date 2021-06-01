PartyRelationshipList = {
    load: function(newContentToExplore, withoutResponder) {
        var formToExplore = Object.isElement(newContentToExplore) ? newContentToExplore.up('form') : null;
        if(!formToExplore) {
            var formsToExplore = $$('form');
            formsToExplore.each(function(form) {
                if (form.identify() == 'PRFMM001_PartyRelationship' || form.identify() =='PRTOMM001_PartyRelationship' || form.identify() == 'PRFM001_PartyRelationship' || form.identify() =='PRTOM001_PartyRelationship') {
                    PartyRelationshipList.loadForm(form);
                }
            });
        }
        else
            PartyRelationshipList.loadForm(formToExplore);
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(PartyRelationshipList.responder, 'PartyRelationshipList');
        }
    },

    responder : {
                onLoad : function(newContent) {
                    PartyRelationshipList.load(newContent, true);
                },
                unLoad : function() {
                    //TODO Prevedere eventuali deregistrazioni per liberare memoria
                    return typeof 'PartyRelationshipList' === 'undefined';
                }
    },

    loadForm : function(form) {
        if (form) {
            //TableKit.registerObserver($('table_'+form.identify()), 'onDblClickSelectEnd', PartyRelationshipList.openManagement);
            var formName = form.readAttribute('name');

            // in ascolto sul cambiamento della droplist del partyRelationshipTypeId
            var dropLists = form.select('div').findAll(function(element) {
                if(element.readAttribute('id')) {
                    return element.readAttribute('id').indexOf(formName + '_partyRelationshipTypeId') > -1;
                }
            });
            if(dropLists) {
                dropLists.each(function(element) {
                    var dropList = DropListMgr.getDropList(element.readAttribute('id'));
                    if (dropList) {
                        //dropList.deregisterOnChangeListener(PartyRelationshipList.partyRelationshipTypeIdChanged.bind(dropList));
                        dropList.registerOnChangeListener(PartyRelationshipList.partyRelationshipTypeIdChanged.bind(dropList), "PartyRelationshipList.partyRelationshipTypeIdChanged");
                    }
                });
            }
            PartyRelationshipList.setValueUomIdDecimalDigits(form);
            // in ascolto sul cambiamento della lookup del valueUomId
            var divLookupField = form.down("div#" + formName + "_valueUomId");
            //se registro sulla lookup non risalgo al valore restituito ->
            //registro sul field
            if(divLookupField) {
                var lookup = LookupMgr.getLookup(divLookupField.identify());
                if (lookup) {
                    lookup.registerOnSetInputFieldValue(PartyRelationshipList.setValueUomIdDecimalDigits.curry(form), 'PartyRelationshipList_setValueUomIdDecimalDigits');
                }
            }
        }
    },
    setValueUomIdDecimalDigits : function(form){
        var decimalScaleField = $(form.down("input.decimalScale"));
        var valueUomIdField = $(form.down("input.mask_field_decimalScale"));
        if(decimalScaleField && valueUomIdField)
        	valueUomIdField.setDecimalDigits(decimalScaleField);
    },
        
    cloneForm : function(originalForm, fields){
        var newFormId = originalForm.readAttribute('id') + '_clone';
        var form = new Element('form', {'id' : newFormId, 'name' : newFormId});

        var onclickStr = originalForm.readAttribute('onSubmit');
        var request = onclickStr.substring(onclickStr.indexOf('/'), onclickStr.lastIndexOf(','));
        form.action = request;
        var parameters = onclickStr.substring(onclickStr.lastIndexOf(',')+ 1, onclickStr.indexOf('\')'));
        if (parameters && Object.isString(parameters)) {
            var parametersMap = $H(parameters.toQueryParams());
            if (parametersMap) {
                parametersMap.each(function(pair) {
                    if(!(fields && fields.size() > 0 && fields.include(pair.key))) {
                        var field = form.getInputs('hidden', pair.key).first();
                        if (field) {
                            field.writeAttribute('value', pair.value);
                        } else {
                            field = new Element('input', { 'type': 'hidden', 'name': pair.key, 'value': pair.value });
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
        var onclickStr = originalForm.readAttribute('onSubmit');
        var parameters = onclickStr.split(',');
        return parameters[2].substring(parameters[2].indexOf('\'')+1)
    },

    cloneSelectedRow : function(originalForm, form, fields) {
        if(fields) {
            fields.each(function(element) {
                var name = element.readAttribute('name');
				if (name.indexOf('_o_') != -1) {
                    name = name.substring(0, name.indexOf('_o_'));
                }
                var field = form.getElements().find(function(elementInNewForm) {
                    return elementInNewForm.readAttribute('name') === name;
                });
				
				if (Object.isElement(field)) {
					field.setValue(element.getValue());
				} else {                
	                field = element.cloneNode(true);
	                
	                field.writeAttribute('name', name);
                	form.insert(field);
                }
            });
        }
        var specialeCaseFromOldForm = originalForm.getElements().select(function(element) {
            return ['textarea','select'].include(element.tagName.toLowerCase());
        });
        if (specialeCaseFromOldForm && specialeCaseFromOldForm.size() > 0) {
            var newFormElements = form.getElements();
            specialeCaseFromOldForm.each(function(element) {

                var name = element.readAttribute('name');
                if (name.indexOf('_o_') != -1) {
                    name = name.substring(0, name.indexOf('_o_'));
                }
                var newElement = newFormElements.find(function(elementInNewForm) {
                    return elementInNewForm.readAttribute('name') === name;
                });

                if (newElement) {
                    var name = newElement.readAttribute('name');
                    if (name.indexOf('_o_') != -1) {
                        name = name.substring(0, name.indexOf('_o_'));
                    }
                    newElement.writeAttribute('name', name);
                    newElement.setValue($F(element));
                }
            });
        }
        return form;
    },

    partyRelationshipTypeIdChanged: function(){
        var originalForm = this._formContainer;
        var formCloned = PartyRelationshipList.cloneForm(originalForm);
        var field = new Element('input', { 'type': 'hidden', 'name': 'insertMode', 'value': 'W' });
        formCloned.insert(field);
        field = new Element('input', { 'type': 'hidden', 'name': 'operation', 'value': 'CREATE' });
        formCloned.insert(field);
        if (!Object.isElement(formCloned.down('#justRegisters'))) {
            var newElement = new Element('input', {'type':'hidden', 'id': 'justRegisters', 'name':'justRegisters', 'value':'Y', 'class':'auxiliary-parameters'});
            formCloned.insert(newElement);
        }
        var container = PartyRelationshipList.getContainer(originalForm);
        var table = originalForm.down('table.multi-editable');
        if (table && TableKit.isSelectable(table)) {
            var selectedRows = TableKit.Selectable.getSelectedRows(table);
            if(selectedRows) {
                var selectedRow = selectedRows.first();
                var fields = $A($(selectedRow).select('input','select','textarea')).select(function(element) {
                    var name = element.readAttribute('name');
                    if (name.indexOf('_o_') != -1) {
                        name = name.substring(0, name.indexOf('_o_'));
                    }
                    return !['subFolder', 'folderIndex', 'insertMode', 'operation', 'entityName', 'parentEntityName', 'successCode', 'partyIdFrom', 'roleTypeIdFrom', 'roleTypeIdTo', 'partyIdTo'].include(name)
                });
             }
             formCloned = PartyRelationshipList.cloneSelectedRow(originalForm, formCloned, fields);
             ajaxSubmitFormUpdateAreas(formCloned.identify(), container,'' , {preBeforeLoadElaborateContent : PartyRelationshipList.preBeforeLoadElaborateContent, preLoadElaborateContent : PartyRelationshipList.preLoadElaborateContent, postLoadElaborateContent : RegisterManagementMenu.postLoadElaborateContent});
        }
         else {
            var fields = $A($(originalForm.getElements())).select(function(element) {
                var name = element.readAttribute('name');
                if (name.indexOf('_o_') != -1) {
                    name = name.substring(0, name.indexOf('_o_'));
                }
                return !['subFolder', 'folderIndex', 'insertMode', 'operation', 'entityName', 'parentEntityName', 'successCode', 'partyIdFrom', 'roleTypeIdFrom', 'roleTypeIdTo', 'partyIdTo'].include(name)
            });
            formCloned = PartyRelationshipList.cloneSelectedRow(originalForm, formCloned, fields);
            ajaxSubmitFormUpdateAreas(formCloned.identify(), container,'');
        }     
        formCloned.remove();
    },
    
    preBeforeLoadElaborateContent : function(contentToUpdate) {
    	if (contentToUpdate) {
            contentToUpdate = $(contentToUpdate);
            tableToUpdate = contentToUpdate.down('table.multi-editable');
            
            var oldTr = TableKit.Selectable.getSelectedRows(tableToUpdate);
            oldTr.each(function(selectedRow){
                // in ascolto sul cambiamento della droplist del partyRelationshipTypeId
                var dropLists = selectedRow.select('div').findAll(function(element) {
                    if(element.readAttribute('id')) {
                        return element.readAttribute('id').indexOf('_partyRelationshipTypeId') > -1;
                    }
                });
                if(dropLists) {
                    dropLists.each(function(element) {
                        var dropList = DropListMgr.getDropList(element.readAttribute('id'));
                        if (dropList) {
                            dropList.deregisterOnChangeListener(PartyRelationshipList.partyRelationshipTypeIdChanged.bind(dropList));
                            //dropList.registerOnChangeListener(PartyRelationshipList.partyRelationshipTypeIdChanged.bind(dropList), "PartyRelationshipList.partyRelationshipTypeIdChanged");
                        }
                    });
                }
                var divLookupField = selectedRow.down("div.lookup_field");
                //se registro sulla lookup non risalgo al valore restituito ->
                //registro sul field
                if(divLookupField) {
                    var lookup = LookupMgr.getLookup(divLookupField.identify());
                    if (lookup) {
                        lookup.deregisterOnSetInputFieldValue(PartyRelationshipList.setValueUomIdDecimalDigits, 'PartyRelationshipList_setValueUomIdDecimalDigits');
                    }
                }
        		
        		//Memorizzo l'indice della riga selezionata
        		var input = selectedRow.down("input[name]");
	            if (input) {
	                var name = input.readAttribute("name");
	                if (name.lastIndexOf("_o_")>-1) {
	                    var pos = name.lastIndexOf("_");
	                    var fieldIdx = name.slice(pos+1);
	                    this._selectedIndex = fieldIdx; 
	                }
	            }
                //selectedRow.remove();
            });
        }
        return contentToUpdate;
    },
    
    preLoadElaborateContent: function(contentToUpdate, tmpElement) {
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
                    tbody.insert(lastTableRow);
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
		                    return fieldIdx == this._selectedIndex;
		                }
		            }
                }); 
                if(Object.isElement(rowToDelete)) {
                	rowToDelete.remove();
                }
                
                //aggiusto l'indice della riga creata, è l'ultima
                var tr = tableToUpdate.down("tbody").select("tr").last(); 
                if(Object.isElement(tr)) {
                	var elements = tr.select("div, input, select, textarea");
                	elements.each( function(el) {
                		var name = el.readAttribute("name");
                		var id = el.readAttribute("id");
                		if(name) {
                			name = PartyRelationshipList._changeIndex(name, this._selectedIndex);
                			el.writeAttribute("name", name);
                		}
                		if(id) {
                			id = PartyRelationshipList._changeIndex(id, this._selectedIndex);
                			el.writeAttribute("id", id);
                		}
                		if (el.hasClassName("lookup_field")) {
		                    PartyRelationshipList._reindexNewAutocompleterElement(el, this._selectedIndex);
		                }
		                if (el.hasClassName("droplist_field")) {
		                    PartyRelationshipList._reindexNewAutocompleterElement(el, this._selectedIndex);
		                }
                	});
                }
                tableToUpdate.multi_editable_insert = true;
                return tableToUpdate;
            }
        }
        return null;
    },

    postLoadElaborateContent : function(contentToUpdate, tmpElement) {
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
    	return RegisterManagementMenu.postLoadElaborateContent(contentToUpdate, tmpElement);
    },

   <#-- postLoadElaborateContent : function(contentToUpdate, tmpElement) {
        // tmpElement è un div se nuova riga della ricerca
        //            è una table negli altri casi
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
    },
    /**
     * New element's input field have to be reindexed
     */
    _reindexNewElement: function(currentContent, newElement, replace) {
        var trArray = currentContent.select("tr");
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
            if(!replace) {
                idx = idx + 1;
            }
            var elArray = newElement.select("div, input, select, textarea");
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
            }.bind(this));
        }
    }, -->

    _changeIndex: function(attr, newIdx) {
        var newAttr = attr.replace(/(_o_[0-9]+)/g, "_o_" + newIdx);
        return newAttr;
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
    }
}

<#if !parameters.justRegisters?has_content>
PartyRelationshipList.load();
</#if>
