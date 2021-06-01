package com.mapsengineering.base.services.authentication;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;

public final class Authentication {

    private static final String MODULE = Authentication.class.getName();

    private Authentication() {
        // empty
    }

    public static String checkAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralException {
        Map<String, Object> parameters = getCombinedMap(request);

        return getUserLoginId(request, parameters);
    }
    
    private static String getUserLoginId(HttpServletRequest request, Map<String, Object> parameters) {
        Locale locale = (Locale)parameters.get("locale");
        String externalKey = (String)parameters.get(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR);
        String userLoginId = (String)parameters.get("userLoginId");

        if (externalKey == null) {
            ServiceUtil.setMessages(request, UtilProperties.getMessage("BaseUiLabels", "BaseErrorExternalLoginKeyNotFound", locale), null, null);
            return "error";
        }
        
        GenericValue userLogin = (GenericValue) LoginWorker.externalLoginKeys.get(externalKey);
        if (userLogin == null) {
            ServiceUtil.setMessages(request, UtilProperties.getMessage("BaseUiLabels", "BaseUserLoginNotFound", locale), null, null);
            return "error";
        }
        
        if(!userLoginId.equals(userLogin.getString("userLoginId"))) {
            ServiceUtil.setMessages(request, UtilProperties.getMessage("BaseUiLabels", "BaseErrorJobAccessDenied", locale), null, null);
            return "error";
        }
        
        return "success";
    }

    private static Map<String, Object> getCombinedMap(HttpServletRequest request) {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        if (UtilValidate.isEmpty(parameters.get("locale"))) {
            parameters.put("locale", UtilHttp.getLocale(request));
        }
        if (UtilValidate.isEmpty(parameters.get("timeZone"))) {
            parameters.put("timeZone", UtilHttp.getTimeZone(request));
        }
        return parameters;
    }
}
