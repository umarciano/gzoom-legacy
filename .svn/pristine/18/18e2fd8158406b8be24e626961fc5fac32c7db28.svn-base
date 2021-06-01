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
 * Utility for find PartyRole 
 */
public class PartyRoleFindServices extends BaseFindServices {
    public static final String MODULE = PartyRoleFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public PartyRoleFindServices(Delegator delegator) {
        super(delegator);
    }

    /**
     * Cerca unico PartyRole, Ritorna roleTypeId, altrimenti restituisce eccezione 
     * @param gv
     * @param parentRoleTypeId
     * @param partyId
     * @param orgRoleTypeId
     * @param getEntityName
     * @throws GeneralException
     */
    public String getPartyRoleTypeId(String partyId, String roleTypeId, String parentRoleTypeId, String noFound, String foundMore) throws GeneralException {
        GenericValue value = getPartyRole(partyId, roleTypeId, parentRoleTypeId, noFound, foundMore);

        return value.getString(E.roleTypeId.name());
    }
    
    /**
     * Ritorna unico partyId, altrimenti restituisce eccezione 
     * @param partyId
     * @param roleTypeId
     * @param parentRoleTypeId
     * @param noFound
     * @param foundMore
     * @throws GeneralException
     */
    public String getPartyId(String partyId, String roleTypeId, String parentRoleTypeId, String noFound, String foundMore) throws GeneralException {
        GenericValue value = getPartyRole(partyId, roleTypeId, parentRoleTypeId, noFound, foundMore);

        return value.getString(E.partyId.name());
    }

    /**
     * Cerca unico PartyRole, mediante le condizioni con partyId, eventuale roleTypeId, eventuale parentRoleTypeId
     * @param partyId
     * @param roleTypeId
     * @param parentRoleTypeId
     * @param noFound
     * @param foundMore
     * @throws GeneralException
     */
    private GenericValue getPartyRole(String partyId, String roleTypeId, String parentRoleTypeId, String noFound, String foundMore) throws GeneralException {
        List<EntityCondition> partyRoleCond = FastList.newInstance();
        partyRoleCond.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        if(UtilValidate.isNotEmpty(roleTypeId)) {
            partyRoleCond.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        }
        if(UtilValidate.isNotEmpty(parentRoleTypeId)) {
            partyRoleCond.add(EntityCondition.makeCondition(E.parentRoleTypeId.name(), parentRoleTypeId));
        
        }
        return FindUtilService.findOne(getDelegator(), E.PartyRole.name(), EntityCondition.makeCondition(partyRoleCond), foundMore, noFound);
    }
}
