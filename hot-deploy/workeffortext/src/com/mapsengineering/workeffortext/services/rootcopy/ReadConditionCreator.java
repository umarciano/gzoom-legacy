package com.mapsengineering.workeffortext.services.rootcopy;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.workeffortext.services.E;
import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * ReadCondition
 *
 */
public class ReadConditionCreator {

    /**
     * Return ReadCondition
     * @param ctx
     * @return
     */
    public EntityCondition buildReadCondition(Map<String, Object> ctx) {
        String workEffortTypeIdFrom = (String)ctx.get("workEffortTypeIdFrom");
        String workEffortId = (String)ctx.get(E.workEffortId.name());
        Timestamp estimatedStartDateFrom = (Timestamp)ctx.get(E.estimatedStartDateFrom.name());
        Timestamp estimatedCompletionDateFrom = (Timestamp)ctx.get(E.estimatedCompletionDateFrom.name());

        List<EntityCondition> readCondition = FastList.newInstance();
        readCondition.add(EntityCondition.makeCondition(E.organizationId.name(), (String)ctx.get(GenericService.ORGANIZATION_PARTY_ID)));
        readCondition.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeIdFrom));
        readCondition.add(EntityCondition.makeCondition(E.weIsRoot.name(), E.Y.name()));
        readCondition.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDateFrom));
        readCondition.add(EntityCondition.makeCondition(E.estimatedStartDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, estimatedCompletionDateFrom));
        readCondition.add(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), null));
        if (UtilValidate.isNotEmpty(workEffortId)) {
            readCondition.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
        }
        Debug.log("readCondition" + readCondition);
        return EntityCondition.makeCondition(readCondition);
    }
}
