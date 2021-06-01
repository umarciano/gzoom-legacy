package com.mapsengineering.base.standardimport.helper;


import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for InputEnum
 *
 */
public class GlAccountInterfaceInputEnumHelper {
    public static final String ACCOUNT_TYPE_ID_INDICATOR = "INDICATOR";
    public static final String INPUT_ENUM_UO = "ACCINP_UO";
    public static final String INPUT_ENUM_OBJ = "ACCINP_OBJ";
	
    private final ImportManager manager;
    private final GenericValue glAccount;
    private final String entityName;

    private String inputEnumId;

    /**
     * 
     * @param manager
     * @param glAccount
     * @param entityName
     */
    public GlAccountInterfaceInputEnumHelper(ImportManager manager, GenericValue glAccount, String entityName) {
        this.manager = manager;
        this.glAccount = glAccount;
        this.entityName = entityName;

        this.inputEnumId = glAccount.getString(E.inputEnumId.name());
    }

    /**
     * Check periodTypeId
     * @throws GeneralException
     */
    public void importInputEnum() throws GeneralException {
    	if (! ValidationUtil.isEmptyOrNA(inputEnumId)) {
    		checkInputEnumId();
    		return;
    	}
    	setDefaultInputEnum();
    }

    /**
     * 
     * @throws GeneralException
     */
    private void checkInputEnumId() throws GeneralException {
    	GenericValue enumeration = getEnumeration();
    	checkValidEnumeration(enumeration);
    }
    
    /**
     * 
     * @return
     * @throws GeneralException
     */
    private GenericValue getEnumeration() throws GeneralException {
    	GenericValue enumeration = manager.getDelegator().findOne(E.Enumeration.name(), UtilMisc.toMap(E.enumId.name(), inputEnumId), false);
    	if (UtilValidate.isEmpty(enumeration)) {
            String msg = "inputEnumId " + inputEnumId + " for accountCode " + glAccount.getString(E.accountCode.name()) + " not found";
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
    	}
    	return enumeration;
    }
    
    /**
     * 
     * @param enumeration
     * @throws GeneralException
     */
    private void checkValidEnumeration(GenericValue enumeration) throws GeneralException {
    	if(! E.GLACCINPUT.name().equals(enumeration.getString(E.enumTypeId.name()))) {
            String msg = "inputEnumId " + inputEnumId + " for accountCode " + glAccount.getString(E.accountCode.name()) + " " + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
    	}
    }

    /**
     * 
     */
    private void setDefaultInputEnum() {
        if (ACCOUNT_TYPE_ID_INDICATOR.equals(glAccount.getString(E.accountTypeEnumId.name()))) {
            inputEnumId = INPUT_ENUM_UO;
            return;
        }
        inputEnumId = INPUT_ENUM_OBJ;
    }

    /**
     * Return periodTypeId
     * @return
     */
    public String getInputEnumId() {
        return inputEnumId;
    }

}
