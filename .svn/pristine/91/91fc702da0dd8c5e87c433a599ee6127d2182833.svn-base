package com.mapsengineering.base.services.async;

public class AsyncJobUtil {

    public static final String ASYNC_JOB_PARAM_NAME = "asyncJob";
    public static final String ASYNC_QUEUE_PARAM_NAME = "asyncJobQueue";
    public static final String ASYNC_JOB_OFBIZ_EXECUTOR = "asyncJobOfbizExecutor";
    /** Allegati temporanei e cancellati dopo 24 ore, ma utilizzati per essere mostrati nella portlet */
    public static final String CONTENT_TYPE_TMP_ENCLOSE = "TMP_ENCLOSE";
    public static final String CONTENT_ID_FIELD = "contentId";
    public static final String SERVICE_LOG_ID_FIELD = "sessionId";

    public static String getString(Object obj, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        return obj.toString();
    }

    public static Integer getInteger(Object obj, Integer defaultValue) {
        try {
            return Integer.valueOf(getString(obj, null));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static Long getLong(Object obj, Long defaultValue) {
        try {
            return Long.valueOf(getString(obj, null));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
