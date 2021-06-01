package com.mapsengineering.workeffortext.scorecard;

/**
 * Periodo valore target
 * 
 */
public enum TargetPeriod {
    /** Target periodo di lancio : Cerca Target al periodo di lancio */
    TARGET_EXEC_PERIOD,
    
    /** Target periodo padre: in base al periodo attivo sulla scheda, *
     *  cerca il relativo periodo padre e quindi ricerca il valore alla data fine periodo padre;
     *   in caso di assenza periodo su scheda o di assenza periodo padre sul periodo della scheda 
     *   cerca il target al periodo di lancio */
    TARGET_PARENT_PERIOD,
    
    /** Periodo padre se target alla data,*
     *  se non c'e' target alla data di lancio no neffettua il calcolo */
    TARGET_PARENT_EXEC; 
    
    /**
     * Return default value = WEWITHPERF_PERF_0 
     * @return
     */
    public static boolean isTargetParent(String value) {
        return TARGET_PARENT_PERIOD.name().equals(value) || TARGET_PARENT_EXEC.name().equals(value);
    }
    
}
