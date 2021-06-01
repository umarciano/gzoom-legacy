package com.mapsengineering.base.services.async;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.util.OfbizSingletonCache;

public class AsyncJobQueue {

    private static final String MODULE = AsyncJobQueue.class.getName();
    private static final Object LOCK = new Object();

    private final BlockingQueue<AsyncJob> queue;
    private int executorCount;

    public AsyncJobQueue() {
        queue = new PriorityBlockingQueue<AsyncJob>();
        executorCount = 0;
    }

    protected AsyncJobQueueConfig getConfig() {
        AsyncJobQueueConfig config;
        synchronized (LOCK) {
            final String objName = AsyncJobQueueConfig.class.getName();
            config = (AsyncJobQueueConfig)OfbizSingletonCache.INSTANCE.get(objName);
            if (config == null) {
                config = new AsyncJobQueueConfig();
                OfbizSingletonCache.INSTANCE.put(objName, config);
            }
        }
        return config;
    }

    protected void add(AsyncJob job) throws GenericServiceException {
        int poolSize = getConfig().getPoolSize();
        boolean doServiceRun;
        int executorCountCurr;
        synchronized (LOCK) {
            queue.add(job);
            executorCountCurr = executorCount;
            doServiceRun = executorCount < poolSize;
        }
        Debug.logInfo("Added job " + job.getJobId() + ", pool " + executorCountCurr + "/" + poolSize, MODULE);
        checkServiceRun(doServiceRun, job.getDispatcher(), job.getContext());
    }

    protected void remove(AsyncJob job) {
        queue.remove(job);
    }

    protected AsyncJob poll() {
        AsyncJob job = null;
        int poolSize = getConfig().getPoolSize();
        synchronized (LOCK) {
            if (executorCount < poolSize) {
                job = queue.poll();
                if (job != null) {
                    ++executorCount;
                }
            }
        }
        return job;
    }

    protected void onExecutorTerminated(LocalDispatcher dispatcher, Map<String, ? extends Object> context) throws GenericServiceException {
        int poolSize = getConfig().getPoolSize();
        boolean doServiceRun;
        int executorCountCurr;
        synchronized (LOCK) {
            if (--executorCount < 0) {
                executorCount = 0;
            }
            executorCountCurr = executorCount;
            doServiceRun = executorCount < poolSize;
        }
        Debug.logInfo("Executor terminated, pool " + executorCountCurr + "/" + poolSize, MODULE);
        checkServiceRun(doServiceRun, dispatcher, context);
    }

    protected void checkServiceRun(boolean doServiceRun, LocalDispatcher dispatcher, Map<String, ? extends Object> context) throws GenericServiceException {
        if (doServiceRun) {
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(AsyncJobUtil.ASYNC_JOB_OFBIZ_EXECUTOR, ModelService.IN_PARAM, context);
            serviceMap.put(AsyncJobUtil.ASYNC_QUEUE_PARAM_NAME, this);
            dispatcher.runAsync(AsyncJobUtil.ASYNC_JOB_OFBIZ_EXECUTOR, serviceMap, false);
        }
    }
}
