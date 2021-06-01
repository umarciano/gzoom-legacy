package com.mapsengineering.base.standardimport.common;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.util.DateUtilService;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;

public abstract class TakeOverService {

    public static final String MODULE = TakeOverService.class.getName();

    private ImportManager manager;
    private String entityName;
    private GenericValue localValue = null;
    private GenericValue externalValue = null;
    private boolean imported;

    public ImportManager getManager() {
        return manager;
    }

    public void setManager(ImportManager manager) {
        this.manager = manager;
    }

    public String getEntityName() {
        return entityName;
    }

    protected void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public GenericValue getLocalValue() {
        return localValue;
    }

    protected void setLocalValue(GenericValue localValue) {
        this.localValue = localValue;
    }

    public GenericValue getExternalValue() {
        return externalValue;
    }

    public void setExternalValue(GenericValue externalValue) {
        this.externalValue = externalValue;
    }
    
    public void setImported(boolean imported) {
    	this.imported = imported;
    }
    
    public boolean isImported() {
    	return imported;
    }

    /**
     * Ottiene un singolo record locale in base alla chiave logica esterna.
     * 
     * @param extLogicKey
     *            chiave logica esterna
     * @throws GeneralException
     */
    public abstract void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException;

    /**
     * Esegue l'importazione del record esterno.
     * 
     * @throws GeneralException
     */
    public abstract void doImport() throws GeneralException;

    /**
     * Innesca l'importazione di record esterni relazionati al record esterno.
     * 
     * @param entityName
     *            Entita' esterna relazionata
     * @param extLogicKey
     *            chiave logica del record esterno relazionato
     * @return record locale aggiornato corrispondente al record esterno
     *         relazionato.
     * @throws GeneralException
     */
    protected GenericValue doImport(String entityName, Map<String, ? extends Object> extLogicKey) throws GeneralException {
        return manager.importRelated(entityName, extLogicKey);
    }

    /**
     * Innesca l'importazione di record esterni relazionati al record esterno.
     * 
     * @param entityName
     *            Entita' esterna relazionata
     * @param extLogicKey
     *            chiave logica parziale del record esterno relazionato
     * @return lista di record locali aggiornati corrispondenti al record esterno
     *         relazionato.
     * @throws GeneralException
     */
    public Map<String, List<GenericValue>> doImportMulti(String entityName, Map<String, ? extends Object> extLogicKey) throws GeneralException {
        return manager.importRelatedMulti(entityName, extLogicKey);
    }

    public void addLogInfo(String msg) {
        addLogInfo(msg, null);
    }

    public void addLogInfo(String msg, Map<String, ? extends Object> extLogicKey) {
        String msgNew = entityName + ": " + msg;
        if (extLogicKey == null && getExternalValue() != null) {
            extLogicKey = getExternalValue().getPrimaryKey();
        }
        manager.addLogInfo(msgNew, MODULE, extLogicKey != null ? manager.toString(extLogicKey) : null);
    }

    public void addLogError(String msg) {
        String msgNew = entityName + ": " + msg;
        manager.addLogError(msgNew, MODULE, manager.toString(getExternalValue().getPrimaryKey()));
    }

    public void addLogWarning(String msg) {
        String msgNew = entityName + ": " + msg;
        manager.addLogWarning(msgNew, MODULE, manager.toString(getExternalValue().getPrimaryKey()));
    }

