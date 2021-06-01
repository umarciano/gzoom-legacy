package com.mapsengineering.workeffortext.scorecard;

import java.util.Comparator;
import java.util.Map;

/**
 * Measure Kpi Comparator: workEffortMeasureId, glAccountId, kpiScoreWeight, weScoreRangeEnumId, 
 * weScoreConvEnumId, weOtherGoalEnumId, weWithoutPerf, weTransTypeValueId
 *
 */
public enum MeasureKpiComparator implements Comparator<Map<String, Object>> {
    WE_MEASURE_ID {
        /**
         * Compare workEffortMeasureId
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String weMeasureId1 = (String) m1.get(E.workEffortMeasureId.name());
            String weMeasureId2 = (String) m2.get(E.workEffortMeasureId.name());
            return weMeasureId1.compareTo(weMeasureId2);
        }
    },
    GL_ACCOUNT {
        /**
         * Compare glAccountId
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String glFiscal1 = (String) m1.get(E.glAccountId.name());
            String glFiscal2 = (String) m2.get(E.glAccountId.name());
            return glFiscal1.compareTo(glFiscal2);
        }
    },
    KPI_SCORE {
        /**
         * Compare kpiScoreWeight
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Double kpiScore1 = (Double) m1.get(E.kpiScoreWeight.name());
            Double kpiScore2 = (Double) m2.get(E.kpiScoreWeight.name());
            return kpiScore1.compareTo(kpiScore2);
        }
    },
    WE_SCORE_RANGE {
        /**
         * Compare weScoreRangeEnumId
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String weScoreRange1 = (String) m1.get(E.weScoreRangeEnumId.name());
            String weScoreRange2 = (String) m2.get(E.weScoreRangeEnumId.name());
            return weScoreRange1.compareTo(weScoreRange2);
        }
    },
    WE_SCORE_CONV {
        /**
         * Compare weScoreConvEnumId
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String weScoreConv1 = (String) m1.get(E.weScoreConvEnumId.name());
            String weScoreConv2 = (String) m2.get(E.weScoreConvEnumId.name());
            return weScoreConv1.compareTo(weScoreConv2);
        }
    },
    WE_OTHER_GOAL {
        /**
         * Compare weOtherGoalEnumId
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String weOtherGoal1 = (String) m1.get(E.weOtherGoalEnumId.name());
            String weOtherGoal2 = (String) m2.get(E.weOtherGoalEnumId.name());
            return weOtherGoal1.compareTo(weOtherGoal2);
        }
    },
    WE_WITHOUT {
        /**
         * Compare weWithoutPerf
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String weWithout1 = (String) m1.get(E.weWithoutPerf.name());
            String weWithout2 = (String) m2.get(E.weWithoutPerf.name());
            return weWithout1.compareTo(weWithout2);
        }
    },
    DESC_WE_TRANS_TYPE {
        /**
         * Compare weTransTypeValueId
         */
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String weTransType1 = (String) m1.get(E.weTransTypeValueId.name());
            String weTransType2 = (String) m2.get(E.weTransTypeValueId.name());
            if (weTransType2 == null) {
                // Bug 4415
                return weTransType1 == null ? 0 : -1;
            }
            return weTransType2.compareTo(weTransType1);
        }
    };

    /**
     * Comparator
     * @param multipleOptions
     * @return
     */
    public static Comparator<Map<String, Object>> getComparator(final MeasureKpiComparator... multipleOptions) {
        return new Comparator<Map<String, Object>>() {
            /**
             * Comparator: workEffortMeasureId, glAccountId, kpiScoreWeight, weScoreRangeEnumId, 
             * weScoreConvEnumId, weOtherGoalEnumId, weWithoutPerf, weTransTypeValueId
             *  @param m1
             * @param m2
             * @return
             */
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                for (MeasureKpiComparator option : multipleOptions) {
                    int result = option.compare(m1, m2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }
}