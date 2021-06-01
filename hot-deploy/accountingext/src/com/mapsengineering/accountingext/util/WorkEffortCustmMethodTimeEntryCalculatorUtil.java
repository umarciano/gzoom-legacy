package com.mapsengineering.accountingext.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;


import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

import com.mapsengineering.accountingext.services.E;


public class WorkEffortCustmMethodTimeEntryCalculatorUtil extends BaseCustomMethodCalculatorUtil {
	
	private static final String queryCustomMethodTimeEntryWorkEffort = "sql/workeffort/queryTimeEntryWorkEffortCustomMethod.sql.ftl";

	/**
	 * Constructor
	 * @param delegator
	 * @param context
	 */
	public WorkEffortCustmMethodTimeEntryCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
		super(delegator, context);
	}
	
	/**
	 * Return map with parameters for query
	 * @param glAccountId
	 * @param refDate
	 * @param customMethodName
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 * @throws SQLException
	 */
    public MapContext<String, Object> mapContextUpdate(String glAccountId, Timestamp refDate, String customMethodName, Map<String, ? extends Object> context) throws GenericEntityException, SQLException {
        String workEffortId = (String)context.get(E.workEffortId.name());
        
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WorkEffortFieldEnum.workEffortId.name(), workEffortId);
        mapContext.put(WorkEffortFieldEnum.glAccountId.name(), glAccountId);
        mapContext.put(WorkEffortFieldEnum.calcType.name(), getCalcType(customMethodName));
        mapContext.put(WorkEffortFieldEnum.refDate.name(), refDate);
        return mapContext;
    }
	
    /**
     * get calculation type
     * @param customMethodName
     * @return
     */
    private String getCalcType(String customMethodName) {		
		int l = customMethodName.length();
		String funcAggreg = customMethodName.substring(0, l-2).trim();
		int i = funcAggreg.length();
		return funcAggreg.substring(5, i);		
    }
    
    /**
     * return query
     * @return
     */
    public String getQuery() {
    	return queryCustomMethodTimeEntryWorkEffort;
    }

}
