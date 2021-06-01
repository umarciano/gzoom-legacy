package com.mapsengineering.accountingext.services;

import org.ofbiz.entity.GenericValue;

/**
 * Manage enumeration type for GlAccount
 *
 */
public final class InputAndDetailValue {

    public static final String ACCINP_OBJ = "ACCINP_OBJ";
    public static final String ACCINP_PRD = "ACCINP_PRD";
    public static final String ACCINP_UO = "ACCINP_UO";
    public static final String ACCDET_NULL = "ACCDET_NULL";
    public static final String ACC_INP_UO_DET_NULL = "ACC_INP_UO_DET_NULL";
    public static final String ACC_INP_UO_DET_NOT_NULL = "ACC_INP_UO_DET_NOT_NULL";
    public static final String ACC_INP_PRD_DET_NULL = "ACC_INP_PRD_DET_NULL";
    public static final String ACC_INP_PRD_DET_NOT_NULL = "ACC_INP_PRD_DET_NOT_NULL";
    public static final String ACC_INP_OBJ = "ACC_INP_OBJ";
    public static final String ACC_INP_DETECT_UO_PEUO = "ACC_INP_DETECT_UO_PEUO";
    public static final String ACC_INP_DETECT_UO = "ACC_INP_DETECT_UO";
    public static final String ACC_INP_OBJ_AGGREG = "ACC_INP_OBJ_AGGREG";
    public static final String ACC_INP_UO_MODA = "ACC_INP_UO_MODA";
    public static final String AGGREG = "AGGREG";
    public static final String ANNO = "ANNO";
    public static final String AGGREG_ANNO = "AGGREG_ANNO";
    public static final String AGGREG_MAX = "AGGREG_MAX";
    public static final String AGGREG_MAX_ANNO = "AGGREG_MAX_ANNO";
    private static final String PEUO = "PEUO";
    private static final String UO = "UO";
    private static final String MODA = "MODA";
    public static final String ACC_INP_OBJ_TSWE_ORE = "ACC_INP_OBJ_TSWE_ORE";
    public static final String TSWE_ORE = "TSWE_ORE";   
    public static final String ACCINP_UO_ANNO = "ACCINP_UO_ANNO";
    public static final String ACCINP_UO_DETECT_ANNO = "ACCINP_UO_DETECT_ANNO";
    public static final String ACCINP_UO_DET_NOT_NULL_ANNO = "ACCINP_UO_DET_NOT_NULL_ANNO";
    public static final String UO_GROUP = "UO_GROUP";
    public static final String ACC_INP_PRD_ANNO = "ACC_INP_PRD_ANNO";
    public static final String ACC_INP_OBJ_ANNO = "ACC_INP_OBJ_ANNO";
    public static final String ANNO_SUM = "ANNO_SUM";
    public static final String TSWE_ORE_ORE = "TSWE_ORE_ORE";

    private InputAndDetailValue() {
        // Empty
    }

    /**
     * Return Enumeration type
     * @param indicator
     * @return
     */
    public static String getEnumeration(GenericValue indicator, String customMethodName) {
        String inputEnumId = indicator.getString(E.inputEnumId.name());
        String detailEnumId = indicator.getString(E.detailEnumId.name());
        String detectOrgUnitIdFlag = indicator.getString(E.detectOrgUnitIdFlag.name());
        if (ACCINP_PRD.equals(inputEnumId)) {
            return returnCaseInputPrd(customMethodName, detailEnumId);

        }
        if (ACCINP_UO.equals(inputEnumId)) {
            return returnCaseInputUo(detailEnumId, detectOrgUnitIdFlag, customMethodName);
        }
        if (ACCINP_OBJ.equals(inputEnumId)) {
            return returnCaseInputObj(customMethodName);
        }
        return null;
    }

    private static String returnCaseInputUo(String detailEnumId, String detectOrgUnitIdFlag, String customMethodName) {
        if (E.Y.name().equals(detectOrgUnitIdFlag)) {
            return returnCaseInputUoDetect(customMethodName);
        }
        return returnCaseInputUoWithDett(customMethodName, detailEnumId);
    }
    
    private static String returnCaseInputUoWithDett(String customMethodName, String detailEnumId) {
        if (!ACCDET_NULL.equals(detailEnumId)) {
            if (customMethodName.indexOf(ANNO) > -1) {
                return ACCINP_UO_DET_NOT_NULL_ANNO;
            }
            return ACC_INP_UO_DET_NOT_NULL;
        }
        if (customMethodName.indexOf(ANNO) > -1) {
            return ACCINP_UO_ANNO;
        }
        if (customMethodName.indexOf(MODA) > -1) {
            return ACC_INP_UO_MODA;
        }
        return ACC_INP_UO_DET_NULL;
    }
    
    private static String returnCaseInputUoDetect(String customMethodName) {
        if (customMethodName.indexOf(PEUO) > -1) {
            return ACC_INP_DETECT_UO_PEUO;
        }
        if (customMethodName.indexOf(ANNO) > -1) {
            return ACCINP_UO_DETECT_ANNO;
        }
        if (customMethodName.indexOf(UO) > -1) {
            return UO_GROUP;
        }
        if (customMethodName.indexOf(MODA) > -1) {
            return ACC_INP_UO_MODA;
        }
        return ACC_INP_DETECT_UO;
    }

    private static String returnCaseInputPrd(String customMethodName, String detailEnumId) {
        if (customMethodName.indexOf(ANNO) > -1) {
            return ACC_INP_PRD_ANNO;
        }
        if (ACCDET_NULL.equals(detailEnumId)) {
            return ACC_INP_PRD_DET_NULL;
        }
        return ACC_INP_PRD_DET_NOT_NULL;
    }

    private static String returnCaseInputObj(String customMethodName) {
        if (customMethodName.indexOf(AGGREG) > -1) {
            return ACC_INP_OBJ_AGGREG;
        }
        if (customMethodName.indexOf(ANNO) > -1) {
            return ACC_INP_OBJ_ANNO;
        }
        if (customMethodName.indexOf(TSWE_ORE) > -1) {
            return ACC_INP_OBJ_TSWE_ORE;
        }
        if (customMethodName.indexOf(MODA) > -1) {
            return ACC_INP_UO_MODA;
        }
        return ACC_INP_OBJ;

    }
}