package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.JobLogger;

/**
 * Nessuna conversione
 * Il valore e' gia' lo score, ovvero Score = Valore
 */
public class NoConversion extends ValueConverter {

    public static final String MODULE = NoConversion.class.getName();

    private JobLogger jLogger;

    /**
     * Constructor
     * @param delegator
     */
    public NoConversion(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountCode) throws Exception {
        return UtilValidate.isNotEmpty(currentKpi.get(E.actualValue.name())) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
    }

}
