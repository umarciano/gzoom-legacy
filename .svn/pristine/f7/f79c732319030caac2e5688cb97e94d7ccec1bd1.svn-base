package com.mapsengineering.base.bl.validation;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

public class LongResolver extends FieldResolver {

    protected Long doResolve(String fieldName, Object inputField,
            ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {

        if ((inputField instanceof Long)||(inputField instanceof Integer)) {
            return (Long)inputField;
        }

        if (inputField instanceof Double) {
            long val = ((Double)inputField).longValue();
            return Long.valueOf(val);
        }

        if (inputField instanceof Float) {
            long val = ((Float)inputField).longValue();
            return Long.valueOf(val);
        }

        if (inputField instanceof String) {

            String num = (String)inputField;

            Long res = ValidationUtil.checkIntegerAgainstLocale(num, locale);
            if (res==null) {
                errorMap.putAll(MessageUtil.buildErrorMap("NotNumericFormat",  locale));
            }
            return res;
        }

        //Altri casi segnalo incompatibilità
        errorMap.putAll(MessageUtil.buildErrorMap("NotNumericFormat", locale));
        return null;
    }

}
