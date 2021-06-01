package com.mapsengineering.base.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

public class OfbizEventHookWrapper {

    public String runByConfig(HttpServletRequest request, HttpServletResponse response, String resource, String name) throws GeneralException {
        return runByClassName(request, response, UtilProperties.getPropertyValue(resource, name, null));
    }

    public String runByClassName(HttpServletRequest request, HttpServletResponse response, String className) throws GeneralException {
        if (UtilValidate.isNotEmpty(className)) {
            try {
                run(request, response, Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new GeneralException(e);
            }
        }
        return OfbizEventHook.SUCCESS_CODE;
    }

    public String run(HttpServletRequest request, HttpServletResponse response, Class<? extends Object> clazz) throws GeneralException {
        try {
            return run(request, response, (OfbizEventHook)clazz.newInstance());
        } catch (InstantiationException e) {
            throw new GeneralException(e);
        } catch (IllegalAccessException e) {
            throw new GeneralException(e);
        }
    }

    public String run(HttpServletRequest request, HttpServletResponse response, OfbizEventHook hook) throws GeneralException {
        if (hook != null) {
            return hook.run(request, response);
        }
        return OfbizEventHook.SUCCESS_CODE;
    }
}
