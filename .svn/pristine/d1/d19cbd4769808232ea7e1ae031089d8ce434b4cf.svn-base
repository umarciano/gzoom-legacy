
import javolution.util.FastMap;
import javolution.util.FastList;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.client.TreeReader;
import com.mapsengineering.base.client.TreeWorker;


delegator = context.delegator;
dispatcher = context.dispatcher;
parameters.locale = context.locale;
parameters.timeZone = context.timeZone;

if(/*"CREATE".equals(parameters.operation) || */UtilValidate.isNotEmpty(parameters.id) || "Y".equals(parameters.dragdrop)) {
//	Debug.log("********************************************* parameters.id = " + parameters.id);
	
	if (UtilValidate.isNotEmpty(parameters.id)) {
		parameters.idMap = StringUtil.strToMap(parameters.id);
	}
	
}

//Debug.log("*********************************** parameters.workEffortId = " + parameters.workEffortId);
//Debug.log("*********************************** context.workEffortId = " + context.workEffortId);

//---------------------
// Read all parent-child list
//---------------------
//Load entity
//
//Nota bene: Le chiavi definite nel menu iniziale sono usate nella classe TreeWorker
//
//TreeWorker tw = new TreeWorker(parameters, dispatcher, locale, UtilValidate.isNotEmpty(parameters.id));
TreeWorker tw = new TreeWorker(parameters, dispatcher, "CREATE".equals(parameters.operation));
Map result = tw.findTreeList();
session.removeAttribute("treeviewCreateSelectedId");

//Sandro : qui controllo se il servizio di save ha resituito come parametro dentro retValues 
//il campo alternativo sul quale effettuare il posizonamento dell'albero
if (UtilValidate.isNotEmpty(session.getAttribute("treeviewAlternateSelectedKey"))) {
	altKey = session.getAttribute("treeviewAlternateSelectedKey");
	session.removeAttribute("treeviewAlternateSelectedKey");
	session.removeAttribute(altKey);
}

//set it into context
context.treeViewList = result.get(TreeWorker.VALUE_LIST);

//---------------------
// Read tree xml structure
//---------------------

treeViewLocation = parameters.treeViewLocation;
treeViewName = parameters.treeViewName;

context.treeViewLocation = treeViewLocation;
context.treeViewName = treeViewName;

//Read full Tree structure
treeElement = TreeReader.readTreeElement(treeViewLocation, treeViewName);

if (UtilValidate.isEmpty(treeElement)) {
    org.ofbiz.base.util.Debug.log("[loadTreeView.groovy]: =============================== Impossibile costruire albero, treeElement restituito é vuoto");
}

//Open-close attribute
narrow = "N";
if (treeElement.hasAttribute("open-depth")&&"0".equals(treeElement.getAttribute("open-depth"))) {
    narrow = "Y";
}
context.narrow = narrow;

//Root node name
String rootNodeName = treeElement.getAttribute("root-node-name");

//Fill all root child element list
childList =  org.ofbiz.base.util.UtilXml.childElementList(treeElement);
mappingList = javolution.util.FastList.newInstance();
rootMap = javolution.util.FastMap.newInstance();
//loop over childnode
for (org.w3c.dom.Element item: childList) {
    if (rootNodeName.equalsIgnoreCase(item.getAttribute("name"))) {
        rootMap = TreeReader.getTreeColumnFieldMap(item, context);
    } else {
        mappingList.add(TreeReader.getTreeColumnFieldMap(item, context));
    }
}

//put tree items map into context for ftl
context.put("rootMap", rootMap);
context.put("mappingList", mappingList);

//-------------------------
//Data for ftl
//-------------------------

// root
rootValues = context.rootValues;
if (UtilValidate.isEmpty(rootValues)) {
    rootValues = parameters.rootValues;
}

// orderByFields
if (UtilValidate.isEmpty(context.orderByFields))
    context.orderByFields = parameters.orderByFields;


// forceUniqueRoot
if (UtilValidate.isEmpty(context.forceUniqueRoot))
    context.forceUniqueRoot = parameters.forceUniqueRoot;

//uniqueRootNotManaged
if (UtilValidate.isEmpty(context.uniqueRootNotManaged))
    context.uniqueRootNotManaged = parameters.uniqueRootNotManaged;
	
//searchFromManagement
if (UtilValidate.isEmpty(context.searchFromManagement))
	context.searchFromManagement = parameters.searchFromManagement;

//disable drag and drop
if (UtilValidate.isEmpty(context.disableDragAndDrop))
    context.disableDragAndDrop = parameters.disableDragAndDrop;

//Key fields
keyFields = context.keyFields;
if (UtilValidate.isEmpty(keyFields)) {
    keyFields = parameters.keyFields;
}
if (UtilValidate.isNotEmpty(keyFields)) {
    List keyFieldsList = StringUtil.split(keyFields, "|");
    for (i = 0; i < keyFieldsList.size(); ++i) {
        keyFieldsList[i] = StringUtil.wrapString(keyFieldsList[i]);
    }
    context.put("keyFieldsList", keyFieldsList);
}


