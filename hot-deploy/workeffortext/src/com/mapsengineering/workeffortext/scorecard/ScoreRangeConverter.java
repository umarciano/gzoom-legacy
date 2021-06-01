package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.entity.Delegator;

/**
 * Score Range  
 *
 */
public abstract class ScoreRangeConverter extends BaseConverter {

    public static final String MODULE = ScoreRangeConverter.class.getName();
    
    /**
     * Constructor
     * @param delegator
     */
    public ScoreRangeConverter(Delegator delegator) {
        super(delegator);
    }
    
    /**
     * Convert
     * @param kpi
     * @param refValue
     * @return
     */
    public abstract double convert(Map<String, Object> kpi, double refValue);
    
}
