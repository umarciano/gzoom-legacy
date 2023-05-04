import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusCntParamsEvaluator;

def getActiveTab(listsize) {
	Debug.log(" - Devo nasconde il primo folder");
	// il folder principale e' invibibile, capire quale folder si deve aprire al posto del primo,
    // innanzitutto in base al context.folderIndex, cioe' in base al click dell'utente 
    // poi in base al cookie
	context.hideMainFolder = true;
	// GN-5280
	if (UtilValidate.isEmpty(context.folderIndex) && (UtilValidate.isNotEmpty(parameters.tableActiveTab) && UtilValidate.isNotEmpty(parameters.tableActiveTab.WorkEffortViewStandardManagementTabMenu) && Integer.valueOf(parameters.tableActiveTab.WorkEffortViewStandardManagementTabMenu) != Integer.valueOf(0))) {
		context.folderIndex = parameters.tableActiveTab.WorkEffortViewStandardManagementTabMenu;
		parameters.folderIndex = parameters.tableActiveTab.WorkEffortViewStandardManagementTabMenu;
		context.folderIndex = parameters.tableActiveTab.WorkEffortViewStandardManagementTabMenu;
	}		
    if (UtilValidate.isEmpty(context.folderIndex) && (UtilValidate.isNotEmpty(parameters.tableActiveTab) && UtilValidate.isNotEmpty(parameters.tableActiveTab.WorkEffortViewManagementTabMenu) && Integer.valueOf(parameters.tableActiveTab.WorkEffortViewManagementTabMenu) != Integer.valueOf(0))) {
		context.folderIndex = parameters.tableActiveTab.WorkEffortViewManagementTabMenu;
        parameters.folderIndex = parameters.tableActiveTab.WorkEffortViewManagementTabMenu;
        context.folderIndex = parameters.tableActiveTab.WorkEffortViewManagementTabMenu;
    }
    
    if (UtilValidate.isEmpty(context.folderIndex) && (UtilValidate.isEmpty(parameters.tableActiveTab) || UtilValidate.isEmpty(parameters.tableActiveTab.WorkEffortViewManagementTabMenu) || Integer.valueOf(parameters.tableActiveTab.WorkEffortViewManagementTabMenu) == Integer.valueOf(0))) {
		parameters.tableActiveTab = [:];
	    
    	if(listsize >= 1) {
	    	parameters.tableActiveTab.WorkEffortViewManagementTabMenu = 1;
	    } else {
	    	 // Non posso nascondere il primo folder perche ho solo il primo folder
	    	Debug.log(" - Non posso nascondere il primo folder perche ho solo il primo folder");
	    	parameters.tableActiveTab.WorkEffortViewManagementTabMenu = 0;
		    context.hideMainFolder = null;
	    }
    	context.folderIndex = parameters.tableActiveTab.WorkEffortViewManagementTabMenu;
        parameters.folderIndex = parameters.tableActiveTab.WorkEffortViewManagementTabMenu;
        context.folderIndex = parameters.tableActiveTab.WorkEffortViewManagementTabMenu;
    }
    parameters.tableActiveTab = [:];
    parameters.tableActiveTab.WorkEffortViewManagementTabMenu = context.folderIndex;// perchè il folder principale ci deve sempre essere...
	parameters.tableActiveTab.WorkEffortViewStandardManagementTabMenu = context.folderIndex;// perchè il folder principale ci deve sempre essere...
}

def baseExcludedScript = "component://base/webapp/common/WEB-INF/actions/getExcludedContent.groovy";
def baseExcludedPermissionScript = "component://base/webapp/common/WEB-INF/actions/getExcludedContentPermission.groovy";
def completeTabList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition([EntityCondition.makeCondition("contentIdStart", context.rootFolder),
																							 EntityCondition.makeCondition("caContentAssocTypeId", "FOLDER_OF")]), null, null, null, false);

def workEffortId = parameters.workEffortId;
if (UtilValidate.isEmpty(workEffortId)) {
	workEffortId = parameters.weTransWeId;
}

