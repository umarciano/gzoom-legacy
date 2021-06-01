import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;


def isPortletReadOnly = false;

def specialized = parameters.specialized;
def customTimePeriodId = parameters.customTimePeriodId;
def glFiscalTypeEnumId = parameters.glFiscalTypeEnumId;
def parentWorkEffortTypeId = parameters.parentWorkEffortTypeId;

def weTransPeriodIsClosed = UtilValidate.isNotEmpty(context.weTransPeriodIsClosed) ? context.weTransPeriodIsClosed : parameters.weTransPeriodIsClosed;
def isPosted = UtilValidate.isNotEmpty(context.isPosted) ? context.isPosted : parameters.isPosted;
def crudEnumIdSecondary = UtilValidate.isNotEmpty(context.crudEnumIdSecondary) ? context.crudEnumIdSecondary : parameters.crudEnumIdSecondary;
def valModId = UtilValidate.isNotEmpty(context.valModId) ? context.valModId : parameters.valModId;
def weTransTypeValueId = UtilValidate.isNotEmpty(context.weTransTypeValueId) ? context.weTransTypeValueId : parameters.weTransTypeValueId;

def isReadOnly = UtilValidate.isNotEmpty(context.isReadOnly) ? context.isReadOnly : parameters.isReadOnly;


if ("Y".equals(parameters.rootInqyTree) || "Y".equals(isPosted)) {
	isPortletReadOnly = true;
} else if (! checkWorkEffortPermissions()) {
	isPortletReadOnly = true;
} else {
	def adminPermission = getAdminPermission(parentWorkEffortTypeId, specialized);
	if (security.hasPermission(adminPermission, context.userLogin)) {
		isPortletReadOnly = false;
	} else {
		isPortletReadOnly = "Y".equals(weTransPeriodIsClosed) || "NONE".equals(crudEnumIdSecondary) || "ALL_NOT_MOD".equals(valModId) || isActualMod(valModId, weTransTypeValueId) || isBudgetMod(valModId, weTransTypeValueId) || isTrue(isReadOnly) || ! rootHasPeriod(parentWorkEffortTypeId, customTimePeriodId, glFiscalTypeEnumId);
	}
}

def checkWorkEffortPermissions() {
	return security.hasPermission("WORKEFFORTMGR_ADMIN", context.userLogin) || security.hasPermission("WORKEFFORTMGR_CREATE", context.userLogin) || security.hasPermission("WORKEFFORTMGR_UPDATE", context.userLogin) || security.hasPermission("WORKEFFORTORG_ADMIN", context.userLogin) || security.hasPermission("WORKEFFORTROLE_ADMIN", context.userLogin);
}

def getAdminPermission(parentWorkEffortTypeId, specialized) {
	if ("Y".equals(specialized) && UtilValidate.isNotEmpty(parentWorkEffortTypeId)) {
		def parentWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : parentWorkEffortTypeId], false);
		if (UtilValidate.isNotEmpty(parentWorkEffortType)) {
			def weContextId = parentWorkEffortType.parentTypeId;
			return ContextPermissionPrefixEnum.getPermissionPrefix(weContextId) + "MGR_ADMIN";
		}
	}
	return "WORKEFFORTMGR_ADMIN";
}

def isActualMod(valModId, weTransTypeValueId) {
	return "ACTUAL_NOT_MOD".equals(valModId) && "ACTUAL".equals(weTransTypeValueId);
}

def isBudgetMod(valModId, weTransTypeValueId) {
	return "BUDGET_NOT_MOD".equals(valModId) && "BUDGET".equals(weTransTypeValueId);
}

def isTrue(isReadOnly) {
	return UtilValidate.isNotEmpty(isReadOnly) && isReadOnly.booleanValue();
}

def rootHasPeriod(parentWorkEffortTypeId, customTimePeriodId, glFiscalTypeEnumId) {
	def conditionList = [];
	conditionList.add(EntityCondition.makeCondition("workEffortTypeId", parentWorkEffortTypeId));
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("glFiscalTypeEnumId", glFiscalTypeEnumId));
	conditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
	
	def periodList = delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(conditionList), null, null, null, false);
	if (UtilValidate.isEmpty(periodList)) {
		return true;
	}
	def periodItem = EntityUtil.getFirst(periodList);
	if (UtilValidate.isNotEmpty(periodItem)) {
		return "OPEN".equals(periodItem.statusEnumId) || "REOPEN".equals(periodItem.statusEnumId) || "DETECTABLE".equals(periodItem.statusEnumId);
	}
	return true;
}

context.isPortletFormDisabled = isPortletReadOnly ? "Y" : "N";
Debug.log(" - context.isPortletFormDisabled " + context.isPortletFormDisabled);