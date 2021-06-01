package com.mapsengineering.base.events;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.control.SessionViewHistory;

/**
 * Gestisce sessione
 *
 */
public final class SessionHistoryWorker {

	public static final String MODULE = SessionHistoryWorker.class.getName();
	
	private static final String ENTITY_NAME = "entityName";
	private static final String LOOKUP = "lookup";
    private static final String SUCCESS = "success";
    private static final String MANAGEMENT_FORM_TYPE = "managementFormType";
    private static final String SUCCESS_CODE = "successCode";
    private static final String ORDER_BY = "orderBy";
    private static final String QUERY_STRING_MAP = "queryStringMap";
    private static final String SEARCH_PARAMS_MAP_ = "_searchParamsMap_";
    
    private SessionHistoryWorker() {}

    /**
     * checkSaveView 
     * @param request
     * @param response
     * @return SUCCESS 
     */
    public static String checkSaveView(HttpServletRequest request, HttpServletResponse response) {
        return SUCCESS;
    }

    /**
     * If there is SessionViewHistory for _SAVED_VIEW_ return Y else return N
     * @param request
     * @param response
     * @return Y or N
     */
    public static String checkExistenceSaveViewStack(HttpServletRequest request, HttpServletResponse response) {
        SessionViewHistory svh = SessionViewHistory.get(request, "_SAVED_VIEW_");

        if (UtilValidate.isEmpty(svh) || (UtilValidate.isNotEmpty(svh) && svh.isEmpty())) {
            return "N";
        }

        return "Y";
    }
    
    /**
     * Get last _SAVED_VIEW_ and read his backAreaId, then set returnBackAreaId in request
     * @param request
     * @param response
     * @return SUCCESS
     */
    public static String readReturnBackAreaId(HttpServletRequest request, HttpServletResponse response) {
    	SessionViewHistory.Entry lastView = SessionViewHistory.get(request, "_SAVED_VIEW_").peek();
    	if (UtilValidate.isNotEmpty(lastView) && UtilValidate.isNotEmpty(lastView.getParamMap())) {
    		String backAreaId = UtilGenerics.cast(lastView.getParamMap().get("backAreaId"));
    		if (UtilValidate.isNotEmpty(backAreaId)) {
    			request.setAttribute("returnBackAreaId", backAreaId);
    		}
    	}
    	
    	return SUCCESS;
    }
    
