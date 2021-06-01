package com.mapsengineering.base.authentication;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

public class OfbizHttpBasicAuth implements Filter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    protected String paramBasicRealm = null;

    public void init(FilterConfig config) throws ServletException {
        this.paramBasicRealm = "Basic realm=\"" + config.getInitParameter("basic_realm") + "\"";
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        if (!isUserLoggedIn(request)) {
            if (!doBasicAuth(request, response))
                return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }

    protected boolean doBasicAuth(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (UtilValidate.isNotEmpty(authorizationHeader)) {

            String[] authorizationArray = authorizationHeader.split("\\s+");
            String authorization = authorizationArray[0];
            String credentials = new String(Base64.base64Decode(authorizationArray[1]));

            if (authorization.equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {

                String[] loginAndPassword = credentials.split("\\:");
                String username = loginAndPassword[0].trim();
                String password = null;

                if (loginAndPassword.length > 1)
                    password = loginAndPassword[1].trim();

                HttpSession session = request.getSession();
                session.setAttribute("USERNAME", username);
                session.setAttribute("PASSWORD", password);

                return true;
            }
        }

        response.setHeader(WWW_AUTHENTICATE, this.paramBasicRealm);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    public static boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession();
        GenericValue currentUserLogin = (GenericValue)session.getAttribute("userLogin");
        if (currentUserLogin != null) {
            String hasLoggedOut = currentUserLogin.getString("hasLoggedOut");
            if (!"Y".equals(hasLoggedOut)) {
                return true;
            }
        }
        return false;
    }
}
