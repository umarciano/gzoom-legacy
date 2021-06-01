package com.mapsengineering.workeffortext.services.wepa;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
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
 * WorkEffortPartyAssignment Sum RoleTypeWeight Service
 */
public class WorkEffortPartyAssignmentSumRoleTypeWeight extends GenericService {

    public static final String MODULE = WorkEffortPartyAssignmentSumRoleTypeWeight.class.getName();
    private static final String SERVICE_NAME = "getSumRoleTypeWeight";
    private static final String SERVICE_TYPE = null;
    private static final BigDecimal BIG_100 = new BigDecimal(100);
    
    private static final String queryWepaSumRoleTypeWeight = "sql/wepa/queryWepaSumRoleTypeWeight.sql.ftl";

    /**
     * WorkEffortPartyAssignment Sum RoleTypeWeight Service
     */
    public static Map<String, Object> getSumRoleTypeWeight(DispatchContext dctx, Map<String, Object> context) {
        WorkEffortPartyAssignmentSumRoleTypeWeight obj = new WorkEffortPartyAssignmentSumRoleTypeWeight(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public WorkEffortPartyAssignmentSumRoleTypeWeight(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        long startTime = System.currentTimeMillis();
        int index = 0;
        BigDecimal sumMax = new BigDecimal(0);
        try {
            if (UtilValidate.isNotEmpty(getPartyId())) {
                JdbcQueryIterator queryWepaSumRoleTypeWeightList = new FtlQuery(getDelegator(), queryWepaSumRoleTypeWeight, mapContextUpdate()).iterate();
                try {
                    while (queryWepaSumRoleTypeWeightList.hasNext()) {
                        if(index == 0) {
                            long endTime = System.currentTimeMillis();
                            JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                            addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                            index++;
                        }
                        ResultSet ele = queryWepaSumRoleTypeWeightList.next();
                        sumMax = getSumRoleTypeWeight(ele);
                    }

                } finally {
                    queryWepaSumRoleTypeWeightList.close();
                }
            }

            result.put(EN.sumMax.name(), sumMax);
            result.put(EN.availability.name(), BIG_100.subtract(sumMax));
        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }

    private BigDecimal getSumRoleTypeWeight(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(EN.SUM_ROLE_TYPE_WEIGHT.name()))) {
            return ele.getBigDecimal(EN.SUM_ROLE_TYPE_WEIGHT.name());
        }
        return null;
    }

    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(EN.partyId.name(), getPartyId());
        mapContext.put(EN.fromDate.name(), getFromDate());
        mapContext.put(EN.thruDate.name(), getThruDate());

        return mapContext;
    }

    private String getPartyId() {
        return (String)context.get(EN.partyId.name());
    }

    private Timestamp getFromDate() {
        return (Timestamp)context.get(EN.fromDate.name());
    }

    private Timestamp getThruDate() {
        return (Timestamp)context.get(EN.thruDate.name());
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
