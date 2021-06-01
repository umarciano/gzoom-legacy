import org.ofbiz.base.util.*;


/**
 * readOnly crudEnum definito dallo stato per ogni singolo folder,
 * tranne in obiettivo la differenza dei due viene fatta tramite il paramettro 'isObiettivo'
 * utilizzato anche nei layout personalizzati dei folder di indicatori e risorse, passandogli contentIdSecondary e workEffortIdSecondary
*/


def isObiettivo = UtilValidate.isNotEmpty(parameters.isObiettivo) ? parameters.isObiettivo : context.isObiettivo;
if (UtilValidate.isNotEmpty(isObiettivo) && isObiettivo == "Y") {
	return;
}


def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : context.workEffortId;
// Debug.log("*****************************  parameters.workEffortId  "+ parameters.workEffortId );
// Debug.log("*****************************  context.workEffortId  "+ context.workEffortId );
// Debug.log("*****************************  workEffortId  "+ workEffortId );

def contentId = UtilValidate.isNotEmpty(parameters.contentId) ?  parameters.contentId : context.contentId;
// Debug.log("*****************************  parameters.contentId  "+ parameters.contentId );
// Debug.log("*****************************  context.contentId  "+ context.contentId );
// Debug.log("*****************************  contentId  "+ contentId );

/**
 * Nel caso degli indicatori il content lo trovo nella variabile "contentIdInd"
 */
def contentIdInd = UtilValidate.isNotEmpty(parameters.contentIdInd) ? parameters.contentIdInd : context.contentIdInd;
if (UtilValidate.isNotEmpty(contentIdInd)) {
	contentId = contentIdInd;
	Debug.log("Folder degli indicatori *****************************  contentId = " + contentId);
}
/**
 * Nel caso del contextLink per valori indicatori il content lo trovo nella variabile "contentIdSecondary"
 */
if (UtilValidate.isEmpty(contentId) && UtilValidate.isNotEmpty(parameters.contentIdSecondary)) {
	contentId = parameters.contentIdSecondary;
	context.contentId = parameters.contentIdSecondary;
	Debug.log("ContextLink per valori indicatori *****************************  contentId = "+ contentId );
}


if (UtilValidate.isEmpty(contentId)) {	
	def folderIndex = UtilValidate.isEmpty(parameters.folderIndex) ? UtilValidate.isEmpty(context.folderIndex) ? 0 : Integer.valueOf(context.folderIndex) : Integer.valueOf(parameters.folderIndex);
	
	def folderContentIds = context.folderContentIds;
	if (UtilValidate.isNotEmpty(folderContentIds) && folderContentIds.size() > folderIndex) {
		contentId = folderContentIds[folderIndex];
	}
	
}

/**
 * Nel caso di layout customizzato con formato che mostra sia gli indicatori che i rispettivi valori 
 */
def contentIdSecondary = UtilValidate.isNotEmpty(parameters.contentIdSecondary) ? parameters.contentIdSecondary : context.contentIdSecondary;
// Debug.log("*****************************  contentIdSecondary  "+ contentIdSecondary );
def workEffortIdSecondary = UtilValidate.isNotEmpty(parameters.workEffortIdSecondary) ? parameters.workEffortIdSecondary : context.workEffortIdSecondary;
// Debug.log("*****************************  workEffortIdSecondary  "+ workEffortIdSecondary );
workEffortIdSecondary = UtilValidate.isNotEmpty(workEffortIdSecondary) ? workEffortIdSecondary : workEffortId;
if (UtilValidate.isNotEmpty(contentIdSecondary)) {
	def crudEnumIdSecondary = "";
	// Debug.log("*****************************  contentIdSecondary  "+ contentIdSecondary );
	// Debug.log("*****************************  workEffortIdSecondary  "+ workEffortIdSecondary );
	def childViewSec = delegator.findOne("WorkEffortTypeStatusCntChildView", ["workEffortId" : workEffortIdSecondary, "contentId" : contentIdSecondary], false);
	if (UtilValidate.isNotEmpty(childViewSec)) {
		// Debug.log("*****************************  childViewSec  "+ childViewSec );
		crudEnumIdSecondary = childViewSec.crudEnumId;
	} else {
		def parentViewSec = delegator.findOne("WorkEffortTypeStatusCntParentView", ["workEffortId" : workEffortIdSecondary, "contentId" : contentIdSecondary], false);
		if (UtilValidate.isNotEmpty(parentViewSec)) {
			//Debug.log("*****************************  parentView  "+ parentView );
			crudEnumIdSecondary = parentViewSec.crudEnumId;
		}
	}
	context.crudEnumIdSecondary = crudEnumIdSecondary;
	Debug.log("*****************************  context.crudEnumIdSecondary  "+ context.crudEnumIdSecondary );
}

def crudEnumId = "";
//Debug.log("*****************************  contentId  "+ contentId );
// Debug.log("*****************************  workEffortId  "+ workEffortId );

//Devo gestire anche i casi dove ho i layout del folder
//prima di cercare nella vista, controllo se il contentId che mi è stato pssato è un di tipo FOLDER
//altrimenti sono nel caso si layout e prendo il contentTypeId (che in questo caso è il folder ) per fai la ricerca

def content = delegator.findOne("Content", ["contentId" : contentId], false);
if (UtilValidate.isNotEmpty(content) && UtilValidate.isNotEmpty(content.contentTypeId) && content.contentTypeId != "FOLDER") {
    contentId = content.contentTypeId;
}

def childView = delegator.findOne("WorkEffortTypeStatusCntChildView", ["workEffortId" : workEffortId, "contentId" : contentId], false);
if (UtilValidate.isNotEmpty(childView)) {
	// Debug.log("*****************************  childView  "+ childView );
	crudEnumId = childView.crudEnumId;
} else {
	def parentView = delegator.findOne("WorkEffortTypeStatusCntParentView", ["workEffortId" : workEffortId, "contentId" : contentId], false);
	if (UtilValidate.isNotEmpty(parentView)) {
		//Debug.log("*****************************  parentView  "+ parentView );
		crudEnumId = parentView.crudEnumId;
	} 
}

context.crudEnumId = crudEnumId;


// Debug.log("*****************************  contentId  "+ contentId );
// Debug.log("*****************************  workEffortId  "+ workEffortId );
Debug.log("*****************************  context.crudEnumId  "+ context.crudEnumId );
