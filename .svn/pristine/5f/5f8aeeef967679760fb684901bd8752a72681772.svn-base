package com.mapsengineering.base.bl.validation;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

public class DatetimeResolver extends FieldResolver {

	protected Object doResolve(String fieldName, Object inputField,
			ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {

		//Se input = Date - Ok
		if (inputField instanceof java.sql.Timestamp) {
			return inputField;
		}

		//Timestamp - conversione
		if (inputField instanceof java.util.Date) {
			Timestamp res = new Timestamp(((java.util.Date)inputField).getTime());
			return res;
		}

		if (inputField instanceof java.sql.Date) {
			Timestamp res = new Timestamp(((java.sql.Date)inputField).getTime());
			return res;
		}
		
		if (inputField instanceof String) {

			String date = ((String)inputField).trim();
			Timestamp res = ValidationUtil.checkDateTimeAgainstLocale(date, locale);
			if (UtilValidate.isEmpty(res)) {
				errorMap.putAll(MessageUtil.buildErrorMap("InvalidDateFormat", locale, UtilMisc.toList(date, locale.getDisplayName())));
				return null;
			}
			return res;
		}
		
		//Altri casi segnalo incompatibilità
		errorMap.putAll(MessageUtil.buildErrorMap("InvalidDateFormat", locale, UtilMisc.toList(inputField.toString(), locale.getDisplayName())));
		
		return null;
	}

}
