import java.util.*;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import com.mapsengineering.workeffortext.services.trans.AccountFilterEnum;

def transactionPanelMap = [:];
def totalMap = [:];
def showTotal = false;
def rootWorkEffortTypeId = "";

def measureForProduct = null;

def workEffortMeasureId = UtilValidate.isNotEmpty(context.workEffortMeasureId) ? context.workEffortMeasureId : parameters.workEffortMeasureId;

def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : workEffortMeasureId], false);

def localeSecondarySet = context.localeSecondarySet;

Debug.log(" - getWorkEffortMeasureIndicatorDetailTransactionPanelData.groovy workEffortMeasureId " + workEffortMeasureId);

if(UtilValidate.isNotEmpty(workEffortMeasure)) {
	def g = delegator.findOne("GlAccount", ["glAccountId" : workEffortMeasure.glAccountId], false);
	if("ACCDET_SUM".equals(g.detailEnumId)) {
		showTotal = true;
	}
	if("ACCINP_PRD".equals(g.inputEnumId)) {
	    measureForProduct = EntityUtil.getFirst(delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("productId", workEffortMeasure.productId)), null, null, null, false));
	}
	workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortMeasure.workEffortId], false);
	def rootWorkEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffort.workEffortParentId], false);
	rootWorkEffortTypeId = rootWorkEffort.workEffortTypeId;
}

values = AccountFilterEnum.values();
try {
    AccountFilterEnum.valueOf(parameters.accountFilter);
} catch (e) {
    def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale);
    parameters.errorLoadMeasure = uiLabelMap.ErrorLoadTrans;
    parameters.errorLoadMeasureDescr = uiLabelMap.ErrorLoadTransDescr_accountFilter;
    return;
}

//Cerco i periodi
def fromDate = workEffortMeasure.fromDate;
def actualPeriod = delegator.findOne("WorkEffortTypePeriodAndCustomTime", ["workEffortMeasureId": workEffortMeasureId], false);
def dayBefore = null;
def tp0 = null;
if(UtilValidate.isNotEmpty(actualPeriod)) {
	//7 periodi precedenti all'attuale
	//tp0 = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": actualPeriod.customTimePeriodId], false);
	def actualCustomTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": actualPeriod.customTimePeriodId], false);
	tp0 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", workEffortMeasure.periodTypeId), EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, actualCustomTimePeriod.thruDate)),null, ["-thruDate"], null, false));
    if (UtilValidate.isEmpty(tp0)) {
        thruDateError = UtilDateTime.toDateString(actualCustomTimePeriod.thruDate, locale);
        Debug.log(" - ERROR not exists CustomTimePeriod with periodTypeId " + workEffortMeasure.periodTypeId + " and thruDate <= " + actualCustomTimePeriod.thruDate);
    }
}
else {
	//7 periodi precedenti alla data fine misura
	tp0 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", workEffortMeasure.periodTypeId), EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, workEffortMeasure.thruDate)),null, ["-thruDate"], null, false));
    if (UtilValidate.isEmpty(tp0)) {
        thruDateError = UtilDateTime.toDateString(workEffortMeasure.thruDate, locale);
        Debug.log(" - ERROR not exists CustomTimePeriod with periodTypeId " + workEffortMeasure.periodTypeId + " and thruDate <= " + workEffortMeasure.thruDate);
    }
}

if (UtilValidate.isEmpty(tp0)) {
    def periodTypeError = delegator.findOne("PeriodType", ["periodTypeId": workEffortMeasure.periodTypeId], true);
    def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale, ["periodTypeDescError" : periodTypeError.description, "thruDateError" : thruDateError]);
	parameters.errorLoadMeasure = uiLabelMap.ErrorLoadMeasure;
	parameters.errorLoadMeasureDescr = uiLabelMap.ErrorLoadMeasureDescr;
	return;
}

