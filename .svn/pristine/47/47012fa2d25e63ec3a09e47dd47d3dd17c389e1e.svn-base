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


def localeSecondarySet = "N";


def secondaryLang = "";

List<Locale> availableBaseConfigLocaleList = UtilLanguageLocale.availableBaseConfigLocales();

if (UtilValidate.isNotEmpty(availableBaseConfigLocaleList)) {
    if (availableBaseConfigLocaleList.size() > 1) {
        secondaryLang =  availableBaseConfigLocaleList.get(1).getLanguage();
    }
}


def multiTypeLang = UtilProperties.getPropertyValue("BaseConfig", "Language.multi.type");
 if ("BILING".equals(multiTypeLang)) {
    def lang = locale.getLanguage();                        
    if (UtilValidate.isNotEmpty(secondaryLang) && secondaryLang.equals(lang)) {
        localeSecondarySet = "Y";
    }
}

context.localeSecondarySet = localeSecondarySet;