if ("DELETE".equals(parameters.operation)) {
	if (UtilValidate.isNotEmpty(parameters.parentSelectedId)) {
		parameters.selectedId = parameters.parentSelectedId;
	}
}
if(UtilValidate.isEmpty(workEffortId) && "WorkEffortView".equals(parameters.entityName) && "Y".equals(parameters.newInsert) && UtilValidate.isNotEmpty(parameters.id)) {
	//caso inserimento da scheda cambiare
	def  work1 = parameters.id.substring(parameters.id.indexOf("workEffortId=")+13);
	workEffortId = work1.indexOf("|") > 0 ? work1.substring(0, work1.indexOf("|")) : work1;
	
}
/* Eliminato perche nell'inserimento di un nuovo figlio obiettivo
 * va a sostituire workEffortId = localWorkEffortId dove localWorkEffortId
 * e ancora il padre.
 * 
else if(!"Y".equals(parameters.fromDelete)) {
	if (UtilValidate.isNotEmpty(parameters.selectedId)) {
		def localWorkEffortId = parameters.selectedId.substring(parameters.selectedId.lastIndexOf("_")+1);
		if (!workEffortId.equals(localWorkEffortId)) {
			workEffortId = localWorkEffortId;
		}
	}
}*/

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);
def paramsMap = [:];
if (UtilValidate.isNotEmpty(workEffort)) {
	def workEffortTypeStatusCntParamsEvaluator = new WorkEffortTypeStatusCntParamsEvaluator(delegator);
	workEffortTypeStatusCntParamsEvaluator.init(workEffort.workEffortId, workEffort.workEffortTypeId, workEffort.currentStatusId, "");
	workEffortTypeStatusCntParamsEvaluator.run();
	paramsMap = workEffortTypeStatusCntParamsEvaluator.getParamsContentMap();
}

if(UtilValidate.isNotEmpty(workEffort) && completeTabList.size() > 0) {
	def workEffortType = workEffort.getRelatedOne("WorkEffortType");
	
	def enabledTabsByType = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "FOLDER"), EntityCondition.makeCondition("isVisible",EntityOperator.NOT_EQUAL, "N")]), null, null, null, false);
	def enabledTabsByTypeContentIdList = EntityUtil.getFieldListFromEntityList(enabledTabsByType, "contentId", true);
		
	GroovyUtil.runScriptAtLocation(baseExcludedPermissionScript, context);
	GroovyUtil.runScriptAtLocation(baseExcludedScript, context);

	completeTabList.each { currentTab ->
	    if(!enabledTabsByTypeContentIdList.contains(currentTab.contentId) || isContentToHide(currentTab.contentId, paramsMap) || ("WEFLD_SNAP".equals(currentTab.contentId) && UtilValidate.isNotEmpty(workEffort.workEffortSnapshotId))) {
            // lo chiamo qui, perche il caos di cosa mostrare e' solo se il primo folder e' nascosto
	    	if ("WEFLD_MAIN".equals(currentTab.contentId)){ 
            	getActiveTab(enabledTabsByTypeContentIdList.size());
            } else {
            	// non carico tutti i folder che non sono il primo, il primo deve sempre esserci anche se invisibile
            	context[currentTab.contentId] = currentTab.contentId;
            }
		}
		else {
			def workEffortTypeFolder = EntityUtil.getFirst(EntityUtil.filterByCondition(enabledTabsByType, EntityCondition.makeCondition("contentId", currentTab.contentId)));
			def folderEtch = "";
			if (UtilValidate.isNotEmpty(workEffortTypeFolder)) {
				folderEtch = context.localeSecondarySet == "Y" ? workEffortTypeFolder.etchLang : workEffortTypeFolder.etch;
			}
			
			if(UtilValidate.isNotEmpty(folderEtch)) {
				context[currentTab.contentId + "_title"] = folderEtch;
			}
			else {
				if(UtilValidate.isNotEmpty(context.uiLabelMap[currentTab.contentId]) && !currentTab.contentId.equals(context.uiLabelMap[currentTab.contentId])) {
					context[currentTab.contentId + "_title"] = context.uiLabelMap[currentTab.contentId];
				}
				else {
					context[currentTab.contentId + "_title"] = currentTab.description;
				}
			}
			
		}
	}
	
}

def isContentToHide(contentId, paramsMap) {
	if (UtilValidate.isNotEmpty(paramsMap)) {
		def contentParamsMap = paramsMap.get(contentId);
		if (UtilValidate.isNotEmpty(contentParamsMap) && contentParamsMap.containsKey("hideContent") && "Y".equals(contentParamsMap.get("hideContent"))) {
			return true;
		}		
	}
	return false;
}
