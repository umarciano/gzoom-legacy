import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;
import com.mapsengineering.base.services.*;
import com.mapsengineering.workeffortext.scorecard.ScoreCard;
import org.ofbiz.service.ServiceUtil;


// parametri input
def weContextId = context.weContextId;
def workEffortTypeId = context.workEffortTypeId;
def filiera = context.filiera;
def orgUnitId = context.orgUnitId;
def roleTypeId = context.roleTypeId;
def partyId = context.partyId;

def thruDate = context.thruDate;
def estimatedStartDate = context.estimatedStartDate;
def estimatedCompletionDate = context.estimatedCompletionDate;
def target = context.target;
def performance = context.performance;
def limitExcellent = context.limitExcellent;
def limitMax = context.limitMax;
def limitMin = context.limitMin;
def scoreValueType = context.scoreValueType;
def weightType = context.weightType;
def sessionId = context.sessionId;
def recalculate = UtilValidate.isNotEmpty(context.recalculate) ? context.recalculate : "N";
def uvUserLoginId = UtilValidate.isNotEmpty(context.uvUserLoginId) ? context.uvUserLoginId : "admin";
def organizationId = context.organizationId;
def cleanOnlyScoreCard = context.cleanOnlyScoreCard;

def delegator = context.dctx.delegator;
def dispatcher = context.dctx.dispatcher;

// Lista risultati per il joblog
def runResults = javolution.util.FastList.newInstance();
def blockingErrors = 0L;
def recordElaborated = 0L;
def warningMessages = 0L;
def workEffortRootList = [];

def workEffortType = delegator.findOne("WorkEffortType", [ workEffortTypeId : weContextId ], false);
def description = workEffortType.description
// Messaggio di inizio elaborazione
runResults.add(ServiceLogger.makeLogInfo(String.format("Inizio elaborazione massiva calcolo score per contesto : " + description), ""));

def searchContext = [:];
def searchParameters = [:];
searchParameters.workEffortTypeId = context.workEffortTypeId;
searchParameters.orgUnitId = context.orgUnitId;
searchParameters.roleTypeId = context.roleTypeId;
searchParameters.partyId = context.partyId;
searchParameters.weContextId = weContextId;
searchParameters.thruDate = context.thruDate;
searchParameters.uvUserLoginId = UtilValidate.isNotEmpty(context.uvUserLoginId) ? context.uvUserLoginId : "admin";
searchParameters.organizationId = organizationId;
searchParameters.sourceReferenceId_fld0_ic = context.sourceReferenceId_fld0_ic;
searchParameters.sourceReferenceId_fld0_op = context.sourceReferenceId_fld0_op;
searchParameters.sourceReferenceId_fld0_value = context.sourceReferenceId_fld0_value;
searchParameters.sourceReferenceId_fld1_value = context.sourceReferenceId_fld1_value;

def entityName = "WorkEffortRootView";
def orderBy = "sourceReferenceId|workEffortId";
def fieldList = UtilMisc.toList("sourceReferenceId", "workEffortId");

if(UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(partyId)) {
	entityName = "WorkEffortRootViewAndAssign";
}

// condizione per filiera
if(UtilValidate.isNotEmpty(filiera)){
	def workEffortTypeList  = delegator.findList("WorkEffortTypeTypeToView", EntityCondition.makeCondition("workEffortTypeIdRoot", filiera), 
		UtilMisc.toSet("workEffortTypeId"), ["-sequenceNum"], null, false);
	if (UtilValidate.isNotEmpty(workEffortTypeList)) {
		def workEffortTypeTypeList = EntityUtil.getFieldListFromEntityList(workEffortTypeList, "workEffortTypeId", true);
		searchParameters.workEffortTypeId_value = StringUtil.join(workEffortTypeTypeList, ",");
		searchParameters.workEffortTypeId_op = "in";
	}
	entityName = "WorkEffortRootTypeView";
	orderBy = "-sequenceNum|sourceReferenceId|workEffortId";
	fieldList = UtilMisc.toList("sequenceNum", "sourceReferenceId", "workEffortId");
	if(UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(partyId)) {
		entityName = "WorkEffortRootTypeViewAndAssign";
	}
	searchParameters.workEffortTypeIdRoot = filiera;
}

