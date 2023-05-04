package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.WeBaseInterfaceTakeOverService;
import com.mapsengineering.base.standardimport.helper.WeAssocInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeRootInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeTypeInterfaceHelper;
import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Elaborate party assignment for workEffort
 *
 */
public class WeAssocInterfaceTakeOverService extends WeBaseInterfaceTakeOverService {

    public static final String MODULE = WeAssocInterfaceTakeOverService.class.getName();

    private static final Double DEFAULT_ASSOC_WEIGHT = 100D;

    private String workEffortTypeIdRoot;

    private WeAssocInterfaceHelper weAssocInterfaceHelper;

    private String sourceReferenceRootIdFrom;
    private String workEffortTypeIdFrom;
    private String workEffortTypeIdTo;
    private String sourceReferenceRootIdTo;
    private String workEffortIdFrom;
    private String workEffortIdTo;

    private String workEffortAssocTypeId;

    private Long sequenceNum;

    private Double assocWeight;

    private String sourceReferenceRootId;

    /** fromDate = workEffortassoc.fromDate if exist, else max estimatedStartDate */
    private Timestamp fromDate;
    /** thruDate = workEffortassoc.thruDate if exist, else min estimatedCompetionDate */
    private Timestamp thruDate;

    private Timestamp estimatedStartDateFrom;

    private Timestamp estimatedCompletionDateFrom;

    private Timestamp estimatedCompletionDateTo;

    private Timestamp estimatedStartDateTo;

    @Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento. 
     * Recupera tutti i campi da externalValue, record sulla tabella di interfaccia
     * @params extLogicKey chiave logica esterna
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        GenericValue externalValue = getExternalValue();

        // se externalValue != null importa da WeAssocInterface
        if (externalValue == null) {
            throw new ImportException(getEntityName(), "", "record required to import");
        }

        sourceReferenceRootId = externalValue.getString(E.sourceReferenceRootId.name());

        // per avere id sourceReferenceRootId
        getContextData();

