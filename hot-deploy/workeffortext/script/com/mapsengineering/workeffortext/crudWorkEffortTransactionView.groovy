import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ModelService;
import com.mapsengineering.base.find.AcctgTransFindServices;

def parametersCrud = parameters;
def parameters = parameters.parameters;

def returnMap = ServiceUtil.returnError("ERROR");

serviceName = "crudServiceDefaultOrchestration_AcctgTransAndEntries";
def localParameters = dctx.makeValidContext(serviceName, ModelService.IN_PARAM, context);

if ("RATING_SCALE" == parameters.weTransUomType) {
	uomRatingScaleList = delegator.findList("UomRatingScale", EntityCondition.makeCondition("uomId", parameters.weTransCurrencyUomId), null, null, null, false);
	def stringa = null;
	uomRatingScaleList.each { uomRatingScale ->
		if ("RADIO_BUTTON" == parameters.modello) {
			stringa = "weTransValue_" + uomRatingScale.uomRatingValue;
			if(UtilValidate.isNotEmpty(parameters[stringa])) {
				def valore = parameters[stringa];
				localParameters.weTransValue = valore;
			}
		} else {
			localParameters.weTransValue = parameters.weTransValue;
		}
	}	
} else {
	localParameters.weTransValue = parameters.weTransValue;
}

localParameters.weTransAccountId = parameters.weTransAccountId;

localParameters.weTransWeId = parameters.weTransWeId;
localParameters.weTransTypeValueId = parameters.weTransTypeValueId;
localParameters.weTransCurrencyUomId = parameters.weTransCurrencyUomId;

def workEffortMeasureId = parameters.workEffortMeasureId;
def errorMessage = "";
def weTransValue = null;
try {
	weTransValue = ObjectType.simpleTypeConvert(localParameters.weTransValue, "Double", null, locale);
} catch(Exception e) {
	weTransValue = null;
}

if (UtilValidate.isNotEmpty(workEffortMeasureId)) {
	def glAccountMeasureUomViewList = delegator.findList("GlAccountMeasureUomView", EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasureId), null, null, null, false);
	def glAccountMeasureUomViewItem = EntityUtil.getFirst(glAccountMeasureUomViewList);
	if (UtilValidate.isNotEmpty(glAccountMeasureUomViewItem)) {
		if (UtilValidate.isNotEmpty(glAccountMeasureUomViewItem.minValue) && UtilValidate.isNotEmpty(glAccountMeasureUomViewItem.maxValue)) {
			if (glAccountMeasureUomViewItem.minValue != 0 || glAccountMeasureUomViewItem.maxValue != 0) {
				if (weTransValue != null) {
					if (weTransValue < glAccountMeasureUomViewItem.minValue || weTransValue > glAccountMeasureUomViewItem.maxValue) {
						if ("Y".equals(parameters.localeSecondarySet)) {
							accountName = glAccountMeasureUomViewItem.accountNameLang;
						} else {
							accountName = glAccountMeasureUomViewItem.accountName;
						}
						uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale, ["weTransValue" : weTransValue, "accountName" : accountName]);
                        errorMessage = "<br> - " + uiLabelMap.AmountOutOfUomRangeMeasure;
					}
				}
			}
		}		
	}
}

if (UtilValidate.isNotEmpty(errorMessage)) {
	return ServiceUtil.returnFailure(errorMessage);
}

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.weTransWeId], false);
if (UtilValidate.isNotEmpty(parameters.partyId)) {
	localParameters.partyId = parameters.partyId;
}
if (UtilValidate.isNotEmpty(parameters.roleTypeId)) {
	localParameters.roleTypeId = parameters.roleTypeId;
}
localParameters.entryPartyId = null;
if (UtilValidate.isNotEmpty(parameters.entryPartyId)) {
	localParameters.entryPartyId = parameters.entryPartyId;
}
if (UtilValidate.isNotEmpty(parameters.entryRoleTypeId)) {
	localParameters.entryRoleTypeId = parameters.entryRoleTypeId;
}

/** Modello su obiettivo */
if (UtilValidate.isNotEmpty(parameters.weTransMeasureId)) {
	localParameters.weTransMeasureId = parameters.weTransMeasureId;
}

GenericValue workEffortType = delegator.findOne("WorkEffortType", false, "workEffortTypeId", workEffort.workEffortTypeId);
localParameters.acctgTransTypeId = workEffortType.getString("parentTypeId");

AcctgTransFindServices acctgTransFindServices = new AcctgTransFindServices(delegator, parameters.defaultOrganizationPartyId);
def weTransDate = null;
if(UtilValidate.isEmpty(parameters.weTransDate)) {
    GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", false, "customTimePeriodId", parameters.customTimePeriodId);
    weTransDate = customTimePeriod.thruDate;
} else {
    weTransDate = ObjectType.simpleTypeConvert(parameters.weTransDate, "Timestamp", null, locale);
}
    
localParameters.weTransDate = weTransDate;

def conditionIsUpdate = acctgTransFindServices.getConditionIsUpdate(localParameters.partyId, localParameters.entryPartyId, weTransDate, parameters.weTransTypeValueId, parameters.weTransMeasureId, parameters.weTransWeId, parameters.weTransAccountId, parameters.transProductId);

List<GenericValue> acctgTransAndEntriesViews = delegator.findList("AcctgTransAndEntriesView", EntityCondition.makeCondition(conditionIsUpdate) , null, null, null, false);
GenericValue acctgTransAndEntriesView = EntityUtil.getFirst(acctgTransAndEntriesViews);
Debug.log("For conditionIsUpdate " + conditionIsUpdate + " found  acctgTransAndEntriesView = " + acctgTransAndEntriesView);

def operation = "CREATE";
if(UtilValidate.isNotEmpty(acctgTransAndEntriesView)) {
	if(UtilValidate.isNotEmpty(localParameters.weTransValue)) {
		operation = "UPDATE";
	} else {
		operation = "DELETE";
	}
	localParameters.weTransId = acctgTransAndEntriesView.acctgTransId;
	localParameters.weTransEntryId = acctgTransAndEntriesView.entryAcctgTransEntrySeqId;
} else {
	localParameters._AUTOMATIC_PK_ = "Y";
}
localParameters.operation = operation;
localParameters.defaultOrganizationPartyId = parameters.defaultOrganizationPartyId;

serviceMap = [:];
serviceMap.put("entityName", "WorkEffortTransactionView");
serviceMap.put("operation", operation);
serviceMap.put("userLogin", userLogin);
serviceMap.put("parameters", localParameters);
serviceMap.put("locale", locale);
serviceMap.put("timeZone", timeZone);

returnMap = dctx.getDispatcher().runSync(serviceName, serviceMap);

return returnMap;