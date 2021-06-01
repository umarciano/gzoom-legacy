package com.mapsengineering.base.bl.validation;

import java.sql.Time;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

public class TimeResolver extends FieldResolver {

    protected Object doResolve(String fieldName, Object inputField,
            ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {

        //Se input = Time - Ok
        if (inputField instanceof java.sql.Time) {
            return inputField;
        }

        //Timestamp o date - conversione
        if ((inputField instanceof java.sql.Timestamp)||
                (inputField instanceof java.sql.Date)||
                (inputField instanceof java.util.Date)) {
            Time res = new Time(((java.util.Date)inputField).getTime());
            return res;
        }

        //Provo la conversione
        if (inputField instanceof String) {
            String value = (String)inputField;
            Time time = ValidationUtil.checkTimeAgainstLocale(value, locale);
            if (UtilValidate.isEmpty(time)) {
                errorMap.putAll(MessageUtil.buildErrorMap("InvalidTimeFormat", locale, UtilMisc.toList(value)));
                return null;
            }

            return time;
        }

        //Altri casi segnalo incompatibilità
        errorMap.putAll(MessageUtil.buildErrorMap("InvalidTimeFormat", locale, UtilMisc.toList(inputField.toString())));

        return null;
    }

}
