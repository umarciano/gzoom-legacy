package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * Avanzamento % con soglie
 */
public class PercLimitsConverter extends ValueConverter {
	
    public static final String MODULE = PercLimitsConverter.class.getName();

    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public PercLimitsConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    @Override
	public JobLogger getJobLogger() {
		return jLogger;
	}

	@Override
	public double convert(Map<String, Object> currentKpi, String workEffortId, String accountName) throws Exception {
        double actual = getValue(currentKpi, E.actualValue.name());
        double target = getValue(currentKpi, E.targetValue.name());
        double limitMax = getValue(currentKpi, E.limitMaxValue.name());
        double limitMin = getValue(currentKpi, E.limitMinValue.name());
                
        //migliora con valori piu alti
        if (limitMin <= target && target <= limitMax) {
        	if (actual < limitMin) {
        		return -1d;
        	}
        	if (actual < target) {
        		return ((actual - limitMin) * ScoreCard.HUNDRED) / (target - limitMin);
        	}
        	if (actual < limitMax) {
        		return (((actual - target) * ScoreCard.HUNDRED) / (limitMax - target)) + ScoreCard.HUNDRED;
        	}
        	return 201d;
        }
        //migliora con valori piu bassi
        if (limitMin >= target && target >= limitMax) {
        	if (actual > limitMin) {
        		return -1d;
        	}
        	if (actual > target) {
        		return ((limitMin - actual) * ScoreCard.HUNDRED) / (limitMin - target);
        	}
        	if (actual > limitMax) {
        		return (((target - actual) * ScoreCard.HUNDRED) / (target - limitMax)) + ScoreCard.HUNDRED;
        	}
        	return 201d;
        }
        //errore impostazione soglia
        GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
        String wrkDesc = workEffortId + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
        String msg = String.format("Found error for workEffort %s in setting limitMin %s, target %s, limitMax %s", wrkDesc, limitMin, target, limitMax);
        jLogger.addMessage(ServiceLogger.makeLogError(msg, "019", workEffortId, accountName, null));
        return 0d;       
	}

}
