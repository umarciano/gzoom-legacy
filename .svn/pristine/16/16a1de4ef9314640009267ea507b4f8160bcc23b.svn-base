import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import com.mapsengineering.base.util.*;

Debug.log("***************************************** setPropertyParameters.groovy context.folderIndex = " + context.folderIndex + " parameters.ignoreFolderIndex " + parameters.ignoreFolderIndex + " context.detail = " + context.detail);
Debug.log("***************************************** setPropertyParameters.groovy context.parentEntityName = " + context.parentEntityName + " parameters.parentEntityName " + parameters.parentEntityName);

contextManagement = context.contextManagement;
if(UtilValidate.isEmpty(contextManagement)) {
	contextManagement = parameters.contextManagement;
}
if(UtilValidate.isEmpty(contextManagement)) {
	contextManagement = "N";
}

/** se ignoreFolderIndex diverso da Y, carico il folder all'indice folderIndex */
if(UtilValidate.isNotEmpty(context.folderIndex) && 0 != context.folderIndex && !"Y".equals(parameters.ignoreFolderIndex)) {
	
	//    Debug.log("***************************************** context.arrayEntityName = " + context.arrayEntityName);
	//    Debug.log("***************************************** context.detail = " +context.detail);
	
	if(!"Y".equals(context.detail) || "Y".equals(parameters.fromDelete)) {
		if(UtilValidate.isEmpty(context.parentEntityName)){
			context.parentEntityName = parameters.parentEntityName;
		}
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/setManagementScreenList.groovy", context);
		
		if ("Y".equals(contextManagement)) {
			context.folderIndex = 0;
		}
		if (context.folderIndex < context.arrayEntityName.size() || context.folderIndex < context.managementScreenNameList.size()) {
			if (context.folderIndex < context.arrayParentEntityName.size())
				context.parentEntityName = context.arrayParentEntityName[context.folderIndex];
			context.noParentEntityName = context.arrayNoParentEntityName[context.folderIndex];
			context.relationTitle = context.arrayRelationTitle[context.folderIndex];
			context.childManagementFormListName = context.arrayChildManagementFormListName[context.folderIndex];
			context.childManagementFormListLocation = context.arrayChildManagementFormListLocation[context.folderIndex];
			context.childManagementFormListScreenName = context.arrayChildManagementFormListScreenName[context.folderIndex];
			context.childManagementFormListScreenLocation = context.arrayChildManagementFormListScreenLocation[context.folderIndex];
			context.managementFormName = context.arrayManagementFormName[context.folderIndex];
			context.managementFormLocation = context.arrayManagementFormLocation[context.folderIndex];
			context.managementFormScreenName = context.arrayManagementFormScreenName[context.folderIndex];
			context.managementFormScreenLocation = context.arrayManagementFormScreenLocation[context.folderIndex];
			context.managementFormType = context.arrayManagementFormType[context.folderIndex];
			context.sortField = context.arraySortField[context.folderIndex];
			context.actionMenuName = context.menuBarNameList[context.folderIndex]
			context.actionMenuLocation = context.menuBarLocationList[context.folderIndex]
			
			if (UtilValidate.isNotEmpty(context.arrayEntityName) && context.folderIndex < context.arrayEntityName.size()) {
				context.entityName = context.arrayEntityName[context.folderIndex];
			}
			
			context.customExecutionChildPerformFindScript = context.arrayCustomExecutionChildPerformFindScript[context.folderIndex];
		} else if (UtilValidate.isNotEmpty(context.oldManagementFormType)){
			context.managementFormType = context.oldManagementFormType
		}
		
		//		Debug.log("***************************************** setPropertyParameters.groovy -> context.entityName = " + context.entityName);
		//		Debug.log("***************************************** setPropertyParameters.groovy -> context.parentEntityName = " + context.parentEntityName);
		
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/putInputFieldsInContext.groovy", context);
		
		//		Debug.log("***************************************** context.managementFormName = " + context.managementFormName);
		//		Debug.log("***************************************** context.managementFormScreenName = " + context.managementFormScreenName);
		
		
		// executeChildPerformFind prima lo cerca tra i cookies,
		// poi prende il valore che trova nel contesto,
		// infine lo costruito in base alle pk
		if (UtilValidate.isEmpty(context.customExecutionChildPerformFindScript)) {
			// Debug.log("********************************** setPropertyParameters.grrovy lancia executeChildPerformFind.groovy");
			GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
		}
		else {
		 // Debug.log("********************************** setPropertyParameters.grrovy lancia " + context.customExecutionChildPerformFindScript);
			GroovyUtil.runScriptAtLocation(context.customExecutionChildPerformFindScript, context);
		}
	} else {
	 // Debug.log("********************************** setPropertyParameters.grrovy entityOne, setManagementScreenList, putInputFieldsInContext");
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/entityOne.groovy", context);
		// da settare adesso perchè prima per fare entityOne parentEntityName deve essere vuoto 
		if(UtilValidate.isEmpty(context.parentEntityName)){
			context.parentEntityName = parameters.parentEntityName;
		}
		
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/setManagementScreenList.groovy", context);
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/putInputFieldsInContext.groovy", context);
		
		//		Debug.log("********************************** setPropertyParameters.grrovy --> context.arrayManagementFormScreenName=" + context.arrayManagementFormScreenName);
		//		Debug.log("********************************** setPropertyParameters.grrovy --> context.arrayManagementFormScreenLocation=" + context.arrayManagementFormScreenLocation);
		
		context.actionMenuName = context.arrayDetailMenuName[folderIndex];
		context.actionMenuLocation = context.arrayDetailMenuLocation[folderIndex];
		
		context.managementFormName = context.arrayManagementFormName[folderIndex];
		context.managementFormLocation = context.arrayManagementFormLocation[folderIndex];
		context.managementFormScreenName = context.arrayManagementFormScreenName[folderIndex];
		context.managementFormScreenLocation = context.arrayManagementFormScreenLocation[folderIndex];
		
		
	}
	
	//    Debug.log("***************************************** context.managementFormType = " + context.managementFormType);
	//Debug.log("***************************************** context.insertMode = " + context.insertMode);
	//Debug.log("***************************************** parameters.fromDelete = " + parameters.fromDelete);
	
	if("Y".equals(context.insertMode)) {
	 // Debug.log("********************************** setPropertyParameters.grrovy putParameterInContext per insertMode");
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/putParameterInContext.groovy", context);
	}
	else if("list".equals(context.managementFormType) || "multi".equals(context.managementFormType) || "Y".equals(parameters.fromDelete)) {
		context.actionMenuName = context.menuBarNameList[folderIndex]
		context.actionMenuLocation = context.menuBarLocationList[folderIndex]
	}
	context.managementFormScreenName = context.arrayManagementFormScreenName[folderIndex];
	context.managementFormScreenLocation = context.arrayManagementFormScreenLocation[folderIndex];
	
	context.contextManagementScreenName = context.contextManagementScreenNameList[context.folderIndex];
	context.contextManagementScreenLocation = context.contextManagementScreenLocationList[context.folderIndex]
	context.contextManagementFormName = context.contextManagementFormNameList[context.folderIndex]
	context.contextManagementFormLocation = context.contextManagementFormLocationList[context.folderIndex]
	if("Y".equals(context.detail)) {
		context.childManagementScreenName = context.arrayDetailScreenName[folderIndex];
		context.childManagementScreenLocation = context.arrayDetailScreenLocation[folderIndex];
	}
	
	//	Debug.log("********************************* context.actionMenuName: "+ context.actionMenuName)
	//	Debug.log("********************************* context.actionMenuLocation: "+ context.actionMenuLocation)
	
	//	Debug.log("********************************* context.managementFormScreenName: "+ context.managementFormScreenName)
	//	Debug.log("********************************* context.managementFormScreenLocation: "+ context.managementFormScreenLocation)
	
	//	Debug.log("********************************* context.contextManagementScreenName: "+context.contextManagementScreenName)
	//	Debug.log("********************************* context.contextManagementScreenLocation: "+context.contextManagementScreenLocation)
	//	Debug.log("********************************* context.contextManagementFormName: "+context.contextManagementFormName)
	//	Debug.log("********************************* context.contextManagementFormLocation: "+context.contextManagementFormLocation)
	//	Debug.log("********************************* context.childManagementScreenName: "+context.childManagementScreenName)
	//	Debug.log("********************************* context.childManagementScreenLocation: "+context.childManagementScreenLocation)
}
else {
 // Debug.log("********************************* E il main, 2 groovy scambiati");
 // Debug.log("********************************** setPropertyParameters.grrovy entityOne, putInputFieldsInContext, setManagementScreenList");
		
	//context.parent_value_just_populated = parameters.parent_value_just_populated;
	
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/entityOne.groovy", context);
	if(UtilValidate.isEmpty(context.parentEntityName)){
		context.parentEntityName = parameters.parentEntityName;
	}
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/putInputFieldsInContext.groovy", context);
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/setManagementScreenList.groovy", context);
	
	
	//Aggiunto ordinamento per contextLink folder principale!	
	context.sortField = UtilValidate.isNotEmpty(context.arraySortField) && context.arraySortField.size() > 0 ? context.arraySortField[0] : null;
	
	context.customExecutionChildPerformFindScript = context.arrayCustomExecutionChildPerformFindScript[0];
	if (UtilValidate.isEmpty(context.customExecutionChildPerformFindScript)) {
	 // Debug.log("********************************** setPropertyParameters.grrovy lancia executeChildPerformFind.groovy");
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
	}
	else {
	 // Debug.log("********************************** setPropertyParameters.grrovy lancia " + context.customExecutionChildPerformFindScript);
		GroovyUtil.runScriptAtLocation(context.customExecutionChildPerformFindScript, context);
	}
	
	if("Y".equals(context.insertMode) || "Y".equals(parameters.insertMode)) {
	 // Debug.log("********************************* setPropertyParameters.grrovy lancia putParameterInContext 2 groovy scambiati")
		GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/putParameterInContext.groovy", context);
	}
	
//	Debug.log("********************************* context.arrayManagementFormType[0]: "+context.arrayManagementFormType[0])
	if ("Y".equals(contextManagement)) {
		if ("Y".equals(parameters.detail)) {
			context.managementFormName = context.arrayManagementFormName[0];
			context.managementFormLocation = context.arrayManagementFormLocation[0];
		}
		else if (UtilValidate.isNotEmpty(context.arrayChildManagementFormListName) && UtilValidate.isNotEmpty(context.arrayChildManagementFormListLocation)) {
			context.childManagementFormListName = context.arrayChildManagementFormListName[0]
			context.childManagementFormListLocation = context.arrayChildManagementFormListLocation[0]
		}
	} else {
		if (UtilValidate.isNotEmpty(context.arrayManagementFormName) && UtilValidate.isNotEmpty(context.arrayManagementFormLocation)) {
			if (!"list".equals(context.arrayManagementFormType[0]) && !"multi".equals(context.arrayManagementFormType[0])) {
				context.managementFormName = context.arrayManagementFormName[0]
				context.managementFormLocation = context.arrayManagementFormLocation[0]
			} else  {
				context.childManagementFormListName = context.arrayManagementFormName[0]
				context.childManagementFormListLocation = context.arrayManagementFormLocation[0]
			}
		}
	}
	//
	//
	//	Debug.log("--------------------------------------------- context.arrayDetailScreenName: "+context.arrayDetailScreenName);
	
	//	Debug.log("********************************* parameters.detail: "+parameters.detail);
	if ("Y".equals(parameters.detail)) {
		context.actionMenuName = context.arrayDetailMenuName[0];
		context.actionMenuLocation = context.arrayDetailMenuLocation[0];
		context.childManagementScreenName = context.arrayDetailScreenName[0]
		context.childManagementScreenLocation = context.arrayDetailScreenLocation[0]
		context.childManagementFormName = context.arrayDetailFormName[0]
		context.childManagementFormLocation = context.arrayDetailFormLocation[0]
		context.contextManagementScreenName = context.arrayDetailContextScreenName[0]
		context.contextManagementScreenLocation = context.arrayDetailContextScreenLocation[0]
		context.contextManagementFormName = context.arrayDetailContextFormName[0]
		context.contextManagementFormLocation = context.arrayDetailContextFormLocation[0]
	} else {
		if ("Y".equals(contextManagement)) {
			context.childManagementFormListScreenName = context.arrayChildManagementFormListScreenName[0]
			context.childManagementFormListScreenLocation = context.arrayChildManagementFormListScreenLocation[0]
		} else {	
			context.childManagementScreenName = context.arrayChildManagementScreenName[0]
			context.childManagementScreenLocation = context.arrayChildManagementScreenLocation[0]
		}
		context.contextManagementScreenName = context.contextManagementScreenNameList[0];
		context.contextManagementScreenLocation = context.contextManagementScreenLocationList[0]
		context.contextManagementFormName = context.contextManagementFormNameList[0]
		context.contextManagementFormLocation = context.contextManagementFormLocationList[0]
	}
	
	//	Debug.log("********************************* context.actionMenuName: "+context.actionMenuName);
}

