package com.mapsengineering.base.events;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.control.LoginWorker;

/** Put parameters and cookie value in attribute of request*/
public final class CookieEvents {
	
	public static final String MODULE = CookieEvents.class.getName();
	private static final String DOUBLE_QUOTE = "%22";
	private static final String COOKIE_VIEW_INDEX_ = "_VIEW_INDEX_";
	private static final String COOKIE_PAGINATOR_NUMBER = "PAGINATOR_NUMBER";
	/** Id della riga nella tabella */
    private static final String COOKIE_SELECTED_ROW = "selectedRow";
	/** Id del tab attivo */
    private static final String COOKIE_ACTIVE_LINK = "activeLink";
	/** Id del nodo dell'albero attualmente selezionato	 */
	private static final String COOKIE_SELECTED_ID = "selectedId";
	/** Id del padre del nodo  dell'albero attualmente selezionato     */
    private static final String COOKIE_PARENT_SELECTED_ID = "parentSelectedId";
	/** ordinamento */
	private static final String COOKIE_SORT_FIELD = "sortField";
	
	private static final String COOKIE_TABLE_COOKIES = "tableCookies";
	private static final String COOKIE_PAGINATOR_COOKIES = "paginatorCookies";
    
	private CookieEvents() {}

	/**
	 * Put in request attribute the cookies
	 * @param request
	 * @param response
	 * @return
	 */
	public static String putCookiesInContext(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		if (UtilValidate.isNotEmpty(cookies)) {
			Map<String, String> tableCookies = new HashMap<String, String>();

			Map<String, Object> parameters = UtilHttp.getCombinedMap(request);

			String cookieName = null;
			String entityName = UtilGenerics.cast(parameters.get("entityName"));
			Map<String, String> tableSortField = new HashMap<String, String>();
			Map<String, String> tableActiveTab = new HashMap<String, String>();

			for(int i = 0; i < cookies.length; i++) {
				cookieName = cookies[i].getName();

				if (cookieName.contains(COOKIE_SELECTED_ROW)) {
				    //Example: __CJ_selectedRowtable_WETSRL001ORP_WorkEffortType
					String[] splits = cookieName.split("_");
					String tableName = splits[splits.length - 2];
					entityName = splits[splits.length - 1];
					 //storedValue is like %22#%22
					String correctValue = getCorrectValue(cookies[i].getValue());
					//correctValue contains only the number #
					if(UtilValidate.isNotEmpty(correctValue)) {
					    tableCookies.put(tableName + "_" + entityName, correctValue);
					}
					continue;
				}
				
				/** ignoreFolderIndex usato per non utilizzare l' activeLink proveniente dai cookie */
				if (!"Y".equals((String)parameters.get("ignoreFolderIndex")) && cookieName.contains(COOKIE_ACTIVE_LINK) && cookieName.contains("TabMenu")) {
				    //Example: __CJ_activeLinkWorkEffortViewManagementTabMenu, value: "management_0"
					String tabMenuName = cookieName.substring(cookieName.indexOf(COOKIE_ACTIVE_LINK) + COOKIE_ACTIVE_LINK.length());
					String storedValue = cookies[i].getValue();
					if (UtilValidate.isNotEmpty(storedValue) && storedValue.length() > 6) {
                        String correctValue = storedValue.substring("management_".length()+3, storedValue.length() - 3);
                        if (UtilValidate.isNonnegativeInteger(correctValue)) {
                            tableActiveTab.put(tabMenuName, correctValue);
                        }
					}
					continue;
				}

				if (cookieName.contains(COOKIE_SORT_FIELD)) {
					//Example: __CJ_sortFieldtable_DSTMM001_DataSourceType
					String[] splits = cookieName.split("_");
					String tableName = splits[splits.length - 2];
					entityName = splits[splits.length - 1];
					//storedValue is like %22[-]#%22;
					String correctValue = getCorrectValue(cookies[i].getValue());
                    //correctValue contains only the number #
                    if(UtilValidate.isNotEmpty(correctValue)) {
                        tableSortField.put(tableName + "_" + entityName, correctValue);
                        request.setAttribute(cookieName.substring(5), correctValue);
                    }
					continue;
				}

				if (cookieName.contains(COOKIE_VIEW_INDEX_) && UtilValidate.isEmpty(parameters.get(COOKIE_PAGINATOR_NUMBER))) { 
				    //Example: __CJ_WorkEffortAssignmentAssignmentView_2_VIEW_INDEX_1, value: "1" or null
					//storedValue is like %22#%22
				    //correctValue contains only the number #
		            setCookieValueInRequest(request, cookieName, cookies[i]);
					continue;
				}
				
				//Bug 3506
				if (cookieName.contains(COOKIE_SELECTED_ID)) {
				    // Example: __CJ_selectedId, value: "WorkEffortAssocExtView_child_12059"
				    setCookieValueInRequest(request, cookieName.substring(5), cookies[i]);
					continue;
				}
				if (cookieName.contains(COOKIE_PARENT_SELECTED_ID)) {
					// Example: __CJ_parentSelectedId, value: "WorkEffortAssocExtView_child_12053"
				    setCookieValueInRequest(request, cookieName.substring(5), cookies[i]);
                    continue;
				}
			}

			if (!tableCookies.isEmpty()) {
				request.setAttribute("tableCookies", tableCookies);
			}

			if (!tableSortField.isEmpty()) {
				request.setAttribute("tableSortField", tableSortField);
			}
				
			if (!tableActiveTab.isEmpty()) {
				request.setAttribute("tableActiveTab", tableActiveTab);
			}
		}

        return "success";
    }

	/**
	 * Set in request an attribute with name cookieName
	 * @param request
	 * @param cookieName
	 * @param cookie
	 */
	private static void setCookieValueInRequest(HttpServletRequest request, String cookieName, Cookie cookie) {
	    String storedValue = cookie.getValue();
	    String correctValue = getCorrectValue(storedValue);
	    if(UtilValidate.isNotEmpty(correctValue)) {
            request.setAttribute(cookieName, correctValue);
        }
        
    }

    /**
     * Return value of cookie, without doubleQuote
     * @param storedValue like %22#%22
     * @return #
     */
	private static String getCorrectValue(String storedValue) {
        if(UtilValidate.isNotEmpty(storedValue) && storedValue.indexOf(DOUBLE_QUOTE) > -1) {
            return storedValue.substring(DOUBLE_QUOTE.length(), storedValue.length() - DOUBLE_QUOTE.length());
        }
        return null;
    }

    /**
	 * Remove cookie below 
	 * @param request
	 * @param response
	 * @return
	 */
    public static String removeCookiesFromContext(HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(COOKIE_TABLE_COOKIES);
        request.removeAttribute(COOKIE_PAGINATOR_COOKIES);

        return "success";
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String addExternalLoginKeyToCookie(HttpServletRequest request, HttpServletResponse response) {
    	if ("TRUE".equals(request.getAttribute("_LOGIN_PASSED_"))) {
	    	String externalLoginKey = LoginWorker.getExternalLoginKey(request);
	    	if(UtilValidate.isNotEmpty(externalLoginKey)) {
	    		Map<String, String> jsonMap = UtilMisc.toMap(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, externalLoginKey);
	    		
	            String jsonStr = null;
	            try {
	                jsonStr = JSON.from(jsonMap).toString();
	            } catch (IOException e) {
	                Debug.logError(e, "Error json", MODULE);
	            }
	    		response.setHeader("X-JSON", jsonStr);
	    	}
    	}
    	
    	return "success";
    }
}