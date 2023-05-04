import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ModelService;
import com.mapsengineering.base.find.AcctgTransFindServices;
import com.mapsengineering.workeffortext.util.*;

def parametersCrud = parameters;
def parameters = parameters.parameters;

def returnMap = ServiceUtil.returnError("ERROR");

serviceName = "crudServiceDefaultOrchestration_AcctgTransAndEntries";
def localParameters = dctx.makeValidContext(serviceName, ModelService.IN_PARAM, context);

/*
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransUomType " + parameters.weTransUomType);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransCurrencyUomId " + parameters.weTransCurrencyUomId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransValue " + parameters.weTransValue);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransWeId " + parameters.weTransWeId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransAccountId " + parameters.weTransAccountId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransTypeValueId " + parameters.weTransTypeValueId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.workEffortMeasureId " + parameters.workEffortMeasureId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.localeSecondarySet " + parameters.localeSecondarySet);
Debug.log("crudWorkEffortTransactionView.groovy parameters.partyId " + parameters.partyId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.roleTypeId " + parameters.roleTypeId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.entryPartyId " + parameters.entryPartyId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.entryRoleTypeId " + parameters.entryRoleTypeId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.defaultOrganizationPartyId " + parameters.defaultOrganizationPartyId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.customTimePeriodId " + parameters.customTimePeriodId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransDate " + parameters.weTransDate);
*/
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
/** entryPartyId inteso come ACCTG_TRANS_ENTRY.PARTY_ID e' stato deprecato,
 * in fase di salvataggio questo campo viene passato SEMPRE vuoto al servizio,
 * in fase di lettura, invece viene utilizzato per contenere il partyId del dettaglio
 * nel caso in cui vengono mostrati i diversi soggetti legati ad un indicatore */
localParameters.entryPartyId = null;
/*if (UtilValidate.isNotEmpty(parameters.entryPartyId)) {
	localParameters.entryPartyId = parameters.entryPartyId;
}
if (UtilValidate.isNotEmpty(parameters.entryRoleTypeId)) {
	localParameters.entryRoleTypeId = parameters.entryRoleTypeId;
}*/

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
localParameters.customTimePeriodId = parameters.customTimePeriodId;

def conditionIsUpdate = acctgTransFindServices.getConditionIsUpdate(localParameters.partyId, localParameters.entryPartyId, weTransDate, parameters.weTransTypeValueId, parameters.weTransMeasureId, parameters.weTransWeId, parameters.weTransAccountId, parameters.transProductId);

List<GenericValue> acctgTransAndEntriesViews = delegator.findList("AcctgTransAndEntriesView", EntityCondition.makeCondition(conditionIsUpdate) , null, null, null, false);
GenericValue acctgTransAndEntriesView = EntityUtil.getFirst(acctgTransAndEntriesViews);
Debug.log("For conditionIsUpdate " + conditionIsUpdate + " found  acctgTransAndEntriesView = " + acctgTransAndEntriesView);


Debug.log("crudWorkEffortTransactionView.groovy ****************** context.operation " + context.operation);
Debug.log("crudWorkEffortTransactionView.groovy ****************** localParameters.weTransValue " + localParameters.weTransValue);

// da mappa arriva sempre context.operation = "UPDATE" oppure context.operation = "DELETE", quindi
def operation = "CREATE";
if(UtilValidate.isNotEmpty(acctgTransAndEntriesView)) {
    Debug.log("crudWorkEffortTransactionView.groovy ****************** esiste 1 movimento quindi ... context.operation " + context.operation + " - localParameters.weTransValue " + localParameters.weTransValue);
    if("DELETE".equals(context.operation) || UtilValidate.isEmpty(localParameters.weTransValue)) {
		operation = "DELETE";
		Debug.log("crudWorkEffortTransactionView.groovy ****************** esiste movimento ma operation " + context.operation + " e value " + localParameters.weTransValue + " quindi operation " + operation);
	} else {
		operation = "UPDATE";
		Debug.log("crudWorkEffortTransactionView.groovy ****************** esiste movimento and value " + localParameters.weTransValue+ " quindi operation " + operation);
	    
	}
	localParameters.weTransId = acctgTransAndEntriesView.acctgTransId;
	localParameters.weTransEntryId = acctgTransAndEntriesView.entryAcctgTransEntrySeqId;
} else {
    Debug.log("crudWorkEffortTransactionView.groovy ****************** non esiste nessun movimento quindi operation " + operation);
    localParameters._AUTOMATIC_PK_ = "Y";
}
Debug.log("crudWorkEffortTransactionView.groovy ****************** operation " + operation);
localParameters.operation = operation;
localParameters.defaultOrganizationPartyId = parameters.defaultOrganizationPartyId;


