package com.mapsengineering.base.util;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

public class UtilLanguageLocale {


	public static List<Locale> availableBaseConfigLocaleList = null;
	/** Returns a List of available locales  */
    public static List<Locale> availableBaseConfigLocales() {
        if (availableBaseConfigLocaleList == null) {
            synchronized(UtilLanguageLocale.class) {
                if (availableBaseConfigLocaleList == null) {
                    Map<String, Locale> localeMap = new LinkedHashMap<String, Locale>();
                    String localesString = UtilProperties.getPropertyValue("BaseConfig", "locales.available");
                    if (UtilValidate.isNotEmpty(localesString)) { 
                        int end = -1;
                        int start = 0;
                        for (int i=0; start < localesString.length(); i++) {
                            end = localesString.indexOf(",", start);
                            if (end == -1) {
                                end = localesString.length();
                            }
                            Locale curLocale = UtilMisc.ensureLocale(localesString.substring(start, end));
                            localeMap.put(curLocale.getDisplayName(), curLocale);
                            start = end + 1;
                        }
                        availableBaseConfigLocaleList = new FastList<Locale>(localeMap.values());
                    }                    
                }
            }
        }
        return availableBaseConfigLocaleList;
    }
    
}
