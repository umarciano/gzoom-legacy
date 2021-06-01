package com.mapsengineering.workeffortext.services.partyrole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;


import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.workeffort.WE;


/**
 * ExecutePerformFindPartyRoleOrgUnit Service Find
 */
public class ExecutePerformFindPartyRoleOrgUnit extends GenericService {

    private static final String SERVICE_NAME = "ExecutePerformFindPartyRoleOrgUnit";
    public static final String MODULE = ExecutePerformFindPartyRoleOrgUnit.class.getName();
    private static final String SERVICE_TYPE = null;
    
    private static final String queryPartyRole = "sql/partyrole/queryPartyRoleOrgUnit.sql.ftl";


    /**
     * ExecutePerformFindPartyRoleOrgUnit
     */
    public static Map<String, Object> executePerformFindPartyRoleOrgUnit(DispatchContext dctx, Map<String, Object> context) {
        ExecutePerformFindPartyRoleOrgUnit obj = new ExecutePerformFindPartyRoleOrgUnit(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
   
    public ExecutePerformFindPartyRoleOrgUnit(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }
    

    /**
     * Main loop
     */
    public void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        
        try {
            JdbcQueryIterator queryWorkEffortList = new FtlQuery(getDelegator(), queryPartyRole, mapContextUpdate()).iterate();
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
                    row.put(E.parentRoleCode.name(), ele.getString(E.PARENT_ROLE_CODE.name()));
                    row.put(E.partyName.name(), ele.getString(E.PARTY_NAME.name()));                   
                    row.put(E.partyNameLang.name(), ele.getString(E.PARTY_NAME_LANG.name()));
                    row.put(E.partyId.name(), ele.getString(E.PARTY_ID.name()));
                    row.put(E.roleTypeId.name(), ele.getString(E.ROLE_TYPE_ID.name()));
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

       
    protected MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WE.isOrgMgr.name(), (Boolean)context.get(WE.isOrgMgr.name()));
        mapContext.put(WE.isSup.name(), (Boolean)context.get(WE.isSup.name()));   
        mapContext.put(WE.isTop.name(), (Boolean)context.get(WE.isTop.name()));
        mapContext.put(E.roleTypeId.name(), (String)context.get(E.roleTypeId.name()));
        mapContext.put(E.statusId.name(), (String)context.get(E.statusId.name()));
        mapContext.put(E.organizationId.name(), (String)context.get(E.organizationId.name()));
        return mapContext;
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
 
}
