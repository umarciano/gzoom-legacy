import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;

res = "success";

//sandro: inserita gestione per giro non standard (vedi screen WorkEffortAchieveViewContextManagementScreen) 
useFolder = ("Y".equals(parameters.useFolder));
if (useFolder) {
	return res;
}

//Gestione cache
useCache = "Y".equals(parameters.useCache) ? true : false;
//Debug.log("************************************* populateManagement.groovy --> useCache=" + useCache);

entityName = parameters.entityName;
//Debug.log("####################################### populateManagement.groovy -> entityName =" + entityName);
//if ("Y".equals(parameters.contextManagement)) {
//    entityName = parameters.parentEntityName;
//}

//Debug.log("####################################### populateManagement.groovy -> entityName =" + entityName);

insertMode = parameters.insertMode;
if (UtilValidate.isEmpty(insertMode))
    insertMode = "N";
childManagement = parameters.childManagement;
if (UtilValidate.isEmpty(childManagement))
    childManagement = "N";
managementFormType = parameters.managementFormType;
if (UtilValidate.isEmpty(managementFormType))
    managementFormType = "single";
contextManagement = parameters.contextManagement;
if (UtilValidate.isEmpty(contextManagement))
    contextManagement = "N";
//se seleziono selectall il mangementFormtype ï¿½ single, ma devo ancora fare la ricerca, quindi
fromSelectAll = parameters.fromSelectAll;
if (UtilValidate.isEmpty(fromSelectAll))
    fromSelectAll = "N";
//se fromManagement e' uguale a N, viene effettuata la ricerca con la queryString precedente
fromManagement = parameters.fromManagement;
if (UtilValidate.isEmpty(fromManagement))
    fromManagement = "N";
fromDelete = parameters.fromDelete;
if (UtilValidate.isEmpty(fromDelete))
    fromDelete = "N";
fromPortlet = parameters.fromPortlet;
if (UtilValidate.isEmpty(fromPortlet))
    fromPortlet = "N";
wizard = parameters.wizard;
if (UtilValidate.isEmpty(wizard))
    wizard = "N";
loadTreeView = parameters.loadTreeView;
if (UtilValidate.isEmpty(loadTreeView))
	loadTreeView = "N";

defaultNoFilterByDateEntityArray = ["WorkEffortView", "WorkEffortStatus", "WorkEffortAssignmentView", "WorkEffortAssignmentOrgUnitView", "WorkEffortAssignmentGoalView", "WorkEffortAssignmentAssignmentView", "WorkEffortAssignmentContentView", "WorkEffortAssignmentContentOrgMgrView", "WorkEffortAssocExtView", "WorkEffortAssignmentRoleView", "WorkEffortAttribute", "WorkEffortNoteAndData", "WorkEffortMeasure", "CustomTimePeriod", "WorkEffortMeasureAndPurposeAccountInd", "WorkEffortMeasureAndPurposeAccountRes"];
defaultFilterByDateSet = false;

if (UtilValidate.isEmpty(parameters.filterByDate)) {
    for (i = 0; i < defaultNoFilterByDateEntityArray.size(); i++ ) {
        if (entityName.equals(defaultNoFilterByDateEntityArray[i])) {
            filterByDate = "N";
            defaultFilterByDateSet = true;
            break;
        }
    }
}

if(UtilValidate.isEmpty(parameters.filterByDate)) {
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
    filterByDate = parameters.filterByDate;
}
parameters.filterByDate = filterByDate;

tableSortField = parameters.get("tableSortField");
if (tableSortField) {
    it = tableSortField.keySet().iterator();
    while (it.hasNext()) {
      tableSortFieldKey = it.next();
      entityFromTableSortField = tableSortFieldKey.substring(tableSortFieldKey.lastIndexOf("_") + 1);

      if (UtilValidate.isNotEmpty(entityName) && entityFromTableSortField.equals(entityName)) {
          parameters.orderBy = tableSortField.get(tableSortFieldKey);
      }
   }
}

