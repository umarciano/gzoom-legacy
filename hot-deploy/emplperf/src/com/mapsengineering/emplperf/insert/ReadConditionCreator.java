package com.mapsengineering.emplperf.insert;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.find.EmplPerfFindServices;
import com.mapsengineering.emplperf.insert.EmplPerfRootViewFieldEnum;
import com.mapsengineering.emplperf.insert.ParamsEnum;

/**
 * Condition for RelationshipTemplateView and EmplPerfRootView
 */
public class ReadConditionCreator {
    
    private ReadConditionCreator() {}

    /**
     * Return condition for RelationshipTemplateView
     */
    public static EntityCondition buildReadCondition(Map<String, Object> ctx) {
        Timestamp estimatedStartDate = (Timestamp)ctx.get(ParamsEnum.estimatedStartDate.name());
        Timestamp estimatedCompletionDate = (Timestamp)ctx.get(ParamsEnum.estimatedCompletionDate.name());
        String partyRelationshipTypeId = (String)ctx.get(ParamsEnum.partyRelationshipTypeId.name());
        String roleTypeId = (String)ctx.get(ParamsEnum.roleTypeId.name());

        EmplPerfFindServices emplPerfFindServices = new EmplPerfFindServices();
        List<EntityCondition> readCondition = emplPerfFindServices.getCondition(ctx);

        List<EntityCondition> relatThruPeriodCondition = FastList.newInstance();
        relatThruPeriodCondition.add(EntityCondition.makeCondition(ParamsEnum.thruDate.name(), EntityOperator.EQUALS, null));
        relatThruPeriodCondition.add(EntityCondition.makeCondition(ParamsEnum.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDate));

        readCondition.add(EntityCondition.makeCondition(ParamsEnum.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, estimatedCompletionDate));
        readCondition.add(EntityCondition.makeCondition(relatThruPeriodCondition, EntityJoinOperator.OR));
        
        if (UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
        	readCondition.add(EntityCondition.makeCondition(ParamsEnum.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        }
        
        if (UtilValidate.isNotEmpty(roleTypeId)) {
        	readCondition.add(EntityCondition.makeCondition(ParamsEnum.roleTypeId.name(), roleTypeId));
        }
        
        return EntityCondition.makeCondition(readCondition);
    }
    
    /**
     * Return condition for EmplPerfRootView
     */
    public static List<EntityCondition> buildBaseCondition(String evalPartyId, String organizationId, String orgUnitId, String templateId, String evaluator, String approver) {
        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.wepaPartyId.name(), evalPartyId));
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.organizationId.name(), organizationId));
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.orgUnitId.name(), orgUnitId));
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.templateId.name(), templateId));
        if (UtilValidate.isNotEmpty(evaluator)) {
            cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.valPartyId.name(), evaluator));
        }
        if (UtilValidate.isNotEmpty(approver)) {
            cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.appPartyId.name(), approver));
        }
        
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.workEffortId.name(), EntityOperator.EQUALS_FIELD, EmplPerfRootViewFieldEnum.workEffortParentId.name()));

        List<EntityCondition> orCondList = FastList.newInstance();
        orCondList.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.workEffortSnapshotId.name(), null));
        orCondList.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.workEffortSnapshotId.name(), ""));
        cond.add(EntityCondition.makeCondition(orCondList, EntityJoinOperator.OR));
        
        return cond;
    }
}
