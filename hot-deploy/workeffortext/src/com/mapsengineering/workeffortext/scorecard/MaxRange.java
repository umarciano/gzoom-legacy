package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.scorecard.helper.UomRangeValuesHelper;

/**
 * Il valore viene raffrontato alla fascia indicata nella misura <br>
 * (sara una fascia ad unico range) <br>
 * e se il valore risulta maggiore del valore massimo di fascia viene assunto il valore massimo di fascia come valore di punteggio,<br>
 * se il valore risulta minore del valore minimo di fascia viene assunto il valore minimo di fascia come valore di punteggio,<br>
 * se invece il valore e compreso nel range di fascia il valore del punteggio rimane uguale al valore.
 */
public class MaxRange extends ScoreRangeConverter {

    public static final String MODULE = MaxRange.class.getName();

    private JobLogger jLogger;

    /**
     * Constructor
     * @param delegator
     */
    public MaxRange(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    /**
     * Applica al punteggio la regola MaxRange
     * 
     * @param kpi
     * @param refValue
     * @return value
     */
    @Override
    public double convert(Map<String, Object> kpi, double refValue) {
        double returnValue = refValue;
        String uomRangeId = (String)kpi.get(E.weMeasureUomId.name());
        String workEffortMeasureId = (String)kpi.get(E.workEffortMeasureId.name());
        String workEffortId = (String)kpi.get(E.workEffortId.name());

        double fromValue = getLimitValue(ScoreCard.ZEROD, uomRangeId, E.fromValue.name(), workEffortMeasureId, workEffortId);
        double thruValue = getLimitValue(ScoreCard.HUNDREDD, uomRangeId, E.thruValue.name(), workEffortMeasureId, workEffortId);

        if (refValue > thruValue) {
            returnValue = thruValue;
        } else if (refValue < fromValue) {
            returnValue = fromValue;
        }
        return returnValue;
    }

    /** Return the valueLimit from UomRangeValues with uomRangeId */
    private double getLimitValue(double limit, String uomRangeId, String fieldName, String workEffortMeasureId, String workEffortId) {
        double value = limit;
        UomRangeValuesHelper uomRangeValuesHelper = new UomRangeValuesHelper(getDelegator());
        Double uomRangeValue = uomRangeValuesHelper.searchUomRangeAndValues(uomRangeId, fieldName, workEffortMeasureId, workEffortId, uomRangeValuesHelper.getOrderBy(fieldName));
        if (UtilValidate.isNotEmpty(uomRangeValue)) {
            value = uomRangeValue.doubleValue();
        }
        jLogger.mergeData(uomRangeValuesHelper.getJobLogger());
        return value;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

}
