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
 * Helper for PeriodType
 *
 */
public class GlAccountInterfacePeriodHelper {
    private final ImportManager manager;
    private final GenericValue externalValue;
    private final String entityName;

    private String periodTypeDesc;
    private String periodTypeId;

    /**
     * 
     * @param manager
     * @param externalValue
     * @param entityName
     */
    public GlAccountInterfacePeriodHelper(ImportManager manager, GenericValue externalValue, String entityName) {
        this.manager = manager;
        this.externalValue = externalValue;
        this.entityName = entityName;

        this.periodTypeDesc = externalValue.getString(E.periodTypeDesc.name());
    }

    /**
     * Check periodTypeId
     * @throws GeneralException
     */
    public void importPeriodType() throws GeneralException {
        checkPeriodTypeDesc();

        Set<String> fieldsToSelect = UtilMisc.toSet(E.periodTypeId.name(), E.description.name());
        List<GenericValue> periods = manager.getDelegator().findList(E.PeriodType.name(), EntityCondition.makeCondition(E.description.name(), periodTypeDesc), fieldsToSelect, null, null, false);
        GenericValue period = EntityUtil.getFirst(periods);

        checkPeriod(period);

        this.periodTypeId = period.getString(E.periodTypeId.name());
    }

    private void checkPeriodTypeDesc() throws GeneralException {
        if (UtilValidate.isEmpty(periodTypeDesc)) {
            String msg = "periodTypeDesc in input for accountCode " + externalValue.getString(E.accountCode.name()) + " not found";
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * 
     * @param period
     * @throws GeneralException
     */
    private void checkPeriod(GenericValue period) throws GeneralException {
        if (UtilValidate.isEmpty(period)) {
            String msg = "periodTypeDesc " + periodTypeDesc + " for accountCode " + externalValue.getString(E.accountCode.name()) + " " + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * Return periodTypeId
     * @return
     */
    public String getPeriodTypeId() {
        return periodTypeId;
    }

}
