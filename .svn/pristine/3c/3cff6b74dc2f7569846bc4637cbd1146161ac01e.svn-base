package com.mapsengineering.emplperf.update;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import com.mapsengineering.emplperf.update.acctgtrans.EmplPerfReadAcctgTransConditionCreator;
import com.mapsengineering.emplperf.update.acctgtrans.EmplPerfValueUpdateAcctgTrans;
import com.mapsengineering.emplperf.update.assoc.EmplPerfReadAssocConditionCreator;
import com.mapsengineering.emplperf.update.assoc.EmplPerfValueUpdateAssoc;
import com.mapsengineering.emplperf.update.note.EmplPerfReadNoteConditionCreator;
import com.mapsengineering.emplperf.update.note.EmplPerfValueUpdateNote;
import com.mapsengineering.workeffortext.services.E;

import javolution.util.FastMap;

/**
 * Update workEffort note from previous for emplperf 
 *
 */
public class EmplPerfUpdateFromPrevious extends GenericService {
    private static final String MODULE = EmplPerfUpdateFromPrevious.class.getName();
    private static final String SERVICE_NAME_NOTE = "executeEmplPerfUpdateWorkEffortNote";
    private static final String SERVICE_TYPE_ID_NOTE = "EMPL_PERF_UPDATE_NOT";
    private static final String SERVICE_NAME_ACCTGTRANS = "executeEmplPerfUpdateAcctgTrans";
    private static final String SERVICE_TYPE_ID_ACCTGTRANS = "EMPL_PERF_UPDATE_ACC";
    private static final String SERVICE_NAME_ASSOC = "executeEmplPerfUpdateWorkEffortAssoc";
    private static final String SERVICE_TYPE_ID_ASSOC = "EMPL_PERF_UPDATE_ASS";

    /**
     * contains the key for write log
     */
    private String keyId;

    private EmplPerfUpdateHelper emplPerfUpdateHelper;

    /**
     * Constructor
     */
    public EmplPerfUpdateFromPrevious(DispatchContext dctx, Map<String, Object> context, String serviceName, String serviceTypeId, String module) {
        super(dctx, context, serviceName, serviceTypeId, module);
        emplPerfUpdateHelper = new EmplPerfUpdateHelper(getDelegator());
    }

