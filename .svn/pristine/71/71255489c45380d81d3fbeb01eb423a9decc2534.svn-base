package org.ofbiz.widget;

import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

/**
 * Generic services for User, UserPreferences, UserTools etc.
 * @author Maps 
 *
 */
public class WidgetUserPrefService {

	public static final String module = WidgetUserPrefService.class.getName();

	/**
	 * Save User Preferences Service
	 * @param dctx 
	 * @param context formName (IN); parms (Map all parameters to save into user preferences) 
	 * @return messageMap
	 */
	@SuppressWarnings("unchecked")
	public static Map saveWidgetUserPref(DispatchContext dctx,  Map context) {
		//formName mandatory
		String formName =  (String) context.get("formName");
		//Parms mandatory
		Map parms = (Map) context.get("params");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		return WidgetUserPrefWorker.saveUserPreference(dctx.getDelegator(), userLogin.getString("userLoginId"), formName, parms);
	}

	/**
	 * Gets User preferences 
	 * @param dctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map getWidgetUserPref(DispatchContext dctx,  Map context) {
		
		//formName mandatory
		String formName =  (String) context.get("formName");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String userLoginId = "";
		if(UtilValidate.isEmpty(userLogin)){
			userLoginId = (String) context.get("userLoginId");
		}else{
			userLoginId =  userLogin.getString("userLoginId");
		}

		return WidgetUserPrefWorker.getUserPref(dctx.getDelegator(), userLoginId, formName);
	}
}
