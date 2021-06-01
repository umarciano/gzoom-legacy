package com.mapsengineering.base.services.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;

public final class AsyncJobManager {

    private static final String MODULE = AsyncJobManager.class.getName();
    private static final Object LOCK = new Object();
    private static volatile Map<String, AsyncJob> JOB_MAP;
    private static volatile AsyncJobQueue QUEUE;

    private AsyncJobManager() {
        // empty
    }

    public static AsyncJob submit(AsyncJob job) throws GeneralException {
        synchronized (LOCK) {
            lazyInit();
            add(job);
            QUEUE.add(job);
        }
        return job;
    }

    public static AsyncJob get(String jobId) throws GeneralException {
        synchronized (LOCK) {
            lazyInit();
            return JOB_MAP.get(jobId);
        }
    }

    public static List<Map<String, Object>> getListView(String userLoginId) throws GeneralException, ConversionException {
        Set<AsyncJob> sortedSet = new TreeSet<AsyncJob>(new AsyncJobListViewComparator());
        synchronized (LOCK) {
            lazyInit();
            for (Map.Entry<String, AsyncJob> jobEntry : JOB_MAP.entrySet()) {
                AsyncJob job = jobEntry.getValue();
                sortedSet.add(job);
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        AsyncJobToMap converter = new AsyncJobToMap();
        for (AsyncJob job : sortedSet) {
            Map<String, Object> conv = converter.convert(job);
            // Show ONLY owner content or running and queue content
            if(userLoginId.equals(conv.get("userLoginId")) || job.isQueuedOrRunningStatus()) {
                result.add(conv);
            }
        }
        return result;
    }

    static void remove(String jobId) throws GeneralException {
        synchronized (LOCK) {
            lazyInit();
            AsyncJob job = JOB_MAP.remove(jobId);
            if (job != null) {
                QUEUE.remove(job);
                Debug.logInfo("Removed job " + jobId, MODULE);
            }
        }
    }

    static AsyncJob findByResultKey(String resultKeyName, Object resultKeyValue) throws GeneralException {
        synchronized (LOCK) {
            lazyInit();
            for (Map.Entry<String, AsyncJob> jobEntry : JOB_MAP.entrySet()) {
                AsyncJob job = jobEntry.getValue();
                Object resultKeyValueFound = job.getResult().get(resultKeyName);
                if (resultKeyValue.equals(resultKeyValueFound)) {
                    return job;
                }
            }
            return null;
        }
    }

    private static void add(AsyncJob job) throws GeneralException {
        purge();
        JOB_MAP.put(job.getJobId(), job);
        Debug.logInfo("Added job " + job.getJobId(), MODULE);
    }

    private static void addPersisted(AsyncJob job) {
        JOB_MAP.put(job.getJobId(), job);
        Debug.logInfo("Added persisted job " + job.getJobId(), MODULE);
    }

    private static void purge() throws GeneralException {
        if (!JOB_MAP.isEmpty()) {
            Debug.logInfo("Purging jobs", MODULE);
            long millisBeforeDelete = AsyncJobCleaner.getMillisBeforeDelete();
            Map<String, AsyncJob> cloneMap = new HashMap<String, AsyncJob>();
            cloneMap.putAll(JOB_MAP);
            Set<Entry<String, AsyncJob>> entrySet = cloneMap.entrySet();
            for(Iterator<Entry<String, AsyncJob>> it = entrySet.iterator(); it.hasNext();){
                Map.Entry<String, AsyncJob> jobEntry = it.next();
                AsyncJob job = jobEntry.getValue();
                if (!job.isRunning()) {
                    if (job.getKeepAliveTimeLast() <= millisBeforeDelete) {
                        job.remove();
                    }
                }
            }
        }
    }

    private static void lazyInit() throws GenericEntityException {
        if (JOB_MAP == null) {
            JOB_MAP = new HashMap<String, AsyncJob>();
            QUEUE = new AsyncJobQueue();
            Delegator delegator = DelegatorFactory.getDelegator("default");
            new AsyncJobFromContentLoader(delegator) {
                protected boolean load(AsyncJob job) throws GenericEntityException {
                    addPersisted(job);
                    return true;
                }
            }.load();
        }
    }
}
