package com.mapsengineering.accountingext.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;


public class UoCustomMethodCalculatorUtil extends BaseCustomMethodCalculatorUtil {
	
	private static final String queryCustomMethod = "sql/uo/queryUoCustomMethod.sql.ftl";
	private String workEffortId;

	/**
	 * Constructor
	 * @param delegator
	 * @param context
	 */
	public UoCustomMethodCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
		super(delegator, context);
		this.workEffortId = (String) context.get(WorkEffortFieldEnum.workEffortId.name());
	}
	
	/**
	 * setta il contesto
	 * @param glAccountId
	 * @param refDate
	 * @param glFiscalTypeId
	 * @param customMethodName
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 * @throws SQLException
	 */
    public MapContext<String, Object> mapContextUpdate(String glAccountId, Timestamp refDate, String glFiscalTypeId, String customMethodName, Map<String, ? extends Object> context) throws GenericEntityException, SQLException {       
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WorkEffortFieldEnum.aggregType.name(), getAggregType(customMethodName));
        mapContext.put(WorkEffortFieldEnum.glFiscalTypeId.name(), glFiscalTypeId);
        mapContext.put(WorkEffortFieldEnum.glAccountId.name(), glAccountId);
        mapContext.put(WorkEffortFieldEnum.refDate.name(), refDate);
        if (UtilValidate.isNotEmpty(this.workEffortId)) {
        	GenericValue workEffort = getDelegator().findOne(WorkEffortFieldEnum.WorkEffort.name(), UtilMisc.toMap(WorkEffortFieldEnum.workEffortId.name(), this.workEffortId), false);
        	if (UtilValidate.isNotEmpty(workEffort)) {
        		mapContext.put(WorkEffortFieldEnum.orgUnitId.name(), workEffort.getString(WorkEffortFieldEnum.orgUnitId.name()));
        	}
        }
        return mapContext;
    }
    
    /**
     * ritorna il tipo aggregazione
     * @param customMethodName
     * @return
     */
    private String getAggregType(String customMethodName) {
		int l = customMethodName.length();
		int s = customMethodName.indexOf("(");
		if (s < 0) {
			return customMethodName.substring(3, l);
		}
		return customMethodName.substring(3, s);
    }
    
    public String getQuery() {
        return queryCustomMethod;
    }

}
