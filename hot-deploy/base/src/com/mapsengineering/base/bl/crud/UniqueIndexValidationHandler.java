package com.mapsengineering.base.bl.crud;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelIndex;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.util.MessageUtil;

public class UniqueIndexValidationHandler extends AbstractCrudHandler {

    protected boolean doExecution() {

        if (Operation.READ.equals(operation)) {
            return true;
        }

        try {
            //Controllo validazione indici univoci
            if (Operation.CREATE.equals(operation)||Operation.UPDATE.equals(operation)) {
                Iterator<ModelIndex> it = modelEntity.getIndexesIterator();
                while (it.hasNext()) {
                    ModelIndex index = it.next();
                    if (index.getUnique()) {
                        Map<String, Object> indexMap = new FastMap<String, Object>();

                        Iterator<String> fieldIt = index.getIndexFieldsIterator();
                        while (fieldIt.hasNext()) {
                            String name = fieldIt.next();
                            if (parameters.containsKey(name)) {
                                indexMap.put(name, modelEntity.convertFieldValue(modelEntity.getField(name), parameters.get(name), delegator, UtilMisc.toMap("locale", locale, "timeZone", timeZone)));
                            } else {
                                indexMap.put(name, null);
                            }
                        }

                        if (UtilValidate.isNotEmpty(indexMap)) {
                            EntityCondition condition = EntityCondition.makeCondition(indexMap);

                            if (Operation.UPDATE.equals(operation)) {
                                List<String> pkList = modelEntity.getPkFieldNames();

                                List<EntityCondition> notEqualsPkConditionList = new FastList<EntityCondition>();

                                for (String pk : pkList) {
                                    notEqualsPkConditionList.add(EntityCondition.makeCondition(pk, EntityOperator.NOT_EQUAL, parameters.get(pk)));
                                }

                                if (UtilValidate.isNotEmpty(notEqualsPkConditionList)) {
                                    EntityCondition notEqualsPkCondition = EntityCondition.makeCondition(notEqualsPkConditionList);

                                    condition = EntityCondition.makeCondition(condition, notEqualsPkCondition);
                                }
                            }



                            List<GenericValue> result = delegator.findList(entityName, condition, null, null, null, false);
                            if (UtilValidate.isNotEmpty(result)) {
                                String fields = StringUtil.join(UtilMisc.toList(indexMap.keySet()), ", ");
                                // null return false
                                if (!parameters.containsKey(AbstractCrudHandler.THROW_ERROR) ||  Boolean.TRUE.equals((Boolean) parameters.get(AbstractCrudHandler.THROW_ERROR))) {
                                    returnMap.putAll(MessageUtil.buildErrorMap("DuplicateUniqueIndex", locale, UtilMisc.toList(fields)));
                                    return false;
                                }
                                String message = MessageUtil.getErrorMessage("DuplicateUniqueIndex", locale, UtilMisc.toList(fields));
                                returnMap.put(ModelService.FAIL_MESSAGE, message);
                                parameters.put(ModelService.FAIL_MESSAGE, message);
                                return false;
                            }
                        }
                    }
                }

            }

        } catch (GenericEntityException e) {
            returnMap.putAll(MessageUtil.buildErrorMap("GenericServiceError", e, locale, UtilMisc.toList("BaseCrud")));
            return false;
        }

        return true;
    }

}
