import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/*Debug.log(" - context.workEffortTypeId: " + context.workEffortTypeId);
Debug.log(" - parameters.workEffortTypeId: " + parameters.workEffortTypeId);
Debug.log(" - context.workEffortId: " + context.workEffortId);
Debug.log(" - parameters.workEffortId: " + parameters.workEffortId);

Debug.log(" - context.workEffortIdFrom: " + context.workEffortIdFrom);
Debug.log(" - parameters.workEffortIdFrom: " + parameters.workEffortIdFrom);
*/

/* GN-667 scambio recupero workEffortTypeId tra context e parameters perche negli screen il workEffortId e poi recuperato dai parameters */
/*def workEffortTypeId = context.workEffortTypeId;
if (UtilValidate.isEmpty(workEffortTypeId)) {
	workEffortTypeId = parameters.workEffortTypeId;
}*/

def workEffortTypeId = parameters.workEffortTypeId;
if (UtilValidate.isEmpty(workEffortTypeId)) {
	workEffortTypeId = context.workEffortTypeId;
}

if (UtilValidate.isEmpty(workEffortTypeId)) {
	//ricavo workEffortTypeId dal workeffortId
	if (UtilValidate.isNotEmpty(wrk)) {
		//Debug.log(" - wrk.workEffortId: " + wrk.workEffortId);
		workEffortTypeId = wrk.workEffortTypeId;
	}
}	

/*Debug.log(" - context.layoutType: " + context.layoutType + " - " + parameters.layoutType);
Debug.log(" - context.folderIndex: " + context.folderIndex + " - " + parameters.folderIndex);
Debug.log(" - workEffortTypeId: " + workEffortTypeId);
Debug.log(" - context.rootFolder: " + context.rootFolder);
Debug.log(" - parameters.contentId: " + parameters.contentId);
*/
def layoutType = UtilValidate.isNotEmpty(context.layoutType) ? context.layoutType : parameters.layoutType;
def folderIndex = UtilValidate.isNotEmpty(context.folderIndex) ? context.folderIndex : parameters.folderIndex;
Debug.log(" - context.folderContentIds: " + context.folderContentIds);
Debug.log(" - folderIndex: " + folderIndex);


if (UtilValidate.isEmpty(layoutType) && UtilValidate.isNotEmpty(context.folderContentIds)) {
	if(UtilValidate.isNotEmpty(folderIndex) && Integer.valueOf(folderIndex) < context.folderContentIds.size()){
		layoutType = context.folderContentIds[Integer.valueOf(folderIndex)];
	} else{
		layoutType = context.folderContentIds[0];
	}
}
//Debug.log(" - context.layoutType: " + context.layoutType);
//Debug.log(" - layoutType: " + layoutType);
context.layoutType = layoutType;
//Debug.log(" - specialized: " + context.specialized);
//Debug.log(" - specialized: " + parameters.specialized);

def tree = context.rootTree;
if (UtilValidate.isEmpty(tree)) {
	tree = parameters.rootTree;
}
if (UtilValidate.isEmpty(tree) || "N".equals(tree)){
	tree = context.specialized;
}
if (UtilValidate.isEmpty(tree) || "N".equals(tree)){
	tree = parameters.specialized;
}
if (UtilValidate.isEmpty(tree) || "N".equals(tree)){
	tree = (UtilValidate.isEmpty(context.screenNameListIndex) ? "N" : "Y");
}


def baseExcludedScript = "component://base/webapp/common/WEB-INF/actions/getExcludedContent.groovy";

if(tree && "Y".equals(tree)) {
	def enabledTabsByType = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "FOLDER")]), null, null, null, false);
	
	def enabledTabsByTypeContentIdList = EntityUtil.getFieldListFromEntityList(enabledTabsByType, "contentId", true);
		
	GroovyUtil.runScriptAtLocation(baseExcludedScript, context);
	
	if (UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(workEffortTypeId)) {
		def layoutContent = EntityUtil.getFirst(delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", layoutType)]), null, ["sequenceNum"], null, false));
		
		if (UtilValidate.isNotEmpty(layoutContent)) {
			context.layoutContentId = layoutContent.contentId;
		}
	}
}