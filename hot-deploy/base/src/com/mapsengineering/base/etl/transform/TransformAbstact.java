package com.mapsengineering.base.etl.transform;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.etl.EtlException;

public abstract class TransformAbstact implements TransformInterface {
	
	/* (non-Javadoc)
	 * @see com.mapsengineering.base.etl.transform.TransformInterface#execute(org.ofbiz.entity.Delegator, java.util.List, java.util.Map)
	 */
	public abstract List<GenericValue> execute(Delegator delegator, List<GenericValue> extraction, Map<String, Object> parameters) throws EtlException;

}
