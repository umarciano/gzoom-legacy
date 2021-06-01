package com.mapsengineering.base.events;

import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * 13/12/12
 *
 * @author Andrea Fossi
 */
public class CustomXmlRpcHttpRequestConfigImpl extends XmlRpcHttpRequestConfigImpl {
    private HttpServletRequest request;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
