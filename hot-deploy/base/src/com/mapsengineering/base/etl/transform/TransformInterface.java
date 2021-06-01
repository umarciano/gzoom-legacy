package com.mapsengineering.base.etl.transform;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.etl.EtlException;

public interface TransformInterface {
	
	/**
	 * Execute transform step
	 * 
	 * @param delegator
	 * @param extraction
	 * @param parameters
	 * @return
	 * @throws EtlException
	 */
	List<GenericValue> execute(Delegator delegator, List<GenericValue> extraction, Map<String, Object> parameters) throws EtlException;

}
