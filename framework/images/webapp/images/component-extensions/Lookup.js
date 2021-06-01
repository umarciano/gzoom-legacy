/**
 * Lookup structure
 */
var Lookup = Class.create({
    /**
     * Constructor
     *
     */
    initialize : function(form, options) {
        //init form array
        this._form = $(form);

        //add (if exists) suffix to the forms's id
        if (options && options.suffix) {
            var currentId = this._form.readAttribute("id");
            currentId += "_" + options.suffix;
            this._form.writeAttribute("id", currentId);
        }

        this.initializeForm(options);
    },

    /**
     * Init form data/events
     */
    initializeForm: function(options) {

        //Init form field's references
        this._inputField = this._form.down("input.lookup_field_code");
        this._descFieldArray = this._form.select("input.lookup_field_description");
        this._submitField = this._form.down("a.lookup_field_submit");
        this._inputFieldValueSetted = false;
        //Get ajax parameters from input fields
        this._changeValueMandatory = "N";
        var changeValueMandatory = this._form.down("input.change_value_mandatory");
        if(changeValueMandatory) {
            this._changeValueMandatory = $(changeValueMandatory).getValue();
            changeValueMandatory.remove();
        }
        this._autocompleter_parameters = $H({});
        this._constraint_parameters = '';
        var paramArray = this._form.select("input.autocompleter_parameter");
        if (paramArray) {
            paramArray.each( function(item) {
                item = $(item);
                if ("constraintFields" !== item.readAttribute("name")) {
                    this._autocompleter_parameters.set(item.readAttribute("name"), item.readAttribute("value"));
                } else {
                    this._constraint_parameters = item.readAttribute("value");
                }
                item.remove();
            }.bind(this));
        }

        //Get target paraneters for lookup
        this._lookup_parameters = $H({});
        var lookupParamArray = this._form.select("input.lookup_parameter");
        if (lookupParamArray) {
            lookupParamArray.each( function(item) {
                item = $(item);
                this._lookup_parameters.set(item.readAttribute("name"), item.readAttribute("value"));
                item.remove();
            }.bind(this));
        }

        //Get options
        options = $H(options || {});
        options.set("autoSelect", "true");
        options.set("frequency", "6.0E-1");
        options.set("minChars", "1");
        options.set("readOnly", this._inputField.hasClassName("readonly"));

        var optionList = this._form.select("input.autocompleter_option");
        if (optionList) {
            optionList.each(function(optionElement) {
                optionElement = $(optionElement);
                options.set(optionElement.readAttribute("name"), optionElement.readAttribute("value"));
                optionElement.remove();
            });
        }

        this._autocompleter_parameters.set("fieldName", this._inputField.readAttribute("name"));
        this._autocompleter_parameters.set("saveView", "N");

        //Trick for FindAutocompleOptions.groovy, forse search into all select fields
        //this._parameters.set("searchFields", this._parameters.get("selectFields"));

        options.set("parameters",  this._autocompleter_parameters.toQueryString());

		 //create Ajax.AutoCompleter component for this form
		 var textId = this._inputField.getAttribute("id");
	     var hiddenId = this._form.getAttribute("id");
	
	     if (options.get("lookupAutocomplete") === "Y") {
	         this.buildAutocompleter(textId, hiddenId, options);
	     }
	        
        if (!this._inputField.readOnly) {
	
	        //catch click event on submit field
	        if (options.get("lookupAutocomplete") !== "Y")
	            this.enableButton();
	
	        Event.observe(this._submitField, "click", this.onClick.bind(this));
	
	        this.options = options;
	
	        this._onSetInputFieldValueListener = null;
	
        } else {
        	this._submitField.up().hide();
        }
        
        var parent = this._inputField.up('.other_elem_parent');
        if (!parent) {
        	if (this._inputField.readAttribute("name").indexOf("_o_") != -1) {
            	parent = this._inputField.up('tr');
            } else {
            	parent = this._inputField.up('form');
            }
        }
                
        if (this._autocompleter_parameters != null && this._autocompleter_parameters.get("selectFields") != null) {
    		var selectFields = this._autocompleter_parameters.get("selectFields");
    		
    		var selectFieldListForEntityName = selectFields.substring(1).substring(0, selectFields.length-2).split('|');
    		
    		var entityName = this._autocompleter_parameters.get("entityName");
    		var entityNameList = entityName.substring(1).substring(0, entityName.length-2).split('|');
    		
    		entityNameList.each(function(entityName, index) {
    			var selectFieldForEntityName = selectFieldListForEntityName[index];
    			if (selectFieldForEntityName) {
        			var selectFieldList = selectFieldForEntityName.substring(1).substring(0, selectFieldForEntityName.length-2).split(", ");
        			selectFieldList.each(function(selectField) {
        				var elementList = parent.select('input.' + selectField + ', select.' + selectField);
        				elementList.each(function(element) {
        					var ancestor = $A(element.ancestors()).find(function(parentElement) {
        	        			return parentElement.hasClassName('lookup_field');
        	        		}.bind(this));
        					
        					if (!Object.isElement(ancestor) || ancestor === this._form) {
	        					if (Object.isArray(this.otherElementList)) {
	        						this.otherElementList.push(element);
	        					} else {
	        						this.otherElementList = [element];
	        					}
        					}
        				}.bind(this))
        			}.bind(this));
    			}
    		}.bind(this));
    	}
    },

    clearInstance : function() {
        Event.stopObserving(this._submitField, 'click');

        var update = this._createUpdateId(this.options);
        if (update && Object.isElement($(update))) {
            $(update).remove();
        }

        if (Object.isHash(this._onChangeListener))
            this._onChangeListener = $H({});

        if (Object.isHash(this._onSetInputFieldValueListener))
            this._onSetInputFieldValueListener = $H({});
            
        if (this._autocompleter)
            this._autocompleter = null;
    },

    _createUpdateId : function(options) {
        var hiddenId = this._form.readAttribute("id");
        return hiddenId + "_autoCompleterOptions_Lookup" + (options.get('suffix') ? "_" + options.get('suffix') : "");
    },
    
    _setInputFromModalBox : false,

    /**
     * Instance Autocompleter object
     */
    buildAutocompleter: function(textId, hiddenId, options) {

        Ajax.Autocompleter.Lookup = Class.create(Ajax.Autocompleter, {
        	_lookupObject : false, 
        	
            initialize: function($super, element, update, url, options) {
                $super(element, update, url, options);
                Event.observe(this.element, 'change', this.onChange.bindAsEventListener(this));
                Event.observe(this.element, 'dom:change', this.onChange.bindAsEventListener(this));
            },

            onKeyPress: Prototype.K,
            onBlur: Prototype.K,
            
            _lookupComponent : false,
            
            setLookupComponent : function(lookupComponent) {
            	this._lookupComponent = lookupComponent;
            },

            onChange : function(event) {
            	if (!this._lookupComponent._setInputFromModalBox) {
	                // from super.onKeyPress
	                this.changed = true;
	                this.hasFocus = false;
	                if (this.getToken().length === 0) {
                		//In questo caso cancello.....
	                	var requestObj = {};
	                	var responseText = '<ul><li>';
	                	
	                	var selectFields = this._lookupComponent._autocompleter_parameters.get("selectFields");
	                	if (selectFields) {
	                		var selectFieldListForEntityName = selectFields.substring(1).substring(0, selectFields.length-2).split('|');
	                		
	                		var entityName = this._lookupComponent._autocompleter_parameters.get("entityName");
	                		var entityNameList = entityName.substring(1).substring(0, entityName.length-2).split('|');
	                		
	                		entityNameList.each(function(entityName, index) {
	                			var selectFieldForEntityName = selectFieldListForEntityName[index];
	                			if (selectFieldForEntityName) {
	                    			var selectFieldList = selectFieldForEntityName.substring(1).substring(0, selectFieldForEntityName.length-2).split(", ");
	                    			selectFieldList.each(function(selectField) {
	                    				responseText = responseText +'<span class=\'informal hidden\'>' + selectField + ':</span>';
	                    			});
	                			}
	                		});
	                	}
	                	
	                	responseText = responseText + '</li></ul>';
	                	requestObj['responseText'] = responseText;
	                	this._lookupComponent.onSuccess(requestObj)
	                } else {
		                if (this.observer)
		                    clearTimeout(this.observer);
		                this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency*1000);
	                }
            	} else {
            		this._lookupComponent._setInputFromModalBox = false;
            	}
            }
        });

        var update = this._createUpdateId(options);
        document.body.insert('<div class="autocomplete"' + 'id=' + update + '></div>');

        options.set('updateElement', this.updateElement.bind(this));
        options.set('onShow', this.onShow.bind(this));
        options.set('callback', this.callBack.bind(this));

        //Create autocompleter
        this._autocompleter = new Ajax.Autocompleter.Lookup($(textId), update, options.get('target'), options.toObject());
        this._autocompleter.setLookupComponent(this);

        this._autocompleter.startIndicator = this.disableButton.bind(this);
        this._autocompleter.stopIndicator = this.enableButton.bind(this);

        //see Ajax.Autocompleter class for known Ajax.request flow
        this._autocompleter.options.onSuccess = this.onSuccess.bind(this);

        //init enable click
        this.enableButton();
    },

    callBack : function(element, entry) {
        var returnString = entry || "";

        var contraintString = this.buildConstraintParameters();
        if (Object.isString(contraintString) && contraintString.length > 0)
            returnString += (Object.isString(returnString) && returnString.length > 0 ? "&" : "") + "constraintFields=" + contraintString;
        return returnString;
    },

    /**
     * Manages onSuccess for ajax request into Autocompleter object
     */
    onSuccess: function(request) {
        //At this point if request get nothing, delete inputField value and description fields
        try {
            var el = new Element("div");
            el.update(request.responseText);
            var liList = el.select("li");
            if (Object.isArray(liList)) {
            	if (liList.length == 0) {
                    //Clear description labels
	                if (this._descFieldArray) {
	                    this._descFieldArray.each( function(descField) {
	                        descField.clear();
	                    });
	                    this.dispatchOnSetInputFieldValue();
                    } 
                    modal_box_messages.alert(["BaseNoElementFound"], null, function() {
                        this._inputField.focus();
                    }.bind(this));
            	} else if(liList.length == 1) {
                    var hiddenList = el.select("span.informal.hidden");
                    if (hiddenList) {
                        hiddenList.each( function(item) {
                            var value = item.innerHTML;
                            if (Object.isString(value)) {
                            	var parArray = value.split("_:_");
                                    if (Object.isArray(parArray)) {
                                    if (parArray.length==2) {
                                        this.setInputFieldValue(parArray[0], parArray[1]);
                                    }
                                }
                            }
                        }.bind(this) );
             			  this.dispatchOnSetInputFieldValue();
                    }
                    
                } else  if (liList.length > 1) {
                    // Value not found or not unique.
                    matchFound = false;
                    	
                    var valueToCheck = this._inputField.getValue();
                    var hiddenListMatched = [];
	                for (var i = 0; !matchFound && i < liList.length; i++) {
	                	var hiddenList = liList[i].select("span.informal.hidden");
	                	if (Object.isArray(hiddenList)) {
	                		for(var j = 0; j < hiddenList.length; j++) {
	                			var value = hiddenList[j].innerHTML;
	                			if (Object.isString(value)) {
	                				var parArray = value.split("_:_");
                                    if (Object.isArray(parArray)) {
			                            if (parArray.length==2) {
			                                if (this._inputField.hasClassName("lookup_field_" + parArray[0]) && parArray[1] === valueToCheck) {
			                                	hiddenListMatched = hiddenList;
			                                	matchFound = true;
			                                	break;
			                                }
			                			}
			                        }
	                			}
	                		}
	                	}
	                }
                    	
                    if (matchFound) {
                    	if (Object.isArray(hiddenListMatched)) {
                    		hiddenListMatched.each( function(item) {
                                var value = item.innerHTML;
                                if (Object.isString(value)) {
                                    var parArray = value.split("_:_");
                                    if (Object.isArray(parArray)) {
                                        if (parArray.length==2) {
                                            this.setInputFieldValue(parArray[0], parArray[1]);
                                        }
                                    }
                                }
                            }.bind(this) );
     			  			this.dispatchOnSetInputFieldValue();
                    	}
                	}
                	else {
                        //Clear description labels
        	            if (this._descFieldArray) {
        	                this._descFieldArray.each( function(descField) {
        	                    descField.clear();
        	                });
        	                this.dispatchOnSetInputFieldValue();
                        } 
                        modal_box_messages.alert(["BaseTooElementFound"], null, function() {
                            this._inputField.focus();
                        }.bind(this));
        	        }
                } 
            }
            
            
            // from super.onBlur
        } finally {
            this._autocompleter.hide();
            this.hasFocus = false;
            this.active = false;
        }
    },

    /**
     * Manage onclick event on form's submit field
     */
    onClick: function(element) {
        if (this._clickAllowed) {
            try {
                //Collect parameters for ajaxAutocompleteOptions request
                var parms = $H(this._lookup_parameters || {});

                //
                //Field to search for
                //
                if (!Object.isUndefined(this._inputField.getValue()) && "" !== this._inputField.getValue()) {
                    var fieldName = this._autocompleter_parameters.get("fieldName");
                    parms.set(fieldName, this._inputField.getValue());
                    parms.set(fieldName + "_op", "contains");
                    parms.set(fieldName + "_ic", "Y");
                }

                //
                //add constraints to param map
                //
                parms = parms.merge(this.buildConstraintParameters(true).toObject());

                //entity
                var entityName = this._autocompleter_parameters.get("entityName");
                entityName = entityName.gsub(/\[|\]/, '');

                parms.set("entityName", entityName);

                var lookupTarget = this._autocompleter_parameters.get("lookupTarget") || "lookup";
                new Ajax.Request(lookupTarget, {
                    parameters: parms,
                    onComplete: function(request) {
                        var formWidth = document.viewport.getWidth() - (document.viewport.getWidth() * 0.20);
                        var formHeight = document.viewport.getHeight() - (document.viewport.getHeight() * 0.20);
                        Modalbox.show(request.responseText, {title: "Choices", width: formWidth, height: formHeight,
                                afterLoad: this.afterLoadModal, load : this.loadModal, afterHide : this.afterHideModal, beforeHide : this.beforeHideModal});
                    }.bind(this)
                });

                //Segnalo a LookupMgr che questo lookup ha aperto la modalbox
                LookupMgr.setActiveLookup(this);

            } catch(e) {
                alert("Error: " + e);
            }
        }
    },

    /**
     * Event fired after load modalbox
     */
    afterLoadModal: Prototype.K,

    /**
     * Event fired after load modalbox
     */
    loadModal: Prototype.K,

    /**
     * Event fired after hide modalbox
     */
    afterHideModal: Prototype.K,

    /**
     * Disable button prevent click
     **/
    disableButton: function() {
        this._clickAllowed = false;
    },

    /**
     * Enable button, click allowed
     **/
    enableButton: function() {
        this._clickAllowed = true;
    },

    setInputFieldFromElements: function(elements) {
        if (Object.isArray(elements) && elements.size() > 0) {
        	this._setInputFromModalBox = true;
            var elementSize = elements.size();
            elements.each(function(element, index) {
                var elementName = element.readAttribute("name");
                var elementValue = element.getValue();
                this.setInputFieldValue(elementName, elementValue, index < elementSize - 1);
            }.bind(this));
            //this.dispatchOnSetInputFieldValue();
        }
        this._setInputFromModalBox = false;
    },


    /**
     * Set this form input field value
     */
    setInputFieldValue: function(fieldName, fieldValue, blockDispatch) {
      /*  if (this._inputField.readOnly) {
            return;
        } */

        if (this._inputField.hasClassName("lookup_field_" + fieldName)) {
            this._inputField.setValue(fieldValue);
            if (this._changeValueMandatory == 'Y') {
                this.changeAfterUpdate();
            }
            return;
        }

        if (this._descFieldArray) {
            this._descFieldArray.each(function(descField){
                if (descField.hasClassName("lookup_field_" + fieldName)) {
                    if (fieldValue) {
                        if (['INPUT', 'SELECT'].include(descField.tagName)) {
                            descField.setValue(fieldValue);
                            //Sets new size of label
                            descField.writeAttribute("size", fieldValue.length + 1);
                        } else {
                            descField.update(fieldValue);
                        }
                    } else {
                        descField.clear();
                    }
                    throw $break;
                }
            });
        }
        
        if (Object.isArray(this.otherElementList)) {
        	var elementToPopulateList = this.otherElementList.select(function(element) {
        		var res = element.hasClassName(fieldName);
        		if (res) {
	        		var ancestor = $A(element.ancestors()).find(function(parentElement) {
	        			return parentElement.hasClassName('lookup_field');
	        		}.bind(this));
	        		res = Object.isUndefined(ancestor) || ancestor === this._form;
        		}
        		
        		return res;
        	}.bind(this));
        	
        	if (Object.isArray(elementToPopulateList)) {
        		elementToPopulateList.each(function(elementToPopulate) {
        			elementToPopulate.setValue(fieldValue);
        		});
        	}
        }

   //     if (!blockDispatch)
    //        this.dispatchOnSetInputFieldValue();
    },
    
    registerOnSetInputFieldValue : function(f, name) {
    	var hiddenId = this._inputField.readAttribute("id");
        if (Object.isFunction(f)) {
        	var onSetInputFieldValueListenerHash = $H({});
            if (!Object.isHash(this._onSetInputFieldValueListener)) {
                this._onSetInputFieldValueListener = $H({});
            }
            if (Object.isHash(this._onSetInputFieldValueListener.get(hiddenId)) && this._onSetInputFieldValueListener.get(hiddenId).values().size() > 0)
            	onSetInputFieldValueListenerHash = this._onSetInputFieldValueListener.get(hiddenId);
            
            if (name) {
            	onSetInputFieldValueListenerHash.unset(name);
            	onSetInputFieldValueListenerHash.set(name, f);
            } else if (!onSetInputFieldValueListenerHash.values().include(f)) {
                var counter = 1;
                do { name = counter++ } while (onSetInputFieldValueListenerHash.get(name));
                onSetInputFieldValueListenerHash.set(name, f);
            }
            this._onSetInputFieldValueListener.set(hiddenId, onSetInputFieldValueListenerHash);
        }
    },

    deregisterOnSetInputFieldValue : function(f, name) {
        var hiddenId = this._inputField.readAttribute("id");

        if (Object.isFunction(f) && typeof this._onSetInputFieldValueListener !== 'undefined' && this._onSetInputFieldValueListener.keys().size() > 0 && Object.isHash(this._onSetInputFieldValueListener.get(hiddenId)) && this._onSetInputFieldValueListener.get(hiddenId).keys().size() > 0) {
            var onSetInputFieldValueListenerHash = this._onSetInputFieldValueListener.get(hiddenId);
            if (name) {
            	onSetInputFieldValueListenerHash.unset(name);
            } else if (onSetInputFieldValueListenerHash.values().include(f)) {
            	onSetInputFieldValueListenerHash.keys().each(function(key) {
                    var fn = onSetInputFieldValueListenerHash.get(key);
                    if (fn === f) {
                    	onSetInputFieldValueListenerHash.unset(key);
                        throw $break;
                    }
                });
            }
        }
    },
    
    dispatchOnSetInputFieldValue : function() {
    	var hiddenId = this._inputField.readAttribute("id");
    	if (Object.isHash(this._onSetInputFieldValueListener) && this._onSetInputFieldValueListener.keys().size() > 0 && Object.isHash(this._onSetInputFieldValueListener.get(hiddenId)) && this._onSetInputFieldValueListener.get(hiddenId).values().size() > 0) {
        	this._onSetInputFieldValueListener.get(hiddenId).each(function(pair) {
            	var listener = pair.value;
                if (Object.isFunction(listener)) {
                	try {
                        listener.apply(this);
                    } catch (e) { }
                }
            }.bind(this));
        }
    },

    /*dispatchOnSetInputFieldValue : function() {
        // richiama registrazioni
        if (Object.isArray(this._onSetInputFieldValueListener) && this._onSetInputFieldValueListener.size() > 0) {
            this._onSetInputFieldValueListener.invoke('apply');
        }
    },
    
    registerOnSetInputFieldValue : function(f) {
        if (Object.isFunction(f)) {
            if (!Object.isArray(this._onSetInputFieldValueListener))
                this._onSetInputFieldValueListener = new Array();
            this._onSetInputFieldValueListener.push(f);
        }
    },

    deregisterOnSetInputFieldValue : function(f) {
        if (Object.isFunction(f)) {
            if (Object.isArray(this._onSetInputFieldValueListener))
                this._onSetInputFieldValueListener = this._onSetInputFieldValueListener.without(f);
        }
    },*/
    
    changeAfterUpdate : function() {
        this._inputField.addClassName("red");
    },
    /**
     * Clean field name of index. If there isn't index return name
     */
    _cleanIndex: function(attr) {
        var newName = attr.replace(/(_o_[0-9]+)/, "");
        return newName;
    },
    
    refreshContraintParameters: function(newIndex, global) {
    	if (this._constraint_parameters && newIndex > -1) {
    		if (global) {
    			this._constraint_parameters = this._constraint_parameters.replace(/(_o_[0-9]+)/g, '_o_'+newIndex);
    		} else {
    			this._constraint_parameters = this._constraint_parameters.replace(/(_o_[0-9]+)/, '_o_'+newIndex);
    		}
    	}
    },

    /**
     * Get lookup input field
     */
    getInputField: function() {
        if (this._inputField) {
            return this._inputField;
        }
        return;
    },

    /**
     * Get entity key field parameter if any
     */
    getEntityKeyField: function() {
        return this._autocompleter_parameters.get("entityKeyField");
    },

    buildConstraintParameters: function(asMap) {
        var returnString = null;
        var paramMap = $H({});
        if (this._constraint_parameters) {

            //Clean external []
            var param = this._removeTrailingSquare(this._constraint_parameters);

            var paramList = param.split("; ");
            if (Object.isArray(paramList)) {
                paramList = $A(paramList);
                if (!asMap) {
                    returnString = '[';
                }
                paramList.each( function(paramItem, paramIndex) {
                    paramItem = this._removeTrailingSquare(paramItem);
                    var list = paramItem.split("! ");

                    if (Object.isArray(list)) {
                        list = $A(list);
                        if (!asMap) {
                            returnString += '[';
                        }
                        list.each( function(item, index) {
                            item = this._removeTrailingSquare(item);
                            var valueList = item.split("| ");
                            if (Object.isArray(valueList)) {

                                //Build parameters
                                if (valueList.length <= 2) {
                                    if (valueList.length == 2) {
                                        var paramName = valueList[0];
                                        var paramElement = this._inputField.form.getElements().find(function(element) {
                                            return element.readAttribute('name') === paramName || element.readAttribute('name') === paramName + "_fld0_value";
                                        });
                                        if (!Object.isElement(paramElement)) {
                                            return; //Exit
                                        }
                                        var paramValue = paramElement.getValue();
                                        if (!paramValue) {
                                            return; //Exit
                                        }
                                        valueList.push(paramValue);
                                    } else {
                                        return; //Exit
                                    }
                                }
                                if (valueList.length > 2) {
                                    var name = this._safeTrim(valueList[0]);
                                    var values = "";
                                    for (i=2; i<valueList.length; i++) {
                                        values += (values.length > 0) ? "| " : "";

                                        var value = this._safeTrim(valueList[i]);
                                    	if (value.indexOf("field:") == -1) {
                                            values += value;
                                        } else {
                                            var valueSplitted = value.split("field:");
                                            var fieldName = valueSplitted[1];
                                            var form = $(this._inputField.form);
                                            var field = form.getElements().find(function(element) {
                                                return element.readAttribute('name') === fieldName;
                                            });
                                            if (Object.isElement(field)) {
                                                values += field.getValue();
                                            }
                                        }
                                    }
                                    if (values.length > 0) {
                                        if (asMap) {
                                            paramMap.set(name, values);
                                            paramMap.set(name + "_op", this._safeTrim(valueList[1]));
                                            paramMap.set(name + "_ic", "Y");
                                        } else {
                                            returnString += '[' + name + '| ' + this._safeTrim(valueList[1]) + '| ' + values + ']';
                                        }
                                    }
                                }
                            }

                            if (!asMap && index < list.size()-1) {
                                returnString += '! '
                            }
                        }.bind(this));

                        if (!asMap) {
                            returnString += ']';
                        }
                    }

                    if (!asMap && paramIndex < paramList.size()-1) {
                        returnString += '; '
                    }
                }.bind(this));

                if (!asMap) {
                    returnString += ']';
                }
            }
        }
        if (asMap)
            return paramMap;
        else
            return returnString;
    },

    /**
     * Utility to remove external '[' and ']' in a string
     */
    _removeTrailingSquare: function (strItem) {
        if (Object.isString(strItem)) {
            if (strItem.startsWith("[")&&strItem.endsWith("]")) {
                strItem = strItem.substring(1, strItem.length-1);
            }
        }
        return strItem;
    },

    /**
     * Safely remove trailing spaces
     */
    _safeTrim: function(str) {
        if (Object.isString(str)) {
            str = str.strip();
        }
        return str;
    },

    /**
     * Event automatically launched when autocompleter go back from query
     */
    updateElement: function( liElement ) {
        var hiddenList = liElement.select("span.informal.hidden");
        if (hiddenList) {
            hiddenList.each( function(item) {
                var value = item.innerHTML;
                if (Object.isString(value)) {
                    var parArray = value.split("_:_");
                    if (Object.isArray(parArray)) {
                        if (parArray.length==2) {
                            this.setInputFieldValue(parArray[0], parArray[1]);
                        }
                    }
                }
            }.bind(this) );
        }
    },

    /**
     * Don't show list
     */
    onShow : function(element, update) {
        return;
   },
   
   setValue: function(value) {
   		this._inputFieldValueSetted = true;
   		this._inputField.writeAttribute("value", value);
   		this._inputField.fire("dom:change");
   }

});


