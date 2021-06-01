package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;


import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import com.mapsengineering.base.util.JobLogger;

/**
 * Avanzamento a percentuale cons.a.p.
 */
public class WrkCapConverter extends ValueConverter {

    public static final String MODULE = WrkCapConverter.class.getName();
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public WrkCapConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }
    
    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountName) throws Exception {
        
        double result = 0d;
        
    	Map message = super.commonChecks(currentKpi, workEffortId, accountName);
        jLogger.addMessage(message);
        if(UtilValidate.isEmpty(message)) {
        	double actualPy = (currentKpi.get(E.actualPyValue.name()) != null) ? (Double)currentKpi.get(E.actualPyValue.name()) : 0d;
            double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
            String debitCreditDefault = (String) currentKpi.get(E.debitCreditDefault.name());

        	if (actualPy == 0) {                 
        		return targetZeroValueDefault(debitCreditDefault, actual, number_100); 
            } else if (E.D.name().equals(currentKpi.get(E.debitCreditDefault.name())) || UtilValidate.isEmpty(currentKpi.get(E.debitCreditDefault.name()))) {
                result = (actual * ScoreCard.HUNDRED) / actualPy;
            } else {
                result = ((actual + (actual - actualPy)) * ScoreCard.HUNDRED) / actualPy;
            }
           
            return result;
        }
        
        return result;
    }

}
