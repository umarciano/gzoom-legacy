package com.mapsengineering.base.bl.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Handler for CRUD operation management.
 * <blockquote>
 * <b>It stores operation results (like GenericValues got from db) into _GET_VALUES_LIST_ parameter into context.</b>
 * <blockquote>
 * @author sandro
 *
 */
public class OperationCrudHandler extends AbstractCrudHandler {

    /**
     * Parameter name for operations return values
     */
    public static final String RET_VALUES = "retValues";
    
    /**
     * Parameter name for operation return in case of READ with PK
     */
    public static final String RET_VALUE = "retValue";
    /**
     * Parameter name for return id
     */
    public static final String ID = "id";

    
    /**
     * Set All field operation
     */
    private GenericValue setAllFields(Map<String, Object> parameters) {
    	GenericValue gv = delegator.makeValue(entityName);
    	
        ModelEntity me = delegator.getModelEntity(entityName);
        Iterator<String> itL = (me.getAllFieldNames()).iterator();
        while (itL.hasNext()) {
        	String fieldName = itL.next();
            if (!parameters.containsKey(fieldName)) {
                continue;
            }
            Object fieldValue = null;
            ModelField mf = me.getField(fieldName);
            try {
                ModelFieldType mft = delegator.getEntityFieldType(me, mf.getType());
                fieldValue = ObjectType.simpleTypeConvert(parameters.get(fieldName), mft.getJavaType(), null, locale);
            } catch (GenericEntityException e) {
                fieldValue = parameters.get(fieldName);
            } catch (GeneralException e) {
                fieldValue = parameters.get(fieldName);
            }
            gv.set(fieldName, fieldValue);
        }
    	
    	return gv;
    }
    /**
     * Create operation
     */
    protected void doCreate() {

        try {

            GenericValue gv = delegator.makeValue(entityName);

            //Genero le chiavi automatiche se richiesto
            if (parameters.containsKey(AbstractCrudHandler.AUTOMATIC_PK) && UtilValidate.isNotEmpty(parameters.get(AbstractCrudHandler.AUTOMATIC_PK))) {

                //Rimuovo innanzitutto il flag
                parameters.remove(AbstractCrudHandler.AUTOMATIC_PK);
                //Set new key
                String newPk = delegator.getNextSeqId(entityName);
                String pkName = modelEntity.getFirstPkFieldName();
                parameters.put(pkName, newPk);
                gv = this.setAllFields(parameters);
            } else {
            	  gv = this.setAllFields(parameters);	
            }

            gv = delegator.create(gv);

            //setting return values
            Map<String, Object> retValue = FastMap.newInstance();
            if (UtilValidate.isNotEmpty(gv)) {
                GenericPK pk = gv.getPrimaryKey();
                if (UtilValidate.isNotEmpty(pk)) {
                    retValue = pk.getAllFields();
                }
            }
            returnMap.put(OperationCrudHandler.ID, retValue);

        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("CrudExecutionError", e, locale, UtilMisc.toList("Create", entityName)));
        }
    }

    /**
     * Update operation
     */
    protected void doUpdate() {
        try {

            GenericValue gv = delegator.makeValue(entityName);
            gv = this.setAllFields(parameters);	
            //Check if record is reserved
            if (ValidationUtil.checkIsReserved(delegator, gv)) {
                returnMap.putAll(MessageUtil.buildErrorMap("ErrorRecordReserved", locale, UtilMisc.toList("Update")));
                return;
            }

            delegator.store(gv, true);

        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("CrudExecutionError", e, locale, UtilMisc.toList("Update", entityName)));
        }
    }

    /**
     * Read operation.
     * <blockquote>
     * <b>Stores got values into context with _GET_VALUES_LIST_ parameter.</b>
     * </blockquote>
     */
    protected void doRead() {
        try {
        	List<GenericValue> foundValues = null;
        	Map<String, Object> pkMap = null;
        	for (String pkField : delegator.getModelEntity(entityName).getPkFieldNames()) {
        		if (parameters.containsKey(pkField)) {
        			if (UtilValidate.isEmpty(pkMap)) {
        				pkMap = new HashMap<String, Object>();
        			}
        			pkMap.put(pkField, parameters.get(pkField));
        		} else {
        			pkMap = null;
        			break;
        		}
        	}
        	
        	if (UtilValidate.isEmpty(pkMap)) {
        		foundValues = delegator.findList(entityName, EntityCondition.makeCondition(parameters), null, null, null, true);
        	} else {
        		foundValues = new ArrayList<GenericValue>();
        		GenericValue foundValue = delegator.findOne(entityName, pkMap, false);
        		if (UtilValidate.isNotEmpty(foundValue)) {
        			returnMap.put(OperationCrudHandler.RET_VALUE, foundValue);
        			foundValues.add(foundValue);
        		}
        	}
            returnMap.put(OperationCrudHandler.RET_VALUES, foundValues);
        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("CrudExecutionError", e, locale, UtilMisc.toList("Read", entityName)));
        }
    }

    /**
     * Delete operation
     */
    protected void doDelete() {
        try {

            GenericValue gv = delegator.makeValue(entityName);

            gv = this.setAllFields(parameters);	
            //Check record reserved
            if (ValidationUtil.checkIsReserved(delegator, gv)) {
                returnMap.putAll(MessageUtil.buildErrorMap("ErrorRecordReserved", locale, UtilMisc.toList("Delete")));
                return;
            }

            delegator.removeValue(gv);
        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("CrudExecutionError", e, locale, UtilMisc.toList("Delete", entityName)));
        }
    }

    protected boolean doExecution() {
        try {
            switch (operation) {
            case CREATE:
                doCreate();
                break;
            case READ:
                doRead();
                break;
            case UPDATE:
                doUpdate();
                break;
            case DELETE:
                doDelete();
                break;
            }
        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("GenericErrorCrudOperation", e, locale));
            return false;
        }
        return true;
    }

}