        if (UtilValidate.isEmpty(getWorkEffortRootId())) {
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), WeRootInterfaceContext.ERROR_ROOT_RECORD);
        }

        setWeRootInterfaceHelper(new WeRootInterfaceHelper(this, getManager().getDelegator()));
        setWeInterfaceHelper(new WeInterfaceHelper(this, getManager().getDelegator()));
        // l'initLocalValue cerca workeffort senza utilizzare externalValue, perche' potrebbe essere invocato in altri punti, quindi i controli sul workEffortTypeId e operatioType vengono fatti solo nel doImport()
        GenericValue weRoot = getWeRootInterfaceHelper().getWorkEffort(sourceReferenceRootId, getOrganizationId());

        checkValiditySourceReferenceRootId();

        if (UtilValidate.isNotEmpty(weRoot)) {
            // Default per il from
            if (ValidationUtil.isEmptyOrNA(sourceReferenceRootIdFrom)) {
                sourceReferenceRootIdFrom = sourceReferenceRootId;
            }
            if (ValidationUtil.isEmptyOrNA(sourceReferenceRootIdTo)) {
                sourceReferenceRootIdTo = sourceReferenceRootId;
            }
            setWorkEffortRootId(weRoot.getString(E.workEffortId.name()));
            workEffortIdFrom = getWorkEffortRootId();
            workEffortTypeIdFrom = weRoot.getString(E.workEffortTypeId.name());
            workEffortTypeIdRoot = workEffortTypeIdFrom;
            estimatedStartDateFrom = weRoot.getTimestamp(E.estimatedStartDate.name());
            estimatedCompletionDateFrom = weRoot.getTimestamp(E.estimatedCompletionDate.name());
        }

        // extLogicKey = externalValue
        setWeTypeInterfaceHelper(new WeTypeInterfaceHelper(this));

        checkValidityFrom();
        checkValidityWorkEffortTypeTo();
        checkValidityWorkEffortTo();

        checkValidityWorkEffortAssocType();
        if (UtilValidate.isEmpty(workEffortAssocTypeId)) {
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), "The workEffortAssocTypeId must be valid");
        }

        getkWorkEffortAssoc();
        setDates();
    }

    /** Set default fromDate e thruDate from workEffortFrom and workEffortTo,
     * override fromDate and thruDate if exists workeffortAssoc */
    private void setDates() {
        setDatesByWorkEfforts();

        if (UtilValidate.isNotEmpty(getLocalValue())) {
            setFromDate(getLocalValue().getTimestamp(E.fromDate.name()));
            setThruDate(getLocalValue().getTimestamp(E.thruDate.name()));
        }
    }

    private void checkValidityFrom() throws GeneralException {
        GenericValue externalValue = getExternalValue();

        String localWorkEffortTypeId = getExternalValue().getString(E.workEffortTypeIdFrom.name());
        String sourceReferenceIdFrom = externalValue.getString(E.sourceReferenceIdFrom.name());
        String workEffortNameFrom = externalValue.getString(E.workEffortNameFrom.name());

        if (!(ValidationUtil.isEmptyOrNA(localWorkEffortTypeId) && ValidationUtil.isEmptyOrNA(sourceReferenceIdFrom) && ValidationUtil.isEmptyOrNA(workEffortNameFrom))) {
            checkValidityWorkEffortTypeFrom();
            checkValidityWorkEffortFrom();
        }

    }

    private void getkWorkEffortAssoc() throws GeneralException {
        GenericValue localValue = null;

        localValue = weAssocInterfaceHelper.getWorkEffortAssoc(workEffortIdFrom, workEffortIdTo, workEffortAssocTypeId);
        setLocalValue(localValue);
    }

    private void checkValidityWorkEffortAssocType() throws GeneralException {
        GenericValue externalValue = getExternalValue();

        weAssocInterfaceHelper = new WeAssocInterfaceHelper(this, getManager().getDelegator());
        String workEffortAssocTypeCode = externalValue.getString(E.workEffortAssocTypeCode.name());

        if (!ValidationUtil.isEmptyOrNA(workEffortAssocTypeCode)) {
            GenericValue workEffortAssocType = weAssocInterfaceHelper.getWorkEffortAssocType(workEffortAssocTypeCode);
            workEffortAssocTypeId = workEffortAssocType.getString(E.workEffortAssocTypeId.name());
        }
    }

    private void checkValidityWorkEffortTo() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        String sourceReferenceIdFrom = externalValue.getString(E.sourceReferenceIdFrom.name());
        String sourceReferenceIdTo = externalValue.getString(E.sourceReferenceIdTo.name());
        GenericValue weRootTo = doImportWeRoot(sourceReferenceRootIdTo, sourceReferenceIdFrom, sourceReferenceIdTo);
        
        String workEffortParentIdTo = weRootTo.getString(E.workEffortId.name());
        String workEffortNameTo = externalValue.getString(E.workEffortNameTo.name());
        GenericValue workEffort = getWeInterfaceHelper().getValidityWorkEffort(workEffortParentIdTo, sourceReferenceIdTo, getOrganizationId(), workEffortNameTo, workEffortTypeIdTo);
        workEffortIdTo = workEffort.getString(E.workEffortId.name());
        estimatedStartDateTo = workEffort.getTimestamp(E.estimatedStartDate.name());
        estimatedCompletionDateTo = workEffort.getTimestamp(E.estimatedCompletionDate.name());

    }

    private void checkValidityWorkEffortFrom() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        String sourceReferenceIdFrom = externalValue.getString(E.sourceReferenceIdFrom.name());
        String sourceReferenceIdTo = externalValue.getString(E.sourceReferenceIdTo.name());
        GenericValue weRootFrom = doImportWeRoot(sourceReferenceRootIdFrom, sourceReferenceIdFrom, sourceReferenceIdTo);
            
        String workEffortParentIdFrom = weRootFrom.getString(E.workEffortId.name());
        String workEffortNameFrom = externalValue.getString(E.workEffortNameFrom.name());
        GenericValue workEffort =  getWeInterfaceHelper().getValidityWorkEffort(workEffortParentIdFrom, sourceReferenceIdFrom, getOrganizationId(), workEffortNameFrom, workEffortTypeIdFrom);
        workEffortIdFrom = workEffort.getString(E.workEffortId.name());
        estimatedStartDateFrom = workEffort.getTimestamp(E.estimatedStartDate.name());
        estimatedCompletionDateFrom = workEffort.getTimestamp(E.estimatedCompletionDate.name());
        
   }

    private GenericValue doImportWeRoot(String sourceReferenceRootId, String sourceReferenceIdFrom, String sourceReferenceIdTo) throws GeneralException {      
        GenericValue weRoot = null;
        try {
        	weRoot = doImport(ImportManagerConstants.WE_ROOT_INTERFACE, UtilMisc.toMap(E.sourceReferenceRootId.name(), sourceReferenceRootId));
        } catch (GeneralException e) {
            Map<String, Object> parameters = UtilMisc.toMap(E.workEffortCode.name(), (Object) sourceReferenceRootIdFrom, E.sourceReferenceIdFrom.name(), sourceReferenceIdFrom, E.sourceReferenceIdTo.name(), sourceReferenceIdTo, E.exceptionMessage.name(),  e.getMessage());
            JobLogLog noWorkEffortFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_ROOT_FOUND_ASSOC", parameters, getManager().getLocale());
            throw new ImportException(getEntityName(), getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), noWorkEffortFound);
        }
        if (UtilValidate.isEmpty(weRoot)) {
            Map<String, Object> parameters = UtilMisc.toMap(E.workEffortCode.name(), (Object) sourceReferenceRootIdFrom, E.sourceReferenceIdFrom.name(), sourceReferenceIdFrom, E.sourceReferenceIdTo.name(), sourceReferenceIdTo);
            JobLogLog noWorkEffortFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_ROOT_FOUND_ASSOC", parameters, getManager().getLocale());
            throw new ImportException(getEntityName(), getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), noWorkEffortFound);
        
        }
        return weRoot;        
    }

    private void checkValidityWorkEffortTypeFrom() throws GeneralException {
        String localWorkEffortTypeId = getExternalValue().getString(E.workEffortTypeIdFrom.name());
        GenericValue workEffortType = getWeTypeInterfaceHelper().checkValidityWorkEffortType(localWorkEffortTypeId, null, null);
        workEffortTypeIdFrom = workEffortType.getString(E.workEffortTypeId.name());
    }

    private void checkValidityWorkEffortTypeTo() throws GeneralException {
        String localWorkEffortTypeId = getExternalValue().getString(E.workEffortTypeIdTo.name());
        GenericValue workEffortType = getWeTypeInterfaceHelper().checkValidityWorkEffortType(localWorkEffortTypeId, null, null);
        workEffortTypeIdTo = workEffortType.getString(E.workEffortTypeId.name());
    }

    @Override
    /**
     * Esegue importazione record esterno
     */
    public void doImport() throws GeneralException {
    	setImported(true);
        String msg;

        //3.2 Controllo validita' relazione
        checkValidityWorkEffortTypeAndAssocType();

        createOrUpdateWorkEffortAssoc();

        updateWeAssocInterface();

        msg = "END IMPORT " + workEffortAssocTypeId + "[" + workEffortIdFrom + " type " + workEffortTypeIdFrom + " - " + workEffortIdTo + " type " + workEffortTypeIdTo + "]";
        addLogInfo(msg);
    }

    private void checkValiditySourceReferenceRootId() throws ImportException {
        GenericValue externalValue = getExternalValue();

        sourceReferenceRootIdFrom = externalValue.getString(E.sourceReferenceRootIdFrom.name());
        sourceReferenceRootIdTo = externalValue.getString(E.sourceReferenceRootIdTo.name());

        getWeRootInterfaceHelper().checkValiditySourceReferenceRootId(sourceReferenceRootId, sourceReferenceRootIdFrom, sourceReferenceRootIdTo);
    }

    private void checkValidityWorkEffortTypeAndAssocType() throws GeneralException {
        GenericValue externalValue = getExternalValue();

        GenericValue workEffortTypeType = getManager().getDelegator().findOne("WorkEffortTypeType", false, "workEffortTypeIdRoot", workEffortTypeIdRoot, "workEffortTypeIdFrom", workEffortTypeIdFrom, "workEffortTypeIdTo", workEffortTypeIdTo);
        if (UtilValidate.isEmpty(workEffortTypeType)) {

            GenericValue workEffortTypeAssocTo = getManager().getDelegator().findOne(E.WorkEffortTypeAssoc.name(), false, E.wefromWetoEnumId.name(), "WETATO", E.workEffortAssocTypeId.name(), workEffortAssocTypeId, E.workEffortTypeId.name(), workEffortTypeIdTo, E.workEffortTypeIdRef.name(), workEffortTypeIdFrom);
            if (UtilValidate.isEmpty(workEffortTypeAssocTo)) {

                GenericValue workEffortTypeAssocFrom = getManager().getDelegator().findOne(E.WorkEffortTypeAssoc.name(), false, E.wefromWetoEnumId.name(), "WETAFROM", E.workEffortAssocTypeId.name(), workEffortAssocTypeId, E.workEffortTypeId.name(), workEffortTypeIdFrom, E.workEffortTypeIdRef.name(), workEffortTypeIdTo);
                if (UtilValidate.isEmpty(workEffortTypeAssocFrom)) {
                    String msg = "WorkEffortTypeAssoc not valid for " + workEffortTypeIdFrom + " and " + workEffortTypeIdTo;
                    throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
                }
            }
        }

    }

    private void updateWeAssocInterface() {
        getExternalValue().set(E.workEffortAssocTypeId.name(), workEffortAssocTypeId);
        getExternalValue().set(E.workEffortIdFrom.name(), workEffortIdFrom);
        getExternalValue().set(E.workEffortIdTo.name(), workEffortIdTo);
        getExternalValue().set(E.workEffortRootId.name(), getWorkEffortRootId());
        getExternalValue().set(E.elabResult.name(), ImportManager.RECORD_ELAB_RESULT_OK);
    }

    private void createOrUpdateWorkEffortAssoc() throws GeneralException {
        GenericValue we = getLocalValue();

        String msg = "";

        Map<String, Object> result = null;

        Map<String, Object> serviceMapParams = setServiceMapParameters(UtilValidate.isEmpty(we));
        
        if (UtilValidate.isEmpty(we)) {

            serviceMapParams.put(E.workEffortAssocTypeId.name(), workEffortAssocTypeId);
            serviceMapParams.put(E.workEffortIdFrom.name(), workEffortIdFrom);
            serviceMapParams.put(E.workEffortIdTo.name(), workEffortIdTo);
            serviceMapParams.put(E.fromDate.name(), getFromDate());
            serviceMapParams.put(E.thruDate.name(), getThruDate());
            
            serviceMapParams.put(E.executeToClone.name(), E.Y.name());
            serviceMapParams.put(E.relationTitle.name(), E.to.name());
            
            // Creazione WorkEffortRoot
            msg = "Trying to create workEffortAssoc with " + workEffortAssocTypeId + " workEffortIdFrom = " + workEffortIdFrom + " workEffortIdTo = " + workEffortIdTo + " fromDate = " + getFromDate() + " assocWeight = " + assocWeight;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortAssoc.name(), E.WorkEffortAssoc.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffortAssoc.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + E.WorkEffortAssoc.name(), true);

        } else {
            if (UtilValidate.isNotEmpty(serviceMapParams)) {
                serviceMapParams.put(E.workEffortAssocTypeId.name(), workEffortAssocTypeId);
                serviceMapParams.put(E.workEffortIdFrom.name(), workEffortIdFrom);
                serviceMapParams.put(E.workEffortIdTo.name(), workEffortIdTo);
                serviceMapParams.put(E.fromDate.name(), getFromDate());

                serviceMapParams.put(E.executeToClone.name(), E.Y.name());
                serviceMapParams.put(E.relationTitle.name(), E.to.name());
                
                msg = "Trying to update workEffortAssoc with " + workEffortAssocTypeId + " workEffortIdFrom = " + workEffortIdFrom + " workEffortIdTo = " + workEffortIdTo + " fromDate = " + getFromDate() + " assocWeight = " + assocWeight;
                addLogInfo(msg);
                result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortAssoc.name(), E.WorkEffortAssoc.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffortAssoc.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.WorkEffortAssoc.name(), true);
            } else {
                msg = "No update to do ";
                addLogInfo(msg);
            }
        }
        Map<String, Object> key = UtilMisc.toMap(E.workEffortAssocTypeId.name(), workEffortAssocTypeId, E.workEffortIdFrom.name(), workEffortIdFrom, E.workEffortIdTo.name(), workEffortIdTo, E.fromDate.name(), getFromDate());

        manageResult(result, E.WorkEffortAssoc.name(), key);

    }

    /**
     * Set executeToClone = Y and relationTitle = to to force workEffort clone
     * @return
     */
    private Map<String, Object> setServiceMapParameters(boolean isInsert) {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        GenericValue externalValue = getExternalValue();

        if (UtilValidate.isNotEmpty(externalValue.get(E.sequenceNum.name()))) {
            sequenceNum = (Long)externalValue.get(E.sequenceNum.name());
            serviceMapParams.put(E.sequenceNum.name(), sequenceNum);
        } else if(isInsert){
            serviceMapParams.put(E.sequenceNum.name(), 1L);
        }

        if (UtilValidate.isNotEmpty(externalValue.get(E.assocWeight.name()))) {
            assocWeight = (Double)externalValue.get(E.assocWeight.name());
            serviceMapParams.put(E.assocWeight.name(), assocWeight);
        } else if(isInsert){
            serviceMapParams.put(E.assocWeight.name(), DEFAULT_ASSOC_WEIGHT);
        }
        
        serviceMapParams.put(E.comments.name(), externalValue.get(E.comments.name()));
        serviceMapParams.put(E.note.name(), externalValue.get(E.note.name()));

        return serviceMapParams;
    }

    /** Set fromDate e thruDate from max e min between workEffortFrom e workEffortTo, the values will be override from workeffortAssoc if exist */
    private void setDatesByWorkEfforts() {
        setFromDate(getMaxDate());
        setThruDate(getMinDate());
    }

    private Timestamp getMinDate() {
        if (estimatedCompletionDateFrom.before(estimatedCompletionDateTo)) {
            return estimatedCompletionDateFrom;
        }
        return estimatedCompletionDateTo;
    }

    private Timestamp getMaxDate() {
        if (estimatedStartDateFrom.after(estimatedStartDateTo)) {
            return estimatedStartDateFrom;
        }
        return estimatedStartDateTo;
    }

    protected void setWithWeRootInterfaceContext(WeRootInterfaceContext weRootInterfaceContext){
    	workEffortAssocTypeId = weRootInterfaceContext.getWorkEffortAssocTypeId();
    }

    /**
     * Override ordinamento chiavi per i log
     * @parmas msg, log message
     */
    public void addLogInfo(String msg) {
        String msgNew = getEntityName() + ": " + msg;

        Map<String, Object> map = FastMap.newInstance();
        map.put(E.sourceReferenceRootId.name(), getExternalValue().get(E.sourceReferenceRootId.name()));
        map.put(E.workEffortTypeIdFrom.name(), getExternalValue().get(E.workEffortTypeIdFrom.name()));
        map.put(E.workEffortTypeIdTo.name(), getExternalValue().get(E.workEffortTypeIdTo.name()));
        map.put(E.sourceReferenceIdFrom.name(), getExternalValue().get(E.sourceReferenceIdFrom.name()));
        map.put(E.sourceReferenceIdTo.name(), getExternalValue().get(E.sourceReferenceIdTo.name()));
        map.put(E.workEffortAssocTypeCode.name(), getExternalValue().get(E.workEffortAssocTypeCode.name()));
        map.put(E.workEffortNameFrom.name(), getExternalValue().get(E.workEffortNameFrom.name()));
        map.put(E.workEffortNameTo.name(), getExternalValue().get(E.workEffortNameTo.name()));

        getManager().addLogInfo(msgNew, MODULE, map.toString());
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    public Timestamp getThruDate() {
        return thruDate;
    }

    public void setThruDate(Timestamp thruDate) {
        this.thruDate = thruDate;
    }
}
