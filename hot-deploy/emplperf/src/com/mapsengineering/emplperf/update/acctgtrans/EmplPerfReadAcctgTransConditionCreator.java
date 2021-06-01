package com.mapsengineering.emplperf.update.acctgtrans;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.find.EmplPerfFindServices;
import com.mapsengineering.emplperf.update.EmplPerfReadConditionCreator;
import com.mapsengineering.emplperf.update.EmplPerfServiceEnum;
import com.mapsengineering.emplperf.update.ParamsEnum;

/**
 * Condition for TODO
 */
public class EmplPerfReadAcctgTransConditionCreator extends EmplPerfReadConditionCreator {

    /**
     * Return condition
     */
    public EntityCondition buildReadCondition(Map<String, Object> ctx) {
        EmplPerfFindServices emplPerfFindServices = new EmplPerfFindServices();
        List<EntityCondition> condition = emplPerfFindServices.getConditionForRoot(ctx);
        
        Timestamp estimatedStartDate = (Timestamp)ctx.get(ParamsEnum.estimatedStartDate.name());
        Timestamp readDate = (Timestamp)ctx.get(ParamsEnum.readDate.name());
        Timestamp estimatedCompletionDate = (Timestamp)ctx.get(ParamsEnum.estimatedCompletionDate.name());
        
        buildEstimatedCompletionDateCondition(condition, estimatedStartDate, readDate);
        buildEstimatedStartDateCondition(condition, estimatedCompletionDate);                       

        if (UtilValidate.isNotEmpty(readDate)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.transactionDate.name(), readDate));
        }
        if (UtilValidate.isNotEmpty(estimatedCompletionDate)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.thruDate.name(), estimatedCompletionDate));
        }
        
        String workEffortPurposeTypeId = (String)ctx.get(ParamsEnum.updateWorkEffortPurposeType.name());
        if (UtilValidate.isNotEmpty(workEffortPurposeTypeId)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.workEffortPurposeTypeId.name(), workEffortPurposeTypeId));
        }

        condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.entryWorkEffortRevisionId.name(), null));
        
        return EntityCondition.makeCondition(condition);
    }
}
