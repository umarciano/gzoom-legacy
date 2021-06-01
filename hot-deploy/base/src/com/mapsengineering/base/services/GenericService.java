package com.mapsengineering.base.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.common.ImportManagerConstants.MessageCode;
import com.mapsengineering.base.util.DateUtilService;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.MessageUtil;

/**
 * Generic Service
 *
 */
public abstract class GenericService {
    
    public static final String DEFAULT_ORGANIZATION_PARTY_ID = "defaultOrganizationPartyId";
    public static final String ORGANIZATION_PARTY_ID = "organizationPartyId";
    public static final String ORGANIZATION_ID = "organizationId";
    
    /**
     * Lenght for JobLogLog.valueRef3
     */
    public static int VALUE_REF3_LENGHT = 255;
    
    protected JobLogger jobLogger;
    private DispatchContext dctx;
    protected Delegator delegator;
    protected LocalDispatcher dispatcher;
    protected Map<String, Object> context;
    protected GenericValue userLogin;
    private Locale locale;
    private Map<String, Object> result = ServiceUtil.returnSuccess();
    private long recordElaborated;
    private long blockingErrors;
    private long warningMessages;
    private boolean useCache;
    private TimeZone timeZone;
    private String serviceName;
    private String serviceType;
    private String elabRef1;
    private String module;
    private String sessionId;
    private int transactionTimeout;
    
    /**
     * Description of service name
     */
    private String descriptionEntityName;
    private String defaultOrganizationPartyId;
    
    /**
     * Constructor, sessionId, locale e timeZone from context, jobLogger from context or create new 
     * @param dctx
     * @param context
     * @param serviceName
     * @param serviceType
     * @param module
     */
    public GenericService(DispatchContext dctx, Map<String, Object> context, String serviceName, String serviceType, String module) {
        this.delegator = dctx.getDelegator();
        this.dispatcher = dctx.getDispatcher();
        this.context = context;
        this.userLogin = (GenericValue)context.get("userLogin");
        this.locale = (Locale)context.get(ServiceLogger.LOCALE);
        this.timeZone = (TimeZone)context.get(ServiceLogger.TIME_ZONE);
        if (UtilValidate.isNotEmpty(context.get(ServiceLogger.JOB_LOGGER))) {
            this.jobLogger = (JobLogger)context.get(ServiceLogger.JOB_LOGGER);
        } else {
            this.jobLogger = new JobLogger(module);
        }
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.module = module;
        this.dctx = dctx;
        this.sessionId = (String)context.get("sessionId");
        initTransactionTimeout();
    }

    /**
     * Constructor, sessionId, locale e timeZone from context
     * @param dctx
     * @param context
     * @param jobLogger
     * @param serviceName
     * @param serviceType
     * @param module
     */
    public GenericService(DispatchContext dctx, Map<String, Object> context, JobLogger jobLogger, String serviceName, String serviceType, String module) {
        this.delegator = dctx.getDelegator();
        this.dispatcher = dctx.getDispatcher();
        this.context = context;
        this.userLogin = (GenericValue)context.get("userLogin");
        this.locale = (Locale)context.get("locale");
        this.timeZone = (TimeZone)context.get(ServiceLogger.TIME_ZONE);
        this.jobLogger = jobLogger;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.module = module;
        this.dctx = dctx;
        this.sessionId = (String)context.get("sessionId");
        initTransactionTimeout();
       
    }

    /**
     * Return the day after
     * @param date
     * @return
     */
    protected Date getNextDay(Date date) {
        return DateUtilService.getNextDay(date);
    }
    
    /**
     * Return the day before
     * @param date
     * @return
     */
    protected Date getPreviousDay(Date date) {
        return DateUtilService.getPreviousDay(date);
    }
    
