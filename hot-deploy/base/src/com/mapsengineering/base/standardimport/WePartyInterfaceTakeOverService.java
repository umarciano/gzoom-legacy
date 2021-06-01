package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.WeBaseDateInterfaceTakeOverService;
import com.mapsengineering.base.standardimport.helper.PartyRoleHelper;
import com.mapsengineering.base.standardimport.helper.WePartyInterfaceHelper;
import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * 
 *
 */
public class WePartyInterfaceTakeOverService extends WeBaseDateInterfaceTakeOverService {

    public static final String MODULE = WePartyInterfaceTakeOverService.class.getName();

    private static final Double DEFAULT_ROLE_TYPE_WEIGHT = 100D;

    private WePartyInterfaceHelper wePartyInterfaceHelper;
    private PartyRoleHelper partyRoleHelper;

    private String roleTypeId;
    private String parentTypeId;
    private String partyId;

    private Double roleTypeWeight;
    
    private Timestamp fromDate;
    private Timestamp thruDate;
    
    private Long sequenceNum;

    @Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento. 
     * Recupera tutti i campi da externalValue, record sulla tabella di interfaccia
     * @params extLogicKey chiave logica esterna
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        super.initLocalValue(extLogicKey);
        
        wePartyInterfaceHelper = new WePartyInterfaceHelper(this, getManager().getDelegator());
        partyRoleHelper = new PartyRoleHelper(this);
        
        checkValidityRoleTypeParam();
        checkValidityRoleType();
        checkValidityWorkEffortTypeRole();
        checkValidityPartyParam();
        checkValidityPartyParentRole();
        checkValidityParty();
        checkValidityPartyRole();
        setDates();
        setSequenceNum();

        String msg = " Try workEffortPartyAssignment for id " + getWorkEffortLevelId() + " and fromDate " + fromDate + " and partyId " + partyId + " and roleTypeId " + roleTypeId;
        addLogInfo(msg);

        GenericValue localValue = wePartyInterfaceHelper.getWorkEffortPartyAssignment(getWorkEffortLevelId(), fromDate, roleTypeId, partyId);

        if (UtilValidate.isNotEmpty(localValue)) {
            msg = "Found workEffortPartyAssignment with " + localValue.getPrimaryKey();
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

        updateWePartyInterface();

        createOrUpdateWorkEffortPartyAssignment();

        msg = "END IMPORT WorkEffortPartyAssignment [workEffortId=" + getWorkEffortLevelId() + " fromDate=" + fromDate + " partyId=" +partyId + " roleTypeId=" +roleTypeId + "]";
        addLogInfo(msg);
    }

    private void updateWePartyInterface() {
        getExternalValue().set(E.workEffortId.name(), getWorkEffortLevelId());
        getExternalValue().set(E.workEffortRootId.name(), getWorkEffortRootId());
        getExternalValue().set(E.partyId.name(), partyId);
        getExternalValue().set(E.roleTypeIdOut.name(), roleTypeId);
        getExternalValue().set(E.elabResult.name(), ImportManager.RECORD_ELAB_RESULT_OK);
    }

