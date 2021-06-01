package com.mapsengineering.workeffortext.services.workeffort;

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
import com.mapsengineering.workeffortext.services.partyrole.E;


/**
 * ExecutePerformFindOrgValuedParty Service Find
 */
public class ExecutePerformFindPartyRelationshipEvaluated extends GenericService {

    private static final String SERVICE_NAME = "ExecutePerformFindPartyRelationshipEvaluated";
    public static final String MODULE = ExecutePerformFindPartyRelationshipEvaluated.class.getName();
    private static final String SERVICE_TYPE = null;
    
    private static final String queryPartyRole = "sql/partyrole/queryPartyRelationshipEvaluated.sql.ftl";


    /**
     * ExecutePerformFindPartyRoleOrgUnit
     */
    public static Map<String, Object> executePerformFindPartyRelationshipEvaluated(DispatchContext dctx, Map<String, Object> context) {
    	ExecutePerformFindPartyRelationshipEvaluated obj = new ExecutePerformFindPartyRelationshipEvaluated(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
   
    public ExecutePerformFindPartyRelationshipEvaluated(DispatchContext dctx, Map<String, Object> context) {
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
                    row.put(E.partyIdTo.name(), ele.getString(E.PARTY_ID_TO.name()));                   
                    row.put(E.partyIdFrom.name(), ele.getString(E.PARTY_ID_FROM.name()));
                    row.put(E.roleTypeIdTo.name(), ele.getString(E.ROLE_TYPE_ID_TO.name()));
                    row.put(E.roleTypeIdFrom.name(), ele.getString(E.ROLE_TYPE_ID_FROM.name()));                   
                    row.put(E.partyRelationshipTypeId.name(), ele.getString(E.PARTY_RELATIONSHIP_TYPE_ID.name()));                   
                    row.put(E.fromDate.name(), ele.getTimestamp(E.FROM_DATE.name()));                   
                    row.put(E.thruDate.name(), ele.getTimestamp(E.THRU_DATE.name()));                   
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
        mapContext.put(E.partyRelationshipTypeId.name(), (String)context.get(E.partyRelationshipTypeId.name()));
        mapContext.put(E.orgUnitId.name(), (String)context.get(E.orgUnitId.name()));
        mapContext.put(E.partyIdFrom.name(), (String)context.get(E.partyIdFrom.name()));
        mapContext.put(E.partyIdTo.name(), (String)context.get(E.partyIdTo.name()));
        mapContext.put(E.fromDate.name(), (String)context.get(E.fromDate.name()));
        mapContext.put(E.thruDate.name(), (String)context.get(E.thruDate.name()));
        mapContext.put(E.onlyCurrentEvalRef.name(), (String)context.get(E.onlyCurrentEvalRef.name()));

        return mapContext;
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
 
}
