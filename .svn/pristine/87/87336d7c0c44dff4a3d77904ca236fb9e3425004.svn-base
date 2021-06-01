package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

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
import com.mapsengineering.base.standardimport.common.DeleteEnum;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.workeffort.OperationTypeEnum;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for WeMeasureInterface 
 *
 */
public class WeMeasureInterfaceHelper {
    private TakeOverService takeOverService;
    private Delegator delegator;
    
    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WeMeasureInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
    }

    /**
     * Import i workEffort figli della scheda, e poi scorre quelli presenti sul db ed elimina quelli che non sono stati gestiti nell'importazione attuale
     * @param sourceReferenceRootId
     * @param workEffortRootId
     */
    public void importWorkEffortMeasure(String sourceReferenceRootId, String workEffortRootId, DeleteEnum deletePreviousItem) throws GeneralException {
        Map<String, List<GenericValue>> resultList = getTakeOverService().doImportMulti(ImportManagerConstants.WE_MEASURE_INTERFACE, UtilMisc.toMap(E.sourceReferenceRootId.name(), sourceReferenceRootId));
        GenericValue gv = getTakeOverService().getExternalValue();
        if (! OperationTypeEnum.A.name().equals(gv.getString(E.operationType.name()))) {
        	List<GenericValue> resultListInterface = resultList.get("externalValues");
        	if (UtilValidate.isNotEmpty(resultListInterface)) {
        		checkWorkEffortMeasureList(resultListInterface, workEffortRootId, deletePreviousItem);
        	}
        }        
    }

    private void checkWorkEffortMeasureList(List<GenericValue> weMeasureInterfaceList, String workEffortRootId, DeleteEnum deletePreviousItem) throws GeneralException {
        List<String> workEffortTypeIdList = getworkEffortTypeManaged(weMeasureInterfaceList);
        String msg = "Case: " + deletePreviousItem + ", the following workEffortTypeId are managed " + workEffortTypeIdList;
        getTakeOverService().addLogInfo(msg);
        
        for(String workEffortTypeId: workEffortTypeIdList) {
            List<EntityCondition> conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId)));
            conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortParentId.name(), workEffortRootId)));
            EntityCondition condition = EntityCondition.makeCondition(conditionList);
            List<GenericValue> workEffortList = getTakeOverService().getManager().getDelegator().findList(E.WorkEffort.name(), condition, null, null, null, false);
            if(UtilValidate.isNotEmpty(workEffortList)){
                checkWorkEffortList(workEffortList, weMeasureInterfaceList, deletePreviousItem);
            } else {
                msg = "No workEffort found with condition " + condition;
                getTakeOverService().addLogInfo(msg);
            }
        }
        
    }

    private void checkWorkEffortList(List<GenericValue> workEffortList, List<GenericValue> weMeasureInterfaceList, DeleteEnum deletePreviousItem) throws GeneralException {
        List<String> weMeasureDataSourceIdList = getweMeasureDataSourceIdManaged(weMeasureInterfaceList);
        String msg = "The following weMeasureDataSourceId are managed " + weMeasureDataSourceIdList;
        getTakeOverService().addLogInfo(msg);
        
        List<String> weMeasureTypeEnumIdList = getweMeasureTypeEnumIdManaged(weMeasureInterfaceList);
        msg = "The following weMeasureTypeEnumId are managed " + weMeasureTypeEnumIdList;
        getTakeOverService().addLogInfo(msg);
        
        List<String> workEffortIdList = EntityUtil.getFieldListFromEntityList(workEffortList, E.workEffortId.name(), true);
        msg = "The following workEffortId are managed " + workEffortIdList;
        getTakeOverService().addLogInfo(msg);
        
        for(String workEffortId : workEffortIdList) {
            for(String weMeasureTypeEnumId: weMeasureTypeEnumIdList) {
                List<EntityCondition> conditionWeList = FastList.newInstance();
                conditionWeList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortId.name(), workEffortId)));
                conditionWeList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.weMeasureTypeEnumId.name(), weMeasureTypeEnumId)));
                EntityCondition conditionWe = EntityCondition.makeCondition(conditionWeList);

                List<GenericValue> workEffortMeasureList = getTakeOverService().getManager().getDelegator().findList(E.WorkEffortMeasure.name(), conditionWe, null, null, null, false);
                
                for(GenericValue workEffortMeasure : workEffortMeasureList) {
                    checkDeleteWorkEffortMeasure(weMeasureInterfaceList, workEffortMeasure, deletePreviousItem, weMeasureDataSourceIdList);
                }
            }
        }
    }

    private List<String> getweMeasureDataSourceIdManaged(List<GenericValue> weMeasureInterfaceList) {
        return EntityUtil.getFieldListFromEntityList(weMeasureInterfaceList, E.dataSource.name(), true);
    }

    private void checkDeleteWorkEffortMeasure(List<GenericValue> weMeasureInterfaceList, GenericValue workEffortMeasure, DeleteEnum deletePreviousItem, List<String> weMeasureDataSourceIdList) throws GeneralException {
        boolean toDelete = true;
        String localWorkEffortMeasureId = workEffortMeasure.getString(E.workEffortMeasureId.name());
        String localDataSourceId = workEffortMeasure.getString(E.dataSourceId.name());
        
        toDelete =  getToDelete(weMeasureInterfaceList, deletePreviousItem, localWorkEffortMeasureId, localDataSourceId, weMeasureDataSourceIdList);
        if (toDelete) {
            deleteWorkEffortMeasure(localWorkEffortMeasureId);
        }
    }

    /**
     * Check deletePreviousItem value and then if the measure is imported
     * @param weMeasureInterfaceList
     * @param deletePreviousItem
     * @param localWorkEffortMeasureId
     * @param localDataSourceId
     * @param weMeasureDataSourceIdList
     * @return
     * @throws GenericEntityException
     */
    private boolean getToDelete(List<GenericValue> weMeasureInterfaceList, DeleteEnum deletePreviousItem, String localWorkEffortMeasureId, String localDataSourceId, List<String> weMeasureDataSourceIdList) throws GenericEntityException {
        boolean toDelete = DeleteEnum.getToDelete(getTakeOverService().getManager().getDelegator(), deletePreviousItem, localWorkEffortMeasureId, localDataSourceId, weMeasureDataSourceIdList);
        
        if (toDelete) {
            List<String> workEffortMeasureIdList = EntityUtil.getFieldListFromEntityList(weMeasureInterfaceList, E.workEffortMeasureId.name(), true);
            
            if (workEffortMeasureIdList.contains(localWorkEffortMeasureId)){
                toDelete = false;
            }
        }
        
        return toDelete;
    }

    private void deleteWorkEffortMeasure(String localWorkEffortMeasureId) throws GeneralException {
        String msg = "Trying to delete WorkEffortMeasure with id = " + localWorkEffortMeasureId;
        getTakeOverService().addLogInfo(msg);
        Map<String, Object> serviceMapParams = setServiceMapParameters(localWorkEffortMeasureId);
        
        Map<String, Object> result = getTakeOverService().runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortMeasure.name(), E.WorkEffortMeasure.name(), CrudEvents.OP_DELETE, serviceMapParams, E.WorkEffortMeasure.name() + FindUtilService.MSG_SUCCESSFULLY_DELETE, FindUtilService.MSG_ERROR_DELETE + E.WorkEffortMeasure.name(), true);
    
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull delete WorkEffortMeasure with id = " + localWorkEffortMeasureId;
            getTakeOverService().addLogInfo(msg);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            getTakeOverService().addLogInfo(msg);
        }
        
    }

    /** Crea mappa parametri epr la cancellazione del workEffortMeasure, aggiungendo glAccountId in modo da cancellare anche eventuale WorkEffortMeasRatSc
     * @throws GenericEntityException */
    private Map<String, Object> setServiceMapParameters(String localWorkEffortMeasureId) throws GenericEntityException {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        
        serviceMapParams.put(E.workEffortMeasureId.name(), localWorkEffortMeasureId);
        
        GenericValue workEffortMeasureToDelete = getTakeOverService().getManager().getDelegator().findOne(E.WorkEffortMeasure.name(), false, E.workEffortMeasureId.name(), localWorkEffortMeasureId);
        
        if(UtilValidate.isNotEmpty(workEffortMeasureToDelete)) {
            serviceMapParams.put(E.glAccountId.name(), workEffortMeasureToDelete.getString(E.glAccountId.name()));
        }
        
        return serviceMapParams;
    }

    private List<String> getworkEffortTypeManaged(List<GenericValue> weMeasureInterfaceList) throws GenericEntityException {
        List<String> workEffortAssocTypeIdList = FastList.newInstance();
        List<String> workEffortIdList = EntityUtil.getFieldListFromEntityList(weMeasureInterfaceList, E.workEffortId.name(), true);
        
        for (String workEffortId : workEffortIdList) {
            GenericValue workEffort = delegator.findOne(E.WorkEffort.name(), false, E.workEffortId.name(), workEffortId);
            if(UtilValidate.isNotEmpty(workEffort)) {
                String workEffortTypeId = workEffort.getString(E.workEffortTypeId.name());
                if(!workEffortAssocTypeIdList.contains(workEffortTypeId)) {
                    workEffortAssocTypeIdList.add(workEffortTypeId);
                }
            }
            
        }
        
        return workEffortAssocTypeIdList;
    }

    private List<String> getweMeasureTypeEnumIdManaged(List<GenericValue> weMeasureInterfaceList) {
        return EntityUtil.getFieldListFromEntityList(weMeasureInterfaceList, E.weMeasureTypeEnumId.name(), true);
    }
    
    /**
     * Cerca misura in base al codice workEffortMeasureId, altrimenti in base a workEffortId e glAccountId.
     * Se ne trova piu di una utilizza l'uomDescr per distinguerle.
     * <Br>In inserimento bisogna passare all'inserimento di una nuova misura
     * 
     * @param workEffortMeasureCode
     * @param workEffortRootId
     * @param entityName
     * @param id
     * @return
     * @throws GeneralException
     */
    public GenericValue getWorkEffortMeasure(String workEffortMeasureCode, String workEffortRootId, String workEffortId, String glAccountId, String uomDescr, String operationType) throws GeneralException {
        GenericValue workEffortMeasure = null;
        
        if(!ValidationUtil.isEmptyOrNA(workEffortMeasureCode)) {
            workEffortMeasure = getWorkEffortMeasureById(workEffortMeasureCode, workEffortRootId);
        } else if (!OperationTypeEnum.I.name().equals(operationType) && !OperationTypeEnum.O.name().equals(operationType)) {
            workEffortMeasure = getWorkEffortMeasureByWorkEffortAndGlAccount(workEffortMeasureCode, workEffortId, glAccountId, uomDescr);
        }
        return workEffortMeasure;
    }

    private GenericValue getWorkEffortMeasureByWorkEffortAndGlAccount(String workEffortMeasureCode, String workEffortId, String glAccountId, String uomDescr) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortId.name(), workEffortId, E.glAccountId.name(), glAccountId)));
        if(!ValidationUtil.isEmptyOrNA(workEffortMeasureCode)) {
            conditionList.add(EntityCondition.makeCondition(E.workEffortMeasureId.name(), workEffortMeasureCode));
        }
        EntityCondition cond = EntityCondition.makeCondition(conditionList);
        
        List<GenericValue> workEffortMeasureList = delegator.findList(E.WorkEffortMeasure.name(), cond, null, null, null, false);
        
        if(UtilValidate.isNotEmpty(workEffortMeasureList) && workEffortMeasureList.size() > 1) {
            if(!ValidationUtil.isEmptyOrNA(uomDescr)) {
                conditionList.add(EntityCondition.makeCondition(E.uomDescr.name(), uomDescr));
                EntityCondition conditionWithUomDescr = EntityCondition.makeCondition(conditionList);
                List<GenericValue> workEffortMeasureListWithUomDescr = delegator.findList(E.WorkEffortMeasure.name(), conditionWithUomDescr, null, null, null, false);
                
                if(UtilValidate.isNotEmpty(workEffortMeasureListWithUomDescr) && workEffortMeasureListWithUomDescr.size() > 1) {
                    throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), "Found more workEffortMeasure with condition " + conditionWithUomDescr);
                }
                return EntityUtil.getFirst(workEffortMeasureListWithUomDescr);
            } else {
                throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), "Found more workEffortMeasure with condition " + cond);
            }
            
        }
        
        return EntityUtil.getFirst(workEffortMeasureList);
    }

    private GenericValue getWorkEffortMeasureById(String workEffortMeasureCode, String workEffortRootId) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureCode));
        List<GenericValue> workEffortMeasureList = delegator.findList(E.WorkEffortMeasure.name(), cond, null, null, null, false);
        GenericValue workEffortMeasure = EntityUtil.getFirst(workEffortMeasureList);
        if (UtilValidate.isNotEmpty(workEffortMeasure)) {
            String workEffortId = workEffortMeasure.getString(E.workEffortId.name());
            
            List<EntityCondition> conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortId.name(), workEffortId)));
            conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortParentId.name(), workEffortRootId)));
            
            EntityCondition condition = EntityCondition.makeCondition(conditionList);
            String foundMore = "";
            String noFound = "";
            takeOverService.findOne(E.WorkEffort.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
        }
        return workEffortMeasure;
    }
    
    private TakeOverService getTakeOverService() {
        return takeOverService;
    }
}
