import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;

Debug.log("******************************* getPrintBirtWorkEffortTypeList.groovy -> parameters.repContextContentId = " + parameters.repContextContentId);
Debug.log("******************************* getPrintBirtWorkEffortTypeList.groovy -> parameters.parentTypeId = " + parameters.parentTypeId);
list = []

if (UtilValidate.isNotEmpty(parameters.repContextContentId)) {
	def conditionList = [];
	def permission = ContextPermissionPrefixEnum.getPermissionPrefix(parameters.parentTypeId);
	if (! security.hasPermission(permission + "MGR_ADMIN", userLogin)) {
	    conditionList = [EntityCondition.makeCondition("contentIdStart", parameters.repContextContentId),
	                     EntityCondition.makeCondition("parentTypeId", parameters.parentTypeId),
	                     EntityCondition.makeCondition("isVisible", "Y"),
	                     EntityCondition.makeCondition("onlyAdmin", "N")];		
	} else {
	    conditionList = [EntityCondition.makeCondition("contentIdStart", parameters.repContextContentId),
	                     EntityCondition.makeCondition("parentTypeId", parameters.parentTypeId),
	                     EntityCondition.makeCondition("isVisible", "Y")];		
	}
    def setElement = ["contentId", "serviceName", "sequenceNum", "etch", "etchLang", "description", "descriptionLang", "useFilter"] as Set<String>;
    listReport = delegator.findList("WorkEffortTypeContentReportView", EntityCondition.makeCondition(conditionList), setElement, ["sequenceNum"], null, false);
    if (UtilValidate.isNotEmpty(listReport)) {
    	list.addAll(listReport);
    }
    
    /*
    * Carico la lista dei report collegati al WorkEffortAnalysis  - WorkEffortAnalysisAndTypeReportGroup
    */
    listAnalysisReport = delegator.findList("WorkEffortAnalysisAndTypeReportGroup", EntityCondition.makeCondition("parentTypeId", parameters.parentTypeId), UtilMisc.toSet("contentId", "serviceName", "etch", "etchLang", "description", "descriptionLang"), null, null, false);
    if (UtilValidate.isNotEmpty(listAnalysisReport)) {
    	for(ele in listAnalysisReport) {
    		def map = UtilMisc.toMap(ele);
    		map.workEffortAnalysisId = "Y";
    		list.add(map);
    	}
    }
    	
}
	
context.listReport = list;
Debug.log("******************************* getPrintBirtWorkEffortTypeList.groovy -> context.listReport = " + context.listReport);