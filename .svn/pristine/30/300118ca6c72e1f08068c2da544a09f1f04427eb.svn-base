package com.mapsengineering.base.standardimport.util.jobloglog;

import java.util.Locale;
import java.util.Map;

import com.mapsengineering.base.util.JobLogLog;

/**
 * JobLogLogCreator with resourceName, parameters, notFoundLogCode, notUniqueLogCode
 *
 */
public abstract class JobLogLogCreator {
	private String resourceName;
	private Map<String, Object> parameters;
	private String notFoundLogCode;
	private String notUniqueLogCode;
	
	/**
	 * 
	 * @param resourceName
	 */
	public JobLogLogCreator(String resourceName) {
		this.resourceName = resourceName;
	}
	
	/**
	 * 
	 */
	public void run() {
		setParameters(buildParameters());
		setNotFoundLogCode(buildNotFoundLogCode());
		setNotUniqueLogCode(buildNotUniqueLogCode());
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract Map<String, Object> buildParameters();	
	
	/**
	 * 
	 * @param parameters
	 */
	private void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract String buildNotFoundLogCode();

	/**
	 * 
	 * @param notFoundLogCode
	 */
	private void setNotFoundLogCode(String notFoundLogCode) {
		this.notFoundLogCode = notFoundLogCode;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract String buildNotUniqueLogCode();

	/**
	 * 
	 * @param notUniqueLogCode
	 */
	private void setNotUniqueLogCode(String notUniqueLogCode) {
		this.notUniqueLogCode = notUniqueLogCode;
	}
	
	/**
	 * 
	 * @param locale
	 * @return
	 */
	public JobLogLog getNotFoundJobLogLog(Locale locale) {
		return new JobLogLog().initLogCode(resourceName, notFoundLogCode, parameters, locale);
	}
	
	/**
	 * 
	 * @param locale
	 * @return
	 */
	public JobLogLog getNotUniqueJobLogLog(Locale locale) {
		return new JobLogLog().initLogCode(resourceName, notUniqueLogCode, parameters, locale);
	}

}
