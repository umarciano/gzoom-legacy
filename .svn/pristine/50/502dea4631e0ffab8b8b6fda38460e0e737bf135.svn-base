import java.util.Comparator;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.base.util.*;
import com.mapsengineering.base.birt.util.*;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import com.mapsengineering.workeffortext.services.trans.ExecuteChildPerformFindTransGroupIndicator;
import com.mapsengineering.workeffortext.services.trans.AccountFilterEnum;

/*
 * Se non sono un utente amministratore
 * 
 * Controllo se il sistema alimentante è  = 'INDICATOR_RESP' 
 * se si devo controllare che 
 * respCenterRoleTypeId = orgUnitRoleTypeId and respCenterId = orgUnitId
 * se si posso modificare sia l'indicatore che i valori
 * altrimenti deve essere readOnly
 * 
 */

def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", locale);
def uiLabelErrorMap = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale);

/** Recupero workEffort, workEffortType, workEffortRoot, workEffortTypePeriodId */
def workEffortView = delegator.findOne("WorkEffortAndTypePeriodAndCustomTime", ["workEffortId" : parameters.workEffortId], false);
def securityPermission = Utils.permissionLocalDispatcherName(dispatcher.name);


def checkWorkEffortPermissions() {
    return security.hasPermission("WORKEFFORTMGR_ADMIN", context.userLogin) || security.hasPermission("WORKEFFORTMGR_CREATE", context.userLogin) || security.hasPermission("WORKEFFORTMGR_UPDATE", context.userLogin) || security.hasPermission("WORKEFFORTORG_ADMIN", context.userLogin) || security.hasPermission("WORKEFFORTROLE_ADMIN", context.userLogin);
}
def checkWorkEffortPermissions = checkWorkEffortPermissions();
context.checkWorkEffortPermissions = checkWorkEffortPermissions;

def getAdminPermission(parentWorkEffortTypeId) {
    if ("Y".equals(parameters.specialized) && UtilValidate.isNotEmpty(parentWorkEffortTypeId)) {
        def parentWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : parentWorkEffortTypeId], false);
        if (UtilValidate.isNotEmpty(parentWorkEffortType)) {
            def weContextId = parentWorkEffortType.parentTypeId;
            return ContextPermissionPrefixEnum.getPermissionPrefix(weContextId) + "MGR_ADMIN";
        }
    }
    return "WORKEFFORTMGR_ADMIN";
}
def adminPermission = getAdminPermission(workEffortView.workEffortTypeRootId);
context.adminPermission = adminPermission;

/** Recupero Info per Periodo Rilevabile,
 *  nel caso di formato radioButton il glFiscalTypeId per adesso e' solo 1 quindi il recupero e' fatto nel groovy
 *  nel caso di formato standard il recupero e' fatto riga per riga nell'ftl */
context.workEffortView = workEffortView;
context.prilStatusSet = UtilMisc.toSet("OPEN", "REOPEN", "DETECTABLE");

/** Recupero layout e contentIdInd e contentIdSecondary */
def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromContext();
def mappaContent = ["WEFLD_AIND" : "WEFLD_IND", "WEFLD_AIND2" : "WEFLD_IND2", "WEFLD_AIND3" : "WEFLD_IND3", "WEFLD_AIND4" : "WEFLD_IND4", "WEFLD_AIND5" : "WEFLD_IND5"];
if(UtilValidate.isNotEmpty(layoutType)) {
	context.contentIdInd = mappaContent[layoutType];
	context.contentIdSecondary = layoutType;
	// set in parameters for script below
	parameters.contentIdInd = context.contentIdInd;
	parameters.contentIdSecondary = context.contentIdSecondary;
}

Debug.log(" - executeChildPerformFindTransIndicator.groovy parameters.workEffortId = " + parameters.workEffortId + ", layoutType = " + layoutType + ", parameters.contentIdInd = " + parameters.contentIdInd + "[" + parameters.contentIdSecondary + "]");

