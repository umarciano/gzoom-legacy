package com.mapsengineering.accountingext.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.util.JobLogger;

/**
 * Basic 
 *
 */
public abstract class BaseFiller implements FactorCalculatorFiller {

	public static final String MODULE = BaseFiller.class.getName();

	private Delegator delegator;

	private Timestamp thruDate;

	private String glFiscalTypeIdInput;
	
	private String glFiscalTypeIdOutput;
	
	private String customMethodName;

	private JobLogger jLogger;

	private Map<String, Map<String, Object>> resultMap;

	/**
	 * Constructor, invoke initResultMap for the first time,<br/> then initResultMap is invoke for each extraParameter of List
	 * @param delegator
	 * @param thruDate
	 * @param glFiscalTypeIdInput
	 * @param factorFieldNames
	 */
	BaseFiller(Delegator delegator, Timestamp thruDate, String glFiscalTypeIdInput, List<String> factorFieldNames) {
		this.delegator = delegator;
		this.thruDate = thruDate;
		this.glFiscalTypeIdInput = glFiscalTypeIdInput;
		this.jLogger = new JobLogger(MODULE);
		initResultMap(factorFieldNames);
	}

	/**
	 * @return resultMap like {"amount" = {A = 70, B = 10}, "origAmount" = {A =
	 *         80, B = 18}} or null if there is not row to process for factor
	 *         calculation.
	 */
	public Map<String, Map<String, Object>> fillFactorMap(GenericValue inputCalc, String glAccountId, Map<String, Object> extraCondition) throws GeneralException {
		String msg;

		if (UtilValidate.isEmpty(inputCalc)) {
            msg = "No Input Calc for the indicator id " + glAccountId;
            jLogger.printLogInfo(msg, glAccountId);
            return null;
        }
		String glAccountIdRef = inputCalc.getString(E.glAccountIdRef.name());
		String factorCalculator = inputCalc.getString(E.factorCalculator.name());

		// implements loop for specific condition as workEffortMeasure, productId, etc...
		EntityCondition condition = getReadValuesCondition(inputCalc, glAccountIdRef, extraCondition);
		msg = "Specific condition for glAccount " + condition;
		getjLogger().printLogInfo(msg);

		if (UtilValidate.isNotEmpty(condition)) {
			List<GenericValue> valueList = delegator.findList("AcctgTransAndEntriesIndicCalcView", condition, null, null, null, false);

			msg = "Found " + valueList.size() + " sub-Kpi for factor calculation " + factorCalculator + " condition: " + condition;
			getjLogger().printLogInfo(msg);

			if (UtilValidate.isNotEmpty(valueList)) {
				calculateResultMap(valueList, factorCalculator);
			} else {
				msg = "No Row to process for factor calculation " + factorCalculator;
				getjLogger().printLogInfo(msg);
				return resultMap;
			}
		}
		return resultMap;
	}

	private void calculateResultMap(List<GenericValue> valueList, String factorCalculator) {
		Iterator<String> fieldNameIt = resultMap.keySet().iterator();
		int counter = 0;
		String msg = "";
		String factorCalculatorCounter = "COUNT_" + factorCalculator;
		
		while (fieldNameIt.hasNext()) {
			Iterator<GenericValue> valueIt = valueList.iterator();
			counter = 0;
			String factorFieldName = fieldNameIt.next();
			while (valueIt.hasNext()) {
				GenericValue value = valueIt.next();
				if (UtilValidate.isEmpty(resultMap.get(factorFieldName).get(factorCalculator))) {
					resultMap.get(factorFieldName).put(factorCalculator, 0d);
				}
				double sum = (Double) resultMap.get(factorFieldName).get(factorCalculator) + value.getDouble(factorFieldName);
				resultMap.get(factorFieldName).put(factorCalculator, sum);
				counter++;
			}
			
			if (UtilValidate.isEmpty(resultMap.get(factorFieldName).get(factorCalculatorCounter))) {
				resultMap.get(factorFieldName).put(factorCalculatorCounter, 0);
			}
			int acualCounter = (Integer) resultMap.get(factorFieldName).get(factorCalculatorCounter) + counter;
			resultMap.get(factorFieldName).put(factorCalculatorCounter, acualCounter);
			
			msg = "Partial Sum for factor calculation \"" + factorCalculator + "\" , " + factorFieldName + " = " + resultMap.get(factorFieldName).get(factorCalculator);
			getjLogger().printLogInfo(msg);
		}
		getjLogger().addRecordElaborated(counter);

	}

	/**
	 * Return jobLogger
	 */
	public JobLogger getJobLogger() {
		return getjLogger();
	}

