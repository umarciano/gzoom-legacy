package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.JobLogger;

/**
 * Coefficiente di Avanzamento
 * Lo score e' l'avanzamento del valore rispetto al target ovvero: Score = Valore / Target
 */
public class ProgresWrkConverter extends ValueConverter {

    public static final String MODULE = ProgresWrkConverter.class.getName();

    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public ProgresWrkConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    /**
     * Convert
     * @param currentKpi
     * @param workEffortId
     * @param accountName
     * @return
     */
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountName) throws Exception {
        double result = 0d;
        
    	Map message = super.commonChecks(currentKpi, workEffortId, accountName);
        jLogger.addMessage(message);
        if(UtilValidate.isEmpty(message)) {
        	result = super.convertWrk(currentKpi, workEffortId, accountName, 1);
        }
        
        return result;
    	
    }

}
