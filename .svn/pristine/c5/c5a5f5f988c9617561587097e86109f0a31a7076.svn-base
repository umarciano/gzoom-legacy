package com.mapsengineering.workeffortext.services.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
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
 * ExecuteChildPerformFindResourceValue
 */
public class ExecuteChildPerformFindResourceValue extends GenericService {

    public static final String MODULE = ExecuteChildPerformFindResourceValue.class.getName();
    private static final String SERVICE_NAME = "executePerformFindResource";
    private static final String SERVICE_TYPE = null;

    private static final String queryResource = "sql/resource/queryResource.sql.ftl";

    /**
     * ExecuteChildPerformFindResourceValue
     */
    public static Map<String, Object> executeChildPerformFindResourceValue(DispatchContext dctx, Map<String, Object> context) {
        ExecuteChildPerformFindResourceValue obj = new ExecuteChildPerformFindResourceValue(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindResourceValue(DispatchContext dctx, Map<String, Object> context) {
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
            JdbcQueryIterator queryResourceList = new FtlQuery(getDelegator(), queryResource, mapContextUpdate()).iterate();
            try {
                while (queryResourceList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryResourceList.next();
                    Map<String, Object> genericValue = populateGenericValue(ele);
                    rowList.add(genericValue);
                }

                result.put("rowList", rowList);
            } finally {
                queryResourceList.close();
            }

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }

    private Map<String, Object> populateGenericValue(ResultSet ele) throws SQLException {
        Map<String, Object> genericValue = new HashMap<String, Object>();
        genericValue.put("weTransWeId", getObiettivoId(ele));
        genericValue.put("weTransMeasureId", getMisuraObj(ele));
        genericValue.put("weTransId", ele.getString(I.WE_TRANS_ID.name()));
        genericValue.put("weTransEntryId", ele.getString(I.WE_TRANS_ENTRY_ID.name()));
        genericValue.put("weTransValue", getTransValue(ele));
        genericValue.put("weTransDate", getTransactionDate(ele));
        genericValue.put("weTransAccountId", ele.getString(I.WE_TRANS_ACCOUNT_ID.name()));
        genericValue.put("weMeasureMeasureType", ele.getString(I.WE_MEASURE_MEASURE_TYPE.name()));
        genericValue.put("isPosted", ele.getString(I.IS_POSTED.name()));
        genericValue.put("groupStatusId", ele.getString(I.GROUP_STATUS_ID.name()));
        genericValue.put("weTransTypeValueId", ele.getString(I.WE_TRANS_TYPE_VALUE_ID.name()));
        genericValue.put("weTransComments", ele.getString(I.WE_TRANS_COMMENTS.name()));
        genericValue.put("weTransCommentsLang", ele.getString(I.WE_TRANS_COMMENTS_LANG.name()));
        genericValue.put("weTransWorkEffortSnapShotId", ele.getString(I.WE_TRANS_WORK_EFFORT_SNAP_SHOT_ID.name()));
        genericValue.put("weTransWorkEffortRevisionId", ele.getString(I.WE_TRANS_WORK_EFFORT_REVISION_ID.name()));
        genericValue.put("glResourceTypeId", ele.getString(I.GL_RESOURCE_TYPE_ID.name()));
        genericValue.put("weTransAccountDesc", ele.getString(I.WE_TRANS_ACCOUNT_DESC.name()));
        genericValue.put("weTransAccountCode", ele.getString(I.WE_TRANS_ACCOUNT_CODE.name()));
        genericValue.put("weTransTypeValueDesc", ele.getString(I.WE_TRANS_TYPE_VALUE_DESC.name()));
        genericValue.put("weTransValueCode", ele.getString(I.WE_TRANS_VALUE_CODE.name()));
        genericValue.put("weAcctgTransAccountId", ele.getString(I.WE_ACCTG_TRANS_ACCOUNT_ID.name()));
        genericValue.put("weAcctgTransAccountFinId", ele.getString(I.WE_ACCTG_TRANS_ACCOUNT_FIN_ID.name()));
        genericValue.put("weTransCurrencyUomId", ele.getString(I.WE_TRANS_CURRENCY_UOM_ID.name()));
        genericValue.put("weTransComment", ele.getString(I.WE_TRANS_COMMENT.name()));
        genericValue.put("weTransCommentLang", ele.getString(I.WE_TRANS_COMMENT_LANG.name()));
        genericValue.put("origAmount", ele.getDouble(I.ORIG_AMOUNT.name()));
        genericValue.put("transProductId", ele.getString(I.TRANS_PRODUCT_ID.name()));
        genericValue.put("weTransValueDesc", ele.getString(I.WE_TRANS_VALUE_DESC.name()));
        genericValue.put("weTransUomDesc", ele.getString(I.WE_TRANS_UOM_DESC.name()));
        genericValue.put("weTransUomType", ele.getString(I.WE_TRANS_UOM_TYPE.name()));
        genericValue.put("weTransDecimalScale", ele.getInt(I.WE_TRANS_DECIMAL_SCALE.name()));
        genericValue.put("weTransPeriodIsClosed", ele.getString(I.WE_TRANS_PERIOD_IS_CLOSED.name()));
        genericValue.put("periodTypeId", ele.getString(I.PERIOD_TYPE_ID.name()));
        genericValue.put("customTimePeriodId", ele.getString(I.CUSTOM_TIME_PERIOD_ID.name()));
        genericValue.put("weTransWorkEffortSnapshotId", ele.getString(I.WE_TRANS_WORK_EFFORT_SNAPSHOT_ID.name()));
        genericValue.put("wmValModId", ele.getString(I.WM_VAL_MOD_ID.name()));
        genericValue.put("glValModId", ele.getString(I.GL_VAL_MOD_ID.name()));
        genericValue.put("inputEnumId", ele.getString(I.INPUT_ENUM_ID.name()));
        
        return genericValue;
    }
    
    private String getObiettivoId(ResultSet ele) throws SQLException {
        return ele.getString(I.WE_TRANS_WE_ID.name());
    }

    private String getMisuraObj(ResultSet ele) throws SQLException {
        return ele.getString(I.WE_TRANS_MEASURE_ID.name());
    }
    
    private Timestamp getTransactionDate(ResultSet ele) throws SQLException {
        if(UtilValidate.isNotEmpty(ele.getTimestamp(I.WE_TRANS_DATE.name()))) {
            return ele.getTimestamp(I.WE_TRANS_DATE.name());
        }
        return null;
    }
    
    private Double getTransValue(ResultSet ele) throws SQLException {
        if(UtilValidate.isNotEmpty(ele.getString(I.WE_TRANS_VALUE.name()))) {
            return ele.getDouble(I.WE_TRANS_VALUE.name());
        }
        return null;
    }

    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(I.workEffortId.name(), getWorkEffortId());
        mapContext.put(I.workEffortMeasureId.name(), getWorkEffortMeasureId());
        mapContext.put(I.glAccountId.name(), getGlAccountId());
        
        return mapContext;
    }

    private String getWorkEffortId() {
        return (String)context.get(I.workEffortId.name());
    }
    
    private String getWorkEffortMeasureId() {
        return (String)context.get(I.workEffortMeasureId.name());
    }

    private String getGlAccountId() {
        return (String)context.get(I.glAccountId.name());
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
