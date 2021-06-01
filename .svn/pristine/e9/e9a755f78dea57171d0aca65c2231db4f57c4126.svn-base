package com.mapsengineering.accountingext.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

import com.mapsengineering.accountingext.services.E;
import com.mapsengineering.accountingext.services.InputAndDetailValue;
import com.mapsengineering.base.services.ServiceLogger;

/**
 * WorkEffort Aggregation CustomMethod Calculator Util
 *
 */
public class WorkEffortCustomMethodCalculatorUtil extends BaseCustomMethodCalculatorUtil {

    private static final String queryCustomMethodWorkEffort = "sql/workeffort/queryAggregWorkEffortCustomMethod.sql.ftl";

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortCustomMethodCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
        super(delegator, context);
    }

    /**
     * Return map with parameters for query
     * @param glAccountId
     * @param refDate
     * @param glFiscalTypeId
     * @param context
     * @return
     * @throws GenericEntityException
     * @throws SQLException
     */
    public MapContext<String, Object> mapContextUpdate(String glAccountId, Timestamp refDate, String glFiscalTypeId, String customMethodName, Map<String, ? extends Object> context) throws GenericEntityException, SQLException {
        String workEffortId = (String)context.get(E.workEffortId.name());
        boolean isAggregAnno = getIsAggregAnno(customMethodName);
        
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WorkEffortFieldEnum.workEffortId.name(), workEffortId);
        mapContext.put(WorkEffortFieldEnum.isAggregAnno.name(), isAggregAnno);
        mapContext.put(WorkEffortFieldEnum.aggregType.name(), getAggregType(customMethodName));
        mapContext.put(WorkEffortFieldEnum.glFiscalTypeId.name(), glFiscalTypeId);
        mapContext.put(WorkEffortFieldEnum.glAccountId.name(), glAccountId);
        mapContext.put(WorkEffortFieldEnum.refDate.name(), refDate);
        mapContext.put(WorkEffortFieldEnum.startYearDate.name(), UtilDateTime.getYearStart(refDate));
        mapContext.put(WorkEffortFieldEnum.endYearDate.name(), UtilDateTime.getYearEnd(refDate, (TimeZone) context.get(ServiceLogger.TIME_ZONE), (Locale) context.get(ServiceLogger.LOCALE)));
        return mapContext;
    }
    
    /**
     * ritorna il tipo di aggregazione
     * @param customMethodName
     * @return
     */
    private String getAggregType(String customMethodName) {
		int l = customMethodName.length();
		String funcAggreg = customMethodName.substring(0, l-2).trim();		
		if (InputAndDetailValue.AGGREG.equals(funcAggreg) || InputAndDetailValue.AGGREG_ANNO.equals(funcAggreg)) {
			return "SUM";
		}		
		if (UtilValidate.isNotEmpty(funcAggreg)) {		
			int i1 = getIsAggregAnno(customMethodName) ? funcAggreg.indexOf("_") : funcAggreg.lastIndexOf("_");
			int i2 = getIsAggregAnno(customMethodName) ? funcAggreg.lastIndexOf("_") : funcAggreg.length();
			return funcAggreg.substring(i1+1, i2);
		}
		return "SUM";
    }

    /**
     * 
     * @param customMethodName
     * @return
     */
    private boolean getIsAggregAnno(String customMethodName) {
        if (customMethodName.indexOf(InputAndDetailValue.ANNO) > -1) {
            return true;
        }
        return false;
    }

    /**
     * Return query
     * @return
     */
    public String getQuery() {
        return queryCustomMethodWorkEffort;
    }
}
