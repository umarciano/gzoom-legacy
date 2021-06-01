package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

/**
 * Helper for Uom
 *
 */
public class GlAccountInterfaceUomHelper {
    public static final String ACCOUNT_TYPE_ID_INDICATOR = "INDICATOR";

    private final ImportManager manager;
    private final GenericValue externalValue;
    private final String entityName;

    private String accountTypeEnumId;
    private String defaultUomCode;
    private String defaultUomId;

    /**
     * 
     * @param manager
     * @param externalValue
     * @param entityName
     */
    public GlAccountInterfaceUomHelper(ImportManager manager, GenericValue externalValue, String entityName) {
        this.manager = manager;
        this.externalValue = externalValue;
        this.entityName = entityName;

        this.accountTypeEnumId = externalValue.getString(E.accountTypeEnumId.name());
        this.defaultUomCode = externalValue.getString(E.defaultUomCode.name());
    }

    /**
     * Set defaultUomId
     * @throws GeneralException
     */
    public void importUom() throws GeneralException {
        checkDefaultUomCode();
        this.defaultUomId = "";

        if (UtilValidate.isNotEmpty(defaultUomCode)) {
            Set<String> fieldsToSelect = UtilMisc.toSet(E.uomId.name(), E.abbreviation.name());
            List<GenericValue> uoms = manager.getDelegator().findList(E.Uom.name(), EntityCondition.makeCondition(E.abbreviation.name(), defaultUomCode), fieldsToSelect, null, null, false);
            GenericValue uom = checkUom(uoms);
            this.defaultUomId = uom.getString(E.uomId.name());
        }
    }

    private void checkDefaultUomCode() throws GeneralException {
        if (ACCOUNT_TYPE_ID_INDICATOR.equals(this.accountTypeEnumId)) {
            if (UtilValidate.isEmpty(defaultUomCode)) {
                String msg = "defaultUomCode in input for accountCode " + externalValue.getString(E.accountCode.name()) + " and accountTypeEnumId " + ACCOUNT_TYPE_ID_INDICATOR + " not found";
                throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
    }

    /**
     * 
     * @param uoms
     * @return
     * @throws GeneralException
     */
    private GenericValue checkUom(List<GenericValue> uoms) throws GeneralException {
        if (UtilValidate.isEmpty(uoms)) {
            String msg = "defaultUomCode " + defaultUomCode + " for accountCode " + externalValue.getString(E.accountCode.name()) + " " + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (uoms.size() > 1) {
            String msg = "More than one Uom found for accountCode  " + externalValue.getString(E.accountCode.name()) + " and defaultUomCode " + defaultUomCode;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        return EntityUtil.getFirst(uoms);
    }

    /**
     * 
     * @param defUomId
     * @return
     */
    public String getUomId(String defUomId) {
        if (UtilValidate.isEmpty(defaultUomId)) {
            return defUomId;
        }
        return defaultUomId;
    }
}
