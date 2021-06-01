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
 * Helper for WorkEffortAssocType
 *
 */
public class WeAssocInterfaceHelper {
    private TakeOverService takeOverService;
    private Delegator delegator;
    
    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WeAssocInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
    }
    
    /**
     * 
     * @param workEffortAssocTypeId
     * @return
     * @throws GeneralException
     */
    public GenericValue getWorkEffortAssocType(String workEffortAssocTypeId) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        List<EntityCondition> listaCondition = FastList.newInstance();
        List<EntityCondition> listaConditionType = FastList.newInstance();
        if (!ValidationUtil.isEmptyOrNA(workEffortAssocTypeId)) {
            EntityCondition conditionId = EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), workEffortAssocTypeId);
            listaConditionType.add(conditionId);
            // foundMore += " workEffortTypeId = ".concat(workEffortTypeId);
            // noFound += " workEffortTypeId = ".concat(workEffortTypeId);
        }
    
        listaCondition.add(EntityCondition.makeCondition(listaConditionType));
        
        List<EntityCondition> listaHasTable = FastList.newInstance();
        EntityCondition hasTableN = EntityCondition.makeCondition(E.hasTable.name(), "N");
        EntityCondition hasTableEmpty = EntityCondition.makeCondition(E.hasTable.name(), null);
        listaHasTable.add(hasTableN);
        listaHasTable.add(hasTableEmpty);
        
        listaCondition.add(EntityCondition.makeCondition(listaHasTable, EntityJoinOperator.OR));
        
        EntityCondition condition = EntityCondition.makeCondition(listaCondition);
        String foundMore = "Found more than one workEffortAssocType with condition :" + condition;
        String noFound = "No workEffortAssocType with condition :" + condition;
        
        return getTakeOverService().findOne(E.WorkEffortAssocType.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }
    
    /**
     * Ritorna un WorkEffortAssoc se esiste, altrimenti null.
     * <br>Lancia eccezione se ne trova piu di uno
     * @param workEffortIdFrom
     * @param workEffortIdTo
     * @param workEffortAssocTypeId
     * @return
     * @throws GeneralException
     */
    public GenericValue getWorkEffortAssoc(String workEffortIdFrom, String workEffortIdTo, String workEffortAssocTypeId) throws GeneralException {
        GenericValue workEffortAssoc = null;
        GenericValue gv = getTakeOverService().getExternalValue();
        String msg = "";
        
        List<GenericValue> parents = getWorkEffortAssocList(workEffortIdFrom, workEffortIdTo, workEffortAssocTypeId);
                
        if (UtilValidate.isNotEmpty(parents) && parents.size() > 1) {
            msg = "Found more than one workEffortAssoc with  workEffortAssocTypeId = " + workEffortAssocTypeId + " workEffortIdFrom = " + workEffortIdFrom + " workEffortIdTo = " + workEffortIdTo;
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }else {
            workEffortAssoc = EntityUtil.getFirst(parents);
        }
        return workEffortAssoc;
    }
    
    /**
     * Ritorna lista di WorkEffortAssoc
     * @param workEffortIdFrom
     * @param workEffortIdTo
     * @param workEffortAssocTypeId
     * @return
     * @throws GeneralException
     */
    public List<GenericValue> getWorkEffortAssocList(String workEffortIdFrom, String workEffortIdTo, String workEffortAssocTypeId) throws GeneralException {
        EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortIdFrom.name(), workEffortIdFrom, E.workEffortIdTo.name(), workEffortIdTo, E.workEffortAssocTypeId.name(), workEffortAssocTypeId));
        return getDelegator().findList(E.WorkEffortAssoc.name(), cond, null, null, null, false);
    }

    /**
     * Import i workEffort figli della scheda, e poi scorre quelli presenti sul db ed elimina quelli che non sono stati gestiti nell'importazione attuale
     * @param sourceReferenceRootId
     * @param workEffortRootId
     */
    public void importWorkEffortAssoc(String sourceReferenceRootId, String workEffortRootId) throws GeneralException {
        Map<String, List<GenericValue>> resultList = getTakeOverService().doImportMulti(ImportManagerConstants.WE_ASSOC_INTERFACE, UtilMisc.toMap(E.sourceReferenceRootId.name(), sourceReferenceRootId));
        GenericValue gv = getTakeOverService().getExternalValue();
        if (! OperationTypeEnum.A.name().equals(gv.getString(E.operationType.name()))) {
        	List<GenericValue> localValues = resultList.get("localValues");
        	if (UtilValidate.isNotEmpty(localValues)) {
        		checkWorkEffortAssocList(localValues, workEffortRootId);
        	}
        }
    }
    
    private void checkWorkEffortAssocList(List<GenericValue> resultList, String workEffortRootId) throws GeneralException {
        List<String> workEffortAssocTypeIdList = getworkEffortAssocTypeManaged(resultList);
        String msg = "The following workEffortAssocTypeId are managed " + workEffortAssocTypeIdList;
        getTakeOverService().addLogInfo(msg);
        for (String workEffortAssocTypeId : workEffortAssocTypeIdList) {
            List<GenericValue> workEffortAssocList = getWorkEffortAssocByCondition(workEffortAssocTypeId, workEffortRootId);
            for (GenericValue workEffortAssoc : workEffortAssocList) {
                checkWorkEffortAssocWeAssocInterface(workEffortAssoc, resultList);
            }
        }
    }

    private List<GenericValue> getWorkEffortAssocByCondition(String workEffortAssocTypeId, String workEffortRootId) throws GeneralException {
        EntityCondition conditionAssocTypeId = EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), workEffortAssocTypeId);
        
        List<EntityCondition> conditionList = FastList.newInstance();
        EntityCondition conditionParentIdFrom = EntityCondition.makeCondition(E.wrToParentId.name(), workEffortRootId);
        EntityCondition conditionParentIdTo = EntityCondition.makeCondition(E.wrFromParentId.name(), workEffortRootId);
        conditionList.add(conditionParentIdFrom);
        conditionList.add(conditionParentIdTo);
        EntityCondition conditionParentId = EntityCondition.makeCondition(conditionList, EntityJoinOperator.OR);
        
        EntityCondition condition = EntityCondition.makeCondition(conditionParentId, conditionAssocTypeId);

        return getDelegator().findList(E.WorkEffortAssocAndParentView.name(), condition, null, null, null, false);
    }

    private void checkWorkEffortAssocWeAssocInterface(GenericValue workEffortAssocAndParentView, List<GenericValue> workEffortAssocStoredList) throws GeneralException {
        boolean found = false;
        for (GenericValue workEffortAssocStored : workEffortAssocStoredList) {
            if (workEffortAssocStored.getString(E.workEffortIdFrom.name()).equals(workEffortAssocAndParentView.getString(E.workEffortIdFrom.name())) 
                    && workEffortAssocStored.getString(E.workEffortIdTo.name()).equals(workEffortAssocAndParentView.getString(E.workEffortIdTo.name())) 
                    && workEffortAssocStored.getString(E.workEffortAssocTypeId.name()).equals(workEffortAssocAndParentView.getString(E.workEffortAssocTypeId.name()))
                    && workEffortAssocStored.getString(E.fromDate.name()).equals(workEffortAssocAndParentView.getString(E.fromDate.name()))) {
                found = true;
                break;
            }
        }
        if (!found) {
            disabledWorkEffortAssoc(workEffortAssocAndParentView);
        }
    }

    private void disabledWorkEffortAssoc(GenericValue workEffortWorkEffortAssocAndType) throws GeneralException {
        String msg = "The following workEffortAssoc will be deleted " + workEffortWorkEffortAssocAndType.getPrimaryKey();
        getTakeOverService().addLogInfo(msg);
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.workEffortAssocTypeId.name(), (Object)workEffortWorkEffortAssocAndType.get(E.workEffortAssocTypeId.name()), 
                E.workEffortIdFrom.name(), (Object)workEffortWorkEffortAssocAndType.get(E.workEffortIdFrom.name()), 
                E.workEffortIdTo.name(), (Object)workEffortWorkEffortAssocAndType.get(E.workEffortIdTo.name()),
                E.fromDate.name(), (Object)workEffortWorkEffortAssocAndType.get(E.fromDate.name()),
                E.executeToClone.name(), E.Y.name());
        Map<String, Object> result = getTakeOverService().runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortAssoc.name(), E.WorkEffortAssoc.name(), CrudEvents.OP_DELETE, serviceMapParams, E.WorkEffortAssoc.name() + FindUtilService.MSG_SUCCESSFULLY_DELETE, FindUtilService.MSG_ERROR_DELETE + E.WorkEffortAssoc.name(), true);
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull delete workeffortAssoc";
            getTakeOverService().addLogInfo(msg);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            getTakeOverService().addLogInfo(msg);
        }
    }

    private List<String> getworkEffortAssocTypeManaged(List<GenericValue> resultList) throws GeneralException {
        return EntityUtil.getFieldListFromEntityList(resultList, E.workEffortAssocTypeId.name(), true);
    }
    
    private Delegator getDelegator() {
        return delegator;
    }

    private TakeOverService getTakeOverService() {
        return takeOverService;
    }
}
