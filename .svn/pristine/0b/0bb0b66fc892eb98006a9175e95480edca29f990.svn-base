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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.accountingext.util.BaseCustomMethodCalculatorUtil;
import com.mapsengineering.accountingext.util.PartyRelationshipCustomMethodCalculatorUtil;
import com.mapsengineering.accountingext.util.PartyRelationshipFieldEnum;
import com.mapsengineering.base.comparator.PartialMapComparator;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;

/**
 * GlAccount no Model with Detail
 *
 */
public class AccinpUoFlagFiller extends BaseFiller {

    public static final String MODULE = AccinpUoFlagFiller.class.getName();

    /**
     * In this case there is one query with all result.<br/>
     * All result is store in this map 
     */
    private List<Map<String, Object>> extraResultMapList;

    /**
     * Constructor
     * @param delegator
     * @param thruDate
     * @param glFiscalTypeIdInput
     * @param factorFieldNames
     */
    AccinpUoFlagFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
        super(delegator, thruDate, glFiscalTypeIdInput, factorFieldNames);
    }

    protected EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition conditionValues) throws GenericEntityException {
        List<EntityCondition> valueCond = FastList.newInstance();
        valueCond.add(conditionValues);
        valueCond.add(EntityCondition.makeCondition(E.partyId.name(), extraCondition.get(E.partyId.name())));
        valueCond.add(EntityCondition.makeCondition(E.roleTypeId.name(), extraCondition.get(E.roleTypeId.name())));

        return EntityCondition.makeCondition(valueCond);
    }

    /**
     * @return resultMap like {"amount" = {A = 70, B = 10}, "origAmount" = {A =
     *         80, B = 18}} or null if there is not row to process for factor
     *         calculation.
     */
    public Map<String, Map<String, Object>> fillFactorMap(GenericValue inputCalc, String glAccountId, Map<String, Object> extraCondition) throws GeneralException {
        Iterator<String> fieldNameIt = getResultMap().keySet().iterator();
        int counter = 0;
        String msg = "";

        while (fieldNameIt.hasNext()) {
            String factorFieldName = fieldNameIt.next();
            getResultMap().get(factorFieldName).put(BaseCustomMethodCalculatorUtil.DUMMY_A, getFieldValue(extraCondition));

            msg = "Partial result for factor calculation \"" + BaseCustomMethodCalculatorUtil.DUMMY_A + "\" , " + factorFieldName + " = " + getResultMap().get(factorFieldName).get(BaseCustomMethodCalculatorUtil.DUMMY_A);
            getjLogger().printLogInfo(msg);
        }
        getjLogger().addRecordElaborated(counter);

        return getResultMap();
    }

    protected double getFieldValue(Map<String, Object> extraCondition) {
        double result = 0d;
        List<String> listKeys = FastList.newInstance();
        listKeys.add(E.partyId.name());
        listKeys.add(E.roleTypeId.name());

        PartialMapComparator<String, Object> partialMapComparator = new PartialMapComparator<String, Object>(listKeys);
        for (Map<String, Object> mappa : extraResultMapList) {
            int res = partialMapComparator.compare(mappa, extraCondition);
            if (res == 0) {
                result = (Double)mappa.get(E.amount.name());
            }

        }

        return result;
    }

    protected BigDecimal getAmount(ResultSet ele, String aggregType) throws SQLException {
    	if (BaseCustomMethodCalculatorUtil.MEDIA.equals(aggregType)) {
    		return getAverage(ele);
    	}
        return ele.getBigDecimal(BaseCustomMethodCalculatorUtil.AMOUNT);
    }
    
    private BigDecimal getAverage(ResultSet ele) throws SQLException {
    	BigDecimal sumAmount = ele.getBigDecimal(BaseCustomMethodCalculatorUtil.SUM_AMOUNT);
		BigDecimal countAmount = ele.getBigDecimal(BaseCustomMethodCalculatorUtil.COUNT_AMOUNT);
		
		if (UtilValidate.isEmpty(countAmount) || BigDecimal.ZERO.compareTo(countAmount) == 0) {
			return null;
		}
		if (UtilValidate.isEmpty(sumAmount) || BigDecimal.ZERO.compareTo(sumAmount) == 0) {
			return BigDecimal.ZERO;
		}
		return sumAmount.divide(countAmount, 2, BigDecimal.ROUND_HALF_DOWN);
    }

    @Override
    public List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GeneralException {
        List<Map<String, Object>> extraConditionList = FastList.newInstance();
        inizializeExtraResultMap();

        // Search customMethodName
        PartyRelationshipCustomMethodCalculatorUtil partyRelationshipCustomMethodCalculatorUtil = new PartyRelationshipCustomMethodCalculatorUtil(getDelegator(), context);

        try {
            JdbcQueryIterator queryPartyRelationshipList = new FtlQuery(getDelegator(), partyRelationshipCustomMethodCalculatorUtil.getQuery(getCustomMethodName()), partyRelationshipCustomMethodCalculatorUtil.mapContextUpdate(glAccountId, getThruDate(), getCustomMethodName(), context)).iterate();
            Debug.log(" - queryPartyRelationshipList " + queryPartyRelationshipList);
            try {
                while (queryPartyRelationshipList.hasNext()) {
                    ResultSet ele = queryPartyRelationshipList.next();
                    String partyId = getPartyId(ele);
                    String roleTypeId = getRoleTypeId(ele);
                    BigDecimal amount = getAmount(ele, "");
                    String msg = " - For " + partyId + " - " + roleTypeId + " = " + amount;
                    getjLogger().printLogInfo(msg);
                    
                    Map<String, Object> extraCondition = FastMap.newInstance();
                    extraCondition.put(E.partyId.name(), partyId);
                    extraCondition.put(E.roleTypeId.name(), roleTypeId);
                    extraConditionList.add(extraCondition);

                    Map<String, Object> extraResultMap = FastMap.newInstance();
                    extraResultMap.put(E.partyId.name(), partyId);
                    extraResultMap.put(E.roleTypeId.name(), roleTypeId);
                    Double outputValue = new Double(amount.doubleValue());
                    extraResultMap.put(E.amount.name(), outputValue);
                    extraResultMapList.add(extraResultMap);
                }

            } finally {
                queryPartyRelationshipList.close();

            }
        } catch (IOException e) {
            Debug.logError(e, MODULE);
        } catch (SQLException e) {
            Debug.logError(e, MODULE);
        }
        return extraConditionList;
    }

    protected void inizializeExtraResultMap() {
        extraResultMapList = FastList.newInstance();
    }

    private String getPartyId(ResultSet ele) throws SQLException {
        return ele.getString(PartyRelationshipFieldEnum.PARTY_ID.name());
    }

    private String getRoleTypeId(ResultSet ele) throws SQLException {
        return ele.getString(PartyRelationshipFieldEnum.ROLE_TYPE_ID.name());
    }

    /**
     * Call super.getParametersToStore and super.getPartyRoleExtraParametersToStore
     * @throws GenericEntityException 
     */
    public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException {
        Map<String, Object> extraParametersToStore = FastMap.newInstance();
        extraParametersToStore.putAll(super.getParametersToStore(glAccountId, extraCondition));
        extraParametersToStore.putAll(super.getPartyRoleExtraParametersToStore(extraCondition));
        return extraParametersToStore;
    }

    /**
     * @return the extraResultMapList
     */
    public List<Map<String, Object>> getExtraResultMapList() {
        return extraResultMapList;
    }

    /**
     * @param extraResultMapList the extraResultMapList to set
     */
    public void setExtraResultMapList(List<Map<String, Object>> extraResultMapList) {
        this.extraResultMapList = extraResultMapList;
    }
}
