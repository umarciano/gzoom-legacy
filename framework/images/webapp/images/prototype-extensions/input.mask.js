/*
 *  Maps spa - Sandro 2009.04.23  
 */

/**
 * Common Mask
 */
var InputMask = Class.create({
	/**
	 * Class constructor
	 */
	initialize: function(field, options) {
		this._baseInitialize(field, options);
	},

	/**
	 * Common initialization
	 */
	_baseInitialize: function(field, options) {
		this._input = $(field);
		
		//try to get some attributes form field
		var textAlign = this._input.getStyle("text-align");
		if (!textAlign) {
			textAlign = "left";
		}
		
		//Default options
		this._options = $({
			type: "generic",
			textAlign: textAlign,
			onChange: this.onChange,
			regex: null, 
			validatorFormName: null,
		    validator: null			
		});
		
		//Overrides options
		Object.extend(this._options, options || {});
		
		//Set some options on the field
		var style = $H({ 
			textAlign: this._options.get("textAlign")
		});
		this._input.setStyle(style.toObject());
		
		//override del metodo setValue in modo che venga formattato
		this._input.setValue = this._input.setValue.wrap(
			function(proceed, value) {
				this.setFormattedValue(proceed, value);
			}.bind(this)
		);
		
		// Formatta anche i numeri che non hanno alcuna cifra dopo la virgola,
		// in questo caso aggiunge 00
		this._input.setValue(this._input.getValue());
		
		//Starting event observation
		Event.observe(this._input, "change", this._options.onChange.bind(this));
	},
	
	/**
	 * Base onchange event 
	 */
	onChange: function(event) {
		//validateValue method has to be defined by overriedes
		this._input.setValue(this._input.getValue());
	}
	
});

/**
 * Remote validation inputmask
 */
var RemoteInputMask = Class.create(InputMask, {
	
	initialize: function(field, options) {
		var localOptions = $H({});
		localOptions.set("type", "remote");
		localOptions.set("textAlign", "left");
		localOptions.set("onChange", this.onChange.bind(this));
		localOptions.set("decimalDigits", 2); //Example on validation decimal value
		
		localOptions.set("validatorFormName", "CommonValidatorRoutines");
		localOptions.set("validator", null);

		Object.extend(localOptions, options || {});
		//init base options
		this._baseInitialize(field, localOptions);
	},

	setFormattedValue: function(proceed, fieldValue) {
		
		//Specific parameters for validation routine
		var otherParm = $H({});
		otherParm.set("decimalDigits", this._options.get("decimalDigits"));
		
		var parms = $H({});
		//Set fieldName to validate and field value 
		//Field name is the name that validation routine expect
		parms.set("fieldName", "doubleFormatValidation");
		parms.set("fieldValue", fieldValue);

		//Set form name and validator
		parms.set("formName", this._options.get("validatorFormName"));
		parms.set("validator", this._options.get("validator"));
		
		//Add other parameters
		if (routineParams) {
			parms = parms.merge(routineParams);
		}
		
		//do validation
		new Ajax.Request(getOfbizUrl("fieldValidation"), {
			method: 'post',
			parameters: parms,
			onSuccess: function(transport) {
				if (transport.responseText) {
					this.performResponse(proceed, transport.responseText);
				}
			}.bind(this)
		});
	},
	
	/**
	 * Do specific perform on response
	 */
	performResponse: function(proceed, responseText) {
		var div = new Element("div");
		div.update(responseText);
		//Looking in for results
		var response = div.down("li.response");
		if (response) {
			//Validation OK
			if (response.innerHTML=="OK") {
				var valueEl = div.down("li.formatted-value");
				if (valueEl) {
					proceed(valueEl.innerHTML);
					return;
				}
			}
			//Validation Errors
			if (response.innerHTML=="ERROR") {
				var valueEl = div.down("li.error-message");
				if (valueEl) {
					Modalbox.alert(valueEl.innerHTML, null, null);
					return;
				}
			}
		}
	}
});


/**
 * Double inputmask
 */
