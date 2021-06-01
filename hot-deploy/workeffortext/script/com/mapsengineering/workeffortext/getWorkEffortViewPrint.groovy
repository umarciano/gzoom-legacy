import org.ofbiz.service.ServiceUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.client.*;
import javolution.util.*;

def getScoreValue(workEffortId, glFiscalTypeId, transactionDate) {
	conditionMap = new FastMap();
	conditionMap.workEffortId = workEffortId;
	conditionMap.glFiscalTypeId = glFiscalTypeId;
	conditionMap.transactionDate = transactionDate;	
	return delegator.findOne("WorkEffortScoreView", conditionMap, false); 
}

def getScoreDate(workEffortId, referenceDate) {
	scoreDate = null; 
	
	if (UtilValidate.isEmpty(referenceDate)) {
		workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		scoreDate = workEffort.lastCorrectScoreDate;
		
		if (UtilValidate.isEmpty(scoreDate)) { 	
			workEffort = delegator.findOne("WorkEffort", ["workEffortId" : context.workEffortIdRoot], false);
			scoreDate = workEffort.lastCorrectScoreDate;
		}
		
		return scoreDate;
	} else {
		return referenceDate;
	}
}

context.treeViewEntityName = "WorkEffortAssocExtView";
context.customFindScriptLocation = "component://workeffortext/webapp/workeffortext/WEB-INF/actions/executePerformFindWorkEffortRootInqyTree.groovy";
context.orderByFields = "sequenceNum|wrToCode|workEffortIdTo";
context.keyFields = "workEffortIdTo|workEffortIdFrom|workEffortAssocTypeId|fromDate";
context.parentRelKeyFields = "workEffortIdFrom;workEffortIdTo|workEffortAssocTypeId";
context.rootValues = "workEffortIdFrom;" + context.workEffortIdRoot + "|workEffortAssocTypeId;"+context.weHierarchyTypeId;
res = [];
delegator = dctx.getDelegator();
dispatcher = dctx.getDispatcher();
treeWorker = new TreeWorker(context, dispatcher, false);
result = treeWorker.findTreeList();
treeViewList = result.get(TreeWorker.VALUE_LIST);

if (UtilValidate.isNotEmpty(treeViewList)) {
	depth = 0;
	parentMap = new FastMap();
	workEffortRoot = null;
	treeViewList.each { treeViewNode ->
		if (!"Y".equals(treeViewNode.IS_LEAF)) {
			//Debug.log("***************************** Nodo non foglia..... "  + treeViewNode);
			if (UtilValidate.isEmpty(treeViewNode._PARENT_NODE_ID_)) {
				//Debug.log("***************************** Nodo di primo livello collegato con una radice..... ");
				//Se sto elabprando la radice ne recupero l'id e mi porto a profondità 1
				depth = 1;
				parentMap = new FastMap();
			}
			
			//Se non sono ancora arrivato alla foglia recupero tutte le info relative ai raggruppamenti
			parentMap["workEffortNameL"+depth] = treeViewNode.wrToName;
			parentMap["workEffortDescriptionL"+depth] = treeViewNode.wrDescription;
			parentMap["workEffortIdL"+depth] = treeViewNode.workEffortIdTo;
			parentMap["assocWeightL"+depth] = treeViewNode.assocWeight;
			parentMap["sequenceNumL"+depth] = treeViewNode.sequenceNum;
			scoreValue = getScoreValue(treeViewNode.workEffortIdTo, 
							(UtilValidate.isNotEmpty(context.glFiscalTypeId) ? context.glFiscalTypeId : "ACTUAL"),
			                getScoreDate(treeViewNode.workEffortIdFrom, context.referenceDate));
			
			if (UtilValidate.isNotEmpty(scoreValue)) {
				parentMap["scoreL"+depth] = scoreValue.score;
			}
		 		
			depth++;
		} else {
			//Per ogni leaf recupero la mappa con tutti i dati dei predecessori e a questa aggiungo i dati specifici 
			//per la foglia
			if (UtilValidate.isEmpty(treeViewNode._PARENT_NODE_ID_)) {
				depth = 0;
			}
			
			leafMap = new FastMap();
			leafMap["workEffortNameL"+depth] = treeViewNode.wrToName;
			leafMap["workEffortDescriptionL"+depth] = treeViewNode.wrDescription;
			leafMap["workEffortIdL"+depth] = treeViewNode.workEffortIdTo;
			leafMap["sequenceNumL"+depth] = treeViewNode.sequenceNum;
			leafMap["assocWeightL"+depth] = treeViewNode.assocWeight;
			
			def scoreDate = getScoreDate(treeViewNode.workEffortIdTo, context.referenceDate);
					
			scoreValue = getScoreValue(treeViewNode.workEffortIdTo, 
				(UtilValidate.isNotEmpty(context.glFiscalTypeId) ? context.glFiscalTypeId : "ACTUAL"),
			    scoreDate);
			
			if (UtilValidate.isNotEmpty(scoreValue)) {
				leafMap["scoreL"+depth] = scoreValue.score;
			}
			
			leafMap.putAll(parentMap);
			
			//Recupero i valori
			def workEffortScoreKpiConditionMap = ["glFiscalTypeId" : (UtilValidate.isNotEmpty(context.glFiscalTypeId) ? context.glFiscalTypeId : "ACTUAL")];
			workEffortScoreKpiConditionMap.put("transactionDate", scoreDate);
			
			def wmConditionList = [EntityCondition.makeCondition("workEffortId", treeViewNode.workEffortIdTo), 
								   EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, scoreDate),
								   EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, scoreDate)]
			def workEffortMeasureKpiList = delegator.findList("WorkEffortMeasureKpi", EntityCondition.makeCondition(wmConditionList), null, null, null, true);
			if (UtilValidate.isNotEmpty(workEffortMeasureKpiList)) {
				workEffortMeasureKpiList.each{ workEffortMeasurekpi ->
					rowMap = new FastMap();
					rowMap.putAll(leafMap);
					rowMap.putAll(workEffortMeasurekpi);
					
					workEffortScoreKpiConditionMap.put("workEffortMeasureId", workEffortMeasurekpi.workEffortMeasureId);
					def workEffortScoreKpi = delegator.findOne("WorkEffortScorekpiView", workEffortScoreKpiConditionMap, false);
					if (UtilValidate.isNotEmpty(workEffortScoreKpi)) {
						rowMap.putAll(workEffortScoreKpi);
					}
					
					res.add(rowMap);
				}
			} else {
				res.add(leafMap);
			}
			
			
			
			//			Debug.log("************************************************ conditionList = " + EntityCondition.makeCondition(conditionList));
			
//			workEffortScorekpiViewList = delegator.findList("WorkEffortScorekpiView", EntityCondition.makeCondition(conditionList), null, ["accountName"], null, true);
//			if (UtilValidate.isNotEmpty(workEffortScorekpiViewList)) {
//				workEffortScorekpiViewList.each{ workEffortScorekpiView ->
//					rowMap = new FastMap();
//					rowMap.putAll(leafMap);
//					rowMap.putAll(workEffortScorekpiView);
//					
//					res.add(rowMap);
//				}
//			} else {
//				res.add(leafMap);
//			}
		}
	}
}

//Debug.log("***************************** res = " + res);

results = ServiceUtil.returnSuccess();
results.put("workEffortViewList", res);

return results;