package com.mapsengineering.base.services.async;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;

public class AsyncJobConfig {

    private static final String MODULE = AsyncJobConfig.class.getName();
    private static final long DEFAULT_KEEP_ALIVE_TIMEOUT = 2L * 60L * 1000L;
    private static final long MIN_INITIAL_CLIENT_WAIT = 3L * 1000L;

    private final String jobId;
    private long keepAliveTimeout;
    private long initialClientWait;
    private long finalClientWait;

    public AsyncJobConfig(String jobId) {
        this.jobId = jobId;
        setKeepAliveTimeout(-1L);
    }

    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        init(keepAliveTimeout);
    }

    public long getInitialClientWait() {
        return initialClientWait;
    }

    public long getFinalClientWait() {
        return finalClientWait;
    }

    private void init(long keepAliveTimeoutConfig) {
        final String prefix = AsyncJobConfig.class.getSimpleName() + '.';

        if (keepAliveTimeoutConfig < 0L) {
            keepAliveTimeout = AsyncJobUtil.getLong(UtilProperties.getPropertyValue("BaseConfig", prefix + "keepAliveTimeout"), DEFAULT_KEEP_ALIVE_TIMEOUT);
        } else {
            keepAliveTimeout = keepAliveTimeoutConfig;
        }

        if (keepAliveTimeout < 0L) {
            keepAliveTimeout = DEFAULT_KEEP_ALIVE_TIMEOUT;
        }
        long maxFinalClientWait = keepAliveTimeout == 0L ? DEFAULT_KEEP_ALIVE_TIMEOUT : keepAliveTimeout / 4L;

        initialClientWait = AsyncJobUtil.getLong(UtilProperties.getPropertyValue("BaseConfig", prefix + "initialClientWait"), MIN_INITIAL_CLIENT_WAIT);
        finalClientWait = AsyncJobUtil.getLong(UtilProperties.getPropertyValue("BaseConfig", prefix + "finalClientWait"), maxFinalClientWait);

        if (initialClientWait < MIN_INITIAL_CLIENT_WAIT) {
            initialClientWait = MIN_INITIAL_CLIENT_WAIT;
        }
        if (initialClientWait > maxFinalClientWait) {
            initialClientWait = maxFinalClientWait;
        }

        Debug.logInfo("Config job " + jobId //
                + ": keepAliveTimeout=" + keepAliveTimeout //
                + ", initialClientWait=" + initialClientWait //
                + ", finalClientWait=" + finalClientWait, MODULE);
    }
}
