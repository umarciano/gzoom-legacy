import org.ofbiz.base.util.*;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;

/**
 * Dato com eparametro queryparam, mi ricavo la lista selezionata dall'utente
 * e per ogni elemento chiamo il cambio stato
 */

languageSettinngs = request.getSession().getAttribute("languageSettinngs");

def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", locale)

parameters.putAll(StringUtil.toMap(parameters.queryStringMap));

Debug.log("*****  ChangeAllStatus weContextId : " +  parameters.weContextId);
/*
 * In base al weContextId eseguo lo script di ricerca per filtrare in base al permission
 */
def findlist = "com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy";
if(parameters.weContextId == 'CTX_OR'){
	findlist = "com/mapsengineering/orgperf/executePerformFindORWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_BS'){
	findlist = "com/mapsengineering/stratperf/executePerformFindBSWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_EP'){
	findlist = "com/mapsengineering/emplperf/executePerformFindEPWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_DI'){
	findlist = "com/mapsengineering/dirigperf/executePerformFindDIWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'MANAGACC'){
	findlist = "com/mapsengineering/managacc/executePerformFindMAWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'PROJECTMGR'){
	findlist = "com/mapsengineering/projectmgrext/executePerformFindPMWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_CO'){
	findlist = "com/mapsengineering/corperf/executePerformFindCOWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_PR'){
	findlist = "com/mapsengineering/procperf/executePerformFindWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_CG'){
    findlist = "com/mapsengineering/cdgperf/executePerformFindWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_TR'){
    findlist = "com/mapsengineering/trasperf/executePerformFindWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_RE'){
    findlist = "com/mapsengineering/rendperf/executePerformFindWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_GD'){
    findlist = "com/mapsengineering/gdprperf/executePerformFindWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_PA'){
    findlist = "com/mapsengineering/partperf/executePerformFindWorkEffortRootInqy.groovy";
} else if(parameters.weContextId == 'CTX_DI'){
    findlist = "com/mapsengineering/dirigperf/executePerformFindWorkEffortRootInqy.groovy";
}
res = GroovyUtil.runScriptAtLocation(findlist, context);

def itemSuccess = 0;
def itemWarning = 0;
def itemFailed = 0;
def noPrevStatusError = "";

