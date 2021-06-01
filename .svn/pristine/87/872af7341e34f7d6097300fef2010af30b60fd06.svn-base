import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.apache.commons.lang.StringUtils;

// Bug Fix: non venivano presi in considerazione campi di sort
sortFieldList = [];
try {
 
	sortField = (parameters.sortField != null) ? parameters.sortField : context.sortField;
	if (sortField == null || sortField == "") {
		sortField = "workEffortId"
	}
	sortFieldList = Arrays.asList(StringUtils.split(sortField, "|"));
	
} catch (Exception e) {

	Debug.log("*********************** ERRORE LoadWorkEffortAchieveViewExt.groovy: errore parse dei campi di sort. Il parametro sortField vale: " + sortField + " " + e.getMessage()); 
}	

Debug.log("########## workEffortId="+parameters.workEffortId+" ########## transactionDate="+parameters.transactionDate);
Debug.log("########## workEffortAnalysisId="+parameters.workEffortAnalysisId);

if(UtilValidate.isEmpty(parameters.transactionDate)) {
	//caso monitoraggio non attivo -> non ho score e mi sono perso la data!!
	def analisi = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false); 
	parameters.transactionDate = analisi.referenceDate;		
}

if (UtilValidate.isNotEmpty(parameters.workEffortId) && UtilValidate.isNotEmpty(parameters.transactionDate)) {
	def conditionList = [];
	conditionList.add(EntityCondition.makeCondition("workEffortIdFrom", parameters.workEffortId));
	conditionList.add(EntityCondition.makeCondition(
			EntityCondition.makeCondition("transactionDate", EntityOperator.EQUALS, null),
			EntityOperator.OR,
			EntityCondition.makeCondition("transactionDate", EntityOperator.EQUALS, parameters.transactionDate)
			));
	
	if (UtilValidate.isNotEmpty(parameters.snapshot) && parameters.snapshot == 'Y') {
		conditionList.add(EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, null));		
	} else {
		conditionList.add(EntityCondition.makeCondition(
			EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.EQUALS, null),
			EntityOperator.OR,
			EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.EQUALS, "")
			));
	}
	conditionList.add(EntityCondition.makeCondition("actStEnumId", EntityOperator.NOT_EQUAL, "ACTSTATUS_REPLACED"));
	Debug.log("########## LoadWorkEffortAchieveViewExt.groovy conditionList="+conditionList);
	context.listIt = delegator.findList("WorkEffortAchieveExtAndWeTypeCntView", EntityCondition.makeCondition(conditionList), null, sortFieldList, null, false);
	// Debug.log("########## LoadWorkEffortAchieveViewExt.groovy context.listIt="+context.listIt.size());
}

if (UtilValidate.isNotEmpty(context.listIt)) {
	def firstElement = context.listIt[0];
	context.workEffortRowTypeId = firstElement.workEffortTypeId;
}