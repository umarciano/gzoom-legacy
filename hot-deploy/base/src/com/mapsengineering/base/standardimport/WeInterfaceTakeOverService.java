package com.mapsengineering.base.standardimport;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.bl.crud.AbstractCrudHandler;
import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.standardimport.helper.WeInterfaceHelper;
import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Elaborate workEffort
 *
 */
public class WeInterfaceTakeOverService extends AbstractWorkEffortRootTakeOverService {

    public static final String MODULE = WeInterfaceTakeOverService.class.getName();

    private String workEffortLevelId;
    private String sourceReferenceId;
    private String workEffortTypeLevelId;

    private WeInterfaceHelper weInterfaceHelper;

    private String statusRootId;

    private String currentStatusId;

    @Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento. 
     * Recupera tutti i campi da externalValue, record sulla tabella di interfaccia
     * @params extLogicKey chiave logica esterna
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        ImportManager manager = getManager();
        GenericValue externalValue = getExternalValue();

        // se externalValue != null importa da WEInterface, altrimenti non e possibile continuare l'esecuzione del servizio
        if (externalValue == null) {
            throw new ImportException(getEntityName(), "", "record required to import");
        }
        // extLogicKey {sourceReferenceRootId=BSC12}
        // externalValue 

        // per avere id sourceReferenceRootId
        getContextData();

