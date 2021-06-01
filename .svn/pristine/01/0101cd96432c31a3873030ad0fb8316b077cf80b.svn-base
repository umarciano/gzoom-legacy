package com.mapsengineering.base.bl.validation.validator;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.commons.validator.routines.DoubleValidator;
import org.apache.commons.validator.util.ValidatorUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.util.MessageUtil;

/**
 * Static base validation routines
 *
 * @author sandro
 *
 */
public class BaseValidationRoutines {
	
	private BaseValidationRoutines() {}
	
	/**
	 * Mandatory field validator
	 * @param bean
	 * @param field
	 * @return True if field is valid
	 */
	public static boolean fieldRequired(Object bean, Field field) {
	    String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
	    return GenericValidator.isBlankOrNull(value)? false: true;
	}

	/**
	 * Date format validation
	 * @param bean
	 * @param field
	 * @return True if field is valid
	 */
	public static boolean dateFormatValidation(Object bean, Field field, Locale locale) {
	    String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
	    return GenericValidator.isDate(value, locale);
	}

	/**
	 * Double type validation (see GPlus Standard GUI Specifics)
	 * @param bean
	 * @param field
	 * @return Formatted value if validation ok, Validation error message otherwise
	 */
	public static ValidatorResult doubleFormatValidation(Object bean, Field field, Locale locale) {
		//Get decimal digits field name
		ValidatorResult res = new ValidatorResult(field);
		String decimalDigits = ValidatorUtils.getValueAsString(bean, field.getVarValue("decimalDigitsFieldName"));
		int maxDigits = 0;
		if (UtilValidate.isNotEmpty(decimalDigits)) { 
			try {
				maxDigits = Integer.parseInt(decimalDigits);
			} catch (Exception e) { }
		}
		
		//Get field value to check
	    String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
	    
	    NumberFormat nf = NumberFormat.getNumberInstance(locale);
	    nf.setMaximumFractionDigits(maxDigits);
	    nf.setMinimumFractionDigits(maxDigits);
	    nf.setParseIntegerOnly(false);
	    nf.setGroupingUsed(true);
	    try {
	    	//Tranform string to double
	    	DoubleValidator dv = DoubleValidator.getInstance();
	    	Double d = dv.validate(value, locale);
	    	if (d==null) {
	    		String errMsg = MessageUtil.getErrorMessage("DoubleValidationError", locale, UtilMisc.toList(value));
	    		res.add(field.getKey(), false, errMsg);
	    		return res;
	    	}
	    	//Format double to locale-format set before
	    	String formatted = nf.format(d);
	    	res.add(field.getKey(), true, formatted);
	    	
	    } catch (Exception e) {
	    	String message = MessageUtil.getErrorMessage("DoubleValidationError", locale, UtilMisc.toList(value));
	    	res.add(field.getKey(), false, message);
	    }
	    return res;
	}
	
	/**
	 * Checks validity of date pair (Last date >= dateFrom)
	 * @param bean looks for a field named dateFromFieldName into the bean. Either date values has to be Date fields.
	 * @param field value to check 
	 * @param locale
	 * @return True if check ok.
	 */
	public static boolean datePairValidity(Object bean, Field field, Locale locale) {
		
		String dateFromFieldName = field.getVarValue("dateFromFieldName");
		if (UtilValidate.isEmpty(dateFromFieldName)) {
			return false;
		}

		try {
				
			Date dateThru = (Date)PropertyUtils.getProperty(bean, field.getProperty());			
			Date dateFrom = (Date)PropertyUtils.getProperty(bean, dateFromFieldName);
			
			if (dateFrom==null||dateThru==null) {
				return false;
			}
			
			Calendar calFrom = Calendar.getInstance(locale);
			Calendar calThru = Calendar.getInstance(locale);
			calFrom.setTime(dateFrom);
			calThru.setTime(dateThru);
			
			CalendarValidator cv = CalendarValidator.getInstance();
			//Compare days of month
			return (cv.compareDates(calFrom, calThru) <= 0) ? true: false;
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
}
