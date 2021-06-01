package com.mapsengineering.base.standardimport.helper;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

public class ValidationHelper {
    private final GenericValue externalValue;
    private final String entityName;
      
	/**
	 * 
	 * @param externalValue
	 * @param entityName
	 */
	public ValidationHelper(GenericValue externalValue, String entityName) {
		this.externalValue = externalValue;
		this.entityName = entityName;
	}

    /**
     * checks if fieldValue is Y or N
     * @param fieldName
     * @param fieldValue
     * @throws GeneralException
     */
	public void checkValidIndicatorTypeField(String fieldName, String fieldValue) throws GeneralException {
		String msg = "";	
		if(! E.Y.name().equals(fieldValue) && ! E.N.name().equals(fieldValue)) {
	        msg = "The " + fieldName + " " + fieldValue + ImportManagerConstants.STR_IS_NOT_VALID + ", it must be Y or N";        
	        throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
		}
	}

}
