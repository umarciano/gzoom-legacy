package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;

/**
 * Helper for GlAccountInterface 
 *
 */
public class GlAccountInterfaceHelper {

    private TakeOverService takeOverService;
    private LocalDispatcher dispatcher;
    private Delegator delegator;

    /**
     * Constructor
     * @param takeOverService
     * @param dispatcher
     * @param delegator
     */
    public GlAccountInterfaceHelper(TakeOverService takeOverService, LocalDispatcher dispatcher, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.dispatcher = dispatcher;
        this.delegator = delegator;
    }

    private void updateGlAccountRole(String glAccountId, String partyId, String roleTypeId, Timestamp fromDate, Timestamp thruDate) throws GeneralException {
        Map<String, Object> keysGlAccountRoleParams = new HashMap<String, Object>();

        Map<String, Object> serviceMapGlAccountRoleParams = new HashMap<String, Object>();
        keysGlAccountRoleParams.put(E.glAccountId.name(), glAccountId);
        keysGlAccountRoleParams.put(E.partyId.name(), partyId);
        keysGlAccountRoleParams.put(E.roleTypeId.name(), roleTypeId);
        keysGlAccountRoleParams.put(E.fromDate.name(), fromDate);

        serviceMapGlAccountRoleParams.putAll(keysGlAccountRoleParams);
        serviceMapGlAccountRoleParams.put(E.thruDate.name(), thruDate);

        // Update
        String msg = "Trying to update glAccountRole for " + glAccountId;
        takeOverService.addLogInfo(msg);

        Map<String, Object> serviceMap = takeOverService.baseCrudInterface("GlAccountRole", "UPDATE", serviceMapGlAccountRoleParams);
        Map<String, Object> res = dispatcher.runSync(E.crudServiceDefaultOrchestration.name(), serviceMap);
        msg = ServiceUtil.getErrorMessage(res);
        if (UtilValidate.isEmpty(msg)) {
            msg = "GlAccountRole successfully update";
            takeOverService.addLogInfo(msg);
        } else {
            msg = "Error in GlAccountRole update " + msg;
            takeOverService.addLogError(msg);
        }
    }

    private void updateGlAccountRoleEmpty(GenericValue glAccountRole, Timestamp refDate) throws GeneralException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(refDate);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date td = cal.getTime();
        Timestamp thruDate = new Timestamp(td.getTime());

