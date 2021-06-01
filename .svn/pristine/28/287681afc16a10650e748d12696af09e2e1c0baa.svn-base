package com.mapsengineering.base.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

public class OfbizWsContext extends OfbizHttpContext {

    public static final String module = OfbizWsContext.class.getName();

    public MessageContext context;

    protected void getContext() {
        this.context = MessageContext.getCurrentContext();
        this.request = (HttpServletRequest)this.context.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        this.response = (HttpServletResponse)this.context.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
        super.getContext();
    }
}
