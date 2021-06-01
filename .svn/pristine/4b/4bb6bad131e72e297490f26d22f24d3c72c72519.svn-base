package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * Scostamento a valore 
 * Lo score e' la differenza tra target e valore rilevato, ovvero Score
 * = Target - Valore.
 */
public class AbsoluteGapConverter extends ValueConverter {

    public static final String MODULE = AbsoluteGapConverter.class.getName();
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public AbsoluteGapConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }
    
    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountCode) throws Exception  {
        boolean targetExist = (currentKpi.get(E.targetValue.name()) != null);
        double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
        double target = (currentKpi.get(E.targetValue.name()) != null) ? (Double)currentKpi.get(E.targetValue.name()) : 0d;
        
        if (!targetExist) {
            GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
            String wrkDesc = workEffortId + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
            String msg = String.format("Target value does not exist for workEffort %s, next.", wrkDesc);
            jLogger.addMessage(ServiceLogger.makeLogError(msg, "013", wrk.getString(E.sourceReferenceId.name()), accountCode, null));
            return 0d;
        }
        return actual - target;
    }

}
