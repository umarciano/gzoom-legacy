import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

def wepaWorkEffortIdFrom = UtilValidate.isNotEmpty(context.wepaWorkEffortIdFrom) ? context.wepaWorkEffortIdFrom : parameters.wepaWorkEffortIdFrom;
def wepaWorkEffortName = UtilValidate.isNotEmpty(context.wepaWorkEffortName) ? context.wepaWorkEffortName : parameters.wepaWorkEffortName;
def wepaWorkEffortEtch = UtilValidate.isNotEmpty(context.wepaWorkEffortEtch) ? context.wepaWorkEffortEtch : parameters.wepaWorkEffortEtch;
def wepaFromDateFrom = UtilValidate.isNotEmpty(context.wepaFromDateFrom) ? context.wepaFromDateFrom : parameters.wepaFromDateFrom;
def wepaRoleTypeWeight = UtilValidate.isNotEmpty(context.wepaRoleTypeWeight) ? context.wepaRoleTypeWeight : parameters.wepaRoleTypeWeight;
def partyId = UtilValidate.isNotEmpty(context.partyId) ? context.partyId : parameters.partyId;
def defaultFromDate = UtilValidate.isNotEmpty(context.defaultFromDate) ? context.defaultFromDate : parameters.defaultFromDate;
def defaultThruDate = UtilValidate.isNotEmpty(context.defaultThruDate) ? context.defaultThruDate : parameters.defaultThruDate;

def fromDate = null;
if (UtilValidate.isNotEmpty(context.fromDate)) {
	fromDate = context.fromDate;
} else {
	if (UtilValidate.isNotEmpty(parameters.workEffortIdRoot)) {
		if ("OPEN".equals(context.startDate)) {
			def workEffortTypePeriod = delegator.findOne("WorkEffortAndTypePeriodAndThruDate", ["workEffortId": parameters.workEffortIdRoot], false);
			if (UtilValidate.isNotEmpty(workEffortTypePeriod)) {
				fromDate = "GLFISCTYPE_ACTUAL".equals(workEffortTypePeriod.glFiscalTypeEnumId) ? workEffortTypePeriod.thruDate : workEffortTypePeriod.fromDate;
			} else {
				if (UtilValidate.isNotEmpty(context.we)) {
					fromDate = we.estimatedStartDate;
				}
			}
		} else {
			if (UtilValidate.isNotEmpty(context.we)) {
				fromDate = we.estimatedStartDate;
			}
		}
	} else {
		if (UtilValidate.isNotEmpty(context.we)) {
			fromDate = we.estimatedStartDate;
		}		
	}
}

def thruDate = null;
if (UtilValidate.isNotEmpty(context.thruDate)) {
	thruDate = context.thruDate;
} else {
	if (UtilValidate.isNotEmpty(context.we)) {
		thruDate = context.we.estimatedCompletionDate;
	}
}

def fromDateOld = null;
if (UtilValidate.isNotEmpty(context.fromDate)) {
	fromDateOld = context.fromDate;
} else {
	if (UtilValidate.isNotEmpty(context.we)) {
		fromDateOld = context.we.estimatedStartDate;
	}
}

def category = "";
def categoryLang = "";
def profile = "";
def profileLang = "";
def employmentAmount = null;
if (UtilValidate.isNotEmpty(partyId)) {
	def partyRoleViewAndEmplCondList = [];
	partyRoleViewAndEmplCondList.add(EntityCondition.makeCondition("roleTypeId", context.roleTypeId));
	partyRoleViewAndEmplCondList.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
	partyRoleViewAndEmplCondList.add(EntityCondition.makeCondition("partyId", partyId));
	def partyRoleViewAndEmplList = delegator.findList("PartyRoleViewAndEmpl", EntityCondition.makeCondition(partyRoleViewAndEmplCondList), null, ["partyId"], null, false);
	def partyRoleViewAndEmplItem = EntityUtil.getFirst(partyRoleViewAndEmplList);
	if (UtilValidate.isNotEmpty(partyRoleViewAndEmplItem)) {
		category = partyRoleViewAndEmplItem.category;
		categoryLang = partyRoleViewAndEmplItem.categoryLang;
		profile = partyRoleViewAndEmplItem.profile;
		profileLang = partyRoleViewAndEmplItem.profileLang;
		employmentAmount = partyRoleViewAndEmplItem.employmentAmount;
	}	
}

