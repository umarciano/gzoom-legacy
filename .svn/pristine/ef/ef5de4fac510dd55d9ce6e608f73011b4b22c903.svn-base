package com.mapsengineering.base.bl.validation.validator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;

public abstract class AbstractEngine {

	protected String entityName;
	protected List<String> validators;
	protected Map<String,Object> parameters;
	protected Locale locale;
	protected Map<String, Object> errorMap;
	
	public AbstractEngine() {
	}
	
	/**
	 * Initialization method
	 * @param entityName
	 * @param validator
	 * @param parameters
	 * @param locale
	 * @param errorMap
	 */
	private final void init(String entityName, Map<String,Object> parameters, Locale locale, Map<String,Object> errorMap) {
		this.entityName = entityName;
		this.parameters = parameters;
		this.locale = locale;
		this.errorMap = errorMap;
		this.validators = FastList.newInstance();
	}
	
	/**
	 * Adds a validator resources to validators list
	 * @param validatorResource
	 */
	protected final void addValidatorResource(String validatorResource) {
		if (UtilValidate.isNotEmpty(validatorResource)) {
			this.validators.add(validatorResource);
		}
	}

	/**
	 * Override thie method for specifics validation
	 * @return
	 */
	protected abstract boolean doValidate();

	/**
	 * Override this method for specifics validation and formattings
	 * @return Formatted value if validation ok, null otherwise
	 */
	protected abstract boolean doValidateAndFormat();

	/**
	 * Exec validation method
	 * @param errorMap, filled with error messages
	 * @return true if validation ok, if false see errorMap.
	 */
	public final boolean validate(String entityName, String validator, Map<String,Object> parameters, Locale locale, Map<String,Object> errorMap) {
		init(entityName, parameters, locale, errorMap);
		addValidatorResource(validator);
		return doValidate();
	}
	
	/**
	 * Exec validation method
	 * @param errorMap, filled with error messages
	 * @return true if validation ok, if false see errorMap.
	 */
	public final boolean validate(String entityName, String[] validators, Map<String,Object> parameters, Locale locale, Map<String,Object> errorMap) {
		init(entityName, parameters, locale, errorMap);
		for (int i=0;i<validators.length; i++) {
			addValidatorResource(validators[i]);
		}
		return doValidate();
	}
	
	/**
	 * Exec validation and formatting methods
	 * @param resultMap, filled with error messages
	 * @return String filled with formatted value or null if value not valid
	 */
	public final boolean validateAndFormat(String entityName, String validator, Map<String,Object> parameters, Locale locale, Map<String,Object> resultMap) {
		init(entityName, parameters, locale, resultMap);
		addValidatorResource(validator);
		return doValidateAndFormat();
	}

	/**
	 * Exec validation method
	 * @param resultMap, filled with error messages
	 * @return String filled with formatted value or null if value not valid
	 */
	public final boolean validateAndFormat(String entityName, String[] validators, Map<String,Object> parameters, Locale locale, Map<String,Object> resultMap) {
		init(entityName, parameters, locale, resultMap);
		for (int i=0;i<validators.length; i++) {
			addValidatorResource(validators[i]);
		}
		return doValidateAndFormat();
	}
	
}
