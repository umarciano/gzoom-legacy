package com.mapsengineering.base.services;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.bl.crud.AbstractCrudHandler;
import com.mapsengineering.base.bl.crud.FkValidationHandler;
import com.mapsengineering.base.bl.crud.OperationCrudHandler;
import com.mapsengineering.base.bl.crud.PkValidationHandler;
import com.mapsengineering.base.bl.crud.TypeValidationHandler;
import com.mapsengineering.base.bl.crud.UniqueIndexValidationHandler;
import com.mapsengineering.base.bl.crud.UploadAttachmentHandler;
import com.mapsengineering.base.bl.crud.ValueValidationHandler;
import com.mapsengineering.base.util.MessageUtil;

/**
 * Base CRUD Service
 * @author sandro
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class CrudServices {
	public static final String MODULE = CrudServices.class.getName();
	
    public static final String DEFAULT_SERVICE = "crudServiceDefaultOrchestration";
    
    private CrudServices() {}

    /**
     * Crud validation  steps. Checks all primary keys of the entity.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServicePkValidation(DispatchContext dctx, Map param) {

        //parsing params
        Delegator delegator = dctx.getDelegator();
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        Map parameters = (Map) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        Map context = (Map) param.get("context");

        //Intancing and executing business logic object for this service
        try {

            AbstractCrudHandler ach = new PkValidationHandler();
            ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
               return ach.getReturnMap();

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }

    /**
     * Crud validation steps. Checks all foreign keys existence.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceFkValidation(DispatchContext dctx, Map param) {

        //parsing params
        Delegator delegator = dctx.getDelegator();
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        Map parameters = (Map) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        Map context = (Map) param.get("context");

        //Intancing and executing business logic object for this service
        try {

            AbstractCrudHandler ach = new FkValidationHandler();
            ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
               return ach.getReturnMap();

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }

    /**
     * Crud validation steps. Checks all unique index existence.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceUniqueIndexValidation(DispatchContext dctx, Map param) {

        //parsing params
        Delegator delegator = dctx.getDelegator();
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        Map parameters = (Map) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        Map context = (Map) param.get("context");

        //Intancing and executing business logic object for this service
        try {

            AbstractCrudHandler ach = new UniqueIndexValidationHandler();
            ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
               return ach.getReturnMap();

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }

    /**
     * Crud validation  steps. Checks parameter fields type against relative entity fields type.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceTypeValidation(DispatchContext dctx, Map param) {

        //parsing params
        Delegator delegator = dctx.getDelegator();
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        Map parameters = (Map) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        Map context = (Map) param.get("context");

        //Intancing and executing business logic object for this service
        try {

            AbstractCrudHandler ach = new TypeValidationHandler();
            ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
               return ach.getReturnMap();

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }

    /**
     * Crud validation steps. Checks parameter fields type against relative entity fields type.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceValueValidation(DispatchContext dctx, Map param) {

        //parsing params
        Delegator delegator = dctx.getDelegator();
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        Map parameters = (Map) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        Map context = (Map) param.get("context");

        //Intancing and executing business logic object for this service
        try {

            AbstractCrudHandler ach = new ValueValidationHandler();
            ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
               return ach.getReturnMap();

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }
    
    /**
     * Crud validation  steps. Checks parameter fields type against relative entity fields type.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceUploadAttachment(DispatchContext dctx, Map param) {

    	try {
    		//parsing params
    		Delegator delegator = dctx.getDelegator();
    		String entityName = (String) param.get("entityName");
    		String operation = (String) param.get("operation");
    		Map parameters = (Map) param.get("parameters");
    		Locale locale = (Locale) param.get("locale");
    		TimeZone timeZone = (TimeZone) param.get("timeZone");
    		
    		Map context = (Map) param.get("context");

    		String uploadServiceName = UtilGenerics.cast(param.get("uploadServiceName"));
    		if (UtilValidate.isEmpty(uploadServiceName)) {
    			uploadServiceName = "createContentFromUploadedFile";
    		}
    		
    		ModelService model = dctx.getModelService(uploadServiceName);

    		if (UtilValidate.isNotEmpty(model)) {
    			Map<String, Object> uploadServiceInParametersMap = new HashMap<String, Object>();
    			for (ModelParam modelParam : model.getInModelParamList()) {
    				String paramName = modelParam.getName();
    				Object value = param.get(paramName);
    				if (UtilValidate.isEmpty(value)) {
    					value = parameters.get(paramName);
    				}
    				if (UtilValidate.isNotEmpty(value)) {
    					try {
                            value = ObjectType.simpleTypeConvert(value, modelParam.type, null, timeZone, locale, true);
                        } catch (GeneralException e) {
                            String errMsg = "Could not convert field value for the parameter/attribute: [" + modelParam.name + "] on the [" + uploadServiceName + "] service to the [" + modelParam.type + "] type for the value [" + value + "]: " + e.toString();
                            Debug.logError(e, errMsg, MODULE);
                            // add the message to the list and set the value to null - tried and failed, just leave it out
                            value = null;
                        }
    					uploadServiceInParametersMap.put(paramName, value);
    				}
    			}
    			
    			if (UtilValidate.isNotEmpty(uploadServiceInParametersMap)) {
    				if (UtilValidate.isEmpty(context)) {
    	    			context = new HashMap<String, Object>();
    					param.put("context", context);
    	    		}
    				context.put("uploadServiceInParametersMap", uploadServiceInParametersMap);
    				context.put("dispatchContext", dctx);
    				context.put("uploadServiceName", uploadServiceName);
    			}
    		}

    		//Intancing and executing business logic object for this service
    		try {

    			AbstractCrudHandler ach = new UploadAttachmentHandler();
    			ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
    			return ach.getReturnMap();

    		} catch (Exception e) {
    			return MessageUtil.buildErrorMap("GenericError", e, locale);
    		}
    	} catch (GenericServiceException e1) { 	}
    	
    	return param;
    }

    /**
     * Crud execution steps. Execute relative operation against db.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceOperation(DispatchContext dctx, Map param) {

        //parsing params
        Delegator delegator = dctx.getDelegator();
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        Map parameters = (Map) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        Map context = (Map) param.get("context");

        //Intancing and executing business logic object for this service
        try {

            AbstractCrudHandler ach = new OperationCrudHandler();
            ach.execute(delegator, entityName, operation, locale, timeZone, parameters, context);
               return ach.getReturnMap();

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }

    /**
     * Call CRUD Base service passing parameters and context like string of key-value pairs.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudService(DispatchContext dctx, Map parameters) {

        Locale locale = (Locale) parameters.get("locale");

        try {

            String crudService = (String) parameters.remove("crudService");

            if (UtilValidate.isEmpty(crudService)) {
                crudService = CrudServices.DEFAULT_SERVICE;
            }

            Map<String, Object> ret = dctx.getDispatcher().runSync(crudService, parameters);

            return ret;

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }
    }

    /**
     * Call CRUD Base service passing parameters and context like string of key-value pairs.
     * @return Error Map. Empty if all ok, filled with error elsewhere.
     */
    public static Map crudServiceByStringParam(DispatchContext dctx, Map param) {

        //parsing params
        String crudService = (String) param.get("crudService");
        String entityName = (String) param.get("entityName");
        String operation = (String) param.get("operation");
        String parameters = (String) param.get("parameters");
        Locale locale = (Locale) param.get("locale");
        TimeZone timeZone = (TimeZone) param.get("timeZone");
        GenericValue userLogin = (GenericValue) param.get("userLogin");

        Map serviceMap = FastMap.newInstance();

        try {
            Map paramMap = StringUtil.strToMap(parameters, "|");
            Map contextMap = StringUtil.strToMap(parameters, "|");

            serviceMap.put("entityName", entityName);
            serviceMap.put("operation", operation);
            serviceMap.put("parameters", paramMap);
            serviceMap.put("locale", locale);
            serviceMap.put("timeZone", timeZone);
            serviceMap.put("context", contextMap);
            serviceMap.put("userLogin", userLogin);

            if (UtilValidate.isEmpty(crudService)) {
                crudService = CrudServices.DEFAULT_SERVICE;
            }

            //Get return parameters if any
            Map<String, Object> ret = dctx.getDispatcher().runSync(crudService, serviceMap);
            //Check return id's (only for create operation)
            Object id = ret.get("id");
            if (UtilValidate.isNotEmpty(id)) {
                Map<String, Object> idMap = UtilGenerics.checkMap(id);               
                	for (Map.Entry<String, Object>  idMapEntry : idMap.entrySet()) {
                    Object value = idMapEntry.getValue();
                    if (UtilValidate.isNotEmpty(value) && !(value instanceof String)) {
                        try {
                            value = ObjectType.simpleTypeConvert(value, "String", null, locale);
                        } catch (Exception e) {
                            value = "";
                        }

                        idMap.put(idMapEntry.getKey(), value);
                    }
                }

                String ids = StringUtil.mapToStr(idMap);
                ret.put("id", ids);
            }

            Object retValues = ret.get("retValues");
            StringBuffer retValuesStringBuf = new StringBuffer();
            if (UtilValidate.isNotEmpty(retValues)) {
                List retList = (List)retValues;
                for (Object item: retList) {
                    if (item instanceof String) {
                        if (UtilValidate.isNotEmpty(retValuesStringBuf)) {
                        	retValuesStringBuf.append(";");
                        }
                        retValuesStringBuf.append((String)item);
                    }
                    if (item instanceof Map) {
                        if (UtilValidate.isNotEmpty(retValuesStringBuf)) {
                        	retValuesStringBuf.append(";");
                        }
                        retValuesStringBuf.append(StringUtil.mapToStr((Map)item, "|"));
                    }
                }
            }
            if (UtilValidate.isNotEmpty(retValuesStringBuf)) {
                ret.put("retValues", retValuesStringBuf.toString());
            }

            return ret;

        } catch (Exception e) {
            return MessageUtil.buildErrorMap("GenericError", e, locale);
        }

    }

}
