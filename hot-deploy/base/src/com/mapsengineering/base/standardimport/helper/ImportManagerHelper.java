package com.mapsengineering.base.standardimport.helper;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.service.DispatchContext;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.util.TakeOverUtil;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

/**
 * Helper for Import Manager
 *
 */
public class ImportManagerHelper implements ImportManagerConstants {

	private Delegator delegator;
	private GenericValue userLogin;
	private DispatchContext dctx;
    private String sessionId;
    private String jobLogId;
	
	public static final String MODULE = ImportManagerHelper.class.getName();
	
	/**
	 * Constructor
	 * @param dctx
	 * @param context
	 */
	public ImportManagerHelper(DispatchContext dctx, Map<String, Object> context) {
		this.dctx = dctx;
		this.delegator = dctx.getDelegator();
		this.userLogin = (GenericValue) context.get("userLogin");
		this.sessionId = (String) context.get(ServiceLogger.SESSION_ID);
		this.jobLogId = (String) context.get(ServiceLogger.JOB_LOG_ID);
	}
	
	/**
	 * Il metodo serve per aggiungere i log del Db e nella lista
	 * 
	 * @param serviceTypeString
	 * @param startTimestamp
	 * @param entityName
	 * @param recordElaborated
	 * @param blockingErrors
	 * @param messages
	 * @param importedListPKUploadFile
	 * @return
	 */
	public Map<String, Object> onImportAddList(final String serviceTypeString, final Timestamp startTimestamp, final String entityName, final long recordElaborated, final long blockingErrors, final long warningMessages, final List<Map<String, Object>> messages, final List<Map<String, ? extends Object>> importedListPK) {
	    final Map<String, Object> result = FastMap.newInstance();
	    new TransactionRunner(MODULE, true, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                Map<String, Object> resultTrxItem = onImportAddListTrxItem(serviceTypeString, startTimestamp, entityName, recordElaborated, blockingErrors, warningMessages, messages, importedListPK);
                if (resultTrxItem != null) {
                    result.putAll(resultTrxItem);
                }
            }
        }).run();
	    return result;
    }

	private Map<String, Object> onImportAddListTrxItem(String serviceTypeString, Timestamp startTimestamp, String entityName, long recordElaborated, long blockingErrors, long warningMessages, List<Map<String, Object>> messages, List<Map<String, ? extends Object>> importedListPK) {
        String descriptionEntityName = "";
        String localJobLogId = jobLogId;
        if (UtilValidate.isEmpty(jobLogId)) {
            localJobLogId = delegator.getNextSeqId("JobLog");
        }
        try {
            descriptionEntityName = writeLog(serviceTypeString, localJobLogId, entityName, startTimestamp, recordElaborated, blockingErrors, warningMessages, messages);
        } catch (GenericEntityException e) {
            Debug.logInfo("Error writing log: " + e.getMessage(), MODULE);
        }
        return allLogToList(serviceTypeString, localJobLogId, descriptionEntityName, recordElaborated, blockingErrors, warningMessages, importedListPK);
	}
	
	/**
	 * Scrittura dei log
	 * @param serviceTypeString
	 * @param jobLogId
	 * @param entityName
	 * @param startTimestamp
	 * @param recordElaborated
	 * @param blockingErrors
	 * @param messages
	 * @return
	 * @throws GenericEntityException
	 */
	protected String writeLog(String serviceTypeString, String localJobLogId, String entityName, Timestamp startTimestamp, long recordElaborated, long blockingErrors, long warningMessages, List<Map<String, Object>> messages) throws GenericEntityException{
    	Map<String, Object> logParameters = FastMap.newInstance();
    	String descriptionEntityName = "";
    	String serviceName = "";
    	
    	if (serviceTypeString.equals(SERVICE_TYPE_UPLOAD_FILE)){
    		serviceName = SERVICE_NAME_UPLOAD_FILE;
		} else {
			serviceName = SERVICE_NAME;
		}
    	
    	logParameters.put(ServiceLogger.USER_LOGIN, userLogin);
		logParameters.put(ServiceLogger.LOG_DATE, startTimestamp);
		logParameters.put(ServiceLogger.RECORD_ELABORATED, recordElaborated);
		logParameters.put(ServiceLogger.WARNING_MESSAGES, warningMessages);
		logParameters.put(ServiceLogger.BLOCKING_ERRORS, blockingErrors);
		logParameters.put(ServiceLogger.JOB_LOG_ID, localJobLogId);
        logParameters.put(ServiceLogger.MESSAGES, messages);        
		logParameters.put(ServiceLogger.SERVICE_NAME, serviceName);
		
		if (UtilValidate.isNotEmpty(sessionId)){
		    logParameters.put(ServiceLogger.SESSION_ID, sessionId);
        }
		
		String nameServiceType = serviceTypeString + StringUtils.upperCase(entityName);
		nameServiceType = nameServiceType.substring(0, nameServiceType.length() > ServiceLogger.MAX_LENGHT_STRING ? ServiceLogger.MAX_LENGHT_STRING : nameServiceType.length());
		
		// Check valid service type id (Foreign Key)
		GenericValue serviceType = delegator.findOne("JobLogServiceType", UtilMisc.toMap(ServiceLogger.SERVICE_TYPE_ID, nameServiceType), true);
		if (UtilValidate.isNotEmpty(serviceType)) {
			descriptionEntityName = serviceType.getString(ServiceLogger.DESCRIPTION);
			logParameters.put(ServiceLogger.SERVICE_TYPE_ID, serviceType.getString(ServiceLogger.SERVICE_TYPE_ID));
		}

		ServiceLogger.writeLogs(dctx, logParameters);
				
		return descriptionEntityName;
    }
    
    /**
     * 
     * @param serviceTypeString
     * @param localJobLogId
     * @param descriptionEntityName
     * @param recordElaborated
     * @param blockingErrors
     * @param warningMessages
     * @param importedListPKUploadFile
     * @return
     */
    protected Map<String, Object> allLogToList(String serviceTypeString, String localJobLogId, String descriptionEntityName, long recordElaborated, long blockingErrors, long warningMessages, List<Map<String, ? extends Object>> importedListPK){
    	
    	Map<String, Object> addJobJogResult = FastMap.newInstance();
		
		addJobJogResult.put(ServiceLogger.JOB_LOG_ID, localJobLogId);		
		addJobJogResult.put(ServiceLogger.ENTITY_NAME, descriptionEntityName);
		addJobJogResult.put("importedListPK", importedListPK);
		
		addJobJogResult.put(ServiceLogger.BLOCKING_ERRORS, blockingErrors);
	    addJobJogResult.put(ServiceLogger.RECORD_ELABORATED, recordElaborated);
	    addJobJogResult.put(ServiceLogger.WARNING_MESSAGES, warningMessages);
        
		return addJobJogResult;
    	
    }
    
    /**
     * Assicura che il record esterno abbia un id valorizzato, se nullo o vuoto assegna un valore univoco.
     * @param externalValue record esterno
     * @param externalValuePersist se true aggiorna il record esterno sul database
     * @return valore dell'id
     * @throws GeneralException
     */
    public String ensureExternalValueId(GenericValue externalValue, boolean externalValuePersist) throws GeneralException {
        String id = externalValue.getString(RECORD_FIELD_ID);
        if (UtilValidate.isEmpty(id)) {
            id = delegator.getNextSeqId(externalValue.getEntityName());
            externalValue.set(RECORD_FIELD_ID, RECORD_ID_PREFIX + id);
            if (externalValuePersist) {
                externalValue.store();
            }
        }
        return id;
    }
	
    /**
     * Ottiene un singolo record esterno in base alla chiave esterna primaria o logica.
     * 
     * @param extKey chiave primaria o logica del record esterno
     * @return record esterno
     * @throws GenericEntityException 
     * @throws GeneralException
     */
    public GenericValue findExternalValue(String entityName, Map<String, ? extends Object> extKey) throws GeneralException {
        List<GenericValue> values = delegator.findByAnd(entityName, extKey);
        // Verifica che ci sia al massimo un record
        if (values != null && values.size() > 1) {
            throw new ImportException(entityName, TakeOverUtil.toString(extKey), "not unique");
        }
        return EntityUtil.getFirst(values);
    }

    /**
     * Ottiene una lista di record esterni in base alla chiave esterna parziale.
     * 
     * @param extKey chiave parziale del record esterno
     * @return una lista di record esterni
     * @throws GenericEntityException 
     * @throws GeneralException
     */
    public List<GenericValue> findExternalValues(String entityName, Map<String, ? extends Object> extKey) throws GeneralException {
        return delegator.findByAnd(entityName, extKey);
    } 

    /**
     * Return Condition for status 
     * @return
     */
    public EntityCondition buildReadCondition() {
        List<String> notInStatusList = FastList.newInstance();
        notInStatusList.add(RECORD_STATUS_OK);
        notInStatusList.add(RECORD_STATUS_KO);
        notInStatusList.add(RECORD_STATUS_LOCKED);
        return EntityCondition.makeCondition(EntityCondition.makeCondition(RECORD_FIELD_STATUS, EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition(RECORD_FIELD_STATUS, EntityOperator.NOT_IN, notInStatusList));
    }

    /**
     * Return EntityFindOptions with maxRows = 10
     * @return
     */
    public EntityFindOptions getFindOptions() {
        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setMaxRows(MAX_ROWS);
        return findOptions;
    }

    /**
     * Return order by id
     * @return
     */
    public List<String> getOrderBy() {
        List<String> orderBy = FastList.newInstance();
        orderBy.add(RECORD_FIELD_ID);
        return orderBy;
    }
   
    /**
     * @return the jobLogId
     */
    public String getJobLogId() {
        return jobLogId;
    }

    /**
     * @param jobLogId the jobLogId to set
     */
    public void setJobLogId(String jobLogId) {
        this.jobLogId = jobLogId;
    }
   
}