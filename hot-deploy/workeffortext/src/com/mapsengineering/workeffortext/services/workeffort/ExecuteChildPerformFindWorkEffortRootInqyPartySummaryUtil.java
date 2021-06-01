package com.mapsengineering.workeffortext.services.workeffort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.ofbiz.base.util.collections.MapContext;

public class ExecuteChildPerformFindWorkEffortRootInqyPartySummaryUtil {

	/**
	 * riempie la mappa coi risultati
	 * @param row
	 * @param ele
	 * @param isStratOrg
	 * @throws SQLException
	 */
	public static void fillRow(Map<String, Object> row, ResultSet ele, boolean isStratOrg) throws SQLException {
        row.put(WE.workEffortId.name(), ele.getString(WE.WORK_EFFORT_ID.name()));
        row.put(WE.currentStatusId.name(), ele.getString(WE.CURRENT_STATUS_ID.name()));
        row.put(WE.workEffortParentId.name(), ele.getString(WE.WORK_EFFORT_PARENT_ID.name()));                  
        row.put(WE.organizationId.name(), ele.getString(WE.ORGANIZATION_ID.name()));
        row.put(WE.orgUnitRoleTypeId.name(), ele.getString(WE.ORG_UNIT_ROLE_TYPE_ID.name()));
        row.put(WE.orgUnitId.name(), ele.getString(WE.ORG_UNIT_ID.name()));                    
        row.put(WE.partyName.name(), ele.getString(WE.PARTY_NAME.name()));
        row.put(WE.partyNameLang.name(), ele.getString(WE.PARTY_NAME_LANG.name()));
        row.put(WE.sourceReferenceId.name(), ele.getString(WE.SOURCE_REFERENCE_ID.name()));
        row.put(WE.workEffortName.name(), ele.getString(WE.WORK_EFFORT_NAME.name()));
        row.put(WE.workEffortNameLang.name(), ele.getString(WE.WORK_EFFORT_NAME_LANG.name()));
        row.put(WE.estimatedStartDate.name(), ele.getTimestamp(WE.ESTIMATED_START_DATE.name()));                   
        row.put(WE.estimatedCompletionDate.name(), ele.getTimestamp(WE.ESTIMATED_COMPLETION_DATE.name()));
        row.put(WE.workEffortSnapshotId.name(), ele.getString(WE.WORK_EFFORT_SNAPSHOT_ID.name()));                    
        row.put(WE.weEtch.name(), ele.getString(WE.WE_ETCH.name()));                          
        row.put(WE.uvUserLoginId.name(), ele.getString(WE.UV_USER_LOGIN_ID.name()));               
        row.put(WE.workEffortRootTypeId.name(), ele.getString(WE.WORK_EFFORT_ROOT_TYPE_ID.name()));
        row.put(WE.weContextId.name(), ele.getString(WE.WE_CONTEXT_ID.name()));
        row.put(WE.weIsRoot.name(), ele.getString(WE.WE_IS_ROOT.name()));                                       
        row.put(WE.weIsTemplate.name(), ele.getString(WE.WE_IS_TEMPLATE.name()));
        row.put(WE.weTypeDescription.name(), ele.getString(WE.WE_TYPE_DESCRIPTION.name()));
        row.put(WE.weTypeDescriptionLang.name(), ele.getString(WE.WE_TYPE_DESCRIPTION_LANG.name()));                  
        row.put(WE.etch.name(), ele.getString(WE.ETCH.name()));
        row.put(WE.etchLang.name(), ele.getString(WE.ETCH_LANG.name()));
        row.put(WE.weHierarchyTypeId.name(), ele.getString(WE.WE_HIERARCHY_TYPE_ID.name()));                   
        row.put(WE.weStatusDescr.name(), ele.getString(WE.WE_STATUS_DESCR.name())); 
        row.put(WE.weStatusDescrLang.name(), ele.getString(WE.WE_STATUS_DESCR_LANG.name()));
        row.put(WE.weActivation.name(), ele.getString(WE.WE_ACTIVATION.name()));
        row.put(WE.glFiscalTypeId.name(), ele.getString(WE.GL_FISCAL_TYPE_ID.name()));
        row.put(WE.weStatusTypeId.name(), ele.getString(WE.WE_STATUS_TYPE_ID.name()));                  
        row.put(WE.nextStatusId.name(), ele.getString(WE.NEXT_STATUS_ID.name()));
    	row.put(WE.managementRoleTypeId.name(), ele.getString(WE.MANAGEMENT_ROLE_TYPE_ID.name()));
    	row.put(WE.managWeStatusEnumId.name(), ele.getString(WE.MANAG_WE_STATUS_ENUM_ID.name()));

        if (! isStratOrg) {
        	row.put(WE.weTotal.name(), ele.getLong(WE.WE_TOTAL.name()));
        	row.put(WE.partyIdInCharge.name(), ele.getString(WE.PARTY_ID_IN_CHARGE.name()));
        	row.put(WE.partyNameInCharge.name(), ele.getString(WE.PARTY_NAME_IN_CHARGE.name()));
        	row.put(WE.roleTypeIdInCharge.name(), ele.getString(WE.ROLE_TYPE_ID_IN_CHARGE.name()));                                  
        	row.put(WE.partyIdTo.name(), ele.getString(WE.PARTY_ID_TO.name()));
        	row.put(WE.partyId.name(), ele.getString(WE.PARTY_ID.name()));     	
            row.put(WE.workEffortIdChild.name(), ele.getString(WE.WORK_EFFORT_ID_CHILD.name()));
            row.put(WE.workEffortTypeId.name(), ele.getString(WE.WORK_EFFORT_TYPE_ID.name()));
            row.put(WE.weTypeEtch.name(), ele.getString(WE.WE_TYPE_ETCH.name()));
            row.put(WE.weTypeEtchLang.name(), ele.getString(WE.WE_TYPE_ETCH_LANG.name()));    
        }
        
        row.put(WE.sequenceId.name(), ele.getString(WE.MIN_SEQUENCE_ID.name()));
	}
	
    public static MapContext<String, Object> mapContextUpdate(Map<String, Object> context) {
    	MapContext<String, Object> mapContext = mapContext(context);
        mapContext.put(WE.isOrgMgr.name(), (Boolean)context.get(WE.isOrgMgr.name()));
        mapContext.put(WE.isRole.name(), (Boolean)context.get(WE.isRole.name()));
        mapContext.put(WE.isSup.name(), (Boolean)context.get(WE.isSup.name()));
        mapContext.put(WE.weContextId.name(), (String)context.get(WE.weContextId.name())); 
        mapContext.put(WE.orgUnitId.name(), (String)context.get(WE.orgUnitId.name()));
        mapContext.put(WE.organizationId.name(), (String)context.get(WE.organizationId.name()));
        mapContext.put(WE.queryOrderBy.name(), (String)context.get(WE.queryOrderBy.name()));

        return mapContext;
    }

    private static MapContext<String, Object> mapContext(Map<String, Object> context){
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }

}
