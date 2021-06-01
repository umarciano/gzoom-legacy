/**
 * 
 */
package com.mapsengineering.gzoomjbpm;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Aldo Figlioli Maps S.p.A
 * RMI client interface
 *
 */
public interface JbpmStub {
	
	/**
	 * Key for obtain the user variable
	 */
	public static final String USER = "user";
	/**
	 * Key for obtain the process id
	 */
	public static final String PID = JbpmManager.PID;
	/**
	 * Key for obtain the error message
	 */
	public static final String ERROR = JbpmManager.ERROR;
	/**
	 * Key for obtain the process description variable
	 */
	public static final String DESCRIPTION = "description";
	/**
	 * Key for obtain the active activity name
	 */
	public static final String ACTIVE_ACTIVITY = "activity";
	/**
	 * Key for obtain the status variable
	 */
	public static final String STATUS = "status";
	/**
	 * Key to see if the process is finished
	 */
	public static final String ENDED = "ended";
	/**
	 * Key for obtain the process image bytes
	 */
	public static final String STREAM = "stream";
	/**
	 * Key for obtain the process image type
	 */
	public static final String IMAGE_TYPE = "png";
	
	/**
	 * Starts a process definition
	 * @param processName The process definition name
	 * @param processDescription The process description
	 * @param user The assignee of process first activity
	 * @param initParams Map containing parameters set before process starts
	 * @param toReturnVariables The variables to return after execution
	 * @return Map containing the process id, the status of the process, error message and the required variables
	 * @throws Exception
	 */
	Map<String, Object> startProcess(String processName, String processDescription, String user, Map<String, Object> initParams, List<String> toReturnVariables) throws Exception;
	
	/**
	 * Continues the process execution
	 * @param pid The process id
	 * @param signalName The transition to signal 
	 * @param user The assignee of process next activity
	 * @param variablesToSet Variables that are set in process instance before the execution starts 
	 * @param toReturnVariables The variables to return after execution
	 * @return Map containing the status of the process, error message and the required variables
	 * @throws Exception
	 */
	Map<String, Object> signalExecution(String pid, String signalName, String user, Map<String, Object> variablesToSet, List<String> toReturnVariables) throws Exception;
	
	/**
	 * Get the variables of the process instance
	 * @param pid The process id
	 * @param variableNames The variables to retrieve
	 * @return Map containing the process status and the required variables
	 * @throws Exception 
	 */
	Map<String, Object> getVariables(String pid, List<String> variableNames) throws Exception;
	
	/**
	 * Sets the assignee of current process activity 
	 * @param pid The process id
	 * @param user The user
	 * @return Map containing an error message
	 * @throws Exception 
	 */
	Map<String, Object> setStepUser(String pid, String user) throws Exception;
	
	/**
	 * Gets the task list assigned to the user 
	 * @param processName The process name
	 * @param user The user
	 * @return List of tasks (pid, activity name and status)
	 * @throws Exception
	 */
	List<Map<String, Object>> getTaskList(String processName, String user) throws Exception;
	
	/**
	 * Gets the process progress history
	 * @param pid The process id
	 * @return List of status and date of each activity progress
	 * @throws Exception
	 */
	List<Map<String, Object>> getProcessHistory(String pid) throws Exception;
	
	/**
	 * Is the process ended?
	 * @param pid The process id
	 * @throws Exception
	 */
	boolean isEnded(String pid) throws Exception;
	
	/**
	 * Deletes a process instance
	 * @param pid The process id
	 * @param selectVars The process process variables to display 
	 * @return Map containing an error message
	 * @throws Exception
	 */
	Map<String, Object> deleteProcess(String pid) throws Exception;
	
	/**
	 * Gets the process execution image
	 * @param pid The process id
	 * @param selectVars The variables to display
	 * @return A Map containing errors or the image bytes
	 * @throws Exception
	 */
	Map<String, Object> getProcessHistoryImage(String pid, List<String> selectVars) throws Exception;
	
	/**
	 * Returns the outgoing transitions and the destination activities of the current active activity
	 * @param pid The process id
	 * @return A Map containing transition names with relative detinations
	 * @throws Exception
	 */
	Map<String, String> getOutgoingDestinations(String pid) throws Exception;
}
