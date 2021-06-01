package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * Scostamento a percentuale cons.a.p.
 */
public class GapCapConverter extends ValueConverter {

    public static final String MODULE = GapCapConverter.class.getName();
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public GapCapConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }
    
    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountCode) throws Exception {
    	double actualPy = (currentKpi.get(E.actualPyValue.name()) != null) ? (Double)currentKpi.get(E.actualPyValue.name()) : 0d;
        double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
        String debitCreditDefault = (String) currentKpi.get(E.debitCreditDefault.name());
        if (actualPy == 0) {                	     	
        	return super.targetZeroValueDefault(debitCreditDefault, actualPy, number_100);     	
        }
        return ((actual - actualPy) * ScoreCard.HUNDRED) / actualPy;
    }

}
