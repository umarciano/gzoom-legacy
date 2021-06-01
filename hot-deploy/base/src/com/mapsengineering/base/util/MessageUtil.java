package com.mapsengineering.base.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Error message static utils container
 * @author sandro
 *
 */
public class MessageUtil {

    private static final String MODULE = MessageUtil.class.getName();

    /**
     * ENtity labels base resources
     */
    public static final String BASE_ENTITY_LABELS = "BaseEntityLabels";

    /**
     * Error messages base resource
     */
    public static final String BASE_ERROR_LABEL = "BaseErrorLabels";

    private MessageUtil() {
    }

    /**
     * Add error message to errorMap.errorMessageList
     */
    @SuppressWarnings("unchecked")
    private static void addErrorMessageToErrorList(Map<String, Object> errorMap, String message) {
        List<Object> errorList = null;
        if (errorMap.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            errorList = (List<Object>)errorMap.get(ModelService.ERROR_MESSAGE_LIST);
        } else {
            errorList = new LinkedList<Object>();
            errorMap.put(ModelService.ERROR_MESSAGE_LIST, errorList);
        }
        errorList.add(message);
    }

    /**
     * Set single error message in a errorMap
     * @param errorMap
     * @param message
     */
    private static void setSingleErrorMessageToErrorMap(Map<String, Object> errorMap, String message) {
        errorMap.put(ModelService.ERROR_MESSAGE, message);
    }

    /**
     * Gets exception message avoiding null
     * @param e Exception
     * @return message or Exception.toString if message is null
     */
    public static String getExceptionMessage(Exception e) {
        if (e == null)
            return "";
        String msg = e.getMessage();
        if (msg == null) {
            msg = e.toString();
        }
        return msg;
    }

