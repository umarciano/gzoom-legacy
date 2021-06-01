package com.mapsengineering.base.etl.load;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.mapsengineering.base.etl.EtlException;

public interface LoadInterface {
	
	/**
	 * Execute load step
	 * 
	 * @param transformed
	 * @return
	 * @throws EtlException
	 */
	Map<String, Object> execute(List<GenericValue> transformed) throws EtlException;
	
	/**
	 * Execute load step
	 * 
	 * @param transformed
	 * @param dctx
	 * @param context
	 * @return
	 * @throws EtlException
	 */
	Map<String, Object> execute(List<GenericValue> transformed, DispatchContext dctx, Map<String, Object> context) throws EtlException;

}
