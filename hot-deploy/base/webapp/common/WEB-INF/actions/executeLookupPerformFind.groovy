import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

executePerformFind = context.executePerformFind;
if (UtilValidate.isEmpty(executePerformFind)) {
    executePerformFind = parameters.executePerformFind;
}
if (UtilValidate.isEmpty(executePerformFind)) {
    executePerformFind = "Y";
}
lookup = context.lookup;
if (UtilValidate.isEmpty(lookup)) {
    lookup = parameters.lookup;
}

preLoadListIt = context.preLoadListIt;
if (UtilValidate.isEmpty(preLoadListIt)) {
	preLoadListIt = parameters.preLoadListIt;
}
if (UtilValidate.isEmpty(preLoadListIt)) {
	preLoadListIt = "N";
}

executePerformFindScriptName = context.executePerformFindScriptName;

//Debug.log("************************************ executeLookupPerformFind -> preLoadListIt = " + preLoadListIt);
//Debug.log("************************************ executeLookupPerformFind -> lookup = " + lookup);
//Debug.log("************************************ executeLookupPerformFind -> executePerformFindScriptName = " + executePerformFindScriptName);
//
//Debug.log("************************************ executeLookupPerformFind -> context.locale = " + context.locale);


if ("Y".equals(lookup)) {
	
	/* Per la ricerca del GlAccount */
	if(preLoadListIt.equals("Y")){
		context.listIt = parameters.listIt;
	} else  if (UtilValidate.isEmpty(executePerformFindScriptName)) {
//        Debug.log("************************************ executeLookupPerformFind -> context.locale = " + context.locale);
        if (parameters.containsKey("fieldList")) {
            if (UtilValidate.isNotEmpty(parameters.fieldList)) {
                if (ObjectType.instanceOf(parameters.fieldList, String.class)) {
                    parameters.fieldList = StringUtil.toList(parameters.fieldList,"\\|");
                } else {
                    parameters.remove("fieldList");
                }
            } else {
                parameters.remove("fieldList");
            }
        }

        tableSortField = parameters.get("tableSortField");
        if (tableSortField) {
            it = tableSortField.keySet().iterator();
            while (it.hasNext()) {
              tableSortFieldKey = it.next();
              entityFromTableSortField = tableSortFieldKey.substring(tableSortFieldKey.lastIndexOf("_") + 1);

              if (UtilValidate.isNotEmpty(parameters.entityName) && entityFromTableSortField.equals(parameters.entityName)) {
                  parameters.sortField = tableSortField.get(tableSortFieldKey);
              }
           }
        }
        result = FindWorker.performFind(parameters, dispatcher, timeZone, locale);

        if("Y".equals(parameters.enableMassiveInsert)){
        	context.massiveInsertQueryString = result.queryString.replaceAll("&amp;", "|");
        }
        if (!ServiceUtil.isError(result)) {
            if (UtilValidate.isNotEmpty(result.listIt) && result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
                list = result.listIt.getCompleteList();
//              bugfix: chiudo entitylistiterator per non avere warnings
                result.listIt.close();
                result.listIt = list;
            }
            context.listIt = result.listIt;
        }

//        Debug.log("************************************ executeLookupPerformFind -> context.locale = " + context.locale);

    } else {
//        Debug.log("************************************ executeLookupPerformFind -> prima di richiamare lo script " + executePerformFindScriptName +  " context.locale = " + context.locale);

        res = GroovyUtil.runScriptAtLocation(executePerformFindScriptName, context);

//        Debug.log("************************************ executeLookupPerformFind -> dopo aver rihiamato lo script " + executePerformFindScriptName + " context.locale = " + context.locale);
    }
}
