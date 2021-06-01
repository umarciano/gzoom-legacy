package com.mapsengineering.base.util;

import org.ofbiz.base.util.UtilValidate;

public class WidgetNameUtils {

    public static final String SUCCESS_CODE_SUCCESS = "success";
    public static final String SUCCESS_CODE_MANAGEMENT = "management";

    public static final String LAYOUT_TYPE_SINGLE = "";
    public static final String LAYOUT_TYPE_LIST = "list";
    public static final String LAYOUT_TYPE_MULTI = "multi";

    public static final String COMBINED_NAME_SCREEN = "Screen";
    public static final String COMBINED_NAME_FORM = "Form";
    public static final String COMBINED_NAME_FORM_SCREEN = "FormScreen";
    public static final String COMBINED_NAME_SEARCH_RESULT = "SearchResult";
    public static final String COMBINED_NAME_MANAGEMENT = "Management";
    public static final String COMBINED_NAME_CONTEXT = "Context";
    
    private WidgetNameUtils() {}

    public static String getCombinedName(String prefix, String successCode, String layoutType, String suffix) {
        if (UtilValidate.isEmpty(prefix)) {
            prefix = "";
        }
        if (UtilValidate.isEmpty(successCode) || SUCCESS_CODE_SUCCESS.equalsIgnoreCase(successCode)) {
            successCode = COMBINED_NAME_SEARCH_RESULT;
        } else if (SUCCESS_CODE_MANAGEMENT.equalsIgnoreCase(successCode)) {
            successCode = COMBINED_NAME_MANAGEMENT;
        } else {
            successCode = "";
        }
        if (UtilValidate.isEmpty(layoutType)) {
            layoutType = "";
        }
        return prefix + successCode + org.apache.commons.lang.StringUtils.capitalize(layoutType) + suffix;
    }

    public static String getManagementFormScreenName(String layoutType) {
        if (LAYOUT_TYPE_MULTI.equalsIgnoreCase(layoutType)) {
            layoutType = LAYOUT_TYPE_LIST;
        }
        return getCombinedName(null, SUCCESS_CODE_MANAGEMENT, layoutType, COMBINED_NAME_FORM_SCREEN);
    }

    public static String getContextManagementFormScreenName(String layoutType) {
        return getCombinedName(COMBINED_NAME_CONTEXT, SUCCESS_CODE_MANAGEMENT, layoutType, COMBINED_NAME_FORM_SCREEN);
    }
}
