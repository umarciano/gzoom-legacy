package com.mapsengineering.base.services;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;

import com.mapsengineering.base.bl.HelpWorker;

/**
 * Help management services
 */
public class HelpServices {
	
	private HelpServices() {}

	/**
	 * Gets on line help for a form field. 
	 * Keys are in sequence: form, entity, field. 
	 * Entity and field are mandatory. 
	 * If accurate flag is set, search is done precise. If key is not found, service creates the key. 
	 * If accurate flag is not set (default), search is done before with entire key, then with only entity and field. If not found key, service creates
	 * key with only entity and field.  
	 * @param dctx
	 * @param formName (in), entityName (in), fieldName (in), accurate (in - default false), helpText (out),  
	 * @return result Map, a string helpText variable filled with help conten or errorMessage and errorMessagesList
	 */
	@SuppressWarnings("unchecked")
	public static Map getContextHelp(DispatchContext dctx,  Map context) {
		
        //parsing params
		GenericDispatcher dispatcher = (GenericDispatcher)dctx.getDispatcher();
		//formName not mandatory
        String formName =   (context.get("formName")!=null) ? (String) context.get("formName") : "";
        String entityName = (String) context.get("entityName");       
        String fieldName = (String) context.get("fieldName");
       	boolean accurate = ((Boolean) context.get("accurate")).booleanValue();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map<String, Object> res = HelpWorker.getHelpDataResourceId(formName, entityName, fieldName, accurate, locale, dispatcher.getDelegator());
        
        //If not found dataResourceId i have to create 
        //Help resource
        if (UtilValidate.isEmpty(res.get("dataResourceId"))) {
        	if (accurate) {
        		//if accurate i create with entire key 
        		res = HelpWorker.createHelpContent(formName, entityName, fieldName, locale, null, userLogin, dispatcher);
        	} else {
        		//else i create with partial key
        		res = HelpWorker.createHelpContent("", entityName, fieldName, locale, null, userLogin, dispatcher);
        	}
        }
        
        //Now i get help
        res = HelpWorker.getHelpContent((String)res.get("dataResourceId"), locale ,dispatcher.getDelegator());
        return res;
	}
	
	
	/**
	 * Create on context help (helpText may be null, in this case will be created help content with default text)
	 * @param dctx
	 * @param context formName, entityName, fieldName, helpText
	 * @return Map with result, dataResourceId of help content or errorMap
	 */
	@SuppressWarnings("unchecked")
	public static Map createContextHelp(DispatchContext dctx,  Map context) {
		
        //parsing params
		GenericDispatcher dispatcher = (GenericDispatcher)dctx.getDispatcher();
        String formName = (String) context.get("formName");		
        String entityName = (String) context.get("entityName");       
        String fieldName = (String) context.get("fieldName");
        Locale locale = (Locale) context.get("locale");
        String helpText = (String) context.get("helpText");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        return HelpWorker.createHelpContent(formName, entityName, fieldName, locale, helpText, userLogin, dispatcher);
	}
	
	
	
}
