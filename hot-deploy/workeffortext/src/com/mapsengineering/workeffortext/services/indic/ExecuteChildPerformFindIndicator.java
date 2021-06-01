package com.mapsengineering.workeffortext.services.indic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
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
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;

/**
 * ExecuteChildPerformFindIndicator
 */
public class ExecuteChildPerformFindIndicator extends GenericService {

    public static final String MODULE = ExecuteChildPerformFindIndicator.class.getName();
    private static final String SERVICE_NAME = "executePerformFindIndicator";
    private static final String SERVICE_TYPE = null;

    private static final String queryIndicator = "sql/indicator/queryIndicator.sql.ftl";

    /**
     * ExecuteChildPerformFindIndicator
     */
    public static Map<String, Object> executeChildPerformFindIndicator(DispatchContext dctx, Map<String, Object> context) {
        ExecuteChildPerformFindIndicator obj = new ExecuteChildPerformFindIndicator(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindIndicator(DispatchContext dctx, Map<String, Object> context) {
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
            JdbcQueryIterator queryIndicatorList = new FtlQuery(getDelegator(), queryIndicator, mapContextUpdate()).iterate();
            try {
                while (queryIndicatorList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryIndicatorList.next();
                    Map<String, Object> genericValue = populateGenericValue(ele);
                    rowList.add(genericValue);
                }

                result.put("rowList", rowList);
            } finally {
                queryIndicatorList.close();
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
        genericValue.put(I.workEffortMeasureId.name(), getMisuraObj(ele));
        genericValue.put(I.workEffortId.name(), getObiettivoId(ele));
        genericValue.put(I.glAccountId.name(), getGlAccountId(ele));
        genericValue.put(I.uomDescr.name(), ele.getString(I.UOM_DESCR.name()));
        genericValue.put(I.uomDescrLang.name(), ele.getString(I.UOM_DESCR_LANG.name()));
        genericValue.put(I.isPosted.name(), ele.getString(I.IS_POSTED.name()));
        
        genericValue.put(I.weScoreConvEnumId.name(), ele.getString(I.WE_SCORE_CONV_ENUM_ID.name()));
        genericValue.put(I.weScoreRangeEnumId.name(), ele.getString(I.WE_SCORE_RANGE_ENUM_ID.name()));
        genericValue.put(I.weAlertRuleEnumId.name(), ele.getString(I.WE_ALERT_RULE_ENUM_ID.name()));
        
        genericValue.put(I.weMeasureTypeEnumId.name(), ele.getString(I.WE_MEASURE_TYPE_ENUM_ID.name()));
        
        genericValue.put(I.weWithoutPerf.name(), ele.getString(I.WE_WITHOUT_PERF.name()));
        genericValue.put(I.uomRangeId.name(), ele.getString(I.UOM_RANGE_ID.name()));
        genericValue.put(I.kpiScoreWeight.name(), ele.getDouble(I.KPI_SCORE_WEIGHT.name()));
        genericValue.put(I.kpiOtherWeight.name(), ele.getDouble(I.KPI_OTHER_WEIGHT.name()));
        genericValue.put(I.sequenceId.name(), ele.getLong(I.SEQUENCE_ID.name()));
        
        genericValue.put(I.inputEnumId.name(), ele.getString(I.INPUT_ENUM_ID.name()));
        genericValue.put(I.detailEnumId.name(), ele.getString(I.DETAIL_ENUM_ID.name()));
        genericValue.put(I.calcCustomMethodId.name(), ele.getString(I.CALC_CUSTOM_METHOD_ID.name()));
        
        genericValue.put(I.glAccountTypeDescription.name(), ele.getString(I.GL_ACCOUNT_TYPE_DESC.name()));
        genericValue.put(I.fromDate.name(), ele.getTimestamp(I.FROM_DATE.name())); 
        genericValue.put(I.thruDate.name(), ele.getTimestamp(I.THRU_DATE.name())); 
        
        genericValue.put(I.productId.name(), ele.getString(I.PRODUCT_ID.name()));
        genericValue.put(I.uomTypeId.name(), ele.getString(I.UOM_TYPE_ID.name()));
        
        return genericValue;
    }

    private String getGlAccountId(ResultSet ele) throws SQLException {
        return ele.getString(I.GL_ACCOUNT_ID.name());
    }
    
    private String getObiettivoId(ResultSet ele) throws SQLException {
        return ele.getString(I.WORK_EFFORT_ID.name());
    }

    private String getMisuraObj(ResultSet ele) throws SQLException {
        return ele.getString(I.WORK_EFFORT_MEASURE_ID.name());
    }

    private MapContext<String, Object> mapContextUpdate() throws SQLException, GeneralException {
    	FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, context, delegator, false);
    	fromAndThruDatesProvider.run();
    	
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(I.workEffortId.name(), getWorkEffortId());
        mapContext.put(I.contentId.name(), getContentId());
        mapContext.put(I.showScorekpi.name(), showScorekpi());
        
        if (UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
        	mapContext.put(I.fromDate.name(), fromAndThruDatesProvider.getFromDate());
        }
        if (UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
        	mapContext.put(I.thruDate.name(), fromAndThruDatesProvider.getThruDate());
        }

        return mapContext;
    }

    private String getContentId() {
        return (String)context.get(I.contentId.name());
    }
    
    private String showScorekpi() {
        return ( UtilValidate.isNotEmpty(context.get(I.showScorekpi.name())) && "Y".equals(context.get(I.showScorekpi.name())) ) ? "Y" : "N";
    }

    private String getWorkEffortId() {
        return (String)context.get(I.workEffortId.name());
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
