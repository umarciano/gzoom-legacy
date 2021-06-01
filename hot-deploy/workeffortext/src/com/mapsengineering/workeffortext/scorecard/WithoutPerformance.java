package com.mapsengineering.workeffortext.scorecard;

/**
 * Flag prestazione
 * 
 * @author sandro
 */
public enum WithoutPerformance {
    WEWITHPERF_ERROR, WEWITHPERF_PERF_0, WEWITHPERF_SCORE_0, WEWITHPERF_NO_CALC;

    /**
     * Return default value = WEWITHPERF_PERF_0 
     * @return
     */
    public static WithoutPerformance getDefault() {
        return WithoutPerformance.WEWITHPERF_PERF_0;
    }
}
