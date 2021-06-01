import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
** Utilizzato epr caricarire le sezioni del workEffortAnalysis
**/

Debug.log("******************************* getPrintBirtWorkEffortAnalysisList.groovy -> parameters.workEffortAnalysisId = " + parameters.workEffortAnalysisId);

listWorkEffortContentType = [];
if (UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {	
	
	workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", [workEffortAnalysisId: parameters.workEffortAnalysisId], false);
	
	if(UtilValidate.isNotEmpty(workEffortAnalysis)){
		map = [:];
		if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortTypeIdSez1)){			
			workEffortTypeIdSez1 = delegator.findOne("WorkEffortType", [workEffortTypeId: workEffortAnalysis.workEffortTypeIdSez1], false);
			map.titolo = 'workEffortTypeIdSez1';
			map.workEffortTypeId = workEffortAnalysis.workEffortTypeIdSez1;
			map.description = workEffortTypeIdSez1.description;
			listWorkEffortContentType.add(map);
		}
		
		map = [:];
		if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortTypeIdSez2)){			
			workEffortTypeIdSez2 = delegator.findOne("WorkEffortType", [workEffortTypeId: workEffortAnalysis.workEffortTypeIdSez2], false);
			map.titolo = 'workEffortTypeIdSez2';
			map.workEffortTypeId = workEffortAnalysis.workEffortTypeIdSez2;
			map.description = workEffortTypeIdSez2.description;
			listWorkEffortContentType.add(map);
		}
		
		map = [:];
		if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortTypeIdSez3)){			
			workEffortTypeIdSez3 = delegator.findOne("WorkEffortType", [workEffortTypeId: workEffortAnalysis.workEffortTypeIdSez3], false);
			map.titolo = 'workEffortTypeIdSez3';
			map.workEffortTypeId = workEffortAnalysis.workEffortTypeIdSez3;
			map.description = workEffortTypeIdSez3.description;
			listWorkEffortContentType.add(map);
		}
		
		map = [:];
		if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortTypeIdSez4)){			
			workEffortTypeIdSez4 = delegator.findOne("WorkEffortType", [workEffortTypeId: workEffortAnalysis.workEffortTypeIdSez4], false);
			map.titolo = 'workEffortTypeIdSez4';
			map.workEffortTypeId = workEffortAnalysis.workEffortTypeIdSez4;
			map.description = workEffortTypeIdSez4.description;
			listWorkEffortContentType.add(map);
		}
		
		map = [:];
		if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortTypeIdSez5)){			
			workEffortTypeIdSez5 = delegator.findOne("WorkEffortType", [workEffortTypeId: workEffortAnalysis.workEffortTypeIdSez5], false);
			map.titolo = 'workEffortTypeIdSez5';
			map.workEffortTypeId = workEffortAnalysis.workEffortTypeIdSez5;
			map.description = workEffortTypeIdSez5.description;
			listWorkEffortContentType.add(map);
		}
		
		map = [:];
		if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortTypeIdSez6)){			
			workEffortTypeIdSez6 = delegator.findOne("WorkEffortType", [workEffortTypeId: workEffortAnalysis.workEffortTypeIdSez6], false);
			map.titolo = 'workEffortTypeIdSez6';
			map.workEffortTypeId = workEffortAnalysis.workEffortTypeIdSez6;
			map.description = workEffortTypeIdSez6.description;
			listWorkEffortContentType.add(map);
		}
	}
	
	
}

context.listWorkEffortContentType = listWorkEffortContentType

Debug.log("******************************* getPrintBirtWorkEffortAnalysisList.groovy -> context.listWorkEffortContentType = " + context.listWorkEffortContentType);