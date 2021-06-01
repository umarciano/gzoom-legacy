package org.ofbiz.webapp.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.webapp.control.ConfigXMLReader.Event;
import org.ofbiz.webapp.control.ConfigXMLReader.RequestMap;

public class JSONGroovyEventHandler implements EventHandler {
	

    public static final String module = JSONJavaEventHandler.class.getName();
    protected EventHandler service;

    public void init(ServletContext context) throws EventHandlerException {
        this.service = new GroovyEventHandler();
        this.service.init(context);
    }

    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        // call into the java handler for parameters parsing and invocation
        String respCode = service.invoke(event, requestMap, request, response);

        // pull out the service response from the request attribute
        Map<String, Object> attrMap = UtilHttp.getJSONAttributeMap(request);

        // create a JSON Object for return
        JSON json = null;
        try {
            json = JSON.from(attrMap);
        } catch (IOException e) {
            throw new EventHandlerException("No JSON Object from serviceContext; fatal error!");
        }
        String jsonStr = json.toString();
        if (jsonStr == null) {
            throw new EventHandlerException("JSON Object was empty; fatal error!");
        }

        // set the X-JSON content type
        response.setContentType("application/x-json");
        // jsonStr.length is not reliable for unicode characters 
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            throw new EventHandlerException("Problems with Json encoding", e);
        }

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            throw new EventHandlerException("Unable to get response writer", e);
        }

        return respCode;
    }
}
