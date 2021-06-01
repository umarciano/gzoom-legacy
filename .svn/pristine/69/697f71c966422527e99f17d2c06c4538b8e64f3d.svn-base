package org.ofbiz.widget;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.ServiceUtil;

public class WidgetUserPrefEvents {

	public static final String module = WidgetUserPrefEvents.class.getName();
	/**
	 * Event return type
	 */
	public static final String RETURN_SUCCESS = "success";	
	/**
	 * Event return type
	 */
	public static final String RETURN_ERROR = "error";	

	/**
	 * Checks form param map if exists param value map for a name 
	 * @param formParam
	 * @param name
	 * @return new or param map
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> getFieldParamMap(Map<String, Object> formParam, String name) {
		Map<String, Object> values;
		if (formParam.containsKey(name)) {
			values = (Map<String, Object>)formParam.get(name);
		} else {
			values = FastMap.newInstance();
			formParam.put(name, values);
		}
		return values;
	}
	
	/**
	 * Check and convert parameter to String 
	 * @param parm
	 * @return String value
	 */
	@SuppressWarnings("unchecked")
	private static String paramToString(Object param) {
		if (UtilValidate.isNotEmpty(param)) {
			if (param instanceof List) {
				return ((List<String>)param).get(0);
			} else if (param instanceof String) {
				return (String)param;
			}
		}
		return "";
	}
	
	
	/**
	 * Check type of request parameter and return it like List<String> 
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<String> paramToList(Object param) {
		List<String> list = FastList.newInstance();
		if (param instanceof List) {
			list = (List<String>)param;
		} else if (param instanceof String) {
			list.add((String)param);
		}
		return list;
	}
	
	/**
	 * Save User Preferences Event
	 * @param request
	 * @param response
	 * @return
	 */
	public static String saveWidgetUserPref(HttpServletRequest request, HttpServletResponse response) {
		try {
	        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin"); 
			Locale locale = UtilHttp.getLocale(request);
			GenericDispatcher dispatcher = (GenericDispatcher) request.getAttribute("dispatcher");

			Map <String, Object> parameterMap = UtilHttp.getParameterMap(request);
			
			//Get form name list or single parm
			Object formNameParm = parameterMap.get("formName");
			List<String> formNameArray = WidgetUserPrefEvents.paramToList(formNameParm);
			
			//form array loop
			for (String formName: formNameArray) {
				
				//Set current form name
				Map<String, Object> formParam = FastMap.newInstance();
				formParam.put("formName", formName);
				
				//loop over current form field
				for (String key: parameterMap.keySet()) {

					//Check if param name contains form name
					if (key.indexOf(formName)==-1) {
						continue;
					}

					//Check parameters type
					if (key.startsWith("pos_")) {
						//position parameters
						String name = key.substring(4);
						//create value map for this parameter
						Map<String, Object> values = WidgetUserPrefEvents.getFieldParamMap(formParam, name);
						//Set value
						values.put("position", WidgetUserPrefEvents.paramToString(parameterMap.get(key)));
					}
					if (key.startsWith("groupId_")) {
						//Group id parameter
						String name = key.substring(8);
						//create value map for this parameter
						Map<String, Object> values = WidgetUserPrefEvents.getFieldParamMap(formParam, name);
						//Set value
						values.put("groupId", WidgetUserPrefEvents.paramToString(parameterMap.get(key)));
					}
					if (key.startsWith("width_")) {
						//Group id parameter
						String name = key.substring(6);
						//create value map for this parameter
						Map<String, Object> values = WidgetUserPrefEvents.getFieldParamMap(formParam, name);
						//Set value
						values.put("width", WidgetUserPrefEvents.paramToString(parameterMap.get(key)));
					}
				}

				//save current form settings
				Map<String, Object> serviceMap = FastMap.newInstance();
				serviceMap.put("userLogin", userLogin);
				serviceMap.put("locale", locale);
				serviceMap.put("formName", formName);
				serviceMap.put("params", formParam);

				Map<String, Object> res = dispatcher.runSync("saveWidgetUserPref", serviceMap);
				
				if (ServiceUtil.isError(res)||ServiceUtil.isFailure(res)) {
					request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(res));            
					return WidgetUserPrefEvents.RETURN_ERROR;
				}
			}
		} catch (Exception e) {
			String errMsg = "[WidgetUserPrefEvents.saveWidgetUserPrefEvent] Exception: " + e.getMessage();
			request.setAttribute("_ERROR_MESSAGE_", errMsg);            
			Debug.logError(errMsg, module);
			return "error";
		}
		return WidgetUserPrefEvents.RETURN_SUCCESS;
	}
	
}