dayBefore = Calendar.getInstance();
dayBefore.setTime(tp0.fromDate);
dayBefore.add(Calendar.DATE, -1);
def tp1 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
if(UtilValidate.isNotEmpty(tp1)) {
	fromDate = tp1.fromDate;
	dayBefore = Calendar.getInstance();
	dayBefore.setTime(tp1.fromDate);
	dayBefore.add(Calendar.DATE, -1);
	def tp2 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
	if(UtilValidate.isNotEmpty(tp2)) {
		fromDate = tp2.fromDate;
		dayBefore = Calendar.getInstance();
		dayBefore.setTime(tp2.fromDate);
		dayBefore.add(Calendar.DATE, -1);
		def tp3 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
		if(UtilValidate.isNotEmpty(tp3)) {
			fromDate = tp3.fromDate;
			dayBefore = Calendar.getInstance();
			dayBefore.setTime(tp3.fromDate);
			dayBefore.add(Calendar.DATE, -1);
			def tp4 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
			if(UtilValidate.isNotEmpty(tp4)) {
				fromDate = tp4.fromDate;
				dayBefore = Calendar.getInstance();
				dayBefore.setTime(tp4.fromDate);
				dayBefore.add(Calendar.DATE, -1);
				def tp5 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
				if(UtilValidate.isNotEmpty(tp5)) {
					fromDate = tp5.fromDate;
					dayBefore = Calendar.getInstance();
					dayBefore.setTime(tp5.fromDate);
					dayBefore.add(Calendar.DATE, -1);
					def tp6 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
					if(UtilValidate.isNotEmpty(tp6)) {
						fromDate = tp6.fromDate;
						dayBefore = Calendar.getInstance();
						dayBefore.setTime(tp6.fromDate);
						dayBefore.add(Calendar.DATE, -1);
						def tp7 = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", tp0.periodTypeId), EntityCondition.makeCondition("thruDate", dayBefore.getTime())), null, ["-thruDate"], null, false));
						if(UtilValidate.isNotEmpty(tp7)) {
							fromDate = tp7.fromDate;
						}
					}
				}
			}
		}
	}
}

//da adesso tutto procede come per i progetti
def periodList = delegator.findList("CustomTimePeriodAndMeasureProcessView", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasureId), EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)), null, ["thruDate"], null, false);


//**********************************************************************
//5214 punto 3
def glAccount = delegator.getRelatedOne("GlAccount", workEffortMeasure);
def inputEnumId = glAccount.inputEnumId;

//GN-1265 - controllo se sono responsabile sia dell'indicatore che della scheda e lo metto nel context.isReadOnlyIndicatorResponsable
context.glAccount = glAccount;
context.we = workEffort;
context.workEffortMeasure = workEffortMeasure;
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkWorkEffortMeasureIndicatorResponsible.groovy", context);

def glFiscalTypeList = delegator.findList("GlAccountTypeGlFiscalTypeView", EntityCondition.makeCondition("glAccountTypeId", glAccount.glAccountTypeId), null, ["sequenceId"], null, true);

def destinationColumnDescription = "";
for(glAccountRole in context.destinationList) {
	transactionPanelMap[glAccountRole.partyId] = [];
	for(period in periodList) {
		for(glFiscalType in glFiscalTypeList) {
			def periodMap = period.getAllFields();
			periodMap.glFiscalTypeId = glFiscalType.glFiscalTypeId;
			periodMap.glFiscalTypeEnumId = glFiscalType.glFiscalTypeEnumId;
			periodMap.entryPartyId = glAccountRole.partyId;
			periodMap.entryRoleTypeId = glAccountRole.roleTypeId;
			periodMap.glFiscalTypeDescription = glFiscalType.description;
			
			// aggiungo valori di default per panelItem.valModId
			def datasource = delegator.getRelatedOne("DataSource", glAccount);
			if (UtilValidate.isNotEmpty(datasource)) {
				periodMap.valModId = datasource.valModId;
				
				if (inputEnumId == "ACCINP_PRD") {
					datasource = delegator.getRelatedOne("DataSource", workEffortMeasure);
					if (UtilValidate.isNotEmpty(datasource)) {
						periodMap.valModId = datasource.valModId;
					}
				}
			}
			transactionPanelMap[glAccountRole.partyId].add(periodMap);
		}
	}
	if(UtilValidate.isEmpty(destinationColumnDescription)) {
		destinationColumnDescription = glAccountRole.description;
	}
	if(!destinationColumnDescription.equals(glAccountRole.description)) {
		destinationColumnDescription = UtilProperties.getPropertyValue("WorkeffortExtUiLabels", "DestinationColumn");
	}
}

