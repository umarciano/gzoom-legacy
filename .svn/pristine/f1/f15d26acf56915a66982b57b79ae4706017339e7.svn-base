import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericEntity;

/**
*  Estrazione dati per albero relativo a glAccountAndClassView
*/

childFolderFile = context.inputFields.remove("childFolderFile"); 
parentId = context.inputFields.get("parentId"); 

condition = EntityCondition.makeCondition(context.inputFields);
List<GenericValue> res = javolution.util.FastList.newInstance();

//Estraggo le roots
if (UtilValidate.isEmpty(parentId)) {
	
	rootList = delegator.findList("GlAccountClassFolder", condition, null, context.orderByFields, null, false);
	for (org.ofbiz.entity.GenericValue root: rootList) {
		root.setString("childFolderFile", "FOLDER");
		def rootMap = root.getAllFields();
		rootMap.entityName = "GlAccountClassView"
		res.add(rootMap);
	}
	
} else {//Estraggo i childs 
	
	//Estraggo folder e file child di folder
	if (UtilValidate.isEmpty(childFolderFile)||"FOLDER".equalsIgnoreCase(childFolderFile)) {
		
		foldersList = delegator.findList("GlAccountClassFolder", condition, null, context.orderByFields, null, false);
		for (org.ofbiz.entity.GenericValue folder: foldersList) {
			folder.setString("childFolderFile", "FOLDER");
			def folderMap = folder.getAllFields();
			folderMap.entityName = "GlAccountClassView"
			res.add(folderMap);
		}
		
		childList = delegator.findList("GlAccountFolderFile", condition, null, context.orderByFields, null, false);
		for (GenericValue child: childList) {
			child.setString("childFolderFile", "FILE");
			def childMap = child.getAllFields();
			childMap.entityName = "GlAccountView"
			res.add(childMap);
		}
		
	} else {
	
		//Estraggo i files child di file
		childFileList = delegator.findList("GlAccountFileFile", condition, null, context.orderByFields, null, false);
		for (GenericValue child: childFileList) {
			child.setString("childFolderFile", "FILE");
			def childMap = child.getAllFields();
			childMap.entityName = "GlAccountView"
			res.add(childMap);
		}
	}
	
}

context.foundList = res;

