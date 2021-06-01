package com.mapsengineering.base.bl.validation;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

public class DateResolver extends FieldResolver {

	protected  Object doResolve(String fieldName, Object inputField,
			ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {

		//Se input = Date - Ok
		if (inputField instanceof java.sql.Date) {
			return inputField;
		}

		//Util date - conversione
		if (inputField instanceof java.util.Date) {
			Date date = new Date(((java.util.Date)inputField).getTime());
			return date;
		}

		//Timestamp - conversione
		if (inputField instanceof java.sql.Timestamp) {
			
			Date date = new Date(((Timestamp)inputField).getTime());
			return date;
		}

		//String - conversione
		if (inputField instanceof String) {

			String dateStr = ((String)inputField).trim();
			
			Date date = ValidationUtil.checkDateAgainstLocale(dateStr, locale);
			if (UtilValidate.isEmpty(date)) {
				errorMap.putAll(MessageUtil.buildErrorMap("InvalidDateFormat", locale, UtilMisc.toList(dateStr, locale.getDisplayName())));
				return null;
			}
			return date;
		}

		//Altri casi segnalo incompatibilità
		errorMap.putAll(MessageUtil.buildErrorMap("InvalidDateFormat", locale, UtilMisc.toList(inputField.toString(), locale.getDisplayName())));
		
		return null;
	}

}