/** Recupero crudEnumId */
def crudEnumIdSecondary = "";
def childViewSec = delegator.findOne("WorkEffortTypeStatusCntChildView", ["workEffortId" : parameters.workEffortId, "contentId" : context.contentIdSecondary], false);
if (UtilValidate.isNotEmpty(childViewSec)) {
	// Debug.log("*****************************  childViewSec  "+ childViewSec );
	crudEnumIdSecondary = childViewSec.crudEnumId;
} else {
	def parentViewSec = delegator.findOne("WorkEffortTypeStatusCntParentView", ["workEffortId" : parameters.workEffortId, "contentId" : context.contentIdSecondary], false);
	if (UtilValidate.isNotEmpty(parentViewSec)) {
		//Debug.log("*****************************  parentView  "+ parentView );
		crudEnumIdSecondary = parentViewSec.crudEnumId;
	}
}
context.crudEnumIdSecondary = crudEnumIdSecondary;
Debug.log(" - executeChildPerformFindTransIndicator.groovy context.crudEnumIdSecondary = "+ context.crudEnumIdSecondary );
/** uso crudEnumIdSecondary, perche crudEnumId viene sovrascritto in altri punti */

context.accountTypeEnumId = "INDICATOR";

/** Recupero Data, a partire dalla data e dalla periodicita' calcolo la lista di customTimePeriod da mostrare a video,
 *  in base anche allo showPeriods, periodScrolling, periodElapsed */
searchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);
if(UtilValidate.isEmpty(searchDate)) {
	searchDate = workEffortView.thruDate;
}
if(UtilValidate.isEmpty(searchDate)) {
	Debug.logError(" **** executeChildPerformFindTransIndicator.groovy -> searchDate is null", null);
	parameters.errorLoadTrans = uiLabelErrorMap.ErrorLoadTrans;
	parameters.errorLoadTransDescr = uiLabelErrorMap.ErrorLoadTransDescr_searchDate;
	Debug.log(" **** executeChildPerformFindTransIndicator.groovy -> parameters.errorLoadTrans" + parameters.errorLoadTrans);
    return;
}
Debug.log(" **** executeChildPerformFindTransIndicator.groovy -> searchDate" + searchDate);

context.customTimePeriodList = [];
context.scrollInt = UtilValidate.isNotEmpty(parameters.scrollInt)? new Integer(parameters.scrollInt) : 0;

context.showValuesPanel = UtilValidate.isNotEmpty(context.showValuesPanel) ? context.showValuesPanel : "N";
context.showPeriods = UtilValidate.isNotEmpty(context.showPeriods) ? context.showPeriods : "OPEN";
// altri period possibili: NONE / TWO_PREV / TWO_NEXT / THREE_PREV / THREE_MIDDLE / THREE_NEXT 
context.detailEnabled = UtilValidate.isNotEmpty(context.detailEnabled) ? context.detailEnabled : "NONE"; // ALL /SOME / NONE
context.showDetail = UtilValidate.isNotEmpty(context.showDetail) ? context.showDetail : "N"; // ALL / ONE
// context.glFiscalTypeId = da params, potrebbe essere ALL, ACTUAL, BUDGET, ecc...;
context.periodScrolling = UtilValidate.isNotEmpty(context.periodScrolling) ? context.periodScrolling : "N";
context.periodElapsed = UtilValidate.isNotEmpty(context.periodElapsed) ? context.periodElapsed : "PROJECT"; // PROCESS / PARENT
context.manageAccount = UtilValidate.isNotEmpty(context.manageAccount) ? context.manageAccount : "N";
context.showUomDescr = UtilValidate.isNotEmpty(context.showUomDescr) ? context.showUomDescr : "N";
context.showUom = UtilValidate.isNotEmpty(context.showUom) ? context.showUom : "Y";
context.showSequenceId = UtilValidate.isNotEmpty(context.showSequenceId) ? context.showSequenceId : "N";
context.showKpiWeight = UtilValidate.isNotEmpty(context.showKpiWeight) ? context.showKpiWeight : "N";
context.showKpiOtherWeight = UtilValidate.isNotEmpty(context.showKpiOtherWeight) ? context.showKpiOtherWeight : "N";
context.onlyWithBudget = UtilValidate.isNotEmpty(context.onlyWithBudget) ? context.onlyWithBudget : "N";
context.accountFilter = UtilValidate.isNotEmpty(context.accountFilter) ? context.accountFilter : "ALL"; //OBJ, NOOBJ, ALL
context.showComments = UtilValidate.isNotEmpty(context.showComments) ? context.showComments : "N"; //Y, N, RIGHT, LEFT
context.commentsEtchDescr = UtilValidate.isNotEmpty(context.commentsEtchDescr) ? context.commentsEtchDescr : "comments"; //comments, action, verificationSource, dataSource, category

