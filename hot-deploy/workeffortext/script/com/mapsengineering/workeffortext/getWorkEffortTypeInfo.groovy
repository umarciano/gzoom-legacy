import org.ofbiz.service.*;
import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

GroovyUtil.runScriptAtLocation("component://base/webapp/base/WEB-INF/actions/setLocaleSecondary.groovy", context);

result = ServiceUtil.returnSuccess();

DateFormat df = new SimpleDateFormat(UtilDateTime.getDateFormat(locale));

def showEtch = "";
def showCode = "";
def showEtchField = "";
def fromDateFormatted = "";
def thruDateFormatted = "";
def startDate = "";
def onlyRefDate = "";
def showRoleType = "";
def noThruDate = "";
def insertTitlePosition = "";
def isParentRelAss = "N";
def usePeriod = "";
def periodFromDate = "";
def periodThruDate = "";
def periodCodeFromDate = "";
def periodCodeThruDate = "";

def workEffortTypeId = parameters.workEffortTypeId;
def parentAssocTypeId = parameters.parentAssocTypeId;

def getWefldMainParamsServiceMap = [:];
getWefldMainParamsServiceMap.put("userLogin", userLogin);
getWefldMainParamsServiceMap.put("locale", locale);
getWefldMainParamsServiceMap.put("workEffortTypeId", workEffortTypeId);

def workEffortType = delegator.findOne("WorkEffortTypeAndRoleTypeView", ["workEffortTypeId" : workEffortTypeId], false);
def orgUnitRoleTypeId = "";
def orgUnitRoleTypeDesc = "";
if (UtilValidate.isNotEmpty(workEffortType)) {
	orgUnitRoleTypeId = workEffortType.roleTypeId;
	if ("Y".equals(context.localeSecondarySet)) {
	    orgUnitRoleTypeDesc = workEffortType.descriptionLang;
	} else {
	    orgUnitRoleTypeDesc = workEffortType.description;
	}	
}

def getWefldMainParamsServiceOutMap = dctx.getDispatcher().runSync("getWefldMainParams", getWefldMainParamsServiceMap);
if (UtilValidate.isNotEmpty(getWefldMainParamsServiceOutMap)) {
	showCode = getWefldMainParamsServiceOutMap.get("showCode");
	showEtchField = getWefldMainParamsServiceOutMap.get("showEtchField");
	showEtch = getWefldMainParamsServiceOutMap.get("showEtch");
	startDate = getWefldMainParamsServiceOutMap.get("startDate");
	onlyRefDate = getWefldMainParamsServiceOutMap.get("onlyRefDate");
	showRoleType = getWefldMainParamsServiceOutMap.get("showRoleType");
	noThruDate = getWefldMainParamsServiceOutMap.get("noThruDate");
	insertTitlePosition = getWefldMainParamsServiceOutMap.get("insertTitlePosition");
	usePeriod = getWefldMainParamsServiceOutMap.get("usePeriod");
}

if ("Y".equals(onlyRefDate)) {
	thruDateFormatted = df.format(UtilDateTime.nowTimestamp());
} else {
	def getWorkEffortTypePeriodDatesServiceMap = [:];
	getWorkEffortTypePeriodDatesServiceMap.put("userLogin", userLogin);
	getWorkEffortTypePeriodDatesServiceMap.put("locale", locale);
	getWorkEffortTypePeriodDatesServiceMap.put("workEffortTypeId", workEffortTypeId);
	def getWorkEffortTypePeriodDatesServiceOutMap = dctx.getDispatcher().runSync("getWorkEffortTypePeriodDates", getWorkEffortTypePeriodDatesServiceMap);
	if (UtilValidate.isNotEmpty(getWorkEffortTypePeriodDatesServiceOutMap)) {
		fromDateFormatted = getWorkEffortTypePeriodDatesServiceOutMap.get("fromDateFormatted");
		thruDateFormatted = getWorkEffortTypePeriodDatesServiceOutMap.get("thruDateFormatted");
	}	
}

if ("OPEN".equals(startDate)) {
	def workEffortId = parameters.workEffortId;
	if (UtilValidate.isNotEmpty(workEffortId)) {
		def workEffortParentId = "";
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		if (UtilValidate.isNotEmpty(workEffort)) {
			workEffortParentId = workEffort.workEffortParentId;
			if (UtilValidate.isNotEmpty(workEffortParentId)) {
				def workEffortTypePeriod = delegator.findOne("WorkEffortAndTypePeriodAndThruDate", ["workEffortId" : workEffortParentId], false);
				if (UtilValidate.isNotEmpty(workEffortTypePeriod)) {
					def fromDate = "GLFISCTYPE_ACTUAL".equals(workEffortTypePeriod.glFiscalTypeEnumId) ? workEffortTypePeriod.thruDate : workEffortTypePeriod.fromDate;
					if (UtilValidate.isNotEmpty(fromDate)) {
						def fromDateDefault = UtilDateTime.getYearStart(fromDate);
						fromDateFormatted = df.format(fromDateDefault);
					}			
				}
			}
		}
	}
}

