package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;


/**
 * "Avanzamento % (bdg,cons.,cons.a.p.)"
 */
public class PercentPyWrkConverter extends ValueConverter {

    public static final String MODULE = PercentPyWrkConverter.class.getName();

    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public PercentPyWrkConverter(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    @Override
    public double convert(Map<String, Object> currentKpi, String workEffortId, String accountCode) throws Exception {
        double result = 0d;

        boolean targetExist = (currentKpi.get(E.targetValue.name()) != null);
        boolean actualPyExists = (currentKpi.get(E.actualPyValue.name()) != null);
        double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
        double actualPy = (currentKpi.get(E.actualPyValue.name()) != null) ? (Double)currentKpi.get(E.actualPyValue.name()) : 0d;
        double target = (currentKpi.get(E.targetValue.name()) != null) ? (Double)currentKpi.get(E.targetValue.name()) : 0d;
        GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
        String wrkDesc = workEffortId + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
        String sourceReferenceId = wrk.getString(E.sourceReferenceId.name());
        boolean isTargetZero = (target == 0d);
        String debitCreditDefault = (String) currentKpi.get(E.debitCreditDefault.name());
        
        //Bug 13
        if (!actualPyExists) {
            actualPy = target;
        }

        if (E.D.name().equals(currentKpi.get(E.debitCreditDefault.name())) || UtilValidate.isEmpty(currentKpi.get(E.debitCreditDefault.name()))) {
            if (actual >= target) {
                if (!targetExist) {
                    String msg = String.format(ErrorMessages.TARGET_NOT_EXISTS, wrkDesc);
                    jLogger.addMessage(ServiceLogger.makeLogError(msg, "018", sourceReferenceId, accountCode, null));
                    return 0d;
                }
                if (isTargetZero) {
                	    	 
                	return super.targetZeroValueDefault(debitCreditDefault, actual, number_100);
                	
                }
                result = number_100;
            } else {
                if (actual <= actualPy) {
                    result = 0d;
                } else {
                    result = (actual - actualPy) * number_100 / (target - actualPy);
                }
            }
        } else {
            if (actual <= target) {
                if (currentKpi.get("actualValue") == null) {
                    String msg = String.format(ErrorMessages.ACTUAL_NOT_EXISTS, wrkDesc);
                    jLogger.addMessage(ServiceLogger.makeLogError(msg, "018", sourceReferenceId, accountCode, null));
                    return 0d;
                }
                if (actual == 0d) {
                    String msg = String.format(ErrorMessages.ACTUAL_ZERO, wrkDesc);
                    jLogger.addMessage(ServiceLogger.makeLogError(msg, MessageCode.ERROR_NEXTCARD.toString()));
                    return 0d;
                }
                result = number_100;
            } else {
                if (actual >= actualPy) {
                    result = 0d;
                } else {
                    result = (1 - (actual - target)/(actualPy - target)) * number_100;
                }
            }
        }

        return result;
    }

}
