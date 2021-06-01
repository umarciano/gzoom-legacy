import org.ofbiz.base.util.*;

Debug.log("************************* entityOne.groovy --> context.entityName=" + context.entityName + " with context.parentEntityName=" + context.parentEntityName + ", context.useCache= " + context.useCache);

//Sandro:
// Aggiunto parametro per caricare le entity non dalla cache
// (Se non esiste default = false, diversamente da come in origine)
useCache = "Y".equals(parameters.useCache) ? true : false;

entityName = null;
if (UtilValidate.isNotEmpty(context.parentEntityName))
    entityName = context.parentEntityName
// se ho selezionato la copia insertMode = Y ma ho bisogno di fare un'entityOne
else //if (!"Y".equals(parameters.insertMode))
    entityName = context.entityName;

if (UtilValidate.isNotEmpty(entityName)) {
    modelEntity = delegator.getModelEntity(entityName);
    if (UtilValidate.isNotEmpty(modelEntity)) {
    	if (UtilValidate.isEmpty(context["parent_value_just_populated"]) || !context["parent_value_just_populated"]) {
            pkFieldsFromRequest = [:];
            pkFieldsName = modelEntity.getPkFieldNames();  //List<String>

            pkMapValues = [:];
            
            if("Y".equals(parameters.detail) && UtilValidate.isNotEmpty(parameters.id)) {
                idMap = StringUtil.strToMap(parameters.id);
                context.putAll(idMap);
            }    
            
            for(pkField in pkFieldsName) {
                pkModelField = modelEntity.getField(pkField);
                pkFieldType = delegator.getEntityFieldType(modelEntity, pkModelField.getType());

                pkFieldFromParameter = UtilValidate.isNotEmpty(context[pkField]) ? context[pkField] : parameters[pkField];

                //Potrebbe arrivasrmi in formato [10000|10000|10000] in caso di selezione multipla di child
                //Cerco di trasformarla in list e prendere solo il primo valore
                try {
                    pkFieldFromParameter = StringUtil.toList(pkFieldFromParameter, "\\|")[0];
                } catch (Exception e) {

                }

                if (UtilValidate.isNotEmpty(pkFieldFromParameter)) {
                    try {
                        pkMapValues[pkField] = ObjectType.simpleTypeConvert(pkFieldFromParameter, pkFieldType.getJavaType(), null, context.locale, true);
                    } catch(IllegalArgumentException e) {
                    } catch(GeneralException e) {
                    }
                }
            }
//            Debug.log("************************* entityOne.groovy --> pkMapValues=" + pkMapValues);
            if (UtilValidate.isNotEmpty(pkMapValues) && pkMapValues.size() == pkFieldsName.size()) {
                //Sandro (vedi sopra) value = delegator.findOne(entityName, pkMapValues, true);
                value = delegator.findOne(entityName, pkMapValues, useCache);
                if (UtilValidate.isNotEmpty(value)) {
//                    Debug.log("************************* entityOne.groovy --> value=" + value);
                    context.putAll(value);
                }
            }
        }
    }
}