import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

/*
    Sandro: 29/07/2009
    A differenza dello script putInputFieldInContext, qui prendo tutti i campi chiave di entityName
    e li ricerco nel contesto, andando a settarli dentro inputFields.
    Oltre a questi aggiungo la possibilità di impostare altri campi di where aggiuntivi tramite apposito parametro
*/

if (UtilValidate.isNotEmpty(context.entityName) && !("N".equals(context.executePerformFind))) {

    modelEntity = delegator.getModelEntity(context.entityName);
    inputFields = [:];

    if (UtilValidate.isNotEmpty(modelEntity)) {

        //
        //Se richiesto estraggo i campi chiave da quelli passati non uso tutti i campi key dell'entity (',' separated list of keys name).
        //Inoltre, in questo caso, non cerco tra i parametri ma solo nel contesto (é appunto una forzatura)
        //
        if (UtilValidate.isNotEmpty(context.forceKeyFields)) {
            keyFields = org.ofbiz.base.util.StringUtil.split(context.forceKeyFields, ",");
            if (UtilValidate.isNotEmpty(keyFields)) {
                for (keyName in keyFields) {
                    keyValue = UtilValidate.isNotEmpty(context[keyName]) ? context[keyName] : "";
                    if (UtilValidate.isNotEmpty(keyValue)) {
                        keyValue = org.ofbiz.base.util.ObjectType.simpleTypeConvert(keyValue, "String",	null, locale);
                        inputFields[keyName] = keyValue;
                    }
                }
            }
        } else {
            //
            //Altrimenti funzionamento normale
            //
            fieldList = modelEntity.getPkFieldNames();
            for (fieldName in fieldList) {
                fieldValue = UtilValidate.isNotEmpty(context[fieldName]) ? context[fieldName] : parameters[fieldName];
                if (UtilValidate.isNotEmpty(fieldValue)) {
                    fieldValue = org.ofbiz.base.util.ObjectType.simpleTypeConvert(fieldValue, "String",	null, locale);
                    inputFields[fieldName] = fieldValue;
                }
            }
        }

        //
        //Added fields for where clause (',' separated list of key=value pairs)
        //
        if (UtilValidate.isNotEmpty(context.whereFields)) {
            whereFields = org.ofbiz.base.util.StringUtil.split(context.whereFields, ",");
            if (UtilValidate.isNotEmpty(whereFields)) {
                for (pair in whereFields) {
                    //detach key and value
                    keyValue = StringUtil.split(pair, "=");
                    if (keyValue.size()==2) {
                        fieldName = keyValue[0];
                        fieldValue = keyValue[1];
                          inputFields[fieldName] = fieldValue;
                    }
                }
            }
        }

        //
        //managementChildExtraParams contiene i parametri extra non solo nel caso del management ma anche nel caso del contextLink
        //
        if (UtilValidate.isNotEmpty(inputFields)) {
            if (UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
                managementChildExtraParams = StringUtil.strToMap(context.managementChildExtraParams);
                //context.managementChildExtraParams += "&";
                managementChildExtraParams.putAll(inputFields);
                context.managementChildExtraParams = StringUtil.mapToStr(managementChildExtraParams);
            } else {
                context.managementChildExtraParams = StringUtil.mapToStr(inputFields);
            }
        }
    }
    context.inputFields = inputFields;
}
