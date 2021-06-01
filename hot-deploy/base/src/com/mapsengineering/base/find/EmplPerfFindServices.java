package com.mapsengineering.base.find;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.find.enumeration.EmplPerfFieldEnum;
import com.mapsengineering.base.find.enumeration.WorkEffortViewFieldEnum;


/**
 * Condition for EmplPerf
 */
public class EmplPerfFindServices {
    
    /**
     * Return basic condition with orgUnitRoleTypeId, orgUnitId, emplPositionTypeId, templateTypeId, templateId
     */
    public List<EntityCondition> getCondition(Map<String, Object> ctx) {
        String organizationId = (String)ctx.get(EmplPerfFieldEnum.organizationId.name());
        String orgUnitId = (String)ctx.get(EmplPerfFieldEnum.orgUnitId.name());
        String orgUnitRoleTypeId = (String)ctx.get(EmplPerfFieldEnum.orgUnitRoleTypeId.name());
        String emplPositionTypeId = (String)ctx.get(EmplPerfFieldEnum.emplPositionTypeId.name()); 
        String templateTypeId = (String)ctx.get(EmplPerfFieldEnum.templateTypeId.name());
        String templateId = (String)ctx.get(EmplPerfFieldEnum.templateId.name());
        String parentTypeId = (String)ctx.get(EmplPerfFieldEnum.parentTypeId.name());
        List<EntityCondition> readCondition = FastList.newInstance();
        readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.parentTypeId.name(), parentTypeId));
        
        if (UtilValidate.isNotEmpty(organizationId)) {
            readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.organizationId.name(), organizationId));
        }
        if (UtilValidate.isNotEmpty(orgUnitId)) {
            readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.orgUnitId.name(), orgUnitId));
        }
        if (UtilValidate.isNotEmpty(orgUnitRoleTypeId)) {
            readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.orgUnitRoleTypeId.name(), orgUnitRoleTypeId));
        }
        if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
            readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.emplPositionTypeId.name(), emplPositionTypeId));
        }        
        if (UtilValidate.isNotEmpty(templateTypeId)) {
            readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.templateTypeId.name(), templateTypeId));
        }
        if (UtilValidate.isNotEmpty(templateId)) {
            readCondition.add(EntityCondition.makeCondition(EmplPerfFieldEnum.templateId.name(), templateId));
        }
        return readCondition;
    }
    
    /**
     * Return root condition with parentTypeId, orgUnitRoleTypeId, orgUnitId, emplPositionTypeId, templateTypeId, templateId
     */
    public List<EntityCondition> getConditionForRoot(Map<String, Object> ctx) {
        List<EntityCondition> readCondition = getCondition(ctx);

        readCondition.add(EntityCondition.makeCondition(WorkEffortViewFieldEnum.isTemplate.name(), WorkEffortViewFieldEnum.N.name()));
        readCondition.add(EntityCondition.makeCondition(WorkEffortViewFieldEnum.isRoot.name(), WorkEffortViewFieldEnum.Y.name()));
        readCondition.add(EntityCondition.makeCondition(WorkEffortViewFieldEnum.workEffortRevisionId.name(), null));
        return readCondition;
    }
}
