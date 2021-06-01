package com.mapsengineering.base.services.async;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.UtilProperties;

public class AsyncJobQueueConfig {

    private static final String MODULE = AsyncJobQueueConfig.class.getName();

    private int poolSize;

    public AsyncJobQueueConfig() {
        init();
    }

    public int getPoolSize() {
        return poolSize;
    }

    private void init() {
        final String prefix = AsyncJobQueueConfig.class.getSimpleName() + '.';
        final Map<String, Object> cfgContext = new HashMap<String, Object>();

        poolSize = AsyncJobUtil.getInteger(GroovyUtil.eval(UtilProperties.getPropertyValue("BaseConfig", prefix + "poolSize"), cfgContext), 0);
        if (poolSize <= 0) {
            poolSize = Runtime.getRuntime().availableProcessors() - 1;
        }
        if (poolSize <= 0) {
            poolSize = 1;
        }

        Debug.logInfo("Config job queue" //
                + ", poolSize=" + poolSize, MODULE);
    }
}
