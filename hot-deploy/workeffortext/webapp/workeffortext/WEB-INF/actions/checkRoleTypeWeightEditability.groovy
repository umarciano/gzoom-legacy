import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;


def canModifyRoleTypeWeight = "N";
def canModifyRoleTypeWeightActual = "N";


/*
 * gn2046 se sono nel dettaglio devo fare evaluation dei parametri
 * per sapere se e' always
 */
if(! "Y".equals(parameters.isObiettivo) && "Y".equals(parameters.detail)) {
	def workEffortTypeId = UtilValidate.isNotEmpty(parameters.workEffortTypeId) ? parameters.workEffortTypeId: context.workEffortTypeId;
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(workEffortTypeId, false);
}

def showRoleTypeWeight = UtilValidate.isNotEmpty(context.showRoleTypeWeight) ? context.showRoleTypeWeight : "Y";
def showRoleTypeWeightActual = UtilValidate.isNotEmpty(context.showRoleTypeWeightActual) ? context.showRoleTypeWeightActual : "N";

if("Y".equals(parameters.isObiettivo) || "ALWAYS".equalsIgnoreCase(showRoleTypeWeight)) {
	canModifyRoleTypeWeight = "Y";
} else if ("Y".equalsIgnoreCase(showRoleTypeWeight) || "Y".equals(parameters.detail)) {
	def workEffortTypePeriodId = getWorkEffortTypePeriodId();
	if(UtilValidate.isNotEmpty(workEffortTypePeriodId)) {
		def workEffortTypePeriod = delegator.findOne("WorkEffortTypePeriod", ["workEffortTypePeriodId" : workEffortTypePeriodId], false);
		canModifyRoleTypeWeight = isFieldEditable(workEffortTypePeriod, "GLFISCTYPE_TARGET");
	} else {
		canModifyRoleTypeWeight = "Y";
	}
} else {
	canModifyRoleTypeWeight = "N";
}

if("Y".equals(parameters.isObiettivo) || "ALWAYS".equalsIgnoreCase(showRoleTypeWeightActual)) {
	canModifyRoleTypeWeightActual = "Y";
} else if ("Y".equalsIgnoreCase(showRoleTypeWeightActual) || "Y".equals(parameters.detail)) {
	def workEffortTypePeriodId = getWorkEffortTypePeriodId();
	if(UtilValidate.isNotEmpty(workEffortTypePeriodId)) {
		def workEffortTypePeriod = delegator.findOne("WorkEffortTypePeriod", ["workEffortTypePeriodId" : workEffortTypePeriodId], false);
		canModifyRoleTypeWeightActual = isFieldEditable(workEffortTypePeriod, "GLFISCTYPE_ACTUAL");
	} else {
		canModifyRoleTypeWeightActual = "Y";
	}
} else {
	canModifyRoleTypeWeightActual = "N";
}


context.canModifyRoleTypeWeight = canModifyRoleTypeWeight;
context.canModifyRoleTypeWeightActual = canModifyRoleTypeWeightActual;


def getWorkEffortTypePeriodId() {
	def workEffortIdRoot = UtilValidate.isNotEmpty(context.workEffortIdRoot) ? context.workEffortIdRoot : parameters.workEffortIdRoot;
	if(UtilValidate.isNotEmpty(workEffortIdRoot)) {
		def workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId" : workEffortIdRoot], false);
		return UtilValidate.isNotEmpty(workEffortRoot) ? workEffortRoot.workEffortTypePeriodId : "";
	}
	return "";
}

def isFieldEditable(workEffortTypePeriod, glFiscalTypeEnumId) {
	if(UtilValidate.isEmpty(workEffortTypePeriod)) {
		return "Y";
	}
	return glFiscalTypeEnumId.equals(workEffortTypePeriod.glFiscalTypeEnumId) &&\
	       ("OPEN".equals(workEffortTypePeriod.statusEnumId) ||\
	    	"REOPEN".equals(workEffortTypePeriod.statusEnumId) ||\
	    	"DETECTABLE".equals(workEffortTypePeriod.statusEnumId)) ? "Y" : "N";
}