context.glAccountIdTitleAreaClass = UtilValidate.isNotEmpty(context.glAccountIdTitleAreaClass) ? context.glAccountIdTitleAreaClass : "";
context.uomDescrTitleAreaClass = UtilValidate.isNotEmpty(context.uomDescrTitleAreaClass) ? context.uomDescrTitleAreaClass : "";

/** verranno sovrascritti a Y con il layout a radio button, invece per lo standard restano N */
context.showKpiScore = UtilValidate.isNotEmpty(context.showKpiScore) ? context.showKpiScore : "N";
context.showKpiTotal = UtilValidate.isNotEmpty(context.showKpiTotal) ? context.showKpiTotal : "N";

/** parametri molto tecnici per gestire altezza e lunghezza nei diversi casi, la gestione dell'alatezza comporta del ritard onela caricamento della mappa */
context.manageHeight = UtilValidate.isNotEmpty(context.manageHeight) ? context.manageHeight : "Y";
context.manageWidth = UtilValidate.isNotEmpty(context.manageWidth) ? context.manageWidth : "Y";

context.showAccountCode = UtilValidate.isNotEmpty(context.showAccountCode) ? context.showAccountCode : "N";

context.showDirection = UtilValidate.isNotEmpty(context.showDirection) ? context.showDirection : "N";
context.showType = UtilValidate.isNotEmpty(context.showType) ? context.showType : "N"; //Y, N, SX
context.showAccountReference = UtilValidate.isNotEmpty(context.showAccountReference) ? context.showAccountReference : "N"; //Y, N
context.showScoreWeighted = "";

/** Recupero params */
conditionWorkEffortTypeContent = [];
conditionWorkEffortTypeContent.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS,  workEffortView.workEffortTypeId));
conditionWorkEffortTypeContent.add(EntityCondition.makeCondition("weTypeContentTypeId", EntityOperator.EQUALS, layoutType));

def workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition(conditionWorkEffortTypeContent), null, null, null, false);
def workEffortTypeContent = EntityUtil.getFirst(workEffortTypeContentList);

if (UtilValidate.isEmpty(workEffortTypeContent) || UtilValidate.isEmpty(workEffortTypeContent.params)) {
	Debug.logError(" **** executeChildPerformFindTransIndicator.groovy -> workEffortTypeContent is null", null);
	parameters.errorLoadTrans = uiLabelErrorMap.ErrorLoadTrans;
	parameters.errorLoadTransDescr = uiLabelErrorMap.ErrorLoadTransDescr;
	return;
}

BshUtil.eval(workEffortTypeContent.params, context);

def assocWeighted = null;
def fiscalTypeParam = "";
if (UtilValidate.isNotEmpty(context.showScoreWeighted)) {
	def assocTypeParam = "";
	def i = context.showScoreWeighted.indexOf("|");
	int l = context.showScoreWeighted.length();
	if (i > -1 && i < l) {
		fiscalTypeParam = context.showScoreWeighted.substring(0, i);
		assocTypeParam = context.showScoreWeighted.substring(i+1, l);
	}
	if (UtilValidate.isNotEmpty(assocTypeParam)) {
		def assocConditionList = [];
		assocConditionList.add(EntityCondition.makeCondition("workEffortIdTo", parameters.workEffortId));
		assocConditionList.add(EntityCondition.makeCondition("workEffortAssocTypeId", assocTypeParam));
		def assocList = delegator.findList("WorkEffortAssoc", EntityCondition.makeCondition(assocConditionList), null, null, null, false);
		def assocItem = EntityUtil.getFirst(assocList);
		if (UtilValidate.isNotEmpty(assocItem)) {
			assocWeighted = assocItem.assocWeight;
		}
	}
}

