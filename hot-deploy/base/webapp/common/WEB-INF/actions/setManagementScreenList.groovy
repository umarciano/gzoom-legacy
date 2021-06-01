import org.ofbiz.base.location.FlexibleLocation
import org.ofbiz.base.util.*

def getPropertyCommon(propertyName, scriptUrl, componentName, type, parentEntityName, entityName, relationTitle, screenNameListIndex, isMain, onlyWithSpecification, inheritScreenList, defaultValue) {
	res = "";
	
	if (!isMain) {
		if(UtilValidate.isNotEmpty(relationTitle)) {
			res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + screenNameListIndex + "." + propertyName + "." + entityName + "-" + relationTitle);
			if (!res) {
				if (inheritScreenList)
					res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + propertyName + "." + entityName+ "-" + relationTitle);
				else
					res = "";
			}
		}
		if(UtilValidate.isEmpty(res)) {
			res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + screenNameListIndex + "." + propertyName + "." + entityName);
			if (!res) {
				if (inheritScreenList)
					res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + propertyName + "." + entityName);
				else
					res = "";
			}
		}
	} else {
		res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + screenNameListIndex + "." + propertyName + ".main");
		if (UtilValidate.isEmpty(res)) {
			if (inheritScreenList)
				res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + propertyName + ".main");
			else
				res = "";
		}
	}
	if(UtilValidate.isEmpty(res)) {
		if (!onlyWithSpecification) {
			res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + screenNameListIndex + "." + propertyName);
			if (!res) {
				if (inheritScreenList) {
					res = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + parentEntityName + type + propertyName, defaultValue);
				} else
					res = defaultValue;
			}
		} else {
			res = defaultValue
		}
	}
	
	return res;
}

def getProperty(propertyName, scriptUrl, componentName, parentEntityName, entityName, relationTitle, contentIdValue, screenNameListIndex, isMain, onlyWithSpecification, inheritScreenList, defaultValue) {
	res = getPropertyCommon(propertyName, scriptUrl, componentName, ".management.", parentEntityName, entityName, relationTitle, screenNameListIndex, isMain, onlyWithSpecification, inheritScreenList, defaultValue);
	
	defaultValue = res;
	if(UtilValidate.isNotEmpty(contentIdValue)) {
		res = getPropertyCommon(propertyName, scriptUrl, componentName, ".layout.", parentEntityName, entityName, relationTitle, contentIdValue + "." + screenNameListIndex, isMain, onlyWithSpecification, inheritScreenList, defaultValue)
	}
	return res;
}

// Dal file di properties e' possibile impostare una form di contextManagement per un Folder,
// che verra' inserita in uno screen di default
// oppure uno screen di contextManagement per un Folder,
// che inserira' una propria form, quindi i parametri contextManagementFormNameList e contextManagementFormLocationList non vengono utilizzati

childManagement = context.childManagement;
if(UtilValidate.isEmpty(childManagement)) {
    childManagement = parameters.childManagement;
}
if(UtilValidate.isEmpty(childManagement)) {
    childManagement = "N";
}

contextManagement = context.contextManagement;
if(UtilValidate.isEmpty(contextManagement)) {
	contextManagement = parameters.contextManagement;
}
if(UtilValidate.isEmpty(contextManagement)) {
	contextManagement = "N";
}

parentEntityName = parameters.parentEntityName;
if (UtilValidate.isEmpty(parentEntityName)) {
	parentEntityName = context.parentEntityName;
}

if("N".equals(childManagement) && "N".equals(contextManagement) && UtilValidate.isNotEmpty(context.parentEntityName)) {
    entityName = context.parentEntityName;
}
else
    entityName = context.entityName;

Debug.log("********************************** setManagementScreenList.grrovy --> entityName=" + entityName);