    /**
     * Merge result
     * @param result
     * @param successfullMsg
     * @param errorMsg
     * @throws GeneralException
     */
    protected void manageResult(Map<String, Object> result, String successfullMsg, String errorMsg) {
        addLogInfo(serviceName + " result = " + result);
        if (UtilValidate.isNotEmpty(result)) {
            getResult().put(ServiceLogger.JOB_LOG_ID, result.get(ServiceLogger.JOB_LOG_ID));
            setBlockingErrors(getBlockingErrors() + (Long) result.get(ServiceLogger.BLOCKING_ERRORS));
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            setRecordElaborated(getRecordElaborated() + (Long) result.get(ServiceLogger.RECORD_ELABORATED));
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            if (ServiceUtil.isSuccess(result)) {
                addLogInfo(successfullMsg);
            } else {
                String msg = errorMsg + ServiceUtil.getErrorMessage(result);
                addLogInfo(msg);
            }
        }
    }
    
    /**
     * Add log Error with empty valueRef1
     * @param t
     * @param message
     */
    public void addLogError(Throwable t, String message) {
        message += MessageUtil.getExceptionMessage(t);
        addLogError(message);
    }

    /**
     * Add log Error with valueRef1 = key
     * @param t
     * @param message
     * @param key
     */
    public void addLogError(Throwable t, String message, String key) {
        message += MessageUtil.getExceptionMessage(t);
        addLogError(message, key);
    }

    /**
     * Add log Error with empty valueRef1
     * @param message
     */
    public void addLogError(String message) {
        addLogError(message, null);
    }

    /**
     * Add log Info with empty valueRef1
     * @param message
     */
    public void addLogInfo(String message) {
        addLogInfo(message, null);
    }

    /**
     * Add log Warning with empty valueRef1
     * @param message
     */
    public void addLogWarning(String message) {
        addLogWarning(message, null);
    }

    /**
     * Write log Error with valueRef1 = key
     * @param message
     * @param key
     */
    public void addLogError(String message, String key) {
        Debug.logError(message, module);
        setBlockingErrors(getBlockingErrors() + 1);
        jobLogger.addMessage(ServiceLogger.makeLogError(message, MessageCode.ERROR_BLOCKING.toString(), key, null, null));
    }
    
    /**
     * Add error message with t.getMessage <br/>
     * Add debug message with logMessage + t
     * @param t
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     */
    public void addLogError(Throwable t, String logCode, String logMessage, String key, String valueRef2, String valueRef3) {
        setBlockingErrors(getBlockingErrors() + 1);
        logMessage += MessageUtil.getExceptionMessage(t);
        Debug.logError(logMessage, module);
        valueRef3 = getValueRef3(valueRef3);
        if(t != null) {
            jobLogger.addMessage(ServiceLogger.makeLogError(t.getMessage(), logCode, key, valueRef2, valueRef3));
        }
        jobLogger.addMessage(ServiceLogger.makeLogDebug(logMessage, logCode, key, valueRef2, valueRef3));
    }
    
    /**
     * LogInfo with 
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     */
    public void addLogInfo(String logCode, String logMessage, String key, String valueRef2, String valueRef3) {
        Debug.logInfo(logMessage, module);
        valueRef3 = getValueRef3(valueRef3);
        jobLogger.addMessage(ServiceLogger.makeLogInfo(logMessage, logCode, key, valueRef2, valueRef3));
    }
    
    /**
     * LogInfo with 
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     */
    public void addLogWarning(String logCode, String logMessage, String key, String valueRef2, String valueRef3) {
        Debug.logWarning(logMessage, module);
        setWarningMessages(getWarningMessages() + 1);
        valueRef3 = getValueRef3(valueRef3);
        jobLogger.addMessage(ServiceLogger.makeLogWarn(logMessage, logCode, key, valueRef2, valueRef3));
    }

    /**
     * Write log Warning with valueRef1 = key
     * @param message
     * @param key
     */
    public void addLogInfo(String message, String key) {
        Debug.logInfo(message, module);
        jobLogger.addMessage(ServiceLogger.makeLogInfo(message, MessageCode.INFO_GENERIC.toString(), key, null, null));
    }

    /**
     * Write log Warning with valueRef1 = key
     * @param message
     * @param key
     */
    public void addLogWarning(String message, String key) {
        Debug.logWarning(message, module);
        setWarningMessages(getWarningMessages() + 1);
        jobLogger.addMessage(ServiceLogger.makeLogWarn(message, MessageCode.WARNING.toString(), key, null, null));
    }

