import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";
//Creo una mappa locale in cui elimino i parametri viewIndex e viewSize, che cobn la nuova versione di OFBiz sono passabili
//al servizio di ricerrca e questo limiterebbe i dati in uscita
localParameters = [:];
localParameters.putAll(parameters);

/*
 * Adesso per default tutte le entity non vengono filtrate per data, quelle da filtrare bisogna specificarlo nella form con
 *  filterByDate = "Y"
 */
/*
defaultNoFilterByDateEntityArray = ["WorkEffortView", "WorkEffortStatus", "WorkEffortAssignmentView", "WorkEffortAssignmentOrgUnitView", "WorkEffortAssignmentGoalView", "WorkEffortAssignmentAssignmentView", "WorkEffortAssignmentView", "WorkEffortAssocExtView", "WorkEffortAssignmentRoleView", "WorkEffortAttribute", "WorkEffortNoteAndData", "WorkEffortMeasure", "CustomTimePeriod", "WorkEffortType"];
defaultFilterByDateSet = false;

entityName = UtilValidate.isNotEmpty(context.entityName) ? context.entityName : localParameters.entityName;
if (UtilValidate.isEmpty(parameters.filterByDate)) {
	for (i = 0; i < defaultNoFilterByDateEntityArray.size(); i++ ) {
		if (entityName.equals(defaultNoFilterByDateEntityArray[i])) {
			filterByDate = "N";
			defaultFilterByDateSet = true;
			break;
		}
	}
}

if(UtilValidate.isEmpty(localParameters.filterByDate)) {
	if(!UtilValidate.isEmpty(context.filterByDate)) {
		filterByDate = context.filterByDate;
	}
	else {
		if (!defaultFilterByDateSet) {
			filterByDate = "Y";
		}
	}
}
else {
	filterByDate = localParameters.filterByDate;
}
localParameters.filterByDate = filterByDate;
*/

localParameters.filterByDate = filterByDate = UtilValidate.isEmpty(localParameters.filterByDate) ? UtilValidate.isEmpty(context.filterByDate) ? "N" : context.filterByDate : localParameters.filterByDate;


//Se arrivo da una cancellazione da management allora elimino da parameters i campi chiave
if ("Y".equals(localParameters.searchFromManagement) && "Y".equals(localParameters.fromDelete) && UtilValidate.isNotEmpty(localParameters.id)) {
//	Debug.log("****************************** executePerformFind.groovy -> Eliminazione degli id in caso di cancellazione da management")
	if (localParameters.id.startsWith("["))
		localParameters.id = localParameters.id.substring(1);
	if (localParameters.id.endsWith("]"))
		localParameters.id = localParameters.id.substring(0, localParameters.id.length()-1);

	idMap = StringUtil.strToMap(localParameters.id);
	
//	Debug.log("****************************** executePerformFind.groovy -> idMap = " + idMap)
	
	if (UtilValidate.isNotEmpty(idMap)) {
		for (key in idMap.keySet()) {
//			Debug.log("****************************** executePerformFind.groovy -> localParameters["+key+"] = " + localParameters[key])
			if(UtilValidate.isNotEmpty(localParameters[key])) {
				localParameters.remove(key);
			}
		}
	}

	localParameters.remove("id");
	request.removeAttribute("id");
}

//Debug.log("*********************************** parameters.successCode"+parameters.successCode);
if (UtilValidate.isNotEmpty(localParameters.successCode)) {
    // in questo modo la ricerca di una lista di tipo multi apre il managemnt
    if("multi".equals(localParameters.managementFormType))
        res = "management";
    else
        res = localParameters.successCode;
}

if (localParameters.containsKey("fieldList")) {
    if (UtilValidate.isNotEmpty(localParameters.fieldList)) {
        if (ObjectType.instanceOf(localParameters.fieldList, String.class)) {
        	localParameters.fieldList = StringUtil.toList(localParameters.fieldList,"\\|");
        } else {
        	localParameters.remove("fieldList");
        }
    } else {
    	localParameters.remove("fieldList");
    }
}
////Debug.log("parameters");

tableSortField = localParameters.get("tableSortField");
if (tableSortField) {
    it = tableSortField.keySet().iterator();
    while (it.hasNext()) {
      tableSortFieldKey = it.next();
      entityFromTableSortField = tableSortFieldKey.substring(tableSortFieldKey.lastIndexOf("_") + 1);

//      Debug.log("**** populateManagement.groovy -> entityFromTableSortField = "+ entityFromTableSortField);
      if (UtilValidate.isNotEmpty(localParameters.entityName) && entityFromTableSortField.equals(localParameters.entityName)) {
    	  localParameters.sortField = tableSortField.get(tableSortFieldKey);
//          Debug.log("**** populateManagement.groovy -> parameters.orderBy = "+ parameters.orderBy);
      }
   }
}

localParameters.remove("viewIndex");
localParameters.remove("viewSize");

// la performFind prima cerca il valore di sortField e se non c'è prende ordrBy, quindi nella form di ricerca mettiamo orderBy
// e nelle freccette di ordinamento nelle tabelle mettiamo sortField
//Debug.log("**** executePerformFind.groovy -> parameters.sortField = "+ parameters.sortField);
//Debug.log("**** executePerformFind.groovy -> context.sortField = "+ context.sortField);
//Debug.log("**** executePerformFind.groovy -> parameters.orderBy = "+ parameters.orderBy);
//Debug.log("**** executePerformFind.groovy -> context.orderBy = "+ context.orderBy);
result = FindWorker.performFind(localParameters, dispatcher, timeZone, locale);
//Debug.log("************************************ executePerformFind.groovy --> result.queryString=" + result.queryString);
if (ServiceUtil.isError(result)) {
    res = "error";
    request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
} else {
    if (UtilValidate.isNotEmpty(result.listIt) && result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
         lista = result.listIt.getCompleteList();
         result.listIt.close();
         result.listIt = lista;
         if (UtilValidate.isNotEmpty(context))
        	 context.listIt = lista;
//         Debug.log("************************************** result.listIt="+result.listIt);
    } else {
        request.setAttribute("noResult", "Y");
    }
    for(key in result.keySet()) {
        request.setAttribute(key, result[key]);
    }
}

if (res == "success") {
	// check if this is massive-print-search or export-search 
    res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkExportSearchResult.groovy", context);
}
Debug.log("*** executePerformFind.groovy res=" + res);
return res;
