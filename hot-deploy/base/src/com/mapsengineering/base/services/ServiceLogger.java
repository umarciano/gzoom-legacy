package com.mapsengineering.base.services;

import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ModelServiceIface;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

/**
 * ServiceLogger
 *
 */
public class ServiceLogger {
    public static final String module = ServiceLogger.class.getName();

    public static final String JOB_LOG_ID = "jobLogId";
    public static final String WARNING_MESSAGES = "warningMessages";
    public static final String BLOCKING_ERRORS = "blockingErrors";
    public static final String RECORD_ELABORATED = "recordElaborated";
    public static final String ENTITY_NAME = "entityName";
    
    public static final String USER_LOGIN = "userLogin";
    public static final String USER_LOGIN_ID = "userLoginId";
    public static final String SERVICE_NAME = "serviceName";
    public static final String MESSAGES = "messages";
    public static final String SERVICE_TYPE_ID = "serviceTypeId";
    public static final String SESSION_ID = "sessionId";
    public static final String ELAB_REF1 = "elabRef1";
    public static final String LOG_MESSAGE = "logMessage";
    public static final String DESCRIPTION = "description";
    public static final String ENABLE_LOG_INFO = "enableLogInfo";
    public static final String LOG_DATE = "logDate";
    public static final String LOG_END_DATE = "logEndDate";
    
    public static final String SERVICE_PARAMETERS = "serviceParameters";

    public static final String LOG_TYPE_DEBUG = "LOG_DEBUG";
    public static final String LOG_TYPE_ERROR = "LOG_ERR";
    public static final String LOG_TYPE_WARN = "LOG_WARN";
    public static final String LOG_TYPE_INFO = "LOG_INFO";
    public static final String CODE_GENERIC = "CODE_GENERIC";
    public static final String LOGGED_SERVICE_INTERFACE = "loggedServiceInterface";

    public static final int TRANSACTION_TIMEOUT_DEFAULT = 7200;
    public static final int TRANSACTION_TIMEOUT = 28800;
    
    public static final int MAX_LENGHT_STRING = 20;

    public static final String JOB_LOGGER = "jobLogger";

    public static final String LOCALE = "locale";
    public static final String TIME_ZONE = "timeZone";

    
    private ServiceLogger() {
    }

    /**
     * Controlla se il servizio implementa l'interfaccia
     * 
     * @param name
     * @return
     */
    public static boolean checkIntfImplemented(DispatchContext dctx, String intfName, String serviceName) {
        try {
            ModelService ms = dctx.getModelService(serviceName);
            Set<ModelServiceIface> intfSet = ms.implServices;
            if (intfSet != null) {
                for (Iterator<ModelServiceIface> it = intfSet.iterator(); it.hasNext();) {
                    if (intfName.equalsIgnoreCase(it.next().getService())) {
                        return true;
                    }
                }
            }
        } catch (GenericServiceException e) {
        }
        return false;
    }

    /**
     * Creazione Mappa messaggio
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> makeLog(String logType, String message, String code, String valueRef1, String valueRef2, String valueRef3, String valuePk1) {
        Map res = FastMap.newInstance();
        res.put("logType", logType);
        res.put("logCode", code);
        res.put(LOG_MESSAGE, message);
        res.put("valueRef1", valueRef1);
        res.put("valueRef2", valueRef2);
        res.put("valueRef3", valueRef3);
        res.put("valuePk1", valuePk1);
        return res;
    }

    /**
     * Creazione Mappa per messaggio di errore
     * 
     * @param message
     * @param code
     * @return
     */
    public static Map<String, Object> makeLogError(String message, String code) {
        return makeLogError(message, code, null, null, null);
    }

    /**
     * makeLogError
     * @param message
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     * @return
     */
    public static Map<String, Object> makeLogError(String message, String code, String valueRef1, String valueRef2, String valueRef3) {
        return makeLogError(message, code, valueRef1, valueRef2, valueRef3, null);
    }
    
    /**
     * makeLogError
     * @param message
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     * @param valuePk1
     * @return
     */
    public static Map<String, Object> makeLogError(String message, String code, String valueRef1, String valueRef2, String valueRef3, String valuePk1) {
        return makeLog(ServiceLogger.LOG_TYPE_ERROR, message, code, valueRef1, valueRef2, valueRef3, valuePk1);
    }

