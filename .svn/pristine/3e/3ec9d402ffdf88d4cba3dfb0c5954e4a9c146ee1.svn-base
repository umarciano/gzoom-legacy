package com.mapsengineering.base.bl.crud;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.bl.validation.FieldResolver;
import com.mapsengineering.base.bl.validation.ResolverFactory;
import com.mapsengineering.base.util.MessageUtil;

public class TypeValidationHandler extends AbstractCrudHandler {

    protected boolean doExecution() {
        List<String> listFieldsName = modelEntity.getAllFieldNames();
        List<String> listPkFieldsName = modelEntity.getPkFieldNames();
        boolean isAutoPk = "Y".equals(parameters.get(TypeValidationHandler.AUTOMATIC_PK));
        
        for (String fieldName: listFieldsName) {

            ModelField field = modelEntity.getField(fieldName);

        	//Controllo se sono in creazione con chiavi automatiche
        	if (listPkFieldsName.contains(fieldName) && UtilValidate.isEmpty(parameters.get(fieldName))) {
        		if (Operation.CREATE.equals(operation) && isAutoPk) {
        			continue;
        		}
        	}
            
            if(!Operation.DELETE.equals(operation) || listPkFieldsName.contains(fieldName)) {
                //Verifico esistenza del campo nella map parametri
                if (UtilValidate.isEmpty(parameters.get(fieldName))) {
                    //Controllo obbligatorietà
                    if (field.getIsNotNull()) {
                        String fLabel = MessageUtil.getFieldName(field, locale);
                        returnMap.putAll(MessageUtil.buildErrorMap("MandatoryField", locale, UtilMisc.toMap("fieldName", fLabel)));
                        return false;
                    }
                    continue;
                }
            }
            
            //Controllo non modificabilità (solo update)
            if (Operation.UPDATE.equals(operation)) {
                //Controllo che il campo non sia una chiave primaria
                if (!field.getIsPk()) {
                    //In questo caso é stato deciso di non segnalare errore ma non
                    //modificare il campo
                    if (!field.getIsUpdatable()) {
                        parameters.remove(fieldName);
                        continue;
                    }
                }
            }

            if(!Operation.DELETE.equals(operation) || listPkFieldsName.contains(fieldName)) {
                //Parametro da esaminare
                Object parm  = parameters.get(fieldName);
                try {
                    //Get field model del campo atteso
                    ModelFieldType mft = delegator.getEntityFieldType(modelEntity, field.getType());
    
                    //Istanza della classe di conversione del campo
                    FieldResolver fr = null;
                    try {
                        fr = ResolverFactory.getInstance(mft);
                    } catch (Exception e) {
                        returnMap.putAll(MessageUtil.buildErrorMap("ExpectedFieldNotKnown", e, locale, UtilMisc.toList(mft.getJavaType(), entityName)));
                        return false;
                    }
    
                    //Validazione e conversione nel tipo campo atteso
                    Object converted = fr.resolve(fieldName, parm, mft, returnMap, locale);
                    if (converted==null) {
                        return false; //La error map in questo caso é già valorizzata dal metodo resolve
                    }
    
                    parameters.put(fieldName, converted);
    
                } catch (Exception e) {
                    returnMap.putAll(MessageUtil.buildErrorMap("ValidationTypeError", e, locale, UtilMisc.toList(entityName)));
                    return false;
                }
            }
        }//end for

        return true;
    }

}
