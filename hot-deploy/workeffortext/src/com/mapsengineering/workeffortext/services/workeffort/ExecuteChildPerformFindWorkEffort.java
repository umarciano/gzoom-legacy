package com.mapsengineering.workeffortext.services.workeffort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * ExecuteChildPerformFindWorkEffort Service Find
 * @author nito
 *
 */
public class ExecuteChildPerformFindWorkEffort extends GenericService {
	
    public static final String MODULE = ExecuteChildPerformFindWorkEffort.class.getName();
    private static final String SERVICE_TYPE = null;
	
    /**
     * Constructor
     * @param dctx
     * @param context
     * @param serviceName
     */
    public ExecuteChildPerformFindWorkEffort(DispatchContext dctx, Map<String, Object> context, String serviceName) {
        super(dctx, context, new JobLogger(MODULE), serviceName, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }
    
    /**
     * Main loop
     */
    protected void mainLoop(String queryWorkEffort) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        
        try {
            JdbcQueryIterator queryWorkEffortList = new FtlQuery(getDelegator(), queryWorkEffort, mapContextUpdate()).iterate();
            try {
                while (queryWorkEffortList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryWorkEffortList.next();
                    Map<String, Object> row = FastMap.newInstance();
                    
                    row.put(WE.workEffortId.name(), ele.getString(WE.WORK_EFFORT_ID.name()));
                    row.put(WE.workEffortTypeId.name(), ele.getString(WE.WORK_EFFORT_TYPE_ID.name()));                   
                    row.put(WE.currentStatusId.name(), ele.getString(WE.CURRENT_STATUS_ID.name()));
                    row.put(WE.workEffortParentId.name(), ele.getString(WE.WORK_EFFORT_PARENT_ID.name()));
                    row.put(WE.organizationId.name(), ele.getString(WE.ORGANIZATION_ID.name()));
                    row.put(WE.orgUnitRoleTypeId.name(), ele.getString(WE.ORG_UNIT_ROLE_TYPE_ID.name()));
                    row.put(WE.orgUnitId.name(), ele.getString(WE.ORG_UNIT_ID.name()));
                    row.put(WE.sourceReferenceId.name(), ele.getString(WE.SOURCE_REFERENCE_ID.name()));
                    row.put(WE.workEffortName.name(), ele.getString(WE.WORK_EFFORT_NAME.name()));
                    row.put(WE.estimatedStartDate.name(), ele.getTimestamp(WE.ESTIMATED_START_DATE.name()));                   
                    row.put(WE.estimatedCompletionDate.name(), ele.getTimestamp(WE.ESTIMATED_COMPLETION_DATE.name()));
                    row.put(WE.weEtch.name(), ele.getString(WE.WE_ETCH.name()));
                    row.put(WE.workEffortSnapshotId.name(), ele.getString(WE.WORK_EFFORT_SNAPSHOT_ID.name()));
                    row.put(WE.uvUserLoginId.name(), ele.getString(WE.UV_USER_LOGIN_ID.name()));
                    row.put(WE.weContextId.name(), ele.getString(WE.WE_CONTEXT_ID.name()));
                    row.put(WE.weIsRoot.name(), ele.getString(WE.WE_IS_ROOT.name()));
                    row.put(WE.weIsTemplate.name(), ele.getString(WE.WE_IS_TEMPLATE.name()));
                    row.put(WE.weTypeDescription.name(), ele.getString(WE.WE_TYPE_DESCRIPTION.name()));
                    row.put(WE.etch.name(), ele.getString(WE.ETCH.name()));
                    row.put(WE.weHierarchyTypeId.name(), ele.getString(WE.WE_HIERARCHY_TYPE_ID.name()));                  
                    row.put(WE.weStatusDescr.name(), ele.getString(WE.WE_STATUS_DESCR.name()));
                    row.put(WE.weActivation.name(), ele.getString(WE.WE_ACTIVATION.name()));
                    row.put(WE.weStatusSeqId.name(), ele.getString(WE.WE_STATUS_SEQ_ID.name()));
                    row.put(WE.weStatusTypeId.name(), ele.getString(WE.WE_STATUS_TYPE_ID.name()));
                    row.put(WE.glFiscalTypeId.name(), ele.getString(WE.GL_FISCAL_TYPE_ID.name()));
                    row.put(WE.nextStatusId.name(), ele.getString(WE.NEXT_STATUS_ID.name()));
                    row.put(WE.managementRoleTypeId.name(), ele.getString(WE.MANAGEMENT_ROLE_TYPE_ID.name()));
                    row.put(WE.managWeStatusEnumId.name(), ele.getString(WE.MANAG_WE_STATUS_ENUM_ID.name()));
                    row.put(WE.partyIdTo.name(), ele.getString(WE.PARTY_ID_TO.name()));
                    row.put(WE.partyId.name(), ele.getString(WE.PARTY_ID.name()));
                    row.put(WE.partyName.name(), ele.getString(WE.PARTY_NAME.name()));                    
                    row.put(WE.workEffortNameLang.name(), ele.getString(WE.WORK_EFFORT_NAME_LANG.name()));
                    row.put(WE.weTypeDescriptionLang.name(), ele.getString(WE.WE_TYPE_DESCRIPTION_LANG.name()));
                    row.put(WE.weStatusDescrLang.name(), ele.getString(WE.WE_STATUS_DESCR_LANG.name()));
                    row.put(WE.partyNameLang.name(), ele.getString(WE.PARTY_NAME_LANG.name()));
                    row.put(WE.etchLang.name(), ele.getString(WE.ETCH_LANG.name()));
                    row.put(WE.parentRoleCode.name(), ele.getString(WE.PARENT_ROLE_CODE.name()));
                    row.put(WE.externalId.name(), ele.getString(WE.EXTERNAL_ID.name()));
                    row.put(WE.weFromName.name(), ele.getString(WE.	WE_FROM_NAME.name()));
                	row.put(WE.weFromNameLang.name(), ele.getString(WE.WE_FROM_NAME_LANG.name()));
                    rowList.add(row);                                   
                }

                result.put("rowList", rowList);
            } finally {
            	queryWorkEffortList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }
    
    /**
     * Set mapContext
     * @return
     * @throws SQLException
     */
    protected MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WE.isOrgMgr.name(), (Boolean)context.get(WE.isOrgMgr.name()));
        mapContext.put(WE.isRole.name(), (Boolean)context.get(WE.isRole.name()));
        mapContext.put(WE.isSup.name(), (Boolean)context.get(WE.isSup.name()));   
        mapContext.put(WE.isTop.name(), (Boolean)context.get(WE.isTop.name()));
        mapContext.put(WE.orgUnitId.name(), (String)context.get(WE.orgUnitId.name()));
//        mapContext.put(WE.currentStatusId.name(), (String)context.get(WE.currentStatusId.name()));
        mapContext.put(WE.weStatusDescr.name(), (String)context.get(WE.weStatusDescr.name()));
        mapContext.put(WE.weStatusDescrLang.name(), (String)context.get(WE.weStatusDescrLang.name()));
        mapContext.put(WE.workEffortTypeId.name(), (String)context.get(WE.workEffortTypeId.name()));
        mapContext.put(WE.sourceReferenceId.name(), (String)context.get(WE.sourceReferenceId.name()));
        mapContext.put(WE.workEffortName.name(), (String)context.get(WE.workEffortName.name()));
        mapContext.put(WE.workEffortNameLang.name(), (String)context.get(WE.workEffortNameLang.name()));
        mapContext.put(WE.weIsTemplate.name(), (String)context.get(WE.weIsTemplate.name()));
        mapContext.put(WE.weEtch.name(), (String)context.get(WE.weEtch.name()));
        mapContext.put(WE.weContextId.name(), (String)context.get(WE.weContextId.name()));
        mapContext.put(WE.organizationId.name(), (String)context.get(WE.organizationId.name()));
        mapContext.put(WE.withProcess.name(), (String)context.get(WE.withProcess.name()));
        mapContext.put(WE.weFromName.name(), (String)context.get(WE.weFromName.name()));
        mapContext.put(WE.weFromNameLang.name(), (String)context.get(WE.weFromNameLang.name()));

        return mapContext;
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
