package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * Scostamento a percentuale
 * Lo score e' la differenza percentuale tra valore rilevato e target,
 * ovvero Score =( (Target - Valore) * 100 )/ Target
 */
public class PercentGapConverter extends ValueConverter {

    public static final String MODULE = PercentGapConverter.class.getName();
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public PercentGapConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }
    
    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountCode) throws Exception {
        double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
        double target = (currentKpi.get(E.targetValue.name()) != null) ? (Double)currentKpi.get(E.targetValue.name()) : 0d;
        boolean targetExist = (currentKpi.get(E.targetValue.name()) != null);
        boolean isTargetZero = (target == 0d);
        String debitCreditDefault = (String) currentKpi.get(E.debitCreditDefault.name());
        
        GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
        String wrkDesc = workEffortId + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
        if (!targetExist) {
            String msg = String.format("Target value does not exist for workEffort %s , next.", wrkDesc);
            jLogger.addMessage(ServiceLogger.makeLogError(msg, "014", workEffortId, accountCode, null));
            return 0d;
        }
        if (isTargetZero) {           
        	     	 
        	return super.targetZeroValueDefault(debitCreditDefault, actual, number_100);
        	
        }
        return ((actual - target) * ScoreCard.HUNDRED) / target;
    }

}
