/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package org.ofbiz.securityext.login;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.product.ProductEvents;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.control.LoginWorker;

/**
 * LoginEvents - Events for UserLogin and Security handling.
 */
public class LoginEvents {

    public static final String module = LoginEvents.class.getName();
    public static final String resource = "SecurityextUiLabels";
    public static final String usernameCookieName = "OFBiz.Username";

    /**
     * Save USERNAME and PASSWORD for use by auth pages even if we start in non-auth pages.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return String
     */
    public static String saveEntryParams(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        HttpSession session = request.getSession();

        // save entry login parameters if we don't have a valid login object
        if (userLogin == null) {

            String username = request.getParameter("USERNAME");
            String password = request.getParameter("PASSWORD");

            if ((username != null) && ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
                password = password.toLowerCase();
            }

            // save parameters into the session - so they can be used later, if needed
            if (username != null) session.setAttribute("USERNAME", username);
            if (password != null) session.setAttribute("PASSWORD", password);

        } else {
            // if the login object is valid, remove attributes
            session.removeAttribute("USERNAME");
            session.removeAttribute("PASSWORD");
        }

        return "success";
    }

    /**
     * The user forgot his/her password.  This will call showPasswordHint, emailPassword or simply returns "success" in case
     * no operation has been specified.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        if ((UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) || (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT.x")))) {
            return showPasswordHint(request, response);
        } else if ((UtilValidate.isNotEmpty(request.getParameter("EMAIL_PASSWORD"))) || (UtilValidate.isNotEmpty(request.getParameter("EMAIL_PASSWORD.x")))) {
            return emailPassword(request, response);
        } else {
            return "success";
        }
    }

    /** Show the password hint for the userLoginId specified in the request object.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String showPasswordHint(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String userLoginId = request.getParameter("USERNAME");
        String errMsg = null;

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;

        try {
            supposedUserLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee, "", module);
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String passwordHint = supposedUserLogin.getString("passwordHint");

        if (!UtilValidate.isNotEmpty(passwordHint)) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.no_password_hint_specified_try_password_emailed", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        Map<String, String> messageMap = UtilMisc.toMap("passwordHint", passwordHint);
        errMsg = UtilProperties.getMessage(resource, "loginevents.password_hint_is", messageMap, UtilHttp.getLocale(request));
        request.setAttribute("_EVENT_MESSAGE_", errMsg);
        return "success";
    }

    /**
     *  Email the password for the userLoginId specified in the request object.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
        String defaultScreenLocation = "component://securityext/widget/EmailSecurityScreens.xml#PasswordEmail";

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);

        String errMsg = null;

        Map<String, String> subjectData = FastMap.newInstance();
        subjectData.put("productStoreId", productStoreId);

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try {
            supposedUserLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
            if (supposedUserLogin == null) {
                // the Username was not found
                errMsg = UtilProperties.getMessage(resource, "loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            if (useEncryption) {
                // password encrypted, can't send, generate new password and email to user
                double randNum = Math.random();

                // multiply by 100,000 to usually make a 5 digit number
                passwordToSend = "auto" + ((long) (randNum * 100000));
                supposedUserLogin.set("currentPassword", HashCrypt.getDigestHash(passwordToSend, LoginServices.getHashType()));
                supposedUserLogin.set("passwordHint", "Auto-Generated Password");
            } else {
                passwordToSend = supposedUserLogin.getString("currentPassword");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
            errMsg = UtilProperties.getMessage(resource, "loginevents.error_accessing_password", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            Map<String, String> messageMap = UtilMisc.toMap("userLoginId", userLoginId);
            errMsg = UtilProperties.getMessage(resource, "loginevents.user_with_the_username_not_found", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        StringBuilder emails = new StringBuilder();
        GenericValue party = null;

        try {
            party = supposedUserLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            party = null;
        }
        if (party != null) {
            Iterator<GenericValue> emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = emailIter.next();
                emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
            }
        }

        if (!UtilValidate.isNotEmpty(emails.toString())) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.no_primary_email_address_set_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", "PRDS_PWD_RETRIEVE");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        if (productStoreEmail == null) {
            errMsg = UtilProperties.getMessage(resource, "loginevents.problems_with_configuration_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        // set the needed variables in new context
        Map<String, Object> bodyParameters = FastMap.newInstance();
        bodyParameters.put("useEncryption", Boolean.valueOf(useEncryption));
        bodyParameters.put("password", UtilFormatOut.checkNull(passwordToSend));
        bodyParameters.put("locale", UtilHttp.getLocale(request));
        bodyParameters.put("userLogin", supposedUserLogin);
        bodyParameters.put("productStoreId", productStoreId);

        Map<String, Object> serviceContext = FastMap.newInstance();
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emails.toString());
        serviceContext.put("partyId", party.getString("partyId"));

        try {
            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_unable_email_password_contact_customer_service_errorwas", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "", module);
            errMsg = UtilProperties.getMessage(resource, "loginevents.error_unable_email_password_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_saving_new_password_email_not_correct_password", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }

        if (useEncryption) {
            errMsg = UtilProperties.getMessage(resource, "loginevents.new_password_createdandsent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
        } else {
            errMsg = UtilProperties.getMessage(resource, "loginevents.new_password_sent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
        }
        return "success";
    }

    public static String storeCheckLogin(HttpServletRequest request, HttpServletResponse response) {
        String responseString = LoginWorker.checkLogin(request, response);
        if ("error".equals(responseString)) {
            return responseString;
        }
        // if we are logged in okay, do the check store customer role
        return ProductEvents.checkStoreCustomerRole(request, response);
    }

    public static String storeLogin(HttpServletRequest request, HttpServletResponse response) {
        String responseString = LoginWorker.login(request, response);
        if (!"success".equals(responseString)) {
            return responseString;
        }
        if ("Y".equals(request.getParameter("rememberMe"))) {
            setUsername(request, response);
        }
        // if we logged in okay, do the check store customer role
        return ProductEvents.checkStoreCustomerRole(request, response);
    }

    public static String getUsername(HttpServletRequest request) {
        String cookieUsername = null;
        Cookie[] cookies = request.getCookies();
        if (Debug.verboseOn()) Debug.logVerbose("Cookies:" + cookies, module);
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(usernameCookieName)) {
                    cookieUsername = cookie.getValue();
                    break;
                }
            }
        }
        return cookieUsername;
    }

    public static void setUsername(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String domain = UtilProperties.getPropertyValue("url.properties", "cookie.domain");
        // first try to get the username from the cookie
        synchronized (session) {
            if (UtilValidate.isEmpty(getUsername(request))) {
                // create the cookie and send it back
                Cookie cookie = new Cookie(usernameCookieName, request.getParameter("USERNAME"));
                cookie.setMaxAge(60 * 60 * 24 * 365);
                cookie.setPath("/");
                cookie.setDomain(domain);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * SAML SSO Login - Authenticates user based on SAML matricola and fiscalCode
     * Uses the same login flow as the standard userLogin service
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String samlLogin(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        
        String MODULE = "LoginEvents.samlLogin";
        Debug.logInfo("=== SAML Login - START ===", MODULE);
        
        // Recupera i parametri SAML dalla richiesta
        String samlAccount = request.getParameter("saml_account");
        String samlFiscalCode = request.getParameter("saml_fiscalcode");
        
        if (UtilValidate.isEmpty(samlAccount)) {
            Debug.logError("SAML Login FAILED - saml_account parameter is missing or empty", MODULE);
            request.setAttribute("_ERROR_MESSAGE_", "SSO Login failed: matricola missing");
            return "error";
        }
        
        // Decodifica Base64 se necessario (per compatibilità con soa.jsp)
        try {
            String decoded = new String(org.ofbiz.base.util.Base64.base64Decode(samlAccount));
            if (UtilValidate.isNotEmpty(decoded)) {
                samlAccount = decoded;
            }
        } catch (Exception e) {
            // Se non è Base64, usa il valore originale
            Debug.logInfo("SAML account is not Base64 encoded, using as-is: " + samlAccount, MODULE);
        }
        
        Debug.logInfo("SAML Login - matricola: '" + samlAccount + "', fiscalCode: '" + 
                     (UtilValidate.isNotEmpty(samlFiscalCode) ? samlFiscalCode : "NOT PROVIDED") + "'", MODULE);
        
        // Usa SamlUserMatcher per trovare l'utente
        GenericValue userLogin = null;
        try {
            // Import della classe SamlUserMatcher
            userLogin = com.mapsengineering.base.authentication.SamlUserMatcher.matchUser(
                delegator, samlAccount, samlFiscalCode);
        } catch (Exception e) {
            Debug.logError(e, "Error during SAML user matching", MODULE);
            request.setAttribute("_ERROR_MESSAGE_", "SSO Login failed: user matching error");
            return "error";
        }
        
        if (userLogin == null) {
            Debug.logError("SAML Login FAILED - No user found for matricola: '" + samlAccount + "'", MODULE);
            request.setAttribute("_ERROR_MESSAGE_", "SSO Login failed: user not found or fiscal code mismatch");
            return "error";
        }
        
        String userLoginId = userLogin.getString("userLoginId");
        Debug.logInfo("SAML Login - UserLogin found: '" + userLoginId + "'", MODULE);
        
        // Verifica che l'utente non sia disabilitato
        String enabled = userLogin.getString("enabled");
        if (enabled != null && "N".equals(enabled)) {
            Debug.logError("SAML Login FAILED - User is disabled: " + userLoginId, MODULE);
            request.setAttribute("_ERROR_MESSAGE_", "SSO Login failed: user account is disabled");
            return "error";
        }
        
        // Usa LoginWorker.doMainLogin() per configurare correttamente la sessione
        // esattamente come fa il login standard
        try {
            Debug.logInfo("SAML Login - Calling LoginWorker.doMainLogin() for user: " + userLoginId, MODULE);
            
            // Questo metodo configura: userLogin, person, partyGroup, javaScriptEnabled, visit, etc.
            String result = LoginWorker.doMainLogin(request, response, userLogin, null);
            
            if ("success".equals(result)) {
                Debug.logInfo("SAML Login - doMainLogin() succeeded", MODULE);
                
                // Ottieni l'externalLoginKey creato da doMainLogin()
                String externalLoginKey = LoginWorker.getExternalLoginKey(request);
                Debug.logInfo("SAML Login - externalLoginKey: " + externalLoginKey, MODULE);
                
                // Log the login attempt
                try {
                    String visitId = (String) session.getAttribute("visitId");
                    Map<String, Object> ulLogContext = UtilMisc.toMap("userLoginId", userLoginId, "visitId", visitId);
                    ulLogContext.put("userLogin", userLogin);
                    dispatcher.runAsync("createUserLoginSession", ulLogContext);
                } catch (GenericServiceException e) {
                    Debug.logWarning("Error logging SAML login: " + e.getMessage(), MODULE);
                }
                
                // Redirect diretto ad Angular con externalLoginKey come parametro URL
                String angularUrl = "http://localhost:4200/sso-callback?externalLoginKey=" + externalLoginKey;
                Debug.logInfo("SAML Login - Redirecting to Angular SSO callback: " + angularUrl, MODULE);
                
                try {
                    response.sendRedirect(angularUrl);
                    Debug.logInfo("=== SAML Login - END - SUCCESS (redirected) ===", MODULE);
                    return null; // null = il redirect è già stato gestito, non servono altre response
                } catch (java.io.IOException ioe) {
                    Debug.logError(ioe, "SAML Login - Error sending redirect to Angular", MODULE);
                    request.setAttribute("_ERROR_MESSAGE_", "SSO Login succeeded but redirect failed");
                    return "error";
                }
                
            } else {
                Debug.logError("SAML Login FAILED - doMainLogin() returned: " + result, MODULE);
                return result;
            }
            
        } catch (Exception e) {
            Debug.logError(e, "SAML Login - Error during doMainLogin()", MODULE);
            request.setAttribute("_ERROR_MESSAGE_", "SSO Login failed: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Valida un externalLoginKey e ritorna i dati dell'utente in formato JSON.
     * Questo endpoint è chiamato da Spring Boot per convertire l'externalLoginKey in userLoginId.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return "success" sempre (la risposta è scritta direttamente in response)
     */
    public static String validateExternalLoginKey(HttpServletRequest request, HttpServletResponse response) {
        Debug.logInfo("=== validateExternalLoginKey - START ===", module);
        
        // Leggi externalLoginKey dal parametro
        String externalLoginKey = request.getParameter("externalLoginKey");
        Debug.logInfo("validateExternalLoginKey - Key received: " + externalLoginKey, module);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            if (UtilValidate.isEmpty(externalLoginKey)) {
                Debug.logWarning("validateExternalLoginKey - Empty externalLoginKey", module);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"externalLoginKey parameter is required\"}");
                return "success";
            }
            