    private void createOrUpdateWorkEffortPartyAssignment() throws GeneralException {
        GenericValue we = getLocalValue();
        GenericValue externalValue = getExternalValue();

        String msg = "";

        Map<String, Object> serviceMapParams = setServiceMapParameters();
        Map<String, Object> result = null;
        if (UtilValidate.isEmpty(we)) {
            if (UtilValidate.isNotEmpty(externalValue.get(E.roleTypeWeight.name()))) {
                roleTypeWeight = (Double)externalValue.get(E.roleTypeWeight.name());
            } else {
                roleTypeWeight = DEFAULT_ROLE_TYPE_WEIGHT;
            }
            serviceMapParams.put(E.roleTypeWeight.name(), roleTypeWeight);

            // Creazione WorkEffort
            msg = "Trying to create WorkEffortPartyAssignment with id " + getWorkEffortLevelId() + " partyId " + partyId + " roleTypeId " + roleTypeId;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortPartyAssignment.name(), E.WorkEffortPartyAssignment.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffortPartyAssignment.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortPartyAssignment.name(), true);
        } else {
            if (UtilValidate.isNotEmpty(externalValue.get(E.roleTypeWeight.name()))) {
                serviceMapParams.put(E.roleTypeWeight.name(), (Double)externalValue.get(E.roleTypeWeight.name()));
            }

            msg = "Trying to update WorkEffort with id " + getWorkEffortLevelId() + " partyId " + partyId + " roleTypeId " + roleTypeId;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortPartyAssignment.name(), E.WorkEffortPartyAssignment.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffortPartyAssignment.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.WorkEffortPartyAssignment.name(), true);
        }
        Map<String, Object> keys = UtilMisc.toMap(E.workEffortId.name(), getWorkEffortLevelId(), E.partyId.name(), partyId, E.roleTypeId.name(), roleTypeId, E.fromDate.name(), fromDate);
        manageResult(result, E.WorkEffortPartyAssignment.name(), keys);
    }

    private Map<String, Object> setServiceMapParameters() {
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.workEffortId.name(), (Object)getWorkEffortLevelId());
        serviceMapParams.put(E.partyId.name(), partyId);
        serviceMapParams.put(E.roleTypeId.name(), roleTypeId);

        GenericValue externalValue = getExternalValue();
        String comments = externalValue.getString(E.comments.name());
        if (UtilValidate.isNotEmpty(comments)) {
            serviceMapParams.put(E.comments.name(), comments);
        }
        
        serviceMapParams.put(E.fromDate.name(), fromDate);
        serviceMapParams.put(E.thruDate.name(), thruDate);
        serviceMapParams.put(E.sequenceNum.name(), sequenceNum);

        return serviceMapParams;
    }

    private void checkValidityPartyParam() throws ImportException {
        GenericValue externalValue = getExternalValue();
        if (ValidationUtil.isEmptyOrNA(externalValue.getString(E.partyCode.name())) && ValidationUtil.isEmptyOrNA(externalValue.getString(E.partyName.name()))) {
            String msg = "The field partyCode or partyName must be not empty";
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private void checkValidityRoleTypeParam() throws ImportException {
        GenericValue externalValue = getExternalValue();
        if (ValidationUtil.isEmptyOrNA(externalValue.getString(E.roleTypeId.name())) && ValidationUtil.isEmptyOrNA(externalValue.getString(E.roleTypeDesc.name()))) {
            String msg = "The field roleTypeId or roleTypeDesc must be not empty";
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    private void checkValidityPartyRole() throws GeneralException {
    	partyRoleHelper.checkValidityPartyRole(partyId, roleTypeId, null, getSourceReferenceId());
    }

    private void checkValidityPartyParentRole() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        String partyCode = externalValue.getString(E.partyCode.name());
        if (!ValidationUtil.isEmptyOrNA(partyCode)) {
            partyId = partyRoleHelper.checkValidityPartyParentRole(partyCode, parentTypeId, getSourceReferenceId());
        }
    }

    private void checkValidityWorkEffortTypeRole() throws GeneralException {
        if (!E.WE_ASSIGNMENT.name().equals(roleTypeId)) {
            getWeTypeInterfaceHelper().checkValidityWorkEffortTypeRole(roleTypeId, getWorkEffortTypeLevelId());
        }
    }

    private void checkValidityParty() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        if (!ValidationUtil.isEmptyOrNA(externalValue.getString(E.partyName.name()))) {
            partyId = wePartyInterfaceHelper.checkValidityParty(externalValue.getString(E.partyName.name()), partyId);
        }
    }

    private void checkValidityRoleType() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        List<EntityCondition> listaCondition = FastList.newInstance();
        List<EntityCondition> listaConditionType = FastList.newInstance();
        if (!ValidationUtil.isEmptyOrNA(externalValue.getString(E.roleTypeId.name()))) {
            EntityCondition conditionId = EntityCondition.makeCondition(E.roleTypeId.name(), externalValue.getString(E.roleTypeId.name()));
            listaConditionType.add(conditionId);
            // foundMore += " roleTypeId = ".concat(roleTypeId);
            // noFound += " roleTypeId = ".concat(roleTypeId);
        }

        if (!ValidationUtil.isEmptyOrNA(externalValue.getString(E.roleTypeDesc.name()))) {
            EntityCondition conditionDesc = EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.roleTypeDesc.name()));
            listaConditionType.add(conditionDesc);
            // foundMore += " description = ".concat(roleTypeDesc);
            // noFound += " description = ".concat(roleTypeDesc);
        }

        listaCondition.add(EntityCondition.makeCondition(listaConditionType, EntityJoinOperator.OR));

        EntityCondition condition = EntityCondition.makeCondition(listaCondition);

        String foundMore = "Found more than one orgType with condition :" + condition;
        String noFound = "No orgType with condition :" + condition;

        GenericValue roleType = findOne(E.RoleType.name(), condition, foundMore, noFound, getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID));
        roleTypeId = roleType.getString(E.roleTypeId.name());
        parentTypeId = roleType.getString(E.parentTypeId.name());
    }
    
    private void setDates() {
    	GenericValue externalValue = getExternalValue();
    	WeRootInterfaceContext weRootInterfaceContext = WeRootInterfaceContext.get(getManager());
    	fromDate = UtilValidate.isNotEmpty(externalValue.getTimestamp(E.fromDate.name())) ? 
    			   externalValue.getTimestamp(E.fromDate.name()) : 
    			   weRootInterfaceContext.getEstimatedStartDate();
        thruDate = UtilValidate.isNotEmpty(externalValue.getTimestamp(E.thruDate.name())) ? 
    		       externalValue.getTimestamp(E.thruDate.name()) : 
    		       weRootInterfaceContext.getEstimatedCompletionDate();
    }
    
    private void setSequenceNum(){
    	if (UtilValidate.isNotEmpty(getExternalValue().get(E.sequenceNum.name()))){
    		sequenceNum = getExternalValue().getLong((E.sequenceNum.name()));
    	}
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
        map.put(E.partyCode.name(), getExternalValue().get(E.partyCode.name()));
        map.put(E.roleTypeId.name(), getExternalValue().get(E.roleTypeId.name()));
        map.put(E.workEffortName.name(), getExternalValue().get(E.workEffortName.name()));
        map.put(E.partyName.name(), getExternalValue().get(E.partyName.name()));
        map.put(E.roleTypeDesc.name(), getExternalValue().get(E.roleTypeDesc.name()));

        getManager().addLogInfo(msgNew, MODULE, map.toString());
    }
}
