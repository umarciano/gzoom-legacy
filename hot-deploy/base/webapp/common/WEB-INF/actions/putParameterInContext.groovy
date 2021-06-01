import org.ofbiz.base.util.*;

entityName = parameters.entityName;
if (UtilValidate.isNotEmpty(entityName)) {
    modelEntity = delegator.getModelEntity(entityName);
    if (UtilValidate.isNotEmpty(modelEntity)) {
        modelRelation = null;

        if (UtilValidate.isNotEmpty(parameters.parentEntityName)) {
            relationTitle = UtilValidate.isNotEmpty(context.relationTitle) ? context.relationTitle : parameters.relationTitle;
            if (UtilValidate.isEmpty(relationTitle)) {
                relationTitle = "";
            }
            relationTitle += parameters.parentEntityName;

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

        pkFieldsName = modelEntity.getPkFieldNames();  //List<String>
        allFieldsName = modelEntity.getAllFieldNames();
        if (UtilValidate.isNotEmpty(allFieldsName)) {
            for(fieldName in allFieldsName) {
//                if ("NoteData".equals(entityName)) {
//                    Debug.log("********************************** putParameterInContext.groovy -> parameters.insertMode = " + parameters.insertMode);
//                    Debug.log("********************************** putParameterInContext.groovy -> 1 - context." + fieldName + " = " + context.fieldName);
//                    Debug.log("********************************** putParameterInContext.groovy -> parameters." + fieldName + " = " + parameters.fieldName);
//                }
                if (UtilValidate.isEmpty(context[fieldName])) {
                    fieldNameValue = UtilValidate.isNotEmpty(parameters[fieldName]) ? parameters[fieldName] : "";
                    if(UtilValidate.isNotEmpty(fieldNameValue)) {
                        //nel caso di insertMode nel ContextLink vengono copiati tutti i campi della entity
                        if("Y".equals(parameters.insertMode)) {
//                            if ("NoteData".equals(entityName)) {
//                                Debug.log("********************************** putParameterInContext.groovy -> pkFieldsName.contains(" + fieldName + ") = " + pkFieldsName.contains(fieldName));
//                            }
                            if(!pkFieldsName.contains(fieldName) || (UtilValidate.isNotEmpty(modelRelation) && (UtilValidate.isNotEmpty(modelRelation.findKeyMap(fieldName)) || UtilValidate.isNotEmpty(modelRelation.findKeyMapByRelated(fieldName))))) {
                                context[fieldName] = fieldNameValue;
                            }
//                            Debug.log("********************************** putParameterInContext.groovy -> 2 - context." + fieldName + " = " + context.fieldName);
                        } else {
                            context[fieldName] = fieldNameValue;
                        }
                    }
                } else {
                    if("Y".equals(parameters.insertMode) && pkFieldsName.contains(fieldName)) {

//                        if ("NoteData".equals(entityName)) {
//                            Debug.log("********************************** putParameterInContext.groovy -> Sono in insertModel e il campo " + fieldName + " è un campo chiave");
//                        }

                        if (UtilValidate.isEmpty(parameters.parentEntityName)) {
//                            if ("NoteData".equals(entityName)) {
//                                Debug.log("********************************** putParameterInContext.groovy -> Il parentEntityName non è nei parametri quindi elimino il campo dal context e dal parameters");
//                            }
                            context.remove(fieldName);
                            parameters.remove(fieldName);
                            context.remove("pkQueryString");
                        } else {
                            if (UtilValidate.isNotEmpty(modelRelation)) {
                                if (!(UtilValidate.isNotEmpty(modelRelation.findKeyMap(fieldName)) || UtilValidate.isNotEmpty(modelRelation.findKeyMapByRelated(fieldName)))) {
                                    context.remove(fieldName);
                                    parameters.remove(fieldName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//if ("NoteData".equals(entityName)) {
//    Debug.log("****************************** putParametersInContext.groovy -> prima della gestione managementChildExtraParams context.noteId = " + context.noteId);
//}

if (UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
    extraParams = StringUtil.strToMap(context.managementChildExtraParams);

//    if ("NoteData".equals(entityName)) {
//        Debug.log("****************************** putParametersInContext.groovy -> extraParams = " + extraParams);
//    }

    extraParams.each { fieldName, fieldValue ->
        if (UtilValidate.isEmpty(context[fieldName])) {
            context[fieldName] = fieldValue;
        }
    }
}

//if ("NoteData".equals(entityName)) {
//    Debug.log("****************************** putParametersInContext.groovy -> dopo la gestione managementChildExtraParams context.noteId = " + context.noteId);
//}