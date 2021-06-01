package com.mapsengineering.workeffortext.services.rootcopy;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.util.WorkEffortAssocCopyUtil;

/**
 * Massive snapshot 
 *
 */
public class MassiveWorkEffortRootCopyService extends GenericService {

    public static final String MODULE = MassiveWorkEffortRootCopyService.class.getName();
    public static final String SERVICE_NAME = "MassiveWorkEffortRootCopyService";
    public static final String SERVICE_TYPE_ID = "MAS_WRK_ROOT_COPY";

    private String workEffortRevisionId;
    private String description;
    private String isStoricized;
    private String jobLogId;

    private WorkEffortAssocCopyUtil workEffortAssocCopyUtil;
    
    /** For specific revision, copy all workEffort */
    public static Map<String, Object> massiveWorkEffortRootCopyService(DispatchContext dctx, Map<String, Object> context) {
        context.put(ServiceLogger.JOB_LOGGER, new JobLogger(MODULE));
        MassiveWorkEffortRootCopyService obj = new MassiveWorkEffortRootCopyService(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public MassiveWorkEffortRootCopyService(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
        isStoricized = E.N.name();
        setUseCache(false);
    }

    /** For specific revision, copy all workEffort 
     * */
    public void mainLoop() {
        String msg;
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        workEffortRevisionId = (String)context.get(E.workEffortRevisionId.name());
        
        
        try {
            jobLogId = delegator.getNextSeqId("JobLog");
            
            addLogInfo("Start Elaboration of massive snapshot for revision " + workEffortRevisionId, workEffortRevisionId);
            
            EntityCondition condRevision = EntityCondition.makeCondition(E.workEffortRevisionId.name(), workEffortRevisionId);
            GenericValue workEffortRevision = findOne(E.WorkEffortRevision.name(), condRevision, "Found more workEffortRevision", "No revision found with id = " + workEffortRevisionId);
            
            String isClosed = workEffortRevision.getString(E.isClosed.name());
            
            checkIsClosed(isClosed);
            
            executeDeleteOldRevision();
            
            description = workEffortRevision.getString(E.description.name());
            String workEffortTypeIdFil = workEffortRevision.getString(E.workEffortTypeIdFil.name());
            String organizationPartyId = workEffortRevision.getString(E.organizationId.name());
            Timestamp refDate = workEffortRevision.getTimestamp(E.refDate.name());
            workEffortAssocCopyUtil = new WorkEffortAssocCopyUtil(this, workEffortRevisionId, E.Y.name(), refDate, refDate);
            searchAndRunWorkEffortType(workEffortTypeIdFil, refDate, organizationPartyId);
            
            // ASSOCIAZIONI
            manageWorkeffortAssoc();
        } catch (Exception e) {
            msg = "Error: ";
            addLogError(e, msg, workEffortRevisionId);
        } finally {
            setIsStoricized();
            
            super.writeLogs(startTimestamp, jobLogId);
            
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(E.isStoricized.name(), isStoricized);
        }
    }
    
    private void executeDeleteOldRevision() throws Exception {
        new TransactionRunner(MODULE, true, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                deleteOldRevision();
            }
        }).execute().rethrow();
    }
    
    /**
     * Only for Snapshot with automatic Revision 
     * @throws Exception 
     */
    private void manageWorkeffortAssoc() throws Exception {
       workEffortAssocCopyUtil.copyMassiveWorkEffortAssoc(workEffortRevisionId);
    }

    /** Check if the revision is closed */
    private void checkIsClosed(String isClosed) throws GeneralException{
        if("Y".equals(isClosed)) {
            throw new GeneralException("Error during elaborating revision " + workEffortRevisionId + " : the revision is closed.");
        }
    }

    /** Return list of workEffortType related with workEffortTypeIdFil */
    private List<String> getWorkEffortTypeList(String workEffortTypeIdFil) throws GeneralException {
        List<String> workEffortTypeIdList = FastList.newInstance();
        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(E.workEffortTypeIdRoot.name(), workEffortTypeIdFil));
        List<GenericValue> workEffortTypeList = findList(E.WorkEffortTypeType.name(), EntityCondition.makeCondition(cond), true, workEffortRevisionId);
        