    /**
     * Creazione Mappa per messaggio di errore
     * 
     * @param message
     * @param code
     * @return
     */
    public static Map<String, Object> makeLogWarn(String message, String code) {
        return makeLogWarn(message, code, null, null, null);
    }

    /**
     * makeLogWarn
     * @param message
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     * @return
     */
    public static Map<String, Object> makeLogWarn(String message, String code, String valueRef1, String valueRef2, String valueRef3) {
        return makeLog(ServiceLogger.LOG_TYPE_WARN, message, code, valueRef1, valueRef2, valueRef3, null);
    }

    /**
     * Creazione Mappa per messaggio di errore
     * 
     * @param message
     * @param code
     * @return
     */
    public static Map<String, Object> makeLogInfo(String message, String code) {
        return makeLogInfo(message, code, null, null, null);
    }

    /**
     * makeLogInfo
     * @param message
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     * @return
     */
    public static Map<String, Object> makeLogInfo(String message, String code, String valueRef1, String valueRef2, String valueRef3) {
        return makeLog(ServiceLogger.LOG_TYPE_INFO, message, code, valueRef1, valueRef2, valueRef3, null);
    }
    
    /**
     * makeLogDebug
     * @param message
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     * @return
     */
    public static Map<String, Object> makeLogDebug(String message, String code, String valueRef1, String valueRef2, String valueRef3) {
        return makeLogDebug(message, code, valueRef1, valueRef2, valueRef3, null);
    }
    
    /**
     * 
     * @param message
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     * @param valuePk1
     * @return
     */
    public static Map<String, Object> makeLogDebug(String message, String code, String valueRef1, String valueRef2, String valueRef3, String valuePk1) {
        return makeLog(ServiceLogger.LOG_TYPE_DEBUG, message, code, valueRef1, valueRef2, valueRef3, valuePk1);
    }

