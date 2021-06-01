package com.mapsengineering.base.standardimport.common;

import org.ofbiz.base.util.GeneralException;

import com.mapsengineering.base.util.JobLogLog;

public class ImportException extends GeneralException {

    private static final long serialVersionUID = 1L;

    private String entityName;
    private String id;
    private String message;
    private String logCode;
    private String valueRef1;
    private String valueRef2;
    /**
     * Contains parameters json
     */
    private String valueRef3;

    public ImportException(String entityName, String id, String message) {
        super();
        this.entityName = entityName;
        this.id = id;
        this.message = message;
    }
    
    public ImportException(String entityName, String id, JobLogLog jobLogLog) {
        super();
        this.entityName = entityName;
        this.id = id;
        this.message = jobLogLog.getLogMessage();
        this.logCode = jobLogLog.getLogCode();
        this.valueRef3 = jobLogLog.getParametersJSON();
    }
    
    public ImportException(Throwable t, String entityName, String id, JobLogLog jobLogLog) {
        super(t);
        this.entityName = entityName;
        this.id = id;
        this.message = jobLogLog.getLogMessage();
        this.logCode = jobLogLog.getLogCode();
        this.valueRef3 = jobLogLog.getParametersJSON();
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    public String getEntityName() {
        return entityName;
    }

    public String getId() {
        return id;
    }

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
     * @return the valueRef1
     */
    public String getValueRef1() {
        return valueRef1;
    }

    /**
     * @param valueRef1 the valueRef1 to set
     */
    public void setValueRef1(String valueRef1) {
        this.valueRef1 = valueRef1;
    }

    /**
     * @return the valueRef2
     */
    public String getValueRef2() {
        return valueRef2;
    }

    /**
     * @param valueRef2 the valueRef2 to set
     */
    public void setValueRef2(String valueRef2) {
        this.valueRef2 = valueRef2;
    }

    /**
     * @return the valueRef3
     */
    public String getValueRef3() {
        return valueRef3;
    }

    /**
     * @param valueRef3 the valueRef3 to set
     */
    public void setValueRef3(String valueRef3) {
        this.valueRef3 = valueRef3;
    }
}
