package com.mapsengineering.gzoomjbpm.query;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * Query the process execution ids that have the desired variables
 */
public interface ExecutionIdByVariableQuery {

	/**
	 * Execute the query and obtain the List of process execution id
	 * @return The List of process execution id
	 */
	List<String> list();
	
	/**
	 * For obtain the hibernate query 
	 * @return The hibernate query
	 */
	String getQuery();
	
	/**
	 * Adds a variable to query for
	 * @param name The variable name
	 * @param value The variable value
	 * @return The ExecutionIdByVariableQuery instance
	 */
	ExecutionIdByVariableQuery variable(String name, Object value);
	
	/**
	 * Adds variables to query for
	 * @param variables The variables to query
	 * @return The ExecutionIdByVariableQuery instance
	 */
	ExecutionIdByVariableQuery variables(Map<String, Object> variables);
}
