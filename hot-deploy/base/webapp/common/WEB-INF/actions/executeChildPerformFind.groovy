import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;

Debug.log("**** executeChildPerformFind.groovy -> context.parentEntityName=" + context.parentEntityName + " context.entityName ="+context.entityName)
//Debug.log("**** executeChildPerformFind.groovy -> context.insertMode="+context.insertMode)
//Debug.log("**** executeChildPerformFind.groovy -> context.parentEntityName="+context.parentEntityName)
//Affinchè tornando da fromdelete effettui la ricerca
insertMode = context.insertMode;
if (UtilValidate.isEmpty(insertMode))
    insertMode = "N";
orderBy = "";

context.noExecuteChildPerformFind = UtilValidate.isEmpty(context.noExecuteChildPerformFind) ? parameters.noExecuteChildPerformFind : context.noExecuteChildPerformFind;

//Debug.log("**** executeChildPerformFind.groovy -> context.noExecuteChildPerformFind =" + context.noExecuteChildPerformFind)
//Debug.log("**** executeChildPerformFind.groovy -> parameters.queryString ="+parameters.queryString)
//Debug.log("**** executeChildPerformFind.groovy -> parameters.noConditionFind ="+parameters.noConditionFind)

if (UtilValidate.isNotEmpty(context.entityName)) {
	
	//Rimuovo il valore originale in parameters perchè potrebbe provenire da altre elaborazioni...lo rimetto poi
	oldFilterByDate = parameters.filterByDate;
	parameters.remove("filterByDate");
	
	defaultNoFilterByDateEntityArray = ["WorkEffortView", "WorkEffortStatus", "WorkEffortAssignmentView", "WorkEffortAssignmentOrgUnitView",
		"WorkEffortAssignmentGoalView", "WorkEffortAssignmentAssignmentView", "WorkEffortAssignmentView", "WorkEffortAssocExtView",
		"WorkEffortAssignmentRoleView", "WorkEffortAttribute", "WorkEffortNoteAndData", "WorkEffortType", "WorkEffortTimeEntry",
		"WorkEffortMeasure", "UserLoginSecurityGroup", "PartyRelationship", "ProductRole", "PartyRoleViewRelationship"];
	
	filterByDate = null;

//	if (UtilValidate.isEmpty(parameters.filterByDate)) {
		for (i = 0; i < defaultNoFilterByDateEntityArray.size(); i++ ) {
			if (context.entityName.equals(defaultNoFilterByDateEntityArray[i])) {
				filterByDate = "N";
				break;
			}
		}
//	}
	
//	if(UtilValidate.isEmpty(parameters.filterByDate)) {
	if(UtilValidate.isEmpty(filterByDate)) {
		if(!UtilValidate.isEmpty(context.filterByDate)) {
			filterByDate = context.filterByDate;
		} else if(UtilValidate.isNotEmpty(oldFilterByDate)) {
			filterByDate = oldFilterByDate;
		}
	}
//	else {
//		filterByDate = parameters.filterByDate;
//	}
	parameters.filterByDate = filterByDate;
    //Debug.log("**** executeChildPerformFind.groovy -> context.entityName ="+context.entityName)
    if (!"N".equals(insertMode)) {
        //Debug.log("**** executeChildPerformFind.groovy -> context.insertMode="+insertMode)
        //Create only one row
        org.ofbiz.entity.GenericValue genericValue = delegator.makeValue(context.entityName);
        java.util.Map valueMap = javolution.util.FastMap.newInstance();
        valueMap.putAll(genericValue);
        valueMap.put('entityName', context.entityName);
        valueMap.put('parentEntityName', context.parentEntityName);
        valueMap.put("operation", "CREATE");
        if(UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
            extraParams = StringUtil.strToMap(context.managementChildExtraParams);
            valueMap.putAll(extraParams);
        }
        context.listIt = [valueMap];

    } else if(!"Y".equals(context.noExecuteChildPerformFind)){
    
//        Debug.log("**** executeChildPerformFind.groovy -> context.insertMode="+insertMode)

        if (UtilValidate.isNotEmpty(context.parentEntityName)) {

            modelEntity = delegator.getModelEntity(context.entityName);

//            Debug.log("**** executeChildPerformFind.groovy -> context.executePerformFind="+context.executePerformFind)
//            Debug.log("**** executeChildPerformFind.groovy -> cmodelEntity="+modelEntity);

            if (UtilValidate.isNotEmpty(modelEntity) && !("N".equals(context.executePerformFind))) {
                performFindParameters = ["noConditionFind": UtilValidate.isNotEmpty(parameters.noConditionFind) ? parameters.noConditionFind : "Y", "entityName" : context.entityName];

                if(UtilValidate.isNotEmpty(filterByDate)) {
                    performFindParameters["filterByDate"] = filterByDate;
                }
                //Debug.log("**** executeChildPerformFind.groovy -> filterByDate="+filterByDate)
				
//				Debug.log("****************************** executeChildPerformFind.groovy -> parameters.sortField = " + parameters.sortField);
				if (UtilValidate.isNotEmpty(parameters.sortField)) {
					orderBy = parameters.sortField;
				} else {
	                tableSortField = parameters.get("tableSortField");
//					Debug.log("****************************** executeChildPerformFind.groovy -> tableSortField = " + tableSortField);
	                if (tableSortField) {
	                    it = tableSortField.keySet().iterator();
	                    while (it.hasNext()) {
	                      tableSortFieldKey = it.next();
	                      entityFromTableSortField = tableSortFieldKey.substring(tableSortFieldKey.lastIndexOf("_") + 1);
	                      if (UtilValidate.isNotEmpty(context.entityName) && entityFromTableSortField.equals(context.entityName)) {
	                          orderBy = tableSortField.get(tableSortFieldKey);
	                      }
	                   }
	                }
				}
                // controlo se il parameters.sortField appartiene all'entityModel
                /*fieldNames = modelEntity.getAllFieldNames();
                currentSortField = parameters.sortField;
                if(UtilValidate.isNotEmpty(currentSortField) && currentSortField.indexOf("-") == 0) {
                    currentSortField = currentSortField.substring(1);
                }
                Debug.log("**** executeChildPerformFind.groovy -> fieldNames="+fieldNames);
                if(fieldNames.contains(currentSortField)) {
                    Debug.log("**** executeChildPerformFind.groovy -> c'è");
                } else {
                    parameters.sortField = "";
                    Debug.log("**** executeChildPerformFind.groovy -> non c'è");
                }
                */
                // se ordino la prima tabella, parameters.sortField contiene il sortField della prima tabella,
                //orderBy invece o è vuoto oppure contiene il campo della seconda tabella
                if (UtilValidate.isEmpty(orderBy)) {
                    orderBy = UtilValidate.isNotEmpty(orderBy) ? orderBy : context.sortField;
                    //orderBy = UtilValidate.isNotEmpty(parameters.sortField) ? parameters.sortField : context.sortField;
                    if (UtilValidate.isEmpty(orderBy)) {
                        orderByPkFieldNames = [];
                        pkFieldNames = modelEntity.getPkFieldNames();
                        for (pkFieldName in pkFieldNames) {
                            modelField = modelEntity.getField(pkFieldName);
                            pkFieldType = delegator.getEntityFieldType(modelEntity, modelField.getType());

                            javaType = pkFieldType.getJavaType();
                            if (javaType.indexOf(".") == -1)
                                javaType = "java.lang." + javaType;
                            if (ObjectType.isOrSubOf(Class.forName(javaType),"java.util.Date")) {
                                pkFieldName = "-"+pkFieldName;
                            }

                            orderByPkFieldNames.add(pkFieldName);
                        }
                        orderBy = StringUtil.join(orderByPkFieldNames,"|");
                    }
                }
                performFindParameters["orderBy"] = orderBy;

                if (UtilValidate.isNotEmpty(context.inputFields)) {
                    performFindParameters["inputFields"] = context.inputFields;
                }
//                Debug.log("******************************** executeChildPerformFind.groovy -> performFindParameters = " + performFindParameters);

                result = FindWorker.performFind(performFindParameters, dispatcher, timeZone, locale);
                if (!ServiceUtil.isError(result)) {

                    //Add operation parameter
                    if (UtilValidate.isNotEmpty(result.listIt)) {

                        java.util.List newList = javolution.util.FastList.newInstance();
                        java.util.List resultList = javolution.util.FastList.newInstance();

                        if(result.listIt instanceof EntityListIterator ){
                            resultList = result.listIt.getCompleteList();
                            //bugfix: chiudo entitylistiterator per non avere warnings
                            result.listIt.close();
                        }
                        else if(UtilValidate.isNotEmpty(result.listIt)) {
                            resultList = result.listIt;
                        }
						context.resultList = resultList;
						
//						if (UtilValidate.isNotEmpty(context.customExecutionChildPerformFindScript)) {
//							res = GroovyUtil.runScriptAtLocation(context.customExecutionChildPerformFindScript, context);
//						}
						//Debug.log("*********************************** executeChildPerformFind.groovy -> context.resultList = " + context.resultList);
						
						if (UtilValidate.isNotEmpty(context.resultList)) {
	                        for (GenericValue value: context.resultList) {
	                            //Debug.log("*********************************** executeChildPerformFind.groovy -> value" + value)
	                            java.util.Map map = javolution.util.FastMap.newInstance();
	                            map.putAll(value);
	                            map.put("operation", "UPDATE");
	                            newList.add(map);
	                        }
						}
						context.gvListIt = context.resultList;
						
						
						context.listIt = newList;
					} else {

                        context.listIt = result.listIt;
                    }
//                    Debug.log("*********************************** executeChildPerformFind.groovy -> context.listIt=" + context.listIt)

                    context.orderBy = orderBy;
                }
            }
        }
    }
	
	parameters.filterByDate = oldFilterByDate;
    //Debug.log("**** executeChildPerformFind.groovy -> context.entityName ="+context.entityName)
}