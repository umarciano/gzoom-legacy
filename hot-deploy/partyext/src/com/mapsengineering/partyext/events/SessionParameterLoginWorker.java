package com.mapsengineering.partyext.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * SessionParameterLoginWorker
 *
 */
public final class SessionParameterLoginWorker {

    public static final String MODULE = SessionParameterLoginWorker.class.getName();

    private static final String USER_LOGIN = "userLogin";
    private static final String SUCCESS = "success";
    private static final String SUCCESS_START_SEARCH = "succesStartSearch";
    private static final String USER_LOGIN_PARENT_ROLE_TYPE_LIST = "userLoginParentRoleTypeList";
    private static final String SEARCH_MAP = "searchMap";
    private static final String PARENT_ROLE_TYPE_ID = "parentRoleTypeId";
    private static final String ROLE_TYPE_ID = "roleTypeId";
    private static final String ROLE_TYPE_ID_OP = "roleTypeId_op";
    private static final String USER_LOGIN_VALID_ROLE_LIST = "userLoginValidRoleList";
    private static final String USER_LOGIN_ID = "userLoginId";
    private static final String USER_LOGIN_PERMISSION = "userLoginPermission";

    private SessionParameterLoginWorker() {
    }

    /**
     * After Login set userLoginPermission, userLoginValidRoleList and userLoginParentRoleTypeList in request
     * @param request
     * @param response
     * @return
     */
    public static String afterLogin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute(USER_LOGIN);

        if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isEmpty(session.getAttribute(USER_LOGIN_PARENT_ROLE_TYPE_LIST))) {
            Map<String, Object> context = FastMap.newInstance();
            try {
                if (UtilValidate.isEmpty(session.getAttribute(USER_LOGIN_PERMISSION))) {
                    GenericDispatcher dispatcher = (GenericDispatcher) request.getAttribute("dispatcher");

                    context.put(USER_LOGIN, userLogin);
                    context.put(USER_LOGIN_ID, userLogin.get(USER_LOGIN_ID));
                    Map<String, Object> results = dispatcher.runSync("getUserLoginPermission", context);

                    if (ServiceUtil.isError(results)) {
                        Debug.logWarning((String) results.get(ModelService.ERROR_MESSAGE), MODULE);
                    } else if (UtilValidate.isEmpty(session.getAttribute(USER_LOGIN_VALID_ROLE_LIST))) {
                        session.setAttribute(USER_LOGIN_PERMISSION, results.get(USER_LOGIN_PERMISSION));
                        try {
                            results = dispatcher.runSync("loadUserLoginValidRoleList", context);

                            if (ServiceUtil.isError(results)) {
                                Debug.logWarning((String) results.get(ModelService.ERROR_MESSAGE), MODULE);
                            } else {
                                context.put(USER_LOGIN_VALID_ROLE_LIST, results.get(USER_LOGIN_VALID_ROLE_LIST));
                                session.setAttribute(USER_LOGIN_VALID_ROLE_LIST, results.get(USER_LOGIN_VALID_ROLE_LIST));
                                try {
                                    results = dispatcher.runSync("loadUserLoginParentRoleTypeList", context);

                                    if (ServiceUtil.isError(results)) {
                                        Debug.logWarning((String) results.get(ModelService.ERROR_MESSAGE), MODULE);
                                    } else if (results != null) {
                                        session.setAttribute(USER_LOGIN_PARENT_ROLE_TYPE_LIST, results.get(USER_LOGIN_PARENT_ROLE_TYPE_LIST));
                                    }
                                } catch (GenericServiceException e) {
                                    Debug.logWarning(e, MODULE);
                                }
                            }
                        } catch (GenericServiceException e) {
                            Debug.logWarning(e, MODULE);
                        }
                    }
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, MODULE);
            }
        }

        return SUCCESS;
    }

    /**
     * Put roleTypeId and parentRoleTypeId
     * @param request
     * @param response
     * @return
     */
    public static String putParametersInSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        Set<String> keys = parameters.keySet();
        Map<String, Object> searchMap = FastMap.newInstance();
        for (String key : keys) {
            if ((key.startsWith(ROLE_TYPE_ID) && key.endsWith("value") || key.equals(ROLE_TYPE_ID)) && UtilValidate.isNotEmpty(parameters.get(key))) {
                if (UtilValidate.isEmpty(parameters.get(ROLE_TYPE_ID_OP)) || (UtilValidate.isNotEmpty(parameters.get(ROLE_TYPE_ID_OP)) && "equals".equals(parameters.get(ROLE_TYPE_ID_OP)))) {
                    searchMap.put(ROLE_TYPE_ID, parameters.get(key));
                }
            }
            if ((key.startsWith(PARENT_ROLE_TYPE_ID) && key.endsWith("value") || key.equals(PARENT_ROLE_TYPE_ID)) && UtilValidate.isNotEmpty(parameters.get(key))) {
                if (UtilValidate.isEmpty(parameters.get("parentRoleTypeId_op")) || (UtilValidate.isNotEmpty(parameters.get("parentRoleTypeId_op")) && "equals".equals(parameters.get(ROLE_TYPE_ID_OP)))) {
                    searchMap.put(PARENT_ROLE_TYPE_ID, parameters.get(key));
                }
            }
        }
        session.setAttribute(SEARCH_MAP, searchMap);
        return SUCCESS;
    }

    /**
     * Remove roleTypeId and parentRoleTypeId
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String removeParametersFromSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Map<String, Object> searchMap = (Map<String, Object>) session.getAttribute(SEARCH_MAP);

        if (UtilValidate.isNotEmpty(searchMap) && UtilValidate.isNotEmpty(searchMap.get(ROLE_TYPE_ID))) {
            searchMap.remove(ROLE_TYPE_ID);
        }
        if (UtilValidate.isNotEmpty(searchMap) && UtilValidate.isNotEmpty(searchMap.get(PARENT_ROLE_TYPE_ID))) {
            searchMap.remove(PARENT_ROLE_TYPE_ID);
        }
        
        List<String> entityStartSearchList = new ArrayList<String>();
        entityStartSearchList.add("QueryConfigView");
        String entityName = request.getParameter("entityName");
        if (UtilValidate.isNotEmpty(entityName) && entityStartSearchList.contains(entityName)) {
        	return SUCCESS_START_SEARCH;
        }

        return SUCCESS;
    }
}
