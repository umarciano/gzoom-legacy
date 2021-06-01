package com.mapsengineering.base.services.async;

import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class AsyncJobOfbizService extends AsyncJob {

    private final String serviceName;
    private final int transactionTimeout;
    private final boolean requireNewTransaction;

    public AsyncJobOfbizService(LocalDispatcher dispatcher, String serviceName, Map<String, Object> context) {
        this(dispatcher, serviceName, context, -1, false);
    }
    
    @Override
    public String getJobName() {
        String jobName = getJobNameInternal();
        if (UtilValidate.isEmpty(jobName)) {
            try {
                ModelService service = getDispatcher().getDispatchContext().getModelService(serviceName);
                if (UtilValidate.isNotEmpty(service.description)) {
                    return service.description;
                }
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }
            return serviceName;
        }
        return jobName;
    }

    /**
     * I job 
     * @param dispatcher
     * @param serviceName
     * @param context
     * @param transactionTimeout
     * @param requireNewTransaction
     */
    public AsyncJobOfbizService(LocalDispatcher dispatcher, String serviceName, Map<String, Object> context, int transactionTimeout, boolean requireNewTransaction) {
        super(AsyncJobIdGenerator.getIdFromServiceLogJob(context));
        setDispatcher(dispatcher);
        setContext(context);
        this.serviceName = serviceName;
        this.transactionTimeout = transactionTimeout;
        this.requireNewTransaction = requireNewTransaction;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getTransactionTimeout() {
        return transactionTimeout;
    }

    public boolean isRequireNewTransaction() {
        return requireNewTransaction;
    }

    @Override
    protected void callJob() throws Exception {
        beforeRun();
        serviceRun();
    }

    protected void beforeRun() throws GenericServiceException {
        ModelService service = getDispatcher().getDispatchContext().getModelService(serviceName);
        ModelParam modelParam = service.getParam(AsyncJobUtil.ASYNC_JOB_PARAM_NAME);
        if (modelParam != null) {
            getContext().put(AsyncJobUtil.ASYNC_JOB_PARAM_NAME, this);
        }
    }

    protected void serviceRun() throws GenericServiceException {
        try {
            Map<String, Object> result = getDispatcher().runSync(serviceName, getContext(), transactionTimeout, requireNewTransaction);
            setResult(result);
        } catch (Exception ex) {
            updateResult(ServiceUtil.returnError(ex.getMessage()));
        }
    }
}
