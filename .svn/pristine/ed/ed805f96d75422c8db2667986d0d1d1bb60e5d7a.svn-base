package com.mapsengineering.base.find;

import java.util.List;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;

/**
 * Utility for find Party 
 */
public class PartyFindServices extends BaseFindServices {
    public static final String MODULE = PartyFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public PartyFindServices(Delegator delegator) {
        super(delegator);
    }

    /**
     * Get partyId from parentRoleCode and roleTypeId
     * @param parentRoleCode
     * @param roleTypeId
     * @return
     * @throws GeneralException
     */
    public String getPartyId(String parentRoleCode, String roleTypeId) throws GeneralException {
        return getPartyId(parentRoleCode, EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
    }
    
    /**
     * Get partyId from parentRoleCode and roleTypeId
     * @param parentRoleCode
     * @param roleTypeId
     * @return
     * @throws GeneralException
     */
    public String getMatricola(String partyId, String roleTypeId) throws GeneralException {
        return getMatricola(partyId, EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
    }
    
    /**
     * Get partyId from parentRoleCode and roleTypeId
     * @param parentRoleCode
     * @param roleTypeCondition
     * @return
     * @throws GeneralException
     */
    public String getPartyId(String parentRoleCode, EntityCondition roleTypeCondition) throws GeneralException {
        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition(E.parentRoleCode.name(), parentRoleCode), roleTypeCondition);

        List<GenericValue> partyList = getDelegator().findList(E.PartyParentRole.name(), condition, null, null, null, false);
        throwFoundMore(1, true, "Found more than one party with condition :" + condition);
        
        GenericValue partyValue = EntityUtil.getFirst(partyList);
        throwNoFound(partyValue, "No party with condition :" + condition);
        
        return partyValue.getString(E.partyId.name());
    }
    
    /**
     * Get partyId from parentRoleCode and roleTypeId
     * @param parentRoleCode
     * @param roleTypeCondition
     * @return
     * @throws GeneralException
     */
    public String getMatricola(String partyId, EntityCondition roleTypeCondition) throws GeneralException {
        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition(E.partyId.name(), partyId), roleTypeCondition);

        List<GenericValue> partyList = getDelegator().findList(E.PartyParentRole.name(), condition, null, null, null, false);
        throwFoundMore(1, true, "Found more than one party with condition :" + condition);
        
        GenericValue partyValue = EntityUtil.getFirst(partyList);
        throwNoFound(partyValue, "No party with condition :" + condition);
        
        return partyValue.getString(E.parentRoleCode.name());
    }
}
