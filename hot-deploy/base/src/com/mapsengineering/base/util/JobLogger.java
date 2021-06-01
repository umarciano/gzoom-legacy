package com.mapsengineering.base.util;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants.MessageCode;

/**
 * Job Logger
 *
 */
public class JobLogger {

    public static final String ERROR_MESSAGES = "errorMessages";
    
    private List<Map<String, Object>> messages;

    private long warnMessages;

    private long errorMessages;

    private long recordElaborated;

    private final String module;

    private String serviceName;

    private String serviceTypeId;

    private String elabRef1;

    private String description;

    private GenericValue userLogin;

    /**
     * Constructor
     * @param module
     */
    public JobLogger(String module) {
        this.module = module;
        this.messages = FastList.newInstance();
    }

    /** 
     * 
     * @param module
     * @param serviceName
     * @param serviceTypeId
     * @param elabRef1
     * @param description
     * @param userLogin
     */
    public JobLogger(String module, String serviceName, String serviceTypeId, String elabRef1, String description, GenericValue userLogin) {
        this.module = module;
        this.messages = FastList.newInstance();
        this.serviceName = serviceName;
        this.serviceTypeId = serviceTypeId;
        this.elabRef1 = elabRef1;
        this.description = description;
        this.userLogin = userLogin;
    }

    /**
     * 
     * @param msg
     */
    public void printLogInfo(String msg) {
        Debug.logInfo(msg, module);
        messages.add(ServiceLogger.makeLogInfo(msg, ""));
    }
    
    /**
     * 
     * @param msg
     * @param key
     */
    public void printLogInfo(String msg, String key) {
        Debug.logInfo(msg, module);
        messages.add(ServiceLogger.makeLogInfo(msg, "", key, "", ""));
    }
    
    /**
     * 
     * @param msg
     * @param code
     * @param valueRef1
     * @param valueRef2
     * @param valueRef3
     */
    public void printLogInfo(String msg, String code, String valueRef1, String valueRef2, String valueRef3) {
        Debug.logInfo(msg, module);
    	messages.add(ServiceLogger.makeLogInfo(msg, code, valueRef1, valueRef2, valueRef3));
    }
    
    /**
     * 
     * @param msg
     */
    public void printLogError(String msg) {
        Debug.logError(msg, module);
        messages.add(ServiceLogger.makeLogError(msg, ""));
        errorMessages++;
    }
    
    /**
     * 
     * @param msg
     * @param key
     */
    public void printLogError(String msg, String key) {
        Debug.logError(msg, module);
        messages.add(ServiceLogger.makeLogError(msg, "", key, "", ""));
        errorMessages++;
    } 

    /**
     * 
     * @param e
     * @param msg
     */
    public void printLogError(Exception e, String msg) {
        Debug.logError(e, module);
        messages.add(ServiceLogger.makeLogError(String.format(msg + " " + e.getMessage()), ""));
        errorMessages++;
    }
    
    /**
     * 
     * @param msg
     */
    public void printLogWarn(String msg) {
        Debug.logWarning(msg, module);
        messages.add(ServiceLogger.makeLogWarn(msg, MessageCode.WARNING.toString()));
        warnMessages++;
    }
    
    /**
     * 
     * @param msg
     * @param key
     */
    public void printLogWarn(String msg, String key) {
        Debug.logWarning(msg, module);
        messages.add(ServiceLogger.makeLogWarn(msg, MessageCode.WARNING.toString(), key, "", ""));
        warnMessages++;
    }    

    /**
     * 
     * @return
     */
    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    /**
     * 
     * @param messages
     */
    public void addMessages(List<Map<String, Object>> messages) {
        this.messages.addAll(messages);
    }

    /**
     * 
     * @param message
     */
    public void addMessage(Map<String, Object> message) {
        if (UtilValidate.isNotEmpty(message)) {
            Debug.logInfo((String)message.get(ServiceLogger.LOG_MESSAGE), module);
            this.messages.add(message);
        }
    }

    /**
     * 
     * @return
     */
    public long getWarnMessages() {
        return warnMessages;
    }

    /**
     * 
     * @param warnMessages
     */
    public void addWarnMessages(long warnMessages) {
        this.warnMessages += warnMessages;
    }

    /**
     * 
     * @return
     */
    public long getErrorMessages() {
        return errorMessages;
    }

    /**
     * 
     * @param errorMessages
     */
    public void addErrorMessages(long errorMessages) {
        this.errorMessages += errorMessages;
    }

    /**
     * 
     * @return
     */
    public long getRecordElaborated() {
        return recordElaborated;
    }

    /**
     * 
     * @param recordElaborated
     */
    public void addRecordElaborated(long recordElaborated) {
        this.recordElaborated += recordElaborated;
    }

    /**
     * 
     */
    public void incrementRecordElaborated() {
        recordElaborated++;
    }

    /**
     * 
     * @return
     */
    public String getModule() {
        return module;
    }

    /**
     * 
     * @param jobLogger
     */
    public void mergeData(JobLogger jobLogger) {
        addRecordElaborated(jobLogger.getRecordElaborated());
        addErrorMessages(jobLogger.getErrorMessages());
        addWarnMessages(jobLogger.getWarnMessages());
        addMessages(jobLogger.getMessages());
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public String getElabRef1() {
        return elabRef1;
    }

    public GenericValue getUserLogin() {
        return userLogin;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
