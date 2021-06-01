package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.workeffort.OperationTypeEnum;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Helper for WorkEffortPartyAssignment, with Helper for Party, Person and PartyGroup
 *
 */
public class WePartyInterfaceHelper {

    private TakeOverService takeOverService;
    private Delegator delegator;

    /**
     * Constructor
     * @param takeOverService
     */
    public WePartyInterfaceHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }
    
    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WePartyInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
    }

    /**
     * Controlla esistenza unico roleType che corrisponde ai paramtetri passati, ritorna il generic value oppure eccezione 
     * @param roleTypeId
     * @param roleTypeDesc
     * @return GenericValue statusItem
     * @throws GeneralException
     */
    public GenericValue checkValidityRoleType(String roleTypeId, String roleTypeDesc) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        List<EntityCondition> listaCondition = FastList.newInstance();
        List<EntityCondition> listaConditionType = FastList.newInstance();
        if (UtilValidate.isNotEmpty(roleTypeId)) {
            EntityCondition conditionId = EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId);
            listaConditionType.add(conditionId);
            // foundMore += " roleTypeId = ".concat(roleTypeId);
            // noFound += " roleTypeId = ".concat(roleTypeId);
        }

        if (UtilValidate.isNotEmpty(roleTypeDesc)) {
            EntityCondition conditionDesc = EntityCondition.makeCondition(E.description.name(), roleTypeDesc);
            listaConditionType.add(conditionDesc);
            // foundMore += " description = ".concat(roleTypeDesc);
            // noFound += " description = ".concat(roleTypeDesc);
        }

        listaCondition.add(EntityCondition.makeCondition(listaConditionType));
        listaCondition.add(EntityCondition.makeCondition(E.parentTypeId.name(), E.ORGANIZATION_UNIT.name()));

        EntityCondition condition = EntityCondition.makeCondition(listaCondition);
        
        String foundMore = "Found more than one orgType with condition :" + condition;
        String noFound = "No orgType with condition :" + condition;
        
        return takeOverService.findOne(E.RoleType.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }
    
    /**
     * 
     * @param partyId
     * @param orgDesc
     * @return
     * @throws GeneralException
     */
    public String checkValidityPartyGroup(String partyId, String orgDesc) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        List<EntityCondition> listaCondition = FastList.newInstance();
        
        if (UtilValidate.isNotEmpty(partyId)) {
            listaCondition.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        }
        
        listaCondition.add(EntityCondition.makeCondition(E.groupName.name(), orgDesc));
        
        EntityCondition condition = EntityCondition.makeCondition(listaCondition);
        
        String foundMore = "Found more than one orgUnit with condition :" + condition;
        String noFound = "No orgUnit with condition :" + condition;
        
        GenericValue partyRole = takeOverService.findOne(E.PartyGroup.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
        return partyRole.getString(E.partyId.name());
    }

    /**
     * Import i workEffort figli della scheda, e poi scorre quelli presenti sul db ed elimina quelli che non sono stati gestiti nell'importazione attuale
     * @param sourceReferenceRootId
     * @param workEffortRootId
     */
    public void importWorkEffortParty(String sourceReferenceRootId, String workEffortRootId) throws GeneralException {
        Map<String, List<GenericValue>> resultList = takeOverService.doImportMulti(ImportManagerConstants.WE_PARTY_INTERFACE, UtilMisc.toMap(E.sourceReferenceRootId.name(), sourceReferenceRootId));
        GenericValue gv = getTakeOverService().getExternalValue();
        if (! OperationTypeEnum.A.name().equals(gv.getString(E.operationType.name()))) {
        	List<GenericValue> localValues = resultList.get("localValues");
        	if (UtilValidate.isNotEmpty(localValues)) {
        		checkWorkEffortPartyList(localValues, workEffortRootId);
        	}
        }
    }

    private void checkWorkEffortPartyList(List<GenericValue> resultList, String workEffortRootId) throws GeneralException {
        List<String> roleTypeIdList = getRoleTypeIdManaged(resultList);
        String msg = "The following roleTypeId are managed " + roleTypeIdList;
        takeOverService.addLogInfo(msg);
        List<String> workEffortIdList = getWorkEffortId(workEffortRootId);
        msg = "The following workEffortId " + workEffortIdList + " have the parent id managed " + workEffortRootId;
        takeOverService.addLogInfo(msg);
        for (String roleTypeId : roleTypeIdList) {
            for (String workEffortId : workEffortIdList) {
                List<GenericValue> workEffortPartyAssignmentList = getWorkEffortPartyAssignmentByCondition(workEffortId, roleTypeId);
                checkWorkEffortPartyAssignment(workEffortPartyAssignmentList, resultList);
            }
        }
    }

    private void checkWorkEffortPartyAssignment(List<GenericValue> workEffortPartyAssignmentList, List<GenericValue> workEffortPartyAssignmentStoredList) throws GeneralException {
        for (GenericValue workEffortPartyAssignment : workEffortPartyAssignmentList) {
            boolean found = false;
            for (GenericValue workEffortPartyAssignmentStored : workEffortPartyAssignmentStoredList) {
                if (workEffortPartyAssignmentStored.getString(E.workEffortId.name()).equals(workEffortPartyAssignment.getString(E.workEffortId.name())) 
                && workEffortPartyAssignmentStored.getString(E.roleTypeId.name()).equals(workEffortPartyAssignment.getString(E.roleTypeId.name()))
                && workEffortPartyAssignmentStored.getString(E.partyId.name()).equals(workEffortPartyAssignment.getString(E.partyId.name()))
                && workEffortPartyAssignmentStored.getString(E.fromDate.name()).equals(workEffortPartyAssignment.getString(E.fromDate.name()))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                disabledWorkEffortPartyAssignment(workEffortPartyAssignment);
            }
        }
    }

    private void disabledWorkEffortPartyAssignment(GenericValue workEffortPartyAssignment) throws GeneralException {
        String msg = "The following workeffortPartyAssignment will be deleted " + workEffortPartyAssignment.getPrimaryKey();
        getTakeOverService().addLogInfo(msg);
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.workEffortId.name(), (Object)workEffortPartyAssignment.get(E.workEffortId.name()), E.roleTypeId.name(), (Object)workEffortPartyAssignment.get(E.roleTypeId.name()), 
                E.partyId.name(), (Object)workEffortPartyAssignment.get(E.partyId.name()), E.fromDate.name(), (Object)workEffortPartyAssignment.get(E.fromDate.name()));
        Map<String, Object> result = getTakeOverService().runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortPartyAssignment.name(), E.WorkEffortPartyAssignment.name(), CrudEvents.OP_DELETE, serviceMapParams, E.WorkEffortPartyAssignment.name() + FindUtilService.MSG_SUCCESSFULLY_DELETE, FindUtilService.MSG_ERROR_DELETE + E.WorkEffortPartyAssignment.name(), true);
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull delete workeffortPartyAssignment";
            getTakeOverService().addLogInfo(msg);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            getTakeOverService().addLogInfo(msg);
        }
    }

    private TakeOverService getTakeOverService() {
        return takeOverService;
    }

    private List<GenericValue> getWorkEffortPartyAssignmentByCondition(String workEffortId, String roleTypeId) throws GenericEntityException {
        List<EntityCondition> listaCondition = FastList.newInstance();
        
        listaCondition.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
        listaCondition.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        
        EntityCondition condition = EntityCondition.makeCondition(listaCondition);
        String msg = "Try WorkEffortPartyAssignment with condition " + condition;
        getTakeOverService().addLogInfo(msg);
        
        List<GenericValue> workEffortPartyAssignmentList = delegator.findList(E.WorkEffortPartyAssignment.name(), condition, null, null, null, false);
        
        msg = "Found workEffortPartyAssignmentList = " + workEffortPartyAssignmentList;
        getTakeOverService().addLogInfo(msg);
        
        return workEffortPartyAssignmentList;
    }

    private List<String> getWorkEffortId(String workEffortParentId) throws GenericEntityException {
        List<EntityCondition> listaCondition = FastList.newInstance();
        listaCondition.add(EntityCondition.makeCondition(E.workEffortParentId.name(), workEffortParentId));
        List<GenericValue> workEffortList = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(listaCondition), null, null, null, false);
        return EntityUtil.getFieldListFromEntityList(workEffortList, E.workEffortId.name(), true);
    }

    private List<String> getRoleTypeIdManaged(List<GenericValue> resultList) {
        return EntityUtil.getFieldListFromEntityList(resultList, E.roleTypeId.name(), true);
    }

    /**
     * 
     * @param partyName
     * @param partyId
     * @return
     * @throws GeneralException
     */
    public String checkValidityParty(String partyName, String partyId) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        List<EntityCondition> listaCondition = FastList.newInstance();
        
        if (UtilValidate.isNotEmpty(partyId)) {
            listaCondition.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        }
        
        listaCondition.add(EntityCondition.makeCondition(E.partyName.name(), partyName));
        
        EntityCondition condition = EntityCondition.makeCondition(listaCondition);
               
        Map<String, Object> parameters = UtilMisc.toMap(E.partyName.name(), (Object) partyName, E.sourceId.name(), gv.get(E.sourceReferenceRootId.name()));
        JobLogLog partyNotUnique = new JobLogLog().initLogCode("StandardImportUiLabels", "PARTY_NOT_UNIQUE", parameters, getTakeOverService().getManager().getLocale());
        JobLogLog noPartyFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_PARTY_FOUND", parameters, getTakeOverService().getManager().getLocale());
        
        GenericValue party = takeOverService.findOne(E.Party.name(), condition, partyNotUnique, noPartyFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
        return party.getString(E.partyId.name());
    }

    /**
     * Ritorna un workEffortPartyAssignment se esiste o null, indipendentemente dalle date
     * @param workEffortId
     * @param roleTypeId
     * @param partyId
     * @return GenericValue WorkEffortPartyAssignment
     * @throws GeneralException
     */
    public GenericValue getWorkEffortPartyAssignment(String workEffortId, Timestamp fromDate, String roleTypeId, String partyId) throws GeneralException {
    	return delegator.findOne(E.WorkEffortPartyAssignment.name(), 
    		   UtilMisc.toMap(E.workEffortId.name(), workEffortId, E.fromDate.name(), fromDate, E.roleTypeId.name(), roleTypeId, E.partyId.name(), partyId), 
    		   false);
    }
}
