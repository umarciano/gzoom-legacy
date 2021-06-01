package com.mapsengineering.base.services.async;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.LocalDispatcher;

public abstract class AsyncJobBase implements Comparable<AsyncJob> {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final long sequenceId;
    private final String jobId;
    private int priority;
    private String jobName;
    private Timestamp createdDate;
    private AsyncJobConfig config;
    private LocalDispatcher dispatcher;
    private Map<String, Object> context;

    protected AsyncJobBase() {
        this(null);
    }

    protected AsyncJobBase(String jobId) {
        this.jobId = jobId != null ? jobId : AsyncJobIdGenerator.nextStringId();
        sequenceId = SEQUENCE.getAndIncrement();
        priority = 0;
        createdDate = UtilDateTime.nowTimestamp();
        init();
    }

    /**
     * Gets sequence id
     * @return
     */
    public long getSequenceId() {
        return sequenceId;
    }

    /**
     * Gets job id
     * @return Job id
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Gets priority, lower value is higher priority
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets priority, lower value is higher priority
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the job name or jobId
     * @return
     */
    public String getJobName() {
        if (UtilValidate.isEmpty(getJobNameInternal())) {
            return getJobId();
        }
        return getJobNameInternal();
    }
    
    /**
     * Gets the job name
     * @return
     */
    protected String getJobNameInternal() {
        return jobName;
    }

    /**
     * Sets job name
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets creation timestamp
     * @return
     */
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets creation timestamp
     * @param createdDate
     */
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 
     * @return config object
     */
    public AsyncJobConfig getConfig() {
        return config;
    }

    /**
     * 
     * @return dispatcher
     */
    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Sets dispatcher
     * @param dispatcher
     */
    public void setDispatcher(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * 
     * @return current context
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Sets current context
     * @param context
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AsyncJob)) {
            return false;
        }
        return compareTo((AsyncJob)obj) == 0;
    }

    @Override
    public int hashCode() {
        return (int)sequenceId;
    }

    @Override
    public int compareTo(AsyncJob other) {
        if (other == null) {
            return 1;
        }
        if (priority < other.getPriority()) {
            return 1;
        } else if (priority > other.getPriority()) {
            return -1;
        }
        if (this.sequenceId < other.getSequenceId()) {
            return -1;
        } else if (this.sequenceId > other.getSequenceId()) {
            return 1;
        }
        return 0;
    }

    protected void initAsyncJobBase() {
        init();
    }

    private final void init() {
        config = new AsyncJobConfig(getJobId());
    }
}