/** Recupero glAccount Title */
def glAccountIdTitleValue = uiLabelMap["FormFieldTitle_glAccountId"];
/** Recupero title per glAccountId */
if (UtilValidate.isEmpty(context.etchDescr)) {
	if(UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(context[layoutType + "_title"])) {
		glAccountIdTitleValue = context[layoutType + "_title"];
	}
}
if ("indicType".equals(context.etchDescr)) {
	glAccountIdTitleValue = uiLabelMap["WemTypeIndicator"];
}
if ("calcRule".equals(context.etchDescr)) {
	glAccountIdTitleValue = uiLabelMap["WemRuleCalculation"];
}
context.glAccountIdTitleValue = glAccountIdTitleValue;


/** Eventuale glFiscalType.description trovato e' inserito nel contesto per mostarlo a video */
glFiscalType = delegator.findOne("GlFiscalType", ["glFiscalTypeId" : context.glFiscalTypeId], false);
if (UtilValidate.isEmpty(context.glFiscalTypeId) || (!"ALL".equals(context.glFiscalTypeId) && UtilValidate.isEmpty(glFiscalType))) {
    Debug.logError(" **** executeChildPerformFindTransIndicator.groovy -> glFiscalTypeId is null", null);
    parameters.errorLoadTrans = uiLabelErrorMap.ErrorLoadTrans;
    parameters.errorLoadTransDescr = uiLabelErrorMap.ErrorLoadTransDescr_glFiscalTypeId;
    return;
}
values = AccountFilterEnum.values();
try {
    AccountFilterEnum.valueOf(context.accountFilter);
} catch (e) {
    Debug.logError(" **** executeChildPerformFindTransIndicator.groovy -> context.accountFilter " + context.accountFilter + " is not valid ", null);
    parameters.errorLoadTrans = uiLabelErrorMap.ErrorLoadTrans;
    parameters.errorLoadTransDescr = uiLabelErrorMap.ErrorLoadTransDescr_accountFilter;
    return;
}
if(!"ALL".equals(context.glFiscalTypeId)) {
    context.glFiscalTypeDesc = glFiscalType.description;
    context.glFiscalTypeDescLang = glFiscalType.descriptionLang;
    context.glFiscalTypeEnumId = glFiscalType.glFiscalTypeEnumId;
}


/** il servizio per capire il customTimePeriod va fatto sempre */
cutomTimePeriodServiceMap = ["periodTypeId": context.periodTypeId, 
                             "showPeriods": context.showPeriods, 
                             "searchDate": searchDate, 
                             "organizationPartyId": workEffortView.organizationId, 
                             "scrollInt": context.scrollInt,
                             "periodElapsed": context.periodElapsed,
                             "locale": locale];

if ("PARENT".equals(context.periodElapsed)) {
	cutomTimePeriodServiceMap.estimatedStartDate = workEffortView.getTimestamp("rootEstimatedStartDate");
	cutomTimePeriodServiceMap.estimatedCompletionDate = workEffortView.getTimestamp("rootEstimatedCompletionDate");
} else {
	cutomTimePeriodServiceMap.estimatedStartDate = workEffortView.getTimestamp("estimatedStartDate");
	cutomTimePeriodServiceMap.estimatedCompletionDate = workEffortView.getTimestamp("estimatedCompletionDate");
}

Debug.log(" Run sync service getCustomTimePeriodList with "+ cutomTimePeriodServiceMap + ", userLoginId=" + context.userLogin.userLoginId);
cutomTimePeriodServiceMap.put("userLogin", context.userLogin);