localResult = ServiceUtil.returnSuccess();
errorList = [];
warningList = [];
if(UtilValidate.isNotEmpty(context.listIt)){
	
	nowStamp = UtilDateTime.nowAsString();
	sessionId = HashCrypt.getDigestHash(nowStamp);
	sessionId = sessionId.substring(37);
	Debug.log("***sessionId " + sessionId);
	
	def permission = "";
	if (UtilValidate.isNotEmpty(parameters.weContextId)) {
		permission = ContextPermissionPrefixEnum.getPermissionPrefix(parameters.weContextId);
	}
	if (UtilValidate.isEmpty(permission)) {
		permission = "WORKEFFORT";
	}
	def isMgrAdmin = true;
	if (! security.hasPermission(permission + "MGR_ADMIN", userLogin)) {
		isMgrAdmin = false;
	}
	
	/**
	 * Per ogni elemento della lista vado ad eseguire il cambio di stato
	 */	
	for(item in context.listIt) {
        //GN-4280
	    if (parameters.statusType == 'PREV' && ! isMgrAdmin) {
	    	def noPreviousStatus = "";
			def workEffortTypeStatusParamsEvaluator = new WorkEffortTypeStatusParamsEvaluator(context, delegator);
			def paramsMap = workEffortTypeStatusParamsEvaluator.evaluateParams(item.workEffortTypeId, item.currentStatusId, false);
			if (UtilValidate.isNotEmpty(paramsMap) && UtilValidate.isNotEmpty(paramsMap.noPreviousStatus)) {
				noPreviousStatus = paramsMap.noPreviousStatus;
			}
	    	if ("Y".equals(noPreviousStatus)) {
	    		continue;
	    	}
	    }

		/**
		 * Controllo se lo stato � next o prev
		 */
	    def statusId = "";
	    if (parameters.statusType == 'PREV') {
	    	def conditionList = [];
			conditionList.add(EntityCondition.makeCondition("workEffortId", item.workEffortId));
			conditionList.add(EntityCondition.makeCondition("statusIdTo", item.currentStatusId));
			def statusList = delegator.findList("WorkEffortStatusValidChange", EntityCondition.makeCondition(conditionList), null, ["-statusDatetime"], null, false);
			def listItem = EntityUtil.getFirst(statusList);
			if (UtilValidate.isNotEmpty(listItem)) {
				statusId = listItem.statusId;
			} else {
				def condition = EntityCondition.makeCondition("statusIdTo", item.currentStatusId);
				def statusItemList = delegator.findList("StatusItemAndValidChangeStatusTo", condition, null, ["sequenceId"], null, false);
				def statusItem = EntityUtil.getFirst(statusItemList);
				if (UtilValidate.isNotEmpty(statusItem)) {
					statusId = statusItem.statusId;
				}
			}
	    } else {
	    	def condition = EntityCondition.makeCondition("statusId", item.currentStatusId);
	    	def statusItemList = delegator.findList("StatusItemAndValidChangeStatusTo", condition, null, ["sequenceId"], null, false);
			def statusItem = EntityUtil.getFirst(statusItemList);
			if (UtilValidate.isNotEmpty(statusItem)) {
				statusId = statusItem.statusIdTo;
			}
	    }
		if(UtilValidate.isNotEmpty(statusId)){
			def inputMap = [:];
			inputMap.workEffortId = item.workEffortId;
			inputMap.statusDatetime = UtilDateTime.nowTimestamp();
			inputMap.reason = parameters.reason;
			inputMap.sessionId = parameters.sessionId;			
			inputMap.statusId = statusId;

			localResult = dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffortRootStatus", ["parameters": inputMap, "userLogin": context.userLogin, "operation": "CREATE", "entityName": "WorkEffortStatus", "locale" : locale]);
			Debug.log(" - localResult " + localResult);
			
			/**
			 * Controllo se � andata a buon fine oppure no per aumentare il contatore
			 */			
			if(localResult.containsKey("responseMessage") && localResult.get("responseMessage").equals("success")){
				itemSuccess++;
				if (localResult.containsKey(ModelService.FAIL_MESSAGE)) {
				    warningList.add(localResult.get(ModelService.FAIL_MESSAGE));
				    itemWarning++;
				}
                // reset for next iteration
                localResult = ServiceUtil.returnSuccess();
				
			} else {
				errorList.add(ServiceUtil.getErrorMessage(localResult));
				itemFailed++;
				// reset for next iteration
				localResult = ServiceUtil.returnSuccess();
			}
		} else {
			/**
			 * Incremmo il numero di elementi che � fallito
			 */
		    itemFailed++;
		    def errorLabel = parameters.statusType == 'PREV' ? uiLabelMap.WorkEffortStatusChangeStatusError : uiLabelMap.WorkEffortStatusChangeStatusNextError;
		    def statusItem = delegator.findOne("StatusItem", ["statusId" : item.currentStatusId], false);
		    noPrevStatusError = errorLabel + " \"" + statusItem.description + "\"";	    
		    if (UtilValidate.isNotEmpty(languageSettinngs) && "Y".equals(languageSettinngs.localeSecondarySet)) {
		        noPrevStatusError = errorLabel + " \" " + statusItem.descriptionLang + "\"";
		    }
		}
		
	}
}
res = "success";

Debug.log(" - itemSuccess " + itemSuccess);
Debug.log(" - itemWarning " + itemWarning);
Debug.log(" - itemFailed " + itemFailed);
// TODO
if (ServiceUtil.isError(localResult)) {
    res = "error";
    request.setAttribute("_ERROR_MESSAGE_", localResult);
} else if (UtilValidate.isNotEmpty(noPrevStatusError)) {
    res = "error";
    errorList.add(0, uiLabelMap.ChangeStatusAll_finished + "<br>" + uiLabelMap.ChangeStatusAll_itemSuccess + itemSuccess + "<br>" + uiLabelMap.ChangeStatusAll_itemFailed + itemFailed + "<br>" + noPrevStatusError);
    
    request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
} else if (UtilValidate.isNotEmpty(errorList)) {
    res = "error";
    errorList.add(0, uiLabelMap.ChangeStatusAll_finished + "<br>" + uiLabelMap.ChangeStatusAll_itemSuccess + itemSuccess + "<br>" + uiLabelMap.ChangeStatusAll_itemFailed + itemFailed + "<br>" + uiLabelMap.WorkEffortStatusChangeError);
    
    request.setAttribute("_ERROR_MESSAGE_LIST_", errorList);
}
if (UtilValidate.isNotEmpty(warningList)) {
    request.setAttribute("failMessageList", warningList);
}
request.setAttribute("itemSuccess", itemSuccess);
request.setAttribute("itemWarning", itemWarning);
request.setAttribute("itemFailed", itemFailed);

return res;
