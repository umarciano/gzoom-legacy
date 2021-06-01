package com.mapsengineering.accountingext.util;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.accountingext.services.E;

/**
 * WorkEffort Utility for workEffortType
 * @author dain
 *
 */
public class WorkEffortUtil {

    private Delegator delegator;

    /**
     * Constructor
     */
    public WorkEffortUtil(Delegator delegator) {
        this.delegator = delegator;
    }

    private boolean getIsRoot(GenericValue workEffort) throws GenericEntityException {
        boolean isRoot = false;
        
        GenericValue workEffortType = delegator.findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), workEffort.getString(E.workEffortTypeId.name())), false);
        if (UtilValidate.isNotEmpty(workEffortType)) {
            isRoot = E.Y.name().equals(workEffortType.getString(E.isRoot.name()));
        }

        return isRoot;
    }

    /**
     * Return if is root
     * @param workEffortId
     * @return
     * @throws GenericEntityException
     */
    public boolean isRoot(String workEffortId) throws GenericEntityException {
        boolean isRoot = false;
        
        GenericValue workEffort = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
        if (UtilValidate.isNotEmpty(workEffort)) {
            isRoot = getIsRoot(workEffort);
        }

        return isRoot;
    }
}
