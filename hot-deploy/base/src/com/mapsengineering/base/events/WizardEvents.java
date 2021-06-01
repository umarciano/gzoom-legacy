package com.mapsengineering.base.events;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.ServiceUtil;

public final class WizardEvents {

	public static final String MODULE = WizardEvents.class.getName();
	
    private static final String ERROR = "error";
	private static final String SUCCESS = "success";
	private static final String SESSION_STEP = "sessionStep";
    public static final String RESOURCE_ERROR = "BaseUiLabels";
    public static final String WIZARD_PAGES_MAP = "wizardDataMap";
    public static final String WIZARD_PAGES_LIST = "wizardDataList";
    public static final String PARAM_PAGE_MAP = "wizardMapName";
    public static final String PARAM_NEXT_PAGE_MAP = "wizardNextMapName";
    public static final String PARAM_PAGE_INDEX = "wizardIndex";
    
    private WizardEvents() {}

    public static String storeWizardData(HttpServletRequest request, HttpServletResponse response) {
        boolean committed = false;
        String mapName = null;
        try {
            List<String> wizardMapList = null;
            mapName = request.getParameter(PARAM_PAGE_MAP);
            Map<String, Object> sessionMap = UtilHttp.getSessionMap(request);
            if (UtilValidate.isNotEmpty(mapName)) {
                Map<String, Object> parametersMap = UtilHttp.getParameterMap(request, UtilMisc.toSet(PARAM_PAGE_MAP, "currentStep"), false);

                if (UtilValidate.isNotEmpty(parametersMap)) {
                    Map<String, Object> wizardMap = (Map<String, Object>)sessionMap.get(WIZARD_PAGES_MAP);
                    if (wizardMap == null) {
                        wizardMap = FastMap.newInstance();
                        request.getSession().setAttribute(WIZARD_PAGES_MAP, wizardMap);
                    }
                    wizardMapList = getWizardMapList(sessionMap);
                    if (wizardMapList == null) {
                        wizardMapList = FastList.newInstance();
                        request.getSession().setAttribute(WIZARD_PAGES_LIST, wizardMapList);
                    }
                    for (String key : parametersMap.keySet()) {
                        if (key.contains("compositeType")) {
                            String paramName = key.substring(0, key.indexOf(UtilHttp.COMPOSITE_DELIMITER));
                            Object res = UtilHttp.makeParamValueFromComposite(request, paramName, UtilHttp.getLocale(request));
                            if (UtilValidate.isNotEmpty(res)) {
                                ObjectType.simpleTypeConvert(res, "java.lang.String", null, UtilHttp.getLocale(request));
                                parametersMap.put(paramName, res.toString());
                            }
                        }
                    }
                    wizardMap.put(mapName, parametersMap);
                }
            }

            String withoutCurrentStep = request.getParameter("withoutCurrentStep");
            if (UtilValidate.isEmpty(withoutCurrentStep)) {
                Integer oldSessionStep = (Integer)sessionMap.get(SESSION_STEP);
                Integer currentStep = null;
                try {
                    currentStep = Integer.parseInt(request.getParameter("currentStep"));
                } catch (Exception e) {
                }

                if (UtilValidate.isNotEmpty(currentStep)) {
                    if (UtilValidate.isEmpty(oldSessionStep) || (UtilValidate.isNotEmpty(oldSessionStep) && currentStep.intValue() > oldSessionStep.intValue())) {
                        request.getSession().setAttribute(SESSION_STEP, currentStep);
                    }
                }
            }

            // Commit page transition.
            if (wizardMapList != null && UtilValidate.isNotEmpty(mapName)) {
                wizardMapList.add(mapName);
                committed = true;
            }
        } catch (Exception e) {
            if (!committed) {
                // On errors reload current page.
                if (UtilValidate.isNotEmpty(mapName)) {
                    request.setAttribute(PARAM_NEXT_PAGE_MAP, mapName);
                }
            }
            Debug.logError(e, MODULE);
            ServiceUtil.setMessages(request, UtilProperties.getMessage(RESOURCE_ERROR, "BaseWizardStoreParamsError", UtilHttp.getLocale(request)), null, null);
            return ERROR;
        }
        return SUCCESS;
    }

    public static String clearWizardData(HttpServletRequest request, HttpServletResponse response) {
        try {
            clearWizardData(request);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            ServiceUtil.setMessages(request, UtilProperties.getMessage(RESOURCE_ERROR, "BaseWizardClearParamsError", UtilHttp.getLocale(request)), null, null);
            return ERROR;
        }
        return SUCCESS;
    }

    public static String popWizardData(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> sessionMap = UtilHttp.getSessionMap(request);
            Map<String, Object> paramMap = UtilHttp.getParameterMap(request, UtilMisc.toSet(PARAM_PAGE_INDEX), true);
            Object objWizardIndex = paramMap.get(PARAM_PAGE_INDEX);
            int wizardIndex = -1;
            if (UtilValidate.isNotEmpty(objWizardIndex)) {
                wizardIndex = UtilMisc.toInteger(objWizardIndex);
            }
            String mapName = popWizardData(sessionMap, wizardIndex);
            if (UtilValidate.isNotEmpty(mapName)) {
                request.setAttribute(PARAM_NEXT_PAGE_MAP, mapName);
            }
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            ServiceUtil.setMessages(request, UtilProperties.getMessage(RESOURCE_ERROR, "BaseWizardPopParamsError", UtilHttp.getLocale(request)), null, null);
            return ERROR;
        }
        return SUCCESS;
    }

    public static void clearWizardData(HttpServletRequest request) {
        Map<String, Object> sessionMap = UtilHttp.getSessionMap(request);

        if (sessionMap.containsKey(WIZARD_PAGES_MAP)) {
            request.getSession().removeAttribute(WIZARD_PAGES_MAP);
        }
        if (sessionMap.containsKey(WIZARD_PAGES_LIST)) {
            request.getSession().removeAttribute(WIZARD_PAGES_LIST);
        }
        if (sessionMap.containsKey(SESSION_STEP)) {
            request.getSession().removeAttribute(SESSION_STEP);
        }
        if (sessionMap.containsKey("firstStep")) {
            request.getSession().removeAttribute("firstStep");
        }
    }

    public static String popWizardData(Map<String, Object> sessionMap, int index) {
    	int elabIndex = index;
        List<String> list = getWizardMapList(sessionMap);
        if (list != null) {
            int size = list.size();
            if (size > 0) {
                int lastIndex = size - 1;
                if (elabIndex < 0) {
                	elabIndex = size + elabIndex;
                    if (elabIndex < 0) {
                    	elabIndex = 0;
                    }
                } else {
                    if (elabIndex > lastIndex) {
                    	elabIndex = lastIndex;
                    }
                }
                for (int i = lastIndex; i > elabIndex; --i) {
                    list.remove(i);
                }
                return list.remove(elabIndex);
            }
        }
        return null;
    }

    public static boolean isEmptyWizardMapList(HttpServletRequest request) {
        List<String> list = getWizardMapList(request);
        return UtilValidate.isEmpty(list);
    }

    public static List<String> getWizardMapList(HttpServletRequest request) {
        Map<String, Object> sessionMap = UtilHttp.getSessionMap(request);
        return getWizardMapList(sessionMap);
    }

    protected static List<String> getWizardMapList(Map<String, Object> sessionMap) {
        if (sessionMap != null) {
            return (List<String>)sessionMap.get(WIZARD_PAGES_LIST);
        }
        return null;
    }
}