searchContext.orderBy = orderBy;
searchContext.inputFields = searchParameters;
searchContext.inputFields.uvUserLoginId = uvUserLoginId;
searchContext.entityName = entityName;

prepareResult = dispatcher.runSync("prepareFind", searchContext);

def dateCondition = [];
dateCondition.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, estimatedCompletionDate));
dateCondition.add(EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedStartDate));

if("N".equals(recalculate)) {
	dateCondition.add(EntityCondition.makeCondition(EntityCondition.makeCondition("lastCorrectScoreDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()),
		EntityOperator.OR, EntityCondition.makeCondition("lastCorrectScoreDate", null)));
	runResults.add(ServiceLogger.makeLogInfo(String.format("Non eseguo il ricalcolo del punteggio"), ""));
}
	
def conditionList = EntityCondition.makeCondition(prepareResult.entityConditionList,  EntityCondition.makeCondition(dateCondition));
def entityConditionList = EntityCondition.makeCondition(conditionList);
    
searchResult = dispatcher.runSync("executeFind", [entityName : entityName,
                                                  distinct : "Y",
                                                  fieldList : fieldList,
                                                  orderByList : prepareResult.orderByList,
                                                  entityConditionList : entityConditionList,
                                                  noConditionFind :context.noConditionFind
                                  ] );

workEffortRootList = searchResult.listIt.getCompleteList();
searchResult.listIt.close();

recordElaborated = new Long(workEffortRootList.size());

Debug.log(" - Found " + recordElaborated + " " + entityName + " to elaborate with condition " + entityConditionList);

runResults.add(ServiceLogger.makeLogInfo(String.format("Trovate %d schede da elaborare per il contesto selezionato", recordElaborated), ""));

for(workEffort in workEffortRootList) {
	def inputMap = [:];
	inputMap.workEffortId = workEffort.workEffortId;
	inputMap.thruDate = thruDate;
	inputMap.target = target;
	inputMap.performance = performance;
	inputMap.limitExcellent = limitExcellent;
	inputMap.limitMax = limitMax;
	inputMap.limitMed = limitMed;
	inputMap.limitMin = limitMin;
    inputMap.scoreValueType = scoreValueType;
	inputMap.weightType = weightType;
	inputMap.userLogin = userLogin;
	inputMap.sessionId = sessionId;
	inputMap.cleanOnlyScoreCard = cleanOnlyScoreCard;
	
	localResult = dispatcher.runSync("scoreCardCalc", inputMap);
	
	blockingErrors += localResult.blockingErrors;
	warningMessages += localResult.warningMessages;
}

runResults.add(ServiceLogger.makeLogInfo(String.format("Terminato servizio calcolo massivo score per il contesto selezionato : %s", description), ""));

//
// Costruisco la mappa di riotrno in base all interfaccia MassiveScoreCardCalcImpl
//
def res = [:];
if (blockingErrors > 0 ) {
	res = ServiceUtil.returnSuccess(String.format("Segnalati %d errori bloccanti", blockingErrors));
} else {
	res = ServiceUtil.returnSuccess();
}
res.put("runResults", runResults);
res.put("recordElaborated", recordElaborated);
res.put("errorMessages", blockingErrors);
res.put("warnMessages", warningMessages);
res.put("elabRef1", workEffortTypeId);


//Creazione nota
def noteData = delegator.makeValue("NoteData");
noteData.noteId = delegator.getNextSeqId("NoteData"); 
noteData.noteName = "SYSTEMNOTE";
noteData.noteDateTime = UtilDateTime.nowTimestamp();
noteData.noteInfo = "Elaborazione di calcolo massivo punteggio per il contesto selezionato : " + description;
noteData.noteParty = userLogin.partyId;
noteData.isPublic = "N";
delegator.create(noteData);

return res;
