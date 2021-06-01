package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.FindServices;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.BaseImportManager;
import com.mapsengineering.base.standardimport.common.DeleteEnum;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.EntityNameStdImportEnum;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.TakeOverFactory;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.helper.ImportManagerHelper;
import com.mapsengineering.base.standardimport.util.TakeOverUtil;

/**
 * ImportManager
 *
 */
public class ImportManager extends BaseImportManager {

    public static final String MODULE = ImportManager.class.getName();

    // List of external records elaborating
    private List<GenericValue> lockedExtValues = new ArrayList<GenericValue>();

    private DeleteEnum deletePrevious;
    private String checkEndYearElab;
    private ImportManagerHelper importManagerHelper;
    private ImportManagerFromETL importManagerFromETL;
    private boolean isError;

    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> doImportSrv(DispatchContext dctx, Map<String, Object> context) {
        ImportManager obj = new ImportManager(dctx, context);
        obj.doImport();
        return obj.getResult();
    }

    /**
     * 
     * @param dctx
     * @param context
     */
    public ImportManager(DispatchContext dctx, Map<String, Object> context) {
    	super(dctx, context);
        importManagerFromETL = new ImportManagerFromETL(dctx, context);
        importManagerHelper = new ImportManagerHelper(dctx, context);
    }

    
    private DeleteEnum checkDeleteEnum(String deletePreviuosStr) {
        if(UtilValidate.isEmpty(deletePreviuosStr)) {
            deletePreviuosStr = UtilProperties.getPropertyValue("BaseConfig", "StandardImport.weMeasureInterface.deletePreviuos", DeleteEnum.ALL.name());
        }
        return DeleteEnum.valueOf(deletePreviuosStr);
    }
    
    /**
     * Read from table and do import, it is possible add condition with filterConditions or filterMapList
     */
    public void doImport() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        String msg;

