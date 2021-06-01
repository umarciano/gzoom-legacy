package com.mapsengineering.accountingext.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import bsh.EvalError;

import com.mapsengineering.accountingext.util.CustomMethodCalculatorUtil;
import com.mapsengineering.accountingext.util.GlFiscalTypeOutputUtil;
import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.OfbizServiceContext;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

/**
 * Indicator Calc Service
 *
 */
public final class IndicatorCalcServices {

    public static final String MODULE = IndicatorCalcServices.class.getName();

    private LocalDispatcher dispatcher;

    private Delegator delegator;

    private Map<String, Object> res;

    private Map<String, ? extends Object> context;

    private JobLogger jLogger;

    private List<String> factorFieldNames;

    private final Map<String, FactorCalculatorFiller> factorCalculatorFillerMap;

    private Map<String, Map<String, Object>> factorMap;
    
    private String organizationId;

    private String location = "com/mapsengineering/accountingext/CustomMethodCalculatorUtil.bsh";

    /**
     * Esegue calcolo indicatori
     * <p>Parametri attesi:
     * <p>thruDate - data del calcolo;
     * <p>prioCalc - priorita'
     * <p>glFiscalTypeIdInput - tipo rilevazione
     * <p>glFiscalTypeIdOutput - tipo rilevazione output     
     * @param dctx
     * @param context 
     * @return risultati del calcolo degli indicatori
     */
    public static Map<String, Object> indicatorCalcImpl(DispatchContext dctx, Map<String, ? extends Object> context) {
        @SuppressWarnings("unchecked")
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, (Map<String, Object>)context);
        try {
            IndicatorCalcServices srv = new IndicatorCalcServices(ctx.getDispatcher().getDispatchContext(), context);
            srv.execute();
            return srv.getResult();
        } finally {
            try {
                ctx.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Inizializza i dati per i vari tipi di indicatori, se modello su prodotto o servizio o se modello su obiettivo, 
     * <p>Parametri attesi:
     * <p>thruDate - data del calcolo;
     * <p>glFiscalTypeIdInput - tipo rilevazione input   
     * <p>glFiscalTypeIdOutput - tipo rilevazione output 
     * @param dctx
     * @param context 
     * @return
     */
    public IndicatorCalcServices(DispatchContext dctx, Map<String, ? extends Object> context) {
        // locale and timeZone is in context
        this.context = context;
        
        res = ServiceUtil.returnSuccess();
        delegator = dctx.getDelegator();
        dispatcher = dctx.getDispatcher();
        
        jLogger = new JobLogger(MODULE);

        initFactorFieldNames();

        factorCalculatorFillerMap = FastMap.newInstance();
        // Case Default
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_UO_DET_NULL, new AccinpUoFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 3.A
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_PRD_DET_NULL, new AccinpPrdFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 3.B
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_OBJ, new AccinpWeFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 3.C
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_UO_DET_NOT_NULL, new AccinpUoDetailFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 3.D
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_PRD_DET_NOT_NULL, new AccinpPrdDetailFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 3.E
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_DETECT_UO, new AccinpUoDetectFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 5
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_DETECT_UO_PEUO, new AccinpUoFlagFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 6
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_OBJ_AGGREG, new AccinpWeAggregFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 7
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_OBJ_TSWE_ORE, new AccinpWeTimeEntryFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 8
        factorCalculatorFillerMap.put(InputAndDetailValue.ACCINP_UO_ANNO, new AccinpUoYearFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 9
        factorCalculatorFillerMap.put(InputAndDetailValue.ACCINP_UO_DETECT_ANNO, new AccinpUoDetectYearFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 10           
        factorCalculatorFillerMap.put(InputAndDetailValue.ACCINP_UO_DET_NOT_NULL_ANNO, new AccinpUoDetailYearFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 11            
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_OBJ_ANNO, new AccinpObjYearFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        // Particular Case 12
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_PRD_ANNO, new AccinpPrdYearFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        //Particular Case 13
        factorCalculatorFillerMap.put(InputAndDetailValue.UO_GROUP, new AccinpUOFlagGroupFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
        //Particular Case 14
        factorCalculatorFillerMap.put(InputAndDetailValue.ACC_INP_UO_MODA, new AccinpUoModaFiller(delegator, (Timestamp)context.get(E.thruDate.name()), (String)context.get(E.glFiscalTypeIdInput.name()), factorFieldNames));
    
        WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(dispatcher.getDelegator(), dispatcher);
        try {
			organizationId = workEffortFindServices.getOrganizationId((GenericValue) context.get(ServiceLogger.USER_LOGIN), false);
		} catch (GeneralException e) {
		}
    }

    /**
     * Ritorna risultati del calcolo degli indicatori
     * @return mappa contenente: 
     * <p>runResults con i messaggi di log
     * <p>recordElaborated
     * <p>warnMessages
     * <p>errorMessages
     */
    public Map<String, Object> getResult() {
        return res;
    }

    /**
     * Esegue calcolo indicatori, popola mappa coi risultati dell'elaborazione
     */
    public void execute() {

        String msg = "Start Elaboration Indicator Calculation";
        jLogger.printLogInfo(msg);

        try {
            checkAndRunValidaIndicatorList();
        } catch (Exception e) {
            msg = "Indicator Calc Service return the error below: ";
            jLogger.printLogError(e, msg);
            res = ServiceUtil.returnError(e.getMessage());
        }

        // always jLogger.getRecordElaborated() = 0, not manage
        msg = "Finished elaboration indicator calc with " + jLogger.getRecordElaborated() + " elaborated values and " + jLogger.getErrorMessages() + " errors";
        jLogger.printLogInfo(msg);

        res.put("runResults", jLogger.getMessages());
        res.put("recordElaborated", jLogger.getRecordElaborated());
        res.put("warnMessages", jLogger.getWarnMessages());
        res.put(JobLogger.ERROR_MESSAGES, jLogger.getErrorMessages());
    }

    private void checkAndRunValidaIndicatorList() throws GeneralException, EvalError {
        List<GenericValue> indicatorList = getValidIndicatorList((String)context.get(E.glAccountId.name()));
        Iterator<GenericValue> indicIt = indicatorList.iterator();
        // Navigate List of glAccountId
        while (indicIt.hasNext()) {
            GenericValue indicator = indicIt.next();
            runSingleIndicatorTransaction(indicator);
        }
    }

    /**
     * Run indicator in transaction
     * @param indicator
     * @throws Exception
     */
    private void runSingleIndicatorTransaction(final GenericValue indicator) throws GeneralException, EvalError {
        if (indicator == null) {
            String msg = "Indicator id is not a valid identifier ";
            jLogger.printLogError(msg);
        } else {
            new TransactionRunner(MODULE, true, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
                @Override
                public void run() throws Exception {
                    runSingleIndicatorElaboration(indicator);
                }
            }).execute().logError(jLogger);
        }
    }

    private void runSingleIndicatorElaboration(GenericValue indicator) throws GeneralException, EvalError {
        String msg = "Indicator Id = " + indicator.getString(E.glAccountId.name()) + ", Calculation method id = " + indicator.getString(E.calcCustomMethodId.name()) + ", inputEnumId = " + indicator.getString(E.inputEnumId.name()) + ", detectOrgUnitIdFlag = " + indicator.getString(E.detectOrgUnitIdFlag.name()) + ", detailEnumId = " + indicator.getString(E.detailEnumId.name());
        jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));

        List<GenericValue> inputCalcList = findInputCalcList(indicator);

        String glAccountId = indicator.getString(E.glAccountId.name());
        
        if (UtilValidate.isEmpty(inputCalcList)) {
            msg = "No Factor found for the indicator id " + glAccountId;
            jLogger.printLogInfo(msg, glAccountId);
        }
        String customMethodName = getCustomMethodName(glAccountId, indicator.getString(E.calcCustomMethodId.name()));
        if(UtilValidate.isNotEmpty(customMethodName)) {
            FactorCalculatorFiller fcf = factorCalculatorFillerMap.get(InputAndDetailValue.getEnumeration(indicator, customMethodName));
    
            // setGlfiscalType From indicator or context
            GlFiscalTypeOutputUtil glFiscalTypeOutputUtil = new GlFiscalTypeOutputUtil(delegator, context);
            String glFiscalTypeOutput = glFiscalTypeOutputUtil.getGlFiscalTypeIdOutput(indicator);
    
            fcf.setGlFiscalTypeIdOutput(glFiscalTypeOutput);
            fcf.setCustomMethodName(customMethodName);
    
            List<Map<String, Object>> extraParametersList = fcf.getExtraParametersList(glAccountId, context);
            // if there is not extraParameters, the list is {{}}, so elaborate indicator
            if (UtilValidate.isNotEmpty(extraParametersList)) {
                elaborateIndicator(indicator, fcf, inputCalcList, extraParametersList);
            } else {
                msg = "No Row to process for the indicator id " + glAccountId;
                jLogger.printLogInfo(msg, glAccountId);
            }
        }
    }

    private void elaborateIndicator(GenericValue indicator, FactorCalculatorFiller fcf, List<GenericValue> inputCalcList, List<Map<String, Object>> extraParametersList) throws EvalError, GeneralException {
        for (Map<String, Object> extraParameters : extraParametersList) {
            elaborateIndicatorWithParameter(indicator, fcf, inputCalcList, extraParameters);
        }
    }

    /**
     * Check wether valid customMethod and customMethodName
     * @param glAccountId
     * @param customMethodId
     * @return customMethodName or null
     * @throws GeneralException
     */
    private String getCustomMethodName(String glAccountId, String customMethodId) throws GeneralException {
        GenericValue customMethod = delegator.findOne("CustomMethod", UtilMisc.toMap("customMethodId", customMethodId), false);
        if(UtilValidate.isEmpty(customMethod)) {
            String msg = "Error while elaborating indicator with id " + glAccountId + ": customMethod for " + customMethodId + " is not valid";
            jLogger.printLogError(msg, glAccountId);
            return null;
        }
        String customMethodName = customMethod.getString(E.customMethodName.name());
        if(UtilValidate.isEmpty(customMethodName)) {
            String msg = "Error while elaborating indicator with id " + glAccountId + ": the expressione for customMethod " + customMethodId + " is empty";
            jLogger.printLogError(msg, glAccountId);
            return null;
        }
        return customMethodName;
    }

    /**
     * Return list of glAccount child (GlAccountInputCalc)
     * @param indicator
     * @return empty list or List of GlAccountInputCalc
     * @throws GeneralException
     */
    private List<GenericValue> findInputCalcList(GenericValue indicator) throws GeneralException {
        String msg = "";
        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(E.glAccountId.name(), indicator.getString(E.glAccountId.name())));
        cond.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountIdRef", EntityOperator.NOT_EQUAL, null)));
        List<GenericValue> inputCalcList = delegator.findList("GlAccountInputCalc", EntityCondition.makeCondition(cond), null, null, null, false);

        if (UtilValidate.isEmpty(inputCalcList)) {
            msg = "No Input Calc for the indicator id " + indicator.getString(E.glAccountId.name());
            jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));
        }

        return inputCalcList;
    }

