package com.mapsengineering.base.util;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;

import com.mapsengineering.base.util.JSONUtils;

/**
 * Job Log Log
 *
 */
public class JobLogLog {

    private String logCode;

    private String logMessage;

    private Map<String, Object> parameters;

    /**
     * @return the logCode
     */
    public String getLogCode() {
        return logCode;
    }

    /**
     * @param logCode the logCode to set
     */
    public void setLogCode(String logCode) {
        this.logCode = logCode;
    }

    /**
     * @return the logMessage
     */
    public String getLogMessage() {
        return logMessage;
    }

    /**
     * @param logMessage the logMessage to set
     */
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    /**
     * Return Map
     * @return
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Return Json
     * @return
     */
    public String getParametersJSON() {
        if (parameters == null) {
            return null;
        }
        return JSONUtils.toJson(parameters);
    }

    /**
     * Initialize record JobLogLog
     * @param logCode: max 20 characters, because JobLogLog.logCode is varchar(20)
     * @param logMessage: message with parameters
     * @param parameters: map
     * @return JobLogLog
     */
    public JobLogLog initLogCode(String logCode, String logMessage, Map<String, Object> parameters) {
        this.logCode = logCode;
        this.logMessage = logMessage;
        this.parameters = parameters;
        return this;
    }
    
    /**
     * Initialize record JobLogLog
     * @param resourceName: name of file .xml with labels
     * @param logCode: max 20 characters, because JobLogLog.logCode is varchar(20)
     * @param parameters: map
     * @param locale
     * @return JobLogLog
     */
    public JobLogLog initLogCode(String resourceName, String logCode, Map<String, Object> parameters, Locale locale) {
        this.logCode = logCode;
        this.parameters = parameters;
        this.logMessage = UtilProperties.getMessage(resourceName, logCode, parameters, locale);
        return this;
    }
}