        if (UtilValidate.isNotEmpty(glAccountRole.get(E.thruDate.name())) && refDate.before((Timestamp)glAccountRole.get(E.thruDate.name()))) {
            // update thruDate
            updateGlAccountRole((String)glAccountRole.get(E.glAccountId.name()), (String)glAccountRole.get(E.partyId.name()), (String)glAccountRole.get(E.roleTypeId.name()), (Timestamp)glAccountRole.get(E.fromDate.name()), thruDate);
        } else if (UtilValidate.isEmpty(glAccountRole.get(E.thruDate.name()))) {
            // update thruDate
            updateGlAccountRole((String)glAccountRole.get(E.glAccountId.name()), (String)glAccountRole.get(E.partyId.name()), (String)glAccountRole.get(E.roleTypeId.name()), (Timestamp)glAccountRole.get(E.fromDate.name()), thruDate);
        }
        // create
    }

    private boolean setGlAccountRoleEmpty(List<GenericValue> glAccountRoles, String partyIdLocal, String roleTypeIdLocal, String glAccountIdLocal, Timestamp refDate) throws GeneralException {
        boolean found = true;

        for (GenericValue glAccountRole : glAccountRoles) {
            if (!partyIdLocal.equals(glAccountRole.get(E.partyId.name())) || !roleTypeIdLocal.equals(glAccountRole.get(E.roleTypeId.name()))) {
                found = false;
                updateGlAccountRoleEmpty(glAccountRole, refDate);
            } else {
                found = true;
                if (UtilValidate.isNotEmpty(glAccountRole.get(E.thruDate.name())) && refDate.after((Timestamp)glAccountRole.get(E.thruDate.name()))) {
                    // update thruDate
                    updateGlAccountRole(glAccountIdLocal, partyIdLocal, roleTypeIdLocal, (Timestamp)glAccountRole.get(E.fromDate.name()), null);
                }
            }
        }
        return found;
    }

    private void createGlAccountRole(String id, Map<String, Object> keysGlAccountRoleParams, String glAccountIdLocal, String getEntityName) throws GeneralException {
        // create
        String msg = "";

        Map<String, Object> serviceMapGlAccountRoleParams = new HashMap<String, Object>();
        serviceMapGlAccountRoleParams.putAll(keysGlAccountRoleParams);

        msg = "Trying to create glAccountRole " + glAccountIdLocal;
        takeOverService.addLogInfo(msg);

        Map<String, Object> serviceMap = takeOverService.baseCrudInterface("GlAccountRole", CrudEvents.OP_CREATE, serviceMapGlAccountRoleParams);
        Map<String, Object> res = dispatcher.runSync(E.crudServiceDefaultOrchestration.name(), serviceMap);
        msg = ServiceUtil.getErrorMessage(res);
        if (UtilValidate.isEmpty(msg)) {
            msg = "GlAccountRole successfully created";
            takeOverService.addLogInfo(msg);
        } else {
            msg = "Error in glAccountRole creation for " + msg;
            throw new ImportException(getEntityName, id, msg);
        }

    }

    /**
     * Check if exists one GlAccountRole and create if not exists
     * @param id
     * @param keysGlAccountRoleParams
     * @param getEntityName
     * @throws GeneralException
     */
    public void setGlAccountRole(String id, Map<String, Object> keysGlAccountRoleParams, String getEntityName) throws GeneralException {

        String glAccountIdLocal = (String)keysGlAccountRoleParams.get(E.glAccountId.name());
        String partyIdLocal = (String)keysGlAccountRoleParams.get(E.partyId.name());
        String roleTypeIdLocal = (String)keysGlAccountRoleParams.get(E.roleTypeId.name());
        Timestamp refDate = (Timestamp)keysGlAccountRoleParams.get(E.fromDate.name());

        boolean found = true;

        List<GenericValue> glAccountRoles = this.delegator.findList("GlAccountRole", EntityCondition.makeCondition(E.glAccountId.name(), keysGlAccountRoleParams.get(E.glAccountId.name())), null, null, null, false);
        if (UtilValidate.isEmpty(glAccountRoles) || glAccountRoles.size() == 0) {
            found = false;
            // create
        } else {
            found = setGlAccountRoleEmpty(glAccountRoles, partyIdLocal, roleTypeIdLocal, glAccountIdLocal, refDate);
        }
        if (!found) {
            createGlAccountRole(id, keysGlAccountRoleParams, glAccountIdLocal, getEntityName);
        }
    }

    /**
     * Update WorkEffortPurposeAccount
     * @param id
     * @param glAccountId2
     * @param glAccountId
     * @param getEntityName
     * @throws GeneralException
     */
    private void updateWorkEffortPurposeAccount(String id, String glAccountId, String workEffortPurposeTypeId, String getEntityName) throws GeneralException {
        // Update wePurposeAccount
        String msg = "";
        msg = "Trying to update WorkEffortPurposeAccount for " + glAccountId;
        takeOverService.addLogInfo(msg);

        Map<String, String> serviceMapWorkEffortPurposeAccountParams = new HashMap<String, String>();
        serviceMapWorkEffortPurposeAccountParams.put(E.glAccountId.name(), glAccountId);
        serviceMapWorkEffortPurposeAccountParams.put(E.workEffortPurposeTypeId.name(), workEffortPurposeTypeId);

        Map<String, Object> serviceMap = takeOverService.baseCrudInterface(E.WorkEffortPurposeAccount.name(), "UPDATE", serviceMapWorkEffortPurposeAccountParams);
        Map<String, Object> res = this.dispatcher.runSync(E.crudServiceDefaultOrchestration.name(), serviceMap);
        msg = ServiceUtil.getErrorMessage(res);
        if (UtilValidate.isEmpty(msg)) {
            msg = "WorkEffortPurposeAccount successfully update";
            takeOverService.addLogInfo(msg);
        } else {
            msg = "Error in WorkEffortPurposeAccount update " + msg;
            throw new ImportException(getEntityName, id, msg);
        }
    }

    /**
     * Create WorkEffortPurposeAccount
     * @param id
     * @param glAccountId
     * @param glAccountId
     * @param getEntityName
     * @throws GeneralException
     */
    private void createWorkEffortPurposeAccount(String id, String glAccountId, String workEffortPurposeTypeId, String getEntityName) throws GeneralException {

        // Create wePurposeAccount
        String msg = "";
        msg = "Trying to create WorkEffortPurposeAccount for " + glAccountId;
        takeOverService.addLogInfo(msg);

        Map<String, String> serviceMapWorkEffortPurposeAccountParams = new HashMap<String, String>();
        serviceMapWorkEffortPurposeAccountParams.put(E.glAccountId.name(), glAccountId);
        serviceMapWorkEffortPurposeAccountParams.put(E.workEffortPurposeTypeId.name(), workEffortPurposeTypeId);

        Map<String, Object> serviceMap = takeOverService.baseCrudInterface(E.WorkEffortPurposeAccount.name(), CrudEvents.OP_CREATE, serviceMapWorkEffortPurposeAccountParams);
        Map<String, Object> res = this.dispatcher.runSync(E.crudServiceDefaultOrchestration.name(), serviceMap);
        msg = ServiceUtil.getErrorMessage(res);
        if (UtilValidate.isEmpty(msg)) {
            msg = "WorkEffortPurposeAccount successfully created";
            takeOverService.addLogInfo(msg);
        } else {
            msg = "Error in WorkEffortPurposeAccount creation " + msg;
            throw new ImportException(getEntityName, id, msg);
        }
    }

    /**
     * Check if exists WorkEffortPurposeAccount, create or update it.
     * @param id
     * @param keysWorkEffortPurposeAccountParams
     * @param glAccountId
     * @param getEntityName
     * @throws GeneralException
     */
    public void setWorkEffortPurposeAccount(String id, String glAccountId, String workEffortPurposeTypeId, String getEntityName) throws GeneralException {
        // Fa ricerca per glAccountId:
        // - se non esistea, crea
        // - se esiste solo 1, aggiorna
        // - se esiste piu' di una e quella nuova non c'e', viene aggiunta
        List<GenericValue> wePurposeAccountList = this.delegator.findList(E.WorkEffortPurposeAccount.name(), EntityCondition.makeCondition(E.glAccountId.name(), glAccountId), null, null, null, false);

        if (UtilValidate.isEmpty(wePurposeAccountList)) {
            createWorkEffortPurposeAccount(id, glAccountId, workEffortPurposeTypeId, getEntityName);
        } else if (wePurposeAccountList.size() == 1){
            updateWorkEffortPurposeAccount(id, glAccountId, workEffortPurposeTypeId, getEntityName);
        } else {
            List<String> wePurposeAccountIdList = EntityUtil.getFieldListFromEntityList(wePurposeAccountList, E.workEffortPurposeTypeId.name(), true);
            if (!wePurposeAccountIdList.contains(workEffortPurposeTypeId)) {
                createWorkEffortPurposeAccount(id, glAccountId, workEffortPurposeTypeId, getEntityName);
            }
        }

    }

    /**
     * controllo coerenza di accountTypeEnumId in caso di aggiornamento
     * @param localValue
     * @param accountTypeEnumId
     * @param entityName
     * @param id
     * @throws GeneralException
     */
    public void checkAccountTypeEnumId(GenericValue localValue, String accountTypeEnumId, String entityName, String id) throws GeneralException {
        if (!accountTypeEnumId.equals(localValue.getString(E.accountTypeEnumId.name()))) {
            String msg = "accountTypeEnumId " + accountTypeEnumId + " for accountCode " + localValue.getString(E.accountCode.name()) + " " + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(entityName, id, msg);
        }
    }
}
