package com.mapsengineering.workeffortext.services.sequence;


import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.sequence.common.WorkEffortSequenceConstants;

public class WorkEffortSequenceHelper {
	
	private Delegator delegator;
	private GenericValue workEffortType;
	private int MAX_RETRY = 10;
	
	private String prefisso;
	private String progressivo;
	
		
	/**
	 * 
	 * @param delegator
	 * @param workEffortType
	 * @param prefisso
	 */
	public WorkEffortSequenceHelper(Delegator delegator, GenericValue workEffortType, String prefisso) {
		this.delegator = delegator;
		this.workEffortType = workEffortType;
		this.prefisso = prefisso;
	}
	
	/**
	 * inserisce il WorkEffortSequence: se dopo 10 tentativi non trova il progressivo da errore
	 * set ProgAttuale = select seq_id from work_effort_sequence where seq_name = Prefisso
     * se non trovato 
     * set ProgAttuale = 0 
     * insert into work_effort_sequence (seq_name, seq_id) values (Prefisso, 0) )
     * set Progressivo = ProgAttuale + 1 (con zeri riempitivi a sinistra fino ad arrivare al numero di caratteri work_effort_type.seq_digit)
     * se record non trovato ripartire da b. (ripetere max dieci volte poi Errore "Codice occupato. Riprovare.")
	 * se record trovato insert work_effort
	 * @throws GeneralException
	 */
	public void insertWorkEffortSequence() throws GeneralException {
    	for (int i = 0; i < MAX_RETRY; i ++) {
    		GenericValue workEffortSequence = delegator.findOne(E.WorkEffortSequence.name(), UtilMisc.toMap(E.seqName.name(), prefisso), false);         	
    		Long progrAttuale = getActualSeq(workEffortSequence); 
        	progressivo = getProgr(progrAttuale);        	
        	
        	if (!WorkEffortSequenceConstants.PFAAUOCP.equals(workEffortType.getString(E.frameEnumId.name()))) {
	        	workEffortSequence = EntityUtil.getOnly(delegator.findByAnd(E.WorkEffortSequence.name(), UtilMisc.toMap(E.seqName.name(), prefisso, E.seqId.name(), progrAttuale)));
	        	if (UtilValidate.isEmpty(workEffortSequence)) {	        		
	        		continue;
	        	}
	        	progrAttuale =  progrAttuale + 1L;
	        	workEffortSequence.set(E.seqId.name(), progrAttuale);
	        	delegator.store(workEffortSequence);
        	}
        	return;
    	}
    	throw new GeneralException("Code already used. Try again.");
	}
	
	/**
	 * Find actual sequence by workEffortSequence
	 * @param workEffortSequence
	 * @return
	 * @throws GeneralException
	 */
	private Long getActualSeq(GenericValue workEffortSequence) throws GeneralException {		
		Long progrAttuale = 0L;
		
		if (!WorkEffortSequenceConstants.PFAAUOCP.equals(workEffortType.getString(E.frameEnumId.name()))) {
    	    if (UtilValidate.isEmpty(workEffortSequence)) {    		
    		    GenericValue newSeq = delegator.makeValue(E.WorkEffortSequence.name());
    		    newSeq.set(E.seqId.name(), progrAttuale);
    		    newSeq.set(E.seqName.name(), prefisso);
    		    delegator.create(newSeq);
    	    } else {    		
    		    progrAttuale = workEffortSequence.getLong(E.seqId.name());
    	    }
		}    	
    	return progrAttuale;		
	}
	
	/**
	 * Find progressive by workEffortType
	 * @param progrAttuale
	 * @return
	 */
	private String getProgr(Long progrAttuale) {		
		String progr = "";
    	Long seqDigit = workEffortType.getLong(E.seqDigit.name());
    	if (!WorkEffortSequenceConstants.PFAAUOCP.equals(workEffortType.getString(E.frameEnumId.name()))) {
        	if (seqDigit == null || seqDigit.intValue() == 0) {        		
            	progr = String.format("%01d", progrAttuale + 1);
            	// La formattazione a 0 caratteri darebbe problemi
        	} else {       		
        		progr = String.format("%0" + seqDigit.intValue() + "d", progrAttuale + 1);
        	}
    	}
		return progr;
	}
	
	
	
	/**
	 * resetta il workEffortSequence
	 * @throws GeneralException
	 */
	public void resetWorkEffortSequence() throws GeneralException {
		GenericValue workEffortSequence = delegator.findOne(E.WorkEffortSequence.name(), UtilMisc.toMap(E.seqName.name(), prefisso), false);  
		if (UtilValidate.isNotEmpty(workEffortSequence)) {
			workEffortSequence.set(E.seqId.name(), 0L);
			delegator.store(workEffortSequence);
		}
    	return;
	}

	public String getPogressivo() {
		return progressivo;
	}
	
	public String getPrefisso() {
		return prefisso;
	}

}
