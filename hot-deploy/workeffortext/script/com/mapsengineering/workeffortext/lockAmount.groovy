import org.ofbiz.base.util.UtilProperties;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

import com.mapsengineering.base.util.MessageUtil;

import java.text.*;

Debug.log("**************************** BEGIN LOCK_TRANSACTIONS ******************************");


//def parameters = parameters.parameters;
def numLocked = 0;
def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", locale)
def itemList = [];

if (UtilValidate.isNotEmpty(parameters.amountLocked)) {
	def amountLocked = parameters.amountLocked;


	def transactionDate = parameters.transactionDate;
	if (UtilValidate.isNotEmpty(transactionDate)) {
		//transactionDate = new SimpleDateFormat(UtilDateTime.getDateFormat(locale)).parse(transactionDate, new ParsePosition(0));
	}
	def weContextId = parameters.weContextId;
	
	Debug.log("*** transactionDate = " + transactionDate);
	Debug.log("*** weContextId = " + weContextId);
	Debug.log("*** amountLocked = " + amountLocked);
	
	def conditionList = [];
	if (UtilValidate.isNotEmpty(transactionDate)) {
		conditionList.add(EntityCondition.makeCondition("transactionDate", transactionDate));
	}
	else {
		throw new Exception(UtilProperties.getMessage(MessageUtil.BASE_ERROR_LABEL, "MandatoryField", UtilMisc.toMap("fieldName", uiLabelMap.FormFieldTitle_transactionDate), locale)); 
	}
	
	if (UtilValidate.isNotEmpty(weContextId)) {
		def workEffortTypeIdList = [];
		def workEffortTypeParentList = delegator.findList("WorkEffortTypeParent", EntityCondition.makeCondition("workEffortTypeParentId", weContextId), null, null, null, false);
		if (UtilValidate.isNotEmpty(workEffortTypeParentList)) {
			workEffortTypeIdList = EntityUtil.getFieldListFromEntityList(workEffortTypeParentList, "workEffortTypeId", false);
			conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.IN, workEffortTypeIdList));
		}
	}
	
	if (UtilValidate.isNotEmpty(parameters.weId)) {
		Debug.log("*** parameters.weId = " + parameters.weId);
		conditionList.add(EntityCondition.makeCondition("workEffortId", parameters.weId));
	}
	
	conditionList.add(EntityCondition.makeCondition("glFiscalTypeId", parameters.glFiscalTypeId));
	
	Debug.log("*** condition= " + EntityCondition.makeCondition(conditionList));
	
	//SCOREKPI
	def scoreKpiList = delegator.findList("WorkEffortMeasureScoreKpi", EntityCondition.makeCondition(conditionList), null, null, null, false);
	Debug.log("*** Found " + scoreKpiList.size() + " rows for WorkEffortMeasureScoreKpi");
	
	//SCORE
	def scoreList = delegator.findList("WorkEffortMeasureScore", EntityCondition.makeCondition(conditionList), null, null, null, false);
	Debug.log("*** Found " + scoreList.size() + " rows for WorkEffortMeasureScore");
	
	
	itemList.addAll(scoreKpiList);
	itemList.addAll(scoreList);
	for(item in itemList) {
		def acctgTransId = item.acctgTransId;
		def acctgTransEntrySeqId = item.acctgTransEntrySeqId;
		def acctgTransEntry = delegator.findOne("AcctgTransEntry", UtilMisc.toMap("acctgTransEntrySeqId", acctgTransEntrySeqId, "acctgTransId", acctgTransId), false);
		acctgTransEntry.amountLocked = amountLocked;
		delegator.store(acctgTransEntry);
		numLocked++;
	}
}
Debug.log("*** ITEMS LOCKED: " + numLocked);

Debug.log("**************************** END LOCK_TRANSACTIONS ******************************");

def result = ServiceUtil.returnSuccess();
result.put("numLocked", numLocked);

return result;