if (UtilValidate.isNotEmpty(entityName)) {
    screensNameList = [UtilValidate.isEmpty(context.managementScreenName) ? "ManagementFormScreen" : context.managementScreenName];
    screensLocationList = [UtilValidate.isEmpty(context.managementScreenLocation) ? "component://base/widget/ManagementScreens.xml" : context.managementScreenLocation];
    contextMenuNameList = [""];
    contextMenuLocationList = [""];
	menuBarScreenNameList =[""];
	menuBarScreenLocationList =[""];
    menuBarNameList = [""];
    menuBarLocationList = [""];
    contextManagementScreenNameList = [""];
    contextManagementScreenLocationList = [""];
    contextManagementFormNameList = [""];
    contextManagementFormLocationList = [""];
    headerScreenName = "";
    headerScreenLocation = null;
    screenNameListIndex = "";
    inheritScreenList = true;
    defaultMenuBarLocation = UtilValidate.isNotEmpty(context.actionMenuLocation) ? context.actionMenuLocation : "component://base/widget/BaseMenus.xml";
	
    contentIdValue = "";
    
    /* se e' presente un "entityName" per un subFolder viene creato un array con gli entityName dei subFolder */
    /* se non e' presente uno screen ...FolderScreen alla lista di screenName e' aggiunto un ManagementDetailCommonScreen e i parametri vengono letti dal file di properties*/
    arrayEntityName = [entityName];
	arrayParentManagementFormName = [""];
    arrayParentManagementFormLocation = [""];
    arrayParentManagementFormScreenName = [""];
    arrayParentManagementFormScreenLocation = [""];
    arrayChildManagementScreenName = [];
	arrayChildManagementScreenLocation = [];
    arrayChildManagementFormListName = [""];
    arrayChildManagementFormListLocation = [""];
    arrayChildManagementFormListScreenName = [""];
	arrayChildManagementFormListScreenLocation = [""];
	arrayChildManagementDetailScreenName = [""];
	arrayChildManagementDetailScreenLocation = [""];
	arrayDetailScreenName = []
	arrayDetailScreenLocation = []
	arrayDetailFormName = []
	arrayDetailFormLocation = []
	arrayDetailContextScreenName = []
	arrayDetailContextScreenLocation = []
	arrayDetailContextFormName = []
	arrayDetailContextFormLocation = []
	arrayDetailMenuName = [""]
	arrayDetailMenuLocation = [""]
    arrayParentEntityName = [""];
    arrayManagementFormType = [];
    arrayRelationTitle = [""];
    arraySortField = [""];
	arrayCustomExecutionChildPerformFindScript = [""];
    arrayEntityNameSize = 1;
	arrayManagementFormName = [];
	arrayManagementFormLocation = [];
    arrayManagementFormScreenName = [];
    arrayManagementFormScreenLocation = [];
    arrayNoParentEntityName = ["N".equals(contextManagement) ? "Y" : "N"];
    managementTabMenuName = UtilValidate.isNotEmpty(parameters.managementTabMenuName) ? parameters.managementTabMenuName : (UtilValidate.isNotEmpty(context.managementTabMenuName) ? context.managementTabMenuName : null);
    managementTabMenuLocation = UtilValidate.isNotEmpty(parameters.managementTabMenuLocation) ? parameters.managementTabMenuLocation : (UtilValidate.isNotEmpty(context.managementTabMenuLocation) ? context.managementTabMenuLocation : null);
    if (UtilValidate.isEmpty(context.managementScreenName)) {
        if (UtilValidate.isNotEmpty(parameters.formLocationProperties)) {
            baseLocation = new StringBuffer(FlexibleLocation.stripLocationType(parameters.formLocationProperties));

            // componentName is between the first slash and the second
            componentName = "";
            firstSlash = baseLocation.indexOf("/");
            secondSlash = baseLocation.indexOf("/", firstSlash + 1);
            if (firstSlash == 0 || secondSlash != -1) {
                componentName = baseLocation.substring(firstSlash + 1, secondSlash);
            }

            scriptUrl = FlexibleLocation.resolveLocation(parameters.formLocationProperties);
			
	        /** checkConditionScript for management */
            conditionScriptLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.checkConditionScript");

//	        Debug.log("********************************** setManagementScreenList.grrovy --> conditionScriptLocation=" + conditionScriptLocation);
			if (UtilValidate.isNotEmpty(conditionScriptLocation)) {
	        	screenNameList = GroovyUtil.runScriptAtLocation(conditionScriptLocation, context);
	        	if (UtilValidate.isString(screenNameList)) {
	        		screenNameListIndex = screenNameList; 
	        	}
	        }
			if (screenNameListIndex) {
            	inherit = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".inheritScreenList", "Y");
                inheritScreenList = "Y".equals(inherit);
            }

			Debug.log("********************************** setManagementScreenList.grrovy --> screenNameListIndex=" + screenNameListIndex);
            
			
			/** populate arrayEntityName and folderContentIds */
            def arrayContextValueCheckEntityNameDisabledList = getProperty("contextValueCheckEntityNameDisabled", scriptUrl, componentName, entityName, entityName, null, null, screenNameListIndex, true, false, inheritScreenList, "");
			def excludedContentScript = getProperty("excludedContentScript", scriptUrl, componentName, entityName, entityName, null, null, screenNameListIndex, true, false, inheritScreenList, "component://base/webapp/common/WEB-INF/actions/getExcludedContent.groovy");
			def rootFolder = getProperty("rootFolder", scriptUrl, componentName, entityName, entityName, null, null, screenNameListIndex, true, false, inheritScreenList, "component://base/webapp/common/WEB-INF/actions/getExcludedContent.groovy");
			
			/** myLocalEntityName */
			myLocalEntityName = context.entityName;
			
			context.scriptUrl = scriptUrl;
			context.componentName = componentName;
			context.entityName = entityName;
			context.screenNameListIndex = screenNameListIndex;
			context.inheritScreenList = inheritScreenList;
			context.arrayEntityName = arrayEntityName;
			
			context.arrayContextValueCheckEntityNameDisabledList = arrayContextValueCheckEntityNameDisabledList;
			context.excludedContentScript = excludedContentScript;
			context.rootFolder = rootFolder;
			
			
			// estrazione ricerca folderContentIds, usosempre lo steso context, perche per esempio il groovy delle analisi cerca dei valori particolari nel context
			GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getFolderContentIds.groovy", context);
			
			arrayEntityName = context.arrayEntityName;
			arrayEntityNameSize = context.arrayEntityName.size();
			/** myLocalEntityName */
			context.entityName = myLocalEntityName;
			
			/** checkConditionScript for layout */
			overrideScript = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".layout.checkConditionScript");
			
            if(UtilValidate.isNotEmpty(overrideScript)) {
				context.i = 0;
            	contentIdValue = GroovyUtil.runScriptAtLocation(overrideScript, context);
            	if(UtilValidate.isNotEmpty(contentIdValue)) {
            		Debug.log("********************************** setManagementScreenList.grrovy --> contentIdValue = " + contentIdValue);
            	}
			}
            
			managementBaseInnerScreenName = parameters.managementBaseInnerScreenName;
			if (UtilValidate.isEmpty(managementBaseInnerScreenName)) {
				managementBaseInnerScreenName = context.managementBaseInnerScreenName;
				if (UtilValidate.isEmpty(managementBaseInnerScreenName)) {
					managementBaseInnerScreenName = getProperty("managementBaseInnerScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
				}
			}
			context.managementBaseInnerScreenName = managementBaseInnerScreenName;
			
			managementBaseInnerScreenLocation = parameters.managementBaseInnerScreenLocation;
			if (UtilValidate.isEmpty(managementBaseInnerScreenLocation)) {
				managementBaseInnerScreenLocation = context.managementBaseInnerScreenLocation;
				if (UtilValidate.isEmpty(managementBaseInnerScreenLocation)) {
					managementBaseInnerScreenLocation = getProperty("managementBaseInnerScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
				}
			}
			context.managementBaseInnerScreenLocation = managementBaseInnerScreenLocation;
            
			mainScreenName = getProperty("screenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "ManagementFormScreen")
            screensNameList = [mainScreenName];
			
			mainScreenLocation = getProperty("screenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "component://base/widget/ManagementScreens.xml")
            screensLocationList = [mainScreenLocation];
			
			managementFormType = "";
			if (!("Y".equals(parameters.detail) && !"Y".equals(parameters.fromDelete) && (UtilValidate.isEmpty(parameters.folderIndex) || parameters.folderIndex == 0))) {
				managementFormType = getProperty("managementFormType", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, UtilValidate.isNotEmpty(parameters.managementFormType) ? parameters.managementFormType : "")
			}
			arrayManagementFormType.add(managementFormType);
			
			managementFormName = parameters.managementFormName;
			if (UtilValidate.isEmpty(managementFormName)) {
				managementFormName = context.managementFormName;
				if (UtilValidate.isEmpty(managementFormName)) {
					managementFormName = getProperty("managementFormName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, ("Y".equals(contextManagement) ? parameters.parentEntityName : entityName)+"Management" + ("Y".equals(contextManagement) ? "Parent" : "") + ("Y".equals(contextManagement) ? "" : (UtilValidate.isNotEmpty(managementFormType) ? org.apache.commons.lang.StringUtils.capitalize(managementFormType) : "")) + "Form")
				}
			}
			arrayManagementFormName.add(managementFormName);
			
			customExecutionChildPerformFindScript = getProperty("customExecutionChildPerformFindScript", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayCustomExecutionChildPerformFindScript = [customExecutionChildPerformFindScript];
			
			// perche' managementFormScreenName include ManagementFormScreen che usa managmentFormName oopure BaseManagementForm
			// ma managementFormName e' anche la form del parent 
			// quindi arrayChildManagementFormListScreenName DEVE essere diverso da [managementFormScreenName];
			childManagementFormListName = "";
			if("Y".equals(contextManagement)) {
			    childManagementFormListName = parameters.childManagementFormListName;
				if (UtilValidate.isEmpty(childManagementFormListName)) {
				    childManagementFormListName = context.childManagementFormListName;
					if (UtilValidate.isEmpty(childManagementFormListName)) {
						childManagementFormListName = getProperty("childManagementFormListName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, entityName +"Management" + (UtilValidate.isNotEmpty(managementFormType) ? org.apache.commons.lang.StringUtils.capitalize(managementFormType) : "") + "Form")
					}
				}
				arrayChildManagementFormListName = [childManagementFormListName];
			}
			
			def defaultManagementFormLocation = parameters.managementFormLocation;
			if (UtilValidate.isEmpty(defaultManagementFormLocation)) {
				defaultManagementFormLocation = context.managementFormLocation;
				if (UtilValidate.isEmpty(defaultManagementFormLocation)) {
					defaultManagementFormLocation = "component://base/widget/ManagementForms.xml";
				}
			}
			managementFormLocation = getProperty("managementFormLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, defaultManagementFormLocation)
			arrayManagementFormLocation.add(managementFormLocation);
			
			if("Y".equals(contextManagement)) {
				def defaultChildManagementFormListLocation = parameters.childManagementFormListLocation;
				if (UtilValidate.isEmpty(defaultChildManagementFormListLocation)) {
					defaultChildManagementFormListLocation = context.childManagementFormListLocation;
					if (UtilValidate.isEmpty(defaultChildManagementFormListLocation)) {
						defaultManagementFormLocation = "";
					}
				}
				childManagementFormListLocation = getProperty("childManagementFormListLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, defaultManagementFormLocation)
				arrayChildManagementFormListLocation = [childManagementFormListLocation];
			}
			
			def defaultManagementFormScreenName = parameters.managementFormScreenName;
			if (UtilValidate.isEmpty(defaultManagementFormScreenName)) {
				defaultManagementFormScreenName = context.managementFormScreenName;
				if (UtilValidate.isEmpty(defaultManagementFormScreenName)) {
					defaultManagementFormScreenName = "ManagementFormScreen";
				}
			}
			managementFormScreenName = getProperty("managementFormScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, defaultManagementFormScreenName)
			arrayManagementFormScreenName.add(managementFormScreenName);
			
			childManagementFormListScreenName = "";
			if ("Y".equals(parameters.contextManagement)) {
				def defaultChildManagementFormListScreenName = parameters.childManagementFormListScreenName;
				if (UtilValidate.isEmpty(defaultChildManagementFormListScreenName)) {
					defaultChildManagementFormListScreenName = context.childManagementFormListScreenName;
					if (UtilValidate.isEmpty(defaultChildManagementFormListScreenName)) {
						defaultChildManagementFormListScreenName = "ManagementListFormScreen";
					}
				}
				childManagementFormListScreenName = getProperty("childManagementFormListScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "ManagementListFormScreen")
				arrayChildManagementFormListScreenName = [childManagementFormListScreenName];
			}
			
			def defaultManagementFormScreenLocation = parameters.managementFormScreenLocation;
			if (UtilValidate.isEmpty(defaultManagementFormScreenLocation)) {
				defaultManagementFormScreenLocation = context.managementFormScreenLocation;
				if (UtilValidate.isEmpty(defaultManagementFormScreenLocation)) {
					defaultManagementFormScreenLocation = "component://base/widget/ManagementScreens.xml";
				}
			}
			managementFormScreenLocation = getProperty("managementFormScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "component://base/widget/ManagementScreens.xml")
			arrayManagementFormScreenLocation.add(managementFormScreenLocation);
			
			childManagementFormListScreenLocation = "";
			if ("Y".equals(parameters.contextManagement)) {
				def defaultChildManagementFormListScreenLocation = parameters.childManagementFormListScreenLocation;
				if (UtilValidate.isEmpty(defaultChildManagementFormListScreenLocation)) {
					defaultChildManagementFormListScreenLocation = context.childManagementFormListScreenLocation;
					if (UtilValidate.isEmpty(defaultChildManagementFormListScreenLocation)) {
						defaultChildManagementFormListScreenLocation = "component://base/widget/ManagementScreens.xml";
					}
				}
				childManagementFormListScreenLocation = getProperty("childManagementFormListScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "component://base/widget/ManagementScreens.xml")
				arrayChildManagementFormListScreenLocation = [childManagementFormListScreenLocation];
			}
			
			// sortField
			sortField = getProperty("sortField", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
        	arraySortField = [sortField];
            
			mainContextMenuName = getProperty("contextMenuName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
            contextMenuNameList = [mainContextMenuName];
			
			mainContextMenuLocation = getProperty("contextMenuLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
            contextMenuLocationList = [mainContextMenuLocation];
			
			mainMenuBarScreenName = getProperty("menuBarScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "ActionMenu")
			menuBarScreenNameList = [mainMenuBarScreenName];
			
			mainMenuBarScreenLocation = getProperty("menuBarScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "component://base/widget/MenuScreens.xml")
			menuBarScreenLocationList = [mainMenuBarScreenLocation];
			
			mainMenuBarName = getProperty("menuBarName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "ManagementMenuBar")
            menuBarNameList = [mainMenuBarName];
			
			mainMenuBarLocation = getProperty("menuBarLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "component://base/widget/BaseMenus.xml")
            menuBarLocationList = [mainMenuBarLocation];

			mainContextManagementScreenName = getProperty("contextManagementScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "ContextManagementFormScreen")
            contextManagementScreenNameList = [mainContextManagementScreenName];
			
			mainContextManagementScreenLocation = getProperty("contextManagementScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "component://base/widget/ManagementScreens.xml")
            contextManagementScreenLocationList = [mainContextManagementScreenLocation];

			mainContextManagementFormName = getProperty("contextManagementFormName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "BaseContextManagementForm")
            contextManagementFormNameList = [mainContextManagementFormName];
			
			mainContextManagementFormLocation = getProperty("contextManagementFormLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "component://base/widget/BaseForms.xml")
            contextManagementFormLocationList = [mainContextManagementFormLocation];

			managementTabMenuName = getProperty("tabMenuName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, managementTabMenuName)
            if (UtilValidate.isNotEmpty(managementTabMenuName)) {
				managementTabMenuLocation = getProperty("tabMenuLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, managementTabMenuLocation)
            }
            else {
                managementTabMenuLocation = "";
            }

			headerScreenName = getProperty("headerScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
            if (headerScreenName) {
				headerScreenLocation = getProperty("headerScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
            }
            
            if ("Y".equals(parameters.contextManagement) && UtilValidate.isNotEmpty(parentEntityName)) {
            	arrayParentEntityName = [parentEntityName]
            	
            	// parentManagementFormName
				parentManagementFormName = getProperty("parentManagementFormName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, UtilValidate.isNotEmpty(arrayParentEntityName[0]) ? arrayParentEntityName[0]+"ManagementParentForm" : "BaseManagementForm")
				arrayParentManagementFormName = [parentManagementFormName];
				
                // parentManagementFormLocation
				parentManagementFormLocation = getProperty("parentManagementFormLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
                arrayParentManagementFormLocation =[parentManagementFormLocation];

                // parentManagementFormScreenName
				parentManagementFormScreenName = getProperty("parentManagementFormScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "ParentManagementFormScreen")
				arrayParentManagementFormScreenName = [parentManagementFormScreenName];
				
                // parentManagementFormLocation
				parentManagementFormScreenLocation = getProperty("parentManagementFormScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "component://base/widget/ManagementScreens.xml")
                arrayParentManagementFormScreenLocation =[parentManagementFormScreenLocation];
            
            }
            
//          detailMenuName
			detailMenuName = getProperty("detailMenuName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
			arrayDetailMenuName = [detailMenuName];
			
			// detailMenuLocation
			detailMenuLocation = getProperty("detailMenuLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, false, inheritScreenList, "")
			arrayDetailMenuLocation = [detailMenuLocation];
			
            // detailScreenName
			detailScreenName = getProperty("detailScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailScreenName.add(detailScreenName);
			
			// detailScreenLocation
			detailScreenLocation = getProperty("detailScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailScreenLocation.add(detailScreenLocation);
			
			// detailFormName
			detailFormName = getProperty("detailFormName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailFormName.add(detailFormName);
			
			// detailScreenLocation
			detailFormLocation = getProperty("detailFormLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailFormLocation.add(detailFormLocation);
			
			// detailContextScreenName
			detailContextScreenName = getProperty("detailContextScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailContextScreenName.add(detailContextScreenName);
			
			// detailScreenLocation
			detailContextScreenLocation = getProperty("detailContextScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailContextScreenLocation.add(detailContextScreenLocation);
			
			// detailContextFormName
			detailContextFormName = getProperty("detailContextFormName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailContextFormName.add(detailContextFormName);
			
			// detailContextFormLocation
			detailContextFormLocation = getProperty("detailContextFormLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayDetailContextFormLocation.add(detailContextFormLocation);
			
			// childManagementScreenName
			childManagementScreenName = getProperty("childManagementScreenName", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayChildManagementScreenName.add(childManagementScreenName);
			
			// childManagementScreenLocation
			childManagementScreenLocation = getProperty("childManagementScreenLocation", scriptUrl, componentName, entityName, entityName, null, contentIdValue, screenNameListIndex, true, true, inheritScreenList, "")
			arrayChildManagementScreenLocation.add(childManagementScreenLocation);
			
			/** GESTIONE SUBFOLDER */ 
			if(UtilValidate.isNotEmpty(managementTabMenuName) && UtilValidate.isNotEmpty(managementTabMenuLocation)) {
				
				if(UtilValidate.isNotEmpty(arrayEntityName) && arrayEntityName.size() > 1) {
					//    per ogni entityName aggiungo:
					//    - un parentEntityName all' arrayParentEntityName
					//    - un managementFormType all' arrayManagementFormType
					//    - un relationTitle all' arrayRelationTitle
					//    - ecc...
					for (i in 1..arrayEntityNameSize - 1) {
						contentIdValue = "";
						
						relationTitle = "";
						array = arrayEntityName[i].split("-");
						if(array.size() > 1) {
							relationTitle = array[1];
							arrayEntityName[i] = array[0];
						}
						arrayRelationTitle.add(relationTitle);
						
						if(UtilValidate.isNotEmpty(overrideScript)) {
							context.i = i;
							contentIdValue = GroovyUtil.runScriptAtLocation(overrideScript, context);
							if(UtilValidate.isNotEmpty(contentIdValue)) {
			            		Debug.log("********************************** setManagementScreenList.grrovy --> contentIdValue = " + contentIdValue);
			            	}
						}
						
						
						//noParentEntityName
						noParentEntityName = getProperty("noParentEntityName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "N")
						arrayNoParentEntityName.add(noParentEntityName);
                        arrayParentEntityName.add(entityName);
                    	    
                        
//                      sortField
						sortField = getProperty("sortField", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "")
                    	arraySortField.add(sortField);
						
                        // managementFormType
						managementFormType = "";
						if (!("Y".equals(parameters.detail) && !"Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.folderIndex) && i == Integer.parseInt(parameters.folderIndex))) {
							managementFormType = getProperty("managementFormType", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "");
						}
                    	arrayManagementFormType.add(managementFormType);
						
//                    	 customExecutionChildPerformFindScript
						customExecutionChildPerformFindScript = getProperty("customExecutionChildPerformFindScript", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "")
						arrayCustomExecutionChildPerformFindScript.add(customExecutionChildPerformFindScript);
						
                        // parentManagementFormName
						parentManagementFormName = getProperty("parentManagementFormName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, UtilValidate.isNotEmpty(arrayParentEntityName[i]) ? arrayParentEntityName[i]+"ManagementParentForm" : "ParentManagementFormScreen")
						arrayParentManagementFormName.add(parentManagementFormName);
						
						// parentManagementFormLocation
						parentManagementFormLocation = getProperty("parentManagementFormLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "")
                        arrayParentManagementFormLocation.add(parentManagementFormLocation);
                        
						// parentManagementFormScreenName
						parentManagementFormScreenName = getProperty("parentManagementFormScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ParentManagementFormScreen")
						arrayParentManagementFormScreenName.add(parentManagementFormScreenName);
						
						// parentManagementFormLocation
						parentManagementFormScreenLocation = getProperty("parentManagementFormScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/ManagementScreens.xml")
						arrayParentManagementFormScreenLocation.add(parentManagementFormScreenLocation);
						
                        // childManagementFormListName
						childManagementFormListName = getProperty("childManagementFormListName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, arrayEntityName[i] + "Management"  + org.apache.commons.lang.StringUtils.capitalize(arrayManagementFormType[i]) + "Form")
						arrayChildManagementFormListName.add(childManagementFormListName);
                        
                        
                        // childManagementFormListLocation
						childManagementFormListLocation = getProperty("childManagementFormListLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "")
                        arrayChildManagementFormListLocation.add(childManagementFormListLocation);
						
						// childManagementFormListScreenName
						childManagementFormListScreenName = getProperty("childManagementFormListScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ManagementListFormScreen")
						arrayChildManagementFormListScreenName.add(childManagementFormListScreenName);
						
						
						// childManagementFormListScreenLocation
						childManagementFormListScreenLocation = getProperty("childManagementFormListScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/ManagementScreens.xml")
						arrayChildManagementFormListScreenLocation.add(childManagementFormListScreenLocation);
						
						// childManagementFormListScreenName
						childManagementDetailScreenName = getProperty("childManagementDetailScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ContextManagementBaseScreen")
						arrayChildManagementDetailScreenName.add(childManagementDetailScreenName);
						
						
						// childManagementDetailScreenLocation
						childManagementDetailScreenLocation = getProperty("childManagementDetailScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/BaseScreens.xml")
						arrayChildManagementDetailScreenLocation.add(childManagementDetailScreenLocation);
						
                        //ScreenName
						screensNamei = getProperty("screenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ManagementDetailCommonScreen")
                        screensNameList.add(screensNamei);
                        
//                      screensLocation 
						screensLocationi = getProperty("screenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/CommonScreens.xml")
                        screensLocationList.add(screensLocationi);
						
						// detailScreenName
						detailScreenName = getProperty("detailScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "")
						arrayDetailScreenName.add(detailScreenName);
						
						// detailScreenLocation
						detailScreenLocation = getProperty("detailScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "")
						arrayDetailScreenLocation.add(detailScreenLocation);
						
						// contextMenuName
						detailMenuName = getProperty("detailMenuName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "ManagementMenuDetailBar")
						arrayDetailMenuName.add(detailMenuName);
						
						// detailMenuLocation
						detailMenuLocation = getProperty("detailMenuLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "")
						arrayDetailMenuLocation.add(detailMenuLocation);
						
                        // managementFormName
						managementFormName = getProperty("managementFormName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, ("Y".equals(contextManagement) ? arrayParentEntityName[i] : arrayEntityName[i]) + "Management" + ("Y".equals(contextManagement) ? "Parent" : "") + ("Y".equals(contextManagement) ? "" : org.apache.commons.lang.StringUtils.capitalize(arrayManagementFormType[i])) + "Form")
                        arrayManagementFormName.add(managementFormName);
						
						// managementFormLocation
						managementFormLocation = getProperty("managementFormLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/ManagementForms.xml")
						arrayManagementFormLocation.add(managementFormLocation);
						//Debug.log("********************************** setManagementScreenList.grrovy --> arrayManagementFormLocation=" + arrayManagementFormLocation);
						
                        // managementFormScreenName
						managementFormScreenName = getProperty("managementFormScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ManagementFormScreen")
                        arrayManagementFormScreenName.add(managementFormScreenName);
                       
                        // managementFormScreenLocation
						managementFormScreenLocation = getProperty("managementFormScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/ManagementScreens.xml")
                        arrayManagementFormScreenLocation.add(managementFormScreenLocation);
                       
                        // menuBarName
						menuBarName = getProperty("menuBarName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ManagementMenuBar")
                        menuBarNameList.add(menuBarName);
                        
                        // menuBarLocation
						menuBarLocation = getProperty("menuBarLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, defaultMenuBarLocation)
                        menuBarLocationList.add(menuBarLocation);
						
						// menuBarScreenName
						menuBarScreenName = getProperty("menuBarScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ActionMenu")
						menuBarScreenNameList.add(menuBarScreenName);
						
						// menuBarScreenLocation
						menuBarScreenLocation = getProperty("menuBarScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/MenuScreens.xml")
						menuBarScreenLocationList.add(menuBarScreenLocation);
                        
                        // contextManagementScreenName
						contextManagementScreenName = getProperty("contextManagementScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "ContextManagement" + org.apache.commons.lang.StringUtils.capitalize(arrayManagementFormType[i]) + "FormScreen")
                        contextManagementScreenNameList.add(contextManagementScreenName);
                        
						if (!"ContextManagementFormScreen".equals(contextManagementScreenName)) {
							// contextManagementScreenLocation
							contextManagementScreenLocation = getProperty("contextManagementScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/ManagementScreens.xml")
						} else {
							contextManagementScreenLocation = "component://base/widget/ManagementScreens.xml";
						}
                        contextManagementScreenLocationList.add(contextManagementScreenLocation);
                        
                        // contextManagementFormName
						contextManagementFormName = getProperty("contextManagementFormName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "BaseContextManagementForm")
                        contextManagementFormNameList.add(contextManagementFormName);
                        
                        // contextManagementFormLocation
						contextManagementFormLocation = getProperty("contextManagementFormLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "component://base/widget/BaseForms.xml")
                        contextManagementFormLocationList.add(contextManagementFormLocation);
                        
                        // contextMenuName
						contextMenuName = getProperty("contextMenuName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "")
                        contextMenuNameList.add(contextMenuName);
                        
                        // contextMenuLocation
						contextMenuLocation = getProperty("contextMenuLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, false, inheritScreenList, "")
                        contextMenuLocationList.add(contextMenuLocation);
						
						// childManagementScreenName
						childManagementScreenName = getProperty("childManagementScreenName", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "")
						arrayChildManagementScreenName.add(childManagementScreenName);
						
						// childManagementScreenLocation
						childManagementScreenLocation = getProperty("childManagementScreenLocation", scriptUrl, componentName, entityName, arrayEntityName[i], relationTitle, contentIdValue, screenNameListIndex, false, true, inheritScreenList, "")
						arrayChildManagementScreenLocation.add(childManagementScreenLocation);
                    }    
                } else {
                    listSize = 0;
                    
                    screensName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".screenName");
                    if (!screensName) {
                    	if (inheritScreenList)
                    		screensName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.screenName");
                    }
                    //Debug.log("********************************** setManagementScreenList.grrovy --> screensName=" + screensName);
                    
                    if (UtilValidate.isNotEmpty(screensName)){
                        //Debug.log("********************************** setManagementScreenList.grrovy --> screensName=" + screensName);
                        screensNameList.addAll(StringUtil.toList(screensName));
                    
                        listSize = screensNameList.size();
                    }
                    
                    screensLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".screenLocation");
                    if (!screensLocation) {
                    	if (inheritScreenList)
                    		screensLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.screenLocation");
                    }
                    if (UtilValidate.isNotEmpty(screensLocation)){
                        //Debug.log("********************************** setManagementScreenList.grrovy --> screensLocation=" + screensLocation);
                        screensLocationList.addAll(StringUtil.toList(screensLocation));
                        if (screensLocationList.size() > listSize)
                            listSize = screensLocationList.size();
                    }
                    
                    if (screensNameList.size() < listSize) {
                        for (i in 0..listSize-1) {
                            screensNameList.add(UtilValidate.isNotEmpty(context.managementFormScreenName) ? context.managementFormScreenName : "ManagementFormScreen");
                        }
                    }
                    

                    if (screensLocationList.size() < listSize) {
                        for (i in 0..listSize-1) {
                            screensLocationList.add(UtilValidate.isNotEmpty(context.managementFormScreenLocation) ? context.managementFormScreenLocation : "component://base/widget/ManagementScreens.xml");
                        }
                    }
                    
                    for (i in 1..screensNameList.size()-1) {
                    	contextMenuName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".contextMenuName." + screensNameList[i]);
                    	if (!contextMenuName) {
                    		if (inheritScreenList)
                    			contextMenuName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.contextMenuName." + screensNameList[i], "");
                    		else
                    			contextMenuName = "";
                    	}
                        contextMenuNameList.add(contextMenuName);
                        
                        contextMenuLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".contextMenuLocation." + screensNameList[i]);
                        if (!contextMenuLocation) {
                        	if (inheritScreenList)
                        		contextMenuLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.contextMenuLocation." + screensNameList[i], "");
                        	else
                        		contextMenuLocation = "";
                        }
                        contextMenuLocationList.add(contextMenuLocation);
                        
                        menuBarName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".menuBarName." + screensNameList[i]);
                        if (!menuBarName) {
                        	if (inheritScreenList)
                        		menuBarName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.menuBarName." + screensNameList[i], "ManagementMenuBar");
                        	else
                        		menuBarName = "ManagementMenuBar";
                        }
                        menuBarNameList.add(menuBarName);
                        
                        menuBarLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".menuBarLocation." + screensNameList[i]);
                        if (!menuBarLocation) {
                        	if (inheritScreenList)
                        		menuBarLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.menuBarLocation." + screensNameList[i], defaultMenuBarLocation);
                        	else
                        		menuBarLocation = defaultMenuBarLocation;
                        }
                        menuBarLocationList.add(menuBarLocation);
						
						menuBarScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".menuBarScreenName." + screensNameList[i]);
						if (!menuBarScreenName) {
							if (inheritScreenList)
								menuBarScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.menuBarScreenName." + screensNameList[i], "ActionMenu");
							else
								menuBarScreenName = "ActionMenu";
						}
						menuBarScreenNameList.add(menuBarScreenName);
						
						menuBarScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".menuBarScreenLocation." + screensNameList[i]);
						if (!menuBarScreenLocation) {
							if (inheritScreenList)
								menuBarScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.menuBarScreenLocation." + screensNameList[i], "component://base/widget/MenuScreens.xml");
							else
								menuBarScreenLocation = "component://base/widget/MenuScreens.xml";
						}
						menuBarScreenLocationList.add(menuBarScreenLocation);
                        
                        contextManagementScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".contextManagementScreenName." + screensNameList[i]);
                        if (!contextManagementScreenName) {
                        	if (inheritScreenList)
                        		contextManagementScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.contextManagementScreenName." + screensNameList[i], "ContextManagementFormScreen");
                        	else
                        		contextManagementScreenName = "ContextManagementFormScreen";
                        }
                        
                        contextManagementScreenNameList.add(contextManagementScreenName);
                        contextManagementScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".contextManagementScreenLocation." + screensNameList[i]);
                        if (!contextManagementScreenLocation) {
                        	if (inheritScreenList)
                        		contextManagementScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.contextManagementScreenLocation." + screensNameList[i], "component://base/widget/ManagementScreens.xml");
                        	else
                        		contextManagementScreenLocation = "component://base/widget/ManagementScreens.xml";
                        }
                        
                        contextManagementScreenLocationList.add(contextManagementScreenLocation);
                        contextManagementFormName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".contextManagementFormName." + screensNameList[i]);
                        if (!contextManagementFormName) {
                        	if (inheritScreenList)
                        		contextManagementFormName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.contextManagementFormName." + screensNameList[i], "BaseContextManagementForm");
                        	else
                        		contextManagementFormName = "BaseContextManagementForm";
                        }
						contextManagementFormNameList.add(contextManagementFormName);
						
						managementFormScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".managementFormScreenName." + screensNameList[i]);;
						if (!managementFormScreenName) {
							if (inheritScreenList)
								managementFormScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.managementFormScreenName." + screensNameList[i], "ManagementFormScreen");
							else
								managementFormScreenName = "ManagementFormScreen";
						}
						arrayManagementFormScreenName.add(managementFormScreenName);						
						
						managementFormScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".managementFormScreenLocation." + screensNameList[i]);;
						if (!managementFormScreenLocation) {
							if (inheritScreenList)
								managementFormScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.managementFormScreenLocation." + screensNameList[i], "component://base/widget/ManagementScreens.xml");
							else
							managementFormScreenLocation = "component://base/widget/ManagementScreens.xml";
						}
						arrayManagementFormScreenLocation.add(managementFormScreenLocation);
                        
                        contextManagementFormLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".contextManagementFormLocation." + screensNameList[i]);
                        if (!contextManagementFormLocation)
                        	if (inheritScreenList)
                        		contextManagementFormLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.contextManagementFormLocation." + screensNameList[i], "component://base/widget/BaseForms.xml");
                        	else
                        		contextManagementFormLocation = "component://base/widget/BaseForms.xml";
                        contextManagementFormLocationList.add(contextManagementFormLocation);
						
						// parentManagementFormName
						parentManagementFormName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".parentManagementFormName." + screensNameList[i]);
						if (!parentManagementFormName)
							if (inheritScreenList)
								parentManagementFormName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.parentManagementFormName." + screensNameList[i], UtilValidate.isNotEmpty(entityName) ? entityName+"ManagementParentForm" : "ParentManagementFormScreen");
							else
								parentManagementFormName = UtilValidate.isNotEmpty(entityName) ? entityName+"ManagementParentForm" : "ParentManagementFormScreen";
						arrayParentManagementFormName.add(parentManagementFormName);
						
						// parentManagementFormLocation
						parentManagementFormLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".parentManagementFormLocation." + screensNameList[i]);
						if (!parentManagementFormLocation)
							if (inheritScreenList)
								parentManagementFormLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.parentManagementFormLocation." + screensNameList[i], "");
							else
								parentManagementFormLocation = "";
						arrayParentManagementFormLocation.add(parentManagementFormLocation);
						
						// parentManagementFormScreenName
						parentManagementFormScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".parentManagementFormScreenName." + screensNameList[i]);
						if (!parentManagementFormScreenName)
							if (inheritScreenList)
								parentManagementFormScreenName = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.parentManagementFormScreenName." + screensNameList[i], "ParentManagementFormScreen");
							else
								parentManagementFormScreenName = "ParentManagementFormScreen";
						arrayParentManagementFormScreenName.add(parentManagementFormScreenName);
						
						// parentManagementFormLocation
						parentManagementFormScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management." + screenNameListIndex + ".parentManagementFormScreenLocation." + screensNameList[i]);
						if (!parentManagementFormScreenLocation)
							if (inheritScreenList)
								parentManagementFormScreenLocation = UtilProperties.getPropertyValue(scriptUrl, componentName + "." + entityName + ".management.parentManagementFormScreenLocation." + screensNameList[i], "component://base/widget/ManagementScreens.xml");
							else
								parentManagementFormScreenLocation = "component://base/widget/ManagementScreens.xml";
						arrayParentManagementFormScreenLocation.add(parentManagementFormScreenLocation);
                    }
                    
                }
                context.managementScreenNameList = screensNameList;
                context.managementScreenLocationList = screensLocationList;
            }    
            else {
                context.managementScreenNameList = screensNameList;
                context.managementScreenLocationList = screensLocationList;
            }
               
        } else {
            context.managementScreenNameList = screensNameList;
            context.managementScreenLocationList = screensLocationList;
            // default values
            contextManagementScreenNameList = ["ContextManagementFormScreen"];
            contextManagementScreenLocationList = ["component://base/widget/ManagementScreens.xml"];
            contextManagementFormNameList = ["BaseContextManagementForm"];
            contextManagementFormLocationList = ["component://base/widget/BaseForms.xml"];
		
			managementFormName = parameters.managementFormName;
			if (UtilValidate.isEmpty(managementFormName)) {
				managementFormName = context.managementFormName;
				if (UtilValidate.isEmpty(managementFormName)) {
					managementFormName = ("Y".equals(contextManagement) ? parameters.parentEntityName : entityName)+"Management" + ("Y".equals(contextManagement) ? "Parent" : "") + ("Y".equals(contextManagement) ? "" : org.apache.commons.lang.StringUtils.capitalize(context.managementFormType)) + "Form";
				}
			}
			arrayManagementFormName.add(managementFormName);
			
			managementFormLocation = parameters.managementFormLocation;
			if (UtilValidate.isEmpty(managementFormLocation)) {
				managementFormLocation = context.managementFormLocation;
				if (UtilValidate.isEmpty(managementFormLocation)) {
					managementFormLocation = "component://base/widget/BaseForms.xml";
				}
			}
			arrayManagementFormLocation.add(managementFormLocation);
        }
    } else {
        context.managementScreenNameList = screensNameList;
        context.managementScreenLocationList = screensLocationList;

        if (UtilValidate.isNotEmpty(parameters.contextMenuName)) {
            contextMenuNameList = [parameters.contextMenuName];
        }
        if (UtilValidate.isNotEmpty(parameters.contextMenuLocation)) {
            contextMenuLocationList = [parameters.contextMenuLocation];
        }
	
		managementFormName = parameters.managementFormName;
		if (UtilValidate.isEmpty(managementFormName)) {
			managementFormName = context.managementFormName;
			if (UtilValidate.isEmpty(managementFormName)) {
				managementFormName = ("Y".equals(contextManagement) ? parameters.parentEntityName : entityName)+"Management" + ("Y".equals(contextManagement) ? "Parent" : "") + ("Y".equals(contextManagement) ? "" : org.apache.commons.lang.StringUtils.capitalize(context.managementFormType)) + "Form";
			}
		}
		arrayManagementFormName.add(managementFormName);

		managementFormLocation = parameters.managementFormLocation;
		if (UtilValidate.isEmpty(managementFormLocation)) {
			managementFormLocation = context.managementFormLocation;
			if (UtilValidate.isEmpty(managementFormLocation)) {
				managementFormLocation = "component://base/widget/BaseForms.xml";
			}
		}
		arrayManagementFormLocation.add(managementFormLocation);
    }

    context.contextMenuNameList = contextMenuNameList;
    context.contextMenuLocationList = contextMenuLocationList;
    // la lista di menu viene cmq messa nel contesto perche' altrimenti il setPropertyParameter da un'eccezione
    // nel caso di form di tipo multi o list
    if (UtilValidate.isNotEmpty(menuBarNameList)) {
        context.menuBarNameList = menuBarNameList;
        context.menuBarLocationList = menuBarLocationList;
        if (UtilValidate.isEmpty(context.actionMenuName)) {
            context.actionMenuName =  menuBarNameList[0];
        }
        if (UtilValidate.isEmpty(context.actionMenuLocation)) {
            context.actionMenuLocation =  menuBarLocationList[0];
        }
		
		context.menuBarScreenNameList = menuBarScreenNameList;
		context.menuBarScreenLocationList = menuBarScreenLocationList;
		if (UtilValidate.isEmpty(context.actionMenuScreenName)) {
			context.actionMenuScreenName =  menuBarScreenNameList[0];
		}
		if (UtilValidate.isEmpty(context.actionMenuScreenLocation)) {
			context.actionMenuScreenLocation =  menuBarScreenLocationList[0];
		}
    }
    context.contextManagementScreenNameList = contextManagementScreenNameList;
    context.contextManagementScreenLocationList = contextManagementScreenLocationList;
    context.contextManagementFormNameList = contextManagementFormNameList;
    context.contextManagementFormLocationList = contextManagementFormLocationList;
    context.managementTabMenuName = managementTabMenuName;
    context.managementTabMenuLocation = managementTabMenuLocation;
    context.headerScreenName = headerScreenName;
    context.headerScreenLocation = headerScreenLocation;
    context.arrayEntityName = arrayEntityName;
    context.arrayEntityNameSize = arrayEntityNameSize;
    context.arrayParentEntityName = arrayParentEntityName; 
    context.arrayNoParentEntityName = arrayNoParentEntityName;
    context.arrayRelationTitle = arrayRelationTitle;
    context.arrayParentManagementFormLocation = arrayParentManagementFormLocation; 
	context.arrayParentManagementFormName = arrayParentManagementFormName;
	context.arrayParentManagementFormScreenLocation = arrayParentManagementFormScreenLocation; 
	context.arrayParentManagementFormScreenName = arrayParentManagementFormScreenName;
    context.arrayManagementFormType = arrayManagementFormType;
    context.arrayManagementFormName = arrayManagementFormName; 
    context.arrayManagementFormLocation = arrayManagementFormLocation;
	context.arrayDetailScreenName = arrayDetailScreenName;
	context.arrayDetailScreenLocation = arrayDetailScreenLocation; 
	context.arrayDetailFormName = arrayDetailFormName;
	context.arrayDetailFormLocation = arrayDetailFormLocation; 
	context.arrayDetailContextScreenName = arrayDetailContextScreenName;
	context.arrayDetailContextScreenLocation = arrayDetailContextScreenLocation; 
	context.arrayDetailContextFormName = arrayDetailContextFormName;
	context.arrayDetailContextFormLocation = arrayDetailContextFormLocation; 
	context.arrayDetailMenuName = arrayDetailMenuName;
	context.arrayDetailMenuLocation = arrayDetailMenuLocation; 
    context.arrayChildManagementFormListName = arrayChildManagementFormListName;
    context.arrayChildManagementFormListLocation = arrayChildManagementFormListLocation; 
	context.arrayChildManagementFormListScreenName = arrayChildManagementFormListScreenName;
	context.arrayChildManagementFormListScreenLocation = arrayChildManagementFormListScreenLocation; 
	context.arrayChildManagementDetailScreenName = arrayChildManagementDetailScreenName;
	context.arrayChildManagementDetailScreenLocation = arrayChildManagementDetailScreenLocation;
    context.arrayManagementFormScreenName = arrayManagementFormScreenName; 
    context.arrayManagementFormScreenLocation = arrayManagementFormScreenLocation;
    context.arrayChildManagementScreenName = arrayChildManagementScreenName; 
    context.arrayChildManagementScreenLocation = arrayChildManagementScreenLocation;
    context.arraySortField = arraySortField;
	context.screenNameListIndex = screenNameListIndex;
	context.arrayCustomExecutionChildPerformFindScript = arrayCustomExecutionChildPerformFindScript;
    
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayParentManagementFormName=" + context.arrayParentManagementFormName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayParentManagementFormScreenName=" + context.arrayParentManagementFormScreenName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayParentManagementFormScreenLocation=" + context.arrayParentManagementFormScreenLocation);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayDetailMenuName=" + context.arrayDetailMenuName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayDetailMenuLocation =" + context.arrayDetailMenuLocation);
	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayCustomExecutionChildPerformFindScript=" + context.arrayCustomExecutionChildPerformFindScript);
	Debug.log("********************************** setManagementScreenList.grrovy --> context.menuBarNameList=" + context.menuBarNameList);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.menuBarLocationList =" + context.menuBarLocationList);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayManagementFormName=" + context.arrayManagementFormName);
//  Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayManagementFormLocation=" + context.arrayManagementFormLocation);
	
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayManagementFormScreenName=" + context.arrayManagementFormScreenName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayManagementFormScreenLocation=" + context.arrayManagementFormScreenLocation);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayParentManagementFormScreenName=" + context.arrayParentManagementFormScreenName);
	
	
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.managementFormScreenNameList =" + context.managementFormScreenNameList);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayManagementFormType=" + context.arrayManagementFormType);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayChildManagementFormListName=" + context.arrayChildManagementFormListName);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayChildManagementFormListLocation=" + context.arrayChildManagementFormListLocation);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayChildManagementFormListScreenName=" + context.arrayChildManagementFormListScreenName);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayChildManagementFormListScreenLocation=" + context.arrayChildManagementFormListScreenLocation);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayParentManagementFormLocation=" + context.arrayParentManagementFormLocation);
    Debug.log("********************************** setManagementScreenList.grrovy --> context.contextMenuNameList=" + context.contextMenuNameList);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.contextMenuLocationList=" + context.contextMenuLocationList);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.contextManagementScreenNameList=" + context.contextManagementScreenNameList);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.contextManagementScreenLocationList=" + context.contextManagementScreenLocationList);
    Debug.log("********************************** setManagementScreenList.grrovy --> context.contextManagementFormNameList=" + context.contextManagementFormNameList);
//  Debug.log("********************************** setManagementScreenList.grrovy --> context.contextManagementFormLocationList=" + context.contextManagementFormLocationList);
    Debug.log("********************************** setManagementScreenList.grrovy --> context.managementScreenNameList=" + context.managementScreenNameList);
//  Debug.log("********************************** setManagementScreenList.grrovy --> context.managementScreenLocationList=" + context.managementScreenLocationList);
//    Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayNoParentEntityName=" + context.arrayNoParentEntityName);
//	  Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayManagementFormScreenName=" + context.arrayManagementFormScreenName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayDetailContextFormName=" + context.arrayDetailContextFormName);
	
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayChildManagementScreenName=" + context.arrayChildManagementScreenName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayChildManagementScreenLocation=" + context.arrayChildManagementScreenLocation);
	
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayDetailScreenName=" + context.arrayDetailScreenName);
//	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayDetailScreenLocation=" + context.arrayDetailScreenLocation);
	
	Debug.log("********************************** setManagementScreenList.grrovy --> context.managementTabMenuName=" + context.managementTabMenuName);
	
	
	Debug.log("********************************** setManagementScreenList.grrovy --> context.arrayDetailContextScreenName=" + context.arrayDetailContextScreenName);
}