    /**
     * Esecuzione controllata servizio, <br>
     * requireNewTransaction e transactionTimeout presi dai param
     * 
     * @param dctx
     * @param param
     * @return
     * @throws GenericEntityException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> runService(DispatchContext dctx, Map param) throws GenericEntityException {

        long recordElaborated = 0;
        long warningMessage = 0;
        long blockingErrors = 0;
        String elabRef1 = "";
        Map<String, Object> serviceResult = FastMap.newInstance();
        Timestamp startTimestamp = UtilDateTime.nowTimestamp(); // start elaborazione
        Map<String, Object> res = FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        List<Map<String, Object>> logDataList = FastList.newInstance();

        String serviceName = (String)param.get(SERVICE_NAME);
        String serviceTypeId = (String)param.get(SERVICE_TYPE_ID);
        String sessionId = (String)param.get(SESSION_ID);
        boolean enableLogInfo = true; // default
        GenericValue userLogin = (GenericValue)param.get(USER_LOGIN);

        Map serviceParameters = FastMap.newInstance();
        serviceParameters.put(USER_LOGIN, userLogin);
        
        Locale locale = (Locale)param.get(LOCALE);
        TimeZone timeZone = (TimeZone)param.get(TIME_ZONE);
        serviceParameters.put(LOCALE, locale);
        serviceParameters.put(TIME_ZONE, timeZone);
        
        Map runParams = (Map)param.get(SERVICE_PARAMETERS);
        if (runParams != null) {
            serviceParameters.putAll(runParams);
        }

        // Timeout transazione
        Boolean requireNewTransaction = (Boolean)param.get("requireNewTransaction");
        if(requireNewTransaction == null) {
            requireNewTransaction = false;
        }
        Integer transactionTimeout = (Integer)param.get("transactionTimeout");
        if(transactionTimeout == null) {
            transactionTimeout = -1;
        }
        
        // Controllo serviceTypeId ( se passato )
        if (UtilValidate.isNotEmpty(serviceTypeId)) {
            try {
                GenericValue gv = delegator.findOne("JobLogServiceType", UtilMisc.toMap(SERVICE_TYPE_ID, serviceTypeId), true);
                if (UtilValidate.isNotEmpty(gv)) {
                    enableLogInfo = gv.getBoolean("logInfo");
                } else {
                    logDataList.add(makeLogInfo(String.format("Parametro ServiceTypeId %s non esiste nella tabella JobLogServiceType", serviceTypeId), ServiceLogger.CODE_GENERIC));
                }
            } catch (Exception e) {
                logDataList.add(makeLogInfo(String.format("Errore recupero ServiceTypeId %s nella tabella JobLogServiceType", serviceTypeId), ServiceLogger.CODE_GENERIC));
            }
        }

        // Esecuzione servizio e raccolta dati relativi all'esecuzione
        try {

            serviceResult = dispatcher.runSync(serviceName, serviceParameters, transactionTimeout.intValue(), requireNewTransaction.booleanValue());

            // Il servizio implementa l'interfaccia loggedServiceInterface,
            // quindi ne estraggo i dati
            if (ServiceLogger.checkIntfImplemented(dctx, LOGGED_SERVICE_INTERFACE, serviceName)) {

                List runResults = (List)serviceResult.get("runResults");
                blockingErrors = (Long)serviceResult.get("errorMessages");
                warningMessage = (Long)serviceResult.get("warnMessages");
                recordElaborated = (Long)serviceResult.get("recordElaborated");
                elabRef1 = (String)serviceResult.get(ELAB_REF1);

                for (Object result : runResults) {
                    if (result instanceof Map) {
                        Map resMap = (Map)result;
                        String valueRef1 = "";
                        String valueRef2 = "";
                        String valueRef3 = "";
                        if (UtilValidate.isNotEmpty(resMap.get("valueRef1"))) {
                            valueRef1 = (String)resMap.get("valueRef1");
                        }
                        if (UtilValidate.isNotEmpty(resMap.get("valueRef2"))) {
                            valueRef2 = (String)resMap.get("valueRef2");
                        }
                        if (UtilValidate.isNotEmpty(resMap.get("valueRef3"))) {
                            valueRef3 = (String)resMap.get("valueRef3");
                        }
                        // error
                        logDataList.add(makeLog((String)resMap.get("logType"), (String)resMap.get(LOG_MESSAGE), (String)resMap.get("logCode"), valueRef1, valueRef2, valueRef3, null));
                    }
                }
            } else {

                // il servizio mi ha dato errore
                if (ServiceUtil.isError(serviceResult) || ServiceUtil.isFailure(serviceResult)) {

                    blockingErrors++;
                    logDataList.add(makeLogError(ServiceUtil.getErrorMessage(serviceResult), ServiceLogger.CODE_GENERIC));
                } else {

                    // Il servizio e terminato correttamente // makeLog
                    logDataList.add(makeLogInfo("success", ServiceLogger.CODE_GENERIC));
                }
            }
        } catch (GenericServiceException e) {

            String msg = String.format("Errore lancio servizio %s: %s", serviceName, e.getMessage());
            Debug.logError(msg, "ServiceLogger");
            logDataList.add(makeLogError(msg, ServiceLogger.CODE_GENERIC));
            blockingErrors++;
        }

        String jobLogId = delegator.getNextSeqId("JobLog");

        Map<String, Object> logParameters = FastMap.newInstance();
        logParameters.put(JOB_LOG_ID, jobLogId);
        logParameters.put(SESSION_ID, sessionId);
        logParameters.put(USER_LOGIN, userLogin);
        logParameters.put(SERVICE_NAME, serviceName);
        logParameters.put(SERVICE_TYPE_ID, serviceTypeId);
        logParameters.put(LOG_DATE, startTimestamp);
        logParameters.put(RECORD_ELABORATED, recordElaborated);
        logParameters.put(WARNING_MESSAGES, warningMessage);
        logParameters.put(BLOCKING_ERRORS, blockingErrors);
        logParameters.put(ELAB_REF1, elabRef1);
        logParameters.put(MESSAGES, logDataList);
        logParameters.put(ENABLE_LOG_INFO, enableLogInfo);

        writeLogs(dctx, logParameters, runParams);

        // Setto valori per uscita da job
        res.put(ServiceLogger.JOB_LOG_ID, jobLogId);
        res.put(ServiceLogger.BLOCKING_ERRORS, blockingErrors);
        res.put(ServiceLogger.WARNING_MESSAGES, warningMessage);
        res.put(ServiceLogger.RECORD_ELABORATED, recordElaborated);
        String retMsg = String.format("jobLogId: %s", jobLogId);
        res.putAll(serviceResult);
        res.putAll(ServiceUtil.returnSuccess(retMsg));

        return res;
    }

    /**
     * 
     * @param dctx
     * @param param
     * @throws GenericEntityException
     */
    public static void writeLogs(final DispatchContext dctx, final Map param) throws GenericEntityException {
        writeLogs(dctx, param, null);
    }
    
