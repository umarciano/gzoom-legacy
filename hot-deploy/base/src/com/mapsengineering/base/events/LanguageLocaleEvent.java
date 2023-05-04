package com.mapsengineering.base.events;


import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;

import com.mapsengineering.base.util.UtilLanguageLocale;

public class LanguageLocaleEvent {
    
    public static Boolean isPrimaryLanguage(Locale locale) {
        // recupera le lingue configurate sui properties
        List<Locale> availableBaseConfigLocaleList = UtilLanguageLocale.availableBaseConfigLocales();
        String secondaryLanguage = "";
        if (UtilValidate.isNotEmpty(availableBaseConfigLocaleList)) 
        {
            if(availableBaseConfigLocaleList.size() > 1){
                secondaryLanguage =  availableBaseConfigLocaleList.get(1).getLanguage();    
            }
        }     
        
        String lang = locale.getLanguage();                        
        if (UtilValidate.isNotEmpty(secondaryLanguage) && secondaryLanguage.equals(lang)) {
            return false;
        }
        
        return true;
    }
    
    public static String getLanguageSettings(HttpServletRequest request, HttpServletResponse response) {
        
        Map<String, Object> languageSettinngs = FastMap.newInstance();
        Locale locale = UtilHttp.getLocale(request);
        
        String primaryLang = "";
        String secondaryLang = "";

        List<Locale> availableBaseConfigLocaleList = UtilLanguageLocale.availableBaseConfigLocales();
        
        if (UtilValidate.isNotEmpty(availableBaseConfigLocaleList)) {
            primaryLang = availableBaseConfigLocaleList.get(0).getLanguage();
            languageSettinngs.put("primaryLang", primaryLang);
            if (availableBaseConfigLocaleList.size() > 1) {
                secondaryLang =  availableBaseConfigLocaleList.get(1).getLanguage();
                languageSettinngs.put("secondaryLang", secondaryLang);
            }
        }
        
        ResourceBundleMapWrapper commonUiLabelMap = UtilProperties.getResourceBundleMap("CommonUiLabels", locale);
        if (UtilValidate.isNotEmpty(primaryLang)) {
            languageSettinngs.put("primaryLangFlagPath", (String) UtilProperties.getPropertyValue("BaseConfig", "Language.multi.flag_" + primaryLang));
            languageSettinngs.put("primaryLangTooltip", (String) commonUiLabelMap.get(primaryLang));
            
        }
        if (UtilValidate.isNotEmpty(secondaryLang)) {
            languageSettinngs.put("secondaryLangFlagPath", (String) UtilProperties.getPropertyValue("BaseConfig", "Language.multi.flag_" + secondaryLang));
            languageSettinngs.put("secondaryLangTooltip", (String) commonUiLabelMap.get(secondaryLang));
        }
        
        //localeSecondarySet
        String localeSecondarySet = "N";
        String multiTypeLang = UtilProperties.getPropertyValue("BaseConfig", "Language.multi.type");
        languageSettinngs.put("multiTypeLang", multiTypeLang);
        if ("BILING".equals(multiTypeLang)) {
            String lang = locale.getLanguage();                        
            if (UtilValidate.isNotEmpty(secondaryLang) && secondaryLang.equals(lang)) {
                localeSecondarySet = "Y";
            }
        }
        languageSettinngs.put("localeSecondarySet", localeSecondarySet);
        
        request.setAttribute("languageSettinngs", languageSettinngs);
        request.getSession().setAttribute("languageSettinngs", languageSettinngs);
              
        return "success";
    }
    
}
