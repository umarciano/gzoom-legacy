package com.mapsengineering.base.bl.crud;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.util.MessageUtil;

public class PkValidationHandler extends AbstractCrudHandler {

    protected boolean doExecution() {

        if (Operation.READ.equals(operation)) {
            return true;
        }

        try {
            Map<String, Object> keysMap = FastMap.newInstance();
            List<String> pkList = modelEntity.getPkFieldNames();

            //Se sono in creazione e ho il flag attivo di creazione automatica,
            //allora non segnalo errore e creo le chiavi nel modulo OperationCrudHandler
            if ((Operation.CREATE.equals(operation))&&(parameters.containsKey(AbstractCrudHandler.AUTOMATIC_PK))) {
                return true;
            }

            //Controllo esistenza nei parametri di tutti i campi chiave
            //e costruzione mappa delle sole chiavi pk
            if (Operation.CREATE.equals(operation)||Operation.DELETE.equals(operation)||Operation.UPDATE.equals(operation)) {

                for (String name: pkList) {
                    if (!parameters.containsKey(name)) {
                        Debug.logWarning(MessageUtil.getErrorMessage("PrimaryKeyNeeded", locale, UtilMisc.toList(name)) + " for " + modelEntity.getEntityName(), null);
                        returnMap.putAll(MessageUtil.buildErrorMap("PrimaryKeyNeeded", locale, UtilMisc.toList(name)));
                        return false;
                    }
                    keysMap.put(name, modelEntity.convertFieldValue(modelEntity.getField(name), parameters.get(name), delegator, UtilMisc.toMap("locale", locale, "timeZone", timeZone)));
                }

            }

            //Controllo chiavi duplicate
            if (Operation.CREATE.equals(operation)) {

                GenericValue gv = delegator.findOne(entityName, keysMap, false);
                if (UtilValidate.isNotEmpty(gv)) {
                    Debug.logWarning(MessageUtil.getErrorMessage("DuplicatePrimaryKey", locale) + ": For " + entityName + " key = " + keysMap, null);
                    returnMap.putAll(MessageUtil.buildErrorMap("DuplicatePrimaryKey", locale));
                    return false;
                }

            }

        } catch (GenericEntityException e) {
            returnMap.putAll(MessageUtil.buildErrorMap("GenericServiceError", e, locale, UtilMisc.toList("BaseCrud")));
            return false;
        }

        return true;
    }

}
