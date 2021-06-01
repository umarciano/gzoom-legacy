package com.mapsengineering.base.standardimport;

import java.util.Map;

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
import com.mapsengineering.base.standardimport.common.WeContextEnum;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.standardimport.helper.WeAssocInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeMeasureInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeNoteInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WePartyInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeRootInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeStatusInterfaceHelper;
import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;
import com.mapsengineering.base.standardimport.workeffort.OperationTypeEnum;
import com.mapsengineering.base.standardimport.workeffort.WorkEffortRootDeleter;
import com.mapsengineering.base.util.ContextIdEnum;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Elaborate WorkEffort Root
 *
 */
public class WeRootInterfaceTakeOverService extends AbstractWorkEffortRootTakeOverService {

    public static final String MODULE = WeRootInterfaceTakeOverService.class.getName();

    private String sourceReferenceRootId;
    private WeContextEnum weContext;

    private WeRootInterfaceHelper weRootInterfaceHelper;
    private WeStatusInterfaceHelper weStatusInterfaceHelper;
    private String workEffortTypeRootId;

    private String statusRootId;
    private String parentTypeId;
    private String defaultStatusPrefix;

    @Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento 
     * @params extLogicKey chiave logica esterna,
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        GenericValue externalValue = getExternalValue();
        if (UtilValidate.isNotEmpty(externalValue)) {
        	sourceReferenceRootId = externalValue.getString(E.sourceReferenceRootId.name());
        } else {
        	sourceReferenceRootId = (String)extLogicKey.get(E.sourceReferenceRootId.name());
        }

        String msg = "Try WorkEffort with sourceReferenceId = " + sourceReferenceRootId;
        addLogInfo(msg);

        weRootInterfaceHelper = new WeRootInterfaceHelper(this, getManager().getDelegator());

        setOrganizationId();

        // l'initLocalValue cerca workeffort Root senza utilizzare externalValue, perche' potrebbe essere invocato in altri punti, quindi i controlli sul workEffortTypeId e operatioType vengono fatti solo nel doImport()
        GenericValue localValue = weRootInterfaceHelper.getWorkEffort(sourceReferenceRootId, getOrganizationId());