def res = dispatcher.runSync("getCustomTimePeriodList", cutomTimePeriodServiceMap);
if (ServiceUtil.isFailure(res)) {
	Debug.logError(" **** executeChildPerformFindTransIndicator.groovy -> getCustomTimePeriodList isFailure  "+res.failMessage, null);
    parameters.errorLoadTrans = uiLabelErrorMap.ErrorLoadTrans;
    parameters.errorLoadTransDescr = res.failMessage;
    return;
}

context.customTimePeriodList = res.customTimePeriodList;
/** Primo e ultimo customTimePeriod, per non mostrare le frecce e per mostrare solo le misure valide nel periodo */
context.firstCustomTimePeriodId = res.firstCustomTimePeriodId;
context.lastCustomTimePeriodId = res.lastCustomTimePeriodId;
def firstCustomTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId" : context.firstCustomTimePeriodId], false);
def lastCustomTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId" : context.lastCustomTimePeriodId], false);
def firstCustomTimePeriodFromDate = firstCustomTimePeriod.fromDate;
def lastCustomTimePeriodThruDate = lastCustomTimePeriod.thruDate;

context.masterCustomTimePeriodId = res.customTimePeriodId;
searchDate = res.thruDate;
context.searchDateCalculate = searchDate;
//Debug.log("........................ context.searchDateCalculate="+context.searchDateCalculate);


if("OPEN".equals(context.showPeriods)) {
    customTimePeriodTmp = EntityUtil.getFirst(context.customTimePeriodList);
    /** il customTimePeriodCode trovato e' inserito nel contesto per mostarlo a video */
    /** il customTimePeriodId trovato e' inserito nel contesto per gestire l'UNICO periodo del RadioButton e dello standard nel caso OPEN */
    context.customTimePeriodId = customTimePeriodTmp.customTimePeriodId;
    context.customTimePeriodCode = customTimePeriodTmp.customTimePeriodCode;
    firstCustomTimePeriodFromDate = customTimePeriodTmp.fromDate;
    lastCustomTimePeriodThruDate = customTimePeriodTmp.thruDate;
}

/** Recupero Misure e Movimenti */
def workEffortIdRoot = UtilValidate.isNotEmpty(context.workEffortIdRoot) ? context.workEffortIdRoot : parameters.workEffortIdRoot;
def paramSearchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);

serviceMap = ["workEffortId": parameters.workEffortId, 
              "periodTypeId": context.periodTypeId, 
              "showPeriods": context.showPeriods,
  	          "showDetail": context.showDetail,
  	          "onlyWithBudget": context.onlyWithBudget,
  	          "accountFilter": context.accountFilter,
	          "contentId": mappaContent[layoutType],
	          "organizationId": context.defaultOrganizationPartyId,
	          "firstCustomTimePeriodFromDate": firstCustomTimePeriodFromDate,
	          "lastCustomTimePeriodThruDate": lastCustomTimePeriodThruDate,
              "searchDate": paramSearchDate,
              "workEffortIdRoot": workEffortIdRoot,
              "timeZone": context.timeZone
	          ];

// il NONE trova tutti i periodi possibili nel servizio precedente e poi nell ftl non li mostra come colonne
// cosi se ce ne sono piu di 1 si nota subito
// OPEN spedisce una lista con solo 1 customTimePeriodId
serviceMap.put("customTimePeriodIdList", EntityUtil.getFieldListFromEntityList(context.customTimePeriodList, "customTimePeriodId", true)); 

if(!"ALL".equals(context.glFiscalTypeId)) {
    serviceMap.put("glFiscalTypeId", context.glFiscalTypeId);
}
/** perche nello standard l'uomId dipende dalla misura */
if(UtilValidate.isNotEmpty(context.uomId)) {
	serviceMap.put("weTransCurrencyUomId", context.uomId);
}

// Innanzitutto INVOCO il servizio:
// se showDetail = N avro' la lista con tutti i movimenti
// se showDetail = ALL avro' la lista con tutti i movimenti e, per le misure con dettagli, avro' anche i relativi dettagli
// se showDetail = ONE avro' la lista con tutte le misure chiuse, cioe' senza dettagli,
// e SOLO in questo caso INVOCO il servizio una seconda volta per avere i dettagli di una sola misura
Debug.log(" Run sync service executeChildPerformFindTransGroupIndicator with "+ serviceMap + ", userLoginId=" + context.userLogin.userLoginId);
serviceMap.put("userLogin", context.userLogin);
resultService = dispatcher.runSync("executeChildPerformFindTransGroupIndicator", serviceMap);

