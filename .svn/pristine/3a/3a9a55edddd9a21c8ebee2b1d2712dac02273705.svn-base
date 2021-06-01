import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

treeViewEntityName = parameters.treeViewEntityName;
if (UtilValidate.isEmpty(treeViewEntityName)) {
    request.setAttribute("_ERROR_MESSAGE_", com.mapsengineering.base.util.MessageUtil.getErrorMessage("entityNameNotFound", locale));
	return "error";	
}

parentRelKeyFields = parameters.parentRelKeyFields;
if (UtilValidate.isEmpty(parentRelKeyFields)) {
    res = "error";
    request.setAttribute("_ERROR_MESSAGE_", com.mapsengineering.base.util.MessageUtil.getErrorMessage("relationKeyNamesNotFound", locale));
	return "error";	
}

defaultValueFields = parameters.defaultValueFields;

parentRelOtherKeyFields = parameters.parentRelOtherKeyFields;

suffixChild = treeViewEntityName + "_child_";
suffixRoot = treeViewEntityName + "_root_";

//find out keys from parameters
for (key in parameters.keySet()) {
	
	//every parameter of our interest starts with treeViewEntityName + "child" or "root"
	//ex. Party_child_1 or Party_root_1
	if ((key.indexOf(suffixChild)>-1)||(key.indexOf(suffixRoot)>-1)) {
		value = parameters.get(key);
		
		//Original keys and parent keys are list of values separated by ;;;  (see loadTreeView.js)
		//ex. key1|value1,key2|value2;;;parent1|value1,parent2|value2
		
		List values = org.ofbiz.base.util.StringUtil.split(value, ";;;");
		//I cannot find complete parameters list
		if (values.size()!=2) {
		    request.setAttribute("_ERROR_MESSAGE_", com.mapsengineering.base.util.MessageUtil.getErrorMessage("parametersNotComplete", locale));
			return "error";	
		}
		
		//
		//Now build keys maps
		//
		
		Map parentKeys = javolution.util.FastMap.newInstance();
		Map originalKeys = javolution.util.FastMap.newInstance();

		// list of keys are stored like pairs list comma separated: keyName1|value1,keyName2|value2...
		
		//first string is new parent keys  (see loadTreeView.js)
		List pairs = StringUtil.split(values.get(0), ",");
		for (pair in pairs) {
			List lv = StringUtil.split(pair, "|");
			//if i have only one value make assuption that is key
			if (lv.size() > 1) {
				parentKeys.put(lv.get(0), lv.get(1));
			} else {
				parentKeys.put(lv.get(0), "");
			}
		}

		//last string is original keys  (see loadTreeView.js)
		pairs = StringUtil.split(values.get(1), ",");
		for (pair in pairs) {
			List lv = StringUtil.split(pair, "|");
			//if i have only one value make assuption that is key
			if (lv.size() > 1) {
				originalKeys.put(lv.get(0), lv.get(1));
			} else {
				originalKeys.put(lv.get(0), "");
			}
		}
		
		Map defaultValueMap = javolution.util.FastMap.newInstance();
		if (UtilValidate.isNotEmpty(defaultValueFields)) {
			//last string is original keys  (see loadTreeView.js)
			List lv = StringUtil.split(defaultValueFields, "|");
			lv.each { currentValue ->
				currentList = StringUtil.split(currentValue, ";");
				//if i have only one value make assuption that is key
				if (currentList.size() > 1) {
					defaultValueMap.put(currentList[0], currentList[1]);
				} else {
					defaultValueMap.put(currentList[0], "");
				}
				
			}
			
		}
		
		//
		//get relation keys names (list of either single or '|' separated values)
		//
		List relationKeys = StringUtil.split(parentRelKeyFields, ";");
		
		//Now put all into map and pass it to engine that apply changes to db
		Map serviceMap = javolution.util.FastMap.newInstance();
		serviceMap.put("entityName", treeViewEntityName);
		serviceMap.put("parentKeys", parentKeys);
		serviceMap.put("originalKeys", originalKeys);
		serviceMap.put("relationKeys", relationKeys);
		serviceMap.put("defaultValueMap", defaultValueMap);
		
		serviceMap.put("userLogin", userLogin);
		serviceMap.put("locale", locale);

		//Custom save method
		serviceName = parameters.treeChangesSaveMethods;
		if (UtilValidate.isEmpty(serviceName)) {
			serviceName = context.treeChangesSaveMethods;
		}
		if (UtilValidate.isEmpty(serviceName)) {
			serviceName = "saveTreeViewChanges";  //Default method			
		}

		resultMap = dispatcher.runSync(serviceName, serviceMap);
		if (UtilValidate.isNotEmpty(resultMap)) {
			if (org.ofbiz.service.ServiceUtil.isError(resultMap)||org.ofbiz.service.ServiceUtil.isFailure(resultMap)) {
				request.setAttribute("_ERROR_MESSAGE_", org.ofbiz.service.ServiceUtil.getErrorMessage(resultMap));
				return "error";
			} else {
//				Debug.log("********************************* resultMap.retValues = " + resultMap.retValues);
//				Debug.log("********************************* resultMap.id = " + resultMap.id);
				resultMap.each{key_sup, value_sup ->
					request.setAttribute(key_sup, value_sup);
				}
			}
		}
		
	} //end loop
}

return "success";