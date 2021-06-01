
WorkEffortTransactionViewMulti = {
	load: function() {
		WorkEffortTransactionViewMulti.loadTable($("table_WETVMM001${accountTypeEnumId?if_exists}_WorkEffortTransactionView"));
	},
	loadTable: function(table) {
		if(Object.isElement(table) && (typeof TableKit != "undefined")) {
			if (TableKit.isSelectable(table)) {
				WorkEffortTransactionViewMulti.manageSelectedRow(table);
                TableKit.registerObserver(table, 'onSelectEnd', 'WorkEffortTransactionViewMulti', function(table, e) {
                    WorkEffortTransactionViewMulti.manageSelectedRow(table);
                });
			}
		}
	},
	
	
	manageSelectedRow: function(table){
		var form = table.up("form");
		var formName = form.readAttribute("name");
			
		var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
        	var rowIndex = TableKit.getRowIndex(selectedRow);        	
			var dropListId = formName + "_weTransMeasureId_o_" + rowIndex;
			var measureDropList = DropListMgr.getDropList(dropListId);
			if(measureDropList) {
				measureDropList.registerOnChangeListener(WorkEffortTransactionViewMulti.measureDropListHandler.curry(measureDropList._field), "measureDropListHandlerMulti_" + rowIndex);
			}
			
			/***
			* Controllo per la gestione della calcellazione in base al valModId			
			*/
			WorkEffortTransactionViewMulti.disabledDelete(selectedRow, formName, rowIndex);
		}
	},
	
	classNameProperty : 'disabledByRowSelectedValMod',
	arrayAnchor : false,
	
	disabledDelete: function(selectedRow, formName, rowIndex) {
		
		var valModId = selectedRow.down("input[name='valModId_o_" + rowIndex + "']");
		var weTransTypeValueId =selectedRow.down("input[name='weTransTypeValueId_o_" + rowIndex + "']");
		var isRowReadOnlyWorkEffortMeasure = selectedRow.down("input[name='isRowReadOnlyWorkEffortMeasure_o_" + rowIndex + "']");
		if (Object.isElement(valModId) && Object.isElement(isRowReadOnlyWorkEffortMeasure)) {
			/**
			* Devo disabilitare la cancellazione
			*/
			var toolbarSelected = $(document.body).down('div.screenlet-title-bar');
			var icons = $(toolbarSelected).select('li.delete', 'li.delete-disabled');
			
			var valModIdValue = valModId.getValue();
			var isRowReadOnlyWorkEffortMeasureValue = isRowReadOnlyWorkEffortMeasure.getValue();
			
			if (isRowReadOnlyWorkEffortMeasureValue == 'true' || valModIdValue == 'ALL_NOT_MOD' || (valModIdValue == 'ACTUAL_NOT_MOD' && weTransTypeValueId.getValue() == 'ACTUAL') || (valModIdValue == 'BUDGET_NOT_MOD' && weTransTypeValueId.getValue() == 'BUDGET')) {
				WorkEffortTransactionViewMulti.disabledButton(icons);
			} else {
				WorkEffortTransactionViewMulti.activedButton(icons);
			}
			
		}
	},
	
	disabledButton: function(icons) {
		if (Object.isArray(icons)) {
        	icons.each(function(element, index){
        		element.addClassName(WorkEffortTransactionViewMulti.classNameProperty);
        		element.className = element.className.replace('management-delete delete', 'delete-disabled disabled ');
        		if(!Object.isArray(WorkEffortTransactionViewMulti.arrayAnchor)){
        			WorkEffortTransactionViewMulti.arrayAnchor = $A();
        		} 
        		
        		if (Object.isElement(element.down('a'))) {
    		        WorkEffortTransactionViewMulti.arrayAnchor[index] = element.down('a').remove();
    		    }
    		    if (Object.isElement(WorkEffortTransactionViewMulti.arrayAnchor[index])) {
                   element.innerHTML = WorkEffortTransactionViewMulti.arrayAnchor[index].innerHTML;
                }
                
        	});
        }
	},
	
	activedButton: function(icons) {
		if (Object.isArray(icons)) {
			icons.each(function(element, index){
			
				if(element.hasClassName(WorkEffortTransactionViewMulti.classNameProperty)){
					element.removeClassName(WorkEffortTransactionViewMulti.classNameProperty);
					element.removeClassName('disabled');
        			element.className = element.className.replace('delete-disabled', 'management-delete delete');
        			
        			if(!Object.isArray(WorkEffortTransactionViewMulti.arrayAnchor)){
            			WorkEffortTransactionViewMulti.arrayAnchor = $A();
            		} 
        			if (Object.isElement(WorkEffortTransactionViewMulti.arrayAnchor[index])) {
                        element.innerHTML = '';
                        element.insert(WorkEffortTransactionViewMulti.arrayAnchor[index]);
        			}
        		}
			});
        }
	},
	
	measureLookupHandler: function(measureLookup, rowIndex) {
		var weMeasureUomType = $(measureLookup.down("input#weMeasureUomType_weTransMeasureId_o_" + rowIndex));
		
		if(Object.isElement(weMeasureUomType)) {
			var weMeasureUomTypeValue = weMeasureUomType.getValue();
			
			var weMeasureAccountCode = $(measureLookup.down("input#weMeasureAccountCode_weTransMeasureId_o_" + rowIndex));
			var weMeasureAccountDesc = $(measureLookup.down("input#weMeasureAccountDesc_weTransMeasureId_o_" + rowIndex));
			//var weMeasureDefaultUomDesc = $(measureLookup.down("input#weMeasureAccountDesc_weTransMeasureId_o_" + rowIndex));
			
			var originalForm = measureLookup.up("form");
			var formCloned = WorkEffortTransactionViewMulti.cloneForm(originalForm);
			var field = new Element("input", { "type": "hidden", "name": "operation", "value": "CREATE" });
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "insertMode", "value": "W" });
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "weMeasureUomType", "value": weMeasureUomTypeValue });
        	formCloned.insert(field);
        	if(Object.isElement(weMeasureAccountCode) && Object.isElement(weMeasureAccountDesc)) {
        		field = new Element("input", { "type": "hidden", "name": "weTransAccountCode", "value": weMeasureAccountCode.getValue() });
        		formCloned.insert(field);
        		field = new Element("input", { "type": "hidden", "name": "weTransAccountDesc", "value": weMeasureAccountDesc.getValue() });
        		formCloned.insert(field);
        	}
        	var container = WorkEffortTransactionViewMulti.getContainer(originalForm);
        	var table = originalForm.down("table.multi-editable");
	        if (table && TableKit.isSelectable(table)) {
	            var selectedRows = TableKit.Selectable.getSelectedRows(table);
	            if(selectedRows) {
	                var selectedRow = selectedRows.first();
	                var fields = $A($(selectedRow).select("input","select","textarea")).select(function(element) {
	                    var name = element.readAttribute("name");
	                    if (name.indexOf("_o_") != -1) {
	                        name = name.substring(0, name.indexOf("_o_"));
	                    }
	                    return !["subFolder", "folderIndex", "insertMode", "operation", "entityName", "parentEntityName", "successCode", "workEffortId", "weTransWeId", "weTransValue"].include(name)
	                });
	             }
	             formCloned = WorkEffortTransactionViewMulti.cloneSelectedRow(originalForm, formCloned, fields);
	             ajaxSubmitFormUpdateAreas(formCloned.identify(), container,'' , {preBeforeLoadElaborateContent : WorkEffortTransactionViewMulti.preBeforeLoadElaborateContent, preLoadElaborateContent : WorkEffortTransactionViewMulti.preLoadElaborateContent, postLoadElaborateContent : WorkEffortTransactionViewMulti.postLoadElaborateContent});
	        }
	        else {
	        	var fields = $A($(originalForm.getElements())).select(function(element) {
	            	var name = element.readAttribute("name");
	                if (name.indexOf("_o_") != -1) {
	                    name = name.substring(0, name.indexOf("_o_"));
	                }
	                return !["subFolder", "folderIndex", "insertMode", "operation", "entityName", "parentEntityName", "successCode", "workEffortId", "weTransWeId", "weTransValue"].include(name)
	            });
	            formCloned = WorkEffortTransactionViewMulti.cloneSelectedRow(originalForm, formCloned, fields);
	            ajaxSubmitFormUpdateAreas(formCloned.identify(), container,'');
	        }     
        	formCloned.remove();
		}
	},
	
	measureDropListHandler : function(droplistDiv, span1, span2, span3, span4, span5, span6, span7, span8, span9, span10, span11, span12, span13, span14, span15) {
		var array = new Array(span1, span2, span3, span4, span5, span6, span7, span8, span9, span10, span11, span12, span13, span14, span15);
		var weMeasureUomType;
		var weMeasureUomDecimalScale;
		var weMeasurePeriodTypeId;
		var weMeasureId;
		var weMeasureProductId;
		array.each(function(span) {
			if (Object.isElement(span)) {
				var infoList = span.innerHTML.split(":");
				if (Object.isArray(infoList)) {
					var finished = 0;
					for (var i = 0; i < infoList.size(); i++) {
						if (infoList[i] === "weMeasureUomType") {
							weMeasureUomType = infoList[i + 1];
							finished = finished +1 ;
						}
						if (infoList[i] === "weMeasureUomDecimalScale") {
							//alert("weMeasureUomDecimalScale i : " + infoList[i + 1]);
							weMeasureUomDecimalScale = infoList[i + 1];
							finished = finished +1 ;
						}
						if (infoList[i] === "weMeasurePeriodTypeId") {
							weMeasurePeriodTypeId = infoList[i + 1];
							finished = finished +1 ;
						}
						if (infoList[i] === "weMeasureProductId") {
							weMeasureProductId = infoList[i + 1];
							finished = finished +1 ;
						}
						if (infoList[i] === "weMeasureId") {
                            //alert("weMeasureUomDecimalScale i : " + infoList[i + 1]);
                            weMeasureId = infoList[i + 1];
                            finished = finished +1 ;
                        }
                    }
				}
			}
		});
		if(weMeasureUomType) {
			//alert("weMeasureUomDecimalScale: " + weMeasureUomDecimalScale);
			var originalForm = droplistDiv.up("form");
			var formCloned = WorkEffortTransactionViewMulti.cloneForm(originalForm);
			var field = new Element("input", { "type": "hidden", "name": "operation", "value": "UPDATE" }); //operation obbligatori ma cosi si distingue il caso dell'update dal create
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "insertMode", "value": "W" });
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "weMeasureUomType", "value": weMeasureUomType });
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "weMeasureUomDecimalScale", "value": weMeasureUomDecimalScale });
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "weMeasurePeriodTypeId", "value": weMeasurePeriodTypeId });
        	formCloned.insert(field);
        	field = new Element("input", { "type": "hidden", "name": "weMeasureId", "value": weMeasureId });
            formCloned.insert(field);
            if(weMeasureProductId) {
            	field = new Element("input", { "type": "hidden", "name": "weMeasureProductId", "value": weMeasureProductId });
            	formCloned.insert(field);
            }
        	
        	var container = WorkEffortTransactionViewMulti.getContainer(originalForm);
        	var table = originalForm.down("table.multi-editable");
	        if (table && TableKit.isSelectable(table)) {
	            var selectedRows = TableKit.Selectable.getSelectedRows(table);
	            if(selectedRows) {
	                var selectedRow = selectedRows.first();
	                var fields = $A($(selectedRow).select("input","select","textarea")).select(function(element) {
	                    var name = element.readAttribute("name");
	                    if (name.indexOf("_o_") != -1) {
	                        name = name.substring(0, name.indexOf("_o_"));
	                    }
	                    return !["subFolder", "folderIndex", "insertMode", "operation", "entityName", "parentEntityName", "successCode", "workEffortId", "weTransWeId", "weTransValue"].include(name)
	                });
	             }
	             formCloned = WorkEffortTransactionViewMulti.cloneSelectedRow(originalForm, formCloned, fields);
	             ajaxSubmitFormUpdateAreas(formCloned.identify(), container,'' , {preBeforeLoadElaborateContent : WorkEffortTransactionViewMulti.preBeforeLoadElaborateContent, preLoadElaborateContent : WorkEffortTransactionViewMulti.preLoadElaborateContent, postLoadElaborateContent : WorkEffortTransactionViewMulti.postLoadElaborateContent});
	        }
	        else {
	        	var fields = $A($(originalForm.getElements())).select(function(element) {
	            	var name = element.readAttribute("name");
	                if (name.indexOf("_o_") != -1) {
	                    name = name.substring(0, name.indexOf("_o_"));
	                }
	                return !["subFolder", "folderIndex", "insertMode", "operation", "entityName", "parentEntityName", "successCode", "workEffortId", "weTransWeId", "weTransValue"].include(name)
	            });
	            formCloned = WorkEffortTransactionViewMulti.cloneSelectedRow(originalForm, formCloned, fields);
	            ajaxSubmitFormUpdateAreas(formCloned.identify(), container,'');
	        }     
        	formCloned.remove();
		}
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
    	//alert("preLoadElaborateContent");
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
                			name =WorkEffortTransactionViewMulti._changeIndex(name, this._selectedIndex);
                			el.writeAttribute("name", name);
                		}
                		if(id) {
                			id = WorkEffortTransactionViewMulti._changeIndex(id, this._selectedIndex);
                			el.writeAttribute("id", id);
                		}
                		if (el.hasClassName("lookup_field")) {
		                    WorkEffortTransactionViewMulti._reindexNewAutocompleterElement(el, this._selectedIndex);
		                }
		                if (el.hasClassName("droplist_field")) {
		                    WorkEffortTransactionViewMulti._reindexNewAutocompleterElement(el, this._selectedIndex);
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
    
    setValueUomIdDecimalDigits : function(tr){
    	var decimalScaleField = $(tr.down("input.weMeasureUomDecimalScale"));
        var valueUomIdField = $(tr.down("input.mask_field_weMeasureUomDecimalScale"));
        if(decimalScaleField && valueUomIdField){
        	//alert("chiama " + valueUomIdField.setDecimalDigits);
        	valueUomIdField.setDecimalDigits(decimalScaleField);
        	//valueUomIdField._input.setValue(valueUomIdField.getValue());
        }	
    }
}

WorkEffortTransactionViewMulti.load();