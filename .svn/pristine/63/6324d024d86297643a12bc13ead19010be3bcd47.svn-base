package com.mapsengineering.base.services;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.service.DispatchContext;

import com.mapsengineering.base.bl.validation.validator.AbstractEngine;
import com.mapsengineering.base.bl.validation.validator.ApacheEngine;

public class ClientValidationServices {
	
	private ClientValidationServices() {}
	
	@SuppressWarnings("unchecked")
	public static Map doFieldValidation(DispatchContext dctx, Map param) {

		//Form name is the form name we'are looking for to do field validation 
		String formName = (String) param.get("formName");
		
		//Validator is file name where find form definition, if null or vlak use default values
		String validator = (param.containsKey("validator"))?(String)param.get("validator"):"";
		
		//Field name is field name definition into form validation defintion using for validation  
		String fieldName = (String) param.get("fieldName");
		String fieldValue = (String) param.get("fieldValue");
		
		Locale locale = (Locale) param.get("locale");
		
		//Now compose field for validation routine
		param.put(fieldName, fieldValue);
		
		Map<String, Object> resultMap = FastMap.newInstance();
		
		//Standard field validation
		AbstractEngine engine = new ApacheEngine();
		engine.validateAndFormat(formName, validator, param, locale, resultMap);
		
		return resultMap;
	}
}
