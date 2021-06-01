package com.mapsengineering.emplperf.update.assoc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.find.EmplPerfFindServices;
import com.mapsengineering.emplperf.update.EmplPerfReadConditionCreator;
import com.mapsengineering.emplperf.update.EmplPerfServiceEnum;
import com.mapsengineering.emplperf.update.ParamsEnum;

/**
 * Condition for Assoc
 */
public class EmplPerfReadAssocConditionCreator extends EmplPerfReadConditionCreator {

    /**
     * Return condition
     * @param ctx
     * @return
     */
    public EntityCondition buildReadCondition(Map<String, Object> ctx) {
        EmplPerfFindServices emplPerfFindServices = new EmplPerfFindServices();
        List<EntityCondition> condition = emplPerfFindServices.getConditionForRoot(ctx);
        
        Timestamp estimatedStartDate = (Timestamp)ctx.get(ParamsEnum.estimatedStartDate.name());
        Timestamp readDate = (Timestamp)ctx.get(ParamsEnum.readDate.name());
        Timestamp estimatedCompletionDate = (Timestamp)ctx.get(ParamsEnum.estimatedCompletionDate.name());
        
        if (UtilValidate.isNotEmpty(estimatedStartDate)) {
            condition.add(EntityCondition.makeCondition(ParamsEnum.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDate));
        }
        buildEstimatedStartDateCondition(condition, estimatedCompletionDate);                       
        
        
        if (UtilValidate.isNotEmpty(readDate)) {
        	condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, readDate));
        	condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, readDate));
        }
        
        List<EntityCondition> fromConditionList = FastList.newInstance();
        fromConditionList.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldEstimatedStartDate.name(), EntityOperator.EQUALS_FIELD, EmplPerfServiceEnum.thruDate.name()));
        fromConditionList.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldEstimatedStartDate.name(), EntityOperator.LESS_THAN_FIELD, EmplPerfServiceEnum.thruDate.name()));
        condition.add(EntityCondition.makeCondition(fromConditionList, EntityOperator.OR));
        
        List<EntityCondition> thruConditionList = FastList.newInstance();
        thruConditionList.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldEstimatedCompletionDate.name(), EntityOperator.EQUALS_FIELD, EmplPerfServiceEnum.fromDate.name()));
        thruConditionList.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldEstimatedCompletionDate.name(), EntityOperator.GREATER_THAN_FIELD, EmplPerfServiceEnum.fromDate.name()));
        condition.add(EntityCondition.makeCondition(thruConditionList, EntityOperator.OR));
        
        
        String workEffortAssocTypeId = (String)ctx.get(ParamsEnum.updateWorkEffortAssocType.name());
        if (UtilValidate.isNotEmpty(workEffortAssocTypeId)) {
            condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldWeAssocTypeId.name(), workEffortAssocTypeId));
        }
        
        condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.oldWeRevisionId.name(), null));
        condition.add(EntityCondition.makeCondition(EmplPerfServiceEnum.sameWorkEffortId.name(), null));
        
        return EntityCondition.makeCondition(condition);
    }
   
}