def ctxMap = context;
ctxMap.parameters.workEffortId = workEffortMeasure.workEffortId;
ctxMap.parameters.workEffortMeasureId = workEffortMeasure.workEffortMeasureId;
ctxMap.parameters.layoutType = parameters.contentIdInd;
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindValueIndicator.groovy", ctxMap);
def valueIndicList = ctxMap.listIt;

def destinationIterator =  transactionPanelMap.keySet().iterator()
while(destinationIterator.hasNext()) {
	def destinationId = destinationIterator.next();
	
	boolean hasBudget = false;

    for(panelItem in transactionPanelMap[destinationId]) {
		panelItem.hasMandatoryBudgetEmpty = false; // default
        if ("ACTUAL".equals(panelItem.glFiscalTypeId) && !"WECONVER_NOCONVERSIO".equals(workEffortMeasure.weScoreConvEnumId)) {
            panelItem.hasMandatoryBudgetEmpty = true; // forzo problema
        }
        
		for(valueIndic in valueIndicList) {
		    if(!destinationId.equals(valueIndic.entryPartyId)) {
				continue;
			}
			//prima LEFT JOIN
			if(valueIndic.weTransTypeValueId.equals(panelItem.glFiscalTypeId) && valueIndic.weTransDate.equals(panelItem.thruDate)) {
				if(!"RATING_SCALE".equals(valueIndic.weTransUomType)) {
					panelItem.weTransValue = valueIndic.weTransValue;
				}
				else {
					panelItem.weTransValue = "Y".equals(localeSecondarySet) ? valueIndic.weTransValueCodeLang : valueIndic.weTransValueCode;
				}
				panelItem.weTransUomType = valueIndic.weTransUomType;
				panelItem.partyId = valueIndic.entryPartyId;
				panelItem.isPosted = valueIndic.isPosted;
				panelItem.weTransId = valueIndic.weTransId;
				panelItem.weTransEntryId = valueIndic.weTransEntryId;				

				if ("BUDGET".equals(panelItem.glFiscalTypeId)) {
                    hasBudget = true;
                }

				panelItem.hasComments = ((UtilValidate.isNotEmpty(valueIndic.weTransComments) && UtilValidate.isNotEmpty(valueIndic.weTransComments.trim()))
						|| (UtilValidate.isNotEmpty(valueIndic.weTransComment) && UtilValidate.isNotEmpty(valueIndic.weTransComment.trim()))
						|| (UtilValidate.isNotEmpty(valueIndic.weTransCommentsLang) && UtilValidate.isNotEmpty(valueIndic.weTransCommentsLang.trim()))
						|| (UtilValidate.isNotEmpty(valueIndic.weTransCommentLang) && UtilValidate.isNotEmpty(valueIndic.weTransCommentLang.trim()))) ? "Y" : "N";				
						
				if (inputEnumId == "ACCINP_PRD") {
					panelItem.valModId = valueIndic.wmValModId;
				} else {
					panelItem.valModId = valueIndic.glValModId;
				}
				
				if(UtilValidate.isEmpty(totalMap[panelItem.glFiscalTypeId + panelItem.customTimePeriodId])) {
					totalMap[panelItem.glFiscalTypeId + panelItem.customTimePeriodId] = panelItem.weTransValue;
				}
				else {
					totalMap[panelItem.glFiscalTypeId + panelItem.customTimePeriodId] += panelItem.weTransValue;
				}
			}
		}
		if (panelItem.hasMandatoryBudgetEmpty) {
            if(hasBudget) {
                panelItem.hasMandatoryBudgetEmpty = false;
                hasBudget = false;
            }
        }
		//seconda LEFT JOIN
		def prilConditionList = [];
		prilConditionList.add(EntityCondition.makeCondition("workEffortTypeId", rootWorkEffortTypeId));
		prilConditionList.add(EntityCondition.makeCondition("customTimePeriodId", panelItem.customTimePeriodId));
		prilConditionList.add(EntityCondition.makeCondition("glFiscalTypeEnumId", panelItem.glFiscalTypeEnumId));
		prilConditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
		def prilList = delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(prilConditionList), null, null, null, false);
		def prilStatusSet = UtilMisc.toSet("OPEN", "REOPEN", "DETECTABLE");
		// Debug.log("Found " + prilList.size() + " WorkEffortTypePeriod with condition " + prilConditionList);
		
		for(pril in prilList) {
			panelItem.isRil = prilStatusSet.contains(pril.statusEnumId) ? "Y" : "N";
	        // Debug.log("panelItem.isRil = " + panelItem.isRil + " because period " + pril.workEffortTypePeriodId + " has statusEnumId = " + pril.statusEnumId);
			panelItem.glFiscalTypeEnumId = pril.glFiscalTypeEnumId;
			panelItem.workEffortTypeId = pril.workEffortTypeId;
			panelItem.customTimePeriodId = pril.customTimePeriodId;
		}
        
	    def disabilitato = (parameters.onlyWithBudget == "Y" && !context.isPermission && panelItem.hasMandatoryBudgetEmpty) || context.isReadOnlyIndicatorResponsable || "Y".equals(panelItem.isReadOnly) || ("Y".equals(panelItem.isPosted) || "N".equals(panelItem.isRil)) ? "Y" : "N";
        Debug.log("panelItem.isReadOnly = " + disabilitato + " because context.isReadOnlyIndicatorResponsable = " + context.isReadOnlyIndicatorResponsable + " - panelItem.isReadOnly = " + panelItem.isReadOnly + " - panelItem.isPosted = " + panelItem.isPosted + " panelItem.isRil = " + panelItem.isRil + " parameters.onlyWithBudget = " + parameters.onlyWithBudget + " context.isPermission = " + context.isPermission + " & panelItem.hasMandatoryBudgetEmpty = " + panelItem.hasMandatoryBudgetEmpty);
        panelItem.isReadOnly = disabilitato;
        
		panelItem.weTransMeasureId = workEffortMeasureId;
		
        
		/*GN-324*/
		if("ACCINP_PRD".equals(inputEnumId)){
			panelItem.weTransProductId = workEffortMeasure.productId;
			if(UtilValidate.isNotEmpty(measureForProduct)) {
			    panelItem.voucherRef = measureForProduct.workEffortMeasureId;
			}
		}
		panelItem.weTransTypeValueId = panelItem.glFiscalTypeId;
		panelItem.weTransWeId = workEffortMeasure.workEffortId;

		if(UtilValidate.isNotEmpty(context.crudEnumIdSecondary)) {
			panelItem.crudEnumId = context.crudEnumIdSecondary;
		}
		if(context.isReadOnly == true || "Y".equals(context.isReadOnly) || "true".equals(parameters.isReadOnly) || "Y".equals(parameters.isReadOnly)) {
		    panelItem.isReadOnly = "Y";
		}
	}
}

context.transactionPanelMap = transactionPanelMap;
context.glFiscalTypeList = glFiscalTypeList;
context.periodList = periodList;
context.destinationColumnDescription = destinationColumnDescription;
context.totalMap = totalMap;
context.showTotal = showTotal;