package com.mapsengineering.base.bl.validation;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.model.ModelFieldType;

public abstract class FieldResolver {

	/**
	 * Override this for implements specifics functionality
	 */
	protected abstract Object doResolve(String fieldName, Object inputField, ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale);
	
	/**
	 * Public synchronized access.
	 * @param fieldName 
	 * @param inputField 
	 * @param expectedField
	 * @param errorMap
	 * @param locale
	 * @return Resolved field object
	 */
	public synchronized Object resolve(String fieldName, Object inputField, ModelFieldType expectedField, Map<String, Object> errorMap, Locale locale) {
		return doResolve(fieldName, inputField, expectedField, errorMap, locale); 
	}
	
}