/**
 * Lookup Manager static Class
 */
var LookupMgr = Class.create({ });

/**
 * Create structure for every lookup in page
 */
LookupMgr.loadElement = function(baseElement, newInstance, index, options) {
    //Recupero la mappa di elementi droplist nell'array cache
    if (!(Object.isArray(LookupMgr._lookupMapArray) && LookupMgr._lookupMapArray.size() > 0)) {
        LookupMgr._lookupMapArray = new Array();
    }

    var elementMap = null;
    if (newInstance || LookupMgr._lookupMapArray.size() == 0) {
        elementMap = $H({});
        LookupMgr._lookupMapArray.push(elementMap);
    }
    else if (Object.isNumber(index) && LookupMgr._lookupMapArray.size() > index){
        elementMap = LookupMgr._lookupMapArray[index];
    }
    else {
        elementMap = LookupMgr._lookupMapArray.last();
    }

    //Per prima cosa verifico che eventuali autocompleter presenti abbiamo ancora
    //validita, solo se non richiesta una nuova instanza
    if (!newInstance && baseElement && !baseElement.multi_editable_insert) {
        var autocompleterPanelList = $$('div.autocomplete');
        if (Object.isArray(autocompleterPanelList) && autocompleterPanelList.size() > 0) {
            autocompleterPanelList.each(function(autocompleterPanel) {
                var id = autocompleterPanel.identify();

                var suffix = '_autoCompleterOptions_Lookup' + ((options && options.suffix) ? '_' + options.suffix : '');
                if (id.indexOf(suffix) != -1) {
                    var idAutocompleterComponent = id.substring(0, id.indexOf(suffix));

                    //Elimino l'oggetto dalla cache degli autocompleter solo se è presente un autocompleter
                    //con lo stesso id nel nuovo contenuto da inserire (baseElement) in quanto dovrò riaggiornare
                    //l'oggetto js associato, oppure quando non ho un contenuto da inserire (non sono in risposta
                    //ad una chiamata ajax) e nel DOM non è presente nessuno componente con l'idAutocompleterComponent
                    if ((Object.isElement($(baseElement)) && Object.isElement($(baseElement).down('#' + idAutocompleterComponent))) || !Object.isElement($(idAutocompleterComponent)) ) {
                        if (Object.isHash(elementMap)) {
                            elementMap.unset(idAutocompleterComponent);

                            if (elementMap.keys().size() == 0) {
                                LookupMgr._lookupMapArray = LookupMgr._lookupMapArray.without(elementMap);
                                elementMap = $H({});
                                LookupMgr._lookupMapArray.push(elementMap);
                            }
                        }

                        autocompleterPanel.remove();
                    }
                }
            });
        }
    }
    //init form array
    if (baseElement) {
        LookupMgr._lookupArray = $(baseElement).select('div.lookup_field');
    } else {
        LookupMgr._lookupArray = $$('div.lookup_field');
    }

    if (Object.isHash(elementMap)) {
        LookupMgr._lookupArray.each( function(item) {
            var key = $(item).readAttribute("id");
            var itemFound = elementMap.get(key);
            if (Object.isUndefined(itemFound) || itemFound._form != item) {
                elementMap.set(key, new Lookup(item, options));
            }
        });
    }

    if (Object.isHash(elementMap))
        return LookupMgr._lookupMapArray.indexOf(elementMap);
    else
        return null;


    //Load elements
    /*if (baseElement) {
        LookupMgr._lookupArray = $(baseElement).select('div.lookup_field');
    } else {
        LookupMgr._lookupArray = $$('div.lookup_field');
    }

    //create lookup map
    if (Object.isUndefined(LookupMgr._elementMap)) {
        LookupMgr._elementMap = $H({});
    }

    //Pupulate lookup map
    LookupMgr._lookupArray.each( function(item) {
        var key = $(item).readAttribute("id");
        if (!LookupMgr._elementMap.get(key) || $(key) !== LookupMgr._elementMap.get(key)) {
            LookupMgr._elementMap.set($(item).readAttribute("id"), new Lookup(item));
        }
    });*/


}

