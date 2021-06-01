package com.mapsengineering.gzoomjbpm.query;

import java.util.List;

public interface FullHistoryVariableQuery {

	/**
	 * Execute the query and obtain the variable history
	 * @return The List of varibale history
	 */
	List<FullHistoryVariableQueryResult> list();
	
	/**
	 * For obtain the hibernate query 
	 * @return The hibernate query
	 */
	String getQuery();
	
	FullHistoryVariableQuery processInstanceId(String processInstanceId);
	
	FullHistoryVariableQuery executionId(String executionId);
	
	//FullHistoryVariableQuery variableName(String variableName);
	
	FullHistoryVariableQuery value(String value);
	
	FullHistoryVariableQuery activityName(String activityName);
	
	FullHistoryVariableQuery variableNames(List<String> variableNames);
}
