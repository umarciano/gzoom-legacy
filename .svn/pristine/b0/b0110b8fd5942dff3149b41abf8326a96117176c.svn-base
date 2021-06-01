package com.mapsengineering.workeffortext.events;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.webapp.control.LoginWorker;

import com.mapsengineering.base.util.ContextIdEnum;
import com.mapsengineering.base.util.OfbizEventHook;
import com.mapsengineering.base.util.OfbizEventHookWrapper;
import com.mapsengineering.base.util.SurveyErrorCodes;

public final class SessionWorkeffortWorker {

    public static final String MODULE = SessionWorkeffortWorker.class.getName();

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String ERROR_NO_TARGET_CODE = "errorNoTargetCode";
    private static final String EXT_WE_REQUEST = "/control/externalWorkEffortViewPage?";

    private enum E {
        _ERROR_MESSAGE_, WorkeffortExtConfig, targetCode, surveyErrorCode, WorkeffortExtErrorLabels, WorkEffortTargetCodeNotFoundError;
    }

    private SessionWorkeffortWorker() {
    }

    public static String checkModifiedData(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        parameters.putAll(UtilHttp.getAttributeMap(request));

        if (UtilValidate.isNotEmpty(parameters.get("modifiedDate"))) {
            session.setAttribute("modifiedDate", parameters.get("modifiedDate"));
        }

        return SUCCESS;
    }

    public static String loadTreeViewSelectedId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        Cookie cookie = null;
        for (int i = 0; i < cookies.length; i++) {
            String cookieName = cookies[i].getName();

            if (cookieName.contains("selectedId")) {
                cookie = cookies[i];
                break;
            }
        }

        String selectedId = null;
        if (UtilValidate.isNotEmpty(cookie)) {
            String storedValue = cookie.getValue();
            selectedId = storedValue.substring(3, storedValue.length() - 3);
        }

        if (UtilValidate.isNotEmpty(selectedId)) {

            String entityName = selectedId.substring(0, selectedId.indexOf('_'));

            if ("WorkEffortView".equals(entityName) || "WorkEffortAssocExtView".equals(entityName)) {
                String workEffortId = selectedId.substring(selectedId.lastIndexOf('_') + 1);
                if (UtilValidate.isNotEmpty(workEffortId)) {
                    request.setAttribute("workEffortId", workEffortId);
                }
            }
        }

        return SUCCESS;
    }

    public static String openWorkEffortViewPagePreprocessor(HttpServletRequest request, HttpServletResponse response) {
        try {
            return new OfbizEventHookWrapper().runByConfig(request, response, E.WorkeffortExtConfig.name(), "SessionWorkeffortWorker.openWorkEffortViewPage.preprocessor");
        } catch (GeneralException e) {
            Debug.logError(e, e.getMessage(), MODULE);
        }
        return OfbizEventHook.SUCCESS_CODE;
    }

    public static String selectRedirectUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String targetCode = null;
        String webcontext = null;

        String resourceLabel = UtilProperties.getPropertyValue("CustomConfig", "survey.resourceLabel", E.WorkeffortExtErrorLabels.name());
        String extWeRequest = UtilProperties.getPropertyValue("CustomConfig", "survey.extWeRequest");
        String surverErrorCodes = UtilProperties.getPropertyValue("CustomConfig", "survey.errorCodes", "N");
        try {
            openWorkEffortViewPagePreprocessor(request, response);

            Map<String, Object> urlParams = UtilHttp.getUrlOnlyParameterMap(request);
            urlParams.put(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, LoginWorker.getExternalLoginKey(request));
            
            // gestione codice d'errore dei questionari
            if (UtilValidate.isNotEmpty(surverErrorCodes) && "Y".equalsIgnoreCase(surverErrorCodes)) {
            	SurveyErrorCodes errCode = (SurveyErrorCodes)request.getAttribute(E.surveyErrorCode.name());
            	if (UtilValidate.isNotEmpty(errCode)){
            		String errMsg = UtilProperties.getMessage(resourceLabel, errCode.name(), UtilHttp.getLocale(request));
                    request.setAttribute(E._ERROR_MESSAGE_.name(), errMsg);
                    return ERROR_NO_TARGET_CODE;
            	}
            }
            
            targetCode = (String)request.getAttribute(E.targetCode.name());
            if (targetCode == null) {
                targetCode = request.getParameter(E.targetCode.name());
            }
            urlParams.put(E.targetCode.name(), targetCode);
            if (UtilValidate.isEmpty(targetCode)) {
                String errMsg = UtilProperties.getMessage(resourceLabel, E.WorkEffortTargetCodeNotFoundError.name(), UtilMisc.toMap(E.targetCode.name(), targetCode), UtilHttp.getLocale(request));
                request.setAttribute(E._ERROR_MESSAGE_.name(), errMsg);
                return ERROR_NO_TARGET_CODE;
            }
            
            if(UtilValidate.isEmpty(extWeRequest)) {
                extWeRequest = webcontext + EXT_WE_REQUEST;
                Delegator delegator = (Delegator)request.getAttribute("delegator");
                List<GenericValue> workEffortList = delegator.findList("WorkEffort", EntityCondition.makeCondition("sourceReferenceId", targetCode), null, null, null, false);
    
                if (UtilValidate.isNotEmpty(workEffortList)) {
                    GenericValue workEffort = EntityUtil.getFirst(workEffortList);
                    Debug.log("*** workEffort = " + workEffort);
    
                    GenericValue workEffortView = delegator.findOne("WorkEffortView", UtilMisc.toMap("workEffortId", workEffort.getString("workEffortId")), false);
    
                    String contextId = workEffortView.getString("weContextId");
                    ContextIdEnum contextIdEnum = ContextIdEnum.parse(contextId);
                    webcontext = contextIdEnum.webcontext();
                }
                
                if (UtilValidate.isEmpty(webcontext)) {
                    String errMsg = UtilProperties.getMessage(resourceLabel, E.WorkEffortTargetCodeNotFoundError.name(), UtilMisc.toMap(E.targetCode.name(), targetCode), UtilHttp.getLocale(request));
                    request.setAttribute(E._ERROR_MESSAGE_.name(), errMsg);
                    return ERROR;
                }
            }
            
            String redirectUrl = '/' + extWeRequest + UtilHttp.urlEncodeArgs(urlParams, false);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), MODULE);
        }
        return SUCCESS;
    }
}
