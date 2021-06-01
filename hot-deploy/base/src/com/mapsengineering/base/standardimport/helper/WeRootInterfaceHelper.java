package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.workeffort.OperationTypeEnum;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for WorkEffortRoot
 *
 */
public class WeRootInterfaceHelper {

    private TakeOverService takeOverService;
    private Delegator delegator;

    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WeRootInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
    }

    /** 2.1.7 Controllo esistenza WORK_EFFORT
     * Controlla validita dei dati passati, quindi con quel sourceReferenceId e workEffortSnapshotId = null,
     * Trovare un solo record oppure niente,
     * @return workEffort or null
     * @exception se trova piu di un record,
     * @exception se il record trovato non ha stesso tipo obiettivo,
     * @exception non ha trovato il record e operationType = U*/
    public GenericValue getValidWorkEffortRoot(String sourceReferenceId, String organizationId, String workEffortTypeId, String operationType) throws GeneralException {
        GenericValue workEffort = null;
        GenericValue gv = getTakeOverService().getExternalValue();
        String msg = "";

        List<GenericValue> parents = getWorkEffortList(sourceReferenceId, organizationId);

        if (UtilValidate.isNotEmpty(parents) && parents.size() > 1) {
            msg = "Found more than one workEffort with empty workEffortSnapshotId and sourceReferenceId = " + sourceReferenceId;
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        } else if (UtilValidate.isNotEmpty(parents)) {
            workEffort = getValidWorkEffortWithType(sourceReferenceId, workEffortTypeId, EntityUtil.getFirst(parents));
        } else if (UtilValidate.isEmpty(parents) && isOperationUpdateOrDelete(operationType)) {
            msg = "No workEffort found to update with sourceReferenceId = " + sourceReferenceId;
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        return workEffort;
    }

    /**
     * @param sourceReferenceId
     * @param workEffortTypeId
     * @param workEffort
     * @param getEntityName
     * @param id
     * @return
     * @throws GeneralException
     */
    public GenericValue getValidWorkEffortWithType(String sourceReferenceId, String workEffortTypeId, GenericValue workEffort) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        if (UtilValidate.isNotEmpty(workEffort)) {
            String actualWorkEffortTypeId = (String)workEffort.get(E.workEffortTypeId.name());
            if (!actualWorkEffortTypeId.equals(workEffortTypeId)) {
                String msg = "Found workEffort with same sourceReferenceId = " + sourceReferenceId + " but different workEffortTypeId ( " + workEffortTypeId + " and " + workEffort.get(E.workEffortTypeId.name()) + ")";
                throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
        return workEffort;
    }

    /**
     * Ricerca e restituisce primo workEffort a partire dal sourceReferenceId, con workEffortSnapshotId vuoto
     * @param sourceReferenceId
     * @param getEntityName
     * @param id
     * @return
     * @throws GeneralException
     */
    public GenericValue getWorkEffort(String sourceReferenceId, String organizationId) throws GeneralException {
        return EntityUtil.getFirst(getWorkEffortList(sourceReferenceId, organizationId));
    }

    protected Delegator getDelegator() {
        return delegator;
    }

    /**
     * Ricerca workEffort a partire dal sourceReferenceId, con snapshot vuoto
     * @param sourceReferenceId
     * @param entityName
     * @param id
     * @return
     * @throws GeneralException
     */
    public List<GenericValue> getWorkEffortList(String sourceReferenceId, String organizationId) throws GeneralException {
        EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.sourceReferenceId.name(), sourceReferenceId, E.workEffortSnapshotId.name(), null, E.organizationId.name(), organizationId));
        return delegator.findList(E.WorkEffort.name(), cond, null, null, null, false);
    }

    protected TakeOverService getTakeOverService() {
        return takeOverService;
    }

    /**
     * Check sourceReferenceRootIdFrom and sourceReferenceRootIdTo
     * @param sourceReferenceRootId
     * @param sourceReferenceRootIdFrom
     * @param sourceReferenceRootIdTo
     * @throws ImportException
     */
    public void checkValiditySourceReferenceRootId(String sourceReferenceRootId, String sourceReferenceRootIdFrom, String sourceReferenceRootIdTo) throws ImportException {
        GenericValue gv = getTakeOverService().getExternalValue();
        if (!ValidationUtil.isEmptyOrNA(sourceReferenceRootIdFrom) && !ValidationUtil.isEmptyOrNA(sourceReferenceRootIdTo)) {
            if(!sourceReferenceRootId.equals(sourceReferenceRootIdFrom) && !sourceReferenceRootId.equals(sourceReferenceRootIdTo)) {
                Map<String, Object> parameters = UtilMisc.toMap("sourceId", (Object) sourceReferenceRootId);
                JobLogLog sourceReferenceError = new JobLogLog().initLogCode("StandardImportUiLabels", "SOURCE_REF_ERR", parameters, takeOverService.getManager().getLocale());
                
                throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), sourceReferenceError);
            }
        }
    }
    
    /**
     * verifica se e un update o un delete
     * @param operationType
     * @return
     */
    private boolean isOperationUpdateOrDelete(String operationType) {
    	return OperationTypeEnum.U.name().equals(operationType) || OperationTypeEnum.D.name().equals(operationType);
    }
}