//other fields which values have to be parametrized
otherFields = context.otherFields;
if (UtilValidate.isEmpty(otherFields)) {
    otherFields = parameters.otherFields;
}
if (UtilValidate.isNotEmpty(otherFields)) {
    List otherFieldsList = StringUtil.split(otherFields, "|");
    context.put("otherFieldsList", otherFieldsList);
}
context.otherFields = otherFields;


//Const Fields
if (UtilValidate.isEmpty(context.constFields)) {
    context.constFields = parameters.constFields;
}
if (UtilValidate.isNotEmpty(context.constFields)) {
    List constFieldsList = StringUtil.split(context.constFields, "|");
    //Se esistono campi costanti creo nel context una mappa dei valori passati per usarla poi in loadTreeView.ftl
    Map constValueMap = javolution.util.FastMap.newInstance();
    for (String item: constFieldsList) {
        String[] pair = item.split(";");
        if (pair.length == 2) {
            constValueMap.put(pair[0], pair[1]);
        }
    }
    context.put("constValueMap", constValueMap);
}

if (UtilValidate.isEmpty(context.defaultValueFields)) {
	context.defaultValueFields = parameters.defaultValueFields;
}
if (UtilValidate.isEmpty(context.extraFields)) {
	context.extraFields = parameters.extraFields;
}
if (UtilValidate.isNotEmpty(context.extraFields)) {
	List extraFieldsList = StringUtil.split(context.extraFields, "|");
	//Se esistono campi costanti creo nel context una mappa dei valori passati per usarla poi in loadTreeView.ftl
	Map extraValueMap = javolution.util.FastMap.newInstance();
	for (String item: extraFieldsList) {
		String[] pair = item.split(";");
		if (pair.length == 2) {
			extraValueMap.put(pair[0], pair[1]);
		}
	}
	context.put("extraValueMap", extraValueMap);
}

//Custom D&D method
if (UtilValidate.isEmpty(context.treeChangesSaveMethods)) {
    context.treeChangesSaveMethods = parameters.treeChangesSaveMethods;
}

//Custom script
if (UtilValidate.isEmpty(context.customFindScriptLocation)) {
    context.customFindScriptLocation = parameters.customFindScriptLocation;
}

//Entity per tree
treeViewEntityName = parameters.treeViewEntityName;

//Relazione
parentRelKeyFields = parameters.parentRelKeyFields;

//
//Save parameteres into session for insert when there isn't any node into tree (red from main screen)
//
Map treeViewMap = javolution.util.FastMap.newInstance();
treeViewMap.put("treeViewLocation", treeViewLocation);
treeViewMap.put("treeViewName", treeViewName);
treeViewMap.put("rootValues", rootValues);
treeViewMap.put("keyFields", keyFields);
treeViewMap.put("otherFields", otherFields);
treeViewMap.put("constFields", context.constFields);
treeViewMap.put("treeChangesSaveMethods", treeChangesSaveMethods);
treeViewMap.put("treeViewEntityName", treeViewEntityName);
treeViewMap.put("parentRelKeyFields", parentRelKeyFields);
treeViewMap.put("orderByFields", context.orderByFields);
treeViewMap.put("forceUniqueRoot", context.forceUniqueRoot);
treeViewMap.put("uniqueRootNotManaged", context.uniqueRootNotManaged);
treeViewMap.put("searchFromManagement", context.searchFromManagement);
treeViewMap.put("disableDragAndDrop", context.disableDragAndDrop);
treeViewMap.put("constFields", context.constFields);
treeViewMap.put("customFindScriptLocation",  context.customFindScriptLocation);
treeViewMap.put("defaultValueFields",  context.defaultValueFields);
treeViewMap.put("extraFields",  context.extraFields);

session.setAttribute("treeViewMap", treeViewMap);

// Bug 3506
if(UtilValidate.isNotEmpty(result.get(TreeWorker.SELECTED_NODE))) {
	parameters.putAll(result.get(TreeWorker.SELECTED_NODE));
} else if (UtilValidate.isNotEmpty(parameters.parentSelectedId) && UtilValidate.isNotEmpty(result.get(TreeWorker.ALL_NODE))){
	def allNodeMap = result.get(TreeWorker.ALL_NODE);
	def parentNodeMap = allNodeMap[parameters.parentSelectedId];
	
	if (UtilValidate.isNotEmpty(parentNodeMap)) {
		def indexInList = context.treeViewList.indexOf(parentNodeMap);
		if (indexInList != -1) {
			parentNodeMap = context.treeViewList.get(indexInList);
		}
	}
	if (UtilValidate.isNotEmpty(parentNodeMap)) {
		parameters.putAll(parentNodeMap);
		parentNodeMap._SELECTED_ = "Y";
	}
	parameters.selectedId = parameters.parentSelectedId;
}

return "success";