    /**
     * 
     * @param dctx
     * @param param
     * @param runParams
     * @throws GenericEntityException
     */
    public static void writeLogs(final DispatchContext dctx, final Map param, final Map runParams) throws GenericEntityException {
        new TransactionRunner(ServiceLogger.class.getName(), true, TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                writeLogsEntity(dctx, param);
                String jobLogId = (String)param.get(JOB_LOG_ID);
                writeExecParamsEntity(dctx, jobLogId, runParams);
            }
        }).execute();
    }

    /**
     * 
     * @param dctx
     * @param jobLogger
     * @throws GenericEntityException
     */
    public static void writeLogs(DispatchContext dctx, JobLogger jobLogger) throws GenericEntityException {
        Map<String, Object> map = FastMap.newInstance();
        map.put(MESSAGES, jobLogger.getMessages());
        map.put(SERVICE_NAME, jobLogger.getServiceName());
        map.put(WARNING_MESSAGES, jobLogger.getWarnMessages());
        map.put(RECORD_ELABORATED, jobLogger.getRecordElaborated());
        map.put(SERVICE_TYPE_ID, jobLogger.getServiceTypeId());
        map.put(ELAB_REF1, jobLogger.getElabRef1());
        map.put(DESCRIPTION, jobLogger.getDescription());
        map.put(USER_LOGIN, jobLogger.getUserLogin());

        writeLogs(dctx, map);
    }

    /**
     * 
     * @param dctx
     * @param jobLogId
     * @param runParams
     * @throws GenericEntityException
     */
    public static void writeExecParamsEntity(DispatchContext dctx, String jobLogId, Map runParams) throws GenericEntityException {
       Delegator delegator = dctx.getDelegator();
       // Parametri attuali di esecuzione job
       if (runParams != null) {
           for (Iterator it = runParams.entrySet().iterator(); it.hasNext();) {
               Entry entry = (Entry)it.next();
               if (UtilValidate.isNotEmpty(entry.getValue())) {
                   String parameterName = (String)entry.getKey();
                   if ("userLogin".equalsIgnoreCase(parameterName)) {
                       continue;
                   }
                   GenericValue jep = delegator.makeValue("JobLogJobExecParams");
                   jep.set("jobLogId", jobLogId);
                   jep.set("parameterName", parameterName);
                   String value = entry.getValue().toString();
                   jep.set("parameterValue", value);
                   delegator.create(jep);
               }
           }
       }

    }
    
    /**
     * 
     * @param dctx
     * @param param
     * @throws GenericEntityException
     */
    @SuppressWarnings("unchecked")
    public static void writeLogsEntity(DispatchContext dctx, Map param) throws GenericEntityException {

        long recordElaborated = 0L;
        long warningMessages = 0L;
        long blockingErrors = 0L;
        String jobLogId = "";
        // Map<String, Object> serviceResult = FastMap.newInstance();
        Timestamp logDate = UtilDateTime.nowTimestamp(); // start elaborazione
        Delegator delegator = dctx.getDelegator();
        List<Map<String, Object>> messages = FastList.newInstance();
        String sessionId = (String)param.get(SESSION_ID);
        String serviceName = (String)param.get(SERVICE_NAME);
        String serviceTypeId = (String)param.get(SERVICE_TYPE_ID);
        String description = (String)param.get(DESCRIPTION);
        if (UtilValidate.isNotEmpty(param.get(JOB_LOG_ID))) {
            jobLogId = (String)param.get(JOB_LOG_ID);
        }
        String elabRef1 = "";
        if (UtilValidate.isNotEmpty(param.get(ELAB_REF1))) {
            elabRef1 = (String)param.get(ELAB_REF1);
        }
        GenericValue userLogin = (GenericValue)param.get(USER_LOGIN);
        if (UtilValidate.isNotEmpty(param.get(LOG_DATE))) {
            logDate = (Timestamp)param.get(LOG_DATE);
        }
        if (UtilValidate.isNotEmpty(param.get(RECORD_ELABORATED))) {
            recordElaborated = (Long)param.get(RECORD_ELABORATED);
        }
        if (UtilValidate.isNotEmpty(param.get(WARNING_MESSAGES))) {
            warningMessages = (Long)param.get(WARNING_MESSAGES);
        }
        if (UtilValidate.isNotEmpty(param.get(BLOCKING_ERRORS))) {
            blockingErrors = (Long)param.get(BLOCKING_ERRORS);
        }
        if (UtilValidate.isNotEmpty(param.get(MESSAGES))) {
            messages = (List<Map<String, Object>>)param.get(MESSAGES);
        }

        boolean enableLogInfo = true; // default
        if (UtilValidate.isNotEmpty(param.get(ENABLE_LOG_INFO))) {
            enableLogInfo = (Boolean)param.get(ENABLE_LOG_INFO);
        }

        // Termine esecuzione
        Timestamp endTimestamp = UtilDateTime.nowTimestamp();

        // Creazione job log
        GenericValue jobLog = delegator.makeValue("JobLog");
        if (UtilValidate.isEmpty(jobLogId)) {
            jobLogId = delegator.getNextSeqId("JobLog");
        }
        
        GenericValue localJobLog = null;
        if (UtilValidate.isNotEmpty(jobLogId)) {
            localJobLog = delegator.findOne("JobLog", UtilMisc.toMap(JOB_LOG_ID, jobLogId), false);
        }
        if (UtilValidate.isEmpty(localJobLog)) {
            jobLog.set(JOB_LOG_ID, jobLogId);
            jobLog.set(SESSION_ID, sessionId);
            jobLog.set(USER_LOGIN_ID, userLogin.get(USER_LOGIN_ID));
            jobLog.set(SERVICE_NAME, serviceName);
            // Inserisco il service type id in maniera controllata per evitare
            // errori (Foreign Key)
            if (UtilValidate.isNotEmpty(serviceTypeId)) {
                GenericValue serviceType = delegator.findOne("JobLogServiceType", UtilMisc.toMap(SERVICE_TYPE_ID, serviceTypeId), true);
                if (UtilValidate.isNotEmpty(serviceType)) {
                    jobLog.set(SERVICE_TYPE_ID, serviceType.getString(SERVICE_TYPE_ID));
                }
            }

            jobLog.set(DESCRIPTION, description);
            jobLog.set(LOG_DATE, logDate);
            jobLog.set(RECORD_ELABORATED, recordElaborated);
            jobLog.set(WARNING_MESSAGES, warningMessages);
            jobLog.set(BLOCKING_ERRORS, blockingErrors);
            jobLog.set(ELAB_REF1, elabRef1);

            delegator.create(jobLog);
        } else {
            // update some field
            localJobLog.set(LOG_END_DATE, endTimestamp);
            localJobLog.set(RECORD_ELABORATED, localJobLog.getLong(RECORD_ELABORATED) + recordElaborated);
            localJobLog.set(WARNING_MESSAGES, localJobLog.getLong(WARNING_MESSAGES) + warningMessages);
            localJobLog.set(BLOCKING_ERRORS, localJobLog.getLong(BLOCKING_ERRORS) + blockingErrors);
            localJobLog.store();
        }

        // Dettagli messagge per questo log
        for (Map<String, Object> message : messages) {
            // Se non e abilitata scrittura informativi continuo
            if (!enableLogInfo && ServiceLogger.LOG_TYPE_INFO.equals(message.get("logType"))) {
                continue;
            }
			
			String logMessage = (String)message.get(LOG_MESSAGE);
            String logMessageLong = null;
            if (UtilValidate.isNotEmpty(logMessage) && logMessage.length() > 1999) {
                logMessageLong = logMessage;
                logMessage = logMessage.substring(0, 1999);
            }
            
            GenericValue log = delegator.makeValue("JobLogLog");
            log.set("jobLogLogId", delegator.getNextSeqId("JobLogLog"));
            log.set(JOB_LOG_ID, jobLogId);
            log.set("logTypeEnumId", message.get("logType"));
            log.set("logCode", message.get("logCode"));
            log.set(LOG_MESSAGE, logMessage);
            log.set("logMessageLong", logMessageLong);
            String valueRef1 = (String) message.get("valueRef1");
            if (UtilValidate.isNotEmpty(valueRef1) && valueRef1.length() > 255) {
                valueRef1 = valueRef1.substring(0, 254);
            }
            log.set("valueRef1", valueRef1);
            log.set("valueRef2", message.get("valueRef2"));
            log.set("valueRef3", message.get("valueRef3"));
            log.set("valuePk1", message.get("valuePk1"));
            log = delegator.create(log);
        }

    }
}
