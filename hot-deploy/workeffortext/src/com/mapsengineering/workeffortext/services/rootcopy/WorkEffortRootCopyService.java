package com.mapsengineering.workeffortext.services.rootcopy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.util.WorkEffortAssocCopyUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Service for copy and snaphot
 *
 */
public class WorkEffortRootCopyService extends GenericService {

    public static final String MODULE = WorkEffortRootCopyService.class.getName();
    public static final String SERVICE_NAME = "workEffortRootCopyService";
    public static final String SERVICE_TYPE_ID = "WRK_ROOT_COPY";
    public static final int MAX_ROWS = 10;
    public static final String COLON_SEP = " : ";
    public static final String DELETE_OLD_ROOTS = "deleteOldRoots";
    public static final String CHECK_EXISTING = "checkExisting";
    public static final String GL_ACCOUNT_CREATION = "glAccountCreation";
    public static final String SNAPSHOT = "snapshot";

    private WorkEffortAssocCopyUtil workEffortAssocCopyUtil;
    private ServiceTypeEnum serviceTypeEnum;
    private List<String> workEffortRootIdNewList;
    private String copyWorkEffortAssocCopy;
    /**
     * skipCopyContent if true not copy the content anda dataResource
     */
    private boolean skipCopyContent;

    /**
     * Create workEffort and other data
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> workEffortRootCopyService(DispatchContext dctx, Map<String, Object> context) {
        WorkEffortRootCopyService obj = new WorkEffortRootCopyService(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortRootCopyService(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
        String snapshotStr = (String)context.get(SNAPSHOT);
        String duplicateAdmit = (String)context.get(E.duplicateAdmit.name()); //  = "COPY", "CLONE", "SNAPSHOT", "N"
        serviceTypeEnum = ServiceTypeEnum.getEnumeration(snapshotStr, duplicateAdmit);
        setServiceType(serviceTypeEnum.getServiceTypeId());
        setUseCache(false);
    }

    /**
     * Main service
     */
    public void mainLoop() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        String skipCopyContentStr = UtilProperties.getPropertyValue("WorkeffortExtConfig", "WorkEffortRootCopyService.WorkEffortAttributeCopy.skipCopyContent", "N");
        skipCopyContent = Boolean.parseBoolean(skipCopyContentStr);
        workEffortRootIdNewList = new ArrayList<String>();
                
        String msg;
        String deleteOldRoots = (String)context.get(DELETE_OLD_ROOTS);
        String checkExisting = (String)context.get(CHECK_EXISTING);
        Timestamp estimatedStartDateTo = (Timestamp)context.get(E.estimatedStartDateTo.name());
        Timestamp estimatedCompletionDateTo = (Timestamp)context.get(E.estimatedCompletionDateTo.name());
        String workEffortRevisionId = UtilGenerics.cast(context.get(E.workEffortRevisionId.name()));
        String storeRevisionWorkEffortAssoc = UtilGenerics.cast(context.get(E.storeRevisionWorkEffortAssoc.name()));
        copyWorkEffortAssocCopy = UtilGenerics.cast(context.get(E.copyWorkEffortAssocCopy.name()));
        workEffortAssocCopyUtil = new WorkEffortAssocCopyUtil(this, workEffortRevisionId, storeRevisionWorkEffortAssoc, estimatedStartDateTo, estimatedCompletionDateTo);

        msg = "Starting WorkEffort ROOT " + serviceTypeEnum.name();
        addLogInfo(msg, null);

