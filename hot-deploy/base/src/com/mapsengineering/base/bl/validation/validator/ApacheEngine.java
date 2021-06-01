package com.mapsengineering.base.bl.validation.validator;

import java.util.Iterator;

import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.base.util.MessageUtil;

/**
 * Base class for validator engines. This class perform validation with Apache Validators
 * @author sandro
 *
 */
public class ApacheEngine extends AbstractEngine {
    /**
     * Default common validator routines
     */
    public static final String DEFAULT_COMMON_VALIDATOR = "com/mapsengineering/base/common-validator-routines.xml";
    /**
     * Default base validator path
     */
    public static final String DEFAULT_BASE_VALIDATOR = "com/mapsengineering/base/base-validator.xml";
    
    public ApacheEngine() {
    	super();
    }
    
    
    /**
     * Override thie method for specifics validation
     * @return
     */
    @SuppressWarnings("unchecked")
    protected boolean doValidate() {
        //Default validator
    	addValidatorResource(DEFAULT_COMMON_VALIDATOR);
        addValidatorResource(DEFAULT_BASE_VALIDATOR);

        //Exec validation
        try {
            ApacheValidatorFactory.CacheStoreValidator csv = ApacheValidatorFactory.getInstance(validators, entityName);

            Validator engine = csv.validator;
            ValidatorResources resources = csv.resources;
            
            engine.setParameter(Validator.BEAN_PARAM, parameters);
            engine.setParameter(Validator.LOCALE_PARAM, locale);
            engine.setOnlyReturnErrors(true);

            ValidatorResults res = engine.validate();
            if (res.isEmpty()) {
                return true;
            }

            // Iterate over each of the properties of the Bean which has messages.
            Iterator propertyNames = res.getPropertyNames().iterator();
            while (propertyNames.hasNext()) {

                String propertyName = (String) propertyNames.next();
                ValidatorResult result = res.getValidatorResult(propertyName);

                // Get all the actions run against the property, and iterate over their names
                Iterator actionIt = result.getActions();
                while (actionIt.hasNext()) {

                    String actName = (String) actionIt.next();

                    //Check wether action ran correctly
                    if (!result.isValid(actName)) {

                        //Gets message key
                        ValidatorAction action = resources.getValidatorAction(actName);
                        String messageKey = action.getMsg();

                        //Gets relative error message
                        if (UtilValidate.isNotEmpty(messageKey)) {
                            //Try to locate property value for error message
                            Object fieldValue = null;
                            if (parameters.containsKey(result.getField().getProperty())) {
                                fieldValue = parameters.get(result.getField().getProperty());
                            }
                            String fieldName = MessageUtil.getFieldName(propertyName, entityName, locale);
                            MessageUtil.addErrorToErrorMessageList(messageKey, locale,
                                    UtilMisc.toMap("fieldName", fieldName, "fieldValue", fieldValue) , errorMap);
                        }
                        MessageUtil.setErrorMessage("ValidationError", locale, errorMap);
                        MessageUtil.setErrorResponse(errorMap);
                    }
                }
            }

        } catch (Exception e) {
            errorMap.putAll(MessageUtil.buildErrorMap("ValidationError", e, locale));
        }

        return false;
    }

    protected boolean doValidateAndFormat() {

        //Default validator
        if (UtilValidate.isEmpty(validators)) {
            addValidatorResource(DEFAULT_COMMON_VALIDATOR);
            addValidatorResource(DEFAULT_BASE_VALIDATOR);
        }

        //Exec validation
        try {
            //entityName = form name
            ApacheValidatorFactory.CacheStoreValidator csv = ApacheValidatorFactory.getInstance(validators, entityName);

            Validator engine = csv.validator;

            //Field name is the validation routine field name used to validate value
            String fieldName = (String)this.parameters.get("fieldName");

            engine.setParameter(Validator.BEAN_PARAM, parameters);
            engine.setParameter(Validator.LOCALE_PARAM, locale);
            engine.setOnlyReturnErrors(false); //Important: without this valoidators doesn't return values

            ValidatorResults res = engine.validate();

            //Validation result (formatted value if ok, or error message otherwise)
            //are put into Validator result.
            ValidatorResult vr = res.getValidatorResult(fieldName);
            if (vr.isValid(fieldName)) {
                //if valid return formatted value into key 'successMessage'
                errorMap.putAll(MessageUtil.buildSuccessMap((String)vr.getResult(fieldName)));

            } else {
                //Build error Map with error get from validator
                String message = (String)vr.getResult(fieldName);
                throw new GenericServiceException(message);
            }

        } catch (Exception e) {
            errorMap.putAll(MessageUtil.buildErrorMap("ValidationError", e, locale));
            return false;
        }

        return true;
    }
}
