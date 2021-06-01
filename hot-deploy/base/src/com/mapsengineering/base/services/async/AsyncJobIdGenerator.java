package com.mapsengineering.base.services.async;

import java.util.Map;

import org.ofbiz.entity.GenericValue;

public final class AsyncJobIdGenerator {

    public static final String NON_PERSISTENT_JOB_ID_PREFIX = "NP";
    public static final String FROM_CONTENT_JOB_ID_PREFIX = "C";
    public static final String FROM_SERVICE_LOG_JOB_ID_PREFIX = "S";

    private static final Object LOCK = new Object();

    private static long lastId = 0L;

    private AsyncJobIdGenerator() {
        // empty
    }

    /**
     * Gets unique generated id for non persistent jobs.
     * @return
     */
    public static String nextStringId() {
        return NON_PERSISTENT_JOB_ID_PREFIX + nextId();
    }

    /**
     * Gets unique id for job loaded from content
     * @param gv
     * @return
     */
    public static String getIdFromContent(GenericValue gv) {
        String contentId = gv.getString(AsyncJobUtil.CONTENT_ID_FIELD);
        return FROM_CONTENT_JOB_ID_PREFIX + contentId;
    }
    
    /**
     * Gets unique id for sessionId from context
     * @param context
     * @return
     */
    public static String getIdFromServiceLogJob(Map<String, Object> context) {
        String sessionId = context != null ? (String) context.get(AsyncJobUtil.SERVICE_LOG_ID_FIELD) : null;
        return sessionId != null ? FROM_SERVICE_LOG_JOB_ID_PREFIX + sessionId : null;
    }

    /**
     * Gets unique millis timestamp
     * @return
     */
    private static long nextId() {
        while (true) {
            synchronized (LOCK) {
                long id = System.currentTimeMillis();
                if (id != lastId) {
                    lastId = id;
                    break;
                }
            }
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                break;
            }
        }
        return lastId;
    }
}
