package com.mapsengineering.accountingext.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.accountingext.util.BaseCustomMethodCalculatorUtil;
import com.mapsengineering.accountingext.util.UoCustomMethodCalculatorUtil;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;

public class AccinpUOFlagGroupFiller extends AccinpUoFlagFiller {

	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpUOFlagGroupFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
		super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
	}
	
	/**
	 * ritorna le values conditions
	 */
    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        List<EntityCondition> valueCond = FastList.newInstance();
        valueCond.add(conditionValues);
        valueCond.add(EntityCondition.makeCondition(E.partyId.name(), extraCondition.get(E.partyId.name())));
        valueCond.add(EntityCondition.makeCondition(E.roleTypeId.name(), extraCondition.get(E.roleTypeId.name())));

        return EntityCondition.makeCondition(valueCond);
    }
	
    @Override
    public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
        inizializeExtraResultMap();
        List<Map<String, Object>> extraConditionList = FastList.newInstance();

        UoCustomMethodCalculatorUtil uoCustomMethodCalculatorUtil = new UoCustomMethodCalculatorUtil(getDelegator(), context);        
        try {
        	Map<String, Object> queryContext = uoCustomMethodCalculatorUtil.mapContextUpdate(glAccountId, getThruDate(), getGlFiscalTypeIdInput(), getCustomMethodName(), context);
            JdbcQueryIterator queryUoList = new FtlQuery(getDelegator(), uoCustomMethodCalculatorUtil.getQuery(), queryContext).iterate();
            Debug.log(" - queryUoList " + queryUoList);
            try {
                while (queryUoList.hasNext()) {
                    ResultSet ele = queryUoList.next();
                    
                    Map<String, Object> extraCondition = FastMap.newInstance();
                    extraCondition.put(E.partyId.name(), ele.getString("ORG_UNIT_ID"));
        			extraCondition.put(E.roleTypeId.name(), ele.getString("ORG_UNIT_ROLE_TYPE_ID"));
                    extraConditionList.add(extraCondition);
                    
                    Map<String, Object> extraResultMap = FastMap.newInstance();
                    extraResultMap.put(E.partyId.name(), ele.getString("ORG_UNIT_ID"));
                    extraResultMap.put(E.roleTypeId.name(), ele.getString("ORG_UNIT_ROLE_TYPE_ID"));
                    extraResultMap.put(E.amount.name(), getValue(ele.getBigDecimal(BaseCustomMethodCalculatorUtil.AMOUNT)));
                    getExtraResultMapList().add(extraResultMap);
                }

            } finally {
            	queryUoList.close();
            }
        } catch (IOException e) {
            Debug.logError(e, MODULE);
        } catch (SQLException e) {
            Debug.logError(e, MODULE);
        }

        return extraConditionList;
    }
    
    /**
     * Call super.getParametersToStore and super.getWeExtraParametersToStore
     */
    public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException {
        Map<String, Object> extraParametersToStore = FastMap.newInstance();
        extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition, organizationId));
        extraParametersToStore.putAll(super.getWeExtraParametersToStore(extraCondition));
        return extraParametersToStore;
    }
    
    /**
     * @return resultMap like {"amount" = {A = 70, B = 10}, "origAmount" = {A =
     *         80, B = 18}} or null if there is not row to process for factor
     *         calculation.
     */
    public Map<String, Map<String, Object>> fillFactorMap(GenericValue inputCalc, String glAccountId, Map<String, Object> extraCondition) throws GeneralException {
        Iterator<String> fieldNameIt = getResultMap().keySet().iterator();
        int counter = 0;

        while (fieldNameIt.hasNext()) {
            String factorFieldName = fieldNameIt.next();
            getResultMap().get(factorFieldName).put(BaseCustomMethodCalculatorUtil.DUMMY_A, getFieldValue(extraCondition));
        }
        getjLogger().addRecordElaborated(counter);

        return getResultMap();
    }
    
    /**
     * ritorna amount
     * @param amount
     * @return
     */
    private Double getValue(BigDecimal amount) {
    	if (amount == null) {
    		return Double.NaN;
    	}
    	return new Double(amount.doubleValue());
    }

}
