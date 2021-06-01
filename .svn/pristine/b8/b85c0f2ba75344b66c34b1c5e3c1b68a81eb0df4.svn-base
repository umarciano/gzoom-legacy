import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

import com.mapsengineering.workeffortext.services.trans.AccountFilterEnum;

def transactionPanelList = [];
def periodList = [];
def glFiscalTypeList = [];
def glAccountDescr = "";

def workEffortMeasureId = UtilValidate.isNotEmpty(context.workEffortMeasureId) ? context.workEffortMeasureId : parameters.workEffortMeasureId;

def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : workEffortMeasureId], false);

def parentWorkEffortTypeId = "";

def localeSecondarySet = context.localeSecondarySet;

values = AccountFilterEnum.values();
try {
    AccountFilterEnum.valueOf(parameters.accountFilter);
} catch (e) {
    def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale);
    parameters.errorLoadMeasure = uiLabelMap.ErrorLoadTrans;
    parameters.errorLoadMeasureDescr = uiLabelMap.ErrorLoadTransDescr_accountFilter;
    return;
}

if(UtilValidate.isNotEmpty(workEffortMeasure)) {
	
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortMeasure.workEffortId], false);
	def parentWorkEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffort.workEffortParentId], false);
	parentWorkEffortTypeId = parentWorkEffort.workEffortTypeId; 
	
	periodList = delegator.findList("CustomTimePeriodAndMeasureProjectView", EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasureId), null, ["thruDate"], null, false);
	
	if (UtilValidate.isEmpty(periodList)) {
		Debug.log(" - ERROR not exists CustomTimePeriodAndMeasureProjectView for workEffortMeasureId " + workEffortMeasureId + " with periodTypeId = " +  workEffortMeasure.periodTypeId +" and thruDate " + workEffortMeasure.thruDate);
		def periodTypeError = delegator.findOne("PeriodType", ["periodTypeId": workEffortMeasure.periodTypeId], true);
	    thruDateError = UtilDateTime.toDateString(workEffortMeasure.thruDate, locale);
	    def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale, ["periodTypeDescError" : periodTypeError.description, "thruDateError" : thruDateError]);
	    parameters.errorLoadMeasure = uiLabelMap.ErrorLoadMeasure;
		parameters.errorLoadMeasureDescr = uiLabelMap.ErrorLoadMeasureDescr;
		return;
	}
	
	//5214 punto 3
	def glAccount = delegator.getRelatedOne("GlAccount", workEffortMeasure);
	glAccountDescr = "Y".equals(localeSecondarySet) ? glAccount.descriptionLang : glAccount.description;
	def inputEnumId = glAccount.inputEnumId;
	glFiscalTypeList = delegator.findList("GlAccountTypeGlFiscalTypeView", EntityCondition.makeCondition("glAccountTypeId", glAccount.glAccountTypeId), null, ["sequenceId"], null, false);
	
	for(period in periodList) {
		for(glFiscalType in glFiscalTypeList) {
			def periodMap = period.getAllFields();
			periodMap.glFiscalTypeId = glFiscalType.glFiscalTypeId;
			periodMap.glFiscalTypeEnumId = glFiscalType.glFiscalTypeEnumId;
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
			
			transactionPanelList.add(periodMap);
		}
	}
	
	def ctxMap = context;
	ctxMap.parameters.workEffortId = workEffortMeasure.workEffortId;
	ctxMap.parameters.workEffortMeasureId = workEffortMeasureId;
	
	def valueIndicList = null;	
	
	if("IndicatorProject".equals(parameters.reloadRequestType)) {
		ctxMap.parameters.layoutType = parameters.contentIdInd;
		GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindValueIndicator.groovy", ctxMap);
		valueIndicList = ctxMap.listIt;
	}
	if("ResourceProject".equals(parameters.reloadRequestType)) {
	    /** Recupero Misure e Movimenti */
		serviceMap = ["workEffortId": workEffortMeasure.workEffortId,
		              "workEffortMeasureId": workEffortMeasureId,
		              "glAccountId": workEffortMeasure.glAccountId,
		              "timeZone": context.timeZone];

		Debug.log(" Run sync service executeChildPerformFindResourceValue with "+ serviceMap + ", userLogin=" + context.userLogin.userLoginId);
		serviceMap.put("userLogin", context.userLogin);

		def res = dispatcher.runSync("executeChildPerformFindResourceValue", serviceMap);
		if(UtilValidate.isNotEmpty(res.rowList)) {
			valueIndicList = res.rowList;
		} else {
			valueIndicList = [];
		}
	}

	boolean hasBudget = false;
	
	for(panelItem in transactionPanelList) {
		panelItem.hasMandatoryBudgetEmpty = false; // default
        if ("ACTUAL".equals(panelItem.glFiscalTypeId) && !"WECONVER_NOCONVERSIO".equals(workEffortMeasure.weScoreConvEnumId)) {
            panelItem.hasMandatoryBudgetEmpty = true; // forzo problema
	    }
	    for(valueIndic in valueIndicList) {
			//prima LEFT JOIN
			if(valueIndic.weTransTypeValueId.equals(panelItem.glFiscalTypeId) && valueIndic.weTransDate.equals(panelItem.thruDate)) {
				if(!"RATING_SCALE".equals(valueIndic.weTransUomType)) {
					panelItem.weTransValue = valueIndic.weTransValue;
				} else {
					panelItem.weTransValue = "Y".equals(localeSecondarySet) ? valueIndic.weTransValueCodeLang : valueIndic.weTransValueCode;
				}
				panelItem.weTransUomType = valueIndic.weTransUomType;
				panelItem.isPosted = valueIndic.isPosted;
				def isFinancial = UtilValidate.isEmpty(valueIndic.weTransMeasureId) && workEffortMeasure.glAccountId.equals(valueIndic.weAcctgTransAccountFinId) ? "Y":"N";
				if (isFinancial) {
				    Debug.log("panelItem.isReadOnly = " + isFinancial + " because valueIndic.weTransMeasureId = " + valueIndic.weTransMeasureId + " - workEffortMeasure.glAccountId = " + workEffortMeasure.glAccountId + " - valueIndic.weAcctgTransAccountFinId " + valueIndic.weAcctgTransAccountFinId);
				}
				panelItem.isReadOnly = isFinancial;
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
		prilConditionList.add(EntityCondition.makeCondition("workEffortTypeId", parentWorkEffortTypeId));
		prilConditionList.add(EntityCondition.makeCondition("customTimePeriodId", panelItem.customTimePeriodId));
		prilConditionList.add(EntityCondition.makeCondition("glFiscalTypeEnumId", panelItem.glFiscalTypeEnumId));
		prilConditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
		def prilList = delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(prilConditionList), null, null, null, false);
		def prilStatusSet = UtilMisc.toSet("OPEN", "REOPEN", "DETECTABLE");
		//Debug.log("Found " + prilList.size() + " WorkEffortTypePeriod with condition " + prilConditionList);
		
			
		for(pril in prilList) {
			panelItem.isRil = prilStatusSet.contains(pril.statusEnumId) ? "Y" : "N";
	        //Debug.log("panelItem.isRil = " + panelItem.isRil + " because period " + pril.workEffortTypePeriodId + " has statusEnumId = " + pril.statusEnumId);
			panelItem.glFiscalTypeEnumId = pril.glFiscalTypeEnumId;
			panelItem.workEffortTypeId = pril.workEffortTypeId;
			panelItem.customTimePeriodId = pril.customTimePeriodId;
		}
		
        //GN-1265 - controllo se sono responsabile sia dell'indicatore che della scheda
        context.glAccount = glAccount;
        context.we = workEffort;
        context.workEffortMeasure = workEffortMeasure;
        GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkWorkEffortMeasureIndicatorResponsible.groovy", context);
 
	    //Aggiungo alla disabilitazione anche glFiscalTypeEnumId='GLFISCTYPE_ACTUAL' commentata la condizione "GLFISCTYPE_ACTUAL".equals(panelItem.glFiscalTypeEnumId BUG 5630
        def disabilitato = (parameters.onlyWithBudget == "Y" && !context.isPermission && panelItem.hasMandatoryBudgetEmpty) || context.isReadOnlyIndicatorResponsable || "Y".equals(panelItem.isReadOnly) || ("Y".equals(panelItem.isPosted) || "N".equals(panelItem.isRil)) ? "Y" : "N";
        Debug.log("panelItem.glFiscalTypeEnumId = " + panelItem.glFiscalTypeEnumId + " panelItem.customTimePeriodId = " + panelItem.customTimePeriodId + " panelItem.isReadOnly = " + disabilitato + " because context.isReadOnlyIndicatorResponsable = " + context.isReadOnlyIndicatorResponsable + " - panelItem.isReadOnly = " + panelItem.isReadOnly + " - panelItem.isPosted = " + panelItem.isPosted + " panelItem.isRil = " + panelItem.isRil + " parameters.onlyWithBudget = " + parameters.onlyWithBudget + " context.isPermission = " + context.isPermission + " & panelItem.hasMandatoryBudgetEmpty = " + panelItem.hasMandatoryBudgetEmpty);
        panelItem.isReadOnly = disabilitato;
        
        panelItem.weTransMeasureId = workEffortMeasureId;
		/*GN-324*/
		if("ACCINP_PRD".equals(inputEnumId)){
			panelItem.weTransProductId = workEffortMeasure.productId;
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

context.transactionPanelList = transactionPanelList;
context.glFiscalTypeList = glFiscalTypeList;
context.periodList = periodList;
context.glAccountDescr = glAccountDescr;
context.parentWorkEffortTypeId = parentWorkEffortTypeId;
