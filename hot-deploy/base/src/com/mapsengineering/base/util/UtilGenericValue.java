package com.mapsengineering.base.util;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

public class UtilGenericValue {
	public static boolean containsGenericValue(List<GenericValue> list, GenericValue item) {
		if (UtilValidate.isEmpty(list) || UtilValidate.isEmpty(item)) {
			return false;
		}
		List<String> pkFields = item.getModelEntity().getPkFieldNames();
		if (UtilValidate.isNotEmpty(pkFields)) {
			for (GenericValue gv : list) {
				boolean areEqual = true;
				for (String pkField : pkFields) {
					Object field1 = gv.get(pkField);
					Object field2 = item.get(pkField);
					if (!areEqual(field1, field2)) {
						areEqual = false;
					}
				}
				if (areEqual) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean areEqual(Object field1, Object field2) {
		if (UtilValidate.isNotEmpty(field1) && UtilValidate.isEmpty(field2)) {
			return false;
		}
		if (UtilValidate.isEmpty(field1) && UtilValidate.isNotEmpty(field2)) {
			return false;
		}
		if (UtilValidate.isNotEmpty(field1) && UtilValidate.isNotEmpty(field2)) {
			return field1.equals(field2);
		}
		return true;
	}
}