/**
 * Static method for submit lookup forms
 */
LookupMgr.submitLookupForm = function(areaId, formId, entity) {

    var form = $(formId);
    if (form) {
        var parms = form.serialize(true);
        /* main-lookup-container-screenlet definition is in LookupCommonScreen */
        new Ajax.Updater('main-lookup-container-screenlet', form.readAttribute("action"), {parameters: parms, evalScripts: true});
    }
}

/**
 * Static method for reset form fields
 */
LookupMgr.resetSearchFormInput = function(formId) {
    var form = $(formId);
    if (form) {
        form.reset();
    }
}

/**
 * Set lookup that activated modal box
 */
LookupMgr.setActiveLookup = function(lookup) {
    LookupMgr._lastLookup = lookup;
}

/**
 * Get active lookup (see method above)
 */
LookupMgr.getActiveLookup = function() {
    if (LookupMgr._lastLookup) {
        return LookupMgr._lastLookup;
    }
    return;
}

LookupMgr.getLookup = function(divLookupField) {
    if (Object.isArray(LookupMgr._lookupMapArray) && LookupMgr._lookupMapArray.size() > 0) {
    	var obj = LookupMgr._lookupMapArray.find(function(item){
            return item.keys().include(divLookupField);
        });
        if (obj) {
	        var lookup = obj.get(divLookupField);
	        return lookup;
        }
    }
    return;
}

LookupMgr.clearInstance = function(instance) {
    if (Object.isArray(LookupMgr._lookupMapArray) && LookupMgr._lookupMapArray.size() > 0) {
        if (Object.isNumber(instance)) {
            if (LookupMgr._lookupMapArray.size() > instance) {
                instance = LookupMgr._lookupMapArray[instance];
                LookupMgr._lookupMapArray = LookupMgr._lookupMapArray.without(instance);
            }
        } else if (Object.isHash(instance)){
            LookupMgr._lookupMapArray = LookupMgr._lookupMapArray.without(instance);
        }

        if (Object.isHash(instance)) {
            instance.each(function(pair) {
                var lookupElement = pair.value;

                lookupElement.clearInstance();
            });
        }
    }
}

//*****************
//Init object
//*****************
document.observe("dom:loaded", function() {
    if (!Object.isUndefined(LookupMgr)) {
        LookupMgr.loadElement();
    }
});
