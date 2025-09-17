import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.*;

def workEffortTypeId = parameters.workEffortTypeId;

def listIt = [];
/**
 * Casi di stamper per ANALISI e SCHEDE OBIETTIVO
 */
if (UtilValidate.isNotEmpty(parameters.workEffortAnalysisId)) {
    def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], true);
	if (UtilValidate.isNotEmpty(workEffortAnalysis) ) {
        //GN-977 e GN-1055
		workEffortTypeId = null;//workEffortAnalysis.workEffortTypeId;
		
		def reportId = workEffortAnalysis.reportId;
		if (UtilValidate.isNotEmpty(reportId)) {
			def reportContent = delegator.findOne("Content", ["contentId" : reportId], true);
			if (UtilValidate.isNotEmpty(reportContent)) {
				listIt.add(reportContent);
			}
		}
	}
} else {

//  Debug.log("..............  workEffortIdRoot="+parameters.workEffortIdRoot);
//  Debug.log("..............  workEffortId="+parameters.workEffortId);
//  Debug.log("..............  workEffortIdChild="+parameters.workEffortIdChild);
	
	def workEffortId = parameters.workEffortIdChild;
	
	if (UtilValidate.isEmpty(workEffortId)) {
		workEffortId = parameters.workEffortIdRoot;
	}	
	if (UtilValidate.isEmpty(workEffortId)) {
		workEffortId = parameters.workEffortId;
	}

	def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], true);

	if (UtilValidate.isNotEmpty(workEffort)) {
		def workEffortParent = delegator.findOne("WorkEffort", ["workEffortId" : workEffort.workEffortParentId], true);
		if (UtilValidate.isNotEmpty(workEffortParent)) {
			workEffortTypeId = workEffortParent.workEffortTypeId;	
		}
	}
	// Debug.log("..............  workEffortId="+workEffortId);
	
}
if (UtilValidate.isNotEmpty(workEffortTypeId)) {
	def workEffortTypeCondition = EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId);
	
	/*Prendo la lista dei report del figlio
	 * def workEffortIdChild = parameters.workEffortIdChild;
	if (UtilValidate.isNotEmpty(workEffortIdChild)) {
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortIdChild], true);
	
		if (UtilValidate.isNotEmpty(workEffort)) {
			workEffortTypeCondition = EntityCondition.makeCondition([workEffortTypeCondition, EntityCondition.makeCondition("workEffortTypeId", workEffort.workEffortTypeId)], EntityJoinOperator.OR);
		}
	}*/
	
	def conditionList = [workEffortTypeCondition,
		EntityCondition.makeCondition("weTypeContentTypeId", EntityOperator.IN, ["REPORT", "JREPORT"])];
	
	def workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffortTypeId], false);
	if (UtilValidate.isNotEmpty(workEffortType)) {
		def permission = ContextPermissionPrefixEnum.getPermissionPrefix(workEffortType.parentTypeId);
		if (UtilValidate.isNotEmpty(permission)) {
			if (! security.hasPermission(permission + "MGR_ADMIN", userLogin)) {
				 conditionList = [workEffortTypeCondition,
				     EntityCondition.makeCondition("weTypeContentTypeId", EntityOperator.IN, ["REPORT", "JREPORT"]),
				     EntityCondition.makeCondition("onlyAdmin", "N")];
			}			
		}
	}
	// Debug.log("getWorkEffortPrintBirtList conditionList " + conditionList);
    
	def workEffortTypeContentList = delegator.findList("WorkEffortTypeAndContent", EntityCondition.makeCondition(conditionList), null, ["sequenceNum"], null, true);
	// Debug.log("getWorkEffortPrintBirtList workEffortTypeContentList " + workEffortTypeContentList);
    if (UtilValidate.isNotEmpty(workEffortTypeContentList)) {
		listIt.addAll(workEffortTypeContentList);
	} else {
		/**
		 * La lista � vuota e non voglio caricare tutti i report
		 */
		context.listIsEmpty = 'Y';
	}
}

/**
 *  Casi Stamper per TIPO OBIETTIVO E Casi Stamper per UNITA CONTABILI E EXTRA e per UNITA ORGANIZZATIVE
 */
if (UtilValidate.isNotEmpty(parameters.repContextContentId) && (parameters.repContextContentId == "WE_PRINT_BILANCIO" || parameters.repContextContentId == "WE_PRINT_WET" || parameters.repContextContentId == "WE_PRINT_UCF" || parameters.repContextContentId == "WE_PRINT_UO")) {	
	def conditionList = [EntityCondition.makeCondition("contentTypeId", "REPORT"), EntityCondition.makeCondition("contentIdStart", parameters.repContextContentId)];
	def listContent = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
		listIt.addAll(listContent);
}

/**
 * FILTERING per permesso EMPLVALUTATO_VIEW
 * Il Valutato deve vedere solo "Stampa scheda Obiettivi" e non "Lista Valutazioni Individuali"
 */
if (security.hasPermission("EMPLVALUTATO_VIEW", userLogin)) {
    // Lista di contentId da escludere per gli utenti Valutato
    def excludedReportsForValutato = ["REPORT_SLVI", "REPORT_LVI", "REPORT_LVI_STA", "REPORT_LVI_RIE"];
    
    // Filtra la lista escludendo i report non autorizzati per il Valutato
    def filteredList = [];
    listIt.each { item ->
        def contentId = item.contentId;
        if (!excludedReportsForValutato.contains(contentId)) {
            filteredList.add(item);
        }
    }
    
    listIt = filteredList;
    Debug.log("Filtered list size: " + listIt.size());
}

// Debug.log("................... LIST " +listIt);
context.listIt = listIt;