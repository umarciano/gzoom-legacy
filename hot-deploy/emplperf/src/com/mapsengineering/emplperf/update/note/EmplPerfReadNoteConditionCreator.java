package com.mapsengineering.emplperf.update.note;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.find.EmplPerfFindServices;
import com.mapsengineering.emplperf.update.EmplPerfReadConditionCreator;
import com.mapsengineering.emplperf.update.EmplPerfServiceEnum;
import com.mapsengineering.emplperf.update.ParamsEnum;

/**
 * Condition for TODO
 */
public class EmplPerfReadNoteConditionCreator extends EmplPerfReadConditionCreator {

    private static final int MIN_SEQUENCE_ID = 100;
    private static final int MAX_SEQUENCE_ID = 199;

    /**
     * Return condition
     */
    public EntityCondition buildReadCondition(Map<String, Object> ctx) {
        EmplPerfFindServices emplPerfFindServices = new EmplPerfFindServices();
        List<EntityCondition> condition = emplPerfFindServices.getConditionForRoot(ctx);
        Timestamp estimatedStartDate = (Timestamp)ctx.get(ParamsEnum.estimatedStartDate.name());       
        Timestamp estimatedCompletionDate = (Timestamp)ctx.get(ParamsEnum.estimatedCompletionDate.name());
        Timestamp readDate = (Timestamp)ctx.get(ParamsEnum.readDate.name());
               
        buildEstimatedStartDateCondition(condition, estimatedCompletionDate);
        buildEstimatedCompletionDateCondition(condition, estimatedStartDate, readDate);
       
        if (UtilValidate.isNotEmpty(readDate)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldNoteDateTime.name(), EntityOperator.EQUALS, readDate));
        }
        Timestamp writeDate = (Timestamp)ctx.get(ParamsEnum.writeDate.name());
        if (UtilValidate.isNotEmpty(writeDate)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.noteDateTime.name(), EntityOperator.EQUALS, writeDate));
        }

        condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldWorkEffortRevisionId.name(), null));

        condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.sequenceId.name(), EntityOperator.LESS_THAN_EQUAL_TO, MAX_SEQUENCE_ID));
        condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.sequenceId.name(), EntityOperator.GREATER_THAN_EQUAL_TO, MIN_SEQUENCE_ID));

        return EntityCondition.makeCondition(condition);
    }
}