Debug.log("crudWorkEffortTransactionView.groovy recupero params... ");
/*Debug.log("crudWorkEffortTransactionView.groovy parameters.contentIdInd " + parameters.contentIdInd);
Debug.log("crudWorkEffortTransactionView.groovy context.weTransWeId " + context.weTransWeId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.weTransWeId " + parameters.weTransWeId);
Debug.log("crudWorkEffortTransactionView.groovy parameters.workEffortTypeId " + parameters.workEffortTypeId);
*/
amountWeAssocType = "";
Debug.log("crudWorkEffortTransactionView.groovy amountWeAssocType " + amountWeAssocType);
Debug.log("crudWorkEffortTransactionView.groovy parameters.contentIdInd " + parameters.contentIdInd);
Debug.log("crudWorkEffortTransactionView.groovy parameters.workEffortTypeId " + parameters.workEffortTypeId);
if (UtilValidate.isNotEmpty(parameters.contentIdInd)) {
    if ("WEFLD_IND".equals(parameters.contentIdInd)) {
        contentId = "WEFLD_AIND";
    }
    if ("WEFLD_IND2".equals(parameters.contentIdInd)) {
        contentId = "WEFLD_AIND2";
    }
    if ("WEFLD_IND3".equals(parameters.contentIdInd)) {
        contentId = "WEFLD_AIND3";
    }
    if ("WEFLD_IND4".equals(parameters.contentIdInd)) {
        contentId = "WEFLD_AIND4";
    }
    if ("WEFLD_IND5".equals(parameters.contentIdInd)) {
        contentId = "WEFLD_AIND5";
    }
    WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
    def mapParams = paramsEvaluator.getParams(parameters.workEffortTypeId, contentId, false);
    if (UtilValidate.isNotEmpty(mapParams)) {
        amountWeAssocType = mapParams.amountWeAssocType;
    }
}
Debug.log("crudWorkEffortTransactionView.groovy amountWeAssocType " + amountWeAssocType);
if (UtilValidate.isNotEmpty(amountWeAssocType)) {
    groovyContext = new java.util.HashMap();
    groovyContext.put("dispatcher", dispatcher);
    groovyContext.put("delegator", delegator);
    groovyContext.put("locale", locale);
    groovyContext.put("timeZone", timeZone);
    groovyContext.put("parameters", parameters);
    groovyContext.put("dctx", dctx);
    groovyContext.put("operation", operation);
    groovyContext.put("workEffortAssocTypeId", amountWeAssocType);
    groovyContext.putAll(localParameters);
    // Debug.log("crudWorkEffortTransactionView.groovy che params mettere nel groovyContext " + groovyContext);

    long startTime = System.currentTimeMillis();
    serviceResult = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executeAmountWeAssocType.groovy", groovyContext);
    long endTime = System.currentTimeMillis();
    Debug.log("crudWorkEffortTransactionView.groovy dopo serviceResult " + serviceResult);
    if (ServiceUtil.isError(serviceResult)) {
        Debug.log("crudWorkEffortTransactionView.groovy error");
        return serviceResult;
    }
}

//i valori si trovano nei parameters, ma esssendo un crud, il makeValidContext non li riesce a recuperare 
//def serviceMap = dctx.makeValidContext(serviceName, ModelService.IN_PARAM, context);
serviceMap = [:];
serviceMap.put("entityName", "WorkEffortTransactionView");
serviceMap.put("operation", operation);
serviceMap.put("userLogin", userLogin);
serviceMap.put("parameters", localParameters);
serviceMap.put("locale", locale);
serviceMap.put("timeZone", timeZone);

Debug.log("crudWorkEffortTransactionView.groovy principale serviceMap " + serviceMap);
returnMap = dctx.getDispatcher().runSync(serviceName, serviceMap);
Debug.log("crudWorkEffortTransactionView.groovy principale returnMap " + returnMap);
return returnMap;