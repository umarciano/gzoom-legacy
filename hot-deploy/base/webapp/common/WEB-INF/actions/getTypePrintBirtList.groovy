import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


def listTypePrintIt = [];
def listAddParamsIt = [];

def loadContentReport(contentId){
	
	def listTypePrintIt = [];
    def conditionList = [EntityCondition.makeCondition("contentId", contentId),
		EntityCondition.makeCondition("contentTypeId", "REPORT"),
		EntityCondition.makeCondition("caContentAssocTypeId", "REP_PERM")];
    
	listTypePrintIt = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
    
	if(UtilValidate.isNotEmpty(listTypePrintIt)){
		for(int i=0; i<listTypePrintIt.size(); i++){
			def outputFormat = listTypePrintIt[i].mimeTypeId;
			
			if("text/html" == outputFormat){
				listTypePrintIt[i].mimeTypeId = "html";
			}else if("application/vnd.ms-excel" == outputFormat){
				listTypePrintIt[i].mimeTypeId = "xls";
			}else if("application/vnd.ms-word" == outputFormat){
				listTypePrintIt[i].mimeTypeId = "doc";
			}else {
				listTypePrintIt[i].mimeTypeId = "pdf";
			}
		}
	}
	return listTypePrintIt;
}

/**
 *  Dato il contenId del report carico el stampe associate 
 */
if (UtilValidate.isNotEmpty(parameters.contentId)) {	
	
	def conditionList = [EntityCondition.makeCondition("contentTypeId", "TYPE_PRINT"), EntityCondition.makeCondition("contentIdStart", parameters.contentId), EntityCondition.makeCondition("caContentAssocTypeId", "TYPE_PRINT")];
	listTypePrintIt = delegator.findList("ContentAssocViewFrom",EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
	Debug.log("Found " + listTypePrintIt.size() + " listTypePrintIt with " + EntityCondition.makeCondition(conditionList));
    
	def conditionList2 = [EntityCondition.makeCondition("contentTypeId", "ADDITIONAL_PARAMS"), EntityCondition.makeCondition("contentIdStart", parameters.contentId), EntityCondition.makeCondition("caContentAssocTypeId", "ADDITIONAL_PARAMS")];
    listAddParamsIt = delegator.findList("ContentAssocViewFrom",EntityCondition.makeCondition(conditionList2), null, ["caSequenceNum"], null, true);
    Debug.log("Found " + listAddParamsIt.size() + " listAddParamsIt with " + EntityCondition.makeCondition(conditionList2));
    
	if(UtilValidate.isEmpty(listTypePrintIt)){
	    Debug.log("Old report, load content from " + parameters.contentId);
	    /**
		 * Se non ho nessun elemento passo al sistena di report vecchio, cioè carico il content del report
		 */
		listTypePrintIt = loadContentReport(parameters.contentId);
	}	
}

context.listTypePrintIt = listTypePrintIt;
context.listAddParamsIt = listAddParamsIt;