	/**
     * get condition, return condition on Detail only if detailEnumId = ACCDET_NULL
     * @param glAccountIdRef
     * @param extraCondition
     * @param conditionValues
     * @return
     * @throws GenericEntityException
     */
    protected abstract EntityCondition getValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition, EntityCondition condition) throws GenericEntityException;
	
    /**
     * The returned EntityCondition contains the parameters for specific
     * workEffortMesure, glAccountRole, ect...
     */
    public EntityCondition getReadValuesCondition(GenericValue inputCalc, String glAccountIdRef, Map<String, Object> extraCondition) throws GenericEntityException {
        return getValuesCondition(glAccountIdRef, extraCondition, getBaseReadValuesCondition(inputCalc, glAccountIdRef));
    }
    
    /**
     * The returned EntityCondition contains the parameters for specific
     * workEffortMesure, glAccountRole, ect...
     */
    public EntityCondition getWriterValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition) throws GenericEntityException {
        return getValuesCondition(glAccountIdRef, extraCondition, getBaseWriterValuesCondition(glAccountIdRef));
    }
    
	protected Delegator getDelegator() {
		return this.delegator;
	}

	protected Timestamp getThruDate() {
		return this.thruDate;
	}

	protected String getGlFiscalTypeIdInput() {
		return this.glFiscalTypeIdInput;
	}
	
	/**
	 * Return glFiscalTypeIdOutput
	 */
	public String getGlFiscalTypeIdOutput() {
		return this.glFiscalTypeIdOutput;
	}
	
	/**
	 * set glFiscalTypeIdOutput
	 */
	public void setGlFiscalTypeIdOutput(String glFiscalTypeIdOutput) {
	    this.glFiscalTypeIdOutput = glFiscalTypeIdOutput;
	}
	
	   
    /**
     * Return customMethodId
     */
    public String getCustomMethodName() {
        return this.customMethodName;
    }
    
	/**
	 * Init Result with map for amount e origAmount
	 */
    public void initResultMap(List<String> factorFieldNames) {
		this.resultMap = new HashMap<String, Map<String, Object>>();
		Iterator<String> it = factorFieldNames.iterator();
		while (it.hasNext()) {
			String fieldName = it.next();
			resultMap.put(fieldName, new HashMap<String, Object>());
		}
	}

	/**
	 * Return entityCondition with baseValuesCondition and entryGlFiscalTypeId
	 * @param glAccountIdRef
	 * @return
	 */
	protected EntityCondition getBaseReadValuesCondition(GenericValue inputCalc, String glAccountIdRef) {
		List<EntityCondition> valueCond = getBaseValuesCondition(glAccountIdRef);
		if (UtilValidate.isNotEmpty(inputCalc) && UtilValidate.isNotEmpty(inputCalc.getString(E.glFiscalTypeId.name()))) {
			valueCond.add(EntityCondition.makeCondition(E.entryGlFiscalTypeId.name(), inputCalc.getString(E.glFiscalTypeId.name())));
		} else {
			valueCond.add(EntityCondition.makeCondition(E.entryGlFiscalTypeId.name(), getGlFiscalTypeIdInput()));
		}
		return EntityCondition.makeCondition(valueCond);
	}
	
	/**
	 * Return entityCondition with baseValuesCondition and entryGlFiscalTypeId
     * @param glAccountIdRef
	 * @return
	 */
	protected EntityCondition getBaseWriterValuesCondition(String glAccountIdRef) {
		List<EntityCondition> valueCond = getBaseValuesCondition(glAccountIdRef);
		valueCond.add(EntityCondition.makeCondition(E.entryGlFiscalTypeId.name(), getGlFiscalTypeIdOutput()));
		return EntityCondition.makeCondition(valueCond);
	}
	
	/**
	 * Return entityCondition with transactionDate, entryGlAccountId, empty workEffortSnapshotId and empty entryWorkEffortSnapshotId
	 * @param glAccountIdRef
	 * @return
	 */
	private List<EntityCondition> getBaseValuesCondition(String glAccountIdRef) {
		List<EntityCondition> valueCond = new ArrayList<EntityCondition>();
		valueCond.add(EntityCondition.makeCondition(E.transactionDate.name(), getThruDate()));
		valueCond.add(EntityCondition.makeCondition(E.entryGlAccountId.name(), glAccountIdRef));
		valueCond.add(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), null));
		valueCond.add(EntityCondition.makeCondition(E.entryWorkEffortSnapshotId.name(), null));
		return valueCond;

	}

	/**
	 * Get base parameters for all search, with transactionDate,
	 * entryGlAccountId, entryGlFiscalTypeId
	 * @throws GenericEntityException 
	 */
	protected List<Map<String, Object>> getParametersList(String glAccountIdRef) throws GenericEntityException {
		List<Map<String, Object>> extraParametersList = new ArrayList<Map<String, Object>>();
		extraParametersList.add(getParametersToStore(glAccountIdRef, null));

		return extraParametersList;
	}
	
	/**
	 * Store transactionDate, entryGlAccountId and entryGlFiscalTypeId
	 * @throws GenericEntityException 
	 */
	public Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition) throws GenericEntityException {
		Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put(E.transactionDate.name(), getThruDate());
		extraParameters.put(E.entryGlAccountId.name(), glAccountId);
		extraParameters.put(E.entryGlFiscalTypeId.name(), getGlFiscalTypeIdOutput());
		return extraParameters;
	}

	/**
	 * Store weTransProductId from extraCondition.productId
	 * @param extraCondition
	 * @return
	 */
	protected Map<String, Object> getProductExtraParametersToStore(Map<String, Object> extraCondition) {
		Map<String, Object> extraParametersToStore = new HashMap<String, Object>();
		extraParametersToStore.put(E.weTransProductId.name(), extraCondition.get(E.productId.name()));
		return extraParametersToStore;
	}

	/**
	 * Store weTransMeasureId from extraCondition.workEffortMeasureId
	 * @param extraCondition
	 * @return
	 */
	protected Map<String, Object> getWeExtraParametersToStore(Map<String, Object> extraCondition) {
		Map<String, Object> extraParametersToStore = new HashMap<String, Object>();
		extraParametersToStore.put(E.weTransMeasureId.name(), extraCondition.get(E.workEffortMeasureId.name()));
		return extraParametersToStore;
	}

	/**
	 * Store entryPartyId and entryRoleTypeId from extraCondition.partyId and extraCondition.roleTypeId
	 * @param extraCondition
	 * @return
	 */
	protected Map<String, Object> getEntryPartyRoleExtraParametersToStore(Map<String, Object> extraCondition) {
		Map<String, Object> extraParametersToStore = new HashMap<String, Object>();
		extraParametersToStore.put(E.entryPartyId.name(), extraCondition.get(E.partyId.name()));
		extraParametersToStore.put(E.entryRoleTypeId.name(), extraCondition.get(E.roleTypeId.name()));
		return extraParametersToStore;
	}
	
	/**
     * Store partyId = organizationId and roleTypeId from extraCondition.partyId and extraCondition.roleTypeId
     * @param extraCondition
     * @return
	 * @throws GenericEntityException 
     */
    protected Map<String, Object> getPartyRoleExtraParametersToStore(String organizationId) throws GenericEntityException {
        Map<String, Object> extraParametersToStore = new HashMap<String, Object>();
        extraParametersToStore.put(E.partyId.name(), E.Company.name());
        extraParametersToStore.put(E.roleTypeId.name(), getTransRoleTypeId(organizationId));
        return extraParametersToStore;
    }
    

    /**
     * Return roleType of organizationId
     * @return
     * @throws GenericEntityException
     */
    private String getTransRoleTypeId(String organizationId) throws GenericEntityException {
        List<GenericValue> pRoleList = delegator.findList("PartyRole", EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", organizationId), EntityCondition.makeCondition("parentRoleTypeId", "ORGANIZATION_UNIT")), null, UtilMisc.toList("-roleTypeId"), null, false);
        GenericValue partyRole = EntityUtil.getFirst(pRoleList);
        return UtilValidate.isNotEmpty(partyRole) ? partyRole.getString("roleTypeId") : "";
    }

    /**
     * @return the jLogger
     */
    public JobLogger getjLogger() {
        return jLogger;
    }

    /**
     * @param jLogger the jLogger to set
     */
    public void setjLogger(JobLogger jLogger) {
        this.jLogger = jLogger;
    }

    /**
     * @param customMethodName the customMethodName to set
     */
    public void setCustomMethodName(String customMethodName) {
        this.customMethodName = customMethodName;
    }

    /**
     * @return the resultMap
     */
    public Map<String, Map<String, Object>> getResultMap() {
        return resultMap;
    }

    /**
     * @param resultMap the resultMap to set
     */
    public void setResultMap(Map<String, Map<String, Object>> resultMap) {
        this.resultMap = resultMap;
    }

    /**
     * Store partyId and roleTypeId from extraCondition.partyId and extraCondition.roleTypeId
     * @param extraCondition
     * @return
     */
    protected Map<String, Object> getPartyRoleExtraParametersToStore(Map<String, Object> extraCondition) {
        Map<String, Object> extraParametersToStore = new HashMap<String, Object>();
        extraParametersToStore.put(E.partyId.name(), extraCondition.get(E.partyId.name()));
        extraParametersToStore.put(E.roleTypeId.name(), extraCondition.get(E.roleTypeId.name()));
        return extraParametersToStore;
    }
}
