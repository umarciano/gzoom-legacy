package com.mapsengineering.base.menu;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.entity.GenericValue;

public final class MenuUtil {
	
	private MenuUtil() {}
	
	/**
	 * 
	 * @param originalParameterList
	 * @param parameterFieldName
	 * @return
	 */
    public static List<StringWrapper> createParameterList(List<GenericValue> originalParameterList, String parameterFieldName) {
        List<StringWrapper> res = new FastList<StringWrapper>();

        if (UtilValidate.isNotEmpty(originalParameterList)) {
            for(GenericValue parameter : originalParameterList) {
                String value = parameter.getString(parameterFieldName);
                if (UtilValidate.isNotEmpty(value) && value.indexOf('=') != -1) {
                    if (value.indexOf('?') != -1) {
                        value = value.substring(value.indexOf('?')+1);
                    }
                    value = value.trim();
                    if (!value.startsWith("[")) {
                        value = "[" + value;
                    }
                    if (!value.endsWith("]")) {
                        value = value + "]";
                    }
                    List<String> splittedStringList = StringUtil.toList(value, "&");
                    for(String splittedString : splittedStringList) {
                        splittedString = splittedString.trim();
                        if (!res.contains(splittedString)) {
                            res.add(StringUtil.wrapString(splittedString));
                        }
                    }
                }
            }
        }

        return res;
    }
}
