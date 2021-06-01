package com.mapsengineering.workeffortext.services.rootcopy.util;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEffortAssoc
 *
 */
public class WorkEffortAssocCopyUtil {
    private GenericService service;
    private String workEffortRevisionId;
    private String storeRevisionWorkEffortAssoc;

    private List<String> workEffortRootIdList;
    private HashSet<Map<String, Object>> workEffortAssocInfo;
    private Timestamp estimatedStartDate;
    private Timestamp estimatedCompletionDate;
    

    /**
     * Constructor
     * @param service
     * @param workEffortRevisionId
     * @param refDate 
     * @param estimatedCompletionDateTo 
     */
    public WorkEffortAssocCopyUtil(GenericService service, String workEffortRevisionId, String storeRevisionWorkEffortAssoc, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate) {
        this.service = service;
        this.workEffortRevisionId = workEffortRevisionId;
        this.storeRevisionWorkEffortAssoc = storeRevisionWorkEffortAssoc;
        this.workEffortAssocInfo = new HashSet<Map<String, Object>>();
        this.workEffortRootIdList = FastList.newInstance();
        this.estimatedStartDate = estimatedStartDate;
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    /**
     * Transaction for search and run WorkEffortAssoc
     * @param workEffortRevisionId
     * @throws Exception
     */
    public void copyMassiveWorkEffortAssoc(final String workEffortRevisionId) throws Exception {
        new TransactionRunner(service.getModule(), true, ServiceLogger.TRANSACTION_TIMEOUT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                searchAndRunMassiveWorkEffortAssoc(service.getDelegator(), workEffortRevisionId);
            }
        }).execute().rethrow();
    }

    private void addWorkEffortAssocInfo(GenericValue assoc) {
        Map<String, Object> assocPk = getPrimaryKey(assoc);
        if (!workEffortAssocInfo.contains(assocPk)) {
            workEffortAssocInfo.add(assocPk);
        }
    }

    private Map<String, Object> getPrimaryKey(GenericValue assoc) {
        Map<String, Object> assocPk = UtilMisc.toMap(E.fromDate.name(), assoc.getTimestamp(E.fromDate.name()), E.workEffortAssocTypeId.name(), assoc.getString(E.workEffortAssocTypeId.name()), E.workEffortRevisionId.name(), assoc.getString(E.workEffortRevisionId.name()));

        if (UtilValidate.isEmpty(assoc.getString(E.workEffortIdFrom.name()))) {
            assocPk.put(E.workEffortIdFrom.name(), (Object)assoc.getString(E.workEffortSnapshotIdFrom.name()));
        } else {
            assocPk.put(E.workEffortIdFrom.name(), (Object)assoc.getString(E.workEffortIdFrom.name()));
        }
        if (UtilValidate.isEmpty(assoc.getString(E.workEffortIdTo.name()))) {
            assocPk.put(E.workEffortIdTo.name(), (Object)assoc.getString(E.workEffortSnapshotIdTo.name()));
        } else {
            assocPk.put(E.workEffortIdTo.name(), (Object)assoc.getString(E.workEffortIdTo.name()));
        }
        return assocPk;
    }

    private boolean hasWorkEffortAssocInfo(GenericValue assoc) {
        Map<String, Object> assocPk = getPrimaryKey(assoc);
        
        return workEffortAssocInfo.contains(assocPk);
    }

    private void searchAndRunMassiveWorkEffortAssoc(Delegator delegator, String workEffortRevisionId) throws GeneralException {
        for (String workEffortRootId : workEffortRootIdList) {
            List<EntityCondition> conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition(E.workEffortRootId.name(), workEffortRootId));
            conditionList.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), workEffortRevisionId));
            conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition(E.workEffortRevisionIdFrom2.name(), workEffortRevisionId), EntityCondition.makeCondition(E.workEffortRevisionIdFrom2.name(), null)));
            conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition(E.workEffortRevisionIdTo2.name(), workEffortRevisionId), EntityCondition.makeCondition(E.workEffortRevisionIdTo2.name(), null)));
            
            
            conditionList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDate));
            conditionList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, estimatedCompletionDate));
            
            List<GenericValue> assocList = delegator.findList(E.WorkEffortRevisionAndAssoc.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
            service.addLogInfo(" Found " + assocList.size() + " WorkEffortRevisionAndAssoc with condition " + EntityCondition.makeCondition(conditionList), workEffortRevisionId);
            
            for (GenericValue assoc : assocList) {
                setAndCreateWorkEffortAssoc(delegator, assoc);
            }
        }
    }

    /**
     * Create WorkEffortAssoc with isPosted = N
     * @param newWorkEffort
     * @param assoc
     * @param origWorkEffortId
     * @param newWorkEffortId
     * @throws GeneralException
     */
    private void setAndCreateWorkEffortAssoc(Delegator delegator, GenericValue assoc) throws GeneralException {
        if (!hasWorkEffortAssocInfo(assoc)) {
            Map<String, Object> serviceMap = FastMap.newInstance();

            serviceMap.putAll(assoc.getAllFields());
            serviceMap.remove(E.workEffortSnapshotIdFrom.name());
            serviceMap.remove(E.workEffortSnapshotIdTo.name());

            if (UtilValidate.isEmpty(assoc.getString(E.workEffortIdFrom.name()))) {
                serviceMap.put(E.workEffortIdFrom.name(), assoc.getString(E.workEffortSnapshotIdFrom.name()));
            }
            if (UtilValidate.isEmpty(assoc.getString(E.workEffortIdTo.name()))) {
                serviceMap.put(E.workEffortIdTo.name(), assoc.getString(E.workEffortSnapshotIdTo.name()));
            }
            if (E.Y.name().equals(storeRevisionWorkEffortAssoc)) {
            	serviceMap.put(E.workEffortRevisionId.name(), workEffortRevisionId);
            }

            createWorkEffortAssoc(delegator, serviceMap);
            addWorkEffortAssocInfo(assoc);
        }
    }

    /**
     * Create WorkEffortAssoc without Business service (crudServiceDefaultOrchestration)
     * @param newWorkEffort
     * @param assoc
     * @param origWorkEffortId
     * @param newWorkEffortId
     * @throws GeneralException
     */
    private void createWorkEffortAssoc(Delegator delegator, Map<String, Object> serviceMap) throws GeneralException {
        String successMsg = "Created relation of type " + serviceMap.get(E.workEffortAssocTypeId.name()) + " from " + serviceMap.get(E.workEffortIdFrom.name()) + " to " + serviceMap.get(E.workEffortIdTo.name());
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortAssoc.name() + " of type " + serviceMap.get(E.workEffortAssocTypeId.name()) + " from " + serviceMap.get(E.workEffortIdFrom.name()) + " to " + serviceMap.get(E.workEffortIdTo.name());
        service.runSyncCrud("crudServiceDefaultOrchestration", E.WorkEffortAssoc.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, false, workEffortRevisionId);
    }

    /**
     * Add workEffortRootId to List workEffortRootIdList
     * @param workEffortRootId
     */
    public void addWorkEffortRootIdList(String workEffortRootId) {
        if (!workEffortRootIdList.contains(workEffortRootId)) {
            workEffortRootIdList.add(workEffortRootId);
        }
    }

    /**
     * Return workEffortRootIdList
     * @return
     */
    public List<String> getWorkEffortRootIdList() {
        return this.workEffortRootIdList;
    }

    /**
     * Add list of workEffortRootId to workEffortRootIdList
     * @param workEffortRootIdList2
     */
    public void addWorkEffortRootIdList(List<String> workEffortRootIdList2) {
        this.workEffortRootIdList.addAll(workEffortRootIdList2);
    }
}
