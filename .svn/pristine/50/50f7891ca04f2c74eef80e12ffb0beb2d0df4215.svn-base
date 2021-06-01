import java.rmi.Naming.ParsedNamingURL;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


//Debug.log("#### getWorkEffortAchieveViewManagerExcludedContent ");


def baseExcludedScript = "component://base/webapp/common/WEB-INF/actions/getExcludedContent.groovy";
//def completeTabList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition([EntityCondition.makeCondition("contentIdStart", context.rootFolder),
//																							 EntityCondition.makeCondition("caContentAssocTypeId", "FOLDER_OF")]), null, null, null, true);
def completeTabList = delegator.findList("ContentType", EntityCondition.makeCondition("parentTypeId", "AN_LAYOUT"), null, null, null, true);
																							 
																							 
//Debug.log("#### context.rootFolder: " + context.rootFolder);
//Debug.log("#### completeTabList: " + completeTabList);

//Debug.log("#### arrayContextValueCheckEntityNameDisabled: " + context.arrayContextValueCheckEntityNameDisabled);

def workEffortId = parameters.workEffortId;
if (UtilValidate.isEmpty(workEffortId)) {
	workEffortId = parameters.weTransWeId;
}
//Debug.log("#### parameters.selectedId: " + parameters.selectedId);

if (UtilValidate.isNotEmpty(parameters.selectedId)) {
	def localWorkEffortId = parameters.selectedId.substring(parameters.selectedId.lastIndexOf("_")+1);
	if (!workEffortId.equals(localWorkEffortId)) {
		workEffortId = localWorkEffortId;
	}
}

def workEffortTypeId = parameters.workEffortTypeId;
if (UtilValidate.isEmpty(workEffortId) && UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
	def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false);
	if (UtilValidate.isNotEmpty(workEffortAnalysis)) {
		workEffortTypeId = workEffortAnalysis.workEffortTypeId;
		workEffortId = workEffortAnalysis.workEffortId;
		if (UtilValidate.isEmpty(workEffortAnalysis.workEffortId)) {
			context["whiteTab"] = "whiteTab";
		}
	}
}

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);

if(UtilValidate.isNotEmpty(workEffortTypeId) && completeTabList.size() > 0) {
	//def workEffortType = workEffort.getRelatedOneCache("WorkEffortType");
	def workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffortTypeId], false);
	GroovyUtil.runScriptAtLocation(baseExcludedScript, context);

	completeTabList.each { currentTab ->
		def hideFolder = false;
		if(!arrayContextValueCheckEntityNameDisabled.contains(currentTab.contentTypeId)){
			hideFolder = true;
		}
		else{
			def layoutContentId = null;
			def workEffortTypeFolder = EntityUtil.getFirst(delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", currentTab.contentTypeId)]), null, ["sequenceNum"], null, true));
			if(UtilValidate.isNotEmpty(workEffortTypeFolder)){
				layoutContentId = workEffortTypeFolder.contentId;
			}
			if("CON_AN_LAYOUT".equals(currentTab.contentTypeId)){
				GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getAttachementForWorkEffortAchieveContext.groovy", context);
				if(!"Y".equals(context.contentPresent)){
					hideFolder = true;
				}
			} else if(UtilValidate.isEmpty(layoutContentId) && "OBJ_AN_LAYOUT".equals(currentTab.contentTypeId)){
				context.hideMainFolder = true;
				parameters.tableActiveTab = [:];
				parameters.tableActiveTab.WorkEffortAchieveExtTabMenu = "1";// perchè il folder principale ci deve sempre essere...
			} else if(UtilValidate.isEmpty(layoutContentId)){
				hideFolder = true;
			} else {
				if(UtilValidate.isNotEmpty(workEffortTypeFolder) && UtilValidate.isNotEmpty(workEffortTypeFolder.etch)) {
					context[currentTab.contentTypeId + "_title"] = workEffortTypeFolder.etch;
				}
				else {
					if("IND_AN_LAYOUT".equals(currentTab.contentTypeId)){
						context[currentTab.contentTypeId + "_title"] = context.uiLabelMap["FormFieldTitle_wePurposeTypeIdInd"];
					} else if(UtilValidate.isNotEmpty(context.uiLabelMap[currentTab.contentTypeId]) && !currentTab.contentTypeId.equals(context.uiLabelMap[currentTab.contentTypeId])) {
						context[currentTab.contentTypeId + "_title"] = context.uiLabelMap[currentTab.contentTypeId];
					}
					else {
						context[currentTab.contentTypeId + "_title"] = currentTab.description;
					}
				}
				// ricerca elementi nel tab...
				if("OBJ_AN_LAYOUT".equals(currentTab.contentTypeId)){
					// Gli elementi nel primo tab sono contenuti in context.listIt
				}
				if("IND_AN_LAYOUT".equals(currentTab.contentTypeId)){					
					
					def localContext = context;
					localContext.workEffortId = parameters.workEffortId;
					localContext.workEffortAnalysisId = parameters.workEffortAnalysisId;
					def listaInd = GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWorkEffortAchieveInqMeasViewOnlyList.groovy", localContext);
					
					try{
						def indSize = new Integer(listaInd.size());
						if(indSize <= 0){
							hideFolder = true;
						}
					}catch (e){
						hideFolder = true;
					}
				}	
				if("DET_AN_LAYOUT".equals(currentTab.contentTypeId)){
					if(UtilValidate.isNotEmpty(workEffortId)){
						
						/** Caso snapshot **/
						def conditionList = [];
						conditionList.add(EntityCondition.makeCondition("workEffortIdFrom", workEffortId));
						
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
						def childList = delegator.findList("WorkEffortAchieveViewExtGrouped", EntityCondition.makeCondition(conditionList), null, ["sequenceNum", "sourceReferenceId", "workEffortId"], null, true);
						context.childList = childList;
						GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/achieveChildViewList.groovy", context);
						GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/loadChildView.groovy", context);
						GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/achieveChildViewMap.groovy", context);
						
						//Bug 4686 la lista mi serve solo se seleziono il folder
						if (UtilValidate.isEmpty(parameters.folderIndex)) {
							context.listIt = [];
						}
						
						try{
							def childSize = new Integer(context.childMaxSize);
							if(childSize <= 0){
								hideFolder = true;
							}
						}catch (e){
							hideFolder = true;
						}
					}else{
						hideFolder = false;
					}
				}
			}
		}
		if(hideFolder){
			context[currentTab.contentTypeId] = currentTab.contentTypeId;
		}
	}
}
