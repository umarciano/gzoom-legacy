package com.mapsengineering.workeffortext.services.massiveinsert.wepa;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import com.mapsengineering.workeffortext.services.E;

public class MassiveInsertWEPA extends GenericService{

    public static final String MODULE = MassiveInsertWEPA.class.getName();
    public static final String SERVICE_NAME = "massiveInsertWEPA";
    public static final String SERVICE_TYPE = "MASS_INS_WEPASS";
    
    private Timestamp fromDate;
    private Timestamp thruDate;
    private String workEffortId;
    private String roleTypeId;
    private Double roleTypeWeight;

    public static Map<String, Object> massiveInsertWEPA(DispatchContext dctx, Map<String, Object> context) {
        MassiveInsertWEPA obj = new MassiveInsertWEPA(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    public MassiveInsertWEPA(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(E.userLogin.name());
    }

    public void mainLoop() {
        String msg;
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        try {
            
            roleTypeId = (String)context.get(E.roleTypeId.name());
            if (UtilValidate.isEmpty(roleTypeId)) {
                roleTypeId = E.WE_ASSIGNMENT.name();
            }
            
            roleTypeWeight = new Double(0);
            
            workEffortId = (String)context.get(E.workEffortId.name());
            setElabRef1(workEffortId);
            addLogInfo("Start Massive Insert WorkEffortPartyAssignment for WorkEffort with id = " + workEffortId);
            checkValidateWorkEffort(workEffortId);
            
            
            List<Map<String, Object>> partyRoleViewlistIt = (List<Map<String, Object>>)context.get("listIt");
            elaboratePartyRoleViewList(partyRoleViewlistIt);
            
            setResult(ServiceUtil.returnSuccess());
        } catch (Exception e) {
            msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            writeLogs(startTimestamp, jobLogId);
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
        }
    }
    
    private void manageResult(Map<String, Object> result, String partyId) throws GeneralException {
        String msg = "";
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull created/updated WorkeffortPartyAssignment for workEffortId = " + workEffortId + " and partyId " + partyId;
            addLogInfo(msg, partyId);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg, partyId);
        }
    }


    private void elaboratePartyRoleViewList(List<Map<String, Object>> partyRoleViewlistIt) throws GeneralException {
        checkPartyRoleViewList(partyRoleViewlistIt);
        
        for (Map<String, Object> partyRoleView : partyRoleViewlistIt) {
            String partyId = (String) partyRoleView.get(E.partyId.name());
            
            if(!checkExistingValue(partyId)){
                insertValue(partyId);
            }
        }
    }

    private boolean checkExistingValue(String partyId) throws GeneralException {
        List<GenericValue> workEffortList = getWorkEffortPartyAssignmentList(partyId);
        if(UtilValidate.isNotEmpty(workEffortList)) {
            addLogWarning("WorkEffortPartyAssignment already exists for partyId = " + partyId, partyId);
            return true;
        }
        return false;
    }

    private void insertValue(final String partyId) throws GeneralException {
        try {
            new TransactionRunner(MODULE, true, new TransactionItem() {
                @Override
                public void run() throws Exception {
                    runSingleSyncCrud(partyId);
                }
            }).execute().rethrow();
        } catch (Exception e) {
            addLogError(e, "Error during service crudServiceDefaultOrchestration_WorkEffortPartyAssignment: ", partyId);
        }
    }

    private void runSingleSyncCrud(String partyId) throws Exception {
        Map<String, Object> serviceMap = createServiceMap(partyId);       
        
        serviceMap.put(E.roleTypeWeight.name(), roleTypeWeight);
        serviceMap.put(E.roleTypeId.name(), roleTypeId);
        serviceMap.put(E.workEffortId.name(), workEffortId);
            
        addLogInfo("Create WorkEffortPartyAssignment for workEffortId = " + workEffortId + ", partyId = " + partyId + ", roleTypeId" + roleTypeId + ", roleTypeWeight = " + roleTypeWeight, partyId);
            
        Map<String, Object> serviceResult = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortPartyAssignment.name(), E.WorkEffortPartyAssignment.name(), CrudEvents.OP_CREATE, serviceMap, E.WorkEffortPartyAssignment.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortPartyAssignment.name(), true);
        manageResult(serviceResult, partyId);
            
        setRecordElaborated(getRecordElaborated() + 1);
    }   

    private Map<String, Object> createServiceMap(String partyId) {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.userLogin.name(), userLogin);
        serviceMap.put(E.fromDate.name(), fromDate);
        serviceMap.put(E.thruDate.name(), thruDate);
        serviceMap.put(E.partyId.name(), partyId);
        
        return serviceMap;
    }

    private void checkPartyRoleViewList(List<Map<String, Object>> partyRoleViewlistIt) throws GeneralException {
        // addLogInfo("Lista di partyRoleViewlistIt: " + partyRoleViewlistIt);
        if(UtilValidate.isEmpty(partyRoleViewlistIt)) {
            throw new GeneralException("No PartyRole list found to insert");
        }
    }

    private void checkValidateWorkEffort(String workEffortId) throws GeneralException {
        addLogInfo("Check if exist WorkEffort for id " + workEffortId);
        
        EntityCondition condition = EntityCondition.makeCondition(E.workEffortId.name(), workEffortId);
        String foundMore = "Found more than one workEffort with condition :" + condition;
        String noFound = "No workEffort with condition :" + condition;
        GenericValue workEffort = findOne(E.WorkEffort.name(), condition, foundMore, noFound);
        fromDate = workEffort.getTimestamp(E.estimatedStartDate.name());
        thruDate = workEffort.getTimestamp(E.estimatedCompletionDate.name());
    }
    
    /**
     * Ritorna lista workEffortPartyAssignment indipendentemente dalle date
     * @param workEffortId
     * @param roleTypeId
     * @param partyId
     * @return List WorkEffortPartyAssignment
     * @throws GeneralException
     */
    public List<GenericValue> getWorkEffortPartyAssignmentList(String partyId) throws GeneralException {
        List<GenericValue> parents = null;
        if(delegator != null) {
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortId.name(), workEffortId, E.roleTypeId.name(), roleTypeId, E.partyId.name(), partyId));
            parents = delegator.findList(E.WorkEffortPartyAssignment.name(), cond, null, null, null, false);
        }
        return parents;
    }
    
 }