    private void elaborateIndicatorWithParameter(GenericValue indicator, FactorCalculatorFiller fcf, List<GenericValue> inputCalcList, Map<String, Object> extraParameters) throws EvalError, GeneralException {
        String msg = "";
        boolean hasResult = false;
        String glAccountId = indicator.getString(E.glAccountId.name());
        fcf.initResultMap(factorFieldNames);
        if(UtilValidate.isEmpty(inputCalcList)) {
            factorMap = fcf.fillFactorMap(null, (String)context.get(E.glAccountId.name()), extraParameters);
            if (UtilValidate.isNotEmpty(factorMap)) {
                hasResult = true;
            }
        } else {
            boolean hasOneResult = false;
            // chiamo per tutti tranne AccinpUoFlagFiller, la cui inputCalcList e' vuota
            for (GenericValue inputCalc : inputCalcList) {
                hasOneResult = setFactorMap(fcf, inputCalc, extraParameters);
                
                if(hasOneResult) {
                    hasResult = true;
                }
            }
        }
        if (hasResult) {
            Map<String, Object> extraParametersToStore = fcf.getParametersToStore(glAccountId, extraParameters, organizationId);
            calculateAndStore(fcf.getCustomMethodName(), indicator, extraParametersToStore, fcf.getWriterValuesCondition(glAccountId, extraParameters));
        } else {
            msg = "For indicator " + glAccountId + " there is all input Calc with 0 row to process";
            jLogger.printLogInfo(msg, glAccountId);
        }

    }

