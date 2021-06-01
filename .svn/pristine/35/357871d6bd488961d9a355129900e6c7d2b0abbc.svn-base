/**
 * StandardImportServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mapsengineering.standardImport.ws;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.OfbizWsContext;
import com.mapsengineering.standardImport.utils.ServiceCodeEnum;
import com.mapsengineering.standardImport.utils.StandardImportAllocationEnum;
import com.mapsengineering.standardImport.utils.StandardImportEnum;
import com.mapsengineering.standardImport.utils.StandardImportOrganizationEnum;
import com.mapsengineering.standardImport.utils.StandardImportPersonEnum;

public class StandardImportServiceSoapBindingImpl implements com.mapsengineering.standardImport.ws.StandardImportServiceSoap{
	
	public static final String MODULE = StandardImportServiceSoapBindingImpl.class.getName();
	
	public static final String SERVICE_NAME = "standardImportService";
    public static final String SERVICE_TYPE_ID = "STR_IMPORT_EXT_WSDL";
	public static final String RESOURE_LABEL = "StandardImportUiLabels";
	// TODO gestire locale
	private Locale locale = Locale.ITALY;
	private JobLogger jobLogger = new JobLogger(MODULE);
	private OfbizWsContext context = new OfbizWsContext();
	private String sessionId = getSessionId();
	private Timestamp startTimestamp = UtilDateTime.nowTimestamp();

	public com.mapsengineering.standardImport.ws.StandardImportWSResponse setOrganizationInterfaceExt(com.mapsengineering.standardImport.ws.OrganizationInterfaceExt[] parameters) throws java.rmi.RemoteException {
        Debug.log("StandardImportServiceSoapBindingImpl setOrganizationInterfaceExt");
        
        addLogInfo("START_IMPORT_SER_ORG", null);
        
        int recordElaborated = 0;
                
        try {	
        	// controllo validita' parametri
            if (UtilValidate.isEmpty(parameters)) {
            	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, null);
            }
        	
        	for (OrganizationInterfaceExt requestParam: parameters) {
		        // verifica campi obbligatori
	        	StandardImportWSResponse mandatoryMis = checkMandatoryParams(requestParam, StandardImportOrganizationEnum.values(), recordElaborated);
		        
		        if (UtilValidate.isNotEmpty(mandatoryMis)) {
		        	return mandatoryMis;
		        }
		        		        
		        // creazione record tabella
		        GenericValue gv = createGenericValueFromParameters(ImportManagerConstants.ORGANIZATION_INTERFACE_EXT, requestParam, StandardImportOrganizationEnum.values());
		        
		        Map<String, Object> elaborateStrutturaParams = UtilMisc.toMap(StandardImportOrganizationEnum.dataSource.name(), requestParam.getDataSource(), StandardImportOrganizationEnum.refDate.name(), 
		        		requestParam.getRefDate(), StandardImportOrganizationEnum.orgCode.name(), requestParam.getOrgCode());
		        
		        // controlla se esiste gia' una struttura precedente
		        List<EntityCondition> orgConditions = new ArrayList<EntityCondition>();
		        orgConditions.add(EntityCondition.makeCondition(StandardImportOrganizationEnum.dataSource.name(), requestParam.getDataSource()));
		        orgConditions.add(EntityCondition.makeCondition(StandardImportOrganizationEnum.refDate.name(), requestParam.getRefDate()));
		        orgConditions.add(EntityCondition.makeCondition(StandardImportOrganizationEnum.orgCode.name(), requestParam.getOrgCode()));
		        if (UtilValidate.isEmpty(context.getDelegator().findList(ImportManagerConstants.ORGANIZATION_INTERFACE_EXT, EntityCondition.makeCondition(orgConditions), null, null, null, false))) {
		        	// inserimento struttura
		        	addLogInfo("START_IMPORT_ORG_CRE", elaborateStrutturaParams);
		        	gv.create();
		        	recordElaborated++;
		        } else {
		        	// aggiornamento struttura
		        	addLogInfo("START_IMPORT_ORG_AGG", elaborateStrutturaParams);
		            gv.store();
		            recordElaborated++;
		        }
        	}
        } catch (Exception ex) {
        	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, ex.toString());
        } finally {
        	addLogInfo("END_IMPORT_SER_ORG", null);
        	writeLog(startTimestamp, context);
            context.close();
        }
        
        return new StandardImportWSResponse(new StandardImportResponse("0", "OK", recordElaborated, 0, 0));
    }

    public com.mapsengineering.standardImport.ws.StandardImportWSResponse setPersonInterfaceExt(com.mapsengineering.standardImport.ws.PersonInterfaceExt[] parameters) throws java.rmi.RemoteException {
    	Debug.log("StandardImportServiceSoapBindingImpl setPersonInterfaceExt");
    	
    	addLogInfo("START_IMPORT_SER_PER", null);
    	
    	int recordElaborated = 0;
                
    	try {	
        	// controllo validita' parametri
            if (UtilValidate.isEmpty(parameters)) {
            	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, null);
            }
        	
        	for (PersonInterfaceExt requestParam: parameters) {
		        // verifica campi obbligatori
	        	StandardImportWSResponse mandatoryMis = checkMandatoryParams(requestParam, StandardImportPersonEnum.values(), recordElaborated);
		        
		        if (UtilValidate.isNotEmpty(mandatoryMis)) {
		        	return mandatoryMis;
		        }
		        		        
		        // creazione record tabella
		        GenericValue gv = createGenericValueFromParameters(ImportManagerConstants.PERSON_INTERFACE_EXT, requestParam, StandardImportPersonEnum.values());
		        
		        Map<String, Object> elaborateStrutturaParams = UtilMisc.toMap(StandardImportPersonEnum.dataSource.name(), requestParam.getDataSource(), StandardImportPersonEnum.refDate.name(), 
		        		requestParam.getRefDate(), StandardImportPersonEnum.personCode.name(), requestParam.getPersonCode());
		        
		        // controlla se esiste gia' una persona precedente
		        List<EntityCondition> persConditions = new ArrayList<EntityCondition>();
		        persConditions.add(EntityCondition.makeCondition(StandardImportPersonEnum.dataSource.name(), requestParam.getDataSource()));
		        persConditions.add(EntityCondition.makeCondition(StandardImportPersonEnum.refDate.name(), requestParam.getRefDate()));
		        persConditions.add(EntityCondition.makeCondition(StandardImportPersonEnum.personCode.name(), requestParam.getPersonCode()));
		        if (UtilValidate.isEmpty(context.getDelegator().findList(ImportManagerConstants.PERSON_INTERFACE_EXT, EntityCondition.makeCondition(persConditions), null, null, null, false))) {
		        	// inserimento struttura
		        	addLogInfo("START_IMPORT_PER_CRE", elaborateStrutturaParams);
		        	gv.create();
		        	recordElaborated++;
		        } else {
		        	// aggiornamento struttura
		        	addLogInfo("START_IMPORT_PER_AGG", elaborateStrutturaParams);
		            gv.store();
		            recordElaborated++;
		        }
        	}
    	} catch (Exception ex) {
        	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, ex.toString());
        } finally {
        	addLogInfo("END_IMPORT_SER_PER", null);
        	writeLog(startTimestamp, context);
            context.close();
        }
    	
        return new StandardImportWSResponse(new StandardImportResponse("0", "OK", recordElaborated, 0, 0));
    }

    public com.mapsengineering.standardImport.ws.StandardImportWSResponse setAllocationInterfaceExt(com.mapsengineering.standardImport.ws.AllocationInterfaceExt[] parameters) throws java.rmi.RemoteException {
    	Debug.log("StandardImportServiceSoapBindingImpl setAllocationInterfaceExt");
    	
    	addLogInfo("START_IMPORT_SER_ALL", null);
    	
    	int recordElaborated = 0;
        
    	try {	
        	// controllo validita' parametri
            if (UtilValidate.isEmpty(parameters)) {
            	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, null);
            }
        	
        	for (AllocationInterfaceExt requestParam: parameters) {
		        // verifica campi obbligatori
	        	StandardImportWSResponse mandatoryMis = checkMandatoryParams(requestParam, StandardImportAllocationEnum.values(), recordElaborated);
		        
		        if (UtilValidate.isNotEmpty(mandatoryMis)) {
		        	return mandatoryMis;
		        }
		        		        
		        // creazione record tabella
		        GenericValue gv = createGenericValueFromParameters(ImportManagerConstants.ALLOCATION_INTERFACE_EXT, requestParam, StandardImportAllocationEnum.values());
		        
		        Map<String, Object> elaborateStrutturaParams = UtilMisc.toMap(StandardImportAllocationEnum.dataSource.name(), requestParam.getDataSource(), StandardImportAllocationEnum.refDate.name(), 
		        		requestParam.getRefDate(), StandardImportAllocationEnum.personCode.name(), requestParam.getPersonCode(), StandardImportAllocationEnum.allocationOrgCode.name(), requestParam.getAllocationOrgCode(),
		        		StandardImportAllocationEnum.allocationFromDate.name(), requestParam.getAllocationFromDate());
		        
		        // controlla se esiste gia' una allocation precedente
		        List<EntityCondition> allocConditions = new ArrayList<EntityCondition>();
		        allocConditions.add(EntityCondition.makeCondition(StandardImportAllocationEnum.dataSource.name(), requestParam.getDataSource()));
		        allocConditions.add(EntityCondition.makeCondition(StandardImportAllocationEnum.refDate.name(), requestParam.getRefDate()));
		        allocConditions.add(EntityCondition.makeCondition(StandardImportAllocationEnum.personCode.name(), requestParam.getPersonCode()));
		        allocConditions.add(EntityCondition.makeCondition(StandardImportAllocationEnum.allocationOrgCode.name(), requestParam.getAllocationOrgCode()));
		        allocConditions.add(EntityCondition.makeCondition(StandardImportAllocationEnum.allocationFromDate.name(), requestParam.getAllocationFromDate()));
		        if (UtilValidate.isEmpty(context.getDelegator().findList(ImportManagerConstants.ALLOCATION_INTERFACE_EXT, EntityCondition.makeCondition(allocConditions), null, null, null, false))) {
		        	// inserimento struttura
		        	addLogInfo("START_IMPORT_ALL_CRE", elaborateStrutturaParams);
		        	gv.create();
		        	recordElaborated++;
		        } else {
		        	// aggiornamento struttura
		        	addLogInfo("START_IMPORT_ALL_AGG", elaborateStrutturaParams);
		            gv.store();
		            recordElaborated++;
		        }
        	}
    	} catch (Exception ex) {
        	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, ex.toString());
        } finally {
        	addLogInfo("END_IMPORT_SER_ALL", null);
        	writeLog(startTimestamp, context);
            context.close();
        }
    	
        return new StandardImportWSResponse(new StandardImportResponse("0", "OK", recordElaborated, 0, 0));
    }
    
    private void addLogInfo(String code, Map<String, Object> logParameters ) {
        JobLogLog logMessage = new JobLogLog().initLogCode(RESOURE_LABEL, code, logParameters, locale);
        jobLogger.printLogInfo(logMessage.getLogMessage(), logMessage.getLogCode(), null, RESOURE_LABEL, logMessage.getParametersJSON());
        Debug.log(logMessage.getLogMessage(), MODULE);
    }
    
    private StandardImportWSResponse getErrorResult(ServiceCodeEnum code, int recordElaborated, int blockingErrors, int warningMessages, String exceptionMessage) {
    	
    	String errorMessage = "";
    	if(UtilValidate.isEmpty(exceptionMessage)) {
    		errorMessage = code.getMessage();
    	} else {
    		errorMessage = exceptionMessage;
    	}
    	
        Map<String, Object> logParameters = UtilMisc.toMap(E.errorCode.name(), (Object) code.getCode(), E.errorMessage.name(), (Object) errorMessage);
        JobLogLog logMessage = new JobLogLog().initLogCode(RESOURE_LABEL, "START_IMPORT_ERROR", logParameters, locale);
        jobLogger.printLogInfo(logMessage.getLogMessage(), logMessage.getLogCode(), null, RESOURE_LABEL, logMessage.getParametersJSON());
        Debug.logError(logMessage.getLogMessage(), MODULE);
        
        return new StandardImportWSResponse(new StandardImportResponse(code.getCode(), errorMessage, recordElaborated, blockingErrors, warningMessages));
    }
    
    /**
     * Controlla i parametri obbligatori
     * @param recordElaborated 
     * @param requestParams 
     * 
     * @param params
     * @return
     * @throws NoSuchFieldException 
     * @throws SecurityException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private StandardImportWSResponse checkMandatoryParams(Serializable requestParam, StandardImportEnum[] standardImportEnum, int recordElaborated) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    	        
        if (UtilValidate.isEmpty(requestParam)) {
        	return getErrorResult(ServiceCodeEnum.CODE_0002, recordElaborated, 1, 0, null);
        }
        
        // verifica campi obbligatori
        int mandatoryMis = 0;
        for (StandardImportEnum f: standardImportEnum) {
        	Field paramField = requestParam.getClass().getDeclaredField(f.getName());
        	paramField.setAccessible(true);
        	if (f.isMandatory() && UtilValidate.isEmpty(paramField.get(requestParam))) {
	        	mandatoryMis++;
	        } 
    	}
        
        if (mandatoryMis != 0) {
        	return getErrorResult(ServiceCodeEnum.CODE_0001, recordElaborated, mandatoryMis, 0, null);
        }
    	
        return null;
    }
    
    private GenericValue createGenericValueFromParameters(String entityName, Serializable requestParam, StandardImportEnum[] standardImportEnum) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    	GenericValue gv = context.getDelegator().makeValue(entityName);
        
        for (StandardImportEnum f: standardImportEnum) {
        	Field paramField = requestParam.getClass().getDeclaredField(f.getName());
        	paramField.setAccessible(true);
        	if(UtilValidate.isNotEmpty(paramField.get(requestParam)))
        	{
        		if(paramField.get(requestParam) instanceof java.util.Date) {
        			gv.put(f.getName(), UtilDateTime.toTimestamp((java.util.Date)paramField.get(requestParam)));
        		} else if(paramField.get(requestParam) instanceof java.lang.Integer) {
        			gv.put(f.getName(), Long.valueOf((Integer)paramField.get(requestParam)));
        		} else {
        			gv.put(f.getName(), paramField.get(requestParam));
        		}
        	}
    	}
        
        return gv;
    }
    
    private void writeLog(Timestamp startTimestamp, OfbizWsContext context) {
        String jobLogId = context.getDelegator().getNextSeqId("JobLog");
        
        Map<String, Object> logParameters = new HashMap<String, Object>();
        try {
            logParameters.put(ServiceLogger.JOB_LOG_ID, jobLogId);
            logParameters.put(ServiceLogger.SESSION_ID, sessionId);
            logParameters.put(ServiceLogger.MESSAGES, jobLogger.getMessages());
            // get the system userid and store in context otherwise logger service does not work
            GenericValue userLogin = context.getDelegator().findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            logParameters.put(ServiceLogger.USER_LOGIN, userLogin);
            logParameters.put(ServiceLogger.LOG_DATE, startTimestamp);
            logParameters.put(ServiceLogger.SERVICE_NAME, SERVICE_NAME);
            GenericValue serviceTypeValue = context.getDelegator().findOne("JobLogServiceType", UtilMisc.toMap(ServiceLogger.SERVICE_TYPE_ID, SERVICE_TYPE_ID), true);
            if (UtilValidate.isNotEmpty(serviceTypeValue)) {
                logParameters.put(ServiceLogger.SERVICE_TYPE_ID, serviceTypeValue.getString(ServiceLogger.SERVICE_TYPE_ID));
            }
            ServiceLogger.writeLogs(context.getDctx(), logParameters, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
        }
    }
    
    private String getSessionId() {
        String nowAsString = UtilDateTime.nowAsString();
        String sessionId = HashCrypt.getDigestHash(nowAsString, "SHA");
        return sessionId = sessionId.substring(37);
    }

}