//Debug.log("####################################### populateManagement.groovy -> entityName =" + entityName);
if (UtilValidate.isNotEmpty(entityName)) {
    if ("N".equals(insertMode)) {
        modelEntity = delegator.getModelEntity(entityName);
        if ((!"Y".equals(fromSelectAll) && "single".equals(managementFormType)) || "Y".equals(contextManagement) || "Y".equals(fromPortlet)) {
            operation = parameters.operation;
            if("single".equals(managementFormType) && !"DELETE".equals(operation)) {
                //parameters.id contiene "id" dell'elemento aggiornato o creato
                //parameters.key contiene "id" degli elementi selezionati nel management, per esempio noteId=10000|10001|10002
                if (UtilValidate.isNotEmpty(parameters.id)) {
                    if (parameters.id.startsWith("["))
                        parameters.id = parameters.id.substring(1);
                    if (parameters.id.endsWith("]"))
                        parameters.id = parameters.id.substring(0, parameters.id.length()-1);

                    idMap = StringUtil.strToMap(parameters.id);
//                    Debug.log("******************************* populateManagement.groovy -> idMap =" + idMap);
                    
                    if (UtilValidate.isNotEmpty(idMap)) {
                        for (key in idMap.keySet()) {
                            // Se ho appena inserito da gestione FormCrud devo recuperare dall request il nuovo id
                            if("CREATE".equals(parameters.operation)) {
                                parameters.put(key, idMap[key]);
                            }
                            if(UtilValidate.isEmpty(request.getAttribute(key))) {
                                if(UtilValidate.isNotEmpty(parameters.get(key))){
                                    request.setAttribute(key, parameters.get(key));
                                }
                                else {
                                    request.setAttribute(key, idMap[key]);
                                }
                            }
                        }
                    }

                    request.removeAttribute("id");
                }
            }

            //Debug.log("******************************* populateManagement.groovy -> modelEntity =" + modelEntity);
            //Debug.log("******************************* populateManagement.groovy -> parameters =" + parameters.workEffortId);
            if (UtilValidate.isNotEmpty(modelEntity)) {
                pkFieldsFromRequest = [:];
                pkFieldsName = modelEntity.getPkFieldNames();  //List<String>

                pkFieldMap = [:];
                rowSelected = 0;

                for (pkFieldName in pkFieldsName) {
//                    Debug.log("******************************* populateManagement.groovy -> pkFieldName =" + pkFieldName);
//					Debug.log("******************************* populateManagement.groovy -> parameters["+pkFieldName+"] =" + parameters[pkFieldName]);

                    pkValue = UtilValidate.isNotEmpty(parameters["actual_" + pkFieldName]) ? parameters["actual_" + pkFieldName] : parameters[pkFieldName];
                    if (UtilValidate.isEmpty(pkValue)) {
                        rowSelected = 0;
                        break;
                    }

                    if (!(pkValue instanceof List)) {
                        try {
                            if (!pkValue.startsWith('['))
                                pkValue = '[' + pkValue;
                            if (!pkValue.endsWith(']'))
                                pkValue += ']';
                            pkValue = StringUtil.toList(pkValue, "\\|");
                        } catch(IllegalArgumentException e) {
                            pkValue = [pkValue];
                        }
                    }
                    currentRowSelected = pkValue.size();

                    if (currentRowSelected == 0) {
                        rowSelected = 0;
                        break;
                    }

                    if (currentRowSelected > rowSelected) {
                        rowSelected = currentRowSelected;
                    }

                    pkFieldMap[pkFieldName] =  pkValue;
                }

//				Debug.log("***************************** populateManagement.groovy -> rowSelected = " + rowSelected);
				
                if (rowSelected > 0) {
                    //In questa mappa metto tutte le chiavi ancora esistenti, nel caso si provenga da cancellazione da selezione multipla
                    //Questa mappa verrï¿½ poi trasformata in stringa e sostituirï¿½ la requestAttribute da cui ï¿½ stata derivata precedentemente
                    pkFieldExistingMap = [:];
                    listIt = [];
                    for (i=0; i < rowSelected; i++) {
                        pkMapValues = [:];

                        pkFieldMap.each { key,value ->
                            pkModelField = modelEntity.getField(key);
                            pkFieldType = delegator.getEntityFieldType(modelEntity, pkModelField.getType());

                            pkMapValues[key] = ObjectType.simpleTypeConvert((i < value.size() ? value[i] : value[0]), pkFieldType.getJavaType(), null, context.locale, true);
                        }

//						Debug.log("***************************** populateManagement.groovy -> pkMapValues = " + pkMapValues);
//						Debug.log("***************************** populateManagement.groovy -> useCache = " + useCache);
						
                        value = delegator.findOne(entityName, pkMapValues, useCache);
                        if (UtilValidate.isNotEmpty(value)) {
                            listIt.add(value);

                            //Ogni volta che per una determinata chiave si trova un valore, tale chiave dovrï¿½ essere inviata come parametro
                            //alle elaborazioni successive
                            pkFieldMap.each { key,value ->
                                if (UtilValidate.isEmpty(pkFieldExistingMap[key])) {
                                    pkFieldExistingMap[key] = [(i < value.size() ? value[i] : value[0])];
                                } else {
                                    pkFieldExistingMap[key].add((i < value.size() ? value[i] : value[0]));
                                }
                            }
                        }
                    }
					
//					Debug.log("***************************** populateManagement.groovy -> Dopo eventuale aggiunta di chiave: listIt = " + listIt);

                    if (UtilValidate.isNotEmpty(listIt)) {
                        //Trasformo il gruppo di chiavi in string e la metto negli attributi della request
                        pkFieldExistingMap.each { key,value ->
                            if (value.size() > 1)
                                stringValue = "[" + StringUtil.join(value, "|") + "]";
                            else
                                stringValue = value[0];

                            request.setAttribute(key, stringValue);
                        }

                        if (listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
                            def listItOrig = listIt;
                            listIt = listItOrig.getCompleteList();
                            listItOrig.close();
                        }
                    }

                    if (UtilValidate.isEmpty(listIt)) {
//                        Debug.log("***************************** populateManagement.groovy -> La lista è vuota");
//						Debug.log("***************************** populateManagement.groovy -> parameters.searchIfEmpty = " + parameters.searchIfEmpty);
//						Debug.log("***************************** populateManagement.groovy -> managementFormType = " + managementFormType);
//						Debug.log("***************************** populateManagement.groovy -> fromDelete = " + fromDelete);
                        if ("single".equals(managementFormType) || "Y".equals(fromDelete)) {
							searchIfEmpty = parameters.searchIfEmpty;
							if ("Y".equals(searchIfEmpty)) {
								res = "searchRequest";
								request.setAttribute("searchFromManagement", "Y");
							} else if (!"Y".equals(loadTreeView)) {
                            	request.setAttribute("insertMode", "Y");
							}
                        } else {
                            request.setAttribute("noResult", "Y");
                        }
                        request.setAttribute('fromDelete', fromDelete);
                    } else {
                        request.setAttribute('listIt',listIt);
                    }

                }
            } else {
    //          TODO Aggiungere lo stack-trace
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
                res = "error";
            }
        } else if (!managementFormType.equals("single") || "Y".equals(fromSelectAll)){
            idFormParameters = null;
            if (UtilValidate.isNotEmpty(parameters.id)) {
                idFormParameters = parameters.id;
                parameters.remove('id');
            }
			
            result = FindWorker.performFind(parameters, dispatcher, timeZone, locale);
            if (ServiceUtil.isError(result)) {
                res = "error";
                request.setAttribute("_ERROR_MESSAGE_", result.get(ModelService.ERROR_MESSAGE));
            } else {
                if (UtilValidate.isNotEmpty(idFormParameters)) {
                    if (!idFormParameters.startsWith('['))
                        idFormParameters = '[' + idFormParameters;
                    if (!idFormParameters.endsWith(']'))
                        idFormParameters += ']';
                    idList = StringUtil.toList(idFormParameters, '\\,');
                    if (UtilValidate.isEmpty(idList)) {
                        idList = [idFormParameters];
                    }

                    if (UtilValidate.isEmpty(result.listIt)) {
                        for(id in idList) {
                            pkMapValues = StringUtil.strToMap(id, true);
                            for(key in pkMapValues.keySet()) {
                                pkMapValues.put(key, modelEntity.convertFieldValue(modelEntity.getField(key), pkMapValues.get(key), delegator, UtilMisc.toMap("locale", locale, "timeZone", timeZone)));
                            }

                            try {
                            	value = delegator.findOne(entityName, pkMapValues, useCache);
                                if (value) {
                                    if (UtilValidate.isEmpty(result.listIt)) {
                                        result.listIt = [value];
                                    } else {
                                        if (result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
                                            def listItOrig = result.listIt;
                                            result.listIt = listItOrig.getCompleteList();
                                            listItOrig.close();
                                        }
                                        result.listIt.add(value);
                                    }
                                }
                            } catch (e) {
                            	Debug.logError(e, null);
                            }
                        }
                    } else {
                        if (result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
                            def listItOrig = result.listIt;
                            result.listIt = listItOrig.getCompleteList();
                            listItOrig.close();
                        }

                        for(id in idList) {
                            pkMapValues = StringUtil.strToMap(id, true);
                            for(key in pkMapValues.keySet()) {
                                pkMapValues.put(key, modelEntity.convertFieldValue(modelEntity.getField(key), pkMapValues.get(key), delegator, UtilMisc.toMap("locale", locale, "timeZone", timeZone)));
                            }
                            if (UtilValidate.isEmpty(EntityUtil.filterByAnd(result.listIt, pkMapValues))) {
                            	try {
	                            	value = delegator.findOne(entityName, pkMapValues, useCache);
	                                if (value) {
	                                    result.listIt.add(value);
	                                }
                            	} catch (e){
                                	Debug.logError(e, null);
                                }
                            }
                        }
                    }
                }
//				Debug.log(" ########################################## SONO QUI " + result.listId);
                if (UtilValidate.isEmpty(result.listIt)) {
                    res = "search";

                    request.setAttribute('fromDelete', fromDelete);
                    request.setAttribute("noResult", "Y");
                }
                else {
                    if (result.listIt instanceof org.ofbiz.entity.util.EntityListIterator) {
                        def listItOrig = result.listIt;
                        result.listIt = listItOrig.getCompleteList();
                        listItOrig.close();
                    }
                    if (result.listIt.size() == 0) {
                        res = "search";
                        request.setAttribute('fromDelete', fromDelete);
                        request.setAttribute("noResult", "Y");
                    }
                }
                for(key in result.keySet()) {
                    request.setAttribute(key, result[key]);
                }
            }
        }
    } else {
        genericValue = delegator.makeValue(entityName);
        valueMap = [:];
        valueMap.putAll(genericValue);
        valueMap.put('entityName', entityName);
        valueMap.put("operation", "CREATE");
        listIt = [valueMap];
        request.setAttribute("listIt", listIt);


        if ("Y".equals(wizard)) {
            res = "wizard";
        }

    }
} else {
//  TODO Aggiungere lo stack-trace
    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorEntityNameNotSet", locale));
    res = "error";
}

return res;