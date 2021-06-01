package com.mapsengineering.base.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.JSONConverters.JSONToMap;
import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;

public class JSONUtils {

    public static final String module = JSONUtils.class.getName();

    protected static String toJsonDate(java.util.Date date) {
        DateFormat jsonTimestampFormat = new SimpleDateFormat(UtilDateTime.DATE_TIME_FORMAT);
        return jsonTimestampFormat.format(date);
    }

    public static String toJson(Object obj) {
        return toJson(obj, -1, 0);
    }

    public static String toJson(Object obj, int indentFactor, int indent) {
        if (obj instanceof java.util.Date)
            return '"' + toJsonDate((java.util.Date)obj) + '"';
        JSON json;
        try {
            json = JSON.from(obj);
            return json.toString();
        } catch (IOException e) {
            Debug.logError(e, "Error toJson json " + obj, module);
        }
        return null;
    }

    public static Map<String, Object> fromJson(String str) {
        JSON jsonObject = JSON.from(str);
        JSONToMap jsonMap = new JSONToMap();
        try {
            return jsonMap.convert(jsonObject);
        } catch (ConversionException e) {
            Debug.logError(e, "Error fromJson str " + str, module);
        }
        return null;
    }

    /**
     * Create a map from a HttpRequest (attributes) object used in JSON requests
     * @return The resulting Map
     */
    public static Map<String, Object> getJSONAttributeMap(HttpServletRequest request) {
        return getJSONAttributeMap(UtilHttp.getAttributeMap(request));
    }

    /**
     * Create a map from a source map (attributes) object used in JSON requests
     * @return The resulting Map
     */
    public static Map<String, Object> getJSONAttributeMap(Map<String, Object> attrMap) {
        Map<String, Object> returnMap = FastMap.newInstance();
        for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof String || val instanceof Number || val instanceof Map || val instanceof Collection || val instanceof Boolean || val instanceof java.util.Date) {
                if (Debug.verboseOn())
                    Debug.logVerbose("Adding attribute to JSON output: " + entry.getKey(), module);
                returnMap.put(entry.getKey(), val);
            }
        }

        return returnMap;
    }

    /**
     * Same as {@link org.ofbiz.common.CommonEvents#jsonResponseFromRequestAttributes}
     * but ignores internal request attributes.
     * @param request
     * @param response
     * @return json string
     */
    public static String jsonResponseFromRequestAttributes(HttpServletRequest request, HttpServletResponse response) {
        // pull out the service response from the request attribute
        Map<String, Object> attrMap = getJSONAttributeMap(request);
        removeInternalRequestAttributes(attrMap);

        boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
        String contentType = isMultipartContent ? "text/html" : "application/x-json";

        // create a JSON Object for return
        String jsonStr = toJson(attrMap);

        if (UtilValidate.isEmpty(jsonStr)) {
            jsonStr = "{}";
        }

        if (isMultipartContent) {
            jsonStr = "<html><body><textarea cols=\"80\" rows=\"25\">" + jsonStr + "</textarea></body></html>";
        }

        // set the X-JSON content type
        response.setContentType(contentType);
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding: " + e, module);
        }

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }

        return "success";
    }

    public static void removeInternalRequestAttributes(Map<String, Object> attrMap) {
        attrMap.remove("_CONTEXT_ROOT_");
        attrMap.remove("_CONTROL_PATH_");
        attrMap.remove("_SERVER_ROOT_URL_");
        attrMap.remove("javax.servlet.request.cipher_suite");
        attrMap.remove("javax.servlet.request.key_size");
        attrMap.remove("javax.servlet.request.ssl_session");
        attrMap.remove("targetRequestUri");
        attrMap.remove("thisRequestUri");
    }
}