        if (UtilValidate.isNotEmpty(localValue)) {
            setWorkEffortRootId(localValue.getString(E.workEffortId.name()));
            msg = "Found workEffort with id " + getWorkEffortRootId();
            addLogInfo(msg);
        } else if (UtilValidate.isNotEmpty(externalValue)) {
            // se externalValue != null importa da WERootInterface, quindi inserisco nuovo workEffort
            setWorkEffortRootId(WeInterfaceConstants.WORK_EFFORT_PREFIX + getManager().getDelegator().getNextSeqId(E.WorkEffort.name()));
        }
        setLocalValue(localValue);
    }

    @Override
    /**
     * Esegue importazione record esterno
     */
    public void doImport() throws GeneralException {
    	setImported(true);
        // sempre prima
        WeRootInterfaceContext.push(getManager());
        try {
            checkValidityOperationType();
            if (OperationTypeEnum.O.name().equals(getOperationType()) && UtilValidate.isNotEmpty(getLocalValue())) {
                deleteRoot();
                setLocalValue(null);
            }
            weStatusInterfaceHelper = new WeStatusInterfaceHelper(this);

            //1.1.1 Controlli preliminari sul tracciato

            checkValidityWeContext();
            String enableSnapshot = checkValidityEnableSnapshot();
            checkValiditySnapshotDescription(enableSnapshot);

            // set workEffortName
            setWorkEffortName(getExternalValue().getString(E.workEffortName.name()));

            setWorkEffortDates();

            // Controlli solo in caso di inserimento nuova scheda.
            if (isRealOperationInsert()) {
                checkValidityInsert();
            }

            // 1.1.2 Impostazione variabili di contesto
            setStatusAndTypeId();

            // 1.1.3 Controllo esistenza WORK_EFFORT_TYPE
            workEffortTypeRootId = checkValidityWorkEffortType(parentTypeId, WeInterfaceConstants.IS_ROOT_Y);

            // 1.1.5 Controllo esistenza WORK_EFFORT_TYPE_STATUS
            checkValidityWorkEffortTypeStatus();

            // 1.1.11 Controllo esistenza WORK_EFFORT_PURPOSE_TYPE
            checkValidityWorkEffortPurposeType();

            // 1.1.6 Controllo esistenza PARTY and ROLE
            checkValidityOrgUnitRoleTypeId();
            checkValidityOrgUnitId(sourceReferenceRootId);
            checkValidityOrgUnitName();
            checkValidityOrgUnit();

            checkValidWorkEffortRoot();
            if (isToDelete()) {
                deleteRoot();
                return;
            }
            createOrUpdateWorkEffort();

            doImportWE();
            doImportWEAssoc();
            doImportWEMeasure();
            doImportWEParty();
            doImportWENote();

            updateWeRootInterface();

            addLogInfo("END IMPORT WorkEffort " + sourceReferenceRootId + " [" + getWorkEffortRootId() + "]");
        } finally {
            WeRootInterfaceContext.pop(getManager());
        }
    }

    private void doImportWENote() throws GeneralException {
        String msg = "Trying to import WorkEffortNoteAndParent with parent " + sourceReferenceRootId + " [" + getWorkEffortRootId() + "]";
        addLogInfo(msg);
        WeNoteInterfaceHelper weNoteInterfaceHelper = new WeNoteInterfaceHelper(this, getManager().getDelegator());
        weNoteInterfaceHelper.importWorkEffortNoteAndData(sourceReferenceRootId, getWorkEffortRootId());

    }

    /** 1.1.5 Controllo esistenza WORK_EFFORT_TYPE_STATUS */
    private void checkValidityWorkEffortTypeStatus() throws GeneralException {
        GenericValue gv = getExternalValue();
        String statusDesc = weStatusInterfaceHelper.getStatusItemDesc(gv, isRealOperationInsert());
        if (UtilValidate.isNotEmpty(statusDesc)) {
            GenericValue workEffortTypeStatus = weStatusInterfaceHelper.checkValidityWorkEffortTypeStatus(workEffortTypeRootId, statusDesc);
            statusRootId = workEffortTypeStatus.getString(E.currentStatusId.name());
        }
    }

    private void doImportWEParty() throws GeneralException {
        String msg = "Trying to import WorkEffortPartyAssignment with parent " + sourceReferenceRootId + " [" + getWorkEffortRootId() + "]";
        addLogInfo(msg);
        WePartyInterfaceHelper wePartyInterfaceHelper = new WePartyInterfaceHelper(this, getManager().getDelegator());
        wePartyInterfaceHelper.importWorkEffortParty(sourceReferenceRootId, getWorkEffortRootId());

    }

    private void doImportWEMeasure() throws GeneralException {
        String msg = "Trying to import WorkEffortMeasure with parent " + sourceReferenceRootId + " [" + getWorkEffortRootId() + "]";
        addLogInfo(msg);
        WeMeasureInterfaceHelper weMeasureInterfaceHelper = new WeMeasureInterfaceHelper(this, getManager().getDelegator());
        weMeasureInterfaceHelper.importWorkEffortMeasure(sourceReferenceRootId, getWorkEffortRootId(), getManager().getDeletePreviuos());
    }

    private void doImportWEAssoc() throws GeneralException {
        String msg = "Trying to import WorkEffortAssoc with parent " + sourceReferenceRootId + " [" + getWorkEffortRootId() + "]";
        addLogInfo(msg);
        WeAssocInterfaceHelper weAssocInterfaceHelper = new WeAssocInterfaceHelper(this, getManager().getDelegator());
        weAssocInterfaceHelper.importWorkEffortAssoc(sourceReferenceRootId, getWorkEffortRootId());
    }

    private void updateWeRootInterface() {
        getExternalValue().set(E.workEffortRootId.name(), getWorkEffortRootId());
        getExternalValue().set(E.elabResult.name(), ImportManager.RECORD_ELAB_RESULT_OK);
    }

    /** 1.2.2 Impostazione variabili di contesto */
    private void setStatusAndTypeId() {
        ContextIdEnum contextIdEnum = ContextIdEnum.parseWeContext(weContext.name());
        if (contextIdEnum != null) {
            parentTypeId = contextIdEnum.code();
            defaultStatusPrefix = contextIdEnum.defaultStatusPrefix();
        }
    }

    /** Controllo preliminare organizzazione */
    private void setOrganizationId() throws GeneralException {
        addLogInfo("Trying defaultOrganizationPartyId = " + getManager().getContext().get(E.defaultOrganizationPartyId.name()));
        setOrganizationId((String)getManager().getContext().get(E.defaultOrganizationPartyId.name()));
    }

    /**
     * controlli generali esistenza WorkEffort
     * @throws GeneralException
     */
    private void checkValidWorkEffortRoot() throws GeneralException {
        weRootInterfaceHelper.getValidWorkEffortRoot(sourceReferenceRootId, getOrganizationId(), workEffortTypeRootId, getOperationType());
    }

    /**
     * cancella la scheda
     * @throws GeneralException
     */
    private void deleteRoot() throws GeneralException {
        new WorkEffortRootDeleter(this, getWorkEffortRootId()).execute();
    }

    private void createOrUpdateWorkEffort() throws GeneralException {
        GenericValue weRoot = getLocalValue();

        //TODO snapShotDescription

        Map<String, Object> serviceMapParams = setServiceMapParameters();
        Map<String, Object> result = null;
        if (UtilValidate.isEmpty(weRoot)) {
            // 1.4 Inserimento WORK_EFFORT 
            serviceMapParams.put(AbstractCrudHandler.AUTOMATIC_PK, "Y");

            serviceMapParams.put(E.organizationId.name(), getOrganizationId());
            serviceMapParams.put(E.workEffortTypeId.name(), workEffortTypeRootId);
            serviceMapParams.put(E.revisionNumber.name(), 1L);

            serviceMapParams.put(E.weightKpi.name(), getWeightKpi());
            serviceMapParams.put(E.weightSons.name(), getWeightSons());
            serviceMapParams.put(E.weightAssocWorkEffort.name(), getWeightAssocWorkEffort());
            serviceMapParams.put(E.totalEnumIdKpi.name(), getTotalEnumIdKpi());
            serviceMapParams.put(E.totalEnumIdSons.name(), getTotalEnumIdSons());
            serviceMapParams.put(E.totalEnumIdAssoc.name(), getTotalEnumIdAssoc());

            serviceMapParams.put(E.workEffortAssocTypeId.name(), getWorkEffortAssocTypeId());
            serviceMapParams.put(E.wePurposeTypeIdWe.name(), getWePurposeTypeIdWe());
            serviceMapParams.put(E.workEffortTypeHierarchyId.name(), getWorkEffortTypeHierarchyId());

            serviceMapParams.put(E.isRoot.name(), WeInterfaceConstants.IS_ROOT_Y);
            serviceMapParams.put(E.fromCard.name(), WeInterfaceConstants.FROM_CARD_S);
            serviceMapParams.put(E.workEffortPurposeTypeId.name(), getWorkEffortPurposeTypeId());

            // Creazione WorkEffortRoot
            String msg = "Trying to create workEffortRoot with sourceReferenceId " + sourceReferenceRootId + " and id " + getWorkEffortRootId();
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffort.name(), E.WorkEffort.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffort.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + E.WorkEffort.name(), true, true);

        } else {
            // 1.3 Aggiornamento WORK_EFFORT
            String msg = "Trying to update workEffortRoot with sourceReferenceId " + sourceReferenceRootId + " and id " + getWorkEffortRootId();
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffort.name(), E.WorkEffort.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffort.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.WorkEffort.name(), true, true);
        }

        manageResult(result);
    }

    private void manageResult(Map<String, Object> result) throws GeneralException {
        String msg = "";
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull created/updated workeffort with " + sourceReferenceRootId + " and id " + getWorkEffortRootId();
            addLogInfo(msg);
            setContextData(result);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg);
        }

    }

    private void getWorkEffortRootId(Map<String, Object> result) {
        if (UtilValidate.isNotEmpty(result.get("id"))) {
            Map<String, Object> resultId = UtilMisc.toMap(result.get("id"));
            if (UtilValidate.isNotEmpty(resultId)) {
                setWorkEffortRootId((String)resultId.get(E.workEffortId.name()));
            }
        }
    }

    private void checkValidityDate() throws GeneralException {
        GenericValue gv = getExternalValue();
        if (UtilValidate.isEmpty(getEstimatedStartDate()) || UtilValidate.isEmpty(getEstimatedCompletionDate())) {
            String msg = MSG_IF_IS_REAL_INSERT_OPERATION + E.estimatedStartDate.name() + " and " + E.estimatedCompletionDate.name() + FindUtilService.MSG_NOT_NULL;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (getEstimatedStartDate().after(getEstimatedCompletionDate())) {
            String msg = "The field actualCompletionDate must be greater or equals than actualStartDate";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private Map<String, Object> setServiceMapParameters() {
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.sourceReferenceId.name(), (Object)sourceReferenceRootId, E.workEffortId.name(), (Object)getWorkEffortRootId());
        serviceMapParams.putAll(setServiceMapParametersWorkEffort());
        String msg = "Estimated Start Date = " + serviceMapParams.get(E.estimatedStartDate.name()) + " and estimated Completion Date = " + serviceMapParams.get(E.estimatedCompletionDate.name());
        addLogInfo(msg);
        // insert e update solo se
        if (UtilValidate.isNotEmpty(statusRootId)) {
            serviceMapParams.put(E.currentStatusId.name(), statusRootId);
        } else {
            serviceMapParams.put(E.defaultStatusPrefix.name(), defaultStatusPrefix);
        }
        serviceMapParams.putAll(setServiceMapParameterOrgUnit());
        return serviceMapParams;
    }

    private void checkValidityOrgUnit() throws GeneralException {
        if (UtilValidate.isNotEmpty(getOrgUnitId()) || UtilValidate.isNotEmpty(getOrgUnitRoleTypeId())) {
            GenericValue partyRole = getPartyRoleHelper().checkValidityPartyRole(getOrgUnitId(), getOrgUnitRoleTypeId(), E.ORGANIZATION_UNIT.name(), sourceReferenceRootId);

            // prende i dati dal partyRole perche potrebbe essere arrivato al servizio solo orgCode e quindi manca il roleTypeId
            setOrgUnitId(partyRole.getString(E.partyId.name()));
            setOrgUnitRoleTypeId(partyRole.getString(E.roleTypeId.name()));

            String msg = "Elaborating workEffortRoot with OrgUnit " + getOrgUnitId() + " and role " + getOrgUnitRoleTypeId();
            addLogInfo(msg);
        }
    }

    private void doImportWE() throws GeneralException {
        String msg = "Trying to import WorkEffort with parent " + sourceReferenceRootId + " [" + getWorkEffortRootId() + "]";
        addLogInfo(msg);

        WeInterfaceHelper weInterfaceHelper = new WeInterfaceHelper(this, getManager().getDelegator());
        weInterfaceHelper.importWorkEffort(sourceReferenceRootId, getWorkEffortRootId());
    }

    private void checkValidityInsert() throws GeneralException {
        checkValidityOrgUnitNotEmpty();
        checkValidityWorkEffortName();
        checkValidityWorkEffortNameLang();
        checkValidityDate();
    }

    private void checkValidityOrgUnitNotEmpty() throws GeneralException {
        GenericValue gv = getExternalValue();
        String orgDesc = gv.getString(E.orgDesc.name());
        String orgCode = gv.getString(E.orgCode.name());

        if (UtilValidate.isEmpty(orgDesc) && UtilValidate.isEmpty(orgCode)) {
            String msg = MSG_IF_IS_REAL_INSERT_OPERATION + E.orgCode.name() + " or " + E.orgDesc.name() + FindUtilService.MSG_NOT_NULL;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private void checkValiditySnapshotDescription(String enableSnapshot) throws GeneralException {
        GenericValue gv = getExternalValue();
        String snapShotDescription = gv.getString(E.snapshotDescription.name());
        if (WeInterfaceConstants.CREATE_SNAPSHOT_Y.equals(enableSnapshot) && UtilValidate.isEmpty(snapShotDescription)) {
            String msg = "If createSnapshot is Y the field " + E.snapshotDescription.name() + FindUtilService.MSG_NOT_NULL;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private String checkValidityEnableSnapshot() throws GeneralException {
        GenericValue gv = getExternalValue();
        String createSnapshot = gv.getString(E.createSnapshot.name());

        if (UtilValidate.isNotEmpty(createSnapshot) && !WeInterfaceConstants.CREATE_SNAPSHOT_Y.equals(createSnapshot) && !WeInterfaceConstants.CREATE_SNAPSHOT_N.equals(createSnapshot)) {
            String msg = "The field createSnapshot must be Y or N ";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        } else if (UtilValidate.isEmpty(createSnapshot)) {
            createSnapshot = WeInterfaceConstants.CREATE_SNAPSHOT_N;
        }

        return createSnapshot;
    }

    /**
     * verifico validita operationType
     * @throws GeneralException
     */
    private void checkValidityOperationType() throws GeneralException {
        GenericValue gv = getExternalValue();
        setOperationType(gv.getString(E.operationType.name()));
        try {
            OperationTypeEnum.valueOf(getOperationType());
        } catch (IllegalArgumentException e) {
            String msg = "The field operationType must be I (Insert) or U (Update) or D (Delete) or O (Overwrite) or A (Append)";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * controlla validita del weContext
     * @throws GeneralException
     */
    private void checkValidityWeContext() throws GeneralException {
        GenericValue gv = getExternalValue();
        String weContextStr = gv.getString(E.weContext.name());
        try {
            weContext = WeContextEnum.valueOf(weContextStr);
        } catch (IllegalArgumentException e) {
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), getWeContextErrorMessage());
        }
    }

    /**
     * costruisce messaggio di errore weContext
     * @return
     */
    private String getWeContextErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("The field weContext must be one of ");
        WeContextEnum[] weContextEnumValues = WeContextEnum.values();
        if (weContextEnumValues != null) {
            int l = weContextEnumValues.length;
            for (int i = 0; i < l; i++) {
                if (weContextEnumValues[i] != null) {
                    sb.append("'");
                    sb.append(weContextEnumValues[i].name());
                    sb.append("'");
                    if (i < l - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        return sb.toString();
    }

    private void setContextData(Map<String, Object> result) throws GeneralException {
        WeRootInterfaceContext wERootInterfaceContext = WeRootInterfaceContext.get(getManager());
        getWorkEffortRootId(result);
        wERootInterfaceContext.setWorkEffortRootId(getWorkEffortRootId());
        GenericValue workEffortRoot = getManager().getDelegator().findOne(E.WorkEffort.name(), false, E.workEffortId.name(), getWorkEffortRootId());
        setLocalValue(workEffortRoot);
        wERootInterfaceContext.setOrgUnitRootId(workEffortRoot.getString(E.orgUnitId.name()));
        wERootInterfaceContext.setOrgUnitRoleTypeRootId(workEffortRoot.getString(E.orgUnitRoleTypeId.name()));
        wERootInterfaceContext.setEstimatedStartDate(workEffortRoot.getTimestamp(E.estimatedStartDate.name()));
        String msg = "EstimatedStartDate " + workEffortRoot.getTimestamp(E.estimatedStartDate.name());
        addLogInfo(msg);

        wERootInterfaceContext.setEstimatedCompletionDate(workEffortRoot.getTimestamp(E.estimatedCompletionDate.name()));
        msg = "EstimatedCompletionDate  " + workEffortRoot.getTimestamp(E.estimatedCompletionDate.name());
        addLogInfo(msg);

        wERootInterfaceContext.setOperationType(getOperationType());
        wERootInterfaceContext.setWeContext(weContext);
        wERootInterfaceContext.setOrganizationId(getOrganizationId());
        wERootInterfaceContext.setParentTypeId(parentTypeId);
        wERootInterfaceContext.setWorkEffortAssocTypeId(getWorkEffortAssocTypeId());
        wERootInterfaceContext.setStatusRootId(workEffortRoot.getString(E.currentStatusId.name()));
    }

    /**
     * verifica se e da cancellare
     * @return
     */
    private boolean isToDelete() {
        return UtilValidate.isNotEmpty(getLocalValue()) && OperationTypeEnum.D.name().equals(getOperationType());
    }
}
