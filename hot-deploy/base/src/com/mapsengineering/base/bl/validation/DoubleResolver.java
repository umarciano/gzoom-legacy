package com.mapsengineering.base.bl.validation;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

public class DoubleResolver extends FieldResolver {

    protected Object doResolve(String fieldName, Object inputField,
            ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {

        if ((inputField instanceof Double)||(inputField instanceof Float)) {
            return inputField;
        }

        if ((inputField instanceof Integer)||(inputField instanceof Long)) {
            long val = ((Number)inputField).longValue();
            return new Double(val);
        }

        if (inputField instanceof String) {

            String num = (String)inputField;

            Double res = ValidationUtil.checkFloatingPointAgainstLocale(num, locale);
            if (res==null) {
                errorMap.putAll(MessageUtil.buildErrorMap("NotDecimalFormat",  locale));
            }
            return res;
        }

        //Altri casi segnalo incompatibilità
        errorMap.putAll(MessageUtil.buildErrorMap("NotDecimalFormat", locale));
        return null;
    }

}