    /**
     * Read 
     * @param request
     * @param response
     * @return
     */
    public static String readFindQueryString(HttpServletRequest request, HttpServletResponse response) {
    	String requestUri = RequestHandler.getRequestUri(request.getPathInfo());
    	
    	String searchFromManagement = UtilGenerics.cast(request.getParameter("searchFromManagement"));
		if (UtilValidate.isEmpty(searchFromManagement)) {
			searchFromManagement = UtilGenerics.cast(request.getAttribute("searchFromManagement"));
		}
		if (UtilValidate.isEmpty(searchFromManagement)) {
			searchFromManagement = "Y";
		}
    	
    	if ("startSearch".equals(requestUri) || "searchContainerOnly".equals(requestUri) || "searchResultContainerOnly".equals(requestUri) || ("managementContainerOnly".equals(requestUri) && "Y".equals(searchFromManagement))) {
    		String entityName = UtilGenerics.cast(request.getParameter(ENTITY_NAME));
    		if (UtilValidate.isEmpty(entityName)) {
    			entityName = UtilGenerics.cast(request.getAttribute(ENTITY_NAME));
    		}

    		if (UtilValidate.isNotEmpty(entityName)) {
    			String lookup = UtilGenerics.cast(request.getParameter(LOOKUP));
				if (UtilValidate.isEmpty(lookup)) {
					lookup = UtilGenerics.cast(request.getAttribute(LOOKUP));
				}
				if (UtilValidate.isEmpty(lookup)) {
					lookup = "N";
				}
    			
    			if (UtilValidate.isEmpty(request.getParameter("ignoreFromSession")) && UtilValidate.isEmpty(request.getAttribute("ignoreFromSession"))) {
    				Map<String, Object> searchParamsMap = UtilGenerics.checkMap(request.getSession().getAttribute(entityName + SEARCH_PARAMS_MAP_ + lookup));
    				if (UtilValidate.isNotEmpty(searchParamsMap)) {
    					Map<String, Object> queryStringMap = UtilGenerics.checkMap(searchParamsMap.get(QUERY_STRING_MAP));
    					if (UtilValidate.isNotEmpty(queryStringMap)) {   						
    							for (Map.Entry<String, Object>  queryStringMapEntry : queryStringMap.entrySet()) {
    							request.setAttribute(queryStringMapEntry.getKey(), queryStringMapEntry.getValue());
    						}
    					}

    					String orderBy = UtilGenerics.cast(searchParamsMap.get(ORDER_BY));
    					if (UtilValidate.isEmpty(request.getParameter(ORDER_BY)) && UtilValidate.isEmpty(request.getAttribute(ORDER_BY)) && UtilValidate.isNotEmpty(orderBy)) {
    						request.setAttribute(ORDER_BY, orderBy);
    					}
    					String successCode = UtilGenerics.cast(searchParamsMap.get(SUCCESS_CODE));
    					if (UtilValidate.isEmpty(request.getParameter(SUCCESS_CODE)) && UtilValidate.isEmpty(request.getAttribute(SUCCESS_CODE)) && UtilValidate.isNotEmpty(successCode)) {
    						request.setAttribute(SUCCESS_CODE, successCode);
    					}
    					String managementFormType = UtilGenerics.cast(searchParamsMap.get(MANAGEMENT_FORM_TYPE));
    					if (UtilValidate.isEmpty(request.getParameter(MANAGEMENT_FORM_TYPE)) && UtilValidate.isEmpty(request.getAttribute(MANAGEMENT_FORM_TYPE)) && UtilValidate.isNotEmpty(managementFormType)) {
    						request.setAttribute(MANAGEMENT_FORM_TYPE, managementFormType);
    					}
    					
    					String searchFormLocationParameters = UtilGenerics.cast(searchParamsMap.get("searchFormLocationParameters"));
    					if (UtilValidate.isNotEmpty(searchFormLocationParameters)) {
    						Map<String, String> searchFormLocationParameterMap = StringUtil.strToMap(searchFormLocationParameters, "&", false);
    						
    						if (UtilValidate.isNotEmpty(searchFormLocationParameterMap)) {
    							for (Map.Entry<String, String>  mapEntry : searchFormLocationParameterMap.entrySet()) {
    								request.setAttribute(mapEntry.getKey(), mapEntry.getValue());
    	                		}
    						}
    					}
    				}
    			} else {
    				request.getSession().removeAttribute(entityName + SEARCH_PARAMS_MAP_ + lookup);
    			}
    		}
    	}
    	
    	return SUCCESS;
    }
    