    private boolean setFactorMap(FactorCalculatorFiller fcf, GenericValue inputCalc, Map<String, Object> extraParameters) throws GeneralException {
        boolean hasResult = false;
        String glAccountId = inputCalc.getString(E.factorCalculator.name());
        if (UtilValidate.isEmpty(inputCalc.getString(E.glAccountId.name()))) {
            throw new GenericServiceException("For glAccount id " + glAccountId + " factorCalculator is null");
        }

        factorMap = fcf.fillFactorMap(inputCalc, (String)context.get(E.glAccountId.name()), extraParameters);
        if (UtilValidate.isNotEmpty(factorMap)) {
            for (Entry<String, Map<String, Object>> entry: factorMap.entrySet()) {
                Map<String, Object> map = entry.getValue();
                if (UtilValidate.isNotEmpty(map)) {
                    hasResult = true;
                }
            }
        }

        return hasResult;
    }

    protected void calculateAndStore(String customMethodName, GenericValue indicator, Map<String, Object> extraParametersToStore, EntityCondition condition) throws EvalError, GeneralException {

        String customMethodCalculatorUtilBsh = "";
        try {
            customMethodCalculatorUtilBsh = CustomMethodCalculatorUtil.loadFileBsh(location);
        } catch (IOException e) {
            String msg = "Error reading file at location: " + location;
            jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));
        }

        int decimalScale = 0;

        GenericValue uomValue = delegator.findOne("Uom", UtilMisc.toMap("uomId", indicator.getString(E.defaultUomId.name())), false);
        if (UtilValidate.isNotEmpty(uomValue)) {
            decimalScale = Integer.parseInt(uomValue.getString(E.decimalScale.name()));
        }

        Iterator<String> factorMapIt = factorFieldNames.iterator();
        while (factorMapIt.hasNext()) {
            String factorFieldName = factorMapIt.next();
            Map<String, Object> factorCalculatorMap = getFactorCalculatorMap( indicator.getString(E.glAccountId.name()), factorFieldName, indicator.getString(E.calcCustomMethodId.name()), extraParametersToStore);
            
            Double valResultDouble = null;

            try {
                valResultDouble = (Double)BshUtil.eval(customMethodCalculatorUtilBsh + customMethodName, factorCalculatorMap);
            } catch (EvalError evalError) {
                String msg = "Error while elaborating expression  " + customMethodName + " with the following parameters : " + factorCalculatorMap;
                jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));
                throw evalError;
            }
            if(UtilValidate.isNotEmpty(valResultDouble)) {
                checkValueAndStore(setDecimalScale(valResultDouble, decimalScale), indicator, factorFieldName, extraParametersToStore, condition);
            }
        }
    }

    private Map<String, Object> getFactorCalculatorMap(String glAccountId, String factorFieldName, String calcCustomMethodId, Map<String, Object> extraParametersToStore) {
        Map<String, Object> factorCalculatorMap = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(factorMap)) {
            factorCalculatorMap = factorMap.get(factorFieldName);
        } else {
            String msg = "No value to process for the indicator id " + glAccountId;
            jLogger.printLogInfo(msg, glAccountId);
        }
        factorCalculatorMap.put("customMethodId", calcCustomMethodId);
        factorCalculatorMap.put("glAccountId", glAccountId);
        factorCalculatorMap.putAll(extraParametersToStore);

        return factorCalculatorMap;
    }

    private Double setDecimalScale(Double valResultDouble, int decimalScale) {

        if (!valResultDouble.equals(Double.NaN) && !valResultDouble.equals(Double.POSITIVE_INFINITY) && !valResultDouble.equals(Double.NEGATIVE_INFINITY)) {
            BigDecimal valResult = new BigDecimal(valResultDouble);
            return valResult.setScale(decimalScale, RoundingMode.HALF_UP).doubleValue();
        }

        return valResultDouble;
    }

    private void checkValueAndStore(Double valResult, GenericValue indicator, String factorFieldName, Map<String, Object> extraParametersToStore, EntityCondition condition) throws GeneralException {
        String msg = "";
        if (UtilValidate.isNotEmpty(valResult)) {
            msg = "The result for indicator id " + indicator.getString(E.glAccountId.name()) + " " + factorFieldName + " " + indicator.getString(E.calcCustomMethodId.name()) + " = " + valResult;
            jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));

            if (!valResult.equals(Double.NaN) && !valResult.equals(Double.POSITIVE_INFINITY) && !valResult.equals(Double.NEGATIVE_INFINITY)) {
                ValuesWriter vw = new ValuesWriter(dispatcher, context, jLogger);
                String acctgTransId = vw.storeValues(indicator, factorFieldName, valResult, extraParametersToStore, condition);
                msg = "Written result in AcctgTrans with acctgTransId = " + acctgTransId;
                jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));

            } else {
                logIndicatorCalcNanValResult(indicator, valResult, factorFieldName);
            }
        } else {
            msg = "It is not possible calcolate resul for the indicator id " + indicator.getString(E.glAccountId.name()) + " the value is null";
            jLogger.printLogInfo(msg, indicator.getString(E.glAccountId.name()));
        }

    }

    /**
     * Return glAccountId or a List of glAccountId with same prioCalc
     * @param glAccountId
     * @return List of glAccountId with one or more value
     * @throws Exception
     */
    private List<GenericValue> getValidIndicatorList(String glAccountId) throws GeneralException {
        List<GenericValue> indicatorList = FastList.newInstance();
        if (UtilValidate.isEmpty(glAccountId)) {
            indicatorList = getIndicatorList();
        } else {
            GenericValue indicator = getIndicator(glAccountId);
            indicatorList.add(indicator);
        }
        return indicatorList;
    }

    private GenericValue getIndicator(String glAccountId) throws GenericEntityException {
        String msg = "Search indicator with id = " + glAccountId;
        jLogger.printLogInfo(msg, glAccountId);
        return delegator.findOne("GlAccount", UtilMisc.toMap(E.glAccountId.name(), glAccountId), false);
    }

    private List<GenericValue> getIndicatorList() throws GeneralException {
        List<GenericValue> indicatorList = FastList.newInstance();
        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition("accountTypeEnumId", "INDICATOR"));
        cond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.calcCustomMethodId.name(), EntityOperator.NOT_EQUAL, null)));
        cond.add(EntityCondition.makeCondition("prioCalc", (Long)context.get("prioCalc")));
        cond.add(EntityCondition.makeCondition("organizationPartyId", organizationId));
        indicatorList = delegator.findList("GlAccountAndGlAccountOrganization", EntityCondition.makeCondition(cond), null, null, null, false);
        String msg = "Find  " + indicatorList.size() + " for condition " + EntityCondition.makeCondition(cond);
        jLogger.printLogInfo(msg);
        return indicatorList;
    }

    private void initFactorFieldNames() {
        factorFieldNames = FastList.newInstance();
        factorFieldNames.add(E.amount.name());
        factorFieldNames.add(E.origAmount.name());
    }

    /**
     * da un log di errore o di warning a seconda che sia nan il risultato del calcolo su amount o su origAmount
     * @param indicator
     * @param valResult
     * @param factorFieldName
     */
    private void logIndicatorCalcNanValResult(GenericValue indicator, Double valResult, String factorFieldName) {
        if (E.amount.name().equals(factorFieldName)) {
            String msg = "Error while elaborating indicator with id " + indicator.getString(E.glAccountId.name()) + " on field " + factorFieldName + ": " + valResult;
            jLogger.printLogError(msg, indicator.getString(E.glAccountId.name()));
        }
        if (E.origAmount.name().equals(factorFieldName)) {
            String msg = "The result of elaborating indicator with id " + indicator.getString(E.glAccountId.name()) + " on field " + factorFieldName + " is " + valResult;
            jLogger.printLogWarn(msg, indicator.getString(E.glAccountId.name()));
        }
    }
}
