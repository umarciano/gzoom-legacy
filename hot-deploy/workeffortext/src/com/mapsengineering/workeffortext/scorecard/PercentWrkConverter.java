package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.JobLogger;

/**
 * Avanzamento in Percentuale
 * Lo score e' l'avanzamento percentuale del valore rispetto al target
 * ovvero: Score = (Valore* 100) / Target
 */
public class PercentWrkConverter extends ValueConverter {

    public static final String MODULE = PercentWrkConverter.class.getName();

    private JobLogger jLogger;

    /**
     * Constructor
     * @param delegator
     */
    public PercentWrkConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountName) throws Exception {
        
        double result = 0d;
        
    	Map message = super.commonChecks(currentKpi, workEffortId, accountName);
        jLogger.addMessage(message);
        if(UtilValidate.isEmpty(message)) {
        	result = super.convertWrk(currentKpi, workEffortId, accountName, ScoreCard.HUNDRED);
        }
        
        return result;
    }
    
    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

}
