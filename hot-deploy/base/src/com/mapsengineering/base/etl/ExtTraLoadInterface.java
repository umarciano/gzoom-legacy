package com.mapsengineering.base.etl;

import java.util.Map;


/**
 * @author rime
 *
 */
public interface ExtTraLoadInterface {
	
	/**
	 * Execute method for the ETL process
	 * 
	 */
	void execute();
	
	/**
	 * Get result of the ETL process
	 * 
	 * @return
	 */
	Map<String, Object> getResult();

}
