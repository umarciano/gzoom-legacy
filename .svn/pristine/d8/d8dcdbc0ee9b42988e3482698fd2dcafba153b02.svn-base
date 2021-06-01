package com.mapsengineering.workeffortext.scorecard;

/**
 * Regole di conversione
 * 
 * @author sandro
 */
public enum ConversionRule {
    /**
     * Scostamento a valore 
     * Lo score e' la differenza tra target e valore rilevato, ovvero Score
     * = Target - Valore.
     */
    WECONVER_ABSOLUTEGAP,
    /**
     * Nessuna conversione
     * Il valore e' gia' lo score, ovvero Score = Valore
     */
    WECONVER_NOCONVERSIO,
    /**
     * Scostamento a percentuale
     * Lo score e' la differenza percentuale tra valore rilevato e target,
     * ovvero Score =( (Target - Valore) * 100 )/ Target
     */
    WECONVER_PERCENTGAP,
    /**
     * Avanzamento in Percentuale
     * Lo score e' l'avanzamento percentuale del valore rispetto al target
     * ovvero: Score = (Valore* 100) / Target
     */
    WECONVER_PERCENTWRK,
    /**
     * Coefficiente di Avanzamento
     * Lo score e' l'avanzamento del valore rispetto al target ovvero: Score
     * = Valore / Target
     */
    WECONVER_PROGRESWRK,
    /**
     * "Avanzamento % (bdg,cons.,cons.a.p.)"
     */
    WECONVER_PERCENTPY,
    /**
     * Avanzamento % con soglie
     */
    WECONVER_PERCLIMITS,
    /**
     * Avanzamento % 4 soglie
     */
    WECONVER_4PERCLIMITS,
    /**
     * Avanzamento % cons.a.p.
     */
    WECONVER_WRKCAP,
    /**
     * Scostamento % cons.a.p.
     */
    WECONVER_GAPCAP;
}
