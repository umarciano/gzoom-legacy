import org.ofbiz.entity.GenericEntity;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

nowDate = UtilDateTime.nowTimestamp();



conditionList = [EntityCondition.makeCondition("partyId", userLogin.partyId),
                 EntityCondition.makeCondition("contactFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowDate),
		         EntityCondition.makeCondition([
		            EntityCondition.makeCondition("contactThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowDate),
		            EntityCondition.makeCondition("contactThruDate", GenericEntity.NULL_FIELD)], EntityJoinOperator.OR),
		         EntityCondition.makeCondition("purposeFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowDate),
		         EntityCondition.makeCondition([
		            EntityCondition.makeCondition("purposeThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowDate),
		            EntityCondition.makeCondition("purposeThruDate", GenericEntity.NULL_FIELD)], EntityJoinOperator.OR),
		         EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_EMAIL"),
		         EntityCondition.makeCondition("contactMechTypeId", "EMAIL_ADDRESS")];

context.emailAddressesList = delegator.findList("PartyContactWithPurpose", EntityCondition.makeCondition(conditionList), null, ["infoString"], null, true);

conditionList = [EntityCondition.makeCondition("contactFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowDate),
		         EntityCondition.makeCondition([
		            EntityCondition.makeCondition("contactThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowDate),
		            EntityCondition.makeCondition("contactThruDate", GenericEntity.NULL_FIELD)], EntityJoinOperator.OR),
		         EntityCondition.makeCondition("purposeFromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowDate),
		         EntityCondition.makeCondition([
		            EntityCondition.makeCondition("purposeThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowDate),
		            EntityCondition.makeCondition("purposeThruDate", GenericEntity.NULL_FIELD)], EntityJoinOperator.OR),
		         EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_EMAIL"),
		         EntityCondition.makeCondition("contactMechTypeId", "EMAIL_ADDRESS")];

if (UtilValidate.isNotEmpty(context.partyIdTo)) {
	conditionList.add(EntityCondition.makeCondition("partyId", context.partyIdTo))
}

//Debug.log("********************************************** contactMechIdToList Condition: " + conditionList);

context.contactMechIdToList = delegator.findList("PartyContactWithPurpose", EntityCondition.makeCondition(conditionList), null, ["infoString"], null, true);

