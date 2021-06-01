package com.mapsengineering.base.standardimport;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.FieldConfig;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: rime
 * Date: 11/25/13
 * Time: 5:03 PM
 */
public class FieldConfigService implements FieldConfig {

    private static final String MODULE = FieldConfigService.class.getName();

    public static final String STANDARD_IMPORT_FIELD_CONFIG = "StandardImportFieldConfig";
    public static final String DATA_SOURCE_ID = "dataSourceId";
    public static final String STANDARD_INTERFACE = "standardInterface";
    public static final String INTERNAL_FIELD_NAME = "internalFieldName";
    public static final String EXTERNAL_FIELD_NAME = "externalFieldName";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String INTERFACE_SEQ = "interfaceSeq";
    
    public static final Long DEFAULT_INTERFACE_SEQ = 1L;


    private Delegator delegator;

    /**
     * Constructor
     * @param context
     */
    public FieldConfigService(DispatchContext dctx) {
        this.delegator = dctx.getDelegator();
    }

    @Override
    public Map<String, Map<String, String>> getMapValue(String standardInterface, String dataSource, Long interfaceSeq) {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        try {
        	List<EntityExpr> conditions = new ArrayList<EntityExpr>();
	    	conditions.add(EntityCondition.makeCondition(STANDARD_INTERFACE, standardInterface));
	    	conditions.add(EntityCondition.makeCondition(DATA_SOURCE_ID, dataSource));
	    	conditions.add(EntityCondition.makeCondition(INTERFACE_SEQ, interfaceSeq));
	    	conditions.add(EntityCondition.makeCondition(DEFAULT_VALUE, EntityOperator.NOT_EQUAL, null));
	    	conditions.add(EntityCondition.makeCondition(INTERNAL_FIELD_NAME, EntityOperator.NOT_EQUAL, null));
	    	EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	    	
	    	List<GenericValue> listFieldConfig = getGenericValues(ecl, UtilMisc.toSet(INTERNAL_FIELD_NAME, DEFAULT_VALUE, DATA_SOURCE_ID));
	        if (UtilValidate.isEmpty(listFieldConfig)){
	        	return result;
	        }        	
        	
            for (GenericValue fieldConfig : listFieldConfig) {
            	String key = fieldConfig.getString(DATA_SOURCE_ID);
                if (!result.containsKey(key)) {         	
                	result.put(key, new HashMap<String, String>());
                }
                result.get(key).put(fieldConfig.getString(INTERNAL_FIELD_NAME), fieldConfig.getString(DEFAULT_VALUE));
            }
        } catch (GenericEntityException e) {
            Debug.logInfo("Error getting : " + e.getMessage(), MODULE);
        }
        return result;
    }

    @Override
    public Map<String, String> getMapField(String standardInterface, String dataSource, Long interfaceSeq) {
        Map<String, String> result = new HashMap<String, String>();
        try {
        	List<EntityExpr> conditions = new ArrayList<EntityExpr>();
	    	conditions.add(EntityCondition.makeCondition(STANDARD_INTERFACE, standardInterface));
	    	conditions.add(EntityCondition.makeCondition(DATA_SOURCE_ID, dataSource));
	    	conditions.add(EntityCondition.makeCondition(INTERFACE_SEQ, interfaceSeq));
	    	conditions.add(EntityCondition.makeCondition(EXTERNAL_FIELD_NAME, EntityOperator.NOT_EQUAL, null));
	    	conditions.add(EntityCondition.makeCondition(INTERNAL_FIELD_NAME, EntityOperator.NOT_EQUAL, null));
	    	EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	    	
	    	List<GenericValue> listFieldConfig = getGenericValues(ecl, UtilMisc.toSet(INTERNAL_FIELD_NAME, EXTERNAL_FIELD_NAME, DATA_SOURCE_ID));
	        if (UtilValidate.isEmpty(listFieldConfig)){
	        	return result;
	        }        	
        	int count = 0;
            for (GenericValue fieldConfig : listFieldConfig) {
            	count ++;
            	String key = getKey(fieldConfig.getString(DATA_SOURCE_ID), count, fieldConfig.getString(EXTERNAL_FIELD_NAME));
                if (!result.containsKey(key)) {               	
                	result.put(key, fieldConfig.getString(INTERNAL_FIELD_NAME));
                }
            }
            result.put("###count###", count + "");
        } catch (GenericEntityException e) {
            Debug.logInfo("Error getting : " + e.getMessage(), MODULE);
        }
        return result;
    }

    private List<GenericValue> getGenericValues(EntityConditionList<EntityExpr> ecl, Set<String> column) throws GenericEntityException {
        return delegator.findList(STANDARD_IMPORT_FIELD_CONFIG, ecl, column, null, null, false);
    }
    
    @Override
    public String getKey(String dataSourceId, String fieldName){
    	return dataSourceId + "|" + fieldName;
    }
    
    @Override
    public String getKey(String dataSourceId, int count, String externalFieldName) {
    	return dataSourceId + "|" + (count) + "|" + externalFieldName;
    }

    @Override
    public GenericValue getStfcDataSource(String dataSourceId) throws GeneralException {
        return EntityUtil.getFirst(getStfcDataSourceList(dataSourceId));
    }

    @Override
    public List<GenericValue> getStfcDataSourceList(String dataSourceId) throws GeneralException {
        List<EntityExpr> conditions = new ArrayList<EntityExpr>();
        conditions.add(EntityCondition.makeCondition(E.dataSourceId.name(), dataSourceId));
        conditions.add(EntityCondition.makeCondition(E.interfaceSeq.name(), DEFAULT_INTERFACE_SEQ));
        return delegator.findList(E.StandardImportFieldConfig.name(), EntityCondition.makeCondition(conditions), null, null,null, false);
    }

    @Override
    public GenericValue getStfcDataSourceDefault(String entityName) throws GeneralException {
        Map<String, Object> stcfFields = new HashMap<String, Object>();
        ModelEntity modelEntity = delegator.getModelEntity(entityName);
        stcfFields.put(E.dataSourceId.name(), "DEFAULT");
        stcfFields.put(E.standardInterface.name(), modelEntity.getPlainTableName());
        stcfFields.put(E.internalFieldName.name(), E.dataSource.name());
        stcfFields.put(E.interfaceSeq.name(), DEFAULT_INTERFACE_SEQ);
        return delegator.findOne(E.StandardImportFieldConfig.name(), stcfFields, false);
    }
    
    @Override
    public List<GenericValue> getStandardImportFieldConfigItems(String dataSourceId) throws GeneralException {
        EntityFindOptions entityFindOption = new EntityFindOptions();
        entityFindOption.setDistinct(true);
        return delegator.findList(E.StandardImportFieldConfig.name(), EntityCondition.makeCondition(E.dataSourceId.name(), dataSourceId), UtilMisc.toSet(E.standardInterface.name(), E.interfaceSeq.name()), null, entityFindOption, false);
    }

    @Override
    public GenericValue getDataSource(String dataSourceId) throws GeneralException {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(E.dataSourceId.name(), dataSourceId);
        return delegator.findOne(E.DataSource.name(), fields, false);
    }
}
