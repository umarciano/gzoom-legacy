package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.find.WorkEffortPartyAssignmentFindServices;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Manage WorkEffort
 *
 */
public class WorkEffortPartyAssignmentHelper {
    private TakeOverService takeOverService;
    private Delegator delegator;
    private WorkEffortPartyAssignmentFindServices wepaFindServices;

    private static final Double ROLE_TYPE_WEIGHT_DEFAUL = 100D;
    
    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WorkEffortPartyAssignmentHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
        this.wepaFindServices = new WorkEffortPartyAssignmentFindServices(delegator);
    }

    /**
     * Create WorkEffortPartyAssignment, roleTypeWeight = 100
     * @param workEffortId
     * @param partyId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @throws GeneralException
     */
    private void insertWEPA(String workEffortId, String partyId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate) throws GeneralException {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        serviceMapParams.put(E.partyId.name(), partyId);
        serviceMapParams.put(E.roleTypeId.name(), E.WE_ASSIGNMENT.name());
        serviceMapParams.put(E.workEffortId.name(), workEffortId);
        serviceMapParams.put(E.fromDate.name(), estimatedStartDate);
        serviceMapParams.put(E.thruDate.name(), estimatedCompletionDate);
        serviceMapParams.put(E.roleTypeWeight.name(), ROLE_TYPE_WEIGHT_DEFAUL);

        // Creazione WorkEffortPartyAssignment
        String msg = "Trying to create WorkEffortPartyAssignment with id " + workEffortId + " partyId " + partyId + " roleTypeId WE_ASSIGNMENT fromDate " + estimatedStartDate + " thruDate " + estimatedCompletionDate;
        takeOverService.addLogInfo(msg);
        takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortPartyAssignment.name(), E.WorkEffortPartyAssignment.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffortPartyAssignment.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortPartyAssignment.name(), true);
    }

    /**
     * Check if exists the wepa, then create it
     * @param workEffortId
     * @param partyId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @throws GeneralException
     */
    private void checkWEPA(String workEffortAssignmentCode, String workEffortId, String partyId, Timestamp fromDate, Timestamp thruDate) throws GeneralException {
        String msg = "";

        EntityCondition condition = wepaFindServices.getValidWepaEntityCondition(workEffortId, partyId, E.WE_ASSIGNMENT.name(), fromDate);

        msg = "Search in WorkEffortPartyAssignment with " + condition;
        takeOverService.addLogInfo(msg);

        List<GenericValue> wepaList = delegator.findList(E.WorkEffortPartyAssignment.name(), condition, null, null, null, false);
        if (UtilValidate.isEmpty(wepaList)) {
            msg = "Create wepa for " + workEffortId + " " + partyId + " " + fromDate + " " + thruDate + ": " + condition;
            insertWEPA(workEffortId, partyId, fromDate, thruDate);

            // Check if exists WEPA with fromDate < workEffortDate e thruDate >= workEffortDate to invalidate with thruDate = workEffortDate - 1
            updateOtherWEPA(partyId, fromDate);

            // Check other wepa with fromDate > workEffortDate
            checkOtherWEPA(partyId, fromDate, workEffortAssignmentCode);
            return;
        }

        msg = "Found wepa for " + condition;
        takeOverService.addLogInfo(msg);
    }

    /**
     * Check one workEffort with sourceReferenceId or etch = workEffortAssignmentCode, and ESTIMATED_START_DATE <= refDate AND W.ESTIMATED_COMPLETION_DATE >= refDate
     * @param partyId
     * @param thruDate, create WEPA only if this field is empty 
     * @param newFromDate, fromDate for create new WEPA
     * @param roleTypeId
     * @throws GeneralException
     */
    public void doImportWorkEffort(String partyId, String workEffortAssignmentCode, Timestamp refDate, Timestamp thruDate) throws GeneralException {
        String msg = "";
        if (UtilValidate.isNotEmpty(workEffortAssignmentCode)) {
            WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(delegator);

            EntityCondition condition = workEffortFindServices.getWorkEffortForCodeEntityCondition(workEffortAssignmentCode, refDate);
            String foundMore = "Found more than one workEffort with condition :" + condition;
            String noFound = "No workEffort with condition :" + condition;

            GenericValue workEffort = takeOverService.findOne(E.WorkEffort.name(), condition, foundMore, noFound, takeOverService.getEntityName(), takeOverService.getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID));
            msg = "WorkEffort " + workEffort.getString(E.workEffortName.name()) + " found for partyId " + partyId + " with code " + workEffortAssignmentCode + " for refDate " + refDate;
            takeOverService.addLogInfo(msg);
            checkWEPA(workEffortAssignmentCode, workEffort.getString(E.workEffortId.name()), partyId, refDate, workEffort.getTimestamp(E.estimatedCompletionDate.name()));
        }
    }

    private void updateOtherWEPA(String partyId, Timestamp newFromDate) throws GeneralException {
        String msg = "";

        EntityCondition condition = wepaFindServices.getWepaListEntityCondition(partyId, E.WE_ASSIGNMENT.name(), newFromDate);

        List<GenericValue> wepaViewList = delegator.findList(E.WorkEffortPartyAssignSnapshotView.name(), condition, null, null, null, false);
        msg = "Found " + wepaViewList.size() + " in WorkEffortPartyAssignSnapshotView with " + condition + " to update with thruDate";
        takeOverService.addLogInfo(msg);

        for (GenericValue wepaView : wepaViewList) {
            updateWEPA(wepaView, takeOverService.getPreviousDay(newFromDate));
        }
    }

    private void updateWEPA(GenericValue wepa, Date previousDay) throws GeneralException {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        serviceMapParams.put(E.partyId.name(), wepa.getString(E.partyId.name()));
        serviceMapParams.put(E.roleTypeId.name(), E.WE_ASSIGNMENT.name());
        serviceMapParams.put(E.workEffortId.name(), wepa.getString(E.workEffortId.name()));
        serviceMapParams.put(E.fromDate.name(), wepa.getTimestamp(E.fromDate.name()));
        serviceMapParams.put(E.thruDate.name(), previousDay);

        // Set thruDate WorkEffortPartyAssignment
        String msg = "Trying to set thruDate " + previousDay + " in  WorkEffortPartyAssignment with id " + wepa.getString(E.workEffortId.name()) + " partyId" + wepa.getString(E.partyId.name()) + " roleTypeId WE_ASSIGNMENT fromDate " + wepa.getTimestamp(E.fromDate.name());
        takeOverService.addLogInfo(msg);
        takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortPartyAssignment.name(), E.WorkEffortPartyAssignment.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffortPartyAssignment.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.WorkEffortPartyAssignment.name(), true);
    }

    private void checkOtherWEPA(String partyId, Timestamp newFromDate, String workEffortCode) throws GeneralException {
        String msg = "";

        EntityCondition condition = wepaFindServices.getWepaOtherListEntityCondition(partyId, E.WE_ASSIGNMENT.name(), newFromDate);

        msg = "Searching other WorkEffortPartyAssignSnapshotView with " + condition;
        takeOverService.addLogInfo(msg);


        List<GenericValue> otherWepaViewList = delegator.findList(E.WorkEffortPartyAssignSnapshotView.name(), condition, null, null, null, false);
        for (GenericValue wepaView : otherWepaViewList) {
            msg = "Cannot update thruDate on workEffortPartyAssignment with code " + wepaView.getString(E.workEffortId.name());
            takeOverService.addLogInfo(msg);
        }
        
        if (otherWepaViewList.size() > 0) {
            throw new ImportException(takeOverService.getEntityName(), takeOverService.getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), "Found one or more workEffortPartyAssignment with " + condition);
        }
    }
}
