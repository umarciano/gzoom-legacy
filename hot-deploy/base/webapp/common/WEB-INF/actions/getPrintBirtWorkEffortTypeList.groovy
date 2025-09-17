import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.security.Security;

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

// Controllo permessi Valutato: se l'utente ha il permesso EMPLVALUTATO_VIEW, 
// mostra solo il report REPORT_SOO e nasconde gli altri
if (context.listReport && userLogin) {
    if (security && security.hasPermission("EMPLVALUTATO_VIEW", userLogin)) {
        // Log essenziale per audit
        Debug.log("EMPLVALUTATO_VIEW: Filtraggio report applicato per utente " + userLogin.userLoginId);
        
        // Lista dei report da escludere per gli utenti Valutato
        def excludedReports = ["REPORT_SLVI", "REPORT_LVI", "REPORT_LVI_STA", "REPORT_LVI_RIE"];
        
        // Filtra la lista mantenendo solo i report consentiti
        def filteredList = [];
        context.listReport.each { report ->
            if (!excludedReports.contains(report.contentId)) {
                filteredList.add(report);
            }
        }
        
        context.listReport = filteredList;
    }
}