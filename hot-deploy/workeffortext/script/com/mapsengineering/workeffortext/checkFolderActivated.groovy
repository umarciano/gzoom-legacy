import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;

/*
Debug.log("[checkFolderActivated.groovy] - parameters.workEffortId: " + parameters.workEffortRootId);
Debug.log("[checkFolderActivated.groovy] - parameters.folder: " + parameters.folder);
Debug.log("[checkFolderActivated.groovy] - parameters.currentStatusId: " + parameters.currentStatusId);
*/

result = ServiceUtil.returnSuccess();

def isFolderVisible = "N";

// Debug.log("[checkFolderActivated.groovy] - Executing query: select * from work_effort where WORK_EFFORT_ID = '" + parameters.workEffortRootId + "'");
def workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortRootId], false);
if (UtilValidate.isNotEmpty(workEffortRoot)) {
	/*
	Debug.log("[checkFolderActivated.groovy] - One record extracted: "
		+ "workEffortId = " + workEffortRoot.workEffortId  + " "
		+ ", workEffortRootId = " + workEffortRoot.workEffortRootId + " "
		+ ", workEffortName = " + workEffortRoot.workEffortName + "");
	*/
	def workEffortTypeContentConditions = [];
	workEffortTypeContentConditions.add(EntityCondition.makeCondition("workEffortTypeId", workEffortRoot.workEffortTypeId));
	workEffortTypeContentConditions.add(EntityCondition.makeCondition("weTypeContentTypeId", "FOLDER"));
	workEffortTypeContentConditions.add(EntityCondition.makeCondition("contentId", parameters.folder));
	def workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition(workEffortTypeContentConditions), null, null, null, false);
	/*
	Debug.log("[checkFolderActivated.groovy] - Executing inner query (work effort type enabled): "
		+ "select * from WORK_EFFORT_TYPE_CONTENT wetc "
		+ "where wetc.WORK_EFFORT_TYPE_ID = '" + workEffortRoot.workEffortTypeId + "' "
		+ "and wetc.WE_TYPE_CONTENT_TYPE_ID = 'FOLDER' "
		+ "and wetc.CONTENT_ID = '" + parameters.folder + "';"
		);
	*/
	def workEffortTypeContent = EntityUtil.getFirst(workEffortTypeContentList);
	
	if (UtilValidate.isNotEmpty(workEffortTypeContent)) {
		if ("Y".equals(workEffortTypeContent.isVisible)) {
			isFolderVisible = "Y";
		}
		// Debug.log("[checkFolderActivated.groovy] - WORK_EFFORT_TYPE_CONTENT.IS_VISIBLE = " + workEffortTypeContent.isVisible + ", isFolderVisible: " + isFolderVisible);
	} else {
		// Debug.log("[checkFolderActivated.groovy] - WORK_EFFORT_TYPE_CONTENT (with constraint) is empty, isFolderVisible: " + isFolderVisible);
	}
} else {
	// Debug.log("[checkFolderActivated.groovy] - work_effort is empty, isFolderVisible: " + isFolderVisible);
}

result.put("isFolderVisible", isFolderVisible);

/*
	Gestione del parametro di ritorno hasMandatoryComment: viene eseguita una query, ad esempio

		select work_effort_type_root_id, params from work_effort_type_status where work_effort_type_root_id in 
			(select work_effort_type_id from work_effort we where we.work_effort_id = '14659')
			and current_status_id = 'GEDIR_EXEC_ACC'

	Se il risultato contiene la stringa 'hasMandatoryComment="Y"' viene ritornato il valore 'Y'
	(NB, viene riportato il valore AS IS, senza manipolazioni), altrimenti N (default)
*/
def hasMandatoryComment = "N"; // Default

WorkEffortTypeStatusParamsEvaluator paramsStatusEvaluator = new WorkEffortTypeStatusParamsEvaluator(context, delegator);
paramsStatusEvaluator.evaluateParams(workEffortRoot.workEffortTypeId, parameters.targetStatusId, true);
Debug.log("[checkFolderActivated.groovy] - targetStatusId: _" + parameters.targetStatusId + "_");
if ("Y".equals(context.hasMandatoryComment)) {
    hasMandatoryComment = "Y";
}
Debug.log("[checkFolderActivated.groovy] - hasMandatoryComment: _" + hasMandatoryComment + "_");

result.put("hasMandatoryComment", hasMandatoryComment);

return result;