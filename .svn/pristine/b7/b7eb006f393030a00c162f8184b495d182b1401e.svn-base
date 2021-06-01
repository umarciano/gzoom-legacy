package com.mapsengineering.accountingext.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;


import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

import com.mapsengineering.accountingext.services.E;
import com.mapsengineering.accountingext.services.InputAndDetailValue;


public class WorkEffortCustmMethodYearCalculatorUtil extends BaseCustomMethodCalculatorUtil {
	
	private static final String queryCustomMethodUoYearWorkEffort = "sql/workeffort/queryUoYearWorkEffortCustomMethod.sql.ftl";
	private static final String queryCustomMethodUoDetectYearWorkEffort = "sql/workeffort/queryUoDetectYearWorkEffortCustomMethod.sql.ftl";	
	private static final String queryCustomMethodUoDetailYearWorkEffort = "sql/workeffort/queryUoDetailYearWorkEffortCustomMethod.sql.ftl";
	private static final String queryCustomMethodUoObjYearWorkEffort = "sql/workeffort/queryUoObjYearWorkEffortCustomMethod.sql.ftl";	
	private static final String queryCustomMethodUoPrdYearWorkEffort = "sql/workeffort/queryUoPrdYearWorkEffortCustomMethod.sql.ftl";
	
	private Map<String, String> queryMap;

	/**
	 * Constructor
	 * @param delegator
	 * @param context
	 */
	public WorkEffortCustmMethodYearCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
		super(delegator, context);
		initQueryMap();
	}
	
	/**
	 * init query map
	 */
	private void initQueryMap() {
		queryMap = FastMap.newInstance();
		queryMap.put(InputAndDetailValue.ACCINP_UO_ANNO, queryCustomMethodUoYearWorkEffort);
		queryMap.put(InputAndDetailValue.ACCINP_UO_DETECT_ANNO, queryCustomMethodUoDetectYearWorkEffort);		
		queryMap.put(InputAndDetailValue.ACC_INP_OBJ_ANNO, queryCustomMethodUoObjYearWorkEffort);		
		queryMap.put(InputAndDetailValue.ACC_INP_PRD_ANNO, queryCustomMethodUoPrdYearWorkEffort);		
		queryMap.put(InputAndDetailValue.ACCINP_UO_DET_NOT_NULL_ANNO, queryCustomMethodUoDetailYearWorkEffort);
	}
	
	/**
	 * Return map with parameters for query
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
        String workEffortId = (String)context.get(E.workEffortId.name());
        
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WorkEffortFieldEnum.workEffortId.name(), workEffortId);
        mapContext.put(WorkEffortFieldEnum.glFiscalTypeId.name(), glFiscalTypeId);
        mapContext.put(WorkEffortFieldEnum.glAccountId.name(), glAccountId);
        mapContext.put(WorkEffortFieldEnum.calcType.name(), getCalcType(customMethodName));
        mapContext.put(WorkEffortFieldEnum.refDate.name(), refDate);
        mapContext.put(WorkEffortFieldEnum.transDate.name(), getTransDate(refDate));
        return mapContext;
    }
    
    /**
     * return transDate
     * @param refDate
     * @return
     */
    private Timestamp getTransDate(Timestamp refDate) {
    	return UtilDateTime.getYearStart(refDate);
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
     * @param glAccType
     * @return
     */
    public String getQuery(String glAccType) {
    	return queryMap.get(glAccType);
    }
    
}
