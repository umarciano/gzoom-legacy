import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.security.Security;

Debug.log("******************************* getPrintBirtList.groovy -> parameters.repContextContentId = " + parameters.repContextContentId);
Debug.log("******************************* getPrintBirtList.groovy -> context.justLoadList = " + context.justLoadList);
if (!"Y".equals(context.justLoadList)) {
	if (UtilValidate.isNotEmpty(parameters.repContextContentId)) {		
		if (UtilValidate.isEmpty(context.listIt) && (UtilValidate.isEmpty(context.listIsEmpty) || context.listIsEmpty != 'Y')) {
		
			def conditionList = [EntityCondition.makeCondition("contentIdStart", parameters.repContextContentId),
				                 EntityCondition.makeCondition("contentTypeId", "REPORT"),
								 EntityCondition.makeCondition("caContentAssocTypeId", "REP_PERM")];
							 
			context.listIt = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
		} else {
			def currentList = [];
			
			context.listIt.each  { content ->
				def conditionList = [EntityCondition.makeCondition("contentIdTo", content.contentId),
					EntityCondition.makeCondition("contentId", parameters.repContextContentId),
					EntityCondition.makeCondition("contentAssocTypeId", "REP_PERM")];
				
				def assocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditionList), null, ["sequenceNum"], null, true);
				if (UtilValidate.isNotEmpty(assocList)) {
					if (UtilValidate.isEmpty(EntityUtil.filterByCondition(currentList, EntityCondition.makeCondition("contentId", content.contentId)))) {
						currentList.add(content);
					}
				}
			}
			
			context.listIt = currentList;
		}
	}
	context.justLoadList = "Y";
} else {
	context.justLoadList = "N";
}

// Controllo permessi Valutato: se l'utente ha il permesso EMPLVALUTATO_VIEW, 
// mostra solo il report REPORT_SOO e nasconde gli altri
if (context.listIt && userLogin) {
    Security security = request.getAttribute("security");
    if (security && security.hasPermission("EMPLVALUTATO_VIEW", userLogin)) {
        Debug.log("******************************* getPrintBirtList.groovy -> Utente Valutato rilevato, applicando filtri");
        
        // Lista dei report da escludere per gli utenti Valutato
        def excludedReports = ["REPORT_SLVI", "REPORT_LVI", "REPORT_LVI_STA", "REPORT_LVI_RIE"];
        
        // Filtra la lista mantenendo solo i report consentiti
        def filteredList = [];
        context.listIt.each { report ->
            if (!excludedReports.contains(report.contentId)) {
                filteredList.add(report);
                Debug.log("******************************* getPrintBirtList.groovy -> Report consentito: " + report.contentId);
            } else {
                Debug.log("******************************* getPrintBirtList.groovy -> Report nascosto: " + report.contentId);
            }
        }
        
        context.listIt = filteredList;
    }
}

Debug.log("******************************* getPrintBirtList.groovy -> context.listIt = " + context.listIt);