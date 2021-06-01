import org.ofbiz.service.ServiceUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.client.*;

def getScoreDate(workEffortId, referenceDate) {
	scoreDate = null; 
	
	if (UtilValidate.isEmpty(referenceDate)) {
		workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		scoreDate = workEffort.lastCorrectScoreDate;
		
		if (UtilValidate.isEmpty(scoreDate)) {
            workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffort.workEffortParentId], false);
            scoreDate = workEffort.lastCorrectScoreDate;
        }
		
		return scoreDate;
	} else {
		return referenceDate;
	}
}


if(! "N".equals(context.showScoreValue) && (UtilValidate.isEmpty(parameters.insertMode) || "N".equals(parameters.insertMode))) {
	def glFiscalTypeId = (UtilValidate.isNotEmpty(context.glFiscalTypeIdForScore) ? context.glFiscalTypeIdForScore : "ACTUAL");
	def searchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);
	
	def transDate = getScoreDate(context.workEffortIdForScore, searchDate);
	if(UtilValidate.isNotEmpty(transDate)) {
		conditionMap = [:];
		conditionMap.weTransWeId = context.workEffortIdForScore;
		conditionMap.weTransAccountId = "SCORE";
		conditionMap.weTransTypeValueId = glFiscalTypeId;
		conditionMap.weTransDate = transDate;
		
		def scoreValueList = delegator.findList("WorkEffortTransactionIndicatorViewWithScorekpi", EntityCondition.makeCondition(conditionMap), null, null, null, false);
		def scoreValue = null;
		Debug.log(" - Found " + scoreValueList.size() + " score with " + EntityCondition.makeCondition(conditionMap));
		if(UtilValidate.isNotEmpty(scoreValueList)) {
			scoreValue = EntityUtil.getFirst(scoreValueList);
			context.weTransValue = scoreValue.weTransValue;
			context.weTransDecimalScale = scoreValue.weTransDecimalScale;
		}
		if("WEIGHTED".equals(context.showScoreValue)) {
			if (UtilValidate.isNotEmpty(scoreValue) && UtilValidate.isNotEmpty(scoreValue.weTransValue) && UtilValidate.isNotEmpty(context.assocWeight)) {
				context.weTransValueWeighted = scoreValue.weTransValue * context.assocWeight / 100.0;
			}
		}
	} else {
		Debug.log(" - No lastCorrectScoreDate found");
	}
}
