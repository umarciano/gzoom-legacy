package com.mapsengineering.workeffortext.scorecard;

/**
 * Regole Fascia
 * 
 * @author sandro
 */
public enum RangeRule {
    /**
     * Il punteggio e' gia' lo score
     */
    WESCORE_ISVALUE,
    /**
     * il punteggio e' il valore associato alla fascia di valori in cui
     * rientra lo score.
     */
    WESCORE_DIRECTRANGE,
    /**
     * Il punteggio e' determinato dalla posizione dello score rispetto al
     * migliore e peggiore risultato rappresentati dalla fascia in cui
     * ricade. <br>
     * Nel dettaglio l'algoritmo dovra': <br>
     * 1) Determinare se la fascia e' positiva o meno <br>
     * 2) Se positiva viene calcolato: (valore- min ) * 100 / max - min <br>
     * 3) Se negativa viene calcolato: (max - valore ) * 100 / max - min
     */
    WESCORE_PRORATERANGE,
    /**
     * Il valore viene raffrontato alla fascia indicata nella misura <br>
     * (sara una fascia ad unico range) <br>
     * e se il valore risulta maggiore del valore massimo di fascia viene assunto il valore massimo di fascia come valore di punteggio,<br>
     * se il valore risulta minore del valore minimo di fascia viene assunto il valore minimo di fascia come valore di punteggio,<br>
     * se invece il valore e compreso nel range di fascia il valore del punteggio rimane uguale al valore.
     */
    WESCORE_MAXRANGE
}