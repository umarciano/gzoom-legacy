package com.mapsengineering.workeffortext.services.sequence;

import java.util.HashMap;
import java.util.Map;


import org.ofbiz.base.util.string.FlexibleStringExpander;

import com.mapsengineering.workeffortext.services.sequence.common.WorkEffortSequenceConstants;


public class WorkEffortFieldsGenerator {
	
	private InputParametersHandler inputParametersHandler;
	private String progressivo;
	private Map<String, Object> frameContextMap;
	private String prefisso;
	private String sourceReferenceId;
	private String etch;
	
	
    /**
     * 
     * @param inputParametersHandler
     */
	public WorkEffortFieldsGenerator(InputParametersHandler inputParametersHandler) {
		this.inputParametersHandler = inputParametersHandler;
		prepareContextMap();
		this.prefisso = FlexibleStringExpander.expandString(inputParametersHandler.getTemplPrefisso(), frameContextMap);
	}
	
	/**
	 * 
	 */
	public void run() {
		sourceReferenceId = FlexibleStringExpander.expandString(inputParametersHandler.getFrameCode(), frameContextMap);
		setEtch();
	}
	
	/**
	 * valorizzazione mappa di contesto dei componenti del formato codice
	 */
	private void prepareContextMap() {
		frameContextMap = new HashMap<String, Object>();
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_PF, inputParametersHandler.getCodePrefix());
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_AAAA, inputParametersHandler.getYearString());
        frameContextMap.put(WorkEffortSequenceConstants.FRAME_AA, inputParametersHandler.getYearString());
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_A2, inputParametersHandler.getYear2String());
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_UO, inputParametersHandler.getCodiceUO());
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_PR, inputParametersHandler.getCodicePAR());
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_ET, inputParametersHandler.getEtch());
	}
	
	/**
	 * valorizza etch
	 */
	private void setEtch() {
		if(inputParametersHandler.getFrameCode().contains(WorkEffortSequenceConstants.FRAME_ET)) {
			etch = inputParametersHandler.getEtch();
			return;
		}
		if (isEtchToReplace()) {
			etch = FlexibleStringExpander.expandString(inputParametersHandler.getTemplEtch(), frameContextMap);
			return;
		}
		etch = progressivo;
	}
		
	/**
	 * il replace dell'etichetta va fatto con formato PFAAUOCP o seqOnlyId = N,
	 * altrimenti etichetta = progressivo
	 * @return
	 */
	private boolean isEtchToReplace() {
		return WorkEffortSequenceConstants.VALUE_N.equalsIgnoreCase(inputParametersHandler.getSeqOnlyId()) 
				|| WorkEffortSequenceConstants.PFAAUOCP.equals(inputParametersHandler.getFrameEnumId());
	}
	
	/**
	 * il progressivo viene gestito a parte, quando si gestisce la WorkEffortSequence, quindi lo imposto da fuori
	 * @param progressivo
	 */
	public void setProgressivo(String progressivo) {
		this.progressivo = progressivo;
		frameContextMap.put(WorkEffortSequenceConstants.FRAME_PG, this.progressivo);
	}
	
	public String getPrefisso() {
		return prefisso;
	}
	
	public String getSourceReferenceId() {
		return sourceReferenceId;
	}

	public String getEtch() {
		return etch;
	}

}