if ("Y".equals(noThruDate)) {
	thruDateFormatted = df.format(UtilDateTime.toDate(12, 31, 2099, 0, 0, 0));
}

if (UtilValidate.isNotEmpty(usePeriod)) {
	if (UtilValidate.isNotEmpty(fromDateFormatted)) {
		def fromDate = df.parse(fromDateFormatted);
		def customTimePeriodFromCondList = [];
		customTimePeriodFromCondList.add(EntityCondition.makeCondition("fromDate", fromDate));
		customTimePeriodFromCondList.add(EntityCondition.makeCondition("periodTypeId", usePeriod));
		def customTimePeriodFromList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodFromCondList), null, null, null, false);
		def customTimePeriodFromItem = EntityUtil.getFirst(customTimePeriodFromList);
		if (UtilValidate.isNotEmpty(customTimePeriodFromItem)) {
			periodFromDate = customTimePeriodFromItem.customTimePeriodId;
			periodCodeFromDate = "Y".equals(context.localeSecondarySet) ? customTimePeriodFromItem.customTimePeriodCodeLang : customTimePeriodFromItem.customTimePeriodCode;
		}
	}
	if (UtilValidate.isNotEmpty(thruDateFormatted)) {
		def thruDate = df.parse(thruDateFormatted);
		def customTimePeriodThruCondList = [];
		customTimePeriodThruCondList.add(EntityCondition.makeCondition("thruDate", thruDate));
		customTimePeriodThruCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
		def customTimePeriodThruList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodThruCondList), null, null, null, false);
		def customTimePeriodThruItem = EntityUtil.getFirst(customTimePeriodThruList);
		if (UtilValidate.isNotEmpty(customTimePeriodThruItem)) {
			periodThruDate = customTimePeriodThruItem.customTimePeriodId;
			periodCodeThruDate = "Y".equals(context.localeSecondarySet) ? customTimePeriodThruItem.customTimePeriodCodeLang : customTimePeriodThruItem.customTimePeriodCode;
		}
	}
}

if (UtilValidate.isNotEmpty(parentAssocTypeId)) {
	def workEffortTypeAssocCondList = [];
	workEffortTypeAssocCondList.add(EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId));
	workEffortTypeAssocCondList.add(EntityCondition.makeCondition("workEffortAssocTypeId", parentAssocTypeId));
	workEffortTypeAssocCondList.add(EntityCondition.makeCondition("isParentRel", "Y"));
	def workEffortTypeAssocList = delegator.findList("WorkEffortTypeAssoc", EntityCondition.makeCondition(workEffortTypeAssocCondList), null, null, null, false);
	def workEffortTypeAssoc = EntityUtil.getFirst(workEffortTypeAssocList);
	if (UtilValidate.isNotEmpty(workEffortTypeAssoc)) {
		WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(parameters, parameters, delegator);
		def map = paramsEvaluator.getParams(parameters.workEffortTypeId, workEffortTypeAssoc.contentId, false);
		if (UtilValidate.isNotEmpty(map)) {
			if ("Y".equals(map.assocLevelSameUOAss) || "Y".equals(map.assocLevelParentUOAss) || "Y".equals(map.assocLevelChildUOAss) || "Y".equals(map.assocLevelSisterUOAss) || "Y".equals(map.assocLevelTopUOAss)) {
				isParentRelAss = "Y";
			}
		}
	}
}

result.put("orgUnitRoleTypeId", orgUnitRoleTypeId);
result.put("orgUnitRoleTypeDesc", orgUnitRoleTypeDesc);
result.put("showEtch", showEtch);
result.put("showCode", showCode);
result.put("startDate", startDate);
result.put("onlyRefDate", onlyRefDate);
result.put("showEtchField", showEtchField);
result.put("showRoleType", showRoleType);
result.put("fromDateFormatted", fromDateFormatted);
result.put("thruDateFormatted", thruDateFormatted);
result.put("insertTitlePosition", insertTitlePosition);
result.put("isParentRelAss", isParentRelAss);
result.put("usePeriod", usePeriod);
result.put("periodFromDate", periodFromDate);
result.put("periodThruDate", periodThruDate);
result.put("periodCodeFromDate", periodCodeFromDate);
result.put("periodCodeThruDate", periodCodeThruDate);
return result;
