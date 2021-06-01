package com.mapsengineering.base.find;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Utility for find PartyRole 
 */
public class PartyRelationshipFindServices extends BaseFindServices {
    public static final String MODULE = PartyRelationshipFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public PartyRelationshipFindServices(Delegator delegator) {
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
    public String getReferenteId(String evalPartyId, String roleTypeId, Timestamp refDate, Timestamp fromDate, Timestamp thruDate, String noFound, String foundMore) throws GeneralException {
        GenericValue value = getPartyRelationshipFrom(evalPartyId, roleTypeId, refDate, fromDate, thruDate, E.WEF_EVALUATED_BY.name(), noFound, foundMore);

        return value.getString(E.partyIdTo.name());
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
    public String getResponsibleId(String orgUnitId, String roleTypeResponsibleId, Timestamp refDate, Timestamp fromDate, Timestamp thruDate, String noFound, String foundMore) throws GeneralException {
        GenericValue value = getPartyRelationshipFrom(orgUnitId, roleTypeResponsibleId, refDate, fromDate, thruDate, E.ORG_RESPONSIBLE.name(), noFound, foundMore);

        return value.getString(E.partyIdTo.name());
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
    private GenericValue getPartyRelationshipFrom(String partyIdFrom, String roleTypeIdTo, Timestamp refDate, Timestamp fromDate, Timestamp thruDate, String partyRelationshipTypeId, String noFound, String foundMore) throws GeneralException {
        List<EntityCondition> partyRelationshipCond = FastList.newInstance();
        
        partyRelationshipCond.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        if(UtilValidate.isNotEmpty(roleTypeIdTo)) {
            partyRelationshipCond.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), roleTypeIdTo));
        }
        if(UtilValidate.isNotEmpty(partyIdFrom)) {
            partyRelationshipCond.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyIdFrom));
        }
        if(UtilValidate.isNotEmpty(refDate)) {
            partyRelationshipCond.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.EQUALS, null),
                    EntityOperator.OR,
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, refDate)));
                    
            partyRelationshipCond.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO, refDate));
        } else {
            partyRelationshipCond.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.EQUALS, null),
                    EntityOperator.OR,
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
                    
            partyRelationshipCond.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
        }
        
        return FindUtilService.findOne(getDelegator(), E.PartyRelationship.name(), EntityCondition.makeCondition(partyRelationshipCond), foundMore, noFound);
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
    private GenericValue getPartyRelationshipTo(String partyIdTo, String roleTypeIdFrom, Timestamp refDate, Timestamp fromDate, Timestamp thruDate, String partyRelationshipTypeId, String noFound, String foundMore) throws GeneralException {
        List<EntityCondition> partyRelationshipCond = FastList.newInstance();
        
        partyRelationshipCond.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        if(UtilValidate.isNotEmpty(roleTypeIdFrom)) {
            partyRelationshipCond.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), roleTypeIdFrom));
        }
        if(UtilValidate.isNotEmpty(partyIdTo)) {
            partyRelationshipCond.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyIdTo));
        }
        if(UtilValidate.isNotEmpty(refDate)) {
            partyRelationshipCond.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.EQUALS, null),
                    EntityOperator.OR,
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, refDate)));
                    
            partyRelationshipCond.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO, refDate));
        } else {
            partyRelationshipCond.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.EQUALS, null),
                    EntityOperator.OR,
                    EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
                    
            partyRelationshipCond.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
        }
        
        return FindUtilService.findOne(getDelegator(), E.PartyRelationship.name(), EntityCondition.makeCondition(partyRelationshipCond), foundMore, noFound);
    }
}
