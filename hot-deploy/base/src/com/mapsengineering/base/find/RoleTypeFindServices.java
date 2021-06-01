package com.mapsengineering.base.find;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Utility for find RoleType 
 */
public class RoleTypeFindServices extends BaseFindServices {
    public static final String MODULE = RoleTypeFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public RoleTypeFindServices(Delegator delegator) {
        super(delegator);
    }

    private GenericValue getRoleType(String orgRoleTypeId) throws GeneralException {
        EntityCondition condition = EntityCondition.makeCondition(E.roleTypeId.name(), orgRoleTypeId);

        String foundMore = "Found more than one role with condition: " + condition;
        String noFound = "No role with condition: " + condition;

        return FindUtilService.findOne(getDelegator(), E.RoleType.name(), condition, foundMore, noFound);
    }

    /**
     * Return RoleType.parentTypeId
     * @param orgRoleTypeId
     * @return
     * @throws GeneralException
     */
    public String getRoleTypeId(String orgRoleTypeId) throws GeneralException {
        return getRoleType(orgRoleTypeId).getString(E.parentTypeId.name());
    }
    
    /**
     * Return PartyRelationshipRole.roleTypeValidTo
     * @param gv
     * @param parentRoleTypeId
     * @param partyId
     * @param orgRoleTypeId
     * @param getEntityName
     * @throws GeneralException
     */
    public String getValidRoleTypeId(String roleTypeValidFrom, String roleTypeValidTo, String noFound, String foundMore) throws GeneralException {
        List<EntityCondition> partyRelationshipRoleCond = FastList.newInstance();
        partyRelationshipRoleCond.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.GROUP_ROLLUP.name()));
        partyRelationshipRoleCond.add(EntityCondition.makeCondition(E.roleTypeValidFrom.name(), roleTypeValidFrom));
        
        if(UtilValidate.isNotEmpty(roleTypeValidTo)) {
            partyRelationshipRoleCond.add(EntityCondition.makeCondition(E.roleTypeValidTo.name(), roleTypeValidTo));
        }
        GenericValue value = getValidRoleType(EntityCondition.makeCondition(partyRelationshipRoleCond), noFound, foundMore);
        return value.getString(E.roleTypeValidTo.name());
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
    private GenericValue getValidRoleType(EntityCondition condition, String noFound, String foundMore) throws GeneralException {
       return FindUtilService.findOne(getDelegator(), E.PartyRelationshipRole.name(), condition, foundMore, noFound);
    }
}
