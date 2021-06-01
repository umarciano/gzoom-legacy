package com.mapsengineering.accountingext.services;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.util.JobLoggedService;

/**
 * Interface
 *
 */
public interface FactorCalculatorFiller extends JobLoggedService {

    /** Fill a map with amount e origAmount, the service execute query on AcctgTransAndEntriesIndicCalcView, with specific condition defined by getReadValuesCondition(inputCalc)  */
    Map<String, Map<String, Object>> fillFactorMap(GenericValue inputCalc, String glAccountId, Map<String, Object> extraCondition) throws GeneralException;

    /** Get list of map with specific value for workEffortMesure, glAccountrole, ect... from context 
     * @throws GeneralException */
    List<Map<String, Object>> getExtraParametersList(String glAccountId, Map<String, ? extends Object> context) throws GeneralException;

    /**
     * Return map with parameters to store in transaction
     * @param glAccountId
     * @param extraCondition
     * @param organizationId
     * @return
     * @throws GenericEntityException
     */
    Map<String, Object> getParametersToStore(String glAccountId, Map<String, Object> extraCondition, String organizationId) throws GenericEntityException;

    /**
     * Return entityCondition for extract transaction
     * @param glAccountIdRef
     * @param extraCondition
     * @return
     * @throws GenericEntityException
     */
    EntityCondition getReadValuesCondition(GenericValue inputCalc, String glAccountIdRef, Map<String, Object> extraCondition) throws GenericEntityException;

    /**
     * Return map with parameters to write in transaction
     * @param glAccountIdRef
     * @param extraCondition
     * @return
     * @throws GenericEntityException
     */
    EntityCondition getWriterValuesCondition(String glAccountIdRef, Map<String, Object> extraCondition) throws GenericEntityException;

    /**
     * Init map for result
     * @param factorFieldNames
     */
    void initResultMap(List<String> factorFieldNames);

    /** Set glFiscalTypeIdOutput from indicator or context */
    void setGlFiscalTypeIdOutput(String glFiscalTypeIdOutput);

    /** Get glFiscalTypeIdOutput from indicator or context */
    String getGlFiscalTypeIdOutput();

    /** Set customMethodName from indicator */
    void setCustomMethodName(String customMethodName);

    /** Get customMethodName from indicator*/
    String getCustomMethodName();

}
