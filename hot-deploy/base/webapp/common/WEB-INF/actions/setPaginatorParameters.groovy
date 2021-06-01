import org.ofbiz.base.util.*;

if (UtilValidate.isEmpty(context.entityName) && UtilValidate.isNotEmpty(parameters.entityName)) {
    context.entityName = parameters.entityName;
}

//Serve per il caso dell'albero
subFolderEntityName = "";
if(UtilValidate.isNotEmpty(context.selectedIndex)) {
	subFolderEntityName = arrayEntityName[context.selectedIndex];
}

if (UtilValidate.isNotEmpty(context.entityName)) {
    tmpParameters = [:];

    for (key in parameters.keySet()) {
        value = parameters[key];
        if (key.contains(context.entityName + "_VIEW_INDEX") || key.contains(context.entityName + "_" + parameters.folderIndex + "_VIEW_INDEX") 
			|| (UtilValidate.isNotEmpty(subFolderEntityName) && key.contains(subFolderEntityName + "_VIEW_INDEX"))) {
            paginatorNumber = key.substring(key.lastIndexOf('_') + 1);

            tmpParameters["PAGINATOR_NUMBER"] = paginatorNumber;
            tmpParameters["VIEW_INDEX_" + paginatorNumber] = value;

            break;
        } else {
//            parameters.remove("PAGINATOR_NUMBER");
        }
    }

    parameters.putAll(tmpParameters);
}