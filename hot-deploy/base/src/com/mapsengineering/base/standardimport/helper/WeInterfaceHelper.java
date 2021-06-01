package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.workeffort.OperationTypeEnum;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for WorkEffort
 *
 */
public class WeInterfaceHelper extends WeRootInterfaceHelper {

    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WeInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        super(takeOverService, delegator);
    }

    /**
     * Ricerca e restituisce primo workEffort or null a partire da
     * @param workEffortRootId
     * @param workEffortTypeLevelId
     * @param sourceReferenceId
     * @param workEffortName
     * @return
     * @throws GeneralException
     */
    public GenericValue getWorkEffort(String workEffortRootId, String workEffortTypeLevelId, String sourceReferenceId, String organizationId, String workEffortName) throws GeneralException {
        return EntityUtil.getFirst(getWorkEffortList(workEffortRootId, workEffortTypeLevelId, sourceReferenceId, organizationId, workEffortName));
    }

    /**
     * Ricerca e restituisce lista workEffort a partire da
     * @param workEffortParentId
     * @param workEffortTypeId
     * @param sourceReferenceId
     * @param workEffortName
     * @return
     * @throws GeneralException
     */
    public List<GenericValue> getWorkEffortList(String workEffortParentId, String workEffortTypeId, String sourceReferenceId, String organizationId, String workEffortName) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        if (ValidationUtil.isEmptyOrNA(sourceReferenceId) && ValidationUtil.isEmptyOrNA(workEffortName)) {
            String msg = "The field sourceReferenceId or workEffortName must be not empty";
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        EntityCondition condition = makeConditionWorkEffort(workEffortTypeId, workEffortParentId, sourceReferenceId, organizationId, workEffortName);
        List<GenericValue> parents = getDelegator().findList(E.WorkEffort.name(), condition, null, null, null, false);

        return parents;
    }
    
    private EntityCondition makeConditionWorkEffort(String workEffortTypeId, String workEffortParentId, String sourceReferenceId, String organizationId, String workEffortName){
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId)));
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortParentId.name(), workEffortParentId)));
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.organizationId.name(), organizationId)));
        
        if (!ValidationUtil.isEmptyOrNA(sourceReferenceId)) {
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.sourceReferenceId.name(), sourceReferenceId));
            conditionList.add(cond);
        } else if (!ValidationUtil.isEmptyOrNA(workEffortName)) {
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortName.name(), workEffortName));
            conditionList.add(cond);
        }

        return EntityCondition.makeCondition(conditionList);
    }
    
    /**
     * Ritorna e restituisce unico WorkEffort, altrimenti lancia eccezione
     * @param workEffortParentId
     * @param sourceReferenceId
     * @param workEffortName
     * @param workEffortTypeId
     * @return
     * @throws GeneralException
     */
    public GenericValue getValidityWorkEffort(String workEffortParentId, String sourceReferenceId, String organizationId, String workEffortName, String workEffortTypeId) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        if (ValidationUtil.isEmptyOrNA(sourceReferenceId) && ValidationUtil.isEmptyOrNA(workEffortName)) {
            String msg = "The field sourceReferenceId or workEffortName must be not empty";
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        String msg = " Try workEffort with sourceReferenceId " + sourceReferenceId + " and workEffortName " + workEffortName;
        getTakeOverService().addLogInfo(msg);

        EntityCondition condition = makeConditionWorkEffort(workEffortTypeId, workEffortParentId, sourceReferenceId, organizationId, workEffortName);
        
        String foundMore = "Found more than one workEffort with condition :" + condition;
        String noFound = "No workEffort with condition :" + condition;

        return getTakeOverService().findOne(E.WorkEffort.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    

    /**
     * Import i workEffort figli della scheda, poi se operationType diverso da A scorre quelli presenti sul db ed elimina quelli che non sono stati gestiti nell'importazione attuale
     * @param sourceReferenceRootId
     * @param workEffortRootId
     */
    public void importWorkEffort(String sourceReferenceRootId, String workEffortRootId) throws GeneralException {
        Map<String, List<GenericValue>> resultList = getTakeOverService().doImportMulti(ImportManagerConstants.WE_INTERFACE, UtilMisc.toMap(E.sourceReferenceRootId.name(), sourceReferenceRootId));
        GenericValue gv = getTakeOverService().getExternalValue();
        if (! OperationTypeEnum.A.name().equals(gv.getString(E.operationType.name()))) {
        	List<GenericValue> localValues = resultList.get("localValues");
        	if (UtilValidate.isNotEmpty(localValues)) {
        		checkWorkEffortList(localValues, workEffortRootId);
        	}
        }
    }

    private void checkWorkEffortList(List<GenericValue> resultList, String workEffortRootId) throws GeneralException {
        EntityCondition conditionParent = EntityCondition.makeCondition(E.workEffortParentId.name(), workEffortRootId);
        EntityCondition conditionId = EntityCondition.makeCondition(E.workEffortId.name(), EntityJoinOperator.NOT_EQUAL, workEffortRootId);
        EntityCondition condition = EntityCondition.makeCondition(conditionParent, conditionId);
        List<GenericValue> workEffortList = getDelegator().findList(E.WorkEffort.name(), condition, null, null, null, false);

        List<String> workEffortIdList = EntityUtil.getFieldListFromEntityList(resultList, E.workEffortId.name(), true);
        String msg = "The following workEffort are managed : " + workEffortIdList;
        getTakeOverService().addLogInfo(msg);

        for (GenericValue workEffort : workEffortList) {
            String workEffortId = (String)workEffort.get(E.workEffortId.name());
            disableWorkEffort(workEffortId, workEffortIdList);
        }
    }
    
    private void disableWorkEffort(String workEffortId, List<String> workEffortIdList) throws GeneralException {
        if (!workEffortIdList.contains(workEffortId)) {
            String msg = "The following workEffort will be deleted [" + workEffortId + "]";
            getTakeOverService().addLogInfo(msg);
            Map<String, Object> serviceMapParams = UtilMisc.toMap(E.workEffortId.name(), (Object)workEffortId);
            Map<String, Object> result = getTakeOverService().runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffort.name(), E.WorkEffort.name(), CrudEvents.OP_DELETE, serviceMapParams, E.WorkEffort.name() + FindUtilService.MSG_SUCCESSFULLY_DELETE, FindUtilService.MSG_ERROR_DELETE + E.WorkEffort.name(), true);
            if (ServiceUtil.isSuccess(result)) {
                msg = "Delete workeffort with " + workEffortId;
                getTakeOverService().addLogInfo(msg);
            } else {
                msg = ServiceUtil.getErrorMessage(result);
                getTakeOverService().addLogInfo(msg);
            }
        }

    }
}
