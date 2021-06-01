import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.entity.condition.*;

//
//Creo le immagini dei tachimetri e le associo ai relativi workEffortId nella lista
//
//def workEffortAchieveView = delegator.findOne("WorkEffortAchieveView", ["workEffortId" : parameters.workEffortId], false);

def workEffortAchieveView = null;
def workEffortAchieveViewList = null;


/**Aggiungo caso per snapshot*/
def conditionList = [];
if (UtilValidate.isNotEmpty(parameters.snapshot) && parameters.snapshot == 'Y') {
	conditionList.add(EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, null));
	/*conditionList.add(EntityCondition.makeCondition(
		EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, null),
		EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, "")
		));*/
} else {
	conditionList.add(EntityCondition.makeCondition(
		EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.EQUALS, null),
		EntityOperator.OR,
		EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.EQUALS, "")
		));
}


if ("WorkEffortAchieveView".equals(parameters.headerEntityName)) {
	
	def condition = null;
	condition = EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortAnalysisId", parameters.workEffortAnalysisId), EntityCondition.makeCondition("workEffortId", parameters.workEffortId)]);
	if(UtilValidate.isNotEmpty(parameters.userLoginId) && parameters.userLoginId != "_NA_"){
		condition = EntityCondition.makeCondition([condition, EntityCondition.makeCondition("userLoginId", parameters.userLoginId)]);
	}	
	conditionList.add(condition);
	
	
} else if ("WorkEffortAchieveViewExt".equals(parameters.headerEntityName)) {
	
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortId", parameters.workEffortId), EntityCondition.makeCondition("workEffortIdFrom", parameters.workEffortIdHeader)]));
	if (UtilValidate.isNotEmpty(parameters.transactionDate)) {
		conditionList.add(EntityCondition.makeCondition("transactionDate", parameters.transactionDate));
	}
}	

if (UtilValidate.isNotEmpty(conditionList)) {
	workEffortAchieveViewList = delegator.findList(parameters.headerEntityName, EntityCondition.makeCondition(conditionList), null, null, null, true);
}
/** Prendo il campo typeBalanceScoreTarId e typeBalanceScoreConId per calcolo dell'immagine */
def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false);

if (UtilValidate.isNotEmpty(workEffortAchieveViewList)) {
	workEffortAchieveViewList.each { item ->
		
		if (UtilValidate.isEmpty(workEffortAchieveView)) {
			workEffortAchieveView = item.getAllFields();
		}
		
		if (UtilValidate.isNotEmpty(item.amount)) {
			if (UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreTarId.equals(item.glFiscalTypeId)) {
				if (UtilValidate.isEmpty(workEffortAchieveView.targetAmount) || item.amount.compareTo(workEffortAchieveView.targetAmount) > 0) {
					workEffortAchieveView.targetAmount = item.amount;
				}
			} else if (UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreConId.equals(item.glFiscalTypeId)) {
				if (UtilValidate.isEmpty(workEffortAchieveView.actualAmount) || item.amount.compareTo(workEffortAchieveView.actualAmount) > 0) {
					workEffortAchieveView.actualAmount = item.amount;
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(item.hasScoreAlert) && UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreConId.equals(item.glFiscalTypeId)) {
			if (UtilValidate.isEmpty(workEffortAchieveView.hasAlert) || item.hasScoreAlert.compareTo(workEffortAchieveView.hasAlert) > 0) {
				workEffortAchieveView.hasAlert = item.hasScoreAlert;
			}
		}
	}
}


if (UtilValidate.isNotEmpty(workEffortAchieveView)) {
	def localContext = MapStack.create(context);
	localContext.fieldsMap = workEffortAchieveView;
	localContext.fieldsMap.typeBalanceScoreTarId = UtilValidate.isNotEmpty(workEffortAnalysis) ? workEffortAnalysis.typeBalanceScoreConId : "";
	
	context.item = GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/createSingleAchieveImage.groovy", localContext);
}