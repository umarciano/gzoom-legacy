package com.mapsengineering.base.standardimport.helper;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for WorkEffortType
 *
 */
public class WeTypeInterfaceHelper {

    private TakeOverService takeOverService;

    /**
     * Constructor
     * @param takeOverService
     */
    public WeTypeInterfaceHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }

    /**
     * Ritorna un unico WorkEffortType
     * @param workEffortTypeId
     * @param description
     * @return workEffortType
     * @throws GeneralException
     */
    public GenericValue checkValidityWorkEffortType(String workEffortTypeId, String parentTypeId, String isRoot) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        if (ValidationUtil.isEmptyOrNA(workEffortTypeId)) {
            String msg = "The field workEffortTypeId must be not empty";
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        List<EntityCondition> listaCondition = FastList.newInstance();
        List<EntityCondition> listaConditionType = FastList.newInstance();
        if (!ValidationUtil.isEmptyOrNA(workEffortTypeId)) {
            EntityCondition conditionId = EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId);
            listaConditionType.add(conditionId);
        }

        listaCondition.add(EntityCondition.makeCondition(listaConditionType));

        if (UtilValidate.isNotEmpty(parentTypeId)) {
            EntityCondition conditionId = EntityCondition.makeCondition(E.parentTypeId.name(), parentTypeId);
            listaCondition.add(conditionId);
        }

        if (UtilValidate.isNotEmpty(isRoot)) {
            listaCondition.add(EntityCondition.makeCondition(E.isRoot.name(), isRoot));
        }
        
        EntityCondition condition = EntityCondition.makeCondition(listaCondition);

        String foundMore = "Found more than one workEffortType with condition :" + condition;
        String noFound = "No workEffortType with condition :" + condition;

        return takeOverService.findOne(E.WorkEffortType.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    /**
     * Ritorna un unico WorkEffortPurposeType
     * @param workEffortPurposeTypeId
     * @param description
     * @param parentTypeId
     * @param purposeTypeEnumId
     * @return workEffortPurposeType
     * @throws GeneralException
     */
    public GenericValue checkValidityWorkEffortPurposeType(String workEffortPurposeTypeId, String description, String parentTypeId, String purposeTypeEnumId) throws GeneralException {
        List<EntityCondition> listaCondition = FastList.newInstance();
        List<EntityCondition> listaConditionType = FastList.newInstance();
        GenericValue gv = getTakeOverService().getExternalValue();
        
        if (UtilValidate.isNotEmpty(workEffortPurposeTypeId)) {
            EntityCondition conditionId = EntityCondition.makeCondition(E.workEffortPurposeTypeId.name(), workEffortPurposeTypeId);
            listaConditionType.add(conditionId);
            // foundMore += " workEffortPurposeTypeId = ".concat(workEffortPurposeTypeId);
            // noFound += " workEffortPurposeTypeId = ".concat(workEffortPurposeTypeId);
        }

        if (UtilValidate.isNotEmpty(description)) {
            EntityCondition conditionDesc = EntityCondition.makeCondition(E.description.name(), description);
            listaConditionType.add(conditionDesc);
            // foundMore += " description = ".concat(description);
            // noFound += " description = ".concat(description);
        }

        listaCondition.add(EntityCondition.makeCondition(listaConditionType));
        listaCondition.add(EntityCondition.makeCondition(E.parentTypeId.name(), parentTypeId));
        listaCondition.add(EntityCondition.makeCondition(E.purposeTypeEnumId.name(), purposeTypeEnumId));

        EntityCondition condition = EntityCondition.makeCondition(listaCondition);

        String foundMore = "Found more than one WorkEffortPurposeType with condition :" + condition;
        String noFound = "No WorkEffortPurposeType with condition :" + condition;

        return takeOverService.findOne(E.WorkEffortPurposeType.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    /**
     * Ritorna unico WorkEffortTypeRole
     * @param roleTypeId
     * @param workEffortTypeId
     * @throws GeneralException
     */
    public void checkValidityWorkEffortTypeRole(String roleTypeId, String workEffortTypeId) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        
        List<EntityCondition> listaCondition = FastList.newInstance();
        listaCondition.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        listaCondition.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
        EntityCondition condition = EntityCondition.makeCondition(listaCondition);

        String foundMore = "Found more than one WorkEffortTypeRole with condition :" + condition;
        String noFound = "No WorkEffortTypeRole with condition :" + condition;

        takeOverService.findOne(E.WorkEffortTypeRole.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    private TakeOverService getTakeOverService() {
        return takeOverService;
    }

}