var DoubleInputMask = Class.create(InputMask, {
	
	initialize: function(field, options) {
		var localOptions = $H({});
		localOptions.set("type", "double");
		localOptions.set("textAlign", "right");
		localOptions.set("onChange", this.onChange.bind(this));
		var decDigits = new Number(field.readAttribute("decimal_digits"));
		localOptions.set("decimalDigits", isNaN(decDigits) ? 0 : decDigits);
		localOptions.set("validatorFormName", "CommonValidatorRoutines");
		localOptions.set("validator", null);

		Object.extend(localOptions, options || {});
		
		//init base options
		this._baseInitialize(field, localOptions);
		
		//set validity attibutes
		this._maxValue = this._input.readAttribute("validity-max-value");
	},

	/**
	 * field value validation
	 */
	setFormattedValue: function(proceed, fieldValue) {
		//Check if loaded FormatMgr
		if (Object.isUndefined(FormatMgr)) {
			//TODO: alert
			return;
		} 
		
		//frm is type of NumberFormat
		var frm = FormatMgr.formatDecimal(fieldValue, this._options.get("decimalDigits"), this._maxValue, false);
		if (!frm.isValid()) {
			//TODO: alert
			proceed(null);			
			return;
		}
		//Check maxval
		if (this._maxValue) {
			var unf = frm.toUnformatted();
			var max = Math.max(this._maxValue, unf);
			if (unf==max) {
				//TODO : Localizzare il messaggio e sostituire alert
				alert("Valore superiore al max consentito: " + this._maxValue);
				proceed(null);				
				return;
			}
		}
		
		proceed(frm.toFormatted());
	},
	
    setDecimalDigits : function(numberDecimalDigits) {
	   try {
    	   if(Object.isNumber(numberDecimalDigits)) {
    	       this._options.set("decimalDigits", numberDecimalDigits);
    	   }
    	   else if(Object.isElement(numberDecimalDigits)) {
    	       this._options.set("decimalDigits", numberDecimalDigits.getValue());
    	   } else {
    	       this._options.set("decimalDigits", 2);
    	   }
       }	   
	   catch (e){
	       this._options.set("decimalDigits", 2);
	   }
	}
});

/**
 * Integer inputMask
 */
var IntegerInputMask = Class.create(InputMask, {
	
	initialize: function(field, options) {
		var localOptions = $H({});
		localOptions.set("type", "integer");
		localOptions.set("textAlign", "right");
		localOptions.set("onChange", this.onChange.bind(this));

		Object.extend(localOptions, options || {});
		//init base options
		this._baseInitialize(field, localOptions);
		
		//set validity attibutes
		this._maxValue = this._input.readAttribute("validity-max-value");
	},

	/**
	 * On change on field value
	 */
	setFormattedValue: function(proceed, fieldValue) { 
		//Check if loaded FormatMgr
		if (Object.isUndefined(FormatMgr)) {
			//TODO: alert
			return;
		} 
		
		//frm is type of NumberFormat
		var frm = FormatMgr.formatInteger(fieldValue);
		if (!frm.isValid()) {
			//TODO: alert
			proceed(null);
			return;
		}
		//Check maxval
		if (this._maxValue) {
			var unf = frm.toUnformatted();
			var max = Math.max(this._maxValue, unf);
			if (unf==max) {
				//TODO : Localizzare il messaggio e sostituire alert
				alert("Valore superiore al max consentito: " + this._maxValue);
				proceed(null);
				return;
			}
		}
		//set field
		proceed(frm.toFormatted());
	}
});

/**
 * Input Mask management static methods and utility
 */
var InputMaskManager = Class.create({});

InputMaskManager.maskFactory = function(field, className, options) {
	var obj = null;
	switch(className) {
	case "mask_double": 
		obj = new DoubleInputMask(field, options);
		break;
	case "mask_integer": 
		obj = new IntegerInputMask(field, options);
		break;
	}
	return obj;
}

/**
 * Set input mask on field
 */
InputMaskManager.setInputMask = function(input) {
	input = $(input);
	if (input) {
		//remove input_mask class
		input.removeClassName("input_mask");
		
		var classNames = input.getAttribute("class");
		//Now build exact manager by factory based on mask_*type*  class
		var matches = $A(/mask_(\w+)/.exec(classNames));
		matches.each( function(match) {
			var mask = InputMaskManager.maskFactory(input, match, {});
			//remove also thei class name
			input.removeClassName(match);
			if(mask)
			    Object.extend(input, mask);
		});
	}
}

InputMaskManager.loadAllMask = function(element) {
	
	var fields = (element) ? $(element).select('input.input_mask') : $$('input.input_mask');
	if (fields) {
		fields.each( function(item) {
			InputMaskManager.setInputMask(item);
		});
	}
}


//**************
// Initialize at dom 
//
document.observe("dom:loaded" , function() {

	InputMaskManager.loadAllMask();
	
});

