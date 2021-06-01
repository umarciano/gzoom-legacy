package com.mapsengineering.base.standardimport.common;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.MessageUtil;

/**
 * BaseImportManager
 *
 */
public class BaseImportManager implements ImportManagerConstants{

    public static final String RESOURCE_LABEL = "StandardImportUiLabels";
    
	private Delegator delegator;
    private LocalDispatcher dispatcher;
    private GenericValue userLogin;
    private Map<String, Object> context;
    private Locale locale;
    private TimeZone timeZone;
    private List<Map<String, Object>> messages;
    private Map<String, Object> result = ServiceUtil.returnSuccess();
    protected List<Map<String, Object>> resultList = FastList.newInstance();
    // List primary keys of imported records
    private List<Map<String, ? extends Object>> importedListPK;

    private long recordElaborated = 0L;
    private long blockingErrors = 0L;
    private long warningMessages = 0L;
 
    protected BaseImportManager(DispatchContext dctx, Map<String, Object> context) {
    	this.delegator = dctx.getDelegator();
        this.dispatcher = dctx.getDispatcher();
        this.context = context;
        this.userLogin = (GenericValue)context.get("userLogin");
        this.locale = (Locale)context.get("locale");
        this.timeZone = (TimeZone)context.get("timeZone");
        this.importedListPK = FastList.newInstance();
        this.messages = FastList.newInstance();
    }
    
    /**
	 * Pulizia delle variabili glibali    
	 */
    protected void cleanParameters() {
        messages = FastList.newInstance();
        this.warningMessages = 0L;
        this.blockingErrors = 0L;
        this.recordElaborated = 0L;
        this.importedListPK.clear();
    }
    
    /**
     * 
     * @param message
     * @param module
     * @param key
     */
    public void addLogInfo(String message, String module, String key) {
        Debug.logInfo(message, module);
        addMessage(message, key);
    }

    /**
     * 
     * @param message
     * @param module
     * @param key
     */
    public void addLogWarning(String message, String module, String key) {
        warningMessages++;
        Debug.logWarning(message, module);
        this.messages.add(ServiceLogger.makeLogWarn(message, MessageCode.WARNING.toString(), key, "", ""));
    }

    /**
     * 
     * @param jobLogLog
     * @param module
     * @param key
     */
    public void addLogWarning(JobLogLog jobLogLog, String module, String key) {
        warningMessages++;
        Debug.logWarning(jobLogLog.getLogMessage(), module);
        this.messages.add(ServiceLogger.makeLogWarn(jobLogLog.getLogMessage(), jobLogLog.getLogCode(), key, "", ""));
    }

    /**
     * 
     * @param message
     * @param key
     */
    protected void addMessage(String message, String key) {
        this.messages.add(ServiceLogger.makeLogInfo(message, MessageCode.INFO_GENERIC.toString(), key, "", ""));
    }

    /**
     * 
     * @param t
     * @param message
     * @param module
     */
    public void addLogError(Throwable t, String message, String module) {
        addLogError(t, message, module, null);
    }

    /**
     * If t:ImportException, add logCode, and valueRef1, valueRef2, valueRef3
     * @param t
     * @param message
     * @param module
     * @param key
     */
    public void addLogError(Throwable t, String message, String module, String key) {
    	Debug.logError(t, message, module);
    	String code = MessageCode.ERROR_BLOCKING.toString();
    	String valueRef2 = "";
    	String valueRef3 = "";
    	String debugLogMessage = "";
        String msg = message;
    	if (t instanceof ImportException) {
    	    String codeStr = ((ImportException)t).getLogCode();
    	    if (UtilValidate.isNotEmpty(codeStr)) {
    	        code = codeStr;
    	    }
    	    
    	    /**
    	     * Override key
    	     */
    	    String valueRef1 = ((ImportException)t).getValueRef1();
            if (UtilValidate.isNotEmpty(valueRef1) && UtilValidate.isEmpty(key)) {
                key = valueRef1;
            }
            if (UtilValidate.isNotEmpty(((ImportException)t).getValueRef2())) {
                valueRef2 = ((ImportException)t).getValueRef2();
            }
            if (UtilValidate.isNotEmpty(((ImportException)t).getValueRef3())) {
                valueRef3 = ((ImportException)t).getValueRef3();
            }
            debugLogMessage = "Error during import of table " + ((ImportException)t).getEntityName() + " row with id " + ((ImportException)t).getId();
    	}
    	if(t != null) {
            msg = message + " " + debugLogMessage + t.getMessage();
        }
        blockingErrors++;
        if(t != null) {
            this.messages.add(ServiceLogger.makeLogError(t.getMessage(), code, key, valueRef2, valueRef3, key));
        }
        this.messages.add(ServiceLogger.makeLogDebug(msg, code, key, valueRef2, valueRef3, key)); // LOG DEBUG (TECNICO)
    }
    
