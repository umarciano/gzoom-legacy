package com.mapsengineering.emplperf.insert;

import javolution.util.FastSet;

import java.util.Set;

/**
 * Return fields
 *
 */
public class EmplPerfInsertFromTemplateReadFieldSelect {
    
    /**
     * Return Map with field
     * @return
     */
    public static Set<String> getFieldsToSelect() {
        Set<String> toSelect = FastSet.newInstance();
        toSelect.add(ParamsEnum.partyId.name());
        toSelect.add(ParamsEnum.organizationId.name());
        toSelect.add(ParamsEnum.orgUnitId.name());
        toSelect.add(ParamsEnum.orgUnitRoleTypeId.name());
        toSelect.add(ParamsEnum.emplPositionTypeId.name());
        toSelect.add(ParamsEnum.templateTypeId.name());
        toSelect.add(ParamsEnum.evaluator.name());
        toSelect.add(ParamsEnum.templateId.name());
        toSelect.add(ParamsEnum.approver.name());
        toSelect.add(ParamsEnum.effort.name());
        toSelect.add(ParamsEnum.thruDate.name());
        toSelect.add(ParamsEnum.fromDate.name());

        return toSelect;
    }
}
