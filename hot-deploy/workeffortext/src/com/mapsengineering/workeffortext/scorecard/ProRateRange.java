package com.mapsengineering.workeffortext.scorecard;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * Il punteggio e' determinato dalla posizione dello score rispetto al
 * migliore e peggiore risultato rappresentati dalla fascia in cui
 * ricade. <br>
 * Nel dettaglio l'algoritmo dovra': <br>
 * 1) Determinare se la fascia e' positiva o meno <br>
 * 2) Se positiva viene calcolato: (valore- min ) * 100 / max - min <br>
 * 3) Se negativa viene calcolato: (max - valore ) * 100 / max - min
 */
public class ProRateRange extends ScoreRangeConverter {

    public static final String MODULE = ProRateRange.class.getName();

    private JobLogger jLogger;

    /**
     * Constructor
     * @param delegator
     */
    public ProRateRange(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    /**
     * Applica al punteggio la regola ProRateRange
     * 
     * @param kpi
     * @return value
     */
    @Override
    public double convert(Map<String, Object> kpi, double refValue) {

        GenericValue enumeration = null;
        GenericValue uomRange = null;
        String sourceReferenceId = "";
        String accountCode = "";
        try {
        	
        	GenericValue glAcc = getDelegator().findOne(E.WorkEffortMeasureAndGlAccountView.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), (String)kpi.get(E.workEffortMeasureId.name())), false);
        	accountCode = glAcc.getString(E.accountCode.name());
        	GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), (String)kpi.get(E.workEffortId.name())), false);
        	sourceReferenceId = wrk.getString(E.sourceReferenceId.name());
            
            enumeration = getDelegator().findOne("Enumeration", UtilMisc.toMap("enumId", RangeRule.WESCORE_PRORATERANGE.toString()), true);
            String uomRangeId = (String)kpi.get(E.weMeasureUomId.name());
            uomRange = getDelegator().findOne("UomRange", UtilMisc.toMap(E.uomRangeId.name(), uomRangeId), false);

            EntityCondition condList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(E.uomRangeId.name(), uomRangeId), EntityCondition.makeCondition("fromValue", EntityOperator.LESS_THAN_EQUAL_TO, refValue), EntityCondition.makeCondition(E.thruValue.name(), EntityOperator.GREATER_THAN_EQUAL_TO, refValue));

            List<GenericValue> values = getDelegator().findList(E.UomRangeValues.name(), condList, null, null, null, false);

            if (UtilValidate.isEmpty(values)) {
                String msg = "Uom range values not found for \"" + enumeration.getString(E.description.name()) + "\" and value " + refValue;
                jLogger.addMessage(ServiceLogger.makeLogError(msg, "011", (String)kpi.get(E.workEffortId.name()), (String)kpi.get(E.workEffortMeasureId.name()), null));
                return 0d;
            }

            GenericValue uomRangeValue = values.get(0);

            // Determino se la fascia e' positiva o meno
            boolean isPositive = ("Y".equalsIgnoreCase(uomRangeValue.getString("isPositive")));

            double coefficient = UtilValidate.isNotEmpty(uomRangeValue.get(E.rangeValuesFactor.name())) ? uomRangeValue.getDouble(E.rangeValuesFactor.name()) : ScoreCard.HUNDRED;
            double coefficientMin = UtilValidate.isNotEmpty(uomRangeValue.get(E.rangeValuesFactorMin.name())) ? uomRangeValue.getDouble(E.rangeValuesFactorMin.name()) : ScoreCard.ZEROD;

            // Calcolo
            double min = uomRangeValue.getDouble("fromValue");
            double max = uomRangeValue.getDouble(E.thruValue.name());

            if ("Y".equalsIgnoreCase(uomRangeValue.getString("prorateRange"))) {
            	double devRefValue = isPositive ? (refValue - min) : (max - refValue);
            	return coefficientMin + (devRefValue * (coefficient - coefficientMin)) / (max - min);
            } 
            return uomRangeValue.getDouble("rangeValuesFactor");

        } catch (GenericEntityException e) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while search uom range values for \"%s\": %s", uomRange != null ? uomRange.getString(E.description.name()) : null, e.getMessage()), "012", sourceReferenceId, accountCode, null));
        }

        return refValue;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }
}