    /**
     * 
     * @param message
     * @param module
     */
    public void addLogInfo(String message, String module) {
        Debug.logInfo(message, module);
        this.messages.add(ServiceLogger.makeLogInfo(message, MessageCode.INFO_GENERIC.toString()));
    }


    /**
     * 
     * @param message
     * @param module
     */
    public void addLogError(String message, String module) {
    	addLogError(null, message, module, null);
    }

    /**
     * 
     * @param message
     * @param module
     * @param key
     */
    public void addLogError(String message, String module, String key) {
    	addLogError(null, message, module, key);
    }

    /**
     * LogInfo with 
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     */
    public void addLogInfo(String logCode, String logMessage, String key, String valueRef2, String valueRef3, String module) {
        Debug.logInfo(logMessage, module);
        valueRef3 = getValueRef3(valueRef3);
        this.messages.add(ServiceLogger.makeLogInfo(logMessage, logCode, key, valueRef2, valueRef3));
    }
    
    /**
     * LogDebug with 
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     * @param module
     */
    public void addLogDebug(String logCode, String logMessage, String key, String valueRef2, String valueRef3, String module) {
        Debug.log(logMessage, module);
        valueRef3 = getValueRef3(valueRef3);
        this.messages.add(ServiceLogger.makeLogDebug(logMessage, logCode, key, valueRef2, valueRef3));
    }

    /**
     * LogInfo with 
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     * @param module
     */
    public void addLogWarning(String logCode, String logMessage, String key, String valueRef2, String valueRef3, String module) {
        Debug.logWarning(logMessage, module);
        setWarningMessages(getWarningMessages() + 1);
        valueRef3 = getValueRef3(valueRef3);
        this.messages.add(ServiceLogger.makeLogWarn(logMessage, logCode, key, valueRef2, valueRef3));
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
     * @param module
     */
    public void addLogError(Throwable t, String logCode, String logMessage, String key, String valueRef2, String valueRef3, String module) {
        setBlockingErrors(getBlockingErrors() + 1);
        logMessage += MessageUtil.getExceptionMessage(t);
        Debug.logError(logMessage, module);
        valueRef3 = getValueRef3(valueRef3);
        if(t != null) {
            this.messages.add(ServiceLogger.makeLogError(t.getMessage(), logCode, key, valueRef2, valueRef3));
        }
        this.messages.add(ServiceLogger.makeLogDebug(logMessage, logCode, key, valueRef2, valueRef3));
    }
    
    /**
     * Substring of valueRef3 and concat ...
     * @param valueRef3
     * @return
     */
    private String getValueRef3(String valueRef3) {
        if (UtilValidate.isNotEmpty(valueRef3) && valueRef3.length() > GenericService.VALUE_REF3_LENGHT) {
            valueRef3 = valueRef3.substring(0, GenericService.VALUE_REF3_LENGHT - 4) + ("...");
        }
        return valueRef3;
    }
    
    /**
     * Ritorna il LocalDispatcher
     * @return LocalDispatcher
     */
    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Ritorna ild elegator
     * @return delegator
     */
    public Delegator getDelegator() {
        return delegator;
    }

    /**
     * Ritorna il locale
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Ritorna il timeZone
     * @return timeZone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 
     * @return userLogin
     */
    public GenericValue getUserLogin() {
        return userLogin;
    }

    /**
     * Ritorna la lisya di chiavi primarie
     * @return importedListPK
     */
    public List<Map<String, ? extends Object>> getImportedListPK() {
        return importedListPK;
    }

    /**
     * 
     * @return result
     */
    public Map<String, Object> getResult() {
        return result;
    }

    /**
     * Get Context
     * @return
     */
    public Map<String, Object> getContext() {
        return context;
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
	public void setMessages(List<Map<String, Object>> messages) {
		this.messages = messages;
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
	public void setRecordElaborated(long recordElaborated) {
		this.recordElaborated = recordElaborated;
	}

	/**
	 * 
	 * @return
	 */
	public long getBlockingErrors() {
		return blockingErrors;
	}

	/**
	 * 
	 * @param blockingErrors
	 */
	public void setBlockingErrors(long blockingErrors) {
		this.blockingErrors = blockingErrors;
	}

	/**
	 * 
	 * @param delegator
	 */
	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	/**
	 * 
	 * @param dispatcher
	 */
	public void setDispatcher(LocalDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	/**
	 * 
	 * @param userLogin
	 */
	public void setUserLogin(GenericValue userLogin) {
		this.userLogin = userLogin;
	}

	/**
	 * 
	 * @param context
	 */
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	/**
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * 
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * 
	 * @param importedListPK
	 */
	public void setImportedListPK(List<Map<String, ? extends Object>> importedListPK) {
		this.importedListPK = importedListPK;
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
}