        if (UtilValidate.isEmpty(getWorkEffortRootId())) {
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), WeRootInterfaceContext.ERROR_ROOT_RECORD);
        }

        weInterfaceHelper = new WeInterfaceHelper(this, manager.getDelegator());
        sourceReferenceId = externalValue.getString(E.sourceReferenceId.name());
        setWorkEffortName(externalValue.getString(E.workEffortName.name()));
        String msg = " Try workEffort with sourceReferenceId " + sourceReferenceId + " and title " + getWorkEffortName();
        addLogInfo(msg);

        workEffortTypeLevelId = checkValidityWorkEffortType(null, WeInterfaceConstants.IS_ROOT_N);

        GenericValue localValue = weInterfaceHelper.getWorkEffort(getWorkEffortRootId(), workEffortTypeLevelId, sourceReferenceId, getOrganizationId(), getWorkEffortName());
        msg = "localValue " + localValue;
        addLogInfo(msg);
        if (UtilValidate.isNotEmpty(localValue)) {
            workEffortLevelId = localValue.getString(E.workEffortId.name());
            currentStatusId = localValue.getString(E.currentStatusId.name());
            msg = "Found workEffort with id " + workEffortLevelId;
            addLogInfo(msg);
        } else {
            workEffortLevelId = WeInterfaceConstants.WORK_EFFORT_PREFIX + manager.getDelegator().getNextSeqId(E.WorkEffort.name());
            msg = "Not Found workEffort, so create with id " + workEffortLevelId;
            addLogInfo(msg);
        }
        setLocalValue(localValue);
    }

    @Override
    /**
     * Esegue importazione record esterno
     */
    public void doImport() throws GeneralException {
    	setImported(true);
        String msg;

        updateWeInterface();
        
        setWorkEffortDates();

        // Controlli solo in caso di obiettivo non esistente
        if (isRealOperationInsert()) {
            checkValidityInsert();
        }
        checkValidityDate();

        // 3.1.3 Controllo esistenza ROLE_TYPE
        checkValidityOrgUnitRoleTypeId();
        // 3.1.4 Controllo esistenza PARTY_PARENT_ROLE
        checkValidityOrgUnitId(sourceReferenceId);
        // 3.1.5 Controllo esistenza PARTY_GROUP
        checkValidityOrgUnitName();
        // 3.1.6 Controllo esistenza PARTY_PARENT_ROLE
        checkValidityOrgUnit();

        // 2.2.5 Controllo esistenza WORK_EFFORT_PURPOSE_TYPE
        checkValidityWorkEffortPurposeType();

        // 2.1.7 Controllo esistenza WORK_EFFORT
        createOrUpdateWorkEffort();

        msg = "END IMPORT WorkEffort " + sourceReferenceId + "[" + workEffortLevelId + "]";
        addLogInfo(msg);
    }

    private void updateWeInterface() {
        getExternalValue().set(E.workEffortId.name(), workEffortLevelId);
        getExternalValue().set(E.workEffortRootId.name(), getWorkEffortRootId());
        getExternalValue().set(E.elabResult.name(), ImportManager.RECORD_ELAB_RESULT_OK);
    }

    private void checkValidityOrgUnit() throws GeneralException {
        if (UtilValidate.isNotEmpty(getOrgUnitId()) || UtilValidate.isNotEmpty(getOrgUnitRoleTypeId())) {
            GenericValue partyRole = getPartyRoleHelper().checkValidityPartyRole(getOrgUnitId(), getOrgUnitRoleTypeId(), E.ORGANIZATION_UNIT.name(), sourceReferenceId);

            // prendo i dati dal partyRole perche potrebbero averm ipassato solo orgCode e quindi potrei recuperare roleTtypeId da partyRole
            setOrgUnitId(partyRole.getString(E.partyId.name()));
            setOrgUnitRoleTypeId(partyRole.getString(E.roleTypeId.name()));
        } else {
            // se vuoti prende da Root
            setOrgUnitFromRoot();
        }
        String msg = "Elaborating workEffort with OrgUnit " + getOrgUnitId() + " and role " + getOrgUnitRoleTypeId();
        addLogInfo(msg);
    }

    private void setOrgUnitFromRoot() {
        WeRootInterfaceContext weRootInterfaceContext = WeRootInterfaceContext.get(getManager());

        setOrgUnitId(weRootInterfaceContext.getOrgUnitRootId());
        setOrgUnitRoleTypeId(weRootInterfaceContext.getOrgUnitRoleTypeRootId());

    }

    private void createOrUpdateWorkEffort() throws GeneralException {
        GenericValue we = getLocalValue();

        String msg = "";

        Map<String, Object> serviceMapParams = setServiceMapParameters();
        Map<String, Object> result = null;
        if (UtilValidate.isEmpty(we)) {
            serviceMapParams.put(AbstractCrudHandler.AUTOMATIC_PK, "Y");

            serviceMapParams.put(E.organizationId.name(), getOrganizationId());
            serviceMapParams.put(E.workEffortTypeId.name(), workEffortTypeLevelId);
            serviceMapParams.put(E.workEffortParentId.name(), getWorkEffortRootId());
            serviceMapParams.put(E.revisionNumber.name(), 1L);

            serviceMapParams.put(E.weightKpi.name(), getWeightKpi());
            serviceMapParams.put(E.weightSons.name(), getWeightSons());
            serviceMapParams.put(E.weightAssocWorkEffort.name(), getWeightAssocWorkEffort());
            serviceMapParams.put(E.totalEnumIdKpi.name(), getTotalEnumIdKpi());
            serviceMapParams.put(E.totalEnumIdSons.name(), getTotalEnumIdSons());
            serviceMapParams.put(E.totalEnumIdAssoc.name(), getTotalEnumIdAssoc());

            serviceMapParams.put(E.workEffortIdRoot.name(), getWorkEffortRootId());

            serviceMapParams.put(E.workEffortAssocTypeId.name(), getWorkEffortAssocTypeId());
            serviceMapParams.put(E.wePurposeTypeIdWe.name(), getWePurposeTypeIdWe());

            serviceMapParams.put(E.isRoot.name(), WeInterfaceConstants.IS_ROOT_N);
            serviceMapParams.put(E.fromCard.name(), WeInterfaceConstants.FROM_CARD_S);
            serviceMapParams.put(E.workEffortPurposeTypeId.name(), getWorkEffortPurposeTypeId());

            // Creazione WorkEffort
            msg = "Trying to create WorkEffort with sourceReferenceId " + sourceReferenceId + " and id " + workEffortLevelId;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffort.name(), E.WorkEffort.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffort.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + E.WorkEffort.name(), true, true);
        } else {
            msg = "Trying to update WorkEffort with sourceReferenceId " + sourceReferenceId + " and id " + workEffortLevelId;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffort.name(), E.WorkEffort.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffort.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.WorkEffort.name(), true, true);
        }
        manageResult(result);
    }

    private void manageResult(Map<String, Object> result) throws GeneralException {
        String msg = "";
        if (ServiceUtil.isSuccess(result)) {
            msg = "successfull created/updated workeffort with sourceReferenceId " + sourceReferenceId + " and id " + workEffortLevelId;
            addLogInfo(msg);

            GenericValue workEffort = getManager().getDelegator().findOne(E.WorkEffort.name(), false, E.workEffortId.name(), workEffortLevelId);
            setLocalValue(workEffort);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg);
        }
    }

    private Map<String, Object> setServiceMapParameters() {
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.sourceReferenceId.name(), sourceReferenceId, E.workEffortId.name(), (Object)workEffortLevelId);
        serviceMapParams.putAll(setServiceMapParametersWorkEffort());
        // in insert prende stato della root, in update lascio stato del workEffort
        if (UtilValidate.isEmpty(currentStatusId)) {
            serviceMapParams.put(E.currentStatusId.name(), statusRootId);
        }
        serviceMapParams.putAll(setServiceMapParameterOrgUnit());

        return serviceMapParams;
    }

    private void checkValidityInsert() throws GeneralException {
        checkValidityWorkEffortName();
        checkValidityWorkEffortNameLang();
    }

    private void checkValidityDate() throws GeneralException {
        GenericValue gv = getExternalValue();
        if (UtilValidate.isEmpty(getEstimatedStartDate()) && UtilValidate.isEmpty(getEstimatedCompletionDate())) {
            setDatesFromRoot();
        } else if (UtilValidate.isEmpty(getEstimatedStartDate()) || UtilValidate.isEmpty(getEstimatedCompletionDate())) {
            String msg = E.estimatedStartDate.name() + " and " + E.estimatedCompletionDate.name() + FindUtilService.MSG_BOTH_NOT_NULL;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        if (getEstimatedStartDate().after(getEstimatedCompletionDate())) {
            String msg = "The field actualCompletionDate must be greater or equals than actualStartDate";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private void setDatesFromRoot() {
        WeRootInterfaceContext weRootInterfaceContext = WeRootInterfaceContext.get(getManager());
        setEstimatedStartDate(weRootInterfaceContext.getEstimatedStartDate());
        setEstimatedCompletionDate(weRootInterfaceContext.getEstimatedCompletionDate());
    }
    
    protected void setWithWeRootInterfaceContext(WeRootInterfaceContext weRootInterfaceContext){
    	 setOperationType(weRootInterfaceContext.getOperationType());
         weRootInterfaceContext.getParentTypeId();
         statusRootId = weRootInterfaceContext.getStatusRootId();
    }

    /**
     * Override ordinamento chiavi per i log
     * @parmas msg, log message
     */
    public void addLogInfo(String msg) {
        String msgNew = getEntityName() + ": " + msg;

        Map<String, Object> map = FastMap.newInstance();
        map.put(E.sourceReferenceRootId.name(), getExternalValue().get(E.sourceReferenceRootId.name()));
        map.put(E.workEffortTypeId.name(), getExternalValue().get(E.workEffortTypeId.name()));
        map.put(E.sourceReferenceId.name(), getExternalValue().get(E.sourceReferenceId.name()));
        map.put(E.workEffortName.name(), getExternalValue().get(E.workEffortName.name()));

        getManager().addLogInfo(msgNew, MODULE, map.toString());
    }
}