    /**
     * Main service for update Note
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> emplPerfUpdateWorkEffortNoteFromPrevious(DispatchContext dctx, Map<String, Object> context) {
        EmplPerfUpdateFromPrevious obj = new EmplPerfUpdateFromPrevious(dctx, context, SERVICE_NAME_NOTE, SERVICE_TYPE_ID_NOTE, MODULE);
        EntityCondition readCondition = new EmplPerfReadNoteConditionCreator().buildReadCondition(context);
        EmplPerfValueUpdateNote emplPerfValueUpdateNote = new EmplPerfValueUpdateNote();
        obj.mainLoop(EmplPerfServiceEnum.WorkEffortNoteAndDataCompareValue.name(), readCondition, emplPerfValueUpdateNote, null);
        return obj.getResult();
    }

    /**
     * Main service for update acctgTrans
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> emplPerfUpdateAcctgTransFromPrevious(DispatchContext dctx, Map<String, Object> context) {
        EmplPerfUpdateFromPrevious obj = new EmplPerfUpdateFromPrevious(dctx, context, SERVICE_NAME_ACCTGTRANS, SERVICE_TYPE_ID_ACCTGTRANS, MODULE);
        EntityCondition readCondition = new EmplPerfReadAcctgTransConditionCreator().buildReadCondition(context);
        EmplPerfValueUpdateAcctgTrans emplPerfValueUpdateAcctgTrans = new EmplPerfValueUpdateAcctgTrans();
        String entityName = EmplPerfServiceEnum.WorkEffortAndAcctgTransCompareValue.name();

        EntityFindOptions entityFindOption = new EntityFindOptions();
        entityFindOption.setDistinct(true);

        String emplPositionTypeId = (String)context.get(E.emplPositionTypeId.name());
        if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
            entityName = EmplPerfServiceEnum.WorkEffortAndEmplPositionTypeAndAcctgTransCompareValue.name();
        }
        String templateTypeId = (String)context.get(E.templateTypeId.name());
        String templateId = (String)context.get(E.templateId.name());
        if (UtilValidate.isNotEmpty(templateTypeId) || UtilValidate.isNotEmpty(templateId)) {
            entityName = EmplPerfServiceEnum.WorkEffortAndTemplateAndAcctgTransCompareValue.name();
            if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
                entityName = EmplPerfServiceEnum.WorkEffortAndTemplateAndEmplPositionTypeAndAcctgTransCompareValue.name();
            }
        }
        obj.mainLoop(entityName, readCondition, emplPerfValueUpdateAcctgTrans, entityFindOption);
        return obj.getResult();
    }

    /**
     * Main service for update assoc
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> emplPerfUpdateWorkEffortAssocFromPrevious(DispatchContext dctx, Map<String, Object> context) {
        EmplPerfUpdateFromPrevious obj = new EmplPerfUpdateFromPrevious(dctx, context, SERVICE_NAME_ASSOC, SERVICE_TYPE_ID_ASSOC, MODULE);
        EntityCondition readCondition = new EmplPerfReadAssocConditionCreator().buildReadCondition(context);
        EmplPerfValueUpdateAssoc emplPerfValueUpdateAssoc = new EmplPerfValueUpdateAssoc();
        obj.mainLoop(EmplPerfServiceEnum.WorkEffortAndAssocCompareValue.name(), readCondition, emplPerfValueUpdateAssoc, null);
        return obj.getResult();
    }

    /**
     * Main loop for search and call action
     */
    private void mainLoop(String entityName, EntityCondition entityCondition, EmplPerfValueUpdate gvUpdater, EntityFindOptions entityFindOption) {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        String msg = "Starting Employment Perfomance UPDATE FROM PREVIOUS";
        addLogInfo(msg, null);

        keyId = null;
        try {
            executeSearchAndUpdate(entityName, entityCondition, gvUpdater, entityFindOption);
        } catch (Exception e) {
            msg = "Error: ";
            addLogError(e, msg, keyId);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            msg = "Finish Employment Perfomance UPDATE FROM PREVIOUS";
            addLogInfo(msg, keyId);

            String jobLogId = delegator.getNextSeqId("JobLog");
            executeWriteLogs(startTimestamp, jobLogId);

            getResult().put(ServiceLogger.SESSION_ID, getSessionId());
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
        }
    }

    private void executeSearchAndUpdate(String entityName, EntityCondition entityCondition, EmplPerfValueUpdate gvUpdater, EntityFindOptions entityFindOption) throws Exception {
        if (gvUpdater.executeStandardImport()) {
            clearTableInterface(gvUpdater);
        }
        executeSearchAndAction(entityName, entityCondition, gvUpdater, entityFindOption);
        if (gvUpdater.executeStandardImport()) {
            executeStandardImport(gvUpdater);
        }

    }