if (UtilValidate.isNotEmpty(context.we)) {
	context.estimatedStartDate = context.we.estimatedStartDate;
	context.estimatedCompletionDate = context.we.estimatedCompletionDate;
}

def periodFromDate = "";
if (UtilValidate.isNotEmpty(context.periodFromDate)) {
	periodFromDate = context.periodFromDate;
} else {
	if (UtilValidate.isNotEmpty(context.usePeriod)) {
		def customTimePeriodFromCondList = [];
		customTimePeriodFromCondList.add(EntityCondition.makeCondition("fromDate", fromDate));
		customTimePeriodFromCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
		def customTimePeriodFromList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodFromCondList), null, null, null, false);
		def customTimePeriodFromItem = EntityUtil.getFirst(customTimePeriodFromList);
		if (UtilValidate.isNotEmpty(customTimePeriodFromItem)) {
			periodFromDate = customTimePeriodFromItem.customTimePeriodId;
		}
	}
}

def periodThruDate = "";
if (UtilValidate.isNotEmpty(context.periodThruDate)) {
	periodThruDate = context.periodThruDate;
} else {
	if (UtilValidate.isNotEmpty(context.usePeriod)) {
		def customTimePeriodThruCondList = [];
		customTimePeriodThruCondList.add(EntityCondition.makeCondition("thruDate", thruDate));
		customTimePeriodThruCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
		def customTimePeriodThruList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodThruCondList), null, null, null, false);
		def customTimePeriodThruItem = EntityUtil.getFirst(customTimePeriodThruList);
		if (UtilValidate.isNotEmpty(customTimePeriodThruItem)) {
			periodThruDate = customTimePeriodThruItem.customTimePeriodId;
		}
	}
}

def personComments = "";
def personAllocation = "";
if (UtilValidate.isNotEmpty(partyId)) {
	def personFieldsServiceMap = ["partyId": partyId,
	                              "fromDate": fromDate,
	                              "thruDate": thruDate,
	                              "periodFromDate": periodFromDate,
	                              "periodThruDate": periodThruDate,
	                              "weTypeSubFilter": context.weTypeSubFilter,
	                              "timeZone": context.timeZone,
	                              "userLogin": context.userLogin];
	def personFieldsServiceRes = dispatcher.runSync("getAssignmentViewPersonFields", personFieldsServiceMap);
	if (UtilValidate.isNotEmpty(personFieldsServiceRes)) {
		if (UtilValidate.isNotEmpty(personFieldsServiceRes.personComments)) {
			personComments = personFieldsServiceRes.personComments;
		}
		if (UtilValidate.isNotEmpty(personFieldsServiceRes.personAllocation)) {
			personAllocation = personFieldsServiceRes.personAllocation;
		}
	}
}

context.wepaWorkEffortIdFrom = wepaWorkEffortIdFrom;
context.wepaWorkEffortName = wepaWorkEffortName;
context.wepaWorkEffortEtch = wepaWorkEffortEtch;
context.wepaFromDateFrom = wepaFromDateFrom;
context.wepaRoleTypeWeight = wepaRoleTypeWeight;
context.partyId = partyId;
context.defaultFromDate = defaultFromDate;
context.defaultThruDate = defaultThruDate;
context.fromDate = fromDate;
context.thruDate = thruDate;
context.fromDateOld = fromDateOld;
context.category = category;
context.categoryLang = categoryLang;
context.profile = profile;
context.profileLang = profileLang;
context.employmentAmount = employmentAmount;
context.periodFromDate = periodFromDate;
context.periodThruDate = periodThruDate;
context.personComments = personComments;
context.personAllocation = personAllocation;