def kpiScoreWeightTotal = 0;
def workEffortTransactionIndicatorViewList = [];

if(UtilValidate.isNotEmpty(resultService.measureList)) {
	resultService.measureList.each { measureMap ->
        def mpList = measureMap.get(ExecuteChildPerformFindTransGroupIndicator.ROW_LIST);
        
        if("ONE".equals(context.showDetail)) {
            // di default si apre il dettagli odella prima misura
            if(UtilValidate.isEmpty(parameters.workEffortMeasureId)) {
                parameters.workEffortMeasureId = resultService.measureList[0].weTransMeasureId;
                serviceMap.put("workEffortMeasureId", parameters.workEffortMeasureId);
                Debug.log(" Run sync service executeChildPerformFindTransGroupIndicator with "+ serviceMap + ", userLoginId=" + context.userLogin.userLoginId);
                resultService2 = dispatcher.runSync("executeChildPerformFindTransGroupIndicator", serviceMap);
                if(UtilValidate.isNotEmpty(resultService2.measureList)) {
                    measureMap = resultService2.measureList[0];
                }
            } else if (parameters.workEffortMeasureId == measureMap.weTransMeasureId) {
                parameters.workEffortMeasureId = measureMap.weTransMeasureId;
                serviceMap.put("workEffortMeasureId", parameters.workEffortMeasureId);
                Debug.log(" Run sync service executeChildPerformFindTransGroupIndicator with "+ serviceMap + ", userLoginId=" + context.userLogin.userLoginId);
                resultService2 = dispatcher.runSync("executeChildPerformFindTransGroupIndicator", serviceMap);
                if(UtilValidate.isNotEmpty(resultService2.measureList)) {
                    measureMap = resultService2.measureList[0];
                }
            }
            
        }
        
        mpList.each { mappaMeasure ->
            def pList = mappaMeasure.get(ExecuteChildPerformFindTransGroupIndicator.ROW_LIST);
            def measure = null;
            
            if (UtilValidate.isNotEmpty(context.showScoreWeighted)) {
            	def scoreWeightedList = [];
            	def measureList = delegator.findList("GlAccountMeasureUomView", EntityCondition.makeCondition("workEffortMeasureId", measureMap.weTransMeasureId), null, null, null, false);
                measure = EntityUtil.getFirst(measureList);
            	if (UtilValidate.isNotEmpty(measure) && "SCORE".equals(measure.glAccountId)) {
            		if (UtilValidate.isNotEmpty(context.customTimePeriodList)) {
            			context.customTimePeriodList.each { customTimePeriodItem ->
            				def weightedValue = null;
            				if (UtilValidate.isNotEmpty(fiscalTypeParam)) {
            					def acctgTransConditionList = [];
            					acctgTransConditionList.add(EntityCondition.makeCondition("voucherRef", measureMap.weTransMeasureId));
            					acctgTransConditionList.add(EntityCondition.makeCondition("transactionDate", customTimePeriodItem.thruDate));
            					acctgTransConditionList.add(EntityCondition.makeCondition("entryGlFiscalTypeId", fiscalTypeParam));
            					def acctgTransList = delegator.findList("AcctgTransAndEntriesView", EntityCondition.makeCondition(acctgTransConditionList), null, null, null, false);
            					def acctgTransItem = EntityUtil.getFirst(acctgTransList);
            					if (UtilValidate.isNotEmpty(acctgTransItem) && UtilValidate.isNotEmpty(acctgTransItem.entryAmount) && UtilValidate.isNotEmpty(assocWeighted)) {
            						weightedValue = acctgTransItem.entryAmount * assocWeighted / 100.0;
            					}
            				}
            				org.ofbiz.entity.GenericValue genericValueScoreWeighted = delegator.makeValue("WorkEffortTransactionIndicatorView");
                            genericValueScoreWeighted.put("weTransMeasureId", measureMap.weTransMeasureId);
                            genericValueScoreWeighted.put("weTransWeId", parameters.workEffortId);
                        	genericValueScoreWeighted.put("weTransAccountId", "SCORE");
                        	genericValueScoreWeighted.put("weTransTypeValueDesc", uiLabelMap.RisultatoPesato);
                        	genericValueScoreWeighted.put("weTransTypeValueDescLang", uiLabelMap.PunteggioPesato);
                        	genericValueScoreWeighted.put("weTransValue", weightedValue);
                        	genericValueScoreWeighted.put("weTransDecimalScale", measure.decimalScale);
                        	genericValueScoreWeighted.put("isPosted", "Y");
                        	scoreWeightedList.add(genericValueScoreWeighted);
            			}
            		}
            		def scoreWeightedMeasureMap = [entryPartyId : "", "rowList" : scoreWeightedList];
                	pList.add(scoreWeightedMeasureMap); 
            	}         	
            }
          
            pList.each { mappaGV ->
                def rowList = mappaGV.get(ExecuteChildPerformFindTransGroupIndicator.ROW_LIST);               
                rowList.each { itemGV ->
                    if(UtilValidate.isNotEmpty(itemGV) && UtilValidate.isNotEmpty(itemGV.weTransId)) {
                        if(itemGV.weTransUomType == "DATE_MEASURE"){
            			    itemGV.weTransValue = com.mapsengineering.base.birt.util.UtilDateTime.numberConvertToDate(itemGV.weTransValue, locale);
            			}
            		}
                    if (UtilValidate.isNotEmpty(context.showScoreWeighted)) {
                    	if (UtilValidate.isNotEmpty(measure) && "SCORE".equals(measure.glAccountId)) {
                    		itemGV.kpiScoreWeight = assocWeighted;
                    	}
                    }                
                }              
            }
        }
        workEffortTransactionIndicatorViewList.add(measureMap);
	}
	
    /** i totali servono solo per i radio button */
	kpiScoreWeightTotal = resultService.kpiScoreWeightTotal;
	
	if("Y".equals(context.showKpiTotal)) {
		// add total row
		// create only one row for TOTAL, only with radioButton so there is always context.glFiscalTypeId
		org.ofbiz.entity.GenericValue genericValueTotali = delegator.makeValue("WorkEffortTransactionIndicatorView");
		genericValueTotali.put("weTransWeId", parameters.workEffortId);
		genericValueTotali.put("weTransMeasureId", "TOTAL");
		genericValueTotali.put("weTransAccountId", "TOTAL");
		
		genericValueTotali.put("weTransAccountDesc", uiLabelMap.TotalsColumn);
		genericValueTotali.put("weTransUomDesc", "");
		genericValueTotali.put("kpiScoreWeight", kpiScoreWeightTotal);
		
		genericValueTotali.put("glValModId", "ALL_NOT_MOD");

		genericValueTotali.put("weTransTypeValueId", context.glFiscalTypeId);
		genericValueTotali.put("weTransDate", null);
		genericValueTotali.put("weTransCurrencyUomId", "");
		
		genericValueTotali.put("isPosted", "N");
		genericValueTotali.put("inputEnumId", "ACCINP_OBJ");
		
		genericValueTotali.put("weTransDecimalScale", context.scoreDecimalScale);
		genericValueTotali.put("weTransUomType", "");
		genericValueTotali.put("weTransUomAbb", "");
		
		def pList = [genericValueTotali];
		def pMappa = [weTransTypeValueId : context.glFiscalTypeId, "rowList" : pList];
		
		def mpList = [pMappa];
		
		def measureMap = [weTransMeasureId : "TOTAL", "rowList" : mpList];
		workEffortTransactionIndicatorViewList.add(measureMap);
		
	}
	
	context.workEffortTransactionIndicatorViewList = workEffortTransactionIndicatorViewList;
}
