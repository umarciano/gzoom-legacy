package com.mapsengineering.workeffortext.services.rootcopy;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEfforPartyAssignment, for copy and snapshot
 *
 */
public class WorkEffortPartyAssignmentCopy extends AbstractWorkEffortDataCopy {
    
    public static final String MODULE = WorkEffortPartyAssignmentCopy.class.getName();

    /**
     * Constructor
     * @param service
     * @param snapshot
     */
    public WorkEffortPartyAssignmentCopy(WorkEffortRootCopyService service, boolean snapshot) {
        super(service, snapshot);
    }

    /**
     * Search WorkEffortPartyAssignAndRoleType for selected workEffortId and execute crudService crudServiceDefaultOrchestration_WorkEffortPartyAssignment_copy.<br/>
     * The WorkEffortPartyAssign selected has PartyRelationship with partyRelationshipTypeId = ORG_EMPLOYMENT.<br/>
     */
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        Timestamp estimatedStartDate = (Timestamp) data.get(E.estimatedStartDate.name());
        Timestamp estimatedCompletionDate = (Timestamp) data.get(E.estimatedCompletionDate.name());        
        
        List<GenericValue> origWpa = getDelegator().findList("WorkEffortPartyAssignAndRoleType", EntityCondition.makeCondition(E.workEffortId.name(), origWorkEffortId), null, null, null, getUseCache());
        for (GenericValue wpa : origWpa) {
            boolean copyAssignment = isCopyAssignment(wpa, estimatedStartDate);
            if (copyAssignment) {
                GenericValue party = getDelegator().findOne(E.Party.name(), UtilMisc.toMap(E.partyId.name(), wpa.getString(E.partyId.name())), getUseCache());
                Map<String, Object> serviceMap = wpa.getAllFields();
                serviceMap.put(E.workEffortId.name(), newWorkEffortId);
                serviceMap.put(E.fromDate.name(), getFromDate(estimatedStartDate,  wpa.getTimestamp(E.fromDate.name())));
                serviceMap.put(E.thruDate.name(), getThruDate(estimatedCompletionDate, wpa.getTimestamp(E.thruDate.name())));
                serviceMap.put(E.isPosted.name(), getIsPosted(wpa.getString(E.isPosted.name())));
                
                String successMsg = "Assigned Party \"" + party.getString(E.partyName.name()) + "\" to workEffort " + newWorkEffortId;
                String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortPartyAssignment.name() + "\"" + party.getString(E.partyName.name()) + "\" to workEffort " + newWorkEffortId;
                if(isUseEnableSnapshot()) {
                	runSyncCrud("crudServiceDefaultOrchestration", E.WorkEffortPartyAssignment.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
                } else {
                	runSyncCrud("crudServiceDefaultOrchestration_WorkEffortPartyAssignment_copy", E.WorkEffortPartyAssignment.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
                }
            }
        }
        return null;
    }
    
    /**
     * se sono nella storicizzazione non devo effettuare alcun controllo nella copia delle risorse
     * @param wpa
     * @param estimatedStartDate
     * @return
     * @throws GeneralException
     */
    private boolean isCopyAssignment(GenericValue wpa, Timestamp estimatedStartDate) throws GeneralException {
        if (isUseEnableSnapshot()) {
            return true;
        }
        
        if (E.EMPLOYEE.name().equals(wpa.getString(E.parentTypeId.name()))) {       
            List<EntityCondition> prCond = FastList.newInstance();
            prCond.add(EntityCondition.makeCondition(E.partyIdTo.name(), wpa.getString(E.partyId.name())));
            prCond.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_EMPLOYMENT.name()));
            prCond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.EQUALS, "")), EntityOperator.OR, EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDate)));
            List<GenericValue> prList = getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(prCond), null, null, null, getUseCache());

            return UtilValidate.isNotEmpty(prList);
        }
        
        return true;
    }
}