    /**
     * LogInfo with 
     * @param logCode
     * @param logMessage
     * @param key
     * @param valueRef2
     * @param valueRef3
     */
    public void addLogWarning(JobLogLog jobLogLog) {
        manager.addLogWarning(jobLogLog, MODULE, manager.toString(getExternalValue().getPrimaryKey()));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> baseCrudInterface(String entityName, String operation, Map parameters) {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put("entityName", entityName);
        serviceMap.put("operation", operation);
        serviceMap.put("userLogin", manager.getUserLogin());
        parameters.put("operation", operation);
        serviceMap.put("parameters", parameters);
        serviceMap.put("locale", manager.getLocale());
        return serviceMap;
    }

    private void manageFailureResult(boolean throwOnFailur, String failureMessage) throws GeneralException {
        if (throwOnFailur) {
            throw new ImportException(getEntityName(), getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), failureMessage);
        } else {
            addLogWarning(failureMessage);
        }
    }

    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> serviceMap, String successMsg, String errorMsg, boolean throwOnError, boolean throwOnFailur) throws GeneralException {
        Map<String, Object> res = manager.getDispatcher().runSync(serviceName, serviceMap);
        if (ServiceUtil.isSuccess(res)) {
            addLogInfo(successMsg);
        } else {
            if (ServiceUtil.isFailure(res)) {
                manageFailureResult(throwOnFailur, errorMsg + FindUtilService.COLON_SEP + res.get(ModelService.FAIL_MESSAGE));
            } else if (ServiceUtil.isError(res)) {
                manageErrorResult(throwOnError, errorMsg + FindUtilService.COLON_SEP + ServiceUtil.getErrorMessage(res));
            } else {
                addLogError(errorMsg + FindUtilService.COLON_SEP + ServiceUtil.getErrorMessage(res));
            }
        }
        return res;
    }

    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> serviceMap, String successMsg, String errorMsg, boolean throwOnError) throws GeneralException {
        return runSync(serviceName, serviceMap, successMsg, errorMsg, throwOnError, false);
    }

    private void manageErrorResult(boolean throwOnError, String errorMessage) throws GeneralException {
        if (throwOnError) {
            throw new ImportException(getEntityName(), getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), errorMessage);
        } else {
            addLogError(errorMessage);
        }
    }
    
    /**
     * Ritorna un solo elemento, aggiunge warning (warningMessages) negli altri casi, usa JobLogLog
     * @param entityNameToFind
     * @param cond
     * @param foundMore
     * @param noFound
     * @return
     * @throws GeneralException
     */
    public GenericValue findOneWarning(String entityNameToFind, EntityCondition cond, JobLogLog foundMore, JobLogLog noFound) throws GeneralException  {
        List<GenericValue> parents = manager.getDelegator().findList(entityNameToFind, cond, null, null, null, false);
        if (UtilValidate.isNotEmpty(parents) && parents.size() > 1) {
            addLogWarning(foundMore);
        } else if (UtilValidate.isEmpty(parents)) {
            addLogWarning(noFound);
        }

        return EntityUtil.getFirst(parents);
    }
    
    /**
     * Ritorna un solo elemento, rilancia eccezione negli altri casi, usa JobLogLog
     * @param entityNameToFind
     * @param cond
     * @param foundMore
     * @param noFound
     * @param getEntityName
     * @param id
     * @return
     * @throws GeneralException
     */
    public GenericValue findOne(String entityNameToFind, EntityCondition cond, JobLogLog foundMore, JobLogLog noFound, String getEntityName, String id) throws GeneralException  {
        List<GenericValue> parents = manager.getDelegator().findList(entityNameToFind, cond, null, null, null, false);
        if (UtilValidate.isNotEmpty(parents) && parents.size() > 1) {
            throw new ImportException(getEntityName, id, foundMore);
        } else if (UtilValidate.isEmpty(parents)) {
            throw new ImportException(getEntityName, id, noFound);
        }

        return EntityUtil.getFirst(parents);
    }

    /** Ritorna un solo elemento, rilancia eccezione negli altri casi*/
    public GenericValue findOne(String entityNameToFind, EntityCondition cond, String foundMore, String noFound, String getEntityName, String id) throws GeneralException  {
        return findOne(entityNameToFind, cond, foundMore, noFound, getEntityName, id, true, true);
        
    }
    
    /** Ritorna un solo elemento, rilancia eccezione in base a throwIfNone and throwIfMany*/
    public GenericValue findOne(String entityNameToFind, EntityCondition cond, String foundMore, String noFound, String getEntityName,  String id, boolean throwIfNone, boolean throwIfMany) throws GeneralException  {
        GenericValue value = null;
        try {
            value = FindUtilService.findOne(manager.getDelegator(), entityNameToFind, cond, foundMore, noFound, throwIfNone, throwIfMany);
        } catch (GeneralException e) {
            throw new ImportException(getEntityName, id, e.getMessage());
        }
                
        return value;
    }

    /** Invoca baseCrudInterface per creare la mappa da passsare al servizio crud e poi il servizio crud */
    public Map<String, Object> runSyncCrud(String serviceName, String entityName, String operation, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError, boolean throwonFailure) throws GeneralException {
        Map<String, Object> serviceMap = baseCrudInterface(entityName, operation, parametersMap);
        return runSync(serviceName, serviceMap, successMsg, errorMsg, throwOnError, throwonFailure);
    }

    /** Invoca baseCrudInterface per creare la mappa da passsare al servizio crud e poi il servizio crud */
    public Map<String, Object> runSyncCrud(String serviceName, String entityName, String operation, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError) throws GeneralException {
        return runSyncCrud(serviceName, entityName, operation, parametersMap, successMsg, errorMsg, throwOnError, false);
    }

    protected Date getLastDayOfPrevMonth(Date date) {
        return DateUtilService.getLastDayOfPrevMonth(date);
    }

    public Date getPreviousDay(Date date) {
        return DateUtilService.getPreviousDay(date);
    }
    
    public String getPkShortValueString(Map<String, ? extends Object> mappaKey) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ? extends Object> curPk : mappaKey.entrySet()) {
            if (sb.length() > 0) {
                sb.append("::");
            }
            sb.append(curPk.getKey());
            sb.append("=");
            sb.append(curPk.getValue());
        }
        return sb.toString();
    }
    
    /**
     * verifica se e impostato il flag multi lingua
     * @return
     */
    protected boolean isMultiLang() {
    	String langMultiType = UtilProperties.getPropertyValue("BaseConfig", "Language.multi.type", null);
    	return UtilValidate.isNotEmpty(langMultiType) && ! "NONE".equals(langMultiType);
    }
}
