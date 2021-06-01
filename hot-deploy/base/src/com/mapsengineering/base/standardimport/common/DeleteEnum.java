package com.mapsengineering.base.standardimport.common;

import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

/**
 * Delete enumeration, only for weMeasureInterface management
 */
public enum DeleteEnum {
    ALL, SAME_DATA_SOURCE, SAME_DATA_SOURCE_WITHOUT_MOV;

    /**
     * Return whether delete the measure<br>
     * SAME_DATA_SOURCE return toDelete = true for measure with same dataSource <br>
     * SAME_DATA_SOURCE_WITHOUT_MOV return toDelete = true for measure with same dataSource without movement
     */
    public static boolean getToDelete(Delegator delegator, DeleteEnum deletePreviousItem, String localWorkEffortMeasureId, String localDataSourceId, List<String> weMeasureDataSourceIdList) throws GenericEntityException {
        boolean toDelete = true;
        switch (deletePreviousItem) {
        case SAME_DATA_SOURCE:
            toDelete = weMeasureDataSourceIdList.contains(localDataSourceId);
            break;
        case SAME_DATA_SOURCE_WITHOUT_MOV:
            if (weMeasureDataSourceIdList.contains(localDataSourceId)) {
                toDelete = countAcctgTransList(delegator, localWorkEffortMeasureId);
            } else {
                toDelete = false;
            }
            break;
        default:
            break;
        }
        return toDelete;
    }
    
    
    /**
     * Return false if there is almost one movement
     */
    private static boolean countAcctgTransList(Delegator delegator, String localWorkEffortMeasureId) throws GenericEntityException {
        List<GenericValue> acctgTransList = delegator.findList(E.AcctgTrans.name(), EntityCondition.makeCondition(E.voucherRef.name(), localWorkEffortMeasureId), null, null, null, false);
        if (acctgTransList.size() > 0) {
            return false;
        }
        return true;
    }

}