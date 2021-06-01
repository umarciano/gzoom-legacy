package com.mapsengineering.workeffortext.services.rootcopy;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.workeffortext.services.E;

/**
 * Check Existing assoc COPY
 *
 */
public class WorkEffortRootChecker {

    /**
     * Search and Check if there is a WorkEffortAssoc with workEffortAssocTypeId = COPY
     * @param delegator
     * @param workEffortId
     * @param estimatedStartDateTo
     * @param useCache
     * @return if exist
     * @throws GeneralException
     */
    public static boolean checkExisting(Delegator delegator, String workEffortId, Timestamp estimatedStartDateTo, boolean useCache) throws GeneralException {
        boolean exists = false;

        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), workEffortId));
        cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), E.COPY.name()));
        cond.add(EntityCondition.makeCondition(E.fromDate.name(), estimatedStartDateTo));
        // TODO aggiungere condizione per le date
        
        List<GenericValue> assocCopy = delegator.findList(E.WorkEffortAssoc.name(), EntityCondition.makeCondition(cond), null, null, null, useCache);
        exists = UtilValidate.isNotEmpty(assocCopy);

        return exists;
    }
}
