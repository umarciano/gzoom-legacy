
/**
	Questo groovy viene richiamato per popolare la lista dei valori indicatore quando ci si accede dal context link nella tab Indicatori
	OPPURE dal folder Indicatori con layout non standard
	
	Fa la stessa cosa di executeChildPerformFindWETIndicator.groovy solo che aggiunge in chiave per la ricerca parameters.workEffortMeasureId in modo 
	da filtrare i risultati per indicatore
	
	-->  vedi file di properties al commento: bug 4144	

**/

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;
// CAZZATA, LO SCRIPT VIENE CHIAMATO ANCHE QUANDO SEI NEL DETTAGLIO DEL CTX LINK!!!! MAH
def workEffortMeasureId = UtilValidate.isNotEmpty(parameters.workEffortMeasureId) ? parameters.workEffortMeasureId : parameters.weTransMeasureId;
context.accountTypeEnumId = "INDICATOR";
context.glAccountConditionField = "workEffortTypeIdInd";

Debug.log(" - executeChildPerformFindValueIndicator.groovy context.insertMode " + context.insertMode + " context.layoutType " + context.layoutType + " folderIndex " + context.folderIndex + " [" + parameters.folderIndex + "] in folderContentIds: " + context.folderContentIds);

if("N".equals(context.insertMode)){
	/** Recupero workEffort, workEffortType, workEffortRoot, workEffortTypePeriodId */
	def workEffortView = delegator.findOne("WorkEffortAndTypePeriodAndCustomTime", ["workEffortId" : parameters.workEffortId], false);

	/** Recupero layout e contentIdInd e contentIdSecondary */
	def layoutType = UtilValidate.isNotEmpty(context.layoutType) ? context.layoutType : parameters.layoutType;
	def folderIndex = UtilValidate.isNotEmpty(context.folderIndex) ? context.folderIndex : parameters.folderIndex;
	
	if(UtilValidate.isEmpty(context.contentIdInd)) {
		context.contentIdInd = parameters.contentIdInd;
	}
	
	if(UtilValidate.isEmpty(context.contentIdSecondary)) {
		context.contentIdSecondary = parameters.contentIdSecondary;
	}
	
	/** Recupero Data */
	searchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);
	if(UtilValidate.isEmpty(searchDate)) {
		searchDate = workEffortView.thruDate;
	}
	
	def serviceMap = ["workEffortId": parameters.workEffortId, 
	              "workEffortMeasureId" : parameters.workEffortMeasureId, 
	              "accountFilter" : parameters.accountFilter, 
	              "contentId": layoutType,
	              "timeZone": context.timeZone];
	Debug.log(" Run sync service executeChildPerformFindTransIndicator with " + serviceMap + ", userLoginId = " + (UtilValidate.isNotEmpty(context.userLogin) ? context.userLogin.userLoginId : context.userLogin));
	serviceMap.put("userLogin", context.userLogin);
	def res = dispatcher.runSync("executeChildPerformFindTransIndicator", serviceMap);
	// Debug.log(" End executeChildPerformFindTransIndicator res.rowList " + res.rowList);
	
	def workEffortTransactionIndicatorViewList = [];

	// la lista in questo caso contiene solo record con movimenti
	if(UtilValidate.isNotEmpty(res.rowList)) {
		res.rowList.each { item ->
			def itemGV = item.get("gv");
			if(UtilValidate.isNotEmpty(itemGV)) {
				
				if(itemGV.weTransUomType == "DATE_MEASURE"){
					itemGV.weTransValue = com.mapsengineering.base.birt.util.UtilDateTime.numberConvertToDate(itemGV.weTransValue, locale);
				}
				
				workEffortTransactionIndicatorViewList.add(itemGV);
			}
		}
		context.listIt = workEffortTransactionIndicatorViewList;
	}
} else {
	def wem = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": workEffortMeasureId], false);
	
    context.condition = EntityCondition.makeCondition([EntityCondition.makeCondition("weTransWeId", parameters.workEffortId),
													   EntityCondition.makeCondition("weTransMeasureId", parameters.workEffortMeasureId),
		                                               EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.EQUALS_FIELD, "weTransAccountId"),
													   EntityCondition.makeCondition("periodTypeId", wem.periodTypeId)]);
	
	context.entityNameFind = "WorkEffortTransactionIndicatorView";
	context.glAccountField = "weTransAccountId";
	context.orderByWemList = ["weTransSequenceId", "weTransAccountId", "-weTransDate", "weTransTypeValueDesc"];
	
	//se inserisco un nuovo elemento devo settare 
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.weTransWeId], false);
	if(UtilValidate.isNotEmpty(workEffort)){
		def workEffortMeasureViewWithWorkEffortPurposeTypeList = delegator.findByAnd("WorkEffortMeasureViewWithWorkEffortPurposeType", [weMeasureWeId: parameters.weTransWeId, workEffortTypeIdInd: workEffort.workEffortTypeId]);
		if(UtilValidate.isNotEmpty(workEffortMeasureViewWithWorkEffortPurposeTypeList)){
			context.weTransCurrencyUomId = workEffortMeasureViewWithWorkEffortPurposeTypeList[0].weMeasureDefaultUomId;
		}
	}
	GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWEMResource.groovy", context);
}
