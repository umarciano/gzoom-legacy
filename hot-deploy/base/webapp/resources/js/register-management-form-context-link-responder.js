RegisterManagementFormContextLinkResponder = {
    load : function(newContent, withoutRegister) {
        RegisterManagementFormContextLinkResponder.registerContextLink(newContent);

        if (!withoutRegister) {
            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    RegisterManagementFormContextLinkResponder.load(newContent, true);
                }
            }, 'register-management-form-context-link-responder');
        }

    },

    registerContextLink : function(newContent) {
        (!Object.isElement(newContent) ? $$('.link a, .context-link a') : newContent.select('.link a, .context-link a')).each(function(linkElement) {
            if (linkElement.readAttribute('onclick')) {
                Object.extend(linkElement, {onClickFunction : linkElement.readAttribute('onclick')});
                linkElement.onclick = '';
                linkElement.removeAttribute('onclick');
            }

            Event.stopObserving(linkElement, "click");
            Event.observe(linkElement, "click", function(event) {
                Event.stop(event);
                
                var elm = $(Event.element(event));
                var clickHandler = function() {
                    var href = elm.readAttribute('href');

                    var correctSelection = true;
                    //Sandro: 09/09/09 - se non ho una table cerco una form single-editable
                    var singleForm = false;
                    
                    var frmSingle = elm.up('div').up().down('table.single-editable');
                    //schede obiettivi, folder indicatori, layout progetti 
                    if(Object.isElement(frmSingle) && frmSingle.hasClassName('ignore-context-link')) {
                        frmSingle = null;
                    }
                    if (Object.isElement(frmSingle)) {
                    	singleForm = true;
                    } else {
                    	var table = elm.up('div').up().down('table.selectable');
                    	if (!Object.isElement(table)) {
                    		correctSelection = false;
                    	} else {
                    		if(!elm.up().hasClassName('always-active')){
                    			if(TableKit.isSelectable($(table))) {
            	                    var selectedRows = TableKit.Selectable.getSelectedRows(table);
            	                    if (selectedRows && selectedRows.size() > 0) {
            	                        selectedRows.each(function(row) {
            	                            var inputFields = $A(row.select('input'));
            	                            if(inputFields && inputFields.size() > 0) {
            	                                inputFields.select(function(element) {
            	                                    var name = element.readAttribute('name');
            	                                    if (name.indexOf('_o_') != -1) {
            	                                    	name = name.substring(0, name.indexOf('_o_'));
            	                                    }
            	                                    if(name == 'operation' && element.getValue() == 'CREATE') {
            	                                        correctSelection = false;
            	                                        modal_box_messages.alert(['BaseMessageInsertSelection']);
            	                                    }
            	                                });
            	                            }
            	                        });
            	                    }else{
            	                    	correctSelection = false;
            	                    }
            	                }
                    		}
                    	}
                    }
                    
                    if(correctSelection) {
                        var onSubmitString = null;
                        
                        if ('onClickFunction' in elm) {
                            var areaCsvString = elm.onClickFunction.substring(elm.onClickFunction.indexOf('(')+2, elm.onClickFunction.indexOf('\')'));
                            var areaArray = areaCsvString.split(",");

                            var backAreaId = areaArray[0];

                            href = areaArray[1];
        
                            if (Object.isString(areaArray[2]) && !areaArray[2].empty()) {
//                                var queryStringObject = areaArray[2].toQueryParams();
//                                if (queryStringObject && queryStringObject.forcedBackAreaId) {
//                                    backAreaId = queryStringObject.forcedBackAreaId;
//                                }

                                if (href.indexOf('?'))
                                    href = href + '?';
                                href = href + areaArray[2];
                            }
                            onSubmitString = 'ajaxSubmitFormUpdateAreas(\'linkForm\', \'' + backAreaId + '\'); return false;';
                        }
        
                        var form = RegisterManagementFormContextLinkResponder._createForm(href, null, {'id' : 'linkForm', 'name' : 'linkForm'});
                        document.body.insert(form);
        
                        //Sandro: 09/09/09 - se non ho una table cerco una form single-editable
                        if (singleForm) {
                        	//Estraggo la form contenitore per leggere anche hidden fields
                        	var frmSingle =	$(frmSingle).up("form");
                            RegisterManagementFormContextLinkResponder._populateForm(form, $A(frmSingle.select('input')));
                            
                        } else {
                        	var table = elm.up('div').up().down('table.selectable');
                            if(TableKit.isSelectable($(table))) {
                                var selectedRows = TableKit.Selectable.getSelectedRows(table);
                                if (selectedRows) {
                                    selectedRows.each(function(row) {
                                        //var firstCell = $(row.cells[0]);
                                        RegisterManagementFormContextLinkResponder._populateForm(form, $A(row.select('input')));
            });
                                }
                            }
                        }

                        var contextManagementInputHidden = form.down('input#contextManagement') ;
                        if (!contextManagementInputHidden) {
                            new Element('input', {'type' : 'hidden', 'value' : 'Y', 'name' : 'contextManagement', 'id' : 'contextManagement'});
                            form.insert(contextManagementInputHidden);
                        }
                        if (!onSubmitString || (onSubmitString && RegisterManagementFormContextLinkResponder._evalBool(onSubmitString))) {
                            form.submit();
                        }
                
                        form.remove();
                    }
                };

                if (elm.onPreClickFunction) {
                	elm.onPreClickFunction(clickHandler);
                } else {
                	clickHandler();
                }
            });
        });
    },
    _evalFunc : function(str) {
        eval('var evalFuncTmp = function() { ' + str + '; }');
        return evalFuncTmp();
    },

    _evalBool : function(str) {
        return (this._evalFunc(str) != false);
    },
    _createForm : function(actionUrl, elementCollection, option) {
        option = Object.extend(option || {}, {'class' : 'basic-form', 'method' : 'POST'});
        var form = new Element('form', option);
        RegisterManagementFormContextLinkResponder._initFormFromAction(form, actionUrl, null, elementCollection);
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
                        var inputHidden = RegisterManagementFormContextLinkResponder._setHiddenFormField(form, pair.key, pair.value);
                        inputHidden.addClassName('url-params');
                    }
                }));
            }

            RegisterManagementFormContextLinkResponder._populateForm(form, elementCollection);
        }
    },

    _populateForm : function(form, elementCollection) {
        $A(elementCollection).each(function(element) {
            var elementName = element.readAttribute("name");
            if(elementName.indexOf('_') > -1) {
                elementName = elementName.substring(0,elementName.indexOf('_'));
            }
            if (element.readAttribute("value")) {
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
                    valueHidden = valueHidden.replace(']', element.readAttribute('value')+ ']');
                    if (valueHidden.indexOf('[') == -1)
                        valueHidden = '['.concat(valueHidden);
                    findElement.writeAttribute('value', valueHidden);
                } else if (!findElement || (findElement && !findElement.hasClassName('url-params'))) {
                    var inputHidden = new Element('input', {'type' : 'hidden', 'value' : element.readAttribute('value'), 'name' : elementName, 'id' : elementName});
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
};

document.observe("dom:loaded", RegisterManagementFormContextLinkResponder.load);