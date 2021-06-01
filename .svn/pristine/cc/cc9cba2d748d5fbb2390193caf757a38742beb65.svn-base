import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], true);

if(UtilValidate.isNotEmpty(workEffort)) {
		
	def workEffortType = workEffort.getRelatedOneCache("WorkEffortType");
	
	analisiObiettiviList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "OBJ_AN_LAYOUT")]), null, null, null, true);
	if(UtilValidate.isNotEmpty(analisiObiettiviList) ) {
		if(UtilValidate.isNotEmpty(analisiObiettiviList[0]) && UtilValidate.isNotEmpty(analisiObiettiviList[0].etch)){
			context["managementFirstTitle"] = analisiObiettiviList[0].etch;
		}else{
			context["managementFirstTitle"] = workEffortType.etch;
		}
	}else{
		context["managementFirstTitle"] = workEffortType.etch;
	}
	
	analisiObiettiviList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "DET_AN_LAYOUT")]), null, null, null, true);
	if(UtilValidate.isNotEmpty(analisiObiettiviList) ) {
		if(UtilValidate.isNotEmpty(analisiObiettiviList[0])){
			context["managementSecondTitle"] = analisiObiettiviList[0].etch;			
		}
	}
	
	analisiObiettiviList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), EntityCondition.makeCondition("weTypeContentTypeId", "IND_AN_LAYOUT")]), null, null, null, true);
	if(UtilValidate.isNotEmpty(analisiObiettiviList) ) {
		if(UtilValidate.isNotEmpty(analisiObiettiviList[0])){
			context["managementThirdTitle"] = analisiObiettiviList[0].etch;			
		}
	}
	
}

