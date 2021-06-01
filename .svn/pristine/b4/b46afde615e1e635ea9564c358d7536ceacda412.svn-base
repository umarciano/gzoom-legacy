package com.mapsengineering.base.bl.validation.validator;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.MessageString;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMapProcessor;

import com.mapsengineering.base.util.MessageUtil;

public class SimpleMapProcessorEngine extends AbstractEngine {
    /**
     * Default validator name (prefix + entity name)
     */
    private static final String SMP_VALIDATOR_PREFIX = "validator_";
    /**
     * Default validator path
     */
    private static final String SMP_VALIDATOR_RESOURCE = "com/mapsengineering/base/DefaultValidator.xml";

    /**
     * Validation interface
     * @param errorMap, filled with error messages
     * @return true if validation ok, if false see errorMap.
     */
    protected boolean doValidate() {

        //Default validator
        //prefix name + _ + entity name
        if (UtilValidate.isEmpty(validators)) {
            addValidatorResource(SMP_VALIDATOR_PREFIX + entityName.toLowerCase());
        }

        try {
            List<Object> messages = FastList.newInstance();
            Map<String, Object> errors = FastMap.newInstance();

            SimpleMapProcessor.runSimpleMapProcessor(SMP_VALIDATOR_RESOURCE, validators.get(0), parameters, errors, messages, locale);
            if (messages.size() > 0) {
                for (Object message: messages) {
                    //String message
                    if (message instanceof String) {
                        MessageUtil.addErrorToErrorMessageList((String)message, errorMap);
                    }
                    //MessageString message
                    if (message instanceof MessageString) {
                        MessageString ms = (MessageString)message;
                        //Replace values setted to fieldName and  toFieldName
                        String em = MessageUtil.getErrorMessage(ms.getPropertyName(), locale,
                                UtilMisc.toMap("fieldName", ms.getFieldName(), "fieldValue", ms.getToFieldName()));
                        MessageUtil.addErrorToErrorMessageList(em, errorMap);
                    }
                }
                MessageUtil.setErrorMessage("ValidationError", locale, errorMap);
                MessageUtil.setErrorResponse(errorMap);
                return false;
            }

            return true;

        } catch (MiniLangException e) {
            Debug.logError("Errore di validazione " + e.getMessage(), SimpleMapProcessor.class.getName());
            errorMap.putAll(MessageUtil.buildErrorMap("ValidationError", e, locale));
            return false;
        }
    }

    /**
     * Validation interface
     * @param errorMap, filled with error messages
     * @return String with valid formatted value or null if not valid
     */
    protected boolean doValidateAndFormat() {
        // TODO To implements
        return true;
    }

}
