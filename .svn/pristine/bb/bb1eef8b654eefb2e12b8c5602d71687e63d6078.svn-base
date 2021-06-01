package com.mapsengineering.workeffortext.scorecard;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * il punteggio e' il valore associato alla fascia di valori in cui
 * rientra lo score.
 */
public class DirectRange extends ScoreRangeConverter {

    public static final String MODULE = DirectRange.class.getName();
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public DirectRange(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    /**
     * Applica al punteggio la regola DirectRange
     * 
     * @param kpi
     * @return value
     */
    @Override
    public double convert(Map<String, Object> kpi, double refValue) {

        String uomRangeId = (String)kpi.get(E.weMeasureUomId.name());
        GenericValue uomRange = null;
        String sourceReferenceId = "";
        String accountCode = "";
        
        try {
        	
        	GenericValue glAcc = getDelegator().findOne(E.WorkEffortMeasureAndGlAccountView.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), (String)kpi.get(E.workEffortMeasureId.name())), false);
        	accountCode = glAcc.getString(E.accountCode.name());
        	GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), (String)kpi.get(E.workEffortId.name())), false);
        	sourceReferenceId = wrk.getString(E.sourceReferenceId.name());
        	
            uomRange = getDelegator().findOne("UomRange", UtilMisc.toMap(E.uomRangeId.name(), uomRangeId), false);
            List<GenericValue> values = getDelegator().findByAnd(E.UomRangeValues.name(), UtilMisc.toMap(E.uomRangeId.name(), uomRangeId), UtilMisc.toList(E.uomRangeValuesId.name()));

            for (GenericValue value : values) {
                double from = value.getDouble("fromValue");
                double thru = value.getDouble(E.thruValue.name());
                jLogger.addMessage(ServiceLogger.makeLogInfo(refValue + "refValue" + " from " + from + " - thru " + thru, MessageCode.INFO_GENERIC.toString(), (String)kpi.get(E.workEffortId.name()), (String)kpi.get(E.workEffortMeasureId.name()), null));
                if (refValue >= from && refValue <= thru) {
                    return value.getDouble(E.rangeValuesFactor.name());
                }
            }

            String msg = "For uom range \"" + uomRange + "\" and value " + refValue + ", it is not possible apply the conversion";
            jLogger.addMessage(ServiceLogger.makeLogError(msg, "010", (String)kpi.get(E.workEffortId.name()), (String)kpi.get(E.workEffortMeasureId.name()), null));
        } catch (GenericEntityException e) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while search uom range values for uom range \"" + uomRange + "\": %s", e.getMessage()), "012", sourceReferenceId, accountCode, null));
        }

        return refValue;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }
}
