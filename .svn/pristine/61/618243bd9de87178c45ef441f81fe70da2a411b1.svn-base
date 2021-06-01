package com.mapsengineering.workeffortext.services.rootcopy;

import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

/**
 * Manage WorkEfforTransaction, only for snapshot, snapshot = true
 *
 */
public class WorkEffortTransactionCopy extends AbstractWorkEffortDataCopy {

    public static final String MODULE = WorkEffortTransactionCopy.class.getName();

    public static final String ACCINP_OBJ = "ACCINP_OBJ";
     
    /**
     * Constructor, snapshot = true
     */
    public WorkEffortTransactionCopy(WorkEffortRootCopyService service, boolean snapshot) {
        super(service, snapshot);
    }

    /**
     * Search WorkEffortTransactionIndicatorViewWithScorekpi with selected workEffortMeasureId and execute crudServiceDefaultOrchestration_WorkEffortTransactionView
     */
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        if (UtilValidate.isNotEmpty(data) && UtilValidate.isNotEmpty(data.get(E.workEffortMeasureId.name()))) {
            List<Map<String, String>> newWorkEffortMeasureIdList = UtilGenerics.checkList(data.get(E.workEffortMeasureId.name()));
            String workEffortRevisionId = (String)data.get(E.workEffortRevisionId.name());
            for (Map<String, String> oldAndNewWorkEffortMeasureIdMap : newWorkEffortMeasureIdList) {
                String oldWorkEffortMeasureId = oldAndNewWorkEffortMeasureIdMap.keySet().iterator().next();
                String newWorkEffortMeasureId = oldAndNewWorkEffortMeasureIdMap.get(oldWorkEffortMeasureId);
                
                // i movimenti di SCORE sono estratti nella prima lista
                List<EntityCondition> conditionList = getTransCondition(origWorkEffortId, oldWorkEffortMeasureId);
                conditionList.add(addOtherCondition(data));
                // i movimenti di SCOREKPI sono estratti successivamente nella seconda lista
                conditionList.add(EntityCondition.makeCondition(E.acctgTransTypeId.name(), EntityComparisonOperator.NOT_EQUAL, E.SCOREKPI.name()));
                List<GenericValue> transactionList = getDelegator().findList(E.WorkEffortTransactionIndicatorView.name(), EntityCondition.makeCondition(conditionList), null, null, null, getUseCache());
                addLogInfo("Found " + transactionList.size() + " transaction with condition : " + conditionList, origWorkEffortId);
                
                List<EntityCondition> conditionListScore = getTransCondition(origWorkEffortId, oldWorkEffortMeasureId);
                conditionListScore.add(addOtherCondition(data));
                
                conditionListScore.add(EntityCondition.makeCondition(E.acctgTransTypeId.name(), E.SCOREKPI.name()));
                transactionList.addAll(getDelegator().findList(E.WorkEffortTransactionIndicatorViewWithScorekpi.name(), EntityCondition.makeCondition(conditionListScore), null, null, null, getUseCache()));
                addLogInfo("Found " + transactionList.size() + " transaction with condition : " + conditionListScore, origWorkEffortId);
                createTransactions(transactionList, newWorkEffortMeasureId, origWorkEffortId, newWorkEffortId, workEffortRevisionId);
                
                // i movimenti di RESOURCE sono estratti nella terza lista, in modo da escludere gli SCORE
                searchAndCreateResourceTransaction(oldWorkEffortMeasureId, newWorkEffortMeasureId, origWorkEffortId, newWorkEffortId, workEffortRevisionId, (String) data.get(GenericService.ORGANIZATION_PARTY_ID));
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private void searchAndCreateResourceTransaction(String oldWorkEffortMeasureId, String newWorkEffortMeasureId, String origWorkEffortId, String newWorkEffortId, String workEffortRevisionId, String organizationId) throws GeneralException {
        //Eseguo metodo
        Map<String, Object> groovyContext = new java.util.HashMap<String, Object>();
        Map<String, Object> parameters = new java.util.HashMap<String, Object>();
        parameters.put("workEffortId", origWorkEffortId);
        
        groovyContext.put("userLogin", getUserLogin());
        groovyContext.put(DEFAULT_ORGANIZATION_PARTY_ID, organizationId);
        groovyContext.put("parameters", parameters);
        groovyContext.put("locale", getLocale());
        groovyContext.put("timeZone", getTimeZone());
        groovyContext.put("dispatcher", getDispatcher());
        groovyContext.put("delegator", getDelegator());
        groovyContext.put("workEffortId", origWorkEffortId);
        
        addLogInfo("Search resource transaction with service... ", origWorkEffortId);
        //Attendo in output un parametro "listIt" - list di generic value
        GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWETResource.groovy", groovyContext);
        List<GenericValue> values = (List<GenericValue>) groovyContext.get("listIt");
        List<GenericValue> resourceTransactionList = EntityUtil.filterByCondition(values, EntityCondition.makeCondition(E.weTransMeasureId.name(), oldWorkEffortMeasureId));
        addLogInfo("Found " + resourceTransactionList.size() + " transaction with condition " + EntityCondition.makeCondition(E.weTransMeasureId.name(), oldWorkEffortMeasureId), origWorkEffortId);
        createTransactions(resourceTransactionList, newWorkEffortMeasureId, origWorkEffortId, newWorkEffortId, workEffortRevisionId);
    }

    private void createTransactions(List<GenericValue> transactionList, String newWorkEffortMeasureId, String origWorkEffortId, String newWorkEffortId, String workEffortRevisionId) throws GeneralException {
        for (GenericValue transaction : transactionList) {
            createTransaction(transaction, newWorkEffortMeasureId, origWorkEffortId, newWorkEffortId, workEffortRevisionId);
        }
        
    }

    /** Add condition for ModelloObiettivo o NOT modelloSuObiettivo
     * @throws GeneralException */
    private EntityCondition addOtherCondition(Map<String, ? extends Object> data) throws GeneralException {
        List<EntityCondition> conditionList = FastList.newInstance();
        // Esistono movimenti :
        // - non modello ACCINP_UO
        // - modello su obiettivo, ACCINP_OBJ
        // - modello su prodotto, servizio ACCINP_PRD
        EntityCondition conditionModelloSuObiettivo = EntityCondition.makeCondition(E.inputEnumId.name(), ACCINP_OBJ);
        EntityCondition conditionNonModelloSuObiettivo = getConditionNonModelloSuObiettivo(data);
        
        // in or
        if (UtilValidate.isNotEmpty(data.get(E.hasAcctgTrans.name())) && !"Y".equals(data.get(E.hasAcctgTrans.name()))) {
            conditionList.add(conditionModelloSuObiettivo);
        } else {
            List<EntityCondition> inputEnumIdCondition = FastList.newInstance();
            inputEnumIdCondition.add(conditionModelloSuObiettivo);
            inputEnumIdCondition.add(conditionNonModelloSuObiettivo);
            
            conditionList.add(EntityCondition.makeCondition(inputEnumIdCondition, EntityJoinOperator.OR));
            
        }
        
        return EntityCondition.makeCondition(conditionList);
    }

    /**Create condition for NOT ModelloSuObiettivo, and weTransDate if fromDate or toDate are not empty */
    private EntityCondition getConditionNonModelloSuObiettivo(Map<String, ? extends Object> data) {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(E.inputEnumId.name(), EntityOperator.NOT_EQUAL, ACCINP_OBJ));
        if(UtilValidate.isNotEmpty(returnPeriodTransCondition(data))) {
            condition.addAll(returnPeriodTransCondition(data));
        }
        return EntityCondition.makeCondition(condition);
    }

    /**Create condition for weTransDate if fromDate or toDate are not empty */
    private List<EntityCondition> returnPeriodTransCondition(Map<String, ? extends Object> data) {
        List<EntityCondition> periodTransCondition = FastList.newInstance();
        if (UtilValidate.isNotEmpty(data.get(E.fromDate.name()))) {
            periodTransCondition.add(EntityCondition.makeCondition(E.weTransDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, data.get(E.fromDate.name())));
        }
        if (UtilValidate.isNotEmpty(data.get(E.toDate.name()))) {
            periodTransCondition.add(EntityCondition.makeCondition(E.weTransDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, data.get(E.toDate.name())));
        }
        return periodTransCondition;
    }

    /** Filter movement with workEffortSnapshotId empty */
    private List<EntityCondition> getTransCondition(String origWorkEffortId, String oldWorkEffortMeasureId) throws GenericEntityException {
        List<EntityCondition> originalTransCondition = FastList.newInstance();
        originalTransCondition.add(EntityCondition.makeCondition(E.weTransWeId.name(), origWorkEffortId));
        originalTransCondition.add(EntityCondition.makeCondition(E.weTransMeasureId.name(), oldWorkEffortMeasureId));                
        //GN-319 prendo movimenti con workEffortSnapshotId non vuoto
        originalTransCondition.add(EntityCondition.makeCondition(E.weTransWorkEffortSnapShotId.name(), null));
        
        return originalTransCondition;
    }


    /**
     * Create AcctgTrans and AcctgTransEntry with workEffortSnapshotId and workEffortRevisionId
     * @param transaction
     * @param newWorkEffortMeasureId
     * @param origWorkEffortId
     * @param workEffortRevisionId
     * @throws GeneralException
     */
    private void createTransaction(GenericValue transaction, String newWorkEffortMeasureId, String origWorkEffortId, String newWorkEffortId, String workEffortRevisionId) throws GeneralException {
        Map<String, Object> serviceMap = FastMap.newInstance();

        serviceMap.putAll(transaction.getAllFields());
        serviceMap.put(E._AUTOMATIC_PK_.name(), E.Y.name());
        serviceMap.remove(E.weTransId.name());
        serviceMap.remove(E.weTransEntryId.name());

        serviceMap.put(E.voucherRef.name(), newWorkEffortMeasureId);
        serviceMap.put(E.weTransMeasureId.name(), newWorkEffortMeasureId);
        serviceMap.put(E.origTransValue.name(), transaction.get(E.origAmount.name()));
        serviceMap.put(E.weTransProductId.name(), transaction.get(E.transProductId.name()));
        WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(getDelegator(), getDispatcher());
        serviceMap.put(E.defaultOrganizationPartyId.name(), workEffortFindServices.getOrganizationId(getUserLogin(), false));

        // Per copiare i movimenti SCOREKPI bisogna fare delle modifiche al genericValue:
        if(E.SCOREKPI.name().equals(transaction.get(E.weAcctgTransAccountId.name()))) {
            serviceMap.put(E.weTransAccountId.name(), E.SCOREKPI.name());
        }
        
        String successMsg = "Created Transaction for workEffort " + newWorkEffortId;
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortTransactionView.name() + " for workEffort " + newWorkEffortId;
        // in caso di inputEnumId = ACCINP_OBJ sara i lservizio createWeTrans a recuperare il workEffortId dal workEffortMeasure...
        Map<String, Object> result = runSyncCrud("crudServiceDefaultOrchestration_WorkEffortTransactionView", E.WorkEffortTransactionView.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
        if (ServiceUtil.isSuccess(result) && UtilValidate.isNotEmpty(workEffortRevisionId)) {
            Map<String, Object> resultId = UtilGenerics.checkMap(result.get(E.id.name()));
            addSnapshotParam(resultId, origWorkEffortId, workEffortRevisionId);
        }
    }

    private void addSnapshotParam(Map<String, Object> resultId, String origWorkEffortId, String workEffortRevisionId) throws GeneralException {
        if (UtilValidate.isNotEmpty(resultId)) {
            String acctgTransId = UtilGenerics.cast(resultId.get(E.weTransId.name()));
            String acctgTransEntrySeqId = UtilGenerics.cast(resultId.get(E.weTransEntryId.name()));

            GenericValue acctgTrans = getDelegator().findOne(E.AcctgTrans.name(), UtilMisc.toMap(E.acctgTransId.name(), acctgTransId), false);
            GenericValue acctgTransEntry = getDelegator().findOne(E.AcctgTransEntry.name(), UtilMisc.toMap(E.acctgTransId.name(), acctgTransId, E.acctgTransEntrySeqId.name(), acctgTransEntrySeqId), false);

            acctgTrans.set(E.workEffortSnapshotId.name(), origWorkEffortId);
            acctgTrans.set(E.workEffortRevisionId.name(), workEffortRevisionId);

            acctgTransEntry.set(E.workEffortSnapshotId.name(), origWorkEffortId);
            acctgTransEntry.set(E.workEffortRevisionId.name(), workEffortRevisionId);

            acctgTrans.store();
            acctgTransEntry.store();
        }
        
    }
}
