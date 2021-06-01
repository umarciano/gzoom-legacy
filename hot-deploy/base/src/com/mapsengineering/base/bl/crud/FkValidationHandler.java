package com.mapsengineering.base.bl.crud;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelRelation;

import com.mapsengineering.base.util.MessageUtil;

public class FkValidationHandler extends AbstractCrudHandler {

    protected boolean doExecution() {

        if (Operation.READ.equals(operation)||Operation.DELETE.equals(operation)) {
            return true;
        }

        /*
         * Le relazioni che prendo in considerazione sono solamente il tipo 'one' (fk-pk)
         * Questo perché il tipo 'many' (pk-fk) implica relazioni con fk della tabella relata, le quali possono anche non esserci.
         * Invece il tipo 'one-nofk' (pk-pk) implica una relazione tra tra la pk ed una pk della tabella relata, e le pk
         * non le prendiamo in considerzione in questo metodo.
         */
        Iterator<ModelRelation>	it = modelEntity.getRelationsIterator();
        while (it.hasNext()) {
            ModelRelation mr = it.next();

            //Controllo tipo di relazione
            if (!mr.getType().equalsIgnoreCase("one")) {
                continue;
            }

            //Entity relazionata
            String relEntityName = mr.getRelEntityName();
            //Mappa per le key della releazione
            Map<String, Object> fkMap = FastMap.newInstance();
            //Mappa per i campi fk mancanti
            List<String> fkFault = FastList.newInstance();

            //Ciclo key della relazione
            ModelEntity me = delegator.getModelEntity(mr.getRelEntityName());
            
            Iterator<ModelKeyMap> mkIt = mr.getKeyMapsIterator();
            while (mkIt.hasNext()) {
                ModelKeyMap mkm = mkIt.next();
                String fieldName = mkm.getFieldName();
                String relFieldName = mkm.getRelFieldName();
                //Se uno dei campi della relazione salto
                if (parameters.containsKey(fieldName) && UtilValidate.isEmpty(parameters.get(fieldName))) {
                    fkFault.add(MessageUtil.getFieldName(fieldName, entityName, locale)); //Add field for error message
                    continue;
                }
                //Aggiungo alla map
                Object fieldValue = null;
                ModelField mf = me.getField(relFieldName);
                try {
                    ModelFieldType mft = delegator.getEntityFieldType(me, mf.getType());
                    fieldValue = ObjectType.simpleTypeConvert(parameters.get(fieldName), mft.getJavaType(), null, locale);
                } catch (GenericEntityException e) {
                    fieldValue = parameters.get(fieldName);
                } catch (GeneralException e) {
                    fieldValue = parameters.get(fieldName);
                }
                
                if (UtilValidate.isNotEmpty(fieldValue))
                	fkMap.put(relFieldName, fieldValue);
            }

            //Importante: Controllo, se esiste almeno uno dei campi della relazione nei parametri,
            //allora devo esistere tutti
            if (fkMap.isEmpty()) {
                continue; //Non ne esiste nessuno, vado a prox. relazione
            }

            //Discordanza tra campi attesi e presenti nella Fk.
            if (fkMap.size()!=mr.getKeyMapsSize()) {
                Debug.logWarning(MessageUtil.getErrorMessage("ForeignKeyNotComplete", locale, UtilMisc.toList(StringUtil.join(fkFault, ","), relEntityName, fkMap.toString())), null);
                returnMap.putAll(MessageUtil.buildErrorMap("ForeignKeyNotComplete", locale, UtilMisc.toList(StringUtil.join(fkFault, ","), relEntityName, fkMap.toString())));
                return false;
            }

            //Chiave completa
            try {
                long count = delegator.findCountByCondition(relEntityName, EntityCondition.makeCondition(fkMap), null, null);
                if (count==0) {
                    //Key not found
                    //Get descriptive field name
                    List<String> names = FastList.newInstance();
                    for (String fName: fkMap.keySet()) {
                        names.add(MessageUtil.getFieldName(fName, entityName, locale));
                    }
                    Debug.logWarning(MessageUtil.getErrorMessage("ForeignKeyDontExist", locale,
                            UtilMisc.toList(StringUtil.join(names, ","), relEntityName, fkMap.toString())), null);
                    returnMap.putAll(MessageUtil.buildErrorMap("ForeignKeyDontExist", locale,
                            UtilMisc.toList(StringUtil.join(names, ","), relEntityName, fkMap.toString())));
                    return false;
                }
            } catch (Exception e) {
                returnMap.putAll(MessageUtil.buildErrorMap("ForeignKeyCheckExec", e, locale));
                return false;
            }

        } //end while

        return true;
    }

}
