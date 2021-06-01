
/***
 * Maps spa. Sandro 12/05/2009.
 * Locale number formatting class
 * Based on original work by www.mredkj.com
 */
var NumberFormatter = Class.create({
	
	initialize: function()	{
		this.VERSION = 'Number Format v1.5.4';
		this.COMMA = ',';
		this.PERIOD = '.';
		this.DASH = '-'; 
		this.LEFT_PAREN = '('; 
		this.RIGHT_PAREN = ')'; 
		this.LEFT_OUTSIDE = 0; 
		this.LEFT_INSIDE = 1;  
		this.RIGHT_INSIDE = 2;  
		this.RIGHT_OUTSIDE = 3;  
		this.LEFT_DASH = 0; 
		this.RIGHT_DASH = 1; 
		this.PARENTHESIS = 2; 
		this.NO_ROUNDING = -1 
		this.num;
		this.numOriginal;
		this.hasSeparators = false;  
		this.separatorValue;  
		this.inputDecimalValue; 
		this.decimalValue;  
		this.negativeFormat; 
		this.negativeRed; 
		this.hasCurrency;  
		this.currencyPosition;  
		this.currencyValue;  
		this.places;
		this.roundToPlaces; 
		this.truncate;
		//Flag for number
		this._isValid = true;
		
		this.setCommas(true);
		this.setNegativeFormat(this.LEFT_DASH); 
		this.setNegativeRed(false); 
		this.setCurrency(false); 
		this.setCurrencyPrefix('$');
		this.setPlaces(2);
	},

	setInputDecimal: function(val)
	{
		this.inputDecimalValue = val;
	},
	
	setNumber: function(num, inputDecimal)
	{
		if (inputDecimal != null) {
			this.setInputDecimal(inputDecimal); 
		}
		this.numOriginal = num;
		this.num = this.justNumber(num);
	},
	
	toUnformatted: function()
	{
		return (this.num);
	},
	
	getOriginal: function()
	{
		return (this.numOriginal);
	},
	
	setNegativeFormat: function(format)
	{
		this.negativeFormat = format;
	},
	
	setNegativeRed: function(isRed)
	{
		this.negativeRed = isRed;
	},
	
	setSeparators: function(isC, separator, decimal)
	{
		this.hasSeparators = isC;
		if (separator == null) separator = this.COMMA;
		if (decimal == null) decimal = this.PERIOD;
		if (separator == decimal) {
			this.decimalValue = (decimal == this.PERIOD) ? this.COMMA : this.PERIOD;
		} else {
			this.decimalValue = decimal;
		}
		this.separatorValue = separator;
	},
	
	setCommas: function(isC)
	{
		this.setSeparators(isC, this.COMMA, this.PERIOD);
	},
	
	setCurrency: function(isC)
	{
		this.hasCurrency = isC;
	},
	
	setCurrencyValue: function(val)
	{
		this.currencyValue = val;
	},
	
	setCurrencyPrefix: function(cp)
	{
		this.setCurrencyValue(cp);
		this.setCurrencyPosition(this.LEFT_OUTSIDE);
	},
	
	setCurrencyPosition: function(cp)
	{
		this.currencyPosition = cp
	},
	
	setPlaces: function(p, tr)
	{
		this.roundToPlaces = !(p == this.NO_ROUNDING); 
		this.truncate = (tr != null && tr); 
		this.places = (p < 0) ? 0 : p; 
	},
	
	addSeparators: function(nStr, inD, outD, sep)
	{
		nStr += '';
		var dpos = nStr.indexOf(inD);
		var nStrEnd = '';
		if (dpos != -1) {
			nStrEnd = outD + nStr.substring(dpos + 1, nStr.length);
			nStr = nStr.substring(0, dpos);
		}
		var rgx = /(\d+)(\d{3})/;
		while (rgx.test(nStr)) {
			nStr = nStr.replace(rgx, '$1' + sep + '$2');
		}
		return nStr + nStrEnd;
	},
	
	toFormatted: function()
	{	
		var pos;
		var nNum = this.num; 
		var nStr;            
		var splitString = new Array(2);   
		if (this.roundToPlaces) {
			nNum = this.getRounded(nNum);
			nStr = this.preserveZeros(Math.abs(nNum)); 
		} else {
			nStr = this.expandExponential(Math.abs(nNum)); 
		}
		if (this.hasSeparators) {
			nStr = this.addSeparators(nStr, this.PERIOD, this.decimalValue, this.separatorValue);
		} else {
			nStr = nStr.replace(new RegExp('\\' + this.PERIOD), this.decimalValue); 
		}
		var c0 = '';
		var n0 = '';
		var c1 = '';
		var n1 = '';
		var n2 = '';
		var c2 = '';
		var n3 = '';
		var c3 = '';
		var negSignL = (this.negativeFormat == this.PARENTHESIS) ? this.LEFT_PAREN : this.DASH;
		var negSignR = (this.negativeFormat == this.PARENTHESIS) ? this.RIGHT_PAREN : this.DASH;
		if (this.currencyPosition == this.LEFT_OUTSIDE) {
			if (nNum < 0) {
				if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n1 = negSignL;
				if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n2 = negSignR;
			}
			if (this.hasCurrency) c0 = this.currencyValue;
		} else if (this.currencyPosition == this.LEFT_INSIDE) {
			if (nNum < 0) {
				if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n0 = negSignL;
				if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n3 = negSignR;
			}
			if (this.hasCurrency) c1 = this.currencyValue;
		}
		else if (this.currencyPosition == this.RIGHT_INSIDE) {
			if (nNum < 0) {
				if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n0 = negSignL;
				if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n3 = negSignR;
			}
			if (this.hasCurrency) c2 = this.currencyValue;
		}
		else if (this.currencyPosition == this.RIGHT_OUTSIDE) {
			if (nNum < 0) {
				if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n1 = negSignL;
				if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n2 = negSignR;
			}
			if (this.hasCurrency) c3 = this.currencyValue;
		}
		nStr = c0 + n0 + c1 + n1 + nStr + n2 + c2 + n3 + c3;
		if (this.negativeRed && nNum < 0) {
			nStr = '<font color="red">' + nStr + '</font>';
		}
		return (nStr);
	},
	
	toPercentage: function()
	{
		nNum = this.num * 100;
		nNum = this.getRounded(nNum);
		return nNum + '%';
	},
	
	getZeros: function(places)
	{
		var extraZ = '';
		var i;
		for (i=0; i<places; i++) {
			extraZ += '0';
		}
		return extraZ;
	},
	
	expandExponential: function(origVal)
	{
		if (isNaN(origVal)) return origVal;
		var newVal = parseFloat(origVal) + ''; 
		var eLoc = newVal.toLowerCase().indexOf('e');
		if (eLoc != -1) {
			var plusLoc = newVal.toLowerCase().indexOf('+');
			var negLoc = newVal.toLowerCase().indexOf('-', eLoc); 
			var justNumber = newVal.substring(0, eLoc);
			if (negLoc != -1) {
				var places = newVal.substring(negLoc + 1, newVal.length);
				justNumber = this.moveDecimalAsString(justNumber, true, parseInt(places));
			} else {
				if (plusLoc == -1) plusLoc = eLoc;
				var places = newVal.substring(plusLoc + 1, newVal.length);
				justNumber = this.moveDecimalAsString(justNumber, false, parseInt(places));
			}
			newVal = justNumber;
		}
		return newVal;
	},
	
	moveDecimalRight: function(val, places)
	{
		var newVal = '';
		if (places == null) {
			newVal = this.moveDecimal(val, false);
		} else {
			newVal = this.moveDecimal(val, false, places);
		}
		return newVal;
	},
	
	moveDecimalLeft: function(val, places)
	{
		var newVal = '';
		if (places == null) {
			newVal = this.moveDecimal(val, true);
		} else {
			newVal = this.moveDecimal(val, true, places);
		}
		return newVal;
	},
	
	moveDecimalAsString: function(val, left, places)
	{
		var spaces = (arguments.length < 3) ? this.places : places;
		if (spaces <= 0) return val; 
		var newVal = val + '';
		var extraZ = this.getZeros(spaces);
		var re1 = new RegExp('([0-9.]+)');
		if (left) {
			newVal = newVal.replace(re1, extraZ + '$1');
			var re2 = new RegExp('(-?)([0-9]*)([0-9]{' + spaces + '})(\\.?)');		
			newVal = newVal.replace(re2, '$1$2.$3');
		} else {
			var reArray = re1.exec(newVal); 
			if (reArray != null) {
				newVal = newVal.substring(0,reArray.index) + reArray[1] + extraZ + newVal.substring(reArray.index + reArray[0].length); 
			}
			var re2 = new RegExp('(-?)([0-9]*)(\\.?)([0-9]{' + spaces + '})');
			newVal = newVal.replace(re2, '$1$2$4.');
		}
		newVal = newVal.replace(/\.$/, ''); 
		return newVal;
	},
	
	moveDecimal: function(val, left, places)
	{
		var newVal = '';
		if (places == null) {
			newVal = this.moveDecimalAsString(val, left);
		} else {
			newVal = this.moveDecimalAsString(val, left, places);
		}
		return parseFloat(newVal);
	},
	
	getRounded: function(val)
	{
		val = this.moveDecimalRight(val);
		if (this.truncate) {
			val = val >= 0 ? Math.floor(val) : Math.ceil(val); 
		} else {
			val = Math.round(val);
		}
		val = this.moveDecimalLeft(val);
		return val;
	},
	
	preserveZeros: function(val)
	{
		var i;
		val = this.expandExponential(val);
		if (this.places <= 0) return val; 
		var decimalPos = val.indexOf('.');
		if (decimalPos == -1) {
			val += '.';
			for (i=0; i<this.places; i++) {
				val += '0';
			}
		} else {
			var actualDecimals = (val.length - 1) - decimalPos;
			var difference = this.places - actualDecimals;
			for (i=0; i<difference; i++) {
				val += '0';
			}
		}
		return val;
	},
	
	/**
	 * Check decimal number
	 */
	justNumber: function(val)
	{
		newVal = val + '';
		var isPercentage = false;
		if (newVal.indexOf('%') != -1) {
			newVal = newVal.replace(/\%/g, '');
			isPercentage = true; 
		}
		var re = new RegExp('[^\\' + this.inputDecimalValue + '\\d\\-\\+\\(\\)]', 'g');
		
		newVal = newVal.replace(re, '');
		//if newVal == "" is not a number
		if (newVal=='') {
			this._isValid = false;
		} else {
			this._isValid = true
		}
		
		var tempRe = new RegExp('[' + this.inputDecimalValue + ']', 'g');
		var treArray = tempRe.exec(newVal); 
		if (treArray != null) {
			var tempRight = newVal.substring(treArray.index + treArray[0].length); 
			newVal = newVal.substring(0,treArray.index) + this.PERIOD + tempRight.replace(tempRe, ''); 
		}
		if (newVal.charAt(newVal.length - 1) == this.DASH ) {
			newVal = newVal.substring(0, newVal.length - 1);
			newVal = '-' + newVal;
		}
		else if (newVal.charAt(0) == this.LEFT_PAREN
				&& newVal.charAt(newVal.length - 1) == this.RIGHT_PAREN) {
			newVal = newVal.substring(1, newVal.length - 1);
			newVal = '-' + newVal;
		}
		newVal = parseFloat(newVal);
		if (!isFinite(newVal)) {
			newVal = 0;
		}
		if (isPercentage) {
			newVal = this.moveDecimalLeft(newVal, 2);
		}
		return newVal;
	},
	
	isValid: function() {
		return this._isValid;
	}
	
});