        if(UtilValidate.isNotEmpty(workEffortTypeList)) {
            workEffortTypeIdList = EntityUtil.getFieldListFromEntityList(workEffortTypeList, E.workEffortTypeIdFrom.name(), true);
        }
        return workEffortTypeIdList;
    }
    
    /** Search the workEffortType related with workEffortTypeIdFil */
    private void searchAndRunWorkEffortType(String workEffortTypeIdFil, Timestamp refDate, String organizationPartyId) throws GeneralException {
        List<String> workEffortTypeIdList = getWorkEffortTypeList(workEffortTypeIdFil);
        addLogInfo("Found " + workEffortTypeIdList.size() + " workEffortType to elaborate");
        
        for(String workEffortTypeId : workEffortTypeIdList){
            elaborateWorkEffortType(workEffortTypeId, refDate, organizationPartyId);
        }
    }
    
    /** Execute service workEffortRootCopy for the specific workEffortTypeId 
     * @throws GeneralException */
    @SuppressWarnings("unchecked")
    private void elaborateWorkEffortType(String workEffortTypeId, Timestamp refDate, String organizationPartyId) throws GeneralException {
        addLogInfo("Elaborating workEffortTypeId: " + workEffortTypeId);
        
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.userLogin.name(), userLogin);
        serviceMap.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, organizationPartyId);
        
        
        serviceMap.put("locale", context.get("locale"));
        serviceMap.put("timeZone", context.get("timeZone"));
        
        serviceMap.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
        serviceMap.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
        serviceMap.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
        serviceMap.put(WorkEffortRootCopyService.CHECK_EXISTING, E.N.name());
    
        serviceMap.put(E.snapShotDescription.name(), description);
        
        serviceMap.put(E.workEffortTypeIdFrom.name(), workEffortTypeId);

        serviceMap.put(E.workEffortRevisionId.name(), workEffortRevisionId);
        serviceMap.put(E.storeRevisionWorkEffortAssoc.name(), E.Y.name());

        serviceMap.put(E.estimatedStartDateFrom.name(), refDate);
        serviceMap.put(E.estimatedCompletionDateFrom.name(), refDate);

        serviceMap.put(ServiceLogger.JOB_LOGGER, jobLogger);
        serviceMap.put(ServiceLogger.JOB_LOG_ID, jobLogId);
        
        serviceMap.put(E.workEffortTypeIdTo.name(), workEffortTypeId);

        serviceMap.put(E.estimatedStartDateTo.name(), refDate);
        serviceMap.put(E.estimatedCompletionDateTo.name(), refDate);
    
        Map<String, Object> result = dispatcher.runSync(E.workEffortRootCopyService.name(), serviceMap);
        List<String> workEffortRootIdList = (List<String>) result.get(E.workEffortRootIdList.name());
        workEffortAssocCopyUtil.addWorkEffortRootIdList(workEffortRootIdList);
        
        checkError(result, workEffortTypeId);
    }

    /** Return if there is almost one workeffort with the revisionId.
     *  <BR/>In case of Exception isStoricized = Y */
    private void setIsStoricized() {
        try {
            List<GenericValue> workEffortViewList = getWorkEffortViewList();
            addLogInfo("Set isStoricized : found " + workEffortViewList.size() + " record for the revision " + workEffortRevisionId, workEffortRevisionId);
            if(workEffortViewList.size() > 0) {
                isStoricized = E.Y.name();
            }
        } catch (Exception e) {
            isStoricized = E.Y.name();
            addLogError(e, "Error during set isStoricized searching workeffort with revision id " + workEffortRevisionId, workEffortRevisionId);
        }
    }

    /** Find and Delete workeffort with same workEffortRevisionId */
    private void deleteOldRevision() throws GeneralException {
        // Find related WorkEfforts Root
        // the workEffort child is found by service
        List<GenericValue> toDeleteList = getWorkEffortViewList();
        addLogInfo("Found " + toDeleteList.size() + " roots to delete for revision ", workEffortRevisionId);
        for (GenericValue wrk : toDeleteList) {
            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap = dispatcher.getDispatchContext().makeValidContext(E.workEffortRootPhysicalDelete.name(), "IN", context);
            serviceMap.put(E.workEffortId.name(), wrk.getString(E.workEffortId.name()));
            serviceMap.put(E.userLogin.name(), userLogin);
            serviceMap.put(E.jobLogger.name(), jobLogger);
            Map<String, Object> res = dispatcher.runSync(E.workEffortRootPhysicalDelete.name(), serviceMap);
            checkError(res);
        }
    }
    
    /** Return true if there is an exception or almost one blocking errors 
     * @throws GeneralException */
    private void checkError(Map<String, Object> res) throws GeneralException {
        if(!ServiceUtil.isSuccess(res)) {
            throw new GeneralException("Error during deleting workEffort with revision id " + workEffortRevisionId + " : " + ServiceUtil.getErrorMessage(res));
        }
        long blockErr = (Long) res.get(ServiceLogger.BLOCKING_ERRORS);
        if(blockErr > 0) {
            throw new GeneralException("Error during deleting workEffort with revision id " + workEffortRevisionId + " : " + ServiceUtil.getErrorMessage(res));
        }
    }

    private void checkError(Map<String, Object> res, String workEffortTypeId) throws GeneralException {
        if(!ServiceUtil.isSuccess(res)) {
            throw new GeneralException("Error during elaboration revision for type " + workEffortTypeId + ": "+ ServiceUtil.getErrorMessage(res));
        }
        long blockErr = (Long) res.get(ServiceLogger.BLOCKING_ERRORS);
        if(blockErr > 0) {
            setBlockingErrors(blockErr);
            throw new GeneralException("Error during elaboration revision for type " + workEffortTypeId);
        } else {
            addLogInfo("For workEffortTypeId = " + workEffortTypeId + " the number of record elaborated is " + res.get(ServiceLogger.RECORD_ELABORATED));
            setRecordElaborated(getRecordElaborated() + (Long) res.get(ServiceLogger.RECORD_ELABORATED));
        }
    }

    /** Find WorkEffortView with same workEffortRevisionId,
     * <BR/> Search only the WorkEffortRoot */
    private List<GenericValue> getWorkEffortViewList() throws GeneralException {
        //Find related WorkEfforts
        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), workEffortRevisionId));
        cond.add(EntityCondition.makeCondition(E.weIsRoot.name(), E.Y.name()));
        
        List<GenericValue> workEffortViewList = delegator.findList(E.WorkEffortView.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
        return workEffortViewList;
    }
}
