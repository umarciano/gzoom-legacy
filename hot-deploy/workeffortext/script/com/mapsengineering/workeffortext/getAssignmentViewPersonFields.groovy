import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;


result = ServiceUtil.returnSuccess();

def personComments = "";
def personAllocation = "";
def partyId = parameters.partyId;
def weTypeSubFilter = parameters.weTypeSubFilter;
def fromDate = parameters.fromDate;
def thruDate = parameters.thruDate;
def periodFromDate = parameters.periodFromDate;
def periodThruDate = parameters.periodThruDate;

def person = delegator.findOne("Person", ["partyId" : partyId], false);
if (UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.comments)) {
	personComments = person.comments;
}
if ("PERSON".equals(weTypeSubFilter)) {
	if (UtilValidate.isNotEmpty(periodFromDate)) {
		def customTimePriodFrom = delegator.findOne("CustomTimePeriod",["customTimePeriodId": periodFromDate], false);
		if (UtilValidate.isNotEmpty(customTimePriodFrom)) {
			fromDate = customTimePriodFrom.fromDate;
		}
	}
	if (UtilValidate.isNotEmpty(periodThruDate)) {
		def customTimePriodThru = delegator.findOne("CustomTimePeriod",["customTimePeriodId": periodThruDate], false);
		if (UtilValidate.isNotEmpty(customTimePriodThru)) {
			thruDate = customTimePriodThru.thruDate;
		}
	}	
	
	def partyRelationShipCondList = [];
	partyRelationShipCondList.add(EntityCondition.makeCondition("partyIdTo", partyId));
	partyRelationShipCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "ORG_ALLOCATION"));
	partyRelationShipCondList.add(EntityCondition.makeCondition("fromDate", fromDate));
	partyRelationShipCondList.add(EntityCondition.makeCondition("thruDate", thruDate));
	def partyRelationshipList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(partyRelationShipCondList), null, null, null, false);
	if (UtilValidate.isNotEmpty(partyRelationshipList)) {
		def partyIdFromList = EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdFrom", true);
		personAllocation = StringUtil.join(partyIdFromList, ",");
	} else {
		personAllocation = StringUtil.join(["![null-field],"], ",");
	}
}

result.put("personComments", personComments);
result.put("personAllocation", personAllocation);
return result;