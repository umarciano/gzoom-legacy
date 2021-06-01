package com.mapsengineering.base.bl.validation;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;

public class StringResolver extends FieldResolver {

	protected Object doResolve(String fieldName, Object inputField,
			ModelFieldType expectedField, Map<String, Object> errorMap,
			Locale locale) {

		//cast del campo input
		String value = (String)inputField;
		
		String sqlTypeUpperCase = expectedField.getSqlType().toUpperCase();
		if (sqlTypeUpperCase.indexOf("TEXT") == 0 && sqlTypeUpperCase.indexOf("LONG") == 0 && sqlTypeUpperCase.indexOf("CLOB") == 0) {
			//lunghezza max attesa
			int len = expectedField.stringLength();
	
			if (value.length() > len) {
				errorMap.putAll(MessageUtil.buildErrorMap("FieldLenghtTooLong", locale, 
						UtilMisc.toList(fieldName, String.valueOf(len))));
				return null;
			}
		}

		return value;
	}

}
