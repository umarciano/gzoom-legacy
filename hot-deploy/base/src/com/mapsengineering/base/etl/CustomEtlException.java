package com.mapsengineering.base.etl;

import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.util.JobLogLog;

/**
 * @author rime
 *
 */
public class CustomEtlException extends ImportException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	
	public CustomEtlException(String entityName, String id, JobLogLog jobLogLog) {
	    super(entityName, id, jobLogLog);
	}

	public CustomEtlException(Throwable cause, String entityName, String id, JobLogLog jobLogLog) {
	    super(cause, entityName, id, jobLogLog);
    }
}