            // Recupera l'oggetto UserLogin dalla Map in memoria
            GenericValue userLogin = (GenericValue) LoginWorker.externalLoginKeys.get(externalLoginKey);
            
            if (userLogin == null) {
                Debug.logWarning("validateExternalLoginKey - Invalid or expired key: " + externalLoginKey, module);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid or expired externalLoginKey\"}");
                return "success";
            }
            
            // Estrai i dati dell'utente
            String userLoginId = userLogin.getString("userLoginId");
            String partyId = userLogin.getString("partyId");
            String enabled = userLogin.getString("enabled");
            
            Debug.logInfo("validateExternalLoginKey - Valid key! UserLoginId: " + userLoginId + ", PartyId: " + partyId, module);
            
            // Costruisci JSON response
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"valid\":true,");
            json.append("\"userLoginId\":\"").append(userLoginId != null ? userLoginId : "").append("\",");
            json.append("\"partyId\":\"").append(partyId != null ? partyId : "").append("\",");
            json.append("\"enabled\":\"").append(enabled != null ? enabled : "Y").append("\"");
            json.append("}");
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(json.toString());
            
            Debug.logInfo("=== validateExternalLoginKey - END SUCCESS ===", module);
            
        } catch (Exception e) {
            Debug.logError(e, "validateExternalLoginKey - Error validating key", module);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            } catch (Exception ex) {
                Debug.logError(ex, "validateExternalLoginKey - Error writing error response", module);
            }
        }
        
        return "success";
    }
}
