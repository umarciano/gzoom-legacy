package com.mapsengineering.accountingext.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

import com.mapsengineering.accountingext.services.E;

/**
 * WorkEffort Aggregation CustomMethod Calculator Util
 *
 */
public class WorkEffortCustomMethodModaCalculatorUtil extends BaseCustomMethodCalculatorUtil {

	private static final String queryModaMethodWorkEffort = "sql/workeffort/queryModaWorkEffortCustomMethod.sql.ftl";

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortCustomMethodModaCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
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
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WorkEffortFieldEnum.workEffortId.name(), workEffortId);
        mapContext.put(WorkEffortFieldEnum.glFiscalTypeId.name(), glFiscalTypeId);
        mapContext.put(WorkEffortFieldEnum.glAccountId.name(), glAccountId);
        mapContext.put(WorkEffortFieldEnum.refDate.name(), refDate);
        return mapContext;
    }  

    /**
     * Return query
     * @return
     */
    public String getQuery() {
        return queryModaMethodWorkEffort;
    }
}
