package com.mapsengineering.gzbox.services.locale;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.common.CommonEvents;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Locale Service Find
 */
public class SessionLocaleService {

    public static final String MODULE = SessionLocaleService.class.getName();
    
    public static Map<String, Object> gzChangeSessionLocale(DispatchContext ctx, Map<String, ? extends Object> context) {
        try {
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            String newLocale = (String) context.get("newLocale");
            
            CommonEvents.setSessionLocale(request, null, newLocale);
            return ServiceUtil.returnSuccess();
        } catch (Exception e) {
            return ServiceUtil.returnError("Error call gzChangeSessionLocale "+e.getMessage());
        }

    }
}
