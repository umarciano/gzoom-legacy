import org.ofbiz.service.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

result = ServiceUtil.returnSuccess();

def partyId = "";
def partyName = "";
def roleDescription = "";
def roleTypeId = "";
def parentRoleCode = "";
def estimatedStartDateFormatted = "";
def estimatedCompletionDateFormatted = "";

def workEffortId = parameters.workEffortId;
if (UtilValidate.isNotEmpty(workEffortId)) {
	def workEffortType = EntityUtil.getFirst(delegator.findList("WorkEffortAndTypeView", EntityCondition.makeCondition("workEffortId", workEffortId), null, null, null, false));
	if (UtilValidate.isNotEmpty(workEffortType)) {
		def getWorkEffortTypePeriodDatesServiceMap = [:];
		getWorkEffortTypePeriodDatesServiceMap.put("userLogin", userLogin);
		getWorkEffortTypePeriodDatesServiceMap.put("locale", locale);
		getWorkEffortTypePeriodDatesServiceMap.put("workEffortTypeId", workEffortType.childTemplateId);
		def getWorkEffortTypePeriodDatesServiceOutMap = dctx.getDispatcher().runSync("getWorkEffortTypePeriodDates", getWorkEffortTypePeriodDatesServiceMap);
		if (UtilValidate.isNotEmpty(getWorkEffortTypePeriodDatesServiceOutMap)) {
			estimatedStartDateFormatted = getWorkEffortTypePeriodDatesServiceOutMap.get("fromDateFormatted");
			estimatedCompletionDateFormatted = getWorkEffortTypePeriodDatesServiceOutMap.get("thruDateFormatted");
		}
	}
}

def evalPartyId = parameters.evalPartyId;
if (UtilValidate.isNotEmpty(evalPartyId)) {
	def getOrgUnitByEvalPartyIdServiceMap = [:];
	getOrgUnitByEvalPartyIdServiceMap.put("userLogin", userLogin);
	getOrgUnitByEvalPartyIdServiceMap.put("locale", locale);
	getOrgUnitByEvalPartyIdServiceMap.put("evalPartyId", evalPartyId);
	def getOrgUnitByEvalPartyIdOutMap = dctx.getDispatcher().runSync("getOrgUnitByEvalPartyId", getOrgUnitByEvalPartyIdServiceMap);
	if (UtilValidate.isNotEmpty(getOrgUnitByEvalPartyIdOutMap)) {
		partyId = getOrgUnitByEvalPartyIdOutMap.get("partyId");
		partyName = getOrgUnitByEvalPartyIdOutMap.get("partyName");
		roleDescription = getOrgUnitByEvalPartyIdOutMap.get("roleDescription");
		roleTypeId = getOrgUnitByEvalPartyIdOutMap.get("roleTypeId");
		parentRoleCode = getOrgUnitByEvalPartyIdOutMap.get("parentRoleCode");
	}
}

result.put("partyId", partyId);
result.put("partyName", partyName);
result.put("roleDescription", roleDescription);
result.put("roleTypeId", roleTypeId);
result.put("parentRoleCode", parentRoleCode);
result.put("estimatedStartDateFormatted", estimatedStartDateFormatted);
result.put("estimatedCompletionDateFormatted", estimatedCompletionDateFormatted);
return result;
