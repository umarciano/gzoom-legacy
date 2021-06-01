package com.mapsengineering.workeffortext.scorecard;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLoggedService;
import com.mapsengineering.base.util.JobLogger;

/**
 * Calculate kpi Value
 *
 */
public class KpiCalculator implements JobLoggedService {

    public static final String MODULE = KpiCalculator.class.getName();

    private Delegator delegator;

    private JobLogger jLogger;

    private Map<RangeRule, ScoreRangeConverter> weScoreRangeEnumIdMap;
    
    private Map<ConversionRule, ValueConverter> weScoreConvEnumIdMap;
    
    private boolean allowNegativeKPIResult;

    /**
     * Constructor
     * @param delegator
     */
    public KpiCalculator(Delegator delegator) {
        this.delegator = delegator;
        this.jLogger = new JobLogger(MODULE);
        this.allowNegativeKPIResult = "true".equals(UtilProperties.getPropertyValue("WorkeffortExtConfig", "ScoreCard.allowNegativeKPIResult", "false"));
        initWeScoreRangeEnumIdMap();
        initWeScoreConvEnumIdMap();
    }

    private void initWeScoreRangeEnumIdMap() {
        weScoreRangeEnumIdMap = FastMap.newInstance();
        weScoreRangeEnumIdMap.put(RangeRule.WESCORE_ISVALUE, new IsValueRange(delegator));
        weScoreRangeEnumIdMap.put(RangeRule.WESCORE_DIRECTRANGE, new DirectRange(delegator));
        weScoreRangeEnumIdMap.put(RangeRule.WESCORE_PRORATERANGE, new ProRateRange(delegator));
        weScoreRangeEnumIdMap.put(RangeRule.WESCORE_MAXRANGE, new MaxRange(delegator));
    }
    
    private void initWeScoreConvEnumIdMap() {
        weScoreConvEnumIdMap = FastMap.newInstance();
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_ABSOLUTEGAP, new AbsoluteGapConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_NOCONVERSIO, new NoConversion(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_PERCENTGAP, new PercentGapConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_PERCENTWRK, new PercentWrkConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_PROGRESWRK, new ProgresWrkConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_PERCENTPY, new PercentPyWrkConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_PERCLIMITS, new PercLimitsConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_4PERCLIMITS, new PercFourThresholdsLimitsConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_WRKCAP, new WrkCapConverter(delegator));
        weScoreConvEnumIdMap.put(ConversionRule.WECONVER_GAPCAP, new GapCapConverter(delegator));
    }

    /**
     * Applicazione regole di conversione
     * 
     * @param currentKpi
     * @return valore da sommare
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws NoSuchMethodException 
     */
    protected Map<String, Double> calculateKpiValue(Map<String, Object> currentKpi) throws Exception {

        Map<String, Double> toReturn = new FastMap<String, Double>();

        String weScoreConvEnumId = (String)currentKpi.get(E.weScoreConvEnumId.name());
        String weScoreRangeEnumId = (String)currentKpi.get(E.weScoreRangeEnumId.name());
        String workEffortId = (String)currentKpi.get(E.workEffortId.name());
        String workEffortMeasureId = (String)currentKpi.get(E.workEffortMeasureId.name());

        GenericValue wrk = null;
        GenericValue wem = null;
        String wrkDesc = "";
        String sourceReferenceId = "";
        String accountCode = "";
        try {
            wrk = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
            wrkDesc = workEffortId + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
            sourceReferenceId = wrk.getString(E.sourceReferenceId.name());
            
            wem = delegator.findOne(E.WorkEffortMeasureAndGlAccountView.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId), false);
            accountCode = wem.getString(E.accountCode.name());
            
        } catch (Exception e) {
            Debug.log(e); 
        }
        jLogger.addMessage(ServiceLogger.makeLogInfo("Applying conversion for current kpi \"" + wrkDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));

        // Estrazione e controllo valore actual
        double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
        jLogger.addMessage(ServiceLogger.makeLogInfo("actual " + actual, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
        if (actual == 0) {

            jLogger.addMessage(ServiceLogger.makeLogInfo("Actual value for kpi not exist", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));

            // Controllo conteggio actual
            double actualCount = (currentKpi.get(E.actualCount.name()) != null) ? (Double)currentKpi.get(E.actualCount.name()) : 0d;
            // Applicazione regole se mancanza prestazione
            if (actualCount == 0d) {

                // Controllo flag per mancanza prestazione
                String weWithoutPerf = (String)currentKpi.get(E.weWithoutPerf.name());
                if (weWithoutPerf == null) {
                    // Applico il default
                    weWithoutPerf = WithoutPerformance.getDefault().toString();
                    jLogger.addMessage(ServiceLogger.makeLogInfo("\"Performance\" not exist in tab, applying default", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
                }

                // WE.D.3.1.2
                switch (WithoutPerformance.valueOf(weWithoutPerf)) {
                // il case WEWITHPERF_NO_CALC non e' gestito perche' non arriva mai a questo punto, avendo sempre un actual diverso da null
                case WEWITHPERF_ERROR:
                    // Errore bloccante segnalo ed esco
                    jLogger.addMessage(ServiceLogger.makeLogError("Performance equal to 0, fatal error", "005", sourceReferenceId, accountCode, null));
                    toReturn.put(ScoreCard.KPI_VALUE, 0d);
                    return toReturn;
                case WEWITHPERF_PERF_0:
                    // Forzatura consuntivo a 0, continuo nel calcolo
                    jLogger.addMessage(ServiceLogger.makeLogWarn("Actual forced to 0", "009", sourceReferenceId, accountCode, null));
                    actual = 0d;
                    break;
                case WEWITHPERF_SCORE_0:
                    // Forzatura punteggio a 0, quindi interrompo il calcolo e esco
                    jLogger.addMessage(ServiceLogger.makeLogWarn("Score forced to 0", "010", sourceReferenceId, accountCode, null));
                    toReturn.put(ScoreCard.KPI_VALUE, 0d);
                    return toReturn;
                }
            }
        }

        //
        // Applico regole su punteggio
        double result = 0d;

        // Bug 3865
        // WE.D.3.1.2.1, conversione valore
        ValueConverter valueConverter = weScoreConvEnumIdMap.get(ConversionRule.valueOf(weScoreConvEnumId)); 
        result = valueConverter.convert(currentKpi, workEffortId, accountCode);
        jLogger.mergeData(valueConverter.getJobLogger());       
        
        toReturn.put(ScoreCard.PERF_AMOUNT_CALC, result);

        // WE.D.3.1.2.2 Calcolo Score
        // Controllo tipo valore, regola punteggio

        ScoreRangeConverter scoreRangeConverter = weScoreRangeEnumIdMap.get(RangeRule.valueOf(weScoreRangeEnumId));
        result = scoreRangeConverter.convert(currentKpi, result);
        jLogger.mergeData(scoreRangeConverter.getJobLogger());

        // TODO: Controllo fuori fascia

        // Controlli finali sul risultato
        if (result < 0) {
            if (allowNegativeKPIResult) {
                jLogger.addMessage(ServiceLogger.makeLogWarn("Warning, score < 0", "007", sourceReferenceId, accountCode, null));
            } else {
                jLogger.addMessage(ServiceLogger.makeLogError("Error, score < 0, forcing to 0", "007", sourceReferenceId, accountCode, null));
                result = 0d;
            }
        }
        toReturn.put(ScoreCard.KPI_VALUE, result);
        return toReturn;
    }

    @Override
    /**
     * Return jobLogger
     */
    public JobLogger getJobLogger() {
        return jLogger;
    }

}
