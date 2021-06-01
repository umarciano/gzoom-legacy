import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import com.mapsengineering.base.util.*;

//Debug.log("***************************************** putInputFieldInContext.groovy");
//Debug.log("**************************************** putInputFieldInContext.groovy -> parameters.insertMode = " + parameters.insertMode)

modelEntity = null;
modelRelation = null;
inputFields = null;
Debug.log("***************************************** putInputFieldInContext.groovy -> context.parentEntityName = " + context.parentEntityName );

if (UtilValidate.isNotEmpty(context.parentEntityName) && (UtilValidate.isEmpty(context.noParentEntityName) || "N".equals(context.noParentEntityName))) {
    modelEntity = delegator.getModelEntity(context.entityName);

    if (UtilValidate.isNotEmpty(modelEntity)) {
        relationTitle = UtilValidate.isNotEmpty(context.relationTitle) ? context.relationTitle : parameters.relationTitle;
        if (UtilValidate.isEmpty(relationTitle)) {
            relationTitle = "";
        }
        relationTitle += context.parentEntityName;

        modelRelation = modelEntity.getRelation(relationTitle);
        
        inputFields = [:];

        if (UtilValidate.isEmpty(modelRelation)) {
            for (m in modelEntity.getRelationsOneList()) {
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
}
//Debug.log("***************************************** putInputFieldInContext.groovy -> modelRelation = " + modelRelation );
//Debug.log("***************************************** putInputFieldInContext.groovy -> parameters.id = " + parameters.id );
//Debug.log("***************************************** putInputFieldInContext.groovy -> parameters.operation = " + parameters.operation );
//Debug.log("***************************************** putInputFieldInContext.groovy -> parameters.insertMode = " + parameters.insertMode );

if (UtilValidate.isNotEmpty(parameters.id) && !"DELETE".equals(parameters.operation) && (/*!"Y".equals(parameters.insertMode) ||*/ !"Y".equals(context.cleanedIdAfterDelete))) {
    //Debug.log("***************************************** putInputFieldInContext.groovy -> 1 = " + parameters.insertMode );
    if (parameters.id.startsWith("["))
        parameters.id = parameters.id.substring(1);
    if (parameters.id.endsWith("]"))
        parameters.id = parameters.id.substring(0, parameters.id.length()-1);

    idMap = StringUtil.strToMap(parameters.id);
    if (UtilValidate.isNotEmpty(idMap)) {
        /*if (!"Y".equals(parameters.insertMode)) {
            context.putAll(idMap);
        } else {*/
            idMap.each { fieldName, fieldValue ->
                //Debug.log("***************************************** putInputFieldInContext.groovy -> 1 = " + parameters.insertMode );
                if (UtilValidate.isNotEmpty(modelRelation) && (UtilValidate.isNotEmpty(modelRelation.findKeyMap(fieldName)) || UtilValidate.isNotEmpty(modelRelation.findKeyMapByRelated(fieldName)))) {
                //Debug.log("***************************************** putInputFieldInContext.groovy -> 1 = " + parameters.insertMode );
                   context[fieldName] = fieldValue;
                }
            }
        //}
    }
}

if (UtilValidate.isNotEmpty(context.entityName)) {
    //Debug.log("**** context.entityname = " + context.entityName);
    fromDelete = parameters.fromDelete;
    if (UtilValidate.isEmpty(fromDelete))
        fromDelete = "N";

    if (UtilValidate.isNotEmpty(modelEntity) && "Y".equals(fromDelete)) {
        pkFieldsName = modelEntity.getPkFieldNames();

        for(pkField in pkFieldsName) {
            if (UtilValidate.isNotEmpty(modelRelation) && (UtilValidate.isEmpty(modelRelation.findKeyMap(pkField)) && UtilValidate.isEmpty(modelRelation.findKeyMapByRelated(pkField)))) {
                parameters.remove(pkField);
            }
        }
    }
    //Debug.log("***************************************** putInputFieldInContext.groovy -> context.parentEntityName = " + context.parentEntityName );
    //Debug.log("***************************************** putInputFieldInContext.groovy -> modelRelation = " + modelRelation );

    if (UtilValidate.isNotEmpty(context.parentEntityName) && !("N".equals(context.executePerformFind))) {
        if (UtilValidate.isNotEmpty(modelRelation)) {
//            Debug.log("***************************************** putInputFieldInContext.groovy -> modelRelation.name = " + modelRelation.getCombinedName());
            for (modelKeyMap in modelRelation.getKeyMapsClone()) {
                parameterName = modelKeyMap.getFieldName();
                parameterRelFieldName = modelKeyMap.getRelFieldName();

                parameterValue = UtilValidate.isNotEmpty(context[parameterName]) ? context[parameterName] : parameters[parameterName];

//                Debug.log("***************************************** putInputFieldInContext.groovy -> " + parameterName + " = " + parameterValue);

                if (UtilValidate.isEmpty(parameterValue)) {
                    parameterValue = UtilValidate.isNotEmpty(context[parameterRelFieldName]) ? context[parameterRelFieldName] : parameters[parameterRelFieldName];
                }
                if (UtilValidate.isNotEmpty(parameterValue)) {
                    inputFields[parameterName] = parameterValue;
                }
            }
            if("Y".equals(parameters.detail) && UtilValidate.isNotEmpty(parameters.id) && !"Y".equals(fromDelete)) {
                idMap = StringUtil.strToMap(parameters.id);
                inputFields.putAll(idMap);
                //context.putAll(idMap);
            }
             

            childFilter = UtilValidate.isEmpty(context.childFilter) ? parameters.childFilter : context.childFilter;
            if (UtilValidate.isNotEmpty(childFilter)) {
                childFilterParams = StringUtil.strToMap(childFilter);
                inputFields.putAll(childFilterParams);
            }

            //Debug.log("***************************************** putInputFieldInContext.groovy -> inputFields = " + inputFields);
            //managementChildExtraParams contiene i parametri extra non solo nel caso del management ma anche nel caso del contextLink
            //Debug.log("***************************************** putInputFieldInContext.groovy -> context.managementChildExtraParams = " + context.managementChildExtraParams);

            if (UtilValidate.isNotEmpty(inputFields)) {
                managementChildExtraParams = [:];
                if (UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
                    managementChildExtraParams = StringUtil.strToMap(context.managementChildExtraParams);
                }

                inputFields.each { fieldName, fieldValue ->
                    modelKeyMap = modelRelation.findKeyMap(fieldName);
                    modelRelKeyMap = modelRelation.findKeyMapByRelated(fieldName);

                    if (UtilValidate.isEmpty(modelRelation) || (UtilValidate.isNotEmpty(modelRelation) && (UtilValidate.isNotEmpty(modelKeyMap) || UtilValidate.isNotEmpty(modelRelKeyMap)))) {
                        //Debug.log("inserisce manag extra "+ fieldName + " = " + fieldValue );
                        strFieldValue = ObjectType.simpleTypeConvert(fieldValue, "java.lang.String", null, locale);

                        if (UtilValidate.isNotEmpty(modelKeyMap)) {
                            managementChildExtraParams[modelKeyMap.getRelFieldName()] = strFieldValue;
                            managementChildExtraParams[modelKeyMap.getFieldName()] = strFieldValue;
                        } else if (UtilValidate.isNotEmpty(modelRelKeyMap)) {
                            managementChildExtraParams[modelRelKeyMap.getFieldName()] = strFieldValue;
                            managementChildExtraParams[modelRelKeyMap.getRelFieldName()] = strFieldValue;
                        }
                    }
                }
                //Debug.log("***************************************** putInputFieldInContext.groovy -> managementChildExtraParams = " + managementChildExtraParams);
                // se managementChildExtraParams � gia di tipo String il metodo da problemi
                if(!(managementChildExtraParams instanceof String)) {
                   context.managementChildExtraParams = StringUtil.mapToStr(managementChildExtraParams);
                }
//                Debug.log("***************************************** putInputFieldInContext.groovy -> context.managementChildExtraParams = " + context.managementChildExtraParams);
            }
        }
        context.inputFields = inputFields;
		
		if("Y".equals(parameters.detail) && UtilValidate.isNotEmpty(parameters.id) && "Y".equals(fromDelete) && !"multi".equals(context.managementFormType) && !"list".equals(context.managementFormType)) {
			parameters.insertMode = "Y";
		}
    }
}

//Debug.log("***************************************** putInputFieldInContext.groovy -> context.managementFormType = " + context.managementFormType );

////Debug.log("***************************************** putInputFieldInContext.groovy -> parameters.uomRangeValuesId = " + parameters.uomRangeValuesId );
////Debug.log("***************************************** putInputFieldInContext.groovy -> context.uomRangeValuesId = " + context.uomRangeValuesId);
