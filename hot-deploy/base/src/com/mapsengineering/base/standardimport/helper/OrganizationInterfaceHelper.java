package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
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
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.find.PartyRoleFindServices;
import com.mapsengineering.base.find.RoleTypeFindServices;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Helper for PartyRelationshipRole and PartyRelationship
 *
 */
public class OrganizationInterfaceHelper {

    private TakeOverService takeOverService;
    private Delegator delegetor;
    private RoleTypeFindServices roleTypeFindServices;
    private PartyRoleFindServices partyRoleTypeFindServices;

    /**
     * Constructor
     * @param takeOverService
     * @param delegetor
     */
    public OrganizationInterfaceHelper(TakeOverService takeOverService, Delegator delegetor) {
        this.takeOverService = takeOverService;
        this.delegetor = delegetor;
        this.roleTypeFindServices = new RoleTypeFindServices(takeOverService.getManager().getDelegator());
        this.partyRoleTypeFindServices = new PartyRoleFindServices(takeOverService.getManager().getDelegator());
    }

    /** set thruDate for different parent relation
     * @param notPartyIdFrom = id or null */
    public void setParentRelationshipThruDate(String partyRelationshipTypeId, String notPartyIdFrom, String partyIdTo, String notRoleTypeIdTo, Timestamp thruDate) throws GeneralException {

        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
        condList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        condList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyIdTo));

        List<EntityCondition> orCondList = FastList.newInstance();
        orCondList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), EntityOperator.NOT_EQUAL, notPartyIdFrom));
        orCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), EntityOperator.NOT_EQUAL, notRoleTypeIdTo));

        condList.add(EntityCondition.makeCondition(orCondList, EntityJoinOperator.OR));

        List<GenericValue> partyRelList = delegetor.findList(E.PartyRelationship.name(), EntityCondition.makeCondition(condList), null, null, null, false);
        String msg = "Found " + partyRelList.size() + " for condition " + EntityCondition.makeCondition(condList) + " to close";
        takeOverService.addLogInfo(msg);
        for (GenericValue relation : partyRelList) {
            Map<String, Object> parametersMap = UtilMisc.toMap(E.partyIdFrom.name(), relation.getString(E.partyIdFrom.name()), E.roleTypeIdFrom.name(), relation.getString(E.roleTypeIdFrom.name()), E.partyIdTo.name(), relation.getString(E.partyIdTo.name()), E.roleTypeIdTo.name(), relation.getString(E.roleTypeIdTo.name()), E.partyRelationshipTypeId.name(), relation.getString(E.partyRelationshipTypeId.name()), E.fromDate.name(), relation.getTimestamp(E.fromDate.name()), E.thruDate.name(), thruDate);
            takeOverService.runSyncCrudWarning(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_UPDATE, parametersMap, E.PartyRelationship.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.PartyRelationship.name());
        }
    }

    /**
     * Check valid roleTypeId
     * @throws ImportException
     */
    public void checkRoleTypeId(String roleTypeIdTmp, String id) throws GeneralException {
        if (UtilValidate.isNotEmpty(roleTypeIdTmp)) {
            try {
                roleTypeFindServices.getRoleTypeId(roleTypeIdTmp);
                return;
            } catch (GeneralException e) {
                throw new ImportException(takeOverService.getEntityName(), id, e.getMessage());
            }
        }
        String msg = "RoleType MUST be not empty";
        throw new ImportException(takeOverService.getEntityName(), id, msg);
    }

    /**
     * 
     * @return
     * @throws GeneralException
     */
    public String checkRoleType(String partyId, String orgRoleTypeId, String parentRoleTypeId) throws GeneralException {
        if (UtilValidate.isEmpty(orgRoleTypeId)) {
            return checkRoleTypeEmpty(partyId, parentRoleTypeId);
        }
        return checkRoleTypeNotEmpty(orgRoleTypeId, parentRoleTypeId);
    }

    /**
     * 
     * @param gv
     * @param parentRoleTypeId
     * @param partyId
     * @param orgRoleTypeId
     * @param getEntityName
     * @throws GeneralException
     */
    private String checkRoleTypeEmpty(String partyId, String parentRoleTypeId) throws GeneralException {
        String msg = "Trying to find the organization role for " + partyId + " in PartyRelationshipRole for relationship GROUP_ROLLUP and role type " + parentRoleTypeId;
        takeOverService.addLogInfo(msg);
        String noFound = "Error: Can not find the role to give at this organization unit " + partyId + ": role type for parent: " + parentRoleTypeId + " relationship GROUP_ROLLUP";
        String foundMore = "Error: can not find an unique role to give at " + partyId;

        return roleTypeFindServices.getValidRoleTypeId(parentRoleTypeId, null, noFound, foundMore);
    }

    /**
     * 
     * @param gv
     * @param parentRoleTypeId
     * @param orgRoleTypeId
     * @param getEntityName
     * @throws GeneralException
     */
    private String checkRoleTypeNotEmpty(String roleTypeId, String parentRoleTypeId) throws GeneralException {
        String msg = "Doing preliminary check with permitted roles in Organization Unit relationship...";
        takeOverService.addLogInfo(msg);
        String noFound = "... Organization Unit doesn't allows relationship between roles " + parentRoleTypeId + " and " + roleTypeId;
        String foundMore = "Error: can not find an unique role to give at " + parentRoleTypeId + " and " + roleTypeId;

        String orgRoleTypeId = roleTypeFindServices.getValidRoleTypeId(parentRoleTypeId, roleTypeId, noFound, foundMore);
        msg = "...Check OK, the given role " + orgRoleTypeId + " is permitted";
        takeOverService.addLogInfo(msg);

        return orgRoleTypeId;

    }

    /**
     * Invoca servizio partyRoleTypeFindServices.getPartyRoleTypeId
     * @param gv
     * @param parentOrgId id
     * @param parentRoleTypeId
     * @param getEntityName
     * @throws GeneralException
     */
    public String checkParentRole(String parentOrgId, String parentRoleTypeId) throws GeneralException {
        String partyRoleParentRoleTypeId = E.ORGANIZATION_ROOT.name().equals(parentOrgId) ? E.PARENT_ROOT.name() : E.ORGANIZATION_UNIT.name();

        String foundMore = "The Parent Organization Unit " + parentOrgId + " has many roles of type " + partyRoleParentRoleTypeId;
        String nofound = "Parent Organization Unit " + parentOrgId + " Not found.";
        if (UtilValidate.isNotEmpty(parentRoleTypeId)) {
            nofound = "Error: the given role type " + parentRoleTypeId + " for parent organization " + parentOrgId + " is not valid";
        }
        
        return partyRoleTypeFindServices.getPartyRoleTypeId(parentOrgId, parentRoleTypeId, partyRoleParentRoleTypeId, nofound, foundMore);
    }

}