    /**
     * Gestisce in sessione la mappa coi parametri della ricerca, queryStringMap, queryString, ecc...
     * La mappa ha una chiave del tipo:
     * entityName + _searchParamsMap_ + Y o N se e' 
     * Se la requestUri = search la mappa viene ripulita
     * Poi se la request e' search, startSearch, searchResultContainerOnly, managementContainerOnly con management = SUCCESS_CODE && parameters.detail != Y
     * la mappa viene ripopolata
     * 
     * @param request
     * @param response
     * @return
     */
    public static String storeFindQueryString(HttpServletRequest request, HttpServletResponse response) {
        String requestUri = RequestHandler.getRequestUri(request.getPathInfo());
    	
    	String entityName = UtilGenerics.cast(request.getParameter(ENTITY_NAME));
		if (UtilValidate.isEmpty(entityName)) {
			entityName = UtilGenerics.cast(request.getAttribute(ENTITY_NAME));
		}
		String lookup = UtilGenerics.cast(request.getParameter(LOOKUP));
		if (UtilValidate.isEmpty(lookup)) {
			lookup = UtilGenerics.cast(request.getAttribute(LOOKUP));
		}
		if (UtilValidate.isEmpty(lookup)) {
			lookup = "N";
		}
    	
    	if ("search".equalsIgnoreCase(requestUri)) {
    	    request.getSession().removeAttribute(entityName + SEARCH_PARAMS_MAP_ + lookup);
    	}
    	
    	Map<String, Object> parameters = UtilHttp.getParameterMap(request);
    	parameters.putAll(UtilHttp.getAttributeMap(request));
        if ("search".equals(requestUri) || "startSearch".equals(requestUri) || "searchResultContainerOnly".equals(requestUri)
            || (
                "managementContainerOnly".equals(requestUri) && "management".equals(parameters.get(SUCCESS_CODE)) 
                && !"Y".equals(parameters.get("detail"))
                && (UtilValidate.isEmpty(parameters.get("insertMode")) || "N".equals(parameters.get("insertMode")))
            )
        ) {
            Map<String, Object> searchParamsMap = new HashMap<String, Object>();
    		if (UtilValidate.isNotEmpty(parameters.get(QUERY_STRING_MAP))) {
	    		Map<String, Object> queryStringMap = UtilGenerics.checkMap(parameters.get(QUERY_STRING_MAP));
	    		String queryString = StringUtil.mapToStr(queryStringMap, "&");
	    	
	    		
	    		searchParamsMap.put("queryString", queryString);
	    		searchParamsMap.put(QUERY_STRING_MAP, queryStringMap);
	    	}
	    	
	    	if (UtilValidate.isNotEmpty(parameters.get(ORDER_BY))) {
	    		searchParamsMap.put(ORDER_BY, parameters.get(ORDER_BY));
	    	}
	    	if (UtilValidate.isNotEmpty(parameters.get(SUCCESS_CODE))) {
	    		searchParamsMap.put(SUCCESS_CODE, parameters.get(SUCCESS_CODE));
	    	}
	    	if (UtilValidate.isNotEmpty(parameters.get(MANAGEMENT_FORM_TYPE))) {
	    		searchParamsMap.put(MANAGEMENT_FORM_TYPE, parameters.get(MANAGEMENT_FORM_TYPE));
	    	}
	    	
	    	
	    	/* TODO TOGLIERE DA QUI */
	    	StringBuilder searchFormLocationParameters = new StringBuilder();
	    	SessionHistoryWorker worker = new SessionHistoryWorker();
	    	worker.appendParameter(parameters, "advancedSearchFormLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "advancedSearchFormScreenLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchFormLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchFormResultLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchFormResultScreenName", searchFormLocationParameters);
            worker.appendParameter(parameters, "searchFormResultScreenLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchFormScreenLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchFormScreenName", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchResultContextFormLocation", searchFormLocationParameters);
	    	worker.appendParameter(parameters, "searchResultContextFormName", searchFormLocationParameters);
	    	
	    	if (UtilValidate.isNotEmpty(searchFormLocationParameters)) {
	    		searchParamsMap.put("searchFormLocationParameters", searchFormLocationParameters.toString());
	    	}
	    	
	    	if (UtilValidate.isNotEmpty(searchParamsMap)) {
	    		request.getSession().setAttribute(entityName + SEARCH_PARAMS_MAP_ + lookup, searchParamsMap);
	    	}
	    	
    	}
    	
    	return SUCCESS;
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String isLastViewAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
        SessionViewHistory svh = SessionViewHistory.get(request, "_SAVED_VIEW_");

        if (UtilValidate.isNotEmpty(svh) && !svh.isEmpty()) {
            SessionViewHistory.Entry entry = svh.peek();
            if (UtilValidate.isNotEmpty(entry)) {
            	Map<String, Object> paramMap = entry.getParamMap();

	            if (UtilValidate.isNotEmpty(paramMap) && UtilValidate.isNotEmpty(paramMap.get("ajaxRequest"))) {
	                return paramMap.get("ajaxRequest").toString();
	            }
            }
        }

        return "Y";
    }
    
    private StringBuilder appendParameter(Map<String, Object> parameters, String parameterName, StringBuilder searchFormLocationParameters) {
        if (UtilValidate.isNotEmpty(parameters.get(parameterName))) {
            if (UtilValidate.isNotEmpty(searchFormLocationParameters)) {
                searchFormLocationParameters.append("&");
            }
            searchFormLocationParameters.append(parameterName + "=").append(parameters.get(parameterName));
        }
        return searchFormLocationParameters;
    }
}
