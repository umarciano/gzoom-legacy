import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

if (UtilValidate.isEmpty(context.commEntityName))
	commEntityName = "CommunicationEventAndRole";
//Prima parte relativa al recupero delle comunicazioni da soggetti conosciuti
currentListCondition = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "COM_UNKNOWN_PARTY"),
                 EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "COM_PENDING"),
				 EntityCondition.makeCondition("partyId", context.partyId)];

if (!"true".equals(context.all)) {
	currentListCondition.add(EntityCondition.makeCondition("roleStatusId", EntityOperator.NOT_EQUAL, "COM_ROLE_COMPLETED"));
}
if (UtilValidate.isNotEmpty(context.listCondition)) {
	currentListCondition.addAll(context.listCondition);
}


context.commEvents = delegator.findList(commEntityName, EntityCondition.makeCondition(currentListCondition, EntityJoinOperator.AND),
										null, ["datetimeStarted DESC"], null, true);


//Seconda parte relativa al recupero delle comunicazioni da soggetti ignoti
currentListCondition = [EntityCondition.makeCondition("statusId", "COM_UNKNOWN_PARTY"),
		EntityCondition.makeCondition("roleStatusId", EntityOperator.NOT_EQUAL, "COM_ROLE_COMPLETED"),
		EntityCondition.makeCondition("partyId", context.partyId)];
if (UtilValidate.isNotEmpty(context.listCondition)) {
	currentListCondition.addAll(context.listCondition);
}


context.commEventsUnknown = delegator.findList(commEntityName, EntityCondition.makeCondition(currentListCondition, EntityJoinOperator.AND),
		null, ["datetimeStarted DESC"], null, true);

//Terza parte relativa al recupero delle comunicazioni draft
currentListCondition = [EntityCondition.makeCondition("statusId", "COM_PENDING"),
		EntityCondition.makeCondition("partyId", context.partyId)];
if (UtilValidate.isNotEmpty(context.listCondition)) {
	currentListCondition.addAll(context.listCondition);
}


context.commEventDraft = delegator.findList(commEntityName, EntityCondition.makeCondition(currentListCondition, EntityJoinOperator.AND),
		null, ["datetimeStarted DESC"], null, true);