    /**
     * Get localized error message from default error resource file
     * @param messageLabel
     * @param locale
     * @return
     */
    public static synchronized String getErrorMessage(String messageLabel, Locale locale) {
        return UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, messageLabel, locale);
    }

    /**
     * Get error message with replace values
     * @param messageLabel Error message label
     * @param locale
     * @param messageValues List replacing string
     * @return Message with replaced values
     */
    public static synchronized String getErrorMessage(String messageLabel, Locale locale, List<String> messageValues) {
        //Get messaggio
        return UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, messageLabel, messageValues, locale);
    }

    /**
     * Get error message with replace values
     * @param messageLabel Error message label
     * @param locale
     * @param messageValues Map replacing value
     * @return Message with replaced values
     */
    public static synchronized String getErrorMessage(String messageLabel, Locale locale, Map<String, ? extends Object> messageValues) {
        //Get messaggio
        return UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, messageLabel, messageValues, locale);
    }

    /**
     * Builds success return map with: return value inside successMessage, success response
     * @param successMessage
     * @return returnMap
     */
    public static synchronized Map<String, Object> buildSuccessMap(String successMessage) {
        return ServiceUtil.returnSuccess(successMessage);
    }

    /**
     * Builds errorMessageMap with: Localized message, java error message, java Cause message
     * @param messageLabel
     * @param ex
     * @param locale
     * @return Error Map
     */
    public static synchronized Map<String, Object> buildErrorMap(String messageLabel, Exception ex, Locale locale) {
        String message = getErrorMessage(messageLabel, locale);
        return ServiceUtil.returnError(message, UtilMisc.toList(MessageUtil.getExceptionMessage(ex)));
    }

    /**
     * Builds errorMessageMap
     * @param messageLabel
     * @param locale
     * @return Error Map
     */
    public static synchronized Map<String, Object> buildErrorMap(String messageLabel, Locale locale) {
        String message = getErrorMessage(messageLabel, locale);
        return ServiceUtil.returnError(message);
    }

    /**
     * Builds replaced errorMessageMap with: Localized message, replace values
     * @param messageLabel
     * @param locale
     * @param message replace Values
     * @return Error Map
     */
    public static synchronized Map<String, Object> buildErrorMap(String messageLabel, Locale locale, List<String> replaceValues) {
        String message = getErrorMessage(messageLabel, locale, replaceValues);
        return ServiceUtil.returnError(message);
    }

    /**
     * Builds replaced errorMessageMap with: Localized message, replace values
     * @param messageLabel
     * @param locale
     * @param message replace Values
     * @return Error Map
     */
    public static synchronized Map<String, Object> buildErrorMap(String messageLabel, Locale locale, Map<String, ? extends Object> replaceValues) {
        String message = getErrorMessage(messageLabel, locale, replaceValues);
        return ServiceUtil.returnError(message);
    }

    /**
     * Builds replaced errorMessageMap with: Localized message, java error message, java Cause message, replace values
     * @param messageLabel
     * @param Exception
     * @param locale
     * @param message replace Values
     * @return Error Map
     */
    public static synchronized Map<String, Object> buildErrorMap(String messageLabel, Exception ex, Locale locale, List<String> replaceValues) {
        String message = getErrorMessage(messageLabel, locale, replaceValues);
        return ServiceUtil.returnError(message, UtilMisc.toList(MessageUtil.getExceptionMessage(ex)));
    }

    /**
     * Builds replaced errorMessageMap with: Localized message, java error message, java Cause message, replace values
     * @param messageLabel
     * @param Exception
     * @param locale
     * @param message replace Values
     * @return Error Map
     */
    public static synchronized Map<String, Object> buildErrorMap(String messageLabel, Exception ex, Locale locale, Map<String, Object> replaceValues) {
        String message = getErrorMessage(messageLabel, locale, replaceValues);
        return ServiceUtil.returnError(message, UtilMisc.toList(MessageUtil.getExceptionMessage(ex)));
    }

    /**
     * Add Localized message retrived by messageKey to errorList in errorMap.
     * @param messageLabel
     * @param locale
     * @param errorMap
     */
    public static synchronized void addErrorToErrorMessageList(String messageKey, Locale locale, Map<String, Object> errorMap) {
        String message = getErrorMessage(messageKey, locale);
        MessageUtil.addErrorMessageToErrorList(errorMap, message);
    }

    /**
     * Add Localized message retrived by messageKey and with value replaced by keys found in replaceValueMap.
     * @param messageLabel
     * @param replaceValueMap
     * @param locale
     * @param errorMap
     */
    public static synchronized void addErrorToErrorMessageList(String messageKey, Locale locale, Map<String, ? extends Object> replaceValueMap, Map<String, Object> errorMap) {
        String message = getErrorMessage(messageKey, locale, replaceValueMap);
        MessageUtil.addErrorMessageToErrorList(errorMap, message);
    }

    /**
     * Add error message to errorList in errorMap.
     * @param message
     * @param errorMap
     */
    public static synchronized void addErrorToErrorMessageList(String message, Map<String, Object> errorMap) {
        MessageUtil.addErrorMessageToErrorList(errorMap, message);
    }

    /**
     * Set localized single error message retrived by messageLabel in a errorMap.
     * @param messageKey
     * @param locale
     * @param errorMap
     */
    public static synchronized void setErrorMessage(String messageKey, Locale locale, Map<String, Object> errorMap) {
        String message = getErrorMessage(messageKey, locale);
        MessageUtil.setSingleErrorMessageToErrorMap(errorMap, message);
    }

    /**
     * Set Error Message String to errorMessage in a errorMap
     * @param message string
     * @param errorMap
     */
    public static synchronized void setErrorMessage(String message, Map<String, Object> errorMap) {
        MessageUtil.setSingleErrorMessageToErrorMap(errorMap, message);
    }

    /**
     * Set error response into errorMap
     * @param errorMap
     */
    public static synchronized void setErrorResponse(Map<String, Object> errorMap) {
        errorMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
    }

    /**
     * Resolve entity field name in a human redable form against EntityLabels file.
     * EntityLabels file name is default BaseEntityLabels.
     * @param field
     * @param locale
     * @return Field name localized. If not found return normal field name.
     */
    public static synchronized String getFieldName(String fieldName, String entityName, Locale locale) {

        //Try with default resource name
        String key = entityName + "." + fieldName;
        String desc = UtilProperties.getMessage(BASE_ENTITY_LABELS, key, locale);
        if (UtilValidate.isNotEmpty(desc) && (!desc.equalsIgnoreCase(key))) {
            return desc;
        }
        //Last chance
        return fieldName;
    }

    /**
     * Resolve entity field name in a human redable form against EntityLabels file.
     * EntityLabels file name is retrived from default-resource-name field attribute.
     * If not set default-resouce-name it tries with BaseEntityLabels file.
     * @param field
     * @param locale
     * @return Field name localized. If not found return normal field name.
     */
    public static synchronized String getFieldName(ModelField field, Locale locale) {

        //Try with default resource name
        String res = field.getModelEntity().getDefaultResourceName();
        if (UtilValidate.isNotEmpty(res)) {
            String key = field.getModelEntity().getEntityName() + "." + field.getName();
            String desc = UtilProperties.getMessage(res, key, locale);
            if (UtilValidate.isNotEmpty(desc) && (!desc.equalsIgnoreCase(key))) {
                return desc;
            }
        }
        //Last chance
        return MessageUtil.getFieldName(field.getName(), field.getModelEntity().getEntityName(), locale);
    }

    public static synchronized String getMessage(String propertyName, String defaultValue, Locale locale) {
        String resourceName = null;
        if (UtilValidate.isNotEmpty(propertyName)) {
            int idx = propertyName.indexOf('.');
            if (idx < 0)
                return defaultValue;
            resourceName = propertyName.substring(0, idx);
            propertyName = propertyName.substring(idx + 1);
        }
        String msg = UtilProperties.getMessage(resourceName, propertyName, locale);
        if (UtilValidate.isEmpty(msg))
            return defaultValue;
        return msg;
    }

    /**
     * Gets exception message avoiding null
     * @param e Exception
     * @return message or Exception.toString if message is null
     */
    public static String getExceptionMessage(Throwable e) {
        if (e == null)
            return "";
        String msg = e.getMessage();
        if (msg == null) {
            msg = e.toString();
        }
        return msg;
    }

    /**
     * Loads a property map for a locale
     * @param resource
     * @param locale
     * @return the bundle
     */
    public static ResourceBundleMapWrapper loadPropertyMap(String resource, Locale locale) {
        return loadPropertyMap(resource, null, locale);
    }

    /**
     * Loads or appends a property map for a locale
     * @param resource
     * @param existingPropMap if not null, then append to that bundle
     * @param locale
     * @return the bundle
     */
    public static ResourceBundleMapWrapper loadPropertyMap(String resource, ResourceBundleMapWrapper existingPropMap, Locale locale) {
        if (existingPropMap == null) {
            existingPropMap = UtilProperties.getResourceBundleMap(resource, locale, null);
        } else {
            try {
                existingPropMap.addBottomResourceBundle(resource);
            } catch (IllegalArgumentException e) {
                // log the error, but don't let it kill everything just for a typo or bad char in an l10n file
                Debug.logError(e, "Error adding resource bundle [" + resource + "]: " + e.toString(), MODULE);
            }
        }
        return existingPropMap;
    }
}
