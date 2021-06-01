package com.mapsengineering.base.util.importexport;

import java.io.OutputStream;
import java.util.Map;

import org.ofbiz.entity.GenericValue;

public interface ResourceWriter {

    public void setContext(Map<String, Object> context);
    
	public void open(String entityName) throws Exception;
	
	public void write(GenericValue gv) throws Exception;
	
	public void close() throws Exception;
	
	public OutputStream getStream();
}
