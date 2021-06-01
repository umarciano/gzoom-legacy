package com.mapsengineering.base.bl.validation;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

public class BigdecimalResolver extends FieldResolver {

    protected Object doResolve(String fieldName, Object inputField,
            ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {

        if ((inputField instanceof BigDecimal)) {
            return inputField;
        }

        if ((inputField instanceof Integer)||(inputField instanceof Long)) {
            long val = ((Number)inputField).longValue();
            return new BigDecimal(val);
        }

        if (inputField instanceof Float) {
            double val = ((Float)inputField).doubleValue();
            return new BigDecimal(val);
        }

        if (inputField instanceof Double) {
            double val = ((Double)inputField).doubleValue();
            return new BigDecimal(val);
        }

        if (inputField instanceof String) {

            String num = (String)inputField;

            Double res = ValidationUtil.checkFloatingPointAgainstLocale(num, locale);
            if (res!=null) {
            	return new BigDecimal(res);
            }

        }

        //Altri casi segnalo incompatibilità
        errorMap.putAll(MessageUtil.buildErrorMap("NotDecimalFormat", locale));
        return null;
    }

}