    protected void writeLogs(Timestamp startTimestamp, String jobLogId) {
        writeLogs(startTimestamp, jobLogId, null);
    }

    protected void writeLogs(Timestamp startTimestamp, String jobLogId, Map<String, Object> serviceParameters) {
        Map<String, Object> logParameters = FastMap.newInstance();

        try {
            logParameters.put(ServiceLogger.JOB_LOG_ID, jobLogId);
            logParameters.put(ServiceLogger.SESSION_ID, getSessionId());
            logParameters.put(ServiceLogger.MESSAGES, jobLogger.getMessages());
            logParameters.put(ServiceLogger.USER_LOGIN, userLogin);
            logParameters.put(ServiceLogger.LOG_DATE, startTimestamp);
            logParameters.put(ServiceLogger.RECORD_ELABORATED, this.getRecordElaborated());
            logParameters.put(ServiceLogger.WARNING_MESSAGES, this.getWarningMessages());
            logParameters.put(ServiceLogger.BLOCKING_ERRORS, this.getBlockingErrors());

            if (UtilValidate.isNotEmpty(this.getElabRef1())) {
                logParameters.put(ServiceLogger.ELAB_REF1, this.getElabRef1());
            }

            logParameters.put(ServiceLogger.SERVICE_NAME, serviceName);
            GenericValue serviceTypeValue = delegator.findOne("JobLogServiceType", UtilMisc.toMap(ServiceLogger.SERVICE_TYPE_ID, serviceType), true);
            if (UtilValidate.isNotEmpty(serviceTypeValue)) {
                logParameters.put(ServiceLogger.SERVICE_TYPE_ID, serviceTypeValue.getString(ServiceLogger.SERVICE_TYPE_ID));
            }

            ServiceLogger.writeLogs(dctx, logParameters, serviceParameters);
        } catch (GenericEntityException e) {
            Debug.logInfo("Error writing log: " + e.getMessage(), module);
        }
    }

