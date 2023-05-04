package com.mapsengineering.workeffortext.services.workeffort;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

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

/**
 * ExecuteChildPerformFindWorkEffortRootInqySummary Service Find
 * @author nito
 *
 */
public class ExecuteChildPerformFindWorkEffortRootInqySummary extends GenericService {
	
    public static final String MODULE = ExecuteChildPerformFindWorkEffortRootInqySummary.class.getName();
    private static final String SERVICE_NAME = "executeChildPerformFindWorkEffortRootInqySummary";
    private static final String SERVICE_TYPE = null;
    
    private static final String queryWorkEffortRootInqySummary = "sql/workeffort/queryWorkEffortRootInqySummary.sql.ftl";
    
    /**
     * executeChildPerformFindWorkEffortRootInqySummary
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> executeChildPerformFindWorkEffortRootInqySummary(DispatchContext dctx, Map<String, Object> context) {
    	ExecuteChildPerformFindWorkEffortRootInqySummary obj = new ExecuteChildPerformFindWorkEffortRootInqySummary(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
	
    /**
     * Constructor
     * @param dctx
     * @param context
     * @param serviceName
     */
    public ExecuteChildPerformFindWorkEffortRootInqySummary(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }
    
    /**
     * Main loop
     */
    protected void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        try {
            JdbcQueryIterator queryWorkEffortList = new FtlQuery(getDelegator(), queryWorkEffortRootInqySummary, mapContextUpdate()).iterate();
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
                    
                    row.put(WE.orgUnitId.name(), ele.getString(WE.ORG_UNIT_ID.name()));
                    row.put(WE.organizationId.name(), ele.getString(WE.ORGANIZATION_ID.name()));
                    row.put(WE.partyName.name(), ele.getString(WE.PARTY_NAME.name()));
                    row.put(WE.currentStatusId.name(), ele.getString(WE.CURRENT_STATUS_ID.name()));
                    row.put(WE.uvUserLoginId.name(), ele.getString(WE.UV_USER_LOGIN_ID.name()));
                    row.put(WE.weStatusDescr.name(), ele.getString(WE.WE_STATUS_DESCR.name()));
                    row.put(WE.weStatusDescrLang.name(), ele.getString(WE.WE_STATUS_DESCR_LANG.name())); 
                    row.put(WE.weActivation.name(), ele.getString(WE.WE_ACTIVATION.name()));
                    row.put(WE.weStatusSeqId.name(), ele.getString(WE.WE_STATUS_SEQ_ID.name()));
                    row.put(WE.weStatusTypeId.name(), ele.getString(WE.WE_STATUS_TYPE_ID.name()));
                    row.put(WE.weTotal.name(), ele.getLong(WE.WE_TOTAL.name()));
                    row.put(WE.managementRoleTypeId.name(), ele.getString(WE.MANAGEMENT_ROLE_TYPE_ID.name()));
                    row.put(WE.managWeStatusEnumId.name(), ele.getString(WE.MANAG_WE_STATUS_ENUM_ID.name()));                  
                    row.put(WE.partyIdTo.name(), ele.getString(WE.PARTY_ID_TO.name()));
                    row.put(WE.partyId.name(), ele.getString(WE.PARTY_ID.name()));
                    row.put(WE.weContextId.name(), ele.getString(WE.WE_CONTEXT_ID.name()));
                    row.put(WE.workEffortSnapshotId.name(), ele.getString(WE.WORK_EFFORT_SNAPSHOT_ID.name()));
                    row.put(WE.partyNameLang.name(), ele.getString(WE.PARTY_NAME_LANG.name()));                                      
                    row.put(WE.parentRoleCode.name(), ele.getString(WE.PARENT_ROLE_CODE.name()));  
                    row.put(WE.externalId.name(), ele.getString(WE.EXTERNAL_ID.name())); 
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
        mapContext.put(WE.weContextId.name(), (String)context.get(WE.weContextId.name()));
        mapContext.put(WE.organizationId.name(), (String)context.get(WE.organizationId.name()));
        mapContext.put(WE.queryOrderBy.name(), (String)context.get(WE.queryOrderBy.name()));
        
        return mapContext;
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
