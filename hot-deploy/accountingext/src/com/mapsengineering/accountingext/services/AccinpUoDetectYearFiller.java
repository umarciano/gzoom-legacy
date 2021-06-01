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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.accountingext.util.BaseCustomMethodCalculatorUtil;
import com.mapsengineering.accountingext.util.WorkEffortCustmMethodYearCalculatorUtil;
import com.mapsengineering.accountingext.util.WorkEffortFieldEnum;
import com.mapsengineering.base.comparator.PartialMapComparator;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;

public class AccinpUoDetectYearFiller extends AccinpUoFlagFiller {

	/**
	 * Constructor
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	AccinpUoDetectYearFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
		super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
	}

	/**
	 * get values condition
	 */
    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        if(UtilValidate.isEmpty(extraCondition)) {
            return null;
        }
        
        GenericValue glAccountRef = getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountIdRef), true);
        List<EntityCondition> valueCond = FastList.newInstance();
        
        valueCond.add(conditionValues);
        
        if (E.Y.name().equals(glAccountRef.getString(E.detectOrgUnitIdFlag.name()))) {
            valueCond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.partyId.name(), extraCondition.get(E.partyId.name())), EntityCondition.makeCondition(E.roleTypeId.name(), extraCondition.get(E.roleTypeId.name()))));
        }
        
        return EntityCondition.makeCondition(valueCond);
    }

    /**
     * get amount
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

        WorkEffortCustmMethodYearCalculatorUtil workEffortCustmMethodYearCalculatorUtil = new WorkEffortCustmMethodYearCalculatorUtil(getDelegator(), context);        
        try {
        	Map<String, Object> queryContext = workEffortCustmMethodYearCalculatorUtil.mapContextUpdate(glAccountId, getThruDate(), getGlFiscalTypeIdInput(), getCustomMethodName(), context);
            JdbcQueryIterator queryWorkEffortList = new FtlQuery(getDelegator(), workEffortCustmMethodYearCalculatorUtil.getQuery(InputAndDetailValue.ACCINP_UO_DETECT_ANNO), queryContext).iterate();
            Debug.log(" - queryWorkEffortList " + queryWorkEffortList);
            try {
                while (queryWorkEffortList.hasNext()) {
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
                    Double outputValue = new Double(amount.doubleValue());
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
     * Call super.getParametersToStore and super.getWeExtraParametersToStore
     */
    public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException {
        Map<String, Object> extraParametersToStore = FastMap.newInstance();
    	extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition, organizationId));
    	extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(extraCondition));
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
