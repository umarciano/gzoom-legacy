package com.mapsengineering.base.util.importexport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

public class EntityReader implements ResourceReader {

	public static final String DELEGATOR_NAME = "default";
	
	private String entityName;
	
	private GenericDelegator delegator;
	
	private Map<String, Object> context;
	
	public EntityReader() {
	    context = FastMap.newInstance();
	}
	
	public EntityReader(Map<String, Object> context) {
		this.context = context;
	}
	
	@Override
	public void setContext(Map<String, Object> context) {
	    this.context = context;
	}
	
	@Override
	public String open(String entityName, InputStream uploadedFile) throws Exception {
		this.entityName = entityName;
		return null;
	}

	@Override
	public List<GenericValue> read(EntityCondition condition) {
		delegator = (GenericDelegator)DelegatorFactory.getDelegator(DELEGATOR_NAME);
		try {
			return delegator.findList(entityName, condition, null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public OutputStream writeImportResult(String entityName, String pkColumnName, String pkValue, String resultColumnName, String result, InputStream wbStream) throws Exception {
		GenericValue item = delegator.findOne(entityName, UtilMisc.toMap(pkColumnName, pkValue), false);
		if (UtilValidate.isNotEmpty(item)) {
			item.set(resultColumnName, result);
			item.store();
		}
		return null;
	}

}
