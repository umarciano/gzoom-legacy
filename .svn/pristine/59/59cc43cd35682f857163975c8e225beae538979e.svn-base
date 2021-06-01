package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

/**
 * Helper for weMeasureTypeEnumId
 *
 */
public class GlAccountInterfaceMeasureTypeHelper {
    public static final String ACCOUNT_TYPE_ID_FINANCIAL = "FINANCIAL";
    public static final String ACCOUNT_TYPE_ID_ACCOUNT = "ACCOUNT";
    public static final String ACCOUNT_TYPE_ID_INDICATOR = "INDICATOR";
    public static final String WEM_TYPE_ID_FINANCIAL = "WEMT_FINANCIAL";
    public static final String WEM_TYPE_ID_ACCOUNT = "WEMT_ECONOMIC";
    public static final String WEM_TYPE_ID_INDICATOR = "WEMT_PERF";

    public static final String ENUM_TYPE_ID_MEASURE = "WE_MEASURE_TYPE";

    private final ImportManager manager;
    private final GenericValue externalValue;
    private final String entityName;

    private String accountTypeEnumId;
    private String weMeasureTypeEnumId;

    /**
     * Constructor
     * @param manager
     * @param externalValue
     * @param entityName
     */
    public GlAccountInterfaceMeasureTypeHelper(ImportManager manager, GenericValue externalValue, String entityName) {
        this.manager = manager;
        this.externalValue = externalValue;
        this.entityName = entityName;

        this.accountTypeEnumId = externalValue.getString(E.accountTypeEnumId.name());
        this.weMeasureTypeEnumId = externalValue.getString(E.weMeasureTypeEnumId.name());
    }

    /**
     * set weMeasureTypeEnumId
     * @throws GeneralException
     */
    public void importMeasureType() throws GeneralException {
        checkWeMeasureTypeEnumId();
        if (UtilValidate.isEmpty(weMeasureTypeEnumId)) {
            Map<String, String> weMeasureTypeEnumIdMap = weMeasureTypeEnumIdMap();
            this.weMeasureTypeEnumId = weMeasureTypeEnumIdMap.get(accountTypeEnumId);
        }
    }

    private void checkWeMeasureTypeEnumId() throws GeneralException {
        if (UtilValidate.isNotEmpty(weMeasureTypeEnumId)) {
            EntityCondition enumConditions = getEnumConditions();
            Set<String> fieldsToSelect = UtilMisc.toSet(E.enumId.name(), E.enumTypeId.name());
            List<GenericValue> enumerations = manager.getDelegator().findList(E.Enumeration.name(), enumConditions, fieldsToSelect, null, null, false);

            checkEnumerations(enumerations);
        }
    }

    private EntityCondition getEnumConditions() {
        List<EntityCondition> enumConditions = FastList.newInstance();
        enumConditions.add(EntityCondition.makeCondition(E.enumTypeId.name(), ENUM_TYPE_ID_MEASURE));
        enumConditions.add(EntityCondition.makeCondition(E.enumId.name(), weMeasureTypeEnumId));
        return EntityCondition.makeCondition(enumConditions);
    }

    private void checkEnumerations(List<GenericValue> enumerations) throws GeneralException {
        if (UtilValidate.isEmpty(enumerations)) {
            String msg = "weMeasureTypeEnumId " + weMeasureTypeEnumId + " for accountCode " + externalValue.getString(E.accountCode.name()) + " " + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private Map<String, String> weMeasureTypeEnumIdMap() {
        Map<String, String> weMeasureTypeEnumIdMap = FastMap.newInstance();
        weMeasureTypeEnumIdMap.put(ACCOUNT_TYPE_ID_FINANCIAL, WEM_TYPE_ID_FINANCIAL);
        weMeasureTypeEnumIdMap.put(ACCOUNT_TYPE_ID_ACCOUNT, WEM_TYPE_ID_ACCOUNT);
        weMeasureTypeEnumIdMap.put(ACCOUNT_TYPE_ID_INDICATOR, WEM_TYPE_ID_INDICATOR);

        return weMeasureTypeEnumIdMap;
    }

    /**
     * Return weMeasureTypeEnumId
     * @return
     */
    public String getWeMeasureTypeEnumId() {
        return weMeasureTypeEnumId;
    }

}