/**
 * Locale settings, add here more locale settings
 */
LOCALE_TABLE = {
	'it-it': { decimal_sep: ',', thousand_sep: '.', default_decimal: 2 },
	'en-en': { decimal_sep: '.', thousand_sep: ',', default_decimal: 2 },
	'default': { decimal_sep: ',', thousand_sep: '.', default_decimal: 2}
};



/**
 * Formatter locale manager
 */
var FormatMgr = Class.create({});

/**
 * Init object against Locale
 */
FormatMgr.loadLocale = function(localeString) {
	
	if (localeString) {
//		if (Object.isUndefined(FormatMgr._localeMap)) {
//			FormatMgr._localeMap = $({});
//		}
//		
//		var path = "/images/prototype-extensions/format/";
//		var formatterName = "formatter_" + localeString +".js";
//		
//		if (FormatMgr._localeMap.get(formatterName)==null) {
//			dhtmlLoadScript(path + formatterName);
//			FormatMgr._localeMap.set(formatterName, formatterName);
//		}
		var locale = LOCALE_TABLE[localeString];
		if (!locale) {
			locale = LOCALE_TABLE['default'];
		}
		
		FormatMgr.localeSet = locale;
		
		if (Object.isUndefined(FormatMgr._formatterMap)) {
			FormatMgr._formatterMap = $H({});
		}
		
		//Set Decimal formatter
		var frm = new NumberFormatter();
		//Sets locale settings
		frm.setInputDecimal(locale.decimal_sep);
		frm.setCurrency(false);
		frm.setNegativeFormat(frm.LEFT_DASH); //minus sign
		frm.setNegativeRed(false);
		frm.setSeparators(true, locale.thousand_sep, locale.decimal_sep);
		frm.setPlaces(locale.default_decimal, true);
		//Store formatter
		FormatMgr._formatterMap.set("decimal-formatter", frm);
		
		//Set Integer formatter
		var frm = new NumberFormatter();
		//Sets locale settings
		frm.setInputDecimal(locale.decimal_sep);
		frm.setCurrency(false);
		frm.setNegativeFormat(frm.LEFT_DASH);
		frm.setNegativeRed(false);
		frm.setSeparators(true, locale.thousand_sep);
		frm.setPlaces(0, true);
		//Store formatter
		FormatMgr._formatterMap.set("integer-formatter", frm);
		
	}
};

/**
 * Get DecimalFormatter
 */
FormatMgr.getValidator = function(key) {
	return FormatMgr._formatterMap.get(key);
};

/**
 * Decimal formatting method
 */
FormatMgr.formatDecimal = function(num, decimalPlace, maxVal, truncate) {
	var frm = FormatMgr.getValidator("decimal-formatter");
	if (!decimalPlace) {
		//default value
		decimalPlace = FormatMgr.localeSet.default_decimal; 
	} 
	
	frm.setPlaces(decimalPlace, truncate);
	frm.setNumber(num);

	//Formatter return always a num, 0 case of error
	return frm;
};

/**
 * Integer formatting method
 */
FormatMgr.formatInteger = function(num) {
	var frm = FormatMgr.getValidator("integer-formatter");
	frm.setNumber(num);
	//Formatter return always a num, 0 case of error
	return frm;
};

/**
 * Load manager with dom
 */
document.observe("dom:loaded" , function() {
	
	//Look for locale setting
	var fields = $$('input[type=hidden].localeFormatHolder');
	var locale = 'default'; //set default
	var localeField = fields.first();
	if (localeField) {
		locale = localeField.getValue();
	}
		
	FormatMgr.loadLocale(locale);
});

