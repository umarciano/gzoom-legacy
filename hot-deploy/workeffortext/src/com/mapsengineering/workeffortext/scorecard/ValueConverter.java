package com.mapsengineering.workeffortext.scorecard;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;

/**
 * Extend BaseConverter
 *
 */
public abstract class ValueConverter extends BaseConverter {
    
    public static final String MODULE = ValueConverter.class.getName();
    public double number_100 = 100d;
    
    /**
     * Constructor
     * @param delegator
     */
    public ValueConverter(Delegator delegator) {
        super(delegator);
    }
    
    /**
     * Conversion
     * @param currentKpi
     * @param workEffortId
     * @param accountName
     * @return
     * @throws Exception
     */
    public abstract double convert(Map<String, Object> currentKpi, String workEffortId, String accountName) throws Exception;
    
    /**
     * Controllo se il target esiste altrimenti ritorno un errore
     * @param currentKpi
     * @param workEffortId
     * @param accountName
     * @return
     * @throws GenericEntityException
     */
    public Map commonChecks(Map<String, Object> currentKpi, String workEffortId, String accountName) throws GenericEntityException {
        Map result = FastMap.newInstance();
        
        boolean targetExist = (currentKpi.get(E.targetValue.name()) != null);
        GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
        String wrkDesc = workEffortId + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
        String sourceReferenceId = wrk.getString(E.sourceReferenceId.name());
        
        if (!targetExist) {
            String msg = String.format(ErrorMessages.TARGET_NOT_EXISTS, wrkDesc);
            result.putAll(ServiceLogger.makeLogError(msg, "018", sourceReferenceId, accountName, null));
        }
        return result;
    }
    
    /**
     * GN-225: Inserimento di un default quando il target e uguale a zero
     * @param debitCreditDefault
     * @param actual
     * @param defaultValue
     * @return
     */
    public double targetZeroValueDefault(String debitCreditDefault, double actual, double defaultValue){
    	
    	double value = 0d;
    	if((debitCreditDefault.equals(E.D.name()) && actual >= 0) || (debitCreditDefault.equals(E.C.name()) && actual <= 0)){
    		value = defaultValue;    		
    	}
    	return value;    	
    }
    
    
    /**
     * Convert 
     * @param currentKpi
     * @param workEffortId
     * @param accountName
     * @param HUNDRED
     * @return
     */
    public double convertWrk(Map<String, Object> currentKpi, String workEffortId, String accountName, int HUNDRED) {
        double result = 0d;
        double actual = (currentKpi.get(E.actualValue.name()) != null) ? (Double)currentKpi.get(E.actualValue.name()) : 0d;
        double target = (currentKpi.get(E.targetValue.name()) != null) ? (Double)currentKpi.get(E.targetValue.name()) : 0d;
        boolean isTargetZero = (target == 0d);
        String debitCreditDefault = (String) currentKpi.get(E.debitCreditDefault.name());

    	 if (isTargetZero) {                 
    		 return targetZeroValueDefault(debitCreditDefault, actual, number_100); 
         } else if (E.D.name().equals(currentKpi.get(E.debitCreditDefault.name())) || UtilValidate.isEmpty(currentKpi.get(E.debitCreditDefault.name()))) {
            result = (actual * HUNDRED) / target;
        } else {
            result = ((target + (target - actual)) * HUNDRED) / target;
        }
       
        return result;
    }
    
    /**
     * ritorna il valore se presente, altrimenti 0
     * @param currentKpi
     * @param fieldName
     * @return
     */
    protected double getValue(Map<String, Object> currentKpi, String fieldName) {
    	return (currentKpi.get(fieldName) != null) ? (Double)currentKpi.get(fieldName) : 0d;
    }    
    
}
