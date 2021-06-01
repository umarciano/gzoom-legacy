package com.mapsengineering.base.util.importexport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

public interface ResourceReader {

    public void setContext(Map<String, Object> context);
    
	public String open(String entityName, InputStream uploadedFile) throws Exception;
	
	public List<GenericValue> read(EntityCondition condition);
	
	public void close();
	
	public OutputStream writeImportResult(String entityName, String pkColumnName, String pkValue, String resultColumnName, String result, InputStream wbStream) throws Exception;
}
