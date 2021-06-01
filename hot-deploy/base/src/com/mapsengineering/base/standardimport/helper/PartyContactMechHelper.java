package com.mapsengineering.base.standardimport.helper;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Manage Telephon, EmailAddress
 * @author dain
 *
 */
public class PartyContactMechHelper {

    private TakeOverService takeOverService;
    protected static final String MSG_E_MAIL_ADDRESS = "E-Mail address";
    protected static final String MSG_PRIMARY_MAIL = "Primary Email";
    protected static final String MSG_TELECOM_NUMBER = "Telecom number";

    /**
     * Constructor
     * @param takeOverService
     */
    public PartyContactMechHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }

    /**
     * Manage EMAIL_ADDRESS
     * @param partyId
     * @param gv
     * @throws GeneralException
     */
    public void doImportEmail(String partyId, GenericValue gv) throws GeneralException {
        // 1.c Indirizzo E-Mail
        String msgDelete = "Found an email address. Trying to delete it...";
        String msgUpdate = "Found an email address. Trying to update it...";
        String msgCreate = "Trying to create an E-Mail address for party id " + partyId + " address " + gv.getString(E.contactMail.name());
        
        doImport(partyId, E.PRIMARY_EMAIL.name(), E.EMAIL_ADDRESS.name(), E.emailAddress.name(), E.contactMail.name(), E.infoString.name(), gv, MSG_E_MAIL_ADDRESS, msgDelete, msgUpdate, msgCreate, E.updatePartyEmailAddressExt.name());
    }

    /**
     * Call updatePartyContactMechExt for create a PartyContact, contactMech with contactMechTypeId, partyId, fromDate = refDate; <br/>
     * then call crudServiceDefaultOrchestration_PartyContactMechPurpose, with partyId, contactMechPurposeTypeId, contactMechId, purposeFromDate = refDate
     * @param partyId
     * @param contactMechPurposeTypeId
     * @param contactMechTypeId
     * @param entityFieldName
     * @param gvFieldName
     * @param gv
     * @param msgFieldName
     * @throws GeneralException
     */
    private void createPartyContactMechAndPurpose(String partyId, String contactMechPurposeTypeId, String contactMechTypeId, String entityFieldName, String gvFieldName, GenericValue gv, String msgFieldName) throws GeneralException {
        ImportManager manager = takeOverService.getManager();

        Map<String, Object> parametersMap = UtilMisc.toMap(CrudEvents.ENTITY_NAME, E.PartyContactWithPurpose.name(), CrudEvents.OPERATION, CrudEvents.OP_CREATE, E.contactMechTypeId.name(), contactMechTypeId, E.partyId.name(), partyId, entityFieldName, gv.getString(gvFieldName), E.fromDate.name(), gv.getTimestamp(E.refDate.name()), E.thruDate.name(), gv.getTimestamp(E.thruDate.name()), E.userLogin.name(), manager.getUserLogin());
        Map<String, Object> result = takeOverService.runSync(E.updatePartyContactMechExt.name(), parametersMap, msgFieldName + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + msgFieldName, false);
        if(ServiceUtil.isSuccess(result)) {
            parametersMap = UtilMisc.toMap(E.partyId.name(), partyId, E.contactMechPurposeTypeId.name(), contactMechPurposeTypeId, E.contactMechId.name(), result.get(E.contactMechId.name()), E.purposeFromDate.name(), gv.getTimestamp(E.refDate.name()), E.userLogin.name(), manager.getUserLogin());
            takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_PartyContactMechPurpose.name(), E.PartyContactWithPurpose.name(), CrudEvents.OP_CREATE, parametersMap, MSG_PRIMARY_MAIL + FindUtilService.MSG_SUCCESSFULLY_ADD +  msgFieldName, FindUtilService.MSG_ERROR_ADD + MSG_PRIMARY_MAIL + " to " + msgFieldName, false, false);
        }
    }
    
    private void doImport(String partyId, String contactMechPurposeTypeId, String contactMechTypeId, String entityFieldName, String gvFieldName, String contactFieldName, GenericValue gv, String msgFieldName, String msgDelete, String msgUpdate, String msgCreate, String updateServiceName) throws GeneralException {
        ImportManager manager = takeOverService.getManager();
        String msg = msgDelete;
        if (UtilValidate.isNotEmpty(gv.getString(gvFieldName))) {
            List<EntityCondition> condList = FastList.newInstance();
            condList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
            condList.add(EntityCondition.makeCondition(E.contactMechTypeId.name(), contactMechTypeId));
            condList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
            List<GenericValue> contactList = manager.getDelegator().findList(E.PartyNameContactMechView.name(), EntityCondition.makeCondition(condList), null, null, null, false);
            if (UtilValidate.isNotEmpty(contactList)) {
                GenericValue contact = EntityUtil.getFirst(contactList);
                if (UtilValidate.isEmpty(gv.getTimestamp(E.thruDate.name()))) {
                    msg = msgUpdate;
                }
                takeOverService.addLogInfo(msg);

                String value = UtilValidate.isNotEmpty(gv.getString(gvFieldName)) ? gv.getString(gvFieldName) : contact.getString(contactFieldName);
                
                Map<String, Object> parametersMap = UtilMisc.toMap(E.partyId.name(), partyId, E.contactMechId.name(), contact.getString(E.contactMechId.name()), entityFieldName, value, E.thruDate.name(), gv.getTimestamp(E.thruDate.name()), E.userLogin.name(), manager.getUserLogin());
                takeOverService.runSync(updateServiceName, parametersMap, msgFieldName + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + msgFieldName, false);
            } else {
                takeOverService.addLogInfo(msgCreate);
                createPartyContactMechAndPurpose(partyId, contactMechPurposeTypeId, contactMechTypeId, entityFieldName, gvFieldName, gv, msgFieldName);
            }
        }
    }

    /**
     * Manage Telephon
     * @param partyId
     * @param gv
     * @throws GeneralException
     */
    public void doImportTel(String partyId, GenericValue gv) throws GeneralException {
        // 1.d Telefono        
        String msgDelete = "Found a telephon number. Trying to delete it...";
        String msgUpdate = "Found a telephon number. Trying to update it...";
        String msgCreate = "Trying to create a telecom number for party id " + partyId + " number " + gv.getString(E.contactMobile.name());
        doImport(partyId, E.PRIMARY_PHONE.name(), E.TELECOM_NUMBER.name(), E.contactNumber.name(), E.contactMobile.name(), E.contactMechId.name(), gv, MSG_TELECOM_NUMBER, msgDelete, msgUpdate, msgCreate, E.updatePartyTelecomNumber.name());
    }

}
