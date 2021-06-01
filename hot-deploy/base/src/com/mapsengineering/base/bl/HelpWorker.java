package com.mapsengineering.base.bl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.base.util.MessageUtil;

public final class HelpWorker {
	
	private static final String MODULE = HelpWorker.class.getName();
	
	public static final String GPLUS_HELP_DATA_CATEGORY = "GPLUS_HELP";
	public static final String GPLUS_HELP_PLACEHOLDER = "(GPLUS HELP)";
	
	private HelpWorker() {}
	
	/**
	 * Builds param map for search contextual help
	 * @param entityName
	 * @param fieldName
	 * @param formName
	 * @param locale
	 * @return Map filled with params
	 */
	public static Map<String, ? extends Object> buildHelpSearchMap(String formName, String entityName, String fieldName, Locale locale) {
		//Build key for resource 
		StringBuilder sb = new StringBuilder();
		if (UtilValidate.isNotEmpty(formName)) {
			sb.append(formName.trim());
			sb.append("#");
		}
		sb.append(entityName.trim());
		sb.append("#");
		sb.append(fieldName.trim());

		return UtilMisc.toMap("objectInfo", sb.toString(), "dataCategoryId", GPLUS_HELP_DATA_CATEGORY,
					"dataResourceTypeId", "ELECTRONIC_TEXT", "localeString", locale.getCountry());
	}
	
	/**
	 * Gets contextual help data resource Id
	 * @param entityName
	 * @param fieldName
	 * @param formName
	 * @param accurate
	 * @param locale
	 * @param delegator
	 * @return Map with dataResourceId key value pair or ErrorMessages.
	 */
	public static Map<String, Object> getHelpDataResourceId(String formName, String entityName, String fieldName, boolean accurate, Locale locale, Delegator delegator) {

		Map<String, Object> res = FastMap.newInstance();
		try {
			//Look for dataResource
			//initially with full key
			Map<String, ? extends Object> parmsMap = HelpWorker.buildHelpSearchMap(formName, entityName, fieldName, locale);
			
			EntityCondition where = EntityCondition.makeCondition(parmsMap,	EntityJoinOperator.AND); 
			List<GenericValue> list = delegator.findList("DataResource", where, null, null, null, true);
			
			//Not found, search with only entity and field
			if (UtilValidate.isEmpty(list) && UtilValidate.isNotEmpty(formName) && !accurate) {
				//partial key
				parmsMap = HelpWorker.buildHelpSearchMap("", entityName, fieldName, locale);
				where = EntityCondition.makeCondition(parmsMap,	EntityJoinOperator.AND); 
				list = delegator.findList("DataResource", where, null, null, null, true);
			}

			String dataResourceId = "";
			if (UtilValidate.isNotEmpty(list)) {
				dataResourceId = EntityUtil.getFirst(list).getString("dataResourceId");
			}
			
			res.put("dataResourceId", dataResourceId);
			
		} catch (GenericEntityException e) {
			Debug.logError(e, MODULE);
			res.putAll(MessageUtil.buildErrorMap("GenericError", e, locale));
		}
		return res;
	}

	
	/**
	 * Create and persists new Help Content
	 * @param context, Map with seg. keys: entityName, fieldName, formName, locale - key for content  
	 * @param dispatcher
	 * @return Map with errors
	 */
	public static Map<String, Object> createHelpContent(String formName, String entityName, String fieldName, Locale locale, String helpText, GenericValue userLogin, GenericDispatcher dispatcher) {
		
		Map<String, Object> res = FastMap.newInstance();
		Map<String, Object>  ctx = FastMap.newInstance();
		
		ctx.putAll(HelpWorker.buildHelpSearchMap(formName, entityName, fieldName, locale));
		
		if (UtilValidate.isEmpty(helpText)) {
			ctx.put("textData", GPLUS_HELP_PLACEHOLDER);			
		} else {
			ctx.put("textData", helpText);
		}
		ctx.put("userLogin", userLogin);
		ctx.put("mimeTypeId","text/html");
		ctx.put("dataResourceName",GPLUS_HELP_DATA_CATEGORY);
		ctx.put("statusId", "CTNT_PUBLISHED");
		
        try {
        	
			res.putAll(dispatcher.runSync("createDataResourceAndText", ctx));
			
		} catch (GenericServiceException e) {
			Debug.logError(e, MODULE);
			res.putAll(MessageUtil.buildErrorMap("GenericError", e, locale));
		}
		
		return res;
	}
	
	/**
	 * Gets textual content for relative dataResourceId
	 * @param dataResourceId
	 * @param delegator
	 * @return  a Map with helpText key-value pair, ErrorMessages otherwise
	 */
	public static Map<String, Object> getHelpContent(String dataResourceId, Locale locale, Delegator delegator) {
		
		Map<String, Object> res = FastMap.newInstance();		
		try {
			EntityCondition where = EntityCondition.makeCondition(UtilMisc.toMap("dataResourceId", dataResourceId),	EntityJoinOperator.AND);
			List<GenericValue> list = delegator.findList("ElectronicText", where, null, null, null, true);
			if (UtilValidate.isNotEmpty(list)) {
				GenericValue value = EntityUtil.getFirst(list);
				res.put("helpText",value.getString("textData"));
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, MODULE);
			res.putAll(MessageUtil.buildErrorMap("GenericError", e, locale));			
		}
		
		return res;
	}
	
}
