package com.mapsengineering.base.standardimport.glaccount;

import java.util.List;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.GlAccountInterfaceTakeOverService;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.util.TakeOverUtil;

/**
 * Utility for GlAccountType
 *
 */
public class GlAccountTypeHelper {
    private final ImportManager manager;
    private final GenericValue glAccount;
    private final String entityName;
    private String accountTypeEnumId;

    /**
     * Constructor
     * @param manager
     * @param glAccount
     * @param entityName
     */
    public GlAccountTypeHelper(ImportManager manager, GenericValue glAccount,  String entityName) {
        this.manager = manager;
        this.glAccount = glAccount;
        this.entityName = entityName;
    }

    /**
     * Return glResourceTypeId
     * @param glAccountTypeId
     * @return
     * @throws GeneralException
     */
    public String getGlResourceType(String glAccountTypeId) throws GeneralException {
        //1.10. Search glResourceTypeId  
        String glResourceTypeId = glAccount.getString(E.glResourceTypeId.name());
        if (UtilValidate.isNotEmpty(glResourceTypeId)) {
            TakeOverUtil.checkValidEntity(E.glResourceTypeId.name(), glResourceTypeId, E.GlResourceType.name(), entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), manager);

            checkValidGlAccountResource(glResourceTypeId, glAccountTypeId);
        }

        return glResourceTypeId;
    }
    
    private void checkValidGlAccountResource(String glResourceTypeId, String glAccountTypeId) throws GeneralException {
        String msg = "";

        List<GenericValue> glResourceTypes = manager.getDelegator().findByAnd(E.GlAccountResource.name(), UtilMisc.toMap(E.glResourceTypeId.name(), glResourceTypeId, E.glAccountTypeId.name(), glAccountTypeId));
        GenericValue glResourceType = EntityUtil.getFirst(glResourceTypes);
        if (UtilValidate.isEmpty(glResourceType)) {
            msg = "The glResourceTypeId " + glResourceTypeId + " and glAccountTypeId " + glAccountTypeId + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * Return glAccount.accountTypeId
     * @return
     * @throws GeneralException
     */
    public String getGlAccountType() throws GeneralException {
        String msg = "";

        //1.9. Search accountTypeId  
        String accountTypeId = glAccount.getString(E.accountTypeId.name());
        if (UtilValidate.isEmpty(accountTypeId)) {
            msg = "The accountTypeId " + ImportManagerConstants.STR_IS_NULL;
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        TakeOverUtil.checkValidEntity(E.glAccountTypeId.name(), accountTypeId, E.GlAccountType.name(), entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), manager);

        List<GenericValue> glAccountTypes = manager.getDelegator().findByAnd(E.GlAccountType.name(), UtilMisc.toMap(E.glAccountTypeId.name(), accountTypeId, E.accountTypeEnumId.name(), accountTypeEnumId));
        GenericValue glAccountType = EntityUtil.getFirst(glAccountTypes);
        if (UtilValidate.isEmpty(glAccountType)) {
            msg = "The accountTypeId " + accountTypeId + " and accountTypeEnumId " + accountTypeEnumId + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        return accountTypeId;
    }

    /**
     * Return glAccount.accountTypeEnumId
     * @return
     * @throws GeneralException
     */
    public String getAccountTypeEnum() throws GeneralException {
        String msg = "";

        //1.8. Search accountTypeEnumId  
        accountTypeEnumId = glAccount.getString(E.accountTypeEnumId.name());
        if (UtilValidate.isEmpty(accountTypeEnumId)) {
            msg = "The accountTypeEnumId " + ImportManagerConstants.STR_IS_NULL;
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        TakeOverUtil.checkValidEntity(E.enumId.name(), accountTypeEnumId, E.Enumeration.name(), entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), manager);

        List<GenericValue> accountTypeEnums = manager.getDelegator().findByAnd(E.Enumeration.name(), UtilMisc.toMap(E.enumId.name(), accountTypeEnumId, E.enumTypeId.name(), GlAccountInterfaceTakeOverService.ENUM_TYPE_ACC_TYPE));
        GenericValue accountTypeEnum = EntityUtil.getFirst(accountTypeEnums);
        if (UtilValidate.isEmpty(accountTypeEnum)) {
            msg = "The accountTypeEnumId " + accountTypeEnumId + " and enumTypeId " + GlAccountInterfaceTakeOverService.ENUM_TYPE_ACC_TYPE + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, glAccount.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        return accountTypeEnumId;
    }

}
