package com.mapsengineering.base.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.GeneralException;

/**
 * OFBiz hook executable before or after web events.
 * @author sivi
 *
 */
public interface OfbizEventHook {

    /**
     * Default success code.
     */
    public static final String SUCCESS_CODE = "success";

    /**
     * Method called by external event handlers.
     * @param request
     * @param response
     * @return
     * @throws GeneralException
     */
    public String run(HttpServletRequest request, HttpServletResponse response) throws GeneralException;
}
