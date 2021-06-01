package com.mapsengineering.gzoomjbpm.query;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * Query the process instance by name, by owner user and by variables
 *
 */
public interface ProcessVariableQuery {
	
	public final static String USER = "user";
	
	/**
	 * Adds the process name filter
	 * @param processName The process name to query 
	 * @return The ProcessVariableQuery instance
	 */
	ProcessVariableQuery processName(String processName);
	
	/**
	 * Adds the user filter
	 * @param user The user to query
	 * @return The ProcessVariableQuery instance
	 */
	ProcessVariableQuery user(String user);
	
	/**
	 * Adds a variable in query where clause
	 * @param name The variable name
	 * @param value The variable value
	 * @return The ProcessVariableQuery instance
	 */
	ProcessVariableQuery variableInWhereClause(String name, Object value);
	
	/**
	 * Adds variables in query where clause
	 * @param variables The variables to add in where clause
	 * @return The ProcessVariableQuery instance
	 */
	ProcessVariableQuery variablesInWhereClause(Map<String, Object> variables);
	
	/**
	 * Execute the query and obtain the List of ProcessVariableQueryResult
	 * @return The List of ProcessVariableQueryResult
	 */
	List<ProcessVariableQueryResult> list();
	
	/**
	 * For obtain the hibernate query 
	 * @return The hibernate query
	 */
	String getQuery();

}