    /**
     * Create map for BaseCrudInterface
     * @param entityName
     * @param operation
     * @param parameters
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> baseCrudInterface(String entityName, String operation, Map parameters) {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put("entityName", entityName);
        serviceMap.put("operation", operation);
        serviceMap.put("userLogin", userLogin);
        parameters.put("operation", operation);
        serviceMap.put("parameters", parameters);
        serviceMap.put("locale", locale);
        return serviceMap;
    }

    /**
     * Execute serviceName and manage Result
     */
    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> serviceMap, String successMsg, String errorMsg, boolean throwOnError, boolean throwOnFailur, String valueRef1) throws GeneralException {
        Map<String, Object> res = dispatcher.runSync(serviceName, serviceMap);
        // solo su console, perche nel db potrebbe essere troppo onoreso
        Debug.log("Service " + serviceName + " return " + res);
        if (ServiceUtil.isSuccess(res)) {
            addLogInfo(successMsg, valueRef1);
        } else {
            if (ServiceUtil.isError(res)) {
                manageErrorResult(throwOnError, errorMsg + FindUtilService.COLON_SEP + ServiceUtil.getErrorMessage(res), valueRef1);
            } else if (ServiceUtil.isFailure(res)) {
                manageFailureResult(throwOnFailur, errorMsg + FindUtilService.COLON_SEP + res.get(ModelService.FAIL_MESSAGE), valueRef1);
            } else {
                addLogError(errorMsg + FindUtilService.COLON_SEP + ServiceUtil.getErrorMessage(res), valueRef1);
            }
        }
        return res;
    }
    
    /**
     * Substring of valueRef3 and concat ...
     * @param valueRef3
     * @return
     */
    private String getValueRef3(String valueRef3) {
        if (UtilValidate.isNotEmpty(valueRef3) && valueRef3.length() > VALUE_REF3_LENGHT) {
            valueRef3 = valueRef3.substring(0, VALUE_REF3_LENGHT - 4) + ("...");
        }
        return valueRef3;
    }

    private void manageFailureResult(boolean throwOnFailur, String failureMessage, String valueRef1) throws GeneralException {
        if (throwOnFailur) {
            throw new GeneralException(failureMessage);
        } else {
            addLogWarning(failureMessage, valueRef1);
        }
    }

    /**
     * if throwOnError throw Geenral Exception else add log error
     * @param throwOnError
     * @param errorMessage
     * @param valueRef1
     * @throws GeneralException
     */
    private void manageErrorResult(boolean throwOnError, String errorMessage, String valueRef1) throws GeneralException {
        if (throwOnError) {
            throw new GeneralException(errorMessage);
        } else {
            addLogError(errorMessage, valueRef1);
        }
    }

    /**
     * Execute service
     * @param serviceName
     * @param serviceMap
     * @param successMsg
     * @param errorMsg
     * @param throwOnError
     * @return
     * @throws GeneralException
     */
    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> serviceMap, String successMsg, String errorMsg, boolean throwOnError) throws GeneralException {
        return runSync(serviceName, serviceMap, successMsg, errorMsg, throwOnError, false, null);
    }

    /** Ritorna un solo elemento, rilancia eccezione negli altri casi*/
    public GenericValue findOne(String entityNameToFind, EntityCondition cond, String foundMore, String noFound) throws GeneralException  {
        return FindUtilService.findOne(getDelegator(), entityNameToFind, cond, foundMore, noFound);
    }
    
    /** Ritorna lista di genericValue,
     * @params throwIfNone rilancia eccezione */
    public List<GenericValue> findList(String entityNameToFind, EntityCondition cond, boolean throwIfNone, String valueRef1) throws GeneralException {
        List<GenericValue> parents = delegator.findList(entityNameToFind, cond, null, null, null, false);
        if (UtilValidate.isEmpty(parents) && throwIfNone) {
            throw new GeneralException("No Found record with " + cond);
        }
        return parents;
    }
    
    /** Ritorna lista ordinata di genericValue,
     * @params throwIfNone rilancia eccezione */
    public List<GenericValue> findList(String entityNameToFind, EntityCondition cond, List<String> orderBy, boolean throwIfNone, String valueRef1) throws GeneralException {
        List<GenericValue> parents = delegator.findList(entityNameToFind, cond, null, orderBy, null, false);
        if (UtilValidate.isEmpty(parents) && throwIfNone) {
            throw new GeneralException("No Found record with " + cond);
        }
        return parents;
    }

    /** Invoca baseCrudInterface per creare la mappa da passsare al servizio crud e poi il servizio crud 
     * @param serviceName*/
    public Map<String, Object> runSyncCrud(String serviceName, String entityName, String operation, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError, boolean throwonFailure, String valueRef1) throws GeneralException {
        Map<String, Object> serviceMap = baseCrudInterface(entityName, operation, parametersMap);
        return runSync(serviceName, serviceMap, successMsg, errorMsg, throwOnError, throwonFailure, valueRef1);
    }

    /** Invoca baseCrudInterface per creare la mappa da passsare al servizio crud e poi il servizio crud */
    public Map<String, Object> runSyncCrud(String serviceName, String entityName, String operation, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError, boolean throwonFailure) throws GeneralException {
        Map<String, Object> serviceMap = baseCrudInterface(entityName, operation, parametersMap);
        return runSync(serviceName, serviceMap, successMsg, errorMsg, throwOnError, throwonFailure, null);
    }

    /** Invoca baseCrudInterface per creare la mappa da passsare al servizio crud e poi il servizio crud */
    public Map<String, Object> runSyncCrud(String serviceName, String entityName, String operation, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError) throws GeneralException {
        return runSyncCrud(serviceName, entityName, operation, parametersMap, successMsg, errorMsg, throwOnError, false, null);
    }

    /**
     * @return the result
     */
    public Map<String, Object> getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    /**
     * @return the recordElaborated
     */
    public long getRecordElaborated() {
        return recordElaborated;
    }

    /**
     * @param recordElaborated the recordElaborated to set
     */
    public void setRecordElaborated(long recordElaborated) {
        this.recordElaborated = recordElaborated;
    }

    /**
     * @return the blockingErrors
     */
    public long getBlockingErrors() {
        return blockingErrors;
    }

    /**
     * @param blockingErrors the blockingErrors to set
     */
    public void setBlockingErrors(long blockingErrors) {
        this.blockingErrors = blockingErrors;
    }

    /**
     * @return the warningMessages
     */
    public long getWarningMessages() {
        return warningMessages;
    }

    /**
     * @param warningMessages the warningMessages to set
     */
    public void setWarningMessages(long warningMessages) {
        this.warningMessages = warningMessages;
    }

    /**
     * @return the useCache
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * @param useCache the useCache to set
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    /**
     * @return the timeZone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * @param timeZone the timeZone to set
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * @return the elabRef1
     */
    public String getElabRef1() {
        return elabRef1;
    }

    /**
     * @param elabRef1 the elabRef1 to set
     */
    public void setElabRef1(String elabRef1) {
        this.elabRef1 = elabRef1;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return the useCache
     */
    public boolean getUseCache() {
        return this.useCache;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the delegator
     */
    public Delegator getDelegator() {
        return delegator;
    }

    /**
     * @param delegator the delegator to set
     */
    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
    }

    /**
     * @return the jobLogger
     */
    public JobLogger getJobLogger() {
        return jobLogger;
    }

    /**
     * @param jobLogger the jobLogger to set
     */
    public void setJobLogger(JobLogger jobLogger) {
        this.jobLogger = jobLogger;
    }

    /**
     * @return the userLogin
     */
    public GenericValue getUserLogin() {
        return userLogin;
    }

    /**
     * @param userLogin the userLogin to set
     */
    public void setUserLogin(GenericValue userLogin) {
        this.userLogin = userLogin;
    }

    /**
     * @return the dctx
     */
    public DispatchContext getDctx() {
        return dctx;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @return the descriptionEntityName
     */
    public String getDescriptionEntityName() {
        return descriptionEntityName;
    }

    /**
     * @param descriptionEntityName the descriptionEntityName to set
     */
    public void setDescriptionEntityName(String descriptionEntityName) {
        this.descriptionEntityName = descriptionEntityName;
    }

    /**
     * @return the transactionTimeout
     */
    public int getTransactionTimeout() {
        return transactionTimeout;
    }
    
    private void initTransactionTimeout() {
        try {
            ModelService modelService = dctx.getModelService(serviceName);
            transactionTimeout = modelService.transactionTimeout;
        } catch (GenericServiceException e) {
            transactionTimeout = ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT;
            Debug.logWarning("Cannot get ModelService for " + serviceName , module);
        }            
    }

    /**
     * @return the defaultOrganizationPartyId
     */
    public String getDefaultOrganizationPartyId() {
        return defaultOrganizationPartyId;
    }

    /**
     * @param defaultOrganizationPartyId the defaultOrganizationPartyId to set
     */
    public void setDefaultOrganizationPartyId(String defaultOrganizationPartyId) {
        this.defaultOrganizationPartyId = defaultOrganizationPartyId;
    }
    
    /**
     * Aggiunge alla tabella job_log_log il messaggio presente nel file labelsFile con chiave label e parametri params
     * 
     * @param params
     * @param label
     * @param labelsFile
     */
    public void createInfoLogWithLabel(Map<String, Object> params, String label, String labelsFile){
    	JobLogLog jloglog = new JobLogLog().initLogCode(labelsFile, label, params, getLocale());
        addLogInfo(jloglog.getLogMessage(), null);
    }
    
    /**
     * Aggiunge alla tabella job_log_log il messaggio presente nel file labelsFile con chiave label e parametri params
     * 
     * @param params
     * @param label
     * @param labelsFile
     */
    public void createInfoLog(Map<String, Object> params, String label, String labelsFile){
    	JobLogLog jloglog = new JobLogLog().initLogCode(labelsFile, label, params, getLocale());
        addLogInfo(jloglog.getLogCode() ,jloglog.getLogMessage(), null, labelsFile, jloglog.getParametersJSON());
    }

    /**
     * return dispatcher
     * @return
     */
    public LocalDispatcher getDispatcher() {
        return this.dispatcher;
    }
}