    /**
     * esegue import delle tabell interfaccia
     * @param gvUpdater
     * @throws Exception
     */
    private void executeStandardImport(EmplPerfValueUpdate gvUpdater) throws Exception {
        setDefaultOrganizationPartyId((String)context.get(GenericService.ORGANIZATION_ID));
        Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(ImportManagerConstants.SERVICE_NAME, "IN", context);
        serviceMap.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, getDefaultOrganizationPartyId());
        serviceMap.put(E.entityListToImport.name(), gvUpdater.getEntityListToImport());
        Map<String, Object> result = dispatcher.runSync(ImportManagerConstants.SERVICE_NAME, serviceMap);
        String msg = "Finished standardImport with " + result;
        addLogInfo(msg, null);
    }

    /**
     * pulisce tabelle interfaccia
     * @param gvUpdater
     * @throws GenericEntityException
     */
    private void clearTableInterface(EmplPerfValueUpdate gvUpdater) throws GenericEntityException {
        String msg = "Clear table interface = " + gvUpdater.getEntityListToImport();
        addLogInfo(msg, null);
        emplPerfUpdateHelper.deleteAllInterfaceData(gvUpdater.getEntityListToImport());
    }

    private void executeSearchAndAction(final String entityName, final EntityCondition entityCondition, final EmplPerfValueUpdate gvUpdater, final EntityFindOptions entityFindOption) throws Exception {
        new TransactionRunner(getClass().getName(), true, getTransactionTimeout(), new TransactionItem() {
            @Override
            public void run() throws Exception {
                executeSearch(entityName, entityCondition, gvUpdater, entityFindOption);
            }
        }).execute().rethrow();
    }

    private void executeSearch(String entityName, EntityCondition entityCondition, EmplPerfValueUpdate gvUpdater, EntityFindOptions entityFindOption) throws Exception {
        EntityListIterator iter = delegator.find(entityName, entityCondition, null, gvUpdater.getFieldsToSelect(), gvUpdater.getOrderBy(), entityFindOption);
        addLogInfo("Search " + entityName + " with " + entityCondition, null);
        try {
            GenericValue gv;
            while ((gv = iter.next()) != null) {
                setRecordElaborated(getRecordElaborated() + 1);
                emplPerfUpdateHelper.setKeyAndMsg(gvUpdater, gv);
                keyId = emplPerfUpdateHelper.getKeyId();
                addLogInfo(emplPerfUpdateHelper.getLogMsg(), keyId);
                gvUpdater.doAction(getJobLogger(), delegator, gv, context);
                String msg = "Finished Employment Perfomance Form UPDATE FROM PREVIOUS";
                addLogInfo(msg, keyId);
                keyId = null;
            }
        } finally {
            iter.close();
        }
    }

    private void executeWriteLogs(Timestamp startTimestamp, String jobLogId) {
        Map<String, Object> serviceParameters = FastMap.newInstance();

        serviceParameters.put(ParamsEnum.estimatedStartDate.name(), context.get(ParamsEnum.estimatedStartDate.name()));
        serviceParameters.put(ParamsEnum.estimatedCompletionDate.name(), context.get(ParamsEnum.estimatedCompletionDate.name()));
        serviceParameters.put(ParamsEnum.orgUnitId.name(), context.get(ParamsEnum.orgUnitId.name()));
        serviceParameters.put(ParamsEnum.orgUnitRoleTypeId.name(), context.get(ParamsEnum.orgUnitRoleTypeId.name()));

        serviceParameters.put(ParamsEnum.templateId.name(), context.get(ParamsEnum.templateId.name()));
        serviceParameters.put(ParamsEnum.templateTypeId.name(), context.get(ParamsEnum.templateTypeId.name()));
        serviceParameters.put(ParamsEnum.emplPositionTypeId.name(), context.get(ParamsEnum.emplPositionTypeId.name()));

        serviceParameters.put(ParamsEnum.readDate.name(), context.get(ParamsEnum.readDate.name()));
        serviceParameters.put(ParamsEnum.writeDate.name(), context.get(ParamsEnum.writeDate.name()));
        serviceParameters.put(ParamsEnum.updateWorkEffortNote.name(), context.get(ParamsEnum.updateWorkEffortNote.name()));
        serviceParameters.put(ParamsEnum.updateAcctgTransAndEntry.name(), context.get(ParamsEnum.updateAcctgTransAndEntry.name()));
        serviceParameters.put(ParamsEnum.updateWorkEffortPurposeType.name(), context.get(ParamsEnum.updateWorkEffortPurposeType.name()));
        serviceParameters.put(ParamsEnum.updateWorkEffortAssoc.name(), context.get(ParamsEnum.updateWorkEffortAssoc.name()));
        serviceParameters.put(ParamsEnum.updateWorkEffortAssocType.name(), context.get(ParamsEnum.updateWorkEffortAssocType.name()));

        super.writeLogs(startTimestamp, jobLogId, serviceParameters);
    }

}
