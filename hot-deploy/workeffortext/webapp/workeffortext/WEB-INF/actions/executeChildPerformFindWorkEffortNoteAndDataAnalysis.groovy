import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.inputFields.internalNote = "N";



if(UtilValidate.isEmpty(parameters.workEffortTypeId)){
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], false);
	parameters.workEffortTypeId = workEffort.workEffortTypeId;
}

def workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", parameters.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "NOT_AN_LAYOUT")]), null, null, null, false);
def workEffortTypeContentId = "";
def workEffortTypeContent = EntityUtil.getFirst(workEffortTypeContentList);
if (UtilValidate.isNotEmpty(workEffortTypeContent)) {
	workEffortTypeContentId = workEffortTypeContent.contentId;
}

parameters.sortField = "sequenceId";
if ("DATE_NOT_LAY".equals(workEffortTypeContentId)) {
	def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId": parameters.workEffortAnalysisId], false);
	context.inputFields.noteDateTime = workEffortAnalysis.referenceDate;
	context.sortField = "sequenceId";
	parameters.sortField = "sequenceId";
}
if ("ALL_NOT_LAY".equals(workEffortTypeContentId)) {
	context.sortField = "-noteDateTime|sequenceId";
	parameters.sortField = "-noteDateTime|sequenceId";
}

GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
