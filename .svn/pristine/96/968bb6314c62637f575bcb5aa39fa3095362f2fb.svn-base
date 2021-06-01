import java.rmi.Naming.ParsedNamingURL;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


//Debug.log("#### getWorkEffortAchieveViewExtManagerExcludedContent ");


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
//Debug.log("#### workEffortId: " + workEffortId);


if (UtilValidate.isNotEmpty(parameters.selectedId)) {
	def localWorkEffortId = parameters.selectedId.substring(parameters.selectedId.lastIndexOf("_")+1);
	if (!workEffortId.equals(localWorkEffortId)) {
		workEffortId = localWorkEffortId;
	}
}

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);

if(UtilValidate.isNotEmpty(workEffort) && completeTabList.size() > 0) {
	def workEffortType = workEffort.getRelatedOneCache("WorkEffortType");
	//Debug.log("#### workEffortType: " + workEffortType);
	//Debug.log("#### arrayContextValueCheckEntityNameDisabled: " + arrayContextValueCheckEntityNameDisabled);
	
	GroovyUtil.runScriptAtLocation(baseExcludedScript, context);

	completeTabList.each { currentTab ->
		def hideFolder = false;
		//Debug.log("#### currentTab in context : " + currentTab.contentTypeId + " ha valore " + context[currentTab.contentTypeId]);
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
				// ricerca elementi nel tab
				if("OBJ_AN_LAYOUT".equals(currentTab.contentTypeId)){
					try{
						def weChildren = new Integer(context.weChildren.size());
						if(weChildren <= 0){
							context.hideMainFolder = true;
							parameters.tableActiveTab = [:];
							parameters.tableActiveTab.WorkEffortAchieveExtTabMenu = "1";// perchè il folder principale ci deve sempre essere...
						}
					}catch (e){
						context.hideMainFolder = true;
						parameters.tableActiveTab = [:];
						parameters.tableActiveTab.WorkEffortAchieveExtTabMenu = "1";// perchè il folder principale ci deve sempre essere...
					}
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
					
					//inserisco le descrizioni delle tabelle
					context.childDesc1List = delegator.findList("WorkEffortTypeContentAndContentType", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffort.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "OBJ_AN_LAYOUT")]), null, null, null, true);
					
					context.childDesc2List = delegator.findList("WorkEffortTypeContentAndContentType", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffort.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "DET_AN_LAYOUT")]), null, null, null, true);
					
					/** Caso snapshot **/
					def conditionList = [];
					conditionList.add(EntityCondition.makeCondition("workEffortIdFrom", workEffortId));
					
					if (UtilValidate.isNotEmpty(parameters.snapshot) && parameters.snapshot == 'Y') {
						conditionList.add(EntityCondition.makeCondition("workEffortSnapshotId", EntityOperator.NOT_EQUAL, null));							
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
					
				}
				if ("NOT_AN_LAYOUT".equals(currentTab.contentTypeId)) {
					def workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "NOT_AN_LAYOUT")]), null, null, null, false);
					if (UtilValidate.isEmpty(workEffortTypeContentList)) {
						hideFolder = true;
					}
				}
			}
		}
		if(hideFolder){
			context[currentTab.contentTypeId] = currentTab.contentTypeId;
		}
	}
}
