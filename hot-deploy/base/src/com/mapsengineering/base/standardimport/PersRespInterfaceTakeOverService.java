package com.mapsengineering.base.standardimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.PersonTypeEnum;
import com.mapsengineering.base.standardimport.enumeration.PersRespInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.enumeration.PersonInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleanConditionsBuilder;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleaner;
import com.mapsengineering.base.standardimport.util.PartyRelationshipUtil;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Import Person
 */
public class PersRespInterfaceTakeOverService extends AbstractPartyTakeOverService {

    public static final String MODULE = PersRespInterfaceTakeOverService.class.getName();

    protected static final BigDecimal K_100 = new BigDecimal(100);
    private static final String AND_SEP = " and ";

    private String partyId;
    private PartyRelationshipUtil partyRelationshipUtil;
    private PartyRelationshipCleaner partyRelationshipCleaner;

    /** Store thruDate of previous elaboration */
    private Map<String, Timestamp> tmpThruDate;

    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        partyRelationshipUtil = new PartyRelationshipUtil(this);
        partyRelationshipCleaner = new PartyRelationshipCleaner(this);

        tmpThruDate = new HashMap<String, Timestamp>();
        partyId = super.initLocalValuePartyParentRole((String)extLogicKey.get(E.personCode.name()), E.EMPLOYEE.name());
    }

    @Override
    public void doImport() throws GeneralException {
    	setImported(true);
        GenericValue gv = getExternalValue();
        String msg = "Elaborating party with code " + gv.getString(PersonInterfaceFieldEnum.personCode.name());
        addLogInfo(msg);
        if (checkRefDate(getExternalValue())) {
            msg = REF_DATE_AFTER_NOW + getEntityName() + " with " + getManager().toString(getExternalValue().getPrimaryKey());
            throw new ImportException(getEntityName(), getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        doImportOtherParties();
        doImportExpiredRelations(partyId);

        msg = "END IMPORT " + partyId;
        addLogInfo(msg);
    }

    private void doImportOtherParties() throws GeneralException {
        // importa il valutatore
        doImportEvaluatorApprover(PersonTypeEnum.WEM_EVAL_MANAGER);
        // importa l approvatore
        doImportEvaluatorApprover(PersonTypeEnum.WEM_EVAL_APPROVER);
    }

    /**
     * importa il valutatore/approvatore
     * @param type
     * @throws GeneralException
     */
    protected void doImportEvaluatorApprover(PersonTypeEnum type) throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();

        // 3.d Valutatore/ Approvatore
        if (UtilValidate.isNotEmpty(gv.getString(type.interfaceFieldName()))) {
            addLogInfo("Trying to import " + type.description() + " " + gv.getString(type.interfaceFieldName()));
            GenericValue party = getEvaluatorApprover(gv, type);
            if (UtilValidate.isNotEmpty(party)) {
                GenericValue role = manager.getDelegator().findOne(E.PartyRole.name(), UtilMisc.toMap(E.partyId.name(), party.getString(E.partyId.name()), E.roleTypeId.name(), type.roleTypeId()), false);
                if (UtilValidate.isEmpty(role)) {
                    Map<String, Object> parameters = UtilMisc.toMap(PersRespInterfaceFieldEnum.personCode.name(), gv.getString(PersRespInterfaceFieldEnum.personCode.name()), "person", gv.getString(type.interfaceFieldName()), "typeDescription", (Object)type.description());
                    JobLogLog noPersFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_" + type.getCode() + "_R_FOUND", parameters, getManager().getLocale());
                    addLogWarning(noPersFound);
                } else {
                    String partyIdTo = party.getString(E.partyId.name());

                    PartyRelationshipCleanConditionsBuilder evaluatorsAndApproversConditionsBuilder = new PartyRelationshipCleanConditionsBuilder(type.partyRelationshipTypeId(), getEvaluatorFromDate(gv), partyId, null, null, partyIdTo, null, E.GOAL05.name(), null, null);

                    // controllo correzione anomalie
                    tmpThruDate = partyRelationshipCleaner.cleanFromRelationships(evaluatorsAndApproversConditionsBuilder, E.WEM_EVAL_IN_CHARGE.name(), true, true);
                    
                    partyRelationshipUtil.controlloSoggettoUnicoEvaluatorApprover(evaluatorsAndApproversConditionsBuilder, E.WEM_EVAL_IN_CHARGE.name(), gv.getTimestamp(PersRespInterfaceFieldEnum.thruDate.name()));

                    String successMsg = type.partyRelationshipTypeId() + " relationship for party id " + partyId + AND_SEP + type.description() + " manager id " + partyIdTo + FindUtilService.MSG_SUCCESSFULLY_CREATED;
                    String errorMsg = "Error in create PartyRelationship " + type.partyRelationshipTypeId() + " between " + partyId + AND_SEP + party.getString(E.partyId.name());
                    Map<String, Object> serviceMap = UtilMisc.toMap(E.partyIdFrom.name(), partyId, E.roleTypeIdFrom.name(), E.WEM_EVAL_IN_CHARGE.name(), E.partyRelationshipTypeId.name(), type.partyRelationshipTypeId(), E.partyIdTo.name(), partyIdTo, E.roleTypeIdTo.name(), type.roleTypeId(), E.fromDate.name(), evaluatorsAndApproversConditionsBuilder.getFromDate());
                    partyRelationshipUtil.controlloUnicaRelazione(type.partyRelationshipTypeId(), partyId, E.WEM_EVAL_IN_CHARGE.name(), partyIdTo, type.roleTypeId(), serviceMap, successMsg, errorMsg, true, tmpThruDate);
                }
            }

        }
    }

    /**
     * esegue import di valutatore/approvatore se diverso da se stesso
     * @param string 
     * @param type 
     * @param interfaceFieldName
     * @return
     * @throws GeneralException
     */
    private GenericValue getEvaluatorApprover(GenericValue gv, PersonTypeEnum type) throws GeneralException {
        Map<String, Object> parameters = UtilMisc.toMap(PersRespInterfaceFieldEnum.personCode.name(), gv.getString(PersRespInterfaceFieldEnum.personCode.name()), "person", gv.getString(type.interfaceFieldName()), "typeDescription", (Object)type.description());
        JobLogLog noOtherPersonCodeFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_PERS_FOUND", parameters, getManager().getLocale());
        JobLogLog foundMoreOtherPersonCode = new JobLogLog().initLogCode("StandardImportUiLabels", "FOUND_MORE" + type.getCode(), parameters, getManager().getLocale());

        String organizationId = (String) getManager().getContext().get(E.defaultOrganizationPartyId.name());
        List<EntityCondition> partyParentRoleCondList = new ArrayList<EntityCondition>();
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.parentRoleCode.name(), gv.getString(type.interfaceFieldName())));
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.EMPLOYEE.name()));
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.organizationId.name(), organizationId));
        return findOneWarning(E.PartyParentRole.name(), EntityCondition.makeCondition(partyParentRoleCondList), foundMoreOtherPersonCode, noOtherPersonCodeFound);
    }

    private Timestamp getEvaluatorFromDate(GenericValue gv) {
        if (UtilValidate.isNotEmpty(gv.getTimestamp(PersRespInterfaceFieldEnum.evaluatorFromDate.name()))) {
            return gv.getTimestamp(PersRespInterfaceFieldEnum.evaluatorFromDate.name());
        }
        return gv.getTimestamp(PersRespInterfaceFieldEnum.refDate.name());
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }
}
