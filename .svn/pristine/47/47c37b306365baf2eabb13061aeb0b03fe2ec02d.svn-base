package com.mapsengineering.base.etl.extract;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.etl.EtlException;

/**
 * @author rime
 *
 */
public interface ExtractInterface {

	/**
	 * Execute extraction step
	 * 
	 * @param parameters
	 * @return
	 * @throws EtlException 
	 * @throws Exception
	 */
	List<GenericValue> execute(Delegator delegator, Map<String, Object> parameters) throws EtlException;
}
