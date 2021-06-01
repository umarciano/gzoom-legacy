package com.mapsengineering.base.services.async;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public abstract class AsyncJob extends AsyncJobBase implements Callable<Map<String, Object>> {

    private static final String MODULE = AsyncJob.class.getName();
    private static final Object LOCK = new Object();
    private static final String RESPONSE_MESSAGE_QUEUED = "queued";
    private static final String RESPONSE_MESSAGE_RUNNING = "running";
    private static final String RESPONSE_MESSAGE_INTERRUPT = "interrupt";
    private static final String RESPONSE_MESSAGE_TIMEOUT = "timeout";

    private Map<String, Object> result;
    private long keepAliveTimeLast;
    private long currentClientWait;
    private AsyncJobCallback callback;

    protected AsyncJob() {
        this(null);
    }

    protected AsyncJob(String jobId) {
        super(jobId);
        result = ServiceUtil.returnMessage(RESPONSE_MESSAGE_QUEUED, null);
        currentClientWait = 0L;
        refreshKeepAliveTime();
    }

    /**
     * 
     * @return callback to get progress
     */
    public AsyncJobCallback getCallback() {
        return callback;
    }

    /**
     * Sets callback to get progress
     * @param callback
     */
    public void setCallback(AsyncJobCallback callback) {
        this.callback = callback;
    }

    /**
     * Gets the last keep alive time.
     * @return
     */
    public long getKeepAliveTimeLast() {
        synchronized (LOCK) {
            return keepAliveTimeLast;
        }
    }

    /**
     * Removes the job from the history
     * @throws GeneralException 
     */
    public void remove() throws GeneralException {
        AsyncJobManager.remove(getJobId());
    }

    /**
     * @return true if queued
     */
    public boolean isQueued() {
        synchronized (LOCK) {
            return isQueuedStatus();
        }
    }

    /**
     * @return true if running or queued
     */
    public boolean isRunning() {
        synchronized (LOCK) {
            return isQueuedOrRunningStatus();
        }
    }

    /**
     * Gets progress data and refresh the keep alive time.
     * To be called by clients.
     * @return progress data, partial result map.
     */
    public Map<String, Object> getProgress() {
        if (Debug.verboseOn()) {
            Debug.logVerbose("Getting progress for job " + getJobId(), MODULE);
        }
        synchronized (LOCK) {
            refreshKeepAliveTime();
        }
        if (callback != null) {
            try {
                callback.onAsyncJobProgress();
            } catch (Exception ex) {
                Debug.logError(ex, MODULE);
            }
        }
        Map<String, Object> progress = new HashMap<String, Object>();
        synchronized (LOCK) {
            progress.putAll(getResult());
        }
        return progress;
    }

    /**
     * Requests to interrupt the running service.
     * To be called by clients.
     */
    public void requestInterrupt() {
        Debug.logInfo("Interrupt request for job " + getJobId(), MODULE);
        synchronized (LOCK) {
            if (isQueuedOrRunningStatus()) {
                result.put(ModelService.RESPONSE_MESSAGE, RESPONSE_MESSAGE_INTERRUPT);
            }
        }
    }

    /**
     * @return true if interrupted or keep alive expired.
     */
    public boolean isInterrupted() {
        synchronized (LOCK) {
            if (isInterruptedStatus()) {
                return true;
            }
            if (isKeepAliveExpiredStatus()) {
                return true;
            }
            if (isKeepAliveExpired()) {
                Debug.logInfo("Job expired " + getJobId(), MODULE);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the result map with progress data.
     * To be called by the service.
     * @param result progress data, partial result map
     * @return true if not interrupted
     */
    public boolean updateResult(Map<String, Object> result) {
        if (Debug.verboseOn()) {
            Debug.logVerbose("Updating result for job " + getJobId(), MODULE);
        }
        if (isQueuedStatus()) {
            synchronized (LOCK) {
                currentClientWait = 0L;
                this.result.put(ModelService.RESPONSE_MESSAGE, RESPONSE_MESSAGE_RUNNING);
            }
        }
        if (result != null) {
            synchronized (LOCK) {
                this.result.putAll(result);
            }
        }
        return !isInterrupted();
    }

    /**
     * Calculate the next wait period for the client.
     * @return wait period
     */
    public long getNextClientWait() {
        synchronized (LOCK) {
            if (currentClientWait < getConfig().getInitialClientWait()) {
                currentClientWait = getConfig().getInitialClientWait();
            } else {
                currentClientWait += getConfig().getInitialClientWait();
            }
            if (currentClientWait > getConfig().getFinalClientWait()) {
                currentClientWait = getConfig().getFinalClientWait();
            }
            return currentClientWait;
        }
    }

    @Override
    public Map<String, Object> call() throws Exception {
        synchronized (LOCK) {
            refreshKeepAliveTime();
            currentClientWait = 0L;
            this.result.put(ModelService.RESPONSE_MESSAGE, RESPONSE_MESSAGE_RUNNING);
        }
        callJob();
        return getResult();
    }

    protected abstract void callJob() throws Exception;

    protected Map<String, Object> getResult() {
        synchronized (LOCK) {
            return result;
        }
    }

    protected void setResult(Map<String, Object> result) {
        synchronized (LOCK) {
            this.result = result;
        }
    }

    protected boolean isQueuedStatus() {
        return RESPONSE_MESSAGE_QUEUED.equals(result.get(ModelService.RESPONSE_MESSAGE));
    }

    protected boolean isRunningStatus() {
        return RESPONSE_MESSAGE_RUNNING.equals(result.get(ModelService.RESPONSE_MESSAGE));
    }

    protected boolean isQueuedOrRunningStatus() {
        return isQueuedStatus() || isRunningStatus();
    }

    protected boolean isInterruptedStatus() {
        return RESPONSE_MESSAGE_INTERRUPT.equals(result.get(ModelService.RESPONSE_MESSAGE));
    }

    protected boolean isKeepAliveExpiredStatus() {
        return RESPONSE_MESSAGE_TIMEOUT.equals(result.get(ModelService.RESPONSE_MESSAGE));
    }

    protected boolean isKeepAliveExpired() {
        if (getConfig().getKeepAliveTimeout() <= 0L || !isQueuedOrRunningStatus()) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - keepAliveTimeLast;
        if (elapsed >= getConfig().getKeepAliveTimeout()) {
            result.put(ModelService.RESPONSE_MESSAGE, RESPONSE_MESSAGE_TIMEOUT);
            return true;
        }
        return false;
    }

    protected void refreshKeepAliveTime() {
        keepAliveTimeLast = System.currentTimeMillis();
    }
}
