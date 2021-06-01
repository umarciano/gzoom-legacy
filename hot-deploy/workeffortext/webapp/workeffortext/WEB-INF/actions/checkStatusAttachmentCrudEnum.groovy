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

def contentId = "WEFLD_CONT";
def crudEnumId = null;
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
