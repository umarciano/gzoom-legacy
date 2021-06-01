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
import com.mapsengineering.accountingext.util.WorkEffortCustomMethodModaCalculatorUtil;
import com.mapsengineering.accountingext.util.WorkEffortFieldEnum;
import com.mapsengineering.base.comparator.PartialMapComparator;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;

/**
 * GlAccount Model on WorkEffort
 *
 */
public class AccinpUoModaFiller extends AccinpUoFlagFiller {

    public static final String MODULE = AccinpUoModaFiller.class.getName();

    /**
     * Constructor
     * @param delegator
     * @param thruDate
     * @param glFiscalTypeIdInput
     * @param factorFieldNames
     */
    AccinpUoModaFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

    /**
     * get condition, return condition entryVoucherRef
     * @param glAccountIdRef
     * @param extraCondition
     * @param conditionValues
     * @return
     * @throws GenericEntityException
     */
    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        List<EntityCondition> valueCond = FastList.newInstance();
        valueCond.add(conditionValues);
        valueCond.add(EntityCondition.makeCondition(E.entryVoucherRef.name(), extraCondition.get(E.workEffortMeasureId.name())));

        return EntityCondition.makeCondition(valueCond);
    }

    /***
     * get the filed value
     */
    protected double getFieldValue(Map<String, Object> extraCondition) {
        double result = 0d;
        List<String> listKeys = FastList.newInstance();
        listKeys.add(E.workEffortMeasureId.name());

        PartialMapComparator<String, Object> partialMapComparator = new PartialMapComparator<String, Object>(listKeys);
        for (Map<String, Object> mappa : getExtraResultMapList()) {
            int res = partialMapComparator.compare(mappa, extraCondition);
            if (res == 0) {
                result = (Double)mappa.get(E.amount.name());
            }

        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GenericEntityException {
        inizializeExtraResultMap();
        List<Map<String, Object>> extraConditionList = FastList.newInstance();

        WorkEffortCustomMethodModaCalculatorUtil workEffortCustomMethodModaCalculatorUtil = new WorkEffortCustomMethodModaCalculatorUtil(getDelegator(), context);        
        try {
        	Map<String, Object> queryContext = workEffortCustomMethodModaCalculatorUtil.mapContextUpdate(glAccountId, getThruDate(), getGlFiscalTypeIdInput(), getCustomMethodName(), context);
            JdbcQueryIterator queryWorkEffortList = new FtlQuery(getDelegator(), workEffortCustomMethodModaCalculatorUtil.getQuery(), queryContext).iterate();
            Debug.log(" - queryWorkEffortList " + queryWorkEffortList);
            try {
                if (queryWorkEffortList.hasNext()) {
                    ResultSet ele = queryWorkEffortList.next();
                    String voucherRef = ele.getString(WorkEffortFieldEnum.VOUCHER_REF.name());
                    BigDecimal amount = ele.getBigDecimal(BaseCustomMethodCalculatorUtil.AMOUNT);
                    String msg = " - For " + voucherRef + " = " + amount;
                    getjLogger().printLogInfo(msg);            

                    Map<String, Object> extraCondition = FastMap.newInstance();
                    extraCondition.put(E.workEffortMeasureId.name(), voucherRef);
                    extraConditionList.add(extraCondition);

                    Map<String, Object> extraResultMap = FastMap.newInstance();
                    extraResultMap.put(E.workEffortMeasureId.name(), voucherRef);
                    Double outputValue = getValue(amount);
                    extraResultMap.put(E.amount.name(), outputValue);
                    getExtraResultMapList().add(extraResultMap);
                }

            } finally {
                queryWorkEffortList.close();
            }
        } catch (IOException e) {
            Debug.logError(e, MODULE);
        } catch (SQLException e) {
            Debug.logError(e, MODULE);
        }

        return extraConditionList;
    }
    
    /**
     * get the value
     * @param amount
     * @return
     */
    private Double getValue(BigDecimal amount) {
    	if (amount == null) {
    		return Double.NaN;
    	}
    	return new Double(amount.doubleValue());
    }

    /**
     * Call super.getParametersToStore and super.getWeExtraParametersToStore
     * @throws GenericEntityException 
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

}
