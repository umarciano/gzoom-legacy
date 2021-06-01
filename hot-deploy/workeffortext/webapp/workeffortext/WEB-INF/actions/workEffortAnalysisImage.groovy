import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.entity.condition.*;

def listNew = [];
if (UtilValidate.isNotEmpty(workEffortAnalysisList)) {
	for(GenericValue value: workEffortAnalysisList){
	
		def localContext = MapStack.create(context);
		localContext.parameters.workEffortId = value.workEffortId;
		localContext.parameters.workEffortAnalysisId = value.workEffortAnalysisId;
		localContext.parameters.headerEntityName = "WorkEffortAchieveView";
		
		context.item = GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/createAchieveHeaderImage.groovy", localContext);

		if(UtilValidate.isNotEmpty(context.item)){
			context.item.description = value.description;	
			listNew.add(context.item);
		}else{
			listNew.add(value);
		}
		
	}
	context.workEffortAnalysisListImage = listNew;
}

