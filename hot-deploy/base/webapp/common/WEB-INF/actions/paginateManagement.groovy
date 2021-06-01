import org.ofbiz.base.util.*;
import org.ofbiz.widget.form.*;
import org.ofbiz.entity.util.*;

Debug.log("************************************* paginateManagement.groovy parameters.parentEntityName " + parameters.parentEntityName + " parameters.entityName " + parameters.entityName + " UtilValidate.isNotEmpty(context.listIt) " + UtilValidate.isNotEmpty(context.listIt));
long startTime = System.currentTimeMillis();

if (UtilValidate.isNotEmpty(context.listIt)) {
    //In questo caso è stata eseguita una ricerca precendete o comunque un servizio che ha
    //recuperato una lista di elementi da visualizzare
    modelEntity = delegator.getModelEntity(parameters.entityName);
    if (UtilValidate.isNotEmpty(modelEntity)) {
        modelRelation = null;

        if (UtilValidate.isNotEmpty(parameters.parentEntityName)) {
            //Nel caso in cui sia presente l'informazione sul parentEntityName, recupero la relazione che intercorre tra
            //l'entityName e il parentEntityName
            relationTitle = UtilValidate.isNotEmpty(context.relationTitle) ? context.relationTitle : parameters.relationTitle;
            if (UtilValidate.isEmpty(relationTitle)) {
                relationTitle = "";
            }
            relationTitle += context.parentEntityName;

            modelRelation = modelEntity.getRelation(relationTitle);
            
            if (UtilValidate.isEmpty(modelRelation)) {
                for(m in modelEntity.getRelationsOneList()) {
                    findRelation = false;
                    for (modelKeyMap in m.getKeyMapsClone()) {
                        parameterRelFieldName = modelKeyMap.getRelFieldName();
                        parameterName = modelKeyMap.getFieldName();

                        findRelation = UtilValidate.isNotEmpty(context[parameterName]) || UtilValidate.isNotEmpty(parameters[parameterName]) || UtilValidate.isNotEmpty(context[parameterRelFieldName]) || UtilValidate.isNotEmpty(parameters[parameterRelFieldName]);
                        if (!findRelation) {
                            break;
                        }
                    }
                    if (findRelation) {
                        modelRelation = m;
                        break;
                    }
                }
            }
        }

        //Nella mappa pkQueryStringMap recupero tutti i campi chiave eventualmente passati come parametri,
        //nel caso, ad esempio di selezione di dettaglio. Nel caso di selezione multipla questi arriveranno nella forma id1|id2....
        //per ogni campo chiave dell'entità
        pkFieldsName = modelEntity.getPkFieldNames();  //List<String>

        pkFieldMap = [:];
        pkQueryStringMap = [:];

        pkFieldsName.each { fieldName ->
            if (UtilValidate.isNotEmpty(parameters[fieldName])) {
                pkValue = parameters[fieldName];

                if (pkValue.indexOf("|") != -1) {
                    if (!pkValue.startsWith('['))
                        pkValue = '[' + pkValue;
                    if (!pkValue.endsWith(']'))
                        pkValue += ']';
                }
                pkQueryStringMap[fieldName] = pkValue;
            }
        }

        //Nel caso in cui sia indicato un parentEntityName (gestione folder o customizzata) si popola la mappa managementChildExtraParams
        //con i campi della relazione tra le due entità, eliminandoli, poi, dalla pkQueryStringMap costruita precedentemente, così
        //da evitare duplicazioni. Da notare che si considera la possibilità che la mappa managementChildExtraParams sia gia popolata
        //e trasformata in stringa, per cui a quello gia esistente, si aggiunge la collezione di dati qui eleborati, sovrascrivendo eventuali
        //campi gia esistenti
        if (UtilValidate.isNotEmpty(parameters.parentEntityName)) {
            managementChildExtraParams = [:];

            if (UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
                managementChildExtraParams = StringUtil.strToMap(context.managementChildExtraParams);
            }

            keysToRemove = [];
            pkQueryStringMap.each { fieldName, fieldValue ->
                modelKeyMap = null;
                modelRelKeyMap = null;

                if (UtilValidate.isNotEmpty(modelRelation)) {
                    modelKeyMap = modelRelation.findKeyMap(fieldName);
                    modelRelKeyMap = modelRelation.findKeyMapByRelated(fieldName);
                }
                if (UtilValidate.isEmpty(modelRelation) || (UtilValidate.isNotEmpty(modelRelation) && (UtilValidate.isNotEmpty(modelKeyMap) || UtilValidate.isNotEmpty(modelRelKeyMap)))) {
                    managementChildExtraParams[fieldName] = fieldValue;

                    if (UtilValidate.isNotEmpty(modelKeyMap)) {
                        managementChildExtraParams[modelKeyMap.getRelFieldName()] = fieldValue;
                        managementChildExtraParams[modelKeyMap.getFieldName()] = fieldValue;
                    } else if (UtilValidate.isNotEmpty(modelRelKeyMap)) {
                        managementChildExtraParams[modelRelKeyMap.getFieldName()] = fieldValue;
                        managementChildExtraParams[modelRelKeyMap.getRelFieldName()] = fieldValue;
                    }

                    keysToRemove.add(fieldName);
                }
            }

            if(!(managementChildExtraParams instanceof String)) {
                context.managementChildExtraParams = StringUtil.mapToStr(managementChildExtraParams);
            }

            if (UtilValidate.isNotEmpty(keysToRemove)) {
                keysToRemove.each { fieldName ->
                    pkQueryStringMap.remove(fieldName);
                }
            }

//            Debug.log("************************************* paginateManagement.groovy --> managementChildExtraParams=" + managementChildExtraParams)
        }

        //Alla conclusione dell'elaborazione si inserisce nel contesto la stringa corrispondente alla concatenazione della coppia chiave/valire
        //della mappa pkQueryStringMap. Tale stringa verrà usata nelle query string di alcune chiamate (TODO: recuperare dive viene usata)
        if (UtilValidate.isNotEmpty(pkQueryStringMap))
            context.pkQueryString = StringUtil.mapToStr(pkQueryStringMap);
    }
    
    managementFormName = arrayManagementFormName[0];
    managementFormLocation = arrayManagementFormLocation[0];

    //Sulla base del modello di form che si andrà a renderizzare, recupero l'indice della vista
	Debug.log("************************************* paginateManagement.groovy --> managementFormLocation=" + managementFormLocation)
	Debug.log("************************************* paginateManagement.groovy --> managementFormName=" + managementFormName)
	modelForm = null;
	try { 
		modelForm = FormFactory.getFormFromLocation(managementFormLocation, managementFormName, delegator.getModelReader(), dispatcher.getDispatchContext());
		viewIndex = modelForm.getPaginateIndex(context);
	} catch (Exception e) {
		viewIndex = 0;
	}

//    Debug.log("************************************* paginateManagement.groovy --> viewIndex=" + viewIndex)
    if (!(context.listIt instanceof EntityListIterator)) {
        //Nel caso listIt non sia di tipo EntityListIterator, nel contesto inserisco tutta la mappa all'indice currentIndex
        //In questo modo, nel caso di paginazione di dettaglio, ho gia tutti i dati nel contesto
		
		pageSize = 0;
		if (UtilValidate.isNotEmpty(modelForm)) {
			pageSize = modelForm.getPaginateSize(context);
		}
        currentIndex = viewIndex * pageSize;

        tableCookies = parameters.get("tableCookies");
        if (tableCookies) {
            it = tableCookies.keySet().iterator();
            while (it.hasNext()) {
              cookieName = it.next();
              entityFromCookie = cookieName.substring(cookieName.lastIndexOf("_") + 1);

              if (UtilValidate.isNotEmpty(parameters.entityName) && entityFromCookie.equals(parameters.entityName) && UtilValidate.isNotEmpty(parameters.managementFormType) && parameters.managementFormType.equals("multi")) {
                  selectedRowIndex = tableCookies.get(cookieName);
                  currentIndex = currentIndex + new Integer(selectedRowIndex) - 1;
              }
           }
        }

//        Debug.log("************************************* paginateManagement.groovy --> currentIndex=" + currentIndex);
        if (currentIndex < context.listIt.size()) {
            context.putAll(context.listIt[currentIndex]);
        } else {
            context.putAll(context.listIt[context.listIt.size()-1]);
        }
    } else {
        pageSize = modelForm.getPaginateSize(context);
        currentIndex = viewIndex * pageSize+1;

        if (context.listIt.absolute(currentIndex)) {
            firstElement = context.listIt.currentGenericValue();
            if (UtilValidate.isNotEmpty(firstElement)) {
                context.putAll(firstElement);
                context["parent_value_just_populated"] = "true";
            }
        }
    }
} else if (UtilValidate.isNotEmpty(parameters.entityName) && !"Y".equals(parameters.insertMode)) {
    //Nel caso listIt non valorizzato e non insertMode, recupero solo il pkQueryString
    modelEntity = delegator.getModelEntity(parameters.entityName);
    if (UtilValidate.isNotEmpty(modelEntity)) {
        pkFieldsName = modelEntity.getPkFieldNames();  //List<String>

		if (UtilValidate.isNotEmpty(pkFieldsName)) {
	        pkFieldMap = [:];
	        for (i in 0..pkFieldsName.size()-1) {
	            if (UtilValidate.isEmpty(requestParameters["actual_" + pkFieldsName.get(i)])) {
	                requestParameters["actual_" + pkFieldsName.get(i)] = parameters[pkFieldsName.get(i)];
	            }
	            pkFieldMap[pkFieldsName.get(i)] = parameters[pkFieldsName.get(i)];
	        }
	        if (UtilValidate.isNotEmpty(pkFieldMap)) {
	            context.pkQueryString = StringUtil.mapToStr(pkFieldMap, "&");
	        }
		}
    }
}

long endTime = System.currentTimeMillis();
Debug.log("Run groovy paginateManagement.groovy in " + (endTime - startTime) + " milliseconds");
