import org.ofbiz.base.util.*;

res = "success";

def workEffortAnalysisId = parameters.workEffortAnalysisId;

if (UtilValidate.isNotEmpty(workEffortAnalysisId)) {
	def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : workEffortAnalysisId], false);
	if (UtilValidate.isNotEmpty(workEffortAnalysis)) {
		def dataVisibility = workEffortAnalysis.dataVisibility;
		
		if ("RESP_VIS".equals(dataVisibility)) {
			parameters.entityName = "WorkEffortAchieveViewResponsible";	
		} else if ("ROLE_VIS".equals(dataVisibility)) {
			parameters.entityName = "WorkEffortAchieveViewRole";
		}
	}
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/populateManagement.groovy", context);

return res;