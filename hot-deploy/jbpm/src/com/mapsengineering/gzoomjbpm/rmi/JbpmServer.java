package com.mapsengineering.gzoomjbpm.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.mapsengineering.gzoomjbpm.JbpmManager;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * RMI Server Remote interface
 *
 */
public interface JbpmServer extends Remote {
	
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
	 * Key for obtain the status variable
	 */
	public static final String STATUS = "status";
	/**
	 * Key for obtain the active activity name
	 */
	public static final String ACTIVE_ACTIVITY = "activity";
	/**
	 * Key for obtain the process image bytes
	 */
	public static final String STREAM = "stream";
	/**
	 * Key for obtain the process image type
	 */
	public static final String IMAGE_TYPE = "png";
	/**
	 * key for obtain the process definition id variable
	 */
	public static final String PDID = JbpmManager.PDID;
	
	/**
	 * Starts a business process
	 * @param processName The process name
	 * @param description An optional description of the process
	 * @param user The user owner of the first activity
	 * @param returnVars A list of process variables to return after the first activity ends
	 * @return A Map containing the process id, the status of the valuation process, error message and the variables required
	 * @throws RemoteException
	 */
	public Map<String, Object> startProcess(String processName, String description, String user, Map<String, Object> initParams, List<String> returnVars) throws RemoteException;
	
	/**
	 * Method for obtain process variables, from history if process is ended
	 * @param pid The process id
	 * @param variableNames A list of desired variables 
	 * @return A Map containing the required variables
	 * @throws RemoteException
	 */
	public Map<String, Object> getVariables(String pid, List<String> variableNames) throws RemoteException;
	
	/**
	 * Signals the process execution
	 * @param pid The process id
	 * @param signalName The transition to signal
	 * @param user The user owner of the next activity, if there is an effective progress
	 * @param variablesToSet Variables to set before the process progress
	 * @param returnVars Extra variables to obtain after the execution
	 * @return The status of the valuation process, error message and the required variables
	 * @throws RemoteException
	 */
	public Map<String, Object> stepOver(String pid, String signalName, String user, Map<String, Object> variablesToSet, List<String> returnVars) throws RemoteException;
	
	/**
	 * Changes the user owner of the current activity
	 * @param pid The process id
	 * @param user The new user
	 * @return A Map containing error messages
	 * @throws RemoteException
	 */
	public Map<String, Object> changeUser(String pid, String user) throws RemoteException;
	
	/**
	 * Method for obtain a list of processes in charge of an user
	 * @param processName The name of the process to query (optional)
	 * @param user The name of the user to query (optional)
	 * @return A List of Maps each containing a process id, activity description and process status
	 * @throws RemoteException
	 */
	public List<Map<String, Object>> taskList(String processName, String user) throws RemoteException;
	
	/**
	 * Is the process execution ended?
	 * @param pid The process id
	 * @return A boolean flag, false if the process is ended, true otherwise
	 * @throws RemoteException
	 */
	public boolean isEnded(String pid) throws RemoteException;
	
	/**
	 * Deletes a process instance
	 * @param pid The process id
	 * @param selectVars The process variables to display
	 * @return A Map containing error messages
	 * @throws RemoteException
	 */
	public Map<String, Object> deleteProcessInstance(String pid) throws RemoteException;
	
	/**
	 * Gets the process execution image
	 * @param pid The process id
	 * @param selectVars The variables to display
	 * @return A Map containing errors or the image bytes
	 * @throws Exception
	 */
	public Map<String, Object> getProcessHistoryImage(String pid, List<String> selectVars) throws Exception;

	/**
	 * Returns the outgoing transitions and the destination activities of the current active activity
	 * @param pid The process id
	 * @return A Map containing transition names with relative detinations
	 */
	public Map<String, String> getOutgoingDestinations(String pid) throws Exception;
}