        try {
            cleanParameters();
            
            String localJobLogId = importManagerHelper.getJobLogId();
            if (UtilValidate.isEmpty(localJobLogId)) {
                localJobLogId = getDelegator().getNextSeqId("JobLog");
            }
            importManagerHelper.setJobLogId(localJobLogId);

            deletePrevious = checkDeleteEnum((String) getContext().get(E.deletePrevious.name()));
            checkEndYearElab = (String) getContext().get(E.checkEndYearElab.name());
            List<String> entityListToImport = StringUtil.split((String)getContext().get(E.entityListToImport.name()), "|");
            Iterator<String> entityIterator = entityListToImport.iterator();

            List<EntityCondition> entityConditionList = null;
            List<Map<String, Object>> filterMapList = null;
            if (getContext().containsKey(E.filterConditions.name())) {
                entityConditionList = UtilGenerics.toList(getContext().get(E.filterConditions.name()));
                if (UtilValidate.isNotEmpty(entityConditionList) && entityListToImport.size() != entityConditionList.size()) {
                    msg = "FilterConditions size and entityListToImport size are not equal";
                    addLogError(msg, MODULE);
                    throw new ImportException(entityListToImport.toString(), "0", msg);
                }
            } else if (getContext().containsKey(E.filterMapList.name())) {
                filterMapList = UtilGenerics.toList(getContext().get(E.filterMapList.name()));
                if (UtilValidate.isNotEmpty(filterMapList) && entityListToImport.size() != filterMapList.size()) {
                    msg = "FilterMapList size and entityListToImport size are not equal";
                    addLogError(msg, MODULE);
                    throw new ImportException(entityListToImport.toString(), "0", msg);
                }
            }

            EntityCondition readConditionDefault = importManagerHelper.buildReadCondition();
            List<String> orderBy = importManagerHelper.getOrderBy();
            EntityFindOptions findOptions = importManagerHelper.getFindOptions();

            msg = SERVICE_NAME + ": START";
            addLogInfo(msg, MODULE);

            int index = 0;
            getResult().put("resultList", resultList);
            while (entityIterator.hasNext()) {
                String entityName = entityIterator.next();
                try {
                	isError = false;
                    doImportByEntity(entityName, index, readConditionDefault, entityConditionList, filterMapList, orderBy, findOptions);
                } finally {
                    msg = "IMPORT COMPLETED " + entityName;
                    addLogInfo(msg, MODULE);
                    List<Map<String, ? extends Object>> list = new ArrayList<Map<String, ? extends Object>>();
                    list.addAll(getImportedListPK());
                    
                    resultList.add(importManagerHelper.onImportAddList(SERVICE_TYPE, startTimestamp, entityName, getRecordElaborated(), getBlockingErrors(), getWarningMessages(), getMessages(), list));
                    cleanParameters();
                    index++;
                }
            }

            /**Carico ETL**/
            String checkFromETL = (String)getContext().get("checkFromETL");
            Map<String, Object> resultETL = importManagerFromETL.inmportAllETL(entityListToImport, checkFromETL);
            getResult().put("resultETLList", resultETL.get("resultEtl"));

        } catch (Exception e) {
            msg = "Unexpected error: ";
            addLogError(e, msg, MODULE);
            getResult().putAll(ServiceUtil.returnError(e.getMessage()));
        }
    }

    /**
     * 
     * @param entityName
     * @param index
     * @param readConditionDefault
     * @param entityConditionList
     * @param filterMapList
     * @param orderBy
     * @param findOptions
     * @throws GeneralException
     */
    protected void doImportByEntity(String entityName, int index, EntityCondition readConditionDefault, List<EntityCondition> entityConditionList, List<Map<String, Object>> filterMapList, List<String> orderBy, EntityFindOptions findOptions) throws GeneralException {
        String msg;

        EntityCondition readCondition = readConditionDefault;
        if (UtilValidate.isNotEmpty(entityConditionList) && index < entityConditionList.size()) {
            readCondition = EntityCondition.makeCondition(readCondition, (EntityCondition)entityConditionList.get(index));
        } else if (UtilValidate.isNotEmpty(filterMapList) && index < filterMapList.size()) {
            Map<String, Object> queryStringMap = new HashMap<String, Object>();
            Map<String, Object> filterMap = UtilGenerics.toMap(filterMapList.get(index));
            ModelEntity modelEntity = getDelegator().getModelEntity(entityName);
            List<EntityCondition> tmpList = FindServices.createConditionList(filterMap, modelEntity.getFieldsUnmodifiable(), queryStringMap, getDelegator(), getContext());
            tmpList.add(readCondition);
            readCondition = EntityCondition.makeCondition(tmpList);
        }
        msg = SERVICE_NAME + ": elaborating interface " + entityName;
        addLogInfo(msg, MODULE);

        msg = SERVICE_NAME + ": readCondition " + readCondition;
        addLogInfo(msg, MODULE);

        boolean noMoreResults = false;
        while (!noMoreResults) {
            List<GenericValue> curList = getDelegator().findList(entityName, readCondition, null, orderBy, findOptions, false);
            addLogInfo(" Partial list size: " + curList.size(), MODULE);
            if (UtilValidate.isNotEmpty(curList)) {
                for (GenericValue gv : curList) {
                    // replace gv.getPrimaryKey() with getLogicalPrimaryKey()
                    Map<String, Object> key = EntityNameStdImportEnum.getLogicalPrimaryKey(entityName, gv);
                    doImport(gv.getEntityName(), key);
                }
            } else {
                noMoreResults = true;
            }
        }
    }

    /**
     * Avvia l'importazione di un record esterno ed eventuali relazionati.
     * Gestisce i record falliti, mettendoli in stato KO.
     * 
     * @param entityName
     * @param extKey chiave primaria o logica del record esterno
     * @return record locale aggiornato, null in caso di errori
     * @throws GeneralException
     */
    public GenericValue doImport(String entityName, Map<String, ? extends Object> extKey) throws GeneralException {
        // Azzera la lista dei record esterni in elaborazione.
        lockedExtValues.clear();

        // Avvia l'importazione del record esterno ed eventuali relazionati.
        GenericValue value = importValue(entityName, extKey);

        // Il record locale e' nullo, caso di errore.
        if (value == null && isError) {
            updateFailedValues();
        }

        return value;
    }

    /**
     * In una nuova transazione esegue l'importazione di un record esterno ed eventuali relazionati.
     * 
     * @param entityName
     * @param extKey chiave primaria o logica del record esterno
     * @return record locale aggiornato, null in caso di errori
     * @throws GeneralException
     */
    public GenericValue importValue(String entityName, Map<String, ? extends Object> extKey) throws GeneralException {
        GenericValue value = null;
        boolean beganTransaction = false;
        setRecordElaborated(getRecordElaborated()+1);
        try {
            beganTransaction = TransactionUtil.begin(ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT);
            value = importRelated(entityName, extKey);
            TransactionUtil.commit(beganTransaction);
        } catch (Exception e) {
        	isError = true;
            setFieldElabResult(e.getMessage());
            String errMsg = "Import failed for " + entityName + " " + toString(extKey) + ". Rolling back transaction.";
            addLogError(e, errMsg, MODULE, toString(extKey));
            // only rollback the transaction if we started one...
            TransactionUtil.rollback(beganTransaction, errMsg, e);
        }
        return value;
    }

    private void setFieldElabResult(String msg) {
        if (UtilValidate.isNotEmpty(lockedExtValues)) {
            GenericValue value = lockedExtValues.get(0);
            setFieldElabResult(value, msg);
        }
    }

    private void setFieldElabResult(GenericValue value, String msg) {
        if (value != null) {
            ModelEntity modelEntity = value.getModelEntity();
            if (modelEntity.isField(RECORD_FIELD_ELAB_RESULT)) {
                // vedi GN-398 Oracle NLS_LENGTH_SEMANTICS
                if (UtilValidate.isNotEmpty(msg) && msg.length() >= 2000) {
                    msg = msg.substring(0, 2000);
                }
                value.set(RECORD_FIELD_ELAB_RESULT, msg);
            }
        }
    }

    /**
     * Esegue l'importazione di un record esterno.
     * 
     * @param entityName
     * @param extKey chiave primaria o logica del record esterno da caricare
     * @return record locale aggiornato
     * @throws GeneralException in caso di errori o record locale non trovato
     */
    public GenericValue importRelated(String entityName, Map<String, ? extends Object> extKey) throws GeneralException {
        GenericValue externalValue = importManagerHelper.findExternalValue(entityName, extKey);
        return importRelated(entityName, extKey, externalValue, true);
    }

    /**
     * Esegue l'importazione di una lista di record esterni.
     * Elabora sempre tutti i record della lista e in caso di errroi progaga solo il primo
     * 
     * @param entityName
     * @param extKey chiave parziale del record esterno da caricare
     * @return lista di record locali aggiornati
     * @throws GeneralException in caso di errori o record locale non trovato
     */
    public Map<String, List<GenericValue>> importRelatedMulti(String entityName, Map<String, ? extends Object> extKey) throws GeneralException {
        List<GenericValue> localValues = new ArrayList<GenericValue>();
        List<GenericValue> externalValues = importManagerHelper.findExternalValues(entityName, extKey);
        GeneralException firstException = null;

        for (GenericValue externalValue : externalValues) {
            try {
                GenericValue localValue = importRelated(entityName, externalValue.getPrimaryKey(), externalValue, true);
                localValues.add(localValue);
            } catch (GeneralException e) {
                if (firstException == null) {
                    addLogInfo(e.getMessage(), MODULE, toString(extKey));
                    firstException = e;
                }
            }
        }

        if (firstException != null) {
            throw firstException;
        }
        Map<String, List<GenericValue>> resultList = new HashMap<String, List<GenericValue>>();
        resultList.put("localValues", localValues);
        resultList.put("externalValues", externalValues);
        return resultList;
    }

    /**
     * Esegue l'importazione di un record esterno precaricato.
     * 
     * @param externalValue
     * @return
     * @throws GeneralException
     */
    public GenericValue importRelated(GenericValue externalValue) throws GeneralException {
        if (externalValue == null) {
            return null;
        }
        return importRelated(externalValue.getEntityName(), externalValue, externalValue, false);
    }

    /**
     * Esegue l'importazione di un record esterno precaricato.
     * 
     * @param entityName
     * @param extKey chiave primaria o logica del record esterno da caricare
     * @param externalValue record esterno precaricato
     * @param externalValuePersist se true aggiorna il record esterno sul database
     * @return record locale aggiornato
     * @throws GeneralException in caso di errori o record locale non trovato
     */
    public GenericValue importRelated(String entityName, Map<String, ? extends Object> extKey, GenericValue externalValue, boolean externalValuePersist) throws GeneralException {
        // Ottiene una nuova istanza del servizio di importazione record
        // esterno.
        TakeOverService service = TakeOverFactory.instance(entityName);
        service.setManager(this);

        // Ottiene il record esterno in base alla chiave esterna.
        service.setExternalValue(externalValue);

        boolean shallImport;
        if (externalValue != null) {
            // Il record esterno esiste.

            // Assicura che il record esterno abbia un id.
            importManagerHelper.ensureExternalValueId(externalValue, externalValuePersist);

            String status = externalValue.getString(RECORD_FIELD_STATUS);
            if (UtilValidate.isEmpty(status)) {

                // Il record esterno deve ancora essere elaborato.
                shallImport = true;
                updateExternalValueStatus(externalValue, RECORD_STATUS_LOCKED, externalValuePersist);

            } else {
                // Il record esterno e' gia' stato elaborato.
                shallImport = false;
            }

        } else {
            // Il record esterno non esiste.
            shallImport = false;
        }

        // Ottiene il record locale.
        service.initLocalValue(extKey);

        // Esegue l'eventuale importazione.
        if (shallImport) {
            service.doImport();
        }

        // Verifica che il record locale sia presente se e stato effettivamente importato.
        final GenericValue localValue = service.getLocalValue();
        if (service.isImported() && localValue == null) {
            ImportException e = new ImportException(entityName, toString(extKey), entityName + " with " + toString(extKey) + " not found");
            setFieldElabResult(externalValue, e.getMessage());
            throw e;
        }

        // Se il record esterno e' appena stato importato, salva stato OK.
        if (shallImport && externalValue != null) {
            updateExternalValueStatus(externalValue, RECORD_STATUS_OK, externalValuePersist);
            // Sposta il record esterno nello storico.
            moveExternalValueToHist(externalValue, externalValuePersist);
            // Aggiunge chiave alla lista di listCodeImported
            getImportedListPK().add(extKey);
        }

        return localValue;
    }

    /**
     * Aggiorna lo stato del record esterno.
     * Se stato locked, aggiunge il record alla lista di quelli in elaborazione.
     * 
     * @param externalValue record esterno
     * @param externalValuePersist se true aggiorna il record esterno sul database
     * @param status nuovo valore di stato
     * @throws GenericEntityException
     */
    protected void updateExternalValueStatus(GenericValue externalValue, String status, boolean externalValuePersist) throws GeneralException {
        externalValue.set(RECORD_FIELD_STATUS, status);
        if (externalValuePersist) {
            externalValue.store();
        }
        if (RECORD_STATUS_LOCKED.equals(status)) {
            lockedExtValues.add(externalValue);
        }
    }

    /**
     * 
     * @param externalValue
     * @param externalValuePersist
     * @throws GeneralException
     */
    protected void moveExternalValueToHist(GenericValue externalValue, boolean externalValuePersist) throws GeneralException {
        try {
            if (externalValue != null) {
                String entityNameHist = externalValue.getEntityName() + ENTITY_INTERFACE_HIST_SUFFIX;
                GenericValue externalValueHist = getDelegator().makeValue(entityNameHist, externalValue);
                String id = getDelegator().getNextSeqId(entityNameHist);
            	externalValueHist.set(RECORD_FIELD_ID, RECORD_ID_PREFIX + id);
                externalValueHist.create();
                if (externalValuePersist) {
                    externalValue.remove();
                }
            }
        } catch (GenericEntityException e) {
            String msg = "GenericEntityException " + e.getMessage();
            addLogInfo(msg, MODULE);
        }
    }

    /**
     * Assegna lo stato fallito ai record esterni in elaborazione. Assegnazione massiva transazionale.
     * 
     * @throws GeneralException
     */
    protected void updateFailedValues() throws GeneralException {
        if (lockedExtValues != null && lockedExtValues.size() > 0) {
            boolean beganTransaction = false;
            try {
                beganTransaction = TransactionUtil.begin();
                for (GenericValue value : lockedExtValues) {
                    value.set(RECORD_FIELD_STATUS, RECORD_STATUS_KO);
                    value.store();
                }
                TransactionUtil.commit(beganTransaction);
            } catch (Exception e) {
                String errMsg = "updateFailedValues failed. Rolling back transaction.";
                addLogError(e, errMsg, MODULE);
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, errMsg, e);
            }
        }
    }

     /**
     * 
     * @param map
     * @return
     */
    public String toString(Map<String, ? extends Object> map) {
        return TakeOverUtil.toString(map);
    }

    public DeleteEnum getDeletePreviuos() {
        return deletePrevious;
    }

    public void setDeletePreviuos(DeleteEnum deletePreviuos) {
        this.deletePrevious = deletePreviuos;
    }

    /**
     * @return the checkEndYearElab
     */
    public String getCheckEndYearElab() {
        return checkEndYearElab;
    }
    
    
    /**
     * @return the jobLogId
     */
    public String getJobLogId() {
        return importManagerHelper.getJobLogId();
    }

    /**
     * @param jobLogId the jobLogId to set
     */
    public void setJobLogId(String jobLogId) {
        importManagerHelper.setJobLogId(jobLogId);
    }

}
