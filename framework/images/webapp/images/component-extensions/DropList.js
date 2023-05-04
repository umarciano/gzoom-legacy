/**
 * Droplist structure
 */
var DropList = Class.create({
    /**
     * Constructor
     *
     */
    initialize : function(field, options) {
        //init form array
        this._field = $(field);
        this.initializeField(options);
        //this.executeUnitTests(); // Comment/uncomment only when needed
    },

    options : null,

    /**
     * Init form data/events
     */
    initializeField: function(options) {
        //Get form container
        //this._formContainer = this._field.up("form");
        
        this._formContainer = this._field.up('.other_elem_parent');        
        if (!this._formContainer) {
        	this._formContainer = this._field.up("form");
        }
        

        //Init form field's references
        this._editField = this._field.down("input.droplist_edit_field");
        this._codeField = this._field.down("input.droplist_code_field");
        this._submitField = this._field.down("a.droplist_submit_field");

        //Get options
        options = $H(options || {});
        options.set("readOnly", this._editField.hasClassName("readonly"));
        var optionList = this._field.select("input.autocompleter_option");
        if (optionList) {
            optionList.each(function(optionElement) {
                optionElement = $(optionElement);
                options.set(optionElement.readAttribute('name'), optionElement.readAttribute('value'));
                optionElement.remove();
            });
        }

        //Get ajax parameters from input fields
        this._constraint_parameters = '';
        this.autocompleter_parameter = new Hash();
        var paramArray = this._field.select("input.autocompleter_parameter");
        if (paramArray) {
            paramArray.each( function(item) {
                item = $(item);
                if ("constraintFields" !== item.readAttribute("name")) {
                    this.autocompleter_parameter.set(item.readAttribute("name"), item.readAttribute("value"));
                } else {
                    this._constraint_parameters = item.readAttribute("value");
                }
                item.remove();
            }.bind(this));
        }

        var localDataArray = this._field.select("input.autocompleter_local_data");
        if (Object.isArray(localDataArray) && localDataArray.size() > 0) {
            //Get local value list
            var hiddenId = this._field.readAttribute("id");
            this._localData = $A({});
            localDataArray.each(function(localDataElement) {
                var localDataElementId = localDataElement.readAttribute("id");
                var key = localDataElementId.substring(localDataElementId.indexOf(hiddenId) + hiddenId.length + 1);
                var value = localDataElement.readAttribute("value");
                var mappa = $H({});
                mappa.set(key,value);
                this._localData.push(mappa);
                localDataElement.remove();
            }.bind(this));
        } else {
            //field where groovy has to look for value
            if (!this._editField.readOnly)
                this.autocompleter_parameter.set("fieldName", this._editField.readAttribute("name"));

            //Fields where do search
            var searchFields = this.autocompleter_parameter.get("findFields");
            if (searchFields == null) {
            	searchFields = this.autocompleter_parameter.get("selectFields");            	
            }
            this.autocompleter_parameter.set("searchFields", searchFields);
            
            options.set("parameters",  this.autocompleter_parameter.toQueryString());
        }

        if (this._editField.readOnly) {
            //this._submitField.hide();
            this._submitField.up().hide();

        } else {
        	//create Ajax.AutoCompleter component for this field
            this.buildAutocompleter(options);

            this.options = options;
        }
        
        var parent = this._editField.up('.other_elem_parent');
        if (!parent) {
        	if (this._editField.readAttribute("name").indexOf("_o_") != -1) {
            	parent = this._editField.up('tr');        
            } else {
            	parent = this._editField.up('form');
            }
        }
        
    	if (this.autocompleter_parameter != null && this.autocompleter_parameter.get("selectFields") != null) {
    		var selectFields = this.autocompleter_parameter.get("selectFields");
    		
    		var selectFieldListForEntityName = selectFields.substring(1).substring(0, selectFields.length-2).split('|');
    		
    		var entityName = this.autocompleter_parameter.get("entityName");
    		var entityNameList = entityName.substring(1).substring(0, entityName.length-2).split('|');
    		
    		entityNameList.each(function(entityName, index) {
    		    var selectFieldForEntityName = selectFieldListForEntityName[index];
                if (selectFieldForEntityName) {
        			var selectFieldList = selectFieldForEntityName.substring(1).substring(0, selectFieldForEntityName.length-2).split(", ");
        			selectFieldList.each(function(selectField) {
        			    if (selectField) {
                            var element = parent.down('.' + selectField);
                            if (Object.isElement(element)) {
                                if (Object.isArray(this.otherElementList)) {
                                    this.otherElementList.push(element);
                                } else {
                                    this.otherElementList = [element];
                                }
                            }
                        }
        			}.bind(this));
    			}
    		}.bind(this));
    	}
        	
    },

    /**
     * Instance Autocompleter object
     */
    buildAutocompleter: function(options) {

        var update = this._createUpdateId(options);
        document.body.insert('<div class="autocomplete"' + 'id=' + update + '></div>');

        //Event.observe(this._editField, 'keydown', this.onKeyPress.bindAsEventListener(this));
        Event.observe(this._submitField, 'click', this.onSubmitFieldClick.bindAsEventListener(this));

        options.set('afterUpdateElement', this.afterUpdateElement.bind(this));
        options.set('onShow', this.onShow.bind(this));
        if (Object.isHash(this._localData) || "Y" === this.autocompleter_parameter.get('localAutocompleter')) {
            if (!this._editField.readOnly) {
                this._autocompleter = new Autocompleter.Local(this._editField, update, this._localData ||$H({}), options.toObject());
                this._autocompleter = Object.extend(this._autocompleter, {'allElementsChar' : ''});
            } else {
                this._autocompleter = null;
            }
        } else {
            //Create autocompleter
            options.set('callback', this.callBack.bind(this));
            if (!this._editField.readOnly) {
                this._autocompleter = new Ajax.Autocompleter(this._editField, update, options.get('target'), options.toObject());
                //see Ajax.Autocompleter class for known Ajax.request flow
                this._autocompleter.options.onSuccess = this.onSuccess.bind(this);
                this._autocompleter = Object.extend(this._autocompleter, {'allElementsChar' : '%'});
            } else {
                this._autocompleter = null;
            }
        }
        if (this._autocompleter) {
            //WRAP del metodo onObserverEvent di Ajax.Autocompleter
            //in modo che quando venga cancellato il contenuto del campo testo
            //anche il codice nascosto venga cancellato
            this._autocompleter.onObserverEvent = this._autocompleter.onObserverEvent.wrap(
                function(proceed) {
                    var value = encodeURIComponent(this._autocompleter.getToken());
                    if (value) {
                    	if (Object.isHash(this._localData) || "Y" === this.autocompleter_parameter.get('localAutocompleter')) {
                        	proceed(this._localData);
                        } else {
                        	proceed();
                        }
                    } else {
                        this.afterUpdateElement();
                    }
                }.bind(this)
            );
            this._autocompleter.onKeyPress = this._autocompleter.onKeyPress.wrap(function(proceed, event) {
                if (event.keyCode==16||event.keyCode==17||event.keyCode==18) {
                    Event.stop(event);
                    return false;
                } else {
                    proceed(event);
                }
            });
        }
    },

    callBack : function(element, entry) {
        var returnString = entry || "";

        var contraintString = this.buildConstraintParameters();
        if (Object.isString(contraintString) && contraintString.length > 0) {
            contraintString = encodeURIComponent(contraintString);
            returnString += (Object.isString(returnString) && returnString.length > 0 ? "&" : "") + "constraintFields=" + contraintString;
        }
        return returnString;
    },

    /**
     * Builds constrint parameters for modal window
     */
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
                                        if (valueList[1].indexOf('inOrNull') >= 0) {
                                        	 if (asMap) {
                                                 paramMap.set(paramName, '');
                                                 paramMap.set(paramName + "_op", 'inOrNull');
                                                 paramMap.set(paramName + "_ic", "Y");
                                             } else {
                                                 returnString += '[' + paramName + '| inOrNull|]';
                                             }
                                        } else {
                                            var paramElement = this._editField.form.getElements().find(function(element) {
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
                                        }
                                    } else {
                                        return; //Exit
                                    }
                                }
                                if (valueList.length > 2) {
                                    var name = this._safeTrim(valueList[0]);
                                    var op = this._safeTrim(valueList[1]);
                                    var values = "";
                                    
                                    //Bug lookup-for-field-name caso form multi: prendeva il nome campo della prima riga
                                    var selectRow = null;
                                    for (i=2; i<valueList.length; i++) {
                                        values += (values.length > 0) ? "| " : "";
                                        var value = this._safeTrim(valueList[i]);

                                        //Correggo l'indice, vedi bug appena sopra
                                        var table = this._field.up("table");
                                        if (Object.isElement(table) && table.hasClassName('multi-editable')) {
                                            selectRow = TableKit.Selectable.getSelectedRows(table)[0];
                                            var rowIndex = TableKit.getRowIndex(selectRow);
                                            value = this._changeIndex(value, rowIndex);
                                        }
                                        
                                        /*
                                         * gestione contraint, il valore e' dato dal metodo getConstraintValue
                                         */
                                        values += this.getConstraintValue(value, selectRow)                                                                             
                                    }
                                    if (values.length > 0) {
                                        if (asMap) {
                                            paramMap.set(name, values);
                                            paramMap.set(name + "_op", op);
                                            paramMap.set(name + "_ic", "Y");
                                        } else {
                                            returnString += '[' + name + '| ' + op + '| ' + values + ']';
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
     * gestisce il valore della constraint
     */
    getConstraintValue: function(value, selectRow) {
    	var constraintValue = "";
    	var constValue = null;
        var fieldValue = null;
        
		if (value.indexOf("field:") == -1) {
    		constValue = value;
    	} else {
    		var constValueFieldSplitted = value.split("field:");
    
    		fieldValue = "field:" + constValueFieldSplitted[1];
    	}
        
        if (fieldValue) {
        	var valueSplitted = fieldValue.split(":");
        	if (valueSplitted.length == 2) {
        		var fieldName = valueSplitted[1];
                var form = $(this._editField.form);
                
                var field = null;
                
                if (Object.isElement(selectRow)) {
                    field = selectRow.select('input', 'select', 'textarea').find(function(element) {
                        var elementName = element.readAttribute('name');
                        var length = elementName.indexOf('_o_') > 0 ? elementName.indexOf('_o_') : elementName.length;
                        elementName = elementName.substring(0,length);
                        return elementName === fieldName;
                    });
                }
                if (!Object.isElement(field)) {
                    field = form.getElements().find(function(element) {
                        var elementName = element.readAttribute('name');
                        var length = elementName.indexOf('_o_') > 0 ? elementName.indexOf('_o_') : elementName.length;
                        elementName = elementName.substring(0,length);
                        return elementName === fieldName;
                    });
                }
                if (field) {
                	constraintValue += field.getValue();
                }
                if (constValue) {
                	constraintValue += constValue;
                }
            }
        } else if (constValue) {
        	constraintValue = constValue;
        }
        
        return constraintValue;
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

    _createUpdateId : function(options) {
        var hiddenId = this._field.readAttribute("id");
        return hiddenId + "_autoCompleterOptions" + (options && options.get('suffix') ? "_" + options.get('suffix') : "");
    },

    /**
     * Manages onSuccess for ajax request into Autocompleter object
     */
    onSuccess: function(request) {
        //At this point if request get nothing, delete inputField value and description fields
        try {
            var el = new Element("div");
            el.update(request.responseText);
            if (el.select("li").length==0) {
                //alert("Nessun elemento esistente!"); //TODO: gestire finestra standard
                if (!this._editField.readOnly) {
                    //Clear input field
                    this._editField.clear();
                    //Clear code fields
                    this._codeField.clear();
                    this._autocompleter.show();
                }
            }
        } catch (e) { }
    },

    /*
     * Catch enter event on input field
     */
     /*onKeyPress: function(event) {
        // Shift - Ctrl - Alt
        if (event.keyCode==16||event.keyCode==17||event.keyCode==18) {
            Event.stop(event);
            return false;
        }

        this._autocompleter.onKeyPress(event);
    },*/

    /**
     * Catch click event on submit field
     */
    onSubmitFieldClick: function(event) {
        //force activation autocompleter
        var oldVal = this._editField.getValue();
        if (!this._editField.readOnly) {
            this._editField.setValue(this._autocompleter.allElementsChar);
        }
        this._editField.focus();
        if (!this._editField.readOnly) {
            if (Object.isHash(this._localData) || "Y" === this.autocompleter_parameter.get('localAutocompleter')) {
                this._autocompleter.activate(this._localData);
            } else {
                this._autocompleter.activate();
            }
            this._editField.clear();
            if (oldVal!=null) {
                this._editField.setValue(oldVal);
            } else {
                this._editField.clear();
            }
        }
        this._editField.focus();
        Event.stop(event);
    },

    /**
     * Set this form fields
     */
    setDescriptionField: function(fieldName, fieldValue) {
        if (this._editField.readOnly) {
            return;
        }

        //gets and clean field name
        var descriptionFieldName = this._cleanIndex(this._editField.readAttribute("name"));
        var codeFieldName = this._cleanIndex(this._codeField.readAttribute("name"));
        
    	// Issue GN-5262
        fieldValue = this._replaceSpecialCharacters(fieldName, fieldValue);

        if (descriptionFieldName==fieldName||this.getEntityDescriptionField()==fieldName) {
            this._editField.setValue(fieldValue);
        }
        if (codeFieldName==fieldName||this.getEntityKeyField()==fieldName) {
            this._codeField.setValue(fieldValue);
        }
        
        if (Object.isArray(this.otherElementList)) {
        	var elementToPopulate = this.otherElementList.find(function(element) {
        		return element.hasClassName(fieldName);
        	}.bind(this));
        	
        	if (Object.isElement(elementToPopulate)) {
        	    if($(elementToPopulate).hasClassName("droplist_field")) {
        	        console.log("WARNING: set droplist_field is NOT POSSIBLE");
        	    } else if($(elementToPopulate).hasClassName("lookup_field")) {
                    console.log("WARNING: set lookup_field is NOT POSSIBLE");
                } else {
        	        elementToPopulate.setValue(fieldValue);
        	    }
        	}
        }
    },
    
    /**
     * Issue GN-5262
     */
    _replaceSpecialCharacters: function(fieldName, fieldValue) {
    	// console.log("[DropList.js::_replaceSpecialCharacters] BEFORE: _" + fieldName + "_ = _"+ fieldValue+ "_");
    	if (fieldValue == null) {
    		return fieldValue;
    	}
    	
    	/*
    	 * Lista delle sostituzioni (eventualmente da aggiornare)
    	 */
    	fieldValue = this._replaceSpecialCharacter(fieldValue, "&lt;", "<");
    	fieldValue = this._replaceSpecialCharacter(fieldValue, "&gt;", ">");
    	
    	// console.log("[DropList.js::_replaceSpecialCharacters] AFTER: _" + fieldName + "_ = _"+ fieldValue+ "_");
    	return fieldValue;
    
    },
    
    _replaceSpecialCharacter: function(fieldValue, from, to) {
    	index = fieldValue.indexOf(from);
    	if (index >= 0) {
    		fieldValue = fieldValue.replace(from, to);
    		// Ricorsivo, fino a quando non ci sono più sostituzioni necessarie
    		return this._replaceSpecialCharacter(fieldValue, from, to);
    	} else {
    		return fieldValue;
    	}
    
    },

    /**
     * Clean field name of index. If there isn't index return name
     */
    _cleanIndex: function(attr) {
        var newName = attr.replace(/(_o_[0-9]+)/, "");
        return newName;
    },


    /**
     * Get entity key field parameter if any
     */
    getEntityKeyField: function() {
        var fieldVal = null;
        if (this.autocompleter_parameter.get("entityKeyField")) {
            fieldVal = this.autocompleter_parameter.get("entityKeyField");
        }
        return fieldVal;
    },

    /**
     * Get entity description field parameter if any
     */
    getEntityDescriptionField: function() {
        var fieldVal = null;
        if (this.autocompleter_parameter.get("entityDescriptionField")) {
            fieldVal = this.autocompleter_parameter.get("entityDescriptionField");
        }
        return fieldVal;
    },
    
    getDisplayFields: function() {
        var fieldVal = null;
        if (this.autocompleter_parameter.get("displayFields")) {
            fieldVal = this.autocompleter_parameter.get("displayFields");
        }
        return fieldVal;
    },

    /**
     * Event launched when user select id from list
     * It update fields referenced in form
     */
    afterUpdateElement: function(text, li) {
        if (Object.isElement(li)) {
            var hiddenList = li.select("span.informal.hidden");
            if (Object.isArray(hiddenList) && hiddenList.size() > 0) {
            	var displayFields =  this.getDisplayFields();
            	var displayFieldsList = displayFields.substring(2).substring(0, displayFields.length-4).split(",");
            	if(Object.isArray(displayFieldsList) && displayFieldsList.size() > 0) {
            		var descriptionString = "";
            		//displayFieldsList.each( function (displayField) {
            		for(var i = 0; i < displayFieldsList.size(); i++) {
            			displayFieldsList[i] = displayFieldsList[i].strip();
            			var displayField =  displayFieldsList[i];
            			
	            		hiddenList.each( function(item) {
		                    var value = item.innerHTML;
		                    if (Object.isString(value)) {
		                        var parArray = value.split("_:_");
		                        if (Object.isArray(parArray)) {
		                            if (parArray.length==2 && parArray[0] == displayField && parArray[1] != "") {
		                            	if(descriptionString == "") {
		                            		descriptionString += parArray[1];
		                            	}
		                            	else {
		                            		descriptionString += " - " + parArray[1];
		                            	}
		                              
		                            }
		                        }
		                    }
	                	}.bind(this) );
            		}
                	this.setDescriptionField(this.getEntityDescriptionField(), descriptionString);
            	}
                hiddenList.each( function(item) {
                    var value = item.innerHTML;
                    if (Object.isString(value)) {
                        var parArray = value.split("_:_");
                        if (Object.isArray(parArray)) {
                            if (parArray.length==2) {
                            	if(!Object.isArray(displayFieldsList) || 
                            		Object.isArray(displayFieldsList) && displayFieldsList.size() > 0 && displayFieldsList.indexOf(parArray[0]) == -1) {
                            			this.setDescriptionField(parArray[0], parArray[1]);
                            	}
                            }
                        }
                    }
                }.bind(this) );
            } else {
            	var fieldValue = li.innerHTML.stripTags();
                if (this._editField.readAttribute("name") != this._codeField.readAttribute("name") && Object.isString(fieldValue)) {
                	this.setDescriptionField(this._cleanIndex(this._editField.readAttribute("name")), fieldValue);
                	this.setDescriptionField(this._cleanIndex(this._codeField.readAttribute("name")), li.identify());
                } else {
                	this.setDescriptionField(this._cleanIndex(this._codeField.readAttribute("name")), li.identify());
                }
            }
        } else {
            this._editField.clear();
            this._codeField.clear();
            
            if (Object.isArray(this.otherElementList)) {
                console.log(" this.otherElementList ", this.otherElementList);
            	this.otherElementList.each(function(element2) {
            	    console.log(" element.id ", element2);
                    console.log(" element.clear ", element2.clear);
            		element2.clear();
            	});
            }
        }

        var hiddenId = this._field.readAttribute("id");
        if (Object.isHash(this._onChangeListener) && this._onChangeListener.keys().size() > 0 && Object.isHash(this._onChangeListener.get(hiddenId)) && this._onChangeListener.get(hiddenId).values().size() > 0) {
            this._onChangeListener.get(hiddenId).each(function(pair) {
                var listener = pair.value;
                if (Object.isFunction(listener)) {
                    try {
                        listener.apply(this, hiddenList);
                    } catch (e) { }
                }
            }.bind(this));
        }
    },

    /**
     * Set correctly show list
     */
    onShow : function(element, update) {
        var autocompleteToHide = $$('div.autocomplete');
        autocompleteToHide.each(function(el) {
            el.setStyle({display: "none"});
        });
        
          $(update).setStyle({position: "relative"});
          $(update).setStyle({zIndex: 999999});

          var input = $(element);
          var div = input.up("div");
          var width;
          if (div.hasClassName("droplist_input_field"))
              width = div.getWidth();
          else
              width = input.getWidth();
          $(update).setStyle({"width": width + "px"});

          Position.clone(element, update, {
            setHeight: false,
            setWidth: false,
            offsetTop: (39 - document.body.offsetHeight - element.offsetHeight)
          });

        //Calcolo della massima lunghezza della lista di autocompletamento.....
        /*var elementWidth = $(element).getWidth();

        var tmpUpdate = $(update.cloneNode(true));
        tmpUpdate.setStyle({top:'-15000px'});
        tmpUpdate.show();

        document.body.insert(tmpUpdate);

        var updateMaxWidth = tmpUpdate.select('li').max(function(item) {
            return item.down('span.informal').getWidth();
        });

        var width = Math.min(elementWidth,updateMaxWidth);

        $(tmpUpdate).setStyle({'width': width+'px'});

        tmpUpdate.select('li').each(function(width, item) {
            var spanElement = item.down('span.informal');
            spanElement.update(this.fitStringToSize(spanElement.innerHTML, width));

        }.bind(this, width));


        $(update).update(tmpUpdate.down('ul'));

        tmpUpdate.remove();

        $(update).setStyle({'width': width+'px'});*/

        update.select("li").each(function(li) {
            var informalDescendant = li.descendants().find(function(descendant) {
                return descendant.hasClassName("informal") && !descendant.hasClassName("hidden")
            });
            if (!informalDescendant) {
                var span =  new Element("span");
                span.innerHTML = li.innerHTML;
                li.update();
                li.insert(span);
            }
        });

        Effect.Appear(update,{duration:0.15});
   },

   fitStringToSize : function(str,width,className) {
      // str    A string where html-entities are allowed but no tags.
      // width  The maximum allowed width in pixels
      // className  A CSS class name with the desired font-name and font-size. (optional)
      // ----
      // _escTag is a helper to escape 'less than' and 'greater than'
      function _escTag(s){ return s.replace("<","&lt;").replace(">","&gt;");}

      //Create a span element that will be used to get the width
      var span = document.createElement("span");
      //Allow a classname to be set to get the right font-size.
      if (className) span.className=className;
      span.style.display='inline';
      span.style.visibility = 'hidden';
      span.style.padding = '0px';
      document.body.appendChild(span);

      var result = _escTag(str); // default to the whole string
      span.innerHTML = result;
      // Check if the string will fit in the allowed width. NOTE: if the width
      // can't be determinated (offsetWidth==0) the whole string will be returned.
      if (span.offsetWidth > width) {
        var posStart = 0, posMid, posEnd = str.length, posLength;
        // Calculate (posEnd - posStart) integer division by 2 and
        // assign it to posLength. Repeat until posLength is zero.
        while (posLength = (posEnd - posStart) >> 1) {
          posMid = posStart + posLength;
          //Get the string from the begining up to posMid;
          span.innerHTML = _escTag(str.substring(0,posMid)) + '&hellip;';

          // Check if the current width is too wide (set new end)
          // or too narrow (set new start)
          if ( span.offsetWidth > width ) posEnd = posMid; else posStart=posMid;
        }

        result = '<abbr title="' +
          str.replace("\"","&quot;") + '">' +
          _escTag(str.substring(0,posStart)) +
          '&hellip;<\/abbr>';
      }
      document.body.removeChild(span);
      return result;
    },

    registerOnChangeListener : function(f, name) {
        var hiddenId = this._field.readAttribute("id");
        if (Object.isFunction(f)) {
            var onChangeListenerHash = $H({});
            if (!Object.isHash(this._onChangeListener)) {
                this._onChangeListener = $H({});
            }
            if (Object.isHash(this._onChangeListener.get(hiddenId)) && this._onChangeListener.get(hiddenId).values().size() > 0)
                onChangeListenerHash = this._onChangeListener.get(hiddenId);

            if (name) {
                onChangeListenerHash.unset(name);
                onChangeListenerHash.set(name, f);
            } else if (!onChangeListenerHash.values().include(f)) {
                var counter = 1;
                do { name = counter++ } while (onChangeListenerHash.get(name));
                onChangeListenerHash.set(name, f);
            }
            this._onChangeListener.set(hiddenId, onChangeListenerHash);
        }
    },

    deregisterOnChangeListener : function(f, name) {
        var hiddenId = this._field.readAttribute("id");

        if (Object.isFunction(f) && typeof this._onChangeListener !== 'undefined' && this._onChangeListener.keys().size() > 0 && Object.isHash(this._onChangeListener.get(hiddenId)) && this._onChangeListener.get(hiddenId).keys().size() > 0) {
            var onChangeListenerHash = this._onChangeListener.get(hiddenId);
            if (name) {
                onChangeListenerHash.unset(name);
            } else if (onChangeListenerHash.values().include(f)) {
                onChangeListenerHash.keys().each(function(key) {
                    var fn = onChangeListenerHash.get(key);
                    if (fn === f) {
                        onChangeListenerHash.unset(key);
                        throw $break;
                    }
                });
            }
        }

        /*if (Object.isFunction(f) && typeof this._onChangeListener !== 'undefined' && this._onChangeListener.keys().size() > 0 && Object.isArray(this._onChangeListener.get(hiddenId)) && this._onChangeListener.get(hiddenId).size() > 0) {
            var onChangeListenerArray = this._onChangeListener.get(hiddenId);
            onChangeListenerArray.each(function (fn){
                if (Object.inspect(fn) == Object.inspect(f)) {
                    onChangeListenerArray = onChangeListenerArray.without(fn);
                    if (onChangeListenerArray.size() > 0)
                        this._onChangeListener.set(hiddenId, onChangeListenerArray);
                    else
                        this._onChangeListener.unset(hiddenId);
                 }
            }.bind(this));
        }*/
    },

    clearInstance : function() {
        Event.stopObserving(this._submitField, 'click');

        var update = this._createUpdateId(this.options);
        if (update && Object.isElement($(update))) {
            $(update).remove();
        }

        if (Object.isHash(this._onChangeListener))
            this._onChangeListener = $H({});

        if (this._autocompleter)
            this._autocompleter = null;
    },
    
    setValue : function(editFieldValue, codeFieldValue) {
//    	this._editField.writeAttribute("value", editFieldValue);
//    	this._codeField.writeAttribute("value", codeFieldValue);
    	this._editField.setValue(editFieldValue);
    	this._codeField.setValue(codeFieldValue);
    },
    
    _changeIndex: function(attr, newIdx, global) {
        if (global)
            return attr.replace(/(_o_[0-9]+)/g, "_o_" + newIdx);
        return attr.replace(/(_o_[0-9]+)/, "_o_" + newIdx);
    },
     
    executeUnitTests: function() {
    	fieldName = "customMethodId";
     	this._assertEquals("Qualsiasi valore senza caratteri speciali", this._replaceSpecialCharacters(fieldName, "Qualsiasi valore senza caratteri speciali"));
    	this._assertEquals("1 < 2", this._replaceSpecialCharacters(fieldName, "1 &lt; 2"));
    	this._assertEquals("1 << 1000", this._replaceSpecialCharacters(fieldName, "1 &lt;&lt; 1000"));
    	this._assertEquals("9 > 8", this._replaceSpecialCharacters(fieldName, "9 &gt; 8"));
    	this._assertEquals("9000 > > 8 con uno spazio", this._replaceSpecialCharacters(fieldName, "9000 &gt; &gt; 8 con uno spazio"));
    	this._assertEquals("Pésca <> Pèsca", this._replaceSpecialCharacters(fieldName, "Pésca &lt;&gt; Pèsca"));
    	
    	fieldName = "anotherIgnoredField";
    	this._assertEquals("Qualsiasi valore senza caratteri speciali", this._replaceSpecialCharacters(fieldName, "Qualsiasi valore senza caratteri speciali"));
    	this._assertEquals("1 &lt; 2", this._replaceSpecialCharacters(fieldName, "1 &lt; 2"));

    }, 
    
    _assertEquals: function(expected, actual) {
    	if (expected != actual) {
    		console.error("[DropList.js::_assertEquals] actual (" + actual + ") is different from expected (" + expected + ")");
    	} else {
    		console.info("[DropList.js::_assertEquals] actual and expected are the same (as expected): " + actual);
    	}
    }
});

/**
 * DropList Manager
 */
var DropListMgr = Class.create({ });

/**
 * Load all droplist in page
 */
DropListMgr.loadElement = function(baseElement, newInstance, index, options) {

    //Recupero la mappa di elementi droplist nell'array cache
    if (!(Object.isArray(DropListMgr._elementMapArray) && DropListMgr._elementMapArray.size() > 0)) {
        DropListMgr._elementMapArray = new Array();
    }

    var elementMap = null;
    if (newInstance || DropListMgr._elementMapArray.size() == 0) {
        elementMap = $H({});
        DropListMgr._elementMapArray.push(elementMap);
    }
    else if (Object.isNumber(index) && DropListMgr._elementMapArray.size() > index){
        elementMap = DropListMgr._elementMapArray[index];
    }
    else {
        elementMap = DropListMgr._elementMapArray.last();
    }

    //Per prima cosa verifico che eventuali autocompleter presenti abbiamo ancora
    //validita, solo se non richiesta una nuova instanza
    if (!newInstance) {
        var autocompleterPanelList = $$('div.autocomplete');
        if (Object.isArray(autocompleterPanelList) && autocompleterPanelList.size() > 0) {
            autocompleterPanelList.each(function(autocompleterPanel) {
                var id = autocompleterPanel.identify();

                var suffix = '_autoCompleterOptions' + ((options && options.suffix) ? '_' + options.suffix : '');
                if (id.indexOf(suffix) != -1) {
                    var idAutocompleterComponent = id.substring(0, id.indexOf(suffix));

                    //Elimino l'oggetto dalla cache degli autocompleter solo se ï¿½ presente un autocompleter
                    //con lo stesso id nel nuovo contenuto da inserire (baseElement) in quanto dovrï¿½ riaggiornare
                    //l'oggetto js associato, oppure quando non ho un contenuto da inserire (non sono in risposta
                    //ad una chiamata ajax) e nel DOM non ï¿½ presente nessuno componente con l'idAutocompleterComponent
                    if ((Object.isElement($(baseElement)) && Object.isElement($(baseElement).down('#' + idAutocompleterComponent)) && (Object.isElement($(baseElement).down('#' + idAutocompleterComponent).down('input.autocompleter_option')) || Object.isElement($(baseElement).down('#' + idAutocompleterComponent).down('input.autocompleter_parameter')))) || !Object.isElement($(idAutocompleterComponent)) ) {
                        if (Object.isHash(elementMap)) {
                            elementMap.unset(idAutocompleterComponent);

                            if (elementMap.keys().size() == 0) {
                                DropListMgr._elementMapArray = DropListMgr._elementMapArray.without(elementMap);
                                elementMap = $H({});
                                DropListMgr._elementMapArray.push(elementMap);
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
        DropListMgr._droplistArray = $(baseElement).select('div.droplist_field');
    } else {
        DropListMgr._droplistArray = $$('div.droplist_field');
    }

    if (Object.isHash(elementMap)) {
        DropListMgr._droplistArray.each( function(item) {
            var key = $(item).readAttribute("id");
            var itemFound = elementMap.get(key);
            if (Object.isUndefined(itemFound) || itemFound._field != item) {
                elementMap.set(key, new DropList(item, options));
            }
        });
    }

    if (Object.isHash(elementMap))
        return DropListMgr._elementMapArray.indexOf(elementMap);
    else
        return null;
};

DropListMgr.getDropList = function(id, index) {
	if (!Object.isNumber(index)) {
    	index = (Object.isArray(DropListMgr._elementMapArray) ? DropListMgr._elementMapArray.size()-1 : 0);
	}

    if (Object.isArray(DropListMgr._elementMapArray) && DropListMgr._elementMapArray.size() > index) {
        var elementMap = DropListMgr._elementMapArray[index];
        if (Object.isHash(elementMap)) {
            return elementMap.get(id);
        }
    }
    return null;
};

DropListMgr.clearInstance = function(instance) {
    if (Object.isArray(DropListMgr._elementMapArray) && DropListMgr._elementMapArray.size() > 0) {
        if (Object.isNumber(instance)) {
            if (DropListMgr._elementMapArray.size() > instance) {
                instance = DropListMgr._elementMapArray[instance];
                DropListMgr._elementMapArray = DropListMgr._elementMapArray.without(instance);
            }
        } else if (Object.isHash(instance)){
            DropListMgr._elementMapArray = DropListMgr._elementMapArray.without(instance);
        }

        if (Object.isHash(instance)) {
            instance.each(function(pair) {
                var dropListElement = pair.value;

                dropListElement.clearInstance();
            });
        }
    }
}

//*****************
//Init object
//*****************
document.observe("dom:loaded", function() {
   if (typeof DropListMgr != 'undefined') {
       DropListMgr.loadElement();
   }
});
