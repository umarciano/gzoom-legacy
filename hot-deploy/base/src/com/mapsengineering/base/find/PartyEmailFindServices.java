package com.mapsengineering.base.find;

import java.sql.Timestamp;
import java.util.List;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * Utility for find Party Email Address 
 */
public class PartyEmailFindServices extends BaseFindServices {
    public static final String MODULE = PartyEmailFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public PartyEmailFindServices(Delegator delegator) {
        super(delegator);
    }

    /**
     * Get emailAddress from partyId
     * @param partyId
     * @return list of emailAddresses
     * @throws GeneralException
     */
    public List<GenericValue> getEmailAddress(String partyId) throws GeneralException {
        List<EntityCondition> conditionList = getBaseConditionList();

        if (UtilValidate.isNotEmpty(partyId)) {
            conditionList.add(EntityCondition.makeCondition("partyId", partyId));
        }
        
        return getDelegator().findList("PartyContactWithPurpose", EntityCondition.makeCondition(conditionList), null, UtilMisc.toList("infoString"), null, true);
    }
    
    /**
     * Get emailAddress from partyId
     * @param partyIdList
     * @return list of emailAddresses
     * @throws GeneralException
     */
    public List<GenericValue> getEmailAddresses(List<String> partyIdList) throws GeneralException {
        List<EntityCondition> conditionList = getBaseConditionList();

        if (UtilValidate.isNotEmpty(partyIdList)) {
            conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdList));
        }

        return getDelegator().findList("PartyContactWithPurpose", EntityCondition.makeCondition(conditionList), null, null, null, true);
    }

    private List<EntityCondition> getBaseConditionList() {
        Timestamp nowDate = UtilDateTime.nowTimestamp();

        return UtilMisc.toList(
                EntityCondition.makeCondition("contactFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowDate), 
                EntityCondition.makeCondition(
                        UtilMisc.toList(EntityCondition.makeCondition("contactThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowDate), 
                        EntityCondition.makeCondition("contactThruDate", GenericEntity.NULL_FIELD)), EntityJoinOperator.OR
                ), 
                EntityCondition.makeCondition("purposeFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowDate), 
                EntityCondition.makeCondition(
                        UtilMisc.toList(EntityCondition.makeCondition("purposeThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowDate), 
                        EntityCondition.makeCondition("purposeThruDate", GenericEntity.NULL_FIELD)), EntityJoinOperator.OR
                ), 
                EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_EMAIL"), 
                EntityCondition.makeCondition("contactMechTypeId", "EMAIL_ADDRESS")
        );
    }
}
