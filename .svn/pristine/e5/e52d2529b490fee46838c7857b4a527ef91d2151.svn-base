package com.mapsengineering.base.bl.crud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;

import com.mapsengineering.base.bl.validation.validator.AbstractEngine;
import com.mapsengineering.base.bl.validation.validator.ApacheEngine;

public class ValueValidationHandler extends AbstractCrudHandler {

    protected boolean doExecution() {

        //Eseguo controllo solo in Update/Create
        if (Operation.READ.equals(operation)||Operation.DELETE.equals(operation)) {
            return true;
        }
        
        ModelEntity modelEntity = delegator.getModelEntity(entityName);

        String formValidator = null;
        String validator = "";
        
        if (UtilValidate.isNotEmpty(parameters.get("formValidator"))) {
        	formValidator = UtilGenerics.cast(parameters.get("formValidator"));
        }
        
        if (UtilValidate.isNotEmpty(parameters.get("validator"))) {
        	validator = UtilGenerics.cast(parameters.get("validator"));
        }
        
        if (UtilValidate.isEmpty(validator) && UtilValidate.isNotEmpty(context)) {
            validator = (String)context.get("validator");
        }
        
        Map<String, Object> bean = new HashMap<String, Object>();
        
        
        if (Operation.UPDATE.equals(operation) && UtilValidate.isNotEmpty(modelEntity)) {
            GenericPK pk = delegator.makePK(entityName);
            pk.setPKFields(parameters);
        	
            try {
				GenericValue value = delegator.findOne(entityName, false, pk);
				
				for (String fieldName : modelEntity.getNoPkFieldNames()) {
					if (parameters.containsKey(fieldName)) {
						bean.put(fieldName, parameters.get(fieldName));
					} else {
						bean.put(fieldName, value.get(fieldName));
					}
				}
			} catch (GenericEntityException e) {
			}
        }

        String[] validators = null;
        //Standard field validation
        AbstractEngine engine = new ApacheEngine();
        
        if (UtilValidate.isNotEmpty(formValidator) && formValidator.indexOf("#") != -1) {
        	List<String> splittedString = StringUtil.split(formValidator, "#");
        	if (UtilValidate.isNotEmpty(splittedString)) {
        		validators = new String[1];
        		validators[0] = splittedString.get(0);
        		formValidator = splittedString.get(1);
        	}
        } else {
        	formValidator = entityName;
        }
        
        if (UtilValidate.isNotEmpty(validators))
        	return engine.validate(formValidator, validators, bean, locale, returnMap);
        else
        	return engine.validate(formValidator, validator, bean, locale, returnMap);
    }

}
