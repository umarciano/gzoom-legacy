import org.ofbiz.service.*;
import org.ofbiz.base.util.*;
import java.text.*;

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

def workEffortTypeId = parameters.workEffortTypeId;

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
return result;