        try {
            EntityCondition readCondition = new ReadConditionCreator().buildReadCondition(context);
            List<String> orderBy = WorkEffortRootReadOrderBy.getOrderBy();
            List<GenericValue> curList = delegator.findList(E.WorkEffortView.name(), readCondition, WorkEffortRootReadFieldSelect.getFieldsToSelect(), orderBy, null, getUseCache());

            msg = "Found " + curList.size() + " roots to elaborate for condition " + readCondition;
            addLogInfo(msg, null);

            for (GenericValue gv : curList) {
                boolean doRootCopy = true;

                GenericValue workEffortType = delegator.getRelatedOne(E.WorkEffortType.name(), gv);
                if (!getEnable(workEffortType, gv.getString(E.workEffortId.name()))) {
                    continue;
                }

                setRecordElaborated(getRecordElaborated() + 1);
                boolean beganTransaction = TransactionUtil.begin(ServiceLogger.TRANSACTION_TIMEOUT);
                try {
                    if (E.Y.name().equals(deleteOldRoots)) {
                        deleteOldRoots(gv.getString(E.workEffortId.name()), estimatedStartDateTo, estimatedCompletionDateTo);
                    }

                    if (E.Y.name().equals(checkExisting)) {
                        boolean exists = WorkEffortRootChecker.checkExisting(delegator, gv.getString(E.workEffortId.name()), estimatedStartDateTo, getUseCache());
                        doRootCopy = doRootCopy && !exists;

                        if (!doRootCopy) {
                            msg = "WorkEffort \"" + gv.getString(E.workEffortName.name()) + "\" could not be copied: Check Existing WorkEfforts Flag was set and the " + "WorkEffort has already been copied with the same FromDate";
                            addLogWarning(msg, gv.getString(E.workEffortId.name()));
                        }
                    }

                    if (doRootCopy) {
                        // first with empty rootHierarchyAssocTypeId
                        String newWorkEffortRootId = doRootCopy(gv.getString(E.workEffortId.name()), storeRevisionWorkEffortAssoc, workEffortRevisionId, null);
                        workEffortRootIdNewList.add(newWorkEffortRootId);
                    }

                    TransactionUtil.commit(beganTransaction);
                } catch (Exception e) {
                    msg = "Error copying workEffort " + gv.getString(E.workEffortId.name());
                    addLogError(e, msg, gv.getString(E.workEffortId.name()));
                    TransactionUtil.rollback(e);
                }

            }
            if (getBlockingErrors() == 0 && serviceTypeEnum.isUseWorkEffortRevisionId() && UtilValidate.isNotEmpty(workEffortRevisionId)) {
                manageWorkeffortAssoc(workEffortRevisionId);
            }
            msg = "Finished WorkEffort ROOT " + serviceTypeEnum.name();
            addLogInfo(msg, null);

            writeSystemNote(serviceTypeEnum.name());
        } catch (Exception e) {
            msg = "Errore: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
            try {
                TransactionUtil.rollback(e);
            } catch (GenericTransactionException gte) {
                msg = "Errore: ";
                addLogError(gte, msg);
            }
        } finally {
            // if jobLogId not empty the service is called from another service so
            // the log is not write now 
            String jobLogId = (String)context.get(ServiceLogger.JOB_LOG_ID);
            if (UtilValidate.isEmpty(jobLogId)) {
                jobLogId = delegator.getNextSeqId("JobLog");
                executeWriteLogs(startTimestamp, jobLogId);
            }
            getResult().put(E.workEffortRootIdList.name(), workEffortAssocCopyUtil.getWorkEffortRootIdList());
            getResult().put(E.workEffortRootIdNewList.name(), workEffortRootIdNewList);
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());

        }
    }

    private boolean getEnable(GenericValue workEffortType, String key) {
        if (serviceTypeEnum.isUseEnableSnapshot() && !E.Y.name().equals(workEffortType.get("enableSnapshot"))) {
            Map<String, Object> parameters = UtilMisc.toMap(E.description.name(), (Object)workEffortType.get(E.description.name()), E.workEffortTypeId.name(), (Object)workEffortType.get(E.workEffortTypeId.name()));
            String warnMsg = UtilProperties.getMessage("WorkeffortExtErrorLabels", "SNAPSHOT_NOT_ENABLED", parameters, getLocale());
            addLogWarning(warnMsg, key);
            return false;
        } else if (!serviceTypeEnum.isUseEnableSnapshot() && !E.Y.name().equals(workEffortType.get("copy"))) {
            Map<String, Object> parameters = UtilMisc.toMap(E.description.name(), (Object)workEffortType.get(E.description.name()), E.workEffortTypeId.name(), (Object)workEffortType.get(E.workEffortTypeId.name()));
            String warnMsg = UtilProperties.getMessage("WorkeffortExtErrorLabels", "COPY_NOT_ENABLED", parameters, getLocale());
            addLogWarning(warnMsg, key);
            return false;
        }
        return true;
    }

    /**
     * Only for Snapshot with automatic Revision
     * @throws Exception 
     */
    private void manageWorkeffortAssoc(String workEffortRevisionId) throws Exception {
        GenericValue workEffortRevision = findOne(E.WorkEffortRevision.name(), EntityCondition.makeCondition(E.workEffortRevisionId.name(), workEffortRevisionId), "Found more workEffortRevision with id = " + workEffortRevisionId, "No workEffortRevision with id = " + workEffortRevisionId);
        String isAutomatic = workEffortRevision.getString(E.isAutomatic.name());
        if (E.Y.name().equals(isAutomatic)) {
            workEffortAssocCopyUtil.copyMassiveWorkEffortAssoc(workEffortRevisionId);
        }
    }

    private void deleteOldRoots(String workEffortId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate) throws GeneralException {
        //Find related COPY WorkEfforts
        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), workEffortId));
        cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), E.COPY.name()));
        cond.add(EntityCondition.makeCondition(E.estimatedStartDate.name(), estimatedStartDate));
        cond.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), estimatedCompletionDate));

        List<GenericValue> toDeleteList = delegator.findList(E.WorkEffortAssocToView.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
        addLogInfo("Found " + toDeleteList.size() + " roots to delete", workEffortId);
        for (GenericValue assoc : toDeleteList) {
            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap = dispatcher.getDispatchContext().makeValidContext(E.workEffortRootPhysicalDelete.name(), "IN", context);
            serviceMap.put(E.workEffortId.name(), assoc.getString(E.workEffortIdTo.name()));
            serviceMap.put(E.userLogin.name(), userLogin);
            serviceMap.put(E.jobLogger.name(), jobLogger);
            Map<String, Object> res = dispatcher.runSync(E.workEffortRootPhysicalDelete.name(), serviceMap);
            if (UtilValidate.isNotEmpty(res.get(E.jobLogger.name()))) {
                jobLogger = (JobLogger)res.get(E.jobLogger.name());
            }
        }
    }

    private String doRootCopy(String origWorkEffortRootId, String storeRevisionWorkEffortAssoc, String workEffortRevisionId, String rootHierarchyAssocTypeId) throws GeneralException {
        Timestamp estimatedStartDateTo = (Timestamp)context.get(E.estimatedStartDateTo.name());
        Timestamp estimatedCompletionDateTo = (Timestamp)context.get(E.estimatedCompletionDateTo.name());
        GenericValue origRoot = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), origWorkEffortRootId), false);
        GenericValue wrkType = delegator.getRelatedOneCache(E.WorkEffortType.name(), origRoot);
        // hierarchyAssocTypeId contiene la relazione gerarchica del tipo obiettivo
        String hierarchyAssocTypeId = wrkType.getString("hierarchyAssocTypeId");
        // aggiungo il workEffortRootId alla lista di id, serve poi per la gestione delle assoc
        workEffortAssocCopyUtil.addWorkEffortRootIdList(origWorkEffortRootId);

        // listHierarchyAssocTypeId contiene la lista delle relzioni gerarchiche
        // rootHierarchyAssocTypeId che viene dall'eventuale iterazione precedente
        // hierarchyAssocTypeId che viene dall'attuale tipo obiettivo
        List<String> listHierarchyAssocTypeId = FastList.newInstance();
        listHierarchyAssocTypeId.add(hierarchyAssocTypeId);
        if (UtilValidate.isNotEmpty(rootHierarchyAssocTypeId)) {
            listHierarchyAssocTypeId.add(rootHierarchyAssocTypeId);
        }

        // listHierarchyAssocTypeIdForClone, popolato solo per il CLONE
        List<String> listHierarchyAssocTypeIdForClone = FastList.newInstance();
        if (ServiceTypeEnum.CLONE.equals(serviceTypeEnum)) {
            listHierarchyAssocTypeIdForClone.addAll(listHierarchyAssocTypeId);
        }
        // Creazione radice con doWorkEffortAndDataCopy
        // in caso di clonazione bisogna escludere le relazioni gerarchiche listHierarchyAssocTypeIdForClone
        // perche' verranno create dopo nella clonazione di tutto l'albero
        // negli altri casi invece listHierarchyAssocTypeIdForClone e' null
        String newWorkEffortRootId = doWorkEffortAndDataCopy(null, origWorkEffortRootId, (String)context.get(E.workEffortTypeIdTo.name()), null, null, listHierarchyAssocTypeIdForClone, estimatedStartDateTo, estimatedCompletionDateTo, null, null, storeRevisionWorkEffortAssoc, workEffortRevisionId);

        //Creazione associazione SNAPSHOT or COPY, but not for serviceType CLONE
        if (!ServiceTypeEnum.CLONE.equals(serviceTypeEnum)) {
            manageAssocToOriginal(origWorkEffortRootId, newWorkEffortRootId, estimatedStartDateTo, estimatedCompletionDateTo, serviceTypeEnum.isUseWorkEffortRevisionId(), "Y".equals(wrkType.getString(E.isRoot.name())));
        }
        // doRootCopyOther, invoca poi la ricerca delle relazioni da copiare/storicizzare, quindi
        doRootCopyOther(origWorkEffortRootId, newWorkEffortRootId, newWorkEffortRootId, listHierarchyAssocTypeId, storeRevisionWorkEffortAssoc, workEffortRevisionId, hierarchyAssocTypeId);

        return newWorkEffortRootId;
    }

    private void doRootCopyOther(String origWorkEffortRootId, String newWorkEffortRootId, String newWorkEffortParentId, List<String> listHierarchyAssocTypeId, String storeRevisionWorkEffortAssoc, String workEffortRevisionId, String rootHierarchyAssocTypeId) throws GeneralException {
        Timestamp estimatedStartDateTo = (Timestamp)context.get(E.estimatedStartDateTo.name());
        Timestamp estimatedCompletionDateTo = (Timestamp)context.get(E.estimatedCompletionDateTo.name());

        // cerca obiettivi collegati da elaborare, tramite relazione gerarchica
        List<EntityCondition> assocCondition = FastList.newInstance();
        assocCondition.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), origWorkEffortRootId));
        assocCondition.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), rootHierarchyAssocTypeId));
        // assocCondition.add(EntityCondition.makeCondition(E.wrToParentId.name(), origWorkEffortRootId));
        List<GenericValue> hierarchyList = delegator.findList(E.WorkEffortAssocExtView.name(), EntityCondition.makeCondition(assocCondition), null, UtilMisc.toList(E.sequenceNum.name(), E.wrToCode.name(), E.workEffortIdTo.name()), null, getUseCache());
        addLogInfo("Search WorkEffortAssocExtView with = " + EntityCondition.makeCondition(assocCondition) + " to elaborate..." + hierarchyList.size(), origWorkEffortRootId);

        // cerca obiettivi figli da elaborare
        /*List<EntityCondition> weCondition = FastList.newInstance();
        weCondition.add(EntityCondition.makeCondition(E.weParentId.name(), origWorkEffortRootId));
        weCondition.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.NOT_EQUAL, origWorkEffortRootId));
        List<GenericValue> weList = delegator.findList(E.WorkEffortView.name(), EntityCondition.makeCondition(weCondition), null, null, null, getUseCache());
        addLogInfo("Search WorkEffortView with = " + EntityCondition.makeCondition(weCondition) + " to elaborate..." + weList.size(), origWorkEffortRootId);
        */
        for (GenericValue assoc : hierarchyList) {

            // cerca obiettivi figli da elaborare
            /*List<EntityCondition> assCondition = FastList.newInstance();
            assCondition.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), origWorkEffortRootId));
            assCondition.add(EntityCondition.makeCondition(E.workEffortIdTo.name(), we.getString(E.workEffortId.name())));
            assCondition.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), rootHierarchyAssocTypeId));
            List<GenericValue> hierarchyList2 = delegator.findList(E.WorkEffortAssocExtView.name(), EntityCondition.makeCondition(assCondition), null, UtilMisc.toList(E.sequenceNum.name(), E.wrToCode.name(), E.workEffortIdTo.name()), null, getUseCache());
            addLogInfo("Search WorkEffortAssocExtView with = " + EntityCondition.makeCondition(assCondition) + " to elaborate..." + hierarchyList2.size(), origWorkEffortRootId);
            
            GenericValue assoc = EntityUtil.getFirst(hierarchyList2);
            if(UtilValidate.isEmpty(assoc)) {
                addLogWarning("No assoc found with " + rootHierarchyAssocTypeId + " = " + origWorkEffortRootId + " - " + we.getString(E.workEffortId.name()), origWorkEffortRootId);
                continue;
            }*/
            Double assocWeight = assoc.getDouble(E.assocWeight.name());
            Long sequenceNum = assoc.getLong(E.sequenceNum.name());

            GenericValue leaf = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), assoc.getString(E.workEffortIdTo.name())), false);
            GenericValue leafType = delegator.getRelatedOne(E.WorkEffortType.name(), leaf);

            if ("Y".equals(leafType.getString(E.isRoot.name()))) {
                addLogInfo("Found workEffort with id = " + assoc.getString(E.workEffortIdTo.name()) + " ( type " + leafType.getString(E.workEffortTypeId.name()) + ") NOT to elaborate...", origWorkEffortRootId);
                continue;
            }
            if ((serviceTypeEnum.isUseEnableSnapshot() && "Y".equals(leafType.getString(E.enableSnapshot.name()))) || E.Y.name().equals(leafType.getString("copy"))) {

                // if the workEffort is a Root, elaborate the new Root
                if (serviceTypeEnum.isUseEnableSnapshot() && "Y".equals(leafType.getString(E.enableSnapshot.name())) && "Y".equals(leafType.getString(E.isRoot.name()))) {
                    addLogInfo("Found workEffort Root with id = " + assoc.getString(E.workEffortIdTo.name()) + " to elaborate...", origWorkEffortRootId);
                    setRecordElaborated(getRecordElaborated() + 1);

                    //ricorsione per caricare tutto l'albero
                    String newW = doRootCopy(leaf.getString(E.workEffortId.name()), storeRevisionWorkEffortAssoc, workEffortRevisionId, rootHierarchyAssocTypeId);

                    // Per gli snapshot NON aggiungo la relazione, perche tutte le relazioni, anche gerarchiche sono create col metodo successivo,
                    // tramite workEffortRevisionId... (manageWorkeffortAssoc)
                    // invece per il clone, che non ha il workEffortRevisionId, le relazioni gerarchiche e' meglio crearle adesso
                    if (ServiceTypeEnum.CLONE.equals(serviceTypeEnum)) {
                        doWorkEffortAssoc(newWorkEffortRootId, newW, assoc.getTimestamp(E.fromDate.name()), assoc.getTimestamp(E.thruDate.name()), rootHierarchyAssocTypeId);
                    }
                } else {
                    String workEffortParentId = leaf.getString(E.workEffortParentId.name());
                    // ATTENZIONE
                    // se COPY_ALL e il workEffort non e' suo figlio, non duplicarlo
                    if (ServiceTypeEnum.COPY_ALL.equals(serviceTypeEnum)) {
                        addLogInfo("Found workEffort with id = " + assoc.getString(E.workEffortIdTo.name()) + " ( parent " + workEffortParentId + ") NOT to elaborate...", origWorkEffortRootId);
                        continue;
                    }
                    addLogInfo("Found workEffort with id = " + assoc.getString(E.workEffortIdTo.name()) + " ( parent " + workEffortParentId + ") to elaborate...", origWorkEffortRootId);

                    // Creazione obiettivo
                    String newWorkEffortChildId = doWorkEffortAndDataCopy(newWorkEffortRootId, leaf.getString(E.workEffortId.name()), leafType.getString(E.workEffortTypeId.name()), newWorkEffortParentId, rootHierarchyAssocTypeId, listHierarchyAssocTypeId, estimatedStartDateTo, estimatedCompletionDateTo, assocWeight, sequenceNum, storeRevisionWorkEffortAssoc, workEffortRevisionId);
                    // workEffortAssocCopyUtil.putValue(leaf.getString(E.workEffortId.name()), newWorkEffortChildId, hierarchyAssocTypeId);

                    if (serviceTypeEnum.isUseWorkEffortRevisionId()) {
                        //Creazione associazione SNAPSHOT 
                        manageAssocToOriginal(leaf.getString(E.workEffortId.name()), newWorkEffortChildId, leaf.getTimestamp(E.estimatedStartDate.name()), leaf.getTimestamp(E.estimatedCompletionDate.name()), serviceTypeEnum.isUseEnableSnapshot(), "Y".equals(leafType.getString(E.isRoot.name())));
                    }

                    //ricorsione per caricare tutto l'albero e non solo la prima parte
                    doRootCopyOther(leaf.getString(E.workEffortId.name()), newWorkEffortRootId, newWorkEffortChildId, listHierarchyAssocTypeId, storeRevisionWorkEffortAssoc, workEffortRevisionId, rootHierarchyAssocTypeId);
                }
            }
        }
    }

    /**
     * Create Assoc with the new workEffort
     * @param origWorkEffortRootId
     * @param newWorkEffortRootId
     * @param estimatedStartDateTo
     * @param estimatedCompletionDateTo
     * @param snapshot
     * @throws GeneralException
     */
    private void doWorkEffortAssoc(String workEffortIdFrom, String workEffortIdTo, Timestamp estimatedStartDateTo, Timestamp estimatedCompletionDateTo, String workEffortAssocTypeId) throws GeneralException {
        String successMsg = "Relation " + workEffortAssocTypeId + " successfully created";
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + " Relation " + workEffortAssocTypeId;
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.workEffortIdFrom.name(), workEffortIdFrom);
        serviceMap.put(E.workEffortIdTo.name(), workEffortIdTo);
        serviceMap.put(E.workEffortAssocTypeId.name(), workEffortAssocTypeId);
        serviceMap.put(E.fromDate.name(), estimatedStartDateTo);
        serviceMap.put(E.thruDate.name(), estimatedCompletionDateTo);
        runSyncCrud("crudServiceDefaultOrchestration", E.WorkEffortAssoc.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, false, workEffortIdFrom);
    }

    /**
     * Create Assoc with the original workeffort with type COPY or SNAPHOT
     * @param origWorkEffortRootId
     * @param newWorkEffortRootId
     * @param estimatedStartDateTo
     * @param estimatedCompletionDateTo
     * @param snapshot
     * @throws GeneralException
     */
    private void doWorkEffortAssocToOriginal(String origWorkEffortRootId, String newWorkEffortRootId, Timestamp estimatedStartDateTo, Timestamp estimatedCompletionDateTo, boolean snapshot) throws GeneralException {
        String successMsg = "Relation " + (snapshot ? "SNAPSHOT" : "COPY") + " successfully created";
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + " Relation " + (snapshot ? "SNAPSHOT" : "COPY");
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.workEffortIdFrom.name(), origWorkEffortRootId);
        serviceMap.put(E.workEffortIdTo.name(), newWorkEffortRootId);
        serviceMap.put(E.workEffortAssocTypeId.name(), snapshot ? E.SNAPSHOT.name() : E.COPY.name());
        serviceMap.put(E.fromDate.name(), estimatedStartDateTo);
        serviceMap.put(E.thruDate.name(), estimatedCompletionDateTo);
        if (E.Y.name().equals((String)context.get(E.storeRevisionWorkEffortAssoc.name()))) {
            serviceMap.put(E.workEffortRevisionId.name(), context.get(E.workEffortRevisionId.name()));
        }
        runSyncCrud("crudServiceDefaultOrchestration", E.WorkEffortAssoc.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, false, origWorkEffortRootId);
    }

    /**
     * Create Assoc with the original workeffort with type COPY or SNAPHOT
     * <br> for snapshot, that use workEffrotRevisionId, there are one assoc for each workEffort
     * <br> for copy there is only one assoc from Root to Root
     * @param origWorkEffortRootId
     * @param newWorkEffortRootId
     * @param estimatedStartDateTo
     * @param estimatedCompletionDateTo
     * @param snapshot
     * @param isRoot
     * @throws GeneralException
     */
    private void manageAssocToOriginal(String origWorkEffortRootId, String newWorkEffortRootId, Timestamp estimatedStartDateTo, Timestamp estimatedCompletionDateTo, boolean snapshot, boolean isRoot) throws GeneralException {
        if (snapshot) {
            //Creazione associazione SNAPSHOT 
            doWorkEffortAssocToOriginal(origWorkEffortRootId, newWorkEffortRootId, estimatedStartDateTo, estimatedCompletionDateTo, snapshot);
        } else if (isRoot) {
            //Creazione associazione COPY 
            doWorkEffortAssocToOriginal(origWorkEffortRootId, newWorkEffortRootId, estimatedStartDateTo, estimatedCompletionDateTo, snapshot);
        }
    }

    /**
     * Create map with data and create workEffort
     * @param workEffortRootId
     * @param origWorkEffortId
     * @param workEffortTypeId
     * @param workEffortParentId
     * @param parentHierarchyAssocTypeId, eventuale relazione gerarchica per il padre
     * @param listHierarchyAssocTypeId, raccoglie le relazion igerarchiche e le esclude nella ricerca delle workEfortAssoc
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @param snapshot
     * @param assocWeight
     * @param sequenceNum
     * @param workEffortRevisionId
     * @return
     * @throws GeneralException
     */
    private String doWorkEffortAndDataCopy(String workEffortRootId, String origWorkEffortId, String workEffortTypeId, String workEffortParentId, String parentHierarchyAssocTypeId, List<String> listHierarchyAssocTypeId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate, Double assocWeight, Long sequenceNum, String storeRevisionWorkEffortAssoc, String workEffortRevisionId) throws GeneralException {
        //Copying WorkEffort
        Map<String, Object> data = UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId, E.workEffortParentId.name(), workEffortParentId, E.workEffortRootId.name(), workEffortRootId, E.parentHierarchyAssocTypeId.name(), parentHierarchyAssocTypeId, E.hierarchyAssocTypeId.name(), listHierarchyAssocTypeId, E.estimatedStartDate.name(), estimatedStartDate, E.estimatedCompletionDate.name(), estimatedCompletionDate, E.assocWeight.name(), assocWeight, E.sequenceNum.name(), sequenceNum);
        String snapShotDescription = UtilGenerics.cast(context.get(E.snapShotDescription.name()));
        String organizationPartyId = UtilGenerics.cast(context.get(ORGANIZATION_PARTY_ID));

        if (serviceTypeEnum.isUseWorkEffortRevisionId()) {
            data.put(E.snapShotDescription.name(), snapShotDescription);
            data.put(E.workEffortRevisionId.name(), workEffortRevisionId);

            GenericValue workEffortRevision = findOne(E.WorkEffortRevision.name(), EntityCondition.makeCondition(E.workEffortRevisionId.name(), workEffortRevisionId), "Found more workEffortRevision with id = " + workEffortRevisionId, "No workEffortRevision with id = " + workEffortRevisionId);
            data.put(E.hasAcctgTrans.name(), workEffortRevision.getString(E.hasAcctgTrans.name()));
            data.put(E.fromDate.name(), workEffortRevision.getString(E.fromDate.name()));
            data.put(E.toDate.name(), workEffortRevision.getString(E.toDate.name()));
            data.put(E.snapShotDate.name(), UtilDateTime.nowTimestamp());
        } // nel WorkEffortCopy uso la parentHierarchyAssocTypeId, se questa e' valorizzata, per creare la relazione col padre (from)
        Map<String, Object> wrkCreationRes = new WorkEffortCopy(this, serviceTypeEnum, storeRevisionWorkEffortAssoc, workEffortRevisionId).copy(origWorkEffortId, null, data); // serve solo per la relazione col padre
        String newWorkEffortId = (String)wrkCreationRes.get(E.workEffortId.name());
        addLogInfo("New workEffortId=" + newWorkEffortId, origWorkEffortId);

        //Copying Measures
        addLogInfo("Copying Measures", origWorkEffortId);
        Map<String, Object> res = new WorkEffortMeasureCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, UtilMisc.toMap(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, context.get(WorkEffortRootCopyService.GL_ACCOUNT_CREATION)));
        res.put(ORGANIZATION_PARTY_ID, organizationPartyId);

        if (serviceTypeEnum.isUseWorkEffortRevisionId()) {
            //Copying  Party Assignments
            addLogInfo("Copying Party Assignments", origWorkEffortId);
            new WorkEffortPartyAssignmentCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, UtilMisc.toMap(E.estimatedStartDate.name(), estimatedStartDate, E.estimatedCompletionDate.name(), estimatedCompletionDate));

            //Copying Transactions
            addLogInfo("Copying Transactions", origWorkEffortId);
            res.put(E.workEffortRevisionId.name(), workEffortRevisionId);

            res.put(E.hasAcctgTrans.name(), data.get(E.hasAcctgTrans.name()));
            res.put(E.fromDate.name(), data.get(E.fromDate.name()));
            res.put(E.toDate.name(), data.get(E.toDate.name()));
            new WorkEffortTransactionCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, res);

            //Copia changed Status
            addLogInfo("Copying Status changed", origWorkEffortId);
            new WorkEffortStatusCopy(this).copy(origWorkEffortId, newWorkEffortId, UtilMisc.toMap(E.estimatedStartDate.name(), estimatedStartDate));
        } else if (!ServiceTypeEnum.COPY.equals(serviceTypeEnum)) {
            //Copying  Party Assignments
            addLogInfo("Copying Party Assignments", origWorkEffortId);
            new WorkEffortPartyAssignmentCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, UtilMisc.toMap(E.estimatedStartDate.name(), estimatedStartDate, E.estimatedCompletionDate.name(), estimatedCompletionDate));

            //Copying Transactions
            res.put(E.hasAcctgTrans.name(), E.N.name());
            addLogInfo("Copying Transactions", origWorkEffortId);
            new WorkEffortTransactionCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, res);

            // listHierarchyAssocTypeId raccoglie le relazioni gerachiche,
            // Copying Associations tranne quelle gerarchiche, che vanno copiate dopo
            addLogInfo("Copying Associations", origWorkEffortId);
            new WorkEffortAssocCopy(this, storeRevisionWorkEffortAssoc, workEffortRevisionId, copyWorkEffortAssocCopy).copy(origWorkEffortId, newWorkEffortId, UtilMisc.toMap(E.hierarchyAssocTypeId.name(), listHierarchyAssocTypeId));
        }

        //Copying Attributes
        addLogInfo("Copying Attributes", origWorkEffortId);
        new WorkEffortAttributeCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, null);

        //Copying Notes
        addLogInfo("Copying Notes", origWorkEffortId);
        new WorkEffortNoteCopy(this, serviceTypeEnum.isUseEnableSnapshot()).copy(origWorkEffortId, newWorkEffortId, UtilMisc.toMap(E.estimatedStartDate.name(), estimatedStartDate));

        if (serviceTypeEnum.isUseEnableSnapshot()) {
            //Copying Attributes
            if (skipCopyContent) {
                addLogInfo("Skip Copying Attachment", origWorkEffortId);
            } else {
                // Attachment Content
                addLogInfo("Copying Attachment", origWorkEffortId);
                new WorkEffortContentCopy(this).copy(origWorkEffortId, newWorkEffortId, res);
            }
        }

        return newWorkEffortId;
    }

    private void executeWriteLogs(Timestamp startTimestamp, String jobLogId) {
        Map<String, Object> serviceParameters = FastMap.newInstance();

        serviceParameters.put(DELETE_OLD_ROOTS, (String)context.get(DELETE_OLD_ROOTS));
        serviceParameters.put(CHECK_EXISTING, (String)context.get(CHECK_EXISTING));
        serviceParameters.put(E.estimatedStartDateTo.name(), (Timestamp)context.get(E.estimatedStartDateTo.name()));
        serviceParameters.put(E.estimatedCompletionDateTo.name(), (Timestamp)context.get(E.estimatedCompletionDateTo.name()));
        serviceParameters.put(GL_ACCOUNT_CREATION, (String)context.get(GL_ACCOUNT_CREATION));
        serviceParameters.put(E.estimatedStartDateFrom.name(), (Timestamp)context.get(E.estimatedStartDateFrom.name()));
        serviceParameters.put(E.estimatedCompletionDateFrom.name(), (Timestamp)context.get(E.estimatedCompletionDateFrom.name()));
        serviceParameters.put(E.workEffortTypeIdTo.name(), (String)context.get(E.workEffortTypeIdTo.name()));
        serviceParameters.put(E.workEffortTypeIdFrom.name(), (String)context.get(E.workEffortTypeIdFrom.name()));
        if (UtilValidate.isNotEmpty(context.get(E.workEffortId.name()))) {
            serviceParameters.put(E.workEffortId.name(), (String)context.get(E.workEffortId.name()));
        }

        super.writeLogs(startTimestamp, jobLogId, serviceParameters);
    }

    private void writeSystemNote(String serviceName) throws GenericEntityException {
        //Creazione nota
        GenericValue noteData = delegator.makeValue(E.NoteData.name());
        noteData.put(E.noteId.name(), delegator.getNextSeqId(E.NoteData.name()));
        noteData.put(E.noteName.name(), "SYSTEMNOTE");
        noteData.put(E.noteDateTime.name(), UtilDateTime.nowTimestamp());
        noteData.put(E.noteInfo.name(), "WorkEffort Root " + serviceName + " for type \"" + context.get(E.workEffortTypeIdFrom.name()) + "\" finished");
        noteData.put(E.noteParty.name(), userLogin.getString(E.partyId.name()));
        noteData.put(E.isPublic.name(), E.N.name());
        delegator.create(noteData);
    }
}
