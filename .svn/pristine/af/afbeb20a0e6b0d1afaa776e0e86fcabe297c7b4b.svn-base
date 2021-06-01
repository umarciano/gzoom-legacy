package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.JobLogger;

/**
 * Il punteggio e' gia' lo score
 */
public class IsValueRange extends ScoreRangeConverter {

    public static final String MODULE = DirectRange.class.getName();
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public IsValueRange(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    @Override
    public double convert(Map<String, Object> kpi, double refValue) {
        return refValue;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

}
